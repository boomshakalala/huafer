package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.huapu.huafen.R;

/**
 * Created by qwe on 2017/5/20.
 */

public class MoneyLabelImage extends FrameLayout {

    private Context context;

    public MoneyLabelImage(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public MoneyLabelImage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public MoneyLabelImage(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(
                R.layout.layout_label_image, this, true);
    }
}
