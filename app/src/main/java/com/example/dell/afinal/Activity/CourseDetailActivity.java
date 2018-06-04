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
import com.example.dell.afinal.Utils.DialogUtil;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.User;

import org.greenrobot.eventbus.EventBus;

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

    @BindView(R.id.load_courseInfo_pro)
    ContentLoadingProgressBar courseInfoPro;                     // 课程详情读取进度
    @BindView(R.id.course_detail) LinearLayout courseDetailTag;  // 课程详情最外层布局
    @BindView(R.id.cname) TextView nameTag;                      // 课程名tag
    @BindView(R.id.cdescription) TextView desTag;                // 课程简介tag
    @BindView(R.id.ctime) TextView timeTag;                      // 上课时间tag
    @BindView(R.id.cplace) TextView placeTag;                    // 上课地点tag
    @BindView(R.id.ccapatity) TextView capacityTag;              // 课程容量tag
    @BindView(R.id.snum) TextView snumTag;                       // 已选人数tag
    @BindView(R.id.course_name) TextView courseName;               // 课程名content
    @BindView(R.id.course_intro) TextView courseDescription;       // 课程简介content
    @BindView(R.id.course_time) TextView courseTime;               // 上课时间content
    @BindView(R.id.course_place) TextView coursePlace;             // 上课地点content
    @BindView(R.id.course_capacity) TextView courseCapacity;       // 课程容量content
    @BindView(R.id.student_num) TextView studentNum;               // 已选人数content
    @BindView(R.id.back) ImageView backButton;                     // 返回按钮
    @BindView(R.id.join_course_pro) ProgressBar progressBar;       // 加入课程进度条
    @BindView(R.id.join_course) Button joinCourse;                 // 加入课程按钮

    private Unbinder unbinder;
    private String courseId;        // 课程唯一标识
    private String invitationCode;  // 课程邀请码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        unbinder = ButterKnife.bind(this);

        checkDuplication();   // 检查用户是否已经选择该课程
        loadCourseInfo();    // 加载课程信息到视图中
    }

    // 点击逻辑
    @OnClick({R.id.cname, R.id.cdescription, R.id.ctime, R.id.cplace, R.id.ccapatity,
              R.id.snum, R.id.course_name, R.id.course_intro, R.id.course_time, R.id.course_place,
              R.id.course_capacity, R.id.student_num, R.id.join_course, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cname: case R.id.course_name:
                showCourseName();
                break;
            case R.id.cdescription: case R.id.course_intro:
                showCourseDescription();
                break;
            case R.id.ctime: case R.id.course_time:
                showCourseTime();
                break;
            case R.id.cplace: case R.id.course_place:
                showCoursePlace();
                break;
            case R.id.ccapatity: case R.id.course_capacity:
                showCourseCapacity();
                break;
            case R.id.snum: case R.id.student_num:
                showStudentNum();
                break;
            case R.id.join_course:
                addOrQuitCourse();
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
        countCourseStudents();
    }

    // 计算选择这门课程的学生的人数
    private void countCourseStudents() {
        BmobQuery<User> query = new BmobQuery<>();
        Course course = new Course();
        course.setObjectId(courseId);
        query.addWhereRelatedTo("selectors", new BmobPointer(course));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    setStudentNums(list.size());
                    showCourseInfoLayout();
                } else {
                    Log.e("CourseDetailActivity", e.toString());
                }
            }
        });
    }

    // 已选人数需要读取数据库统计,单独处理
    public void setStudentNums(int num) {
        studentNum.setText(String.valueOf(num-1));
    }

    // 加载完成, 显示课程信息
    public void showCourseInfoLayout() {
        courseInfoPro.hide();
        courseDetailTag.setVisibility(View.VISIBLE);
    }

    // 检查用户是否已经选择该课程 | 或者该门课程是否是当前用户(教师)创建
    public void checkDuplication() {
        BmobQuery<Course> query = new BmobQuery<>();
        final User user = BmobUser.getCurrentUser(User.class);
        final String identity = user.getIdentity();
        if(identity.equals("student")) {
            query.addWhereRelatedTo("courses", new BmobPointer(user));
            query.findObjects(new FindListener<Course>() {
                @Override
                public void done(List<Course> list, BmobException e) {
                    if (e == null) {
                        List<String> courseIds = new ArrayList<>();
                        for (Course course : list) {
                            courseIds.add(course.getObjectId());
                        }
                        // 该课程未选择
                        if (!courseIds.contains(courseId) ) {
                            joinCourse.setText("加入课程");
                        } else if(courseIds.contains(courseId) ){
                            joinCourse.setText("退出课程");
                        }
                    } else {
                        netWorkExceptionHint();
                        Log.e("读取用户课程信息失败:", e.toString());
                    }
                }
            });
        } else if(identity.equals("teacher")) {
            query.addWhereEqualTo("manager", new BmobPointer(user));
            query.findObjects(new FindListener<Course>() {
                @Override
                public void done(List<Course> list, BmobException e) {
                    if (e == null) {
                        List<String> courseIds = new ArrayList<>();
                        for (Course course : list) {
                            courseIds.add(course.getObjectId());
                        }
                        // 该课程不是该教师创建, 不提供发布通知接口
                        if (!courseIds.contains(courseId) ) {
                            joinCourse.setVisibility(View.INVISIBLE);
                        } else if(courseIds.contains(courseId) ){
                            joinCourse.setText("发布通知");
                        }
                    } else {
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }

    }

    // 出现网络异常，需要给予用户足够的提示
    public void netWorkExceptionHint() {
        hideProgress();
        ToastUtil.toast(CourseDetailActivity.this, "无法读取课程信息,请检查你的网络");
    }

    // 根据按钮状态判定点击按钮的行为：选课或退课
    public void addOrQuitCourse() {
        if (joinCourse.getText().toString().equals("加入课程")) {
            if (!checkWhetherUpToCapacity())
                showDialogWithInput();
            else
                ToastUtil.toast(CourseDetailActivity.this, "已达课程上限,无法加入课程");
        } else if(joinCourse.getText().toString().equals("退出课程")){
            showQuitCourseDialog();
        } else if(joinCourse.getText().toString().equals("发布通知")){
            sendMessage();
        }
    }

    // 发布通知
    public void sendMessage(){
        Intent intent = new Intent();
        intent.putExtra("id", courseId);
        intent.setClass(CourseDetailActivity.this, SendMessage.class);
        startActivity(intent);
    }

    // 检查是否已达课程上限
    public boolean checkWhetherUpToCapacity() {
        String stu = studentNum.getText().toString();
        int num = Integer.parseInt(stu);
        String cap = courseCapacity.getText().toString();
        int capacity = Integer.parseInt(cap);
        return num + 1 > capacity;
    }

    // 点击加入课程按钮弹出带输入的对话框, 提供输入验证码的接口
    public void showDialogWithInput() {
        LayoutInflater inflater = LayoutInflater.from(CourseDetailActivity.this);
        final View view = inflater.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(CourseDetailActivity.this);
        builder.setView(view);
        builder.setCancelable(true)
        .setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressBar.setVisibility(View.VISIBLE);
                joinCourse(view);
            }
        })
        .show();
    }

    // 加入课程
    public void joinCourse(View view) {
        if (!validateInvitationCode(view)) {
            ToastUtil.toast(CourseDetailActivity.this, "邀请码错误,无法加入课程");
            hideProgress();
            return;
        }
        User user = BmobUser.getCurrentUser(User.class);
        Course c = addUserToCourseRecord(user);
        addCourseToUserRecord(c);
    }

    // 检查邀请码是否正确
    public boolean validateInvitationCode(View view) {
        EditText input = view.findViewById(R.id.invitation_input);
        String inputCode = input.getText().toString();
        return inputCode.equals(invitationCode);
    }

    // 用户关联课程，把该课程添加到用户已选择的课程列表中
    public void addCourseToUserRecord(Course course) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        relation.add(course);
        user.setCourses(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    onJoinCourseSuccess();
                } else {
                    netWorkExceptionHint();
                }
                hideProgress();
            }
        });
    }
    
    // 添加课程成功,及时更新UI
    public void onJoinCourseSuccess() {
        String stu = studentNum.getText().toString();
        int num = Integer.parseInt(stu) + 1;
        studentNum.setText(String.valueOf(num));
        joinCourse.setText("退出课程");
        ToastUtil.toast(getApplicationContext(), "添加课程成功");

        updateUserCourseList("addCourse");
    }

    // 课程关联用户, 把当前用户添加到选择了该课程的所有用户记录中
    public Course addUserToCourseRecord(User user) {
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        Course course = new Course();
        course.setObjectId(courseId);
        course.setSelectors(relation);   // 添加关联
        course.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("CourseDetailActivity", "course selected");
                } else {
                    Log.e("CourseDetailActivity", e.toString());
                }
            }
        });
        return course;
    }

    // 在提示框里完成退选课程操作
    public void showQuitCourseDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CourseDetailActivity.this);
        dialog.setTitle("你确定要退出课程?");
        dialog.setPositiveButton("我确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                quitCourse();
                progressBar.setVisibility(View.VISIBLE);
            }
        })
        .setNegativeButton("再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ToastUtil.toast(CourseDetailActivity.this, "操作已取消");
            }
        })
        .setCancelable(false)
        .show();
    }

    // 退选课程
    public void quitCourse() {
        deleteRelationInUser();
        deleteRelationInCourse();
    }

    // 删除User表中Course记录
    public void deleteRelationInUser() {
        User user = BmobUser.getCurrentUser(User.class);
        Course course = new Course();
        course.setObjectId(courseId);
        BmobRelation relation = new BmobRelation();
        relation.remove(course);   // 解除关联
        user.setCourses(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    onQuitCourseSuccess();
                } else {
                    netWorkExceptionHint();
                    Log.e("Bmob", e.toString());
                }
                hideProgress();
            }
        });
    }

    // 退出课程成功,及时更新相关UI
    public void onQuitCourseSuccess() {
        String stu = studentNum.getText().toString();
        int num = Integer.parseInt(stu) - 1;
        studentNum.setText(String.valueOf(num));
        hideProgress();
        joinCourse.setText("加入课程");
        ToastUtil.toast(CourseDetailActivity.this, "操作成功");

        updateUserCourseList("quitCourse");
    }

    // 退课的同时删除Course表中的User记录
    public void deleteRelationInCourse() {
        Course course = new Course();
        course.setObjectId(courseId);
        BmobRelation relation = new BmobRelation();
        relation.remove(BmobUser.getCurrentUser(User.class));
        course.setSelectors(relation);
        course.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("CourseDetailActivity", "relation removed");
                } else {
                    Log.e("CourseDetailActivity", e.toString());
                }
            }
        });
    }

    // 选课和退课都会造成用户课程列表的变化, 通过EventBus完成用户课程列表的更新
    private void updateUserCourseList(String msg) {
        MessageEvent event = new MessageEvent(msg);
        EventBus.getDefault().post(event);
    }

    // 隐藏进度条
    public void hideProgress() {
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.INVISIBLE);
    }

    // 显示课程名
    public void showCourseName() {
        String title = nameTag.getText().toString();
        String content = courseName.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 显示课程简介
    public void showCourseDescription() {
        String title = desTag.getText().toString();
        String content = courseDescription.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 显示上课时间段
    public void showCourseTime() {
        String title = timeTag.getText().toString();
        String content = courseTime.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 显示上课地点
    public void showCoursePlace() {
        String title = placeTag.getText().toString();
        String content = coursePlace.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 显示课程容量
    public void showCourseCapacity() {
        String title = capacityTag.getText().toString();
        String content = courseCapacity.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 显示已选该课程的人数
    public void showStudentNum() {
        String title = snumTag.getText().toString();
        String content = studentNum.getText().toString();
        DialogUtil.showDialog(CourseDetailActivity.this, title, content);
    }

    // 解绑ButterKnife + 注销EventBus订阅者
    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
