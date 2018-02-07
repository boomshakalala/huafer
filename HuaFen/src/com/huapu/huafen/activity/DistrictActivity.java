package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.DistrictListAdapter;
import com.huapu.huafen.beans.District;

import java.util.ArrayList;

/** 
 * @ClassName: DistrictActivity
 * @Description:  城市选择
 * @author liang_xs
 * @date 2014-8-25
 */
public class DistrictActivity extends BaseActivity {
	private RecyclerView recyclerView;
	private DistrictListAdapter adapter;
	private ArrayList<District> districts;


	/* (non-Javadoc)
	 * @see activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_province);
		initView();
		if(getIntent().hasExtra(MyConstants.EXTRA_DISTRICTS)) {
			districts = (ArrayList<District>) getIntent().getSerializableExtra(MyConstants.EXTRA_DISTRICTS);
		}
		adapter = new DistrictListAdapter(this, districts);
		recyclerView.setAdapter(adapter);
	}

	private void initView(){
		setTitleString("区域选择");
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(layoutManager);
	}

	@Override
	public void onTitleBarDoubleOnClick() {
		recyclerView.smoothScrollToPosition(0);
	}
}
