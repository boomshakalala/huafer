package com.huapu.huafen.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.Config;
import com.umeng.analytics.MobclickAgent;

/**
 * 关于我们
 * @author liang_xs
 *
 */
public class AboutActivity extends BaseActivity {
	private TextView tvAppVersion;
	private View layoutContactUs, layoutZhuCe;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		initView();
		String appVersion = CommonUtils.getAppVersionName();// 获取版本号
		tvAppVersion.setText("v" + appVersion+ (Config.DEBUG?"_DEBUG":""));
	}

	private void initView() {
		setTitleString("关于花粉儿");
		tvAppVersion = (TextView) findViewById(R.id.tvAppVersion);
		layoutZhuCe = findViewById(R.id.layoutZhuCe);
		layoutContactUs = findViewById(R.id.layoutContactUs);
		
		layoutContactUs.setOnClickListener(this);
		layoutZhuCe.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		Intent intent ;
		switch (v.getId()) {
		case R.id.btnTitleBarLeft:
			finish();
			break;

		case R.id.layoutZhuCe:// 条款与规范
			intent = new Intent(this, WebViewActivity.class);
			intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.WEBVIEW_ZHUCE);
			intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "条款与规范");
			startActivity(intent);
			break;
		case R.id.layoutContactUs:// 联系我们
			intent = new Intent(this, WebViewActivity.class);
			intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.CONTACTUS);
			intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "联系我们");
			startActivity(intent);
			break;
			
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		MobclickAgent.onPause(this);
	}
}
