package com.example.dell.afinal.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.example.dell.afinal.Adapter.ViewPagerAdapter;
import com.example.dell.afinal.Fragment.CourseFragment;
import com.example.dell.afinal.Fragment.DiscussionFragment;
import com.example.dell.afinal.Fragment.MineFragment;
import com.example.dell.afinal.R;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.jpeng.jptabbar.anno.Titles;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Titles
    public static final String[] titles = {"课程","讨论区", "我的"};
    @SeleIcons
    public static final int[] selIcons = {R.mipmap.ic_course_sel,
                                          R.mipmap.ic_discuss_sel,
                                          R.mipmap.ic_setting_sel};
    @NorIcons
    public static final int[] icons = {R.mipmap.ic_course,
                                       R.mipmap.ic_discuss,
                                       R.mipmap.ic_setting};

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

        // 开源控件JpTabbar用来绑定导航栏的icon和text
        mainJpTabbar.setContainer(mViewPager);
    }

    // 搭建ViewPager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(CourseFragment.newInstance());
        adapter.addFragment(DiscussionFragment.newInstance());
        adapter.addFragment(MineFragment.newInstance());
        viewPager.setAdapter(adapter);
    }

    // 动态申请权限
    private void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
            permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
            permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA}, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
