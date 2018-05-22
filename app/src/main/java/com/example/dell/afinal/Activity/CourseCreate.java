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
import com.example.dell.afinal.bean.Course;

import com.example.dell.afinal.Fragment.CourseFragment;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.dell.afinal.Activity.CourseCreate;
import com.example.dell.afinal.Activity.LoginActivity;
import com.example.dell.afinal.Activity.MainActivity;
import com.example.dell.afinal.Adapter.CourseListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.User;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
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
    private String courseId;        // 课程唯一标识
    private String invitationCode;  // 课程邀请码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setResult(1);
                //finish();
            }
        });
        CreateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               check();
            }
        });
    }
    public  void check(){
        String Cname = courseName.getText().toString();
        String CDescription = courseDescription.getText().toString();
        String CTime = courseTime.getText().toString();
        String CPlace = coursePlace.getText().toString();
        String Ccapacity = courseCapacity.getText().toString();
        String Ccode = courseName.getText().toString();
        Pattern p = Pattern.compile("[0-9]{8}");
        Matcher result1 = p.matcher(Ccapacity);
        Matcher result2 = p.matcher(Ccode);
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

        }else if(Ccode.isEmpty()){
            ToastUtil.toast(CourseCreate.this, "课程邀请码不能为空");

        }else if(!result1.matches()){
            ToastUtil.toast(CourseCreate.this, "课程容量应为数字");

        }else  if(!result2.matches()){
            ToastUtil.toast(CourseCreate.this, "课程邀请码应为数字");

        } else{
            Course course = new Course();
            course.setCourseName(Cname);
            course.setCourseTime(CTime);
            course.setCoursePlace(CPlace);
            course.setCourseDescription(CDescription);
            int capacity = Integer.parseInt(Ccapacity);
            course.setCourseCapacity(capacity);
            course.setInvitationCode(Ccode);
            course.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        ToastUtil.toast(CourseCreate.this, "创建成功");
                    } else {
                        ToastUtil.toast(CourseCreate.this, "创建失败,请检查您的网络");
                    }
                }
            });
        }
    }


    public void onViewClicked() {

    }
}
