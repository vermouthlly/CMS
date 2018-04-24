package com.example.dell.afinal.Utils;


import android.content.Context;
import android.support.v7.app.AlertDialog;

public class DialogUtil {
    public static void showDialog(Context context, String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(content)
                .setCancelable(true)
                .show();
    }
}
