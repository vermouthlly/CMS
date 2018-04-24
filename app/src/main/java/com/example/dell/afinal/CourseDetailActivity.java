package com.example.dell.afinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CourseDetailActivity extends AppCompatActivity {
    private TextView courseName;
    private TextView courseDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        bindView();
        loadCourseInfo();
    }

    // 加载课程信息到控件内容中
    public void loadCourseInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;
        String name = bundle.getString("courseName", "");
        String description = bundle.getString("courseDescription", "");
        courseName.setText(name);
        courseDescription.setText(description);
    }

    // 绑定控件
    public void bindView() {
        courseName = findViewById(R.id.course_name);
        courseDescription = findViewById(R.id.course_intro);
    }
}
