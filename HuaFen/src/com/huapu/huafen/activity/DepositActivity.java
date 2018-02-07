package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.BuildConfig;
import com.huapu.huafen.R;
import com.huapu.huafen.alipay.AliPayHelper;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.OrderDetailData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.weixin.WeChatPayHelper;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qwe on 2017/8/2.
 */

public class DepositActivity extends BaseActivity {

    @BindView(R.id.personName)
    TextView personName;
    @BindView(R.id.ivShare)
    TextView ivShare;
    @BindView(R.id.btnCheckedZFB)
    Button btnCheckedZFB;
    @BindView(R.id.tvTitle3)
    TextView tvTitle3;
    @BindView(R.id.aliPayLayout)
    LinearLayout aliPayLayout;
    @BindView(R.id.btnCheckedWeChat)
    Button btnCheckedWeChat;
    @BindView(R.id.tvTitle2)
    TextView tvTitle2;
    @BindView(R.id.llWeChat)
    LinearLayout llWeChat;
    @BindView(R.id.layoutPay)
    LinearLayout layoutPay;
    @BindView(R.id.confirmDeposit)
    TextView confirmDeposit;
    @BindView(R.id.addressLayout)
    LinearLayout addressLayout;
    @BindView(R.id.personAddress)
    TextView personAddress;
    @BindView(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;
    @BindView(R.id.lookUpDetail)
    TextView lookUpDetail;
    @BindView(R.id.moneyPay)
    TextView moneyPay;

    private String type = "2";
    private String payId = "";
    private GoodsInfo goodsInfo;
    private Consignee consignee;
    private String price;
    private String goodsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("goodsInfo")) {
            goodsInfo = (GoodsInfo) getIntent().getSerializableExtra("goodsInfo");
        }

        if (getIntent().hasExtra("price")) {
            price = getIntent().getStringExtra("price");
        }

        if (getIntent().hasExtra("goodsId")) {
            goodsId = getIntent().getStringExtra("goodsId");
        }
        initView();

    }

    private void initView() {
        getTitleBar().setTitle("交纳保证金");

        if (type.equals("2")) {
            btnCheckedZFB.setSelected(true);
        }
        if (null != goodsInfo) {
            moneyPay.setText("¥" + goodsInfo.bidDeposit);
        } else {
            if (!TextUtils.isEmpty(price)) {
                moneyPay.setText("¥" + price);
            }
        }

        startRequestForGetAddressList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        confirmDeposit.setEnabled(true);
    }

    @OnClick({R.id.personName, R.id.confirmDeposit, R.id.addressLayout, R.id.lookUpDetail, R.id.aliPayLayout, R.id.llWeChat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personName:
                break;
            case R.id.confirmDeposit:
                confirmDeposit.setEnabled(false);
                confirmDeposit();
                break;
            case R.id.addressLayout:
                Intent intent = new Intent(this, AddressListActivityNew.class);
                intent.putExtra(MyConstants.CHOOSE_ADDRESS_KEY, true);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ADDRESS);
                break;
            case R.id.lookUpDetail:
                Intent intentProtocol = new Intent(this, WebViewActivity.class);
                if (BuildConfig.DEBUG) {
                    intentProtocol.putExtra(MyConstants.EXTRA_WEBVIEW_URL, "http://192.168.0.100:8081/misc/auction-protocol/");
                } else {
                    intentProtocol.putExtra(MyConstants.EXTRA_WEBVIEW_URL, "https://i.huafer.cc/misc/auction-protocol/");
                }
                startActivity(intentProtocol);
                break;
            case R.id.aliPayLayout:
                btnCheckedZFB.setSelected(true);
                btnCheckedWeChat.setSelected(false);
                type = "2";
                break;
            case R.id.llWeChat:
                btnCheckedWeChat.setSelected(true);
                btnCheckedZFB.setSelected(false);
                type = "1";
                break;
        }
    }

    private void confirmDeposit() {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("type", String.valueOf(type));
        if (null != goodsInfo) {
            arrayMap.put("goodsId", String.valueOf(goodsInfo.getGoodsId()));
        } else {
            arrayMap.put("goodsId", goodsId);
        }

        OkHttpClientManager.postAsyn(MyConstants.PAY_DEPOSIT, arrayMap, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                Logger.e("get response:" + e.getMessage());
                confirmDeposit.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                Logger.e("get response:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    OrderDetailData data = JSON.parseObject(baseResult.obj, OrderDetailData.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (data != null) {
                            if ("1".equals(data.type)) {
                                payId = data.orderNo;
                                WeChatPayHelper.createPayment(DepositActivity.this, data.credential);
                            } else {
                                AliPayHelper alipayHelper = new AliPayHelper(DepositActivity.this);
                                alipayHelper.payV2(data.credential, new AliPayHelper.OnAliPayCompleteListener() {

                                    @Override
                                    public void onComplete(String out_trade_no) {
                                        confirmDeposit.setEnabled(true);
                                        payId = out_trade_no;
                                        payDepositSuccess();
                                    }

                                    @Override
                                    public void onFailed(String errorCode) {
                                        confirmDeposit.setEnabled(true);
                                    }
                                });
                            }
                        }
                    } else {
                        ToastUtil.toast(DepositActivity.this, baseResult.msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private void payDepositSuccess() {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("payId", payId);
        OkHttpClientManager.postAsyn(MyConstants.PAY_DEPOSIT_SUCCESS, arrayMap, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                Logger.e("get error:" + e.getMessage());
                ToastUtil.toast(DepositActivity.this, "支付失败！");
            }

            @Override
            public void onResponse(String response) {
                Logger.e("get response:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }

                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        ToastUtil.toast(DepositActivity.this, baseResult.msg);
                        setResult(RESULT_OK);
                        DepositActivity.this.finish();
                    } else {
                        ToastUtil.toast(DepositActivity.this, baseResult.msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void startRequestForGetAddressList() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        OkHttpClientManager.postAsyn(MyConstants.GETUSERCONSIGNEEINFO, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            List<Consignee> data = ParserUtils.parserConsigneesListData(baseResult.obj);
                            getDefaultConsignee(data);
                            personName.setText(consignee.getConsigneeName());
                            tvPhoneNumber.setText(consignee.getConsigneePhone());
                            Area area = consignee.getArea();
                            String province;
                            String city;
                            String areaName;
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
                            personAddress.setText(province + city + areaName + consignee.getConsigneeAddress());

                        }
                    } else {
                        CommonUtils.error(baseResult, DepositActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getDefaultConsignee(List<Consignee> consignees) {
        for (Consignee consignee : consignees) {
            if (consignee.getIsDefault()) {
                this.consignee = consignee;
                break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ADDRESS) {
                if (data != null) {
                    startRequestForGetAddressList();
                }
            } else if (requestCode == WeChatPayHelper.REQUEST_CODE_PAYMENT) {
                String result = data.getExtras().getString("pay_result");
                if (!TextUtils.isEmpty(result) && result.equals("success")) {
                    payDepositSuccess();
                }
            }
        }
    }

}
