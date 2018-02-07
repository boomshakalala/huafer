package com.huapu.huafen.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.CargoAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.CommodityListResult;
import com.huapu.huafen.callbacks.SimpleTextWatcher;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.EmojiFilter;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by admin on 2017/6/5.
 */

public class GoodsListActivity extends BaseActivity implements LoadMoreWrapper.OnLoadMoreListener {

    @BindView(R2.id.ivSearch) ImageView ivSearch;
    @BindView(R2.id.etSearch) EditText etSearch;
    @BindView(R2.id.ivDeleteSearch) ImageView ivDeleteSearch;
    @BindView(R2.id.btnSearchCancel) Button btnSearchCancel;
    @BindView(R2.id.layoutTitle) RelativeLayout layoutTitle;
    @BindView(R2.id.ptrFrameLayout) PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R2.id.recyclerView) RecyclerView recyclerView;
    private CargoAdapter adapter;
    private String keyword = "";
    private View loadMoreLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);
        initView();
        refresh();
    }

    private void initView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, recyclerView,false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_cargo);
        adapter = new CargoAdapter(this);
        adapter.setEmptyView(viewEmpty);
        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        adapter.setOnLoadMoreListener(this);

        recyclerView.setAdapter(adapter.getWrapperAdapter());

        etSearch.setFilters(new InputFilter[]{new EmojiFilter(), new InputFilter.LengthFilter(8)});
        etSearch.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    final String inputKeyWord = s.toString().trim();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            keyword = inputKeyWord;
                            refresh();
                        }
                    },500);
                }

                if (s != null && !TextUtils.isEmpty(s.toString())) {
                    ivDeleteSearch.setVisibility(View.VISIBLE);
                } else {
                    ivDeleteSearch.setVisibility(View.GONE);
                }
            }
        });

        ptrFrameLayout.buildPtr(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int index = manager.findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);
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

        ivDeleteSearch.setVisibility(View.GONE);
        ivDeleteSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });

        btnSearchCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                        }
                        onBackPressed();
                    }
                }, 200);
            }
        });
    }


    private int page;
    private void doRequest(final String extra) {
        HashMap<String,String> params = new HashMap<>();
        params.put("page", page + "");
        params.put("userId",String.valueOf(CommonPreference.getUserId()));
        params.put("keyword",keyword);
        OkHttpClientManager.postAsyn(MyConstants.SEARCH_GOODS_LIST, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.toast(GoodsListActivity.this, "请检查网络连接");
                if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                }
            }


            @Override
            public void onResponse(String response) {
                // 调用刷新完成
                if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            CommodityListResult result = JSON.parseObject(baseResult.obj, CommodityListResult.class);
                            initData(result, extra);
                        }
                    } else {
                        CommonUtils.error(baseResult, GoodsListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void refresh(){
        page = 0;
        doRequest(REFRESH);
    }

    private void loadMore(){
        doRequest(LOAD_MORE);
    }


    private void initData(CommodityListResult result, String extra) {
        if (result.getPage() == 0) {
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(loadMoreLayout);
        }

        page++;


        ArrayList<Commodity> list = result.getGoodsList();

        if (extra.equals(LOADING)) {
            adapter.setData(list);
        } else if (extra.equals(REFRESH)) {
            adapter.setData(list);
        } else if (LOAD_MORE.equals(extra)) {
            if (list == null) {
                list = new ArrayList<>();
            }
            adapter.addDatas(list);
        }
    }


    @Override
    public void onLoadMoreRequested() {
        loadMore();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
