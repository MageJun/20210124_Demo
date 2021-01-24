package com.dushuge.controller.ui.view.banner;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dushuge.controller.model.BookComicStoare;
import com.dushuge.controller.model.PublicIntent;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyGlide;
import com.dushuge.controller.ui.view.banner.holder.Holder;
import com.dushuge.controller.utils.ScreenSizeUtils;

public class DiscoverBannerHolderViewBook implements Holder<PublicIntent> {

    private ImageView imageView;

    @Override
    public View createView(Context context, int size) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(ImageUtil.dp2px(context,10),0,ImageUtil.dp2px(context,10),0);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, PublicIntent data) {
        MyGlide.GlideImageRoundedCornersNoSize(8, (Activity) context, data.getImage(), imageView);
    }
}
