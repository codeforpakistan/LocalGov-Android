package com.zeeroapps.wssp.admin;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.fragments.ComplaintDetailFragment;
import com.zeeroapps.wssp.fragments.PieChartFragment;
import com.zeeroapps.wssp.utils.Constants;
import com.zeeroapps.wssp.utils.NotificationPublisher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;


public class HomeFragemt extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    AVLoadingIndicatorView avi;
    String all_complaints = "0", pending_complaints = "0", inprogress_complaints = "0",
            completed_complaints = "0",
            overdue_complaints = "0", account_id;
    TextView Tv_all_complaints, Tv_pending_complaints, Tv_inprogress_complaints,
            Tv_completed_complaints, Tv_overdue_complaints;
    PieChart pieChart;
    ArrayList<String> PieEntryLabels;
    PieDataSet pieDataSet;
    PieData pieData;
    ArrayList<Entry> entries;
    SharedPreferences sp;
    TextView notification;
    LinearLayout layoutCompleted, layoutAll, layoutPending, layoutInProgress, layoutOverdue;
    View v;
    long RemainingHour, hoursInMilli;

    public HomeFragemt() {
        // Required empty public constructor
    }


    public static HomeFragemt newInstance(String param1, String param2) {
        HomeFragemt fragment = new HomeFragemt();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home_fragemt, container, false);

        init();

        getServerData();

        return v;
    }


    public void init() {

        ////
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");

        Date systemDate = Calendar.getInstance().getTime();
        String myDate = sdf.format(systemDate);

        Date Date1 = null;
        Date Date2 = null;
        try {
            Date1 = sdf.parse(myDate);
            Date2 = sdf.parse("11:59:059 PM");//calculate time difference from current to end of the day
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long millse = Date1.getTime() - Date2.getTime();
        long mills = Math.abs(millse);

        int Hours = (int) (mills/(1000 * 60 * 60));
        int Mins = (int) (mills/(1000*60)) % 60;
        long Secs = (int) (mills / 1000) % 60;

        String diff = Hours + ":" + Mins + ":" + Secs;
        Log.e("current", myDate);
        Log.e("difference", diff);
        hoursInMilli = Hours * 3600000;
        //28,800,000  Milliseconds of 8 hours from 12am to 8 am next day
        RemainingHour = hoursInMilli + 28800000;
        Log.e("hours", RemainingHour+"");
        //////



        avi = (AVLoadingIndicatorView) v.findViewById(R.id.loadingIndicator);
        avi.show();
        pieChart = (PieChart) v.findViewById(R.id.chart1);
        Tv_all_complaints = (TextView) v.findViewById(R.id.allcomp);
        Tv_pending_complaints = (TextView) v.findViewById(R.id.pending);
        Tv_inprogress_complaints = (TextView) v.findViewById(R.id.progress);
        Tv_completed_complaints = (TextView) v.findViewById(R.id.completed);
        Tv_overdue_complaints = (TextView) v.findViewById(R.id.overdue);
        notification = (TextView) v.findViewById(R.id.notification);
        layoutCompleted = (LinearLayout) v.findViewById(R.id.lcompletedcomplaints);
        layoutAll = (LinearLayout) v.findViewById(R.id.lallcomplaints);
        layoutPending = (LinearLayout) v.findViewById(R.id.lpendingcomplaints);
        layoutInProgress = (LinearLayout) v.findViewById(R.id.linprogresscomplaints);
        layoutOverdue = (LinearLayout) v.findViewById(R.id.loverduecomplaints);


        notification.setOnClickListener(this);
        layoutCompleted.setOnClickListener(this);
        layoutAll.setOnClickListener(this);
        layoutPending.setOnClickListener(this);
        layoutInProgress.setOnClickListener(this);
        layoutOverdue.setOnClickListener(this);


        sp = getActivity().getSharedPreferences(getString(R.string.sp), getActivity().MODE_PRIVATE);
        account_id = sp.getString(getString(R.string.spUID), "");

        //adding values to pie chart
        entries = new ArrayList<>();
        PieEntryLabels = new ArrayList<String>();

    }


    private void getServerData() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.Admin_Home + "account_id=" + account_id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        avi.hide();
                        Log.e("ServerResponse", response + "");
                        try {

                            String responseString = response.getString("status");

                            if (responseString.equals("true")) {

                                all_complaints = response.getString("all_complaints");
                                pending_complaints = response.getString("pending_complaints");
                                completed_complaints = response.getString("completed_complaints");
                                inprogress_complaints = response.getString("inprogress_complaints");
                                overdue_complaints = response.getString("over_due_complaints");


                                Log.e("All_complaints", all_complaints + "" + pending_complaints
                                        + "" + completed_complaints + "" + inprogress_complaints + "" + overdue_complaints);

                                Tv_all_complaints.setText(all_complaints);
                                Tv_completed_complaints.setText(completed_complaints);
                                Tv_inprogress_complaints.setText(inprogress_complaints);
                                Tv_pending_complaints.setText(pending_complaints);
                                Tv_overdue_complaints.setText(overdue_complaints);
                                notification.setText(overdue_complaints);

//                                pieChart.getLegend().setEnabled(false);

                                AddValuesToPIEENTRY();
                                AddValuesToPieEntryLabels();

                                ArrayList<Integer> colors = new ArrayList<Integer>();
                                if (Integer.parseInt(completed_complaints) > 0) {
                                    colors.add(ContextCompat.getColor(getActivity(), R.color.completed));
                                }
                                if (Integer.parseInt(inprogress_complaints) > 0) {
                                    colors.add(ContextCompat.getColor(getActivity(), R.color.inprogress));
                                }
                                if (Integer.parseInt(pending_complaints) > 0) {
                                    colors.add(ContextCompat.getColor(getActivity(), R.color.pending));
                                }
                                if (Integer.parseInt(overdue_complaints) > 0) {
                                    colors.add(ContextCompat.getColor(getActivity(), R.color.overdue));
                                }

                                pieDataSet = new PieDataSet(entries, "");
                                pieData = new PieData(PieEntryLabels, pieDataSet);
                                //create own formater for values
                                pieData.setValueFormatter(new MyValueFormatter());
                                pieDataSet.setColors(colors);
                                pieChart.setDescription("");
                                pieChart.setCenterText("Total Complaints");
                                pieChart.setData(pieData);
                                pieChart.animateY(2000);


                                //set notification for overdue complaints
                                if (!overdue_complaints.equals("0")) {
                                    Log.e("Notification", "NotificationTriggered");
                                    scheduleNotification
                                            (getNotification("Tap to View and Resolve overdue complaints"));
                                }

                            }


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
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onClick(View view) {

        int vID = view.getId();

        switch (vID) {

            case R.id.notification:
                OverDueComlpaintsFragment overDueComlpaintsFragment = new OverDueComlpaintsFragment();
                (getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, overDueComlpaintsFragment)
                        .addToBackStack("CDF")
                        .commit();
                break;

            case R.id.lallcomplaints:
                AllComplaintsFragment allComplaintsFragment = new AllComplaintsFragment();
                (getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, allComplaintsFragment)
                        .addToBackStack("CDF")
                        .commit();
                break;

            case R.id.lcompletedcomplaints:
                CompletedComlpaintsFragment completedComlpaintsFragment = new CompletedComlpaintsFragment();
                (getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, completedComlpaintsFragment)
                        .addToBackStack("CDF")
                        .commit();
                break;

            case R.id.lpendingcomplaints:
                PendingComlpaintsFragment pendingComlpaintsFragment = new PendingComlpaintsFragment();
                (getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, pendingComlpaintsFragment)
                        .addToBackStack("CDF")
                        .commit();
                break;

            case R.id.linprogresscomplaints:
                InprogressComlpaintsFragment inprogressComlpaintsFragment = new InprogressComlpaintsFragment();
                (getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, inprogressComlpaintsFragment)
                        .addToBackStack("CDF")
                        .commit();
                break;

            case R.id.loverduecomplaints:
                OverDueComlpaintsFragment overDueComlpaintsFragment1 = new OverDueComlpaintsFragment();
                (getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, overDueComlpaintsFragment1)
                        .addToBackStack("CDF")
                        .commit();
                break;

        }
    }


    //generate notifications
    private void scheduleNotification(Notification notification) {

        Intent notificationIntent = new Intent(getActivity(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, RemainingHour, pendingIntent);
    }

    private Notification getNotification(String content) {

        //pending intent to redirect app to specific activity on notification click
        Intent intent = new Intent(getActivity(), OverDueComlpaintsFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Notification.Builder builder = new Notification.Builder(getActivity());
        builder.setContentTitle("You have Overdue Complaints");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.localgov);
        builder.setContentIntent(pendingIntent);//call pending intent here
        return builder.build();
    }



    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0"); // use one decimal if needed
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value) + ""; // e.g. append a dollar-sign
        }
    }

    public void AddValuesToPIEENTRY() {

        if (Integer.parseInt(completed_complaints) > 0) {
            entries.add(new BarEntry(Integer.parseInt(completed_complaints), 0));
        }
        if (Integer.parseInt(inprogress_complaints) > 0) {
            entries.add(new BarEntry(Integer.parseInt(inprogress_complaints), 1));
        }
        if (Integer.parseInt(pending_complaints) > 0) {
            entries.add(new BarEntry(Integer.parseInt(pending_complaints), 2));
        }
        if (Integer.parseInt(overdue_complaints) > 0) {
            entries.add(new BarEntry(Integer.parseInt(overdue_complaints), 3));
        }

    }

    public void AddValuesToPieEntryLabels() {

        if (Integer.parseInt(completed_complaints) > 0) {
            PieEntryLabels.add("Completed");
        }
        if (Integer.parseInt(inprogress_complaints) > 0) {
            PieEntryLabels.add("InProgress");
        }
        if (Integer.parseInt(pending_complaints) > 0) {
            PieEntryLabels.add("Pending");
        }
        if (Integer.parseInt(overdue_complaints) > 0) {
            PieEntryLabels.add("OverDue");
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
