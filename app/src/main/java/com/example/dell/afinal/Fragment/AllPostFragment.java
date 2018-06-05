package com.example.dell.afinal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.PostListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.Post;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AllPostFragment extends BackHandledFragment {

    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.no_content_hint)
    TextView noContentHint;
    @BindView(R.id.history)
    Button history;
    @BindView(R.id.no_content_field)
    LinearLayout noContentField;
    @BindView(R.id.recycler_view)
    XRecyclerView recyclerView;

    private View mView;
    
    private List<Post> postList = new ArrayList<>();
    private PostListAdapter adapter;
    private Unbinder unbinder;
    private String courseId;
    private int loadFactor = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.allpost_fragment, container, false);
            unbinder = ButterKnife.bind(this, mView);
            Intent intent = getActivity().getIntent();
            courseId = intent.getStringExtra("courseId");
            loadDelay();
            setOnPullLoadingListener();
        }
        return mView;
    }

    // 为加载过程添加加载时间缓冲
    private void loadDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPostsFromServer();
            }
        }, 1000);
    }

    // 从服务器读取属于当前课程的所有帖子数据
    private void loadPostsFromServer() {
        BmobQuery<Post> query = new BmobQuery<>();
        query.setLimit(loadFactor);
        query.addWhereEqualTo("courseId", courseId);
        query.order("-createdAt");
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    onPostsLoadSuccess(list);
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

    // 帖子数据读取成功
    private void onPostsLoadSuccess(List<Post> list) {
        try {
            postList = list;
            createRecyclerView();
            progressBar.hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    // 构建RecyclerView
    private void createRecyclerView() {
        adapter = new PostListAdapter(postList, AllPostFragment.this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        if (postList.size() == 0) {
            noContentField.setVisibility(View.VISIBLE);
            history.setVisibility(View.INVISIBLE);
            noContentHint.setText("讨论区还没有帖子, 快来发表你的第一帖吧~");
        } else {
            noContentField.setVisibility(View.INVISIBLE);
        }
    }
    
    // 出现网络异常
    private void onNetworkException() {
        ToastUtil.toast(getContext(), "数据加载失败, 请刷新重试");
    }

    // 设置RecyclerView下拉刷新和上拉加载监听
    private void setOnPullLoadingListener() {
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                reloadPostData();
            }

            @Override
            public void onLoadMore() {
                loadFactor += 3;
                reloadPostData();
            }
        });
    }

    private void reloadPostData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reloadPostDataFromServer();
            }
        }, 1000);
    }

    // 以新的加载因子从服务器重新读取帖子数据
    private void reloadPostDataFromServer() {
        BmobQuery<Post> query = new BmobQuery<>();
        query.setLimit(loadFactor);
        query.addWhereEqualTo("courseId", courseId);
        query.order("-createdAt");
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    onReloadSuccess(list);
                } else {
                    onNetworkException();
                }
            }
        });
    }

    // 重新加载成功（这里捕获数据加载过程中用户强制退出引起的RecyclerView空指针错误）
    private void onReloadSuccess(List<Post> list) {
        try {
            postList.clear();
            postList.addAll(list);
            adapter.notifyDataSetChanged();
            recyclerView.refreshComplete();
            recyclerView.loadMoreComplete();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}