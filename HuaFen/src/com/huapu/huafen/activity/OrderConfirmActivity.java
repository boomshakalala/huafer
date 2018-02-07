package com.huapu.huafen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.beans.OrderConfirmBean;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 订单确认
 *
 * @author liang_xs
 */
public class OrderConfirmActivity extends BaseActivity {
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    private TextView tvBtnThreeRight;
    private TextView tvConsignName, tvConsignPhone, tvConsignAddress, tvBrand, tvTitle, tvPrice, tvPostAge, tvTotalPrice;
    private LinearLayout llZFB, llWeChat;
    private Button btnCheckedWeChat, btnCheckedZFB;
    private SimpleDraweeView ivGoodsImg;
    private View layoutAddress;
    private OrderConfirmBean bean;
    private Consignee c = new Consignee();
    private String type = "2";
    private View layoutMemo;
    private TextView tvMemo, tvMemoHint;
    private View viewLineMemo;
    private String orderMemo;
    private TextView payTotalPrice;
    private String recTranceId;
    private int recIndex;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm_new);
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_CONFIRM_BEAN)) {
            bean = (OrderConfirmBean) getIntent().getSerializableExtra(MyConstants.EXTRA_ORDER_CONFIRM_BEAN);
        }
        recTranceId = mIntent.getStringExtra(MyConstants.REC_TRAC_ID);
        recIndex = mIntent.getIntExtra(MyConstants.POSITION, -1);
        searchQuery = mIntent.getStringExtra(MyConstants.SEARCH_QUERY);
        initView();
        if (bean != null) {
            getDefaultConsignee(bean.getConsignees());
            initData();
        }

    }

    private void getDefaultConsignee(ArrayList<Consignee> consignees) {
        for (Consignee consignee : consignees) {
            if (consignee.getIsDefault()) {
                c = consignee;
                break;
            }
        }
    }

    private void initData() {
        btnCheckedZFB.setSelected(true);
        tvConsignName.setText(c.getConsigneeName() + " " + c.getConsigneePhone());
        Area area = c.getArea();
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
        tvConsignAddress.setText(province + city + areaName + c.getConsigneeAddress());

        String url = bean.getGoodsInfo().getGoodsImgs().get(0);
        ImageLoader.resizeMiddle(ivGoodsImg, url, 1);

        tvBrand.setText(bean.getGoodsInfo().getGoodsBrand());
        tvTitle.setText(bean.getGoodsInfo().getGoodsName());
        CommonUtils.setPriceSizeData(tvPrice, "", bean.getGoodsInfo().getPrice());
        // --

        boolean isFreeDelivery = bean.getGoodsInfo().getIsFreeDelivery();
        if (isFreeDelivery) {
            tvPostAge.setText("包邮");
        } else {
            if (bean.getGoodsInfo().getPostType() == 4) {//邮费到付
                tvPostAge.setText("邮费到付");
            } else {
                int postage = bean.getGoodsInfo().getPostage();
                if (postage > 0) {
                    CommonUtils.setPriceSizeData(tvPostAge, "", bean.getGoodsInfo().getPostage());
                } else {
                    tvPostAge.setText("邮费待议");
                }
            }

        }

        payTotalPrice.setText("¥" + (bean.getGoodsInfo().getPrice() + bean.getGoodsInfo().getPostage()));

        int totalPrice = bean.getGoodsInfo().getPrice() + bean.getGoodsInfo().getPostage();

        tvTotalPrice.setText(Html.fromHtml("支付总价：" + "<font color='#FF6677'>" + "¥" + totalPrice + "</font>"));
        //TODO 以下两个字段暂时用
        boolean isBindWechat = bean.getUserInfo().getIsBindWechat();
        boolean isBindAli = bean.getUserInfo().getIsBindALi();
        if (isBindWechat) {
            llWeChat.setVisibility(View.VISIBLE);
        } else {
            llWeChat.setVisibility(View.GONE);
        }

        if (isBindAli) {
            llZFB.setVisibility(View.VISIBLE);
        } else {
            llZFB.setVisibility(View.GONE);
        }
        //添加卖家收款渠道默认选中初始化逻辑
        if (isBindWechat && isBindAli) {//卖家微信和支付宝都绑定
            btnCheckedZFB.setSelected(true);
            btnCheckedWeChat.setSelected(false);
            type = "2";
        } else if (isBindWechat && !isBindAli) {//卖家只绑定了微信
            btnCheckedZFB.setSelected(false);
            btnCheckedWeChat.setSelected(true);
            type = "1";
        } else if (!isBindWechat && isBindAli) {//卖家只绑定了支付宝
            btnCheckedZFB.setSelected(true);
            btnCheckedWeChat.setSelected(false);
            type = "2";
        } else {
            //TODO 卖家无收款渠道，理论上是不会出现这种问题
        }
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        setTitleString("确认订单");
        tvBtnThreeRight = (TextView) findViewById(R.id.tvBtnThreeRight);
        tvConsignName = (TextView) findViewById(R.id.tvConsignName);
        tvConsignAddress = (TextView) findViewById(R.id.tvConsignAddress);
        tvBrand = (TextView) findViewById(R.id.tvBrand);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        payTotalPrice = (TextView) findViewById(R.id.payTotalPrice);
        tvPostAge = (TextView) findViewById(R.id.tvPostAge);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        llWeChat = (LinearLayout) findViewById(R.id.llWeChat);
        llZFB = (LinearLayout) findViewById(R.id.llZFB);
        btnCheckedWeChat = (Button) findViewById(R.id.btnCheckedWeChat);
        btnCheckedZFB = (Button) findViewById(R.id.btnCheckedZFB);
        ivGoodsImg = (SimpleDraweeView) findViewById(R.id.ivGoodsImg);
        layoutAddress = findViewById(R.id.layoutAddress);
        layoutMemo = findViewById(R.id.layoutMemo);
        viewLineMemo = findViewById(R.id.viewLineMemo);
        tvMemoHint = (TextView) findViewById(R.id.tvMemoHint);
        tvMemo = (TextView) findViewById(R.id.tvMemo);
        tvBtnThreeRight.setOnClickListener(this);
        layoutAddress.setOnClickListener(this);
        layoutMemo.setOnClickListener(this);
        //TODO
        llZFB.setOnClickListener(this);
        llWeChat.setOnClickListener(this);

        final LinearLayout llRemarks = (LinearLayout) findViewById(R.id.llRemarks);
        ViewTreeObserver observer = llRemarks.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                int[] location = new int[2];
                llRemarks.getLocationOnScreen(location);
                int bottom = location[1];
                int dy = llRemarks.getHeight() / 2;
                Intent intent = new Intent();
                int height = bottom - dy;
                if (bottom >= CommonUtils.getScreenHeight()) {
                    intent.putExtra("overScreen", true);
                } else {
                    intent.putExtra("overScreen", false);
                }
                intent.putExtra("bottom", height);
                intent.putExtra("height", llRemarks.getHeight());
                CommonUtils.buildMontage(OrderConfirmActivity.this, intent, "first_remarks");
                llRemarks.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnTitleBarLeft:
                finish();
                break;
            case R.id.tvBtnThreeRight:
                if (c.getConsigneesId() == null) {
                    toast("请选择收货地址");
                    return;
                }
                startRequestForOrderCreate();
                break;
            case R.id.llWeChat:
                btnCheckedWeChat.setSelected(true);
                btnCheckedZFB.setSelected(false);
                type = "1";
                break;
            case R.id.llZFB:
                btnCheckedZFB.setSelected(true);
                btnCheckedWeChat.setSelected(false);
                type = "2";
                break;

            case R.id.layoutAddress:
                intent = new Intent(this, AddressListActivityNew.class);
                intent.putExtra(MyConstants.CHOOSE_ADDRESS_KEY, true);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ADDRESS);
                break;

            case R.id.layoutMemo:
                orderMemo = tvMemo.getText().toString().trim();
                intent = new Intent(this, OrderMemoEditActivity.class);
                intent.putExtra(MyConstants.EXTRA_ORDER_MEMO_EDIT, orderMemo);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ORDER_MEMO_EDIT);
                break;
        }
    }


    /**
     * 生成订单
     *
     * @param
     */
    private void startRequestForOrderCreate() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(OrderConfirmActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("goodsId", String.valueOf(bean.getGoodsInfo().getGoodsId()));
        params.put("goodsPrice", String.valueOf(bean.getGoodsInfo().getPrice()));
        // --
        params.put("goodsPostage", String.valueOf(bean.getGoodsInfo().getPostage()));
        // --

        // 邮费到付
        // params.put("shippingCost", String.valueOf(bean.getGoodsInfo().getShippingCost()));
        // 邮费到付
        params.put("consigneeId", String.valueOf(c.getConsigneesId()));
        params.put("payChannel", type);
        params.put("orderMemo", orderMemo);
        if (!TextUtils.isEmpty(recTranceId)) {
            params.put("recTraceId", recTranceId);
        }

        if (recIndex != -1) {
            params.put("recIndex", String.valueOf(recIndex));
        }

        if (!TextUtils.isEmpty(searchQuery)) {
            params.put("searchQuery", searchQuery);
        }

        OkHttpClientManager.postAsyn(MyConstants.ORDERCREATE, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        LogUtil.i("liang", "创建订单:" + response);
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
                                    if (bean != null) {
                                        Intent intent = new Intent(OrderConfirmActivity.this, OrderDetailActivity.class);
                                        intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, bean.getOrderInfo().getOrderId());
                                        intent.putExtra(MyConstants.TYPE, type);
                                        if (bean.getGoodsInfo().getCfId() == 20) {
                                            intent.putExtra("toast", true);
                                        }
                                        startActivity(intent);
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }
                            } else if (baseResult.code == ParserUtils.RESPONSE_GOODS_DETAILS_IS_SELL) {
                                tvBtnThreeRight.setText("已被抢走了");
                                tvBtnThreeRight.setOnClickListener(null);
                                tvBtnThreeRight.setTextColor(getResources().getColor(R.color.text_color));
                                tvBtnThreeRight.setBackgroundColor(getResources().getColor(R.color.base_btn_normal));
                            } else {
                                CommonUtils.error(baseResult, OrderConfirmActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ADDRESS) {
                if (data != null) {
                    Consignee consignee = (Consignee) data.getSerializableExtra(MyConstants.EXTRA_ADDRESS);
                    if (consignee != null) {
                        c = consignee;
                        tvConsignName.setText(c.getConsigneeName() + " " + c.getConsigneePhone());
                        Area area = c.getArea();
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
                        tvConsignAddress.setText(province + city + areaName + c.getConsigneeAddress());
                    }
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ORDER_MEMO_EDIT) {
                orderMemo = data.getStringExtra(MyConstants.EXTRA_ORDER_MEMO_EDIT);
                if (!TextUtils.isEmpty(orderMemo)) {
                    tvMemoHint.setText("修改");
                    tvMemo.setText(orderMemo);
                    tvMemo.setVisibility(View.VISIBLE);
                    viewLineMemo.setVisibility(View.VISIBLE);
                } else {
                    tvMemoHint.setText(getString(R.string.order_confirm_empty_tip));
                    tvMemo.setText("");
                    tvMemo.setVisibility(View.GONE);
                    viewLineMemo.setVisibility(View.GONE);
                }
            }
        }
    }
}