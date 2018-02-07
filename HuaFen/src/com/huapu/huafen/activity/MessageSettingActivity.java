package com.huapu.huafen.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.beans.Preferences;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
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


/**
 * 设置
 * @author liang_xs
 *
 */
public class MessageSettingActivity extends BaseActivity implements  RadioGroup.OnCheckedChangeListener {
	private RadioGroup rgGoodsComment;
	private RadioButton rgGoodsCommentAll, rgGoodsCommentFriendOnly, rgGoodsCommentNone;
	private int currRBDisturb, currRBGoodsComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_settings);
		initView();
	}

	private void initView() {
		setTitleString("留言设置");
		rgGoodsComment = (RadioGroup) findViewById(R.id.rgGoodsComment);
		rgGoodsCommentAll = (RadioButton) findViewById(R.id.rgGoodsCommentAll);
		rgGoodsCommentFriendOnly = (RadioButton) findViewById(R.id.rgGoodsCommentFriendOnly);
		rgGoodsCommentNone = (RadioButton) findViewById(R.id.rgGoodsCommentNone);
		String goodsComment = CommonPreference.getGoodsComment();
		if("1".equals(goodsComment)) {
			currRBGoodsComment = R.id.rgGoodsCommentAll;
			rgGoodsComment.check(R.id.rgGoodsCommentAll);
		} else if("2".equals(goodsComment)) {
			currRBGoodsComment = R.id.rgGoodsCommentFriendOnly;
			rgGoodsComment.check(R.id.rgGoodsCommentFriendOnly);
		} else if("0".equals(goodsComment)) {
			currRBGoodsComment = R.id.rgGoodsCommentNone;
			rgGoodsComment.check(R.id.rgGoodsCommentNone);
		}
		rgGoodsComment.setOnCheckedChangeListener(MessageSettingActivity.this);
		startRequestForGetPreferences();
	}


	@Override
	public void onClick(View v) {
	}




	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.rgGoodsCommentAll:
				startRequestForSetPreferences(MyConstants.REQUEST_KEY_GOODS_COMMENT, "1");
				break;
			case R.id.rgGoodsCommentFriendOnly:
				startRequestForSetPreferences(MyConstants.REQUEST_KEY_GOODS_COMMENT, "2");
				break;
			case R.id.rgGoodsCommentNone:
				startRequestForSetPreferences(MyConstants.REQUEST_KEY_GOODS_COMMENT, "0");
				break;
			}

	}



	/**
	 * 设置偏好设置
	 */
	private void startRequestForSetPreferences(final String key, final String value) {
		if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
			toast("请求异常");
			return;
		}
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			requestSetPreferenceError(key, value);
			return;
		}
		ProgressDialog.showProgress(this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(key, value);
		LogUtil.i("liang", "设置偏好设置params:"+params.toString());
		OkHttpClientManager.postAsyn(MyConstants.SETPREFERENCES, params, new StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				LogUtil.i("liang", "设置偏好设置：" + e.toString());
				toast("设置失败，请重试");
				requestSetPreferenceError(key, value);
				ProgressDialog.closeProgress();
			}


			@Override
			public void onResponse(String response) {
				ProgressDialog.closeProgress();
				LogUtil.i("liang", "设置偏好设置：" + response.toString());
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if(!isJson) {
					toast("设置失败，请重试");
					requestSetPreferenceError(key, value);
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if(MyConstants.REQUEST_KEY_GOODS_COMMENT.equals(key)) {
							CommonPreference.setGoodsComment(value);
							if("1".equals(value)) {
								currRBGoodsComment = R.id.rgGoodsCommentAll;
							} else if("2".equals(value)) {
								currRBGoodsComment = R.id.rgGoodsCommentFriendOnly;
							} else if("0".equals(value)) {
								currRBGoodsComment = R.id.rgGoodsCommentNone;
							}
						}
					} else {
						requestSetPreferenceError(key, value);
						CommonUtils.error(baseResult, MessageSettingActivity.this, "");
					}
				} catch (Exception e) {
					toast("设置失败，请重试");
					requestSetPreferenceError(key, value);
					e.printStackTrace();
				}
			}
		});

	}

	private void requestSetPreferenceError(String key, String value) {
		if(MyConstants.REQUEST_KEY_GOODS_COMMENT.equals(key)) {
			rgGoodsComment.setOnCheckedChangeListener(null);
			rgGoodsComment.check(currRBGoodsComment);
			rgGoodsComment.setOnCheckedChangeListener(MessageSettingActivity.this);
        }
	}

	private void startRequestForGetPreferences(){
		if (!CommonUtils.isNetAvaliable(this)){
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(this);
		OkHttpClientManager.postAsyn(MyConstants.GETPREFERENCES, null, new StringCallback() {
			@Override
			public void onError(Request request, Exception e) {
				LogUtil.i("liang", "获取偏好设置：" + e.toString());
				ProgressDialog.closeProgress();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialog.closeProgress();
				LogUtil.i("liang", "获取偏好设置：" + response.toString());
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if (!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response.toString(),BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE){
						Preferences preferences = JSON.parseObject(baseResult.obj,Preferences.class);
						if (preferences != null){
							updateConfig(preferences);
						}
					}else {
						CommonUtils.error(baseResult, MessageSettingActivity.this, "");
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}


	private void updateConfig(Preferences preferences){
		CommonPreference.setGoodsComment(preferences.getComment());
	}
}
