package com.dushuge.controller.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseListAdapter;
import com.dushuge.controller.model.AcquirePrivilegeItem;
import com.dushuge.controller.ui.utils.MyGlide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AcquireBaoyuePrivilegeAdapter extends BaseListAdapter<AcquirePrivilegeItem> {

    public AcquireBaoyuePrivilegeAdapter(Activity activity, List list) {
        super(activity, list);
    }

    @Override
    public int getViewByRes() {
        return R.layout.item_acquire_privilege;
    }

    @Override
    public View getOwnView(int position, AcquirePrivilegeItem been, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder(convertView);
        viewHolder.mItemAcquirePrivilegeTitle.setText(been.getLabel());
        //MyGlide.GlideImageNoSize(activity,been.getIcon(),viewHolder.mItemAcquirePrivilegeImg);
        viewHolder.mItemAcquirePrivilegeImg.setImageResource(been.getLocalIcon());
        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.item_acquire_privilege_img)
        ImageView mItemAcquirePrivilegeImg;
        @BindView(R.id.item_acquire_privilege_title)
        TextView mItemAcquirePrivilegeTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
