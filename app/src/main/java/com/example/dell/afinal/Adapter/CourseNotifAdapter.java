package com.example.dell.afinal.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.afinal.Activity.HistoryNotificationActivity;
import com.example.dell.afinal.Activity.MyNotificationActivity;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.View.CircleImageView;
import com.example.dell.afinal.bean.CourseNotification;
import com.example.dell.afinal.bean.User;
import com.squareup.picasso.Picasso;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class CourseNotifAdapter extends RecyclerView.Adapter<CourseNotifAdapter.ViewHolder> {
    private List<CourseNotification> notifications;
    private Context mContext;
    private View mView;

    public CourseNotifAdapter(List<CourseNotification> list) {
        notifications = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        if (mContext instanceof MyNotificationActivity) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent,
                    false);
        } else if (mContext instanceof HistoryNotificationActivity) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.history_item, parent,
                    false);
        }
        final ViewHolder viewHolder = new ViewHolder(mView);
        viewHolder.readTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReadTagClicked(viewHolder);
            }
        });
        return viewHolder;
    }

    // 点击标记已读
    private void onReadTagClicked(final ViewHolder holder) {
        if (holder.readTag.getText().toString().equals("已读"))
            return;
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        CourseNotification notification = new CourseNotification();
        notification.setObjectId(holder.id);
        relation.add(notification);
        user.setCourseNotifications(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    onCommitSuccess(holder);
                } else {
                    Log.e("提交标记失败:", e.toString());
                }
            }
        });
    }

    // 提交成功, 提醒更新消息列表, 不再显示已读消息
    private void onCommitSuccess(ViewHolder holder) {
        holder.readTag.setText("已读");
        holder.readTag.setTextColor(mContext.getResources().getColor(R.color.white));
        holder.readTag.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        ToastUtil.toast(mContext, "标为已读");
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CourseNotification notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.content.setText(notification.getContent());
        holder.date.setText(notification.getCreatedAt());
        holder.id = notification.getObjectId();
        getAuthorData(notification.getAuthor().getObjectId(), holder);
    }

    // 根据作者id从服务器读取作者头像及用户名
    private void getAuthorData(String id, final ViewHolder holder) {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(id, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    onLoadUserSuccess(user, holder);
                } else {
                    onLoadUserFailed(holder);
                    Log.e("读取用户资料失败:", e.toString());
                }
            }
        });
    }

    // 成功读取用户数据
    private void onLoadUserSuccess(User user, ViewHolder holder) {
        if(user.getNickName() != null) {
            holder.userName.setText(user.getNickName());
        } else {
            holder.userName.setText(user.getUsername());
        }
        if (user.getHeadFile() != null) {
            Picasso.with(mContext).load(user.getHeadFile().getFileUrl())
                    .into(holder.userImage);
        } else {
            Picasso.with(mContext).load(R.mipmap.ic_head).into(holder.userImage);
        }
    }

    // 读取出现异常
    private void onLoadUserFailed(ViewHolder holder) {
        holder.userName.setText("匿名用户");
        Picasso.with(mContext).load(R.mipmap.ic_head).into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        CircleImageView userImage;    // 通知来源的头像
        TextView userName;            // 通知来源的用户名(教师或管理员)
        TextView title;               // 通知的标题
        TextView content;             // 通知的内容
        TextView date;                // 通知的日期
        TextView readTag;             // 标为已读
        String id;                    // 通知的唯一标识


        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            userImage = itemView.findViewById(R.id.cms_team);
            userName = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.notification_title);
            content = itemView.findViewById(R.id.notification_content);
            date = itemView.findViewById(R.id.notification_date);
            readTag = itemView.findViewById(R.id.read_tag);
        }
    }
}
