package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.events.PushEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 选择生育状态
 * @author liang_xs
 *
 */
public class ChoosePregnantStatActivity extends BaseActivity {
	private ImageView ivPregnant, ivPregnancy, ivBaby;
	private String extraMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_pregnant_state);
		EventBus.getDefault().register(this);
		Intent intent = getIntent();
		if(intent.hasExtra(MyConstants.EXTRA_MAP)){
			extraMap = intent.getStringExtra(MyConstants.EXTRA_MAP);
		}
		initView();
	}
	private void initView() {
		setTitleString("选择状态");
		ivPregnant = (ImageView) findViewById(R.id.ivPregnant);
		ivPregnancy = (ImageView) findViewById(R.id.ivPregnancy);
		ivBaby = (ImageView) findViewById(R.id.ivBaby);
		
		ivPregnant.setOnClickListener(this);
		ivPregnancy.setOnClickListener(this);
		ivBaby.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent intent ;
		switch (v.getId()) {

		case R.id.ivPregnant:
			CommonPreference.setStringValue(CommonPreference.PREGNANTSTAT, "1");
			startRequestForUpdateUser();
//			intent = new Intent(ChoosePregnantStatActivity.this, MainActivity.class);
//			intent.putExtra(MyConstants.EXTRA_MAP,extraMap);
//			startActivity(intent);
			break;
		case R.id.ivPregnancy:
			intent = new Intent(ChoosePregnantStatActivity.this, ChoosePregnancyActivity.class);
			intent.putExtra(MyConstants.EXTRA_MAP,extraMap);
			startActivityForResult(intent, 1000);
			break;

		case R.id.ivBaby:
			intent = new Intent(ChoosePregnantStatActivity.this, ChooseChildActivity.class);
			intent.putExtra(MyConstants.EXTRA_MAP,extraMap);
			startActivityForResult(intent, 1001);
			break;
		}
	}

	public void onEventMainThread(final Object obj) {
		if(obj == null) {
			return;
		}
		if(obj instanceof PushEvent){
			LogUtil.e("onEventMainThread..","PushEvent");
			PushEvent event = (PushEvent) obj;
			extraMap=event.extraMap;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1000) {
				startRequestForUpdateUser();
			} else if (requestCode == 1001) {
				startRequestForUpdateUser();
			}
		}
	}


	/**
	 * 修改个人资料
	 */
	private void startRequestForUpdateUser() {
		HashMap<String, String> params = new HashMap<String, String>();
		if(!TextUtils.isEmpty(CommonPreference.getStringValue(CommonPreference.PREGNANTSTAT, ""))) {
			if(CommonPreference.getStringValue(CommonPreference.PREGNANTSTAT, "").equals("1")) {
				params.put("pregnantStat", "1");
			} else {
				String firstSex = CommonPreference.getStringValue(CommonPreference.FIRSRBABYSEX, "");
				String firstBirth = CommonPreference.getStringValue(CommonPreference.FIRSTBABYBIRTH, "");
				String secondSex = CommonPreference.getStringValue(CommonPreference.SECONDBABYSEX, "");
				String secondBirth = CommonPreference.getStringValue(CommonPreference.SECONDBABYBIRTH, "");
				JSONArray array = new JSONArray();
				JSONObject objFirst = new JSONObject();
				if(!TextUtils.isEmpty(firstSex) && !TextUtils.isEmpty(firstBirth)) {
					try {
						objFirst.put("sex", null == firstSex ? "":firstSex);
						objFirst.put("dateOfBirth", null == firstBirth ? "":firstBirth);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					array.put(objFirst);
				}

				JSONObject objSecond = new JSONObject();
				if(!TextUtils.isEmpty(secondSex) && !TextUtils.isEmpty(secondBirth)) {
					try {
						objSecond.put("sex", null == secondSex ? "":secondSex);
						objSecond.put("dateOfBirth", null == secondBirth ? "":secondBirth);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					array.put(objSecond);
				}
				if(!TextUtils.isEmpty(CommonPreference.getStringValue(CommonPreference.DUEDATE, ""))){
					params.put("pregnantStat", "2");
					params.put("dueDate", CommonPreference.getStringValue(CommonPreference.DUEDATE, ""));
				}
				if(array.length() > 0) {
					params.put("pregnantStat", "3");
					params.put("babys", array.toString());
				}
			}
		}
		LogUtil.i("liang", "修改个人资料params：" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.UPDATEUSER, params, new OkHttpClientManager.StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				finish();
				LogUtil.i("liang", "修改个人资料error:" + e.toString());
			}

			@Override
			public void onResponse(String response) {
				LogUtil.i("liang", "修改个人资料:" + response.toString());
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if (!isJson) {
					finish();
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if (!TextUtils.isEmpty(baseResult.obj)) {
							// 解析数据
							UserInfo userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
							CommonPreference.setUserInfo(userInfo);
							finish();
						}
					} else {
						CommonUtils.error(baseResult, ChoosePregnantStatActivity.this, "");
						finish();
					}
				} catch (Exception e) {
					finish();
					e.printStackTrace();
				}
			}

		});
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
