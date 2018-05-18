package com.example.dell.afinal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dell.afinal.Activity.CourseDiscussionActivity;
import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.Course;

import java.util.List;

public class DiscussionListAdapter extends RecyclerView.Adapter<DiscussionListAdapter.ViewHolder> {
    private Context mContext;
    private List<Course> list = null;

    public DiscussionListAdapter(List<Course> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.discussion_item, parent,
                false);
        final ViewHolder holder = new ViewHolder(view);
        holder.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEnterButtonClicked(holder);
            }
        });
        return holder;
    }

    // 点击" 进入讨论区" 按钮
    private void onEnterButtonClicked(ViewHolder holder) {
        Bundle bundle = new Bundle();
        bundle.putString("title", holder.courseName.getText().toString());
        bundle.putString("courseId", holder.courseId);
        Intent intent = new Intent(mContext, CourseDiscussionActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = list.get(position);
        if (course != null) {
            holder.courseId = course.getObjectId();
            holder.courseName.setText(course.getCourseName());
            holder.courseDescription.setText(course.getCourseDescription());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        String courseId;                  // 课程唯一标识
        TextView courseName;              // 课程名
        TextView courseDescription;       // 课程简介
        TextView studentNum;              // 选课学生数目
        TextView postNum;                 // 该课程讨论区的帖子数目
        Button enter;                     // 进入课程讨论区的按钮

        ViewHolder(View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            courseDescription = itemView.findViewById(R.id.course_description);
            studentNum = itemView.findViewById(R.id.course_num);
            postNum = itemView.findViewById(R.id.post_num);
            enter = itemView.findViewById(R.id.enter_discussion);
        }
    }
}
