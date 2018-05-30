package com.example.dell.afinal.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class EditCourseActivity extends AppCompatActivity {

    private Unbinder unbinder;
    private String courseId;

    @BindView(R.id.cancel) TextView cancel;
    @BindView(R.id.commit) TextView commit;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.course_name) EditText courseName;
    @BindView(R.id.course_teacher) EditText courseTeacher;
    @BindView(R.id.course_time) EditText courseTime;
    @BindView(R.id.course_place) EditText coursePlace;
    @BindView(R.id.course_capacity) EditText courseCapacity;
    @BindView(R.id.invite_code) EditText inviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");

        unbinder = ButterKnife.bind(this);
        initViews();
        loadOriginalCourseData();
    }

    @OnClick({R.id.cancel, R.id.commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                EditCourseActivity.this.finish();
                break;
            case R.id.commit:
                onCommitButtonClicked();
                break;
        }
    }

    // 点击提交按钮
    private void onCommitButtonClicked() {
        String newName = courseName.getText().toString();
        String newTeacher = courseTeacher.getText().toString();
        String newTime = courseTime.getText().toString();
        String newPlace = coursePlace.getText().toString();
        String newCapacity = courseCapacity.getText().toString();
        String newCode = inviteCode.getText().toString();
        if (!checkInput(newName, newTeacher, newTime, newPlace, newCapacity, newCode))
            return;
        commitNewCourseInfo(newName, newTeacher, newTime, newPlace, newCapacity, newCode);
    }

    // 新输入合法, 把修改后的课程信息提交到服务器
    private void commitNewCourseInfo(String newName, String newTeacher, String newTime,
                                     String newPlace, String newCapacity, String newCode) {
        Course course = new Course();
        course.setObjectId(courseId);
        course.setCourseName(newName);
        course.setCourseDescription(newTeacher);
        course.setCourseTime(newTime);
        course.setCoursePlace(newPlace);
        course.setCourseCapacity(Integer.parseInt(newCapacity));
        course.setInvitationCode(newCode);
        course.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    onCommitSuccess();
                } else {
                    Log.e("提交修改信息失败", e.toString());
                    ToastUtil.toast(EditCourseActivity.this, "提交失败,请检查你的网络");
                }
            }
        });
    }

    // 提交成功
    private void onCommitSuccess() {
        ToastUtil.toast(getApplicationContext(), "修改成功");
        EventBus.getDefault().post(new MessageEvent("updateCourse"));
        EditCourseActivity.this.finish();
    }

    // 检查新输入的合法性
    private boolean checkInput(String newName, String newTeacher, String newTime,
                            String newPlace, String newCapacity, String newCode) {
        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newTeacher) ||
                TextUtils.isEmpty(newTime) || TextUtils.isEmpty(newPlace) ||
                TextUtils.isEmpty(newCapacity) || TextUtils.isEmpty(newCode)) {
            ToastUtil.toast(EditCourseActivity.this, "请保证输入信息不为空");
            return false;
        }
        return true;
    }

    // 控件初始化
    private void initViews() {
        cancel.setText("放弃修改");
        commit.setText("保存修改");
        title.setText("编辑课程");
    }

    // 把原来的课程信息加载到控件中
    private void loadOriginalCourseData() {
        BmobQuery<Course> query = new BmobQuery<>();
        query.getObject(courseId, new QueryListener<Course>() {
            @Override
            public void done(Course course, BmobException e) {
                if (e == null) {
                    onLoadSuccess(course);
                } else {
                    Log.e("加载课程信息失败", e.toString());
                    ToastUtil.toast(EditCourseActivity.this,
                                    "加载课程信息失败, 请检查你的网络或稍后重试");
                }
            }
        });
    }

    // 加载完成
    private void onLoadSuccess(Course course) {
        courseName.setText(course.getCourseName());
        courseTeacher.setText(course.getCourseDescription());
        courseTime.setText(course.getCourseTime());
        coursePlace.setText(course.getCoursePlace());
        courseCapacity.setText(String.valueOf(course.getCourseCapacity()));
        inviteCode.setText(course.getInvitationCode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}