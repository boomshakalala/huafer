package com.huapu.huafen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.huapu.huafen.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qwe on 2017/5/1.
 */

public class ArticlePicDeleteDialog extends Dialog {

    private DialogCallback btnRightCallBack;

    public ArticlePicDeleteDialog(@NonNull Context context) {
        super(context, R.style.DialogText);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_text_article_delete);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnDialogLeft, R.id.btnDialogRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnDialogLeft:
                this.dismiss();
                break;
            case R.id.btnDialogRight:
                if (null != btnRightCallBack) {
                    btnRightCallBack.Click();
                }
                break;
        }
    }

    public void setRightCall(DialogCallback btnRight) {
        this.btnRightCallBack = btnRight;
    }
}
