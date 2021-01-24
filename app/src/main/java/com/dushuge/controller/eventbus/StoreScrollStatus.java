package com.dushuge.controller.eventbus;

import android.widget.TextView;

/**
 * 用于传递书城滑动
 */
public class StoreScrollStatus {

    public int productType;
    public int scrollY;
    public boolean status, isChangeBg;
    public TextView textView;

    public StoreScrollStatus(int productType, boolean isChange, int scrollY) {
        this.productType = productType;
        this.scrollY = scrollY;
        this.isChangeBg = isChange;
    }

    public StoreScrollStatus(int productType, boolean status, TextView textView) {
        this.productType = productType;
        this.status = status;
        this.textView = textView;
    }

    public interface StoreScrollStatusInterface {

        void StoreScrollStatusListener(StoreScrollStatus storeScrollStatus);
    }
}
