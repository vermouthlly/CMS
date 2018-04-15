package com.example.dell.afinal;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by roger on 2018/1/7.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
