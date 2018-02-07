package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ShareCommodityAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.CommodityListResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 我喜欢的列表
 * @author liang_xs
 *
 */
public class ShareLikeListActivity extends BaseActivity
														implements
														PullToRefreshBase.OnRefreshListener<RecyclerView>,
														LoadMoreWrapper.OnLoadMoreListener {

	private PullToRefreshRecyclerView ptrRecyclerView;
	private HLoadingStateView loadingStateView;
	private ShareCommodityAdapter adapter;
	private int page = 0;
	private View loadMoreLayout;
	private long userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_like);
		if (getIntent().hasExtra(MyConstants.EXTRA_USER_ID)) {
			userId = getIntent().getLongExtra(MyConstants.EXTRA_USER_ID, 0);
		}
		if (userId == 0) {
			userId = CommonPreference.getUserId();
		}

		initView();
		startLoading();
	}

	/**
	 * 获取单品类别列表
	 * @param
	 */

	private void initView() {
		setTitleString("我喜欢的");
		loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
		ptrRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecyclerView);
		CommonUtils.buildPtr(ptrRecyclerView);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		ptrRecyclerView.getRefreshableView().setLayoutManager(linearLayoutManager);

		loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout,
				ptrRecyclerView.getRefreshableView(), false);

		View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, ptrRecyclerView.getRefreshableView(),false);
		ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
		ivEmpty.setImageResource(R.drawable.empty_follow);
		adapter = new ShareCommodityAdapter(this);
		adapter.setEmptyView(viewEmpty);
		ptrRecyclerView.setAdapter(adapter.getWrapperAdapter());

		ptrRecyclerView.setOnRefreshListener(this);
		adapter.setOnLoadMoreListener(this);

	}

	@Override
	public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {//下拉刷新
		refresh();
	}

	@Override
	public void onLoadMoreRequested() {//加载更多
		loadMore();
	}

	private void startLoading(){
		loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
		page=0;
		startRequestForLikes(LOADING);
	}

	private void refresh(){
		page=0;
		startRequestForLikes(REFRESH);
	}

	private void loadMore(){
		startRequestForLikes(LOAD_MORE);
	}

	/**
	 * 获取关注列表
	 * @param
	 */
	private void startRequestForLikes(final String extra){
		if (!CommonUtils.isNetAvaliable(this)) {
			ToastUtil.toast(this, "请检查网络连接");
			return;
		}
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("userId", userId + "");
		params.put("page", page + "");
		LogUtil.i("lalo", "params:" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.I_LIKES, params, new OkHttpClientManager.StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				LogUtil.i("lalo", "campaignError:" + e.toString());
				if(extra.equals(LOADING)){
					loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
				}else if(extra.equals(REFRESH)){
					ptrRecyclerView.onRefreshComplete();
				}else if(extra.equals(LOAD_MORE)){

				}
			}


			@Override
			public void onResponse(String response) {
				if(extra.equals(LOADING)){
					loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
				}else if(extra.equals(REFRESH)){
					ptrRecyclerView.onRefreshComplete();
				}else if(extra.equals(LOAD_MORE)){

				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if (!TextUtils.isEmpty(baseResult.obj)) {
							CommodityListResult result = JSON.parseObject(baseResult.obj, CommodityListResult.class);
							initData(result,extra);
						}
					} else {
						CommonUtils.error(baseResult, ShareLikeListActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initData(CommodityListResult result,String extra){

		if(result.getPage()==0){
			adapter.setLoadMoreView(null);
		}else{
			adapter.setLoadMoreView(loadMoreLayout);
		}

		page++;

		ArrayList<Commodity> list = result.getGoodsList();
		if(extra.equals(LOADING)){
			adapter.setData(list);
		}else if(extra.equals(REFRESH)){
			adapter.setData(list);
		}else if(LOAD_MORE.equals(extra)){
			if(list == null) {
				list = new ArrayList<>();
			}
			adapter.addData(list);
		}

	}

}
