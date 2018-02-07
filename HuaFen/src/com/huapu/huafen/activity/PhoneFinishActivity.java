package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;

/**
 * 手机号绑定成功
 * @author liang_xs
 *
 */
public class PhoneFinishActivity extends BaseActivity {

	private TextView tvPhoneNumber;
	private String phoneNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_finish);
		Intent intent = getIntent();
		if(intent.hasExtra(MyConstants.EXTRA_PHONE_NUMBER)){
			phoneNum = intent.getStringExtra(MyConstants.EXTRA_PHONE_NUMBER);
		}
		initView();

	}

	private void initView() {
		setTitleString("绑定手机号");
		tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
		tvPhoneNumber.setText("您的手机号："+phoneNum);
	}

	
}
