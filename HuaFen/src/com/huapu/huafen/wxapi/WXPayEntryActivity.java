package com.huapu.huafen.wxapi;

/**
 * Created by danielluan on 2017/9/4.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huapu.huafen.weixin.WeChatPayHelper;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.pay_result);
        IWXAPI api = WeChatPayHelper.getWxapi();
        api.handleIntent(getIntent(), this);
        Intent intent = getIntent();
        weChatPay(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        IWXAPI api = WeChatPayHelper.getWxapi();
        api.handleIntent(intent, this);
        weChatPay(intent);
    }

    private void weChatPay(Intent intent) {
        if (intent != null) {
            String content = intent.getStringExtra("PaymentActivity.CHARGE");
            if (!TextUtils.isEmpty(content)) {
                WeChatPayHelper.invokeWeChatPay(WXPayEntryActivity.this, content);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {


        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            int code = resp.errCode;
            Log.d("danielluan", "WXPayEntryActivity#onResp " + code);
            switch (code) {
                case 0:
                    bundle.putString("pay_result", "success");
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case -1:
                    bundle.putString("pay_result", "failed");
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case -2:
                    bundle.putString("pay_result", "cancel");
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                default:
                    bundle.putString("pay_result", "failed");
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }


        }
    }
}