package com.example.dell.afinal.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.MyPostListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Post;
import com.example.dell.afinal.bean.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyPostActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.is_loading)
    ProgressBar postLoading;
    @BindView(R.id.back)
    ImageView backIcon;

    private List<Post> myPost = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        unbinder = ButterKnife.bind(this);
        initPageTitle();
        postLoading.setVisibility(View.VISIBLE);
        loadMyPost();
    }

    @OnClick(R.id.back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                MyPostActivity.this.finish();
                break;
        }
    }

    // 修改页面标题
    private void initPageTitle() {
        title.setText("我的帖子");
    }

    // 从服务器读取用户的主题帖数据
    public void loadMyPost() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Post> query = new BmobQuery<>();
        query.addWhereEqualTo("author", user);
        query.order("-createdAt");
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    onLoadSuccess(list);
                } else {
                    onLoadFailed();
                    Log.e("读取用户主题帖失败:", e.toString());
                }
            }
        });
    }

    // 读取成功
    private void onLoadSuccess(List<Post> list) {
        myPost = list;
        createMyPostList();
        if (list.size() == 0)
            ToastUtil.toast(MyPostActivity.this, "你还没有发过帖子哦~");
        postLoading.setVisibility(View.INVISIBLE);
    }

    // 读取失败
    private void onLoadFailed() {
        ToastUtil.toast(MyPostActivity.this, "数据读取失败, 请检查你的网络");
        postLoading.setVisibility(View.INVISIBLE);
    }

    // 生成帖子列表
    private void createMyPostList() {
        LinearLayoutManager manager = new LinearLayoutManager(MyPostActivity.this);
        recyclerView.setLayoutManager(manager);
        MyPostListAdapter adapter = new MyPostListAdapter(myPost);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}