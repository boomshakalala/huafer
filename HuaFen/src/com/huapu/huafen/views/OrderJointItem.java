package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.OrderData;
import com.huapu.huafen.beans.Orders;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/2/25.
 */

public class OrderJointItem extends RelativeLayout {


    @BindView(R.id.chbSelect)
    CheckBox chbSelect;
    @BindView(R.id.ivPic)
    SimpleDraweeView ivPic;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvPayMethod)
    TextView tvPayMethod;
    @BindView(R.id.llRemark)
    LinearLayout llRemark;
    @BindView(R.id.rlContainer)
    LinearLayout rlContainer;
    @BindView(R.id.tvRemark)
    TextView tvRemark;
    @BindView(R.id.tvIsFreeDelivery)
    TextView tvIsFreeDelivery;

    public OrderJointItem(Context context) {
        this(context, null);
    }

    public OrderJointItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.order_joint_item_item, this, true);
        ButterKnife.bind(this);

    }

    public void setOrders(final Orders orders) {
        UserData userData = orders.getUserData();
        GoodsData goodsData = orders.getGoodsData();

        if (goodsData != null) {
            //设置图片
            List<String> urls = goodsData.getGoodsImgs();
            if (!ArrayUtil.isEmpty(urls)) {
                String url = urls.get(0);
                String tag = (String) ivPic.getTag();
                if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                    ivPic.setTag(url);
                    ImageLoader.resizeSmall(ivPic, url, 1);
                }
            }

            //名字和牌子
            String brand = goodsData.getBrand();
            String goodsName = goodsData.getName();
            String goodsNameDesc;
            if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
                String format = getContext().getString(R.string.goods_name_desc);
                goodsNameDesc = String.format(format, brand, goodsName);
            } else if (!TextUtils.isEmpty(brand) && TextUtils.isEmpty(goodsName)) {
                goodsNameDesc = brand;
            } else if (TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
                goodsNameDesc = goodsName;
            } else {
                goodsNameDesc = "";
            }
            tvName.setText(goodsNameDesc);

            //价格
            int price = goodsData.getPrice();
            tvPrice.setText(Html.fromHtml(String.format(getContext().getString(R.string.price_tag), price)));


            tvPayMethod.setText(orders.getOrderData().getOrderPayType() == 1 ? "支付方式：微信" : "支付方式：支付宝");

        }

        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (orders.getOrderData() != null) {
                    OrderData orderData = orders.getOrderData();
                    Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderData.getOrderId());
                    ((Activity) getContext()).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_REFRESH);
                }
            }
        });

        chbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                orders.isCheck = isChecked;
            }
        });

        chbSelect.setChecked(orders.isCheck);

        OrderData orderData = orders.getOrderData();
        if (orderData != null) {
            if (!TextUtils.isEmpty(orderData.getOrderMemo())) {
                llRemark.setVisibility(VISIBLE);
                tvRemark.setText(orderData.getOrderMemo());
            } else {
                llRemark.setVisibility(GONE);
            }

            int postage = orderData.getPostage();
            int shipType = orderData.getShipType();
            if (shipType == 1) {
                tvIsFreeDelivery.setText("包邮");
            } else if (shipType == 2) {
                if (postage > 0) {
                    tvIsFreeDelivery.setText(String.format(String.format("邮费￥%d", postage)));
                } else {
                    tvIsFreeDelivery.setText("邮费待议");
                }
            } else if (shipType == 3) {
                tvIsFreeDelivery.setText(String.format(String.format("邮费￥%d", 0)));
            }
        }
    }

}
