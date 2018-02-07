package com.huapu.huafen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qwe on 2017/8/2.
 */

public class OfferPriceDialog extends Dialog {

    @BindView(R.id.decPrice)
    TextView decPrice;
    @BindView(R.id.inputPrice)
    EditText inputPrice;
    @BindView(R.id.addPrice)
    TextView addPrice;
    @BindView(R.id.confirmOffer)
    TextView confirmOffer;
    @BindView(R.id.currentPrice)
    TextView currentPrice;

    private int initPrice;

    private int currentPriceNumber;

    private long goodsId;

    private int increasingPrice;
    private OnRefreshListener onRefreshListener;

    public OfferPriceDialog(@NonNull Context context) {
        super(context, R.style.photo_dialog);
    }

    public OfferPriceDialog(@NonNull Context context, int initPrice, int increasingPrice, long goodsId) {
        super(context, R.style.photo_dialog);
        this.initPrice = initPrice;
        this.currentPriceNumber = initPrice;
        this.goodsId = goodsId;
        this.increasingPrice = increasingPrice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_offerprice);
        ButterKnife.bind(this);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.DialogBottomTransScaleStyle);

        decPrice.setText("-"+increasingPrice);
        addPrice.setText("+" + increasingPrice);
        initData();
    }

    private void initData() {
        currentPrice.setText("当前价 ¥" + initPrice);
        inputPrice.setText("¥" + currentPriceNumber);
    }

    @OnClick({R.id.decPrice, R.id.inputPrice, R.id.addPrice, R.id.confirmOffer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.decPrice:
                currentPriceNumber -= increasingPrice;

                if (currentPriceNumber <= initPrice) {
                    decPrice.setBackgroundColor(getContext().getResources().getColor(R.color.share_dialog_bg));
                    decPrice.setEnabled(false);
                }
                initData();
                break;
            case R.id.inputPrice:

                break;
            case R.id.addPrice:
                currentPriceNumber += increasingPrice;
                if (currentPriceNumber > initPrice) {
                    decPrice.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink_light));
                    decPrice.setEnabled(true);
                }
                initData();
                break;
            case R.id.confirmOffer:
                try {
                    String price = inputPrice.getText().toString();
                    if (!price.contains("¥")) {
                        inputPrice.setText("¥" + price);
                    }
                    if (price.contains("¥")) {
                        price = price.substring(1);
                    }
                    Logger.e("get price:" + price);

                    if (Integer.valueOf(price) < initPrice) {
                        ToastUtil.toast(getContext(), "您的出价须大于当前价格");
                        return;
                    }

//                    if ((Integer.valueOf(price) - initPrice) % 10 != 0) {
//                        ToastUtil.toast(getContext(), "您的出价须是当前价格加上整数个加价幅度");
//                        return;
//                    }
                    currentPriceNumber = Integer.valueOf(price);
                    confirmOfferPrice();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void confirmOfferPrice() {
        final ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("price", String.valueOf(currentPriceNumber));
        arrayMap.put("goodsId", String.valueOf(goodsId));
        OkHttpClientManager.postAsyn(MyConstants.CONFIRM_OFFER_PRICE, arrayMap, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                Logger.e("get response:" + e.getMessage());
                ToastUtil.toast(getContext(), "发生错误");
                OfferPriceDialog.this.dismiss();
            }

            @Override
            public void onResponse(String response) {
                Logger.e("get response:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            ToastUtil.toast(getContext(), "出价成功");
                            if(onRefreshListener!=null){
                                onRefreshListener.onRefresh();
                            }
                        }
                    } else {
                        ToastUtil.toast(getContext(), baseResult.msg);
                    }
                    OfferPriceDialog.this.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
