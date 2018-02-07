package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.activity.OrderExpressEditActivity;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.beans.OrderJointResult;
import com.huapu.huafen.beans.Orders;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.chatim.activity.PrivateConversationActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.OrderListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/2/24.
 */

public class OrderJointAdapter extends CommonWrapper<OrderJointAdapter.OrderJointHolder> {

    public static final int REQUEST_CODE_FOR_REFRESH = 0x1232;

    private Context context;
    private List<OrderJointResult.OrderJointData> data;

    public OrderJointAdapter(Context context) {
        this(context, null);
    }

    public OrderJointAdapter(Context context, List<OrderJointResult.OrderJointData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public OrderJointHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderJointHolder(LayoutInflater.from(context).
                inflate(R.layout.order_joint_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final OrderJointHolder holder, int position) {
        final OrderJointResult.OrderJointData item = data.get(position);

        final UserData userData = item.getUserData();
        final Consignee consignee = item.getConsignee();
        final List<Orders> orders = item.getOrders();

        /**用户信息**/
        if (userData != null) {

            //头像
            String url = userData.getAvatarUrl();
            String tag = (String) holder.ivHeader.getTag();
            if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                holder.ivHeader.setTag(url);
                ImageLoader.resizeSmall(holder.ivHeader, url, 1);
            }

            //名字
            holder.ctvName.setData(userData);
        }

        /**收货信息**/
        if (consignee != null) {
            String consigneeName = consignee.getConsigneeName();
            String consigneePhone = consignee.getConsigneePhone();
            StringBuilder stringBuilder = new StringBuilder();

            if (!TextUtils.isEmpty(consigneeName)) {
                stringBuilder.append(consigneeName);
                if (!TextUtils.isEmpty(consigneePhone)) {
                    stringBuilder.append(consigneePhone);
                }
                holder.tvReceiver.setText(stringBuilder.toString());
            }

            Area area = consignee.getArea();
            String province;
            String city;
            String areaName;
            StringBuilder sb = new StringBuilder();
            if (area != null) {
                province = area.getProvince();
                city = area.getCity();
                if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)) {
                    if (province.equals(city)) {
                        sb.append(city);
                    } else {
                        sb.append(province).append(city);
                    }
                } else {
                    if (!TextUtils.isEmpty(province)) {
                        sb.append(province);
                    }

                    if (!TextUtils.isEmpty(city)) {
                        sb.append(city);
                    }
                }

                areaName = area.getArea();
                if (!TextUtils.isEmpty(areaName)) {
                    sb.append(areaName);
                }
            }

            String consigneeAddress = consignee.getConsigneeAddress();
            if (!TextUtils.isEmpty(consigneeAddress)) {
                sb.append(consigneeAddress);
            }

            holder.tvShippingAddress.setText(sb.toString());

        }

        /**订单信息**/
        holder.orderListView.setData(orders);

        holder.tvBatchDelivery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String orderIds = holder.orderListView.getOrderIds();
                if (TextUtils.isEmpty(orderIds)) {
                    ToastUtil.toast(context, "请选择要发货的订单");
                } else {
                    Intent intent = new Intent(context, OrderExpressEditActivity.class);
                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_IDS, orderIds);
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE_FOR_REFRESH);
                }

            }
        });

        holder.tvLinkTA.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonPreference.isLogin()) {
                    if (ArrayUtil.isEmpty(orders) || orders.get(0).getGoodsData() == null || userData == null) {
                        return;
                    }
                    // 启动会话界面
                    // 启动会话界面
                    Intent intent = new Intent(context, PrivateConversationActivity.class);
                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(String.valueOf(orders.get(0).getGoodsData().getGoodsId())));
                    intent.putExtra(MyConstants.IM_PEER_ID, String.valueOf(userData.getUserId()));
                    context.startActivity(intent);
                } else {
                    if (context instanceof Activity) {
                        ActionUtil.loginAndToast(context);
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<OrderJointResult.OrderJointData> batches) {
        this.data = batches;
        notifyWrapperDataSetChanged();
    }

    public void addAll(List<OrderJointResult.OrderJointData> batches) {
        if (data == null) {
            data = new ArrayList<>();
        }
        this.data.addAll(batches);
        notifyWrapperDataSetChanged();

    }


    public static class OrderJointHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivHeader)
        SimpleDraweeView ivHeader;
        @BindView(R.id.ctvName)
        CommonTitleView ctvName;
        @BindView(R.id.tvReceiver)
        TextView tvReceiver;
        @BindView(R.id.tvShippingAddress)
        TextView tvShippingAddress;
        @BindView(R.id.orderListView)
        OrderListView orderListView;
        @BindView(R.id.tvLinkTA)
        TextView tvLinkTA;
        @BindView(R.id.tvBatchDelivery)
        TextView tvBatchDelivery;

        public OrderJointHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
