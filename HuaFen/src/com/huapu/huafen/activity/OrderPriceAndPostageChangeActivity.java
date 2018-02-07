package com.huapu.huafen.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.OrderData;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.OrderInfo;
import com.huapu.huafen.beans.Orders;
import com.huapu.huafen.callbacks.SimpleTextWatcher;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;
import java.util.HashMap;
import butterknife.BindView;

/**
 * Created by mac on 17/8/1.
 */

public class OrderPriceAndPostageChangeActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private final static String TAG = OrderPriceAndPostageChangeActivity.class.getSimpleName();
    @BindView(R.id.blankSpace) View blankSpace;
    @BindView(R.id.etPrice) EditText etPrice;
    @BindView(R.id.tvBtnConfirm1) TextView tvBtnConfirm1;
    @BindView(R.id.llPrice) LinearLayout llPrice;
    @BindView(R.id.chbFree) CheckBox chbFree;
    @BindView(R.id.chbPostpaid) CheckBox chbPostpaid;
    @BindView(R.id.etPostage) EditText etPostage;
    @BindView(R.id.tvBtnConfirm) TextView tvBtnConfirm;
    @BindView(R.id.llPostage) LinearLayout llPostage;
    private int isFree;
    private int postagePaid;
    private int postage;
    private int price;
    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_price_pop_bottom);
        OrderDetailBean bean = (OrderDetailBean) mIntent.getSerializableExtra(MyConstants.EXTRA_ORDER_DETAIL_BEAN);
        Orders orders = (Orders) mIntent.getSerializableExtra(MyConstants.ORDERS);
        if(bean==null && orders == null){
            final TextDialog dialog = new TextDialog(this,false);
            dialog.setContentText("数据异常");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
            return;
        }
        initView();
        if(bean!=null){
            initData(bean);
        }else if(orders!=null){
            initData(orders);
        }
    }

    private void initView() {
        etPostage.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    if ("0".equals(s.toString().trim()) || "00".equals(s.toString().trim())) {
                        etPostage.setText("");
                    } else {
                        isFree = 0;
                        chbFree.setChecked(false);
                        postagePaid = 0;
                        chbPostpaid.setChecked(false);
                        try {
                            postage = Integer.parseInt(s.toString().trim());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        etPrice.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null && s.toString().trim().length()!=0){
                    String p = s.toString().trim();
                    try {
                        price = Integer.parseInt(p);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }else{
                    price = 0;
                }
            }
        });

        chbFree.setOnCheckedChangeListener(this);
        chbPostpaid.setOnCheckedChangeListener(this);
        blankSpace.setOnClickListener(this);
        tvBtnConfirm1.setOnClickListener(this);
        tvBtnConfirm.setOnClickListener(this);
    }

    private void initData(OrderDetailBean bean) {
        OrderInfo orderInfo = bean.getOrderInfo();
        GoodsInfo goodsInfo = bean.getGoodsInfo();
        orderId = orderInfo.getOrderId();
        int shipType = orderInfo.getShipType();
        if(shipType == 2){//显示邮费选项
            llPostage.setVisibility(View.VISIBLE);
            tvBtnConfirm1.setVisibility(View.GONE);
            isFree = 0;
            postagePaid = 0;
            postage = orderInfo.getOrderPostage();
            if(postage>0){
                etPostage.setText(String.valueOf(postage));
            }else{
                etPostage.getText().clear();
            }
        }else{
            llPostage.setVisibility(View.GONE);
            tvBtnConfirm1.setVisibility(View.VISIBLE);
            if(shipType == 1){
                isFree = 1;
                postagePaid = 0;
            }else if(shipType == 4){
                isFree = 0;
                postagePaid = 1;
            }
            postage = 0;
            etPostage.getText().clear();
        }

        int cId = goodsInfo.getCfId();
        int isAuction = goodsInfo.isAuction;
        llPrice.setVisibility((cId==20||isAuction == 1)?View.GONE:View.VISIBLE);
        price = orderInfo.getOrderPrice();
        etPrice.setText(String.valueOf(price));
    }

    private void initData(Orders orders) {
        OrderData orderData = orders.getOrderData();
        GoodsData goodsData = orders.getGoodsData();
        orderId = orderData.getOrderId();
        int shipType = orderData.getShipType();
        if(shipType == 2){//显示邮费选项
            llPostage.setVisibility(View.VISIBLE);
            tvBtnConfirm1.setVisibility(View.GONE);
            isFree = 0;
            postagePaid = 0;
            postage = orderData.getPostage();
            if(postage>0){
                etPostage.setText(String.valueOf(postage));
            }else{
                etPostage.getText().clear();
            }
        }else{
            llPostage.setVisibility(View.GONE);
            tvBtnConfirm1.setVisibility(View.VISIBLE);
            if(shipType == 1){
                isFree = 1;
                postagePaid = 0;
            }else if(shipType == 4){
                isFree = 0;
                postagePaid = 1;
            }
            postage = 0;
            etPostage.getText().clear();
        }

        int cId = goodsData.getFirstCateId();
        int isAuction = goodsData.isAuction;
        llPrice.setVisibility((cId==20||isAuction == 1)?View.GONE:View.VISIBLE);
        price = orderData.getPrice();
        etPrice.setText(String.valueOf(price));
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == chbFree){
            if(isChecked){
                this.isFree = 1;
                this.chbPostpaid.setChecked(false);
                this.postagePaid = 0;
                this.postage = 0;
                etPostage.getText().clear();
            }else{
                this.isFree = 0;
            }
        }else if(buttonView == chbPostpaid){
            if(isChecked){
                this.postagePaid = 1;
                this.chbFree.setChecked(false);
                this.isFree = 0;
                this.postage = 0;
                etPostage.getText().clear();
            }else{
                this.postagePaid = 0;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == blankSpace){
            finish();
            overridePendingTransition(0,0);
        }else if(v == tvBtnConfirm || v == tvBtnConfirm1){
            doRequestForChangeOrderPrice();
        }
    }


    private void doRequestForChangeOrderPrice(){
        if(isFree == 0 && postagePaid == 0 && TextUtils.isEmpty(etPostage.getText().toString().trim())){
            toast("至少选择一种邮费方式");
            return;
        }

        if(isFree == 0 && postagePaid == 0 && postage==0){
            toast("邮费为大于0的整数");
            return;
        }

        if(price == 0){
            toast("售价最小金额1元");
            return;
        }

        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", String.valueOf(orderId));
        params.put("orderPrice", String.valueOf(price));
        params.put("orderPostage", String.valueOf(postage));
        params.put("freightCollect", String.valueOf(postagePaid));
        LogUtil.i(TAG, "修改订单价格params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.CHANGEORDERPRICE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        toast("提交成功");
                        CommonUtils.hideKeyBoard(OrderPriceAndPostageChangeActivity.this);
                        setResult(RESULT_OK);
                        finish();
                        overridePendingTransition(0,0);
                    } else {
                        CommonUtils.error(baseResult, OrderPriceAndPostageChangeActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
