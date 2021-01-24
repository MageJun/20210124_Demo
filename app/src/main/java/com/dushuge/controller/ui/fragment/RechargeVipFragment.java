package com.dushuge.controller.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dushuge.controller.R;
import com.dushuge.controller.base.BWNApplication;
import com.dushuge.controller.base.BaseFragment;
import com.dushuge.controller.constant.Api;
import com.dushuge.controller.eventbus.RefreshMine;
import com.dushuge.controller.model.AcquirePrivilegeItem;
import com.dushuge.controller.model.PayBeen;
import com.dushuge.controller.model.VipPayBeen;
import com.dushuge.controller.net.HttpUtils;
import com.dushuge.controller.net.ReaderParams;
import com.dushuge.controller.ui.adapter.RechargeChannelAdapter;
import com.dushuge.controller.ui.adapter.RechargePrivilegeAdapter;
import com.dushuge.controller.ui.adapter.RechargeVipAdapter;
import com.dushuge.controller.ui.utils.MyGlide;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.utils.LanguageUtil;
import com.dushuge.controller.utils.SystemUtil;
import com.dushuge.controller.utils.UpdateApp;
import com.dushuge.controller.utils.UserUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RechargeVipFragment extends BaseFragment {

    @BindView(R.id.activity_user_img)
    ImageView activityUserImg;
    @BindView(R.id.activity_user_name)
    TextView activityUserName;
    @BindView(R.id.acitivity_vip_img)
    ImageView acitivityVipImg;
    @BindView(R.id.activity_vip_time)
    TextView activityVipTime;
    @BindView(R.id.activity_recharge_head_layout)
    LinearLayout activityRechargeHeadLayout;
    @BindView(R.id.activity_head_bar_rcy)
    RecyclerView activityHeadBarRcy;
    @BindView(R.id.activity_pay_channel_rcy)
    RecyclerView activityPayChannelRcy;
    @BindView(R.id.framgent_tequan_rcy)
    RecyclerView framgentTequanRcy;
    @BindView(R.id.activity_recharge_instructions)
    LinearLayout activity_recharge_instructions;
    @BindView(R.id.MineNewFragment_app_install_desc)
    TextView appInstallDesc;

    public String mGoodsId;
    public PayBeen.ItemsBean.PalChannelBean palChannelBean;

    private TextView pay_recharge_tv;
    private String current_price;
    private RechargePrivilegeAdapter rechargePrivilegeAdapter;
    private VipPayBeen vipPayBeen;
    private List<PayBeen.ItemsBean> payListBeanList;

    private RechargeVipAdapter payHeadBarAdapter;
    private List<PayBeen.ItemsBean.PalChannelBean> palChannelBeanList;
    private RechargeChannelAdapter rechargeChannelAdapter;
    private List<AcquirePrivilegeItem> iconList;
    private int is_vip;

    public RechargeVipFragment(TextView pay_recharge_tv) {
        this.pay_recharge_tv = pay_recharge_tv;
    }

    @Override
    public int initContentView() {
        USE_EventBus = true;
        return R.layout.fragment_recharge_vip;
    }

    @Override
    public void initView() {
        activityRechargeHeadLayout.setBackgroundResource(R.mipmap.pay_user_vip_bg);
        iconList = new ArrayList<>();
        payListBeanList = new ArrayList<>();
        palChannelBeanList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        activityHeadBarRcy.setLayoutManager(linearLayoutManager);
        payHeadBarAdapter = new RechargeVipAdapter(payListBeanList, activity);
        activityHeadBarRcy.setAdapter(payHeadBarAdapter);
        activityPayChannelRcy.setLayoutManager(new LinearLayoutManager(activity));
        rechargeChannelAdapter = new RechargeChannelAdapter(palChannelBeanList, activity);
        activityPayChannelRcy.setAdapter(rechargeChannelAdapter);
        rechargePrivilegeAdapter = new RechargePrivilegeAdapter(iconList, activity);
        framgentTequanRcy.setLayoutManager(new GridLayoutManager(activity, 4));
        framgentTequanRcy.setAdapter(rechargePrivilegeAdapter);
        initListener();
    }

    private void initListener() {
        // 充值通道
        rechargeChannelAdapter.setOnRechargeClick(new RechargeChannelAdapter.onRechargeClick() {
            @Override
            public void onRecharge(int position) {
                for (PayBeen.ItemsBean.PalChannelBean palChannelBean : palChannelBeanList) {
                    palChannelBean.choose = false;
                }
                palChannelBean = palChannelBeanList.get(position);
                palChannelBean.choose = true;
                rechargeChannelAdapter.notifyDataSetChanged();
            }
        });
        // 充值套餐
        payHeadBarAdapter.setOnRechargeClick(new RechargeVipAdapter.onRechargeClick() {
            @Override
            public void onRecharge(int position) {
                palChannelBean = null;
                palChannelBeanList.clear();
                for (PayBeen.ItemsBean payListBean : payListBeanList) {
                    payListBean.choose = false;
                }
                pay_recharge_tv.setText(payListBeanList.get(position).getFat_price());
                payListBeanList.get(position).choose = true;
                payHeadBarAdapter.notifyDataSetChanged();
                mGoodsId = payListBeanList.get(position).goods_id + "";
                if (payListBeanList.get(position).pal_channel != null && !payListBeanList.get(position).pal_channel.isEmpty()) {
                    palChannelBeanList.addAll(payListBeanList.get(position).pal_channel);
                    for (PayBeen.ItemsBean.PalChannelBean palChannelBean : payListBeanList.get(position).pal_channel) {
                        palChannelBean.choose = false;
                    }
                    palChannelBeanList.get(0).choose = true;
                    palChannelBean = palChannelBeanList.get(0);
                    rechargeChannelAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void initData() {
        readerParams = new ReaderParams(activity);
        HttpUtils.getInstance().sendRequestRequestParams(activity,Api.mPayBaoyueCenterUrl, readerParams.generateParamsJson(),responseListener);
    }

    @Override
    public void initInfo(String json) {
        payListBeanList.clear();
        iconList.clear();
        palChannelBeanList.clear();
        activity_recharge_instructions.removeAllViews();
        vipPayBeen = gson.fromJson(json, VipPayBeen.class);
        VipPayBeen.UserBean userBean = vipPayBeen.getUser();
        if (vipPayBeen.getPrivilege() != null && !vipPayBeen.getPrivilege().isEmpty()) {
            iconList.addAll(vipPayBeen.getPrivilege());
            rechargePrivilegeAdapter.notifyDataSetChanged();
        }
        if (UserUtils.isLogin(activity)) {
            is_vip = userBean.getBaoyue_status();
            activityUserName.setText(userBean.getNickname());
            if (UserUtils.isLogin(activity)) {
                if (userBean.avatar != null && !userBean.getAvatar().isEmpty()) {
                    MyGlide.GlideImageHeadNoSize(activity, userBean.getAvatar(), activityUserImg);
                } else {
                    activityUserImg.setImageResource(R.mipmap.hold_user_avatar);
                }
            } else {
                activityUserImg.setImageResource(R.mipmap.hold_user_avatar);
            }
            if (is_vip == 0) {
                acitivityVipImg.setImageResource(R.mipmap.icon_novip);
                activityVipTime.setText(userBean.getVip_desc());
            } else {
                acitivityVipImg.setImageResource(R.mipmap.icon_isvip);
                activityVipTime.setText(userBean.getExpiry_date());
            }
        } else {
            activityUserImg.setImageResource(R.mipmap.hold_user_avatar);
            activityUserName.setText(LanguageUtil.getString(activity, R.string.MineNewFragment_nologin));
            activityVipTime.setText(userBean.getVip_desc());
        }
        payListBeanList.addAll(vipPayBeen.getList());
        if (payListBeanList != null && !payListBeanList.isEmpty()) {
            payListBeanList.get(0).choose = true;
            mGoodsId = payListBeanList.get(0).goods_id + "";
            payHeadBarAdapter.notifyDataSetChanged();
            current_price = vipPayBeen.getList().get(0).getFat_price();
            pay_recharge_tv.setText(current_price);
            if (vipPayBeen.getList().get(0).pal_channel != null && !vipPayBeen.getList().get(0).pal_channel.isEmpty()) {
                palChannelBeanList.addAll(vipPayBeen.getList().get(0).pal_channel);
                palChannelBean = palChannelBeanList.get(0);
                palChannelBean.choose = true;
                rechargeChannelAdapter.notifyDataSetChanged();
            }
            rechargePrivilegeAdapter.notifyDataSetChanged();
        }
        RechargeGoldFragment.setRechargeInfo(activity,vipPayBeen.getAbout(), activity_recharge_instructions);
    }

    /**
     * 用户支付后刷新界面
     * @param toStore
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RefreshPay(RefreshMine toStore) {
        if (toStore.isPayVip) {
            MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.PayActivity_zhifuok));
            // 刷新用户状态
            new UpdateApp(BWNApplication.applicationContext.getActivity()).getRequestData(false, null);
        }
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SystemUtil.checkAppInstalled(activity, SystemUtil.WEChTE_PACKAGE_NAME) &&
                !SystemUtil.checkAppInstalled(activity, SystemUtil.ALIPAY_PACKAGE_NAME)) {
            appInstallDesc.setVisibility(View.VISIBLE);
        } else {
            appInstallDesc.setVisibility(View.GONE);
        }
    }
}
