package com.example.dell.afinal.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.Post;
import com.example.dell.afinal.bean.User;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class NewPostActivity extends AppCompatActivity {

    private Unbinder unbinder;
    private Context context;

    String Imagepath;  // 图片路径
    String courseId;   // 课程的唯一id


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
    @BindView(R.id.add_post_photo)
    ImageView post_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        unbinder = ButterKnife.bind(this);
        courseId = getIntent().getStringExtra("courseId");
    }

    @OnClick({R.id.cancel, R.id.commit, R.id.add_post_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                onCancleClicked();
                break;
            case R.id.commit:
                onCommitClicked();
                break;
            case R.id.add_post_photo:
                checkSelfPermission();
                break;
        }
    }

    // 点击取消
    private void onCancleClicked() {
        NewPostActivity.this.finish();
    }

    // 点击发表
    private void onCommitClicked() {
        if (checkPostContentInput()) {
            ToastUtil.toast(NewPostActivity.this, "正文内容不能为空，说点什么吧");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(Imagepath)) {
            commitDataToServer();
        } else {
            commitDataToServerWithImage();
        }
    }

    /**
     * 检查权限
     */
    private void checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                ToastUtil.toast(getApplicationContext(), "缺少读写权限");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        } else {
            MultiImageSelector.create()
                    .showCamera(true) // 是否显示相机. 默认为显示
                    .single() // 单选模式
                    .multi() // 多选模式, 默认模式;
                    .start(this, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MultiImageSelector.create()
                            .showCamera(true) // 是否显示相机. 默认为显示
                            .single() // 单选模式
                            .multi() // 多选模式, 默认模式;
                            .start(this, 0);
                } else {
                    ToastUtil.toast(getApplicationContext(), "缺少读写权限,请到设置中打开");
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 0) {
            List<String> selectPaths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (selectPaths.size() != 0) {
                Imagepath = selectPaths.get(0);
                Picasso.with(context).load(new File(Imagepath)).into(post_photo);
            }
        }
    }

    // 检查输入正文内容, 不允许为空
    private boolean checkPostContentInput() {
        String input = inputContent.getText().toString();
        return TextUtils.isEmpty(input);
    }

    // 提交数据到服务器 ( 不包含图片 )
    private void commitDataToServer() {
        String content = inputContent.getText().toString();
        String title = inputTitle.getText().toString();
        User user = BmobUser.getCurrentUser(User.class);
        Post post = new Post();
        post.setContent(content);
        post.setTitle(title);
        post.setAuthor(user);
        post.setPopular(false);
        post.setCourseId(courseId);

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

    // 提交数据到服务器 ( 包含图片 )
    private void commitDataToServerWithImage() {
        String content = inputContent.getText().toString();
        String title = inputTitle.getText().toString();
        User user = BmobUser.getCurrentUser(User.class);
        final BmobFile bmobFile = new BmobFile(new File(Imagepath));
        final Post post = new Post();
        post.setContent(content);
        post.setTitle(title);
        post.setAuthor(user);
        post.setPopular(false);
        post.setCourseId(courseId);

        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    post.setImage(bmobFile);
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
            }
        });
    }

    // 提交成功
    private void onSummitSuccess() {
        progressBar.setVisibility(View.INVISIBLE);
        ToastUtil.toast(NewPostActivity.this, "发表成功");
        updatePostList();
        NewPostActivity.this.finish();
    }

    // 提醒更新帖子列表
    private void updatePostList() {
        MessageEvent event = new MessageEvent("addPost");
        EventBus.getDefault().post(event);
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
