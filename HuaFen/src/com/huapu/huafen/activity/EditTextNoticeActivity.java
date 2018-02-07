package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
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
/**
 * 编辑公告
 * @author liang_xs
 *
 */
public class EditTextNoticeActivity extends BaseActivity {
	private EditText etNotice;
	private long userId;
	private TextView tvInputCount;
	private String notice = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_text_notice);
		CommonUtils.hideKeyBoard(this);
		if(getIntent().hasExtra(MyConstants.EXTRA_USER_ID)) {
			userId = getIntent().getLongExtra(MyConstants.EXTRA_USER_ID, 0);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_NOTICE_TEXT)) {
			notice = getIntent().getStringExtra(MyConstants.EXTRA_NOTICE_TEXT);
		}
		initView();
	}

	private void initView() {
		getTitleBar().
				setTitle("编辑公告").
				setRightText("提交", new OnClickListener() {

					@Override
					public void onClick(View v) {
						startRequestForEditUserShopNotice();
					}
				});
		etNotice=(EditText)findViewById(R.id.etNotice);
		tvInputCount = (TextView) findViewById(R.id.tvInputCount);
		etNotice.addTextChangedListener(new TextWatcher() {  
		    @Override  
		    public void afterTextChanged(Editable s) {  
		    }  
		  
		    @Override  
		    public void beforeTextChanged(CharSequence s, int start, int count,  
		            int after) {  
		    }  
		  
		    @Override  
		    public void onTextChanged(CharSequence s, int start, int before,  
		            int count) {  
		        String content = etNotice.getText().toString();  
		        tvInputCount.setText(content.length() + "/"  
		                + "48");  
		    }  
		  
		});  
		if(!TextUtils.isEmpty(notice)) {
			etNotice.setText(notice);
		}
	}

	
	/**
	 * 编辑公告
	 * @param 
	 */
	private void startRequestForEditUserShopNotice(){
		if(!CommonUtils.isNetAvaliable(this)) {
			ToastUtil.toast(this, "请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(EditTextNoticeActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userId", String.valueOf(userId));
		params.put("notice", etNotice.getText().toString());
		LogUtil.i("liang", "编辑公告parmas:" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.EDITUSERSHOPNOTICE, params,
				new StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						LogUtil.i("liang", "编辑公告:"+response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								Intent intent = new Intent();
								intent.putExtra(MyConstants.EXTRA_NOTICE, etNotice.getText().toString());
								setResult(RESULT_OK, intent);
								finish();
							} else {
								CommonUtils.error(baseResult, EditTextNoticeActivity.this, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});
	}
}
