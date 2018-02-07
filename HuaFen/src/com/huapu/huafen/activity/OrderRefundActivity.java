package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.beans.Express;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.Preferences;
import com.huapu.huafen.beans.RefundLogData;
import com.huapu.huafen.beans.Trace;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.events.OrderDetailRequestEvent;
import com.huapu.huafen.events.RefundFinishEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.DashLineView;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 退款信息
 *
 * @author liang_xs
 */
public class OrderRefundActivity extends BaseActivity implements OnClickListener {
    private TextView tvBtnLeft, tvBtnMiddle, tvBtnRight;
    private View layoutLeft;
//    private View viewLine;
    private long orderId;
    /**
     * 当前退款状态
     * 1 申请退款
     * 2 申请退货退款
     * 3 买家取消退款
     * 4 卖家拒绝退款
     * 5拒绝退货退款
     * 6 同意退款
     * 7同意退货退款
     * 8 买家退货
     * 9 收货后退款
     * 10 系统同意退款
     * 11 系统取消退款
     * 12 系统收货并退款
     */
    private int refundStatus;
    private ListView lvOrderRefund;
    private MyListAdapter adapter;
    /**
     * 0买 1卖
     */
    private int orderType;
    private final int TYPE_BUYER = 0;
    private final int TYPE_SELLER = 1;
    //头布局文件
    private View viewHeaderLayout;
    private View layoutRefundBottom;
    private Consignee c = new Consignee();
    private TextDialog dialog;
    private SimpleDraweeView ivGoodsPic;
    private DashLineView dlvGoodsName;
    private TextView tvPrice;
    private ImageView ivRight;
    private RefundLogData refundLogData;
    private String from;
    private Express express;
    private String orderRefundType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_refund);
        EventBus.getDefault().register(this);
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL_ID)) {
            orderId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_REFUND_FROM)) {
            from = getIntent().getStringExtra(MyConstants.EXTRA_ORDER_REFUND_FROM);
        }

        if (getIntent().hasExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE)) {
            orderRefundType = getIntent().getStringExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE);
        }


        initView();
        if (MyConstants.ORDER_DETAILS.equals(from)) {
            ivRight.setVisibility(View.GONE);
        }
        if ("1".equals(orderRefundType)) {
            startRequestForGetRefundLogList(orderId);
        } else if ("2".equals(orderRefundType)) {
            startRequestForGetExpressInfo();
        }


    }

    private void initView() {
        getTitleBar().
                setTitle("退款信息").
                setRightText("退款帮助", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderRefundActivity.this, WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.TUIKUAN_HELP);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "退款帮助");
                        startActivity(intent);
                    }
                });
        tvBtnLeft = (TextView) findViewById(R.id.tvBtnLeft);
        tvBtnMiddle = (TextView) findViewById(R.id.tvBtnMiddle);
        tvBtnRight = (TextView) findViewById(R.id.tvBtnRight);
//        viewLine = findViewById(R.id.viewLine);
//        layoutLeft = findViewById(R.id.layoutLeft);
        lvOrderRefund = (ListView) findViewById(R.id.lvOrderRefund);
        layoutRefundBottom = findViewById(R.id.layoutRefundBottom);
        viewHeaderLayout = View.inflate(this, R.layout.view_headview_order_refund_new, null);
        ivGoodsPic = (SimpleDraweeView) viewHeaderLayout.findViewById(R.id.ivGoodsPic);
        dlvGoodsName = (DashLineView) viewHeaderLayout.findViewById(R.id.dlvGoodsName);
        tvPrice = (TextView) viewHeaderLayout.findViewById(R.id.tvPrice);
        ivRight = (ImageView) viewHeaderLayout.findViewById(R.id.ivRight);
        tvBtnLeft.setOnClickListener(this);
        tvBtnMiddle.setOnClickListener(this);
        tvBtnRight.setOnClickListener(this);
        adapter = new MyListAdapter(new RefundLogData());

        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, lvOrderRefund, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_refund);
        lvOrderRefund.setEmptyView(viewEmpty);
        lvOrderRefund.setAdapter(adapter);
        viewHeaderLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("order_details".equals(from)) {
                    finish();
                } else {
                    Intent intent = new Intent(OrderRefundActivity.this, OrderDetailActivity.class);
                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, refundLogData.getOrderData().getOrderId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (adapter != null && adapter.handler != null && adapter.runnable != null) {
            adapter.handler.removeCallbacks(adapter.runnable);
        }
    }


    public void onEventMainThread(RefundFinishEvent event) {
        if (event != null && event.isFinish) {
            finish();
        }
    }

    private void setBtnClick(TextView view) {
        view.setOnClickListener(this);
        view.setTextColor(getResources().getColor(R.color.white));
        view.setBackgroundColor(getResources().getColor(R.color.base_pink));
    }

    private void setBtnNormal(TextView view) {
        view.setOnClickListener(null);
        view.setTextColor(getResources().getColor(R.color.text_color));
        view.setBackgroundColor(getResources().getColor(R.color.base_btn_normal));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.tvBtnLeft:
                if (refundLogData == null || refundLogData.getRefund() == null || refundLogData.getOrderData() == null) {
                    return;
                }
                // 申请仲裁
                applyArbitration();
                break;

            case R.id.tvBtnMiddle:
                if (refundLogData == null || refundLogData.getRefund() == null || refundLogData.getOrderData() == null) {
                    return;
                }
                switch (orderType) {
                    case 0: // 买家
                        switch (refundStatus) {
                            case 1: // 申请仲裁
                                applyArbitration();
                                break;
                            case 2: // 申请仲裁
                                applyArbitration();
                                break;
                            case 7:
                                dialog = new TextDialog(this, false);
                                dialog.setContentText("您确定取消申请吗？");
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
                                        startRequestForOrderCancel(refundLogData.getRefund().getRid());
                                    }
                                });
                                dialog.show();
                                break;
                            case 8:
                                break;
                        }
                        break;


                    case 1:
                        switch (refundStatus) {
                            case 1: // 拒绝退款
                                intent = new Intent(this, OrderRefundEditActivity.class);
                                intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_ID, refundLogData.getRefund().getRid());
                                intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_TYPE, 1);
                                intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_PRICE, refundLogData.getOrderData().getPrice());
                                intent.putExtra(MyConstants.EXTRA_ORDER_TARGET, 1);
                                startActivity(intent);
                                break;
                            case 2:// 拒绝退货退款
                                intent = new Intent(this, OrderRefundEditActivity.class);
                                intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_ID, refundLogData.getRefund().getRid());
                                intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_TYPE, 2);
                                intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_PRICE, refundLogData.getOrderData().getPrice());
                                intent.putExtra(MyConstants.EXTRA_ORDER_TARGET, 1);
                                startActivity(intent);
                                break;
                            case 7:
                                break;
                            case 8: // 申请仲裁
                                applyArbitration();
                                break;
                        }
                        break;
                }

                break;

            case R.id.tvBtnRight:
                if (refundLogData == null || refundLogData.getRefund() == null || refundLogData.getOrderData() == null) {
                    return;
                }
                switch (orderType) {
                    case 0:
                        switch (refundStatus) {
                            case 1:
                                dialog = new TextDialog(this, false);
                                dialog.setContentText("您确定取消申请吗？");
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
                                        startRequestForOrderCancel(refundLogData.getRefund().getRid());
                                    }
                                });
                                dialog.show();
                                break;
                            case 2:
                                dialog = new TextDialog(this, false);
                                dialog.setContentText("您确定取消申请吗？");
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
                                        startRequestForOrderCancel(refundLogData.getRefund().getRid());
                                    }
                                });
                                dialog.show();
                                break;
                            case 7:
                                intent = new Intent(this, OrderExpressEditActivity.class);
                                intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_ID, refundLogData.getRefund().getRid());
                                startActivity(intent);
                                break;
                            case 8:
                                break;
                        }
                        break;


                    case 1:
                        switch (refundStatus) {
                            case 1:
                                dialog = new TextDialog(this, false);
                                dialog.setContentText("您确定同意退款吗？");
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
                                        startRequestForAgreeRefund(refundLogData.getRefund().getRid());
                                    }
                                });
                                dialog.show();
                                break;
                            case 2: // 同意退款并发送收货地址
                                intent = new Intent(this, AddressListActivityNew.class);
                                intent.putExtra(MyConstants.CHOOSE_ADDRESS_KEY, true);
                                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ADDRESS);
                                break;
                            case 7:
                                break;
                            case 8:
                                dialog = new TextDialog(this, false);
                                dialog.setContentText("您确定收货吗？");
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
                                        startRequestForReceiptGoodsAndRefund(refundLogData.getRefund().getRid());
                                    }
                                });
                                dialog.show();
                                break;
                        }
                        break;
                }
                break;
        }
    }

    /**
     * 申请仲裁
     */
    private void applyArbitration() {
        if (refundLogData.getOrderData().getReportLockTime() == 0) {
            Intent intent = new Intent(this, ArbitrationActivity.class);
            intent.putExtra(MyConstants.EXTRA_REFUND_LOG_DATA_DETAIL, refundLogData);
            intent.putExtra(MyConstants.EXTRA_ORDER_ARBITRATION, 1);
            startActivity(intent);
        } else {
            toast("未到可申请时间，再沟通一下吧");
        }
    }


    /**
     * 卖家确认收货
     *
     * @param
     */
    private void startRequestForReceiptGoodsAndRefund(long refundId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(OrderRefundActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("refundId", String.valueOf(refundId));
        LogUtil.i("liang", "卖家确认收货params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.RECEIPTGOODSANDREFUND, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "卖家确认收货:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
                            OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
                            oEvent.isUpdate = true;
                            EventBus.getDefault().post(oEvent);
                            finish();
                        }
                    } else {
                        CommonUtils.error(baseResult, OrderRefundActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 取消退款申请
     *
     * @param
     */
    private void startRequestForOrderCancel(long refundId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(OrderRefundActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("refundId", String.valueOf(refundId));
        LogUtil.i("liang", "取消退款申请params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.CANCLEREFUND, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "取消退款申请:" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
                            OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
                            oEvent.isUpdate = true;
                            EventBus.getDefault().post(oEvent);
                            finish();
                        }
                    } else {
                        CommonUtils.error(baseResult, OrderRefundActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void startRequestForGetExpressInfo() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(OrderRefundActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", String.valueOf(orderId));
        params.put("orderType", orderRefundType);

        LogUtil.i("liang", "物流信息params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETEXPRESSINFO, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
                startRequestForGetRefundLogList(orderId);
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "物流信息:" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            express = ParserUtils.parserOrderExpressData(baseResult.obj);
                            startRequestForGetRefundLogList(orderId);
                        } else {
                            startRequestForGetRefundLogList(orderId);
                        }
                    } else {
                        startRequestForGetRefundLogList(orderId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    startRequestForGetRefundLogList(orderId);
                }
            }
        });
    }

    /**
     * 获得退款历史
     *
     * @param
     */
    public void startRequestForGetRefundLogList(long orderId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", String.valueOf(orderId));
        LogUtil.i("liang", "退款历史params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETREFUNDLOGLIST, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        LogUtil.i("liang", "退款历史:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
                                    refundLogData = JSON.parseObject(baseResult.obj, RefundLogData.class);
                                    if (refundLogData != null && refundLogData.getRefund() != null) {
                                        lvOrderRefund.addHeaderView(viewHeaderLayout);
                                        refundStatus = refundLogData.getRefund().getRefundStatus();
                                        long myUserId = CommonPreference.getUserId();
                                        long userId = refundLogData.getRefund().getUserId();
                                        if (userId == myUserId) {
                                            orderType = TYPE_BUYER;
                                        } else {
                                            orderType = TYPE_SELLER;
                                        }
                                        initData(refundLogData);
                                        adapter.setData(refundLogData);
                                        adapter.setExpress(express);
                                    }
                                }
                            } else if (baseResult.code == ParserUtils.RESPONSE_ORDER_REFUND_STATUS_CHANGE) {
                                final TextDialog dialog = new TextDialog(OrderRefundActivity.this, false);
                                dialog.setContentText(baseResult.msg);
                                dialog.setLeftText("确定");
                                dialog.setLeftCall(new DialogCallback() {

                                    @Override
                                    public void Click() {
                                        onBackPressed();
                                    }
                                });
                                dialog.show();
                            } else {
                                CommonUtils.error(baseResult, OrderRefundActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 同意退款
     *
     * @param
     */
    private void startRequestForAgreeRefund(long refundId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("refundId", String.valueOf(refundId));
        if (c != null && c.getConsigneesId() != null) {
            params.put("consigneesId", String.valueOf(c.getConsigneesId()));
        }
        LogUtil.i("liang", "同意退款params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.AGREEREFUND, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        LogUtil.i("liang", "同意退款:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
//									JSONObject object = JSON.parseObject(baseResult.obj);
//						        	int next = object.getIntValue("nextpage");
//						        	refundStatus = object.getIntValue("refundStatus");
//									list = ParserUtils.parserRefundLogInfoListData(baseResult.obj);
//									if(list != null) {
//										getData();
//									}
                                    OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
                                    OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
                                    oEvent.isUpdate = true;
                                    EventBus.getDefault().post(oEvent);
                                    finish();
                                }
                            } else {
                                CommonUtils.error(baseResult, OrderRefundActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    private void initData(RefundLogData refundLogData) {
        if (refundLogData == null || refundLogData.getGoodsData() == null ||
                refundLogData.getOrderData() == null ||
                ArrayUtil.isEmpty(refundLogData.getGoodsData().getGoodsImgs()) ||
                refundLogData.getRefund() == null) {
            return;
        }
        tvPrice.setText(String.valueOf(refundLogData.getOrderData().getPrice()));
        dlvGoodsName.setData(refundLogData.getGoodsData().getBrand(), refundLogData.getGoodsData().getName());

        ImageLoader.resizeMiddle(ivGoodsPic, refundLogData.getGoodsData().getGoodsImgs().get(0), 1);

        switch (orderType) {
            case 0: // 买家
                switch (refundStatus) {
                    case 1:
                        layoutRefundBottom.setVisibility(View.VISIBLE);
                        tvBtnLeft.setVisibility(View.GONE);
                        tvBtnMiddle.setText("申请仲裁");
                        tvBtnRight.setText("取消申请");
                        break;
                    case 2:
                        layoutRefundBottom.setVisibility(View.VISIBLE);
                        tvBtnLeft.setVisibility(View.GONE);
                        tvBtnMiddle.setText("申请仲裁");
                        tvBtnRight.setText("取消申请");
                        break;
                    case 6:
                        break;
                    case 7:
                        layoutRefundBottom.setVisibility(View.VISIBLE);
                        tvBtnLeft.setVisibility(View.GONE);
                        tvBtnMiddle.setText("取消申请");
                        tvBtnRight.setText("退货");
                        break;
                    case 8:
                        layoutRefundBottom.setVisibility(View.VISIBLE);
//                        layoutLeft.setVisibility(View.GONE);
                        tvBtnRight.setText("等待卖家收货");
                        setBtnNormal(tvBtnRight);
                        break;
                    case 9:
                        layoutRefundBottom.setVisibility(View.GONE);
                        break;
                }
                break;

            case 1: // 卖家
                switch (refundStatus) {
                    case 1:
                        layoutRefundBottom.setVisibility(View.VISIBLE);
                        if (refundLogData.getOrderData().getIsArbitration()) {
                            tvBtnLeft.setText("申请仲裁");
                        } else {
                            tvBtnLeft.setVisibility(View.GONE);
                        }
                        tvBtnMiddle.setText("拒绝退款");
                        tvBtnRight.setText("同意退款");
                        break;
                    case 2:
                        layoutRefundBottom.setVisibility(View.VISIBLE);
                        if (refundLogData.getOrderData().getIsArbitration()) {
                            tvBtnLeft.setText("申请仲裁");
                        } else {
                            tvBtnLeft.setVisibility(View.GONE);
                        }
                        tvBtnMiddle.setText("拒绝退货退款");
                        tvBtnRight.setText("同意退货退款");
                        break;
                    case 7:
                        layoutRefundBottom.setVisibility(View.VISIBLE);
//                        layoutLeft.setVisibility(View.GONE);
                        tvBtnRight.setText("等待买家退货");
                        setBtnNormal(tvBtnRight);
                        break;
                    case 8:
                        layoutRefundBottom.setVisibility(View.VISIBLE);
                        tvBtnLeft.setVisibility(View.GONE);
                        tvBtnMiddle.setText("申请仲裁");
                        tvBtnRight.setText("确认收货");
                        break;
                    case 9:
                        layoutRefundBottom.setVisibility(View.GONE);
                        break;
                }
                break;
        }
    }


    class MyListAdapter extends BaseAdapter {

        private RefundLogData data;
        private String residueTime = "";
        public Handler handler = new Handler();
        public Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                if(list.size())
                long residue = data.getRefundLogList().get(0).getTime() - 1000;
                data.getRefundLogList().get(0).setTime(residue);
                if (residue > 0) {
                    residueTime = DateTimeUtils.getResidueTime(residue);
                    LogUtil.i("liang", residueTime);
                    handler.postDelayed(this, 1000);
                    notifyDataSetChanged();
                } else {
                    handler.removeCallbacks(runnable);
                    notifyDataSetChanged();
                }

            }
        };
        private Express express;

        public MyListAdapter(RefundLogData data) {
            this.data = data;
        }

        public void setData(RefundLogData data) {
            this.data = data;
            if (ArrayUtil.isEmpty(data.getRefundLogList())) {
                return;
            }
            if (data.getRefund().getResidualTime() != 0) {
                data.getRefundLogList().get(0).setTime(data.getRefund().getResidualTime());
                residueTime = DateTimeUtils.getResidueTime(data.getRefundLogList().get(0).getTime());
                handler.postDelayed(runnable, 1000);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (data == null || ArrayUtil.isEmpty(data.getRefundLogList())) {
                return 0;
            } else {
                return data.getRefundLogList().size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (data == null || ArrayUtil.isEmpty(data.getRefundLogList())) {
                return null;
            } else {
                return data.getRefundLogList().size();
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (data == null || ArrayUtil.isEmpty(data.getRefundLogList())) {
                return null;
            }
            ViewHolder viewHolder;
            if (position < 0 || position >= getCount()) {
                return convertView;
            }
            if (convertView == null) {
                convertView = LayoutInflater.from(OrderRefundActivity.this).inflate(
                        R.layout.item_listview_order_refund, null);
                viewHolder = new ViewHolder();
                viewHolder.tvOrderRefundTitle = (TextView) convertView.findViewById(R.id.tvOrderRefundTitle);
                viewHolder.tvOrderRefundDate = (TextView) convertView.findViewById(R.id.tvOrderRefundDate);
                viewHolder.tvOrderRefundFirst = (TextView) convertView.findViewById(R.id.tvOrderRefundFirst);
                viewHolder.tvOrderRefundSecond = (TextView) convertView.findViewById(R.id.tvOrderRefundSecond);
                viewHolder.tvOrderRefundThird = (TextView) convertView.findViewById(R.id.tvOrderRefundThird);
                viewHolder.layoutRefundItem = convertView.findViewById(R.id.layoutRefundItem);
                viewHolder.layoutResidue = convertView.findViewById(R.id.layoutResidue);
                viewHolder.tvResidueTime = (TextView) convertView.findViewById(R.id.tvResidueTime);
                viewHolder.llExpress = (LinearLayout) convertView.findViewById(R.id.llExpress);
                viewHolder.tvExpress = (TextView) convertView.findViewById(R.id.tvExpress);
                viewHolder.tvExpressTime = (TextView) convertView.findViewById(R.id.tvExpressTime);
                viewHolder.tvResidueSummary = (TextView) convertView.findViewById(R.id.tvResidueSummary);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            int operationType = data.getRefundLogList().get(position).getRefundLog().getOperationType();
            switch (operationType) {
                case 1:
                    viewHolder.tvOrderRefundTitle.setText("买家申请仅退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("退款金额：" + data.getRefundLogList().get(position).getRefundLog().getRefundMoney() + "元");
                    viewHolder.tvOrderRefundSecond.setText("退款原因：" + data.getRefundLogList().get(position).getRefundLog().getRefundLabel());
                    viewHolder.tvOrderRefundThird.setText("退款描述：" + data.getRefundLogList().get(position).getRefundLog().getRefundContent());
                    break;

                case 2:
                    viewHolder.tvOrderRefundTitle.setText("买家申请退货退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("退款金额：" + data.getRefundLogList().get(position).getRefundLog().getRefundMoney() + "元");
                    viewHolder.tvOrderRefundSecond.setText("退款原因：" + data.getRefundLogList().get(position).getRefundLog().getRefundLabel());
                    viewHolder.tvOrderRefundThird.setText("退款描述：" + data.getRefundLogList().get(position).getRefundLog().getRefundContent());
                    break;

                case 3:
                    viewHolder.tvOrderRefundTitle.setText("买家取消退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("退款金额：" + data.getRefundLogList().get(position).getRefundLog().getRefundMoney() + "元");
                    viewHolder.tvOrderRefundSecond.setText("退款原因：" + data.getRefundLogList().get(position).getRefundLog().getRefundLabel());
                    viewHolder.tvOrderRefundThird.setText("退款描述：" + data.getRefundLogList().get(position).getRefundLog().getRefundContent());
                    break;

                case 4:
                    viewHolder.tvOrderRefundTitle.setText("卖家拒绝退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("卖家拒绝了退款");
                    viewHolder.tvOrderRefundSecond.setText("拒绝原因：" + data.getRefundLogList().get(position).getRefundLog().getRefundLabel());
                    viewHolder.tvOrderRefundThird.setText("拒绝描述：" + data.getRefundLogList().get(position).getRefundLog().getRefundContent());
                    break;

                case 5:
                    viewHolder.tvOrderRefundTitle.setText("卖家拒绝退货退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("卖家拒绝了退货退款");
                    viewHolder.tvOrderRefundSecond.setText("拒绝原因：" + data.getRefundLogList().get(position).getRefundLog().getRefundLabel());
                    viewHolder.tvOrderRefundThird.setText("拒绝描述：" + data.getRefundLogList().get(position).getRefundLog().getRefundContent());
                    break;

                case 6:
                    viewHolder.tvOrderRefundTitle.setText("卖家同意退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
//                    viewHolder.tvOrderRefundFirst.setText("退款金额：" + data.getRefundLogList().get(position).getRefundLog().getRefundMoney() + "元");
//                    viewHolder.tvOrderRefundSecond.setText("退款原因：" + data.getRefundLogList().get(position).getRefundLog().getRefundLabel());
//                    viewHolder.tvOrderRefundThird.setText("退款描述：" + data.getRefundLogList().get(position).getRefundLog().getRefundContent());
                    viewHolder.layoutRefundItem.setVisibility(View.GONE);
                    break;

                case 7:
                    viewHolder.tvOrderRefundTitle.setText("卖家同意退货退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("请买家将物品寄到以下地址：");
                    viewHolder.tvOrderRefundSecond.setText("收货人：" + data.getRefundLogList().get(position).getAddress().getConsigneeName() + " " + data.getRefundLogList().get(position).getAddress().getConsigneePhone());
                    Area area = data.getRefundLogList().get(position).getAddress().getArea();
                    String province = "";
                    String city = "";
                    String areaName = "";
                    if (area != null) {
                        province = area.getProvince();
                        city = area.getCity();
                        areaName = area.getArea();
                    } else {
                        province = "";
                        city = "";
                        areaName = "";
                    }
                    if (province.equals(city)) {
                        province = "";
                    }
                    viewHolder.tvOrderRefundThird.setText("收货地址：" + province + city + areaName + data.getRefundLogList().get(position).getAddress().getConsigneeAddress());
                    break;

                case 8:
                    viewHolder.tvOrderRefundTitle.setText("买家退货");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("买家退货物流信息：");
                    viewHolder.tvOrderRefundSecond.setText("物流公司：" + data.getRefundLogList().get(position).getExpressInfo().getExpressName());
                    viewHolder.tvOrderRefundThird.setText("快递单号：" + data.getRefundLogList().get(position).getExpressInfo().getExpressNum());
                    break;
                case 9:
                    viewHolder.tvOrderRefundTitle.setText("收货后退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.layoutRefundItem.setVisibility(View.GONE);
                    break;
                case 10:
                    viewHolder.tvOrderRefundTitle.setText("系统同意退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("退款金额：" + data.getRefundLogList().get(position).getRefundLog().getRefundMoney() + "元");
                    viewHolder.tvOrderRefundSecond.setText("退款原因：" + data.getRefundLogList().get(position).getRefundLog().getRefundLabel());
                    viewHolder.tvOrderRefundThird.setText("退款描述：" + data.getRefundLogList().get(position).getRefundLog().getRefundContent());
                    break;
                case 11:
                    viewHolder.tvOrderRefundTitle.setText("系统取消退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("退款金额：" + data.getRefundLogList().get(position).getRefundLog().getRefundMoney() + "元");
                    viewHolder.tvOrderRefundSecond.setText("退款原因：" + data.getRefundLogList().get(position).getRefundLog().getRefundLabel());
                    viewHolder.tvOrderRefundThird.setText("退款描述：" + data.getRefundLogList().get(position).getRefundLog().getRefundContent());
                    break;
                case 12:
                    viewHolder.tvOrderRefundTitle.setText("系统收货并退款");
                    viewHolder.tvOrderRefundDate.setText(DateTimeUtils.getYearMonthDayHourMinute(data.getRefundLogList().get(position).getRefundLog().getOperatorTime()));
                    viewHolder.tvOrderRefundFirst.setText("退款金额：" + data.getRefundLogList().get(position).getRefundLog().getRefundMoney() + "元");
                    viewHolder.tvOrderRefundSecond.setText("退款原因：" + data.getRefundLogList().get(position).getRefundLog().getRefundLabel());
                    viewHolder.tvOrderRefundThird.setText("退款描述：" + data.getRefundLogList().get(position).getRefundLog().getRefundContent());
                    break;
            }
            if (position == 0) {
                if (data.getRefund() != null) {
                    if (data.getRefund().getResidualTime() != 0) {
                        viewHolder.layoutResidue.setVisibility(View.VISIBLE);
                        viewHolder.tvResidueTime.setText(residueTime);
                        if (data.getOrderData().getBuyerId() != CommonPreference.getUserId()){
                            viewHolder.tvResidueSummary.setBackgroundColor(getResources().getColor(R.color.white));
                            int itemMargin = (int) getResources().getDimension(R.dimen.item_margin);
                            viewHolder.tvResidueSummary.setPadding(0,itemMargin,itemMargin,itemMargin);
                            if (operationType == 1){
                                viewHolder.tvResidueSummary.setText(R.string.text_tip_refund_only_money);
                            }else if (operationType == 2){
                                viewHolder.tvResidueSummary.setText(R.string.text_tip_refund_money_and_goods);
                            }
                        } else {
                            viewHolder.tvResidueSummary.setBackgroundResource(R.drawable.grey_round_bg);
                            int itemMargin = (int) getResources().getDimension(R.dimen.item_margin);
                            viewHolder.tvResidueSummary.setPadding(itemMargin,itemMargin,itemMargin,itemMargin);
                            viewHolder.tvResidueSummary.setText(data.getRefund().getSummary());
                        }

                        if (express != null) {
                            List<Trace> trances = express.getTraces();
                            if (!ArrayUtil.isEmpty(trances)) {
                                viewHolder.llExpress.setVisibility(View.VISIBLE);
                                Trace trance = trances.get(0);
                                if (trance != null) {
                                    String acceptStation = trance.getAcceptStation();
                                    if (!TextUtils.isEmpty(acceptStation)) {
                                        viewHolder.tvExpress.setText(acceptStation);
                                    }
                                    String acceptTime = trance.getAcceptTime();
                                    if (!TextUtils.isEmpty(acceptTime)) {
                                        viewHolder.tvExpressTime.setText(acceptTime);
                                    }
                                }
                                viewHolder.llExpress.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(OrderRefundActivity.this, OrderExpressListActivity.class);
                                        intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, orderRefundType);
                                        intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderId);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                viewHolder.llExpress.setVisibility(View.GONE);
                            }
                        }
                        LogUtil.i("liang", "position == 0");
                        LogUtil.i("liang", "position == 0" + residueTime);
                    } else {
                        viewHolder.layoutResidue.setVisibility(View.GONE);
                    }
                }
            } else {
                viewHolder.layoutResidue.setVisibility(View.GONE);
            }
            return convertView;
        }

        public void setExpress(Express express) {
            this.express = express;
        }

        class ViewHolder {
            private TextView tvOrderRefundTitle;
            private TextView tvOrderRefundDate;
            private TextView tvOrderRefundFirst;
            private TextView tvOrderRefundSecond;
            private TextView tvOrderRefundThird;
            private View layoutRefundItem;
            private View layoutResidue;
            private TextView tvResidueTime;
            private LinearLayout llExpress;
            private TextView tvExpress;
            private TextView tvExpressTime;
            private TextView tvResidueSummary;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ADDRESS) {
                if (data != null) {
                    Consignee cc = (Consignee) data.getSerializableExtra(MyConstants.EXTRA_ADDRESS);
                    if (cc != null) {
                        c = cc;
                        if (refundLogData == null || refundLogData.getRefund() == null) {
                            return;
                        }
                        startRequestForAgreeRefund(refundLogData.getRefund().getRid());
                    }
                }

            }
        }
    }
}
