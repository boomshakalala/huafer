package com.huapu.huafen.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.huapu.huafen.fragment.base.ScrollAbleFragment;
import com.huapu.huafen.scrollablelayoutlib.PagerSlidingTabStrip;

import java.util.List;


public class HPagerIconAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private List<Fragment> fragmentList;
    private int [] icons;

    public HPagerIconAdapter(FragmentManager fm, List<Fragment> fragmentList, int[] icons) {
        super(fm);
        this.fragmentList = fragmentList;
        this.icons = icons;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return titleList.get(position);
//    }

    @Override
    public int getPageIconResId(int position) {
        return icons[position];
    }
}
