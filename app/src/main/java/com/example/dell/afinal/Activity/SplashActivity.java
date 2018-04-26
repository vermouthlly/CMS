package com.example.dell.afinal.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.User;


import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Bmob.initialize(this, "430bfefb7fad055dc47f06a0ba6f73e4");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 已登录直接跳转到主界面, 未登录则跳转到登录界面
                if (BmobUser.getCurrentUser(User.class) != null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    SplashActivity.this.finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                }
            }
        }, 2000);
    }
}
