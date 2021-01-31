package com.dushuge.controller.ui.bwad;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.NativeAdData;
import com.analytics.sdk.client.NativeAdListener;
import com.analytics.sdk.client.feedlist.AdSize;
import com.analytics.sdk.client.feedlist.AdView;
import com.analytics.sdk.client.feedlist.FeedListAdListener;
import com.analytics.sdk.client.feedlist.FeedListNativeAdListener;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.dushuge.controller.model.BaseAd;
import com.dushuge.controller.ui.read.page.PageLoader;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyGlide;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.utils.ShareUitls;

import java.util.ArrayList;
import java.util.List;

import static com.dushuge.controller.ui.bwad.AdHttp.adClick;

/**
 * 聚合广告
 */
public class JHAdShow extends TTAdShow{

    private FrameLayout frameLayoutToday;
    private BaseAd.GetttAdShowBitamp getttAdShowBitamp;

    public void getTodayOneBanner(final FrameLayout frameLayoutToday, BaseAd.GetttAdShowBitamp getttAdShowBitamp,String slodId) {
        if (getttAdShowBitamp != null) {
            this.getttAdShowBitamp = getttAdShowBitamp;
        }
        getTodayOneBanner(frameLayoutToday);
    }

    public void getTodayOneBanner(final FrameLayout frameLayoutToday) {
        this.frameLayoutToday = frameLayoutToday;
//        if (mTTAdNative == null) {
//            mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);
//        }
//        if (flag != 3) {
//            loadTodayOneBannerAdXINXILIU();
//        } else {
//            loadBannerAd();
//        }
        loadJHBannerAd(frameLayoutToday,frameLayoutToday.getContext(),baseAd.ad_android_key);
    }

    /**
     * 加载插屏广告
     */
    private TTAdNative mTTAdNative;
    private Activity activity;
    private String daimaweiID;
    private int flag;
    private BaseAd baseAd;

    public JHAdShow() {
    }

    public JHAdShow(Activity activity, int flag, BaseAd baseAd) {
        this.daimaweiID = baseAd.ad_android_key;
        this.baseAd = baseAd;
        this.activity = activity;
        this.flag = flag;
    }

    private void loadTodayOneBannerAdXINXILIU() {
        //step4:创建feed广告请求类型参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = null;
        if (TextUtils.isEmpty(daimaweiID)) {
            daimaweiID = "945278593";
        }
        switch (flag) {
            case -1:
            case 0:
                adSlot = new AdSlot.Builder()
                        .setCodeId(daimaweiID) //广告位id
                        .setSupportDeepLink(true)
                        .setAdCount(flag == 0 ? 3 : 1) //请求广告数量为1到3条
                        .setExpressViewAcceptedSize(baseAd.ad_width, baseAd.ad_height) //期望个性化模板广告view的size,单位dp
                        .setImageAcceptedSize(640, 320) //这个参数设置即可，不影响个性化模板广告的size
                        .build();
                break;
            default:
                adSlot = new AdSlot.Builder()
                        .setCodeId(daimaweiID) //广告位id
                        .setSupportDeepLink(true)
                        .setAdCount(3) //请求广告数量为1到3条
                        .setExpressViewAcceptedSize(baseAd.ad_width, baseAd.ad_height) //期望个性化模板广告view的size,单位dp
                        .setImageAcceptedSize(640, 320) //这个参数设置即可，不影响个性化模板广告的size
                        .build();
                break;
        }
        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                MyToash.Log("loadNativeExp-erra", message + "  " + code);
                if (getttAdShowBitamp != null) {
                    getttAdShowBitamp.getttAdShowBitamp(null, 0);
                }
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    return;
                }
                if (flag != 0) {
                    TTNativeExpressAd mTTAd = null;
                    int p = (int) (Math.random() * ads.size());
                    mTTAd = ads.get(p);
                    bindAdListener(mTTAd);
                }
            }
        });
    }

    public void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                adClick(activity, baseAd, flag, null);
            }

            @Override
            public void onAdShow(View view, int type) {
                if (getttAdShowBitamp != null) {
                    getttAdShowBitamp.getttAdShowBitamp(view, 0);
                }
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                MyToash.Log("render--11", msg + "  " + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                MyToash.Log("render--11", flag + "   " + baseAd.ad_android_key);
                //frameLayoutToday.removeAllViews();
                frameLayoutToday.addView(view);
            }
        });
        MyToash.Log("render--", flag + "   " + baseAd.ad_android_key);
        ad.render();//调用render开始渲染广告
        //dislike设置
        bindDislike(ad);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        //可选，下载监听设置
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                //TToast.show(NativeExpressActivity.this, "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    // TToast.show(NativeExpressActivity.this, "下载中，点击暂停", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                //TToast.show(NativeExpressActivity.this, "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                //TToast.show(NativeExpressActivity.this, "下载失败，点击重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                //TToast.show(NativeExpressActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                //TToast.show(NativeExpressActivity.this, "点击安装", Toast.LENGTH_LONG);
            }
        });
    }
    public boolean mHasShowDownloadActive;
    public AdSlot adSlot;
    public List<AdView> mJHAdViews;
    public AdView mJHAdView;

    private final  String  JHTAG = "JHTAG";
    public void bindAdListener(PageLoader mPageLoader, Activity activity, FrameLayout frameLayoutToday, BaseAd baseAd, BaseAd.GetttAdShowBitamp getttAdShowBitamp) {
        Log.e(JHTAG,"jh bindAdListener ");
        if (mJHAdViews == null) {
            mJHAdViews = new ArrayList<>();
        } else if (mJHAdViews.size() > 5) {
            Log.e(JHTAG,"bindAdListener #2");
            if (mJHAdView != null) {
                mJHAdView.recycle();
                mJHAdViews.remove(mJHAdView);
            }
            return;
        }
        AdSize adSize = new AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
        Log.e(JHTAG,"bindAdListener #1 activity = "+activity);
       AdRequest adRequest = new AdRequest.Builder(activity)
               .setCodeId(baseAd.ad_android_key)
               .setAdRequestCount(1)
               .setAdSize(adSize)
               .build();
        Log.e(JHTAG,"bindAdListener #2");
        adRequest.loadFeedListAd(new FeedListAdListener() {
            @Override
            public void onAdLoaded(List<AdView> list) {
                Log.e(JHTAG,"onAdLoaded #1");
                if (mJHAdView != null) {
                    mJHAdView.recycle();
                    mJHAdViews.remove(mJHAdView);
                }
                mJHAdViews.addAll(list);
            }

            @Override
            public void onAdClicked(AdView adView) {
                Log.e(JHTAG,"onAdClicked #1");
            }

            @Override
            public void onAdDismissed(AdView adView) {
                Log.e(JHTAG,"onAdDismissed #1");
            }

            @Override
            public void onADExposed(AdView adView) {
                Log.e(JHTAG,"onADExposed #1");
                if (getttAdShowBitamp != null) {
                    getttAdShowBitamp.getttAdShowBitamp(null, 0);
                }

                if (mPageLoader != null && mPageLoader.mCurPage != null && mPageLoader.mCurPage.pageStyle == 3) {
                    frameLayoutToday.removeAllViews();
                    frameLayoutToday.setVisibility(View.VISIBLE);
                    frameLayoutToday.addView(adView.getView());

                }
            }

            @Override
            public void onVideoLoad() {

            }

            @Override
            public void onVideoPause() {

            }

            @Override
            public void onVideoStart() {

            }

            @Override
            public void onAdError(AdError adError) {
                Log.e(JHTAG,"onAdError #1");
                if (mPageLoader != null && mPageLoader.mCurPage != null && mPageLoader.mCurPage.pageStyle == 3) {
                    if (getttAdShowBitamp != null) {
                        getttAdShowBitamp.getttAdShowBitamp(null, -1);
                    }
                }
            }
        });
    }

    public void renderAd(PageLoader mPageLoader, Activity activity, FrameLayout frameLayoutToday, BaseAd baseAd, BaseAd.GetttAdShowBitamp getttAdShowBitamp) {
        Log.e(JHTAG,"renderAd #1");
        if (mJHAdViews == null || mJHAdViews.isEmpty()) {
            return;
        }
        mJHAdView = mJHAdViews.get(0);
        Log.e(JHTAG,"renderAd #2");
        frameLayoutToday.addView(mJHAdView.getView());
        mJHAdView.render(activity);

    }

    /**
     * 设置广告的不喜欢，开发者可自定义样式
     *
     * @param ad
     */
    private void bindDislike(TTNativeExpressAd ad) {
        //使用默认个性化模板中默认dislike弹出样式
        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                //TToast.show(mContext, "点击 " + value);
                //用户选择不喜欢原因后，移除广告展示
                //MyToash.ToashSuccess(activity, "感谢您的反馈,我们将尽量减少此类广告推送");
                //MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.share_noUrl));
                frameLayoutToday.removeAllViews();
                frameLayoutToday.setVisibility(View.GONE);
     /*           ViewGroup.LayoutParams layoutParams = frameLayoutToday.getLayoutParams();
                layoutParams.height = 0;
                frameLayoutToday.setLayoutParams(layoutParams);*/
            }

            @Override
            public void onCancel() {
                //TToast.show(mContext, "点击取消 ");
            }


            public void onRefuse() {

            }
        });
    }

    public interface OnRewardVerify {

        void OnRewardVerify();
    }

    private void loadJHBannerAd(ViewGroup adcontainer, Context context,String codid){
        AdRequest adRequest = new AdRequest.Builder(context)
                .setCodeId(codid)
                .setAdRequestCount(1)
                .build();
        adRequest.loadFeedListNativeAd(new FeedListNativeAdListener() {
            @Override
            public void onAdLoaded(List<NativeAdData> list) {
                Log.e(JHTAG,"onAdLoaded");
                if(list!=null && list.size()>0){
                   View result = bindJHAd(list.get(0),context);
                   if(result!=null){
                       frameLayoutToday.removeAllViews();
                       frameLayoutToday.addView(result);
                   }
                }
            }

            @Override
            public void onAdError(AdError adError) {
                MyToash.Log(JHTAG, "onAdError  " + adError.getErrorCode() + "  " + adError.getErrorMessage());
            }
        });
    }

    private View bindJHAd(NativeAdData adData,Context context){
        Log.e(JHTAG,"bindJHAd");
        if(adData!=null){
            LinearLayout adGroup = new LinearLayout(context);
            adGroup.setOrientation(LinearLayout.HORIZONTAL);
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(this.frameLayoutToday.getHeight(),this.frameLayoutToday.getHeight());
            ViewGroup.MarginLayoutParams imgMarparams = new ViewGroup.MarginLayoutParams(imgParams);
            int margin = ImageUtil.dp2px(context,2);
            imgMarparams.setMargins(margin,margin,margin,margin);
            adGroup.addView(imageView,imgMarparams);
            LinearLayout infoGroup = new LinearLayout(context);
            infoGroup.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            infoParams.weight = 1;

            TextView titleView = new TextView(context);
            TextView descView = new TextView(context);
            descView.setSingleLine();
            LinearLayout.LayoutParams infoChildParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
            infoChildParams.weight = 1;
            infoGroup.addView(titleView,infoChildParams);
            infoGroup.addView(descView,infoChildParams);
            titleView.setText(adData.getTitle());
            descView.setText(adData.getDesc());
            adGroup.addView(infoGroup,infoParams);

            FrameLayout.LayoutParams adlogoParams = new FrameLayout.LayoutParams(0,0);
            adData.attach(activity);
            List<View> clickViews = new ArrayList<>();
            clickViews.add(imageView);
            clickViews.add(titleView);
            clickViews.add(descView);
            View result = adData.bindView(adGroup, null, adlogoParams, clickViews, new NativeAdListener() {
                @Override
                public void onADExposed() {
                    Log.e(JHTAG,"onADExposed");
                }

                @Override
                public void onADClicked() {
                    Log.e(JHTAG,"onADClicked");
                    adClick(activity, baseAd, flag, null);
                }

                @Override
                public void onAdError(AdError adError) {

                }
            });
            MyGlide.GlideImage(activity,adData.getImageUrl(),imageView);
            return result;

        }
        return null;
    }
    private void loadBannerAd() {


        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(daimaweiID) //广告位id
                .setAdCount(1)
                .setSupportDeepLink(true)
                .setExpressViewAcceptedSize(baseAd.ad_width, baseAd.ad_height)
                .setImageAcceptedSize(360, 200)
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {

            @Override
            public void onError(int code, String message) {
                MyToash.Log("loadNativeExpressAd", flag + " " + code + "  " + message);
                //frameLayoutToday.removeAllViews();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    return;
                }
                TTNativeExpressAd mTTAd = ads.get(0);
                buildBannerListener(mTTAd);
                mTTAd.render();
            }
        });
    }

    private void buildBannerListener(TTNativeExpressAd mTTAd) {
        //设置广告互动监听回调
        mTTAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                // TToast.show(mContext, "广告被点击");
                adClick(activity, baseAd, flag, null);
            }

            @Override
            public void onAdShow(View view, int type) {
                // TToast.show(mContext, "广告展示");
            }

            @Override
            public void onRenderFail(View view, String s, int i) {
                //     MyToash.Log("loadNativeExpressAd", s + "---" + i);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //     MyToash.Log("loadNativeExpressAd", "成功" + "-" + width + "-" + height);
                frameLayoutToday.removeAllViews();
                frameLayoutToday.addView(view);
            }
        });
        //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
        mTTAd.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                MyToash.ToashSuccess(activity, "感谢您的反馈,我们将尽量减少此类广告推送");

                //用户选择不喜欢原因后，移除广告展示
                //frameLayoutToday.removeAllViews();
            }

            @Override
            public void onCancel() {
                //TToast.show(mContext, "点击取消 ");
            }


            public void onRefuse() {

            }

        });
        //（可选）设置下载类广告的下载监听
        if (mTTAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            bindDownloadListener(mTTAd);
        }
    }

    private void bindDownloadListener(TTNativeExpressAd ad) {
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {

            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {

            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                //TToast.show(BannerActivity.this, "下载暂停，点击图片继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                //TToast.show(BannerActivity.this, "下载失败，点击图片重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                //TToast.show(BannerActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                //TToast.show(BannerActivity.this, "点击图片安装", Toast.LENGTH_LONG);
            }
        });
    }

    public static void loadJiliAd(Activity activity, OnRewardVerify onRewardVerify) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);
        if (mTTAdNative == null) {
            MyToash.Toash(activity, "广告加载异常");
            return;
        }
        String android_key = ShareUitls.getString(activity, "USE_AD_VIDEO_CODE", null);
        if (TextUtils.isEmpty(android_key)) {
            android_key = "945167808";
        }
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(android_key)
                .setSupportDeepLink(true)
                .setAdCount(1)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(10)   //奖励的数量
                //必传参数，表来标识应用侧唯一用户；若非服务器回调模式或不需sdk透传
                //可设置为空字符串
                .setUserID("user123")
                .setOrientation(TTAdConstant.VERTICAL)  //设置期望视频播放的方向，为TTAdConstant.HORIZONTAL或TTAdConstant.VERTICAL
                .setMediaExtra("") //用户透传的信息，可不传
                .build();
        MyToash.Log("JavascriptInterface  ad", android_key);

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                MyToash.Log("JavascriptInterface  ad", code + "   " + message);
                //Toast.makeText(RewardVideoActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            //视频广告加载后的视频文件资源缓存到本地的回调
            @Override
            public void onRewardVideoCached() {
                //Toast.makeText(RewardVideoActivity.this, "rewardVideoAd video cached", Toast.LENGTH_SHORT).show();
            }

            //视频广告素材加载到，如title,视频url等，不包括视频文件
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ad.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                            @Override
                            public void onAdShow() {

                            }

                            @Override
                            public void onAdVideoBarClick() {

                            }

                            @Override
                            public void onAdClose() {

                                //     MyToash.Log("JavascriptInterface  ad", "onAdClose");
                            }

                            @Override
                            public void onVideoComplete() {
                                onRewardVerify.OnRewardVerify();
                                //     MyToash.Log("JavascriptInterface  ad", "onVideoComplete");
                            }

                            @Override
                            public void onVideoError() {
                                //     MyToash.Log("JavascriptInterface  ad", "onVideoError");
                            }

                            @Override
                            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {

                            }

                            @Override
                            public void onSkippedVideo() {
                                //     MyToash.Log("JavascriptInterface  ad", "onSkippedVideo");
                            }
                        });
                        ad.showRewardVideoAd(activity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
                    }
                });
            }
        });
    }
}
