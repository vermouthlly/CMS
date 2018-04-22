package com.example.dell.afinal;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
