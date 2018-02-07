package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CityListAdapter;
import com.huapu.huafen.beans.City;
import com.huapu.huafen.beans.District;

import java.util.ArrayList;

/** 
 * @ClassName: CityActivity
 * @Description:  城市选择
 * @author liang_xs
 * @date 2014-8-25
 */
public class CityActivity extends BaseActivity {
	private RecyclerView recyclerView;
	private CityListAdapter adapter;
	private ArrayList<City> cities;


	/* (non-Javadoc)
	 * @see activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_province);
		initView();
		if(getIntent().hasExtra(MyConstants.EXTRA_CITIES)) {
			cities = (ArrayList<City>) getIntent().getSerializableExtra(MyConstants.EXTRA_CITIES);
		}
		adapter = new CityListAdapter(this, cities);
		recyclerView.setAdapter(adapter);
	}

	private void initView(){
		setTitleString("城市选择");
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(layoutManager);
	}

	@Override
	public void onTitleBarDoubleOnClick() {
		recyclerView.smoothScrollToPosition(0);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_DISTRICTS) {
				if(data == null) {
					return;
				}
				District district = (District) data.getSerializableExtra(MyConstants.EXTRA_DISTRICT);
				Intent intent = new Intent();
				intent.putExtra(MyConstants.EXTRA_DISTRICT, district);
				intent.putExtra(MyConstants.EXTRA_CITY, adapter.getSelectCity());
				setResult(RESULT_OK, intent);
				finish();
			}
		}

	}

}
