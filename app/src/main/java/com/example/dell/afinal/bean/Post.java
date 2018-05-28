package com.example.dell.afinal.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

// 讨论区贴子类
public class Post extends BmobObject {

    private String title;         // 帖子标题
    private String content;       // 帖子内容
    private User author;          // 帖子的发布者，这里体现的是一对一的关系，该帖子属于某个用户
    private String courseId;      // 帖子所属的课程分区
    private BmobFile image;       // 帖子图片(optional)
    private BmobRelation likes;   // 多对多关系：用于存储喜欢该帖子的所有用户
    private BmobRelation zan;     // 多对多关系：用于存储点赞该帖子的所有用户
    private boolean popularTag;      // 帖子是否被教师加精

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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public BmobRelation getZan() {
        return zan;
    }

    public void setZan(BmobRelation zan) {
        this.zan = zan;
    }

    public boolean isPopular() {
        return popularTag;
    }

    public void setPopular(boolean popular) {
        this.popularTag = popular;
    }
}
