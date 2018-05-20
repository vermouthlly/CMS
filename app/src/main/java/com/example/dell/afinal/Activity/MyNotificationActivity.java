package com.example.dell.afinal.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.NoticeFragmentAdapter;
import com.example.dell.afinal.Fragment.AllPostFragment;
import com.example.dell.afinal.Fragment.PopularPostFragment;
import com.example.dell.afinal.Fragment.SysNotificationFragment;
import com.example.dell.afinal.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MyNotificationActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.notification_tab)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_my_notification);

        unbinder = ButterKnife.bind(this);
        initToolbar();
        createViewPager();
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                MyNotificationActivity.this.finish();
                break;
        }
    }

    // 初始化标题
    private void initToolbar() {
        title.setText("我的通知");
    }

    // 搭建ViewPager
    private void createViewPager() {
        List<Fragment> list = new ArrayList<>();
        list.add(SysNotificationFragment.getInstance());
        list.add(SysNotificationFragment.getInstance());
        NoticeFragmentAdapter adapter = new NoticeFragmentAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}