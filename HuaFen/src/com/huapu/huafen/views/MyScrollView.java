package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

    ScrollViewListener scrollListener;

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (null != scrollListener) {
            scrollListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public ScrollViewListener getScrollListener() {
        return scrollListener;
    }

    public void setScrollListener(ScrollViewListener scrollListener) {
        this.scrollListener = scrollListener;
    }


    public interface ScrollViewListener {

        void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy);

    }
}
