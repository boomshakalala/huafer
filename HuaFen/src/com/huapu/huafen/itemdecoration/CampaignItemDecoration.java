package com.huapu.huafen.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.huapu.huafen.utils.CommonUtils;

/**
 * Created by mac on 17/7/24.
 */

public class CampaignItemDecoration extends RecyclerView.ItemDecoration{

    private int space;

    public CampaignItemDecoration(int spaceDp) {
        this.space = CommonUtils.dp2px(spaceDp);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = space;
    }

}
