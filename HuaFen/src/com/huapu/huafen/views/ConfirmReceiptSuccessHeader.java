package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.OrderInfo;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.chatim.activity.PrivateConversationActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.RenderScriptBlur;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/7/25.
 */
public class ConfirmReceiptSuccessHeader extends LinearLayout {

    @BindView(R.id.orderTopBg)
    SimpleDraweeView orderTopBg;
    @BindView(R.id.llConfirm)
    LinearLayout llConfirm;
    @BindView(R.id.goodsTopSpace)
    View goodsTopSpace;
    @BindView(R.id.goodsImg)
    SimpleDraweeView goodsImg;
    @BindView(R.id.tvGoodsNameAndBrand)
    TextView tvGoodsNameAndBrand;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.rlGoods)
    RelativeLayout rlGoods;
    @BindView(R.id.tvGuarantee)
    TextView tvGuarantee;
    @BindView(R.id.tvTotalPrice)
    TextView tvTotalPrice;
    @BindView(R.id.rlGuarantee)
    RelativeLayout rlGuarantee;
    @BindView(R.id.orderInformationView)
    OrderInformationView orderInformationView;
    @BindView(R.id.avatar)
    SimpleDraweeView avatar;
    @BindView(R.id.ctvName)
    CommonTitleView ctvName;
    @BindView(R.id.tvLink)
    TextView tvLink;
    @BindView(R.id.rlInfo)
    RelativeLayout rlInfo;
    @BindView(R.id.tvOrderStateDes)
    TextView tvOrderStateDes;
    @BindView(R.id.tvOrderStateUnderTitle)
    TextView tvOrderStateUnderTitle;

    public ConfirmReceiptSuccessHeader(Context context) {
        this(context, null);
    }

    public ConfirmReceiptSuccessHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.confirm_receipt_success_header, this, true);
        ButterKnife.bind(this);
    }

    public void setData(OrderDetailBean bean) {
        final UserInfo userInfo = bean.getUserInfo();
        OrderInfo orderInfo = bean.getOrderInfo();
        final GoodsInfo goodsInfo = bean.getGoodsInfo();
        if (userInfo != null) {
            String avatar = userInfo.getUserIcon();
            //卖家头像
            this.avatar.setImageURI(avatar);
            this.avatar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, userInfo.getUserId());
                    getContext().startActivity(intent);
                }
            });

            //卖家名字
            ctvName.setData(userInfo);

            //联系卖家
            tvLink.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (CommonPreference.isLogin()) {

                        // 启动会话界面
                        Intent intent = new Intent(getContext(), PrivateConversationActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(String.valueOf(goodsInfo.getGoodsId())));
                        intent.putExtra(MyConstants.IM_PEER_ID, String.valueOf(userInfo.getUserId()));
                        getContext().startActivity(intent);
                    } else {
                        ActionUtil.loginAndToast(getContext());
                    }
                }
            });
        }

        //商品信息
        //商品图片
        ArrayList<String> list = goodsInfo.getGoodsImgs();
        if (!ArrayUtil.isEmpty(list)) {
            String goodsUrl = list.get(0);
            goodsImg.setImageURI(goodsUrl);

            ImageLoader.loadBitmap(getContext(), Uri.parse(goodsUrl), new BitmapCallback() {
                @Override
                public void onBitmapDownloaded(Bitmap bitmap) {
                    if (bitmap == null)
                        return;
                    RenderScriptBlur renderScriptBlur = new RenderScriptBlur(getContext());
                    final Bitmap bmp = renderScriptBlur.blur(20, bitmap);
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            orderTopBg.setImageBitmap(bmp);
                        }
                    });
                }
            });
        }

        //品牌和名称
        String brand = goodsInfo.getGoodsBrand();
        String goodsName = goodsInfo.getGoodsName();
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

        tvGoodsNameAndBrand.setText(goodsNameDesc);

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

        String orderStatusTitle = orderInfo.getOrderStateTitle();
        if (!TextUtils.isEmpty(orderStatusTitle)) {
            tvOrderStateDes.setVisibility(VISIBLE);
            tvOrderStateDes.setText(orderStatusTitle);
        } else {
            tvOrderStateDes.setVisibility(GONE);
        }

        String priceDes = String.format(getContext().getString(R.string.order_price_des), price, postageDes);
        tvPrice.setText(Html.fromHtml(priceDes));

        rlGoods.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goodsInfo.getGoodsId() + "");
                getContext().startActivity(intent);
            }
        });


        //支付总价
        int totalPrice = orderInfo.getOrderPrice() + orderInfo.getOrderPostage();
        String totalPriceDes = String.format(getContext().getString(R.string.order_total_price_des), totalPrice);
        tvTotalPrice.setText(Html.fromHtml(totalPriceDes));

        //订单信息
        orderInformationView.setData(orderInfo);

    }

}
