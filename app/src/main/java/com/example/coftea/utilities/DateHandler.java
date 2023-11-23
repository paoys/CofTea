package com.example.coftea.utilities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateHandler {


    public static Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
    public static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }
    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getStartOfWeek(Date date) {
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

    public static Date getEndOfWeek(Date date) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static int getQuarter(Month month) {
        return (month.getValue() - 1) / 3 + 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Month getQuarterStartMonth(Month month) {
        int quarter = getQuarter(month);
        return Month.of((quarter - 1) * 3 + 1);
    }

    public static Date getStartOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Set to the beginning of the year
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getEndOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Set to the end of the year
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public static Date getStartOfQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Determine the quarter and set to the beginning of that quarter
        int currentQuarter = (calendar.get(Calendar.MONTH) / 3) * 3; // Calculate the start month of the quarter
        calendar.set(Calendar.MONTH, currentQuarter);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getEndOfQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Determine the quarter and set to the end of that quarter
        int currentQuarter = (calendar.get(Calendar.MONTH) / 3) * 3; // Calculate the start month of the quarter
        calendar.set(Calendar.MONTH, currentQuarter + 2); // Set to the last month of the quarter
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }
}
