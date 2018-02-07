package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.huapu.huafen.R;

/**
 * Created by admin on 2017/1/12.
 */

public class SearchView extends LinearLayout{
    private int maxWidth;
    private int offsetRight;

    public SearchView(Context context) {
       this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.search_view_layout,this,true);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    public void setOffsetRight(int offsetRight){
        this.offsetRight = offsetRight;
    }

    public void setSize(float size){
        int currentWidth = getWidth();
        if(maxWidth < currentWidth){
            //获取最大宽度，为了避免改变宽度之后，无法还原
            maxWidth = currentWidth;
        }
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = Math.round(maxWidth * size);
        setLayoutParams(params);
        int startX = maxWidth - params.width;
        if(startX > maxWidth - offsetRight){
            //设置偏移量，与末尾的圆圈中心对齐
            startX = maxWidth - offsetRight;
        }
        setX(startX);
    }
}
