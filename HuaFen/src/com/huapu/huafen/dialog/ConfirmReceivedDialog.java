package com.huapu.huafen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResultNew;

/**
 * Created by mac on 2018/1/2.
 */

public class ConfirmReceivedDialog extends Dialog implements View.OnClickListener {

    DialogCallback btnLeft,btnRight;
    Button btnDialogLeft,btnDialogRight;

    public ConfirmReceivedDialog(@NonNull Context context) {
        super(context, R.style.DialogText);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm_received);
        btnDialogLeft = (Button) findViewById(R.id.btnDialogLeft);
        btnDialogRight = (Button) findViewById(R.id.btnDialogRight);
        btnDialogLeft.setOnClickListener(this);
        btnDialogRight.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDialogLeft:
                if (btnLeft != null) {
                    btnLeft.Click();
                }
                break;
            case R.id.btnDialogRight:
                if (btnRight != null) {
                    btnRight.Click();
                }
                break;
        }
        this.dismiss();
    }

    public void setLeftCall(DialogCallback btnLeft) {
        this.btnLeft = btnLeft;
    }

    public void setRightCall(DialogCallback btnRight) {
        this.btnRight = btnRight;
    }
}
