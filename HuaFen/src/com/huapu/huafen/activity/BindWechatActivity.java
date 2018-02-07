package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 绑定微信
 *
 * @author liang_xs
 */
public class BindWechatActivity extends BaseActivity {

    private TextView ivBtnBindWechat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_wechat);
        // 初始化ShareSDK
     	ShareSDK.initSDK(this);
        initView();
    }

    private void initView() {
		setTitleString("微信绑定");
        ivBtnBindWechat = (TextView) findViewById(R.id.ivBtnBindWechat);

        ivBtnBindWechat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivBtnBindWechat:
            	shareLogin(this, Wechat.NAME);
                break;
        }
    }

    /**
	 * 获取授权
	 * @param activity
	 * 			上下文
	 * @param name
	 * 			第三方名称
     */
	private void shareLogin(final Activity activity, final String name) {
		ProgressDialog.showProgress(this);
		final Platform platform = ShareSDK.getPlatform(activity, name);
		if (platform != null) {
			if (platform.isValid()) {
				platform.removeAccount(true);
				ShareSDK.removeCookieOnAuthorize(true);
			}
			platform.SSOSetting(false); // 设置false表示使用SSO授权方式
			platform.showUser(null);// 执行登录，登录后在回调里面获取用户资料
			platform.setPlatformActionListener(new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, final int arg1, final Throwable arg2) {
					LogUtil.i("liang", "onError: " + arg2.toString());
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ProgressDialog.closeProgress();
							if (platform != null) {
								if (platform.isValid()) {
									platform.removeAccount(true);
								}
							}
						}
					});
				}

				@Override
				public void onComplete(final Platform platform, int action, HashMap<String, Object> arg2) {
					LogUtil.i("liang", "onComplete: " + action);
					if (action == Platform.ACTION_USER_INFOR) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if(platform != null) {
									PlatformDb platDB = platform.getDb();
									if(platDB != null) {
										String uId = platDB.getUserId();
										startRequestForBind(uId);
									}
									
								}
								
							}
						});
					}
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							toast("取消登录");
							ProgressDialog.closeProgress();
						}
					});
					if (platform != null) {
						if (platform.isValid()) {
							platform.removeAccount(true);
						}
					}
				}
			});
		}
	}

	private void startRequestForBind(String flagId) {
		if (!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(BindWechatActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", "1");
		params.put("uid",flagId);
		LogUtil.e("bindAccountParam",params.toString());
		OkHttpClientManager.postAsyn(MyConstants.BIND_ACCOUNT, params, new StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				ProgressDialog.closeProgress();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialog.closeProgress();
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				Logger.d("danielluan",response);
				if (!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if (!TextUtils.isEmpty(baseResult.obj)) {
							UserInfo userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
							CommonPreference.setUserInfo(userInfo);
							JSONObject object = JSON.parseObject(baseResult.obj);

							Intent data = new Intent();
							data.putExtra("bindWechat", "OK");
							data.putExtra("BIND_RESULT",1);
							setResult(RESULT_OK, data);
							BindWechatActivity.this.finish();
						}
					} else {
						CommonUtils.error(baseResult, BindWechatActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}
}
