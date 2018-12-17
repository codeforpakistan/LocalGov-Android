package com.zeeroapps.wssp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.zeeroapps.wssp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;


public class PieChartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SharedPreferences sp;
    int inprogress_complaints, pending_complaints, completed_complaints
            ,overdue_complaints;
    PieChart pieChart;
    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;
    ArrayList<Entry> entries ;


    public PieChartFragment() {
        // Required empty public constructor
    }


    public static PieChartFragment newInstance() {

        Bundle args = new Bundle();

        PieChartFragment fragment = new PieChartFragment();
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
        View v = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        pieChart = (PieChart) v.findViewById(R.id.chart1);
        sp = inflater.getContext().getSharedPreferences(getString(R.string.sp), Context.MODE_PRIVATE);
        completed_complaints = sp.getInt("completed", 0);
        pending_complaints = sp.getInt("pending", 0);
        inprogress_complaints = sp.getInt("inprogress", 0);
        overdue_complaints = sp.getInt("overdue", 0);

//        Toast.makeText(getActivity(), completed_complaints+"", Toast.LENGTH_SHORT).show();


        //adding values to pie chart
        entries = new ArrayList<>();
        PieEntryLabels = new ArrayList<String>();
        AddValuesToPIEENTRY();
        AddValuesToPieEntryLabels();


        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(ContextCompat.getColor(getActivity(), R.color.completed));
        colors.add(ContextCompat.getColor(getActivity(), R.color.inprogress));
        colors.add(ContextCompat.getColor(getActivity(), R.color.pending));
        colors.add(ContextCompat.getColor(getActivity(), R.color.overdue));

        pieDataSet = new PieDataSet(entries, "");
        pieData = new PieData(PieEntryLabels, pieDataSet);
        //create own formater for values
        pieData.setValueFormatter(new MyValueFormatter());
        pieDataSet.setColors(colors);
        pieChart.setDescription("");
        pieChart.setCenterText("Total Complaints");
        pieChart.setData(pieData);
        pieChart.animateY(2000);

        return v;
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


    public void AddValuesToPIEENTRY(){

        if(completed_complaints > 0){
            entries.add(new BarEntry(completed_complaints, 0));
        }
        if(inprogress_complaints > 0){
            entries.add(new BarEntry(inprogress_complaints, 1));
        }
        if(pending_complaints > 0) {
            entries.add(new BarEntry(pending_complaints, 2));
        }
        if(overdue_complaints > 0) {
            entries.add(new BarEntry(overdue_complaints, 3));
        }

    }

    public void AddValuesToPieEntryLabels(){

            PieEntryLabels.add("Completed");
            PieEntryLabels.add("InProgress");
            PieEntryLabels.add("Pending");
            PieEntryLabels.add("OverDue");

    }



    @Override
    public void onDetach() {
        super.onDetach();
    }


}
