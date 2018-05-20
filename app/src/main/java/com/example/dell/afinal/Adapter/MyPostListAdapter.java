package com.example.dell.afinal.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.afinal.Activity.PostInfoActivity;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Comment;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.Post;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

// 我的主题帖数据适配器
public class MyPostListAdapter extends RecyclerView.Adapter<MyPostListAdapter.ViewHolder> {

    private List<Post> postList = new ArrayList<>();
    private Context mContext;

    public MyPostListAdapter(List<Post> list) {
        postList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.mypost_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPostItemClicked(viewHolder);
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteIconClicked(viewHolder);
            }
        });
        return viewHolder;
    }

    // 响应列表item的点击事件,跳转到帖子评论界面
    private void onPostItemClicked(ViewHolder viewHolder) {
        Intent intent = new Intent(mContext, PostInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("postTitle", viewHolder.title.getText().toString());
        bundle.putString("postContent", viewHolder.content.getText().toString());
        bundle.putString("date", viewHolder.date.getText().toString());
        bundle.putString("authorId", viewHolder.authorId);
        bundle.putString("postId", viewHolder.postId);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    // 响应删除按钮点击事件
    private void onDeleteIconClicked(final ViewHolder viewHolder) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("你确定要删除这个帖子吗?");
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ToastUtil.toast(mContext, "操作已取消");
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                findAndDeletePostComments(viewHolder);
            }
        });
        dialog.show();
    }

    // 删除指定的帖子
    private void deletePostFromList(ViewHolder viewHolder) {
        final int pos = viewHolder.getAdapterPosition();
        Post post = new Post();
        post.setObjectId(viewHolder.postId);
        post.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    onDeleteSuccess(pos);
                } else {
                    onDeleteFailed();
                    Log.e("删除失败:", e.toString());
                }
            }
        });
    }

    // 删除帖子前先查找并删除属于该帖子的所有评论(如果该帖子有评论的话)
    private void findAndDeletePostComments(final ViewHolder viewHolder) {
        BmobQuery<Comment> query = new BmobQuery<>();
        Post post = new Post();
        post.setObjectId(viewHolder.postId);
        query.addWhereEqualTo("post", new BmobPointer(post));
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        deletePostFromList(viewHolder);
                    } else {
                        deleteComments(list, viewHolder);
                    }
                } else {
                    Log.e("查找帖子评论失败:", e.toString());
                }
            }
        });
    }

    private void deleteComments(List<Comment> list, final ViewHolder viewHolder) {
        for (Comment comment : list) {
            Comment toDelete = new Comment();
            toDelete.setObjectId(comment.getObjectId());
            toDelete.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.d("删除评论成功:", "OK");
                        // 此处再删除帖子
                        deletePostFromList(viewHolder);
                    } else {
                        Log.e("删除评论失败:", e.toString());
                    }
                }
            });
        }
    }

    // 删除成功, 更新列表
    private void onDeleteSuccess(int pos) {
        ToastUtil.toast(mContext, "删除成功");
        /*postList.remove(pos);
        notifyDataSetChanged();*/
        updatePostList();
    }

    // 提醒更新帖子列表
    private void updatePostList() {
        MessageEvent event = new MessageEvent("deletePost");
        EventBus.getDefault().post(event);
    }

    // 删除失败
    private void onDeleteFailed() {
        ToastUtil.toast(mContext, "删除失败,请检查你的网络或稍后再试");
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.postId = post.getObjectId();
        holder.authorId = post.getAuthor().getObjectId();
        holder.title.setText(post.getTitle());
        holder.date.setText(post.getCreatedAt());
        holder.content.setText(post.getContent());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView title;          // 帖子标题
        TextView content;        // 帖子内容
        TextView date;           // 帖子发表的时间
        ImageView delete;        // 删除图标
        String postId;           // 帖子的唯一标识
        String authorId;         // 帖子作者的id

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            title = mView.findViewById(R.id.post_title);
            content = mView.findViewById(R.id.post_content);
            date = mView.findViewById(R.id.post_date);
            delete = mView.findViewById(R.id.delete_post);
        }
    }
}
