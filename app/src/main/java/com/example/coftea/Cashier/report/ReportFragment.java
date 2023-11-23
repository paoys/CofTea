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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coftea.Cashier.order.CartItem;
import com.example.coftea.Cashier.order.ReceiptEntry;
import com.example.coftea.R;
import com.example.coftea.data.OrderStatus;
import com.example.coftea.data.Product;
import com.example.coftea.databinding.FragmentReportBinding;
import com.example.coftea.utilities.DateHandler;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class ReportFragment extends Fragment implements ReportDatePickerFragment.OnDateSetListener {
//    BarDataSet barDataSet;
//    ArrayList barArraylist;
//    BarData barData;
//    BarChart barChart;
    private LineChart lineChart;
    private ReportType reportType;
    private Spinner sReportType;
//    private Button btnReportDaily, btnReportWeekly, btnReportMonthly, btnReportQuarterly, btnReportYearly;
    private Button btnReportDateFrom, btnReportDateTo, btnReportExport, btnReportGenerate;
    private Date dateFrom, dateTo;
    private XAxis xAxis;
    private ReportFilterGenerator filterGenerator;
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
        reportType = ReportType.Daily;
        cartItems = new ArrayList<>();
        filterGenerator = new ReportFilterGenerator();
        sReportType = binding.sReportType;
        lineChart = binding.lineChartReport;

        btnReportExport = binding.btnReportExport;
        btnReportGenerate = binding.btnReportGenerate;
        btnReportGenerate.setEnabled(false);
        btnReportDateTo = binding.btnReportDateTo;
        btnReportDateFrom = binding.btnReportDateFrom;

        ArrayAdapter<CharSequence> reportsAdapter = ArrayAdapter.createFromResource(
                binding.getRoot().getContext(),
                R.array.reports_type,
                android.R.layout.simple_spinner_item
        );

        reportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sReportType.setAdapter(reportsAdapter);
        sReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reportType = ReportType.valueOf(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        reportViewModel = new ReportViewModel();


//        barArraylist = new ArrayList<>();
//        barDataSet = new BarDataSet(barArraylist,"Reports");
//        barData = new BarData(barDataSet);
//
//        barChart.setData(barData);
//        barDataSet.setColors(Color.BLACK);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(12f);
//        barChart.getDescription().setEnabled(true);
//
        xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initialListen();
        }

        btnReportDateFrom.setOnClickListener(view -> {
            showDatePickerDialog(null, dateTo, ReportDatePickerFragment.ReportDateType.FROM);
        });

        btnReportDateTo.setOnClickListener(view -> {
            showDatePickerDialog(dateFrom, null, ReportDatePickerFragment.ReportDateType.TO);
        });

        btnReportGenerate.setOnClickListener(view -> generateReportButton());

        btnReportExport.setOnClickListener(view -> {
            exportToPdf();
        });
    }

    private void generateReportButton(){
        if(dateFrom == null && dateTo == null){
            Toast.makeText(getContext(), R.string.cashier_report_select_dates, Toast.LENGTH_SHORT).show();
            return;
        }
        if(dateTo == null){
            Toast.makeText(getContext(), R.string.cashier_report_select_date_to, Toast.LENGTH_SHORT).show();
            return;
        }
        if(dateFrom == null){
            Toast.makeText(getContext(), R.string.cashier_report_select_date_from, Toast.LENGTH_SHORT).show();
            return;
        }
        onGenerateReport();
    }

    private void onGenerateReport(){
        Date from;
        Date to;
        filters = null;
        switch (reportType){
            case Daily:
                from = DateHandler.getStartOfDay(dateFrom);
                to =  DateHandler.getEndOfDay(dateTo);
                filters = ReportFilterGenerator.getReportDailyFilters(from, to);
                Log.e("Report Daily", String.valueOf(from));
                Log.e("Report Daily", String.valueOf(to));
                break;
            case Weekly:
                from = DateHandler.getStartOfWeek(dateFrom);
                to = DateHandler.getEndOfWeek(dateTo);
                filters = ReportFilterGenerator.getReportWeeklyFilters(from, to);
                Log.e("Report Weekly", String.valueOf(from));
                Log.e("Report Weekly", String.valueOf(to));
                break;
            case Monthly:
                from = DateHandler.getStartOfMonth(dateFrom);
                to = DateHandler.getEndOfMonth(dateTo);
                filters = ReportFilterGenerator.getReportMonthlyFilters(from, to);
                Log.e("Report Monthly", String.valueOf(from));
                Log.e("Report Monthly", String.valueOf(to));
                break;
            case Quarterly:
                from = DateHandler.getStartOfQuarter(dateFrom);
                to =  DateHandler.getEndOfQuarter(dateTo);
                filters = ReportFilterGenerator.getReportQuarterlyFilters(from, to);
                Log.e("Report Quarterly", String.valueOf(from));
                Log.e("Report Quarterly", String.valueOf(to));
                break;
            case Yearly:
                from = DateHandler.getStartOfYear(dateFrom);
                to =  DateHandler.getEndOfYear(dateTo);
                filters = ReportFilterGenerator.getReportYearlyFilters(from, to);
                Log.e("Report Yearly", String.valueOf(from));
                Log.e("Report Yearly", String.valueOf(to));
                break;
            default:
                from = null;
                to = null;
                filters = null;
        }

        if(from == null || to == null || filters == null){
            Toast.makeText(getContext(),"Generate Report Error: Invalid Filter", Toast.LENGTH_SHORT).show();
            return;
        }
        processGenerateReport();
        reportViewModel.getFilteredReport(OrderStatus.DONE, from.getTime(), to.getTime());
    }

    private List<ReportFilter> filters = new ArrayList<>();
    public void processGenerateReport(){
        Log.e("Generated Report", String.valueOf(filters));
        for(ReportFilter filter : filters){
            Log.e("===========", String.valueOf(filter.getLabel()));
            Log.e("Filter Start", String.valueOf(filter.getFilterStart()));
            Log.e("Filter End", String.valueOf(filter.getFilterEnd()));
        }
    }

    public void showDatePickerDialog(Date from ,Date to, ReportDatePickerFragment.ReportDateType type) {
        ReportDatePickerFragment datePickerFragment = new ReportDatePickerFragment(from, to);
        datePickerFragment.setOnDateSetListener(this, type);
        datePickerFragment.show(getParentFragmentManager(), "datePicker");
    }
    @Override
    public void onDateSet(String selectedDate, Date date, ReportDatePickerFragment.ReportDateType reportDateType) {
        if(reportDateType == ReportDatePickerFragment.ReportDateType.FROM){
            btnReportDateFrom.setText(selectedDate);
            dateFrom = date;
        } else if (reportDateType == ReportDatePickerFragment.ReportDateType.TO) {
            btnReportDateTo.setText(selectedDate);
            dateTo = date;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialListen(){
        reportViewModel.productList.observe(getViewLifecycleOwner(), products -> {
            if(products == null) return;
            if(products.size() == 0) return;
            this.products = products;
            Log.e("Products", String.valueOf(products));
            btnReportGenerate.setEnabled(true);
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
                List<Entry> entries = new ArrayList<>();
                LineDataSet set = new LineDataSet(entries, "Empty");
                LineData lineData = new LineData(set);
                lineChart.setData(lineData);
                lineChart.invalidate();
                cartItems.clear();
                return;
            }
            Log.e("Receipts Count", String.valueOf(receiptEntries.size()));
            for(ReceiptEntry entry: receiptEntries){
                Log.e("Receipt", String.valueOf(entry.getDate()));
            }
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
//        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < this.products.size(); i++) {
            Product product = this.products.get(i);

            ArrayList<CartItem> list = cartItems.stream()
                    .filter(item -> Objects.equals(item.getId(), product.getId()))
                    .collect(Collectors.toCollection(ArrayList::new));

            double total = list.stream().mapToDouble(CartItem::getTotalPrice).sum();

            entries.add(new Entry(i, (float) total));
        }
        LineDataSet set = new LineDataSet(entries, "TEST");
        LineData lineData = new LineData(set);
        lineChart.setData(lineData);
        lineChart.invalidate();
//        barDataSet = new BarDataSet(barEntries,"Reports");
//        barData = new BarData(barDataSet);
//        barChart.setData(barData);
//        barChart.invalidate();
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