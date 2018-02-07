package com.huapu.huafen.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonUtils;

/**
 * Created by admin on 2016/9/22.
 */
public class DashLineView extends RelativeLayout{

    private ImageView ivPic;
    private TextView tv0,tv1,tv2;
    private View dashLine;
    private final static int SMALL_GRAY = 0;
    private final static int SMALL_BLACK = 1;
    private final static int NORMAL_GRAY = 2;
    private final static int NORMAL_BLACK = 3;
    private final static int BIG_GRAY = 4;
    private final static int BIG_BLACK = 5;
    private final static int HUGE_GRAY = 6;
    private final static int HUGE_BLACK = 7;
    private final static int BIG_HUGE_GRAY = 8;
    private final static int BIG_HUGE_BLACK = 9;

    public DashLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DashLineView);
        int style = array.getInt(R.styleable.DashLineView_dashLineStyle, NORMAL_BLACK);
        int resId = array.getResourceId(R.styleable.DashLineView_drawableStart, -1);
        init(style,resId);
        array.recycle();
    }

    public DashLineView(Context context) {
        this(context,null);
    }

    private void init(int style,int resId){
        LayoutInflater inflater=(LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dash_line_layout,this,true);
        ivPic = (ImageView) findViewById(R.id.ivPic);
        if(resId>0){
            ivPic.setVisibility(View.VISIBLE);
            ivPic.setImageResource(resId);
        }else{
            ivPic.setVisibility(View.GONE);
        }
        tv0 = (TextView) findViewById(R.id.tv0);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        dashLine = findViewById(R.id.dashLine);
        setDashLineStyle(style);

    }

    public void setData(String var1,String var2){
        if(var1==null){
            var1 = "";
        }
        if(var2==null){
            var2 = "";
        }
        tv0.setVisibility(GONE);
        if(TextUtils.isEmpty(var1)&&TextUtils.isEmpty(var2)){
            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            dashLine.setVisibility(View.GONE);
            return;
        }
        if(!TextUtils.isEmpty(var1)&&!TextUtils.isEmpty(var2)){
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            dashLine.setVisibility(View.VISIBLE);
            tv1.setText(var1);
            tv2.setText(var2);
        }else{
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.GONE);
            dashLine.setVisibility(View.GONE);
            String des = var1+var2;
            tv1.setText(des);

        }

    }

    public void setData(String var0,String var1,String var2){
        if(var1==null){
            var1 = "";
        }
        if(var2==null){
            var2 = "";
        }
        if(TextUtils.isEmpty(var0)&&TextUtils.isEmpty(var1)&&TextUtils.isEmpty(var2)){
            tv0.setVisibility(GONE);
            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            dashLine.setVisibility(View.GONE);
            return;
        }

        if(TextUtils.isEmpty(var0)){
            tv0.setVisibility(GONE);
        }else{
            tv0.setVisibility(VISIBLE);
            tv0.setText(var0);
        }

        if(!TextUtils.isEmpty(var1)&&!TextUtils.isEmpty(var2)){
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            dashLine.setVisibility(View.VISIBLE);
            tv1.setText(var1);
            tv2.setText(var2);
        }else{
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.GONE);
            dashLine.setVisibility(View.GONE);
            String des = var1+var2;
            tv1.setText(des);

        }

    }

    public void setDrawableStart(int resId){
        if(resId<=0){
            throw new IllegalArgumentException("DashLineView drawableStart resId can not be <0 or =0");
        }
        ivPic.setVisibility(View.VISIBLE);
        ivPic.setImageResource(resId);
    }


    public void setDashLineStyle(int style){
        if(style<0){
            throw new IllegalArgumentException("DashLineView dashLineStyle can not be <0");
        }
        RelativeLayout.LayoutParams dashLineParams = (RelativeLayout.LayoutParams)dashLine.getLayoutParams();
        switch (style){

            case SMALL_GRAY:
                tv0.setTextSize(12);
                tv0.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv1.setTextSize(12);
                tv1.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv2.setTextSize(12);
                tv2.setTextColor(getResources().getColor(R.color.text_color_gray));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color_gray));
                dashLineParams.height = CommonUtils.dp2px(10);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(12);
                    params.height = CommonUtils.dp2px(12);
                }
                break;
            case SMALL_BLACK:
                tv0.setTextSize(12);
                tv0.setTextColor(getResources().getColor(R.color.text_color));
                tv1.setTextSize(12);
                tv1.setTextColor(getResources().getColor(R.color.text_color));
                tv2.setTextSize(12);
                tv2.setTextColor(getResources().getColor(R.color.text_color));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color));
                dashLineParams.height = CommonUtils.dp2px(10);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(12);
                    params.height = CommonUtils.dp2px(12);
                }
                break;
            case NORMAL_GRAY:
                tv0.setTextSize(14);
                tv0.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv1.setTextSize(14);
                tv1.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv2.setTextSize(14);
                tv2.setTextColor(getResources().getColor(R.color.text_color_gray));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color_gray));
                dashLineParams.height = CommonUtils.dp2px(12);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(14);
                    params.height = CommonUtils.dp2px(14);
                }
                break;
            case NORMAL_BLACK:
                tv0.setTextSize(14);
                tv0.setTextColor(getResources().getColor(R.color.text_color));
                tv1.setTextSize(14);
                tv1.setTextColor(getResources().getColor(R.color.text_color));
                tv2.setTextSize(14);
                tv2.setTextColor(getResources().getColor(R.color.text_color));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color));
                dashLineParams.height = CommonUtils.dp2px(12);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(14);
                    params.height = CommonUtils.dp2px(14);
                }
                break;
            case BIG_GRAY:
                tv0.setTextSize(16);
                tv0.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv1.setTextSize(16);
                tv1.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv2.setTextSize(16);
                tv2.setTextColor(getResources().getColor(R.color.text_color_gray));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color_gray));
                dashLineParams.height = CommonUtils.dp2px(14);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(16);
                    params.height = CommonUtils.dp2px(16);
                }
                break;
            case BIG_BLACK:
                tv0.setTextSize(16);
                tv0.setTextColor(getResources().getColor(R.color.text_color));
                tv1.setTextSize(16);
                tv1.setTextColor(getResources().getColor(R.color.text_color));
                tv2.setTextSize(16);
                tv2.setTextColor(getResources().getColor(R.color.text_color));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color));
                dashLineParams.height = CommonUtils.dp2px(14);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(16);
                    params.height = CommonUtils.dp2px(16);
                }
                break;
            case HUGE_GRAY:
                tv0.setTextSize(18);
                tv0.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv1.setTextSize(18);
                tv1.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv2.setTextSize(18);
                tv2.setTextColor(getResources().getColor(R.color.text_color_gray));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color_gray));
                dashLineParams.height = CommonUtils.dp2px(16);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(18);
                    params.height = CommonUtils.dp2px(18);
                }
                break;
            case HUGE_BLACK:
                tv0.setTextSize(18);
                tv0.setTextColor(getResources().getColor(R.color.text_color));
                tv1.setTextSize(18);
                tv1.setTextColor(getResources().getColor(R.color.text_color));
                tv2.setTextSize(18);
                tv2.setTextColor(getResources().getColor(R.color.text_color));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color));
                dashLineParams.height = CommonUtils.dp2px(16);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(18);
                    params.height = CommonUtils.dp2px(18);
                }
                break;
            case BIG_HUGE_GRAY:
                tv0.setTextSize(20);
                tv0.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv1.setTextSize(20);
                tv1.setTextColor(getResources().getColor(R.color.text_color_gray));
                tv2.setTextSize(20);
                tv2.setTextColor(getResources().getColor(R.color.text_color_gray));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color_gray));
                dashLineParams.height = CommonUtils.dp2px(18);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(20);
                    params.height = CommonUtils.dp2px(20);
                }
                break;
            case BIG_HUGE_BLACK:
                tv0.setTextSize(20);
                tv0.setTextColor(getResources().getColor(R.color.text_color));
                tv1.setTextSize(20);
                tv1.setTextColor(getResources().getColor(R.color.text_color));
                tv2.setTextSize(20);
                tv2.setTextColor(getResources().getColor(R.color.text_color));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color));
                dashLineParams.height = CommonUtils.dp2px(18);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(20);
                    params.height = CommonUtils.dp2px(20);
                }
                break;
            default:
                tv0.setTextSize(14);
                tv0.setTextColor(getResources().getColor(R.color.text_color));
                tv1.setTextSize(14);
                tv1.setTextColor(getResources().getColor(R.color.text_color));
                tv2.setTextSize(14);
                tv2.setTextColor(getResources().getColor(R.color.text_color));
                dashLine.setBackgroundColor(getResources().getColor(R.color.text_color));
                dashLineParams.height = CommonUtils.dp2px(12);
                if(ivPic.getVisibility() == View.VISIBLE){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ivPic.getLayoutParams();
                    params.width = CommonUtils.dp2px(14);
                    params.height = CommonUtils.dp2px(14);
                }
                break;
        }
        dashLine.setLayoutParams(dashLineParams);

    }

}
