package com.huapu.huafen.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.BrandListAdapter;
import com.huapu.huafen.adapter.BrandSuggestListAdapterNew;
import com.huapu.huafen.amzing.AmazingListView;
import com.huapu.huafen.amzing.SideIndex;
import com.huapu.huafen.beans.Brand;
import com.huapu.huafen.beans.BrandsSuggestResult;
import com.huapu.huafen.callbacks.OnBrandListDataLoaded;
import com.huapu.huafen.callbacks.SimpleTextWatcher;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.EmojiFilter;
import com.huapu.huafen.utils.Pair;
import com.huapu.huafen.views.BrandListHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BrandListActivity extends BaseActivity implements BrandListAdapter.OnBrandClickListener {

    private static final int GEN_KEY_WORD_SEARCH_RESULT = 0;//返回搜索结果
    @BindView(R2.id.flMain) FrameLayout flMain;//根布局
    //品牌列表
    @BindView(R2.id.llBrandList) LinearLayout llBrandList;//品牌列表跟布局
    @BindView(R2.id.lvBrandList) AmazingListView lvBrandList;//品牌列表
    @BindView(R2.id.llTextSearch) LinearLayout llTextSearch;//点击跳转搜素
    @BindView(R2.id.sideIndex) SideIndex sideIndex;//A-Z导航
    private BrandListAdapter adapter ;//品牌列表adapter
    private BrandListHeader brandListHeader;//品牌列表headerView
    //搜索
    @BindView(R2.id.llSearch) LinearLayout llSearch;//搜索跟布局
    @BindView(R2.id.etSearch) EditText etSearch;//搜索框
    @BindView(R2.id.ivDeleteSearch) ImageView ivDeleteSearch;//清空搜索keyword
    @BindView(R2.id.btnSearchCancel) Button btnSearchCancel;//取消搜索操作
    @BindView(R2.id.recyclerViewSuggest) RecyclerView recyclerViewSuggest;//搜索结果
    @BindView(R2.id.llEmptySuggest) LinearLayout llEmptySuggest;//空搜索
    @BindView(R2.id.tvInputBrand) TextView tvInputBrand;//空搜索提示内容keyword
    @BindView(R2.id.tvAddBrand) TextView tvAddBrand;//选择品牌按钮
    private BrandSuggestListAdapterNew suggestListAdapter;//搜索列表adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);
        initBrandList();
        initSearch();
        ArrayList<Pair<String, ArrayList<Brand>>> brandGroups = CommonPreference.getBrandGroups();
        if(!ArrayUtil.isEmpty(brandGroups)){
            initData(brandGroups);
        }else{
            CommonUtils.getBrandListDataFromServer(this, new OnBrandListDataLoaded() {

                @Override
                public void onLoad(ArrayList<Pair<String, ArrayList<Brand>>> brandGroups) {
                    initData(brandGroups);
                }
            });
        }
    }

    private void initData(ArrayList<Pair<String, ArrayList<Brand>>> brandGroups) {
        try{
            adapter.setData(brandGroups);
            genSideIndexAndHeader();
        }catch (Exception e){

        }
    }

    private void initBrandList(){
        lvBrandList.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.brand_header,lvBrandList,false));
        brandListHeader = new BrandListHeader(this);
        brandListHeader.setOnItemClickListener(new BrandListHeader.OnItemClickListener() {

            @Override
            public void onItemClick(Brand brand) {
                setResultIntent(brand);
            }
        });
        lvBrandList.addHeaderView(brandListHeader);
        adapter = new BrandListAdapter(this);
        lvBrandList.setAdapter(adapter);
        adapter.setOnBrandListener(this);
        llTextSearch.setOnClickListener(this);
    }

    private void initSearch(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerViewSuggest.setLayoutManager(linearLayoutManager);
        suggestListAdapter = new BrandSuggestListAdapterNew(this);
        suggestListAdapter.setOnItemClickListener(new BrandSuggestListAdapterNew.OnItemClickListener() {

            @Override
            public void onItemClick(Brand brand) {
                CommonUtils.saveHistoryBrand(brand);
                setResultIntent(brand);
            }
        });
        suggestListAdapter.setOnEmptyBrandClickListener(new BrandSuggestListAdapterNew.OnEmptyBrandClickListener() {

            @Override
            public void onClick(String brand) {
                Brand b = new Brand();
                b.brandName = brand;
                CommonUtils.saveHistoryBrand(b);
                setResultIntent(b);
            }
        });

        recyclerViewSuggest.setAdapter(suggestListAdapter.getWrapperAdapter());

        etSearch.setFilters(new InputFilter[]{new EmojiFilter(),new InputFilter.LengthFilter(8)});
        etSearch.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(s!=null){
                    String inputKeyWord = s.toString().trim();
                    handler.postDelayed(runnable = new HandlerRunnable(inputKeyWord),300);
                }

                if(s!= null&&!TextUtils.isEmpty(s.toString())){
                    ivDeleteSearch.setVisibility(View.VISIBLE);
                }else{
                    ivDeleteSearch.setVisibility(View.GONE);
                }

            }
        });

        tvAddBrand.setOnClickListener(this);
        ivDeleteSearch.setOnClickListener(this);
        btnSearchCancel.setOnClickListener(this);
    }


    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            if(what == GEN_KEY_WORD_SEARCH_RESULT){
                try {
                    Pair<String,List<Brand>> p = (Pair<String,List<Brand>>) msg.obj;
                    if(p!=null){
                        String keyword = p.first;
                        List<Brand> brands = p.second;
                        if(TextUtils.isEmpty(keyword)){
                            recyclerViewSuggest.setVisibility(View.GONE);
                            llEmptySuggest.setVisibility(View.GONE);
                        }else{
                            if(!ArrayUtil.isEmpty(brands)){
                                recyclerViewSuggest.setVisibility(View.VISIBLE);
                                llEmptySuggest.setVisibility(View.GONE);
                                List<Object> list = new ArrayList<>();
                                boolean isEquals = false;
                                for(Brand b:brands){
                                    if(b!=null&&!TextUtils.isEmpty(b.brandName)){
                                        if(b.brandName.equals(keyword)){
                                            isEquals = true;
                                        }
                                        list.add(b);
                                    }
                                }
                                if(!isEquals){
                                    list.add(0,keyword);
                                }
                                suggestListAdapter.setData(list);
                            }else{
                                recyclerViewSuggest.setVisibility(View.GONE);
                                llEmptySuggest.setVisibility(View.VISIBLE);
                                tvInputBrand.setText(keyword);
                            }
                        }
                    }
                }catch (Exception e){

                }
            }
            return false;
        }
    });

    private HandlerRunnable runnable;

    private void genSideIndexAndHeader(){
        ArrayList<Brand> historyBrands = CommonPreference.getHistoryBrands();

        if(!ArrayUtil.isEmpty(historyBrands)){
            if(lvBrandList.getHeaderViewsCount() == 0){
                lvBrandList.addHeaderView(brandListHeader);
            }
            brandListHeader.setData(historyBrands);
        }else{
            if(lvBrandList.getHeaderViewsCount() == 1){
                lvBrandList.removeHeaderView(brandListHeader);
            }
        }

        flMain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                sideIndex.genIndexBar(BrandListActivity.this,lvBrandList,0xDE000000,12,lvBrandList.getHeaderViewsCount());
                flMain.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    public class HandlerRunnable implements Runnable {

        private String keyword;

        public HandlerRunnable(String keyword){
            this.keyword = keyword;
        }

        @Override
        public void run() {

            new Thread(new Runnable() {

                @Override
                public void run() {
                    doSearchBrands(keyword);
                }
            }).start();
        }
    }

    private void doSearchBrands(String keyWord){
        List<Brand> list =null;
        if(!TextUtils.isEmpty(keyWord)){
            try {
                String response = OkHttpClientManager.getInstance()._postAsString(MyConstants.BRAND_SUGGEST_BY_KEYWORD,new OkHttpClientManager.Param("input",keyWord));
                BrandsSuggestResult result = JSON.parseObject(response, BrandsSuggestResult.class);
                if(result!=null && result.code == ParserUtils.RESPONSE_SUCCESS_CODE && result.obj!=null){
                    list = result.obj.list;
                    if(!ArrayUtil.isEmpty(list)){
                        for(Brand brand:list){
                            brand.brandName = brand.nameDisplay;
                        }
                    }
                }
            } catch (Exception e) {

            }
        }

        Pair<String,List<Brand>> pair = new Pair<>(keyWord,list);
        Message msg = handler.obtainMessage(GEN_KEY_WORD_SEARCH_RESULT, pair);
        handler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.llTextSearch){//跳转搜索
            llBrandList.setVisibility(View.GONE);
            llSearch.setVisibility(View.VISIBLE);
            ivDeleteSearch.setVisibility(View.GONE);
            recyclerViewSuggest.setVisibility(View.GONE);
            llEmptySuggest.setVisibility(View.GONE);
            etSearch.requestFocus();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    InputMethodManager inputManager = (InputMethodManager)etSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputManager != null) {
                        inputManager.showSoftInput(etSearch, 0);
                    }
                }
            },200);
        }else if(v.getId() == R.id.tvAddBrand){//选择了空的品牌
            String keyword = etSearch.getText().toString().trim();
            Brand brand = new Brand();
            brand.brandName = keyword;
            CommonUtils.saveHistoryBrand(brand);
            setResultIntent(brand);
        }else if(v.getId() == R.id.ivDeleteSearch){//清空keyword
            etSearch.setText("");//清空keyword
            recyclerViewSuggest.setVisibility(View.GONE);
            llEmptySuggest.setVisibility(View.GONE);
            tvInputBrand.setText("");
        }else if(v.getId() == R.id.btnSearchCancel){//取消搜索
            etSearch.setText("");//清空keyword
            llBrandList.setVisibility(View.VISIBLE);
            llSearch.setVisibility(View.GONE);
            recyclerViewSuggest.setVisibility(View.GONE);
            llEmptySuggest.setVisibility(View.GONE);
            tvInputBrand.setText("");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                    }
                }
            },200);
        }
    }

    private void setResultIntent(Brand brand) {
        Intent intent = new Intent();
        intent.putExtra(MyConstants.BRAND_RESULT,brand);
        setResult(RESULT_OK,intent);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }
            }
        },200);

        finish();
    }

    @Override
    public void onItemClick(Brand brand) {
        CommonUtils.saveHistoryBrand(brand);
        setResultIntent(brand);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null){
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
