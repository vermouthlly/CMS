package com.example.dell.afinal.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.dell.afinal.Adapter.CourseListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CourseFragment extends Fragment {
    private View mView;                // 缓存Fragment的View, 避免碎片切换时在onCreateView内重复加载布局
    private ProgressBar progressBar;           // 进度条
    private Toolbar toolbar;                   // 标题栏
    private MaterialSearchView searchView;     // 搜索框
    private SwipeRefreshLayout refreshLayout;  // 下拉刷新
    private RecyclerView recyclerView;         // 课程列表

    private CourseListAdapter adapter;         // 课程适配器
    private List<Course> courseList = new ArrayList<>();
    public static final int DATA_READY = 1;
    public static final int LOAD_FAILED = 2;

    // 通过Handler更新UI
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_READY:
                    progressBar.setVisibility(View.INVISIBLE);
                    refreshLayout.setRefreshing(false);
                    ToastUtil.toast(getContext(), "所有课程加载完成");
                    break;
                case LOAD_FAILED:
                    progressBar.setVisibility(View.INVISIBLE);
                    refreshLayout.setRefreshing(false);
                    ToastUtil.toast(getContext(), "获取课程列表失败, 请刷新重试");
                default: break;
            }
        }
    };

    public static CourseFragment newInstance() {
        return new CourseFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);     // 设置显示菜单
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 避免Fragment切换时重复加载布局
        if (mView == null) {
            mView = inflater.inflate(R.layout.course_fragment, container, false);
            bindViews(mView);
            generateCourseList();        // 这里完成数据加载
            progressBar.setVisibility(View.VISIBLE);
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null)
                parent.removeView(mView);
        }
        return mView;
    }

    // 绑定控件
    public void bindViews(View mView) {
        progressBar = mView.findViewById(R.id.is_loading);
        toolbar = mView.findViewById(R.id.toolbar);
        searchView = mView.findViewById(R.id.search_view);
        refreshLayout = mView.findViewById(R.id.swipe_refresh);
        initSearchView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity thisActivity = (AppCompatActivity) getActivity();
        thisActivity.setSupportActionBar(toolbar);
        refresh();
    }

    // 搭建RecyclerView
    public void createRecyclerView() {
        recyclerView = mView.findViewById(R.id.course_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);  // 设置布局管理器

        adapter = new CourseListAdapter(courseList);
        recyclerView.setAdapter(adapter);        // 构造并添加适配器
        adapter.notifyDataSetChanged();
    }

    // 设置搜索框属性
    public void initSearchView() {
        searchView.setVoiceSearch(false);
        searchView.setEllipsize(true);
    }

    // 给搜索框添加监听事件
    public void setListenerForSearchView() {
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
    public List<Course> filter(List<Course> courses, String query) {
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

    // 定义下拉刷新行为
    public void refresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                generateCourseList();
            }
        });
    }

    // 初始化课程列表：获取Course表中的所有数据记录，Bmob规定上限为500
    public void generateCourseList() {
        BmobQuery<Course> query = new BmobQuery<>();
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    courseList = new ArrayList<>(list);
                    createRecyclerView();
                    setListenerForSearchView();   // 给搜索框添加监听
                    Message msg = new Message();
                    msg.what = DATA_READY;
                    handler.sendMessage(msg);
                }
                else {
                    Message msg = new Message();
                    msg.what = LOAD_FAILED;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    // 加载小菜单,设置搜索按钮
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
    }
}