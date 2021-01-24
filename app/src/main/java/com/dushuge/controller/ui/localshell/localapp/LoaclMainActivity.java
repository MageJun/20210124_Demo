package com.dushuge.controller.ui.localshell.localapp;

import android.app.Activity;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseActivity;
import com.dushuge.controller.ui.view.CustomScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LoaclMainActivity extends BaseActivity {

    public static Activity activity;
    @BindView(R.id.activity_main_FrameLayout)
    public CustomScrollViewPager activity_main_FrameLayout;
    @BindView(R.id.activity_main_RadioGroup)
    public RadioGroup activity_main_RadioGroup;

    @Override
    public int initContentView() {
        FULL_CCREEN = true;
        return R.layout.activity_localmain;
    }

    private List<Fragment> fragmentArrayList;

    @Override
    public void initView() {
        activity = this;
        fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(new LocalShelfFragment());
        fragmentArrayList.add(new NotesOptionFragment());
        fragmentArrayList.add(new LocalCalendarFragment());
        fragmentArrayList.add(new LocalMineFragment());
        new LocalFragmentTabUtils(this, fragmentArrayList, activity_main_FrameLayout, activity_main_RadioGroup);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initInfo(String json) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
