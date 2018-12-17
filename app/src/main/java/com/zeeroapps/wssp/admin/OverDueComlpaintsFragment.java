package com.zeeroapps.wssp.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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

import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.Model.ModelComplaints;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.adapter.AdminCustomAdapterComplaints;
import com.zeeroapps.wssp.utils.Constants;
import com.zeeroapps.wssp.utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class OverDueComlpaintsFragment extends Fragment {

    String TAG = "MyApp", account_id;
    Context mContext;
    RecyclerView recyclerView;
    RelativeLayout layoutMain;
    AVLoadingIndicatorView avi;
    AdminCustomAdapterComplaints customAdapter;
    ArrayList<ModelComplaints> compList;
    private String JSON_TAG = "JSON_ARRAY_TAG";
    EditText et_filter;
    private GetDataFromServer getDataFromServer;

    String[] image_path, c_number, status, c_date, c_detials, c_types, address;

    SharedPreferences sp;
    private String compNo;
    StringRequest jsonReq;

    public OverDueComlpaintsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = inflater.getContext();
//        compNo = getArguments().getString("COMPLAINT_NUMBER");
        View v = inflater.inflate(R.layout.fragment_all_complaints, container, false);

        sp = inflater.getContext().getSharedPreferences(getString(R.string.sp), Context.MODE_PRIVATE);
        account_id = sp.getString(getString(R.string.spUID), "");
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        layoutMain = (RelativeLayout) v.findViewById(R.id.mainLayout);
        et_filter = (EditText) v.findViewById(R.id.search);
        avi = (AVLoadingIndicatorView) v.findViewById(R.id.loadingIndicator);
        avi.show();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        compList = new ArrayList<ModelComplaints>();
        getDataFromServer = new GetDataFromServer();
        getDataFromServer.execute();


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

    public static OverDueComlpaintsFragment newInstance() {

        Bundle args = new Bundle();

        OverDueComlpaintsFragment fragment = new OverDueComlpaintsFragment();
        fragment.setArguments(args);
        return fragment;
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


    JSONObject jsonObj; JSONArray jsonArray; String server_response;
    public class GetDataFromServer extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

           avi.show();

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                JSONObject obj = new JSONObject();

                obj.put("account_id", account_id);

                String str_req = JsonParser.multipartFormRequestForFindFriends(Constants.OverDue_Complaints, "UTF-8", obj, null);

                jsonObj = new JSONObject(str_req);
                Log.e("JObject", str_req);


                server_response = jsonObj.getString("status");
                Log.e("serverRes", server_response);


                if (server_response.equals("true")) {

                    jsonArray = jsonObj.getJSONArray("over_due_complaints");

                    JSONObject jObj;

                    image_path = new String[(jsonArray.length()) ];
                    c_number = new String[(jsonArray.length()) ];
                    status = new String[(jsonArray.length()) ];
                    c_date = new String[(jsonArray.length()) ];
                    c_detials = new String[(jsonArray.length()) ];
                    c_types = new String[(jsonArray.length()) ];
                    address = new String[(jsonArray.length()) ];



                    for (int i = 0; i < jsonArray.length(); i++) {

                        jObj = jsonArray.getJSONObject(i);

                        //news feed array
                        if (jObj.length() > 0) {

                            image_path[i] = jObj.getString("image_path");
                            c_number[i] = jObj.getString("c_number");
                            status[i] = jObj.getString("status");
                            c_date[i] = jObj.getString("c_date_time");
                            c_detials[i] = jObj.getString("c_details");
                            c_types[i] = jObj.getString("c_type");
                            address[i] = jObj.getString("bin_address");



                        }

                    }



                }





            } catch (Exception e) {
                e.printStackTrace();

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

                avi.hide();

                if (server_response.equals("false")){
                    Snackbar.make(layoutMain, "No Pending Complaints Found!", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show();
                }
                else if (server_response.equals("true")){

                    //setting values to arraylist
                    for (int k = 0; k < c_date.length; k++) {

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

                    Log.e("complist", compList.size()+"");

                    customAdapter = new AdminCustomAdapterComplaints(getActivity(), compList);
                    recyclerView.setAdapter(customAdapter);
                }
                else {

                    Snackbar.make(layoutMain, "Error in connection!", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show();
                }



        }
    }


    @Override
    public void onStop() {
        super.onStop();

        if (getDataFromServer != null && getDataFromServer.getStatus() != AsyncTask.Status.FINISHED)
            getDataFromServer.cancel(true);
    }
}
