package com.huapu.huafen.chatim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.OrderConfirmActivity;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsUserOrder;
import com.huapu.huafen.beans.OrderConfirmBean;
import com.huapu.huafen.beans.OrderData;
import com.huapu.huafen.chatim.fragment.PrivateConversationFragment;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.OnKeyboardVisibilityTool;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonPriceView;
import com.huapu.huafen.views.DashLineView;
import com.squareup.okhttp.Request;

import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.utils.LCIMConversationUtils;
import cn.leancloud.chatkit.utils.LCIMLogUtils;

/**
 * 私信 聊天 页面
 */
public class PrivateConversationActivity extends BaseActivity {
    private String TAG = "PrivateConversationActivity";

    protected PrivateConversationFragment conversationFragment;

    @BindView(R.id.tvBtnSend)
    TextView tvBtnSend;
    @BindView(R.id.dlvGoodsName)
    DashLineView dlvGoodsName;
    @BindView(R.id.cpvPrice)
    CommonPriceView cpvPrice;
    @BindView(R.id.tvIsFreeDelivery)
    TextView tvIsFreeDelivery;
    @BindView(R.id.ivGoodsPic)
    SimpleDraweeView ivGoodsPic;
    @BindView(R.id.layoutGoods)
    View layoutGoods;
    @BindView(R.id.viewLine)
    View viewLine;
    @BindView(R.id.tvBtnToDes)
    TextView tvBtnToDes;
    @BindView(R.id.llBtnToDes)
    View llBtnToDes;
    @BindView(R.id.tvSellPrice)
    TextView tvSellPrice;

    //	private GoodsInfo goods;
    private boolean isRequestToServer = true;
    private Handler mHandler;
    private Handler mWorkHandler;
    private GoodsUserOrder goods;
    private String goodsId;
    private String targetId;
    private String title;
    private STATE state = STATE.NONE;
    private String priceState = "成交价";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);
        ButterKnife.bind(this);

        conversationFragment = (PrivateConversationFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_chat);

        initByIntent(getIntent());

        getTitleBar().setRightText("温馨提示", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateConversationActivity.this, WebViewActivity.class);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.CHAT_TIPS);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "温馨提示");
                startActivity(intent);
            }
        }).getDivider().setVisibility(View.GONE);

        // 键盘弹出监听
        OnKeyboardVisibilityTool.setKeyboardListener(this,
                new OnKeyboardVisibilityTool.OnKeyboardVisibilityListener() {
                    public void onVisibilityChanged(boolean visible) {
                        if (visible)
                            scrollToBottom();
                    }
                });
    }

    // 滑动到底部
    private void scrollToBottom() {
        conversationFragment.scrollToBottom();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CommonPreference.isLogin())
            finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initByIntent(intent);
    }

    private void initByIntent(Intent intent) {
        if (null == LCChatKit.getInstance().getClient()) {
            ToastUtil.toast(this, "请登录后重试");
            finish();
            return;
        }
        Bundle extras = intent.getExtras();
        String errorMsg = "必须指定用户或会话之一";
        if (extras == null) {
            ToastUtil.toast(this, errorMsg);
            finish();
            return;
        }
        if (extras.containsKey(MyConstants.IM_PEER_ID)) {
            String peerId = extras.getString(MyConstants.IM_PEER_ID);
            getConversation(peerId);
            this.targetId = peerId;
        } else if (extras.containsKey(MyConstants.IM_CONV_ID)) {
            String convId = extras.getString(MyConstants.IM_CONV_ID);
            AVIMConversation conv = LCChatKit.getInstance().getClient().getConversation(convId);
            for (String s : conv.getMembers()) {
                if (!s.equals(CommonPreference.getUserId() + "")) {
                    this.targetId = s;
                    break;
                }
            }
            updateConversation(conv);
        } else {
            ToastUtil.toast(this, errorMsg);
            finish();
        }
        if (extras.containsKey(MyConstants.EXTRA_GOODS_DETAIL_ID)) {
            this.goodsId = extras.getString(MyConstants.EXTRA_GOODS_DETAIL_ID);
        }
        startRequestForGetGoods();
        if (title != null) {
            getTitleBar().setTitle(title);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 主动刷新 UI
     *
     * @param conversation
     */
    protected void updateConversation(AVIMConversation conversation) {
        LogUtil.i(TAG, "updateConversation ", conversation);
        if (null != conversation) {
            conversationFragment.setConversation(conversation);
            LCIMConversationItemCache.getInstance().insertConversation(conversation.getConversationId());
            LCIMConversationUtils.getConversationName(conversation, new AVCallback<String>() {
                @Override
                protected void internalDone0(String s, AVException e) {
                    if (null != e) {
                        LCIMLogUtils.logException(e);
                    } else {
                        getTitleBar().setTitle(s);
                    }
                }
            });
        }
    }

    /**
     * 获取 conversation
     * 为了避免重复的创建，createConversation 参数 isUnique 设为 true·
     */
    protected void getConversation(final String memberId) {
        LCChatKit.getInstance().getClient().createConversation(
                Collections.singletonList(memberId), "", null, false, true, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        if (null != e) {
                            ToastUtil.toast(PrivateConversationActivity.this, e.getMessage());
                        } else {
                            updateConversation(avimConversation);
                        }
                    }
                });
    }


    private void startRequestForGetGoods() {
        if (!CommonUtils.isNetAvaliable(this)) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("targetId", targetId);
        if (goodsId != null) {
            setGoods();
            params.put("goodsId", goodsId);
        }
        OkHttpClientManager.postAsyn(MyConstants.GETGOODS, params, new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(e);
                layoutGoods.setVisibility(View.GONE);
                viewLine.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response) {
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            GoodsUserOrder goods = JSON.parseObject(baseResult.obj, GoodsUserOrder.class);
                            initGoodsData(goods);
                        }
                    } else {
                        layoutGoods.setVisibility(View.GONE);
                        viewLine.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setGoods() {
        if (!CommonUtils.isNetAvaliable(this)) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("targetId", targetId);
        if (goodsId != null) {
            params.put("goodsId", goodsId);
        }
        OkHttpClientManager.postAsyn(MyConstants.SETGOODS, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(e);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, "setGoods suc!");
            }
        });
    }

    private void initGoodsData(GoodsUserOrder goods) {
        if (goods != null && goods.getGoodsData() != null) {
            this.goods = goods;
            layoutGoods.setVisibility(View.VISIBLE);
            viewLine.setVisibility(View.VISIBLE);
            dlvGoodsName.setData(goods.getGoodsData().getBrand(), goods.getGoodsData().getName());
            String url = (String) ivGoodsPic.getTag();
            tvBtnSend.setTextSize(11);
            if (TextUtils.isEmpty(url) || ArrayUtil.isEmpty(goods.getGoodsData().getGoodsImgs()) || !url.equals(goods.getGoodsData().getGoodsImgs().get(0))) {
                ivGoodsPic.setTag(goods.getGoodsData().getGoodsImgs().get(0));
                ImageLoader.resizeSmall(ivGoodsPic, goods.getGoodsData().getGoodsImgs().get(0), 1);
            }
            int goodsState = goods.getGoodsData().getGoodsState();
            int auditStatus = goods.getGoodsData().getAuditStatus();
            long sellerId = 0;
            if (goods.getSellerData() != null) {
                sellerId = goods.getSellerData().getUserId();
            }
            long buyerId = 0;
            if (goods.getOrderData() != null) {
                buyerId = goods.getOrderData().getBuyerId();
            }
            boolean isSeller;
            if (sellerId == CommonPreference.getUserId()) {
                isSeller = true;
            } else {
                isSeller = false;
            }
            boolean isBuyer;
            if (buyerId == CommonPreference.getUserId()) {
                isBuyer = true;
            } else {
                isBuyer = false;
            }
            /**
             * 1：在售
             2：预售中
             3：交易中
             4：已售出
             5：下架
             6:被举报
             7:已删除
             */
            // 邮费到付
//			int shippingCost;
            // 邮费到付
            switch (goodsState) {
                case 1:
                    /**
                     * 1：通过上首页、2：通过不上首页、3：轻微拒绝、4：严重拒绝、5：未审核
                     */
                    switch (auditStatus) {
                        case 1:
                            if (isSeller) {
                                tvBtnSend.setVisibility(View.GONE);
                                state = STATE.GOODS_DETAIL;
                            } else {
                                if (goods.getGoodsData().isAuction == 1) {
                                    tvBtnSend.setText("拍卖中");
                                    tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                                    tvBtnSend.setBackgroundResource(0);
                                    tvBtnSend.setVisibility(View.VISIBLE);
                                    state = STATE.GOODS_DETAIL;
                                    cpvPrice.setData(goods.getGoodsData());
                                    setGoodsFreeDelivery(goods.getGoodsData());
                                } else {
                                    tvBtnSend.setText("立即购买");
                                    tvBtnSend.setTextColor(getResources().getColor(R.color.base_pink_light));
                                    tvBtnSend.setBackgroundResource(R.drawable.text_rectangle_transparent_pink_light_stroke_bg);
                                    tvBtnSend.setVisibility(View.VISIBLE);
                                    state = STATE.ORDER_CONFIRM;
                                    tvBtnSend.setTextSize(12);
                                }
                            }
                            break;
                        case 2:
                            if (isSeller) {
                                tvBtnSend.setVisibility(View.GONE);
                                state = STATE.GOODS_DETAIL;
                            } else {
                                if (goods.getGoodsData().isAuction == 1) {
                                    tvBtnSend.setText("拍卖中");
                                    tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                                    tvBtnSend.setBackgroundResource(0);
                                    tvBtnSend.setVisibility(View.VISIBLE);
                                    state = STATE.GOODS_DETAIL;
                                    cpvPrice.setData(goods.getGoodsData());
                                    setGoodsFreeDelivery(goods.getGoodsData());
                                } else {
                                    tvBtnSend.setText("立即购买");
                                    tvBtnSend.setTextColor(getResources().getColor(R.color.base_pink_light));
                                    tvBtnSend.setBackgroundResource(R.drawable.text_rectangle_transparent_pink_light_stroke_bg);
                                    tvBtnSend.setVisibility(View.VISIBLE);
                                    state = STATE.ORDER_CONFIRM;
                                    tvBtnSend.setTextSize(12);
                                }

                            }
                            break;
                        case 3:
                            tvBtnSend.setText("审核未通过");
                            tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                            tvBtnSend.setBackgroundResource(0);
                            tvBtnSend.setVisibility(View.VISIBLE);
                            state = STATE.GOODS_DETAIL;
                            break;
                        case 4:
                            tvBtnSend.setText("审核未通过");
                            tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                            tvBtnSend.setBackgroundResource(0);
                            tvBtnSend.setVisibility(View.VISIBLE);
                            state = STATE.GOODS_DETAIL;
                            break;
                        case 5:
                            tvBtnSend.setText("待审核");
                            tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                            tvBtnSend.setBackgroundResource(0);
                            tvBtnSend.setVisibility(View.VISIBLE);
                            state = STATE.GOODS_DETAIL;
                            break;
                        default:
                            tvBtnSend.setVisibility(View.GONE);
                            state = STATE.GOODS_DETAIL;
                            break;
                    }
                    if (goods.getGoodsData().isAuction == 1) {
                        cpvPrice.setVisibility(View.GONE);
                        tvSellPrice.setVisibility(View.VISIBLE);
                        String des;
                        if (goods.getGoodsData().bidder > 0) {
                            des = String.format(getString(R.string.auction_price), "当前价", goods.getGoodsData().hammerPrice);
                        } else {
                            des = String.format(getString(R.string.auction_price), "起拍价", goods.getGoodsData().hammerPrice);
                        }
                        tvSellPrice.setText(Html.fromHtml(des));
                    } else {
                        cpvPrice.setVisibility(View.VISIBLE);
                        tvSellPrice.setVisibility(View.GONE);
                        cpvPrice.setData(goods.getGoodsData());
                    }

                    setGoodsFreeDelivery(goods.getGoodsData());
                    break;
                case 2:
                    if (goods.getGoodsData().isAuction == 1) {
                        tvBtnSend.setText("还未开拍");
                    } else {
                        tvBtnSend.setText("还未开售");
                    }
                    tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                    tvBtnSend.setBackgroundResource(0);
                    tvBtnSend.setVisibility(View.VISIBLE);
                    state = STATE.GOODS_DETAIL;
                    if (goods.getGoodsData().isAuction == 1) {
                        cpvPrice.setVisibility(View.GONE);
                        tvSellPrice.setVisibility(View.VISIBLE);
                        String des = String.format(getString(R.string.auction_price), "起拍价", goods.getGoodsData().hammerPrice);
                        tvSellPrice.setText(Html.fromHtml(des));
                    } else {
                        cpvPrice.setVisibility(View.VISIBLE);
                        tvSellPrice.setVisibility(View.GONE);
                        cpvPrice.setData(goods.getGoodsData());
                    }
                    setGoodsFreeDelivery(goods.getGoodsData());
                    break;
                case 3:
                    if (isSeller) {
                        tvBtnSend.setText("查看订单");
                        tvBtnSend.setTextColor(getResources().getColor(R.color.base_pink_light));
                        tvBtnSend.setBackgroundResource(R.drawable.text_rectangle_transparent_pink_light_stroke_bg);
                        tvBtnSend.setVisibility(View.VISIBLE);
                        state = STATE.ORDER_DETAIL;
                        if (goods.getOrderData() != null) {
                            cpvPrice.setData(String.valueOf(goods.getOrderData().getPrice()), String.valueOf(goods.getGoodsData().getPastPrice()));
                            setOrderFreeDelivery(goods.getOrderData());
                        }
                        tvBtnSend.setTextSize(12);
                    } else {
                        if (isBuyer) {
                            tvBtnSend.setText("查看订单");
                            tvBtnSend.setTextColor(getResources().getColor(R.color.base_pink_light));
                            tvBtnSend.setBackgroundResource(R.drawable.text_rectangle_transparent_pink_light_stroke_bg);
                            tvBtnSend.setVisibility(View.VISIBLE);
                            state = STATE.ORDER_DETAIL;
                            if (goods.getOrderData() != null) {
                                cpvPrice.setData(String.valueOf(goods.getOrderData().getPrice()), String.valueOf(goods.getGoodsData().getPastPrice()));
                                setOrderFreeDelivery(goods.getOrderData());
                            }
                            tvBtnSend.setTextSize(12);
                        } else {
                            tvBtnSend.setText("已被抢走了");
                            tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                            tvBtnSend.setBackgroundResource(0);
                            tvBtnSend.setVisibility(View.VISIBLE);
                            state = STATE.GOODS_DETAIL;
                            cpvPrice.setData(goods.getGoodsData());
                            setGoodsFreeDelivery(goods.getGoodsData());
                        }
                    }

                    if (goods.getGoodsData().isAuction == 1) {
                        cpvPrice.setVisibility(View.GONE);
                        tvSellPrice.setVisibility(View.VISIBLE);
                        if (goods.getGoodsData().getGoodsState() == 5 && goods.getGoodsData().bidder <= 0) {
                            priceState = "起拍价";
                        }
                        String des = String.format(getString(R.string.auction_price), priceState, goods.getGoodsData().hammerPrice);
                        tvSellPrice.setText(Html.fromHtml(des));
                    } else {
                        cpvPrice.setVisibility(View.VISIBLE);
                        tvSellPrice.setVisibility(View.GONE);
                        if (isSeller || isBuyer) {
                            if (goods.getGoodsData() != null && goods.getOrderData() != null) {
                                cpvPrice.setData(String.valueOf(goods.getOrderData().getPrice()), String.valueOf(goods.getGoodsData().getPastPrice()));
                            }
                        } else {
                            cpvPrice.setData(goods.getGoodsData());
                        }
                    }

                    break;
                case 4:
                    if (isSeller) {
                        tvBtnSend.setText("查看订单");
                        tvBtnSend.setTextColor(getResources().getColor(R.color.base_pink_light));
                        tvBtnSend.setBackgroundResource(R.drawable.text_rectangle_transparent_pink_light_stroke_bg);
                        tvBtnSend.setVisibility(View.VISIBLE);
                        state = STATE.ORDER_DETAIL;
                        if (goods.getOrderData() != null) {
                            cpvPrice.setData(String.valueOf(goods.getOrderData().getPrice()), String.valueOf(goods.getGoodsData().getPastPrice()));
                            setOrderFreeDelivery(goods.getOrderData());
                        }
                        tvBtnSend.setTextSize(12);
                    } else {
                        if (isBuyer) {
                            tvBtnSend.setText("查看订单");
                            tvBtnSend.setTextColor(getResources().getColor(R.color.base_pink_light));
                            tvBtnSend.setBackgroundResource(R.drawable.text_rectangle_transparent_pink_light_stroke_bg);
                            tvBtnSend.setVisibility(View.VISIBLE);
                            state = STATE.ORDER_DETAIL;
                            if (goods.getOrderData() != null) {
                                cpvPrice.setData(String.valueOf(goods.getOrderData().getPrice()), String.valueOf(goods.getGoodsData().getPastPrice()));
                                setOrderFreeDelivery(goods.getOrderData());
                            }
                            tvBtnSend.setTextSize(12);
                        } else {
                            if (goods.getGoodsData().isAuction == 1) {
                                tvBtnSend.setText("拍卖已结束");
                            } else {
                                tvBtnSend.setText("已售出");
                            }
                            tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                            tvBtnSend.setBackgroundResource(0);
                            tvBtnSend.setVisibility(View.VISIBLE);
                            state = STATE.GOODS_DETAIL;
                            cpvPrice.setData(goods.getGoodsData());
                            setGoodsFreeDelivery(goods.getGoodsData());
                        }
                    }

                    if (goods.getGoodsData().isAuction == 1) {
                        cpvPrice.setVisibility(View.GONE);
                        tvSellPrice.setVisibility(View.VISIBLE);
                        if (goods.getGoodsData().getGoodsState() == 5 && goods.getGoodsData().bidder <= 0) {
                            priceState = "起拍价";
                        }
                        String des = String.format(getString(R.string.auction_price), priceState, goods.getGoodsData().hammerPrice);
                        tvSellPrice.setText(Html.fromHtml(des));
                    } else {
                        cpvPrice.setVisibility(View.VISIBLE);
                        tvSellPrice.setVisibility(View.GONE);
                        if (isSeller || isBuyer) {
                            if (goods.getGoodsData() != null && goods.getOrderData() != null) {
                                cpvPrice.setData(String.valueOf(goods.getOrderData().getPrice()), String.valueOf(goods.getGoodsData().getPastPrice()));
                            }
                        } else {
                            cpvPrice.setData(goods.getGoodsData());
                        }
                    }

                    break;
                case 5:
                    if (goods.getGoodsData().isAuction == 1) {
                        tvBtnSend.setText("拍卖已结束");
                    } else {
                        tvBtnSend.setText("已下架");
                    }
                    tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                    tvBtnSend.setBackgroundResource(0);
                    tvBtnSend.setVisibility(View.VISIBLE);
                    state = STATE.GOODS_DETAIL;
                    cpvPrice.setData(goods.getGoodsData());
                    setGoodsFreeDelivery(goods.getGoodsData());
                    if (goods.getGoodsData().isAuction == 1) {
                        cpvPrice.setVisibility(View.GONE);
                        tvSellPrice.setVisibility(View.VISIBLE);
                        String des;
                        if (goods.getGoodsData().getGoodsState() == 5 && goods.getGoodsData().bidder <= 0) {
                            priceState = "起拍价";
                        }
                        des = String.format(getString(R.string.auction_price), priceState, goods.getGoodsData().hammerPrice);
//						if (goods.getGoodsData().bidder > 0) {
//							des = String.format(getString(R.string.auction_price), "成交价", goods.getGoodsData().hammerPrice);
//						} else {
//							des = String.format(getString(R.string.auction_price), "起拍价", goods.getGoodsData().hammerPrice);
//						}
                        tvSellPrice.setText(Html.fromHtml(des));
                    } else {
                        cpvPrice.setVisibility(View.VISIBLE);
                        tvSellPrice.setVisibility(View.GONE);
                        cpvPrice.setData(goods.getGoodsData());
                    }
                    break;
                case 7:
                    tvBtnSend.setText("已删除");
                    tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                    tvBtnSend.setBackgroundResource(0);
                    tvBtnSend.setVisibility(View.VISIBLE);
                    state = STATE.NONE;
                    cpvPrice.setData(goods.getGoodsData());
                    setGoodsFreeDelivery(goods.getGoodsData());
                    break;
                default:
                    tvBtnSend.setText("");
                    tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                    tvBtnSend.setBackgroundResource(0);
                    tvBtnSend.setVisibility(View.VISIBLE);
                    state = STATE.NONE;
                    cpvPrice.setData(goods.getGoodsData());
                    setGoodsFreeDelivery(goods.getGoodsData());
                    break;
            }
        } else {
            layoutGoods.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        }
    }

    /**
     * 设置邮递方式
     */
    private void setGoodsFreeDelivery(GoodsData data) {
        tvIsFreeDelivery.setVisibility(View.VISIBLE);
        //邮费
        boolean isFreeDelivery = data.getIsFreeDelivery();
        int postType = data.postType;
        if (isFreeDelivery) {
            tvIsFreeDelivery.setText("包邮");
            tvIsFreeDelivery.setVisibility(View.VISIBLE);
        } else {
            tvIsFreeDelivery.setVisibility(View.VISIBLE);
            if (postType == 4) {//邮费到付
                tvIsFreeDelivery.setText("邮费到付");
            } else {
                if (data.getPostage() != 0) {
                    CommonUtils.setPriceSizeData(tvIsFreeDelivery, "邮费", data.getPostage());
                } else {
                    tvIsFreeDelivery.setText("运费待议");
                }
            }
        }
    }

    /**
     * 设置订单邮递
     */
    private void setOrderFreeDelivery(OrderData data) {
        tvIsFreeDelivery.setVisibility(View.VISIBLE);
        int shipType = data.getShipType();
        int postage = data.getPostage();
        String postageDes = null;
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

        tvIsFreeDelivery.setText(postageDes);

    }

    /**
     * 购买商品
     */
    private void startRequestForOrderBuy(String goodsId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        if (TextUtils.isEmpty(goodsId)) {
            return;
        }
        tvBtnSend.setOnClickListener(null);
        ProgressDialog.showProgress(PrivateConversationActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("goodsId", goodsId);
        OkHttpClientManager.postAsyn(MyConstants.ORDERBUY, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtil.e(e);
                        ProgressDialog.closeProgress();
                        tvBtnSend.setOnClickListener(PrivateConversationActivity.this);
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        tvBtnSend.setOnClickListener(PrivateConversationActivity.this);
                        LogUtil.i("liang", "购买商品:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
                                    OrderConfirmBean bean = ParserUtils.parserOrderBuyData(baseResult.obj);
                                    if (bean != null) {
                                        Intent intent = new Intent(PrivateConversationActivity.this, OrderConfirmActivity.class);
                                        intent.putExtra(MyConstants.EXTRA_ORDER_CONFIRM_BEAN, bean);
                                        startActivity(intent);
                                    }
                                }
                            } else if (baseResult.code == ParserUtils.RESPONSE_GOODS_DETAILS_IS_SELL) {
                                tvBtnSend.setText("已被抢走了");
                                state = STATE.GOODS_DETAIL;
                                tvBtnSend.setTextColor(getResources().getColor(R.color.base_btn_normal));
                                tvBtnSend.setBackgroundResource(0);
                            } else {
                                CommonUtils.error(baseResult, PrivateConversationActivity.this, "");
                            }
                        } catch (Exception e) {
                            LogUtil.e(e);
                        }

                    }
                });
    }

    @Override
    @OnClick({R.id.tvBtnSend, R.id.layoutGoods, R.id.llBtnToDes})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tvBtnSend:
//			mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    mWorkHandler.post(new sendImageMessage(""));
//                }
//            });
                if (goods == null) {
                    return;
                }
                switch (state) {
                    case NONE:
                        break;

                    case GOODS_DETAIL:
                        if (goods.getGoodsData() == null) {
                            return;
                        }
                        intent = new Intent(this, GoodsDetailsActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goods.getGoodsData().getGoodsId()));
                        startActivity(intent);
                        break;

                    case ORDER_DETAIL:
                        if (goods.getOrderData() == null) {
                            return;
                        }
                        intent = new Intent(this, OrderDetailActivity.class);
                        intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, Long.valueOf(goods.getOrderData().getOrderId()));
                        startActivity(intent);
                        break;

                    case ORDER_CONFIRM:
                        if (goods.getGoodsData() == null) {
                            return;
                        }
                        startRequestForOrderBuy(String.valueOf(goods.getGoodsData().getGoodsId()));
                        break;
                }
                break;

            case R.id.layoutGoods:
                if (goods == null || goods.getGoodsData() == null) {
                    return;
                }
                intent = new Intent(this, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goods.getGoodsData().getGoodsId()));
                startActivity(intent);
                break;
            case R.id.llBtnToDes:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.WEB_VIEW_CONVERSATION_NOTICE);
                startActivity(intent);
                break;
        }
    }

    /**
     * 1. 商品详情
     * 2. 订单详情
     * 3. 确认订单
     */
    public enum STATE {
        GOODS_DETAIL,
        ORDER_DETAIL,
        ORDER_CONFIRM,
        NONE;
    }

}