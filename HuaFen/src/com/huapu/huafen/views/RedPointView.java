package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonUtils;

/**
 * Created by admin on 2017/1/9.
 */

public class RedPointView extends FrameLayout {


    private TextView tvRedPoint;
    private TextView tvTitle;

    public RedPointView(Context context) {
        this(context,null);
    }

    public RedPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater inflater=(LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.red_point_layout,this,true);
        tvRedPoint = (TextView) findViewById(R.id.tvRedPoint);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
    }

    public void setPoint(int count){
        if(count<=0){
            tvRedPoint.setVisibility(INVISIBLE);
        }else if(count>0&&count<=999){
            tvRedPoint.setVisibility(VISIBLE);
            tvRedPoint.setText(String.valueOf(count));
            if(count<10){
                FrameLayout.LayoutParams params = (LayoutParams) tvRedPoint.getLayoutParams();
                params.width = params.height =CommonUtils.dp2px(14f);
                tvRedPoint.setBackgroundResource(R.drawable.red_point);
            }else{
                FrameLayout.LayoutParams params = (LayoutParams) tvRedPoint.getLayoutParams();
                params.height =CommonUtils.dp2px(14f);
                params.width =CommonUtils.dp2px(18f);
                tvRedPoint.setBackgroundResource(R.drawable.red_rectangle_point);
            }
        }else{
            FrameLayout.LayoutParams params = (LayoutParams) tvRedPoint.getLayoutParams();
            params.height =CommonUtils.dp2px(14f);
            params.width =CommonUtils.dp2px(18f);
            tvRedPoint.setBackgroundResource(R.drawable.red_rectangle_point);
            tvRedPoint.setVisibility(VISIBLE);
            tvRedPoint.setText("···");
        }
    }

    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
    }

    public void setImage(int resId){
        Drawable drawable = getContext().getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tvTitle.setCompoundDrawables(null, drawable, null, null);
    }

    public void setTextColor(int color){
        tvTitle.setTextColor(color);
    }

    public void setSelect(boolean check,int color){
        tvTitle.setSelected(check);
        tvTitle.setTextColor(color);
    }
}
