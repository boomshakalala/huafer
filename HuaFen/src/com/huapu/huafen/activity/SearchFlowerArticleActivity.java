package com.huapu.huafen.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.SearchFlowerHistoryAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CodeValuePair;
import com.huapu.huafen.beans.KeyWordData;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.HeaderAndFooterWrapper;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.SearchArticleHistoryHelper;
import com.huapu.huafen.views.BGAFlowLayout;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 单独搜索页面
 *
 * @author liang_xs
 */
public class SearchFlowerArticleActivity extends BaseActivity {
    private View layoutSwitch;
    private EditText etSearch;
    private Button btnTitleRight;
    private PopupWindow popwindow;
   // private TextView tvSwitch;
    private ImageView ivCancelSearch;

    private RecyclerView searchHistory;//搜索历史列表
    private SearchFlowerHistoryAdapter mAdapter;
    private View layoutSwitchPersonal, layoutSwitchProduct, layoutSwitchStar;
    private int type = 1;
    private String keyword;
    private TextDialog dialog;
    private HeaderAndFooterWrapper wrapper;
    private View footer;
    private View header, header2;
    private BGAFlowLayout mFlowHotSearch;
    private int page = 0;
    private List<KeyWordData> keyWordList = new ArrayList<KeyWordData>();
    private String ary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_article);
        CommonUtils.buildMontage(this, "first_search");
        initView();
        ary = getIntent().getStringExtra("ary");
        initializeSearchHistory();
        startRequestForSearchKeyWord();
        if (getIntent().hasExtra("type")) {
            type = getIntent().getIntExtra("type", 1);
        }

        if (getIntent().hasExtra(MyConstants.EXTRA_SEARCH_KEYWORD)) {
            keyword = getIntent().getStringExtra(MyConstants.EXTRA_SEARCH_KEYWORD);
        }
        if (!TextUtils.isEmpty(keyword)) {
            etSearch.setText(keyword);
        }

    }

    public void initView() {
        layoutSwitch = findViewById(R.id.layoutSwitch);
//		etSearch.setImeOptions(EditorInfo.IME_ACTION_SEND);
        btnTitleRight = (Button) findViewById(R.id.btnSearchRight);
        //tvSwitch = (TextView) findViewById(R.id.tvSwitch);
        ivCancelSearch = (ImageView) findViewById(R.id.ivCancelSearch);
        etSearch = (EditText) findViewById(R.id.etSearch);
        initEvent();
        layoutSwitch.setOnClickListener(this);
        btnTitleRight.setOnClickListener(this);
        ivCancelSearch.setOnClickListener(this);

        searchHistory = (RecyclerView) findViewById(R.id.searchHistory);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        searchHistory.setLayoutManager(manager);

    }

    private void initTipData(List<KeyWordData> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            mFlowHotSearch.addView(getTagLabel(arrayList.get(i)));
        }
    }

    private TextView getTagLabel(final KeyWordData data) {
        final TextView label = new TextView(this);
        label.setBackgroundResource(R.drawable.text_pink_light_round_bg);
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        label.setTextColor(getResources().getColor(R.color.white));
        if (data.getType() == 0) { // 商品
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)searchHistory.getLayoutParams(); //-----------------
            searchHistory.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
            searchHistory.setLayoutParams(param);
        } else if (data.getType() == 1) { // 人
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)searchHistory.getLayoutParams();//-----------------------
            searchHistory.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
            searchHistory.setLayoutParams(param);
//            label.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pop_search_personal, 0);
        }
        int padding = BGAFlowLayout.dp2px(this, 8);
        int paddingTop = BGAFlowLayout.dp2px(this, 4);
        label.setPadding(padding, paddingTop, padding, paddingTop);
        label.setText(data.getKeyword());
        label.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getType() == 0) {
                    Intent intent = new Intent(SearchFlowerArticleActivity.this, SearchGoodsListActivity.class);
                    intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, data.getKeyword());
                    startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
                } else if (data.getType() == 1) {
                    Intent intent = new Intent(SearchFlowerArticleActivity.this, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, Long.valueOf(data.getUserId()));
                    startActivity(intent);
                }
            }
        });
        return label;
    }

    private void initializeSearchHistory() {
        LinkedList<CodeValuePair> searchList = SearchArticleHistoryHelper.getSearchHistory();

        if (mAdapter == null) {
            mAdapter = new SearchFlowerHistoryAdapter(this, searchList);
            wrapper = new HeaderAndFooterWrapper(mAdapter);
            footer = LayoutInflater.from(this).inflate(R.layout.clear_search_history_layout, searchHistory,false);
            footer.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog = new TextDialog(SearchFlowerArticleActivity.this, false);
                    dialog.setContentText("您确定清空历史记录吗？");
                    dialog.setLeftText("取消");
                    dialog.setLeftCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            dialog.dismiss();
                        }
                    });
                    dialog.setRightText("确定");
                    dialog.setRightCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            if (mAdapter != null) {
                                mAdapter.clearData();
                                wrapper.notifyDataSetChanged();
                                footer.setVisibility(View.GONE);
                                header2.setVisibility(View.GONE);
                                SearchArticleHistoryHelper.clearSearchHistory();
                            }
                        }
                    });
                    dialog.show();
                }
            });
            if (ary != null && ary.equals("arity")) {

            }else{
                header = LayoutInflater.from(this).inflate(R.layout.view_headview_search_hot_personal, null);
                mFlowHotSearch = (BGAFlowLayout) header.findViewById(R.id.mFlowHotSearch);
                wrapper.addHeaderView(header);
            }
            header2 = LayoutInflater.from(this).inflate(R.layout.view_headview_search_hot_personal2, searchHistory,false);
            wrapper.addFootView(footer);
            wrapper.addHeaderView(header2);
            searchHistory.setAdapter(wrapper);

        } else {
            mAdapter.setData(searchList);
            mAdapter.notifyDataSetChanged();
            wrapper.notifyDataSetChanged();
        }

        if (!ArrayUtil.isEmpty(searchList)) {

            footer.setVisibility(View.VISIBLE);
            header2.setVisibility(View.VISIBLE);
        } else {
            footer.setVisibility(View.GONE);
            header2.setVisibility(View.GONE);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
//		hideKeyBoard(etSearch);
        CommonUtils.hideInput(this);
    }

    private void initEvent() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /* 判断是否是“done”键 */
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /* 隐藏软键盘 */
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    if (TextUtils.isEmpty(etSearch.getText().toString().trim())) {
                        toast("请输入搜索内容");
                        return false;
                    }
                    if (!CommonUtils.isNickName(etSearch.getText().toString().trim())) {
                        toast("只支持汉字、字母及数字");
                        return false;
                    }

                    Intent intent = new Intent();

                    if (ary != null && ary.equals("arity")) {
                        intent = new Intent(SearchFlowerArticleActivity.this, SearchArticleActivity.class);
                        intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, etSearch.getText().toString());
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_FLOWER);
                    } else if (type == 1) {
                        intent = new Intent(SearchFlowerArticleActivity.this, SearchGoodsListActivity.class);
                        intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, etSearch.getText().toString());
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
                    } else if (type == 2) {
                        intent = new Intent(SearchFlowerArticleActivity.this, SearchPersonalListActivity.class);
                        intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, etSearch.getText().toString());
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
                    } else if (type == 3) {
                        intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, etSearch.getText().toString());
                        setResult(RESULT_OK, intent);
                    }
                    SearchArticleHistoryHelper.put(new CodeValuePair(type, etSearch.getText().toString()));
//					finish();
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutSwitch:
                initSwitchPop(v);
                break;

            case R.id.btnSearchRight:
                finish();
                break;

            case R.id.ivCancelSearch:
                etSearch.getText().clear();
                break;
            default:
                break;
        }
    }

    private void initSwitchPop(View v) {
        if (popwindow == null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_search_switch, null);
            layoutSwitchPersonal = view.findViewById(R.id.layoutSwitchPersonal);
            layoutSwitchProduct = view.findViewById(R.id.layoutSwitchProduct);
            layoutSwitchStar = view.findViewById(R.id.layoutSwitchStar);
            layoutSwitchPersonal.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                  //  tvSwitch.setText("用户");
                    etSearch.setHint("搜索你想找的人");
                    type = 2;
                    popwindow.dismiss();
                }
            });
            layoutSwitchProduct.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                  //  tvSwitch.setText("宝贝");
                    etSearch.setHint("搜索你想要的宝贝");
                    type = 1;
                    popwindow.dismiss();
                }
            });
            layoutSwitchStar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchFlowerArticleActivity.this, StarListActivity.class);
                    startActivity(intent);
                    popwindow.dismiss();
                }
            });
            popwindow = new PopupWindow(view, (int) (120 * dm.density), LayoutParams.WRAP_CONTENT);
            popwindow.setFocusable(true);
            popwindow.setOutsideTouchable(true);
            popwindow.setBackgroundDrawable(new BitmapDrawable());
            popwindow.setAnimationStyle(R.style.pop_search_switch);
        }
        popwindow.showAsDropDown(v);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyConstants.REQUEST_CODE_FOR_FLOWER) {
            initializeSearchHistory();
        } else if (resultCode == RESULT_OK) {
            if (requestCode == MyConstants.REQUEST_CODE_FOR_SEARCH) {
                initializeSearchHistory();
                if (data != null) {
                    String keyword = data.getStringExtra("keyword");
                    if (!TextUtils.isEmpty(keyword)) {
                        etSearch.setText(keyword);
                    }
                }

            }
        }
    }

    /**
     * 获取查询热搜列表
     *
     * @param
     */
    private void startRequestForSearchKeyWord() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        LogUtil.i("liang", "查询热搜params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.QUERY_HOT_GOODS, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "查询热搜列表:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            JSONObject object = JSON.parseObject(baseResult.obj);
                            int page = object.getIntValue("page");
                            // 解析数据
                            List<KeyWordData> list = ParserUtils.parserSearchKeyWordData(baseResult.obj);
                            if (list != null) {
                                //设置数据
                                keyWordList.addAll(list);
                                initTipData(keyWordList);
                            } else {
                                keyWordList = new ArrayList<KeyWordData>();
                                toast("搜索无结果");
                            }
                        }
                    } else {
                        CommonUtils.error(baseResult, SearchFlowerArticleActivity.this, "");
                    }
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }

            }
        });
    }
}
