package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.dialog.DateChooseDialogNew;
import com.huapu.huafen.events.PushEvent;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.LogUtil;
import de.greenrobot.event.EventBus;

/**
 * 选择预产期
 * @author liang_xs
 *
 */
public class ChoosePregnancyActivity extends BaseActivity {
	private TextView tvChooseDate, tvBtnConfirm;
	private ImageView ivChooseDate;
	private String dueDate;
	private String extraMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		EventBus.getDefault().register(this);
		if(intent.hasExtra(MyConstants.EXTRA_MAP)){
			extraMap = intent.getStringExtra(MyConstants.EXTRA_MAP);
		}
		setContentView(R.layout.activity_choose_pregnancy);
		initView();
	}
	private void initView() {
		setTitleString("预产期");
		ivChooseDate = (ImageView) findViewById(R.id.ivChooseDate);
		tvBtnConfirm = (TextView) findViewById(R.id.tvBtnConfirm);
		tvChooseDate = (TextView) findViewById(R.id.tvChooseDate);
		
		tvChooseDate.setOnClickListener(this);
		tvBtnConfirm.setOnClickListener(this);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvChooseDate:
			String date = "";
			if(tvChooseDate.getText().toString().equals("选择时间")) {
				date = "";
			} else {
				date = tvChooseDate.getText().toString();
			}
			DateChooseDialogNew dateDialog = new DateChooseDialogNew(ChoosePregnancyActivity.this ,DateChooseDialogNew.FUTURE_MODE,Calendar.getInstance().get(Calendar.YEAR)+2,date);
			dateDialog.setDialogTitle("日期选择");
			dateDialog.setOnClickListener(new DateChooseDialogNew.ChooseDateCallback(){

				@Override
				public void dateItemOnClic(String dateString) {
					tvChooseDate.setText(dateString);
					dueDate = DateTimeUtils.getTime("yyyy-MM-dd", dateString);
					
				}});
			dateDialog.show();
			break;
		case R.id.tvBtnConfirm:
			if(TextUtils.isEmpty(dueDate)) {
				toast("请选择预产期时间");
				return;
			}
			CommonPreference.setStringValue(CommonPreference.PREGNANTSTAT, "2");
			CommonPreference.setStringValue(CommonPreference.DUEDATE, dueDate);
//			Intent intent = new Intent(ChoosePregnancyActivity.this, MainActivity.class);
//			intent.putExtra(MyConstants.EXTRA_MAP,extraMap);
//			startActivity(intent);
			setResult(RESULT_OK);
			finish();
			break;
			
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
