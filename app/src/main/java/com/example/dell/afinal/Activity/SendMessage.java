package com.example.dell.afinal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.View.LinedEditText;
import com.example.dell.afinal.bean.CourseNotification;
import com.example.dell.afinal.bean.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SendMessage extends AppCompatActivity {

    private ImageView back;
    private Button save;
    private EditText tit;
    private LinedEditText content;


    String courseId;   // 课程的唯一id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmessage);
        Intent intent=getIntent();
        courseId = intent.getStringExtra("id");

        init();
        addListener();
    }

    private void init() {
        back = findViewById(R.id.back_btn);
        save = findViewById(R.id.save_btn);
        tit = findViewById(R.id.title);
        content = findViewById(R.id.content);
    }

    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = BmobUser.getCurrentUser(User.class);
                String t = tit.getText().toString();
                String c = content.getText().toString();
                String timeMillis = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA).format(new Date());
                CourseNotification cn = new CourseNotification();
                cn.setCreateTime(timeMillis);
                cn.setTitle(t);
                cn.setContent(c);
                cn.setCourseId(courseId);
                cn.setAuthor(user);
                cn.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null) {
                            ToastUtil.toast(SendMessage.this, "发布成功");
                        }else {
                            ToastUtil.toast(SendMessage.this, "发布失败");
                        }
                    }
                });
                finish();
            }
        });
    }
}
