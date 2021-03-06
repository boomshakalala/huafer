package com.huapu.huafen.layoutmanager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huapu.huafen.utils.CommonUtils;

/**
 * Created by admin on 2016/11/19.
 */
public class CameraLayoutManager extends RecyclerView.LayoutManager {


    private GridLayoutManager layoutManager ;

    public CameraLayoutManager(Context context){
        this.layoutManager = new GridLayoutManager(context,4);
        this.layoutManager.setOrientation(GridLayoutManager.VERTICAL);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        //定义竖直方向的偏移量
        int offsetY = 0;
        for (int i = 0; i < getItemCount(); i++) {
            //这里就是从缓存里面取出
            View view = recycler.getViewForPosition(i);
            //将View加入到RecyclerView中
            addView(view);
            //对子View进行测量
            measureChildWithMargins(view, 0, 0);
            //把宽高拿到，宽高都是包含ItemDecorate的尺寸
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);
            //最后，将View布局
            layoutDecorated(view, 0, offsetY, width, offsetY + height);
            //将竖直方向偏移量增大height
            offsetY += height;
        }
        int count = getItemCount();
        if(count<=5){
            if(count==1){
                View view = recycler.getViewForPosition(0);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)view.getLayoutParams();
                params.width = CommonUtils.getScreenWidth()/2;
            }else {

            }
        }

        layoutManager.onLayoutChildren(recycler,state);
    }
}
