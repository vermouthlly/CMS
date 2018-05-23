package com.example.dell.afinal.Activity;

import android.content.Intent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.CourseNotifAdapter;
import com.example.dell.afinal.Adapter.SystemNotifAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.CourseNotification;
import com.example.dell.afinal.bean.SystemNotification;
import com.example.dell.afinal.bean.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class HistoryNotificationActivity extends AppCompatActivity {
    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.no_content_hint)
    TextView noContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_notification);

        unbinder = ButterKnife.bind(this);
        initTitle();

        loadHistoryNotification();
    }

    // 加载历史消息
    private void loadHistoryNotification() {
        Intent intent = getIntent();
        String userId = intent.getStringExtra("user_id");
        String type = intent.getStringExtra("notification_type");
        if (type.equals("system")) {
            loadSystemHistory(userId);
        } else if (type.equals("course")) {
            loadCourseHistory(userId);
        }
    }

    // 读取系统消息历史记录
    private void loadSystemHistory(String userId) {
        User user = new User();
        user.setObjectId(userId);
        BmobQuery<SystemNotification> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(20);
        query.addWhereRelatedTo("sysNotifications", new BmobPointer(user));
        query.findObjects(new FindListener<SystemNotification>() {
            @Override
            public void done(List<SystemNotification> list, BmobException e) {
                if (e == null) {
                    onLoadSysHistorySuccess(list);
                } else {
                    onLoadFailed(e);
                }
            }
        });
    }

    private void onLoadSysHistorySuccess(List<SystemNotification> list) {
        LinearLayoutManager manager = new LinearLayoutManager(HistoryNotificationActivity.this);
        recyclerView.setLayoutManager(manager);
        SystemNotifAdapter adapter = new SystemNotifAdapter(list);
        recyclerView.setAdapter(adapter);
        progressBar.hide();
        if (list.size() == 0) {
            noContent.setText("历史消息记录为空~");
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.INVISIBLE);
        }
    }

    private void onLoadFailed(BmobException e) {
        ToastUtil.toast(getApplicationContext(), "加载失败,请检查你的网络后重试");
        Log.e("读取系统消息历史失败:", e.toString());
    }

    // 读取课程通知历史
    private void loadCourseHistory(String userId) {
        User user = new User();
        user.setObjectId(userId);
        BmobQuery<CourseNotification> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(20);
        query.addWhereRelatedTo("courseNotifications", new BmobPointer(user));
        query.findObjects(new FindListener<CourseNotification>() {
            @Override
            public void done(List<CourseNotification> list, BmobException e) {
                if (e == null) {
                    onLoadCourseHistorySuccess(list);
                } else {
                    onLoadFailed(e);
                }
            }
        });
    }

    private void onLoadCourseHistorySuccess(List<CourseNotification> list) {
        LinearLayoutManager manager = new LinearLayoutManager(HistoryNotificationActivity.this);
        recyclerView.setLayoutManager(manager);
        CourseNotifAdapter adapter = new CourseNotifAdapter(list);
        recyclerView.setAdapter(adapter);
        progressBar.hide();
        if (list.size() == 0) {
            noContent.setText("历史消息记录为空~");
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.INVISIBLE);
        }
    }

    // 设置标题
    private void initTitle() {
        title.setText("历史消息");
    }

    @OnClick(R.id.back)
    public void onViewClicked(View view) {
        HistoryNotificationActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
