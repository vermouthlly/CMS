package com.example.dell.afinal.Activity;

import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.CourseNotification;
import com.example.dell.afinal.bean.User;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SendMessage extends AppCompatActivity {

    @BindView(R.id.back)
    Button BackButton;
    @BindView(R.id.title_input)
    EditText title;
    @BindView(R.id.time_input)
    EditText time;
    @BindView(R.id.content_input)
    EditText content;
    @BindView(R.id.join_course)
    Button send_button;


    String courseId;   // 课程的唯一id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmessage);
//        BackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // SendMessage.this.finish();
//            }
//        });
//        send_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                  check();
//
//            }
//        });
    }
    @OnClick(R.id.back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                SendMessage.this.finish();
                break;
        }
    }

    public void  check(){
        String str_title = title.getText().toString();
        String str_time = time.getText().toString();
        String str_content = time.getText().toString();
        if(str_title.equals("") || str_time.equals("") || str_content.equals("")){
            ToastUtil.toast(SendMessage.this, "标题，时间，内容不能为空");
        }else{
            CourseNotification message = new CourseNotification();
            //尚未设置通知分区，CourseID应该如何设置
            message.setTitle(str_title);
            message.setDate(str_time);
            message.setContent(str_content);
            User user = BmobUser.getCurrentUser(User.class);
            message.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        ToastUtil.toast(SendMessage.this, "发送通知成功");
                    } else {
                        ToastUtil.toast(SendMessage.this, "发送通知失败,请检查您的网络");
                    }
                }
            });
        }
    }
}
