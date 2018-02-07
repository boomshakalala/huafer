package com.huapu.huafen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.huapu.huafen.scrollablelayoutlib.PagerSlidingTabStrip;

import java.util.List;

/**
 * Created by admin on 2017/1/9.
 */

public class RedPointTitleAdapter extends HPagerTitleAdapter implements PagerSlidingTabStrip.RedPointTabProvider {

    private int[] redPoints;
    private int[] imageRes;

    public RedPointTitleAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
        super(fm, fragmentList, titleList);
    }

    public void setRedPoints(int[] redPoints){
        this.redPoints = redPoints;
    }

    public void setImageRes(int[] imageRes) {
        this.imageRes = imageRes;
    }

    @Override
    public int getRedPointCount(int position) {
        return redPoints[position];
    }

    @Override
    public int getImageResId(int position) {
        return imageRes[position];
    }
}
