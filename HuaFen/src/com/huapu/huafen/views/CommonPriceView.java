package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.LikeListBean;
import com.huapu.huafen.beans.ShopArticleData;
import com.huapu.huafen.utils.CommonUtils;

/**
 * Created by admin on 2016/9/22.
 */
public class CommonPriceView extends RelativeLayout {
    private TextView tvPrice;
    private TextView tvPastPrice;

    public CommonPriceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CommonPriceView(Context context) {
        super(context, null);
        initView();
    }

    public void setData(String price, String pastPrice) {
        if (!TextUtils.isEmpty(price) && !TextUtils.isEmpty(pastPrice)) {
            tvPrice.setText(price);
        }

        if (!TextUtils.isEmpty(pastPrice)) {
            try {
                int pp = Integer.valueOf(pastPrice);
                if (pp < 0) {
                    tvPastPrice.setVisibility(GONE);
                } else {
                    tvPastPrice.setVisibility(VISIBLE);
                    CommonUtils.setPriceSizeData(tvPastPrice, "", Integer.valueOf(pastPrice));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

    }

    public void setData(GoodsData info) {
        if (info == null) {
            return;
        }
        tvPrice.setText(String.valueOf(info.getPrice()));
        if (info.getPastPrice() < 0) {
            tvPastPrice.setVisibility(GONE);
        } else {
            tvPastPrice.setVisibility(VISIBLE);
            CommonUtils.setPriceSizeData(tvPastPrice, "", info.getPastPrice());
        }

    }

    public void setData(ArticleAndGoods info) {
        if (info == null) {
            return;
        }
        tvPrice.setText(String.valueOf(info.price));
        if (info.pastPrice < 0) {
            tvPastPrice.setVisibility(GONE);
        } else {
            tvPastPrice.setVisibility(VISIBLE);
            CommonUtils.setPriceSizeData(tvPastPrice, "", info.pastPrice);
        }

    }


    public void setData(ShopArticleData info) {
        if (info == null) {
            return;
        }
        tvPrice.setText(String.valueOf(info.getItem().getPrice()));
        if (info.getItem().getPastPrice() < 0) {
            tvPastPrice.setVisibility(GONE);
        } else {
            tvPastPrice.setVisibility(VISIBLE);
            CommonUtils.setPriceSizeData(tvPastPrice, "", info.getItem().getPastPrice());
        }

    }


    public void setData(LikeListBean.ListInfo.ItemInfo info) {
        if (info == null) {
            return;
        }
        tvPrice.setText(String.valueOf(info.getPrice()));
        if (info.getPastPrice() < 0) {
            tvPastPrice.setVisibility(GONE);
        } else {
            tvPastPrice.setVisibility(VISIBLE);
            CommonUtils.setPriceSizeData(tvPastPrice, "", info.getPastPrice());
        }

    }


    public void setData(GoodsInfo info) {
        if (info == null) {
            return;
        }
        tvPrice.setText(String.valueOf(info.getPrice()));
        if (info.getPastPrice() < 0) {
            tvPastPrice.setVisibility(GONE);
        } else {
            tvPastPrice.setVisibility(VISIBLE);
            CommonUtils.setPriceSizeData(tvPastPrice, "", info.getPastPrice());
        }

    }

    // 人民币符号大小
    public void setPriceUnitSizeSmall() {
        TextView tvPriceUnit = (TextView) findViewById(R.id.price_unit_tv);
        tvPriceUnit.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_small));
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.common_price_layout, this);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvPastPrice = (TextView) findViewById(R.id.tvPastPrice);
        tvPastPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

}
