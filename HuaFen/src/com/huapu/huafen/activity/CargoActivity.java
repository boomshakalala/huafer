package com.huapu.huafen.activity;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CargoAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.CommodityListResult;
import com.huapu.huafen.callbacks.SimpleTextWatcher;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.EmojiFilter;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Pair;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Xk
 * Created by lenovo on 2017/5/3.
 */

public class CargoActivity extends BaseActivity {

    @BindView(R.id.ptrFrameLayout)
    PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recycleCargo;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.llTextSearch)
    LinearLayout llTextSearch;
    @BindView(R.id.ivSearch)
    ImageView ivSearch;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.ivDeleteSearch)
    ImageView ivDeleteSearch;
    @BindView(R.id.btnSearchCancel)
    Button btnSearchCancel;
    @BindView(R.id.layoutTitle)
    RelativeLayout layoutTitle;
    private long userId;
    CargoAdapter adapter;
    private int page;
    private static final int GEN_KEY_WORD_SEARCH_RESULT = 0;//返回搜索结果
    private HandlerRunnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cargo_activity);
        ButterKnife.bind(this);
        initView();
        startRequestForGetRecommend(REFRESH);
    }

    private void initView() {
        llTextSearch.setOnClickListener(this);
        btnSearchCancel.setOnClickListener(this);
        if (getIntent().hasExtra(MyConstants.EXTRA_USER_ID)) {
            userId = getIntent().getLongExtra(MyConstants.EXTRA_USER_ID, 0);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(false);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleCargo.setLayoutManager(layoutManager);

        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, recycleCargo,false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_cargo);
        adapter = new CargoAdapter(this);
        adapter.setEmptyView(viewEmpty);

        recycleCargo.setAdapter(adapter.getWrapperAdapter());
        etSearch.setFilters(new InputFilter[]{new EmojiFilter(), new InputFilter.LengthFilter(8)});
        etSearch.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    String inputKeyWord = s.toString().trim();
                    handler.postDelayed(runnable = new HandlerRunnable(inputKeyWord), 500);
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
                LinearLayoutManager manager = (LinearLayoutManager) recycleCargo.getLayoutManager();
                int index = manager.findFirstVisibleItemPosition();
                View childAt = recycleCargo.getChildAt(0);
                boolean indexTop = false;
                if (childAt == null || (index == 0 && childAt.getTop() == 0)) {
                    indexTop = true;
                }

                return indexTop && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                refresh();
            }
        });

    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            if (what == GEN_KEY_WORD_SEARCH_RESULT) {
                try {
                    Pair<String, List<Commodity>> p = (Pair<String, List<Commodity>>) msg.obj;
                    if (p != null) {
                        String keyword = p.first;
                        ArrayList<Commodity> commodities = (ArrayList<Commodity>) p.second;
                        if (TextUtils.isEmpty(keyword)) {
                            recycleCargo.setVisibility(View.GONE);
                        } else {
                            if (!ArrayUtil.isEmpty(commodities)) {
                                recycleCargo.setVisibility(View.VISIBLE);
                                adapter.setData(commodities);
                            } else {
                                recycleCargo.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            return false;
        }
    });


    public class HandlerRunnable implements Runnable {

        private String keyword;

        public HandlerRunnable(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public void run() {

            new Thread(new Runnable() {

                @Override
                public void run() {
                    List<Commodity> commodities = getSuggest(keyword);
                    Pair<String, List<Commodity>> pair = new Pair<>(keyword, commodities);
                    Message msg = handler.obtainMessage(GEN_KEY_WORD_SEARCH_RESULT, pair);
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }

    private List<Commodity> getSuggest(String keyWord) {
        if (!TextUtils.isEmpty(keyWord)) {
            ArrayList<Commodity> commodities = CommonPreference.getCommod();
            return CommonUtils.searchCommod(keyWord, commodities);
        } else {
            return null;
        }
    }

    private void initData() {
        startRequestForGetRecommend(REFRESH);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.llTextSearch) {//跳转搜索
            llTextSearch.setVisibility(View.GONE);
            layoutTitle.setVisibility(View.VISIBLE);
            ivDeleteSearch.setVisibility(View.GONE);
            recycleCargo.setVisibility(View.GONE);
            etSearch.requestFocus();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    InputMethodManager inputManager = (InputMethodManager) etSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputManager != null) {
                        inputManager.showSoftInput(etSearch, 0);
                    }
                }
            }, 200);
        } else if (v.getId() == R.id.btnSearchCancel) {//取消搜索
            etSearch.setText("");//清空keyword
            llTextSearch.setVisibility(View.VISIBLE);
            layoutTitle.setVisibility(View.GONE);
            recycleCargo.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                    }
                }
            }, 200);
        }


    }

    //获取服务器数据
    public void startRequestForGetRecommend(final String extra) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", 80 + "");
        //        params.put("userId", userId + "");
        OkHttpClientManager.postAsyn(MyConstants.USER_SELLING, params, new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("liang", "推荐列表error:" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                }
                try {
                    LogUtil.e("liang", response.toString());
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            CommodityListResult result = JSON.parseObject(baseResult.obj, CommodityListResult.class);
                            initData(result, extra);
                            CommonPreference.setCommod(result.getGoodsList());
                        }
                    } else {
                        CommonUtils.error(baseResult, CargoActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(CommodityListResult result, String extra) {
        if (result.getPage() == 0) {
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(recycleCargo);
        }

        ArrayList<Commodity> list = result.getGoodsList();
        ArrayList<Commodity> var1 = null;
        if (list != null) {
            var1 = (ArrayList<Commodity>) list.clone();
        }
        if (extra.equals(LOADING)) {
            adapter.setData(var1);
        } else if (extra.equals(REFRESH)) {
            adapter.setData(var1);
        } else if (LOAD_MORE.equals(extra)) {
            if (var1 == null) {
                var1 = new ArrayList<>();
            }
            adapter.addDatas(var1);
        }
    }

}
