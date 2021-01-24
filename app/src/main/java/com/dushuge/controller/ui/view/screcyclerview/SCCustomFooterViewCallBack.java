package com.dushuge.controller.ui.view.screcyclerview;

import android.view.View;

public  interface SCCustomFooterViewCallBack {

    void onLoadingMore(View yourFooterView);

    void onLoadMoreComplete(View yourFooterView);

    void onSetNoMore(View yourFooterView, boolean noMore);
}