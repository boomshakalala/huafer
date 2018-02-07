package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.huapu.huafen.R;
import com.huapu.huafen.utils.LogUtil;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by admin on 2017/1/9.
 */

public class PtrAnimationBackgroundHeader extends FrameLayout implements PtrUIHandler {

    private final static String TAG = PtrAnimationBackgroundHeader.class.getSimpleName();
    private ImageView ivBg;
    private ImageView ivBgRotate;
    private SkateGirlView ivSkateGirl;
    private ImageView ivSkateGirlWalking;
    private ImageView ivSkateGirlBegin;
    private IHandler iHandler;
    private AnimationDrawable cityBackgroundAnimation;
    private AnimationDrawable skateGirlWalkingAnimation;
    private AnimationDrawable skateGirlBeginAnimation;
    private boolean isBegin;
    public PtrAnimationBackgroundHeader(Context context) {
        this(context,null);

    }

    public PtrAnimationBackgroundHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        LayoutInflater inflater=(LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ptr_animation_layout_header,this,true);
        ivBg = (ImageView) findViewById(R.id.ivBg);
        ivBgRotate= (ImageView) findViewById(R.id.ivBgRotate);
        ivBgRotate.setBackgroundResource(R.drawable.ptr_header_bg_animation);

        ivSkateGirl =(SkateGirlView) findViewById(R.id.ivSkateGirl);
        ivSkateGirlWalking =(ImageView) findViewById(R.id.ivSkateGirlWalking);
        ivSkateGirlBegin =(ImageView) findViewById(R.id.ivSkateGirlBegin);

        ivBg.setBackgroundResource(R.drawable.ptr_animation_bg);
        ivSkateGirlWalking.setBackgroundResource(R.drawable.ptr_header_skate_girl_walking_animation);
        ivSkateGirlBegin.setBackgroundResource(R.drawable.ptr_header_skate_girl_begin_animation);

        cityBackgroundAnimation = (AnimationDrawable)ivBgRotate.getBackground();
        skateGirlWalkingAnimation = (AnimationDrawable) ivSkateGirlWalking.getBackground();
        skateGirlBeginAnimation = (AnimationDrawable) ivSkateGirlBegin.getBackground();

    }

    private void reset(){
        ivSkateGirl.setVisibility(VISIBLE);
        ivSkateGirlWalking.setVisibility(GONE);

        ivBg.setVisibility(VISIBLE);
        ivBgRotate.setVisibility(GONE);

    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {
        if(iHandler!=null){
            iHandler.onUIReset(frame);
        }
        isBegin = true;
        reset();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        if(iHandler!=null){
            iHandler.onUIRefreshPrepare(frame);
        }
        isBegin = true;
        reset();
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        stopAnimations();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        int offsetY = ptrIndicator.getCurrentPosY();
        update(offsetY);


    }

    private void update(int offset){

        int height = getHeight();

        if(offset<height){
            stopAnimations();
            ivSkateGirl.setVisibility(VISIBLE);
            ivSkateGirlWalking.setVisibility(GONE);
            ivSkateGirlBegin.setVisibility(GONE);

            ivBg.setVisibility(VISIBLE);
            ivBgRotate.setVisibility(GONE);

            float progress = (float) offset / (float) height;
            ivSkateGirl.setCurrentProgress(progress);

        }else if(offset>=height){
            ivSkateGirl.setVisibility(GONE);
            if(isBegin){
                isBegin = false;
                ivSkateGirlWalking.setVisibility(GONE);
                ivSkateGirlBegin.setVisibility(VISIBLE);
                ivBg.setVisibility(GONE);
                ivBgRotate.setVisibility(VISIBLE);

                if(cityBackgroundAnimation!=null&&!cityBackgroundAnimation.isRunning()){
                    cityBackgroundAnimation.start();
                }

                if(skateGirlBeginAnimation!=null&&!skateGirlBeginAnimation.isRunning()){
                    skateGirlBeginAnimation.start();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            ivSkateGirlWalking.setVisibility(VISIBLE);
                            ivSkateGirlBegin.setVisibility(GONE);
                            skateGirlBeginAnimation.stop();
                            if(skateGirlWalkingAnimation!=null&&!skateGirlWalkingAnimation.isRunning()){
                                skateGirlWalkingAnimation.start();
                                LogUtil.e(TAG,"skateGirlWalkingAnimation.start()");
                            }
                        }
                    },800);
                }
            }else{

                if(skateGirlBeginAnimation!=null&&!skateGirlBeginAnimation.isRunning()){
                    ivSkateGirlWalking.setVisibility(VISIBLE);
                    ivSkateGirlBegin.setVisibility(GONE);

                    ivBg.setVisibility(GONE);
                    ivBgRotate.setVisibility(VISIBLE);

                    if(cityBackgroundAnimation!=null&&!cityBackgroundAnimation.isRunning()){
                        cityBackgroundAnimation.start();
                    }

                    if(skateGirlWalkingAnimation!=null&&!skateGirlWalkingAnimation.isRunning()){
                        skateGirlWalkingAnimation.start();
                    }
                }

            }


        }

    }

    private void stopAnimations() {
        if(cityBackgroundAnimation!=null){
            cityBackgroundAnimation.stop();
        }

        if(skateGirlWalkingAnimation!=null){
            skateGirlWalkingAnimation.stop();
        }
    }


    public interface IHandler{
        void onUIRefreshPrepare(PtrFrameLayout frame);
        void onUIReset(PtrFrameLayout frame);
    }

    public void setHandler(IHandler iHandler) {
        this.iHandler = iHandler;
    }
}
