package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.GoodsSearchResultAdapter;
import com.huapu.huafen.adapter.SearchHistoryAdapter;
import com.huapu.huafen.adapter.UserSearchResultAdapter2;
import com.huapu.huafen.beans.BaseResultNew;
import com.huapu.huafen.beans.CodeValuePair;
import com.huapu.huafen.beans.HotWordsResult;
import com.huapu.huafen.beans.KeyWordData;
import com.huapu.huafen.beans.SuggestGoodsBean;
import com.huapu.huafen.beans.SuggestUsersBean;
import com.huapu.huafen.callbacks.SimpleTextWatcher;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.common.ResultCode;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.KeyBoardUtil;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.SearchHistoryHelper;
import com.huapu.huafen.views.BGAFlowLayout;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 单独搜索页面
 *
 * @author liang_xs
 */
public class SearchActivity extends BaseActivity {

    @BindView(R.id.tvSwitch)
    TextView tvSwitch;
    @BindView(R.id.layoutSwitch)
    LinearLayout layoutSwitch;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.ivCancelSearch)
    ImageView ivCancelSearch;
    @BindView(R.id.layoutTitle)
    RelativeLayout layoutTitle;
    @BindView(R.id.searchHistory)
    RecyclerView searchHistory;
    @BindView(R.id.btnSearchRight)
    Button btnSearchRight;

    @BindView(R.id.goodsList)
    RecyclerView goodsList;

    @BindView(R.id.userSearchList)
    RecyclerView userSearchList;
    @BindView(R.id.empty_view)
    View illegalityView;

    private PopupWindow popupWindow;
    private SearchHistoryAdapter mAdapter;
    private View layoutSwitchPersonal, layoutSwitchProduct, layoutSwitchStar;
    private int type = 1;
    private String keyword;
    private TextDialog dialog;
    private View footer;
    private View header, header2;
    private BGAFlowLayout mFlowHotSearch;
    private HotWordsResult hotGoodsResult;
    private HotWordsResult hotUsersResult;
    private String defaultKeyword;

    private View goodsHeader, goodsHeaderSencond;

    private GoodsSearchResultAdapter searchResultAdapter;

    private UserSearchResultAdapter2 userSearchResultAdapter;

    private View firstHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        CommonUtils.buildMontage(this, "first_search");
        initView();
        initializeSearchHistory();
        startRequestForSearchHotGoods();
        startRequestForSearchHotUsers();
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
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /* 判断是否是“done”键 */
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /* 隐藏软键盘 */
                    KeyBoardUtil.hideSoftInput(SearchActivity.this);

                    String text = etSearch.getText().toString().trim();
                    String key = !TextUtils.isEmpty(text) ? text : defaultKeyword;

                    if (TextUtils.isEmpty(key)) {
                        toast("请输入搜索内容");
                        return false;
                    }

                    // 输入了敏感类容
                    if (illegalityView.getVisibility() == View.VISIBLE) {
                        return false;
                    }

                    Intent intent = new Intent();
                    if (type == 1) {
                        intent = new Intent(SearchActivity.this, SearchGoodsListActivity.class);
                        intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, key);
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
                    } else if (type == 2) {
                        intent = new Intent(SearchActivity.this, SearchPersonalListActivity.class);
                        intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, key);
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
                    } else if (type == 3) {
                        intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, key);
                        setResult(RESULT_OK, intent);
                    }
                    SearchHistoryHelper.put(new CodeValuePair(type, key));
                    return true;
                }
                return false;
            }
        });


        etSearch.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (etSearch.getText().length() > 0) {
                    Logger.e("get type:" + type);
                    if (type == 1) {
                        searchGoodsKeyWord();
                    } else if (type == 2) {
                        searchUserKeyWord();
                    }
                } else {
                    listNormal();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    ivCancelSearch.setVisibility(View.VISIBLE);
                } else {
                    ivCancelSearch.setVisibility(View.GONE);
                }

            }
        });
        layoutSwitch.setOnClickListener(this);
        ivCancelSearch.setOnClickListener(this);
        btnSearchRight.setOnClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(searchHistory.getContext(), LinearLayoutManager.VERTICAL, false);
        searchHistory.setLayoutManager(manager);
        mAdapter = new SearchHistoryAdapter(this);
        header = LayoutInflater.from(this).inflate(R.layout.view_headview_search_hot_personal, searchHistory, false);
        mFlowHotSearch = ButterKnife.findById(header, R.id.mFlowHotSearch);
        header2 = LayoutInflater.from(this).inflate(R.layout.view_headview_search_hot_personal2, searchHistory, false);
        footer = LayoutInflater.from(this).inflate(R.layout.clear_search_history_layout, searchHistory, false);
        TextView tvClearButton = ButterKnife.findById(footer, R.id.tvClearButton);
        tvClearButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog = new TextDialog(SearchActivity.this, false);
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
                            footer.setVisibility(View.GONE);
                            header2.setVisibility(View.GONE);
                            SearchHistoryHelper.clearSearchHistory();
                        }
                    }
                });
                dialog.show();
            }
        });
        mAdapter.addHeaderView(header);
        mAdapter.addHeaderView(header2);
        mAdapter.addFootView(footer);
        searchHistory.setAdapter(mAdapter.getWrapperAdapter());

        firstHeader = LayoutInflater.from(this).inflate(R.layout.search_common_header, null, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(goodsList.getContext(), LinearLayoutManager.VERTICAL, false);
        goodsList.setLayoutManager(linearLayoutManager);
        searchResultAdapter = new GoodsSearchResultAdapter(this);
        goodsList.setAdapter(searchResultAdapter.getWrapperAdapter());
        goodsHeader = LayoutInflater.from(this).inflate(R.layout.layout_goods_search_nodata, goodsList, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(userSearchList.getContext(), LinearLayoutManager.VERTICAL, false);
        userSearchList.setLayoutManager(layoutManager);
        userSearchResultAdapter = new UserSearchResultAdapter2(this);
        userSearchList.setAdapter(userSearchResultAdapter.getWrapperAdapter());
    }

    private void listNormal() {
        searchHistory.setVisibility(View.VISIBLE);
        goodsList.setVisibility(View.GONE);
        userSearchList.setVisibility(View.GONE);
        illegalityView.setVisibility(View.GONE);
    }

    private void initTipData(List<KeyWordData> arrayList) {
        if (mFlowHotSearch != null && !ArrayUtil.isEmpty(arrayList)) {
            mFlowHotSearch.removeAllViews();
            for (int i = 0; i < arrayList.size(); i++) {
                mFlowHotSearch.addView(getTagLabel(arrayList.get(i)));
            }
        }

    }

    private TextView getTagLabel(final KeyWordData data) {
        final TextView label = new TextView(this);

        if (data.getHighlight() == 1) {
            label.setBackgroundResource(R.drawable.search_highlight_bg);
            label.setTextColor(getResources().getColor(R.color.base_pink));
        } else {
            label.setBackgroundResource(R.drawable.search_normal_bg);
            label.setTextColor(getResources().getColor(R.color.text_color));
        }

        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        label.setTextSize(12);
        label.setCompoundDrawablePadding(CommonUtils.dp2px(3f));
        if (data.getType() == 1) { // 人
            Drawable drawable;
            if (data.getHighlight() == 0) {
                drawable = getResources().getDrawable(R.drawable.search_user_default);
            } else {
                drawable = getResources().getDrawable(R.drawable.search_user_import);
            }

            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            label.setCompoundDrawables(null, null, drawable, null);
        }
        int padding = CommonUtils.dp2px(8f);
        int paddingTop = CommonUtils.dp2px(2f);
        label.setPadding(padding, paddingTop, padding, paddingTop);
        label.setText(data.getKeyword());
        label.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (data.getType() == 2) {
                    Intent intent = new Intent(SearchActivity.this, SearchGoodsListActivity.class);
                    intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, data.getKeyword());
                    startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
                } else if (data.getType() == 1) {
                    Intent intent = new Intent(SearchActivity.this, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, Long.valueOf(data.getUserId()));
                    startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
                }

                //CommonUtils.dispatchAction(SearchActivity.this, data.getAction(), data.getTarget());

                SearchHistoryHelper.put(new CodeValuePair(type, data.getKeyword()));
            }
        });
        return label;
    }

    private void initializeSearchHistory() {
        LinkedList<CodeValuePair> searchList = SearchHistoryHelper.getSearchHistory();
        mAdapter.setData(searchList);
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
        CommonUtils.hideInput(this);
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
                listNormal();
                break;
            default:
                break;
        }
    }

    private void initSwitchPop(View v) {
        if (popupWindow == null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_search_switch, null);
            layoutSwitchPersonal = ButterKnife.findById(view, R.id.layoutSwitchPersonal);
            layoutSwitchProduct = ButterKnife.findById(view, R.id.layoutSwitchProduct);
            layoutSwitchStar = ButterKnife.findById(view, R.id.layoutSwitchStar);
            layoutSwitchPersonal.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (hotUsersResult != null && hotUsersResult.obj != null) {
                        initTipData(hotUsersResult.obj.hotWords);
                        if (!TextUtils.isEmpty(hotUsersResult.obj.defaultWord)) {
                            etSearch.setHint(hotUsersResult.obj.defaultWord);
                            defaultKeyword = hotUsersResult.obj.defaultWord;
                        }
                    }
                    listNormal();
                    tvSwitch.setText("用户");
                    type = 2;
                    popupWindow.dismiss();
                }
            });
            layoutSwitchProduct.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (hotGoodsResult != null && hotGoodsResult.obj != null) {
                        initTipData(hotGoodsResult.obj.hotWords);
                        if (!TextUtils.isEmpty(hotGoodsResult.obj.defaultWord)) {
                            etSearch.setHint(hotGoodsResult.obj.defaultWord);
                            defaultKeyword = hotGoodsResult.obj.defaultWord;
                        }
                    }
                    listNormal();
                    tvSwitch.setText("宝贝");
                    type = 1;
                    popupWindow.dismiss();
                }
            });
            layoutSwitchStar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchActivity.this, StarListActivity.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                }
            });
            popupWindow = new PopupWindow(view, (int) (120 * dm.density), LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setAnimationStyle(R.style.pop_search_switch);
        }
        popupWindow.showAsDropDown(v);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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

    private void searchUserKeyWord() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
//        ProgressDialog.showProgress(this);
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("keyword", etSearch.getText().toString().trim());
        OkHttpClientManager.postAsyn(MyConstants.QUERY_KEY_WORDS_USERS, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
//                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                Logger.e("get response:" + response);
//                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                searchHistory.setVisibility(View.GONE);
                adapterViewClear();
                try {
                    SuggestUsersBean result = JSON.parseObject(response, SuggestUsersBean.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        illegalityView.setVisibility(View.GONE);
                        searchHistory.setVisibility(View.GONE);
                        if (null == result.obj.result || result.obj.result.size() > 0) {
                            TextView textView = ButterKnife.findById(firstHeader, R.id.searchKeyWord);
                            textView.setText(Html.fromHtml("搜索" + "<font color='#419BF9'>" + "\"" + etSearch.getText().toString().trim() + "\"" + "</font>" + "相关商品"));
                            textView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchActivity.this, SearchGoodsListActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, etSearch.getText().toString().trim());
                                    startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
                                }
                            });

                            userSearchResultAdapter.addHeaderView(firstHeader);
                            userSearchResultAdapter.setKeyWord(etSearch.getText().toString().trim());
                            userSearchResultAdapter.setData(result.obj.result);
                            userSearchList.setVisibility(View.VISIBLE);
                        } else {

                            goodsList.setVisibility(View.GONE);
                            userSearchList.setVisibility(View.GONE);
                            searchHistory.setVisibility(View.VISIBLE);
//                            userSearchResultAdapter.addHeaderView(goodsHeader);
//                            userSearchResultAdapter.setKeyWord(etSearch.getText().toString().trim());
//                            userSearchResultAdapter.setData(result.obj.result);
//                            userSearchList.setVisibility(View.VISIBLE);
                        }
                    } else {
                        handleError(result);
                    }
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
        });
    }

    private void adapterViewClear() {
        userSearchList.removeAllViews();
        userSearchResultAdapter.removeHeaders();
        goodsList.removeAllViews();
        searchResultAdapter.removeHeaders();
    }

    private void searchGoodsKeyWord() {
        if (!isNetAvailable())
            return;

//        ProgressDialog.showProgress(this);
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("keyword", etSearch.getText().toString().trim());
        OkHttpClientManager.postAsyn(MyConstants.QUERY_KEY_WORDS_GOODS, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
//                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                Logger.e("get response:" + response);
//                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                searchHistory.setVisibility(View.GONE);
                adapterViewClear();
                try {
                    SuggestGoodsBean result = JSON.parseObject(response, SuggestGoodsBean.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {

                        illegalityView.setVisibility(View.GONE);

                        if (null == result.obj.result || result.obj.result.size() > 0) {
                            TextView textView = ButterKnife.findById(firstHeader, R.id.searchKeyWord);
                            textView.setText(Html.fromHtml("搜索" + "<font color='#419BF9'>" + "\"" + etSearch.getText().toString().trim() + "\"" + "</font>" + "相关用户"));
                            textView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchActivity.this, SearchPersonalListActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, etSearch.getText().toString().trim());
                                    startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
                                }
                            });
                            searchResultAdapter.addHeaderView(firstHeader);
                            searchResultAdapter.setData(result.obj.result);
                            goodsList.setVisibility(View.VISIBLE);
                        } else {

                            goodsList.setVisibility(View.GONE);
                            userSearchList.setVisibility(View.GONE);
                            searchHistory.setVisibility(View.VISIBLE);
//                            searchResultAdapter.setData(null);
//                            goodsHeaderSencond = LayoutInflater.from(SearchActivity.this).inflate(R.layout.view_headview_search_hot_personal, goodsList, false);
//                            BGAFlowLayout flowLayout = ButterKnife.findById(goodsHeaderSencond, R.id.mFlowHotSearch);
//                            if (hotGoodsResult != null && hotGoodsResult.obj != null) {
//                                List<KeyWordData> keyWordDataList = hotGoodsResult.obj.hotWords;
//                                if (flowLayout != null && !ArrayUtil.isEmpty(keyWordDataList)) {
//                                    flowLayout.removeAllViews();
//                                    for (int i = 0; i < keyWordDataList.size(); i++) {
//                                        flowLayout.addView(getTagLabel(keyWordDataList.get(i)));
//                                    }
//                                }
//                                if (!TextUtils.isEmpty(hotGoodsResult.obj.defaultWord)) {
//                                    etSearch.setHint(hotGoodsResult.obj.defaultWord);
//                                    defaultKeyword = hotGoodsResult.obj.defaultWord;
//                                }
//                            }
//                            searchResultAdapter.addHeaderView(goodsHeader);
//                            searchResultAdapter.addHeaderView(goodsHeaderSencond);
//                            goodsList.setVisibility(View.VISIBLE);
                        }

                    } else {
                        handleError(result);
                    }
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
        });
    }

    /**
     * 获取查询热搜列表
     */
    private void startRequestForSearchHotGoods() {
        if (!isNetAvailable())
            return;

        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();
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
                    HotWordsResult result = JSON.parseObject(response, HotWordsResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        illegalityView.setVisibility(View.GONE);
                        goodsList.setVisibility(View.GONE);
                        searchHistory.setVisibility(View.VISIBLE);
                        hotGoodsResult = result;
                        if (hotGoodsResult.obj != null) {
                            initTipData(hotGoodsResult.obj.hotWords);
                            if (!TextUtils.isEmpty(hotGoodsResult.obj.defaultWord)) {
                                etSearch.setHint(hotGoodsResult.obj.defaultWord);
                                defaultKeyword = hotGoodsResult.obj.defaultWord;
                            }
                        }
                    } else {
                        handleError(result);
                    }
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }
            }
        });
    }

    private void startRequestForSearchHotUsers() {
        if (!isNetAvailable())
            return;

        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();
        LogUtil.i("liang", "查询热搜params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.QUERY_HOT_USERS, params, new OkHttpClientManager.StringCallback() {

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
                    HotWordsResult result = JSON.parseObject(response, HotWordsResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        illegalityView.setVisibility(View.GONE);
                        hotUsersResult = result;
                    } else {
                        handleError(result);
                    }
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleError(BaseResultNew result) {
        int code = result.code;
        if (code == ResultCode.RESPONSE_ILLEGA_WORDS || code == ResultCode.RESPONSE_ILLEGA_IMAGE) {
            illegalityView.setVisibility(View.VISIBLE);
        } else {
            illegalityView.setVisibility(View.GONE);
            CommonUtils.error(result, SearchActivity.this, "");
        }
    }
}
