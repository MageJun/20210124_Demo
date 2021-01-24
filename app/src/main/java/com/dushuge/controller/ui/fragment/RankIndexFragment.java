package com.dushuge.controller.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseFragment;
import com.dushuge.controller.constant.Api;
import com.dushuge.controller.constant.Constant;
import com.dushuge.controller.model.RankItem;
import com.dushuge.controller.net.HttpUtils;
import com.dushuge.controller.net.ReaderParams;
import com.dushuge.controller.ui.activity.BaseOptionActivity;
import com.dushuge.controller.ui.adapter.RankAdapter;
import com.dushuge.controller.utils.LanguageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.dushuge.controller.constant.Constant.BOOK_CONSTANT;
import static com.dushuge.controller.constant.Constant.COMIC_CONSTANT;
import static com.dushuge.controller.constant.Constant.AUDIO_CONSTANT;

public class RankIndexFragment extends BaseFragment {

    @BindView(R.id.fragment_rankindex_listview)
    ListView mFragmentRankindexListview;
    @BindView(R.id.fragment_option_noresult)
    LinearLayout mFragmentOptionNoresult;

    private int productType;
    private int SEX;
    private String httpUrl;

    private RankAdapter rankAdapter;
    private List<RankItem> mRankItemList = new ArrayList<>();
    private LinearLayout temphead;
    private LayoutInflater layoutInflater;

    @SuppressLint("ValidFragment")
    public RankIndexFragment(int productType, int SEX) {
        this.productType = productType;
        this.SEX = SEX;
    }

    @Override
    public int initContentView() {
        return R.layout.fragment_rank_index;
    }

    @Override
    public void initView() {
        layoutInflater = LayoutInflater.from(activity);
        temphead = (LinearLayout) layoutInflater.inflate(R.layout.item_list_head, null);
        if (productType == BOOK_CONSTANT) {
            httpUrl = Api.mRankUrl;
        } else if (productType == COMIC_CONSTANT) {
            httpUrl = Api.COMIC_rank_index;
        } else if (productType == AUDIO_CONSTANT) {
            httpUrl = Api.AUDIO_TOP_INDEX;
        }
        mFragmentRankindexListview.addHeaderView(temphead);
        mFragmentRankindexListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, BaseOptionActivity.class);
                if (position - 1 == -1) {
                    return;
                }
                intent.putExtra("rank_type", mRankItemList.get(position - 1).getRank_type());
                intent.putExtra("SEX", SEX);
                intent.putExtra("OPTION", Constant.PAIHANG);
                intent.putExtra("productType", productType);
                intent.putExtra("title", mRankItemList.get(position - 1).getTitle());
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        if (httpUrl == null) {
            return;
        }
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("channel_id", SEX + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance().sendRequestRequestParams(activity,httpUrl, json,responseListener);
    }

    @Override
    public void initInfo(String json) {
        try {
            if (!TextUtils.isEmpty(json)) {
                mRankItemList = gson.fromJson(json, new TypeToken<List<RankItem>>() {
                }.getType());
                if (mRankItemList == null || mRankItemList.isEmpty()) {
                    mFragmentOptionNoresult.setVisibility(View.VISIBLE);
                } else {
                    mFragmentOptionNoresult.setVisibility(View.GONE);
                    rankAdapter = new RankAdapter(activity, mRankItemList, productType);
                    rankAdapter.NoLinePosition=mRankItemList.size()-1;
                    mFragmentRankindexListview.setAdapter(rankAdapter);
                }
            } else {
                mFragmentOptionNoresult.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            mFragmentOptionNoresult.setVisibility(View.VISIBLE);
        }
    }
}
