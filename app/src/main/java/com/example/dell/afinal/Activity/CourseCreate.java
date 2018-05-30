package com.example.dell.afinal.Activity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Unbinder unbinder;
    private String Cname;
    private String CDescription ;
    private String CTime;
    private String CPlace;
    private String Ccapacity ;
    private String Ccode ;
    private int number_capacity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        unbinder = ButterKnife.bind(this);

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

    public  void bind(){
         Cname = courseName.getText().toString();
         CDescription = courseDescription.getText().toString();
         CTime = courseTime.getText().toString();
         CPlace = coursePlace.getText().toString();
         Ccapacity = courseCapacity.getText().toString();
         Ccode = courseName.getText().toString();
        courseCapacity.setInputType( InputType.TYPE_CLASS_NUMBER);
    }
    public  void create_course(){
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
        course.setCourseCapacity(number_capacity);
        course.setInvitationCode(Ccode);
        save(user,course);
    }
    public void save(final User user,final Course course){
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
                                ToastUtil.toast(CourseCreate.this, "创建成功");
                            } else {
                                ToastUtil.toast(CourseCreate.this, "创建失败," +
                                        "请检查您的网络");
                            }
                        }
                    });
                } else {
                    ToastUtil.toast(CourseCreate.this, "创建失败,请检查您的网络");
                }
            }
        });
        CourseCreate.this.finish();
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
                    Log.d("CourseFragment", "记录成功");
                } else {
                    Log.e("CourseFragment记录失败", e.toString());
                }
            }
        });
    }
    private void notifyUpdateCourseList() {
        EventBus.getDefault().post(new MessageEvent("addCourse"));
    }
    public  void check_null(){
        if(Cname.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程名称不能为空");
        }else if(CDescription.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程描述不能为空");
        }else if(CTime.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程时间不能为空");
        }else if(CPlace.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程地点不能为空");
        }else if(Ccapacity.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程容量不能为空");
        } else if(Ccode.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程邀请码不能为空");
        }else {
            doit();
        }
    }

}
