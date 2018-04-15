package com.example.dell.afinal.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.dell.afinal.Adapter.ViewPagerAdapter;
import com.example.dell.afinal.Fragment.BaseFragment;
import com.example.dell.afinal.Fragment.pFragment;
import com.example.dell.afinal.Fragment.sFragment;
import com.example.dell.afinal.R;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.jpeng.jptabbar.anno.Titles;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity {

    @Titles
    public static final String[] titles = {"日记","发现", "我的"};
    @SeleIcons
    public static final int[] selIcons = {R.mipmap.ic_diary_sel, R.mipmap.ic_find_sel, R.mipmap.ic_setting_sel};
    @NorIcons
    public static final int[] icons = {R.mipmap.ic_diary, R.mipmap.ic_find, R.mipmap.ic_setting};

    @BindView(R.id.main_jp_tabbar)
    JPTabBar mainJpTabbar;
    @BindView(R.id.vp_main)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupViewPager(mViewPager);

        mainJpTabbar.setContainer(mViewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(BaseFragment.newInstance());
        adapter.addFragment(pFragment.newInstance());
        adapter.addFragment(sFragment.newInstance());
        viewPager.setAdapter(adapter);
    }
}
