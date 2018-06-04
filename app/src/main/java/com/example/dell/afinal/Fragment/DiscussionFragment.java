package com.example.dell.afinal.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.DiscussionListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class DiscussionFragment extends android.support.v4.app.Fragment {

    private View mView;    // Fragment布局
    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.no_course_hint)
    TextView noCourseHint;

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
            loadUserCourse();
            setPullResfreshListener();
        }
        EventBus.getDefault().register(this);
        return mView;
    }

    // 设置下拉刷新行为
    private void setPullResfreshListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUserCourse();
            }
        });
    }

    // 定义订阅者行为, 当讨论区列表需要更新时做出处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("addCourse") || event.getMessage().equals("quitCourse")
                || event.getMessage().equals("updateCourse"))
            loadUserCourse();
    }

    // 读取当前用户的选课记录
    private void loadUserCourse() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Course> query = new BmobQuery<>();
        query.addWhereRelatedTo("courses", new BmobPointer(user));
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    onLoadSuccess(list);
                } else {
                    onLoadFailed();
                    Log.e("读取用户选课结果失败:", e.toString());
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    // 读取选课记录成功
    private void onLoadSuccess(List<Course> list) {
        createRecyclerView(list);
    }

    // 构建RecyclerView
    private void createRecyclerView(List<Course> list) {
        if (list.size() == 0) noCourseHint.setVisibility(View.VISIBLE);
        else noCourseHint.setVisibility(View.INVISIBLE);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        DiscussionListAdapter adapter = new DiscussionListAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    // 读取失败
    private void onLoadFailed() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}