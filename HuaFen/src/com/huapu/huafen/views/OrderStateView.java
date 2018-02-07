package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.OrderConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.enums.OrderEnum;
import com.huapu.huafen.utils.FrescoUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/6/22.
 */

public class OrderStateView extends LinearLayout {

    @BindView(R.id.tvPayment) TextView tvPayment;
    @BindView(R.id.tvShip) TextView tvShip;
    @BindView(R.id.tvReceipt) TextView tvReceipt;
    @BindView(R.id.tvComment) TextView tvComment;
    @BindView(R.id.line1) View line1;
    @BindView(R.id.orderStatePayment) SimpleDraweeView orderStatePayment;
    @BindView(R.id.line2) View line2;
    @BindView(R.id.orderStateShip) SimpleDraweeView orderStateShip;
    @BindView(R.id.line3) View line3;
    @BindView(R.id.orderStateReceipt) SimpleDraweeView orderStateReceipt;
    @BindView(R.id.line4) View line4;
    @BindView(R.id.orderStateComment) SimpleDraweeView orderStateComment;

    public OrderStateView(Context context) {
        this(context,null);
    }

    public OrderStateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.order_state_layout, this, true);
        ButterKnife.bind(this);
    }


    public void setData(int orderStatus){
        if(orderStatus == OrderConstants.OrderStatus.PREPARE_TO_PAY){//待支付
            initOrderState(OrderEnum.OrderState.PREPARE_TO_PAY);
        }else if(orderStatus == OrderConstants.OrderStatus.PAYING){//支付中
            initOrderState(OrderEnum.OrderState.PAYING);
        }else if(orderStatus == OrderConstants.OrderStatus.PAYED){//支付中
            initOrderState(OrderEnum.OrderState.PAYED);
        }else if(orderStatus == OrderConstants.OrderStatus.PREPARE_TO_RECEIPT){//支付中
            initOrderState(OrderEnum.OrderState.PREPARE_TO_RECEIPT);
        }else if(orderStatus == OrderConstants.OrderStatus.RECEIPT){//支付中
            initOrderState(OrderEnum.OrderState.RECEIPT);
        }else if(orderStatus == OrderConstants.OrderStatus.ORDER_COMPLETE){//支付中
            initOrderState(OrderEnum.OrderState.ORDER_COMPLETE);
        }
    }

    private void initOrderState(OrderEnum.OrderState orderState) {
        int[] textRes = orderState.getTextRes();
        int[] textColorRes = orderState.getTextColorRes();
        int[] imageRes = orderState.getImageRes();
        int[] lineBackgroundRes = orderState.getLineBackgroundColorRes();

        TextView[] textViews = new TextView[]{tvPayment,tvShip,tvReceipt,tvComment};
        SimpleDraweeView[] simpleDraweeViews = new SimpleDraweeView[]{orderStatePayment,orderStateShip,orderStateReceipt,orderStateComment};
        View[] lines = new View[]{line1,line2,line3,line4};

        //文案&颜色
        for(int i=0 ; i < textViews.length; i++){
            textViews[i].setText(textRes[i]);
            textViews[i].setTextColor(getContext().getResources().getColor(textColorRes[i]));
        }

        //图片
        for(int i =0; i<simpleDraweeViews.length; i++){
            simpleDraweeViews[i].setImageURI(FrescoUtils.getResUri(imageRes[i]));
        }

        //线颜色
        for(int i=0 ; i<lines.length; i++){
            lines[i].setBackgroundColor(getContext().getResources().getColor(lineBackgroundRes[i]));
        }
    }

}
