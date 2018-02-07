package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class PtrDefaultFrameLayout extends PtrFrameLayout {

    private PtrClassicDefaultHeader mPtrClassicHeader;

    public PtrDefaultFrameLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrDefaultFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrDefaultFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        mPtrClassicHeader = new PtrClassicDefaultHeader(getContext());
        setHeaderView(mPtrClassicHeader);
        addPtrUIHandler(mPtrClassicHeader);
    }

    public PtrClassicDefaultHeader getHeader() {
        return mPtrClassicHeader;
    }


    public void buildPtr(PtrHandler ptrHandler){
        setPtrHandler(ptrHandler);
        PtrAnimationBackgroundHeader header = new PtrAnimationBackgroundHeader(getContext());

        setHeaderView(header);
        addPtrUIHandler(header);
        // the following are default settings
        setResistance(1.7f);
        setRatioOfHeaderHeightToRefresh(1.2f);
        setDurationToClose(200);
        setDurationToCloseHeader(300);

    }

}
