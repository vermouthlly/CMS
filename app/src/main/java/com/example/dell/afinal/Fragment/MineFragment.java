package com.example.dell.afinal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.afinal.Activity.FeedbackActivity;
import com.example.dell.afinal.Activity.InfoActivity;
import com.example.dell.afinal.Activity.LoginActivity;
import com.example.dell.afinal.Activity.MyCourseActivity;
import com.example.dell.afinal.Activity.MyNotificationActivity;
import com.example.dell.afinal.Activity.UserDetailActivity;
import com.example.dell.afinal.R;
import com.example.dell.afinal.View.CircleImageView;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.CourseNotification;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.SystemNotification;
import com.example.dell.afinal.bean.User;
import com.squareup.picasso.Picasso;

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
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MineFragment extends android.support.v4.app.Fragment {

    private Unbinder unbinder;
    private View mView;

    @BindView(R.id.set_civ_head)
    CircleImageView setCivHead;
    @BindView(R.id.set_tv_name)
    TextView setTvName;
    @BindView(R.id.my_courselist)
    LinearLayout myCourseList;
    @BindView(R.id.my_notification)
    LinearLayout myNotification;
    @BindView(R.id.set_ll_info)
    LinearLayout setLlInfo;
    @BindView(R.id.set_ll_feedback)
    LinearLayout setLlFeedback;
    @BindView(R.id.noti_hint_tag)
    TextView hintTag;
    @BindView(R.id.noti_num)
    TextView unreadNotiNum;         // 未读通知数

    private int unreadSys;          // 未读的系统消息数
    private List<CourseNotification> courseNotifications = new ArrayList<>(); // 未读的课程消息

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.mine_fragment, container, false);
            unbinder = ButterKnife.bind(this, mView);
            hintTag.setVisibility(View.INVISIBLE);
            unreadNotiNum.setVisibility(View.INVISIBLE);
            loadSysNotification();
        }
        return mView;
    }

    // 从系统消息表中读取所有消息
    private void loadSysNotification() {
        BmobQuery<SystemNotification> query = new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(new FindListener<SystemNotification>() {
            @Override
            public void done(List<SystemNotification> list, BmobException e) {
                if (e == null) {
                    loadUserReadSysList(list);
                } else {
                    Log.e("读取系统消息失败:", e.toString());
                }
            }
        });
    }

    // 读取用户已读消息列表
    private void loadUserReadSysList(final List<SystemNotification> all) {
        BmobQuery<SystemNotification> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("sysNotifications", new BmobPointer(user));
        query.findObjects(new FindListener<SystemNotification>() {
            @Override
            public void done(List<SystemNotification> list, BmobException e) {
                if (e == null) {
                    countUnreadSysNoti(list, all);
                    loadCourseNotification();
                } else {
                    Log.e("读取用户系统消息列表失败:", e.toString());
                }
            }
        });
    }

    // 计算未读系统消息的数量 ( 这个方法效率比较低下, 有待优化 )
    private void countUnreadSysNoti(List<SystemNotification> read, List<SystemNotification> all) {
        int i, j;
        for (i = 0; i < all.size(); i++) {
            for (j = 0; j < read.size(); j++) {
                if (all.get(i).getObjectId().equals(read.get(j).getObjectId()))
                    break;
            }
            if (j == read.size()) unreadSys++;
        }
    }

    // 从课程消息列表中读取所有的消息
    private void loadCourseNotification() {
        courseNotifications.clear();
        BmobQuery<CourseNotification> query = new BmobQuery<>();
        query.findObjects(new FindListener<CourseNotification>() {
            @Override
            public void done(List<CourseNotification> list, BmobException e) {
                if (e == null) {
                    loadUserCourses(list);
                } else {
                    Log.e("读取课程消息失败：", e.toString());
                }
            }
        });
    }

    // 读取用户已选择的课程
    private void loadUserCourses(final List<CourseNotification> notificationList) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Course> query = new BmobQuery<>();
        query.addWhereRelatedTo("courses", new BmobPointer(user));
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    checkIfSelectedCourse(notificationList, list);
                    loadUserReadCourseList();
                } else {
                    Log.e("读取选课结果失败:", e.toString());
                }
            }
        });
    }

    // 检查课程通知对应的课程是否在用户的选课列表中 -> 学生只会接收已选课程的通知
    private void checkIfSelectedCourse(List<CourseNotification> nList, List<Course> cList) {
        for (CourseNotification cn : nList)
            for (Course c : cList)
                if (cn.getCourseId().equals(c.getObjectId()))
                    courseNotifications.add(cn);
    }

    // 读取用户已读消息列表
    private void loadUserReadCourseList() {
        BmobQuery<CourseNotification> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("courseNotifications", new BmobPointer(user));
        query.findObjects(new FindListener<CourseNotification>() {
            @Override
            public void done(List<CourseNotification> list, BmobException e) {
                if (e == null) {
                    checkIfHasRead(list);
                } else {
                    Log.e("读取用户课程消息列表失败:", e.toString());
                }
            }
        });
    }

    // 检查该消息是否已读, 若已读则移除
    private void checkIfHasRead(List<CourseNotification> read) {
        int i, j;
        List<CourseNotification> list = new ArrayList<>();
        for (i = 0; i < courseNotifications.size(); i++) {
            for (j = 0; j < read.size(); j++) {
                if (courseNotifications.get(i).getObjectId().equals(read.get(j).getObjectId()))
                    break;
            }
            if (j == read.size()) list.add(courseNotifications.get(i));
        }
        courseNotifications = list;
        setUnreadNotiNum();
    }

    // 设置未读消息数
    private void setUnreadNotiNum() {
        int num = courseNotifications.size() + unreadSys;
        if (num == 0) {
            hintTag.setVisibility(View.INVISIBLE);
            unreadNotiNum.setVisibility(View.INVISIBLE);
        } else {
            unreadNotiNum.setText(String.valueOf(num));
            hintTag.setVisibility(View.VISIBLE);
            unreadNotiNum.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.my_courselist, R.id.my_notification, R.id.set_ll_feedback,
            R.id.set_ll_info, R.id.set_tv_name, R.id.set_ll_user})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.my_courselist:
                startActivity(new Intent(getContext(), MyCourseActivity.class));
                break;
            case R.id.my_notification:
                hideUnreadHint();
                startActivity(new Intent(getContext(), MyNotificationActivity.class));
                break;
            case R.id.set_ll_feedback:
                startActivity(new Intent(getContext(), FeedbackActivity.class));
                break;
            case R.id.set_ll_info:
                startActivity(new Intent(getContext(), InfoActivity.class));
                break;
            case R.id.set_tv_name: case R.id.set_ll_user:
                detailOrLogin();
                break;
            default: break;
        }
    }

    // 隐藏未读通知提示
    private void hideUnreadHint() {
        unreadNotiNum.setVisibility(View.INVISIBLE);
        hintTag.setVisibility(View.INVISIBLE);
    }

    // 根据登录状态决定跳转到哪一个界面
    private void detailOrLogin() {
        if (BmobUser.getCurrentUser(User.class) == null) {
            Intent loginIntent = new Intent(getContext(), LoginActivity.class);
            startActivity(loginIntent);
        } else {
            Intent userIntent = new Intent(getContext(), UserDetailActivity.class);
            startActivity(userIntent);
        }
    }

    // 此处判断用户是否登录
    @Override
    public void onResume() {
        super.onResume();
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            if (!TextUtils.isEmpty(user.getNickName())) {
                setTvName.setText(user.getNickName());
            } else {
                setTvName.setText(user.getUsername());
            }
            loadHead(user);
        } else {
            setTvName.setText("未登录");
            Picasso.with(getContext()).load(R.mipmap.ic_head).into(setCivHead);
        }
    }

    // 读取用户头像，未设置则使用默认图像
    private void loadHead(User user) {
        if (user.getHeadFile() != null) {
            BmobFile headFile = user.getHeadFile();
            Picasso.with(getContext()).load(headFile.getFileUrl()).into(setCivHead);
        } else {
            Picasso.with(getContext()).load(R.mipmap.ic_head).into(setCivHead);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
