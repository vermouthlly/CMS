package com.example.dell.afinal.Activity;

import org.greenrobot.eventbus.EventBus;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.User;
import com.example.dell.afinal.bean.MessageEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CourseCreate extends AppCompatActivity {
    @BindView(R.id.cname) TextView nameTag;                      // 课程名tag
    @BindView(R.id.cdescription) TextView desTag;                // 课程简介tag
    @BindView(R.id.ctime) TextView timeTag;                      // 上课时间tag
    @BindView(R.id.cplace) TextView placeTag;                    // 上课地点tag
    @BindView(R.id.ccapatity) TextView capacityTag;              // 课程容量tag
    @BindView(R.id.snum) TextView snumTag;                       // 邀请码人数tag
    @BindView(R.id.course_name) EditText courseName;               // 课程名content
    @BindView(R.id.course_intro) EditText courseDescription;       // 课程简介content
    @BindView(R.id.course_time) EditText courseTime;               // 上课时间content
    @BindView(R.id.course_place) EditText coursePlace;             // 上课地点content
    @BindView(R.id.course_capacity) EditText courseCapacity;       // 课程容量content
    @BindView(R.id.student_num) EditText courseCode;               // 邀请码content
    @BindView(R.id.back) ImageView backButton;                     // 返回按钮

    @BindView(R.id.join_course) Button CreateCourse;                 // 加入课程按钮
    @BindView(R.id.progress_bar) ContentLoadingProgressBar progressBar;

    private Unbinder unbinder;
    private String Cname;
    private String CDescription ;
    private String CTime;
    private String CPlace;
    private String Ccapacity ;
    private String Ccode ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        unbinder = ButterKnife.bind(this);
        progressBar.hide();
    }

    @OnClick({R.id.back,R.id.join_course})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                CourseCreate.this.finish();
                break;
            case R.id.join_course:
                create_course();
                break;
        }
    }

    public void bind(){
         Cname = courseName.getText().toString();
         CDescription = courseDescription.getText().toString();
         CTime = courseTime.getText().toString();
         CPlace = coursePlace.getText().toString();
         Ccapacity = courseCapacity.getText().toString();
         Ccode = courseCode.getText().toString();
    }

    public void create_course(){
        bind();
        check_null();
    }

    public void doit(){
        final User user = BmobUser.getCurrentUser(User.class);
        final Course course = new Course();
        course.setCourseName(Cname);
        course.setCourseTime(CTime);
        course.setCoursePlace(CPlace);
        course.setCourseDescription(CDescription);
        int number_capacity = Integer.parseInt(Ccapacity);
        course.setCourseCapacity(number_capacity);
        course.setInvitationCode(Ccode);
        course.setStatus("released");
        save(user, course);
    }

    public void save(final User user,final Course course) {
        progressBar.show();
        course.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    course.setManager(user);
                    course.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                joinCourse(course);
                            } else {
                                ToastUtil.toast(CourseCreate.this, "课程创建失败," +
                                        "请检查您的网络后重试");
                            }
                        }
                    });
                } else {
                    ToastUtil.toast(CourseCreate.this, "课程创建失败,请检查您的网络后重试");
                    progressBar.hide();
                }
            }
        });
    }

    private void joinCourse(final Course course) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        relation.add(course);
        user.setCourses(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    notifyUpdateCourseList();
                    addTeacherAsMember(course);
                    Log.d("CourseFragment", "教师加入课程成功");
                } else {
                    Log.e("CourseFragment", "教师加入课程失败" + e.toString());
                }
            }
        });
    }

    private void addTeacherAsMember(Course course) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        Course c = new Course();
        c.setObjectId(course.getObjectId());
        c.setSelectors(relation);
        c.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtil.toast(CourseCreate.this, "课程创建成功");
                    CourseCreate.this.finish();
                } else {
                    Log.e("CourseFragment记录失败", e.toString());
                }
                progressBar.hide();
            }
        });
    }

    private void notifyUpdateCourseList() {
        EventBus.getDefault().post(new MessageEvent("addCourse"));
    }

    public  void check_null(){
        if (Cname.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程名称不能为空");
        } else if(CDescription.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程描述不能为空");
        } else if(CTime.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程时间不能为空");
        } else if(CPlace.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程地点不能为空");
        } else if(Ccapacity.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程容量不能为空");
        } else if(Ccode.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程邀请码不能为空");
        } else {
            doit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
