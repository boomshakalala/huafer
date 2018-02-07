package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.IconFilter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/9/26.
 */

public class FeatureItemView extends LinearLayout {

    @BindView(R.id.itemimage)
    SimpleDraweeView itemimag;

    @BindView(R.id.itemprice)
    TextView price;

    @BindView(R.id.contianer)
    FrameLayout contianer;

    private OnItemClickListener listener;

    public FeatureItemView(@NonNull Context context) {
        this(context, null);
    }

    public FeatureItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.item_feature_picture, this, true);
        ButterKnife.bind(this);

    }

    public void setData(IconFilter data) {
        getItemimag().setImageURI(data.getImageUrl());
        getPrice().setText(data.getNameDisplay());
    }

    public SimpleDraweeView getItemimag() {
        return itemimag;
    }

    public void setItemimag(SimpleDraweeView itemimag) {
        this.itemimag = itemimag;
    }

    public TextView getPrice() {
        return price;
    }

    public void setPrice(TextView price) {
        this.price = price;
    }

    public FrameLayout getContianer() {
        return contianer;
    }

    public void setContianer(FrameLayout contianer) {
        this.contianer = contianer;
    }


    public interface OnItemClickListener {
        void onItemClick(IconFilter fi);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}