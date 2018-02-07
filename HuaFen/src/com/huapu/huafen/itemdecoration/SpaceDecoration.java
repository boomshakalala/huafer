package com.huapu.huafen.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

import com.huapu.huafen.utils.CommonUtils;

public class SpaceDecoration extends ItemDecoration {
	private int space;
    private int count;

    public SpaceDecoration(int space, int count) {
        this.space = space;
        this.count = count;
    }  

    @Override  
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {  
    	//不是第一个的格子都设一个左边和底部的间距
    	outRect.left = CommonUtils.dp2px(space);
        outRect.bottom = CommonUtils.dp2px(space);
        int a = parent.getChildLayoutPosition(view);
        if (parent.getChildLayoutPosition(view) % (count + 1) == 0 || parent.getChildLayoutPosition(view) == 1) {
            outRect.left = 0;
        }

    }
}
