package com.example.coftea.utilities;

import android.text.InputFilter;
import android.widget.EditText;
import android.widget.Toast;

public class InputFieldFilter {

    final int minRange = 1;
    final int maxRange = 99;

    private static InputFieldFilter instance;
    public static synchronized InputFieldFilter getInstance() {
        if (instance == null) {
            instance = new InputFieldFilter();
        }
        return instance;
    }

    public InputFilter createNumberFilter (EditText editText) {
        InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
            String inputText = dest.toString() + source.toString();
            try {
                int inputValue = Integer.parseInt(inputText);

                if (inputValue >= minRange && inputValue <= maxRange) {
                    // Input is within the valid range
                    return null;
                } else {
                    // Input is invalid, show a toast or handle it as needed
                    Toast.makeText(editText.getContext(), "Invalid Amount", Toast.LENGTH_SHORT).show();
                    return "";
                }
            } catch (NumberFormatException e) {
                // Input is not a valid number
                Toast.makeText(editText.getContext(), "Invalid Amount", Toast.LENGTH_SHORT).show();
                return "";
            }
        };

        return inputFilter;
    }
}
