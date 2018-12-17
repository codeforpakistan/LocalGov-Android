package com.zeeroapps.wssp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.Model.ModelComplaints;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.adapter.CustomAdapterComplaints;
import com.zeeroapps.wssp.utils.AppController;
import com.zeeroapps.wssp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyComplaintsFragment extends Fragment {
    String TAG = "MyApp";
    int inprogress_complaints, pending_complaints, completed_complaints, overdue_complaints;
    Context mContext;
    RecyclerView recyclerView;
    RelativeLayout layoutMain;
    AVLoadingIndicatorView avi;
    CustomAdapterComplaints customAdapter;
    ArrayList<ModelComplaints> compList;
    private String JSON_TAG = "JSON_ARRAY_TAG";
    EditText et_filter;

    String[] image_path, c_number, status, c_date, c_detials, c_types, address;

    SharedPreferences sp;
    private String compNo;
    StringRequest jsonReq;

    public MyComplaintsFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = inflater.getContext();
        compNo = getArguments().getString("COMPLAINT_NUMBER");
        View v = inflater.inflate(R.layout.fragment_my_complaints, container, false);

        sp = inflater.getContext().getSharedPreferences(getString(R.string.sp), Context.MODE_PRIVATE);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        layoutMain = (RelativeLayout) v.findViewById(R.id.mainLayout);
        et_filter = (EditText) v.findViewById(R.id.search);
        avi = (AVLoadingIndicatorView) v.findViewById(R.id.loadingIndicator);
        avi.show();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        compList = new ArrayList<ModelComplaints>();
        getDataFromDB();
        customAdapter = new CustomAdapterComplaints(mContext, compList);
        recyclerView.setAdapter(customAdapter);


        // Add Text Change Listener to EditText
        et_filter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                customAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return v;
    }

    public static MyComplaintsFragment newInstance() {
        
        Bundle args = new Bundle();
        
        MyComplaintsFragment fragment = new MyComplaintsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void getDataFromDB(){
        avi.show();


        jsonReq = new StringRequest(Request.Method.POST, Constants.URL_MY_COMPLAINTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Response: "+response );
                if (response.toString().contains("[]")) {
                    Snackbar.make(layoutMain, "No complaints!", Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.RED).show();
                }
                try {
                JSONArray jArr = new JSONArray(response);

                    image_path = new String[(jArr.length())];
                    c_number = new String[(jArr.length()) ];
                    status = new String[(jArr.length()) ];
                    c_date = new String[(jArr.length()) ];
                    c_detials = new String[(jArr.length()) ];
                    c_types = new String[(jArr.length()) ];
                    address = new String[(jArr.length()) ];


                    for (int i=0; i<c_number.length; i++ ) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        Log.e(TAG, "Object In Array Response: " + jObj);
                        image_path[i] = jObj.getString("image_path");
                        c_number[i] = jObj.getString("c_number");
                        status[i] = jObj.getString("status");
                        c_date[i] = jObj.getString("c_date_time");
                        c_detials[i] = jObj.getString("c_details");
                        c_types[i] = jObj.getString("c_type");
                        address[i] = jObj.getString("bin_address");

                    }

                    //setting vslue to arrayist
                    for (int k = 0; k < c_number.length; k++) {
                        ModelComplaints modelComplaints = new ModelComplaints(image_path[k],c_number[k],
                                status[k],c_date[k],c_detials[k], c_types[k], address[k]);
                        modelComplaints.setcAddress(address[k]);
                        modelComplaints.setcDetail(c_detials[k]);
                        modelComplaints.setcType(c_types[k]);
                        modelComplaints.setcStatus(status[k]);
                        modelComplaints.setcImageUrl(image_path[k]);
                        modelComplaints.setcNumber(c_number[k]);
                        modelComplaints.setcDateAndTime(c_date[k]);

                        compList.add(modelComplaints);
                    }


                    for (int k = 0; k < compList.size(); k++) {

                        if(compList.get(k).getcStatus().equals("completed")){
                            ++completed_complaints;
                        }
                        else if(compList.get(k).getcStatus().equals("pendingreview")){
                            ++pending_complaints;
                        }
                        else if(compList.get(k).getcStatus().equals("inprogress")){
                            ++inprogress_complaints;
                        }
                        else if (compList.get(k).getcStatus().equals("overdue")) {
                            ++overdue_complaints;
                        }
                    }


                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("completed" , completed_complaints);
                    editor.putInt("pending" , pending_complaints);
                    editor.putInt("inprogress" , inprogress_complaints);
                    editor.putInt("overdue" , overdue_complaints);
                    editor.apply();
//                    Toast.makeText(getActivity(), completed_complaints+"" +
//                            "\n"+ pending_complaints + "\n" + inprogress_complaints, Toast.LENGTH_SHORT).show();


                    customAdapter.notifyDataSetChanged();
                    if (compNo != null){
                        filter(compNo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }

                avi.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: "+error );
                if (error.toString().contains("NoConnectionError")) {
                    Snackbar.make(layoutMain, "Error in connection!", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show();
                } else {
                    Snackbar.make(layoutMain, "Server not responding!", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show();
                }
                avi.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobilenumber", sp.getString(getString(R.string.spUMobile), null));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonReq, JSON_TAG);
    }

    public void filter(String text) {
        ArrayList<ModelComplaints> complaintsListCopy = new ArrayList<ModelComplaints>(compList);
        compList.clear();
        if(text.isEmpty()){
            compList.addAll(complaintsListCopy);
        } else {
            text = text.toLowerCase();
            for(ModelComplaints item: complaintsListCopy){
                if(item.getcNumber().toLowerCase().contains(text)){
                    compList.add(item);
                }
            }
        }
        customAdapter.notifyDataSetChanged();
    }


    @Override
    public void onStop() {
        super.onStop();
        jsonReq.cancel();
    }
}
