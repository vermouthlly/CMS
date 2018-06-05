package com.example.dell.afinal.Fragment;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.afinal.Activity.HistoryNotificationActivity;
import com.example.dell.afinal.Adapter.SystemNotifAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.SystemNotification;
import com.example.dell.afinal.bean.User;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

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

public class SysNotificationFragment extends Fragment {

    private View mView;
    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    XRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.no_content_field)
    LinearLayout noContent;
    @BindView(R.id.no_content_hint)
    TextView noContentHint;
    @BindView(R.id.history)
    Button history;

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
        progressBar.hide();
    }

    // 读取失败
    private void onLoadFailed(BmobException e) {
        Log.e("读取系统消息失败:", e.toString());
        ToastUtil.toast(getContext(), "无法读取系统消息, 请检查你的网络后刷新重试");
    }

    // 读取用户已读消息列表
    private void loadUserReadList(final List<SystemNotification> all) {
        BmobQuery<SystemNotification> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("sysNotifications", new BmobPointer(user));
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

    // 检查该消息是否已读 ( 这个方法效率比较低下, 有待优化 )
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
        notifyMineFragment();
    }

    // 提醒MineFragment更新未读通知数
    private void notifyMineFragment() {
        String unread = Integer.toString(notifications.size());
        MessageEvent event = new MessageEvent(unread);
        EventBus.getDefault().post(event);
    }

    // 生成通知列表
    private void createRecyclerView(List<SystemNotification> list) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        SystemNotifAdapter adapter = new SystemNotifAdapter(list);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        if (list.size() == 0) {
            noContent.setVisibility(View.VISIBLE);
            noContentHint.setText("暂时没有新的通知~");
        } else {
            noContent.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.history})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.history:
                checkHistoryNotification();
                break;
        }
    }

    // 查看历史消息
    private void checkHistoryNotification() {
        User user = BmobUser.getCurrentUser(User.class);
        String userId = user.getObjectId();
        Intent intent = new Intent(getActivity(), HistoryNotificationActivity.class);
        intent.putExtra("notification_type", "system");
        intent.putExtra("user_id", userId);
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}