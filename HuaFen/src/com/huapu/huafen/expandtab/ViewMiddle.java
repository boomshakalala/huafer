//package com.huapu.huafen.expandtab;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.util.SparseArray;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//
//import com.huapu.huafen.R;
//import com.huapu.huafen.bean.ClassChildBean;
//import com.huapu.huafen.bean.ClassGroupBean;
//import com.huapu.huafen.bean.TestBean;
//
//public class ViewMiddle extends LinearLayout implements ViewBaseAction {
//	
//	private ListView groupListView;
//	private ListView childListView;
//	private ArrayList<String> groups = new ArrayList<String>();
//	private LinkedList<String> childrenItem = new LinkedList<String>();
//	private SparseArray<LinkedList<String>> children = new SparseArray<LinkedList<String>>();
//	private ArrayList<ClassGroupBean> list = new ArrayList<ClassGroupBean>();
//	private TextAdapter childListViewAdapter;
//	private TextAdapter groupListViewAdapter;
//	private OnSelectListener mOnSelectListener;
//	private int tGroupPosition = 0;
//	private int tChildPosition = 0;
//	private String showString = "不限";
//	private int id;
//
//	public ViewMiddle(Context context, ArrayList<ClassGroupBean> list) {
//		super(context);
//		init(context, list);
//	}
//
//	public ViewMiddle(Context context, AttributeSet attrs, ArrayList<ClassGroupBean> list) {
//		super(context, attrs);
//		init(context, list);
//	}
//	
//	public void updateShowText(String showArea, String showBlock) {
//		if (showArea == null || showBlock == null) {
//			return;
//		}
//		for (int i = 0; i < groups.size(); i++) {
//			if (groups.get(i).equals(showArea)) {
//				groupListViewAdapter.setSelectedPosition(i);
//				childrenItem.clear();
//				if (i < children.size()) {
//					childrenItem.addAll(children.get(i));
//				}
//				tGroupPosition = i;
//				break;
//			}
//		}
//		for (int j = 0; j < childrenItem.size(); j++) {
//			if (childrenItem.get(j).replace("不限", "").equals(showBlock.trim())) {
//				childListViewAdapter.setSelectedPosition(j);
//				tChildPosition = j;
//				break;
//			}
//		}
//		setDefaultSelect();
//	}
//
//	private void init(Context context, ArrayList<ClassGroupBean> beans) {
//		this.list = beans;
//		LayoutInflater inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		inflater.inflate(R.layout.view_expandtab_mid, this, true);
//		groupListView = (ListView) findViewById(R.id.listView);
//		childListView = (ListView) findViewById(R.id.listView2);
////		setBackgroundDrawable(getResources().getDrawable(
////				R.drawable.expandtab_bg_mid));
////		for(int i=0;i<10;i++){
////			groups.add(i+"行");
////			LinkedList<String> tItem = new LinkedList<String>();
////			for(int j=0;j<15;j++){
////				tItem.add(i+"行"+j+"列");
////			}
////			children.put(i, tItem);
////		}
//		
//
//	}
//
//	public void getData(Context context, ArrayList<ClassGroupBean> beans) {
//		this.list = beans;
//		if (list == null || list.size() == 0) {
//			return;
//		}
//		for(int i=0;i<list.size();i++){
//			groups.add(list.get(i).getFcname());
//		    ArrayList<ClassChildBean> childs = (ArrayList<ClassChildBean>) list.get(i).getSecondcatelist();
//		    LinkedList<String> tItem = new LinkedList<String>();
//		    for(int j=0;j<childs.size();j++) {
//		    	tItem.add(childs.get(j).getScname());
//		    }
//			children.put(i, tItem);
//		}
//
//		groupListViewAdapter = new TextAdapter(context, groups,
//				R.drawable.expandtab_item_selected,
//				R.drawable.expandtab_item_selector);
//		groupListViewAdapter.setTextSize(17);
//		groupListViewAdapter.setSelectedPositionNoNotify(tGroupPosition);
//		groupListView.setAdapter(groupListViewAdapter);
//		groupListViewAdapter
//				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {
//
//					@Override
//					public void onItemClick(View view, int position) {
//						if (position < children.size()) {
//							tGroupPosition = position;
//							childrenItem.clear();
//							childrenItem.addAll(children.get(position));
//							childListViewAdapter.notifyDataSetChanged();
//						}
//					}
//				});
//		if (tGroupPosition < children.size())
//			childrenItem.addAll(children.get(tGroupPosition));
//		childListViewAdapter = new TextAdapter(context, childrenItem,
//				0,
//				R.drawable.expandtab_child_item_selector);
//		childListViewAdapter.setTextSize(15);
//		childListViewAdapter.setSelectedPositionNoNotify(tChildPosition);
//		childListView.setAdapter(childListViewAdapter);
//		childListViewAdapter
//				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {
//
//					@Override
//					public void onItemClick(View view, final int position) {
//						
//						showString = childrenItem.get(position);
//						tChildPosition = position;
//						if (mOnSelectListener != null) {
//							mOnSelectListener.getValue(tGroupPosition, tChildPosition, showString);
//						}
//
//					}
//				});
//		if (tChildPosition < childrenItem.size())
//			showString = childrenItem.get(tChildPosition);
//		if (showString.contains("不限")) {
//			showString = showString.replace("不限", "");
//		}
//		setDefaultSelect();
//	}
//
//	public void setDefaultSelect() {
//		groupListView.setSelection(tGroupPosition);
//		childListView.setSelection(tChildPosition);
//	}
//
//	public String getShowText() {
//		return showString;
//	}
//
//	public void setOnSelectListener(OnSelectListener onSelectListener) {
//		mOnSelectListener = onSelectListener;
//	}
//
//	public interface OnSelectListener {
//		public void getValue(int tGroupPosition, int tChildPosition, String showText);
//	}
//
//	@Override
//	public void hide() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void show() {
//		// TODO Auto-generated method stub
//
//	}
//}
