package com.example.coftea.Cashier.report;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.coftea.data.OrderStatus;
import com.example.coftea.databinding.FragmentReportBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.OutputStream;
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
        btnReportExport.setOnClickListener(view -> {
            exportToPdf();
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
    private void exportToPdf() {
        try {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, "table_export.pdf");

            createPdfLauncher.launch(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error exporting to PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<Intent> createPdfLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // Handle the result here
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            try (OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri)) {
                                if (outputStream != null) {
                                    Document document = new Document();
                                    PdfWriter.getInstance(document, outputStream);
                                    document.open();

                                    PdfPTable pdfPTable = new PdfPTable(3);

                                    // Add table headers
                                    pdfPTable.addCell("Name");
                                    pdfPTable.addCell("Age");
                                    pdfPTable.addCell("Country");

                                    // Add table data
                                    for (int i = 0; i < 4; i++) {
                                        pdfPTable.addCell("TEST1".toString());
                                        pdfPTable.addCell("TEST2".toString());
                                        pdfPTable.addCell("TEST3".toString());
                                    }

                                    document.add(pdfPTable);
                                    document.close();

                                    Toast.makeText(getContext(), "Table exported to PDF", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Error exporting to PDF", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
    );
}