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
import com.example.dell.afinal.Adapter.CourseNotifAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.CourseNotification;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.User;

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

public class CourseNotificationFragment extends Fragment {
    private View mView;
    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.no_content_field)
    LinearLayout noContent;
    @BindView(R.id.no_content_hint)
    TextView noContentHint;
    @BindView(R.id.history)
    Button history;

    private List<CourseNotification> notifications = new ArrayList<>();

    public static CourseNotificationFragment getInstance() {
        return new CourseNotificationFragment();
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

    // 从课程消息列表中读取所有的消息
    public void loadNotification() {
        BmobQuery<CourseNotification> query = new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(new FindListener<CourseNotification>() {
            @Override
            public void done(List<CourseNotification> list, BmobException e) {
                if (e == null) {
                    onLoadSuccess(list);
                } else {
                    onLoadFailed(e);
                }
            }
        });
    }

    // 读取成功
    private void onLoadSuccess(List<CourseNotification> list) {
        loadUserCourses(list);

        refreshLayout.setRefreshing(false);
        progressBar.hide();
    }

    // 读取失败
    private void onLoadFailed(BmobException e) {
        Log.e("读取课程消息失败:", e.toString());
        ToastUtil.toast(getContext(), "无法读取课程消息, 请检查你的网络后刷新重试");
        refreshLayout.setRefreshing(false);
    }

    // 读取用户已选择的课程
    private void loadUserCourses(final List<CourseNotification> notificationList) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Course> query = new BmobQuery<>();
        query.addWhereRelatedTo("courses", new BmobPointer(user));
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    checkIfSelectedCourse(notificationList, list);
                    loadUserReadList();
                } else {
                    Log.e("读取选课结果失败:", e.toString());
                }
            }
        });
    }

    // 检查课程通知对应的课程是否在用户的选课列表中 -> 学生只会接收已选课程的通知
    private void checkIfSelectedCourse(List<CourseNotification> nList, List<Course> cList) {
        for (CourseNotification cn : nList)
            for (Course c : cList)
                if (cn.getCourseId().equals(c.getObjectId()))
                    notifications.add(cn);
    }

    // 读取用户已读消息列表
    private void loadUserReadList() {
        BmobQuery<CourseNotification> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("courseNotifications", new BmobPointer(user));
        query.findObjects(new FindListener<CourseNotification>() {
            @Override
            public void done(List<CourseNotification> list, BmobException e) {
                if (e == null) {
                    checkIfHasRead(list);
                    createRecyclerView(notifications);
                } else {
                    Log.e("读取用户消息列表失败:", e.toString());
                }
            }
        });
    }

    // 检查该消息是否已读, 若已读则移除
    private void checkIfHasRead(List<CourseNotification> read) {
        int i, j;
        List<CourseNotification> all = new ArrayList<>();
        for (i = 0; i < notifications.size(); i++) {
            for (j = 0; j < read.size(); j++) {
                if (notifications.get(i).getObjectId().equals(read.get(j).getObjectId()))
                    break;
            }
            if (j == read.size()) all.add(notifications.get(i));
        }
        notifications = all;
    }

    // 生成通知列表
    private void createRecyclerView(List<CourseNotification> list) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        CourseNotifAdapter adapter = new CourseNotifAdapter(list);
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
        intent.putExtra("notification_type", "course");
        intent.putExtra("user_id", userId);
        getActivity().startActivity(intent);
    }

    // 下拉刷新
    private void setOnPullRefreshListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notifications.clear();
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
