package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/20.
 */

public class BrandCheckHeader extends RelativeLayout{

    @BindView(R2.id.tvInputBrand)
    TextView tvInputBrand;
    @BindView(R2.id.tvAddBrand)
    TextView tvAddBrand;


    public BrandCheckHeader(Context context) {
        this(context,null);
    }

    public BrandCheckHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.brand_check_header,this,true);
        ButterKnife.bind(this);
    }


}
