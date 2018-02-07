package com.huapu.huafen.adapter.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Grid间隔
 *
 * @author dengbin
 */
public class StaggeredGridDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public StaggeredGridDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = space;
        outRect.bottom = 0;

        StaggeredGridLayoutManager.LayoutParams params =
                (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        // 根据params.getSpanIndex()来判断左右边确定分割线
        // 第一列设置左边距为space，右边距为space/2  （第二列反之）
        if (params.getSpanIndex() % 2 == 0) {
            outRect.left = space;
            outRect.right = space / 2;
        } else {
            outRect.left = space / 2;
            outRect.right = space;
        }
    }
}