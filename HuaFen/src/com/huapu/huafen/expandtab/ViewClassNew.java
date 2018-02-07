package com.huapu.huafen.expandtab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.FirstClassAdapter;
import com.huapu.huafen.adapter.SecondClassAdapter;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.utils.LogUtil;

import java.util.ArrayList;

public class ViewClassNew extends LinearLayout implements ViewBaseAction {

	private ListView groupListView;
	private ListView childListView;
//	private ArrayList<String> groups = new ArrayList<String>();
//	private LinkedList<String> childrenItem = new LinkedList<String>();
//	private SparseArray<LinkedList<String>> children = new SparseArray<LinkedList<String>>();
//	private ArrayList<ClassGroupBean> list = new ArrayList<ClassGroupBean>();

	private ArrayList<Cat> firstList = new ArrayList<Cat>();
	private ArrayList<Cat> secondList = new ArrayList<Cat>();


	public SecondClassAdapter secondClassAdapter;
	private FirstClassAdapter firstClassAdapter;
	private OnSelectListener mOnSelectListener;
	private int tGroupPosition = 0;
	private int tChildPosition = 0;
	private String showString = "不限";
	private int secondId = 0;

	public ViewClassNew(Context context) {
		super(context);
		init(context);
	}

	public ViewClassNew(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void setCatData(ArrayList<Cat> firstList){
		if(firstList == null) {
			return;
		}
		this.firstList = firstList;
		firstClassAdapter.setData(firstList);
	}
	
	public void setSelectItem(int firstPos, int secondPos){
		if(firstPos < 0 || secondPos < 0) {
			return;
		}
		this.tGroupPosition = firstPos;
		firstClassAdapter.setSelectedPosition(firstPos);
		if(firstPos >= firstList.size()) {
			return;
		}
		secondList = firstList.get(firstPos).getCats();
		if(secondList == null) {
			return;
		}
		this.tChildPosition = secondPos;
		secondClassAdapter.setData(secondList);
		secondClassAdapter.setSelectedPosition(secondPos - 1);
	}

	public void setSelectItem(int firstPos){
		if(firstPos < 0) {
			return;
		}
		this.tGroupPosition = firstPos;
		firstClassAdapter.setSelectedPosition(firstPos);
		if(firstPos >= firstList.size()) {
			return;
		}
		secondList = firstList.get(firstPos).getCats();
		if(secondList == null) {
			return;
		}
		secondClassAdapter.setData(secondList);
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_expandtab_mid, this, true);
		groupListView = (ListView) findViewById(R.id.listView);
		childListView = (ListView) findViewById(R.id.listView2);
		initData(context);
	}

	public void initData(Context context) {
		firstClassAdapter = new FirstClassAdapter(context);
		firstClassAdapter.setTextSize(17);
		firstClassAdapter.setSelectedPositionNoNotify(tGroupPosition);
		groupListView.setAdapter(firstClassAdapter);
		firstClassAdapter
				.setOnItemClickListener(new FirstClassAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, int position) {
						if (position < firstList.size()) {
							if(tGroupPosition != position) {
								tGroupPosition = position;
								// 刷新二级分类列表
								ArrayList<Cat> list = firstList.get(position).getCats();
								secondList = list;
								secondClassAdapter.setData(secondList);
								for(int i = 0; i < list.size(); i++) {
									if(list.get(i).getCid() == secondId) {
										secondClassAdapter.setSelectedPositionNoNotify(i - 1);
										break;
									}
								}
							}
							
						}
					}
				});

		secondClassAdapter = new SecondClassAdapter(context);
		secondClassAdapter.setTextSize(15);
		secondClassAdapter.setSelectedPositionNoNotify(tChildPosition);
		childListView.setAdapter(secondClassAdapter);
		secondClassAdapter
				.setOnItemClickListener(new SecondClassAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, final int position) {
						
						showString = secondList.get(position).getName();
						tChildPosition = position;
						if (mOnSelectListener != null) {
							mOnSelectListener.getValue(firstList.get(tGroupPosition), secondList.get(tChildPosition), showString);
							LogUtil.i("class-----", "tGroupPosition:" + tGroupPosition);
						}

					}
				});
		if (tGroupPosition < firstList.size()) {
//			secondList.addAll(children.get(tGroupPosition));
			// 刷新二级分类列表
			ArrayList<Cat> list = firstList.get(tGroupPosition).getCats();
			secondList = list;
			secondClassAdapter.setData(secondList);
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getCid() == secondId) {
					secondClassAdapter.setSelectedPositionNoNotify(i);
					break;
				}
			}
		}
		if (tChildPosition < secondList.size()) {
			showString = secondList.get(tChildPosition).getName();
		}
		if (showString.contains("不限")) {
			showString = showString.replace("不限", "");
		}
		setDefaultSelect();
	}

	public void setDefaultSelect() {
		groupListView.setSelection(tGroupPosition);
		childListView.setSelection(tChildPosition);
	}

	public String getShowText() {
		return showString;
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(Cat firstList, Cat secondList, String showText);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}
}
