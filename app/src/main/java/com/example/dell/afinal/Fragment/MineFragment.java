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
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.View.CircleImageView;
import com.example.dell.afinal.bean.SystemNotification;
import com.example.dell.afinal.bean.User;
import com.squareup.picasso.Picasso;

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
    private List<SystemNotification> sysNotifications = new ArrayList<>();

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
    @BindView(R.id.noti_num)
    TextView unreadNotiNum;         // 未读通知数

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.mine_fragment, container, false);
            unbinder = ButterKnife.bind(this, mView);
            loadSysNotification();
        }
        return mView;
    }

    // 从系统消息表中读取所有的消息
    private void loadSysNotification() {
        BmobQuery<SystemNotification> query = new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(new FindListener<SystemNotification>() {
            @Override
            public void done(List<SystemNotification> list, BmobException e) {
                if (e == null) {
                    onLoadSuccess(list);
                } else {
                    onLoadFailed(e);
                }
            }
        });
    }

    // 从已选课程消息表中读取所有消息


    // 读取成功
    private void onLoadSuccess(List<SystemNotification> list) {
        loadUserReadList(list);
    }

    // 读取失败
    private void onLoadFailed(BmobException e) {
        Log.e("读取系统消息失败:", e.toString());
    }

    // 读取用户已读消息列表
    private void loadUserReadList(final List<SystemNotification> all) {
        BmobQuery<SystemNotification> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("sysNotifications", new BmobPointer(user));
        query.findObjects(new FindListener<SystemNotification>() {
            @Override
            public void done(List<SystemNotification> list, BmobException e) {
                if (e == null) {
                    checkIfHasRead(list, all);
                } else {
                    Log.e("读取用户消息列表失败:", e.toString());
                }
            }
        });
    }

    // 检查该消息是否已读 ( 这个方法效率比较低下, 有待优化 )
    private void checkIfHasRead(List<SystemNotification> read, List<SystemNotification> all) {
        int i, j;
        sysNotifications.clear();
        for (i = 0; i < all.size(); i++) {
            for (j = 0; j < read.size(); j++) {
                if (all.get(i).getObjectId().equals(read.get(j).getObjectId()))
                    break;
            }
            if (j == read.size()) sysNotifications.add(all.get(i));
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

    // 根据登录状态决定跳转到哪一个界面
    public void detailOrLogin() {
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
