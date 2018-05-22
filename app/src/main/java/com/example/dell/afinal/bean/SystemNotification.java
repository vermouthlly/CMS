package com.example.dell.afinal.bean;

import cn.bmob.v3.BmobObject;

// 系统消息类
public class SystemNotification extends BmobObject {

    private String title;     // 通知的标题
    private String content;   // 通知的内容
    private String date;      // 通知创建的日期

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
