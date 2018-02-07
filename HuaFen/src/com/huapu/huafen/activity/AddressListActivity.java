package com.huapu.huafen.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;
/**
 * 收货地址列表
 * @author liang_xs
 *
 */
public class AddressListActivity extends BaseActivity implements OnClickListener, OnItemClickListener  {
	private ListView addressListView;
	private ArrayList<Consignee> beans = new ArrayList<Consignee>();
	private MyListAdapter adapter;
	/**
	 * 1，从退款历史进入
	 */
	private int type;
	private Intent intent = new Intent();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_list);
		if(getIntent().hasExtra(MyConstants.EXTRA_ADDRESS)) {
			type = getIntent().getIntExtra(MyConstants.EXTRA_ADDRESS, 0);
		}
		initView();
		if(type == 1) {
			getTitleBar().getTitleTextRight().setText("发送");
		} else {
			getTitleBar().getTitleTextRight().setVisibility(View.GONE);
		}
		startRequestForGetAddressList();
	}
	private void initView() {
		getTitleBar().setTitle("收货地址").setRightText("添加", new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(beans == null || beans.size() == 0) {
					toast("请填写收货地址");
					return;
				}
				intent.putExtra(MyConstants.EXTRA_ADDRESS_LIST, beans);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		addressListView = (ListView) findViewById(R.id.addressListView);
		View view = View.inflate(this, R.layout.view_empty_textview, null);
		TextView textView = (TextView) view.findViewById(R.id.tvEmpty);
		textView.setText("添加地址");
		textView.setTextColor(getResources().getColor(R.color.white));
		textView.setBackgroundResource(R.drawable.text_pink_round_bg);
		addressListView.addFooterView(view);
		adapter = new MyListAdapter(this);
		addressListView.setAdapter(adapter);
		addressListView.setOnItemClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnTitleBarLeft:
			finish();
			break;
		}
	}

	/**
	 * 获取地址列表
	 */
	private void startRequestForGetAddressList(){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(AddressListActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		OkHttpClientManager.postAsyn(MyConstants.GETUSERCONSIGNEEINFO, params,
				new StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						LogUtil.i("liang", "地址列表:"+response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								if(!TextUtils.isEmpty(baseResult.obj)) {
									beans = (ArrayList<Consignee>) ParserUtils.parserConsigneesListData(baseResult.obj);
									if(beans != null) {
										adapter.setData(beans);
									}
								}
							} else {
								CommonUtils.error(baseResult, AddressListActivity.this, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});
	}
	
	class MyListAdapter extends BaseAdapter {
		
		private ArrayList<Consignee> addressBeans = new ArrayList<Consignee>();
		private Context mContext;
		
		public MyListAdapter(Context mContext) {
			super();
			this.mContext = mContext;
		}
		
		public void setData(ArrayList<Consignee> addressBeans) {
			this.addressBeans = addressBeans;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return addressBeans.size();
		}
		
		@Override
		public Object getItem(int position) {
			return addressBeans.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_listview_addresst, null);
				viewHolder = new ViewHolder();
				viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
				viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
				viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);
				viewHolder.ivDefault = (ImageView) convertView.findViewById(R.id.ivDefault);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(addressBeans != null) {
				if(addressBeans.get(position) != null) {
					viewHolder.tvName.setText(addressBeans.get(position).getConsigneeName() + " " + addressBeans.get(position).getConsigneePhone());
					Area area = addressBeans.get(position).getArea();
					String province = "";
					String city = "";
					String areaName = "";
					if(area != null) {
						province = area.getProvince();
						city = area.getCity();
						areaName = area.getArea();
					} else {
						province = "";
						city = "";
						areaName = "";
					}
					if(province.equals(city)) {
						province = "";
					}
					viewHolder.tvAddress.setText(province + city + areaName + addressBeans.get(position).getConsigneeAddress());
					if(addressBeans.get(position).getIsDefault()) {
						viewHolder.ivDefault.setVisibility(View.VISIBLE);
					} else {
						viewHolder.ivDefault.setVisibility(View.GONE);
					}
				}
			}

			return convertView;
		}
		
		class ViewHolder {
			public TextView tvName;
			public TextView tvPhone;
			public TextView tvAddress;
			private ImageView ivDefault;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(position == adapter.getCount()) { // 如果点击的是最后一个，则是添加新的收货地址
			intent = new Intent(AddressListActivity.this, AddressEditActivity.class);
			intent.putExtra(MyConstants.EXTRA_ADDRESS_TYPE, MyConstants.TO_ADDRESS_ADD);
			startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ADDRESS_EDIT);
			return;
		}
		Object item = parent.getItemAtPosition(position);
		if(item instanceof Consignee) { // 如果item是Consignee，则为编辑收货地址
			Consignee bean = (Consignee) item;
			Intent intent = new Intent(AddressListActivity.this, AddressEditActivity.class);
			intent.putExtra(MyConstants.EXTRA_ADDRESS, bean);
			intent.putExtra(MyConstants.EXTRA_ADDRESS_TYPE, MyConstants.TO_ADDRESS_EDIT);
			startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ADDRESS_EDIT);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ADDRESS_EDIT) {
				beans=(ArrayList<Consignee>) data.getSerializableExtra(MyConstants.EXTRA_ADDRESS_LIST);
				if (beans != null && beans.size() > 0) {
					adapter.setData(beans);
				}
			}
		}
	}
}
