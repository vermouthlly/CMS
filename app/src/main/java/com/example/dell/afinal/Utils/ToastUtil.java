package com.example.dell.afinal.Utils;

import android.content.Context;
import android.widget.Toast;

// Toast工具类，重新封装Toast使其使用更方便
public class ToastUtil {

    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
