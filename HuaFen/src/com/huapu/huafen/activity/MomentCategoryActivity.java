package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.MomentCategoryAdapter;
import com.huapu.huafen.beans.MomentCategoryBean;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.Logger;
import com.squareup.okhttp.Request;

import butterknife.BindView;

/**
 * Created by qwe on 2017/4/26.
 */

public class MomentCategoryActivity extends BaseActivity {

    public static final String CATEGORY = "CATEGOR_BEAN";
    public static final String CACHE_JSON = "CACHE_JSON";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private MomentCategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_catogory);
        initView();
    }

    private void initView() {
        setTitleString("选择分类");
        categoryAdapter = new MomentCategoryAdapter();
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        categoryAdapter.setOnRecyclerViewListener(new MomentCategoryAdapter.OnRecyclerViewListener() {

            @Override
            public void onItemClick(int position) {
                MomentCategoryBean.ObjBean.CatsBean catsBean = categoryAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(CATEGORY, catsBean);
                setResult(RESULT_OK, intent);
                MomentCategoryActivity.this.finish();
            }
        });
        recyclerView.setAdapter(categoryAdapter);

        getNetData();
    }

    private void getNetData() {
        ArrayMap<String, String> arrayMap = new ArrayMap<>(1);
        arrayMap.put("cacheVersion", CommonPreference.getStringValue(CommonPreference.MOMENT_CACHE_VERSION, ""));
        OkHttpClientManager.postAsyn(MyConstants.ARTICLE_CATEGORY, arrayMap, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                Logger.e("get error" + e.getMessage());
            }

            @Override
            public void onResponse(String response) {
                try {
                    MomentCategoryBean categoryBean = JSON.parseObject(response, MomentCategoryBean.class);
                    if (!TextUtils.isEmpty(CommonPreference.getStringValue(CommonPreference.MOMENT_CACHE_VERSION, ""))) {
                        if (categoryBean.getObj().getVersion().equals(CommonPreference.getStringValue(CommonPreference.MOMENT_CACHE_VERSION, ""))) {
                            categoryBean = JSON.parseObject(CommonPreference.getStringValue(CACHE_JSON, ""), MomentCategoryBean.class);
                        } else {
                            CommonPreference.setStringValue(CommonPreference.MOMENT_CACHE_VERSION, categoryBean.getObj().getVersion());
                            CommonPreference.setStringValue(CACHE_JSON, response);
                        }
                    } else {
                        CommonPreference.setStringValue(CommonPreference.MOMENT_CACHE_VERSION, categoryBean.getObj().getVersion());
                        CommonPreference.setStringValue(CACHE_JSON, response);
                    }

                    categoryAdapter.setData(categoryBean.getObj().getCats());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
