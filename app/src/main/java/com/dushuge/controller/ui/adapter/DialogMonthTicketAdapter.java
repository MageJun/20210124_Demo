package com.dushuge.controller.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseListAdapter;
import com.dushuge.controller.base.BaseRecViewHolder;
import com.dushuge.controller.model.MonthTicketListBean;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.utils.ScreenSizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogMonthTicketAdapter extends BaseListAdapter<MonthTicketListBean> {

    public DialogMonthTicketAdapter(Activity activity, List<MonthTicketListBean> list) {
        super(activity, list);
    }

    @Override
    public int getViewByRes() {
        return R.layout.item_dialog_month_ticket;
    }

    @Override
    public View getOwnView(int position, MonthTicketListBean been, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder(convertView);
        viewHolder.mineShubi.setText(been.title);
        ViewGroup.LayoutParams layoutParams = viewHolder.item_dialog_month_ticket_layout.getLayoutParams();
        layoutParams.width = (ScreenSizeUtils.getInstance(activity).getScreenWidth() / 5) - ImageUtil.dp2px(activity, 10);
        layoutParams.height = layoutParams.width;
        viewHolder.item_dialog_month_ticket_layout.setLayoutParams(layoutParams);
        if (been.enabled == 1) {
            if (been.isChose) {
                convertView.setBackground(MyShape.setMyshape(activity, ImageUtil.dp2px(activity, 2), ImageUtil.dp2px(activity, 2), ImageUtil.dp2px(activity, 2), ImageUtil.dp2px(activity, 2), 1, ContextCompat.getColor(activity, R.color.maincolor), 0));
                viewHolder.mineShubi.setTextColor(ContextCompat.getColor(activity, R.color.maincolor));
                viewHolder.item_month_ticket_tv.setTextColor(ContextCompat.getColor(activity, R.color.maincolor));
            } else {
                convertView.setBackground(MyShape.setMyshape(activity, ImageUtil.dp2px(activity, 2), ImageUtil.dp2px(activity, 2), ImageUtil.dp2px(activity, 2), ImageUtil.dp2px(activity, 2), 1, ContextCompat.getColor(activity, R.color.grayline), 0));
                viewHolder.mineShubi.setTextColor((been.enabled == 1 ? ContextCompat.getColor(activity, R.color.black_3) : ContextCompat.getColor(activity, R.color.gray_9)));
                viewHolder.item_month_ticket_tv.setTextColor((been.enabled == 1 ? ContextCompat.getColor(activity, R.color.black_3) : ContextCompat.getColor(activity, R.color.gray_9)));
            }
        } else {
            convertView.setBackground(MyShape.setMyshape(activity, ImageUtil.dp2px(activity, 2), ImageUtil.dp2px(activity, 2), ImageUtil.dp2px(activity, 2), ImageUtil.dp2px(activity, 2), 1, ContextCompat.getColor(activity, R.color.grayline), 0));
            viewHolder.mineShubi.setTextColor((been.enabled == 1 ? ContextCompat.getColor(activity, R.color.black_3) : ContextCompat.getColor(activity, R.color.gray_9)));
            viewHolder.item_month_ticket_tv.setTextColor((been.enabled == 1 ? ContextCompat.getColor(activity, R.color.black_3) : ContextCompat.getColor(activity, R.color.gray_9)));
        }

        return convertView;
    }

    public class ViewHolder {

        @BindView(R.id.mine_shubi)
        TextView mineShubi;
        @BindView(R.id.item_month_ticket_tv)
        TextView item_month_ticket_tv;
        @BindView(R.id.item_dialog_month_ticket_layout)
        LinearLayout item_dialog_month_ticket_layout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
