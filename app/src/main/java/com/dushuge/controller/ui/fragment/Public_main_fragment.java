package com.dushuge.controller.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseFragment;
import com.dushuge.controller.constant.Constant;
import com.dushuge.controller.eventbus.ShelfDeleteRefresh;
import com.dushuge.controller.eventbus.StoreHotWords;
import com.dushuge.controller.eventbus.StoreScrollStatus;
import com.dushuge.controller.eventbus.ToStore;
import com.dushuge.controller.model.StoreHotWordBean;
import com.dushuge.controller.ui.activity.SearchActivity;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.ui.utils.StatusBarUtil;
import com.dushuge.controller.ui.view.IndexPagerAdapter;
import com.dushuge.controller.utils.LanguageUtil;
import com.dushuge.controller.utils.ShareUitls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dushuge.controller.constant.Constant.AUDIO_CONSTANT;
import static com.dushuge.controller.constant.Constant.BOOK_CONSTANT;
import static com.dushuge.controller.constant.Constant.COMIC_CONSTANT;
import static com.dushuge.controller.constant.Constant.REFRESH_HEIGHT;
import static com.dushuge.controller.constant.Constant.REFRESH_HEIGHT_TOP;
import static com.dushuge.controller.ui.utils.StatusBarUtil.setStatusTextColor;

public class Public_main_fragment extends BaseFragment {

    @BindView(R.id.fragment_store_XTabLayout)
    SmartTabLayout publicSelectionXTabLayout;
    @BindView(R.id.fragment_pubic_main_ViewPager)
    ViewPager fragmentPubicMainViewPager;

    @BindView(R.id.fragment_store_search_Bookname)
    TextView mFragmentStoreSearchBookname;;
    @BindView(R.id.fragment_store_search_img)
    ImageView mFragmentStoreSearchImg;
    @BindView(R.id.fragment_store_search)
    RelativeLayout mFragmentStoreSearch;
    @BindView(R.id.fragment_store_sex)
    ImageView mFragmentStoreSex;
    @BindView(R.id.fragment_store_top)
    View fragment_store_top;
    @BindView(R.id.fragment_store_manorwoman)
    View fragment_store_manorwoman;
    @BindView(R.id.fragment_top_search_bar_layout)
    LinearLayout mHeadFragmentBookStoreLayout;

    private List<String> stringList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private Map<Integer, StoreScrollStatus> selection_TextViewStoreScrollStatus;

    private int pageType, currentPagePosition;
    private boolean SEX, ChangeSexIng;
    public boolean Status;
    private int pageSize;

    // 顶部搜索框的热词
    private StoreHotWordBean bookStoreHotWordBean, comicStoreHotWordBean, audioStoreHotWordBean;
    private int bookHotWordPosition = 0, comicHotWordPosition = 0, audioHotWordPosition = 0;

    public Public_main_fragment() {

    }

    /**
     * 设置界面类型（书架、书城、发现）
     *
     * @param pageType
     */
    public Public_main_fragment(int pageType) {
        this.pageType = pageType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getInt("pageType", 0) != 0) {
            pageType = savedInstanceState.getInt("pageType", 0);
            Status = savedInstanceState.getBoolean("Status");
            int NotchHeight2;
            if ((NotchHeight2 = savedInstanceState.getInt("NotchHeight", 0)) != 0) {
                NotchHeight = NotchHeight2;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("pageType", pageType);
        outState.putInt("NotchHeight", NotchHeight);
        outState.putBoolean("Status", Status);
        super.onSaveInstanceState(outState);
    }

    @Override
    public int initContentView() {
        USE_EventBus = true;
        return R.layout.fragment_public_main;
    }

    @Override
    public void initView() {
        int sex = ShareUitls.getInt(activity, "sex", 1);
        SEX = (sex == 2);
        if (NotchHeight != 0) {
            setTopBarHeight(ImageUtil.dp2px(activity, 50) + NotchHeight);
        } else {
            setTopBarHeight(ImageUtil.dp2px(activity, 70));
        }
        if (pageType == 2) {
            if (Constant.getProductTypeList(activity).size() > 1) {
                if (!SEX) {
                    mFragmentStoreSex.setImageResource(R.mipmap.comic_mall_boy_dark);
                } else {
                    mFragmentStoreSex.setImageResource(R.mipmap.comic_mall_girl_dark);
                }
            } else {
                fragment_store_manorwoman.setVisibility(View.GONE);
            }
        } else {
            fragment_store_manorwoman.setVisibility(View.GONE);
        }
        if (Constant.getProductTypeList(activity).size() > 1) {
            for (String strType : Constant.getProductTypeList(activity)) {
                setFragmentType(true, strType, sex);
            }
        } else {
            String strType = Constant.getProductTypeList(activity).get(0);
            setFragmentType(false, strType, sex);
        }
        IndexPagerAdapter indexPagerAdapter = new IndexPagerAdapter(getChildFragmentManager(), stringList, fragmentList);
        fragmentPubicMainViewPager.setAdapter(indexPagerAdapter);
        publicSelectionXTabLayout.setViewPager(fragmentPubicMainViewPager);
        pageSize = stringList.size();
        if (pageSize >= 1) {
            selection_TextViewStoreScrollStatus = new HashMap<>();
            for (int k = 0; k < pageSize; k++) {
                TextView textView = publicSelectionXTabLayout.getTabAt(k).findViewById(R.id.item_tablayout_text);
                StoreScrollStatus storeScrollStatus = new StoreScrollStatus(k, false, textView);
                if (k == 0) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
                }
                selection_TextViewStoreScrollStatus.put(k, storeScrollStatus);
            }
            if (pageType == 2) {
                for (int position = 0; position < pageSize; position++) {
                    TextView textView = selection_TextViewStoreScrollStatus.get(position).textView;
                    textView.setTextColor(ContextCompat.getColor(activity, R.color.white));
                }
                publicSelectionXTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(activity, R.color.white));
                // 开始循环热词
                mFragmentStoreSearchBookname.post(new Runnable() {
                    @Override
                    public void run() {
                        HotWordHandler.removeMessages(0);
                        HotWordHandler.sendEmptyMessageDelayed(0, 500);
                    }
                });
            }
        }
        if (pageSize == 3) {
            fragmentPubicMainViewPager.setOffscreenPageLimit(3);
        }
        initListener();
    }

    private void initListener() {
        fragmentPubicMainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPagePosition = position;
                if (pageSize > 1 && selection_TextViewStoreScrollStatus != null && !selection_TextViewStoreScrollStatus.isEmpty()) {
                    for (int k = 0; k < pageSize; k++) {
                        if (selection_TextViewStoreScrollStatus.get(k) != null) {
                            TextView textView = selection_TextViewStoreScrollStatus.get(k).textView;
                            if (k == position) {
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
                            } else {
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                            }
                        }
                    }
                }
                if (pageType == 1) {
                    // 如果当前是书架删除数据状态，发生切换则取消删除
                    EventBus.getDefault().post(new ShelfDeleteRefresh());
                }
                if (pageType == 2) {
                    // 更改顶部颜色
                    if (selection_TextViewStoreScrollStatus != null && !selection_TextViewStoreScrollStatus.isEmpty()) {
                        if (selection_TextViewStoreScrollStatus.get(position) != null) {
                            selection_TextViewStoreScrollStatus.get(position).isChangeBg = true;
                            setTopBg(selection_TextViewStoreScrollStatus.get(position));
                        }
                    }
                    // 切换搜索框的热词
                    HotWordHandler.removeMessages(0);
                    HotWordHandler.sendEmptyMessageDelayed(0, 500);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置UI
     *
     * @param isMore
     * @param strType
     * @param sex
     */
    private void setFragmentType(boolean isMore, String strType, int sex) {
        if (isMore) {
            if (strType.equals("1")) {
                stringList.add(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
            } else if (strType.equals("2")) {
                stringList.add(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
            } else if (strType.equals("3")) {
                stringList.add(LanguageUtil.getString(activity, R.string.noverfragment_audio));
            }
        }
        int type = Integer.valueOf(strType) - 1;
        switch (pageType) {
            case 1:
                if (!isMore) {
                    stringList.add(LanguageUtil.getString(activity, R.string.activity_main1));
                }
                // 书架
                fragmentList.add(new ShelfFragment(type, 0));
                break;
            case 2:
                // 书城
                mHeadFragmentBookStoreLayout.setVisibility(View.VISIBLE);
                mFragmentStoreSearch.setBackground(MyShape.setMyshapeMineStroke(activity, 20, 15));
                if (isMore) {
                    fragmentList.add(new StoreFragment(type, sex));
                } else {
                    if (!SEX) {
                        // 男声
                        stringList.add(LanguageUtil.getString(activity, R.string.storeFragment_boy));
                        stringList.add(LanguageUtil.getString(activity, R.string.storeFragment_gril));
                        fragmentList.add(new StoreFragment(type, 1));
                        fragmentList.add(new StoreFragment(type, 2));
                    } else {
                        // 女生
                        stringList.add(LanguageUtil.getString(activity, R.string.storeFragment_gril));
                        stringList.add(LanguageUtil.getString(activity, R.string.storeFragment_boy));
                        fragmentList.add(new StoreFragment(type, 2));
                        fragmentList.add(new StoreFragment(type, 1));
                    }
                }
                break;
            case 3:
                if (!isMore) {
                    stringList.add(LanguageUtil.getString(activity, R.string.activity_main3));
                }
                // 发现
                fragmentList.add(new DiscoverFragment(type, 0));
                break;
        }
    }

    @OnClick({R.id.fragment_store_manorwoman, R.id.fragment_store_search, R.id.fragment_store_top})
    public void onClick(View v) {
        if (pageType == 2) {
            switch (v.getId()) {
                case R.id.fragment_store_manorwoman:
                    ChangeSexIng = true;
                    // 切换男女频
                    for (int i = 0; i < fragmentList.size(); i++) {
                        Fragment storeFragment = fragmentList.get(i);
                        ((StoreFragment) storeFragment).setChannel_id(SEX ? 1 : 2, (i < fragmentList.size() - 1) ? null : new Public_main_fragment.OnChangeSex() {
                            @Override
                            public void success() {
                                changeSex(SEX);
                            }
                        });
                    }
                    break;
                case R.id.fragment_store_search:
                    int index = ((StoreFragment) fragmentList.get(currentPagePosition)).productType;
                    String mKeyWord = mFragmentStoreSearchBookname.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("mKeyWord", mKeyWord);
                    intent.putExtra("productType", index);
                    intent.setClass(activity, SearchActivity.class);
                    startActivity(intent);
                    break;
                case R.id.fragment_store_top:
                    break;
            }
        }
    }

    /**
     * 切换性别
     * @param isWomen
     */
    private void changeSex(boolean isWomen) {
        SEX = !SEX;
        if (Status) {
            mFragmentStoreSex.setImageResource(isWomen ? R.mipmap.comic_mall_boy : R.mipmap.comic_mall_girl);
        } else {
            mFragmentStoreSex.setImageResource(isWomen ? R.mipmap.comic_mall_boy_dark : R.mipmap.comic_mall_girl_dark);
        }
        ShareUitls.putInt(activity, "sex", isWomen ? 1 : 2);
        MyToash.ToashSuccess(activity, isWomen ? LanguageUtil.getString(activity, R.string.StoreFragment_toman) :
                LanguageUtil.getString(activity, R.string.StoreFragment_towoman));
    }

    @Override
    public void initData() {

    }

    @Override
    public void initInfo(String json) {

    }

    /**
     * 用于刷新书城的底部选择器
     *
     * @param toStore
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ToStore(ToStore toStore) {
        if (!toStore.oneScroll) {
            if (pageType == 2 && !fragmentList.isEmpty()) {
                int index = Constant.getProductTypeList(activity).indexOf(String.valueOf(toStore.PRODUCT + 1));
                fragmentPubicMainViewPager.setCurrentItem(index);
            }
        } else {
            if (toStore.IsOneScroll) {
                if (currentPagePosition < fragmentList.size() - 1) {
                    fragmentPubicMainViewPager.setCurrentItem(currentPagePosition + 1);
                }
            } else {
                if (currentPagePosition > 0) {
                    fragmentPubicMainViewPager.setCurrentItem(currentPagePosition - 1);
                }
            }
        }
    }

    /**
     * 用户获取热词数据
     *
     * @param toStore
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StoreHotWords(StoreHotWords toStore) {
        if (pageType == 2 && toStore.HotWord != null && !toStore.HotWord.isEmpty()) {
            int type = ((StoreFragment) fragmentList.get(currentPagePosition)).productType;
            HotWordHandler.removeMessages(0);
            if (toStore.productType == BOOK_CONSTANT) {
                HotWordHandler.removeMessages(BOOK_CONSTANT);
                if (bookStoreHotWordBean == null) {
                    bookStoreHotWordBean = new StoreHotWordBean();
                }
                bookStoreHotWordBean.setHotWordList(toStore.HotWord);
            } else if (toStore.productType == COMIC_CONSTANT) {
                if (comicStoreHotWordBean == null) {
                    comicStoreHotWordBean = new StoreHotWordBean();
                }
                comicStoreHotWordBean.setHotWordList(toStore.HotWord);
            } else if (toStore.productType == AUDIO_CONSTANT) {
                if (audioStoreHotWordBean == null) {
                    audioStoreHotWordBean = new StoreHotWordBean();
                }
                audioStoreHotWordBean.setHotWordList(toStore.HotWord);
            }
            if (type == toStore.productType) {
                HotWordHandler.sendEmptyMessageDelayed(0, 500);
            }
        }
    }

    /**
     * 热词自动更换handler
     */
    @SuppressLint("HandlerLeak")
    private Handler HotWordHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (pageType == 2 && msg.what == 0) {
                // 根据currentPagePosition获取类型
                int type = ((StoreFragment) fragmentList.get(currentPagePosition)).productType;
                if (type == BOOK_CONSTANT) {
                    if (bookStoreHotWordBean != null && !bookStoreHotWordBean.getHotWordList().isEmpty()) {
                        if (bookHotWordPosition >= bookStoreHotWordBean.getHotWordList().size()) {
                            bookHotWordPosition = 0;
                        }
                        String word = bookStoreHotWordBean.getHotWordList().get(bookHotWordPosition);
                        setHotWord(word);
                        ++bookHotWordPosition;
                    } else {
                        setHotWord("");
                    }
                } else if (type == COMIC_CONSTANT) {
                    if (comicStoreHotWordBean != null && !comicStoreHotWordBean.getHotWordList().isEmpty()) {
                        if (comicHotWordPosition >= comicStoreHotWordBean.getHotWordList().size()) {
                            comicHotWordPosition = 0;
                        }
                        String word = comicStoreHotWordBean.getHotWordList().get(comicHotWordPosition);
                        setHotWord(word);
                        ++comicHotWordPosition;
                    } else {
                        setHotWord("");
                    }
                } else if (type == AUDIO_CONSTANT) {
                    if (audioStoreHotWordBean != null && !audioStoreHotWordBean.getHotWordList().isEmpty()) {
                        if (audioHotWordPosition >= audioStoreHotWordBean.getHotWordList().size()) {
                            audioHotWordPosition = 0;
                        }
                        String word = audioStoreHotWordBean.getHotWordList().get(audioHotWordPosition);
                        setHotWord(word);
                        ++audioHotWordPosition;
                    } else {
                        setHotWord("");
                    }
                }
            }
            HotWordHandler.sendEmptyMessageDelayed(0, 5000);
        }
    };

    /**
     * 设置内容
     * @param hotWord
     */
    private void setHotWord(String hotWord) {
        if (TextUtils.isEmpty(hotWord)) {
            hotWord = "";
        }
        mFragmentStoreSearchBookname.setText(hotWord);
    }

    private void setTopBarHeight(int topBarHeight) {
        ViewGroup.LayoutParams marginLayoutParams = fragment_store_top.getLayoutParams();
        marginLayoutParams.height = topBarHeight;
        fragment_store_top.setLayoutParams(marginLayoutParams);
        if (pageType == 1 || pageType == 3) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentPubicMainViewPager.getLayoutParams();
            layoutParams.topMargin = topBarHeight;
            fragmentPubicMainViewPager.setLayoutParams(layoutParams);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setStoreScrollStatusListener(StoreScrollStatus storeScrollStatus) {
        if (pageType == 2 &&!ChangeSexIng) {
            setTopBg(storeScrollStatus);
        }
        ChangeSexIng=false;
    }

    /**
     * 上滑下滑更改toolbar
     *
     * @param storeScrollStatus
     */
    private void setTopBg(StoreScrollStatus storeScrollStatus) {
        if (storeScrollStatus != null) {
            int height;
            if (storeScrollStatus.isChangeBg) {
                height = REFRESH_HEIGHT;
            } else {
                height = REFRESH_HEIGHT_TOP;
            }
            int scrollY = storeScrollStatus.scrollY;
            if (scrollY > height) {
                scrollY = height;
            }
            float alpha = scrollY * 255 / height;
            if (selection_TextViewStoreScrollStatus != null && storeScrollStatus.isChangeBg) {
                if (selection_TextViewStoreScrollStatus.get(storeScrollStatus.productType) != null) {
                    selection_TextViewStoreScrollStatus.get(storeScrollStatus.productType).scrollY = scrollY;
                }
            }
            fragment_store_top.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
            int color = 255 - (int) alpha;
            if (pageSize > 1) {
                for (int position = 0; position < pageSize; position++) {
                    TextView textView = selection_TextViewStoreScrollStatus.get(position).textView;
                    textView.setTextColor(Color.argb(255, color, color, color));
                    publicSelectionXTabLayout.setSelectedIndicatorColors(Color.argb(255, color, color, color));
                }
            }
            boolean flag = storeScrollStatus.scrollY >= REFRESH_HEIGHT;
            if (Status != flag) {
                setTopBg(storeScrollStatus, SEX);
                Status = flag;
            }
        }
    }

    /**
     * 更改书城顶部控件
     *
     * @param scrollStatus
     * @param status
     */
    private void setTopBg(StoreScrollStatus scrollStatus, boolean status) {
        scrollStatus.status = status;
        if (!Status) {
            setStatusTextColor(true, activity);
            mFragmentStoreSearchBookname.setTextColor(Color.GRAY);
            mFragmentStoreSearchImg.setColorFilter(Color.GRAY);
            mFragmentStoreSearch.setBackground(MyShape.setMyshapeStroke(activity, 20, 1, ContextCompat.getColor(activity, R.color.graybg)));
            if (SEX) {
                mFragmentStoreSex.setImageResource(R.mipmap.comic_mall_girl);
            } else {
                mFragmentStoreSex.setImageResource(R.mipmap.comic_mall_boy);
            }
        } else {
            StatusBarUtil.setStatusStoreColor(activity, false);
            mFragmentStoreSearchBookname.setTextColor(ContextCompat.getColor(activity, R.color.white));
            mFragmentStoreSearchImg.setColorFilter(ContextCompat.getColor(activity, R.color.white));
            mFragmentStoreSearch.setBackground(MyShape.setMyshapeMineStroke(activity, 20, 15));
            if (SEX) {
                mFragmentStoreSex.setImageResource(R.mipmap.comic_mall_girl_dark);
            } else {
                mFragmentStoreSex.setImageResource(R.mipmap.comic_mall_boy_dark);
            }
        }
        if (selection_TextViewStoreScrollStatus != null && selection_TextViewStoreScrollStatus.get(scrollStatus.productType) != null) {
            selection_TextViewStoreScrollStatus.get(scrollStatus.productType).status = Status;
        }
    }

    /**
     * 更换性别监听事件接口
     */
    public interface OnChangeSex {

        void success();
    }
}
