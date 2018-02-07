package com.huapu.huafen.views.component;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.huapu.huafen.beans.GoodsData;

import java.util.List;

/**
 * 可分页左右滑动商品列表
 * Created by dengbin on 18/1/10.
 */
public class GoodsViewPager extends ViewPager {

    private Context context;
    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public GoodsViewPager(Context context) {
        super(context);
        this.context = context;

        initView();
    }

    public GoodsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initView();

        MAdapter adapter = new MAdapter();
        setAdapter(adapter);
    }

    private void initView() {
        addOnPageChangeListener(onPageChangeListener);
    }

    public void setData(List<GoodsData> data) {

    }

    private class MAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }
    }
}
