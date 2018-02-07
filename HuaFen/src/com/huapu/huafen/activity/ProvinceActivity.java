package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ProvinceListAdapter;
import com.huapu.huafen.beans.City;
import com.huapu.huafen.beans.District;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.Region;
import com.huapu.huafen.utils.CommonPreference;

import java.util.ArrayList;

/** 
 * @ClassName: ProvinceActivity
 * @Description:  省份选择
 * @author liang_xs
 * @date 2014-8-25
 */
public class ProvinceActivity extends BaseActivity {
	private RecyclerView recyclerView;
	private ProvinceListAdapter adapter;
	private View header;
	private LocationData locationData;
	private ArrayList<Region> regions;
	private TextView tvRegion;
	private boolean isLocation = false;


	/* (non-Javadoc)
	 * @see activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_province);
		initView();
		if(getIntent().hasExtra("isLocation")) {
			isLocation = getIntent().getBooleanExtra("isLocation", false);
		}
		regions = CommonPreference.getRegions();
		adapter = new ProvinceListAdapter(this, regions);
		recyclerView.setAdapter(adapter.getWrapperAdapter());
		if(isLocation) {
			initLocation();
		}
	}

	private void initLocation() {
		locationData = CommonPreference.getLocalData();
		if(locationData != null) {
			final String province = locationData.province;
			final String city = locationData.city;
			final String district = locationData.district;
			if(!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(district)) {
				header = LayoutInflater.from(this).inflate(R.layout.view_headview_province, recyclerView, false);
				tvRegion = (TextView) header.findViewById(R.id.tvRegion);
				tvRegion.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						District districtData = new District();
						districtData.setName(district);
						City cityData = new City();
						cityData.setName(city);
						Region region = new Region();
						region.setName(province);
						Intent intent = new Intent();
						intent.putExtra(MyConstants.EXTRA_DISTRICT, districtData);
						intent.putExtra(MyConstants.EXTRA_CITY, cityData);
						intent.putExtra(MyConstants.EXTRA_REGION, region);
						setResult(RESULT_OK, intent);
						finish();
					}
				});
				if(province.equals(city)) {
					tvRegion.setText(province + " " + district);
				} else {
					tvRegion.setText(province + " " + city + " " + district);
				}
				adapter.addHeaderView(header);
			}
		}
	}

	private void initView(){
		setTitleString("地区选择");
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(layoutManager);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnTitleBarLeft:
				finish();
				break;

		}
	}

	@Override
	public void onTitleBarDoubleOnClick() {
		recyclerView.smoothScrollToPosition(0);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_CITIES) {
				if(data == null) {
					return;
				}
				District district = (District) data.getSerializableExtra(MyConstants.EXTRA_DISTRICT);
				City city = (City) data.getSerializableExtra(MyConstants.EXTRA_CITY);
				Intent intent = new Intent();
				intent.putExtra(MyConstants.EXTRA_DISTRICT, district);
				intent.putExtra(MyConstants.EXTRA_CITY, city);
				intent.putExtra(MyConstants.EXTRA_REGION, adapter.getSelectRegion());
				setResult(RESULT_OK, intent);
				finish();
			} else if(requestCode == MyConstants.INTENT_FOR_RESULT_TO_DISTRICTS) {
				if(data == null) {
					return;
				}
				District district = (District) data.getSerializableExtra(MyConstants.EXTRA_DISTRICT);
				Intent intent = new Intent();
				intent.putExtra(MyConstants.EXTRA_DISTRICT, district);
				intent.putExtra(MyConstants.EXTRA_REGION, adapter.getSelectRegion());
				setResult(RESULT_OK, intent);
				finish();
			}
		}

	}

}
