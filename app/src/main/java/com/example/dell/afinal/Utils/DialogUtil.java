package com.example.dell.afinal.Utils;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.dell.afinal.R;

public class DialogUtil {

    public static void showDialog(Context context, String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(content)
                .setCancelable(true)
                .show();
    }
}
