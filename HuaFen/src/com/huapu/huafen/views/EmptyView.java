package com.huapu.huafen.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 空视图
 * Created by dengbin
 */
public class EmptyView extends LinearLayout {
    @BindView(R.id.empty_iv)
    SimpleDraweeView img;
    @BindView(R.id.empty_tv)
    TextView tv;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.view_empty, this, true);
        ButterKnife.bind(this);

        if (attrs == null)
            return;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.empty_view);
        String text = typedArray.getString(R.styleable.empty_view_empty_tv);
        int bg = typedArray.getResourceId(R.styleable.empty_view_empty_iv, R.drawable.default_pic);

        tv.setText(text);
        ImageLoader.loadImage(img, MyConstants.RES + bg);
    }

}
