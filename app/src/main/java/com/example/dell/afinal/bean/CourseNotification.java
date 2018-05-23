package com.example.dell.afinal.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

// 系统消息类
public class CourseNotification extends BmobObject {

    private String title;     // 通知的标题
    private String content;   // 通知的内容
    private String date;      // 通知创建的日期
    private String CourseId; // 课程ID,用户通知分区
    private User author;     //课程通知，即老师
    private  BmobRelation student; //待接收通知的学生

    public BmobRelation getStudent() {
        return student;
    }

    public void setStudent(BmobRelation student) {
        this.student = student;
    }

    public  void  setCourseId(String ID){this.CourseId=ID;}

    public String getCourseId(){
        return this.CourseId;
    }
    public void setAuthor(User user){
        this.author = user;
    }
    public User getAuthor(){
        return this.author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
