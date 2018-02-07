package com.huapu.huafen.activity;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BindListActivity extends BaseActivity {
	private RelativeLayout bindALipay;
	private RelativeLayout bindWechat;
	private TextView tvBindAlipay;
	private TextView tvBindWechat;
	private ImageView ivAlipayArrow,ivWechatArrow;
	private boolean isBindALi,isBindWechat;
	private final static int BIND_TO_ALIPAY=0x1111;
	private final static int BIND_TO_WECHAT=BIND_TO_ALIPAY+1;
	private final static String BIND_OK="OK";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_list);
		
		Intent intent = getIntent();
		if(intent.hasExtra("isBindALi")){
			isBindALi=intent.getBooleanExtra("isBindALi", false);
			LogUtil.e("BindList onCreate", "isBindALi"+isBindALi);
		}
		
		if(intent.hasExtra("isBindWechat")){
			isBindWechat=intent.getBooleanExtra("isBindWechat", false);
			LogUtil.e("BindList onCreate", "isBindWechat"+isBindWechat+"");
		}
		initView();
	}
	
	private void initView(){
		setTitleString("账号绑定");
		bindALipay=(RelativeLayout) findViewById(R.id.bindALipay);
		bindWechat=(RelativeLayout) findViewById(R.id.bindWechat);
		
		bindALipay.setOnClickListener(this);
		bindWechat.setOnClickListener(this);
		tvBindAlipay=(TextView) findViewById(R.id.tvBindAlipay);
		tvBindWechat=(TextView) findViewById(R.id.tvBindWechat);
		ivAlipayArrow=(ImageView) findViewById(R.id.ivAlipayArrow);
		ivWechatArrow=(ImageView) findViewById(R.id.ivWechatArrow);
		
		if(isBindALi){
			tvBindAlipay.setTextColor(getResources().getColor(R.color.text_color_gray));
			tvBindAlipay.setText("已绑定");
			ivAlipayArrow.setVisibility(View.INVISIBLE);
			bindALipay.setEnabled(false);
		}else{
			tvBindAlipay.setTextColor(getResources().getColor(R.color.text_color));
			tvBindAlipay.setText("未绑定");
			ivAlipayArrow.setVisibility(View.VISIBLE);
			bindALipay.setEnabled(true);
		}
		
		if(isBindWechat){
			tvBindWechat.setTextColor(getResources().getColor(R.color.text_color_gray));
			tvBindWechat.setText("已绑定");
			ivWechatArrow.setVisibility(View.INVISIBLE);
			bindWechat.setEnabled(false);
		}else{
			tvBindWechat.setTextColor(getResources().getColor(R.color.text_color));
			tvBindWechat.setText("未绑定");
			ivWechatArrow.setVisibility(View.VISIBLE);
			bindWechat.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent=null;
		switch (v.getId()) {
		case R.id.btnTitleBarLeft:
			finish();
			break;
		case R.id.bindALipay:
			intent = new Intent();
			intent.setClass(this, BindALiActivity.class);
			startActivityForResult(intent, BIND_TO_ALIPAY);
			break;

		case R.id.bindWechat:
			intent = new Intent();
			intent.setClass(this, BindWechatActivity.class);
			startActivityForResult(intent, BIND_TO_WECHAT);
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK){
			if(requestCode==BIND_TO_ALIPAY){
				String aliBind=data.getStringExtra("bindAlipay");
				LogUtil.e("BindListActivity onActivityResult" ,"bindAlipay");
				if(BIND_OK.equals(aliBind)){
					LogUtil.e("BindListActivity onActivityResult","bindAlipay OK");
					Intent intent = new Intent();
					intent.putExtra("BIND_RESULT",2);
					setResult(RESULT_OK,intent);
					finish();
				}
				
			}else if(requestCode==BIND_TO_WECHAT){
				String wechatBind=data.getStringExtra("bindWechat");
				LogUtil.e("BindListActivity onActivityResult","bindWechat");
				if(BIND_OK.equals(wechatBind)){
					LogUtil.e("BindListActivity onActivityResult","bindWechat OK");
					Intent intent = new Intent();
					intent.putExtra("BIND_RESULT",1);
					setResult(RESULT_OK,intent);
					finish();
				}
			}
		}
	}
	
	
	
	
}
