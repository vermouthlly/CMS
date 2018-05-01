package com.example.dell.afinal.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Post;
import com.example.dell.afinal.bean.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class NewPostActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.commit)
    TextView commit;
    @BindView(R.id.edit_title)
    EditText inputTitle;
    @BindView(R.id.edit_content)
    EditText inputContent;
    @BindView(R.id.committing)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        unbinder = ButterKnife.bind(this);
    }

    @OnClick({R.id.cancel, R.id.commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                onCancleClicked();
                break;
            case R.id.commit:
                onCommitClicked();
                break;
        }
    }

    // 点击取消
    public void onCancleClicked() {
        NewPostActivity.this.finish();
    }

    // 点击发表
    public void onCommitClicked() {
        if (checkPostContentInput()) {
            ToastUtil.toast(NewPostActivity.this, "正文内容不能为空");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        commitDataToServer();
    }

    // 检查输入正文内容, 不允许为空
    public boolean checkPostContentInput() {
        String input = inputContent.getText().toString();
        return TextUtils.isEmpty(input);
    }

    // 提交数据到服务器
    public void commitDataToServer() {
        String content = inputContent.getText().toString();
        String title = inputTitle.getText().toString();
        User user = BmobUser.getCurrentUser(User.class);
        Post post = new Post();
        post.setContent(content);
        post.setTitle(title);
        post.setAuthor(user);

        post.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    onSummitSuccess();
                } else {
                    onNetworkException();
                }
            }
        });
    }

    // 提交成功
    public void onSummitSuccess() {
        progressBar.setVisibility(View.INVISIBLE);
        ToastUtil.toast(NewPostActivity.this, "发表成功");
        NewPostActivity.this.finish();
    }

    // 网络错误
    public void onNetworkException() {
        ToastUtil.toast(NewPostActivity.this, "发表失败,请检查你的网络");
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
