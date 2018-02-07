package com.huapu.huafen.expandtab;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huapu.huafen.R;

public class TextAdapter extends ArrayAdapter<String> {

	private Context mContext;
	private List<String> mListData;
	private String[] mArrayData;
	private int selectedPos = -1;
	private String selectedText = "";
	private int normalDrawbleId;
	private Drawable selectedDrawble;
	private float textSize;
	private OnClickListener onClickListener;
	private OnItemClickListener mOnItemClickListener;
	private String item;

	public TextAdapter(Context context, List<String> listData, int sId, int nId) {
		super(context, R.string.no_data, listData);
		mContext = context;
		mListData = listData;
		if(sId == 0) {
			selectedDrawble = null;
		} else {
			selectedDrawble = mContext.getResources().getDrawable(sId);
		}
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

	public TextAdapter(Context context, String[] arrayData, int sId, int nId) {
		super(context, R.string.no_data, arrayData);
		mContext = context;
		mArrayData = arrayData;
		if(sId == 0) {
			selectedDrawble = null;
		} else {
			selectedDrawble = mContext.getResources().getDrawable(sId);
		}
		normalDrawbleId = nId;
		init();
	}
	
	public TextAdapter(Context context, String[] arrayData, String item, int sId, int nId) {
		super(context, R.string.no_data, arrayData);
		this.item = item;
		mContext = context;
		mArrayData = arrayData;
		if(sId == 0) {
			selectedDrawble = null;
		} else {
			selectedDrawble = mContext.getResources().getDrawable(sId);
		}
		normalDrawbleId = nId;
		init();
	}
	
	public void setText(String item) {
		this.item = item;
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
	
	public void setSelectTextRedColor(TextView view){
		view.setTextColor(mContext.getResources().getColor(R.color.base_pink));
	}
//	public void setSelectTextColor(String item){
//		view.setTextColor(mContext.getResources().getColor(R.color.black));
//		String mString = "";
//		for(int i = 0; i < mListData.size(); i++) {
//			if (mListData != null) {
//				if (i < mListData.size()) {
//					mString = mListData.get(i);
//				}
//			} else if (mArrayData != null) {
//				if (i < mArrayData.length) {
//					mString = mArrayData[i];
//				}
//			}
//			if (mString.contains("不限")) {
//				view.setText("不限");
//			} else {
//				view.setText(mString);
//			}
//			if (mString.equals(item)) {
//				view.setTextColor(mContext.getResources().getColor(R.color.title_bar_red_bg));
//			} else {
//				view.setTextColor(mContext.getResources().getColor(R.color.black));
//			}
//		}
//		
//
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view;
		if (convertView == null) {
			view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_expandtab, parent, false);
		} else {
			view = (TextView) convertView;
		}
		view.setTag(position);
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
		if (mString.contains("不限")) {
			view.setText("不限");
		} else {
			view.setText(mString);
		}
		if (mString.equals(item)) {
			view.setTextColor(mContext.getResources().getColor(R.color.base_pink));
		} else {
			view.setTextColor(mContext.getResources().getColor(R.color.black));
		}
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);

		if (selectedText != null && selectedText.equals(mString)) {
			if(selectedDrawble == null) {
				view.setBackgroundDrawable(mContext.getResources().getDrawable(normalDrawbleId));//设置选中的背景图片
				view.setTextColor(mContext.getResources().getColor(R.color.base_pink));
			} else {
				view.setBackgroundDrawable(selectedDrawble);//设置选中的背景图片
			}
		} else {
			view.setBackgroundDrawable(mContext.getResources().getDrawable(normalDrawbleId));//设置未选中状态背景图片
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
