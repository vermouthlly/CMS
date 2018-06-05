package com.example.dell.afinal.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.PostFragmentAdapter;
import com.example.dell.afinal.Fragment.AllPostFragment;
import com.example.dell.afinal.Fragment.PopularPostFragment;
import com.example.dell.afinal.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CourseDiscussionActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView toolbarTitle;
    @BindView(R.id.new_post)
    FloatingActionButton newPost;
    @BindView(R.id.my_post)
    FloatingActionButton myPost;
    @BindView(R.id.my_likes)
    FloatingActionButton myLikes;

    private Unbinder unbinder;
    private List<Fragment> fragments = new ArrayList<>();
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_discussion);

        unbinder = ButterKnife.bind(this);
        toolBarSetting();
        createViewPager();
    }

    // 标题栏设置
    private void toolBarSetting() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String title = "标题";
        if (bundle != null) {
            courseId = bundle.getString("courseId", "");
            title = bundle.getString("title", "讨论区");
        }
        toolbarTitle.setText(title);
    }

    @OnClick({R.id.new_post, R.id.my_post, R.id.my_likes})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.new_post:
                onNewPostButtonClick();
                break;
            case R.id.my_post:
                onMyPostButtonClicked();
                break;
            case R.id.my_likes:
                onMyLikesButtonClicked();
                break;
        }
    }

    // 点击“ 发表帖子”按钮
    private void onNewPostButtonClick() {
        Intent intent = new Intent(getApplicationContext(), NewPostActivity.class);
        intent.putExtra("courseId", courseId);
        startActivity(intent);
    }

    // 点击“ 我的帖子”按钮
    private void onMyPostButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), MyPostActivity.class);
        startActivity(intent);
    }

    // 点击" 我的收藏"按钮
    private void onMyLikesButtonClicked() {
        Intent intent = new Intent(getApplicationContext(), MyLikesActivity.class);
        startActivity(intent);
    }

    // 构造讨论区内部的ViewPager页面
    private void createViewPager() {
        fragments.add(new AllPostFragment());
        fragments.add(new PopularPostFragment());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new PostFragmentAdapter(getSupportFragmentManager(), fragments));
        viewPager.setCurrentItem(0);

        TabLayout tab = findViewById(R.id.post_tab);
        tab.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
