package com.example.dell.afinal.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.User;
import com.example.dell.afinal.Utils.ToastUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etUsername,etPassword;
    Button btnIn;
    TextView linkSignup;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindView();
    }

    // 绑定控件
    public void bindView() {
        etUsername = findViewById(R.id.login_et_name);
        etPassword = findViewById(R.id.login_et_pwd);
        btnIn = findViewById(R.id.login_tv_login);
        linkSignup = findViewById(R.id.link_signup);
        linkSignup.setClickable(true);
        progressBar = findViewById(R.id.progress);
        btnIn.setOnClickListener(this);
        linkSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_tv_login:
                hideKeyboard(view);
                login();
                break;
            case R.id.link_signup:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    // 用户登录
    public void login() {
        // 获取用户输入的用户名和密码
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        // 非空验证
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtil.toast(LoginActivity.this, "用户名或密码不能为空");
            return;
        }

        // 显示进度条
        progressBar.setVisibility(View.VISIBLE);

        // 使用BmobSDK提供的登陆功能
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                // 屏蔽进度条
                progressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    ToastUtil.toast(getApplicationContext(), "登陆成功");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    switch (e.getErrorCode()) {
                        case 101:
                            ToastUtil.toast(getApplicationContext(), "用户名或密码错误");
                            break;
                        default:
                            ToastUtil.toast(getApplicationContext(), "登陆失败,请检查你的网络");
                            break;
                    }
                }
            }
        });
    }

    // 需要时隐藏软键盘
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
