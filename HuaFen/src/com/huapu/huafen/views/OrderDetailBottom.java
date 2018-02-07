package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.OrderConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ArbitrationActivity;
import com.huapu.huafen.activity.CommentEditReplyActivity;
import com.huapu.huafen.activity.CommentsDetailActivity;
import com.huapu.huafen.activity.ConfirmReceiptSuccessActivity;
import com.huapu.huafen.activity.MainActivity;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.activity.OrderExpressEditActivity;
import com.huapu.huafen.activity.OrderPriceAndPostageChangeActivity;
import com.huapu.huafen.activity.OrderRefundActivity;
import com.huapu.huafen.activity.RefundOptionActivity;
import com.huapu.huafen.alipay.AliPayHelper;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.OrderData;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.OrderDetailData;
import com.huapu.huafen.beans.OrderInfo;
import com.huapu.huafen.callbacks.OnRequestRetryListener;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ConfirmReceivedDialog;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.SpeechCodeDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.weixin.WeChatPayHelper;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * Created by mac on 17/6/21.
 */
public class OrderDetailBottom extends LinearLayout {

    private final static int TYPE_BUYER = 0;
    private final static int TYPE_SELLER = TYPE_BUYER + 1;
    private final static int REQUEST_CODE_FOR_MODIFY_COURIER_NUMBER = 0x233;
    private final static int REQUEST_CODE_FOR_CHANGE_PRICE_AND_POSTAGE = REQUEST_CODE_FOR_MODIFY_COURIER_NUMBER + 1;
    private final int TYPE_ARBITRATION_BUYER = 0;
    private final int TYPE_ARBITRATION_SELLER = 1;
    private boolean isPaySuccess;
    private String orderNo;
    private boolean isConfirm;
    private OnOrderStateChangedListener mOnOrderStateChangedListener;

    public OrderDetailBottom(Context context) {
        this(context, null);
    }

    public OrderDetailBottom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    public void setData(final OrderDetailBean bean) {
        if (bean == null) {
            return;
        }
        long buyerId = bean.getOrderInfo().getBuyerId();
        long sellerId = bean.getOrderInfo().getSellerid();
        long myUserId = CommonPreference.getUserId();
        int orderType = -1;
        if (myUserId > 0) {
            if (buyerId == myUserId) {
                orderType = TYPE_BUYER;
            } else if (sellerId == myUserId) {
                orderType = TYPE_SELLER;
            }
        }

        if (orderType == -1) {
            return;
        }

        final OrderInfo orderInfo = bean.getOrderInfo();
        final GoodsInfo goodsInfo = bean.getGoodsInfo();
        if (orderInfo == null || goodsInfo == null) {
            return;
        }

        int orderStatus = orderInfo.getOrderStatus(); // 主状态
        int orderState = orderInfo.getOrderState(); // 辅状态
        final int orderStateCode = orderInfo.getOrderStateCode();// 辅状态code

        boolean isEvidence = orderInfo.getIsEvidence();
        int arbitrationType = -1;
        if (bean.getArbitrationInfo() != null) {
            long arbitrationUserId = bean.getArbitrationInfo().getArbitrationUserId();
            if (arbitrationUserId == buyerId) {
                arbitrationType = TYPE_ARBITRATION_BUYER;
            } else if (arbitrationUserId == sellerId) {
                arbitrationType = TYPE_ARBITRATION_SELLER;
            }
        }

        if (orderType == TYPE_BUYER) {//买家
            if (orderStatus == OrderConstants.OrderStatus.PREPARE_TO_PAY) {//待支付
                if (goodsInfo.isAuction == 1) {
                    generateClickAbleButton("立刻支付", new OnBuyerPayNowListener(orderInfo));
                } else {
                    generateWhiteAndRedButtonLayout("取消订单", new OnCancelOrderListener(orderInfo.getOrderId(), "您确定取消订单吗?"), "立刻支付", new OnBuyerPayNowListener(orderInfo));
                }
            } else if (orderStatus == OrderConstants.OrderStatus.PAYING) {//支付中
                if (orderState == OrderConstants.OrderState.NORMAL && orderStateCode == OrderConstants.OrderStateCode.PAYMENT_PROCESSING) {
                    generateEnableButton("订单支付结果获取中");
                } else {
                    if (goodsInfo.isAuction == 1) {
                        generateClickAbleButton("立刻支付", new OnBuyerPayNowListener(orderInfo));
                    } else {
                        generateWhiteAndRedButtonLayout("取消订单", new OnCancelOrderListener(orderInfo.getOrderId(), "您确定取消订单吗?"), "立刻支付", new OnBuyerPayNowListener(orderInfo));
                    }
                }
            } else if (orderStatus == OrderConstants.OrderStatus.PAYED) {//已支付
                if (orderState == OrderConstants.OrderState.NORMAL) {//正常
                    if (goodsInfo.isAuction == 1) {
                        generateClickAbleButton("提醒发货", new OnRemindShipmentListener(orderInfo.getOrderId()));
                    } else {
                        generateWhiteAndRedButtonLayout("申请退款", new OnBuyerApplyRefundListener(orderInfo), "提醒发货", new OnRemindShipmentListener(orderInfo.getOrderId()));
                    }
                } else if (orderState == OrderConstants.OrderState.CANCEL) {//取消

                } else if (orderState == OrderConstants.OrderState.REFUND) {//退款
                    if (orderStateCode == OrderConstants.OrderStateCode.BUYER_HAS_REFUND) {
                        generateClickAbleButton("查看退款信息", new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (orderInfo != null) {
                                    Intent intent = new Intent(getContext(), OrderRefundActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "2");
                                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderInfo.getOrderId());
                                    intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_FROM, MyConstants.ORDER_DETAILS);
                                    getContext().startActivity(intent);
                                }
                            }
                        });
                    } else {
                        generateWhiteAndRedButtonLayout("取消退款申请", new OnBuyerCancelRefundListener(bean.getRefundInfo().getRefundId()), "查看退款信息", new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (orderInfo != null) {
                                    Intent intent = new Intent(getContext(), OrderRefundActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "2");
                                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderInfo.getOrderId());
                                    intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_FROM, MyConstants.ORDER_DETAILS);
                                    getContext().startActivity(intent);
                                }
                            }
                        });
                    }
                } else if (orderState == OrderConstants.OrderState.ARBITRATION) {//仲裁
                    initBuyerArbitration(bean, orderInfo, orderStateCode, isEvidence, arbitrationType);
                }
            } else if (orderStatus == OrderConstants.OrderStatus.PREPARE_TO_RECEIPT) {//待收货
                if (orderState == OrderConstants.OrderState.NORMAL) {//正常
                    if (goodsInfo.isAuction == 1) {
                        generateWhiteAndRedButtonLayout("延长收货", new OnDelayReceiptListener(orderInfo.getOrderId()), "确认收货", new OnReceiptListener(orderInfo));
                    } else {
                        generateBuyerPrepareForReceiptNormal(orderInfo);
                    }
                } else if (orderState == OrderConstants.OrderState.CANCEL) {//取消

                } else if (orderState == OrderConstants.OrderState.REFUND) {//退款
                    if (orderStateCode == OrderConstants.OrderStateCode.BUYER_HAS_REFUND) {//买家已退货
                        generateClickAbleButton("查看退款信息", new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), OrderRefundActivity.class);
                                intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "2");
                                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderInfo.getOrderId());
                                intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_FROM, MyConstants.ORDER_DETAILS);
                                getContext().startActivity(intent);
                            }
                        });
                    } else {
                        generateWhiteAndRedButtonLayout("取消退款申请", new OnBuyerCancelRefundListener(bean.getRefundInfo().getRefundId()), "查看退款信息", new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (orderInfo != null) {
                                    Intent intent = new Intent(getContext(), OrderRefundActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "1");
                                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderInfo.getOrderId());
                                    intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_FROM, MyConstants.ORDER_DETAILS);
                                    getContext().startActivity(intent);
                                }
                            }
                        });
                    }
                } else if (orderState == OrderConstants.OrderState.ARBITRATION) {//仲裁
                    initBuyerArbitration(bean, orderInfo, orderStateCode, isEvidence, arbitrationType);
                }

            } else if (orderStatus == OrderConstants.OrderStatus.RECEIPT) {//已收货
                generateWhiteAndRedButtonLayout("返回首页", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, 0);
                        getContext().startActivity(intent);
                    }
                }, "去评价", new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), CommentEditReplyActivity.class);
                        GoodsData goodsData = new GoodsData();
                        goodsData.setName(goodsInfo.getGoodsName());
                        goodsData.setBrand(goodsInfo.getGoodsBrand());
                        goodsData.setGoodsImgs(goodsInfo.getGoodsImgs());
                        OrderData orderData = new OrderData();
                        orderData.setPrice(orderInfo.getOrderPrice());
                        orderData.setPostage(orderInfo.getOrderPostage());
                        orderData.setOrderId(orderInfo.getOrderId());
                        intent.putExtra(MyConstants.EXTRA_COMMENT_ID, orderInfo.getOrderId());
                        intent.putExtra(MyConstants.EXTRA_ORDER_DATA, orderData);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DATA, goodsData);
                        try {

                            Context context = getContext();
                            if (context != null) {
                                if (context instanceof ConfirmReceiptSuccessActivity) {
                                    ConfirmReceiptSuccessActivity activity = (ConfirmReceiptSuccessActivity) getContext();
                                    activity.startActivityForResult(intent, ConfirmReceiptSuccessActivity.REQUEST_CODE);
                                } else if (context instanceof OrderDetailActivity) {
                                    OrderDetailActivity activity = (OrderDetailActivity) getContext();
                                    activity.startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ORDER_CHANGE);
                                }
                            }
                        } catch (Exception e) {
                            LogUtil.d("danielluan", e.getMessage());
                        }
                    }
                });

            } else if (orderStatus == OrderConstants.OrderStatus.ORDER_CLOSE) {//订单已关闭
                generateEnableButton("订单已关闭");
            } else if (orderStatus == OrderConstants.OrderStatus.ORDER_COMPLETE) {//订单已完成
                generateWhiteAndRedButtonLayout("查看我的界面", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, 4);
                        getContext().startActivity(intent);
                    }
                }, "查看评价", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), CommentsDetailActivity.class);
                        intent.putExtra(MyConstants.EXTRA_COMMENT_ID, orderInfo.getOrderId());
                        intent.putExtra(MyConstants.EXTRA_FROM_USER_ID, CommonPreference.getUserId());
                        getContext().startActivity(intent);
                    }
                });
            }

        } else if (orderType == TYPE_SELLER) {//卖家
            if (orderStatus == OrderConstants.OrderStatus.PREPARE_TO_PAY) {//待支付
                if (goodsInfo.isAuction == 1) {
                    generateClickAbleButton("取消订单", new OnCancelOrderListener(orderInfo.getOrderId(), "您确定取消订单吗?"));
                } else {
                    if (goodsInfo.getCampaignId() == 20 || goodsInfo.getCfId() == 20 && orderInfo.getShipType() != 2) {//邮费到付或包邮
                        generateClickAbleButton("取消订单", new OnCancelOrderListener(orderInfo.getOrderId(), "您确定取消订单吗?"));
                    } else {
                        generateWhiteAndRedButtonLayout("取消订单", new OnCancelOrderListener(orderInfo.getOrderId(), "您确定取消订单吗?"), "修改价格", new OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getContext(), OrderPriceAndPostageChangeActivity.class);
                                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_BEAN, bean);
                                ((Activity) getContext()).startActivityForResult(intent, REQUEST_CODE_FOR_CHANGE_PRICE_AND_POSTAGE);
                                ((Activity) getContext()).overridePendingTransition(0, 0);
                            }
                        });
                    }
                }
            } else if (orderStatus == OrderConstants.OrderStatus.PAYING) {//支付中
                if (orderState == OrderConstants.OrderState.NORMAL && orderStateCode == OrderConstants.OrderStateCode.PAYMENT_PROCESSING) {
                    generateEnableButton("订单支付结果获取中");
                } else {
                    generateEnableButton("买家支付中");
                }
            } else if (orderStatus == OrderConstants.OrderStatus.PAYED) {//已支付
                if (orderState == OrderConstants.OrderState.NORMAL) {//正常
                    generateWhiteAndRedButtonLayout("取消订单", new OnCancelOrderListener(orderInfo.getOrderId(), "买家已付款，确认取消订单吗？建议先与买家联系协商后确认。"),
                            "确认发货", new OnSellerShipListener(orderInfo.getOrderId()));
                } else if (orderState == OrderConstants.OrderState.CANCEL) {//取消

                } else if (orderState == OrderConstants.OrderState.REFUND) {//退款
                    generateClickAbleButton("查看退款申请", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OrderRefundActivity.class);
                            if (orderStateCode == 10) {
                                intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "2");
                            } else {
                                intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "1");
                            }
                            intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, bean.getOrderInfo().getOrderId());
                            intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_FROM, MyConstants.ORDER_DETAILS);
                            getContext().startActivity(intent);
                        }
                    });
                } else if (orderState == OrderConstants.OrderState.ARBITRATION) {//仲裁
                    initSellerArbitration(bean, orderInfo, orderStateCode, isEvidence, arbitrationType);
                }
            } else if (orderStatus == OrderConstants.OrderStatus.PREPARE_TO_RECEIPT) {//待收货
                if (orderState == OrderConstants.OrderState.NORMAL) {//正常
                    generateSellerPrepareReceipt(orderInfo);
                } else if (orderState == OrderConstants.OrderState.CANCEL) {//取消

                } else if (orderState == OrderConstants.OrderState.REFUND) {//退款
                    generateClickAbleButton("查看退款申请", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OrderRefundActivity.class);
                            if (orderStateCode == 10) {
                                intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "2");
                            } else {
                                intent.putExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE, "1");
                            }
                            intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, bean.getOrderInfo().getOrderId());
                            intent.putExtra(MyConstants.EXTRA_ORDER_REFUND_FROM, MyConstants.ORDER_DETAILS);
                            getContext().startActivity(intent);
                        }
                    });
                } else if (orderState == OrderConstants.OrderState.ARBITRATION) {//仲裁
                    initSellerArbitration(bean, orderInfo, orderStateCode, isEvidence, arbitrationType);
                }
            } else if (orderStatus == OrderConstants.OrderStatus.RECEIPT) {//已收货
                generateClickAbleButton("查看我的界面", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, 4);
                        getContext().startActivity(intent);
                    }
                });
            } else if (orderStatus == OrderConstants.OrderStatus.ORDER_CLOSE) {//已关闭
                generateEnableButton("订单已关闭");
            } else if (orderStatus == OrderConstants.OrderStatus.ORDER_COMPLETE) {//已完成
                generateWhiteAndRedButtonLayout("查看我的界面", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, 4);
                        getContext().startActivity(intent);
                    }
                }, "查看评价", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), CommentsDetailActivity.class);
                        intent.putExtra(MyConstants.EXTRA_COMMENT_ID, orderInfo.getOrderId());
                        intent.putExtra(MyConstants.EXTRA_FROM_USER_ID, CommonPreference.getUserId());
                        getContext().startActivity(intent);
                    }
                });
            }
        }

    }

    //卖家待收货状态
    private void generateSellerPrepareReceipt(final OrderInfo orderInfo) {

        removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

        TextView modifyCourierNumberTextView = null;
        if (orderInfo.getShipType() != 3) {//当面交易
            modifyCourierNumberTextView = new TextView(getContext());
            modifyCourierNumberTextView.setText("修改快递单号");
            modifyCourierNumberTextView.setGravity(Gravity.CENTER);
            modifyCourierNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            modifyCourierNumberTextView.setTextColor(getContext().getResources().getColor(R.color.text_black_light));
            modifyCourierNumberTextView.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            modifyCourierNumberTextView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean modifiedCourierNumber = orderInfo.modifiedCourierNumber;
                    if (modifiedCourierNumber) {
                        final TextDialog dialog = new TextDialog(getContext(), false);
                        dialog.setContentText("快递单号只能修改一次，填写前请核对号信息");
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
                                Intent intent = new Intent(getContext(), OrderExpressEditActivity.class);
                                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderInfo.getOrderId());
                                intent.putExtra(MyConstants.MODIFY_COURIER_NUMBER, true);
                                ((Activity) getContext()).startActivityForResult(intent, REQUEST_CODE_FOR_MODIFY_COURIER_NUMBER);
                            }
                        });
                        dialog.show();
                    } else {
                        final TextDialog dialog = new TextDialog(getContext(), false);
                        dialog.setContentText("快递单号只能修改1次，如果还有问题请及时联系买家");
                        dialog.setRightText("确定");
                        dialog.setRightCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            });
        }


        TextView redTextView = new TextView(getContext());
        redTextView.setText("提醒买家收货");
        redTextView.setGravity(Gravity.CENTER);
        redTextView.setTextColor(getContext().getResources().getColor(R.color.white));
        redTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        redTextView.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
        redTextView.setOnClickListener(new OnNoticeBuyerReceiptListener(orderInfo.getOrderId()));

        if (modifyCourierNumberTextView != null) {
            this.addView(modifyCourierNumberTextView, layoutParams);
        }

        this.addView(redTextView, layoutParams);
    }

    private void initBuyerArbitration(final OrderDetailBean bean, OrderInfo orderInfo, int orderStateCode, boolean isEvidence, int arbitrationType) {
        if (orderStateCode == OrderConstants.OrderStateCode.IN_ARBITRATION) {//仲裁中
            if (arbitrationType == TYPE_ARBITRATION_BUYER) {
                generateClickAbleButton("取消仲裁", new OnCancelArbitrationListener(orderInfo.getOrderId()));
            } else if (arbitrationType == TYPE_ARBITRATION_SELLER) {
                generateEnableButton("仲裁中");
            }
        } else if (orderStateCode == OrderConstants.OrderStateCode.ARBITRATION_EVIDENCE_PERIOD) {//三天举证倒计时
            if (arbitrationType == TYPE_ARBITRATION_BUYER) {
                generateArbitrationEvidence(bean, isEvidence);
            } else if (arbitrationType == TYPE_ARBITRATION_SELLER) {
                if (isEvidence) {
                    generateEnableButton("已补充，等待平台处理");
                } else {
                    generateClickAbleButton("补充仲裁证据", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ArbitrationActivity.class);
                            intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL, bean);
                            intent.putExtra(MyConstants.EXTRA_ORDER_ARBITRATION, 2);
                            getContext().startActivity(intent);
                        }
                    });
                }
            }
        } else if (orderStateCode == OrderConstants.OrderStateCode.PLATFORM_INTERVENTION_PERIOD) {//平台介入期，等待仲裁结果
            if (arbitrationType == TYPE_ARBITRATION_BUYER) {
                generateEnableButton("等待仲裁结果");
            } else if (arbitrationType == TYPE_ARBITRATION_SELLER) {
                generateEnableButton("等待仲裁结果");
            }
        }
    }

    private void initSellerArbitration(final OrderDetailBean bean, OrderInfo orderInfo, int orderStateCode, boolean isEvidence, int arbitrationType) {
        if (orderStateCode == OrderConstants.OrderStateCode.IN_ARBITRATION) {//仲裁中
            if (arbitrationType == TYPE_ARBITRATION_BUYER) {
                generateEnableButton("仲裁中");
            } else if (arbitrationType == TYPE_ARBITRATION_SELLER) {
                generateClickAbleButton("取消仲裁", new OnCancelArbitrationListener(orderInfo.getOrderId()));
            }
        } else if (orderStateCode == OrderConstants.OrderStateCode.ARBITRATION_EVIDENCE_PERIOD) {//三天举证倒计时
            if (arbitrationType == TYPE_ARBITRATION_BUYER) {
                if (isEvidence) {
                    generateEnableButton("已补充，等待平台处理");
                } else {
                    generateClickAbleButton("补充仲裁证据", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ArbitrationActivity.class);
                            intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL, bean);
                            intent.putExtra(MyConstants.EXTRA_ORDER_ARBITRATION, 2);
                            getContext().startActivity(intent);
                        }
                    });
                }
            } else if (arbitrationType == TYPE_ARBITRATION_SELLER) {
                generateArbitrationEvidence(bean, isEvidence);
            }
        } else if (orderStateCode == OrderConstants.OrderStateCode.PLATFORM_INTERVENTION_PERIOD) {//平台介入期，等待仲裁结果
            if (arbitrationType == TYPE_ARBITRATION_BUYER) {
                generateEnableButton("等待仲裁结果");
            } else if (arbitrationType == TYPE_ARBITRATION_SELLER) {
                generateEnableButton("等待仲裁结果");
            }
        }
    }

    //买家待收货正常状态
    private void generateBuyerPrepareForReceiptNormal(final OrderInfo orderInfo) {
        removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

        TextView applyRefundTextView = new TextView(getContext());
        applyRefundTextView.setText("申请退款");
        applyRefundTextView.setGravity(Gravity.CENTER);
        applyRefundTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        applyRefundTextView.setTextColor(getContext().getResources().getColor(R.color.text_black_light));
        applyRefundTextView.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        applyRefundTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RefundOptionActivity.class);
                if (orderInfo != null) {
                    if (orderInfo.getShipType() == 3) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(MyConstants.EXTRA_ORDER_DETAIL_ID, orderInfo.getOrderId());
                        bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_TYPE, 2);
                        bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_PRICE, orderInfo.getOrderPrice());
                        bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_POST_AGE, orderInfo.getOrderPostage());
                        bundle.putInt(MyConstants.EXTRA_ORDER_TARGET, 0);
                        bundle.putBoolean(MyConstants.EXTRA_CAN_MODIFY_REFUND, true);
                        intent.putExtras(bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putLong(MyConstants.EXTRA_ORDER_DETAIL_ID, orderInfo.getOrderId());
                        bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_TYPE, 2);
                        bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_PRICE, orderInfo.getOrderPrice());
                        bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_POST_AGE, orderInfo.getOrderPostage());
                        bundle.putInt(MyConstants.EXTRA_ORDER_TARGET, 0);
                        bundle.putBoolean(MyConstants.EXTRA_CAN_MODIFY_REFUND, true);
                        intent.putExtras(bundle);
                    }

                    getContext().startActivity(intent);
                }
            }
        });
        TextView delayReceiptTextView = null;
        View line = null;
        if (orderInfo.getShipType() != 3) {//当面交易
            line = new View(getContext());
            line.setBackgroundColor(getContext().getResources().getColor(R.color.divider_black));
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(0.5f), LinearLayout.LayoutParams.MATCH_PARENT);
            line.setLayoutParams(lineParams);

            delayReceiptTextView = new TextView(getContext());
            delayReceiptTextView.setText("延长收货");
            delayReceiptTextView.setGravity(Gravity.CENTER);
            delayReceiptTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            delayReceiptTextView.setTextColor(getContext().getResources().getColor(R.color.text_black_light));
            delayReceiptTextView.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            delayReceiptTextView.setOnClickListener(new OnDelayReceiptListener(orderInfo.getOrderId()));
        }

        TextView receiptTextView = new TextView(getContext());
        receiptTextView.setText("确认收货");
        receiptTextView.setGravity(Gravity.CENTER);
        receiptTextView.setTextColor(getContext().getResources().getColor(R.color.white));
        receiptTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        receiptTextView.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
        receiptTextView.setOnClickListener(new OnReceiptListener(orderInfo));

        this.addView(applyRefundTextView, layoutParams);
        if (delayReceiptTextView != null && line != null) {
            this.addView(line);
            this.addView(delayReceiptTextView, layoutParams);
        }
        this.addView(receiptTextView, layoutParams);

    }

    // 仲裁举证
    private void generateArbitrationEvidence(final OrderDetailBean bean, boolean isEvidence) {
        removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

        TextView cancelArbitrationTextView = new TextView(getContext());
        cancelArbitrationTextView.setText("取消仲裁");
        cancelArbitrationTextView.setGravity(Gravity.CENTER);
        cancelArbitrationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        cancelArbitrationTextView.setTextColor(getContext().getResources().getColor(R.color.text_black_light));
        cancelArbitrationTextView.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        cancelArbitrationTextView.setOnClickListener(new OnCancelArbitrationListener(bean.getOrderInfo().getOrderId()));

        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        textView.setGravity(Gravity.CENTER);
        if (isEvidence) {
            textView.setText("已补充，等待平台处理");
            textView.setTextColor(getResources().getColor(R.color.text_color));
            textView.setBackgroundColor(getResources().getColor(R.color.base_btn_normal));
        } else {
            textView.setText("补充仲裁证据");
            textView.setTextColor(getContext().getResources().getColor(R.color.white));
            textView.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
            textView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ArbitrationActivity.class);
                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL, bean);
                    intent.putExtra(MyConstants.EXTRA_ORDER_ARBITRATION, 2);
                    getContext().startActivity(intent);
                }
            });
        }


        this.addView(cancelArbitrationTextView, layoutParams);
        this.addView(textView, layoutParams);
    }

    //生成白色和红色左右两个按钮
    private void generateWhiteAndRedButtonLayout(String whiteText, OnClickListener onWhiteButtonClickListener, String redText, OnClickListener onRedButtonClickListener) {
        removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

        TextView whiteTextView = new TextView(getContext());
        whiteTextView.setText(whiteText);
        whiteTextView.setGravity(Gravity.CENTER);
        whiteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        whiteTextView.setTextColor(getContext().getResources().getColor(R.color.text_black_light));
        whiteTextView.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        whiteTextView.setOnClickListener(onWhiteButtonClickListener);

        TextView redTextView = new TextView(getContext());
        redTextView.setText(redText);
        redTextView.setGravity(Gravity.CENTER);
        redTextView.setTextColor(getContext().getResources().getColor(R.color.white));
        redTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        redTextView.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
        redTextView.setOnClickListener(onRedButtonClickListener);

        this.addView(whiteTextView, layoutParams);
        this.addView(redTextView, layoutParams);
    }

    //单个不可点击状态
    private void generateEnableButton(String text) {
        removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.text_black));
        textView.setBackgroundColor(getResources().getColor(R.color.base_btn_normal));
        textView.setText(text);

        addView(textView, layoutParams);
    }

    //单个点击按钮
    private void generateClickAbleButton(String text, OnClickListener onClickListener) {
        removeAllViews();
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setBackgroundColor(getResources().getColor(R.color.base_pink));
        textView.setText(text);
        textView.setOnClickListener(onClickListener);
        addView(textView, layoutParams);
    }

    public void setOnOrderStateChangedListener(OnOrderStateChangedListener onOrderStateChangedListener) {
        this.mOnOrderStateChangedListener = onOrderStateChangedListener;
    }

    //支付成功
    private void startRequestForPaySuccess() {
        ProgressDialog.showProgress(getContext());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderNo", orderNo);
        Logger.d("danielluan", "支付成功通知服务器params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.PAYSUCCESS, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
                ToastUtil.toast(getContext(), "请检查网络连接");
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                try {
                    Logger.d("danielluan", "支付成功通知服务器：" + response.toString());
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        LogUtil.e("liang", "支付成功CODE:" + ParserUtils.RESPONSE_SUCCESS_CODE);
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
                            if (mOnOrderStateChangedListener != null) {
                                mOnOrderStateChangedListener.onOrderDataChanged(bean);
                            }
                        }
                    } else if (baseResult.code == ParserUtils.RESPONSE_ALIPAY_PAYING_CODE) {
                        if (mOnOrderStateChangedListener != null) {
                            mOnOrderStateChangedListener.onOrderRefresh();
                        }
                    } else {
                        CommonUtils.error(baseResult, (Activity) getContext(), "");
                        ((Activity) getContext()).finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == WeChatPayHelper.REQUEST_CODE_PAYMENT) {
                if (data != null) {
                    String result = data.getExtras().getString("pay_result");
                    if ("success".equals(result)) {
                        isPaySuccess = true;
                        startRequestForPaySuccess();
                    } else {
                        if (mOnOrderStateChangedListener != null) {
                            mOnOrderStateChangedListener.onOrderRefresh();
                        }
                    }
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ORDER_CHANGE) {
                if (mOnOrderStateChangedListener != null) {
                    mOnOrderStateChangedListener.onOrderRefresh();
                }
            } else if (requestCode == REQUEST_CODE_FOR_MODIFY_COURIER_NUMBER) {
                if (mOnOrderStateChangedListener != null) {
                    mOnOrderStateChangedListener.onOrderRefresh();
                }
            } else if (requestCode == REQUEST_CODE_FOR_CHANGE_PRICE_AND_POSTAGE) {
                if (mOnOrderStateChangedListener != null) {
                    mOnOrderStateChangedListener.onOrderRefresh();
                }
            }
        }
    }

    public interface OnOrderStateChangedListener {
        void onOrderRefresh();

        void onOrderDataChanged(OrderDetailBean bean);
    }

    //买家取消退款
    private class OnBuyerCancelRefundListener implements OnClickListener {

        private long refundId;

        public OnBuyerCancelRefundListener(long refundId) {
            this.refundId = refundId;
        }

        @Override
        public void onClick(final View v) {
            final TextDialog dialog = new TextDialog(getContext(), false);
            dialog.setContentText("您确定取消退款吗？");
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
                    startRequestForCancelRefund(v, refundId);
                }
            });
            dialog.show();


        }

        /**
         * 取消退款申请
         */
        private void startRequestForCancelRefund(final View view, long refundId) {

            ProgressDialog.showProgress(getContext());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("refundId", String.valueOf(refundId));
            LogUtil.i("liang", "取消退款申请params:" + params.toString());
            view.setEnabled(false);
            OkHttpClientManager.postAsyn(MyConstants.CANCLEREFUND, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    ProgressDialog.closeProgress();
                    view.setEnabled(true);
                }

                @Override
                public void onResponse(String response) {
                    view.setEnabled(true);
                    ProgressDialog.closeProgress();
                    LogUtil.i("liang", "取消退款申请:" + response);
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
                                if (mOnOrderStateChangedListener != null) {
                                    mOnOrderStateChangedListener.onOrderDataChanged(bean);
                                }
                            }
                        } else {
                            CommonUtils.error(baseResult, (Activity) getContext(), "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            });
        }
    }

    //买家取消订单事件
    private class OnCancelOrderListener implements OnClickListener {

        private long orderId;
        private String content;

        public OnCancelOrderListener(long orderId, String content) {
            this.content = content;
            this.orderId = orderId;
        }

        @Override
        public void onClick(View v) {
            final TextDialog dialog = new TextDialog(getContext(), false);
            dialog.setContentText(content);
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
                    dialog.dismiss();
                    startRequestForOrderCancel(orderId);
                }
            });
            dialog.show();
        }

        // 取消订单
        HashMap<String, String> params = new HashMap<String, String>();

        private void startRequestForOrderCancel(final long orderId) {
            if (!CommonUtils.isNetAvaliable(getContext())) {
                ToastUtil.toast(getContext(), "请检查网络连接");
                return;
            }
            ProgressDialog.showProgress(getContext());

            params.put("orderId", String.valueOf(orderId));
            LogUtil.i("liang", "取消订单params：" + params.toString());
            OkHttpClientManager.postAsyn(MyConstants.ORDERCANCEL, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    ProgressDialog.closeProgress();
                }

                @Override
                public void onResponse(String response) {
                    ProgressDialog.closeProgress();
                    try {
                        boolean flag = CommonUtils.parseEvent(getContext(), response, new OnRequestRetryListener() {

                            @Override
                            public void onRetry() {
                                params.put("confirmed", "1");
                                startRequestForOrderCancel(orderId);
                            }
                        });
                        if (flag) {
                            return;
                        }

                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                            if (!TextUtils.isEmpty(baseResult.obj)) {
                                ToastUtil.toast(getContext(), "订单取消成功");
                                ((Activity) getContext()).finish();
                            }
                        } else {
                            CommonUtils.error(baseResult, (Activity) getContext(), "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    //立刻支付点击事件
    private class OnBuyerPayNowListener implements OnClickListener {

        private OrderInfo orderInfo;

        public OnBuyerPayNowListener(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
        }

        /**
         * 付款提示
         */
        private void orderPayTip(final View view) {
            final TextDialog dialog = new TextDialog(getContext(), false);
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
                    doRequestForPayment(view, orderInfo);
                }
            });
            dialog.show();
        }


        private void doRequestForPayment(final View view, OrderInfo orderInfo) {
            if (!CommonUtils.isNetAvaliable(getContext())) {
                ToastUtil.toast(getContext(), "请检查网络连接");
                return;
            }
            ProgressDialog.showProgress(getContext());
            HashMap<String, String> params = new HashMap<>();
            params.put("orderId", String.valueOf(orderInfo.getOrderId()));
            params.put("orderAmount", String.valueOf((orderInfo.getOrderPrice() + orderInfo.getOrderPostage())));
            params.put("orderPayType", String.valueOf(orderInfo.getOrderPayType()));
            LogUtil.i("liang", "支付params:" + params.toString());
            Logger.d("danielluan", "支付params:" + params.toString());
            view.setEnabled(false);
            OkHttpClientManager.postAsyn(MyConstants.ORDERPAY, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    ProgressDialog.closeProgress();
                    view.setEnabled(true);
                    LogUtil.e("onError", "onError");
                }

                @Override
                public void onResponse(String response) {
                    ProgressDialog.closeProgress();
                    LogUtil.e("liang", "支付:" + response);
                    Logger.d("danielluan", "支付:" + response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    try {
                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        OrderDetailData data = JSON.parseObject(baseResult.obj, OrderDetailData.class);
                        LogUtil.e("liang", "OrderDetailData:" + JSON.toJSONString(data));
                        Logger.d("danielluan", "OrderDetailData:" + JSON.toJSONString(data));
                        if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                            if (data != null) {
                                if ("1".equals(data.type)) {
                                    orderNo = data.orderNo;
                                    WeChatPayHelper.createPayment((Activity) getContext(), data.credential);

                                } else {
                                    AliPayHelper alipayHelper = new AliPayHelper((Activity) getContext());
                                    alipayHelper.payV2(data.credential, new AliPayHelper.OnAliPayCompleteListener() {

                                        @Override
                                        public void onComplete(String out_trade_no) {
                                            isPaySuccess = true;
                                            view.setEnabled(true);
                                            orderNo = out_trade_no;
                                            startRequestForPaySuccess();
                                        }

                                        @Override
                                        public void onFailed(String errorCode) {
                                            //TODO errorCode为阿里的错误码 6001 代表手动取消支付 and so on
                                            view.setEnabled(true);
                                            if (mOnOrderStateChangedListener != null) {
                                                mOnOrderStateChangedListener.onOrderRefresh();
                                            }
                                        }
                                    });
                                }
                            }
                        } else if (baseResult.code == ParserUtils.RESPONSE_ORDER_PRICE_CHANGE) {
                            view.setEnabled(true);
                            final OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
                            final OrderInfo orderInfo = bean.getOrderInfo();
                            int totalPrice = orderInfo.getOrderPrice() + orderInfo.getOrderPostage();
                            final TextDialog dialog = new TextDialog(getContext(), false);
                            dialog.setContentText("订单价格改变为总价:" + String.valueOf(totalPrice) + "元");
                            dialog.setLeftText("取消");
                            dialog.setLeftCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.setRightText("支付");
                            dialog.setRightCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    doRequestForPayment(view, orderInfo);
                                }
                            });
                            dialog.show();
                        } else {
                            view.setEnabled(true);
                            CommonUtils.error(baseResult, (Activity) getContext(), "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (isPaySuccess) {
                ToastUtil.toast(getContext(), "您已支付成功，请不要重复点击");
                return;
            }
            int totalPrice = orderInfo.getOrderPrice() + orderInfo.getOrderPostage();
            if (totalPrice >= 500) {
                orderPayTip(v);
            } else {
                doRequestForPayment(v, orderInfo);
            }
        }
    }

    //买家申请退款
    private class OnBuyerApplyRefundListener implements OnClickListener {


        private OrderInfo orderInfo;

        public OnBuyerApplyRefundListener(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), RefundOptionActivity.class);
            if (orderInfo != null) {
                Bundle bundle = new Bundle();
                bundle.putLong(MyConstants.EXTRA_ORDER_DETAIL_ID, orderInfo.getOrderId());
                bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_TYPE, 1);
                bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_PRICE, orderInfo.getOrderPrice());
                bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_POST_AGE, orderInfo.getOrderPostage());
                bundle.putInt(MyConstants.EXTRA_ORDER_TARGET, 0);
                bundle.putBoolean(MyConstants.EXTRA_CAN_MODIFY_REFUND, false);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        }
    }

    //提醒发货
    private class OnRemindShipmentListener implements OnClickListener {

        private long orderId;

        public OnRemindShipmentListener(long orderId) {
            this.orderId = orderId;
        }

        @Override
        public void onClick(View v) {
            startRequestForNoticeShip(orderId);
        }

        //买家提醒发货
        private void startRequestForNoticeShip(long orderId) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("orderId", String.valueOf(orderId));
            OkHttpClientManager.postAsyn(MyConstants.NOTICESHIP, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    ToastUtil.toast(getContext(), "请检查网络连接");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                            if (!TextUtils.isEmpty(baseResult.obj)) {
                                ToastUtil.toast(getContext(), "提醒成功");
                            }
                        } else {
                            CommonUtils.error(baseResult, (Activity) getContext(), "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    //取消仲裁
    private class OnCancelArbitrationListener implements OnClickListener {

        private long orderId;

        public OnCancelArbitrationListener(long orderId) {
            this.orderId = orderId;
        }

        @Override
        public void onClick(View v) {
            startRequestForCancelArbitration(v, orderId);
        }

        /**
         * 取消仲裁
         *
         * @param
         */
        private void startRequestForCancelArbitration(final View view, long orderId) {
            ProgressDialog.showProgress(getContext());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("orderId", String.valueOf(orderId));
            LogUtil.i("liang", "取消仲裁params:" + params.toString());
            view.setEnabled(false);
            OkHttpClientManager.postAsyn(MyConstants.CANCLEAABITRATION, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    view.setEnabled(true);
                    ProgressDialog.closeProgress();
                    ToastUtil.toast(getContext(), "请检查网络");
                }

                @Override
                public void onResponse(String response) {
                    view.setEnabled(true);
                    ProgressDialog.closeProgress();
                    LogUtil.i("liang", "取消仲裁:" + response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    try {
                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                            if (mOnOrderStateChangedListener != null) {
                                mOnOrderStateChangedListener.onOrderRefresh();
                            }
                        } else {
                            CommonUtils.error(baseResult, (Activity) getContext(), "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    //延长收货
    private class OnDelayReceiptListener implements OnClickListener {


        private long orderId;

        public OnDelayReceiptListener(long orderId) {
            this.orderId = orderId;
        }

        @Override
        public void onClick(View v) {
            startRequestForDelayReceipt(orderId);
        }

        /**
         * 延长收货
         *
         * @param
         */
        private void startRequestForDelayReceipt(long orderId) {
            ProgressDialog.showProgress(getContext());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("orderId", String.valueOf(orderId));
            LogUtil.i("liang", "延长收货params：" + params.toString());
            OkHttpClientManager.postAsyn(MyConstants.DELAYDELIVERY, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    ProgressDialog.closeProgress();
                    ToastUtil.toast(getContext(), "请检查网络连接");
                }

                @Override
                public void onResponse(String response) {
                    ProgressDialog.closeProgress();
                    LogUtil.i("liang", "延长收货:" + response);
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
                                if (mOnOrderStateChangedListener != null) {
                                    mOnOrderStateChangedListener.onOrderDataChanged(bean);
                                }
                            }
                        } else {
                            CommonUtils.error(baseResult, (Activity) getContext(), "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    //确认收货
    private class OnReceiptListener implements OnClickListener {

        private OrderInfo orderInfo;

        public OnReceiptListener(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
        }

        @Override
        public void onClick(View v) {
            if (orderInfo.getShipType() == 3) {
                orderDelivery(orderInfo.getOrderId());
            } else {
                orderDeliveryOld(orderInfo.getOrderId());
            }
        }

        public void orderDeliveryOld(final long orderId) {
            final ConfirmReceivedDialog dialog = new ConfirmReceivedDialog(getContext());
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                }
            });
            dialog.setRightCall(new DialogCallback() {

                @Override
                public void Click() {
                    startRequestForOrderDelivery(orderId, 1);
                }
            });
            dialog.show();
        }

        /**
         * 确认收货
         */
        public void orderDelivery(final long orderId) {
            if (!isConfirm) {
                isConfirm = true;
                final TextDialog dialog = new TextDialog(getContext(), false);
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
                        safeValidateCode(orderId);
                    }
                });
                dialog.show();
            } else {
                safeValidateCode(orderId);
            }
        }

        private void safeValidateCode(final long orderId) {
            String phone = CommonPreference.getPhone();
            SpeechCodeDialog codeDialog = new SpeechCodeDialog(getContext(), orderId, phone);
            codeDialog.setOnReceiptFaceToFaceListener(new SpeechCodeDialog.OnReceiptFaceToFaceListener() {

                @Override
                public void onReceiptFace2Face() {
                    final ConfirmReceivedDialog dialogSure = new ConfirmReceivedDialog(getContext());
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
            });
            codeDialog.show();
        }


        /**
         * 确认收货
         *
         * @param
         */
        private void startRequestForOrderDelivery(long orderId, int shipType) {
            ProgressDialog.showProgress(getContext());
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
                    ToastUtil.toast(getContext(), "请检查网络连接");
                }

                @Override
                public void onResponse(String response) {
                    ProgressDialog.closeProgress();
                    try {
                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                            if (!TextUtils.isEmpty(baseResult.obj)) {
                                OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
                                GoodsInfo goodsInfo = bean.getGoodsInfo();
                                if (bean != null && goodsInfo != null) {
                                    Intent intent = new Intent(getContext(), ConfirmReceiptSuccessActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_BEAN, bean);
                                    getContext().startActivity(intent);
                                }
                                if (mOnOrderStateChangedListener != null) {
                                    mOnOrderStateChangedListener.onOrderRefresh();
                                }
                            }
                        } else {
                            CommonUtils.error(baseResult, (Activity) getContext(), "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    //卖家确认发货
    private class OnSellerShipListener implements OnClickListener {

        private long orderId;

        public OnSellerShipListener(long orderId) {
            this.orderId = orderId;
        }

        @Override
        public void onClick(View v) {
            PhotoDialog dialog1 = new PhotoDialog(getContext(), "当面交易", "快递发货");
            dialog1.setCameraCall(new DialogCallback() {

                @Override
                public void Click() {
                    // 当面交易
                    final TextDialog dialog = new TextDialog(getContext(), false);
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
                            startRequestForOrderShip(orderId);
                        }
                    });
                    dialog.show();
                }
            });
            dialog1.setAlbumCall(new DialogCallback() {

                @Override
                public void Click() {
                    // 快递发货
                    Intent intent = new Intent(getContext(), OrderExpressEditActivity.class);
                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderId);
                    getContext().startActivity(intent);
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
            ProgressDialog.showProgress(getContext());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("orderId", String.valueOf(orderId));
            params.put("shipType", "3");
            OkHttpClientManager.postAsyn(MyConstants.ORDERSHIP, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    ProgressDialog.closeProgress();
                    ToastUtil.toast(getContext(), "请检查网络连接");
                }

                @Override
                public void onResponse(String response) {
                    ProgressDialog.closeProgress();
                    try {
                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                            if (mOnOrderStateChangedListener != null) {
                                mOnOrderStateChangedListener.onOrderRefresh();
                            }
                        } else {
                            CommonUtils.error(baseResult, (Activity) getContext(), "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private class OnNoticeBuyerReceiptListener implements OnClickListener {

        private long orderId;

        public OnNoticeBuyerReceiptListener(long orderId) {
            this.orderId = orderId;
        }

        @Override
        public void onClick(View v) {
            startRequestForNoticeDelivery(orderId);
        }

        /**
         * 提醒买家收货
         *
         * @param
         */
        private void startRequestForNoticeDelivery(long orderId) {
            ProgressDialog.showProgress(getContext());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("orderId", String.valueOf(orderId));
            LogUtil.i("liang", "提醒买家收货params：" + params.toString());
            OkHttpClientManager.postAsyn(MyConstants.NOTICEDELIVERY, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    ProgressDialog.closeProgress();
                    ToastUtil.toast(getContext(), "请检查网络连接");
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
                            if (!TextUtils.isEmpty(baseResult.obj)) {
                                ToastUtil.toast(getContext(), "提醒成功");
                            }
                        } else {
                            CommonUtils.error(baseResult, (Activity) getContext(), "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
