package com.example.dell.afinal.Utils;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackBarUtil {

    public static void make(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();
    }
}
