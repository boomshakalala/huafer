package com.huapu.huafen.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * 监听软键盘的弹起
 */
public class OnKeyboardVisibilityTool {

    public static void setKeyboardListener(Context context, final OnKeyboardVisibilityListener listener) {
        final View activityRootView = ((ViewGroup) ((Activity) context)
                .findViewById(android.R.id.content)).getChildAt(0);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    private boolean wasOpened;
                    private final int DefaultKeyboardDP = 100;
                    private final int EstimatedKeyboardDP = DefaultKeyboardDP
                            + (Build.VERSION.SDK_INT >= 21 ? 48
                            : 0);
                    private final Rect r = new Rect();

                    @Override
                    public void onGlobalLayout() {
                        int estimatedKeyboardHeight = (int) TypedValue
                                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                        EstimatedKeyboardDP, activityRootView
                                                .getResources()
                                                .getDisplayMetrics());
                        activityRootView.getWindowVisibleDisplayFrame(r);
                        int heightDiff = activityRootView.getRootView()
                                .getHeight() - (r.bottom - r.top);
                        boolean isShown = heightDiff >= estimatedKeyboardHeight;

                        if (isShown == wasOpened) {
                            return;
                        }
                        wasOpened = isShown;
                        listener.onVisibilityChanged(isShown);
                    }
                });
    }

    /**
     * 监听软键盘的弹起
     */
    public interface OnKeyboardVisibilityListener {
        void onVisibilityChanged(boolean visible);
    }
}

