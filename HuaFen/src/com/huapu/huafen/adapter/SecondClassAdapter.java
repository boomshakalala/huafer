package com.huapu.huafen.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.utils.ArrayUtil;

public class SecondClassAdapter extends BaseAdapter {
	
	private ArrayList<Cat> list = new ArrayList<Cat>();
	private Context mContext;
	
	public SecondClassAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		init();
	}
	
	public void setData(ArrayList<Cat> list) {
		if(!ArrayUtil.isEmpty(list) && list.get(0).getCid() == 0) {
			list.remove(0);
		}
		this.list = list;
		notifyDataSetChanged();
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
	@Override
	public int getCount() {
		return list.size();
	}
	
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	private String item;
	private float textSize;
	private String selectedText = "";
	private int selectedPos = -1;

	private OnClickListener onClickListener;
	private OnItemClickListener mOnItemClickListener;
	/**
	 * 设置列表选中item
	 */
	public void setText(String item) {
		this.item = item;
	}
	
	/**
	 * 设置列表字体大小
	 */
	public void setTextSize(float tSize) {
		textSize = tSize;
	}
	
	/**
	 * 设置选中的position,并通知列表刷新
	 */
	public void setSelectedPosition(int pos) {
		if (list != null && pos < list.size()) {
			selectedPos = pos;
			selectedText = list.get(pos).getName();
			notifyDataSetChanged();
		}

	}

	/**
	 * 设置选中的position,但不通知刷新
	 */
	public void setSelectedPositionNoNotify(int pos) {
		selectedPos = pos;
		if (list != null && pos < list.size()) {
			selectedText = list.get(pos).getName();
		}
	}

	/**
	 * 获取选中的position
	 */
	public int getSelectedPosition() {
		if (list != null && selectedPos < list.size()) {
			return selectedPos;
		}

		return -1;
	}

	
	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		TextView view;
		if (convertView == null) {
			view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_expandtab, parent, false);
		} else {
			view = (TextView) convertView;
		}
		view.setTag(position);
		String mString = "";
		if (list != null) {
			if (position < list.size()) {
				mString = list.get(position).getName();
			}
		}
		view.setText(mString);
		if (mString.equals(item)) {
			view.setTextColor(mContext.getResources().getColor(R.color.base_pink));
		} else {
			view.setTextColor(mContext.getResources().getColor(R.color.black));
		}
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);

		if (selectedText != null && selectedText.equals(mString)) {
				view.setTextColor(mContext.getResources().getColor(R.color.base_pink));
		} else {
			view.setTextColor(mContext.getResources().getColor(R.color.text_color));
		}
		view.setPadding(20, 0, 0, 0);
		view.setOnClickListener(onClickListener);
		return view;
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

	
}