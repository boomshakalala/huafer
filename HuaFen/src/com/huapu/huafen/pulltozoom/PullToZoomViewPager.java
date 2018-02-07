package com.huapu.huafen.pulltozoom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin on 2017/3/10.
 */

public class PullToZoomViewPager extends PullToZoomBase<ViewPager> {


    public PullToZoomViewPager(Context context) {
        super(context);
    }

    public PullToZoomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void handleStyledAttributes(TypedArray a) {

    }

    @Override
    protected void pullHeaderToZoom(int newScrollValue) {

    }

    @Override
    public void setHeaderView(View headerView) {

    }

    @Override
    public void setZoomView(View zoomView) {

    }

    @Override
    protected ViewPager createRootView(Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    protected void smoothScrollToTop() {

    }

    @Override
    protected boolean isReadyForPullStart() {
        return false;
    }
}
