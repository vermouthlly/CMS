package com.example.dell.afinal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.afinal.Adapter.PostListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.Post;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AllPostFragment extends Fragment {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;       // 下拉刷新
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;  // 进度条

    private View mView;
    
    private List<Post> postList = new ArrayList<>();
    private Unbinder unbinder;
    private String courseId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.allpost_fragment, container, false);
            unbinder = ButterKnife.bind(this, mView);
            Intent intent = getActivity().getIntent();
            courseId = intent.getStringExtra("courseId");
            loadPostsFromServer();
            onPullRefresh();
        }
        EventBus.getDefault().register(this);
        return mView;
    }

    // 从服务器读取属于当前课程的所有帖子数据
    public void loadPostsFromServer() {
        progressBar.show(); // 显示进度条开始加载

        BmobQuery<Post> query = new BmobQuery<>();
        query.addWhereEqualTo("courseId", courseId);
        query.order("-createdAt");
        query.setLimit(20);     // 仅加载20个
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    postList = list;
                    createRecyclerView();
                    onDataLoaded();
                } else {
                    onNetworkException();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("addPost") || event.getMessage().equals("deletePost"))
            loadPostsFromServer();
    }
    
    // 构建RecyclerView
    public void createRecyclerView() {
        RecyclerView recyclerView = mView.findViewById(R.id.recycler_view);
        PostListAdapter adapter = new PostListAdapter(postList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }
    
    // 出现网络异常
    public void onNetworkException() {
        ToastUtil.toast(getContext(), "数据加载失败, 请刷新重试");
        refreshLayout.setRefreshing(false);
    }

    // 数据加载完成
    public void onDataLoaded() {
        /*ToastUtil.toast(getContext(), "数据加载完成");*/
        refreshLayout.setRefreshing(false);
        progressBar.hide();
    }

    // 下拉刷新
    public void onPullRefresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPostsFromServer();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}