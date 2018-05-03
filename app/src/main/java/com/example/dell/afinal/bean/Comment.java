package com.example.dell.afinal.bean;


import cn.bmob.v3.BmobObject;

// 评论类
public class Comment extends BmobObject {
    private String content;    // 评论内容
    private Post post;         // 评论属于的帖子
    private User author;       // 发表评论的用户

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
