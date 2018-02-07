package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/10/9.
 */

public class RangeFilterMenuItem extends LinearLayout {

    @BindView(R.id.etPriceLow)
    EditText low;
    @BindView(R.id.etPriceHigh)
    EditText high;
    @BindView(R.id.priceText)
    TextView tag;

    public RangeFilterMenuItem(@NonNull Context context) {
        this(context, null);
    }

    public RangeFilterMenuItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.range_filter_menu_item, this, true);
        ButterKnife.bind(this);
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams();
//        lp.setMargins(0, CommonUtils.dp2px(10), 0, 0);

    }


    public String getLowPrice() {
        return low.getText().toString();
    }

    public String getHighPrice() {
        return high.getText().toString();
    }

    public void clear() {
        low.setText("");
        high.setText("");
    }


    public void setTitle(String title) {
        tag.setText(title);
    }


}