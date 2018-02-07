package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.R;

/**
 * Created by lalo on 2016/10/28.
 */
public class HLoadingStateView extends FrameLayout {


    private final AnimationDrawable mRotateAnimation;
    private ImageView ivLoading;
    private TextView tvLoading;

    public HLoadingStateView(Context context) {
        super(context);
        initView();
        mRotateAnimation  = (AnimationDrawable) getResources().getDrawable(
                R.drawable.anim_loading_all);
    }

    public HLoadingStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        mRotateAnimation  = (AnimationDrawable)getResources().getDrawable(
                R.drawable.anim_loading_all);

    }

    private void initView(){
        LayoutInflater inflater=(LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.loading_state_layout,this,true);
        this.setBackgroundColor(getResources().getColor(R.color.white));
        ivLoading = (ImageView) findViewById(R.id.ivLoading);
        tvLoading = (TextView)findViewById(R.id.tvLoading);

    }

    public void setBackgroudTranslucent(){
        this.setBackgroundColor(getResources().getColor(R.color.translucent));
    }

    public void setStateShown(State state){
        switch (state){
            case LOADING:
                this.setVisibility(View.VISIBLE);
                mRotateAnimation.start();
                ivLoading.setImageDrawable(mRotateAnimation);
                tvLoading.setText("正在加载中...");
                ivLoading.setOnClickListener(null);
                break;
            case COMPLETE:
                this.setVisibility(View.GONE);
                ivLoading.clearAnimation();
                ivLoading.setOnClickListener(null);
                break;
            case LOADING_FAILED:
                this.setVisibility(View.VISIBLE);
                ivLoading.clearAnimation();
                ivLoading.setImageDrawable(mRotateAnimation);
                tvLoading.setText("加载失败...");
                ivLoading.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            default:


        }
    }


    public enum State{
        LOADING,
        COMPLETE,
        LOADING_FAILED,
        ;
    }
}
