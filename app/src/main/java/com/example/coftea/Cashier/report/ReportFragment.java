package com.example.coftea.Cashier.report;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.coftea.data.OrderStatus;
import com.example.coftea.databinding.FragmentReportBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class ReportFragment extends Fragment {
    BarDataSet barDataSet;
    ArrayList barArraylist;
    LineChart lineChart;
    BarData barData;
    BarChart barChart;
    private Button btnReportDaily, btnReportWeekly, btnReportMonthly, btnReportQuarterly, btnReportYearly;
    private Button btnReportExport;
    private XAxis xAxis;
    private FragmentReportBinding binding;
    private ReportViewModel reportViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        init();
        listen();
        return root;
    }

    private void init(){

        barChart = binding.chartReport;
        btnReportDaily = binding.btnReportDaily;
        btnReportWeekly = binding.btnReportWeekly;
        btnReportMonthly = binding.btnReportMonthly;
        btnReportQuarterly = binding.btnReportQuarterly;
        btnReportYearly = binding.btnReportYearly;
        btnReportExport = binding.btnReportExport;

        reportViewModel = new ReportViewModel();

        barArraylist = new ArrayList<>();
        barDataSet = new BarDataSet(barArraylist,"Reports");
        barData = new BarData(barDataSet);

        barChart.setData(barData);
        barDataSet.setColors(Color.BLACK);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barChart.getDescription().setEnabled(true);

        xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        btnReportDaily.setOnClickListener(view -> {
            barArraylist.clear();
            reportViewModel.getDailyReport(OrderStatus.DONE, 1681916247000L, 1702997847000L);
        });
        btnReportWeekly.setOnClickListener(view -> {
            barArraylist.clear();
            reportViewModel.getWeeklyReport(OrderStatus.DONE, 1681916247000L, 1702997847000L);
        });
        btnReportMonthly.setOnClickListener(view -> {
            barArraylist.clear();
            reportViewModel.getMonthlyReport(OrderStatus.DONE, 1681916247000L, 1702997847000L);
        });
        btnReportQuarterly.setOnClickListener(view -> {
            barArraylist.clear();
            reportViewModel.getQuarterlyReport(OrderStatus.DONE, 1681916247000L, 1702997847000L);
        });
        btnReportYearly.setOnClickListener(view -> {
            barArraylist.clear();
            reportViewModel.getYearlyReport(OrderStatus.DONE, 1681916247000L, 1702997847000L);
        });
    }

    private void listen(){
        reportViewModel.labels.observe(getViewLifecycleOwner(), strings -> {
            if(strings == null) return;
            xAxis.setValueFormatter(new IndexAxisValueFormatter(strings));
        });
        reportViewModel.barArraylist.observe(getViewLifecycleOwner(), arrayList -> {
            if(arrayList == null) return;
            barArraylist = arrayList;
        });
    }

}