package com.huapu.huafen.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.PoiListAdapter;
import com.huapu.huafen.beans.PoiAddress;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.callbacks.SimpleTextWatcher;
import com.huapu.huafen.utils.LocationHelper;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;


public class PoiListActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener{

    @BindView(R2.id.ivSearch) ImageView ivSearch;
    @BindView(R2.id.etSearch) EditText etSearch;
    @BindView(R2.id.ivDeleteSearch) ImageView ivDeleteSearch;
    @BindView(R2.id.btnSearchCancel) Button btnSearchCancel;
    @BindView(R2.id.layoutTitle) RelativeLayout layoutTitle;
    @BindView(R2.id.recyclerView) RecyclerView recyclerView;
    private PoiListAdapter adapter;
    private LatLonPoint lp;
    private String cityCode;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_list_activity);
        initView();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PoiListAdapter(this);
        recyclerView.setAdapter(adapter);
        LocationHelper helper = new LocationHelper();
        helper.startLocation(new LocationHelper.OnLocationListener() {

            @Override
            public void onLocationComplete(LocationData locationData) {
                lp = new LatLonPoint(locationData.gLat, locationData.gLng);
                cityCode = locationData.cityCode;
                doSearch("");
            }


            @Override
            public void onLocationFailed() {

            }
        });

        etSearch.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if(s!=null){
                    keyword = s.toString().trim();
                }else{
                    keyword = "";
                }

                if(!TextUtils.isEmpty(keyword)){
                    ivDeleteSearch.setVisibility(View.VISIBLE);
                }else{
                    ivDeleteSearch.setVisibility(View.GONE);
                }
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doSearch(keyword);
                    }
                },500);
            }
        });

        ivDeleteSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
    }

    private void doSearch(String key) {
        PoiSearch.Query query = new PoiSearch.Query(key, "", cityCode);
        query.setPageSize(20);
        query.setPageNum(0);
        if (lp != null) {
            PoiSearch poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));
            poiSearch.searchPOIAsyn();
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {
                ArrayList<PoiItem> poiItems = result.getPois();
                ArrayList<Object> data = new ArrayList<>();
                for(PoiItem item : poiItems){
                    LatLonPoint llp = item.getLatLonPoint();
                    double lon = llp.getLongitude();
                    double lat = llp.getLatitude();
                    String title = item.getTitle();
                    String text = item.getSnippet();
                    String poiId = item.getPoiId();
                    data.add(new PoiAddress(lon, lat, title, text,poiId));
                }
                if(!TextUtils.isEmpty(keyword)){
                    data.add(0,keyword);
                }
                adapter.setData(data);
            } else {
                toast("对不起，没有搜索到相关数据！");
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @OnClick({R.id.ivSearch, R.id.etSearch, R.id.btnSearchCancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSearchCancel:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
