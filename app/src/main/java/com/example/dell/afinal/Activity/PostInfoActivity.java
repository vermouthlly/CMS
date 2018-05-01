package com.example.dell.afinal.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.View.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PostInfoActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.user_head_image)
    CircleImageView userImg;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.post_title)
    TextView title;
    @BindView(R.id.post_text)
    TextView content;
    @BindView(R.id.back)
    ImageView backButton;
    @BindView(R.id.like_icon)
    ImageView likeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);

        unbinder = ButterKnife.bind(this);
        loadPostInfo();
    }

    // 加载帖子信息到布局中
    public void loadPostInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            title.setText(bundle.getString("postTitle", "——"));
            content.setText(bundle.getString("postContent", "——"));
            date.setText(bundle.getString("date", ""));
        }
    }

    @OnClick({R.id.like_icon, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.like_icon:
                onLikeIconClicked();
                break;
            case R.id.back:
                onBackIconClicked();
                break;
        }
    }

    // 点击收藏按钮
    public void onLikeIconClicked() {
        ToastUtil.toast(PostInfoActivity.this, "收藏功能未开放");
    }

    public void onBackIconClicked() {
        PostInfoActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
