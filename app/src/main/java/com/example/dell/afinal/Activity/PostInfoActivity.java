package com.example.dell.afinal.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.afinal.Adapter.CommentListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.View.CircleImageView;
import com.example.dell.afinal.bean.Comment;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.Post;
import com.example.dell.afinal.bean.User;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PostInfoActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.user_head_image) CircleImageView userImg;
    @BindView(R.id.user_name) TextView userName;
    @BindView(R.id.date) TextView date;
    @BindView(R.id.user_sex) ImageView userSex;
    @BindView(R.id.post_title) TextView title;
    @BindView(R.id.post_text) TextView content;
    @BindView(R.id.back) ImageView backButton;
    @BindView(R.id.like_icon) ImageView likeButton;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.comment_content) EditText commentContent;
    @BindView(R.id.commit_comment) Button commit;
    @BindView(R.id.is_loading) ProgressBar progressBar;
    @BindView(R.id.comments_loading) ProgressBar commentsLoading;
    @BindView(R.id.no_comment_hint) TextView noCommentHint;

    private String postId;   // 帖子的唯一标识
    private boolean hasLiked; // 用户是否收藏过该帖子
    private List<Comment> commentList = new ArrayList<>();
    private CommentListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);

        unbinder = ButterKnife.bind(this);
        loadPostInfo();
    }

    @OnClick({R.id.like_icon, R.id.back, R.id.commit_comment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.like_icon:
                onLikeIconClicked();
                break;
            case R.id.back:
                onBackIconClicked();
                break;
            case R.id.commit_comment:
                onCommittingButtonClicked();
                break;
        }
    }

    // 加载帖子信息到布局中
    private void loadPostInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            title.setText(bundle.getString("postTitle", "——"));
            content.setText(bundle.getString("postContent", "——"));
            date.setText(bundle.getString("date", ""));
            postId = bundle.getString("postId", "");
            getAuthorData(bundle.getString("authorId", ""));
            readPostLikes();
        }
    }

    // 根据作者id从服务器读取作者头像及用户名(直接getAuthor获取失败 原因尚未明确)
    private void getAuthorData(String id) {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(id, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    onLoadUserSuccess(user);
                } else {
                    onLoadUserFailed();
                }
            }
        });
    }

    // 成功读取用户数据
    private void onLoadUserSuccess(User user) {
        if(user.getNickName() != null) {
            userName.setText(user.getNickName());
        } else {
            userName.setText(user.getUsername());
        }
        if (user.getHeadFile() != null) {
            Picasso.with(PostInfoActivity.this).load(user.getHeadFile().getFileUrl())
                    .into(userImg);
        }
        else {
            Picasso.with(PostInfoActivity.this).load(R.mipmap.ic_head).into(userImg);
        }
        if (user.getSex() == 0) {
            Picasso.with(PostInfoActivity.this).load(R.mipmap.male).into(userSex);
        } else {
            Picasso.with(PostInfoActivity.this).load(R.mipmap.female).into(userSex);
        }
        loadCommentsFromServer();  // 这里加载评论数据
    }

    // 读取出现异常
    private void onLoadUserFailed() {
        userName.setText("匿名用户");
        Picasso.with(PostInfoActivity.this).load(R.mipmap.ic_head).into(userImg);
    }

    // 读取收藏了该帖子的用户列表
    private void readPostLikes() {
        Post post = new Post();
        post.setObjectId(postId);
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("likes", new BmobPointer(post));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    checkIfHasLiked(list);
                } else {
                    Log.e("读取收藏列表失败:", e.toString());
                }
            }
        });
    }

    // 检查当前用户是否已经收藏过该帖子
    private void checkIfHasLiked(List<User> list) {
        User user = BmobUser.getCurrentUser(User.class);
        List<String> idList = new ArrayList<>();
        for (User u : list)
            idList.add(u.getObjectId());

        if (idList.contains(user.getObjectId())) {
            likeButton.setImageResource(R.mipmap.full_star);
            hasLiked = true;
        } else {
            likeButton.setImageResource(R.mipmap.empty_star);
            hasLiked = false;
        }
    }

    // 点击收藏按钮
    private void onLikeIconClicked() {
        if (hasLiked) {
            cancelLike();
        } else {
            like();
        }
        EventBus.getDefault().post(new MessageEvent("like"));
    }

    // 取消收藏
    private void cancelLike() {
        Post post = new Post();
        post.setObjectId(postId);
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        relation.remove(user);
        post.setLikes(relation);
        post.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    onCancelLikeSuccess();
                } else {
                    onNetworkException();
                }
            }
        });
        removeLikeFromUserLike();
    }

    // 同时从用户收藏列表中移除该帖子
    private void removeLikeFromUserLike() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        Post post = new Post();
        post.setObjectId(postId);
        relation.remove(post);
        user.setLikes(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("User: ", "取消收藏成功");
                } else {
                    Log.e("User: ", "操作失败: " + e.toString());
                }
            }
        });
    }

    // 取消收藏成功
    private void onCancelLikeSuccess() {
        ToastUtil.toast(PostInfoActivity.this, "已取消收藏");
        likeButton.setImageResource(R.mipmap.empty_star);
        hasLiked = false;
    }

    // 取消收藏失败
    private void onNetworkException() {
        ToastUtil.toast(PostInfoActivity.this, "操作失败,请检查你的网络或稍后再试");
    }

    // 增加收藏
    private void like() {
        Post post = new Post();
        post.setObjectId(postId);
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        post.setLikes(relation);
        post.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    onAddLikeSuccess();
                } else {
                    onNetworkException();
                }
            }
        });
        addPostToUserLike();
    }

    // 同时把该帖子增加到用户的收藏列表中
    private void addPostToUserLike() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        Post post = new Post();
        post.setObjectId(postId);
        relation.add(post);
        user.setLikes(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("User: ", "增加收藏成功");
                } else {
                    Log.e("User: ", "收藏失败: " + e.toString());
                }
            }
        });
    }

    // 取消收藏成功
    private void onAddLikeSuccess() {
        ToastUtil.toast(PostInfoActivity.this, "已收藏");
        likeButton.setImageResource(R.mipmap.full_star);
        hasLiked = true;
    }

    // 从服务器读取属于该帖子的所有评论
    private void loadCommentsFromServer() {
        commentsLoading.setVisibility(View.VISIBLE);
        BmobQuery<Comment> query = new BmobQuery<>();
        Post post = new Post();
        post.setObjectId(postId);
        query.addWhereEqualTo("post", new BmobPointer(post));
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    onLoadCommentsSuccess(list);
                } else {
                    onLoadCommentsFailed();
                }
            }
        });
    }

    // 读取评论数据成功
    private void onLoadCommentsSuccess(List<Comment> list) {
        commentList = list;
        createRecyclerView();
        if (!checkHasComment(list)) return;
        commentsLoading.setVisibility(View.INVISIBLE);
    }
    
    // 检查帖子是否有评论
    private boolean checkHasComment(List<Comment> list) {
        if (list.size() == 0) {
            noCommentHint.setText("这个帖子暂时还没有评论~");
            commentsLoading.setVisibility(View.INVISIBLE);
            return false;
        }
        noCommentHint.setText("——全部评论——");
        return true;
    }

    // 读取评论数据失败
    private void onLoadCommentsFailed() {
        ToastUtil.toast(PostInfoActivity.this, "加载评论失败,请检查你的网络");
        commentsLoading.setVisibility(View.INVISIBLE);
        
    }

    // 构建评论列表 RecyclerView
    private void createRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(PostInfoActivity.this);
        recyclerView.setLayoutManager(manager);
        adapter = new CommentListAdapter(commentList);
        recyclerView.setAdapter(adapter);
    }

    // 点击发表按钮
    private void onCommittingButtonClicked() {
        commitCommentToServer();
    }

    // 提交评论内容至服务器
    private void commitCommentToServer() {
        final Comment comment = new Comment();
        String content = commentContent.getText().toString();
        if (checkedCommentContent(content)) return;
        comment.setContent(content);
        User user = BmobUser.getCurrentUser(User.class);
        comment.setAuthor(user);
        Post post = new Post();
        post.setObjectId(postId);
        comment.setPost(post);
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    onCommitSuccess(comment);
                } else {
                    onCommitFailed();
                    Log.d("提交评论失败:", e.toString());
                }
            }
        });
    }

    // 提交评论成功
    private void onCommitSuccess(Comment comment) {
        commentList.add(comment);
        adapter.notifyDataSetChanged();
        ToastUtil.toast(PostInfoActivity.this, "发表成功");
        commentContent.setText("");
        hideKeyboard(commentContent);
        noCommentHint.setText("——全部评论——");
        progressBar.setVisibility(View.INVISIBLE);
    }

    // 提交失败
    private void onCommitFailed() {
        ToastUtil.toast(PostInfoActivity.this, "评论失败！请检查你的网络");
        progressBar.setVisibility(View.INVISIBLE);
    }

    // 检查输入评论内容
    private boolean checkedCommentContent(String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtil.toast(PostInfoActivity.this, "评论不能为空");
            return true;
        }
        progressBar.setVisibility(View.VISIBLE);
        return false;
    }

    private void onBackIconClicked() {
        PostInfoActivity.this.finish();
    }

    // 需要时隐藏软键盘
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
