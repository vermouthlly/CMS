package com.example.dell.afinal.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.afinal.R;
import com.example.dell.afinal.bean.FeedBack;
import com.example.dell.afinal.View.LinedEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends AppCompatActivity {

    @BindView(R.id.feedback_iv_back)
    ImageView feedbackIvBack;
    @BindView(R.id.feedback_et_content)
    LinedEditText feedbackEtContent;
    @BindView(R.id.feedback_et_contact)
    EditText feedbackEtContact;
    @BindView(R.id.feedback_tv_commit)
    TextView feedbackTvCommit;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        unbinder = ButterKnife.bind(this);
    }

    @OnClick({R.id.feedback_iv_back, R.id.feedback_tv_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.feedback_iv_back:
                this.finish();
                break;
            case R.id.feedback_tv_commit:
                doCommit();
                break;
        }
    }

    /**
     * 提交
     */
    private void doCommit() {
        String content = feedbackEtContent.getText().toString().trim();
        String contact = feedbackEtContact.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(FeedbackActivity.this, "请输入反馈内容", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(contact)) {
            Toast.makeText(FeedbackActivity.this, "请输入联系方式", Toast.LENGTH_SHORT).show();
        } else {
            FeedBack feedBack = new FeedBack(content, contact);
            feedBack.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        FeedbackActivity.this.finish();
                        Toast.makeText(FeedbackActivity.this, "提交成功，感谢反馈！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FeedbackActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
