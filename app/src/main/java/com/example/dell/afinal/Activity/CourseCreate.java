package com.example.dell.afinal.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CourseCreate extends AppCompatActivity {

    @BindView(R.id.course_detail) LinearLayout courseDetailTag;  // 课程详情最外层布局
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
    @BindView(R.id.join_course_pro) ProgressBar progressBar;       // 加入课程进度条
    @BindView(R.id.join_course) Button CreateCourse;                 // 加入课程按钮

    private Unbinder unbinder;
    private String courseId;        // 课程唯一标识
    private String invitationCode;  // 课程邀请码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

    }
    public  void check(){
        String Cname = courseName.getText().toString();
        String CDescription = courseDescription.getText().toString();
        String CTime = courseTime.getText().toString();
        String CPlace = coursePlace.getText().toString();
        String Ccapacity = courseCapacity.getText().toString();
        String Ccode = courseName.getText().toString();
    }
    @OnClick({R.id.join_course,R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.join_course:
                check();
                break;
            case R.id.back:
                CourseCreate.this.finish();
                break;
                default: break;

        }
    }
}
