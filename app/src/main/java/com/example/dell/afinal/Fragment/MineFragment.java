package com.example.dell.afinal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.afinal.Activity.FeedbackActivity;
import com.example.dell.afinal.Activity.InfoActivity;
import com.example.dell.afinal.Activity.MyCourseActivity;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Activity.UserDetailActivity;
import com.example.dell.afinal.View.CircleImageView;
import com.example.dell.afinal.bean.User;
import com.squareup.picasso.Picasso;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

import com.example.dell.afinal.Activity.LoginActivity;

public class MineFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private CircleImageView setCivHead;
    private TextView setTvName;

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mine_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindView();
    }

    // 绑定控件并添加点击监听器
    public void bindView() {
        setCivHead = getActivity().findViewById(R.id.set_civ_head);
        setTvName = getActivity().findViewById(R.id.set_tv_name);
        LinearLayout myCourselist = getActivity().findViewById(R.id.my_courselist);
        LinearLayout setLlInfo = getActivity().findViewById(R.id.set_ll_info);
        LinearLayout setLlFeedback = getActivity().findViewById(R.id.set_ll_feedback);
        setTvName.setOnClickListener(this);
        myCourselist.setOnClickListener(this);
        setLlFeedback.setOnClickListener(this);
        setLlInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_courselist:
                startActivity(new Intent(getContext(), MyCourseActivity.class));
                break;
            case R.id.set_ll_feedback:
                startActivity(new Intent(getContext(), FeedbackActivity.class));
                break;
            case R.id.set_ll_info:
                startActivity(new Intent(getContext(), InfoActivity.class));
                break;
            case R.id.set_tv_name:
            case R.id.set_ll_user:
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
            setTvName.setText(user.getUsername());
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
}
