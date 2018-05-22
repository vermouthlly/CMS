package com.example.dell.afinal.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.User;

import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Bmob.initialize(this, "430bfefb7fad055dc47f06a0ba6f73e4");

        TextView tv = (TextView) findViewById(R.id.splash_tv_tip);

        String[] tips = {
                "学知不足，业精于勤。\n" +
                        "                      --（唐）韩愈",
                "学者贵知其当然与所以然，若偶能然，不得谓为学。 \n" +
                        "                      -- 孙中山",
                "青年是学习智慧的时期，中年是付诸实践的时期。\n " +
                        "                      -- 卢梭",
                "不要靠馈赠来获得一个朋友。你须贡献你挚情的爱，学习怎样用正当的方法来赢得一个人的心。\n " +
                        "                      -- 苏格拉底",
                "旧书不厌百回读,熟读深思子自知.\n" +
                        "书山有路勤为径,学海无涯苦作舟.",
                "平庸的教师只是叙述；好的教师讲解；优异的教师示范；伟大的教师启发。",
                "知识是引导人生到光明与真实境界的灯烛。",
                "读书是易事，思索是难事，但两者缺一，便全无用处。",
                "每一本书都是一个用黑字印在白纸上的灵魂，只要我的眼睛、我的理智接触了它，它就活起来了。\n" +
                        "                      ——高尔基",
                "虚弱者无力承受他们在书中读到的杰出见解，因为那只给他们提供了更多犯错误的机会。\n" +
                        "                      ——哈利法克斯"
        };
        int random = new Random().nextInt(tips.length);

        tv.setText(tips[random]);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 已登录直接跳转到主界面, 未登录则跳转到登录界面
                if (BmobUser.getCurrentUser(User.class) != null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    SplashActivity.this.finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                }
            }
        }, 2000);
    }
}
