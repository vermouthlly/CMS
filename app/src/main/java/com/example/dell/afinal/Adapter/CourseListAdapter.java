package com.example.dell.afinal.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.Course;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

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
        ViewHolder viewHolder = new ViewHolder(view);

        /*此处加入事件处理逻辑*/

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.getCourseName());
        holder.courseDescription.setText(course.getCourseDescription());
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
        ImageView courseImg;
        TextView courseName;
        TextView courseDescription;

        ViewHolder(View view) {
            super(view);
            courseItem = view;
            courseImg = courseItem.findViewById(R.id.course_img);
            courseName = courseItem.findViewById(R.id.course_name);
            courseDescription = courseItem.findViewById(R.id.course_description);
        }
    }
}
