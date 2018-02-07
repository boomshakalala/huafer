package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.GridAdapter;
import com.huapu.huafen.beans.Audit;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.GoodsInfoBean;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.events.GoodsEditEvent;
import com.huapu.huafen.events.RefreshEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ShareHelper;
import com.huapu.huafen.views.GoodsDetailBottomLayout;
import com.huapu.huafen.views.GoodsDetailHeader1;
import com.huapu.huafen.views.TitleBarNew;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import butterknife.BindView;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import de.greenrobot.event.EventBus;

/**
 * 商品详情
 */
public class GoodsDetailsActivity extends BaseActivity implements GoodsDetailBottomLayout.OnFavStateChangedListener {

    private final static String TAG = GoodsDetailsActivity.class.getSimpleName();
    @BindView(R2.id.titleBar)
    TitleBarNew titleBar;
    @BindView(R2.id.llContainer)
    LinearLayout llContainer;
    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.goodsDetailBottom)
    GoodsDetailBottomLayout goodsDetailBottom;
    private String goodsId;
    private int position;
    private GoodsDetailHeader1 header;
    private boolean isBuyer;
    private boolean hasCandidateCampaigns;
    private int type = -1;
    private TextView tvMsgUnRead;
    private GridAdapter adapter;
    private String recTraceId;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_goods_details_new);
        CommonUtils.computeCount(this, fromActivity);
        initIntentData();
        initView();
        startRequestForCommodityDetail();
    }

    private void initIntentData() {
        goodsId = mIntent.getStringExtra(MyConstants.EXTRA_GOODS_DETAIL_ID);
        position = mIntent.getIntExtra(MyConstants.POSITION, -1);
        recTraceId = mIntent.getStringExtra(MyConstants.REC_TRAC_ID);
        searchQuery = mIntent.getStringExtra(MyConstants.SEARCH_QUERY);
    }

    private void initView() {
        setContentView(R.layout.activity_goods_details_new);
        titleBar.setBackgroundAlpha(0);

        titleBar.getBtnTitleRight().setImageResource(R.drawable.personal_title_more);
        titleBar.getBtnTitleBarRight2().setImageResource(R.drawable.personal_title_share);
        titleBar.getBtnTitleLeft().setImageResource(R.drawable.personal_title_back);
        titleBar.getBtnTitleBarRight2().setVisibility(View.GONE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new GridAdapter(this);
        recyclerView.setAdapter(adapter.getWrapperAdapter());

        header = new GoodsDetailHeader1(this);
        header.setSearchQuery(searchQuery);
        header.setRecTraceId(recTraceId);
        header.setRecIndex(position);
        header.setOnRefreshListener(new GoodsDetailHeader1.OnRefreshListener() {

            @Override
            public void onRefresh() {
                startRequestForCommodityDetail();
            }
        });
        adapter.addHeaderView(header);
        adapter.setSearchQuery(searchQuery);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LogUtil.e(TAG, dy);
                int lHeight = CommonUtils.getScreenWidth();
                titleBar.setAlphaInRecyclerView(dy, lHeight);
            }
        });
    }

    private void initShare(GoodsInfo goodsInfo) {
        if (goodsInfo == null) {
            titleBar.getBtnTitleBarRight2().setVisibility(View.GONE);
            return;
        }
        Audit audit = CommonPreference.getAudit();
        int shareAble = 0;
        if (audit != null) {
            shareAble = audit.getShareable();
        }
        int goodsState = goodsInfo.getGoodsState();
        int auditStatus = goodsInfo.getAuditStatus();
        if ((goodsState == 1 && auditStatus == 5) || (goodsState == 2 && auditStatus == 5)) {
            if (shareAble == 1) {
                titleBar.getBtnTitleBarRight2().setVisibility(View.VISIBLE);
            } else {
                titleBar.getBtnTitleBarRight2().setVisibility(View.GONE);
            }
        } else {
            titleBar.getBtnTitleBarRight2().setVisibility(View.VISIBLE);
        }


    }

    /**
     * 获取商品详情
     *
     * @param
     */
    private void startRequestForCommodityDetail() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("goodsId", goodsId);
        if (!TextUtils.isEmpty(recTraceId)) {
            params.put("recTraceId", recTraceId);
        }

        if (position != -1) {
            params.put("recIndex", String.valueOf(position));
        }
        if (!TextUtils.isEmpty(searchQuery)) {
            params.put("searchQuery", searchQuery);
        }

        LogUtil.i(TAG, "商品详情params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETGOODSDETAIL, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i(TAG, "商品详情:" + response);
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
                            if (object.containsKey("isBuyer")) {
                                isBuyer = object.getBoolean("isBuyer");
                            }
                            if (object.containsKey("hasCandidateCampaigns")){
                                hasCandidateCampaigns = object.getInteger("hasCandidateCampaigns")!=0;
                            }
                            GoodsInfoBean bean = ParserUtils.parserProDetailsData(baseResult.obj);
                            initData(bean);
                            llContainer.setVisibility(View.VISIBLE);
                        } else {
                            llContainer.setVisibility(View.GONE);
                        }
                    } else {
                        llContainer.setVisibility(View.GONE);
                        CommonUtils.error(baseResult, GoodsDetailsActivity.this, "");
                    }
                } catch (Exception e) {
                    llContainer.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(final GoodsInfoBean bean) {
        if (bean != null) {
            int isAuction = bean.getGoodsInfo().isAuction;
            if (isAuction == 1) {
                titleBar.setTitle("拍卖详情");
            } else {
                titleBar.setTitle("商品详情");
            }

            header.setData(bean, getSupportFragmentManager());
            adapter.setData(bean.recItems);
            adapter.setRecTraceId(bean.recTraceId);
            goodsDetailBottom.setHasCandidateCampaigns(hasCandidateCampaigns);
            goodsDetailBottom.setData(bean, goodsId, isBuyer);
            goodsDetailBottom.setOnFavStateChangedListener(this);
            goodsDetailBottom.setRecTranceId(recTraceId);
            goodsDetailBottom.setRecIndex(position);
            goodsDetailBottom.setSearchQuery(searchQuery);
            goodsDetailBottom.setGoodsId(goodsId);
            goodsDetailBottom.setOnRefreshListener(new GoodsDetailBottomLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    startRequestForCommodityDetail();
                }
            });

            titleBar.setOnRightButtonClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    initPopMore(bean, v);
                }
            });
            titleBar.setOnRightButton2ClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (bean == null || bean.getGoodsInfo() == null) {
                        return;
                    }
                    ShareHelper.shareGoods(GoodsDetailsActivity.this, bean, recTraceId, searchQuery, position);
                    //ShareHelper.shareGoods(GoodsDetailsActivity.this, bean.getGoodsInfo(), recTraceId, searchQuery, position);
                }
            });

            initShare(bean.getGoodsInfo());
        }
    }

    @Override
    public void onBackPressed() {
        if (position >= 0 && type != -1) {
            Intent intent = new Intent();
            intent.putExtra("position", position);
            intent.putExtra("type", type);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    public void onEventMainThread(final Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof GoodsEditEvent) {
            GoodsEditEvent event = (GoodsEditEvent) obj;
            if (event.isSave) {
                startRequestForCommodityDetail();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.REQUEST_CODE_COMMENT) {
                startRequestForCommodityDetail();
            } else if (requestCode == MyConstants.REQUEST_CODE_FOR_ORDER_CONFIRM) {
                startRequestForCommodityDetail();
            } else if (requestCode == MyConstants.EXTRA_DESPOCIT) {
                startRequestForCommodityDetail();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (header != null) {
            header.removeCallback();
        }
    }

    private PopupWindow morePopupWindow;

    private void initPopMore(final GoodsInfoBean bean, View v) {

        if (morePopupWindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_more, null);
            View layoutSwitchHome = view.findViewById(R.id.layoutSwitchHome);
            View layoutSwitchMsg = view.findViewById(R.id.layoutSwitchMsg);
            View layoutSwitchMine = view.findViewById(R.id.layoutSwitchMine);
            View layoutSwitchReport = view.findViewById(R.id.layoutSwitchReport);
            tvMsgUnRead = (TextView) view.findViewById(R.id.tvMsgUnRead);
            layoutSwitchHome.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(1);
                    morePopupWindow.dismiss();
                }
            });
            layoutSwitchMsg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(3);
                    morePopupWindow.dismiss();
                }
            });
            layoutSwitchMine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(4);
                    morePopupWindow.dismiss();
                }
            });
            layoutSwitchReport.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActionToReport(bean);
                    morePopupWindow.dismiss();
                }
            });
            morePopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            morePopupWindow.setFocusable(true);
            morePopupWindow.setOutsideTouchable(true);
            morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            morePopupWindow.setAnimationStyle(R.style.pop_search_switch);
        }
        tvMsgUnRead.setVisibility(titleBar.getMoreBtnBadgeVisibility() ? View.VISIBLE : View.GONE);
        morePopupWindow.showAsDropDown(v);
    }

    private void actionToMineFragment(int selectFragment) {
        Intent intent = new Intent(GoodsDetailsActivity.this, MainActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, selectFragment);
        startActivity(intent);
    }

    private void startActionToReport(GoodsInfoBean goodsInfoBean) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(GoodsDetailsActivity.this);
            return;
        }
        if (goodsInfoBean == null || goodsInfoBean.getGoodsInfo() == null) {
            return;
        }
        Intent intent = new Intent(GoodsDetailsActivity.this, ReportActivity.class);
        intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, "1");
        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL, goodsInfoBean.getGoodsInfo());
        startActivity(intent);
    }


    @Override
    public void onChange(boolean isFav) {
        type = isFav ? 1 : 2;
    }

    public void onEvent(LCIMOfflineMessageCountChangeEvent updateEvent) {
        updateUnreadBadge();
    }

    public void onEvent(LCIMIMTypeMessageEvent event) {
        updateUnreadBadge();
    }

    public void onEventMainThread(RefreshEvent event){
        if (event.refresh){
            startRequestForCommodityDetail();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadBadge();
    }

    private void updateUnreadBadge() {
        titleBar.showMoreBtnBadge(IMUtils.hasUnread());
    }
}
