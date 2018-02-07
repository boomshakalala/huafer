package com.huapu.huafen.views;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin on 2016/12/16.
 */
public class HCoordinatorLayout extends CoordinatorLayout {

    public HCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HCoordinatorLayout(Context context) {
        super(context);
    }

    public HCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if(onScrollListener!=null){
            onScrollListener.onScroll(target,dxConsumed,dyConsumed,dxUnconsumed,dyUnconsumed);
        }
    }

    public interface OnScrollListener{
        void onScroll(View target, int dxConsumed, int dyConsumed,
                      int dxUnconsumed, int dyUnconsumed);
    }

    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener){
        this.onScrollListener = onScrollListener;
    }
}
