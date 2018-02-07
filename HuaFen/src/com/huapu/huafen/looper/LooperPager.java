package com.huapu.huafen.looper;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.fragment.BannerFragment;
import com.huapu.huafen.fragment.VideoPlayerFragment;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by admin on 2016/12/12.
 */
public class LooperPager extends FrameLayout {

    private static final long BANNER_SPEED = 3000;

    private LooperViewPager viewPager;

    private IndicatorView indicatorView;

    private static final Handler mHandler = new Handler();


    private ArrayList<VBanner> saveBanners;
    FragmentManager saveFm;


    private boolean doRun;
    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager == null) {
                return;
            }
            if (doRun) {
                if (viewPager == null) {
                    return;
                }
                if (viewPager.getAdapter().getCount() > 0) {
                    try {
                        viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % viewPager.getAdapter()
                                .getCount(), true);
                    } catch (Exception e) {
                    }
                }
                mHandler.postDelayed(bannerRunnable, BANNER_SPEED);
            }
        }
    };
    private BannerPagerAdapter pagerAdapter;

    public Fragment getCurrentFragment() {
        return pagerAdapter.getItem(viewPager.getCurrentItem());
    }

    private boolean loopAble = true;

    private boolean riseLogo;

    public LooperPager(Context context) {
        this(context, null);
    }

    public LooperPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LooperPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.looper_pager_layout, this, true);
        viewPager = (LooperViewPager) findViewById(R.id.pager);
        indicatorView = (IndicatorView) findViewById(R.id.indicator);
        setIndicatorViewMarginBottom(10.0f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(bannerRunnable);
    }

    public void setLoopAble(boolean loopAble) {
        this.loopAble = loopAble;
    }

    public void setAutoLoop(boolean doRun) {
        this.doRun = doRun;
    }

    public void setData(ArrayList<VBanner> banners, FragmentManager fm) {
        saveBanners = banners;
        saveFm = fm;
        mHandler.removeCallbacks(bannerRunnable);
        if (ArrayUtil.isEmpty(banners)) {
            setVisibility(View.GONE);
            return;
        }
        // set animation
        if (getVisibility() != View.VISIBLE) {
            setVisibility(VISIBLE);
        }

        //set adapter
        pagerAdapter = new BannerPagerAdapter(fm, banners, loopAble);
        viewPager.setAdapter(pagerAdapter, banners.size());
        indicatorView.setCount(pagerAdapter.getRealCount());
        indicatorView.setPosition(viewPager.getRealPosition());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                indicatorView.setPosition(viewPager.getRealPosition());

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        if (doRun) {
            mHandler.postDelayed(bannerRunnable, BANNER_SPEED);
        }
    }

    public boolean isRiseLogo() {
        return riseLogo;
    }

    public void setRiseLogo(boolean riseLogo) {
        this.riseLogo = riseLogo;
    }

    private class BannerPagerAdapter extends LoopPagerAdapter<VBanner> {

        public BannerPagerAdapter(FragmentManager fm, ArrayList<VBanner> datas, boolean loopAble) {
            super(fm, datas, loopAble);
        }

        @Override
        public Fragment newItem(VBanner banner) {
            Bundle args = new Bundle();
            args.putSerializable("banner", banner);
            args.putInt("audioStreamType", AudioManager.STREAM_VOICE_CALL);
            args.putBoolean("isPlay", true);
            args.putBoolean("riselogo", riseLogo);
            if (banner.type == 2) {
                BannerFragment imageFragment = new BannerFragment();
                imageFragment.setArguments(args);
                imageFragment.setOnViewClick(new BannerFragment.OnViewClick() {

                    @Override
                    public void onClick() {
                        if (listener != null) {
                            listener.onItemClick(viewPager.getRealPosition());
                        }
                    }
                });
                return imageFragment;
            } else {
                VideoPlayerFragment videoFragment = new VideoPlayerFragment();
                videoFragment.setArguments(args);
                videoFragment.setOnViewClick(new VideoPlayerFragment.OnViewClick() {

                    @Override
                    public void onClick() {
                        if (listener != null) {
                            listener.onItemClick(viewPager.getRealPosition());
                        }
                    }
                });
                return videoFragment;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                super.destroyItem(container, position, object);
            } catch (Exception ex) {
                LogUtil.e(ex, "ADViewPager.destroyItem invalide fragment state");
            }
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            try {
                super.destroyItem(container, position, object);
            } catch (Exception ex) {
                LogUtil.e(ex, "ADViewPager.destroyItem invalide fragment state");
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setIndicatorViewMarginBottom(float dpValue) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp.bottomMargin = CommonUtils.dp2px(dpValue);
        indicatorView.setLayoutParams(lp);
        if (dpValue > 10) {
            riseLogo = true;
            if (dpValue <= 40 && saveFm != null) {
                setData(saveBanners, saveFm);
            }
        } else {
            riseLogo = false;
        }
    }
}
