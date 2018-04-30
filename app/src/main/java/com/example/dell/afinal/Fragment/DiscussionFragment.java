package com.example.dell.afinal.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dell.afinal.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DiscussionFragment extends android.support.v4.app.Fragment {

    private View mView;    // Fragment布局

    private Unbinder unbinder;

    public static DiscussionFragment newInstance() {
        return new DiscussionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 避免碎片页面切换时重复加载
        if (mView == null) {
            mView = inflater.inflate(R.layout.discussion_fragment, container, false);
            unbinder = ButterKnife.bind(this, mView);
        }
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
