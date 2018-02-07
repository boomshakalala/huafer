package com.huapu.huafen.views.component;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.CommonPriceView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商品Item
 * Created by dengbin on 18/1/9.
 */
public class ViewGoods extends LinearLayout {

    @BindView(R.id.good_iv)
    SimpleDraweeView goodIv;
    @BindView(R.id.good_tv)
    TextView goodTv;
    @BindView(R.id.price_view)
    CommonPriceView priceView;
    private GoodsData mGoods;

    private int type;

    public ViewGoods(Context context, int type) {
        super(context);
        this.type = type;
        initView(context, null);
    }

    public ViewGoods(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ViewGoods(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        View.inflate(context, R.layout.item_goods_new, this);
        ButterKnife.bind(this, this);

        priceView.setPriceUnitSizeSmall();

        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.common_attrs);
            // 0：一排3个；1：小
            type = mTypedArray.getInt(R.styleable.common_attrs_size_model, 0);
        }

        setPara();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoods == null)
                    return;
                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, mGoods.getGoodsId());
            }
        });
    }

    private void setPara() {

        int w = 0;

        if (type == 0) {   // 一排3个
            w = CommonUtils.getScreenWidth();
            int t = ViewUtil.getTenDp();
            w = (w - (t * 4)) / 3;

            goodIv.getLayoutParams().width = w;
            goodIv.getLayoutParams().height = w;
        } else if (type == 1) {    // 小号
            int t = ViewUtil.getTenDp();
            w = t * 6;


            goodIv.getLayoutParams().width = w;
            goodIv.getLayoutParams().height = w;
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
    }

    public void setData(GoodsData data) {
        mGoods = data;
        List<String> images = data.getGoodsImgs();
        ViewUtil.setImgMiddle(goodIv, images != null && images.size() > 0 ? images.get(0) : null, 1);

        goodTv.setText(data.getBrand() + "\n" + data.getName());

        priceView.setData(data);
    }
}
