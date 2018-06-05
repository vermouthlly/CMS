package com.example.dell.afinal.Fragment;

import android.support.v4.app.Fragment;

import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

public abstract class BackHandledFragment extends Fragment implements FragmentBackHandler {
    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }
}