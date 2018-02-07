package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Baby;
import com.huapu.huafen.utils.DateTimeUtils;

import java.util.ArrayList;

/**
 * 我的宝宝
 * @author liang_xs
 *
 */
public class MyChildActivity extends BaseActivity {
	private ArrayList<Baby> babies = new ArrayList<Baby>();
	private View layoutFirstChild, layoutSecondChild;
	private TextView tvFirstChildSex, tvFirstChildBirthday, tvSecondChildSex, tvSecondChildBirthday;
	private TextView tvBtnAdd;
	private View viewFirst, viewSecond;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_child);
		initView();
		if(getIntent().hasExtra(MyConstants.EXTRA_BABY_LIST)) {
			babies = (ArrayList<Baby>) getIntent().getSerializableExtra(MyConstants.EXTRA_BABY_LIST);
		}
		initData();
	}
	private void initData() {
		if (babies != null) {
			if (babies.size() == 0) {
				layoutFirstChild.setVisibility(View.GONE);
				layoutSecondChild.setVisibility(View.GONE);
				viewFirst.setVisibility(View.GONE);
				viewSecond.setVisibility(View.GONE);
				tvBtnAdd.setVisibility(View.VISIBLE);
				tvBtnAdd.setText("编辑");
			} else if (babies.size() == 1) {
				layoutFirstChild.setVisibility(View.VISIBLE);
				layoutSecondChild.setVisibility(View.GONE);
				viewFirst.setVisibility(View.VISIBLE);
				viewSecond.setVisibility(View.GONE);
				tvBtnAdd.setVisibility(View.VISIBLE);
				tvBtnAdd.setText("还有宝宝");
				if(babies.get(0).getSex() == 0) {
					tvFirstChildSex.setText("小公主");
				} else {
					tvFirstChildSex.setText("小王子");
				}
				tvFirstChildBirthday.setText(DateTimeUtils.getYearMonthDay(babies.get(0).getDateOfBirth()));
			} else if (babies.size() == 2) {
				layoutFirstChild.setVisibility(View.VISIBLE);
				layoutSecondChild.setVisibility(View.VISIBLE);
				viewFirst.setVisibility(View.VISIBLE);
				viewSecond.setVisibility(View.VISIBLE);
				tvBtnAdd.setVisibility(View.GONE);
				if(babies.get(0).getSex() == 0) {
					tvFirstChildSex.setText("小公主");
				} else {
					tvFirstChildSex.setText("小王子");
				}
				tvFirstChildBirthday.setText(DateTimeUtils.getYearMonthDay(babies.get(0).getDateOfBirth()));
				if(babies.get(1).getSex() == 0) {
					tvSecondChildSex.setText("小公主");
				} else {
					tvSecondChildSex.setText("小王子");
				}
				tvSecondChildBirthday.setText(DateTimeUtils.getYearMonthDay(babies.get(1).getDateOfBirth()));
			}
		}
	}
	private void initView() {
		setTitleString("我的宝宝");
		layoutFirstChild = findViewById(R.id.layoutFirstChild);
		layoutSecondChild = findViewById(R.id.layoutSecondChild);
		viewFirst = findViewById(R.id.viewFirst);
		viewSecond = findViewById(R.id.viewSecond);
		tvFirstChildSex = (TextView) findViewById(R.id.tvFirstChildSex);
		tvFirstChildBirthday = (TextView) findViewById(R.id.tvFirstChildBirthday);
		tvSecondChildSex = (TextView) findViewById(R.id.tvSecondChildSex);
		tvSecondChildBirthday = (TextView) findViewById(R.id.tvSecondChildBirthday);
		tvBtnAdd = (TextView) findViewById(R.id.tvBtnAdd);
		
		layoutFirstChild.setOnClickListener(this);
		layoutSecondChild.setOnClickListener(this);
		tvBtnAdd.setOnClickListener(this);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra(MyConstants.EXTRA_BABY_LIST, babies);
		setResult(RESULT_OK, intent);
		finish();
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.layoutFirstChild:
			intent = new Intent(MyChildActivity.this, EditChildActivity.class);
			if(babies != null && babies.size() >= 1) {
				intent.putExtra(MyConstants.EXTRA_BABY_LIST, babies);
				intent.putExtra(MyConstants.EXTRA_BABY_POSITION, 0);
				intent.putExtra(MyConstants.EXTRA_BABY_EDIT_TYPE, 1);
			}
			startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_EDIT_CHILD);
			break;
		case R.id.layoutSecondChild:
			intent = new Intent(MyChildActivity.this, EditChildActivity.class);
			if(babies != null && babies.size() >= 2) {
				intent.putExtra(MyConstants.EXTRA_BABY_LIST, babies);
				intent.putExtra(MyConstants.EXTRA_BABY_POSITION, 1);
				intent.putExtra(MyConstants.EXTRA_BABY_EDIT_TYPE, 1);
			}
			startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_EDIT_CHILD);
			break;

		case R.id.tvBtnAdd:
			intent = new Intent(MyChildActivity.this, EditChildActivity.class);
			intent.putExtra(MyConstants.EXTRA_BABY_LIST, babies);
			intent.putExtra(MyConstants.EXTRA_BABY_EDIT_TYPE, 2);
			startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_EDIT_CHILD);
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_EDIT_CHILD) {
				babies = (ArrayList<Baby>) data.getSerializableExtra(MyConstants.EXTRA_BABY_LIST);
				initData();
			}
		}
	}
	
}
