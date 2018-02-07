package com.huapu.huafen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import com.huapu.huafen.R;

/**
 * Created by Administrator on 2017/4/15.
 */

public class ProductPreSellDialog extends Dialog {

    public ProductPreSellDialog(@NonNull Context context) {
        super(context, R.style.DialogText);
    }

    public ProductPreSellDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_presell);
        setCanceledOnTouchOutside(true);
    }
}
