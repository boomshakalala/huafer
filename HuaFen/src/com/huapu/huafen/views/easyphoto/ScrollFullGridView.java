package com.huapu.huafen.views.easyphoto;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by qwe on 2017/4/26.
 */

public class ScrollFullGridView extends GridView {
    public ScrollFullGridView(Context context) {
        super(context);
    }

    public ScrollFullGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollFullGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
