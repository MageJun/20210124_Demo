package com.dushuge.controller.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dushuge.controller.ui.utils.ImageUtil;
import com.dushuge.controller.utils.ScreenSizeUtils;

/**
 * 圆角帧布局
 * @author admin
 * @version 2019/12/12
 */
public class RoundFrameLayout extends FrameLayout {

    // 全部角度
    private int radius;
    // 默认角度
    private int defaultRadius = 10;
    // 宽高
    private float width, height;

    public RoundFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 开启硬件加速
       /* setLayerType(View.LAYER_TYPE_HARDWARE, null);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }*/
        init(context, attrs);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        radius= ImageUtil.dp2px(getContext(),10);
       // TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundFrameLayout);
        //radius = array.getDimensionPixelOffset(R.styleable.RoundFrameLayout_radius, defaultRadius);
        //array.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = ScreenSizeUtils.getInstance(getContext()).getScreenWidth()-ImageUtil.dp2px(getContext(),30);
        height = width*4/5;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        // 绘制
        Path path = new Path();
        // 右上
        path.moveTo(radius, 0);
        path.lineTo(width - radius, 0);
        path.quadTo(width, 0, width, radius);
        // 右下
        path.lineTo(width, height - radius);
        path.quadTo(width, height, width - radius, height);
        // 左下
        path.lineTo(radius, height);
        path.quadTo(0, height, 0, height - radius);
        // 左上
        path.lineTo(0, radius);
        path.quadTo(0, 0, radius, 0);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
