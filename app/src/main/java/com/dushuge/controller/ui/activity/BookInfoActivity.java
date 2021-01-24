package com.dushuge.controller.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.umeng.socialize.UMShareAPI;
import com.dushuge.controller.R;
import com.dushuge.controller.base.BWNApplication;
import com.dushuge.controller.base.BaseActivity;
import com.dushuge.controller.constant.Constant;
import com.dushuge.controller.eventbus.CommentRefresh;
import com.dushuge.controller.eventbus.ReadContinue;
import com.dushuge.controller.eventbus.RefreshBookInfo;
import com.dushuge.controller.eventbus.RefreshBookInfoStatusBar;
import com.dushuge.controller.eventbus.RefreshBookSelf;
import com.dushuge.controller.eventbus.RefreshShelf;
import com.dushuge.controller.eventbus.RefreshShelfCurrent;
import com.dushuge.controller.model.BaseLabelBean;
import com.dushuge.controller.model.BaseTag;
import com.dushuge.controller.model.Book;
import com.dushuge.controller.model.Comment;
import com.dushuge.controller.model.Downoption;
import com.dushuge.controller.model.InfoBook;
import com.dushuge.controller.model.InfoBookItem;
import com.dushuge.controller.net.HttpUtils;
import com.dushuge.controller.net.ReaderParams;
import com.dushuge.controller.ui.adapter.CommentAdapter;
import com.dushuge.controller.ui.adapter.PublicMainAdapter;
import com.dushuge.controller.ui.audio.manager.AudioManager;
import com.dushuge.controller.ui.dialog.BookDownDialogFragment;
import com.dushuge.controller.ui.audio.AudioAiActivity;
import com.dushuge.controller.ui.localshell.localapp.LoaclMainActivity;
import com.dushuge.controller.ui.read.manager.ChapterManager;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyGlide;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.ui.view.AutoTextView;
import com.dushuge.controller.ui.view.screcyclerview.SCOnItemClickListener;
import com.dushuge.controller.ui.view.screcyclerview.SCRecyclerView;
import com.dushuge.controller.utils.InternetUtils;
import com.dushuge.controller.utils.LanguageUtil;
import com.dushuge.controller.utils.MyShare;
import com.dushuge.controller.utils.ObjectBoxUtils;
import com.dushuge.controller.utils.ScreenSizeUtils;
import com.dushuge.controller.utils.ShareUitls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dushuge.controller.constant.Api.mBookInfoUrl;
import static com.dushuge.controller.constant.Constant.AgainTime;
import static com.dushuge.controller.constant.Constant.BOOK_CONSTANT;
import static com.dushuge.controller.constant.Constant.REFRESH_HEIGHT;
import static com.dushuge.controller.ui.utils.StatusBarUtil.setStatusTextColor;

/**
 * 小说详情
 */
public class BookInfoActivity extends BaseActivity {

    @BindView(R.id.activity_book_info_recyclerView_layout)
    RelativeLayout recyclerViewLayout;
    @BindView(R.id.public_recycleview)
    SCRecyclerView publicRecycleview;

    @BindView(R.id.Book_info_titlebar_container)
    public RelativeLayout Book_info_titlebar_container;
    @BindView(R.id.back)
    public ImageView back;
    @BindView(R.id.book_info_titleBar_layout)
    LinearLayout titleBarLayout;
    @BindView(R.id.titlebar_share)
    RelativeLayout titlebar_share;
    @BindView(R.id.titlebar_share_img)
    ImageView titlebar_share_img;
    @BindView(R.id.titlebar_download_img)
    ImageView downloadImage;
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;

    @BindView(R.id.activity_book_info_button_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.activity_Book_info_add_shelf)
    public TextView activity_Book_info_add_shelf;
    @BindView(R.id.activity_Book_info_audio)
    TextView gotoAudioOrDown;
    @BindView(R.id.activity_Book_info_start_read)
    public TextView activity_Book_info_start_read;

    private List<BaseLabelBean> list;
    private long mBookId;
    private ViewHolder viewHolder;
    private int onScrolled_y;
    private boolean is_black;

    private PublicMainAdapter bookStoareAdapter;
    private InfoBook infoBook;
    private Book mBook;
    private Book local_Book;

    private boolean onclickTwo = false;

    @Override
    public int initContentView() {
        FULL_CCREEN = true;
        USE_EventBus = true;
        return R.layout.activity_book_info;
    }

    public void initView() {
        InternetUtils.internett(activity);
        setStatusTextColor(false, this);
        list = new ArrayList<>();
        initSCRecyclerView(publicRecycleview, RecyclerView.VERTICAL, 0);
        publicRecycleview.setLoadingMoreEnabled(false);
        if (!Constant.USE_SHARE) {
            titlebar_share.setVisibility(View.GONE);
        }
        View view = layoutInflater.inflate(R.layout.activity_book_info_content, null);
        viewHolder = new ViewHolder(view);
        publicRecycleview.addHeaderView(view);

        ViewGroup.LayoutParams layoutParamsCbg = viewHolder.activity_Book_info_content_cover.getLayoutParams();
        layoutParamsCbg.width = (ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 30)) / 3;
        layoutParamsCbg.height = layoutParamsCbg.width * 4 / 3;
        viewHolder.activity_Book_info_content_cover.setLayoutParams(layoutParamsCbg);
        viewHolder.activity_book_info_lookallcomment.setBackground(MyShape.setMyshapeStroke2(activity,
                20, 2, ContextCompat.getColor(activity, R.color.maincolor), 0));
        if (mBookId == 0) {
            mBookId = getIntent().getLongExtra("book_id", 0);
        }
        if (mBookId == 0) {
            return;
        }
        local_Book = ObjectBoxUtils.getBook(mBookId);
        if (local_Book == null) {
            mBook = new Book();
            mBook.book_id = mBookId;
        } else {
            mBook = local_Book;
        }
        if (mBook.is_collect == 1) {
            activity_Book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
            activity_Book_info_add_shelf.setEnabled(false);
        } else {
            activity_Book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujia));
            activity_Book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.maincolor));
            activity_Book_info_add_shelf.setEnabled(true);
        }
        if (mBook.is_read == 1) {
            activity_Book_info_start_read.setText(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_goon_read));
        }
        if (Constant.USE_AUDIO_AI) {
            gotoAudioOrDown.setText(LanguageUtil.getString(activity, R.string.noverfragment_audio));
            downloadImage.setVisibility(View.VISIBLE);
        } else {
            gotoAudioOrDown.setText(LanguageUtil.getString(activity, R.string.BookInfoActivity_down));
            downloadImage.setVisibility(View.GONE);
        }
        bookStoareAdapter = new PublicMainAdapter(list, BOOK_CONSTANT, activity, false, true);
        publicRecycleview.setAdapter(bookStoareAdapter);
        ViewGroup.LayoutParams layoutParams = viewHolder.activity_bookinfo_head.getLayoutParams();
        layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        view.setLayoutParams(layoutParams);
        initListener();
        setNoNetLayout();
    }

    private void initListener() {
        publicRecycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onScrolled_y += dy;
                if (onScrolled_y <= REFRESH_HEIGHT) {
                    if (is_black) {
                        is_black = false;
                        ImmersionBar.with(activity).init();
                        setStatusTextColor(false, activity);
                        back.setImageResource(R.mipmap.back_white);
                        titlebar_share_img.setImageResource(R.mipmap.share_white);
                        downloadImage.setImageResource(R.mipmap.icon_down_white);
                    } else {
                        setStatusTextColor(false, activity);
                    }
                } else {
                    if (!is_black) {
                        is_black = true;
                        ImmersionBar.with(activity).statusBarDarkFont(true).init();
                        back.setImageResource(R.mipmap.back_black);
                        titlebar_share_img.setImageResource(R.mipmap.share_black);
                        downloadImage.setImageResource(R.mipmap.icon_down_black);
                    }
                }
                final float ratio = (float) Math.min(Math.max(onScrolled_y, 0), REFRESH_HEIGHT) / REFRESH_HEIGHT;
                float alpha = (int) (ratio * 255);
                Book_info_titlebar_container.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                titlebar_text.setAlpha(ratio);
            }
        });
    }

    @OnClick({R.id.titlebar_back, R.id.activity_Book_info_audio, R.id.activity_Book_info_add_shelf,
            R.id.activity_Book_info_start_read, R.id.titlebar_download, R.id.titlebar_share})
    public void getEvent(View view) {
        long ClickTimeNew = System.currentTimeMillis();
        if (ClickTimeNew - ClickTime > AgainTime) {
            ClickTime = ClickTimeNew;
            if (view.getId() == R.id.titlebar_back) {
                if (!BWNApplication.applicationContext.isMainActivityStartUp()) {
                    startActivity(new Intent(activity, MainActivity.class));
                }
                finish();
            } else if (mBook != null && mBook.name != null) {
                switch (view.getId()) {
                    case R.id.activity_Book_info_audio:
                        if (Constant.USE_AUDIO_AI) {
                            if (!InternetUtils.internet(activity)) {
                                MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.audio_no_support_online_player));
                                return;
                            }
                            ObjectBoxUtils.addData(mBook, Book.class);
                            // 打开服务
                            AudioManager.openService(activity);
                            // 打开ai界面
                            Intent audioIntent = new Intent();
                            audioIntent.putExtra("book", mBook);
                            audioIntent.setClass(this, AudioAiActivity.class);
                            startActivity(audioIntent);
                            overridePendingTransition(R.anim.activity_bottom_top_open, R.anim.activity_stay);
                        } else {
                            // 下载
                            BookDownDialogFragment.getDownBookChapters(activity, mBook, null, new BookDownDialogFragment.OnGetDownOptions() {
                                @Override
                                public void onOptions(List<Downoption> downOptions) {
                                    BookDownDialogFragment bookDownDialogFragment = new BookDownDialogFragment(activity, mBook, null, downOptions);
                                    bookDownDialogFragment.show(getSupportFragmentManager(), "BookDownDialogFragment");
                                }
                            });
                        }
                        break;
                    case R.id.titlebar_download:
                        BookDownDialogFragment.getDownBookChapters(activity, mBook, null, new BookDownDialogFragment.OnGetDownOptions() {
                            @Override
                            public void onOptions(List<Downoption> downOptions) {
                                BookDownDialogFragment bookDownDialogFragment = new BookDownDialogFragment(activity, mBook, null, downOptions);
                                bookDownDialogFragment.show(getSupportFragmentManager(), "BookDownDialogFragment");
                            }
                        });
                        break;
                    case R.id.activity_Book_info_add_shelf:
                        if (mBook.is_collect == 0) {
                            mBook.is_collect = 1;
                            ObjectBoxUtils.addData(mBook, Book.class);
                            EventBus.getDefault().post(new RefreshShelf(Constant.BOOK_CONSTANT, new RefreshBookSelf(mBook, 1)));
                            activity_Book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
                            activity_Book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.maincolor2));
                            activity_Book_info_add_shelf.setEnabled(false);
                            MyToash.ToashSuccess(activity, LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
                        }
                        break;
                    case R.id.activity_Book_info_start_read:
                        if (formIntent.getStringExtra("From") != null && formIntent.getStringExtra("From").equals("ReadActivity")) {
                            activity.finish();
                        } else {
                            if (!onclickTwo) {
                                onclickTwo = true;
                                ChapterManager.getInstance().openBook(activity, mBook);
                                onclickTwo = false;
                            }
                        }
                        break;
                    case R.id.titlebar_share:
                        new MyShare(activity)
                                .setFlag(Constant.BOOK_CONSTANT)
                                .setId(mBook.getBook_id())
                                .Share();
                        break;
                }
            }
        }
    }

    public void initData() {
        if (mBookId != 0) {
            readerParams = new ReaderParams(activity);
            readerParams.putExtraParams("book_id", mBookId + "");
            HttpUtils.getInstance().sendRequestRequestParams(activity, mBookInfoUrl, readerParams.generateParamsJson(), responseListener);
        }
    }

    public void initInfo(String json) {
        if (json != null && !TextUtils.isEmpty(json)) {
            InfoBookItem infoBookItem = gson.fromJson(json, InfoBookItem.class);
            infoBook = infoBookItem.book;
            if (current_page == 1) {
                mBook.setName(infoBook.name);
                mBook.setCover(infoBook.cover);
                mBook.setAuthor(infoBook.author);
                mBook.setDescription(infoBook.description);
                mBook.setTotal_chapter(infoBook.total_chapters);
                viewHolder.activity_Book_info_content_name.setText(infoBook.name);
                viewHolder.activity_Book_info_content_author.setText(infoBook.author);
                viewHolder.activity_Book_info_content_display_label.setText(infoBook.display_label);
                viewHolder.activity_Book_info_content_total_comment.setText(infoBook.hot_num);
                viewHolder.activity_Book_info_content_total_shoucanshu.setText(infoBook.total_favors);
                viewHolder.activity_Book_info_content_description.setAutoText(infoBook.description, null);

                viewHolder.activity_Book_info_content_last_chapter_time.setText(infoBook.last_chapter_time);
                viewHolder.activity_Book_info_content_last_chapter.setText(infoBook.last_chapter);
                titlebar_text.setText(infoBook.name);
                titlebar_text.setAlpha(0);
                MyGlide.GlideImageRoundedGasoMohu(activity, infoBook.cover, viewHolder.activity_Book_info_content_cover_bg);
                viewHolder.activity_Book_info_content_cover_bg.setAlpha(0.75f);
                MyGlide.GlideImageNoSize(activity, infoBook.cover, viewHolder.activity_Book_info_content_cover);
                int dp6 = ImageUtil.dp2px(activity, 5);
                int dp3 = ImageUtil.dp2px(activity, 2);
                for (BaseTag tag : infoBook.tag) {
                    TextView textView = new TextView(activity);
                    if (tag.getTab() != null && !tag.getTab().isEmpty()) {
                        textView.setText(tag.getTab());
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                        textView.setLines(1);
                        textView.setGravity(Gravity.CENTER);
                        textView.setPadding(dp6, dp3, dp6, dp3);
                        textView.setTextColor(Color.parseColor(tag.getColor()));
                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setCornerRadius(ImageUtil.dp2px(activity, 10));
                        drawable.setColor(Color.parseColor("#1A" + tag.getColor().substring(1)));
                        textView.setBackground(drawable);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.rightMargin = ImageUtil.dp2px(activity, 10);
                        layoutParams.gravity = Gravity.CENTER_VERTICAL;
                        viewHolder.activity_Book_info_tag.addView(textView, layoutParams);
                    }
                }
                if (local_Book != null) {
                    ObjectBoxUtils.addData(mBook, Book.class);
                }
                if (infoBookItem.advert != null) {
                    viewHolder.activity_Book_info_ad.setVisibility(View.VISIBLE);
                    viewHolder.activity_Book_info_ad.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                    infoBookItem.advert.setAd(activity, viewHolder.activity_Book_info_ad, 1);
                } else {
                    viewHolder.activity_Book_info_ad.setVisibility(View.GONE);
                }
            }
            setComment(infoBookItem, infoBook);
        }
    }

    /**
     * 设置数据
     *
     * @param infoBookItem
     * @param infoBook
     */
    private void setComment(InfoBookItem infoBookItem, InfoBook infoBook) {
        // 评论
        List<Comment> commentList = infoBookItem.comment;
        CommentAdapter commentAdapter = new CommentAdapter(activity, commentList, new SCOnItemClickListener<Comment>() {
            @Override
            public void OnItemClickListener(int flag, int position, Comment comment) {
                Intent intent = new Intent(activity, CommentActivity.class);
                if (comment != null) {
                    intent.putExtra("comment", comment);
                }
                intent.putExtra("current_id", mBookId);
                intent.putExtra("productType", Constant.BOOK_CONSTANT);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClickListener(int flag, int position, Comment O) {

            }
        });
        viewHolder.activity_Book_info_content_comment_container.setLayoutManager(
                new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        viewHolder.activity_Book_info_content_comment_container.setAdapter(commentAdapter);
        String moreText;
        if (infoBook.total_comment > 0) {
            moreText = String.format(LanguageUtil.getString(activity, R.string.CommentListActivity_lookpinglun),
                    infoBook.total_comment >= 1000 ? "999+" : infoBook.total_comment);
        } else {
            moreText = LanguageUtil.getString(activity, R.string.CommentListActivity_no_pinglun);
        }
        if (viewHolder.activity_book_info_lookallcomment.getVisibility() == View.GONE) {
            viewHolder.activity_book_info_lookallcomment.setVisibility(View.VISIBLE);
        }
        viewHolder.activity_book_info_lookallcomment.setText(moreText);
        // 推荐
        if (infoBookItem.label != null && !infoBookItem.label.isEmpty()) {
            list.clear();
            list.addAll(infoBookItem.label);
        }
        viewHolder.likeLine.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
        bookStoareAdapter.notifyDataSetChanged();
    }

    /**
     * 没有网络的UI
     */
    private void setNoNetLayout() {
        if (!InternetUtils.internet(activity)) {
            titleBarLayout.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) recyclerViewLayout.getLayoutParams();
            layoutParams.bottomMargin = 0;
            recyclerViewLayout.setLayoutParams(layoutParams);
            viewHolder.intoCategoryLayout.setVisibility(View.GONE);
            viewHolder.commentLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            viewHolder.likeLine.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEndRecommend(ReadContinue readContinue) {
        if (readContinue.book.book_id==mBookId) {
            activity_Book_info_start_read.setText(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_goon_read));
        }
    }

    /**
     * 刷新评论
     *
     * @param refreshBookInfo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(CommentRefresh refreshBookInfo) {
        if (refreshBookInfo.productType == Constant.BOOK_CONSTANT && mBookId == refreshBookInfo.id) {
            current_page = 2;
            initData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshBookInfo refreshBookInfo) {
        if (mBook != null && mBook.name != null) {
            if (refreshBookInfo.book.book_id == mBookId) {
                if (refreshBookInfo.isSave) {
                    if (mBook.is_read != 1 && refreshBookInfo.book.is_read == 1) {
                        activity_Book_info_start_read.setText(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_goon_read));
                    }
                    mBook.is_collect = refreshBookInfo.book.is_collect;
                    if (mBook.is_collect == 1) {
                        activity_Book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
                        activity_Book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.maincolor2));
                        activity_Book_info_add_shelf.setEnabled(false);
                    }
                    mBook.setCurrent_chapter_id(refreshBookInfo.book.getCurrent_chapter_id());
                    mBook.setChapter_text(refreshBookInfo.book.getChapter_text());
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshBookSelf(RefreshShelfCurrent s) {
        if (mBook != null && mBook.name != null) {
            if (s.productType == Constant.BOOK_CONSTANT) {
                if (s.book != null && mBook.book_id == s.book.book_id) {
                    mBook = s.book;
                }
            }
        }
    }

    /**
     * 阅读器返回界面监听状态栏状态
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshBookSelfBar(RefreshBookInfoStatusBar s) {
        if (onScrolled_y <= REFRESH_HEIGHT) {
            setStatusTextColor(false, activity);
        } else {
            ImmersionBar.with(activity).statusBarDarkFont(true).init();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onclickTwo = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (ShareUitls.getInt(activity, "check_status", 0) == 1) {
                if (LoaclMainActivity.activity == null) {
                    startActivity(new Intent(activity, LoaclMainActivity.class));
                }
            } else {
                if (!BWNApplication.applicationContext.isMainActivityStartUp()) {
                    startActivity(new Intent(activity, MainActivity.class));
                }
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class ViewHolder {

        @BindView(R.id.activity_Book_info_content_cover_bg)
        public ImageView activity_Book_info_content_cover_bg;
        @BindView(R.id.activity_bookinfo_head)
        public View activity_bookinfo_head;

        @BindView(R.id.activity_Book_info_content_name)
        public TextView activity_Book_info_content_name;
        @BindView(R.id.activity_Book_info_content_author)
        public TextView activity_Book_info_content_author;
        @BindView(R.id.activity_Book_info_content_display_label)
        public TextView activity_Book_info_content_display_label;
        @BindView(R.id.activity_Book_info_content_total_comment)
        public TextView activity_Book_info_content_total_comment;
        @BindView(R.id.activity_Book_info_content_total_shoucanshu)
        public TextView activity_Book_info_content_total_shoucanshu;
        @BindView(R.id.activity_Book_info_content_cover)
        public ImageView activity_Book_info_content_cover;
        @BindView(R.id.activity_Book_info_content_cover_ShadowLayout)
        public View activity_Book_info_content_cover_ShadowLayout;

        @BindView(R.id.activity_Book_info_content_description)
        public AutoTextView activity_Book_info_content_description;
        @BindView(R.id.activity_Book_info_content_last_chapter_time)
        public TextView activity_Book_info_content_last_chapter_time;
        @BindView(R.id.activity_Book_info_content_last_chapter)
        public TextView activity_Book_info_content_last_chapter;
        @BindView(R.id.activity_Book_info_content_comment_container)
        public RecyclerView activity_Book_info_content_comment_container;
        @BindView(R.id.activity_Book_info_content_add_comment)
        public TextView activity_Book_info_content_add_comment;
        @BindView(R.id.activity_Book_info_tag)
        LinearLayout activity_Book_info_tag;

        @BindView(R.id.activity_Book_info_content_category_layout)
        RelativeLayout intoCategoryLayout;
        @BindView(R.id.activity_Book_info_comment_layout)
        LinearLayout commentLayout;
        @BindView(R.id.list_ad_view_layout)
        FrameLayout activity_Book_info_ad;
        @BindView(R.id.activity_book_info_lookallcomment)
        TextView activity_book_info_lookallcomment;
        @BindView(R.id.activity_book_info_like_line)
        View likeLine;

        @OnClick(value = {R.id.activity_Book_info_content_category_layout, R.id.activity_book_info_lookallcomment,
                R.id.activity_Book_info_content_add_comment})
        public void getEvent(View view) {
            if (mBook != null && mBook.name != null) {
                if (view.getId() == R.id.activity_Book_info_content_category_layout) {
                    if (!onclickTwo) {
                        onclickTwo = true;
                        Intent intent = new Intent(activity, BookCatalogMarkActivity.class);
                        intent.putExtra("book", mBook);
                        startActivity(intent);
                        onclickTwo = false;
                    }
                } else {
                    Intent intent = new Intent(activity, CommentActivity.class);
                    intent.putExtra("current_id", mBookId);
                    intent.putExtra("productType", Constant.BOOK_CONSTANT);
                    startActivity(intent);
                }
            }
        }

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
