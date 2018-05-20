package com.example.dell.afinal.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dell.afinal.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyNotificationActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_my_notification);

        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}