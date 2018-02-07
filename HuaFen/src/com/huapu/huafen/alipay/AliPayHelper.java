package com.huapu.huafen.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AliPayHelper {

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;
	private Activity mContext;

	public AliPayHelper(Activity context) {
		this.mContext = context;
	}


	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				@SuppressWarnings("unchecked")
				Map<String, String> params = (Map<String, String>) (msg.obj);
				PayResult payResult = new PayResult(params,true);
				LogUtil.e("payResult",payResult.toString());
				/**
				 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				String orderNumber=payResult.getOut_trade_no();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					ToastUtil.toast(mContext, "支付成功");
					if(mOnAliPayCompleteListener !=null){
						mOnAliPayCompleteListener.onComplete(orderNumber);
					}
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					ToastUtil.toast(mContext, "支付失败");
					if(mOnAliPayCompleteListener !=null){
						mOnAliPayCompleteListener.onFailed(resultStatus);
					}

				}
				break;
			}
			case SDK_AUTH_FLAG: {
				@SuppressWarnings("unchecked")
				Map<String, String> params = (Map<String, String>) (msg.obj);
				AuthResult authResult = new AuthResult(params, true);
				String resultStatus = authResult.getResultStatus();

				if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
					ToastUtil.toast(mContext, "支付宝授权登录成功");
					String userId=authResult.getUserId();
					String authCode=authResult.getAuthCode();
					if(mOnAliPayAuthCompleteListener !=null){
						mOnAliPayAuthCompleteListener.onComplete(userId,authCode);
					}
				} else {
					ToastUtil.toast(mContext, "支付宝授权登录失败");
				}
				break;
			}
			default:
				break;
			}
		}
	};

	/**
	 * 支付宝支付业务
	 *
	 */
	public void payV2(String orderParam,OnAliPayCompleteListener alipayPayCompleteListener) {
		this.mOnAliPayCompleteListener = alipayPayCompleteListener;
		startRequestForPay(orderParam);
	}

	/**
	 * 支付宝账户授权业务
	 *
	 * @param
	 */
	public void authV2(OnAliPayAuthCompleteListener onAlipayAuthCompleteListener) {
		this.mOnAliPayAuthCompleteListener = onAlipayAuthCompleteListener;
		startRequestForAliPayAuthInfo();
	}

	private void startRequestForPay(final String orderParam) {

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask aliPay = new PayTask(mContext);
				Map<String, String> result = aliPay.payV2(orderParam, true);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	private void startRequestForAliPayAuthInfo() {
		ProgressDialog.showProgress(mContext, "正在获取授权登录信息...", false);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(MyConstants.TYPE, "2");
		LogUtil.i("liang", "获取授权登录信息" + params);
		OkHttpClientManager.postAsyn(MyConstants.ALIPAY_AUTH_REQUEST_SIGN, params, new StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				ProgressDialog.closeProgress();
				LogUtil.i("liang", "获取授权登录信息onError " + e.toString());
				ToastUtil.toast(mContext, "获取授权信息失败..");
			}

			@Override
			public void onResponse(String response) {
				LogUtil.i("liang", "获取授权登录信息onResponse " + response);
				ProgressDialog.closeProgress();
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if (!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if (!TextUtils.isEmpty(baseResult.obj)) {
							Map<String, String> authInfoMap = JSON.parseObject(baseResult.obj, new TypeReference<Map<String, String>>(){});

							Set<Entry<String, String>> entrySet = authInfoMap.entrySet();
							Iterator<Entry<String, String>> it = entrySet.iterator();
							while (it.hasNext()) {
								Entry<String, String> entry = it.next();
								LogUtil.e("authMap", entry.getKey() + "--" + entry.getValue());
							}
							final String authInfo = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
							Runnable authRunnable = new Runnable() {

								@Override
								public void run() {
									// 构造AuthTask 对象
									AuthTask authTask = new AuthTask(mContext);
									// 调用授权接口，获取授权结果
									Map<String, String> result = authTask.authV2(authInfo,true);
									Message msg = new Message();
									msg.what = SDK_AUTH_FLAG;
									msg.obj = result;
									mHandler.sendMessage(msg);
								}
							};
							// 必须异步调用
							Thread authThread = new Thread(authRunnable);
							authThread.start();
						}
					} else {
						CommonUtils.error(baseResult, mContext, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private OnAliPayAuthCompleteListener mOnAliPayAuthCompleteListener;

	public interface OnAliPayAuthCompleteListener {
		void onComplete(String aliId,String authCode);
	}


	private OnAliPayCompleteListener mOnAliPayCompleteListener;

	public interface OnAliPayCompleteListener {
		void onComplete(String out_trade_no);
		void onFailed(String errorCode);
	}
}