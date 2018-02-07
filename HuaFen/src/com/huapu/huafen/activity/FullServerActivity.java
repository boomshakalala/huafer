package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;


/**
 * 服务器爆满排队
 * @author liang_xs
 *
 */
public class FullServerActivity extends BaseActivity {
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_server);
		
		initView();
	}

	private void initView() {
		getTitleBar().setTitle("花粉儿").setOnLeftButtonClickListener(0,null);
		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(this);
	}

	private long requestTime;
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.button:
				ProgressDialog.showProgress(FullServerActivity.this);
				long systemTime = System.currentTimeMillis();
            	if(systemTime - requestTime < 1000) {
					button.postDelayed(new Runnable() {
    					@Override
    					public void run() {
							ProgressDialog.closeProgress();
    					}
    				}, 2000);
            	} else {
                	requestTime = System.currentTimeMillis();
					startRequestForTryAgain();
            	}
				break;
		}
	}



	private void startRequestForTryAgain() {
		if (!CommonUtils.isNetAvaliable(this)) {
			ProgressDialog.closeProgress();
			toast("请检查网络连接");
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		LogUtil.e("liang", "params:" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.RETRY, params, new OkHttpClientManager.StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				LogUtil.e("liang", "try again:" + e.toString());
				ProgressDialog.closeProgress();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialog.closeProgress();
				LogUtil.e("liang", "try again:" + response.toString());
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if (!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						requestTime = System.currentTimeMillis();
						startActivity(new Intent(FullServerActivity.this, MainActivity.class));
					} else {
						CommonUtils.error(baseResult, FullServerActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public void onBackPressed() {
	}
}
