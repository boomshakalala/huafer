package com.huapu.huafen.expandtab;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.PinYinUtils;
import com.huapu.huafen.views.RapidView;

public class LeftAdapter extends BaseAdapter {
//	public ArrayList<Map<String, String>> cityList = new ArrayList<Map<String, String>>();
	protected LinkedHashMap<Integer, Character> groupTitles = new LinkedHashMap<Integer, Character>(
			RapidView.count);
//	public boolean bSearchMode = false;
	private Context mContext;
	private List<String> mListData;
	private String[] mArrayData;
	private ArrayList<Map<String,String>> mArrayListData = new ArrayList<Map<String,String>>();
	private int selectedPos = -1;
	private String selectedText = "";
	private int normalDrawbleId;
	private Drawable selectedDrawble;
	private float textSize;
	private OnClickListener onClickListener;
	private OnItemClickListener mOnItemClickListener;
	

	public LeftAdapter(Context mContext, List<String> listData, int sId, int nId) {
		this.mContext = mContext;
		mListData = listData;
		selectedDrawble = mContext.getResources().getDrawable(sId);
		normalDrawbleId = nId;
		init();
	}
	
	public LeftAdapter(Context context, String[] arrayData, int sId, int nId) {
		mContext = context;
		mArrayData = arrayData;
		selectedDrawble = mContext.getResources().getDrawable(sId);
		normalDrawbleId = nId;
		init();
	}
	
	public LeftAdapter(Context context, int sId, int nId) {
		mContext = context;
		selectedDrawble = mContext.getResources().getDrawable(sId);
		normalDrawbleId = nId;
		init();
	}
	
	private void init() {
		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				selectedPos = (Integer) view.getTag();
				setSelectedPosition(selectedPos);
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(view, selectedPos);
				}
			}
		};
	}
	
	/**
	 * 设置选中的position,并通知列表刷新
	 */
	public void setSelectedPosition(int pos) {
		if (mListData != null && pos < mListData.size()) {
			selectedPos = pos;
			selectedText = mListData.get(pos);
			notifyDataSetChanged();
		} else if (mArrayData != null && pos < mArrayData.length) {
			selectedPos = pos;
			selectedText = mArrayData[pos];
			notifyDataSetChanged();
		} else if (mArrayListData != null && pos < mArrayListData.size()) {
			selectedPos = pos;
			selectedText = mArrayListData.get(pos).get("name");
			notifyDataSetChanged();
		}

	}

	/**
	 * 设置选中的position,但不通知刷新
	 */
	public void setSelectedPositionNoNotify(int pos) {
		selectedPos = pos;
		if (mListData != null && pos < mListData.size()) {
			selectedText = mListData.get(pos);
		} else if (mArrayData != null && pos < mArrayData.length) {
			selectedText = mArrayData[pos];
		}
	}

	/**
	 * 获取选中的position
	 */
	public int getSelectedPosition() {
		if (mArrayData != null && selectedPos < mArrayData.length) {
			return selectedPos;
		}
		if (mListData != null && selectedPos < mListData.size()) {
			return selectedPos;
		}

		return -1;
	}

	/**
	 * 设置列表字体大小
	 */
	public void setTextSize(float tSize) {
		textSize = tSize;
	}
	

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	/**
	 * 重新定义菜单选项单击接口
	 */
	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}
	
	public void setData(ArrayList<Map<String, String>> list) {
		if (list != null) {
			mArrayListData = list;
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
			if (map.get("type").equals("全国")) {
				ch = '*';
				if (!groupTitles.containsValue(ch)) {
					groupTitles.put(index, ch);
				}
			} else if (map.get("type").equals("GPS")) {
				ch = '&';
				if (!groupTitles.containsValue(ch)) {
					groupTitles.put(index, ch);
				}
			} else if(map.get("type").equals("HOT")) {
//				ch = '^';
//				if (!groupTitles.containsValue(ch)) {
//					groupTitles.put(index, ch);
//				}
			} else if(map.get("type").equals("CITY")){
				if (TextUtils.isEmpty(map.get("name"))) {
					ch = '#';
				} else {
					strCity = PinYinUtils.getPinYin(map.get("name"));
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
	 * @param id
	 * @return
	 */
	public char getChar(int number) {
		char ch = 'A';
		/*if (number <= 0) {
			ch = '*';
		}else if (number == 1) {
			ch = '&';
		} else if (number == 2) {
			ch = '^';
		} else */if (number > 0 && number <= 26) {
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

//	public void startSearch(ArrayList<String> list, String keyword) {
//		bSearchMode = !TextUtils.isEmpty(keyword);
//	}

	@Override
	public int getCount() {
		return mArrayListData.size();
	}

	@Override
	public Map<String, String> getItem(int position) {
		return mArrayListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Map<String, String> item = getItem(position);
		ViewHolder holder = null;
		if (position < 0 || position >= getCount()) {
			return convertView;
		}
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
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
		
//		holder.layoutItem.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
////				selectCity = cityList.get(position).get(NAME);
////				toast(selectCity);
//			}
//		});
		String var = item.get("type");
		if("GPS".equals(var)){
			holder.tvName.setTextColor(mContext.getResources().getColor(R.color.base_pink));
		}else{
			holder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_color));
		}
		holder.layoutItem.setTag(position);
		String mString = "";
		if (mListData != null) {
			if (position < mListData.size()) {
				mString = mListData.get(position);
			}
		} else if (mArrayData != null) {
			if (position < mArrayData.length) {
				mString = mArrayData[position];
			}
		}
		if (mString.contains("不限"))
			holder.tvName.setText("不限");
		else
			holder.tvName.setText(mString);
		holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);

		if (selectedText != null && selectedText.equals(mString)) {
			holder.layoutItem.setBackgroundDrawable(selectedDrawble);//设置选中的背景图片
		} else {
			holder.layoutItem.setBackgroundDrawable(mContext.getResources().getDrawable(normalDrawbleId));//设置未选中状态背景图片
		}
		holder.layoutItem.setOnClickListener(onClickListener);
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
			if ("全国".equals(map.get("type"))) {
				cache.tvGroupName.setTextSize(15);
				cache.tvGroupName.setGravity(Gravity.CENTER_VERTICAL);
				cache.tvGroupName.setCompoundDrawablesWithIntrinsicBounds(
						0, 0, 0, 0);
				cache.tvGroupName.setText("全国");
			} else if ("GPS".equals(map.get("type"))) {
				cache.tvGroupName.setTextSize(15);
				cache.tvGroupName.setGravity(Gravity.CENTER_VERTICAL);
				cache.tvGroupName.setCompoundDrawablesWithIntrinsicBounds(
						0, 0, 0, 0);
				cache.tvGroupName.setText("当前位置");
			} else if ("HOT".equals(map.get("type"))) {
//				cache.tvGroupName.setTextSize(15);
//				cache.tvGroupName.setGravity(Gravity.CENTER_VERTICAL);
//				cache.tvGroupName.setCompoundDrawablesWithIntrinsicBounds(
//						0, 0, 0, 0);
//				cache.tvGroupName.setText("热门城市");
			} else {
				cache.tvGroupName.setCompoundDrawablesWithIntrinsicBounds(
						0, 0, 0, 0);
				cache.tvGroupName.setTextSize(21);
				cache.tvGroupName.setGravity(Gravity.CENTER_VERTICAL);
			}

		}
		cache.tvName.setText(map.get("name"));
	}

	class ViewHolder {
		public TextView tvName, tvGroupName;
		public View layoutItem, layoutGroup;
		public View line;
	}
}
