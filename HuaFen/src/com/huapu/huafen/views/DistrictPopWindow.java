package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.expandtab.ExpandTabView;
import com.huapu.huafen.expandtab.TextAdapter;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

public class DistrictPopWindow extends PopupWindow {
	private View conentView;
	private ListView mListView;
	private ArrayList<Area> list = new ArrayList<Area>();
	private OnSelectListener mOnSelectListener;
	private TextAdapter adapter;
//	private final String[] items = new String[] { "item1", "item2", "item3", "item4", "item5", "item6" };//显示字段
	private DelTextView tvCity, tvDistrict;
	private View layoutCity, layoutDistrict;
	private int areId;
	private int cityCode;
	private int cityId;
	String items[] ;
	private Activity mContext;
	
	public DistrictPopWindow(final Activity context, final ExpandTabView view, final String city, final String district, final int cityId) {
		this.mContext = context;
		this.cityId = cityId;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.pop_district, null);
		int heiht = context.getWindowManager().getDefaultDisplay().getHeight();
		int width = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置SelectPicPopupWindow的View
		this.setContentView(conentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(width);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight((int)(heiht * 0.7));
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
//		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
//		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.expandtab_bg_left));
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.PopAnimation);
		mListView = (ListView) conentView.findViewById(R.id.listView);
//		layoutCity = conentView.findViewById(R.id.layoutCity);
//		layoutDistrict = conentView.findViewById(R.id.layoutDistrict);
		tvCity = (DelTextView) conentView.findViewById(R.id.tvCity);
		tvDistrict = (DelTextView) conentView.findViewById(R.id.tvDistrict);
		tvCity.setShowDel(true);
		tvDistrict.setShowDel(true);
		if(TextUtils.isEmpty(district)) {
			tvDistrict.setVisibility(View.GONE);
		} else {
			tvDistrict.setVisibility(View.VISIBLE);
		}
		tvCity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mOnSelectListener != null) {
					context.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tvCity.setText("");
							tvDistrict.setText("");
							tvCity.setVisibility(View.GONE);
							tvDistrict.setVisibility(View.GONE);
							view.setTitle("区域", 0);
							CommonPreference.setBooleanValue(CommonPreference.IS_SELECT_DISTRICT, false);
							CommonPreference.setStringValue(CommonPreference.SELECT_CITY, "");
							CommonPreference.setIntValue(CommonPreference.SELECT_CITY_ID, -1);
							CommonPreference.setStringValue(CommonPreference.SELECT_DISTRICT, "");
							DistrictPopWindow.this.dismiss();
						}
					});
				}
			}
		});
		tvDistrict.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mOnSelectListener != null) {
					context.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							CommonPreference.setStringValue(CommonPreference.SELECT_DISTRICT, "");
							CommonPreference.setIntValue(CommonPreference.SELECT_CITY_ID, cityId);
							tvDistrict.setText("");
							tvDistrict.setVisibility(View.GONE);
							view.setTitle("区域", 0);
							adapter.setText("");
							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		});
		if(city.equals("区域")) {
			tvCity.setText("");
		}
		tvCity.setText(city);
		tvDistrict.setText(district);
		startRequestForGetAreaData(cityId, city, district);
	}
	

	/**
	 * 获取区域列表
	 */
	private void startRequestForGetAreaData(final int cityId, final String city, final String district) {
		if(!CommonUtils.isNetAvaliable(mContext)) {
			ToastUtil.toast(mContext, "请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(mContext);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("cityId", String.valueOf(cityId));
		LogUtil.i("liang", "params:"+params.toString());
		OkHttpClientManager.postAsyn(MyConstants.GETAREAS, params, new StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				LogUtil.i("liang", "获取区域：" + e.toString());
				ProgressDialog.closeProgress();
			}


			@Override
			public void onResponse(String response) {
				ProgressDialog.closeProgress();
				LogUtil.i("liang", "获取区域：" + response.toString());
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if(!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if(!TextUtils.isEmpty(baseResult.obj)) {
							list = (ArrayList<Area>) ParserUtils.parserAreaListData(baseResult.obj);
							if(list != null) {
								Area area = new Area();
								area.setArea("全部");
								area.setAreaId(0);
								list.add(0, area);
								items = new String[list.size()];
								for(int i = 0; i< list.size(); i++) {
									items[i] = list.get(i).getArea();
								}
								adapter = new TextAdapter(mContext, items, district, 0, R.drawable.expandtab_item_selector);
								adapter.setTextSize(17);
								mListView.setAdapter(adapter);
//								mListView.setOnItemClickListener(new OnItemClickListener() {
						//
//									@Override
//									public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//											long arg3) {
//										DistrictPopWindow.this.dismiss();
//									}
//								});
								adapter.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

									@Override
									public void onItemClick(View view, int position) {

										if (mOnSelectListener != null) {
											final String showText = items[position];
											for(int i = 0; i < list.size(); i++) {
												if(list.get(i).getArea().equals(showText)) {
													areId = list.get(i).getAreaId();
												}
											}
											if(!TextUtils.isEmpty(items[position])) {
												if(items[position].equals("全部")) {
													items[position] = city;
												}
											}
											mOnSelectListener.getValue(cityId, areId, items[position]);
											mContext.runOnUiThread(new Runnable() {
												
												@Override
												public void run() {
													CommonPreference.setBooleanValue(CommonPreference.IS_SELECT_DISTRICT, true);
													CommonPreference.setStringValue(CommonPreference.SELECT_CITY, city);
													CommonPreference.setStringValue(CommonPreference.SELECT_DISTRICT, showText);
													CommonPreference.setIntValue(CommonPreference.SELECT_CITY_ID, cityId);
//													ToastUtil.toast(context, showText);
													DistrictPopWindow.this.dismiss();
												}
											});
										}
									}
								});
							}
						}
						
					} else {
						CommonUtils.error(baseResult, mContext, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, 0, 0);
		} else {
			this.dismiss();
		}
	}
	
	public void setDistrict(String showText){
		tvDistrict.setText(showText);
	}
	
	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(int cityId, int disId, String showText);
	}
}
