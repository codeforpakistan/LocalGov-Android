package com.zeeroapps.wssp.admin;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.utils.AppController;
import com.zeeroapps.wssp.utils.Constants;
import com.zeeroapps.wssp.utils.SHA1;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminComplaintDetailFragment extends Fragment {

    TextView tvNo, tvComplaintStatus, tvComplaintStatusUrdu, tvDandT, tvTypeEng, tvTypeUrdu, tvDetail;
    NetworkImageView ivCImage;
    private static ImageLoader imageLoader;
    FirebaseAnalytics mFBAnalytics;

    String complaintTypeList[] = {"Drainage", "Trash Bin", "Water Supply", "Garbage", "Other"};
    String complaintTypeListUrdu[] = {"نکاسی آب", "بھرا ہوا گند کا ڈھبہ", "پانی کا مسئلہ", "کوڑا کرکٹ", "کوئی اور مسئلہ"};

    String statusList[] = {"Pending Review", "In Progress", "Completed"};
    String statusListUrdu[] = {"زیر جائزہ", "کام جاری ہے", "مکمّل شدہ"};
    int colorList[] = {Color.RED, Color.parseColor("#ffc200"), Color.parseColor("#FF15762A")};

    Spinner statusSpinner;
    AVLoadingIndicatorView avi;
    String newStatus="";
    Button BtUpdate;
    RelativeLayout layoutMain;
    private String tag_json_obj = "JSON_OBJECT";


    public AdminComplaintDetailFragment() {
        // Required empty public constructor
    }

    public static AdminComplaintDetailFragment newInstance() {
        Bundle args = new Bundle();
        
        AdminComplaintDetailFragment fragment = new AdminComplaintDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_complaint_details, container, false);
        ivCImage = (NetworkImageView) view.findViewById(R.id.ivPreview);
        tvNo = (TextView) view.findViewById(R.id.tvCompNo);
        tvComplaintStatus = (TextView) view.findViewById(R.id.tvCompStatus);
        tvComplaintStatusUrdu = (TextView) view.findViewById(R.id.tvComplaintStatusUrdu);
        tvDandT = (TextView) view.findViewById(R.id.tvDandT);
        tvTypeEng = (TextView) view.findViewById(R.id.tvTypeEng);
        tvTypeUrdu = (TextView) view.findViewById(R.id.tvTypeUrdu);
        tvDetail = (TextView) view.findViewById(R.id.tvCompDetail);
        avi = (AVLoadingIndicatorView) view.findViewById(R.id.loadingIndicator);
        BtUpdate = (Button) view.findViewById(R.id.update);
        layoutMain = (RelativeLayout) view.findViewById(R.id.mainLayout);
        tvDetail.setMovementMethod(new ScrollingMovementMethod());

        mFBAnalytics = FirebaseAnalytics.getInstance(getContext());
        fbAnalytics();


        // Spinner element
        statusSpinner = (Spinner) view.findViewById(R.id.spinner_status);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select Status");
        categories.add("Pending");
        categories.add("InProgress");
        categories.add("Completed");
        categories.add("Irrelevant");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        statusSpinner.setAdapter(dataAdapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position == 0){
                    newStatus = "";
                }
                else if (position == 1){
                    newStatus = "pendingreview";
                }
                else if (position == 2){
                    newStatus = "inprogress";
                }
                else if (position == 3){
                    newStatus = "completed";
                }
                else if (position == 4){
                    newStatus = "irrelevant";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });



        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ivCImage.setImageUrl(getArguments().getString("C_IMAGE_URL"), imageLoader);
//        Glide.with(inflater.getContext()).load(getArguments().getString("C_IMAGE_URL")).override(300, 300).dontAnimate().into(ivCImage);
        tvNo.setText(getArguments().getString("C_NO"));
        tvDandT.setText(getArguments().getString("C_DATE_TIME"));
        tvTypeUrdu.setText(getArguments().getString("C_TYPE"));
        tvDetail.setText(getArguments().getString("C_DETAIL"));

        int i = 0;
        final String status = getArguments().getString("C_STATUS");
        if (status.toLowerCase().contains("pendingreview")){
            i= 0;
        }else if (status.toLowerCase().contains("inprogress")){
            i= 1;
        }else if (status.toLowerCase().contains("completed")){
            i= 2;
        }
        tvComplaintStatus.setText(statusList[i]);
        tvComplaintStatusUrdu.setText(statusListUrdu[i]);
        tvComplaintStatus.setBackgroundColor(colorList[i]);
        tvComplaintStatusUrdu.setBackgroundColor(colorList[i]);

        String type = getArguments().getString("C_TYPE");
        tvTypeEng.setText(getArguments().getString("C_TYPE"));
        if (type.toLowerCase().contains("drainage")){
            tvTypeUrdu.setText(complaintTypeListUrdu[0]);
        }else if (type.toLowerCase().contains("trash")){
            tvTypeUrdu.setText(complaintTypeListUrdu[1]);
        }else if (type.toLowerCase().contains("water")){
            tvTypeUrdu.setText(complaintTypeListUrdu[2]);
        }else if (type.toLowerCase().contains("garbage")){
            tvTypeUrdu.setText(complaintTypeListUrdu[3]);
        }else if (type.toLowerCase().contains("other")){
            tvTypeUrdu.setText(complaintTypeListUrdu[4]);
        }



        BtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newStatus.equals("")){
                    Toast.makeText(getActivity(), "Change Complaint Status to Update", Toast.LENGTH_LONG).show();
                }
                else if (newStatus.equals(status)){
                    Toast.makeText(getActivity(), "Already in" + " " + status + " " + "state", Toast.LENGTH_LONG).show();
                }
                else {
                    avi.show();
                    HomeFragemt cdFragment = new HomeFragemt();
                    (getActivity()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, cdFragment)
                            .disallowAddToBackStack()
                            .commit();
//                    UpdateComplaintStatus();
                }
            }
        });


        return view;
    }

    public void fbAnalytics(){
        Bundle bundle = new Bundle();
        bundle.putString("complaint_number", getArguments().getString("C_NO"));
        mFBAnalytics.logEvent("my_complaints", bundle);
    }

    public void UpdateComplaintStatus() {


        StringRequest jsonReq = new StringRequest(Request.Method.POST, Constants.Update_Complaints, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Tag", response.toString());

                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    String status = jObj.getString("status");

                    if (status.contains("true")) {
                        HomeFragemt cdFragment = new HomeFragemt();
                        (getActivity()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, cdFragment)
                                .addToBackStack("CDF")
                                .commit();
//                        Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "Complaint Status Not Updated", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                avi.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Tag", error.toString());
                if (error.toString().contains("NoConnectionError")) {
                    Snackbar.make(layoutMain, "Error in connection!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(layoutMain, "Server not responding!", Snackbar.LENGTH_LONG).show();
                }
                avi.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("complaint_number", getArguments().getString("C_NO"));
                params.put("status", newStatus);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonReq, tag_json_obj);

    }


}
