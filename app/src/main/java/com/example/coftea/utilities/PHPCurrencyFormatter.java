package com.example.coftea.utilities;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class PHPCurrencyFormatter {
    private static PHPCurrencyFormatter instance;
    private final NumberFormat currencyFormat;

    private PHPCurrencyFormatter() {
        // Create a NumberFormat instance for the Philippines locale and Philippine Peso currency
        Locale philippinesLocale = new Locale("fil", "PH");
        currencyFormat = NumberFormat.getCurrencyInstance(philippinesLocale);

        // Set the currency to Philippine Peso (PHP)
        currencyFormat.setCurrency(Currency.getInstance("PHP"));
    }

    public static synchronized PHPCurrencyFormatter getInstance() {
        if (instance == null) {
            instance = new PHPCurrencyFormatter();
        }
        return instance;
    }

    public String formatAsPHP(double value) {
        // Format the float value as a currency string in Philippine Peso
        return currencyFormat.format(value);
    }
}
