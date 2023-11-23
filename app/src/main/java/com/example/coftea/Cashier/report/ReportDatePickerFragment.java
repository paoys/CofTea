package com.example.coftea.Cashier.report;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.coftea.Customer.CustomerDashboard;

import java.util.Calendar;
import java.util.Date;

public class ReportDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public enum ReportDateType {
        FROM,
        TO
    }
    public interface OnDateSetListener {
        void onDateSet(String selectedDate, Date date, ReportDateType type );
    }

    private Date from,to;
    private ReportDateType reportDateType;
    private OnDateSetListener onDateSetListener;
    public ReportDatePickerFragment(Date from, Date to){
        this.from = from;
        this.to = to;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        if(from != null)
            datePickerDialog.getDatePicker().setMinDate(from.getTime());

        if(to != null)
            datePickerDialog.getDatePicker().setMaxDate(to.getTime());

        // Create a new instance of DatePickerDialog and return it
        return datePickerDialog;
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        Date selectedDate = calendar.getTime();

        String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);

        if (onDateSetListener != null) {
            onDateSetListener.onDateSet(formattedDate, selectedDate, this.reportDateType);
        }
    }

    public void setOnDateSetListener(OnDateSetListener listener, ReportDateType reportDateType) {

        this.onDateSetListener = listener;
        this.reportDateType = reportDateType;
    }
}
