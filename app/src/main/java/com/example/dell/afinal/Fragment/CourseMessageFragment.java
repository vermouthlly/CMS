package com.example.dell.afinal.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.NotificationListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.SystemNotification;
import com.example.dell.afinal.bean.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CourseMessageFragment extends Fragment {

    private View mView;
    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.no_content_hint)
    TextView noContentHint;

    private List<SystemNotification> notifications = new ArrayList<>();

    public static SysNotificationFragment getInstance() {
        return new SysNotificationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.allpost_fragment, container, false);
            unbinder = ButterKnife.bind(this, mView);
            loadNotification();
            setOnPullRefreshListener();
        }
        return mView;
    }

    // 从系统消息列表中读取所有的消息
    private void loadNotification() {
        BmobQuery<SystemNotification> query = new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(new FindListener<SystemNotification>() {
            @Override
            public void done(List<SystemNotification> list, BmobException e) {
                if (e == null) {
                    onLoadSuccess(list);
                } else {
                    onLoadFailed(e);
                }
            }
        });
    }

    // 读取成功
    private void onLoadSuccess(List<SystemNotification> list) {
        loadUserReadList(list);
        refreshLayout.setRefreshing(false);
        progressBar.hide();
    }

    // 读取失败
    private void onLoadFailed(BmobException e) {
        Log.e("读取系统消息失败:", e.toString());
        ToastUtil.toast(getContext(), "无法读取系统消息, 请检查你的网络后刷新重试");
        refreshLayout.setRefreshing(false);
    }

    // 读取用户已读消息列表
    private void loadUserReadList(final List<SystemNotification> all) {
        BmobQuery<SystemNotification> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("notifications", new BmobPointer(user));
        query.findObjects(new FindListener<SystemNotification>() {
            @Override
            public void done(List<SystemNotification> list, BmobException e) {
                if (e == null) {
                    checkIfHasRead(list, all);
                    createRecyclerView(notifications);
                } else {
                    Log.e("读取用户消息列表失败:", e.toString());
                }
            }
        });
    }

    // 检查该消息是否已读
    private void checkIfHasRead(List<SystemNotification> read, List<SystemNotification> all) {
        int i, j;
        notifications.clear();
        for (i = 0; i < all.size(); i++) {
            for (j = 0; j < read.size(); j++) {
                if (all.get(i).getObjectId().equals(read.get(j).getObjectId()))
                    break;
            }
            if (j == read.size()) notifications.add(all.get(i));
        }
    }

    // 生成通知列表
    private void createRecyclerView(List<SystemNotification> list) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        NotificationListAdapter adapter = new NotificationListAdapter(list);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        if (list.size() == 0) {
            noContentHint.setVisibility(View.VISIBLE);
            noContentHint.setText("暂时没有新的通知~");
        } else {
            noContentHint.setVisibility(View.INVISIBLE);
        }
    }

    // 下拉刷新
    private void setOnPullRefreshListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNotification();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}