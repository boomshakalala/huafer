package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.huapu.huafen.utils.CommonUtils;

/**
 * Created by mac on 2018/1/3.
 */

public class MaxHeightListView extends ListView {

    private int maxHeight = CommonUtils.getScreenHeight()/2;

    public MaxHeightListView(Context context) {
        super(context);
    }

    public MaxHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxHeight > -1){
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight,
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
