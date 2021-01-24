package com.dushuge.controller.ui.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseListAdapter;
import com.dushuge.controller.model.Comic;
import com.dushuge.controller.model.ComicChapter;
import com.dushuge.controller.model.ComicInfo;
import com.dushuge.controller.model.Downoption;
import com.dushuge.controller.ui.activity.ComicDownActivity;
import com.dushuge.controller.ui.activity.ComicInfoActivity;
import com.dushuge.controller.ui.activity.ComicLookActivity;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyGlide;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.ui.view.screcyclerview.SCOnItemClickListener;
import com.dushuge.controller.utils.FileManager;
import com.dushuge.controller.utils.LanguageUtil;
import com.dushuge.controller.utils.ObjectBoxUtils;
import com.dushuge.controller.utils.ScreenSizeUtils;
import com.dushuge.controller.utils.ShareUitls;

;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownMangerComicAdapter extends BaseListAdapter<Comic> {

    public List<Comic> checkList;
    public boolean Edit;
    private int WIDTH;

    public DownMangerComicAdapter(Activity activity, List<Comic> list, SCOnItemClickListener scOnItemClickListener) {
        super(activity, list,scOnItemClickListener);
        checkList = new ArrayList<>();
        this.activity = activity;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
    }

    @Override
    public int getViewByRes() {
        return R.layout.item_downmangercomic;
    }

    @Override
    public View getOwnView(int position, Comic baseComic, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        viewHolder = new ViewHolder(convertView);
        viewHolder.mItemDowmmangerOpen.setBackground(MyShape.setMyCustomizeShape(activity, 22, R.color.maincolor));
       /* if (position == mList.size() - 1) {
            viewHolder.item_down_comic_view.setVisibility(View.GONE);
        }*/

        viewHolder.mItemDowmmangerCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Edit) {
                    Intent intent = new Intent(activity, ComicInfoActivity.class);
                    intent.putExtra("comic_id", baseComic.comic_id);
                    activity.startActivity(intent);
                } else {
                    onClickEdit(position, baseComic, viewHolder.itemBianjiCheckBox);
                }
            }
        });
        viewHolder.mItemDowmmangerOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Edit) {
                    //MyToash.Log("baseComic",baseComic.current_chapter_id+"");
                    Intent intent = new Intent(activity, ComicLookActivity.class);
                    intent.putExtra("baseComic", baseComic);
                    activity.startActivity(intent);
                } else {
                    onClickEdit(position, baseComic, viewHolder.itemBianjiCheckBox);
                }
            }
        });
        viewHolder.mItemDowmmangerCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Edit) {
                    Intent intent = new Intent(activity, ComicInfoActivity.class);
                    intent.putExtra("comic_id", baseComic.comic_id);
                    activity.startActivity(intent);
                } else {
                    onClickEdit(position, baseComic, viewHolder.itemBianjiCheckBox);
                }
            }
        });

        viewHolder.mItemDowmmangerLinearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Edit) {
                    onClickEdit(position, baseComic, viewHolder.itemBianjiCheckBox);
                }else {
                    Intent intent = new Intent(activity, ComicDownActivity.class);
                    intent.putExtra("baseComic", baseComic);
                    intent.putExtra("flag", true);  // 只查看已下载
                    activity.startActivity(intent);
                }
            }
        });
        MyGlide.GlideImage(activity, baseComic.vertical_cover, viewHolder.mItemDowmmangerCover, ImageUtil.dp2px(activity, 113), ImageUtil.dp2px(activity, 150));
        viewHolder.mItemDowmmangerName.setText(baseComic.getName());
        viewHolder.mItemDowmmangerXiazaiprocess.setText(String.format(LanguageUtil.getString(activity, R.string.ComicDownActivity_downprocess),
                baseComic.getDown_chapters() ,baseComic.getTotal_chapters()));
        if (Edit) {
            viewHolder.mItemDowmmangerOpen.setVisibility(View.GONE);
            viewHolder.itemBianjiCheckBox.setVisibility(View.VISIBLE);
            if (checkList.contains(baseComic)) {
                viewHolder.itemBianjiCheckBox.setChecked(true);
            } else {
                viewHolder.itemBianjiCheckBox.setChecked(false);
            }
        } else {
            viewHolder.mItemDowmmangerOpen.setVisibility(View.VISIBLE);
            viewHolder.itemBianjiCheckBox.setVisibility(View.GONE);

        }

        return convertView;
    }

    private void onClickEdit(int position, Comic downoption, CheckBox itemBianjiCheckBox) {
        if (checkList.contains(downoption)) {
            checkList.remove(downoption);
            itemBianjiCheckBox.setChecked(false);
        } else {
            checkList.add(downoption);
            itemBianjiCheckBox.setChecked(true);
        }
        if (scOnItemClickListener != null) {
            scOnItemClickListener.OnItemClickListener(checkList.size(), position, downoption);
        }
    }

    class ViewHolder {

        @BindView(R.id.item_dowmmanger_cover)
        ImageView mItemDowmmangerCover;
        @BindView(R.id.item_dowmmanger_name)
        TextView mItemDowmmangerName;
        @BindView(R.id.item_dowmmanger_xiazaiprocess)
        TextView mItemDowmmangerXiazaiprocess;
        @BindView(R.id.item_dowmmanger_open)
        TextView mItemDowmmangerOpen;

        @BindView(R.id.item_dowmmanger_LinearLayout1)
        LinearLayout mItemDowmmangerLinearLayout1;

        @BindView(R.id.item_bianji_checkBox)
        public CheckBox itemBianjiCheckBox;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
