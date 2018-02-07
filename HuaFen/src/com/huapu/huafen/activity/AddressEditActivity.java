package com.huapu.huafen.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.City;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.beans.District;
import com.huapu.huafen.beans.Region;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

/**
 * 收货地址编辑
 * @author liang_xs
 *
 */
public class AddressEditActivity extends BaseActivity {
	private int addressType = 1;
	private String title;
	private Consignee bean = new Consignee();
	private ArrayList<Consignee> list = new ArrayList<Consignee>();
	private View layoutCity;
	private EditText etConsigneeName, etConsigneePhone, etConsigneeAddress;
	private TextView tvConsigneeCity, tvDefault;
	private int areaId;
	private int cityId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_edit);
		if(getIntent().hasExtra(MyConstants.EXTRA_ADDRESS)) {
			bean = (Consignee) getIntent().getSerializableExtra(MyConstants.EXTRA_ADDRESS);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_ADDRESS_TYPE)) {
			addressType = getIntent().getIntExtra(MyConstants.EXTRA_ADDRESS_TYPE, -1);
		}
		
		switch (addressType) {
		case MyConstants.TO_ADDRESS_EDIT:
			title = "修改地址";
			break;

		case MyConstants.TO_ADDRESS_ADD:
			title = "新增地址";
			break;
		}
		initView();
		if(bean != null) {
			etConsigneeName.setText(bean.getConsigneeName());
			etConsigneePhone.setText(bean.getConsigneePhone());
			etConsigneeAddress.setText(bean.getConsigneeAddress());
			if(bean.getArea() != null) {
				areaId = bean.getArea().getAreaId();
				cityId = bean.getArea().getCityId();
				tvConsigneeCity.setText(bean.getArea().getCity() + bean.getArea().getArea());
			}
			if(bean.getConsigneesId() != null) {
				tvDefault.setVisibility(View.VISIBLE);
			}
			
		} 
		
	}
	private void initView() {

		getTitleBar().setTitle(title).setRightText("保存", new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = etConsigneeName.getText().toString().trim();
				String phone = etConsigneePhone.getText().toString().trim();
				String address = etConsigneeAddress.getText().toString().trim();
				if(TextUtils.isEmpty(name)) {
					toast("请填写收货人姓名");
					return;
				}
				if(TextUtils.isEmpty(phone)) {
					toast("请填写收货人手机号");
					return;
				}
				if(TextUtils.isEmpty(address)) {
					toast("请填写收货详细地址");
					return;
				}
				if(cityId == 0) {
					toast("请选择城市");
					return;
				}
				if(areaId == 0) {
					toast("请选择区域");
					return;
				}
				bean.setConsigneeName(name);
				bean.setConsigneeAddress(address);
				bean.setConsigneePhone(phone);
				startRequestForAddConsigneesList();
			}
		});
		layoutCity = findViewById(R.id.layoutCity);
		etConsigneeName = (EditText) findViewById(R.id.etConsigneeName);
		etConsigneePhone = (EditText) findViewById(R.id.etConsigneePhone);
		tvConsigneeCity = (TextView) findViewById(R.id.tvConsigneeCity);
		etConsigneeAddress = (EditText) findViewById(R.id.etConsigneeAddress);
		tvDefault = (TextView) findViewById(R.id.tvDefault);
		
		layoutCity.setOnClickListener(this);
		tvDefault.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent intent ;
		switch (v.getId()) {
		case R.id.btnTitleBarLeft:
			finish();
			break;
		case R.id.layoutCity:
			intent = new Intent(AddressEditActivity.this, ProvinceActivity.class);
			intent.putExtra("isLocation", false);
			startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_PROVINCE);
			break;
			
		case R.id.tvDefault:
			startRequestForSetDefault();
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_CHOOSE_CITY) {
				// 获取返回值，将返回数据设置到bean中
				String city = data.getStringExtra(MyConstants.EXTRA_CHOOSE_CITYNAME);
				String district = data.getStringExtra(MyConstants.EXTRA_CHOOSE_DISNAME);
				areaId = data.getIntExtra(MyConstants.EXTRA_CHOOSE_DISID, 0);
				cityId = data.getIntExtra(MyConstants.EXTRA_CHOOSE_CITYID, 0);
				tvConsigneeCity.setText(city+" "+district);
				Area area = new Area();
				area.setCity(city);
				area.setArea(district);
				area.setAreaId(areaId);
				area.setCityId(cityId);
				bean.setArea(area);
			} else if(requestCode == MyConstants.INTENT_FOR_RESULT_TO_PROVINCE) {
				if(data == null) {
					return;
				}
				String province = "";
				String city = "";
				String district = "";
				Region regionData = new Region();
				if(data.hasExtra(MyConstants.EXTRA_REGION)) {
					regionData = (Region) data.getSerializableExtra(MyConstants.EXTRA_REGION);
					province = regionData.getName();
				}
				if(data.hasExtra(MyConstants.EXTRA_CITY)) {
					City cityData = (City) data.getSerializableExtra(MyConstants.EXTRA_CITY);
					cityId = cityData.getDid();
					city = cityData.getName();
				} else {
					cityId = regionData.getDid();
					city = province;
				}
				if(data.hasExtra(MyConstants.EXTRA_DISTRICT)) {
					District districtData = (District) data.getSerializableExtra(MyConstants.EXTRA_DISTRICT);
					areaId = districtData.getDid();
					district = districtData.getName();
				}
				tvConsigneeCity.setText(city+" "+district);
				Area area = new Area();
				area.setCity(city);
				area.setArea(district);
				area.setAreaId(areaId);
				area.setCityId(cityId);
				bean.setArea(area);
			}
		}
	}
	/**
	 * 从服务端获取收货地址列表
	 */
//	private void startRequestForAddConsigneesData() {
//		if(TextUtils.isEmpty(CommonPreference.getAppToken())) {
//			startRequestForGetSyncConfig();
//		} else {
//			startRequestForAddConsigneesList();
//		}
//
//	}

//	private void startRequestForGetSyncConfig() {
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put(MyConstants.REQUEST_VER, CommonUtils.getAppVersionName());
//		params.put(MyConstants.REQUEST_TOKEN, CommonPreference.getAppToken());
//		OkHttpClientManager.postAsyn(MyConstants.SYNCCONFIG, params, new StringCallback() {
//
//			@Override
//			public void onError(Request request, Exception e) {
//			}
//
//
//			@Override
//			public void onResponse(String response) {
//				LogUtil.i("liang", "配置同步接口:" + response.toString());
//				JsonValidator validator = new JsonValidator();
//				boolean isJson = validator.validate(response);
//				if(!isJson) {
//					return;
//				}
//				try {
//					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
//					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
//						if(!TextUtils.isEmpty(baseResult.obj)) {
//							// 解析数据
//							ConfigBean configBean = ParserUtils.parserConfigData(baseResult.obj);
//							if(configBean != null) {
//								CommonPreference.setAppToken(configBean.getToken());
//								startRequestForAddConsigneesList();
//							}
//						}
//
//					} else {
//						CommonUtils.error(baseResult, AddressEditActivity.this, "");
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * 添加或修改收货地址
	 */
	private void startRequestForAddConsigneesList(){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(AddressEditActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("consigneeName", bean.getConsigneeName());
		params.put("consigneePhone", bean.getConsigneePhone());
		params.put("consigneeAddress", bean.getConsigneeAddress());
		params.put("consigneeCityId", bean.getArea().getCityId() + "");
		params.put("consigneeAreaId", bean.getArea().getAreaId() + "");
		if(bean.getConsigneesId() != null) {
			if (bean.getConsigneesId() != 0) {
				params.put("consigneesId", bean.getConsigneesId()+"");
			}
		}
		LogUtil.i("liang", "params:"+params.toString());
		
		OkHttpClientManager.postAsyn(MyConstants.SAVEUSERCONSIGNEEINFO, params,
				new StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						LogUtil.i("liang", "添加或修改收货地址:"+response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								if(!TextUtils.isEmpty(baseResult.obj)) {
//									bean = ParserUtils.parserAddressData(baseResult.obj);
									list = (ArrayList<Consignee>) ParserUtils.parserConsigneesListData(baseResult.obj);
									if(list != null) {
										Intent intent = new Intent();
										intent.putExtra(MyConstants.EXTRA_ADDRESS_LIST, list);
										setResult(RESULT_OK, intent);
										finish();
									}
								}
							} else {
								CommonUtils.error(baseResult, AddressEditActivity.this, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});
	}

	/**
	 * 设置默认收货地址
	 */
	private void startRequestForSetDefault() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("consigneesId", String.valueOf(bean.getConsigneesId()));
		OkHttpClientManager.postAsyn(MyConstants.SETDEFAULT, params, new StringCallback() {
			
			@Override
			public void onError(Request request, Exception e) {
			}
			
			
			@Override
			public void onResponse(String response) {
				LogUtil.i("liang", "设置默认收货地址:" + response.toString());
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if(!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if(!TextUtils.isEmpty(baseResult.obj)) {
							toast("设置默认收货地址成功");
							list = (ArrayList<Consignee>) ParserUtils.parserConsigneesListData(baseResult.obj);
							if(list != null) {
								Intent intent = new Intent();
								intent.putExtra(MyConstants.EXTRA_ADDRESS_LIST, list);
								setResult(RESULT_OK, intent);
								finish();
							}
						}
					} else {
						CommonUtils.error(baseResult, AddressEditActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
