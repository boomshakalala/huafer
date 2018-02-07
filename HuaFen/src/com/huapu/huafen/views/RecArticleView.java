package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.huapu.huafen.R;

/**
 * Created by qwe on 2017/10/17.
 */

public class RecArticleView extends LinearLayout {
    private  Context context;
    public RecArticleView(Context context) {
        this(context,null);
    }

    public RecArticleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }


    public RecArticleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        context = getContext();
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.rec_article, this, true);
    }

}
