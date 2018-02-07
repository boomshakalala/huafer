package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.alipay.AliPayHelper;
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
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 绑定支付宝
 *
 * @author liang_xs
 */
public class BindALiActivity extends BaseActivity {

    private TextView ivBtnBindALi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_ali);
        initView();
    }

    private void initView() {
		setTitleString("支付宝绑定");
        ivBtnBindALi = (TextView) findViewById(R.id.ivBtnBindALi);
        ivBtnBindALi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBtnBindALi:
            	AliPayHelper helper = new AliPayHelper(this);
            	helper.authV2(new AliPayHelper.OnAliPayAuthCompleteListener() {
					
					@Override
					public void onComplete(String aliId, String authCode) {
						startRequestForBind(aliId, authCode);
					}
				});
                break;
        }
    }

    /**
	 * 绑定支付宝
	 * @param aliId
	 * 			支付宝id
	 * @param authCode
	 * 			授权码
     */
    private void startRequestForBind(String aliId, String authCode) {
		if (!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(BindALiActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","2");
		params.put("aliId",aliId);
		params.put("authCode",authCode);
		Set<Entry<String, String>> entrySet = params.entrySet();
		Iterator<Entry<String, String>> it = entrySet.iterator();
		StringBuilder sb = new StringBuilder();
		while(it.hasNext()){
			Entry<String, String> set = it.next();
			sb.append(set.getKey()).append('=').append(set.getValue()).append(" , ");
			
		}
		LogUtil.e("bindAccountParam",sb.toString());
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
							data.putExtra("bindAlipay", "OK");
							data.putExtra("BIND_RESULT",2);
							setResult(RESULT_OK, data);
							BindALiActivity.this.finish();
						}
					} else {
						CommonUtils.error(baseResult, BindALiActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}


}
