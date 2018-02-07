package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.OrderHistoryAdapter;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.OrderHistoryResult;
import com.huapu.huafen.beans.OrderInfo;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/6/29.
 */

public class OrderHistoryActivity extends BaseActivity {


    @BindView(R.id.orderHistoryList) RecyclerView orderHistoryList;
    private OrderHistoryAdapter adapter ;
    private OrderInfo orderInfo;
    private SimpleDraweeView goodsImage;
    private TextView tvGoodsNameAndBrand;
    private TextView tvPriceAndPostage;
    private TextView tvTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history_list_layout);
        orderInfo  = (OrderInfo) mIntent.getSerializableExtra(MyConstants.EXTRA_ORDER_DETAIL);
        if(orderInfo == null){
            toast("订单不存在");
            return;
        }
        LinearLayoutManager manager = new LinearLayoutManager(orderHistoryList.getContext(),LinearLayoutManager.VERTICAL, false);
        orderHistoryList.setLayoutManager(manager);
        adapter = new OrderHistoryAdapter(orderHistoryList.getContext());
        View headerView = LayoutInflater.from(orderHistoryList.getContext()).inflate(R.layout.order_history_header,orderHistoryList,false);
        butterKnifeFindViews(headerView);
        adapter.addHeaderView(headerView);
        orderHistoryList.setAdapter(adapter.getWrapperAdapter());
        doRequestForOrderHistoryList();
    }

    private void butterKnifeFindViews(View headerView) {
        goodsImage = ButterKnife.findById(headerView, R.id.goodsImage);
        tvGoodsNameAndBrand = ButterKnife.findById(headerView,R.id.tvGoodsNameAndBrand);
        tvPriceAndPostage = ButterKnife.findById(headerView,R.id.tvPriceAndPostage);
        tvTotalPrice = ButterKnife.findById(headerView,R.id.tvTotalPrice);
    }

    private void setHeaderView(GoodsInfo goodsInfo,OrderInfo orderInfo){

        ArrayList<String> list = goodsInfo.getGoodsImgs();
        if(!ArrayUtil.isEmpty(list)){
            String goodsUrl = list.get(0);
            goodsImage.setImageURI(goodsUrl);
        }

        //品牌和名称
        String brand = goodsInfo.getGoodsBrand();
        String goodsName = goodsInfo.getGoodsName();
        String goodsNameDesc ;
        if(!TextUtils.isEmpty(brand)&&!TextUtils.isEmpty(goodsName)){
            String format = getString(R.string.goods_name_desc);
            goodsNameDesc = String.format(format,brand,goodsName);
        }else if(!TextUtils.isEmpty(brand)&&TextUtils.isEmpty(goodsName)){
            goodsNameDesc = brand;
        }else if(TextUtils.isEmpty(brand)&&!TextUtils.isEmpty(goodsName)){
            goodsNameDesc = goodsName;
        }else{
            goodsNameDesc = "";
        }

        tvGoodsNameAndBrand.setText(goodsNameDesc);

        //价格
        int price = orderInfo.getOrderPrice();
        int postage = orderInfo.getOrderPostage();
        String postageDes = null;
        int shipType = orderInfo.getShipType();
        if(shipType == 1){
            postageDes ="包邮";
        }else if(shipType == 2){
            if(postage>0){
                postageDes = String.format(String.format("邮费￥%d", postage));
            }else{
                postageDes = "邮费待议";
            }
        }else if(shipType == 3){
            postageDes = String.format(String.format("邮费￥%d", 0));
        }else if(shipType == 4){
            postageDes = "邮费到付";
        }

        String priceDes = String.format(getString(R.string.order_price_des),price,postageDes);
        tvPriceAndPostage.setText(Html.fromHtml(priceDes));

        //支付总价
        int totalPrice = orderInfo.getOrderPrice() + orderInfo.getOrderPostage();
        String totalPriceDes = String.format(getString(R.string.order_total_price_des),totalPrice);
        tvTotalPrice.setText(Html.fromHtml(totalPriceDes));
    }

    @Override
    public void initTitleBar() {
        setTitleString("历史记录");
    }

    public void doRequestForOrderHistoryList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", String.valueOf(orderInfo.getOrderId()));

        OkHttpClientManager.postAsyn(MyConstants.ORDER_HISTORY_LIST, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                toast("请检查网络连接");
            }

            @Override
            public void onResponse(String response) {

                try {
                    OrderHistoryResult result = JSON.parseObject(response, OrderHistoryResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        setHeaderView(result.obj.orderHistory.goods,orderInfo);
                        adapter.setData(result.obj.orderHistory.orderOperates);
                    } else {
                        CommonUtils.error(result, OrderHistoryActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
