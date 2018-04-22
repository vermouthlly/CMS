package com.example.dell.afinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername,etPassword, etConfirmPsd;
    Button regButton;
    TextView linkLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindView();

        // 点击注册按钮
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        // 跳转到登录
        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
        });
    }

    // 绑定控件
    public void bindView() {
        etUsername = findViewById(R.id.reg_et_name);
        etPassword = findViewById(R.id.reg_et_pwd);
        etConfirmPsd = findViewById(R.id.confirm_et_pwd);
        regButton = findViewById(R.id.reg_bt);
        linkLogin = findViewById(R.id.link_login);
        progressBar = findViewById(R.id.progress);
    }

    // 用户注册
    public void register() {
        String userName = etUsername.getText().toString();
        String userPwd = etPassword.getText().toString();
        String conPwd = etConfirmPsd.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            ToastUtil.toast(getApplicationContext(), "学号不能为空");
            return;
        }
        if (TextUtils.isEmpty(userPwd)) {
            ToastUtil.toast(getApplicationContext(), "密码不能为空");
            return;
        }
        if (!userPwd.equals(conPwd)) {
            ToastUtil.toast(getApplicationContext(), "密码不匹配,请保证两次输入的密码相同");
            return;
        }

        // 隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        // 显示进度条
        progressBar.setVisibility(View.VISIBLE);

        User user = new User();
        user.setUsername(userName);
        user.setPassword(userPwd);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                progressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    ToastUtil.toast(getApplicationContext(), "注册成功");
                    RegisterActivity.this.finish();
                } else {
                    switch (e.getErrorCode()) {
                        case 202:
                            ToastUtil.toast(getApplicationContext(), "用户已存在");
                            break;
                        case 203:
                            ToastUtil.toast(getApplicationContext(), "邮箱已存在");
                            break;
                        default:
                            ToastUtil.toast(getApplicationContext(), "注册失败");
                            break;
                    }
                }
            }
        });
    }
}
