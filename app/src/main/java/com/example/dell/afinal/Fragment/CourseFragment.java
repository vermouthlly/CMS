package com.example.dell.afinal.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.dell.afinal.Adapter.CourseListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseFragment extends Fragment {
    private View mView;  // 缓存Fragment的View, 避免碎片切换时在onCreateView内重复加载布局
    private List<Course> courseList = new ArrayList<>();

    public static CourseFragment newInstance() {
        return new CourseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.course_fragment, container, false);
            initCourseList();
        }
        else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null)
                parent.removeView(mView);
        }

        return mView;
    }

    // 构建RecyclerView展示课程列表
    public void initCourseList() {
        generateCourseList();
        RecyclerView recyclerView = mView.findViewById(R.id.course_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        CourseListAdapter adapter = new CourseListAdapter(courseList);
        recyclerView.setAdapter(adapter);
    }

    // 初始化课程列表
    public void generateCourseList() {
        Course course = new Course();
        course.setCourseName("计算机网络");
        course.setCourseDescription("授课老师：吴迪");
        courseList.add(course);

        Course course1 = new Course();
        course1.setCourseName("操作系统");
        course1.setCourseDescription("授课老师：张永东");
        courseList.add(course1);

        Course course2 = new Course();
        course2.setCourseName("高等数学");
        course2.setCourseDescription("授课老师：江颖");
        courseList.add(course2);

        Course course3 = new Course();
        course3.setCourseName("信息安全");
        course3.setCourseDescription("授课老师：蔡国扬");
        courseList.add(course3);
    }
}