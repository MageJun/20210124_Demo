package com.dushuge.controller.model;

import android.app.Activity;
import android.text.TextUtils;

import com.dushuge.controller.constant.Api;
import com.dushuge.controller.constant.Constant;
import com.dushuge.controller.eventbus.RefreshMine;
import com.dushuge.controller.eventbus.RefreshReadHistory;
import com.dushuge.controller.net.HttpUtils;
import com.dushuge.controller.net.ReaderParams;
import com.dushuge.controller.ui.utils.MainFragmentTabUtils;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.utils.InternetUtils;
import com.dushuge.controller.utils.ShareUitls;
import com.dushuge.controller.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.dushuge.controller.constant.Api.task_read;

public class ReadHistory extends PublicPage {

    public List<BaseReadHistory> list;

    /**
     * 添加历史
     *
     * @param activity
     * @param productType
     * @param id
     * @param chapterId
     */
    public static void addReadHistory(final Activity activity, int productType, final long id, long chapterId) {
        if (!InternetUtils.internet(activity) || !UserUtils.isLogin(activity)) {
            return;
        }
        ReaderParams params = new ReaderParams(activity);
        String url = "";
        if (productType == Constant.BOOK_CONSTANT) {
            params.putExtraParams("book_id", id);
            url = Api.add_read_log;
        } else if (productType == Constant.COMIC_CONSTANT) {
            params.putExtraParams("comic_id", id);
            url = Api.COMIC_read_log_add;
        } else if (productType == Constant.AUDIO_CONSTANT) {
            params.putExtraParams("audio_id", id);
            url = Api.AUDIO_ADD_READ_LOG;
        }
        params.putExtraParams("chapter_id", chapterId);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        HttpUtils.getInstance().sendRequestRequestParams(activity,url, params.generateParamsJson(),  new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        // 向历史界面发送通知(不在历史界面是收不到的)
                        EventBus.getDefault().post(new RefreshReadHistory(productType));
                        ReadTwoBook(activity, id);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }

    /**
     * 阅读两本书的任务
     *
     * @param activity
     * @param mBookId
     */
    public static void ReadTwoBook(Activity activity, long mBookId) {
//        if (!Constant.USE_PAY) {
//            return;
//        }
        if (ShareUitls.getLong(activity, "ReadTwoBookDate", 0) == mBookId) {
            return;
        }
        ShareUitls.putLong(activity, "ReadTwoBookDate", mBookId);
        final ReaderParams params = new ReaderParams(activity);
        HttpUtils.getInstance().sendRequestRequestParams(activity, task_read, params.generateParamsJson(), new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        MainFragmentTabUtils.UserCenterRefarsh = true;
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }
}
