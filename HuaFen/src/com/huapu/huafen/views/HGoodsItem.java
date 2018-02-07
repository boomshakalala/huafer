package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/3/13.
 */

public class HGoodsItem  extends LinearLayout{

    @BindView(R2.id.itemName)
    TextView itemName;
    @BindView(R2.id.rlItemValue)
    RelativeLayout rlItemValue;
    @BindView(R2.id.itemDesc)
    TextView itemDesc;
    @BindView(R2.id.ivArrow)
    ImageView ivArrow;

    public HGoodsItem(Context context) {
        this(context,null);
    }

    public HGoodsItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.h_goods_item,this,true);
        ButterKnife.bind(this);
    }

    public void setItemName(String itemName) {
        this.itemName.setText(itemName);
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc.setText(itemDesc);
    }

    public void setItemDescColor(int colorRes){
        this.itemDesc.setTextColor(colorRes);
    }

    public void setOnValueClickListener(OnClickListener onClickListener){
        this.rlItemValue.setOnClickListener(onClickListener);
    }

    public void setArrowVisible(boolean visible){
        this.ivArrow.setVisibility(visible? View.VISIBLE:View.GONE);
    }
}
