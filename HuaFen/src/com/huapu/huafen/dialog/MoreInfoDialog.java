package com.huapu.huafen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.activity.OrdersListActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;

public class MoreInfoDialog extends Dialog implements OnClickListener {
    private Context mContext;
    private TextView tvCamera, tvAlbum, tvCancel;

    public MoreInfoDialog(Context context) {
        super(context, R.style.photo_dialog);
        this.mContext = context;
        setCancelable(true);
    }


    public MoreInfoDialog(Context context, boolean isCancel) {
        super(context, R.style.photo_dialog);
        this.mContext = context;
        setCancelable(isCancel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_moreinfo);
        tvCamera = (TextView) findViewById(R.id.moneyGo);
        tvAlbum = (TextView) findViewById(R.id.tvMyBuy);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvCamera.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        Window window = getWindow();
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.DialogBottomTransScaleStyle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moneyGo:
                Intent intentDeposit = new Intent(mContext, WebViewActivity.class);
                intentDeposit.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.BID_RULE_WITHOUT_TITLE);
                mContext.startActivity(intentDeposit);
                this.dismiss();
                break;
            case R.id.tvMyBuy:
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(mContext);
                    return;
                }
                Intent intent = new Intent(mContext, OrdersListActivity.class);
                intent.putExtra(MyConstants.EXTRA_ORDERS_ROLE, 1);
                mContext.startActivity(intent);
                this.dismiss();
                break;
            case R.id.tvCancel:
                this.dismiss();
                break;
        }

    }


}
