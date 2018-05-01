package com.example.dell.afinal.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.User;
import com.example.dell.afinal.View.CircleImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class UserDetailActivity extends AppCompatActivity {

    @BindView(R.id.user_iv_back)
    ImageView userIvBack;
    @BindView(R.id.user_civ_head)
    CircleImageView userCivHead;
    @BindView(R.id.user_rl_head)
    RelativeLayout userRlHead;
    @BindView(R.id.user_tv_name)
    TextView userTvName;
    @BindView(R.id.user_rl_name)
    RelativeLayout userRlName;
    @BindView(R.id.user_cb_sex)
    CheckBox userCbSex;
    @BindView(R.id.user_tv_signature)
    TextView userTvSignature;
    @BindView(R.id.user_rl_signature)
    RelativeLayout userRlSignature;
    @BindView(R.id.user_tv_unlogin)
    TextView userTvUnlogin;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        // 若已登录则读取用户数据渲染到用户资料界面中
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            userTvName.setText(user.getUsername());
            userTvSignature.setText(user.getSign());
            if (user.getSex() != null && user.getSex().equals("nan")) {
                userCbSex.setChecked(false);
            } else {
                userCbSex.setChecked(true);
            }
            loadHead(user);
        }
        userCbSex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    changeSex(1);
                }else {
                    changeSex(0);
                }
            }
        });
    }

    /**
     * 加载头像
     *
     * @param user
     */
    private void loadHead(User user) {
        if (user.getHeadFile() != null) {
            BmobFile headFileFile = user.getHeadFile();
            Picasso.with(UserDetailActivity.this).load(headFileFile.getFileUrl()).into(userCivHead);
        } else {
            Picasso.with(UserDetailActivity.this).load(R.mipmap.ic_head).into(userCivHead);
        }
    }

    /**
     * 修改性别
     *
     * @param sex 性别
     */
    private void changeSex(Integer sex) {
        User user = BmobUser.getCurrentUser(User.class);
        user.setSex(sex);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtil.toast(getApplicationContext(), "修改成功");
                } else {
                    ToastUtil.toast(getApplicationContext(), "修改失败");
                }
            }
        });
    }

    @OnClick({R.id.user_iv_back, R.id.user_civ_head, R.id.user_rl_head, R.id.user_rl_name,
            R.id.user_rl_signature, R.id.user_tv_unlogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_iv_back:
                this.finish();
                break;
            case R.id.user_civ_head:
                break;
            case R.id.user_rl_head:
                checkSelfPermission();
                break;
            case R.id.user_rl_name:
                ToastUtil.toast(getApplicationContext(), "昵称暂不支持修改");
                break;
            case R.id.user_rl_signature:
                showEditDialog();
                break;
            case R.id.user_tv_unlogin:
                doLoginOut();
                break;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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


    /**
     * 登出
     */
    private void doLoginOut() {
        ToastUtil.toast(getApplicationContext(), "注销登录");
        BmobUser.logOut();
        startActivity(new Intent(UserDetailActivity.this, LoginActivity.class));
        this.finish();
    }

    private void showEditDialog() {
        View view = getLayoutInflater().inflate(R.layout.edit_dialog, null, false);
        final EditText etSign = (EditText) view.findViewById(R.id.sign_et_sign);
        TextView tvOk = (TextView) view.findViewById(R.id.sign_tv_ok);
        final AlertDialog editDialog = new AlertDialog.Builder(this).create();
        editDialog.setView(view);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.dismiss();
                User user = BmobUser.getCurrentUser(User.class);
                user.setSign(etSign.getText().toString());
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            ToastUtil.toast(getApplicationContext(), "编辑成功");
                        } else {
                            ToastUtil.toast(getApplicationContext(), "编辑失败");
                        }
                    }
                });
            }
        };
        tvOk.setOnClickListener(listener);
        editDialog.show();
    }

    /*
    * 从相册中选择头像并上传到服务器
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 0) {
            List<String> selectPaths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (selectPaths.size() != 0) {
                String imagePath = selectPaths.get(0);
                final BmobFile headFile = new BmobFile(new File(imagePath));
                headFile.uploadblock(new UploadFileListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            User user = BmobUser.getCurrentUser(User.class);
                            user.setHeadFile(headFile);
                            Picasso.with(UserDetailActivity.this).load(headFile.getFileUrl())
                                    .into(userCivHead);
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ToastUtil.toast(getApplicationContext(), "头像上传成功");
                                    } else {
                                        ToastUtil.toast(getApplicationContext(), "头像上传失败");
                                    }
                                }
                            });
                        } else {
                            ToastUtil.toast(getApplicationContext(), "头像上传失败");
                        }

                    }

                    @Override
                    public void onProgress(Integer value) {
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
