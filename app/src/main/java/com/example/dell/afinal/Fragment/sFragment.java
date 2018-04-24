package com.example.dell.afinal.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.afinal.FeedbackActivity;
import com.example.dell.afinal.Activity.InfoActivity;
import com.example.dell.afinal.R;
import com.example.dell.afinal.UserDetailActivity;
import com.example.dell.afinal.View.CircleImageView;
import com.example.dell.afinal.bean.User;
import com.squareup.picasso.Picasso;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

import com.example.dell.afinal.LoginActivity;

public class sFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private CircleImageView setCivHead;
    private TextView setTvName;
    private LinearLayout setLlUser;

    public static sFragment newInstance() {
        return new sFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        return view;
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
        setLlUser = getActivity().findViewById(R.id.set_ll_user);
        LinearLayout setLlInfo = getActivity().findViewById(R.id.set_ll_info);
        LinearLayout setLlFeedback = getActivity().findViewById(R.id.set_ll_feedback);
        setTvName.setOnClickListener(this);
        setLlFeedback.setOnClickListener(this);
        setLlInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_ll_feedback:
                Intent intent1 = new Intent(getContext(), FeedbackActivity.class);
                startActivity(intent1);
                break;
            case R.id.set_ll_info:
                Intent intent2 = new Intent(getContext(), InfoActivity.class);
                startActivity(intent2);
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

    private void loadHead(User user) {
        if (user.getHeadFile() != null) {
            BmobFile headFile = user.getHeadFile();
            Picasso.with(getContext()).load(headFile.getFileUrl()).into(setCivHead);
        } else {
            Picasso.with(getContext()).load(R.mipmap.ic_head).into(setCivHead);
        }
    }
}
