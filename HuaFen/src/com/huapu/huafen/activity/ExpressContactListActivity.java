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
import com.huapu.huafen.adapter.ExpressContactAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Express;
import com.huapu.huafen.beans.ExpressContactListResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
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
 * 物流公司电话
 */
public class ExpressContactListActivity extends BaseActivity
																implements
																PullToRefreshBase.OnRefreshListener<RecyclerView>,
																LoadMoreWrapper.OnLoadMoreListener {

	private PullToRefreshRecyclerView ptrRecyclerExpressContact;
	private HLoadingStateView loadingStateView;
	private ExpressContactAdapter adapter;
	private int page = 0;
	private View loadMoreLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_express_contact_list);
		initView();
		startLoading();
	}

	/**
	 * 获取单品类别列表
	 * @param
	 */

	private void initView() {
		setTitleString("常用快递电话");
		loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
		ptrRecyclerExpressContact = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecyclerExpressContact);
		CommonUtils.buildPtr(ptrRecyclerExpressContact);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		ptrRecyclerExpressContact.getRefreshableView().setLayoutManager(linearLayoutManager);

		loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout,
				ptrRecyclerExpressContact.getRefreshableView(), false);

		View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, ptrRecyclerExpressContact,false);
		ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
		ivEmpty.setImageResource(R.drawable.empty_fans);
		adapter = new ExpressContactAdapter(this);
//		adapter.setEmptyView(viewEmpty);
		ptrRecyclerExpressContact.setAdapter(adapter.getWrapperAdapter());

		ptrRecyclerExpressContact.setOnRefreshListener(this);
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
		startRequestForGetExpressContact(LOADING);
	}

	private void refresh(){
		page=0;
		startRequestForGetExpressContact(REFRESH);
	}

	private void loadMore(){
		startRequestForGetExpressContact(LOAD_MORE);
	}

	/**
	 * 获取快递公司电话
	 * @param
	 */
	private void startRequestForGetExpressContact(final String extra){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("page", String.valueOf(page));
		LogUtil.i("liang", "params:" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.GETEXPRESSCOMPANY, params, new StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				if(extra.equals(LOADING)){
					loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
				}else if(extra.equals(REFRESH)){
					ptrRecyclerExpressContact.onRefreshComplete();
				}
			}

			@Override
			public void onResponse(String response) {
				if(extra.equals(LOADING)){
					loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
				}else if(extra.equals(REFRESH)){
					ptrRecyclerExpressContact.onRefreshComplete();
				}
				try {
					LogUtil.i("liang", "快递公司电话:"+response);
					JsonValidator validator = new JsonValidator();
					boolean isJson = validator.validate(response);
					if(!isJson) {
						return;
					}
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						ExpressContactListResult result = JSON.parseObject(baseResult.obj, ExpressContactListResult.class);
						initData(result,extra);
					} else {
						CommonUtils.error(baseResult, ExpressContactListActivity.this, "");
					}
				} catch (Exception e) {
					ProgressDialog.closeProgress();
					e.printStackTrace();
				}
			}

		});
	}

	private void initData(ExpressContactListResult result,String extra){

		if(result.getPage()==0){
			adapter.setLoadMoreView(null);
		}else{
			adapter.setLoadMoreView(loadMoreLayout);
		}

		page++;

		List<Express> expresses = result.getExpressList();
		if(extra.equals(LOADING)){
			adapter.setData(expresses);
		}else if(extra.equals(REFRESH)){
			adapter.setData(expresses);
		}else if(LOAD_MORE.equals(extra)){
			if(expresses == null) {
				expresses = new ArrayList<>();
			}
			adapter.addAll(expresses);
		}

	}

}
