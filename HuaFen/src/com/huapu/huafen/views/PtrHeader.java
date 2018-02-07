package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonUtils;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by admin on 2017/1/9.
 */

public class PtrHeader extends LinearLayout implements PtrUIHandler {

    private FirstStepView firstStepView;
    private SecondStepView secondStepView;
    private TextView tvPullToRefresh;
    private AnimationDrawable secondAnimation;
    private IHandler iHandler;

    public PtrHeader(Context context) {
        this(context,null);

    }

    public PtrHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        initView();
    }

    private void initView(){
        LayoutInflater inflater=(LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ptr_header_layout,this,true);
        setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
        firstStepView = (FirstStepView) findViewById(R.id.first_step_view);
        secondStepView = (SecondStepView) findViewById(R.id.second_step_view);
        secondStepView.setBackgroundResource(R.drawable.second_step_animation);
        secondAnimation = (AnimationDrawable) secondStepView.getBackground();
        tvPullToRefresh = (TextView) findViewById(R.id.tv_pull_to_refresh);
    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {
        if(iHandler!=null){
            iHandler.onUIReset(frame);
        }
        tvPullToRefresh.setText("下拉刷新");
        firstStepView.setVisibility(View.VISIBLE);
        secondStepView.setVisibility(View.GONE);
        secondAnimation.stop();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
//        tvPullToRefresh.setText("下拉刷新");
        if(iHandler!=null){
            iHandler.onUIRefreshPrepare(frame);
        }
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        tvPullToRefresh.setText("加载中");
        firstStepView.setVisibility(View.GONE);
        secondStepView.setVisibility(View.VISIBLE);
        secondAnimation.stop();
        secondAnimation.start();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        tvPullToRefresh.setText("更新完成");
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        int offsetY = ptrIndicator.getCurrentPosY();
        int max = CommonUtils.dp2px(75.0f);
        if(offsetY>max){
            offsetY = max;
        }

        float progress = (float) offsetY / (float) max;
        firstStepView.setCurrentProgress(progress);
        firstStepView.postInvalidate();

    }


    public interface IHandler{
        void onUIRefreshPrepare(PtrFrameLayout frame);
        void onUIReset(PtrFrameLayout frame);
    }

    public void setHandler(IHandler iHandler) {
        this.iHandler = iHandler;
    }
}
