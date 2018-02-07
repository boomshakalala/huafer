package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.activity.CommentEditReplyActivity;
import com.huapu.huafen.activity.CommentsDetailActivity;
import com.huapu.huafen.activity.ConfirmReceiptSuccessActivity;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.activity.OrderExpressEditActivity;
import com.huapu.huafen.activity.OrderExpressListActivity;
import com.huapu.huafen.activity.OrderPriceAndPostageChangeActivity;
import com.huapu.huafen.activity.OrderRefundActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.alipay.AliPayHelper;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.OrderData;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.OrderDetailData;
import com.huapu.huafen.beans.Orders;
import com.huapu.huafen.chatim.activity.PrivateConversationActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ConfirmReceivedDialog;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.SpeechCodeDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.weixin.WeChatPayHelper;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liang on 2016/10/26.
 */
public class OrdersListAdapter extends CommonWrapper<OrdersListAdapter.BuyListHolder> {

    private final static int REQUEST_CODE_ORDERS_LIST = 0x1111;
    public boolean isPaySuccess = false;
    public boolean isPaying;
    private Context mContext;
    private List<Orders> datas = new ArrayList<Orders>();
    private Fragment fragment;
    private int role;
    private int status;
    private String order_no;
    private boolean isConfirm = false;

    private long orderId;
    private OnItemClickListener onItemClickListener;

    public OrdersListAdapter(Fragment fragment, List<Orders> datas) {
        super();
        this.fragment = fragment;
        this.mContext = fragment.getActivity();
        this.datas = datas;

    }

    public void setData(List<Orders> datas, int role, int status) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        this.datas = datas;
        this.role = role;
        this.status = status;
    }

    public void addAll(List<Orders> datas, int role, int status) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        this.datas.addAll(datas);
        this.role = role;
        this.status = status;
    }

    public boolean isEmpty() {
        return ArrayUtil.isEmpty(datas);
    }

    @Override
    public BuyListHolder onCreateViewHolder(ViewGroup parent, int i) {
        BuyListHolder vh = new BuyListHolder(LayoutInflater.from(mContext).inflate(R.layout.item_listview_orderslist, parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(BuyListHolder viewHolder, int position) {
        final Orders orders = datas.get(position);
        if (orders == null) {
            return;
        }
        orderId = orders.getOrderData().getOrderId();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (orders.getOrderData() == null) {
                    return;
                }
                Intent intent;
                switch (role) {
                    case 0:
                        if (orders.getOrderData() == null) {
                            return;
                        }
                        if (orders.getArbitration() != null) { // 如果仲裁bean不为空，则视为在仲裁中，进入订单详情
                            intent = new Intent(mContext, OrderDetailActivity.class);
                            intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orders.getOrderData().getOrderId());
                            if (fragment != null) {
                                fragment.startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                            } else if (mContext != null) {
                                ((BaseActivity) mContext).startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                            }
                        } else {
                            intent = new Intent(mContext, OrderRefundActivity.class);
                            int orderState = orders.getOrderData().getState();
                            int orderStateCode = orders.getOrderData().getOrderStateCode();
                            LogUtil.e("orderListAdapter", "orderState=" + orderState + ",orderStateCode=" + orderStateCode);
                            if (orderState == 2 && orderStateCode == 10) {
                                intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "2");
                            } else {
                                intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "1");
                            }
                            intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orders.getOrderData().getOrderId());
                            if (fragment != null) {
                                fragment.startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                            } else if (mContext != null) {
                                ((BaseActivity) mContext).startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                            }
                        }
                        break;
                    case 1:
                        intent = new Intent(mContext, OrderDetailActivity.class);
                        intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orders.getOrderData().getOrderId());
                        if (fragment != null) {
                            fragment.startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                        } else if (mContext != null) {
                            ((BaseActivity) mContext).startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                        }
                        break;
                    case 2:
                        intent = new Intent(mContext, OrderDetailActivity.class);
                        intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orders.getOrderData().getOrderId());
                        if (fragment != null) {
                            fragment.startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                        } else if (mContext != null) {
                            ((BaseActivity) mContext).startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                        }
                        break;
                }

            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (role == 1 || role == 2) {
                    if (orders.getOrderData().getStatus() == 7 || orders.getOrderData().getStatus() == 6) {
                        final TextDialog textDialog = new TextDialog(mContext, true);
                        textDialog.setContentText("您确定删除该订单吗？");
                        textDialog.setLeftText("取消");
                        textDialog.setLeftCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                textDialog.dismiss();
                            }
                        });
                        textDialog.setRightText("确定");
                        textDialog.setRightCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                doRequestForDeleteOrder(orders);
                            }
                        });
                        textDialog.show();
                    }
                }

                return false;
            }
        });

        viewHolder.ivPic.setImageURI(orders.getGoodsData().getGoodsImgs().get(0));
        if (orders.getUserData() != null) {
            viewHolder.ivHeader.setImageURI(orders.getUserData().getAvatarUrl());
            viewHolder.ctvName.setData(orders.getUserData());
        }
        if (orders.getRefund() != null) {
            viewHolder.tvRefundStatus.setVisibility(View.VISIBLE);
            viewHolder.tvRefundStatus.setText(orders.getRefund().getTitle());
        } else if (orders.getArbitration() != null) {
            viewHolder.tvRefundStatus.setVisibility(View.VISIBLE);
            viewHolder.tvRefundStatus.setText(orders.getArbitration().getTitle());
        } else {
            viewHolder.tvRefundStatus.setVisibility(View.GONE);
        }

        switch (role) {
            case 0:
                viewHolder.tvBtnMain.setVisibility(View.GONE);
                viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                break;
            case 1:
                if (orders.getOrderData() != null) {
                    switch (orders.getOrderData().getStatus()) {
                        case 1: // ;// 1.已下单、2.支付中、3.已支付、4.已发货、5.已收货、6.关闭、7.已完成、0.all
                            viewHolder.tvBtnMain.setText("立即支付");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                            break;

                        case 2:
                            viewHolder.tvBtnMain.setText("立即支付");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                            break;
                        case 3:
                            viewHolder.tvBtnMain.setText("提醒发货");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                            break;
                        case 4:
                            viewHolder.tvBtnMain.setText("确认收货");
                            viewHolder.tvBtnSecondary.setText("查看物流");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.VISIBLE);
                            break;
                        case 5:
                            viewHolder.tvBtnMain.setText("发布评价");
                            viewHolder.tvBtnSecondary.setText("钱款去向");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.VISIBLE);
                            break;
                        case 7:
                            viewHolder.tvBtnMain.setText("查看评价");
                            viewHolder.tvBtnSecondary.setText("钱款去向");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.VISIBLE);
                            break;
                        default:
                            viewHolder.tvBtnMain.setVisibility(View.GONE);
                            viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                            break;
                    }
                }
                break;

            case 2:
                if (orders.getOrderData() != null) {
                    switch (orders.getOrderData().getStatus()) {
                        case 1: // ;// 1.已下单、2.支付中、3.已支付、4.已发货、5.已收货、6.关闭、7.已完成、0.all
                            if (orders.getGoodsData().isAuction == 1 || (orders.getGoodsData().getFirstCateId() == 20 && orders.getOrderData().getShipType() != 2)) {
                                viewHolder.tvBtnMain.setVisibility(View.GONE);
                            } else {
                                viewHolder.tvBtnMain.setText("修改价格");
                                viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            }
                            viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                            break;

                        case 2:
                            if (orders.getGoodsData().isAuction == 1 || (orders.getGoodsData().getFirstCateId() == 20 && orders.getOrderData().getShipType() != 2)) {
                                viewHolder.tvBtnMain.setVisibility(View.GONE);
                            } else {
                                viewHolder.tvBtnMain.setText("修改价格");
                                viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            }
                            viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                            break;
                        case 3:
                            viewHolder.tvBtnMain.setText("发货");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                            break;
                        case 4:
                            viewHolder.tvBtnMain.setText("查看物流");
                            viewHolder.tvBtnSecondary.setText("提醒收货");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.VISIBLE);
                            break;
                        case 5:
                            viewHolder.tvBtnMain.setText("钱款去向");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                            break;
                        case 7:
                            viewHolder.tvBtnMain.setText("查看评价");
                            viewHolder.tvBtnSecondary.setText("钱款去向");
                            viewHolder.tvBtnMain.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnSecondary.setVisibility(View.VISIBLE);
                            break;
                        default:
                            viewHolder.tvBtnMain.setVisibility(View.GONE);
                            viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                            break;
                    }
                }
                break;
            default:
                viewHolder.tvBtnMain.setVisibility(View.GONE);
                viewHolder.tvBtnSecondary.setVisibility(View.GONE);
                break;
        }

        viewHolder.tvBtnMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (role) {
                    case 1:
                        if (orders.getOrderData() != null) {
                            switch (orders.getOrderData().getStatus()) {
                                case 1:// 立即支付
                                    if (!isPaying) {
                                        orderPay(orders);
                                    }
                                    break;
                                case 2:// 立即支付
                                    if (!isPaying) {
                                        orderPay(orders);
                                    }
                                    break;
                                case 3:// 提醒发货
                                    startRequestForNoticeShip(orders.getOrderData().getOrderId());
                                    break;
                                case 4:// 确认收货
                                    if (orders.getOrderData().getShipType() == 3) {
                                        orderDelivery(orders);
                                    } else {
                                        orderDeliveryOld(orders);
                                    }

                                    break;
                                case 5:// 发布评价
                                    actionToCommentEdit(orders);
                                    break;
                                case 7:// 查看评价
                                    actionToCommentsDetails(orders);
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case 2:
                        if (orders.getOrderData() != null && orders.getGoodsData() != null) {
                            switch (orders.getOrderData().getStatus()) {
                                case 1:// 修改价格
                                    changeOrderPrice(orders);
                                    break;
                                case 2:// 修改价格
                                    ToastUtil.toast(mContext, "正在等待买家支付，无法修改价格");
                                    break;
                                case 3:// 发货
                                    orderShip(orders);
                                    break;
                                case 4:// 查看物流
                                    int orderState = orders.getOrderData().getState();
                                    int orderType;
                                    if (orderState == 2) {
                                        orderType = 2;
                                    } else {
                                        orderType = 1;
                                    }
                                    actionToExpressList(orders.getOrderData().getOrderId(), orderType);
                                    break;
                                case 5:// 钱款去向
                                    actionToWebView();
                                    break;
                                case 7:// 查看评价
                                    actionToCommentsDetails(orders);
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        });
        viewHolder.tvBtnSecondary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (role) {
                    case 1:
                        if (orders.getOrderData() != null) {
                            switch (orders.getOrderData().getStatus()) {
                                case 1:// 无
                                    break;
                                case 2:// 无
                                    break;
                                case 3:// 无
                                    break;
                                case 4:// 查看物流
                                    int orderState = orders.getOrderData().getState();
                                    int orderType;
                                    if (orderState == 2) {
                                        orderType = 2;
                                    } else {
                                        orderType = 1;
                                    }
                                    actionToExpressList(orders.getOrderData().getOrderId(), orderType);
                                    break;
                                case 5:// 钱款去向
                                    actionToWebView();
                                    break;
                                case 7:// 钱款去向
                                    actionToWebView();
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case 2:
                        if (orders.getOrderData() != null) {
                            switch (orders.getOrderData().getStatus()) {
                                case 1:// 无
                                    break;
                                case 2:// 无
                                    break;
                                case 3:// 无
                                    break;
                                case 4:// 提醒收货
                                    startRequestForNoticeDelivery(orders.getOrderData().getOrderId());
                                    break;
                                case 5:// 无
                                    break;
                                case 7:// 钱款去向
                                    actionToWebView();
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });


        //名字和牌子
        GoodsData goodsData = orders.getGoodsData();
        if (goodsData != null) {
            String brand = goodsData.getBrand();
            String goodsName = goodsData.getName();
            String goodsNameDesc;
            if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
                String format = mContext.getString(R.string.goods_name_desc);
                goodsNameDesc = String.format(format, brand, goodsName);
            } else if (!TextUtils.isEmpty(brand) && TextUtils.isEmpty(goodsName)) {
                goodsNameDesc = brand;
            } else if (TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
                goodsNameDesc = goodsName;
            } else {
                goodsNameDesc = "";
            }
            viewHolder.tvGoods.setText(goodsNameDesc);
        }

        OrderData orderData = orders.getOrderData();
        if (orderData != null) {

            if (goodsData.isAuction == 1) {
                int price = goodsData.hammerPrice;
                viewHolder.tvPrice.setText(Html.fromHtml(String.format(mContext.getString(R.string.auction_price1), "成交价", price)));
                viewHolder.flAuctionIcon.setVisibility(View.VISIBLE);
            } else {
                int price = orderData.getPrice();
                viewHolder.tvPrice.setText(Html.fromHtml(String.format(mContext.getString(R.string.price_tag), price)));
                viewHolder.flAuctionIcon.setVisibility(View.GONE);
            }


            int orderPayType = orderData.getOrderPayType();
            viewHolder.tvPayMethod.setText(orderPayType == 1 ? "支付方式：微信" : "支付方式：支付宝");

            int postage = orderData.getPostage();
            int shipType = orderData.getShipType();
            if (shipType == 1) {
                viewHolder.tvIsFreeDelivery.setText("包邮");
            } else if (shipType == 2) {
                if (postage > 0) {
                    viewHolder.tvIsFreeDelivery.setText(String.format(String.format("邮费￥%d", postage)));
                } else {
                    viewHolder.tvIsFreeDelivery.setText("邮费待议");
                }
            } else if (shipType == 3) {
                viewHolder.tvIsFreeDelivery.setText(String.format(String.format("邮费￥%d", 0)));
            } else if (shipType == 4) {
                viewHolder.tvIsFreeDelivery.setText("邮费到付");
            }
        }

        viewHolder.tvBtnCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonPreference.isLogin()) {
                    if (orders == null || orders.getGoodsData() == null || orders.getUserData() == null) {
                        return;
                    }
                    // 开启私信会话
                    Intent intent = new Intent(mContext, PrivateConversationActivity.class);
                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(String.valueOf(orders.getGoodsData().getGoodsId())));
                    intent.putExtra(MyConstants.IM_PEER_ID, String.valueOf(orders.getUserData().getUserId()));
                    mContext.startActivity(intent);
                } else {
                    if (mContext instanceof Activity) {
                        ActionUtil.loginAndToast(mContext);
                    }
                }
            }
        });
    }

    private void orderPay(Orders orders) {
        if (isPaySuccess) {
            ToastUtil.toast(mContext, "您已支付成功，请不要重复点击");
            return;
        }
        int totalPrice = orders.getOrderData().getPrice() + orders.getOrderData().getPostage();
        if (totalPrice >= 500) {
            orderPayTip(orders.getOrderData().getOrderId(), totalPrice, orders.getOrderData().getOrderPayType());
        } else {
            startRequestForOrderPay(orders.getOrderData().getOrderId(), totalPrice, orders.getOrderData().getOrderPayType());
        }
    }

    /**
     * 确认收货
     */
    private void orderDelivery(final Orders orders) {
        if (orders == null || orders.getOrderData() == null) {
            return;
        }

        if (!isConfirm) {
            isConfirm = true;
            final TextDialog dialog = new TextDialog(mContext, false);
            dialog.setContentText("为保障交易安全，验证码将发送至买家手机，验证码10分钟内有效");
            dialog.setLeftText("取消");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                }
            });
            dialog.setRightText("确定");
            dialog.setRightCall(new DialogCallback() {

                @Override
                public void Click() {
                    String phone = CommonPreference.getPhone();
                    safeValidateCode(orders.getOrderData().getOrderId(), phone);
                }
            });
            dialog.show();
        } else {
            String phone = CommonPreference.getPhone();
            safeValidateCode(orders.getOrderData().getOrderId(), phone);
        }
    }

    private void orderDeliveryOld(final Orders orders) {
        if (orders == null || orders.getOrderData() == null) {
            return;
        }
        final ConfirmReceivedDialog dialog = new ConfirmReceivedDialog(mContext);
        dialog.setLeftCall(new DialogCallback() {

            @Override
            public void Click() {
                dialog.dismiss();
            }
        });
        dialog.setRightCall(new DialogCallback() {

            @Override
            public void Click() {
                startRequestForOrderDelivery(orders.getOrderData().getOrderId(), 1);
            }
        });
        dialog.show();
    }

    public void sureReceivedGoods() {
        final ConfirmReceivedDialog dialogSure = new ConfirmReceivedDialog(mContext);
        dialogSure.setLeftCall(new DialogCallback() {

            @Override
            public void Click() {
                dialogSure.dismiss();
            }
        });
        dialogSure.setRightCall(new DialogCallback() {

            @Override
            public void Click() {
                startRequestForOrderDelivery(orderId, 3);
            }
        });
        dialogSure.show();
    }

    private void safeValidateCode(long orderId, String phone) {
        SpeechCodeDialog codeDialog = new SpeechCodeDialog(mContext, orderId, phone);
        codeDialog.setOrderListAdapter(OrdersListAdapter.this);
        codeDialog.show();
    }

    /**
     * 确认收货
     *
     * @param
     */
    private void startRequestForOrderDelivery(long orderId, int shipType) {
        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(mContext);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", String.valueOf(orderId));
        if (shipType == 3) {
            params.put("shipType", "3");
        }
        LogUtil.i("liang", "确认收货params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.ORDERDELIVERY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "确认收货:" + response);
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
                            GoodsInfo goodsInfo = bean.getGoodsInfo();
                            if (bean != null && goodsInfo != null) {
                                Intent intent = new Intent(mContext, ConfirmReceiptSuccessActivity.class);
                                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_BEAN, bean);
                                mContext.startActivity(intent);
                            }


                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick();
                            }
                        }
                    } else {
                        CommonUtils.error(baseResult, (Activity) mContext, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void doRequestForDeleteOrder(final Orders orders) {
        ProgressDialog.showProgress(mContext);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", String.valueOf(orders.getOrderData().getOrderId()));
        OkHttpClientManager.postAsyn(MyConstants.ORDER_DELETE, params, new OkHttpClientManager.StringCallback() {

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
                        if (datas != null) {
                            datas.remove(orders);
                            notifyWrapperDataSetChanged();
                        }
                    } else {
                        CommonUtils.error(baseResult, (Activity) mContext, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 提醒买家收货
     *
     * @param
     */
    private void startRequestForNoticeDelivery(long orderId) {
        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(mContext);
        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", String.valueOf(orderId));
        LogUtil.i("liang", "提醒买家收货params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.NOTICEDELIVERY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "提醒买家收货:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        ToastUtil.toast(mContext, "提醒成功");
                    } else {
                        CommonUtils.error(baseResult, (Activity) mContext, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 买家提醒发货
     *
     * @param
     */
    private void startRequestForNoticeShip(long orderId) {
        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(mContext);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("orderId", String.valueOf(orderId));
        LogUtil.i("liang", "买家提醒发货params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.NOTICESHIP, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "买家提醒发货:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        ToastUtil.toast(mContext, "提醒成功");
                    } else {
                        CommonUtils.error(baseResult, (Activity) mContext, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    /**
     * 付款提示
     */
    private void orderPayTip(final long orderId, final int totalPrice, final int orderPayType) {
        final TextDialog dialog = new TextDialog(mContext, false);
        dialog.setTitleText("温馨提示");
        dialog.setContentText("付款金额较大，为避免交易纠纷，请您与卖家深入沟通，平台不对商品质量与真伪作任何担保，请确认商品无误后再进行支付");
        dialog.setLeftText("取消支付");
        dialog.setLeftCall(new DialogCallback() {

            @Override
            public void Click() {
                dialog.dismiss();
            }
        });
        dialog.setRightText("确定支付");
        dialog.setRightCall(new DialogCallback() {

            @Override
            public void Click() {
                startRequestForOrderPay(orderId, totalPrice, orderPayType);
            }
        });
        dialog.show();
    }

    /**
     * 支付
     *
     * @param
     */
    private void startRequestForOrderPay(long orderId, int totalPrice, int orderPayType) {
        isPaying = true;
        if (!CommonUtils.isNetAvaliable(mContext)) {
            isPaying = false;
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(mContext);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("orderId", String.valueOf(orderId));
        params.put("orderAmount", String.valueOf((totalPrice)));
        params.put("orderPayType", String.valueOf(orderPayType));
        LogUtil.i("danielluan", "支付params:" + params.toString());
        Logger.d("danielluan", "支付params:" + params.toString());

        OkHttpClientManager.postAsyn(MyConstants.ORDERPAY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
                isPaying = false;
                LogUtil.e("onError", "onError");
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e("danielluan", "支付:" + response);
                Logger.d("danielluan", "支付:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    OrderDetailData data = JSON.parseObject(baseResult.obj, OrderDetailData.class);
                    LogUtil.e("danielluan", "OrderDetailData:" + JSON.toJSONString(data));
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (data != null) {
                            if ("1".equals(data.type)) {
                                order_no = data.orderNo;
                                WeChatPayHelper.createPayment((Activity) mContext, data.credential);
                            } else {
                                AliPayHelper alipayHelper = new AliPayHelper((Activity) mContext);
                                alipayHelper.payV2(data.credential, new AliPayHelper.OnAliPayCompleteListener() {

                                    @Override
                                    public void onComplete(String out_trade_no) {
                                        isPaySuccess = true;
                                        isPaying = false;
                                        order_no = out_trade_no;
                                        startRequestForPaySuccess();
                                    }

                                    @Override
                                    public void onFailed(String errorCode) {
                                        //TODO errorCode为阿里的错误码 6001 代表手动取消支付 and so on
                                        isPaying = false;
                                        ToastUtil.toast(mContext, "支付失败");
                                    }
                                });
                            }
                        }
                    } else if (baseResult.code == ParserUtils.RESPONSE_ORDER_PRICE_CHANGE) {
                        OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
                        final long orderId = bean.getOrderInfo().getOrderId();
                        final int orderPayType = bean.getOrderInfo().getOrderPayType();
                        final int totalPrice = bean.getOrderInfo().getOrderPrice() + bean.getOrderInfo().getOrderPostage();
                        final TextDialog dialog = new TextDialog(mContext, false);
                        dialog.setContentText("订单价格改变为总价:" + String.valueOf(totalPrice) + "元");
                        dialog.setLeftText("取消");
                        dialog.setLeftCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                isPaying = false;
                                dialog.dismiss();
                            }
                        });
                        dialog.setRightText("支付");
                        dialog.setRightCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                startRequestForOrderPay(orderId, totalPrice, orderPayType);
                            }
                        });
                        dialog.show();
                    } else {
                        isPaying = false;
                        CommonUtils.error(baseResult, (Activity) mContext, "");
                        LogUtil.e("onError", "onError:" + baseResult.code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 支付成功后通知服务器
     *
     * @param
     */
    public void startRequestForPaySuccess() {
        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(mContext);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("orderNo", order_no);
        LogUtil.i("liang", "支付成功通知服务器params：" + params.toString());
        Logger.d("danielluan", "支付成功通知服务器params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.PAYSUCCESS, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e("liang", "支付成功后通知服务器:" + response);
                Logger.d("danielluan", "支付成功后通知服务器:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        LogUtil.e("liang", "支付成功CODE:" + ParserUtils.RESPONSE_SUCCESS_CODE);
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick();
                            }
                        }
                    } else if (baseResult.code == ParserUtils.RESPONSE_ALIPAY_PAYING_CODE) {
                        LogUtil.e("liang", "支付成功CODE:" + ParserUtils.RESPONSE_ALIPAY_PAYING_CODE);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (onItemClickListener != null) {
                                    onItemClickListener.onItemClick();
                                }
                            }
                        }, 2000);

                    } else {
                        CommonUtils.error(baseResult, (Activity) mContext, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void actionToWebView() {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.WHERE_CASH);
        mContext.startActivity(intent);
    }

    private void actionToCommentEdit(Orders orders) {
        Intent intent = new Intent(mContext, CommentEditReplyActivity.class);
        intent.putExtra(MyConstants.EXTRA_COMMENT_ID, orders.getOrderData().getOrderId());
        intent.putExtra(MyConstants.EXTRA_ORDER_DATA, orders.getOrderData());
        intent.putExtra(MyConstants.EXTRA_GOODS_DATA, orders.getGoodsData());
        if (fragment != null) {
            fragment.startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
        } else if (mContext != null) {
            ((BaseActivity) mContext).startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
        }
    }

    private void actionToExpressList(long orderId, int orderType) {
        Intent intent = new Intent(mContext, OrderExpressListActivity.class);
        intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderId);
        intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, String.valueOf(orderType));
        mContext.startActivity(intent);
    }

    private void actionToCommentsDetails(Orders orders) {
        Intent intent = new Intent(mContext, CommentsDetailActivity.class);
        intent.putExtra(MyConstants.EXTRA_COMMENT_ID, orders.getOrderData().getOrderId());
        intent.putExtra(MyConstants.EXTRA_FROM_USER_ID, CommonPreference.getUserId());
        mContext.startActivity(intent);
    }

    /**
     * 发货
     *
     * @param orders
     */
    private void orderShip(final Orders orders) {
        PhotoDialog dialog1 = new PhotoDialog(mContext, "当面交易", "快递发货");
        dialog1.setCameraCall(new DialogCallback() {

            @Override
            public void Click() {
                // 当面交易
                final TextDialog dialog = new TextDialog(mContext, false);
                dialog.setTitleText("当面交易提醒");
                dialog.setContentText("当面交易时，在买家验货无误后，请要求买家当面点击\"确认收货\"按钮以免钱货两失。");
                dialog.setLeftText("取消");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("确定");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        startRequestForOrderShip(orders.getOrderData().getOrderId());
                    }
                });
                dialog.show();
            }
        });
        dialog1.setAlbumCall(new DialogCallback() {

            @Override
            public void Click() {
                // 快递发货
                Intent intent = new Intent(mContext, OrderExpressEditActivity.class);
                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orders.getOrderData().getOrderId());
                if (fragment != null) {
                    fragment.startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                } else if (mContext != null) {
                    ((BaseActivity) mContext).startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                }
            }
        });
        dialog1.show();
    }

    /**
     * 发货
     *
     * @param
     */
    private void startRequestForOrderShip(long orderId) {
        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(mContext);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("orderId", String.valueOf(orderId));
        params.put("shipType", "3");
        LogUtil.i("liang", "发货params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.ORDERSHIP, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        LogUtil.i("liang", "发货:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (onItemClickListener != null) {
                                    onItemClickListener.onItemClick();
                                }
                            } else {
                                CommonUtils.error(baseResult, (Activity) mContext, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 修改价格
     *
     * @param orders
     */
    private void changeOrderPrice(Orders orders) {
        Intent intent = new Intent(mContext, OrderPriceAndPostageChangeActivity.class);
        intent.putExtra(MyConstants.ORDERS, orders);
        if (fragment != null) {
            fragment.startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
        } else if (mContext != null) {
            ((BaseActivity) mContext).startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

    public class BuyListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivHeader)
        SimpleDraweeView ivHeader;
        @BindView(R.id.ctvName)
        CommonTitleView ctvName;
        @BindView(R.id.tvRefundStatus)
        TextView tvRefundStatus;
        @BindView(R.id.ivPic)
        SimpleDraweeView ivPic;
        @BindView(R.id.tvGoods)
        TextView tvGoods;
        @BindView(R.id.tvPrice)
        TextView tvPrice;
        @BindView(R.id.tvIsFreeDelivery)
        TextView tvIsFreeDelivery;
        @BindView(R.id.tvPayMethod)
        TextView tvPayMethod;
        @BindView(R.id.tvBtnCall)
        TextView tvBtnCall;
        @BindView(R.id.tvBtnSecondary)
        TextView tvBtnSecondary;
        @BindView(R.id.tvBtnMain)
        TextView tvBtnMain;
        @BindView(R.id.flAuctionIcon)
        FrameLayout flAuctionIcon;

        public BuyListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
