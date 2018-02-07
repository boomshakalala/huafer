package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.huapu.huafen.beans.Orders;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.LogUtil;

import java.util.List;

/**
 * Created by admin on 2017/2/25.
 */

public class OrderListView extends LinearLayout{


    private List<Orders> data;

    public OrderListView(Context context) {
        this(context,null);
    }

    public OrderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void setData(List<Orders> ordersList){
        removeAllViews();
        this.data = ordersList;
        if(!ArrayUtil.isEmpty(ordersList)){
            setVisibility(VISIBLE);
            for(Orders orders:ordersList){
                OrderJointItem item = new OrderJointItem(getContext());
                item.setOrders(orders);
                addView(item);
            }
        }else{
            setVisibility(GONE);
        }


    }

    public String getOrderIds(){
        String orderIds = null;
        if(!ArrayUtil.isEmpty(data)){
            StringBuilder sb = new StringBuilder();
            for(Orders order:data){
                if(order.isCheck){
                    sb.append(order.getOrderData().getOrderId()).append(",");
                }
            }
            LogUtil.e("ORDERS_IDS",sb.toString());
            if(sb.length()>0){
                sb.deleteCharAt(sb.length()-1);
            }
            orderIds = sb.toString();

        }
        return orderIds;
    }


}
