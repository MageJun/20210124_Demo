package com.dushuge.controller.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dushuge.controller.R;
import com.dushuge.controller.constant.Api;
import com.dushuge.controller.model.FeedBackPhotoBean;
import com.dushuge.controller.net.HttpUtils;
import com.dushuge.controller.net.ReaderParams;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.utils.LanguageUtil;
import com.dushuge.controller.utils.ScreenSizeUtils;

import java.util.List;

public class AwayFeedBackDialog {

    private static String imageid = "";

    /*public static void askIsNeedToAddShelf(Activity activity, List<FeedBackPhotoBean> list) {
        final Dialog dialog = new Dialog(activity, R.style.NormalDialogStyle);
        View view = View.inflate(activity, R.layout.dialog_add_shelf, null);
        view.setBackground(MyShape.setMyshape(ImageUtil.dp2px(activity, 5), Color.WHITE));
        TextView cancel = view.findViewById(R.id.cancel);
        TextView confirm = view.findViewById(R.id.confirm);
        TextView title = view.findViewById(R.id.title);
        View dialog_add_shelf_finish = view.findViewById(R.id.dialog_add_shelf_finish);
        title.setText(LanguageUtil.getString(activity, R.string.activityAwayFeedBack));
        confirm.setText(LanguageUtil.getString(activity, R.string.GivpXiugai));
        cancel.setText(LanguageUtil.getString(activity, R.string.app_cancle));
        cancel.setBackground(MyShape.setMyshape(ImageUtil.dp2px(activity, 10), ContextCompat.getColor(activity, R.color.gray2)));
        confirm.setBackground(MyShape.setMyshape(ImageUtil.dp2px(activity, 10), ContextCompat.getColor(activity, R.color.maincolor)));
        view.setBackground(MyShape.setMyshape(ImageUtil.dp2px(activity, 5), Color.WHITE));

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        //设置对话框的大小a
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(activity).getScreenWidth() * 0.82f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (FeedBackPhotoBean feedBackPhotoBean : list) {
                    imageid += "||" + feedBackPhotoBean.img;
                }
                ReaderParams readerParams = new ReaderParams(activity);
                readerParams.putExtraParams("image", imageid.substring(2));
                HttpUtils.getInstance().sendRequestRequestParams(activity,Api.DeleteImage, readerParams.generateParamsJson(), new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        activity.finish();
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                });
            }
        });
        dialog_add_shelf_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }*/
}
