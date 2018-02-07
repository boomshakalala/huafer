package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.GridView;

public class ScrollGridView extends GridView {  
  
    public ScrollGridView(Context context) {  
        super(context);  
    }  
  
    public ScrollGridView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public ScrollGridView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        // TODO 自动生成的构造函数存根  
    }  
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        // TODO 自动生成的方法存根  
        int expandSpec = MeasureSpec.makeMeasureSpec(   
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);   
        super.onMeasure(widthMeasureSpec, expandSpec);   
    }  
}  
