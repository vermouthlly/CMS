package com.example.dell.afinal.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

// 课程类
public class Course extends BmobObject {

    private String courseName; // 课程名

    private String courseDescription; // 课程简介

    private String courseTime; // 上课时间

    private String coursePlace; // 上课地点

    private Integer courseCapacity; // 课程容量

    private BmobFile headFile; // 课程图片

    private String invitationCode; // 课程邀请码

    private User manager;      // 一对一关系：用于存储创建并发布该课程的老师

    private BmobRelation selectors;  // 多对多关系：用于存储选择了该课程的学生

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

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public String getCoursePlace() {
        return coursePlace;
    }

    public void setCoursePlace(String coursePlace) {
        this.coursePlace = coursePlace;
    }

    public Integer getCourseCapacity() {
        return courseCapacity;
    }

    public void setCourseCapacity(Integer courseCapacity) {
        this.courseCapacity = courseCapacity;
    }

    public BmobFile getHeadFile() {
        return headFile;
    }

    public void setHeadFile(BmobFile headFile) {
        this.headFile = headFile;
    }

    public BmobRelation getSelectors() {
        return selectors;
    }

    public void setSelectors(BmobRelation selectors) {
        this.selectors = selectors;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
}
