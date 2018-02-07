package com.huapu.huafen.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Express;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.PinYinUtils;
import com.huapu.huafen.views.RapidView;
import com.squareup.okhttp.Request;

/** 
 * @ClassName: ChooseExpressActivity 
 * @Description:  物流公司
 * @author liang_xs
 * @date 2014-8-25
 */
public class ChooseExpressActivity extends BaseActivity {
	private RapidView rapidView;
	private TextView tvToast;
	private ListView listView;
	private ArrayList<Express> expressList = new ArrayList<Express>();
	private ArrayList<Map<String, String>> listAllCity = new ArrayList<Map<String, String>>();
	private ArrayList<Map<String, String>> allList = new ArrayList<Map<String, String>>();
	private MyCityAdapter myAdapter;
	private String NAME = "name";
	private String TYPE = "type";
	private String selectItem = "";	
	private int cityCode = 0;
	
	/* (non-Javadoc)
	 * @see activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_express);
		initView();
		startRequestForGetExpressListData();
	}


	/**
	 * 获取物流公司列表
	 */
	private void startRequestForGetExpressListData() {
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(this);
		HashMap<String, String> params = new HashMap<String, String>();
		LogUtil.i("liang", "params:"+params.toString());
		OkHttpClientManager.postAsyn(MyConstants.GETEXPRESSCOMPANY, params, new StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				LogUtil.i("liang", "获取物流公司列表：" + e.toString());
				ProgressDialog.closeProgress();
			}


			@Override
			public void onResponse(String response) {
				LogUtil.i("liang", "获取物流公司列表：" + response.toString());
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if(!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if(!TextUtils.isEmpty(baseResult.obj)) {
							expressList = (ArrayList<Express>) ParserUtils.parserExpressListData(baseResult.obj);
							if(expressList != null) {
								new Thread(){
									public void run() {
										allList = initCityListData();
										mHandler.sendMessage(Message.obtain(mHandler, 1, allList));
									};
								}.start();
							}
						}
						
					} else {
						CommonUtils.error(baseResult, ChooseExpressActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				ProgressDialog.closeProgress();
				allList = (ArrayList<Map<String, String>>) msg.obj;
				myAdapter.setData(allList);
				break;

			default:
				break;
			}
		};
	};
	/** 
	 * @Title: initCityData 
	 * @Description: TODO
	 * @return void
	 * @author liang_xs
	 */ 
	private ArrayList<Map<String, String>> initCityListData() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		for (int i = 0; i < expressList.size(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(NAME, expressList.get(i).getExpressName());
			map.put(TYPE, "CITY");
			listAllCity.add(map);
		}
		Collections.sort(listAllCity, contactComparator);
		list.addAll(listAllCity);
		return list;
	}
	
	private Comparator<Map<String, String>> contactComparator = new Comparator<Map<String, String>>() {
		public int compare(Map<String, String> c1, Map<String, String> c2) {
			return compareName(c1, c2);
		}
	};
	
	/**
	 * @Title: compareName
	 * @Description: TODO
	 * @return 比较排序
	 * @author liang_xs
	 */
	public int compareName(Map<String, String> c1, Map<String, String> c2) {
		String name1 = (TextUtils.isEmpty(c1.get(NAME)) ? "" : PinYinUtils.getPinYin(c1.get(NAME)));
		String name2 = (TextUtils.isEmpty(c2.get(NAME)) ? "" : PinYinUtils.getPinYin(c2.get(NAME)));

		if (TextUtils.isEmpty(name1) && !TextUtils.isEmpty(name2)) {
			return 1;
		} else if (!TextUtils.isEmpty(name1) && TextUtils.isEmpty(name2)) {
			return -1;
		} else if (!TextUtils.isEmpty(name1) && !TextUtils.isEmpty(name2)) {
			if (!isLetter(name1.substring(0, 1)) && isLetter(name2.substring(0, 1)))
				return 1;
			else if (isLetter(name1.substring(0, 1)) && !isLetter(name2.substring(0, 1)))
				return -1;
			else if (0 == name1.compareToIgnoreCase(name2))
				return 0;
			else if ((name1.compareToIgnoreCase(name2) < 0))
				return -1;
			else if ((name1.compareToIgnoreCase(name2) > 0))
				return 1;
		}
		return 0;
	}
	
	public static boolean isLetter(String str) {
		if (TextUtils.isEmpty(str))
			return false;
		for (int i = 0; i < str.length(); i++) {
			int chr = str.charAt(i);
			if ((chr < 'A' || chr > 'Z') && (chr < 'a' || chr > 'z'))
				return false;
		}
		return true;
	}


	private void initView() {
		setTitleString("快递公司");
		rapidView = (RapidView) findViewById(R.id.rapidview);
		tvToast = (TextView) findViewById(R.id.tvToast);
		listView = (ListView) findViewById(R.id.listView);
		rapidView.setOnTouchListener(rapidOnTouchListener);
		myAdapter = new MyCityAdapter(this);
		listView.setAdapter(myAdapter);
	}
	
	/**
	 * 快速搜索条的监听器
	 */
	private final OnTouchListener rapidOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			float y = event.getY();
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				tvToast.setVisibility(View.VISIBLE);
				break;
				
			case MotionEvent.ACTION_DOWN:
				((RapidView) v).setTouch(true);
				tvToast.setVisibility(View.VISIBLE);
				break;
				
			case MotionEvent.ACTION_UP:
				((RapidView) v).setTouch(false);
				tvToast.setVisibility(View.GONE);
				break;
				
			case MotionEvent.ACTION_CANCEL:
				((RapidView) v).setTouch(false);
				tvToast.setVisibility(View.GONE);
				break;
			}
			float dis = rapidView.getHeight() / RapidView.count;
			int pos = (int) (y / dis);
			if (pos <= 0) {
				pos = 0;
			} else if (pos >= RapidView.count - 1) {
				pos = RapidView.count;
			}

			int index = myAdapter.getHeaderSelection(pos);
			tvToast.setText(index + " ");
			if (index != -1) {
				listView.setSelection(index);
			}
			if (tvToast.getVisibility() == View.VISIBLE) {
				String str = String.valueOf(myAdapter.getChar(pos));
				if (str.equals("*")) {
					str = "GPS";
				} else if (str.equals("^")) {
					str = "热门";
				}
				tvToast.setText(str);
			}
			return true;
		}
	};



	/**
	 * @ClassName: MyCityAdapter
	 * @Description: 联系人列表适配器
	 * @author liang_xs
	 * @date 2014-4-10
	 */
	class MyCityAdapter extends BaseAdapter {

		public ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		protected LinkedHashMap<Integer, Character> groupTitles = new LinkedHashMap<Integer, Character>(
				RapidView.count);
		private Activity parentActivity;
//		public boolean bSearchMode = false;
		

		public MyCityAdapter(Activity activity) {
			parentActivity = activity;
		}

		public void setData(ArrayList<Map<String, String>> list) {
			if (list != null) {
				this.list = list;
			}
			initIndexMap();
			notifyDataSetChanged();
		}

		
		/**
		 * 获取索引map，在此之前，列表必须是有序列表
		 */
		public void initIndexMap() {
			groupTitles.clear();// 先清除之前的索引表
			Map<String, String> map;
			String strCity;
			char ch;
			for (int index = 0, size = getCount(); index < size; index++) {
				map = getItem(index);
				if (map.get(TYPE).equals("GPS")) {
					ch = '*';
					if (!groupTitles.containsValue(ch)) {
						groupTitles.put(index, ch);
					}
				} else if(map.get(TYPE).equals("HOT")) {
//					ch = '^';
//					if (!groupTitles.containsValue(ch)) {
//						groupTitles.put(index, ch);
//					}
				} else if(map.get(TYPE).equals("CITY")){
					if (TextUtils.isEmpty(map.get(NAME))) {
						ch = '#';
					} else {
						strCity = PinYinUtils.getPinYin(map.get(NAME));
						if (TextUtils.isEmpty(strCity)) {
							ch = '#';
						}
						if (TextUtils.isEmpty(strCity.trim())) {
							ch = '#';
						} else {
							ch = strCity.trim().toUpperCase().charAt(0);
							if (ch < 'A' || ch > 'Z') {
								ch = '#';
							}
						}
					}
					if (!groupTitles.containsValue(ch)) {
						groupTitles.put(index, ch);
					}
				}
				
			}
		}

		public String getHeader(int position) {
			Character ch = groupTitles.get(position);
			String str = "";
			if (ch != null) {
				str = String.valueOf(ch);
			}
			return str;
		}

		/**
		 * 获取字母对应的字符
		 * 
		 * @param number
		 * @return
		 */
		public char getChar(int number) {
			char ch = 'A';
			/*if (number <= 0) {
				ch = '*';
			}else if (number == 1) {
				ch = '^';
			}*/ /*else if (number > 26) {
				ch = '#';
			} */ if (number > 0 && number <= 26) {
				ch = (char) (number + 64);
			}
			return ch;
		}

		/**
		 * 获取当前需要显示的位置
		 * 
		 * @param position
		 * @return
		 */
		public int getHeaderSelection(int position) {
			char ch = getChar(position);
			if (groupTitles == null || groupTitles.size() == 0) {
				return -1;
			}

			for (Entry<Integer, Character> entry : groupTitles.entrySet()) {
				if (entry.getValue() == ch) {
					return entry.getKey();
				}
			}
			return -1;
		}

//		public void startSearch(ArrayList<String> list, String keyword) {
//			bSearchMode = !TextUtils.isEmpty(keyword);
//		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Map<String, String> getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (position < 0 || position >= getCount()) {
				return convertView;
			}
			if (convertView == null) {
				convertView = LayoutInflater.from(parentActivity).inflate(
						R.layout.item_listview_rapidview_city, null);
				holder = new ViewHolder();
				holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
				holder.tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
				holder.layoutItem = convertView.findViewById(R.id.layoutItem);
				holder.layoutGroup = convertView.findViewById(R.id.layoutGroup);
				holder.line = convertView.findViewById(R.id.divider);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.layoutItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					selectItem = list.get(position).get(NAME);
					for(Express express : expressList) {
						if(express.getExpressName().equals(selectItem)) {
							Intent intent = new Intent();
							intent.putExtra(MyConstants.EXTRA_EXPRESS, express);
							setResult(RESULT_OK, intent);
							finish();
							break;
						}
					}
					
				}
			});
			createView(holder, position);
			return convertView;
		}

		private void createView(ViewHolder cache, int position) {
			Map<String, String> map = getItem(position);

			if (position+1 < getCount() && groupTitles.containsKey(position+1)) {
				cache.line.setVisibility(View.GONE);
			} else {
				cache.line.setVisibility(View.VISIBLE);
			}
			
			// 索引
			cache.layoutGroup.setVisibility(View.GONE);

			if (groupTitles.containsKey(position)) {
				cache.layoutGroup.setVisibility(View.VISIBLE);
				String strName = getHeader(position);
				cache.tvGroupName.setText(strName);
				if ("GPS".equals(map.get(TYPE))) {
					cache.tvGroupName.setTextSize(15);
					cache.tvGroupName.setGravity(Gravity.CENTER_VERTICAL);
					cache.tvGroupName.setCompoundDrawablesWithIntrinsicBounds(
							0, 0, 0, 0);
					cache.tvGroupName.setText("当前位置");
				} else if ("HOT".equals(map.get(TYPE))) {
					cache.tvGroupName.setTextSize(15);
					cache.tvGroupName.setGravity(Gravity.CENTER_VERTICAL);
					cache.tvGroupName.setCompoundDrawablesWithIntrinsicBounds(
							0, 0, 0, 0);
					cache.tvGroupName.setText("热门城市");
				} else {
					cache.tvGroupName.setCompoundDrawablesWithIntrinsicBounds(
							0, 0, 0, 0);
					cache.tvGroupName.setTextSize(21);
					cache.tvGroupName.setGravity(Gravity.CENTER_VERTICAL);
				}

			}
			cache.tvName.setText(map.get(NAME));
		}

		class ViewHolder {
			public TextView tvName, tvGroupName;
			public View layoutItem, layoutGroup;
			public View line;
		}
	}
	
}
