package com.example.dell.afinal.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class CourseManagerAdapter extends RecyclerView.Adapter<CourseManagerAdapter.ViewHolder> {
    private List<Course> courseList;
    private Context mContext;

    public CourseManagerAdapter(List<Course> list) {
        courseList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.course_manage_item, parent,
                false);
        final ViewHolder holder = new ViewHolder(view);
        holder.deleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteButtonClicked(holder);
            }
        });
        return holder;
    }

    // 点击删除按钮
    private void onDeleteButtonClicked(final ViewHolder holder) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("你确定要删除课程 " + holder.courseName.getText().toString() +
                        " 吗？(此操作不可以撤回)");
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCourse(holder.courseId);
            }
        });
        dialog.show();
    }

    // 删除课程
    private void deleteCourse(String courseId) {
        Course course = new Course();
        course.setObjectId(courseId);
        course.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    EventBus.getDefault().post(new MessageEvent("deleteCourse"));
                    ToastUtil.toast(mContext, "课程删除成功");
                } else {
                    ToastUtil.toast(mContext, "操作失败，请检查你的往后重试");
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseId = course.getObjectId();
        holder.courseName.setText(course.getCourseName());
        String description = getCourseDesrcripion(course);
        holder.courseDescription.setText(description);
    }

    // 根据课程信息生成课程简介
    private String getCourseDesrcripion(Course course) {
        return "任课老师 " + course.getCourseDescription() + "\n" +
                "上课时间 " + course.getCourseTime() + "\n" +
                "上课地点 " + course.getCoursePlace();
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView courseName;
        TextView courseDescription;
        LinearLayout spreadCourse;
        LinearLayout editCourse;
        LinearLayout deleteCourse;
        String courseId;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            courseName = itemView.findViewById(R.id.course_name);
            courseDescription = itemView.findViewById(R.id.course_intro);
            spreadCourse = itemView.findViewById(R.id.spread_course);
            editCourse = itemView.findViewById(R.id.edit_course);
            deleteCourse = itemView.findViewById(R.id.delete_course);
        }
    }

}
