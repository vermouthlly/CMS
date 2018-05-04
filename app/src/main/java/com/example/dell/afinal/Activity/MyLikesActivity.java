package com.example.dell.afinal.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.PostListAdapter;
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
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyLikesActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.is_loading)
    ProgressBar postLoading;
    @BindView(R.id.back)
    ImageView backIcon;

    private List<Post> postList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_likes);

        unbinder = ButterKnife.bind(this);
        initPageTitle();
        loadMyLikes();
    }

    @OnClick(R.id.back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                MyLikesActivity.this.finish();
                break;
        }
    }

    // 修改页面标题
    private void initPageTitle() {
        title.setText("我的收藏");
    }

    // 从服务器读取用户的主题帖数据
    public void loadMyLikes() {
        postLoading.setVisibility(View.VISIBLE);
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Post> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(user));
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    postList = list;
                    onLoadLikesSuccess();
                } else {
                    Log.e("读取收藏列表失败: ", e.toString());
                    onLoadLikesFailed();
                }
            }
        });
    }

    // 读取收藏列表成功
    private void onLoadLikesSuccess() {
        createLikeList();
        postLoading.setVisibility(View.INVISIBLE);
    }

    // 生成收藏列表
    private void createLikeList() {
        LinearLayoutManager manager = new LinearLayoutManager(MyLikesActivity.this);
        recyclerView.setLayoutManager(manager);
        PostListAdapter adapter = new PostListAdapter(postList);
        recyclerView.setAdapter(adapter);
        if (postList.size() == 0)
            ToastUtil.toast(MyLikesActivity.this, "你还没有收藏过帖子,赶紧去讨论区逛逛吧~");
    }

    // 读取收藏列表失败
    private void onLoadLikesFailed() {
        ToastUtil.toast(MyLikesActivity.this, "读取失败,请检查你的网络或稍后重试");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
