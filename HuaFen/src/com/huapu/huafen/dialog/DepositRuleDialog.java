package com.huapu.huafen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;

import com.huapu.huafen.R;

/**
 * Created by qwe on 2017/8/10.
 */

public class DepositRuleDialog extends Dialog {

    public DepositRuleDialog(@NonNull Context context) {
        super(context);
    }

    public DepositRuleDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_deposit_rule);
        findViewById(R.id.closeDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DepositRuleDialog.this.dismiss();
            }
        });

    }
}
