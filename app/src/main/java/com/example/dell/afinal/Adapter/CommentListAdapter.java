package com.example.dell.afinal.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.View.CircleImageView;
import com.example.dell.afinal.bean.Comment;
import com.example.dell.afinal.bean.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    
    private List<Comment> comments = new ArrayList<>();
    private Context mContext;

    public CommentListAdapter(List<Comment> list) {
        comments = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.authorId = comment.getAuthor().getObjectId();
        holder.commentText.setText(comment.getContent());
        holder.date.setText(comment.getCreatedAt());
        holder.floorNum.setText("#" + String.valueOf(position+1));
        getAuthorData(holder.authorId, holder);
    }

    // 根据作者id从服务器读取作者头像及用户名(直接getAuthor获取失败 原因尚未明确)
    private void getAuthorData(String id, final ViewHolder holder) {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(id, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    onLoadUserSuccess(user, holder);
                } else {
                    onLoadUserFailed(holder);
                }
            }
        });
    }

    // 成功读取用户数据后, 加载到相应的控件中
    private void onLoadUserSuccess(User user, ViewHolder holder) {
        if(user.getNickName() != null) {
            holder.userName.setText(user.getNickName());
        } else {
            holder.userName.setText(user.getUsername());
        }
        if (user.getHeadFile() != null) {
            Picasso.with(mContext).load(user.getHeadFile().getFileUrl())
                    .into(holder.userImg);
        }
        else {
            Picasso.with(mContext).load(R.mipmap.ic_head).into(holder.userImg);
        }
        if (user.getSex() == 0) {
            Picasso.with(mContext).load(R.mipmap.male).into(holder.userSex);
        } else {
            Picasso.with(mContext).load(R.mipmap.female).into(holder.userSex);
        }
    }

    // 读取出现异常
    private void onLoadUserFailed(ViewHolder holder) {
        holder.userName.setText("匿名用户");
        Picasso.with(mContext).load(R.mipmap.ic_head).into(holder.userImg);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View commentView;
        CircleImageView userImg; // 用户头像
        TextView userName;       // 用户昵称
        ImageView userSex;       // 用户性别
        TextView date;           // 发表日期
        TextView commentText;    // 评论内容
        TextView floorNum;       // 楼层
        String authorId;         // 发表评论的用户的id

        ViewHolder(View itemView) {
            super(itemView);
            commentView = itemView;
            userImg = itemView.findViewById(R.id.user_head_image);
            userName = itemView.findViewById(R.id.user_name);
            userSex = itemView.findViewById(R.id.user_sex);
            date = itemView.findViewById(R.id.date);
            commentText = itemView.findViewById(R.id.comment_text);
            floorNum = itemView.findViewById(R.id.floor);
        }
    }
}
