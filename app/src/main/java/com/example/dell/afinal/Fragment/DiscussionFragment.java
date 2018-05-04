package com.example.dell.afinal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.afinal.Activity.MyLikesActivity;
import com.example.dell.afinal.Activity.MyPostActivity;
import com.example.dell.afinal.Activity.NewPostActivity;
import com.example.dell.afinal.Adapter.PostFragmentAdapter;
import com.example.dell.afinal.R;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DiscussionFragment extends android.support.v4.app.Fragment {

    private View mView;    // Fragment布局
    @BindView(R.id.new_post)
    FloatingActionButton newPost;
    @BindView(R.id.my_post)
    FloatingActionButton myPost;
    @BindView(R.id.my_likes)
    FloatingActionButton myLikes;

    private Unbinder unbinder;
    private List<Fragment> fragments = new ArrayList<>();

    public static DiscussionFragment newInstance() {
        return new DiscussionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 避免碎片页面切换时重复加载
        if (mView == null) {
            mView = inflater.inflate(R.layout.discussion_fragment, container, false);
            unbinder = ButterKnife.bind(this, mView);
            fragments.add(new AllPostFragment());
            fragments.add(new PopularPostFragment());
        }
        createViewPager();
        return mView;
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
        Intent intent = new Intent(getContext(), NewPostActivity.class);
        startActivity(intent);
    }

    // 点击“ 我的帖子”按钮
    private void onMyPostButtonClicked() {
        Intent intent = new Intent(getContext(), MyPostActivity.class);
        startActivity(intent);
    }

    // 点击" 我的收藏"按钮
    private void onMyLikesButtonClicked() {
        Intent intent = new Intent(getContext(), MyLikesActivity.class);
        startActivity(intent);
    }

    // 构造讨论区内部的ViewPager页面
    private void createViewPager() {
        ViewPager viewPager = mView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new PostFragmentAdapter(getChildFragmentManager(), fragments));
        viewPager.setCurrentItem(0);

        TabLayout tab = mView.findViewById(R.id.post_tab);
        tab.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
