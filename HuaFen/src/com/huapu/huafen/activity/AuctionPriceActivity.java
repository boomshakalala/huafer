package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import butterknife.BindView;

/**
 * Created by mac on 17/8/2.
 */

public class AuctionPriceActivity extends BaseActivity {

    public final static int PRICE_STYLE_MARKET = 1;
    public final static int PRICE_STYLE_BID_DESPOSIT = PRICE_STYLE_MARKET + 1;
    public final static int PRICE_STYLE_BID_INCREMENT = PRICE_STYLE_BID_DESPOSIT + 1;

    @BindView(R.id.blankSpace)
    View blankSpace;
    @BindView(R.id.tvAuctionPrice) TextView tvAuctionPrice;
    @BindView(R.id.etPrice) EditText etPrice;
    @BindView(R.id.tvBtnConfirm) TextView tvBtnConfirm;
    private int priceStyle;
    private int price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auction_price);
        Bundle data = mIntent.getExtras();
        price = data.getInt(MyConstants.PRICE,0);
        priceStyle = data.getInt(MyConstants.PRICE_STYLE,0);

        if(priceStyle == AuctionPriceActivity.PRICE_STYLE_MARKET){
            tvAuctionPrice.setText("起拍价（包邮）");
        }else if(priceStyle == AuctionPriceActivity.PRICE_STYLE_BID_DESPOSIT){
            tvAuctionPrice.setText("保证金");
        }else if(priceStyle == AuctionPriceActivity.PRICE_STYLE_BID_INCREMENT){
            tvAuctionPrice.setText("加价幅度");
        }

        if(price!=0){
            etPrice.setText(String.valueOf(price));
        }

        tvBtnConfirm.setOnClickListener(this);
        blankSpace.setOnClickListener(this);


    }

    public static void hStartActivityForResult(Activity activity, Bundle data, int requestCode) {
        Intent intent = new Intent(activity, AuctionPriceActivity.class);
        intent.putExtras(data);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v == tvBtnConfirm){
            String priceDes = etPrice.getText().toString().trim();
            if(TextUtils.isEmpty(priceDes)){
                toast("请填写金额");
                return;
            }


            try {
                int price = Integer.parseInt(priceDes);
                if(price == 0){
                    if(priceStyle == AuctionPriceActivity.PRICE_STYLE_MARKET){
                        toast("起拍价最少为一元");
                    }else if(priceStyle == AuctionPriceActivity.PRICE_STYLE_BID_DESPOSIT){
                        toast("保证金最少为一元");
                    }else if(priceStyle == AuctionPriceActivity.PRICE_STYLE_BID_INCREMENT){
                        toast("加价幅度最少为一元");
                    }
                    return;
                }

                if(price > 1000000){
                    if(priceStyle == AuctionPriceActivity.PRICE_STYLE_MARKET){
                        toast("起拍价不能大于100万");
                    }else if(priceStyle == AuctionPriceActivity.PRICE_STYLE_BID_DESPOSIT){
                        toast("保证金不能大于100万");
                    }else if(priceStyle == AuctionPriceActivity.PRICE_STYLE_BID_INCREMENT){
                        toast("加价幅度不能大于100万");
                    }
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(MyConstants.PRICE_STYLE,priceStyle);
                intent.putExtra(MyConstants.PRICE,price);
                setResult(RESULT_OK,intent);
                onBackPressed();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }else if(v == blankSpace) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
