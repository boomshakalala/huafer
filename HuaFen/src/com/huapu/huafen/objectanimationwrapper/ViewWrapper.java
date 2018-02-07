package com.huapu.huafen.objectanimationwrapper;

import android.view.View;

/**
 * Created by admin on 2017/1/12.
 */

public class ViewWrapper {

    public static final String WIDTH = "width";
    private View target;

    public ViewWrapper(View target) {
        this.target = target;
    }

    public int getWidth() {
        return target.getLayoutParams().width;
    }

    public void setWidth(int width) {
        target.getLayoutParams().width = width;
        target.requestLayout();
    }



}
