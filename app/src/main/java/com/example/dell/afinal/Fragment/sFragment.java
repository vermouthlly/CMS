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

public class sFragment extends android.support.v4.app.Fragment {

    private CircleImageView setCivHead;
    private TextView setTvName;
    private LinearLayout setLlUser;
    private LinearLayout setLlInfo;
    private LinearLayout setLlFeedback;

    public static sFragment newInstance() {
        sFragment fragment = new sFragment();
        return fragment;
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

        setCivHead = getActivity().findViewById(R.id.set_civ_head);
        setTvName = getActivity().findViewById(R.id.set_tv_name);
        setLlUser = getActivity().findViewById(R.id.set_ll_user);
        setLlInfo = getActivity().findViewById(R.id.set_ll_info);
        setLlFeedback = getActivity().findViewById(R.id.set_ll_feedback);

        setTvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BmobUser.getCurrentUser(User.class) == null) {
                    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    Intent userIntent = new Intent(getContext(), UserDetailActivity.class);
                    startActivity(userIntent);
                }
            }
        });

        setLlFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getContext(), FeedbackActivity.class);
                startActivity(loginIntent);
            }
        });

        setLlInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getContext(), InfoActivity.class);
                startActivity(loginIntent);
            }
        });
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
