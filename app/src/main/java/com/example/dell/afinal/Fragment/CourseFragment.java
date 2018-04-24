package com.example.dell.afinal.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.example.dell.afinal.Adapter.CourseListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CourseFragment extends Fragment {
    private View mView;  // 缓存Fragment的View, 避免碎片切换时在onCreateView内重复加载布局
    private ProgressBar progressBar;  // 进度条
    private List<Course> courseList = new ArrayList<>();
    public static final int DATA_READY = 1;

    // 通过Handler更新UI
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_READY:
                    progressBar.setVisibility(View.INVISIBLE);
                    break;
                default: break;
            }
        }
    };

    public static CourseFragment newInstance() {
        return new CourseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.course_fragment, container, false);
            progressBar = mView.findViewById(R.id.is_loading);
            progressBar.setVisibility(View.VISIBLE);
            generateCourseList();
        }
        else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null)
                parent.removeView(mView);
        }

        return mView;
    }

    // 搭建RecyclerView
    public void createRecyclerView() {
        RecyclerView recyclerView = mView.findViewById(R.id.course_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);  // 设置布局管理器

        CourseListAdapter adapter = new CourseListAdapter(courseList);
        recyclerView.setAdapter(adapter);        // 构造并添加适配器
        adapter.notifyDataSetChanged();
    }

    // 初始化课程列表：获取Course表中的所有数据记录，Bmob规定上限为500
    public void generateCourseList() {
        BmobQuery<Course> query = new BmobQuery<>();
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    courseList = new ArrayList<>(list);
                    createRecyclerView();
                    Message msg = new Message();
                    msg.what = DATA_READY;
                    handler.sendMessage(msg);
                }
                else {
                    ToastUtil.toast(getContext(), "获取课程列表失败, 请刷新重试");
                }
            }
        });
    }
}