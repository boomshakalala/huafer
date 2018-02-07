package com.huapu.huafen.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huapu.huafen.utils.CommonUtils;

/**
 * Created by admin on 2017/5/18.
 */

public class SpacesDecoration  extends RecyclerView.ItemDecoration {


    private int space;

    public SpacesDecoration(int spaceDp) {
        this.space = spaceDp;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildLayoutPosition(view);

        if((position%2) == 0){
            outRect.left = CommonUtils.dp2px(space);
            outRect.right = CommonUtils.dp2px(space)/2;
        }else{
            outRect.right = CommonUtils.dp2px(space);
            outRect.left = CommonUtils.dp2px(space)/2;
        }

        if(parent.getChildLayoutPosition(view)<2){
            outRect.top = CommonUtils.dp2px(space);
        }

        outRect.bottom = CommonUtils.dp2px(space);






    }
}
