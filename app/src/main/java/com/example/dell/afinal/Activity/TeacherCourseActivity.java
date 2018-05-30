package com.example.dell.afinal.Activity;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.CourseManagerAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

public class TeacherCourseActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.no_content_hint)
    TextView noContentHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_course);

        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initTitle();
        loadTeacherCourses();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("updateCourse"))
            loadTeacherCourses();
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        TeacherCourseActivity.this.finish();
    }

    // 读取教师创建的课程信息
    private void loadTeacherCourses() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Course> query = new BmobQuery<>();
        query.addWhereRelatedTo("courses", new BmobPointer(user));
        query.order("-createdAt");
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    createCourseList(list);
                } else {
                    onLoadFailed(e);
                }
            }
        });
    }

    // 搭建课程列表
    private void createCourseList(List<Course> list) {
        LinearLayoutManager manager = new LinearLayoutManager(TeacherCourseActivity.this);
        recyclerView.setLayoutManager(manager);
        CourseManagerAdapter adapter = new CourseManagerAdapter(list);
        recyclerView.setAdapter(adapter);
        if (list.size() == 0) {
            noContentHint.setVisibility(View.VISIBLE);
        } else {
            noContentHint.setVisibility(View.INVISIBLE);
        }
        progressBar.hide();
    }

    // 读取失败
    private void onLoadFailed(BmobException e) {
        Log.e("读取课程信息失败", e.toString());
        ToastUtil.toast(TeacherCourseActivity.this, "无法读取课程, 请检查你的网络");
    }

    // 初始化页面标题
    private void initTitle() {
        title.setText("课程管理");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}
