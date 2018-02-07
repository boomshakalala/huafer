package com.huapu.huafen.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

import com.huapu.huafen.utils.CommonUtils;

public class SpaceItemDecoration extends ItemDecoration {

	private int space;  
	  
    public SpaceItemDecoration(int spaceDp) {
        this.space = spaceDp;
    }  

    @Override  
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {  

    	outRect.left = CommonUtils.dp2px(space);
        outRect.bottom = CommonUtils.dp2px(space);
        if (parent.getChildLayoutPosition(view) %2!=0) {
            outRect.right = CommonUtils.dp2px(space);
        }

        if(parent.getChildLayoutPosition(view)<2){
            outRect.top = CommonUtils.dp2px(space);
        }

    }  
}
