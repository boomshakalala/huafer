package com.huapu.huafen.activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.HPagerAdapter;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.fragment.GalleryUrlFragment;
import com.huapu.huafen.fragment.VideoPlayerFragment;
import com.huapu.huafen.looper.IndicatorView;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/14.
 */
public class GalleryActivity extends BaseActivity {

    private ViewPager viewPager;
    private HPagerAdapter adapter;
    private ArrayList<VBanner> items;
    private IndicatorView indicatorView;
    private int position;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        if (getIntent().hasExtra("banners")) {
            items = (ArrayList<VBanner>) getIntent().getSerializableExtra("banners");
        }

        if (getIntent().hasExtra(MyConstants.EXTRA_IMAGE_INDEX)) {
            position = getIntent().getIntExtra(MyConstants.EXTRA_IMAGE_INDEX,
                    -1);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicatorView = (IndicatorView) findViewById(R.id.indicator);
        if(ArrayUtil.isEmpty(items)||(items.size()==1&&items.get(0).type==1)){
            return;
        }

        ArrayList<Fragment> fragmentArrayList = new ArrayList<Fragment>();

        VBanner banner =items.get(0);
        if (banner.type == 1){
            VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
            Bundle data  = new Bundle();
            data.putSerializable("banner", banner);
            data.putInt("audioStreamType", AudioManager.STREAM_MUSIC);
            videoPlayerFragment.setArguments(data);
            videoPlayerFragment.setOnViewClick(new VideoPlayerFragment.OnViewClick() {

                @Override
                public void onClick() {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });

            videoPlayerFragment.setOnRootViewClick(new VideoPlayerFragment.OnRootViewClick() {

                @Override
                public void onClick() {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
            fragmentArrayList.add(videoPlayerFragment);

            List<VBanner> tmp = items.subList(1, items.size());
            ArrayList<String> list = convertBanners(tmp);
            Bundle bundle  = new Bundle();
            bundle.putSerializable("items",list);
            bundle.putSerializable("banners",convertPositionBanners(tmp));

            if(position==0){
                currentIndex = 0;
            }else if(position>0){
                currentIndex = 1;
                data.putBoolean("isPlay", false);
            }
            bundle.putInt("position",position-1);
            GalleryUrlFragment galleryUrlFragment = GalleryUrlFragment.newInstance(bundle);
            galleryUrlFragment.setOnIndicatorChangeListener(new GalleryUrlFragment.OnIndicatorChangeListener() {

                @Override
                public void onChange(int position) {
                    indicatorView.setPosition(position);
                }
            });
            fragmentArrayList.add(galleryUrlFragment);
        }else {
            ArrayList<String> list = convertBanners(items);
            Bundle bundle  = new Bundle();
            bundle.putSerializable("items",list);
            bundle.putSerializable("banners",convertPositionBanners(items));
            bundle.putInt("position",position);
            currentIndex = 0;
            GalleryUrlFragment galleryUrlFragment = GalleryUrlFragment.newInstance(bundle);
            galleryUrlFragment.setOnIndicatorChangeListener(new GalleryUrlFragment.OnIndicatorChangeListener() {

                @Override
                public void onChange(int position) {
                    indicatorView.setPosition(position);
                }
            });
            fragmentArrayList.add(galleryUrlFragment);
        }

        adapter = new HPagerAdapter(getSupportFragmentManager(),fragmentArrayList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentIndex);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                indicatorView.setPosition(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        isNeedsFinishAnimation = false;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)indicatorView.getLayoutParams();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        indicatorView.setCount(items.size());
        indicatorView.setPosition(position);
    }

    private ArrayList<String> convertBanners(List<VBanner> items){
        ArrayList<String> list = new ArrayList<String>();
        for(VBanner banner: items){
            list.add(banner.imgUrl);
        }
        return list;
    }

    private ArrayList<Integer> convertPositionBanners(List<VBanner> items){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(VBanner banner: items){
            list.add(banner.position);
        }
        return list;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
