package com.dushuge.controller.utils;

import android.app.Activity;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.dushuge.controller.R;
import com.dushuge.controller.constant.Api;
import com.dushuge.controller.model.ShareBean;
import com.dushuge.controller.net.HttpUtils;
import com.dushuge.controller.net.ReaderParams;
import com.dushuge.controller.ui.dialog.ShareDialogFragment;
import com.dushuge.controller.ui.utils.MyToash;

import static com.dushuge.controller.constant.Constant.*;

public class MyShare {

    public static boolean IS_SHARE;
    public int flag;
    public long id;
    public Activity activity;

    public MyShare(Activity activity) {
        this.activity = activity;
    }

    public int getFlag() {
        return flag;
    }

    public MyShare setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public long getId() {
        return id;
    }

    public MyShare setId(long id) {
        this.id = id;
        return this;
    }

    public void Share() {
        chapterShare(activity, getId(), 0, getFlag() + 1);
    }

    /**
     * 分享(要在界面加onActivityResult)
     */
    public void ShareAPP() {
        final ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance().sendRequestRequestParams(activity,Api.APP_SHARE, json, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            ShareBean shareBean = HttpUtils.getGson().fromJson(response, ShareBean.class);
                            if (!TextUtils.isEmpty(shareBean.link) && (shareBean.link.startsWith("www") ||
                                    shareBean.link.startsWith("http"))) {
                                if (MyShare.isEnableShare(activity)) {
                                    ShareDialogFragment shareDialogFragment = new ShareDialogFragment(activity, shareBean);
                                    shareDialogFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "ShareDialogFragment");
                                }
                            } else {
                                MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.share_noUrl));
                            }
                        } catch (Exception e) {
                            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.share_noUrl));
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }

    /**
     * 章节分享接口
     *
     * @param activity
     * @param novel_id
     * @param chapter_id
     * @param type
     */
    public static void chapterShare(Activity activity, long novel_id, long chapter_id, int type) {
        ReaderParams readerParams = new ReaderParams(activity);
        readerParams.putExtraParams("type", type);
        readerParams.putExtraParams("novel_id", novel_id);
        if (chapter_id != 0) {
            readerParams.putExtraParams("chapter_id", chapter_id);
        }
        HttpUtils.getInstance().sendRequestRequestParams(activity,Api.NOVEL_SHARE, readerParams.generateParamsJson(), new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    ShareBean shareBean = HttpUtils.getGson().fromJson(response, ShareBean.class);
                    if (!TextUtils.isEmpty(shareBean.link) && (shareBean.link.startsWith("www") ||
                            shareBean.link.startsWith("http"))) {
                        if (MyShare.isEnableShare(activity)) {
                            ShareDialogFragment shareDialogFragment = new ShareDialogFragment(activity, shareBean);
                            shareDialogFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "ShareDialogFragment");
                        }
                    } else {
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.share_noUrl));
                    }
                } catch (Exception e) {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.share_noUrl));
                }
            }

            @Override
            public void onErrorResponse(String ex) {

            }
        });
    }


    /**
     * @param activity
     * @return 是否可以分享
     */
    public static boolean isEnableShare(Activity activity) {
        if (!(SystemUtil.checkAppInstalled(activity, SystemUtil.WEChTE_PACKAGE_NAME) && USE_WEIXIN) &&
                !((SystemUtil.checkAppInstalled(activity, SystemUtil.QQ_PACKAGE_NAME) && USE_QQ) ||
                        (SystemUtil.checkAppInstalled(activity, SystemUtil.TIM_PACKAGE_NAME) && USE_QQ))) {
            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.share_fail_no_app));
            return false;
        }
        return true;
    }
}
