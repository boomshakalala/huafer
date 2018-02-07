package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.OneClassificationSecondAdapter;
import com.huapu.huafen.beans.OneYuanClassificationResult;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.itemdecoration.SpacesDecoration;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.squareup.okhttp.Request;
import java.util.HashMap;
import butterknife.BindView;

/**
 * Created by admin on 2017/5/18.
 */
public class OneYuanClassificationActivity extends BaseActivity {

    @BindView(R2.id.classification2) RecyclerView classification2;
    private OneClassificationSecondAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification_one_yuan);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        classification2.setLayoutManager(layoutManager);
        classification2.addItemDecoration(new SpacesDecoration(10));
        adapter = new OneClassificationSecondAdapter(this);
        classification2.setAdapter(adapter);
        doRequestForList();
    }

    @Override
    public void initTitleBar() {
        setTitleString("分类");
    }

    private void doRequestForList(){

        HashMap<String, String> params = new HashMap<>();
        params.put("classificationId","20");

        OkHttpClientManager.postAsyn(MyConstants.GETGOODSSECONDCATES, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {

                try {
                    OneYuanClassificationResult result = JSON.parseObject(response, OneYuanClassificationResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if(result!=null && result.obj !=null){
                            adapter.setData(result.obj.classifications);
                        }
                    } else {
                        CommonUtils.error(result, OneYuanClassificationActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
