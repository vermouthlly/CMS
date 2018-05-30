package com.example.dell.afinal.Fragment;
import com.example.dell.afinal.bean.MessageEvent;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;

import com.example.dell.afinal.Activity.CourseCreate;
import com.example.dell.afinal.Activity.StudentCourseActivity;
import com.example.dell.afinal.Adapter.CourseListAdapter;
import com.example.dell.afinal.R;
import com.example.dell.afinal.Utils.ToastUtil;
import com.example.dell.afinal.bean.Course;
import com.example.dell.afinal.bean.MessageEvent;
import com.example.dell.afinal.bean.User;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CourseFragment extends Fragment {
    private View mView;                // 缓存Fragment的View, 避免碎片切换时在onCreateView内重复加载布局
    private ContentLoadingProgressBar progressBar;   // 进度条
    private Toolbar toolbar;                         // 标题栏
    private MaterialSearchView searchView;           // 搜索框
    private XRecyclerView recyclerView;               // 课程列表
    private Button create;                           // 教师增加课程

    private CourseListAdapter adapter;               // 课程适配器
    private List<Course> courseList = new ArrayList<>();
    public static final int REFRESH = 1;
    public static final int LOAD_FAILED = 2;
    public static final int LOAD_MORE = 3;
    private int loadFactor = 5;       // 加载课程的数目

    // 通过Handler更新UI
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:
                    progressBar.hide();
                    recyclerView.refreshComplete();
                    break;
                case LOAD_FAILED:
                    ToastUtil.toast(getContext(), "获取课程列表失败, 请检查你的网络后刷新重试");
                    break;
                case LOAD_MORE:
                    recyclerView.loadMoreComplete();
                    recyclerView.scrollToPosition(courseList.size());
                    break;
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
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("addCourse") || event.getMessage().equals("updateCourse"))
            generateCourseList("refresh");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 避免Fragment切换时重复加载布局
        if (mView == null) {
            mView = inflater.inflate(R.layout.course_fragment, container, false);
            bindViews(mView);
            setOnPullLoadingListener();
            showCreateInterfaceOrNot();
            generateCourseList("refresh");
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

    // 教师创建课程
    private void onCreateButtonClickedListener() {
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCourse();
            }
        });
    }

    // 创建课程, 暂定在可交互对话框中完成
    private void createCourse() {
        startActivity(new Intent(getContext(), CourseCreate.class));
//        final User user = BmobUser.getCurrentUser(User.class);
//        if (user.getIdentity().equals("teacher")) {
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            View layout = inflater.inflate(R.layout.dialoglayout, null);
//            final EditText ed_name= layout.findViewById(R.id.ed_name);
//            final EditText ed_time=  layout.findViewById(R.id.ed_time);
//            final EditText ed_location = layout.findViewById(R.id.ed_location);
//            final EditText ed_teacher =  layout.findViewById(R.id.ed_teacher);
//            final EditText ed_class_capacity = layout.findViewById(R.id.ed_class_capacity);
//            final EditText ed_code=  layout.findViewById(R.id.ed_code);
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("创建课程")
//                    .setView(layout)
//                    .setNegativeButton("取消创建", null)
//                    .setPositiveButton("确认创建", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            String name = ed_name.getText().toString();
//                            String time = ed_time.getText().toString();
//                            String location = ed_location.getText().toString();
//                            String teacher = ed_teacher.getText().toString();
//                            String class_capacity = ed_class_capacity.getText().toString();
//
//                            Pattern pnumber = Pattern.compile("[0-9]*");
//                            Matcher result = pnumber.matcher(class_capacity);
//                            int capacity = 0;
//                            int no_empty=0;
//                            if(!name.isEmpty() && !time.isEmpty() && !location.isEmpty() && !teacher.isEmpty()
//                                    && !class_capacity.isEmpty()){
//                                no_empty= 1;
//                            }
//                            if (class_capacity.isEmpty()){
//                                capacity = 0;
//                            } else if(result.matches()){
//                                capacity = Integer.parseInt(class_capacity);
//                            }
//                            String code = ed_code.getText().toString();
//                            final Course course = new Course();
//                            course.setCourseName(name);
//                            course.setCourseTime(time);
//                            course.setCoursePlace(location);
//                            course.setCourseDescription(teacher);
//                            course.setCourseCapacity(capacity);
//                            course.setInvitationCode(code);
//                            course.setStatus("released");
//                            if(no_empty>0){
//                                course.save(new SaveListener<String>() {
//                                    @Override
//                                    public void done(String s, BmobException e) {
//                                        if (e == null) {
//                                            course.setManager(user);
//                                            course.update(new UpdateListener() {
//                                                @Override
//                                                public void done(BmobException e) {
//                                                    if (e == null) {
//                                                        joinCourse(course);
//                                                        ToastUtil.toast(getActivity(), "创建成功");
//                                                    } else {
//                                                        ToastUtil.toast(getActivity(), "创建失败," +
//                                                                "请检查您的网络");
//                                                    }
//                                                }
//                                            });
//                                        } else {
//                                            ToastUtil.toast(getActivity(), "创建失败,请检查您的网络");
//                                        }
//                                    }
//                                });
//                            } else {
//                                ToastUtil.toast(getActivity(), "创建失败,输入不能为空。");
//                            }
//
//                        }
//                    }).show();
//        }
    }

    // 教师创建课程同时也是该课程的成员, 可以参与该课程讨论区的讨论
    private void joinCourse(final Course course) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        relation.add(course);
        user.setCourses(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    notifyUpdateCourseList();
                    addTeacherAsMember(course);
                    Log.d("CourseFragment", "教师加入课程成功");
                } else {
                    Log.e("CourseFragment", "教师加入课程失败" + e.toString());
                }
            }
        });
    }

    // 课程记录教师作为成员
    private void addTeacherAsMember(Course course) {
        User user = BmobUser.getCurrentUser(User.class);
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        Course c = new Course();
        c.setObjectId(course.getObjectId());
        c.setSelectors(relation);
        c.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("CourseFragment", "记录成功");
                } else {
                    Log.e("CourseFragment记录失败", e.toString());
                }
            }
        });
    }

    // 提醒相关界面更新课程列表
    private void notifyUpdateCourseList() {
        EventBus.getDefault().post(new MessageEvent("addCourse"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity thisActivity = (AppCompatActivity) getActivity();
        thisActivity.setSupportActionBar(toolbar);
    }

    // 设置RecyclerView下拉刷新以及上拉加载监听
    private void setOnPullLoadingListener() {
        recyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
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
                generateCourseList(op);
            }
        }, 2000);
    }

    // 初始化课程列表：获取Course表中的所有数据记录
    private void generateCourseList(final String op) {
        BmobQuery<Course> query = new BmobQuery<>();
        query.order("-createdAt");
        query.addWhereEqualTo("status", "released");
        query.setLimit(loadFactor);
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> list, BmobException e) {
                if (e == null) {
                    onLoadSuccess(list, op);
                } else {
                    onLoadFailed(e);
                }
            }
        });
    }

    // 读取成功
    private void onLoadSuccess(List<Course> list, String op) {
        courseList = list;
        createRecyclerView();
        setListenerForSearchView();
        Message message = new Message();
        if (op.equals("refresh")) {
            message.what = REFRESH;
        } else if (op.equals("loadMore")) {
            message.what = LOAD_MORE;
        }
        handler.sendMessage(message);
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
        Message msg = new Message();
        msg.what = LOAD_FAILED;
        handler.sendMessage(msg);
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