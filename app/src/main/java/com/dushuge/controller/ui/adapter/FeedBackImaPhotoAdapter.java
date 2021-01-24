package com.dushuge.controller.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseListAdapter;
import com.dushuge.controller.ui.activity.LookBigImageActivity;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyGlide;
import com.dushuge.controller.utils.ScreenSizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedBackImaPhotoAdapter extends BaseListAdapter {
    public List<String>list;
    public Activity activity;
    public FeedBackImaPhotoAdapter(Activity activity, List list) {
        super(activity, list);
        this.list = list;
        this.activity = activity;
    }

    @Override
    public int getViewByRes() {
        return R.layout.item_feed_back_photo_img;
    }

    @Override
    public View getOwnView(int position, Object been, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        viewHolder = new ViewHolder(convertView);
        ViewGroup.LayoutParams layoutParams = viewHolder.itemFeedBackImg.getLayoutParams();
        layoutParams.width = (ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity,60))/6;
        layoutParams.height = layoutParams.width;
        viewHolder.itemFeedBackImg.setLayoutParams(layoutParams);
        MyGlide.GlideImageRoundedCornersNoSize(3,activity, list.get(position),viewHolder.itemFeedBackImg);
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.item_feed_back_img)
        ImageView itemFeedBackImg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
