package com.example.dell.afinal.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.DialogUtil;
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
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class CourseDetailActivity extends AppCompatActivity {

    @BindView(R.id.cname)
    TextView nameFiled;           // 课程名tag
    @BindView(R.id.cdescription)
    TextView desFiled;            // 课程简介tag
    @BindView(R.id.ctime)
    TextView timeFiled;           // 上课时间tag
    @BindView(R.id.cplace)
    TextView placeFiled;          // 上课地点tag
    @BindView(R.id.ccapatity)
    TextView capacityFiled;       // 课程容量tag
    @BindView(R.id.course_name)
    TextView courseName;          // 课程名content
    @BindView(R.id.course_intro)
    TextView courseDescription;   // 课程简介content
    @BindView(R.id.course_time)
    TextView courseTime;          // 上课时间content
    @BindView(R.id.course_place)
    TextView coursePlace;         // 上课地点content
    @BindView(R.id.course_capacity)
    TextView courseCapacity;      // 课程容量content
    @BindView(R.id.back)
    ImageView backButton;         // 返回按钮
    @BindView(R.id.join_course_pro)
    ProgressBar progressBar;      // 加入课程进度条
    @BindView(R.id.join_course)
    Button joinCourse;            // 加入课程按钮

    private Unbinder unbinder;
    private String courseId;        // 课程唯一标识
    private String invitationCode;  // 课程邀请码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        unbinder = ButterKnife.bind(this);

        loadCourseInfo();
    }

    // 点击逻辑
    @OnClick({R.id.cname, R.id.cdescription, R.id.ctime, R.id.cplace, R.id.ccapatity,
              R.id.course_name, R.id.course_intro, R.id.course_time, R.id.course_place,
              R.id.course_capacity, R.id.join_course, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cname:
            case R.id.course_name:
                showCourseName();
                break;
            case R.id.cdescription:
            case R.id.course_intro:
                showCourseDescription();
                break;
            case R.id.ctime:
            case R.id.course_time:
                showCourseTime();
                break;
            case R.id.cplace:
            case R.id.course_place:
                showCoursePlace();
                break;
            case R.id.ccapatity:
            case R.id.course_capacity:
                showCourseCapacity();
                break;
            case R.id.join_course:
                progressBar.setVisibility(View.VISIBLE);
                checkDuplication();
                break;
            case R.id.back:
                CourseDetailActivity.this.finish();
                break;
            default: break;
        }
    }

    // 加载课程信息到视图中
    public void loadCourseInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;
        courseId = bundle.getString("courseId", "-");
        invitationCode = bundle.getString("invitationCode", "");
        courseName.setText(bundle.getString("courseName", "-"));
        courseDescription.setText(bundle.getString("courseDescription", "-"));
        courseTime.setText(bundle.getString("courseTime", "-"));
        coursePlace.setText(bundle.getString("coursePlace", "-"));
        courseCapacity.setText(bundle.getString("courseCapacity", "-"));
    }

    // 检查用户是否已经选择该课程, 避免重复选课
    public void checkDuplication() {
        BmobQuery<Course> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("courses", new BmobPointer(user));
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    List<String> courseIds = new ArrayList<>();
                    for (Course course : list) {
                        courseIds.add(course.getObjectId());
                    }
                    if (!courseIds.contains(courseId)) {
                        progressBar.setVisibility(View.INVISIBLE);
                        showDialogWithInput();
                    } else {
                        abnormalResult("duplicated_error");
                    }
                } else {
                    abnormalResult("net_error");
                }
            }
        });
    }

    // 检查操作出现异常结果，需要给予用户足够的提示
    public void abnormalResult(String code) {
        if (code.equals("net_error")) {
            progressBar.setVisibility(View.INVISIBLE);
            ToastUtil.toast(CourseDetailActivity.this, "操作失败,请检查你的网络");
        } else if (code.equals("duplicated_error")) {
            progressBar.setVisibility(View.INVISIBLE);
            ToastUtil.toast(CourseDetailActivity.this, "你已选择该课程,请勿重复操作");
        }
    }

    // 点击加入课程按钮弹出带输入的对话框, 提供输入验证码的接口
    public void showDialogWithInput() {
        LayoutInflater inflater = LayoutInflater.from(CourseDetailActivity.this);
        final View view = inflater.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(CourseDetailActivity.this);
        builder.setView(view)
                .setCancelable(true)
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        joinCourse(view);
                    }
                })
                .show();
    }

    // 检查邀请码是否正确
    public boolean validateInvitationCode(View view) {
        EditText input = view.findViewById(R.id.invitation_input);
        String inputCode = input.getText().toString();
        return inputCode.equals(invitationCode);
    }

    // 加入课程
    public void joinCourse(View view) {
        if (!validateInvitationCode(view)) {
            ToastUtil.toast(CourseDetailActivity.this, "邀请码错误,无法加入课程");
            return;
        }
        User user = BmobUser.getCurrentUser(User.class);
        Course c = addUserToCourseRecord(user);
        addCourseToUserRecord(c);
    }

    // 用户关联课程，把该课程添加到用户已选择的课程列表中
    public void addCourseToUserRecord(Course course) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation1 = new BmobRelation();
        relation1.add(course);
        user.setCourses(relation1);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtil.toast(getApplicationContext(), "添加课程成功");
                } else {
                    Log.e("CourseDetailActivity", e.toString());
                }
            }
        });
    }

    // 课程关联用户, 把当前用户添加到选择了该课程的所有用户记录中
    public Course addUserToCourseRecord(User user) {
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        Course c = new Course();
        c.setObjectId(courseId);
        c.setSelectors(relation);   // 添加关联
        c.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("CourseDetailActivity", "course selected");
                } else {
                    Log.e("CourseDetailActivity", e.toString());
                }
            }
        });
        return c;
    }

    // 显示课程名
    public void showCourseName() {
        String title = nameFiled.getText().toString();
        String content = courseName.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 显示课程简介
    public void showCourseDescription() {
        String title = desFiled.getText().toString();
        String content = courseDescription.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 显示上课时间段
    public void showCourseTime() {
        String title = timeFiled.getText().toString();
        String content = courseTime.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 显示上课地点
    public void showCoursePlace() {
        String title = placeFiled.getText().toString();
        String content = coursePlace.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 显示课程容量
    public void showCourseCapacity() {
        String title = capacityFiled.getText().toString();
        String content = courseCapacity.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 解绑ButterKnife
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
