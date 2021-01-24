package com.dushuge.controller.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseDialogFragment;
import com.dushuge.controller.constant.Api;
import com.dushuge.controller.constant.Constant;
import com.dushuge.controller.eventbus.RefreshMine;
import com.dushuge.controller.model.BannerBottomItem;
import com.dushuge.controller.model.ShareBean;
import com.dushuge.controller.net.HttpUtils;
import com.dushuge.controller.net.ReaderParams;
import com.dushuge.controller.ui.adapter.ShareOptionAdapter;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.utils.LanguageUtil;
import com.dushuge.controller.utils.MyShare;
import com.dushuge.controller.utils.ScreenSizeUtils;
import com.dushuge.controller.utils.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dushuge.controller.constant.Constant.USE_QQ;
import static com.dushuge.controller.constant.Constant.USE_WEIXIN;

/**
 * 用于展示分享
 */
public class ShareDialogFragment extends BaseDialogFragment {

    @BindView(R.id.dialog_share_layout)
    LinearLayout linearLayout;
    @BindView(R.id.dialog_share_recyclerView)
    RecyclerView recyclerView;

    private int mWidth;
    private List<BannerBottomItem> bannerBottomItems;
    private ShareOptionAdapter shareOptionAdapter;

    private ShareBean shareBean;

    public ShareDialogFragment() {

    }

    public ShareDialogFragment(Activity activity, ShareBean shareBean) {
        super(true);
        this.activity = activity;
        this.shareBean = shareBean;
        mWidth = ScreenSizeUtils.getInstance(activity).getScreenWidth();
    }

    @Override
    public int initContentView() {
        return R.layout.dialog_share;
    }

    @Override
    public void initView() {
        bannerBottomItems = new ArrayList<>();
        linearLayout.setBackground(MyShape.setMyshape(ImageUtil.dp2px(activity, 8),
                ImageUtil.dp2px(activity, 8), 0, 0,
                ContextCompat.getColor(activity, R.color.white)));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        shareOptionAdapter = new ShareOptionAdapter(bannerBottomItems, activity);
        recyclerView.setAdapter(shareOptionAdapter);
        shareOptionAdapter.setOnShareOptionListener(new ShareOptionAdapter.OnShareOptionListener() {
            @Override
            public void onClick(String action) {
                if (shareBean == null) {
                    return;
                }
                MyShare.IS_SHARE = true;
                UMWeb web = new UMWeb(shareBean.link);
                web.setTitle(shareBean.title);//标题
                web.setThumb(new UMImage(activity, shareBean.imgUrl));  //缩略图
                web.setDescription(shareBean.desc);//描述
                ShareAction shareAction = new ShareAction(activity).setCallback(umShareListener);
                switch (action) {
                    case "wecate_friend":
                        shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
                        break;
                    case "wecate":
                        shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
                        break;
                    case "qq":
                        shareAction.setPlatform(SHARE_MEDIA.QQ);
                        break;
                    case "qq_friend":
                        shareAction.setPlatform(SHARE_MEDIA.QZONE);
                        break;
                }
                shareAction.withMedia(web).share();
            }
        });
    }

    @OnClick({R.id.dialog_share_close})
    public void onClickOption(View view) {
        dismissAllowingStateLoss();
    }

    @Override
    public void initData() {
        if (!bannerBottomItems.isEmpty()) {
            bannerBottomItems.clear();
        }
        // 添加数据
        if (SystemUtil.checkAppInstalled(activity, SystemUtil.WEChTE_PACKAGE_NAME) && USE_WEIXIN &&
                ((SystemUtil.checkAppInstalled(activity, SystemUtil.QQ_PACKAGE_NAME) && USE_QQ) ||
                        (SystemUtil.checkAppInstalled(activity, SystemUtil.TIM_PACKAGE_NAME) && USE_QQ))) {
            addWeCate();
            addQQ();
        } else {
            if ((SystemUtil.checkAppInstalled(activity, SystemUtil.QQ_PACKAGE_NAME) && USE_QQ) ||
                    (SystemUtil.checkAppInstalled(activity, SystemUtil.TIM_PACKAGE_NAME) && USE_QQ)) {
                addQQ();
            } else if (SystemUtil.checkAppInstalled(activity, SystemUtil.WEChTE_PACKAGE_NAME) && USE_WEIXIN) {
                addWeCate();
            }
        }
        if (!bannerBottomItems.isEmpty()) {
            int width;
            // 设置宽
            if (bannerBottomItems.size() > 4) {
                recyclerView.setOverScrollMode(0);
                width = mWidth * 10 / 47;
            } else {
                recyclerView.setOverScrollMode(2);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                params.leftMargin = params.rightMargin = ImageUtil.dp2px(activity, 10);
                recyclerView.setLayoutParams(params);
                width = (mWidth - ImageUtil.dp2px(activity, 20)) / bannerBottomItems.size();
            }
            shareOptionAdapter.setWidth(width);
        }
        shareOptionAdapter.notifyDataSetChanged();
    }

    @Override
    public void initInfo(String json) {

    }

    /**
     * 添加微信
     */
    private void addWeCate() {
        bannerBottomItems.add(new BannerBottomItem(LanguageUtil.getString(activity, R.string.share_wecate),
                R.mipmap.share_wecate, "wecate"));
        bannerBottomItems.add(new BannerBottomItem(LanguageUtil.getString(activity, R.string.share_wecate_friend),
                R.mipmap.share_wecate_friend, "wecate_friend"));
    }

    /**
     * 添加QQ
     */
    private void addQQ() {
        bannerBottomItems.add(new BannerBottomItem(LanguageUtil.getString(activity, R.string.share_qq),
                R.mipmap.share_qq, "qq"));
        bannerBottomItems.add(new BannerBottomItem(LanguageUtil.getString(activity, R.string.share_qq_friend),
                R.mipmap.share_qq_friend, "qq_friend"));
    }

    private UMShareListener umShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
//            if (Constant.USE_PAY) {
                ShareCompleteToSendHttp();
//            } else {
//                dismissAllowingStateLoss();
//                MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.share_success));
//            }
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            dismissAllowingStateLoss();
            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.share_fail));
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            dismissAllowingStateLoss();
            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.share_cancel));
        }
    };

    /**
     * 分享完成请求接口
     */
    private void ShareCompleteToSendHttp() {
        final ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance().sendRequestRequestParams(activity,Api.ShareAddGold, json, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        dismissAllowingStateLoss();
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String tip = jsonObject.getString("tip");
                            EventBus.getDefault().post(new RefreshMine());
                            MyToash.ToashSuccess(activity, tip);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }
}
