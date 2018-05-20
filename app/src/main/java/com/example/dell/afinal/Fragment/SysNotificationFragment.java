package com.example.dell.afinal.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.afinal.R;

public class SysNotificationFragment extends Fragment {

    private View mView;

    public static SysNotificationFragment getInstance() {
        return new SysNotificationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.allpost_fragment, container, false);
        }
        return mView;
    }
}