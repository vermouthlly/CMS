package com.example.dell.afinal.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

// 课程类
public class Course extends BmobObject {
    // 课程名
    private String courseName;
    // 课程简介
    private String courseDescription;
    // 课程图片
    private BmobFile headFile;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public BmobFile getHeadFile() {
        return headFile;
    }

    public void setHeadFile(BmobFile headFile) {
        this.headFile = headFile;
    }
}
