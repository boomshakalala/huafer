package com.huapu.huafen.looper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by admin on 2016/12/12.
 */
public abstract class LoopPagerAdapter<T> extends FragmentStatePagerAdapter {

    protected final ArrayList<T> datas;
    protected boolean loopAble;

    public LoopPagerAdapter(FragmentManager fm, ArrayList<T> datas, boolean loopAble) {
        super(fm);
        this.datas = datas;
        this.loopAble = loopAble;
    }

    public boolean loopAble() {
        return this.loopAble;
    }

    public Fragment getItem(int position) {
        return this.newItem(this.datas.get(this.getRealPosition(position)));
    }

    public int getRealPosition(int position) {
        return this.loopAble && this.getRealCount() > 1?(position == 0?this.datas.size() - 1:(position == this.datas.size() + 1?0:position - 1)):position;
    }

    public abstract Fragment newItem(T var1);

    public int getCount() {
        int realCount = this.getRealCount();
        return this.loopAble && realCount > 1?realCount + 2:realCount;
    }

    public int getRealCount() {
        return this.datas == null?0:this.datas.size();
    }
}
