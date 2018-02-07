package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by admin on 2016/11/23.
 */
public class HRecyclerView extends RecyclerView{


    public HRecyclerView(Context context) {
        super(context);
    }

    public HRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private float mDownX;
    private float mDownY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if(action==MotionEvent.ACTION_DOWN){
            mDownX=ev.getX();
            mDownY=ev.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        }else if(action==MotionEvent.ACTION_MOVE){
            if(Math.abs(ev.getX()-mDownX)>Math.abs(ev.getY()-mDownY)){
                getParent().requestDisallowInterceptTouchEvent(true);
            }else{
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }else if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL){
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(ev);
    }
}
