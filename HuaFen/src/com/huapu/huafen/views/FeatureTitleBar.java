package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.IconFilter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/9/26.
 */

public class FeatureTitleBar extends LinearLayout {


    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.subTitle)
    TextView subTitle;
    @BindView(R.id.navigationtitle)
    TextView navigationtitle;
    @BindView(R.id.indicator)
    ImageView indicator;


    private OnItemClickListener listener;

    public FeatureTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public FeatureTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.title_head_for_list, this, true);
        ButterKnife.bind(this);

    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(String tvTitle) {
        this.tvTitle.setText(tvTitle);
    }

    public TextView getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle.setText(subTitle);
    }

    public TextView getNavigationtitle() {
        return navigationtitle;
    }

    public void setNavigationtitle(String navigationtitle) {
        this.navigationtitle.setText(navigationtitle);
    }

    public void hideMore() {
        navigationtitle.setVisibility(GONE);
        indicator.setVisibility(GONE);

    }

    public ImageView getIndicator() {
        return indicator;
    }

    public void setIndicator(int resid) {
        this.indicator.setImageResource(resid);
    }


    public interface OnItemClickListener {
        void onItemClick(IconFilter fi);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}