package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.HPagerTitleAdapter;
import com.huapu.huafen.fragment.MyAuctionFragment;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 拍卖
 * Created by qwe on 2017/8/3.
 */
public class MyAuctionActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private HPagerTitleAdapter auctionAdapter;

    private ArrayList<Fragment> fragmentList;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myauction);
        ButterKnife.bind(this);

        if (null != getIntent() && getIntent().hasExtra("index")) {
            index = getIntent().getIntExtra("index", -1);
        }
        initView();

    }

    private void initView() {
        getTitleBar().setTitle("拍卖");
        String[] auctionArray = getResources().getStringArray(R.array.my_auction);
        fragmentList = new ArrayList<>();
        if (ArrayUtil.isEmpty(auctionArray)) {
            return;
        }
        for (int i = 0; i < auctionArray.length; i++) {
            Bundle bundle = new Bundle();
            if (index == i) {
                viewPager.setCurrentItem(i);
                bundle.putBoolean(MyConstants.EXTRA_ORDERS_IS_FIRST_LOAD, true);
            }
            if (i == 0) {
                bundle.putInt("type", 4);
            } else if (i == 1) {
                bundle.putInt("type", 2);
            } else if (i == 2) {
                bundle.putInt("type", 1);
            } else if (i == 3) {
                bundle.putInt("type", 3);
            }

            MyAuctionFragment fragment = MyAuctionFragment.newInstance(bundle);
            fragmentList.add(fragment);
        }
        auctionAdapter = new HPagerTitleAdapter(getSupportFragmentManager(), fragmentList, Arrays.asList(auctionArray));
        viewPager.setAdapter(auctionAdapter);
        viewPager.setOffscreenPageLimit(auctionArray.length);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.e("onPageSelected", "page:" + i);
                index = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setCurrentItem(index);
    }
}
