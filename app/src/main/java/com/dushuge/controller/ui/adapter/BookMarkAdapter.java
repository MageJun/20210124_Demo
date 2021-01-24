package com.dushuge.controller.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseRecAdapter;
import com.dushuge.controller.base.BaseRecViewHolder;
import com.dushuge.controller.model.BookMarkBean;

import java.util.List;

import butterknife.BindView;

import static com.dushuge.controller.utils.DateUtils.*;

public class BookMarkAdapter extends BaseRecAdapter<BookMarkBean, BookMarkAdapter.ViewHolder> {

    private OnBeanClickListener onBeanClickListener;

    public void setOnBeanClickListener(OnBeanClickListener onBeanClickListener) {
        this.onBeanClickListener = onBeanClickListener;
    }

    public BookMarkAdapter(List<BookMarkBean> list, Activity activity) {
        super(list, activity);
    }

    @Override
    public void onHolder(ViewHolder holder, BookMarkBean bean, int position) {
        if (bean != null) {
            if (bean.getTitle() != null && !TextUtils.isEmpty(bean.getTitle())) {
                holder.itemBookMarkTitle.setText(bean.getTitle());
            }
            if (bean.getContent() != null && !TextUtils.isEmpty(bean.getContent())) {
                holder.itemBookMarkContent.setText(bean.getContent());
            }
            if (bean.getAddTime() != 0) {
                if (isToday(bean.getAddTime())) {
                    holder.itemBookMarkTime.setText(getRange(activity, bean.getAddTime()));
                } else {
                    holder.itemBookMarkTime.setText(getDateString(bean.getAddTime(), "yyyy-MM-dd hh:mm"));
                }
            }
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBeanClickListener != null) {
                        onBeanClickListener.onClick(bean);
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateHolder() {
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_book_mark, null));
    }

    public class ViewHolder extends BaseRecViewHolder {

        @BindView(R.id.item_book_mark_layout)
        LinearLayout linearLayout;
        @BindView(R.id.item_book_mark_title)
        TextView itemBookMarkTitle;
        @BindView(R.id.item_book_mark_time)
        TextView itemBookMarkTime;
        @BindView(R.id.item_book_mark_content)
        TextView itemBookMarkContent;

        public ViewHolder(View view) {
            super(view);
        }
    }

    public interface OnBeanClickListener {

        void onClick(BookMarkBean bookMarkBean);
    }
}
