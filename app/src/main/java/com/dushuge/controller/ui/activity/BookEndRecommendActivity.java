package com.dushuge.controller.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.umeng.socialize.UMShareAPI;
import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseActivity;
import com.dushuge.controller.constant.Api;
import com.dushuge.controller.constant.Constant;
import com.dushuge.controller.eventbus.BookEndRecommendRefresh;
import com.dushuge.controller.eventbus.ToStore;
import com.dushuge.controller.model.BaseLabelBean;
import com.dushuge.controller.model.Book;
import com.dushuge.controller.model.BookEndRecommendBean;
import com.dushuge.controller.net.HttpUtils;
import com.dushuge.controller.net.ReaderParams;
import com.dushuge.controller.ui.adapter.PublicMainAdapter;
import com.dushuge.controller.ui.dialog.BookReadDialogFragment;
import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.ui.view.screcyclerview.SCRecyclerView;
import com.dushuge.controller.utils.LanguageUtil;
import com.dushuge.controller.utils.MyShare;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 书籍末尾推荐
 */
public class BookEndRecommendActivity extends BaseActivity {

    @BindViews({R.id.book_end_recommend_toolbar_title, R.id.book_end_recommend_toolbar_status, R.id.book_end_recommend_toolbar_dec})
    List<TextView> topTextViews;
    @BindView(R.id.book_end_recommend_goto_comment_count)
    TextView commentCounts;

    @BindView(R.id.book_end_recommend_goto_reward_text)
    TextView book_end_recommend_goto_reward_text;

    @BindView(R.id.book_end_recommend_recyclerView)
    SCRecyclerView recyclerView;
    @BindViews({R.id.book_end_recommend_goto_reward_layout, R.id.book_end_recommend_goto_share_layout})
    List<LinearLayout> layouts;
    @BindViews({R.id.book_end_recommend_goto_reward_line, R.id.book_end_recommend_goto_share_line})
    List<View> lines;

    private Book mBook;
    private PublicMainAdapter bookStoreAdapter;
    private List<BaseLabelBean> baseLabelBeans;

    @Override
    public int initContentView() {
        USE_EventBus = true;
        FULL_CCREEN = true;
        return R.layout.activity_book_end_recommend;
    }

    @Override
    public void initView() {
        if (Constant.getRewardSwitch(activity) != 1 && Constant.getMonthlyTicket(activity) != 1) {
            layouts.get(0).setVisibility(View.GONE);
            lines.get(0).setVisibility(View.GONE);
        } else {
            if (Constant.getRewardSwitch(activity) != 1) {
                book_end_recommend_goto_reward_text.setText(LanguageUtil.getString(activity, R.string.dialog_Monthly_pass));
            }
        }
        if (!Constant.USE_SHARE) {
            layouts.get(1).setVisibility(View.GONE);
            layouts.get(1).setVisibility(View.GONE);
        }
        baseLabelBeans = new ArrayList<>();
        mBook = (Book) formIntent.getSerializableExtra("book");
        initSCRecyclerView(recyclerView, RecyclerView.VERTICAL, 0);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);
        bookStoreAdapter = new PublicMainAdapter(baseLabelBeans, Constant.BOOK_CONSTANT, activity, true, false);
        recyclerView.setAdapter(bookStoreAdapter);
        if (mBook != null) {
            bookStoreAdapter.setBookEndBookId(mBook.book_id);
            topTextViews.get(0).setText(mBook.name);
        }
        commentCounts.setBackground(MyShape.setMyshape(ImageUtil.dp2px(activity, 10),
                ContextCompat.getColor(activity, R.color.red)));
    }

    @OnClick({R.id.book_end_recommend_toolbar_back_layout, R.id.book_end_recommend_toolbar_store,
            R.id.book_end_recommend_goto_comment_layout, R.id.book_end_recommend_goto_reward_layout, R.id.book_end_recommend_goto_share_layout})
    public void onEndRecommendClick(View view) {
        switch (view.getId()) {
            case R.id.book_end_recommend_toolbar_back_layout:
                finish();
                break;
            case R.id.book_end_recommend_toolbar_store:
                if (!Constant.getProductTypeList(activity).isEmpty()) {
                    int index = Integer.parseInt(Constant.getProductTypeList(activity).get(0)) - 1;
                    EventBus.getDefault().post(new ToStore(index));
                    EventBus.getDefault().post(new BookEndRecommendRefresh(true, true));
                    finish();
                }
                break;
            case R.id.book_end_recommend_goto_comment_layout:
                if (mBook != null) {
                    Intent intentComment = new Intent(this, CommentActivity.class);
                    intentComment.putExtra("current_id", mBook.book_id);
                    intentComment.putExtra("productType", Constant.BOOK_CONSTANT);
                    startActivityForResult(intentComment, 111);
                }
                break;
            case R.id.book_end_recommend_goto_reward_layout:
                BookReadDialogFragment bookReadDialogFragment = new BookReadDialogFragment(activity, mBook.book_id, true);
                bookReadDialogFragment.show(getSupportFragmentManager(), "BookReadDialogFragment");
                break;
            case R.id.book_end_recommend_goto_share_layout:
                if (mBook != null) {
                    new MyShare(activity)
                            .setFlag(Constant.BOOK_CONSTANT)
                            .setId(mBook.getBook_id())
                            .Share();
                }
                break;
        }
    }

    @Override
    public void initData() {
        if (mBook != null) {
            readerParams = new ReaderParams(activity);
            readerParams.putExtraParams("book_id", mBook.book_id);
            httpUtils = HttpUtils.getInstance();
            httpUtils.sendRequestRequestParams(activity, Api.READ_END_RECOMMEND, readerParams.generateParamsJson(), responseListener);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initInfo(String json) {
        if (!TextUtils.isEmpty(json)) {
            BookEndRecommendBean bookEndRecommendBean = HttpUtils.getGson().fromJson(json, BookEndRecommendBean.class);
            if (bookEndRecommendBean != null) {
                topTextViews.get(1).setText(bookEndRecommendBean.getTitle());
                topTextViews.get(2).setText(bookEndRecommendBean.getDesc());
                if (bookEndRecommendBean.getComment_num() > 0) {
                    commentCounts.setVisibility(View.VISIBLE);
                    commentCounts.setText(bookEndRecommendBean.getComment_num() + (bookEndRecommendBean.getComment_num() > 99 ? "+" : ""));
                } else {
                    commentCounts.setVisibility(View.GONE);
                }
                baseLabelBeans.add(bookEndRecommendBean.getGuess_like());
                bookStoreAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNullRefresh() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (data != null) {
                int count = data.getIntExtra("comment_count", 0);
                if (count > 0) {
                    commentCounts.setVisibility(View.VISIBLE);
                    commentCounts.setText(count + (count > 99 ? "+" : ""));
                } else {
                    commentCounts.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().post(new BookEndRecommendRefresh(false, false));
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0, R.anim.activity_left_right_close);
    }
}
