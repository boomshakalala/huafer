package com.huapu.huafen.beans;

import java.util.ArrayList;

/**
 * Created by admin on 2017/3/27.
 */

public class SportCatsResult extends BaseResultNew {

    public SportCatsData obj;

    public static class SportCatsData extends BaseData{
        public ArrayList<Cat> classifications;
        public String cacheVersion;
    }
}
