package com.dushuge.controller.ui.push;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import static com.dushuge.controller.model.PublicIntent.intentTo;

/**
 * 用于保存通知栏的信息
 */
public class PushBeanManager {

    private static PushBeanManager pushBeanManager = null;

    public static PushBeanManager getInstance() {
        if (pushBeanManager == null) {
            synchronized(PushBeanManager.class) {
                if (pushBeanManager == null) {
                    pushBeanManager = new PushBeanManager();
                }
            }
        }
        return pushBeanManager;
    }

    private PushManager pushManager;

    public PushManager getPushManager() {
        return pushManager;
    }

    public void setPushManager(String title, String content, String type) {
        if (pushManager == null) {
            pushManager = new PushManager();
        }
        pushManager.setLabel(title);
        pushManager.setContent(content);
        pushManager.setType(type);
    }

    /**
     * 跳转界面
     *
     * @param activity
     */
    public void jumpActivity(Activity activity) {
        if (pushManager == null || TextUtils.isEmpty(pushManager.getContent())
                || TextUtils.isEmpty(pushManager.getType())) {
            return;
        }
        Intent intent = intentTo(activity, Integer.parseInt(pushManager.getType()),
                pushManager.getContent(), pushManager.getContent());
        if (intent != null) {
            activity.startActivity(intent);
        }
        clear();
    }

    public void clear() {
        pushManager = null;
        pushBeanManager = null;
    }
}
