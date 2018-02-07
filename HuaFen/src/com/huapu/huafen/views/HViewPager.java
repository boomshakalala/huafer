package com.huapu.huafen.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by admin on 2016/11/3.
 */
public class HViewPager extends ViewPager {

    private boolean isCanFlip;

    public HViewPager(Context context) {
        super(context);
    }

    public HViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanFlip(boolean isCanFlip) {
        this.isCanFlip = isCanFlip;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isCanFlip && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isCanFlip && super.onInterceptTouchEvent(ev);
    }
}
