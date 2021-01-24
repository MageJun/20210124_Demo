package com.dushuge.controller.ui.view.screcyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.dushuge.controller.R;
import com.dushuge.controller.eventbus.StoreScrollStatus;
import com.dushuge.controller.ui.utils.MyToash;
import com.dushuge.controller.ui.view.comiclookview.SComicRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SCRecyclerView extends RecyclerView {

    public SCRecyclerView(@NonNull Context context) {
        super(context);
    }

    public SCRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SCRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean isLoadingData = false;
    private boolean isNoMore = false;
    private int mRefreshProgressStyle = SCProgressStyle.SysProgress;
    private int mLoadingMoreProgressStyle = SCProgressStyle.SysProgress;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private WrapAdapter mWrapAdapter;
    private float mLastY = -1;
    private static final float DRAG_RATE = 3;
    private SCCustomFooterViewCallBack footerViewCallBack;
    private LoadingListener mLoadingListener;
    private SCArrowRefreshHeader mRefreshHeader;
    private boolean pullRefreshEnabled = true;
    private boolean loadingMoreEnabled = true;
    private boolean showFooterEnabled = true;//用来控制是否显示尾部.做分页处理
    //下面的ItemViewType是保留值(ReservedItemViewType),如果用户的adapter与它们重复将会强制抛出异常。不过为了简化,我们检测到重复时对用户的提示是ItemViewType必须小于10000
    private static final int TYPE_REFRESH_HEADER = 10007;//设置一个很大的数字,尽可能避免和用户的adapter冲突
    private static final int TYPE_FOOTER = 10008;
    private static final int HEADER_INIT_INDEX = 10009;
    private static final int TYPE_FOOTER_MYSHELF = 10010;//自定义的尾部
    private static List<Integer> sHeaderTypes = new ArrayList<>();//每个header必须有不同的type,不然滚动的时候顺序会变化

    //adapter没有数据的时候显示,类似于listView的emptyView
    private View mEmptyView;
    private View mFootView, mFootViewsMyShelf;
    private final RecyclerView.AdapterDataObserver mDataObserver = new DataObserver();
    private SCAppBarStateChangeListener.State appbarState = SCAppBarStateChangeListener.State.EXPANDED;

    // limit number to call load more
    // 控制多出多少条的时候调用 onLoadMore
    private int limitNumberToCallLoadMore = 1;

    private void init() {
        if (pullRefreshEnabled) {
            mRefreshHeader = new SCArrowRefreshHeader(getContext());
            mRefreshHeader.setProgressStyle(mRefreshProgressStyle);
        }
        SCLoadingMoreFooter footView = new SCLoadingMoreFooter(getContext());
        footView.setProgressStyle(mLoadingMoreProgressStyle);
        mFootView = footView;
        mFootView.setVisibility(GONE);
    }

    /**
     * call it when you finish the activity,
     * when you call this,better don't call some kind of functions like
     * RefreshHeader,because the reference of mHeaderViews is NULL.
     */
    public void destroy() {
        if (mHeaderViews != null) {
            mHeaderViews.clear();
            mHeaderViews = null;
        }
        if (mFootViewsMyShelf != null) {
            mFootViewsMyShelf = null;
        }
        if (mFootView instanceof SCLoadingMoreFooter) {
            ((SCLoadingMoreFooter) mFootView).destroy();
            mFootView = null;
        }
        if (mRefreshHeader != null) {
            mRefreshHeader.destroy();
            mRefreshHeader = null;
        }
    }

    public SCArrowRefreshHeader getDefaultRefreshHeaderView() {
        if (mRefreshHeader == null) {
            return null;
        }
        return mRefreshHeader;
    }

    public SCLoadingMoreFooter getDefaultFootView() {
        if (mFootView == null) {
            return null;
        }
        if (mFootView instanceof SCLoadingMoreFooter) {
            return ((SCLoadingMoreFooter) mFootView);
        }
        return null;
    }

    // set the number to control call load more,see the demo on linearActivity
    public void setLimitNumberToCallLoadMore(int limitNumberToCallLoadMore) {
        this.limitNumberToCallLoadMore = limitNumberToCallLoadMore;
    }

    public View getFootView() {
        return mFootView;
    }

    public void setFootViewText(String loading, String noMore) {
        if (mFootView instanceof SCLoadingMoreFooter) {
            ((SCLoadingMoreFooter) mFootView).setLoadingHint(loading);
            ((SCLoadingMoreFooter) mFootView).setNoMoreHint(noMore);
        }
    }

    public void addHeaderView(View view) {
        if (mHeaderViews == null || sHeaderTypes == null)
            return;
        sHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size());
        mHeaderViews.add(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void addFootView(View view, boolean showFooterEnabled) {
        mFootViewsMyShelf = view;
        setShowFooterEnabled(showFooterEnabled);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void removeFootView() {
        if (mFootViewsMyShelf != null) {
            mFootViewsMyShelf = null;
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }
    }


    public void removeHeaderView(View view) {
        if (mHeaderViews == null || sHeaderTypes == null)
            return;
        sHeaderTypes.remove(new Integer(HEADER_INIT_INDEX + mHeaderViews.size()));
        mHeaderViews.remove(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    //根据header的ViewType判断是哪个header
    private View getHeaderViewByType(int itemType) {
        if (!isHeaderType(itemType)) {
            return null;
        }
        if (mHeaderViews == null)
            return null;
        return mHeaderViews.get(itemType - HEADER_INIT_INDEX);
    }

    //判断一个type是否为HeaderType
    private boolean isHeaderType(int itemViewType) {
        if (mHeaderViews == null || sHeaderTypes == null)
            return false;
        return mHeaderViews.size() > 0 && sHeaderTypes.contains(itemViewType);
    }

    //判断是否是SCRecyclerView保留的itemViewType
    private boolean isReservedItemViewType(int itemViewType) {
        if (itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_FOOTER_MYSHELF || itemViewType == TYPE_FOOTER || sHeaderTypes.contains(itemViewType)) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("all")
    public void setFootView(@NonNull final View view, @NonNull SCCustomFooterViewCallBack footerViewCallBack) {
        if (view == null || footerViewCallBack == null) {
            return;
        }
        mFootView = view;
        this.footerViewCallBack = footerViewCallBack;
    }

    public void loadMoreComplete() {
        isLoadingData = false;
        if (mFootView instanceof SCLoadingMoreFooter) {
            ((SCLoadingMoreFooter) mFootView).setState(SCLoadingMoreFooter.STATE_COMPLETE);
        } else {
            if (footerViewCallBack != null) {
                footerViewCallBack.onLoadMoreComplete(mFootView);
            }
        }
    }

    public void setNoMore(boolean noMore) {
        isLoadingData = false;
        isNoMore = noMore;
        if (mFootView instanceof SCLoadingMoreFooter) {
            ((SCLoadingMoreFooter) mFootView).setState(isNoMore ? SCLoadingMoreFooter.STATE_NOMORE : SCLoadingMoreFooter.STATE_COMPLETE);
        } else {
            if (footerViewCallBack != null) {
                footerViewCallBack.onSetNoMore(mFootView, noMore);
            }
        }
    }

    public void refresh() {
        if (pullRefreshEnabled && mLoadingListener != null) {
            mRefreshHeader.setState(SCArrowRefreshHeader.STATE_REFRESHING);
            mLoadingListener.onRefresh();
        }
    }

    public void reset() {
        setNoMore(false);
        loadMoreComplete();
        refreshComplete();
    }

    public void refreshComplete() {
        if (mRefreshHeader != null)
            mRefreshHeader.refreshComplete();
        setNoMore(false);
    }

    public void setRefreshHeader(SCArrowRefreshHeader refreshHeader) {
        mRefreshHeader = refreshHeader;
    }

    public boolean isShowFooterEnabled() {
        return showFooterEnabled;
    }

    public void setShowFooterEnabled(boolean showFooterEnabled) {//自定义的尾部是否有展示 调用后需要手动刷新适配器否则不生效
        this.showFooterEnabled = showFooterEnabled;
    }

    public void setPullRefreshEnabled(boolean enabled) {
        pullRefreshEnabled = enabled;
    }

    public void setLoadingMoreEnabled(boolean enabled) {
        if (mFootViewsMyShelf != null) {
            showFooterEnabled = !enabled;
        }
        loadingMoreEnabled = enabled;
        if (!enabled) {
            if (mFootView instanceof SCLoadingMoreFooter) {
                ((SCLoadingMoreFooter) mFootView).setState(SCLoadingMoreFooter.STATE_COMPLETE);
            }
        }
    }

    public void setRefreshProgressStyle(int style) {
        mRefreshProgressStyle = style;
        if (mRefreshHeader != null) {
            mRefreshHeader.setProgressStyle(style);
        }
    }

    public void setLoadingMoreProgressStyle(int style) {
        mLoadingMoreProgressStyle = style;
        if (mFootView instanceof SCLoadingMoreFooter) {
            ((SCLoadingMoreFooter) mFootView).setProgressStyle(style);
        }
    }

    public void setArrowImageView(int resId) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setArrowImageView(resId);
        }
    }

    // if you can't sure that you are 100% going to
    // have no data load back from server anymore,do not use this
    @Deprecated
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        mDataObserver.onChanged();
    }

    public View getEmptyView() {
        return mEmptyView;
    }


    public void setAdapter(Adapter adapter, boolean showNomore) {//此方法将展示  没有更多了的尾部
        View view=LayoutInflater.from(getContext()).inflate(R.layout.public_recycleview_maxcountfoot, null);
        addFootView(view, false);
        setAdapter(adapter);
    }
    public void setAdapter(Adapter adapter, boolean showNomore,int color) {//此方法将展示  没有更多了的尾部
        View view=LayoutInflater.from(getContext()).inflate(R.layout.public_recycleview_maxcountfoot, null);
        view.setBackgroundColor(color);
        addFootView(view, false);
        setAdapter(adapter);
    }
    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    //避免用户自己调用getAdapter() 引起的ClassCastException
    @Override
    public Adapter getAdapter() {
        if (mWrapAdapter != null)
            return mWrapAdapter.getOriginalAdapter();
        else
            return null;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (mWrapAdapter != null) {
            if (layout instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) layout);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (mWrapAdapter.isHeader(position) || mWrapAdapter.isFooterMyShelf(position) || mWrapAdapter.isFooter(position) || mWrapAdapter.isRefreshHeader(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }

    /**
     * ===================== try to adjust the position for XR when you call those functions below ======================
     */
    // which cause "Called attach on a child which is not detached" exception info.
    // {reason analyze @link:http://www.cnblogs.com/linguanh/p/5348510.html}
    // by lgh on 2017-11-13 23:55

    // example: listData.remove(position); You can also see a demo on LinearActivity
    public <T> void notifyItemRemoved(List<T> listData, int position) {
        if (mWrapAdapter.adapter == null)
            return;
        int headerSize = getHeaders_includingRefreshCount();
        int adjPos = position + headerSize;
        mWrapAdapter.adapter.notifyItemRemoved(adjPos);
        mWrapAdapter.adapter.notifyItemRangeChanged(headerSize, listData.size(), new Object());
    }

    public <T> void notifyItemInserted(List<T> listData, int position) {
        if (mWrapAdapter.adapter == null)
            return;
        int headerSize = getHeaders_includingRefreshCount();
        int adjPos = position + headerSize;
        mWrapAdapter.adapter.notifyItemInserted(adjPos);
        mWrapAdapter.adapter.notifyItemRangeChanged(headerSize, listData.size(), new Object());
    }

    public void notifyItemChanged(int position) {
        if (mWrapAdapter.adapter == null)
            return;
        int adjPos = position + getHeaders_includingRefreshCount();
        mWrapAdapter.adapter.notifyItemChanged(adjPos);
    }

    public void notifyItemChanged(int position, Object o) {
        if (mWrapAdapter.adapter == null)
            return;
        int adjPos = position + getHeaders_includingRefreshCount();
        mWrapAdapter.adapter.notifyItemChanged(adjPos, o);
    }

    public int getHeaders_includingRefreshCount() {
        return mWrapAdapter.getHeadersCount() + 1;
    }

    /**
     * ======================================================= end =======================================================
     */

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            int adjAdapterItemCount = layoutManager.getItemCount() + getHeaders_includingRefreshCount();
            Log.e("aaaaa", "adjAdapterItemCount " + adjAdapterItemCount + " getItemCount " + layoutManager.getItemCount());

            int status = 3;

            if (mRefreshHeader != null)
                status = mRefreshHeader.getState();
            if (
                    layoutManager.getChildCount() > 0
                            && lastVisibleItemPosition >= adjAdapterItemCount - limitNumberToCallLoadMore
                            && adjAdapterItemCount >= layoutManager.getChildCount()
                            && !isNoMore
                            && status < SCArrowRefreshHeader.STATE_REFRESHING
            ) {
                isLoadingData = true;
                if (mFootView instanceof SCLoadingMoreFooter) {
                    ((SCLoadingMoreFooter) mFootView).setState(SCLoadingMoreFooter.STATE_LOADING);
                } else {
                    if (footerViewCallBack != null) {
                        footerViewCallBack.onLoadingMore(mFootView);
                    }
                }
                mLoadingListener.onLoadMore();
            }
        }
    }

    public interface OnTouchEvent {
        void onTouchEvent(MotionEvent ev);
    }

    public OnTouchEvent onTouchEvent;
    public boolean NoonTouchEvent;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (NoonTouchEvent) {
            return false;
        }
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && pullRefreshEnabled && appbarState == SCAppBarStateChangeListener.State.EXPANDED) {
                    if (mRefreshHeader == null)
                        break;
                    mRefreshHeader.onMove(deltaY / DRAG_RATE);
                    if (storeScrollStatusInterface) {
                        EventBus.getDefault().post(new StoreScrollStatus(productType, false, mRefreshHeader.getVisibleHeight()));
                    }
                    if (mRefreshHeader.getVisibleHeight() > 0 && mRefreshHeader.getState() < SCArrowRefreshHeader.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled && appbarState == SCAppBarStateChangeListener.State.EXPANDED) {
                    if (mRefreshHeader != null && mRefreshHeader.releaseAction()) {
                        if (mLoadingListener != null) {
                            mLoadingListener.onRefresh();
                        }
                    } else {
                        if (storeScrollStatusInterface) {
                            EventBus.getDefault().post(new StoreScrollStatus(productType, false, 0));
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private boolean isOnTop() {
        if (mRefreshHeader == null)
            return false;
        if (mRefreshHeader.getParent() != null) {
            return true;
        } else {
            return false;
        }
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
            if (mWrapAdapter != null && mEmptyView != null) {
                int emptyCount = 1 + mWrapAdapter.getHeadersCount();
                if (loadingMoreEnabled) {
                    emptyCount++;
                }
                if (mWrapAdapter.getItemCount() == emptyCount) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    ;

    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter adapter;

        public WrapAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        public RecyclerView.Adapter getOriginalAdapter() {
            return this.adapter;
        }

        public boolean isHeader(int position) {
            if (mHeaderViews == null)
                return false;
            return position >= 1 && position < mHeaderViews.size() + 1;
        }

        public boolean isFooter(int position) {
            if (loadingMoreEnabled) {
                return position == getItemCount() - 1;
            } else {
                return false;
            }
        }

        public boolean isFooterMyShelf(int position) {
            if (mFootViewsMyShelf != null && showFooterEnabled) {
                if (loadingMoreEnabled) {
                    return position == getItemCount() - 2;
                }
                return position == getItemCount() - 1;
            } else {
                return false;
            }
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }

        public int getHeadersCount() {
            if (mHeaderViews == null)
                return 0;
            return mHeaderViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new WrapAdapter.SimpleViewHolder(mRefreshHeader);
            } else if (isHeaderType(viewType)) {
                return new WrapAdapter.SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (viewType == TYPE_FOOTER_MYSHELF) {
                return new WrapAdapter.SimpleViewHolder(mFootViewsMyShelf);
            } else if (viewType == TYPE_FOOTER) {
                return new WrapAdapter.SimpleViewHolder(mFootView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (isHeader(position) || isRefreshHeader(position)) {
                return;
            }
            int adjPosition = position - (getHeadersCount() + 1);
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        // some times we need to override this
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            if (isHeader(position) || isRefreshHeader(position)) {
                return;
            }

            int adjPosition = position - (getHeadersCount() + 1);
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    if (payloads.isEmpty()) {
                        adapter.onBindViewHolder(holder, adjPosition);
                    } else {
                        adapter.onBindViewHolder(holder, adjPosition, payloads);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            int count;
            int adjLen = (loadingMoreEnabled ? 2 : 1);
            if (adapter != null) {
                count = getHeadersCount() + adapter.getItemCount() + adjLen;
            } else {
                count = getHeadersCount() + adjLen;
            }
            if (mFootViewsMyShelf != null && showFooterEnabled) {
                count += 1;
            }
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            int adjPosition = position - (getHeadersCount() + 1);
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                position = position - 1;
                return sHeaderTypes.get(position);
            }
            if (isFooterMyShelf(position)) {
                return TYPE_FOOTER_MYSHELF;
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    int type = adapter.getItemViewType(adjPosition);
                    if (isReservedItemViewType(type)) {
                        throw new IllegalStateException("SCRecyclerView require itemViewType in adapter should be less than 10000 ");
                    }
                    return type;
                }
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount() + 1) {
                int adjPosition = position - (getHeadersCount() + 1);
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position) || isFooterMyShelf(position) || isRefreshHeader(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isRefreshHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()) || isFooterMyShelf(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {

        void onRefresh();

        void onLoadMore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //解决和CollapsingToolbarLayout冲突的问题
        AppBarLayout appBarLayout = null;
        ViewParent p = getParent();
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                break;
            }
            p = p.getParent();
        }
        if (p instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) p;
            final int childCount = coordinatorLayout.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if (child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout) child;
                    break;
                }
            }
            if (appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(new SCAppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        appbarState = state;
                    }
                });
            }
        }
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;
        private int mOrientation;

        /**
         * Sole constructor. Takes in a {@link Drawable} to be used as the interior
         * divider.
         *
         * @param divider A divider {@code Drawable} to be drawn on the RecyclerView
         */
        public DividerItemDecoration(Drawable divider) {
            mDivider = divider;
        }

        /**
         * Draws horizontal or vertical dividers onto the parent RecyclerView.
         *
         * @param canvas The {@link Canvas} onto which dividers will be drawn
         * @param parent The RecyclerView onto which dividers are being added
         * @param state  The current RecyclerView.State of the RecyclerView
         */
        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                drawHorizontalDividers(canvas, parent);
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                drawVerticalDividers(canvas, parent);
            }
        }

        /**
         * Determines the size and location of offsets between items in the parent
         * RecyclerView.
         *
         * @param outRect The {@link Rect} of offsets to be added around the child
         *                view
         * @param view    The child view to be decorated with an offset
         * @param parent  The RecyclerView onto which dividers are being added
         * @param state   The current RecyclerView.State of the RecyclerView
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) <= mWrapAdapter.getHeadersCount() + 1) {
                return;
            }
            mOrientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                outRect.left = mDivider.getIntrinsicWidth();
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                outRect.top = mDivider.getIntrinsicHeight();
            }
        }

        /**
         * Adds dividers to a RecyclerView with a LinearLayoutManager or its
         * subclass oriented horizontally.
         *
         * @param canvas The {@link Canvas} onto which horizontal dividers will be
         *               drawn
         * @param parent The RecyclerView onto which horizontal dividers are being
         *               added
         */
        private void drawHorizontalDividers(Canvas canvas, RecyclerView parent) {
            int parentTop = parent.getPaddingTop();
            int parentBottom = parent.getHeight() - parent.getPaddingBottom();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int parentLeft = child.getRight() + params.rightMargin;
                int parentRight = parentLeft + mDivider.getIntrinsicWidth();

                mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
                mDivider.draw(canvas);
            }
        }

        /**
         * Adds dividers to a RecyclerView with a LinearLayoutManager or its
         * subclass oriented vertically.
         *
         * @param canvas The {@link Canvas} onto which vertical dividers will be
         *               drawn
         * @param parent The RecyclerView onto which vertical dividers are being
         *               added
         */
        private void drawVerticalDividers(Canvas canvas, RecyclerView parent) {
            int parentLeft = parent.getPaddingLeft();
            int parentRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int parentTop = child.getBottom() + params.bottomMargin;
                int parentBottom = parentTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
                mDivider.draw(canvas);
            }
        }
    }

    /**
     * add by LinGuanHong below
     */
    private int scrollDyCounter = 0;

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
        /** if we scroll to position 0, the scrollDyCounter should be reset */
        if (position == 0) {
            scrollDyCounter = 0;
        }
    }

    /**
     * 用于监听书城界面滑动
     * 上滑、下滑
     */
    public boolean storeScrollStatusInterface = false;
    public int productType;
    private int onScrolled_y;

    public void setStoreScrollStatusInterface(boolean storeScrollStatusInterface, int productType) {
        this.storeScrollStatusInterface = storeScrollStatusInterface;
        this.productType = productType;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (onTouchEvent != null) {
            onTouchEvent.onTouchEvent(null);
        }
        if (storeScrollStatusInterface) {
            onScrolled_y += dy;
            if (onScrolled_y < 0) {
                onScrolled_y = 0;
            }
            EventBus.getDefault().post(new StoreScrollStatus(productType, true, onScrolled_y));
        }
        if (scrollViewListener != null) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
            if (linearLayoutManager != null) {
                scrollViewListener.onScroll(linearLayoutManager.findFirstCompletelyVisibleItemPosition(), linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastCompletelyVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition());
            }
        }

        if (scrollAlphaChangeListener == null) {
            return;
        }
        int height = scrollAlphaChangeListener.setLimitHeight();
        scrollDyCounter = scrollDyCounter + dy;
        if (scrollDyCounter <= 0) {
            scrollAlphaChangeListener.onAlphaChange(0);
        } else if (scrollDyCounter <= height && scrollDyCounter > 0) {
            float scale = (float) scrollDyCounter / height; /** 255/height = x/255 */
            float alpha = (255 * scale);
            scrollAlphaChangeListener.onAlphaChange((int) alpha);
        } else {
            scrollAlphaChangeListener.onAlphaChange(255);
        }
    }

    private ScrollAlphaChangeListener scrollAlphaChangeListener;

    public void setScrollAlphaChangeListener(
            ScrollAlphaChangeListener scrollAlphaChangeListener
    ) {
        this.scrollAlphaChangeListener = scrollAlphaChangeListener;
    }

    public interface ScrollAlphaChangeListener {
        void onAlphaChange(int alpha);

        /**
         * you can handle the alpha insert it
         */
        int setLimitHeight(); /** set a height for the begging of the alpha start to change */
    }


    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        addItemDecoration(decor, -1);
    }
    public interface ScrollViewListener {
        void onScroll(int FirstCompletelyVisibleItemPosition, int FirstVisibleItemPosition, int LastCompletelyVisibleItemPosition, int LastVisibleItemPosition);
    }

    private SComicRecyclerView.ScrollViewListener scrollViewListener;

    public void setScrollViewListener(SComicRecyclerView.ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

}