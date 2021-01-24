package com.dushuge.controller.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dushuge.controller.R;

import com.dushuge.controller.constant.Constant;
import com.dushuge.controller.model.BaseBookComic;
import com.dushuge.controller.model.PublicIntent;
import com.dushuge.controller.ui.utils.MyGlide;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.ui.view.banner.holder.Holder;

/**
 * 轮播图Holder
 */
public class ShelfBannerHolderView implements Holder<PublicIntent> {

    private ImageView item_BookShelfBannerHolderView_img;
    private ImageView item_BookShelfBannerHolderView_audio_img;
    private TextView item_BookShelfBannerHolderView_title;
    private TextView item_BookShelfBannerHolderView_des;
    private View shelffragment_banner;

    private int type;

    public ShelfBannerHolderView(int type) {
        this.type = type;
    }

    @Override
    public View createView(Context context, int size) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookshelfbannerholderview, null);
        item_BookShelfBannerHolderView_img = view.findViewById(R.id.item_BookShelfBannerHolderView_img);
        item_BookShelfBannerHolderView_audio_img = view.findViewById(R.id.item_BookShelfBannerHolderView_audio_img);
        shelffragment_banner = view.findViewById(R.id.shelffragment_banner);
        shelffragment_banner.setBackground(MyShape.setMyshape(20, ContextCompat.getColor(context, R.color.gray_f9)));
        item_BookShelfBannerHolderView_title = view.findViewById(R.id.item_BookShelfBannerHolderView_title);
        item_BookShelfBannerHolderView_des = view.findViewById(R.id.item_BookShelfBannerHolderView_des);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, PublicIntent data) {
        MyGlide.GlideImageNoSize((Activity) context, data.image, item_BookShelfBannerHolderView_img);
        if (type == Constant.AUDIO_CONSTANT) {
            item_BookShelfBannerHolderView_audio_img.setVisibility(View.VISIBLE);
        } else {
            item_BookShelfBannerHolderView_audio_img.setVisibility(View.GONE);
        }
        item_BookShelfBannerHolderView_title.setText(data.title);
        item_BookShelfBannerHolderView_des.setText(data.desc);
    }
}
