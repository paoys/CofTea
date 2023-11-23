package com.example.coftea.Cashier.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportFilterGenerator {
    public static List<ReportFilter> getReportDailyFilters(Date startDate, Date endDate) {
        List<ReportFilter> reportFilters = new ArrayList<>();

        // Generate ReportFilters for each day between start and end
        Date currentDate = startDate;
        while (!currentDate.after(endDate)) {
            String dateText = formatDate(currentDate);
            Date dayStart = currentDate;
            Date dayEnd = getEndOfDay(currentDate);

            ReportFilter reportFilter = new ReportFilter(dateText, dayStart, dayEnd);
            reportFilters.add(reportFilter);

            // Move to the next day
            currentDate = getNextDay(currentDate);
        }

        return reportFilters;
    }

    private static Date getNextDay(Date date) {
        return new Date(date.getTime() + 24 * 60 * 60 * 1000);
    }

    private static Date getEndOfDay(Date date) {
        return new Date(date.getTime() + 24 * 60 * 60 * 1000 - 1);
    }

    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static List<ReportFilter> getReportWeeklyFilters(Date startDate, Date endDate) {
        List<ReportFilter> reportFilters = new ArrayList<>();

        // Find the start of the week for the given start date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date currentStartDate = calendar.getTime();

        // Find the end of the week for the given end date
        calendar.setTime(endDate);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
        Date currentEndDate = calendar.getTime();

        // Generate ReportFilters for each whole week between start and end
        while (!currentEndDate.after(endDate)) {
            String label = formatDateRange(currentStartDate, currentEndDate);
            ReportFilter reportFilter = new ReportFilter(label, currentStartDate, currentEndDate);
            reportFilters.add(reportFilter);

            // Move to the next week
            calendar.setTime(currentEndDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            currentStartDate = calendar.getTime();

            calendar.add(Calendar.DAY_OF_MONTH, 6); // Move to the end of the week
            currentEndDate = calendar.getTime();
        }

        return reportFilters;
    }

    private static String formatDateRange(Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(startDate) + " to " + sdf.format(endDate);
    }

    public static List<ReportFilter> getReportMonthlyFilters(Date startDate, Date endDate) {
        List<ReportFilter> reportFilters = new ArrayList<>();

        // Find the start of the month for the given start date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date currentStartDate = calendar.getTime();

        // Find the end of the month for the given end date
        calendar.setTime(endDate);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date currentEndDate = calendar.getTime();

        // Generate ReportFilters for each whole month between start and end
        while (!currentEndDate.after(endDate)) {
            String label = formatMonth(currentStartDate);
            ReportFilter reportFilter = new ReportFilter(label, currentStartDate, currentEndDate);
            reportFilters.add(reportFilter);

            // Move to the next month
            calendar.setTime(currentEndDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Move to the start of the month
            currentStartDate = calendar.getTime();

            calendar.add(Calendar.MONTH, 1); // Move to the next month
            calendar.add(Calendar.DAY_OF_MONTH, -1); // Move to the end of the previous month
            currentEndDate = calendar.getTime();
        }

        return reportFilters;
    }

    private static String formatMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        return sdf.format(date);
    }

    public static List<ReportFilter> getReportQuarterlyFilters(Date startDate, Date endDate) {
        List<ReportFilter> reportFilters = new ArrayList<>();

        // Find the start of the quarter for the given start date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        setQuarterStart(calendar);
        Date currentStartDate = calendar.getTime();

        // Find the end of the quarter for the given end date
        calendar.setTime(endDate);
        setQuarterEnd(calendar);
        Date currentEndDate = calendar.getTime();

        // Generate ReportFilters for each whole quarter between start and end
        while (!currentEndDate.after(endDate)) {
            String label = formatQuarter(currentStartDate);
            ReportFilter reportFilter = new ReportFilter(label, currentStartDate, currentEndDate);
            reportFilters.add(reportFilter);

            // Move to the next quarter
            calendar.setTime(currentEndDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
            setQuarterStart(calendar);
            currentStartDate = calendar.getTime();

            setQuarterEnd(calendar);
            currentEndDate = calendar.getTime();
        }

        return reportFilters;
    }

    private static void setQuarterStart(Calendar calendar) {
        int currentMonth = calendar.get(Calendar.MONTH);
        int startMonth = (currentMonth / 3) * 3; // Calculate the start month of the quarter
        calendar.set(Calendar.MONTH, startMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Move to the first day of the month
    }

    private static void setQuarterEnd(Calendar calendar) {
        setQuarterStart(calendar);
        calendar.add(Calendar.MONTH, 2); // Move to the end of the quarter
        calendar.add(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
    }

    private static String formatQuarter(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int quarter = (calendar.get(Calendar.MONTH) / 3) + 1;
        return "Q" + quarter + " " + sdf.format(date);
    }

    public static List<ReportFilter> getReportYearlyFilters(Date startDate, Date endDate) {
        List<ReportFilter> reportFilters = new ArrayList<>();

        // Find the start of the year for the given start date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        setYearStart(calendar);
        Date currentStartDate = calendar.getTime();

        // Find the end of the year for the given end date
        calendar.setTime(endDate);
        setYearEnd(calendar);
        Date currentEndDate = calendar.getTime();

        // Generate ReportFilters for each whole year between start and end
        while (!currentEndDate.after(endDate)) {
            String label = formatYear(currentStartDate);
            ReportFilter reportFilter = new ReportFilter(label, currentStartDate, currentEndDate);
            reportFilters.add(reportFilter);

            // Move to the next year
            calendar.setTime(currentEndDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
            setYearStart(calendar);
            currentStartDate = calendar.getTime();

            setYearEnd(calendar);
            currentEndDate = calendar.getTime();
        }

        return reportFilters;
    }

    public static void setYearStart(Calendar calendar) {
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
    }

    public static void setYearEnd(Calendar calendar) {
        setYearStart(calendar);
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
    }

    private static String formatYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(date);
    }
}
