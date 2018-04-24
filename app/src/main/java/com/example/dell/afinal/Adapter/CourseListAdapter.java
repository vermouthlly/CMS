package com.example.dell.afinal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.afinal.CourseDetailActivity;
import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.Course;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

// 课程列表的适配器
public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {
    private Context mContext;
    private List<Course> courseList;

    public CourseListAdapter(List<Course> list) {
        courseList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.course_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        /*点击卡片的事件处理逻辑*/
        viewHolder.courseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCourseDetail(viewHolder);
            }
        });

        return viewHolder;
    }

    // 跳转到课程详情界面，并把课程相关信息传递过去
    private void showCourseDetail(ViewHolder viewHolder) {
        Intent intent = new Intent(mContext, CourseDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("courseName", viewHolder.courseName.getText().toString());
        bundle.putString("courseDescription", viewHolder.courseDescription.getText().toString());
        bundle.putString("courseId", viewHolder.courseId);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.getCourseName());
        holder.courseDescription.setText(course.getCourseDescription());
        holder.courseId = course.getObjectId();
        BmobFile img = course.getHeadFile();
        if (img != null)
            Picasso.with(mContext).load(img.getFileUrl()).into(holder.courseImg);
        else
            Picasso.with(mContext).load(R.drawable.lesson_holder).into(holder.courseImg);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View courseItem;
        ImageView courseImg;  // 图片
        TextView courseName;  // 课程名
        TextView courseDescription;  // 课程简介
        String courseId = null;  // 数据库生成的唯一课程id

        ViewHolder(View view) {
            super(view);
            courseItem = view;
            courseImg = courseItem.findViewById(R.id.course_img);
            courseName = courseItem.findViewById(R.id.course_name);
            courseDescription = courseItem.findViewById(R.id.course_description);
        }
    }
}
