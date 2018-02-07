package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.beans.Express;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.OrderDetailResult;
import com.huapu.huafen.beans.OrderHistoryResult;
import com.huapu.huafen.beans.OrderInfo;
import com.huapu.huafen.beans.Trace;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.chatim.activity.PrivateConversationActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.events.OrderDetailRequestEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.OrderDetailBottom;
import com.huapu.huafen.views.OrderInformationView;
import com.huapu.huafen.views.OrderTopView;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 订单详情
 * Created by mac on 17/6/21.
 */
public class OrderDetailActivity extends BaseActivity {
    public static final String TAG = "OrderDetailActivity";
    private final static int PREPARE_TO_PAY = 1;//待支付
    private final static int PAYING = PREPARE_TO_PAY + 1;//支付中
    private final static int PAYED = PAYING + 1;//已支付
    private final static int PREPARE_TO_RECEIPT = PAYED + 1;//待收货
    private final static int RECEIPT = PREPARE_TO_RECEIPT + 1;//已收货
    private final static int ORDER_CLOSE = RECEIPT + 1;//已关闭
    private final static int ORDER_COMPLETE = ORDER_CLOSE + 1;//订单完成

    private final static int TYPE_BUYER = 0;
    private final static int TYPE_SELLER = TYPE_BUYER + 1;

    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;

    //刷新布局
    @BindView(R.id.ptrFrameLayout)
    PtrDefaultFrameLayout ptrFrameLayout;
    //滚动布局
    @BindView(R.id.scrollLayout)
    ScrollView scrollLayout;
    //顶部View
    @BindView(R.id.orderTopView)
    OrderTopView orderTopView;
    //历史记录
    @BindView(R.id.llOrderHistory)
    LinearLayout llOrderHistory;
    @BindView(R.id.tvLastOrderRecord)
    TextView tvLastOrderRecord;
    @BindView(R.id.tvOlderOrderRecord)
    TextView tvOlderOrderRecord;
    //中间部分
    @BindView(R.id.llContainer)
    LinearLayout llContainer;
    //底部按钮
    @BindView(R.id.orderDetailBottom)
    OrderDetailBottom orderDetailBottom;

    //买家
    @BindView(R.id.llBuyerContainer)
    LinearLayout llBuyerContainer;
    @BindView(R.id.sellerAvatar)
    SimpleDraweeView sellerAvatar;//卖家头像
    @BindView(R.id.ctvSellerName)
    CommonTitleView ctvSellerName;//卖家名
    @BindView(R.id.tvLinkSeller)
    TextView tvLinkSeller;//联系卖家
    @BindView(R.id.llBuyerExpress)
    LinearLayout llBuyerExpress;//快递信息layout（显示在买家模块儿）
    @BindView(R.id.tvBuyerExpress)
    TextView tvBuyerExpress;//快递信息（显示在买家模块儿）
    @BindView(R.id.tvBuyerExpressTime)
    TextView tvBuyerExpressTime;//快递时间（显示在买家模块儿）
    @BindView(R.id.rlBuyerGoods)
    RelativeLayout rlBuyerGoods;//商品信息
    @BindView(R.id.goodsBuyerImg)
    SimpleDraweeView goodsBuyerImg;//商品图片
    @BindView(R.id.flBuyerAuctionIcon)
    FrameLayout flBuyerAuctionIcon;//拍卖条
    @BindView(R.id.tvBuyerGoodsNameAndBrand)
    TextView tvBuyerGoodsNameAndBrand;//商品品牌和名称
    @BindView(R.id.tvBuyerPrice)
    TextView tvBuyerPrice;//商品价格
    @BindView(R.id.rlOrderBuyerNote)
    RelativeLayout rlOrderBuyerNote;//订单备注
    @BindView(R.id.tvBuyerNoNote)
    TextView tvBuyerNoNote;//未填写订单备注
    @BindView(R.id.tvBuyerNote)
    TextView tvBuyerNote;//订单备注
    @BindView(R.id.tvBuyerTotalPrice)
    TextView tvBuyerTotalPrice;
    @BindView(R.id.llBuyerReceiptInfo)
    LinearLayout llBuyerReceiptInfo;//收货人信息layout
    @BindView(R.id.llBuyerReceiptName)
    LinearLayout llBuyerReceiptName;//收货人名称layout
    @BindView(R.id.tvBuyerReceiptName)
    TextView tvBuyerReceiptName;//收货人名称
    @BindView(R.id.llBuyerReceiptAddress)
    LinearLayout llBuyerReceiptAddress;//收货人地址layout
    @BindView(R.id.tvBuyerReceiptAddress)
    TextView tvBuyerReceiptAddress;//收货人地址
    @BindView(R.id.buyerOrderInformationView)
    OrderInformationView buyerOrderInformationView;
    //卖家
    @BindView(R.id.llSellerContainer)
    LinearLayout llSellerContainer;
    @BindView(R.id.llSellerReceiptInfo)
    LinearLayout llSellerReceiptInfo;//收货人信息
    @BindView(R.id.llSellerReceiptName)
    LinearLayout llSellerReceiptName;//收货人名称layout
    @BindView(R.id.tvSellerReceiptName)
    TextView tvSellerReceiptName;//收货人姓名
    @BindView(R.id.llSellerReceiptAddress)
    LinearLayout llSellerReceiptAddress;//收货地址layout
    @BindView(R.id.tvSellerReceiptAddress)
    TextView tvSellerReceiptAddress;//收货人地址
    @BindView(R.id.llSellerExpress)
    LinearLayout llSellerExpress;//快递信息
    @BindView(R.id.tvSellerExpress)
    TextView tvSellerExpress;//快递信息
    @BindView(R.id.tvSellerExpressTime)
    TextView tvSellerExpressTime;//快递时间
    @BindView(R.id.rlOrderSellerNote)
    RelativeLayout rlOrderSellerNote;//订单备注
    @BindView(R.id.tvSellerNoNote)
    TextView tvSellerNoNote;//订单备注无
    @BindView(R.id.tvSellerNote)
    TextView tvSellerNote;//订单备注
    @BindView(R.id.rlBuyerInfo)
    RelativeLayout rlBuyerInfo;//买家信息
    @BindView(R.id.buyerAvatar)
    SimpleDraweeView buyerAvatar;//买家头像
    @BindView(R.id.ctvBuyerName)
    CommonTitleView ctvBuyerName;//买家名称
    @BindView(R.id.tvLinkBuyer)
    TextView tvLinkBuyer;//联系买家
    @BindView(R.id.rlSellerGoods)
    RelativeLayout rlSellerGoods;//商品信息
    @BindView(R.id.goodsSellerImg)
    SimpleDraweeView goodsSellerImg;//商品图片
    @BindView(R.id.flSellerAuctionIcon)
    FrameLayout flSellerAuctionIcon;//拍卖条
    @BindView(R.id.tvSellerGoodsNameAndBrand)
    TextView tvSellerGoodsNameAndBrand;//商品品牌和名称
    @BindView(R.id.tvSellerPrice)
    TextView tvSellerPrice;//订单价格
    @BindView(R.id.tvSellerTotalPrice)
    TextView tvSellerTotalPrice;//总价
    @BindView(R.id.sellerOrderInformationView)
    OrderInformationView sellerOrderInformationView;//订单信息
    private int orderMsgId;
    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        boolean toast = mIntent.getBooleanExtra("toast", false);
        if (toast) {
            toast("请您在20分钟之内完成支付");
        }
        EventBus.getDefault().register(this);
        ptrFrameLayout.buildPtr(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                int scrollViewY = scrollLayout.getScrollY();
                boolean isTop = scrollViewY <= 0;
                return isTop && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }
        });
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_MESSAGE_ID)) {
            orderMsgId = getIntent().getIntExtra(MyConstants.EXTRA_ORDER_MESSAGE_ID, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL_ID)) {
            Object obj = getIntent().getExtras().get(MyConstants.EXTRA_ORDER_DETAIL_ID);
            if (obj instanceof String) {
                orderId = Long.valueOf((String) obj);
            } else if (obj instanceof Long) {
                orderId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, 0);
            } else {
                LogUtil.e(TAG, "onCreate: target 类型错误");
                orderId = 0;
            }
            startLoading();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("订单详情");
    }

    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        startRequestForGetOrderInfo(LOADING);
    }

    private void refresh() {
        startRequestForGetOrderInfo(REFRESH);
    }

    public void onEventMainThread(OrderDetailRequestEvent event) {
        if (event != null) {
            if (event.isUpdate) {
                refresh();
            } else if (event.orderId > 0) {
                if (orderId == event.orderId) {
                    refresh();
                }
            }
        }
    }


    /**
     * 获取订单信息
     *
     * @param
     */
    public void startRequestForGetOrderInfo(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", String.valueOf(orderId));
        if (orderMsgId != 0) {
            params.put("orderMsgId", String.valueOf(orderMsgId));
        }
        LogUtil.i("liang", "订单信息params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETORDERINFO, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (REFRESH.equals(extra)) {
                    ptrFrameLayout.refreshComplete();
                } else if (LOADING.equals(extra)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                }
            }

            @Override
            public void onResponse(String response) {
                if (REFRESH.equals(extra)) {
                    ptrFrameLayout.refreshComplete();
                } else if (LOADING.equals(extra)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                }
                try {
                    OrderDetailResult result = JSON.parseObject(response, OrderDetailResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        OrderDetailBean bean = result.obj;
                        orderId = bean.getOrderInfo().getOrderId();
                        setOrderData(bean);
                    } else {
                        CommonUtils.error(result, OrderDetailActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void setOrderData(final OrderDetailBean bean) {
        if (bean == null) {
            return;
        }

        final OrderInfo orderInfo = bean.getOrderInfo();
        GoodsInfo goodsInfo = bean.getGoodsInfo();

        if (orderInfo != null && goodsInfo != null) {

            //仲裁按钮
            boolean isArbitration = orderInfo.getIsArbitration();
            if (isArbitration) {
                getTitleBar().getTitleTextRight().setVisibility(View.VISIBLE);
                getTitleBar().getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
                getTitleBar().getTitleTextRight().setText("申请仲裁");
                getTitleBar().getTitleTextRight().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (orderInfo.getReportLockTime() != 0) {
                            toast("未到可申请时间，再沟通一下吧");
                            return;
                        }
                        Intent intent = new Intent(OrderDetailActivity.this, ArbitrationActivity.class);
                        intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL, bean);
                        intent.putExtra(MyConstants.EXTRA_ORDER_ARBITRATION, 1);
                        startActivity(intent);
                    }
                });
            } else {
                getTitleBar().getTitleTextRight().setVisibility(View.GONE);
                getTitleBar().getTitleTextRight().setOnClickListener(null);
            }

            //顶部订单状态
            if (!ArrayUtil.isEmpty(goodsInfo.getGoodsImgs())) {
                String url = goodsInfo.getGoodsImgs().get(0);
                if (!TextUtils.isEmpty(url)) {
                    orderTopView.setData(orderInfo, url);
                }
            }

            //订单历史
            initOrderHistory(bean);

            //买家or卖家订单信息
            long buyerId = orderInfo.getBuyerId();
            long sellerId = orderInfo.getSellerid();
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
                llBuyerContainer.setVisibility(View.GONE);
                llSellerContainer.setVisibility(View.GONE);
            }

            if (orderType == TYPE_BUYER) {
                llBuyerContainer.setVisibility(View.VISIBLE);
                llSellerContainer.setVisibility(View.GONE);
                initBuyerData(bean);
            } else {
                llBuyerContainer.setVisibility(View.GONE);
                llSellerContainer.setVisibility(View.VISIBLE);
                initSellerData(bean);
            }

            //底部订单按钮
            orderDetailBottom.setData(bean);
            orderDetailBottom.setOnOrderStateChangedListener(new OrderDetailBottom.OnOrderStateChangedListener() {

                @Override
                public void onOrderRefresh() {
                    refresh();
                }

                @Override
                public void onOrderDataChanged(OrderDetailBean bean) {
                    setOrderData(bean);
                }

            });
        }
    }

    //历史记录
    private void initOrderHistory(final OrderDetailBean bean) {

        List<OrderHistoryResult.OrderOperate> orderHistory = bean.getOrderOperates();

        if (!ArrayUtil.isEmpty(orderHistory)) {
            llOrderHistory.setVisibility(View.VISIBLE);
            if (orderHistory.size() == 1) {
                tvOlderOrderRecord.setVisibility(View.GONE);
                OrderHistoryResult.OrderOperate item = orderHistory.get(0);
                tvLastOrderRecord.setText(item.createdAt + "   " + item.title);
            } else {
                tvOlderOrderRecord.setVisibility(View.VISIBLE);
                OrderHistoryResult.OrderOperate item0 = orderHistory.get(0);
                tvLastOrderRecord.setText(item0.createdAt + "   " + item0.title);
                OrderHistoryResult.OrderOperate item1 = orderHistory.get(1);
                tvOlderOrderRecord.setText(item1.createdAt + "   " + item1.title);
            }
            llOrderHistory.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    OrderInfo orderInfo = bean.getOrderInfo();
                    Intent intent = new Intent(OrderDetailActivity.this, OrderHistoryActivity.class);
                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL, orderInfo);
                    startActivity(intent);
                }
            });
        } else {
            llOrderHistory.setVisibility(View.GONE);
        }
    }

    private void initBuyerData(OrderDetailBean bean) {
        final UserInfo userInfo = bean.getUserInfo();
        OrderInfo orderInfo = bean.getOrderInfo();
        final GoodsInfo goodsInfo = bean.getGoodsInfo();

        if (userInfo != null) {
            String avatar = userInfo.getUserIcon();
            //卖家头像
            sellerAvatar.setImageURI(avatar);
            sellerAvatar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderDetailActivity.this, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, userInfo.getUserId());
                    startActivity(intent);
                }
            });

            //卖家名字
            ctvSellerName.setData(userInfo);

            //联系卖家
            tvLinkSeller.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (CommonPreference.isLogin()) {
                        Intent intent = new Intent(OrderDetailActivity.this, PrivateConversationActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goodsInfo.getGoodsId()));
                        intent.putExtra(MyConstants.IM_PEER_ID, String.valueOf(userInfo.getUserId()));
                        startActivity(intent);
                    } else {
                        ActionUtil.loginAndToast(OrderDetailActivity.this);
                    }
                }
            });
        }

        int orderStatus = orderInfo.getOrderStatus();
        if (orderStatus == PREPARE_TO_RECEIPT || orderStatus == RECEIPT  || orderStatus == ORDER_COMPLETE) {
            //快递信息
            doRequestForBuyerExpress(orderInfo.getOrderId());
        }


        //商品信息
        //商品图片
        ArrayList<String> list = goodsInfo.getGoodsImgs();
        if (!ArrayUtil.isEmpty(list)) {
            String goodsUrl = list.get(0);
            if (goodsInfo.isAuction == 1) {
                flBuyerAuctionIcon.setVisibility(View.VISIBLE);
            } else {
                flBuyerAuctionIcon.setVisibility(View.GONE);
            }
            goodsBuyerImg.setImageURI(goodsUrl);
        }

        //品牌和名称
        String brand = goodsInfo.getGoodsBrand();
        String goodsName = goodsInfo.getGoodsName();
        String goodsNameDesc;
        if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
            String format = getString(R.string.goods_name_desc);
            goodsNameDesc = String.format(format, brand, goodsName);
        } else if (!TextUtils.isEmpty(brand) && TextUtils.isEmpty(goodsName)) {
            goodsNameDesc = brand;
        } else if (TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
            goodsNameDesc = goodsName;
        } else {
            goodsNameDesc = "";
        }

        tvBuyerGoodsNameAndBrand.setText(goodsNameDesc);

        if (goodsInfo.isAuction == 1) {
            int price = orderInfo.getOrderPrice();
            String priceDes = String.format(getString(R.string.order_auction_price_des), price, "包邮");
            tvBuyerPrice.setText(Html.fromHtml(priceDes));
        } else {
            //价格
            int price = orderInfo.getOrderPrice();
            int postage = orderInfo.getOrderPostage();
            String postageDes = null;
            int shipType = orderInfo.getShipType();
            if (shipType == 1) {
                postageDes = "包邮";
            } else if (shipType == 2) {
                if (postage > 0) {
                    postageDes = String.format(String.format("邮费￥%d", postage));
                } else {
                    postageDes = "邮费待议";
                }
            } else if (shipType == 3) {
                postageDes = String.format(String.format("邮费￥%d", 0));
            } else if (shipType == 4) {
                postageDes = "邮费到付";
            }

            String priceDes = String.format(getString(R.string.order_price_des), price, postageDes);
            tvBuyerPrice.setText(Html.fromHtml(priceDes));
        }

        rlBuyerGoods.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goodsInfo.getGoodsId() + "");
                startActivity(intent);
            }
        });


        //订单备注
        String orderMemo = orderInfo.getOrderMemo();
        if (!TextUtils.isEmpty(orderMemo)) {
            tvBuyerNoNote.setVisibility(View.INVISIBLE);
            tvBuyerNote.setVisibility(View.VISIBLE);
            tvBuyerNote.setText(orderMemo);
        } else {
            tvBuyerNoNote.setVisibility(View.VISIBLE);
            tvBuyerNote.setVisibility(View.GONE);
        }

        OnModifyMemoClickListener onModifyMemoClickListener = null;

        if (orderStatus == PREPARE_TO_PAY || orderStatus == PAYING) {//待支付和支付中
            onModifyMemoClickListener = new OnModifyMemoClickListener(orderInfo);
        } else if (orderStatus == PAYED) {
            int orderState = orderInfo.getOrderState();
            if (orderState == 0 || orderState == 1) {
                onModifyMemoClickListener = new OnModifyMemoClickListener(orderInfo);

            }
        }

        rlOrderBuyerNote.setOnClickListener(onModifyMemoClickListener);

        //支付总价
        int totalPrice = orderInfo.getOrderPrice() + orderInfo.getOrderPostage();
        String totalPriceDes = String.format(getString(R.string.order_total_price_des), totalPrice);
        tvBuyerTotalPrice.setText(Html.fromHtml(totalPriceDes));


        //收货人信息
        Consignee consignee = bean.getConsignee();
        if (consignee != null) {
            llBuyerReceiptInfo.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(consignee.getConsigneeName()) && !TextUtils.isEmpty(consignee.getConsigneePhone())) {
                llBuyerReceiptName.setVisibility(View.VISIBLE);
                String consigneeName = consignee.getConsigneeName() + " " + consignee.getConsigneePhone();
                tvBuyerReceiptName.setText(consigneeName);
            } else {
                llBuyerReceiptName.setVisibility(View.GONE);
            }

            Area area = consignee.getArea();
            String address = "";
            if (area != null) {
                String province = area.getProvince();
                if (!TextUtils.isEmpty(province)) {
                    address = address + province;
                }

                String city = area.getCity();
                if (!TextUtils.isEmpty(city)) {
                    address = address + city;
                }

                String district = area.getArea();
                if (!TextUtils.isEmpty(district)) {
                    address = address + district;
                }
            }
            String consigneeAddress = consignee.getConsigneeAddress();
            if (!TextUtils.isEmpty(consigneeAddress)) {
                address = address + consigneeAddress;
            }
            if (!TextUtils.isEmpty(address)) {
                llBuyerReceiptAddress.setVisibility(View.VISIBLE);
                tvBuyerReceiptAddress.setText(address);
            } else {
                llBuyerReceiptAddress.setVisibility(View.GONE);
            }
        }

        //订单信息
        buyerOrderInformationView.setData(orderInfo);

    }

    private class OnModifyMemoClickListener implements View.OnClickListener {

        private OrderInfo orderInfo;

        public OnModifyMemoClickListener(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(OrderDetailActivity.this, OrderMemoEditActivity.class);
            intent.putExtra(MyConstants.EXTRA_ORDER_MEMO_EDIT, orderInfo.getOrderMemo());
            intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderInfo.getOrderId());
            startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ORDER_MEMO_EDIT);
        }
    }

    /**
     * 获取物流信息
     *
     * @param
     */
    private void doRequestForBuyerExpress(long orderId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", String.valueOf(orderId));
        OkHttpClientManager.postAsyn(MyConstants.GETEXPRESSINFO, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            Express express = ParserUtils.parserOrderExpressData(baseResult.obj);
                            initBuyerExpress(express);
                        }
                    } else {
                        CommonUtils.error(baseResult, OrderDetailActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initBuyerExpress(final Express express) {
        if (express != null) {
            List<Trace> trances = express.getTraces();
            if (!ArrayUtil.isEmpty(trances)) {
                llBuyerExpress.setVisibility(View.VISIBLE);
                Trace trance = trances.get(0);
                if (trance != null) {
                    String acceptStation = trance.getAcceptStation();
                    if (!TextUtils.isEmpty(acceptStation)) {
                        tvBuyerExpress.setText(acceptStation);
                    }
                    String acceptTime = trance.getAcceptTime();
                    if (!TextUtils.isEmpty(acceptTime)) {
                        tvBuyerExpressTime.setText(acceptTime);
                    }
                }
                llBuyerExpress.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderDetailActivity.this, OrderExpressListActivity.class);
                        intent.putExtra(MyConstants.EXTRA_EXPRESS, express);
                        startActivity(intent);
                    }
                });
            } else {
                llBuyerExpress.setVisibility(View.GONE);
            }
        } else {
            llBuyerExpress.setVisibility(View.GONE);
        }
    }


    private void initSellerData(OrderDetailBean bean) {
        final UserInfo userInfo = bean.getUserInfo();
        OrderInfo orderInfo = bean.getOrderInfo();
        final GoodsInfo goodsInfo = bean.getGoodsInfo();

        //买家信息
        if (userInfo != null) {
            String avatar = userInfo.getUserIcon();
            //买家头像
            buyerAvatar.setImageURI(avatar);
            buyerAvatar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderDetailActivity.this, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, userInfo.getUserId());
                    startActivity(intent);
                }
            });

            //买家名字
            ctvBuyerName.setData(userInfo);

            //联系买家
            tvLinkBuyer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (CommonPreference.isLogin()) {
                        Intent intent = new Intent(OrderDetailActivity.this, PrivateConversationActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goodsInfo.getGoodsId()));
                        intent.putExtra(MyConstants.IM_PEER_ID, String.valueOf(userInfo.getUserId()));
                        startActivity(intent);
                    } else {
                        ActionUtil.loginAndToast(OrderDetailActivity.this);
                    }
                }
            });
        }

        int orderStatus = orderInfo.getOrderStatus();
        if (orderStatus == PREPARE_TO_RECEIPT || orderStatus == RECEIPT  || orderStatus == ORDER_COMPLETE) {
            //快递信息
            doRequestForSellerExpress(orderInfo.getOrderId());
        }


        //商品信息
        //商品图片
        ArrayList<String> list = goodsInfo.getGoodsImgs();
        if (!ArrayUtil.isEmpty(list)) {
            String goodsUrl = list.get(0);
            goodsSellerImg.setImageURI(goodsUrl);
            if (goodsInfo.isAuction == 1) {
                flSellerAuctionIcon.setVisibility(View.VISIBLE);
            } else {
                flSellerAuctionIcon.setVisibility(View.GONE);
            }
        }

        //品牌和名称
        String brand = goodsInfo.getGoodsBrand();
        String goodsName = goodsInfo.getGoodsName();
        String goodsNameDesc;
        if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
            String format = getString(R.string.goods_name_desc);
            goodsNameDesc = String.format(format, brand, goodsName);
        } else if (!TextUtils.isEmpty(brand) && TextUtils.isEmpty(goodsName)) {
            goodsNameDesc = brand;
        } else if (TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
            goodsNameDesc = goodsName;
        } else {
            goodsNameDesc = "";
        }

        tvSellerGoodsNameAndBrand.setText(goodsNameDesc);

        if (goodsInfo.isAuction == 1) {
            int price = orderInfo.getOrderPrice();
            String priceDes = String.format(getString(R.string.order_auction_price_des), price, "包邮");
            tvSellerPrice.setText(Html.fromHtml(priceDes));
        } else {
            //价格
            int price = orderInfo.getOrderPrice();
            int postage = orderInfo.getOrderPostage();
            String postageDes = null;
            int shipType = orderInfo.getShipType();
            if (shipType == 1) {
                postageDes = "包邮";
            } else if (shipType == 2) {
                if (postage > 0) {
                    postageDes = String.format(String.format("邮费￥%d", postage));
                } else {
                    postageDes = "邮费待议";
                }
            } else if (shipType == 3) {
                postageDes = String.format(String.format("邮费￥%d", 0));
            } else if (shipType == 4) {
                postageDes = "邮费到付";
            }

            String priceDes = String.format(getString(R.string.order_price_des), price, postageDes);
            tvSellerPrice.setText(Html.fromHtml(priceDes));
        }


        rlSellerGoods.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goodsInfo.getGoodsId() + "");
                startActivity(intent);
            }
        });


        //订单备注
        String orderMemo = orderInfo.getOrderMemo();
        if (!TextUtils.isEmpty(orderMemo)) {
            tvSellerNoNote.setVisibility(View.INVISIBLE);
            tvSellerNote.setVisibility(View.VISIBLE);
            tvSellerNote.setText(orderMemo);
        } else {
            tvSellerNoNote.setVisibility(View.VISIBLE);
            tvSellerNote.setVisibility(View.GONE);
        }

        //支付总价
        int totalPrice = orderInfo.getOrderPrice() + orderInfo.getOrderPostage();
        String totalPriceDes = String.format(getString(R.string.order_total_price_des), totalPrice);
        tvSellerTotalPrice.setText(Html.fromHtml(totalPriceDes));


        //收货人信息
        Consignee consignee = bean.getConsignee();
        if (consignee != null) {
            llSellerReceiptInfo.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(consignee.getConsigneeName()) && !TextUtils.isEmpty(consignee.getConsigneePhone())) {
                llSellerReceiptName.setVisibility(View.VISIBLE);
                String consigneeName = consignee.getConsigneeName() + " " + consignee.getConsigneePhone();
                tvSellerReceiptName.setText(consigneeName);
            } else {
                llSellerReceiptName.setVisibility(View.GONE);
            }

            Area area = consignee.getArea();
            String address = "";
            if (area != null) {
                String province = area.getProvince();
                if (!TextUtils.isEmpty(province)) {
                    address = address + province;
                }

                String city = area.getCity();
                if (!TextUtils.isEmpty(city)) {
                    address = address + city;
                }

                String district = area.getArea();
                if (!TextUtils.isEmpty(district)) {
                    address = address + district;
                }
            }
            String consigneeAddress = consignee.getConsigneeAddress();
            if (!TextUtils.isEmpty(consigneeAddress)) {
                address = address + consigneeAddress;
            }
            if (!TextUtils.isEmpty(address)) {
                llSellerReceiptAddress.setVisibility(View.VISIBLE);
                tvSellerReceiptAddress.setText(address);
            } else {
                llSellerReceiptAddress.setVisibility(View.GONE);
            }
        }

        //订单信息
        sellerOrderInformationView.setData(orderInfo);
    }


    /**
     * 获取物流信息
     *
     * @param
     */
    private void doRequestForSellerExpress(long orderId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", String.valueOf(orderId));
        OkHttpClientManager.postAsyn(MyConstants.GETEXPRESSINFO, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            Express express = ParserUtils.parserOrderExpressData(baseResult.obj);
                            initSellerExpress(express);
                        }
                    } else {
                        CommonUtils.error(baseResult, OrderDetailActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initSellerExpress(final Express express) {
        if (express != null) {
            List<Trace> trances = express.getTraces();
            if (!ArrayUtil.isEmpty(trances)) {
                llSellerExpress.setVisibility(View.VISIBLE);
                Trace trance = trances.get(0);
                if (trance != null) {
                    String acceptStation = trance.getAcceptStation();
                    if (!TextUtils.isEmpty(acceptStation)) {
                        tvSellerExpress.setText(acceptStation);
                    }
                    String acceptTime = trance.getAcceptTime();
                    if (!TextUtils.isEmpty(acceptTime)) {
                        tvSellerExpressTime.setText(acceptTime);
                    }
                }
                llSellerExpress.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderDetailActivity.this, OrderExpressListActivity.class);
                        intent.putExtra(MyConstants.EXTRA_EXPRESS, express);
                        startActivity(intent);
                    }
                });
            } else {
                llSellerExpress.setVisibility(View.GONE);
            }
        } else {
            llSellerExpress.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ORDER_MEMO_EDIT) {
                refresh();
            } else {
                orderDetailBottom.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
