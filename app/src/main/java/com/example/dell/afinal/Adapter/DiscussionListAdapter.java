package com.example.dell.afinal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dell.afinal.Activity.CourseDetailActivity;
import com.example.dell.afinal.Activity.CourseDiscussionActivity;
import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.Post;
import com.example.dell.afinal.bean.User;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

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
        setOnClickListener(holder);
        return holder;
    }

    // 设置点击监听
    private void setOnClickListener(final ViewHolder holder) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCourseDetail(holder);
            }
        });

        holder.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEnterButtonClicked(holder);
            }
        });
    }

    // 跳转到课程详情界面，并把课程相关信息传递过去
    private void showCourseDetail(ViewHolder viewHolder) {
        Intent intent = new Intent(mContext, CourseDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("courseName", viewHolder.courseName.getText().toString());
        bundle.putString("courseDescription", viewHolder.courseDescription.getText().toString());
        bundle.putString("courseTime", viewHolder.courseTime);
        bundle.putString("coursePlace", viewHolder.coursePlace);
        bundle.putString("courseCapacity", viewHolder.courseCapacity);
        bundle.putString("courseId", viewHolder.courseId);
        bundle.putString("invitationCode", viewHolder.invitationCode);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
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
            holder.courseTime = course.getCourseTime();
            holder.coursePlace = course.getCoursePlace();
            holder.courseCapacity = String.valueOf(course.getCourseCapacity());
            holder.invitationCode = course.getInvitationCode();
            loadCourseStudents(holder);
            loadCoursePostNumber(holder);
        }
    }

    // 读取选择这门课程的学生的人数
    private void loadCourseStudents(final ViewHolder holder) {
        BmobQuery<User> query = new BmobQuery<>();
        Course course = new Course();
        course.setObjectId(holder.courseId);
        query.addWhereRelatedTo("selectors", new BmobPointer(course));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() <= 99) {
                        holder.studentNum.setText(String.valueOf(list.size()));
                    } else {
                        holder.studentNum.setText("99+");
                    }
                } else {
                    holder.studentNum.setText("-");
                    Log.e("读取选课人数失败:", e.toString());
                }
            }
        });
    }

    // 读取该门课程的帖子数
    private void loadCoursePostNumber(final ViewHolder holder) {
        BmobQuery<Post> query = new BmobQuery<>();
        query.addWhereEqualTo("courseId", holder.courseId);
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    holder.postNum.setText(String.valueOf(list.size()));
                } else {
                    holder.postNum.setText("-");
                    Log.e("读取课程帖子失败:", e.toString());
                }
            }
        });
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
        String courseTime;                // 上课时间
        String coursePlace;               // 上课地点
        String courseCapacity;            // 课程容量
        String invitationCode;            // 课程邀请码
        Button enter;                     // 进入课程讨论区的按钮

        View view;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            courseName = itemView.findViewById(R.id.course_name);
            courseDescription = itemView.findViewById(R.id.course_description);
            studentNum = itemView.findViewById(R.id.course_num);
            postNum = itemView.findViewById(R.id.post_num);
            enter = itemView.findViewById(R.id.enter_discussion);
        }
    }
}
