package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.RedPointTitleAdapter;
import com.huapu.huafen.beans.OrdersResult;
import com.huapu.huafen.fragment.OrdersListFragment;
import com.huapu.huafen.scrollablelayoutlib.PagerSlidingTabStrip;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.weixin.WeChatPayHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * 我买到的
 *
 * Created by liang on 2016/10/26.
 *
 */
public class OrdersListActivity extends BaseActivity
														implements
														OrdersListFragment.OnRefreshOverListener {
	private List<String> titleList;
	private int index;
	private PagerSlidingTabStrip pagerStrip;
	private ViewPager pager;
	private int role;
	private String title;
	private int[] status;
	private int[] state;
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	private RedPointTitleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orders_list);
		Intent intent = getIntent();
		if(intent != null && intent.hasExtra(MyConstants.EXTRA_ORDERS_ROLE)){
			role = intent.getIntExtra(MyConstants.EXTRA_ORDERS_ROLE, 0);
		}
		if(intent != null && intent.hasExtra("index")){
			index = intent.getIntExtra("index", -1);
		}
		switch (role) { // 1表示我买到的、2表示我卖出的、0表示全部（不筛选此项，如退款）
			case 0:
				title = "退款";
				titleList = new ArrayList<String>();
				titleList.add("我的退款");
				titleList.add("相关仲裁");
				status = new int[]{0, 0}; // 1.已下单、2.支付中、3.已支付、4.已发货、5.已收货、6.关闭、7.已完成、0.all
				state = new int[]{2, 3}; // 0:默认值,标示订单为正常状态、1:订单取消(支付超时,卖家,买家,发货超时)、2.退款(有退款操作,退款状态在退款实体中)、3.仲裁(有人申请仲裁,具体状态在仲裁实体中)
				break;
			case 1:
				title = "我买到的";
				titleList = new ArrayList<String>();
				titleList.add("待付款");
				titleList.add("待发货");
				titleList.add("待收货");
				titleList.add("待评价");
				titleList.add("已完成");
				status = new int[]{1, 3, 4, 5, 7};// 1.已下单、2.支付中、3.已支付、4.已发货、5.已收货、6.关闭、7.已完成、0.all
				state = new int[]{0, 0, 0 ,0, 0};// 0:默认值,标示订单为正常状态、1:订单取消(支付超时,卖家,买家,发货超时)、2.退款(有退款操作,退款状态在退款实体中)、3.仲裁(有人申请仲裁,具体状态在仲裁实体中)
				break;
			case 2:
				title = "我卖出的";
				titleList = new ArrayList<String>();
				titleList.add("待付款");
				titleList.add("待发货");
				titleList.add("待收货");
				titleList.add("待评价");
				titleList.add("已完成");
				status = new int[]{1, 3, 4, 5, 7};// 1.已下单、2.支付中、3.已支付、4.已发货、5.已收货、6.关闭、7.已完成、0.all
				state = new int[]{0, 0, 0 ,0, 0};// 0:默认值,标示订单为正常状态、1:订单取消(支付超时,卖家,买家,发货超时)、2.退款(有退款操作,退款状态在退款实体中)、3.仲裁(有人申请仲裁,具体状态在仲裁实体中)
				break;
		}
		if(index == -1 || index >= titleList.size()){
			index = 0;
		}
		initView();
	}



	private void initView() {
		setTitleString(title);
		pagerStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerStrip);
		pager = (ViewPager) findViewById(R.id.pager);
		if(ArrayUtil.isEmpty(titleList)) {
			return;
		}
		for(int i=0; i < titleList.size(); i++){
			Bundle bundle = new Bundle();
			if(index == i){
				pager.setCurrentItem(i);
				bundle.putBoolean(MyConstants.EXTRA_ORDERS_IS_FIRST_LOAD,true);
			}
			bundle.putInt(MyConstants.EXTRA_ORDERS_STATUS,status[i]);
			bundle.putInt(MyConstants.EXTRA_ORDERS_STATE,state[i]);
			bundle.putInt(MyConstants.EXTRA_ORDERS_ROLE,role);
			OrdersListFragment fragment  = OrdersListFragment.newInstance(bundle);
			fragment.setOnRefreshOverListener(this);
			fragmentList.add(fragment);
		}


		adapter = new RedPointTitleAdapter(getSupportFragmentManager(), fragmentList, titleList);
		int[] points =null;
		int[] drawables =null;
		switch (role) {
			case 0://退款
				points = new int[2];
				drawables = new int[]{
						R.drawable.refund_selector,
						R.drawable.arbitration_selector};
				break;
			case 1://我买到的
				points = new int[5];
				drawables = new int[]{
						R.drawable.pay_pending_selector,
						R.drawable.ship_pending_selector,
						R.drawable.receipt_pending_selector,
						R.drawable.rate_pending_selector,
						R.drawable.completed_selector};
				break;
			case 2://我卖出的
				points = new int[5];
				drawables = new int[]{
						R.drawable.pay_pending_selector,
						R.drawable.ship_pending_selector,
						R.drawable.receipt_pending_selector,
						R.drawable.rate_pending_selector,
						R.drawable.completed_selector};

				break;
		}

		adapter.setRedPoints(points);
		adapter.setImageRes(drawables);


		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(titleList.size());
		pagerStrip.setViewPager(pager);
		pagerStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {

			}

			@Override
			public void onPageSelected(int i) {
				Log.e("onPageSelected", "page:" + i);
				index = i;
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});
		pager.setCurrentItem(index);

	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onTitleBarDoubleOnClick() {
		if(ArrayUtil.isEmpty(fragmentList)) {
			return;
		}
		Fragment fragment = fragmentList.get(index);
		OrdersListFragment ordersListFragment = null;
		if(fragment == null) {
			return;
		}
		if(fragment instanceof OrdersListFragment) {
			ordersListFragment = (OrdersListFragment) fragment;
			if(ordersListFragment.ptrRecycler == null) {
				return;
			}
			ordersListFragment.ptrRecycler.getRefreshableView().smoothScrollToPosition(0);
		}

	}

	@Override
	public void onComplete(OrdersResult.OrderStatusCounts orderStatusCounts) {
		if(orderStatusCounts ==null){
			return;
		}
		if(adapter!=null){

			int[] points =null;
			switch (role) {
				case 0://退款
					points = new int[2];
					points[0] = orderStatusCounts.refund;
					points[1] = orderStatusCounts.report;
					break;
				case 1://我买到的
					points = new int[5];
					points[0] = orderStatusCounts.payPending;
					points[1] = orderStatusCounts.shipPending;
					points[2] = orderStatusCounts.receiptPending;
					points[3] = orderStatusCounts.ratePending;
					points[4] = orderStatusCounts.completed;
					break;
				case 2://我卖出的
					points = new int[5];
					points[0] = orderStatusCounts.payPending;
					points[1] = orderStatusCounts.shipPending;
					points[2] = orderStatusCounts.receiptPending;
					points[3] = orderStatusCounts.ratePending;
					points[4] = orderStatusCounts.completed;
					break;
			}

			adapter.setRedPoints(points);
			if(pagerStrip!=null){
				pagerStrip.notifyDataSetChanged();
			}

		}

	}

	/**
	 * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。 最终支付成功根据异步通知为准
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// 支付页面返回处理
			if (requestCode == WeChatPayHelper.REQUEST_CODE_PAYMENT) {
				if(ArrayUtil.isEmpty(fragmentList)) {
					return;
				}
				Fragment fragment = fragmentList.get(index);
				OrdersListFragment ordersListFragment = null;
				if(fragment == null) {
					return;
				}
				if (resultCode == Activity.RESULT_OK) {
					String result = data.getExtras().getString("pay_result");
					/*
					 * 处理返回值 "success" - payment succeed "fail" - payment failed
					 * "cancel" - user canceld "invalid" - payment plugin not
					 * installed
					 */
					if (result.equals("success")) {
						if(fragment instanceof OrdersListFragment) {
							ordersListFragment = (OrdersListFragment) fragment;
							if(ordersListFragment.adapter == null) {
								return;
							}
							ordersListFragment.adapter.isPaying = false;
							ordersListFragment.adapter.isPaySuccess = true;
							ordersListFragment.adapter.startRequestForPaySuccess();
						}
					} else {
						if(fragment instanceof OrdersListFragment) {
							ordersListFragment = (OrdersListFragment) fragment;
							if(ordersListFragment.adapter == null) {
								return;
							}
							ordersListFragment.adapter.isPaying = false;
						}
					}
				}
			}
		}
	}

}