package com.dushuge.controller.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.dushuge.controller.R;
import com.dushuge.controller.ui.push.PushBeanManager;

import java.util.Map;

public class MyAndroidPopupActivity extends AndroidPopupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onSysNoticeOpened(String title, String summary, Map<String, String> extraMap) {
        if (extraMap != null && !TextUtils.isEmpty(extraMap.get("skip_type"))
                && !TextUtils.isEmpty(extraMap.get("content"))) {
            PushBeanManager.getInstance().setPushManager(title, extraMap.get("content"),
                    extraMap.get("skip_type"));
        }
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}
