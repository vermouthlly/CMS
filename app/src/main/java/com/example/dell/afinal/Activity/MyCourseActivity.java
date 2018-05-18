package com.example.dell.afinal.Activity;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.example.dell.afinal.Adapter.CourseListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
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

public class MyCourseActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.back)
    ImageView bt_back;
    @BindView(R.id.course_list)
    RecyclerView my_courseList;
    @BindView(R.id.is_loading)
    ProgressBar progressBar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    private Unbinder unbinder;
    private List<Course> myCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course);

        unbinder = ButterKnife.bind(this);

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

    // 构建RecyclerView
    public void createRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        my_courseList.setLayoutManager(manager);
        CourseListAdapter adapter = new CourseListAdapter(myCourses);
        my_courseList.setAdapter(adapter);
    }

    // 读取用户已选择的课程
    public void loadUserCourses() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Course> query = new BmobQuery<>();
        query.addWhereRelatedTo("courses", new BmobPointer(user));
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    myCourses = list;
                    if (myCourses.size() == 0)
                        ToastUtil.toast(MyCourseActivity.this, "空空如也~赶紧去选课吧");
                    createRecyclerView();
                    hideProgress();
                    refreshLayout.setRefreshing(false);
                } else {
                    ToastUtil.toast(getApplicationContext(), "读取课程数据失败, 请检查你的网络");
                    hideProgress();
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    // 必要的控件属性设置
    public void basicSetting() {
        title.setText("我的课程");
        progressBar.setVisibility(View.VISIBLE);
        addRefreshListener();
    }

    // 隐藏进度条
    public void hideProgress() {
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.INVISIBLE);
    }

    // 添加下拉刷新监听
    public void addRefreshListener() {
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
    }
}