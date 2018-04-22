package com.example.dell.afinal.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dell.afinal.R;

public class pFragment extends android.support.v4.app.Fragment implements BaseQuickAdapter.OnItemClickListener{

    public static pFragment newInstance() {
        pFragment fragment = new pFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plaza_fragment, container, false);
        return view;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        /*DiaryObj diary = datas.get(position);
        User user = diary.getUser();
        Intent intent = new Intent(getContext(), FindDetailActivity.class);
        intent.putExtra("title", diary.getTitle());
        intent.putExtra("content", diary.getContent());
        *//*if (diary.getImage() != null)
            intent.putExtra("image", diary.getImage().getFileUrl());*//*
        intent.putExtra("date", diary.getDate());
        if (user.getHeadFile() != null)
            intent.putExtra("head", user.getHeadFile().getFileUrl());
        intent.putExtra("name", diary.getName());
        intent.putExtra("sex", user.getSex());
        intent.putExtra("sign", user.getSign());
        intent.putExtra("publish", diary.getCreateTime());
        intent.putExtra("diary", diary);
        startActivity(intent);*/
    }
}
