package com.huapu.huafen.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.huapu.huafen.fragment.base.ScrollAbleFragment;

import java.util.List;

/**
 * Created by xk on 2017/5/9.
 */

public class SquareManagerFragment extends FragmentPagerAdapter {
    private List<SquareFragment> fragmentList;
    private List<String> titleList;

    public SquareManagerFragment(FragmentManager fm, List<SquareFragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
