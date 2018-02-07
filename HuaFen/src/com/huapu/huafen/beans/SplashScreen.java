package com.huapu.huafen.beans;

import com.huapu.huafen.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by admin on 2016/10/11.
 */
public class SplashScreen implements Serializable {

    private int repeat;
    private long repeatTime;
    private ArrayList<Screen> list;

    public ArrayList<Screen> getList() {
        return list;
    }

    public void setList(ArrayList<Screen> list) {
        this.list = list;
    }


    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public long getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(long repeatTime) {
        this.repeatTime = repeatTime;
    }


    public Screen getScreen() {

        //LogUtil.d("danielluanll", "screenlist=" + list);
        for (int i = 0; getList() != null && i < getList().size(); i++) {
            Screen screen = getList().get(i);
            if (screen != null && screen.getHasRead() == 0) {
                LogUtil.d("danielluanll", "screenImage=" + screen.getImage());
                return screen;
            }
        }

        int total = 1;
        for (int i = 0; getList() != null && i < getList().size(); i++) {
            Screen screen = getList().get(i);
            total = total + screen.getWeight();
        }
        Random rand = new Random(System.currentTimeMillis());
        int n = rand.nextInt(total);

        int sumWeight = 0;
        for (int i = 0; getList() != null && i < getList().size(); i++) {
            Screen screen = getList().get(i);
            sumWeight = sumWeight + screen.getWeight();
            if (sumWeight > n) {
                LogUtil.d("danielluanll", "total=" + total + "rand n =" + n);
                return screen;
            }
        }


        return null;
    }

}
