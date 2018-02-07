package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.activity.OrderListActivity;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.OrderListBean;
import com.huapu.huafen.beans.OrderLog;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.events.MessageUnReadCountEvent;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.views.CommonTitleView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2017/3/16.
 */

public class OrderListAdapter extends CommonWrapper<OrderListAdapter.OrderListViewHolder> {

    private List<OrderListBean> data;

    private Context context;

    public OrderListAdapter(Context context, List<OrderListBean> data) {
        this.context = context;
        this.data = data;
    }

    public OrderListAdapter(Context context) {
        this(context,null);
    }

    public void setData(List<OrderListBean> data){
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addAll(List<OrderListBean> data){
        if(this.data==null){
            this.data = new ArrayList<>();
        }
        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }

    @Override
    public OrderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_listview_orderlist,parent,false));
    }

    @Override
    public void onBindViewHolder(OrderListViewHolder viewHolder, final int position) {
        final OrderListBean item = data.get(position);
        UserInfo userInfo = item.getUserInfo();
        GoodsInfo goodsInfo = item.getGoodsInfo();
        final OrderLog orderLog = item.getOrderLog();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderLog.getOrderId());
                intent.putExtra(MyConstants.EXTRA_ORDER_MESSAGE_ID, orderLog.getOrderMsgId());
                context.startActivity(intent);
                if(orderLog.getStatus() == 0) {
                    MyConstants.UNREAD_ORDER_COUNT = MyConstants.UNREAD_ORDER_COUNT - 1;
                    MessageUnReadCountEvent event = new MessageUnReadCountEvent();
                    event.isUpdate = true;
                    EventBus.getDefault().post(event);
                    orderLog.setStatus(1);
                    if(context instanceof OrderListActivity){
                        if(hasUnreadMessage()){
                            ((OrderListActivity)context).getTitleBar().getTitleTextRight().setEnabled(true);
                            ((OrderListActivity)context).getTitleBar().getTitleTextRight().setTextColor(context.getResources().getColorStateList(R.color.text_pink_pink45_selector));
                        }else{
                            ((OrderListActivity)context).getTitleBar().getTitleTextRight().setEnabled(false);
                            ((OrderListActivity)context).getTitleBar().getTitleTextRight().setTextColor(Color.parseColor("#cccccc"));
                        }
                    }
                    notifyWrapperDataSetChanged();
                }
            }
        });

        if(goodsInfo!=null){
            ArrayList<String> goodsImages = goodsInfo.getGoodsImgs();
            if(!ArrayUtil.isEmpty(goodsImages)){
                String url = goodsImages.get(0);
                String tag = (String) viewHolder.ivProPic.getTag();
                if(TextUtils.isEmpty(tag)||!tag.equals(url)){
                    viewHolder.ivProPic.setTag(url);
                    viewHolder.ivProPic.setImageURI(url);
                }
            }

            String goodsName = goodsInfo.getGoodsName();
            String goodsBand=goodsInfo.getGoodsBrand();
            if(!TextUtils.isEmpty(goodsName)&&!TextUtils.isEmpty(goodsName)){
                String des=goodsBand+" "+goodsName;
                viewHolder.tvDes.setText(des);
            }
        }

        if(userInfo!=null){
            viewHolder.ctvName.setData(userInfo);
        }

        if(orderLog!=null){
            String operationRemark = orderLog.getOperationRemark();
            if(!TextUtils.isEmpty(operationRemark)){
                viewHolder.tvOperationRemark.setText(operationRemark);
            }
            long operationTime = orderLog.getOperationTime();
            if(operationTime>0){
                viewHolder.tvTime.setText(DateTimeUtils.getYearMonthDay(operationTime));
            }
            int status = orderLog.getStatus();
            if(status == 0) {
                viewHolder.tvOrderUnRead.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvOrderUnRead.setVisibility(View.GONE);
            }
            long buyerId = orderLog.getBuyerId();
            long userId = CommonPreference.getUserId();
            if(buyerId == userId) {
                viewHolder.tvTarget.setText("买");
                viewHolder.tvTarget.setBackground(context.getResources().getDrawable(R.drawable.buy_bg));
            } else {
                viewHolder.tvTarget.setText("卖");
                viewHolder.tvTarget.setBackground(context.getResources().getDrawable(R.drawable.sell_bg));
            }
        }

    }

    public void readAll(){
        if(!ArrayUtil.isEmpty(data)){
            for(OrderListBean var :data){
                OrderLog orderLog = var.getOrderLog();
                if(orderLog!=null){
                    orderLog.setStatus(1);
                }
            }
            notifyWrapperDataSetChanged();
        }
    }

    public boolean hasUnreadMessage(){
        if(!ArrayUtil.isEmpty(data)){
            for(OrderListBean var :data){
                OrderLog orderLog = var.getOrderLog();
                if(orderLog.getStatus() == 0){
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public int getItemCount() {
        return data== null?0:data.size();
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder{

        @BindView(R2.id.ctvName) CommonTitleView ctvName;
        @BindView(R2.id.tvTime) TextView tvTime;
        @BindView(R2.id.tvDes) TextView tvDes;
        @BindView(R2.id.tvOrderUnRead) TextView tvOrderUnRead;
        @BindView(R2.id.tvOperationRemark) TextView tvOperationRemark;
        @BindView(R2.id.tvTarget) TextView tvTarget;
        @BindView(R2.id.ivProPic) SimpleDraweeView ivProPic;

        public OrderListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            ImageLoader.loadImage(ivProPic,null,R.drawable.default_pic,R.drawable.default_pic);
        }
    }

}
