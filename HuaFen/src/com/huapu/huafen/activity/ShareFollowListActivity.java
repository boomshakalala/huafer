package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ShareVUserAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.User;
import com.huapu.huafen.beans.UserListResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 关注列表
 * @author liang_xs
 *
 */
public class ShareFollowListActivity extends BaseActivity
															implements
															PullToRefreshBase.OnRefreshListener<RecyclerView>,
															LoadMoreWrapper.OnLoadMoreListener {

	private PullToRefreshRecyclerView ptrRecyclerFollowing;
	private HLoadingStateView loadingStateView;
	private ShareVUserAdapter adapter;
	private int page = 0;
	private View loadMoreLayout;
	private long userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_following);
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
		setTitleString("关注");
		loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
		ptrRecyclerFollowing = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecyclerFollowing);
		CommonUtils.buildPtr(ptrRecyclerFollowing);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		ptrRecyclerFollowing.getRefreshableView().setLayoutManager(linearLayoutManager);

		loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout,
				ptrRecyclerFollowing.getRefreshableView(), false);

		View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, ptrRecyclerFollowing,false);
		ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
		ivEmpty.setImageResource(R.drawable.empty_follow);
		adapter = new ShareVUserAdapter(this);
		adapter.setEmptyView(viewEmpty);
		ptrRecyclerFollowing.setAdapter(adapter.getWrapperAdapter());

		ptrRecyclerFollowing.setOnRefreshListener(this);
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
		startRequestForFollowingList(LOADING);
	}

	private void refresh(){
		page=0;
		startRequestForFollowingList(REFRESH);
	}

	private void loadMore(){
		startRequestForFollowingList(LOAD_MORE);
	}

	/**
	 * 获取关注列表
	 * @param
	 */
	private void startRequestForFollowingList(final String extra){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userId",String.valueOf(userId));
		params.put("page", page+"");
		params.put("pageNum","12");
		LogUtil.i("liang", "params:" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.FOLLOWING_LIST, params, new StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				if(extra.equals(LOADING)){
					loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
				}else if(extra.equals(REFRESH)){
					ptrRecyclerFollowing.onRefreshComplete();
				}
			}

			@Override
			public void onResponse(String response) {
				if(extra.equals(LOADING)){
					loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
				}else if(extra.equals(REFRESH)){
					ptrRecyclerFollowing.onRefreshComplete();
				}
				try {
					LogUtil.i("liang", "followingList:"+response);
					JsonValidator validator = new JsonValidator();
					boolean isJson = validator.validate(response);
					if(!isJson) {
						return;
					}
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						UserListResult result = JSON.parseObject(baseResult.obj, UserListResult.class);
						initData(result,extra);
					} else {
						CommonUtils.error(baseResult, ShareFollowListActivity.this, "");
					}
				} catch (Exception e) {
					ProgressDialog.closeProgress();
					e.printStackTrace();
				}
			}

		});
	}

	private void initData(UserListResult result,String extra){

		if(result.page==0){
			adapter.setLoadMoreView(null);
		}else{
			adapter.setLoadMoreView(loadMoreLayout);
		}

		page++;

		List<User> users = result.getUsers();
		if(extra.equals(LOADING)){
			adapter.setData(users);
		}else if(extra.equals(REFRESH)){
			adapter.setData(users);
		}else if(LOAD_MORE.equals(extra)){
			if(users == null) {
				users = new ArrayList<>();
			}
			adapter.addAll(users);
		}

	}
	
}
