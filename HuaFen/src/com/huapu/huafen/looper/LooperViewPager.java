package com.huapu.huafen.looper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by admin on 2016/12/12.
 */
public class LooperViewPager extends ViewPager{
    private OnPageChangeListener listener;
    private LoopPagerAdapter adapter;

    public LooperViewPager(Context context) {
        super(context);
        init();
    }

    public LooperViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.setOnPageChangeListener(onPageChangeListener);
    }


    public void setAdapter(PagerAdapter adapter, int limit) {
        this.setOffscreenPageLimit(limit);
        if (adapter instanceof LoopPagerAdapter) {
            this.adapter = (LoopPagerAdapter) adapter;
            super.setAdapter(adapter);
            if (this.adapter.loopAble() && adapter.getCount() > 1) {
                setCurrentItem(1);
            }
        } else {
            throw new RuntimeException("must set a QLoopPagerAdapter");
        }
    }

    OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // 这段神奇的代码可以解决viewpager嵌套再scrollview中滑动异常的状况
            getParent().requestDisallowInterceptTouchEvent(true);
            if (listener != null) {
                listener.onPageScrolled(getRealPosition(position), positionOffset, positionOffsetPixels);
            }
        }


        @Override
        public void onPageSelected(int position) {
            if (listener != null) {
                listener.onPageSelected(getRealPosition(position));
            }
        }


        @Override
        public void onPageScrollStateChanged(int state) {
            if (listener != null) {
                listener.onPageScrollStateChanged(state);
            }
            if (adapter == null || !adapter.loopAble()) {
                return;
            }
            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    if (getCurrentItem() == 0) {
                        //滑动到过度尾页的时候，切换到尾页
                        setCurrentItem(adapter.getCount() - 2, false);
                    }
                    if (getCurrentItem() == adapter.getCount() - 1) {
                        //滑到过度首页的时候，切换到首页
                        setCurrentItem(1, false);
                    }
                    break;
            }
        }

    };

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.listener = listener;
    }


    public int getRealPosition(int position) {
        if (adapter != null && adapter.loopAble() && adapter.getCount() > 1) {
            if (position == 0) {//添加的第一个，return 真实数据的最后一个
                return adapter.getCount() - 2;
            } else if (position == adapter.getCount() - 1) {//添加的最后一个，return 真实数据的第一个
                return 0;
            } else {//中间数据，return position-1
                return position - 1;
            }
        } else {//不能滑动，0和1的时候返回真实position
            return position;
        }
    }

    /**
     * use getRealPosition Instead of getCurrentItem
     */
    @Override
    @Deprecated
    public int getCurrentItem() {
        return super.getCurrentItem();
    }

    public int getRealPosition() {
        int currentPoi = super.getCurrentItem();
        if (adapter != null) {
            return adapter.getRealPosition(currentPoi);
        } else {
            return currentPoi;
        }
    }
}
