package com.huapu.huafen.weixin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.wxapi.WXPayEntryActivity;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

/**
 * 微信支付
 * Created by danielluan on 2017/9/4.
 */

public class WeChatPayHelper {


    public static int REQUEST_CODE_PAYMENT = 1010;
    private static IWXAPI wxapi;

    /**
     * 注册到微信
     * 使用支付前必须注册，可放在activity的oncreate()中执行。
     */
    public static void register(Context context) {

        wxapi = WXAPIFactory.createWXAPI(context, Constants.APP_ID, false);
        wxapi.registerApp(Constants.APP_ID);
    }

    public static IWXAPI getWxapi() {
        return wxapi;
    }


    private static boolean isPaySupported(Context context) {
        boolean isPaySupported = wxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (!isPaySupported) {
            Toast.makeText(context, "不支持微信支付", Toast.LENGTH_SHORT).show();
        }
        return isPaySupported;
    }


    public static void createPayment(Activity var0, String var1) {

        try {
            Logger.d("danielluan", "createPayment:" + var1);
                Intent var5 = new Intent(var0, WXPayEntryActivity.class);
                var5.putExtra("PaymentActivity.CHARGE", var1);
                var0.startActivityForResult(var5, REQUEST_CODE_PAYMENT);

        } catch (Exception var7) {
            var7.printStackTrace();
        }
    }


    public static void invokeWeChatPay(Context context, String content) {

        if (isPaySupported(context)) {
            try {
                Logger.d("danielluan", "调起支付 : " + content);
                JSONObject json = new JSONObject(content);
                if (null != json && !json.has("retcode")) {
                    PayReq req = new PayReq();
                    req.appId = json.getString("appid");
                    req.partnerId = json.getString("partnerid");
                    req.prepayId = json.getString("prepayid");
                    req.nonceStr = json.getString("noncestr");
                    req.timeStamp = json.getString("timestamp");
                    req.packageValue = json.getString("package");
                    req.sign = json.getString("sign");
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    wxapi.sendReq(req);
                }
            } catch (Exception e) {
                Logger.d("danielluan", "调起支付异常 : " + e.toString());
            }
        }
    }

}
