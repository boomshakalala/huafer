package com.huapu.huafen.activity;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.dialog.DateChooseDialog;
import com.huapu.huafen.dialog.DateChooseDialog.ChooseDateCallback;
import com.huapu.huafen.dialog.DateChooseDialogNew;
import com.huapu.huafen.events.PushEvent;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.LogUtil;

import de.greenrobot.event.EventBus;

/**
 * 选择孩子
 * @author liang_xs
 *
 */
@SuppressLint("NewApi")
public class ChooseChildActivity extends BaseActivity implements OnClickListener  {
	private TextView tvChooseFirstDate, tvChooseSecondDate, tvBtnConfirm;
	private Button btnCheckedFirstMale, btnCheckedFirstFemale, btnCheckedSecondMale, btnCheckedSecondFemale;
	private String firstBabyBirth, secondBabyBirth;
	private String firstSex, secondSex;
	private View layoutSecondBaby;
	private Button btnSecondSelect;
	private String extraMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_child);
		EventBus.getDefault().register(this);
		Intent intent = getIntent();
		if(intent.hasExtra(MyConstants.EXTRA_MAP)){
			extraMap = intent.getStringExtra(MyConstants.EXTRA_MAP);
		}
		initView();
	}
	private void initView() {
		getTitleBar().
				setTitle("选择孩子").
				setRightText("体验", new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(TextUtils.isEmpty(firstSex)) {
							toast("请选择孩子性别");
							return;
						}
						if(TextUtils.isEmpty(firstBabyBirth)) {
							toast("请选择孩子生日");
							return;
						}
						if(TextUtils.isEmpty(secondSex) && btnSecondSelect.isSelected()) {
							toast("请选择二孩性别");
							return;
						}
						if(TextUtils.isEmpty(secondBabyBirth) && btnSecondSelect.isSelected()) {
							toast("请选择二孩生日");
							return;
						}
						CommonPreference.setStringValue(CommonPreference.PREGNANTSTAT, "3");
						CommonPreference.setStringValue(CommonPreference.FIRSRBABYSEX, firstSex);
						CommonPreference.setStringValue(CommonPreference.FIRSTBABYBIRTH, firstBabyBirth);
						if(btnSecondSelect.isSelected()) {
							CommonPreference.setStringValue(CommonPreference.SECONDBABYSEX, secondSex);
							CommonPreference.setStringValue(CommonPreference.SECONDBABYBIRTH, secondBabyBirth);
						}

						setResult(RESULT_OK);
						finish();
					}
				});
		btnCheckedFirstMale = (Button) findViewById(R.id.btnCheckedFirstMale);
		btnCheckedFirstFemale = (Button) findViewById(R.id.btnCheckedFirstFemale);
		tvChooseFirstDate = (TextView) findViewById(R.id.tvChooseFirstDate);
		btnCheckedSecondMale = (Button) findViewById(R.id.btnCheckedSecondMale);
		btnCheckedSecondFemale = (Button) findViewById(R.id.btnCheckedSecondFemale);
		tvChooseSecondDate = (TextView) findViewById(R.id.tvChooseSecondDate);
		tvBtnConfirm = (TextView) findViewById(R.id.tvBtnConfirm);
		btnSecondSelect = (Button) findViewById(R.id.btnSecondSelect);
		layoutSecondBaby = findViewById(R.id.layoutSecondBaby);
		
		btnCheckedFirstMale.setOnClickListener(this);
		btnCheckedFirstFemale.setOnClickListener(this);
		tvChooseFirstDate.setOnClickListener(this);
		btnCheckedSecondMale.setOnClickListener(this);
		btnCheckedSecondFemale.setOnClickListener(this);
		tvChooseSecondDate.setOnClickListener(this);
		tvBtnConfirm.setOnClickListener(this);
		btnSecondSelect.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnTitleBarLeft:
			finish();
			break;
		case R.id.btnCheckedFirstMale:
			firstSex = "1";
			btnCheckedFirstMale.setSelected(true);
			btnCheckedFirstFemale.setSelected(false);
			break;
		case R.id.btnCheckedFirstFemale:
			firstSex = "0";
			btnCheckedFirstFemale.setSelected(true);
			btnCheckedFirstMale.setSelected(false);
			break;
		case R.id.tvChooseFirstDate:
			String date = "";
			if(tvChooseFirstDate.getText().toString().equals("选择时间")) {
				date = "";
			} else {
				date = tvChooseFirstDate.getText().toString();
			}
			DateChooseDialogNew dateDialog = new DateChooseDialogNew(ChooseChildActivity.this ,DateChooseDialogNew.PASTTIME_MODE,Calendar.getInstance().get(Calendar.YEAR)-16,date);
			dateDialog.setDialogTitle("日期选择");
			dateDialog.setOnClickListener(new DateChooseDialogNew.ChooseDateCallback(){

				@Override
				public void dateItemOnClic(String dateString) {
					tvChooseFirstDate.setText(dateString);
					firstBabyBirth = DateTimeUtils.getTime("yyyy-MM-dd", dateString);
					btnSecondSelect.setVisibility(View.VISIBLE);
					btnSecondSelect.setSelected(false);
				}});
			dateDialog.show();
			break;
		case R.id.btnCheckedSecondMale:
			secondSex = "1";
			btnCheckedSecondMale.setSelected(true);
			btnCheckedSecondFemale.setSelected(false);
			break;
		case R.id.btnCheckedSecondFemale:
			secondSex = "0";
			btnCheckedSecondFemale.setSelected(true);
			btnCheckedSecondMale.setSelected(false);
			break;
		case R.id.tvChooseSecondDate:
			String dateSecond = "";
			if(tvChooseSecondDate.getText().toString().equals("选择时间")) {
				dateSecond = "";
			}else {
				dateSecond = tvChooseSecondDate.getText().toString();
			}
			DateChooseDialog dateDialog2 = new DateChooseDialog(ChooseChildActivity.this ,dateSecond);
			dateDialog2.setDialogTitle("日期选择");
			dateDialog2.setOnClickListener(new ChooseDateCallback(){

				@Override
				public void dateItemOnClic(String dateString) {
					long time = DateTimeUtils.getLongTime("yyyy-MM-dd", dateString);
					long systemTime = System.currentTimeMillis();
					if(time > systemTime) {
						toast("您选择了未来时间，请重新选择");
						return;
					}
					tvChooseSecondDate.setText(dateString);
					secondBabyBirth = DateTimeUtils.getTime("yyyy-MM-dd", dateString);
				}});
			dateDialog2.show();
			
			break;
		case R.id.tvBtnConfirm:
			
			break;
			
		case R.id.btnSecondSelect:
			if(btnSecondSelect.isSelected()) {
				btnSecondSelect.setSelected(false);
				btnSecondSelect.setText("还有宝宝");
				btnSecondSelect.setBackgroundResource(R.drawable.text_white_round_pink_stroke_bg);
				layoutSecondBaby.setVisibility(View.GONE);
			} else {
				btnSecondSelect.setSelected(true);
				btnSecondSelect.setText("关闭");
				btnSecondSelect.setBackgroundResource(R.drawable.text_white_round_pink_stroke_bg);
				layoutSecondBaby.setVisibility(View.VISIBLE);
			}
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
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
