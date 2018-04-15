package com.example.dell.afinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.afinal.bean.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class Login extends AppCompatActivity {

    EditText etUsername,etPassword;
    Button btnRegister, btnIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this, "1c4654f0b841178662bb6187bb6ed989 ");

        etUsername = (EditText) findViewById(R.id.login_et_name);
        etPassword = (EditText) findViewById(R.id.login_et_pwd);
        btnRegister = (Button) findViewById(R.id.login_tv_register);
        btnIn = (Button) findViewById(R.id.login_tv_login);
        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入的用户名和密码
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // 非空验证
                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 使用BmobSDK提供的登陆功能
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.login(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                            Login.this.finish();
                        } else {
                            switch (e.getErrorCode()) {
                                case 101:
                                    Toast.makeText(getApplicationContext(), "用户不存在或密码错误", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }
                });
            }
        });
    btnRegister.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String userName = etUsername.getText().toString();
            String userMail = etUsername.getText().toString();
            String userPwd = etPassword.getText().toString();
            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(getApplicationContext(), "请输入邮箱", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(userPwd)) {
                Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            User user = new User();
            user.setUsername(userName);
            user.setSign("编辑个性签名");
            user.setEmail(userMail);
            user.setSex(0);
            user.setPassword(userPwd);
            user.signUp(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "注册成功并已经成功登陆", Toast.LENGTH_SHORT).show();
                        Login.this.finish();
                    } else {
                        switch (e.getErrorCode()) {
                            case 202:
                                Toast.makeText(getApplicationContext(), "用户已存在", Toast.LENGTH_SHORT).show();
                                break;
                            case 203:
                                Toast.makeText(getApplicationContext(), "邮箱已存在", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }
            });
        }
    });
    }
}
