package com.huapu.huafen.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
import com.huapu.huafen.beans.Age;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;
/**
 * 孩子年龄段
 * @author liang_xs
 *
 */
public class BabyAgeActivity extends BaseActivity implements OnItemClickListener {
	private ListView ageListView;
	private MyListAdapter adapter;
	private ArrayList<Age> selectList = new ArrayList<Age>();
	private final static int AGE_ID = 8;
	private List<Age> list = new ArrayList<Age>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_age);
		if(getIntent().hasExtra(MyConstants.EXTRA_CHOOSE_AGES)) {
			selectList = (ArrayList<Age>) getIntent().getSerializableExtra(MyConstants.EXTRA_CHOOSE_AGES);
		}
		initView();
		startRequestForGetAgeList();
		adapter = new MyListAdapter(this);
		ageListView.setAdapter(adapter);
	}

	private void initView() {
		getTitleBar().
				setTitle("年龄段").
				setRightText("提交", new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(selectList != null && selectList.size() > 0) {
							//排序
							Collections.sort(selectList, new Comparator<Age>() {
								public int compare(Age arg0, Age arg1) {
									return arg0.getSequence() - (arg1.getSequence());
								}
							});
							Intent intent = new Intent();
							intent.putExtra(MyConstants.EXTRA_CHOOSE_AGES, selectList);
							setResult(Activity.RESULT_OK, intent);
							finish();
						}
					}
				});
		ageListView = (ListView) findViewById(R.id.ageListView);
		ageListView.setOnItemClickListener(this);
	}

	/**
	 * 获取年龄段列表
	 * 
	 * @param
	 */
	private void startRequestForGetAgeList() {
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(this);
		HashMap<String, String> params = new HashMap<>();
		OkHttpClientManager.postAsyn(MyConstants.GETAGELIST, params,
				new StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						// 调用刷新完成
						LogUtil.i("liang", "宝宝年龄段列表:" + response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response,
									BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								if (!TextUtils.isEmpty(baseResult.obj)) {
									list = ParserUtils.parserAgeListData(baseResult.obj);
									if (list != null) {
										for(Age age : list) {
											for(Age select : selectList) {
												if(age.getAgeId() == select.getAgeId()) {
													age.setIsSelect(true);
													continue;
												}
											}
										}
										adapter.setData(list);
									}
								}
							} else {
								CommonUtils.error(baseResult, BabyAgeActivity.this, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});
	}

	class MyListAdapter extends BaseAdapter {

		private List<Age> list = new ArrayList<Age>();
		private Context mContext;

		public MyListAdapter(Context mContext) {
			super();
			this.mContext = mContext;
		}

		public void setData(List<Age> list) {
			this.list = list;
			notifyDataSetChanged();
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

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_listview_age, null);
				viewHolder = new ViewHolder();
				viewHolder.tvAge = (TextView) convertView
						.findViewById(R.id.tvAge);
				viewHolder.ivSelect = (ImageView) convertView.findViewById(R.id.ivSelect);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(list.get(position).getIsSelect()) {
				viewHolder.ivSelect.setVisibility(View.VISIBLE);
			} else {
				viewHolder.ivSelect.setVisibility(View.GONE);
			}
			viewHolder.tvAge.setText(list.get(position).getAgeTitle());
			return convertView;
		}

		class ViewHolder {
			public TextView tvAge;
			private ImageView ivSelect;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Object item = parent.getItemAtPosition(position);
		if(item instanceof Age) {
			Age bean = (Age) item;
			if(selectList != null) {
				if(bean.getIsSelect()) {
					bean.setIsSelect(false);
					selectList.remove(bean);
				} else {
					if(selectList.size() == 0) {
						bean.setIsSelect(true);
						selectList.add(bean);
					} else if(selectList.size() == 1) {
						if(bean.getAgeId() == AGE_ID) { // 如果选中的是 全年龄段，则取消其他选项
							for (Age age : list) {
								age.setIsSelect(false);
							}
							selectList.clear();
							bean.setIsSelect(true);
							selectList.add(bean);
						} else { // 如果选中的不是全年龄段，则取消全年龄段选项
							int ageId = selectList.get(0).getAgeId();
							if(ageId == AGE_ID) {
								for(Age a : list) {
									if(a.getAgeId() == AGE_ID) {
										a.setIsSelect(false);
										break;
									}
								}
							}
							// 如果已经选择了一个，再选中第二个时，判断是否是规定可选的内容
							int selectIndex = selectList.get(0).getSequence();
							int index = bean.getSequence();
							if(((index + 1) != selectIndex && (index - 1) != selectIndex) || ageId == AGE_ID) {
								selectList.remove(0);
							}
							bean.setIsSelect(true);
							selectList.add(bean);
						}
					} else if(selectList.size() == 2) {
						// 如果已经选择两个，则移除所有，再添加
						for(Age age : list) {
							age.setIsSelect(false);
						}
						selectList.clear();
						bean.setIsSelect(true);
						selectList.add(bean);
					}
				}
				adapter.notifyDataSetChanged();
			}
		}
	}
}
