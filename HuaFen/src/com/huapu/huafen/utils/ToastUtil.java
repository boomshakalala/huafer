package com.huapu.huafen.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huapu.huafen.R;

/**
 * toast
 * Created by dengbin on 17/12/20.
 */
public class ToastUtil {

    public static void toast(Context context, String msg) {
        if (context != null) {
            Toast toast = new Toast(context);
            View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            TextView tv = (TextView) view.findViewById(R.id.tvToast);
            tv.setText(msg);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 100);
            toast.show();
        }
    }

    public static void toasttop(Context context, String msg) {
        if (context != null) {
            Toast toast = new Toast(context);
            View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            TextView tv = (TextView) view.findViewById(R.id.tvToast);
            tv.setText(msg);
            toast.setView(view);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }
    }

    public static void toast(Context context, int resId) {
        toast(context, context.getResources().getString(resId));
    }
}
