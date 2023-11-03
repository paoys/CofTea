package com.example.coftea.Database;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

public class CustomDialog {

    public static void showNoInternetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need an internet connection to perform this action.");
        builder.setPositiveButton("OK", null); // You can add an OnClickListener if needed.
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

