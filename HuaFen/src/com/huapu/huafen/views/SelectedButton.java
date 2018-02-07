package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by admin on 2016/10/25.
 */
public class SelectedButton extends Button {
    private int index = 0;

    public SelectedButton(Context context) {
        super(context);
    }

    public SelectedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectedButton(Context context, AttributeSet attrs, String Text, int position) {
        super(context, attrs);
    }

    public SelectedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setIndex(int ind) {
        this.index = ind;
    }

    public int getIndex() {
        return this.index;
    }

    public int getId() {
        return index;
    }
}
