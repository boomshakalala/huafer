package com.huapu.huafen.views.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.pages.ShowcaseBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 可分页左右滑动商品列表
 * Created by dengbin on 18/1/10.
 */
public class ViewPagerCom extends LinearLayout {

    @BindView(R.id.item_title)
    ViewItemTitle itemTitleView;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.indicator_view)
    ViewIndicator indicatorView;

    private Context context;

    public ViewPagerCom(Context context) {
        super(context);
        this.context = context;

        initView();
    }

    public ViewPagerCom(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        View.inflate(context, R.layout.view_pager_com, this);
        ButterKnife.bind(this, this);
    }

    public void setData(ShowcaseBean data) {

    }

}
