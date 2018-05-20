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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.SnackBarUtil;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etUsername,etPassword, etConfirmPsd;
    Button regButton;
    TextView linkLogin;
    ProgressBar progressBar;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindView();
        setRadioGroupListener();
    }

    // 绑定控件
    public void bindView() {
        etUsername = findViewById(R.id.reg_et_name);
        etPassword = findViewById(R.id.reg_et_pwd);
        etConfirmPsd = findViewById(R.id.confirm_et_pwd);
        regButton = findViewById(R.id.reg_bt);
        linkLogin = findViewById(R.id.link_login);
        progressBar = findViewById(R.id.progress);
        radioGroup = findViewById(R.id.identity);
        regButton.setOnClickListener(this);
        linkLogin.setOnClickListener(this);
    }

    public void setRadioGroupListener() {
        // 监听radioGroup状态切换
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                if (id == R.id.teacher) {
                    SnackBarUtil.make(radioGroup, "该账号将以教师身份注册");
                } else {
                    SnackBarUtil.make(radioGroup, "该账号将以学生身份注册");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reg_bt:
                hideKeyboard(view);
                register();
                break;
            case R.id.link_login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
                break;
        }
    }

    // 用户注册
    public void register() {
        // 获取用户输入的用户名和密码
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

        // 显示进度条
        progressBar.setVisibility(View.VISIBLE);

        // 获取用户选中的登陆身份
        String identity = "";
        int id = radioGroup.getCheckedRadioButtonId();
        if (id == R.id.student)
            identity = "student";
        else if (id == R.id.teacher)
            identity = "teacher";

        User user = new User();
        user.setUsername(userName);
        user.setPassword(userPwd);
        user.setIdentity(identity);
        user.setSex(0);
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

    // 需要时隐藏软键盘
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
