package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.OrderInfo;
import com.huapu.huafen.utils.DateTimeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/6/24.
 */

public class OrderInformationView extends LinearLayout {


    @BindView(R.id.tvOrderNumber) TextView tvOrderNumber;
    @BindView(R.id.llOrderNo) LinearLayout llOrderNo;
    @BindView(R.id.tvOrderCreateTime) TextView tvOrderCreateTime;
    @BindView(R.id.llOrderTime) LinearLayout llOrderTime;
    @BindView(R.id.tvPayMethod) TextView tvPayMethod;
    @BindView(R.id.llPayMethod) LinearLayout llPayMethod;
    @BindView(R.id.tvPayTime) TextView tvPayTime;
    @BindView(R.id.llPayTime) LinearLayout llPayTime;
    @BindView(R.id.tvShipTime) TextView tvShipTime;
    @BindView(R.id.llShipTime) LinearLayout llShipTime;
    @BindView(R.id.tvReceiptTime) TextView tvReceiptTime;
    @BindView(R.id.llReceiptTime) LinearLayout llReceiptTime;

    public OrderInformationView(Context context) {
        this(context, null);
    }

    public OrderInformationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.order_info_layout, this, true);
        ButterKnife.bind(this);
    }

    public void setData(OrderInfo orderInfo) {
        if (orderInfo == null) {
            return;
        }

        //订单编号
        long orderNo = orderInfo.getOrderNum();
        if (orderNo > 0) {
            llOrderNo.setVisibility(VISIBLE);
            tvOrderNumber.setText(String.valueOf(orderNo));
        }else{
            llOrderNo.setVisibility(GONE);
        }

        //下单时间
        long orderCreateTime = orderInfo.getOrderCreateTime();
        if(orderCreateTime>0){
            llOrderTime.setVisibility(VISIBLE);
            String createTime = DateTimeUtils.getYearMonthDayHourMinuteSecond(orderCreateTime);
            tvOrderCreateTime.setText(createTime);
        }else{
            llOrderTime.setVisibility(GONE);
        }

        //支付方式
        int payType = orderInfo.getOrderPayType();
        if(payType == 1){//微信
            llPayMethod.setVisibility(VISIBLE);
            tvPayMethod.setText("微信支付");
        }else if(payType == 2){//支付宝
            llPayMethod.setVisibility(VISIBLE);
            tvPayMethod.setText("支付宝支付");
        }else{
            llPayMethod.setVisibility(GONE);
        }

        //支付时间
        long orderPayTime = orderInfo.getOrderPayTime();
        if(orderPayTime>0){
            llPayTime.setVisibility(VISIBLE);
            String payTime = DateTimeUtils.getYearMonthDayHourMinuteSecond(orderPayTime);
            tvPayTime.setText(payTime);
        }else{
            llPayTime.setVisibility(GONE);
        }

        //发货时间
        long orderDeliverTime = orderInfo.getOrderDeliverTime();
        if(orderDeliverTime > 0){
            llShipTime.setVisibility(VISIBLE);
            String deliverTime = DateTimeUtils.getYearMonthDayHourMinuteSecond(orderDeliverTime);
            tvShipTime.setText(deliverTime);
        }else{
            llShipTime.setVisibility(GONE);
        }

        //收货时间
        long orderReceiptTime = orderInfo.getOrderReceiveTime();
        if(orderReceiptTime>0){
            llReceiptTime.setVisibility(VISIBLE);
            String receiptTime = DateTimeUtils.getYearMonthDayHourMinuteSecond(orderReceiptTime);
            tvReceiptTime.setText(receiptTime);
        }else{
            llReceiptTime.setVisibility(GONE);
        }
    }


}
