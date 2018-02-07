package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.PastOneRankingAdapter;
import com.huapu.huafen.beans.PastRankingResult;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import butterknife.BindView;

/**
 * Created by admin on 2017/5/20.
 */

public class PastOneRankingActivity extends BaseActivity {

    @BindView(R2.id.llContainer)
    LinearLayout llContainer;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private PastOneRankingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_ranking_list);
        String id = mIntent.getStringExtra("extra_id");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PastOneRankingAdapter(this,id);
        recyclerView.setAdapter(adapter);
        llContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });
        doRequest();
    }

    private void doRequest() {
        HashMap<String,String> params = new HashMap<>();
        params.put("page", "0");
        params.put("pageSize", "100");
        OkHttpClientManager.postAsyn(MyConstants.PAST_ONE_YUAN, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }


            @Override
            public void onResponse(String response) {
                try {
                    PastRankingResult result = JSON.parseObject(response, PastRankingResult.class);

                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        initData(result);
                    } else {
                        CommonUtils.error(result, PastOneRankingActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(PastRankingResult result) {
        if(result!=null && result.obj!=null){
            adapter.setData(result.obj.list);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);

    }
}
