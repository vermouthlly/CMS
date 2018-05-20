package com.example.dell.afinal.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.View.CircleImageView;
import com.example.dell.afinal.bean.SystemNotification;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    private List<SystemNotification> notifications;
    private Context mContext;

    public NotificationListAdapter(List<SystemNotification> list) {
        notifications = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

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

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            userImage = itemView.findViewById(R.id.cms_team);
            userName = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.notification_title);
            content = itemView.findViewById(R.id.notification_content);
            date = itemView.findViewById(R.id.notification_date);
        }
    }
}
