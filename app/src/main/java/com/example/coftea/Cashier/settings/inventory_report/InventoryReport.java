package com.example.coftea.Cashier.settings.inventory_report;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coftea.Cashier.stock.MainModelIngredients;
import com.example.coftea.R;
import com.example.coftea.repository.RealtimeDB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryReport extends AppCompatActivity {
    private RealtimeDB<MainModelIngredients> realtimeDB;
    private TableLayout tableLayout;
    private List<MainModelIngredients> ingredientList;
    private Button btnReportExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_report);

        realtimeDB = new RealtimeDB<>("Ingredients");
        tableLayout = findViewById(R.id.tableLayout);
        ingredientList = new ArrayList<>();

        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView nameHeaderTextView = createBoldTextView("Name");
        TextView stockHeaderTextView = createBoldTextView("Stock");

        headerRow.addView(nameHeaderTextView);
        headerRow.addView(stockHeaderTextView);

        tableLayout.addView(headerRow);

        realtimeDB.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ingredientList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MainModelIngredients ingredient = dataSnapshot.getValue(MainModelIngredients.class);
                    if (ingredient != null) {
                        ingredientList.add(ingredient);
                    }
                }

                displayFilteredData(ingredientList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InventoryReport.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        btnReportExport = findViewById(R.id.btnReportExport);
        btnReportExport.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exportToPdf();
            }
        });
    }

    private TextView createBoldTextView(String text) {
        TextView textView = createTextView(text);
        textView.setTypeface(null, Typeface.BOLD);
        return textView;
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        textView.setBackgroundResource(R.drawable.table_cell_border);
        return textView;
    }

    private void filter(String searchText) {
        List<MainModelIngredients> filteredList = new ArrayList<>();

        for (MainModelIngredients ingredient : ingredientList) {
            if (ingredient.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(ingredient);
            }
        }

        displayFilteredData(filteredList);
    }

    private void displayFilteredData(List<MainModelIngredients> filteredList) {
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);

        for (MainModelIngredients ingredient : filteredList) {
            TableRow row = new TableRow(InventoryReport.this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.setBackgroundResource(R.drawable.table_row_border);

            TextView nameTextView = createBoldTextView(ingredient.getName());
            TextView stockTextView = createTextView(ingredient.getQty());

            row.addView(nameTextView);
            row.addView(stockTextView);

            tableLayout.addView(row);
        }
    }

    private final ActivityResultLauncher<Intent> createPdfLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        try {
                            Uri uri = intent.getData();
                            if (uri != null) {
                                Document document = new Document();
                                PdfWriter.getInstance(document, getContentResolver().openOutputStream(uri));
                                document.open();

                                PdfPTable pdfTable = new PdfPTable(2); // Create a table with 2 columns

                                // Add table headers
                                PdfPCell cell1 = new PdfPCell(new Phrase("Name"));
                                PdfPCell cell2 = new PdfPCell(new Phrase("Stock"));
                                pdfTable.addCell(cell1);
                                pdfTable.addCell(cell2);

                                // Add data to the PDF table
                                for (MainModelIngredients ingredient : ingredientList) {
                                    pdfTable.addCell(ingredient.getName());
                                    pdfTable.addCell(ingredient.getQty());
                                }

                                // Add title to the PDF document before the table
                                Paragraph title = new Paragraph("INVENTORY REPORT", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
                                title.setAlignment(Element.ALIGN_CENTER);
                                document.add(title);

                                // Add a line break after the title
                                document.add(Chunk.NEWLINE);

                                document.add(pdfTable);

                                document.close();
                                Toast.makeText(InventoryReport.this, "PDF exported successfully", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException | DocumentException e) {
                            e.printStackTrace();
                            Log.e("Export Report Error", String.valueOf(e));
                            Log.e("Export Report Error", String.valueOf(e.getMessage()));
                            Toast.makeText(InventoryReport.this, "Error exporting to PDF", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void exportToPdf() {
        if (isExternalStorageWritable()) {
            try {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_TITLE, "inventory_report.pdf");
                createPdfLauncher.launch(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Export Report Error", String.valueOf(e));
                Log.e("Export Report Error", String.valueOf(e.getMessage()));
                Toast.makeText(InventoryReport.this, "Error exporting to PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(InventoryReport.this, "External storage not writable", Toast.LENGTH_SHORT).show();
        }
    }


    // Check if external storage is writable
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

}
