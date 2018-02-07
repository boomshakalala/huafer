package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 17/6/29.
 */

public class OrderHistoryListLayout extends LinearLayout{

    private int textColor = R.color.text_black_light;

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public OrderHistoryListLayout(Context context) {
        this(context,null);
    }

    public OrderHistoryListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void setData(List<Map<String,String>> data){
        removeAllViews();
        if(!ArrayUtil.isEmpty(data)){

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = CommonUtils.dp2px(5f);
            for(Map<String,String> map:data){
                OrderHistoryItem item = new OrderHistoryItem(getContext());
                item.setData(map,textColor);
                addView(item,layoutParams);
            }
        }
    }
}
