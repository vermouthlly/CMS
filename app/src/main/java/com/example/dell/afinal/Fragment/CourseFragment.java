package com.example.dell.afinal.Fragment;

import com.example.dell.afinal.bean.MessageEvent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.dell.afinal.Activity.CourseCreate;
import com.example.dell.afinal.Adapter.CourseListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.User;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CourseFragment extends Fragment {
    private View mView;                // 缓存Fragment的View, 避免碎片切换时在onCreateView内重复加载布局
    private ContentLoadingProgressBar progressBar;   // 进度条
    private Toolbar toolbar;                         // 标题栏
    private MaterialSearchView searchView;           // 搜索框
    private XRecyclerView recyclerView;               // 课程列表
    private Button create;                           // 教师增加课程

    private CourseListAdapter adapter;               // 课程适配器
    private List<Course> courseList = new ArrayList<>();
    private int loadFactor = 5;       // 加载课程的数目

    public static CourseFragment newInstance() {
        return new CourseFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);     // 设置显示菜单
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("addCourse") || event.getMessage().equals("updateCourse"))
            generateCourseList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 避免Fragment切换时重复加载布局
        if (mView == null) {
            mView = inflater.inflate(R.layout.course_fragment, container, false);
            bindViews(mView);
            showCreateInterfaceOrNot();
            generateCourseList();
            setOnPullLoadingListener();
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null)
                parent.removeView(mView);
        }
        return mView;
    }

    // 绑定控件
    public void bindViews(View mView) {
        recyclerView = mView.findViewById(R.id.course_list);
        progressBar = mView.findViewById(R.id.is_loading);
        toolbar = mView.findViewById(R.id.toolbar);
        searchView = mView.findViewById(R.id.search_view);
        initSearchView();
        create = mView.findViewById(R.id.create_course);
        onCreateButtonClickedListener();
    }

    // 设置搜索框属性
    private void initSearchView() {
        searchView.setVoiceSearch(false);
        searchView.setEllipsize(true);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestion));
    }

    // 教师点击创建课程按钮
    private void onCreateButtonClickedListener() {
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCourse();
            }
        });
    }

    // 创建课程
    private void createCourse() {
        startActivity(new Intent(getContext(), CourseCreate.class));
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity thisActivity = (AppCompatActivity) getActivity();
        thisActivity.setSupportActionBar(toolbar);
    }

    // 设置RecyclerView下拉刷新以及上拉加载监听
    private void setOnPullLoadingListener() {
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                reloadCourseData("refresh");
            }

            @Override
            public void onLoadMore() {
                reloadCourseData("loadMore");
            }
        });
    }

    // 以新的加载因子重新从服务器读取课程信息
    private void reloadCourseData(final String op) {
        if (op.equals("loadMore")) loadFactor += 5;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reloadCoursesFromServer();
            }
        }, 2000);
    }

    // 重新从服务器读取课程信息
    private void reloadCoursesFromServer() {
        BmobQuery<Course> query = new BmobQuery<>();
        query.order("-createdAt");
        query.addWhereEqualTo("status", "released");
        query.setLimit(loadFactor);
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    courseList.clear();
                    courseList.addAll(list);
                    adapter.notifyDataSetChanged();
                    recyclerView.refreshComplete();
                    recyclerView.loadMoreComplete();
                } else {
                    onLoadFailed(e);
                }
            }
        });
    }

    // 初始化课程列表：获取Course表中的所有数据记录
    private void generateCourseList() {
        BmobQuery<Course> query = new BmobQuery<>();
        query.order("-createdAt");
        query.addWhereEqualTo("status", "released");
        query.setLimit(loadFactor);
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    onLoadSuccess(list);
                } else {
                    onLoadFailed(e);
                }
            }
        });
    }

    // 读取成功
    private void onLoadSuccess(List<Course> list) {
        courseList = list;
        createRecyclerView();
        setListenerForSearchView();
        progressBar.hide();
    }

    // 搭建RecyclerView
    private void createRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);  // 设置布局管理器
        adapter = new CourseListAdapter(courseList);
        recyclerView.setAdapter(adapter);        // 构造并添加适配器
    }

    // 读取失败
    private void onLoadFailed(BmobException e) {
        Log.e("读取课程数据失败:", e.toString());
        ToastUtil.toast(getContext(), "获取课程列表失败, 请检查你的网络后刷新重试");
    }

    // 给搜索框添加监听事件
    private void setListenerForSearchView() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Course> filteredModeList = filter(courseList, newText);
                adapter.setFilter(filteredModeList);
                adapter.animateTo(filteredModeList);
                recyclerView.scrollToPosition(0);
                return true;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {}

            @Override
            public void onSearchViewClosed() {
                adapter.setFilter(courseList);
            }
        });
    }

    // 根据输入关键字过滤课程条目
    private List<Course> filter(List<Course> courses, String query) {
        query = query.toLowerCase();

        final List<Course> filteredModeList = new ArrayList<>();
        for (Course course : courses) {
            String courseName = course.getCourseName();
            String courseDescription = course.getCourseDescription();

            if (courseName.contains(query) || courseDescription.contains(query))
                filteredModeList.add(course);
        }
        return filteredModeList;
    }

    // 根据用户身份判断是否呈现创建课程接口, 若为教师则显示，否则隐藏
    private void showCreateInterfaceOrNot() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            if(user.getIdentity().equals("teacher")) {
                create.setVisibility(View.VISIBLE);
            }
        } else {
            ToastUtil.toast(getActivity(), "您未登录，请先登录！");
        }
    }

    // 加载小菜单,设置搜索按钮
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}