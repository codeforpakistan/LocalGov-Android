package com.zeeroapps.wssp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.Model.ModelCategories;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.activities.NewComplaintActivity;
import com.zeeroapps.wssp.adapter.Categories_Adapter;
import com.zeeroapps.wssp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MoreCategoriesFragment extends Fragment {

    ArrayList<ModelCategories> model_classArrayList = new ArrayList<ModelCategories>();
    GridView gridView;
    AVLoadingIndicatorView avi;
    RelativeLayout relativeLayout;

    public MoreCategoriesFragment() {
        // Required empty public constructor
    }

    public static MoreCategoriesFragment newInstance() {
        Bundle args = new Bundle();
        
        MoreCategoriesFragment fragment = new MoreCategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_categories, container, false);

        gridView = (GridView) view.findViewById(R.id.gridView);
        avi = (AVLoadingIndicatorView)  view.findViewById(R.id.loadingIndicator);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.main_layout);

        getServerData();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), NewComplaintActivity.class);
                intent.putExtra("complaintType", model_classArrayList.get(position).getComplaint_types());
                intent.putExtra("selected_item", 404);
                startActivity(intent);

            }
        });

        return view;
    }



    private void getServerData() {
        String urlGetServerData = Constants.END_POINT + "index.php/main/complaint_types";
        System.out.print(urlGetServerData);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlGetServerData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        avi.hide();
                        Log.e("MyResponse", response + "");
                        try {

                            Gson gson = new Gson();
                            JSONArray jsonArray = response.getJSONArray("data");

                            //get single object
                            String kss = response.getString("status");
                            Log.e("serverStatus", kss + "");

                            for (int p = 0; p < jsonArray.length(); p++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(p);
                                ModelCategories rvdata = gson.fromJson(String.valueOf(jsonObject), ModelCategories.class);
                                model_classArrayList.add(rvdata);

                            }

                            Categories_Adapter gridViewAdapter = new Categories_Adapter(getActivity(),model_classArrayList);
                            gridView.setAdapter(gridViewAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        avi.hide();
                        System.out.println(error.toString());
                        Snackbar.make(relativeLayout, "Server not responding!", Snackbar.LENGTH_LONG).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

}
