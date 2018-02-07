package com.huapu.huafen.callbacks;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.huapu.huafen.utils.CommonUtils;

/**
 * Created by admin on 2016/12/7.
 */
public class CustomGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private Context mContext;
    private View mRootView;
    private View mScrollToView;

    public CustomGlobalLayoutListener(Context context,View rootView, View scrollToView){
        this.mContext=context;
        this.mRootView=rootView;
        this.mScrollToView = scrollToView;
    }


    @Override
    public void onGlobalLayout(){
        Rect rect= new Rect();
        mRootView.getWindowVisibleDisplayFrame(rect);
        int rootInvisibleHeight = mRootView.getRootView().getHeight() - rect.bottom;
        if(rootInvisibleHeight>100){
            int[] location = new int[2];
            mScrollToView.getLocationInWindow(location);
            int scrollHeight=(location[1]+mScrollToView.getHeight()) - rect.bottom;
            mRootView.scrollTo(0,scrollHeight+CommonUtils.dp2px(30));
        }else{
            mRootView.scrollTo(0,0);
        }
    }

}
