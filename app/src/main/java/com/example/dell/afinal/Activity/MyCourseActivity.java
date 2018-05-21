package com.example.dell.afinal.Activity;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.CourseListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.User;

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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyCourseActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back)
    ImageView bt_back;
    @BindView(R.id.course_list)
    RecyclerView my_courseList;
    @BindView(R.id.is_loading)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.no_course_hint)
    TextView noCourseHint;

    private Unbinder unbinder;
    private List<Course> myCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course);

        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        basicSetting();
        loadUserCourses();
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                MyCourseActivity.this.finish();
                break;
            default: break;
        }
    }

    // 定义EventBus订阅者行为
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("addCourse") || event.getMessage().equals("quitCourse"))
            loadUserCourses();
    }

    // 构建RecyclerView
    private void createRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        my_courseList.setLayoutManager(manager);
        CourseListAdapter adapter = new CourseListAdapter(myCourses);
        my_courseList.setAdapter(adapter);
    }

    // 读取用户已选择的课程
    private void loadUserCourses() {
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
                }
            }
        });
    }

    // 读取用户课程成功
    private void onLoadSuccess(List<Course> list) {
        myCourses = list;
        if (myCourses.size() == 0)
            noCourseHint.setVisibility(View.VISIBLE);
        else
            noCourseHint.setVisibility(View.INVISIBLE);
        createRecyclerView();
        progressBar.hide();
        refreshLayout.setRefreshing(false);
    }

    // 读取失败
    private void onLoadFailed() {
        ToastUtil.toast(getApplicationContext(), "读取课程数据失败, 请检查你的网络");
        refreshLayout.setRefreshing(false);
    }

    // 必要的控件属性设置
    private void basicSetting() {
        title.setText("我的课程");
        addRefreshListener();
    }

    // 添加下拉刷新监听
    private void addRefreshListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUserCourses();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}