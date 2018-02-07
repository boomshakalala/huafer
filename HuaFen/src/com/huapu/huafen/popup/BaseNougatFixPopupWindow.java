package com.huapu.huafen.popup;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by admin on 2016/11/19.
 */
public class BaseNougatFixPopupWindow extends PopupWindow{

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M+1) {

            int[] a = new int[2];
            anchor.getLocationInWindow(a);

            showAtLocation(anchor, Gravity.NO_GRAVITY, xoff, a[1] + anchor.getHeight() + yoff);

        } else {
            super.showAsDropDown(anchor, xoff, yoff);
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        this.showAsDropDown(anchor,0,0);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M+1) {

            int[] a = new int[2];
            anchor.getLocationInWindow(a);

            showAtLocation(anchor, gravity, xoff, a[1] + anchor.getHeight() + yoff);

        } else {
            super.showAsDropDown(anchor, xoff, yoff,gravity);
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {

        super.showAtLocation(parent, gravity, x, y);
    }


}
