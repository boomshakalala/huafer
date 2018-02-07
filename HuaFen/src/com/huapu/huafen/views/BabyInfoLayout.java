package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Baby;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;

import java.util.ArrayList;

/**
 * Created by admin on 2017/4/17.
 */

public class BabyInfoLayout extends LinearLayout{


    public BabyInfoLayout(Context context) {
        super(context);
    }

    public BabyInfoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    public void setBabies(ArrayList<Baby> babies){
        removeAllViews();

        if(!ArrayUtil.isEmpty(babies)){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = CommonUtils.dp2px(5f);
            for(Baby bb:babies){
                TextView textView = new TextView(getContext());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                textView.setTextColor(getContext().getResources().getColor(R.color.text_color_gray));
                textView.setGravity(Gravity.CENTER_VERTICAL);
                Drawable drawableLeft ;
                if(bb.getSex() == 0){//女
                    drawableLeft = getResources().getDrawable(R.drawable.baby_girl);
                }else{
                    drawableLeft = getResources().getDrawable(R.drawable.baby_boy);
                }

                drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
                textView.setCompoundDrawables(drawableLeft,null,null,null);

                String age = DateTimeUtils.getBabyAgeWithoutMonth(bb.getDateOfBirth());
                textView.setText(age);
                addView(textView,params);
            }
        }else{
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            textView.setTextColor(getContext().getResources().getColor(R.color.text_color_gray));
            textView.setText("添加宝宝信息");
            addView(textView);
        }

    }



}
