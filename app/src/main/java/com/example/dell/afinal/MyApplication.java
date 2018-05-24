package com.example.dell.afinal;

import android.app.Application;

import com.facebook.stetho.Stetho;

import cn.bmob.v3.Bmob;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        Bmob.initialize(this, "430bfefb7fad055dc47f06a0ba6f73e4");
    }
}
