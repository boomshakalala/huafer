package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.BidRecordListAdapter;
import com.huapu.huafen.beans.BidRecord;
import com.huapu.huafen.beans.BidRecordListResult;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by mac on 17/8/2.
 */

public class BidRecordListActivity extends BaseActivity implements LoadMoreWrapper.OnLoadMoreListener {

    @BindView(R.id.bidList) RecyclerView bidList;
    @BindView(R.id.ptrFrameLayout) PtrDefaultFrameLayout ptrFrameLayout;
    private BidRecordListAdapter adapter;
    private int page;
    private long goodsId;
    private int goodsState;
    private View loadMoreLayout;

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("共出价0次").setRightText("出价规则", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BidRecordListActivity.this, WebViewActivity2.class);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.BID_RECORD_RULE);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_record_list);
        goodsId = mIntent.getLongExtra(MyConstants.EXTRA_GOODS_DETAIL_ID,0);
        goodsState = mIntent.getIntExtra(MyConstants.GOODS_STATE,0);
        ptrFrameLayout.buildPtr(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                LinearLayoutManager manager = (LinearLayoutManager) bidList.getLayoutManager();
                int index = manager.findFirstVisibleItemPosition();
                View childAt = bidList.getChildAt(0);
                boolean indexTop = false;
                if (childAt == null || (index == 0 && childAt.getTop() == 0)) {
                    indexTop = true;
                }

                return indexTop && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(bidList.getContext(),LinearLayoutManager.VERTICAL,false);
        bidList.setLayoutManager(linearLayoutManager);
        adapter = new BidRecordListAdapter(bidList.getContext(),goodsState);
        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, bidList, false);
        adapter.setOnLoadMoreListener(this);
        bidList.setAdapter(adapter.getWrapperAdapter());
        refresh();
    }


    private void startRequestForBidList(final String extra){
        if(!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("goodsId", String.valueOf(goodsId));
        params.put("page",String.valueOf(page));

        OkHttpClientManager.postAsyn(MyConstants.AUCTION_BID_LIST, params,new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if(REFRESH.equals(extra)){
                    ptrFrameLayout.refreshComplete();
                }
            }

            @Override
            public void onResponse(String response) {
                if(REFRESH.equals(extra)){
                    ptrFrameLayout.refreshComplete();
                }
                try {
                    BidRecordListResult result = JSON.parseObject(response, BidRecordListResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        initListData(result,extra);
                    } else {
                        CommonUtils.error(result, BidRecordListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refresh(){
        page = 0;
        startRequestForBidList(REFRESH);
    }

    private void loadMore(){
        startRequestForBidList(LOAD_MORE);
    }



    private void initListData(BidRecordListResult result,String extra){

        if(result.obj.page==0){
            adapter.setLoadMoreView(null);
        }else{
            adapter.setLoadMoreView(loadMoreLayout);
        }

        page++;

        List<BidRecord> bidList = result.obj.bidList;
        if(extra.equals(REFRESH)){
            getTitleBar().setTitle("共出价"+result.obj.bidCount+"次");
            adapter.setData(bidList);
        }else if(LOAD_MORE.equals(extra)){
            if(bidList == null) {
                bidList = new ArrayList<>();
            }
            adapter.addAll(bidList);
        }
    }


    @Override
    public void onLoadMoreRequested() {
        loadMore();
    }
}
