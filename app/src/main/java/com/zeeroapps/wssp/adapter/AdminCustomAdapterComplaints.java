package com.zeeroapps.wssp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.Model.AdminComplaintsModel;
import com.zeeroapps.wssp.Model.ModelComplaints;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.admin.AdminComplaintDetailFragment;
import com.zeeroapps.wssp.fragments.ComplaintDetailFragment;
import com.zeeroapps.wssp.utils.AppController;

import java.util.ArrayList;

/**
 * Created by fazalullah on 4/17/17.
 */

public class AdminCustomAdapterComplaints extends RecyclerView.Adapter<AdminCustomAdapterComplaints.ViewHolder>
        implements Filterable{

        String TAG = "MyApp";
        Context mContext;
        ArrayList<ModelComplaints> complaintsList;
        ArrayList<ModelComplaints> mOriginalValues; // Original Values



        public AdminCustomAdapterComplaints(Context c, ArrayList<ModelComplaints> comps) {
            mContext = c;
            complaintsList = comps;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_complaints, parent, false);
            ViewHolder vh = new ViewHolder(mContext, v, complaintsList);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ModelComplaints mc = complaintsList.get(position);
            holder.bindData(mc);
        }

        @Override
        public int getItemCount() {
            return complaintsList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            static String TAG = "MyApp";
            public static Context mContext;
            public ArrayList<ModelComplaints> myComplaintsList;
            private NetworkImageView imageComplaint;
            private TextView tvComplaintNumber;
            private TextView tvComplaintStatus;
            private TextView tvComplaintStatusUrdu;
            private TextView tvDateAndTime;
            private AVLoadingIndicatorView avi;
            private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

            private static String statusListUrdu[] = {"زیر جائزہ", "کام جاری ہے", "مکمّل شدہ"};

            public ViewHolder(Context c, View v, ArrayList<ModelComplaints> compList) {
                super(v);
                mContext = c;
                myComplaintsList = compList;
                if (imageLoader == null)
                    imageLoader = AppController.getInstance().getImageLoader();
                imageComplaint = (NetworkImageView) v.findViewById(R.id.imageComplaint);
                tvComplaintNumber = (TextView) v.findViewById(R.id.tvComplaintNumber);
                tvComplaintStatus = (TextView) v.findViewById(R.id.tvComplaintStatus);
                tvComplaintStatusUrdu = (TextView) v.findViewById(R.id.tvComplaintStatusUrdu);
                tvDateAndTime = (TextView) v.findViewById(R.id.tvDateAndTime);
//            avi = (AVLoadingIndicatorView) v.findViewById(R.id.loadingIndicator);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "Clicked Position: " + getAdapterPosition());
                        ModelComplaints complaint = myComplaintsList.get(getAdapterPosition());
                        Bundle bundle = new Bundle();
                        bundle.putString("C_NO", complaint.getcNumber());
                        bundle.putString("C_IMAGE_URL", complaint.getcImageUrl());
                        bundle.putString("C_STATUS", complaint.getcStatus());
                        bundle.putString("C_DATE_TIME", complaint.getcDateAndTime());
                        bundle.putString("C_DETAIL", complaint.getcDetail());
                        bundle.putString("C_ADDRESS", complaint.getcAddress());
                        bundle.putString("C_TYPE", complaint.getcType());
                        AdminComplaintDetailFragment cdFragment = new AdminComplaintDetailFragment();
                        cdFragment.setArguments(bundle);
                        ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, cdFragment)
                                .addToBackStack("CDF")
                                .commit();
                    }
                });
            }

            public void bindData(ModelComplaints mc) {
                Log.e(TAG, "bindData: "+mc.getcImageUrl());
                imageComplaint.setImageUrl(mc.getcImageUrl(), imageLoader);
//            Picasso.with(mContext).load(Constants.HOST_URL+mc.getcImageUrl()).resize(100, 100).into(imageComplaint);
//            Glide.with(mContext).load(Constants.HOST_URL+mc.getcImageUrl()).override(300, 300)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            avi.hide();
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            avi.hide();
//                            return false;
//                        }
//                    }).into(imageComplaint);
                String status = mc.getcStatus();
                if (status.toLowerCase().contains("pendingreview")){
                    tvComplaintStatus.setText("Pending Review");
                    tvComplaintStatusUrdu.setText(statusListUrdu[0]);
                    tvComplaintStatus.setTextColor(Color.RED);
                    tvComplaintStatusUrdu.setTextColor(Color.RED);
                }else if (status.toLowerCase().contains("inprogress")){
                    tvComplaintStatus.setText("In Progress");
                    tvComplaintStatusUrdu.setText(statusListUrdu[1]);
                    tvComplaintStatus.setTextColor(Color.parseColor("#ffc200"));
                    tvComplaintStatusUrdu.setTextColor(Color.parseColor("#ffc200"));
                }else if (status.toLowerCase().contains("completed")){
                    tvComplaintStatus.setText("Completed");
                    tvComplaintStatusUrdu.setText(statusListUrdu[2]);
                    tvComplaintStatus.setTextColor(Color.parseColor("#FF15762A"));
                    tvComplaintStatusUrdu.setTextColor(Color.parseColor("#FF15762A"));
                }
                tvComplaintNumber.setText(mc.getcNumber());
                tvDateAndTime.setText(mc.getcDateAndTime());
            }
        }

        //filter arraylist
        @Override
        public Filter getFilter() {

            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    complaintsList = (ArrayList<ModelComplaints>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values

                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<ModelComplaints> FilteredArrList = new ArrayList<>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<ModelComplaints>(complaintsList); // saves the original data in mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;

                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i).getcType();
                            String data2 = mOriginalValues.get(i).getcStatus();
                            if (data.toLowerCase().startsWith(constraint.toString())
                                    || data2.toLowerCase().startsWith(constraint.toString())) {

                                FilteredArrList.add(new ModelComplaints(mOriginalValues.get(i).getcType()
                                        ,mOriginalValues.get(i).getcStatus()
                                        ,mOriginalValues.get(i).getcDetail()
                                        ,mOriginalValues.get(i).getcDateAndTime()
                                        ,mOriginalValues.get(i).getcAddress()
                                        ,mOriginalValues.get(i).getcImageUrl()
                                        ,mOriginalValues.get(i).getcNumber()) );


                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }

    }

