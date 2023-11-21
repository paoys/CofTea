package com.example.coftea.Cashier.report;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.Cashier.order.ReceiptEntry;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.data.Product;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

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
    private ArrayList<Product> products;
    private ArrayList<CartItem> cartItems;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        init();
        return root;
    }

    private void init(){
        cartItems = new ArrayList<>();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initialListen();
        }

        btnReportDaily.setOnClickListener(view -> {
            barArraylist.clear();
            Date currentDate = new Date();
            Date from = getStartOfDay(currentDate);
            Date to = getEndOfDay(currentDate);
            reportViewModel.getDailyReport(OrderStatus.DONE, from.getTime(), to.getTime());
            updateButtonState(btnReportDaily);
        });
        btnReportWeekly.setOnClickListener(view -> {
            barArraylist.clear();
            Date currentDate = new Date();
            Date from = getStartOfWeek(currentDate);
            Date to = getEndOfWeek(currentDate);
            reportViewModel.getWeeklyReport(OrderStatus.DONE, from.getTime(), to.getTime());
            updateButtonState(btnReportWeekly);
        });
        btnReportMonthly.setOnClickListener(view -> {
            barArraylist.clear();
            Date currentDate = new Date();
            Date from = getStartOfMonth(currentDate);
            Date to = getEndOfMonth(currentDate);
            reportViewModel.getMonthlyReport(OrderStatus.DONE, from.getTime(), to.getTime());
            updateButtonState(btnReportMonthly);
        });
        btnReportQuarterly.setOnClickListener(view -> {
            barArraylist.clear();
            reportViewModel.getQuarterlyReport(OrderStatus.DONE, 1681916247000L, 1702997847000L);
            updateButtonState(btnReportQuarterly);
        });
        btnReportYearly.setOnClickListener(view -> {
            barArraylist.clear();
            reportViewModel.getYearlyReport(OrderStatus.DONE, 1681916247000L, 1702997847000L);
            updateButtonState(btnReportYearly);
        });
        btnReportExport.setOnClickListener(view -> {
            exportToPdf();
        });
    }

    private void updateButtonState(Button activeButton){
        btnReportDaily.setEnabled(true);
        btnReportWeekly.setEnabled(true);
        btnReportMonthly.setEnabled(true);
        btnReportQuarterly.setEnabled(true);
        btnReportYearly.setEnabled(true);

        activeButton.setEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialListen(){
        reportViewModel.productList.observe(getViewLifecycleOwner(), products -> {
            if(products == null) return;
            if(products.size() == 0) return;
            this.products = products;
            Log.e("Products", String.valueOf(products));
            listen();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void listen(){
        reportViewModel.labels.observe(getViewLifecycleOwner(), strings -> {
            if(strings == null) return;
            xAxis.setValueFormatter(new IndexAxisValueFormatter(strings));
        });

        reportViewModel.receiptEntryList.observe(getViewLifecycleOwner(), receiptEntries -> {
            if(receiptEntries == null) return;
            if(receiptEntries.size() == 0) {
                cartItems.clear();
                return;
            }
            Log.e("Receipts Count", String.valueOf(receiptEntries.size()));
            ArrayList<CartItem> _cartItems = new ArrayList<>();
            for ( ReceiptEntry entry : receiptEntries){
                _cartItems.addAll(entry.getCartItems());
            }
            cartItems = _cartItems;
            updateTable(_cartItems);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateTable(ArrayList<CartItem> cartItems){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < this.products.size(); i++) {
            Product product = this.products.get(i);

            ArrayList<CartItem> list = cartItems.stream()
                    .filter(item -> Objects.equals(item.getId(), product.getId()))
                    .collect(Collectors.toCollection(ArrayList::new));

            double total = list.stream().mapToDouble(CartItem::getTotalPrice).sum();

            barEntries.add(new BarEntry(i, (float) total));
        }

        barDataSet = new BarDataSet(barEntries,"Reports");
        barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();
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


    private static Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
    private static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }
    private static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private static Date getStartOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Set the first day of the week to Sunday
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        // Set the current date
        calendar.setTime(date);
        // Find the first day of the week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        // Set the time components to their minimum values (midnight)
        resetTimeComponents(calendar);
        return calendar.getTime();
    }

    private static Date getEndOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Set the first day of the week to Sunday
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        // Set the current date
        calendar.setTime(date);
        // Find the last day of the week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + Calendar.DAY_OF_WEEK - 1);
        // Set the time components to their maximum values (11:59:59.999 PM)
        setTimeComponentsToMaximum(calendar);
        return calendar.getTime();
    }

    private static void resetTimeComponents(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeComponentsToMaximum(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
}