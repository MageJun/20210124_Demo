package com.dushuge.controller.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dushuge.controller.R;
import com.dushuge.controller.base.BaseFragment;
import com.dushuge.controller.constant.Constant;
import com.dushuge.controller.model.BaseAd;
import com.dushuge.controller.model.BaseBookComic;
import com.dushuge.controller.model.BaseLabelBean;
import com.dushuge.controller.model.Comment;
import com.dushuge.controller.ui.activity.CommentActivity;
import com.dushuge.controller.ui.adapter.CommentAdapter;
import com.dushuge.controller.ui.adapter.PublicMainAdapter;
import com.dushuge.controller.ui.utils.MyShape;
import com.dushuge.controller.ui.view.AutoTextView;
import com.dushuge.controller.ui.view.screcyclerview.SCOnItemClickListener;
import com.dushuge.controller.utils.LanguageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ComicinfoCommentFragment extends BaseFragment {

    @BindView(R.id.activity_book_info_content_comment_container)
    public RecyclerView fragment_comment_rcy;
    @BindView(R.id.activity_book_info_content_comment_des)
    public AutoTextView activity_book_info_content_comment_des;

    @BindView(R.id.activity_book_info_content_label_line)
    View likeLine;
    @BindView(R.id.activity_book_info_content_label_container)
    public RecyclerView fragment_like_rcy;

    @BindView(R.id.list_ad_view_layout)
    FrameLayout list_ad_view_layout;

    @BindView(R.id.activity_Book_info_content_comment_more_text)
    TextView activity_Book_info_content_comment_more_text;
    @BindView(R.id.activity_book_info_content_add_comment)
    public TextView activity_book_info_content_add_comment;

    private BaseBookComic baseComic;
    private List<BaseLabelBean> labelBeans = new ArrayList<>();

    @Override
    public int initContentView() {
        return R.layout.fragment_comicinfo_comment;
    }

    @Override
    public void initView() {
    }

    public ComicinfoCommentFragment() {
    }

    @OnClick({R.id.activity_book_info_content_add_comment, R.id.activity_Book_info_content_comment_more_text})
    public void onCommentClick(View view) {
        intentComment(null);
    }

    @Override
    public void initData() {
        activity_Book_info_content_comment_more_text.setBackground(MyShape.setMyshapeStroke2(activity,
                20, 2, ContextCompat.getColor(activity, R.color.maincolor), 0));
    }

    @Override
    public void initInfo(String json) {

    }

    /**
     * 设置数据
     *
     * @param baseComic
     * @param bookInfoComments
     * @param stroreComicLable
     * @param baseAd
     * @param total_comment
     */
    public void senddata(BaseBookComic baseComic, List<Comment> bookInfoComments,
                         BaseLabelBean stroreComicLable, BaseAd baseAd, int total_comment) {
        if (stroreComicLable != null) {
            labelBeans.clear();
            labelBeans.add(stroreComicLable);
        }
        likeLine.setVisibility(labelBeans.isEmpty() ? View.GONE : View.VISIBLE);
        this.baseComic = baseComic;
        activity_book_info_content_comment_des.setAutoText(baseComic.description, null);
        if (baseAd != null) {
            list_ad_view_layout.setVisibility(View.VISIBLE);
            baseAd.setAd(activity, list_ad_view_layout, 1);
        } else {
            list_ad_view_layout.setVisibility(View.GONE);
        }
        setComment(bookInfoComments, total_comment);
    }

    /**
     * 更新评论数据
     *
     * @param bookInfoComments
     * @param size
     */
    public void setComment(List<Comment> bookInfoComments, int size) {
        if (bookInfoComments != null && !bookInfoComments.isEmpty()) {
            CommentAdapter commentAdapter = new CommentAdapter(activity, bookInfoComments, new SCOnItemClickListener<Comment>() {
                @Override
                public void OnItemClickListener(int flag, int position, Comment comment) {
                    intentComment(comment);
                }

                @Override
                public void OnItemLongClickListener(int flag, int position, Comment O) {

                }
            });
            fragment_comment_rcy.setLayoutManager(new LinearLayoutManager(activity));
            fragment_comment_rcy.setAdapter(commentAdapter);
        }
        if (!activity_Book_info_content_comment_more_text.isShown()) {
            activity_Book_info_content_comment_more_text.setVisibility(View.VISIBLE);
        }
        if (size > 0) {
            activity_Book_info_content_comment_more_text.setText(String.format(
                    LanguageUtil.getString(activity, R.string.CommentListActivity_lookpinglun), size));
        } else {
            activity_Book_info_content_comment_more_text.setText(
                    LanguageUtil.getString(activity, R.string.CommentListActivity_no_pinglun));
        }
        fragment_like_rcy.setLayoutManager(new LinearLayoutManager(activity));
        PublicMainAdapter bookStoareAdapter = new PublicMainAdapter(labelBeans, 1, activity, false, false);
        fragment_like_rcy.setAdapter(bookStoareAdapter);
    }

    /**
     * 打开评论界面
     *
     * @param comment
     */
    private void intentComment(Comment comment) {
        Intent intent = new Intent(activity, CommentActivity.class);
        if (comment != null) {
            intent.putExtra("comment", comment);
        }
        intent.putExtra("current_id", baseComic.comic_id);
        intent.putExtra("productType", Constant.COMIC_CONSTANT);
        startActivity(intent);
    }
}
