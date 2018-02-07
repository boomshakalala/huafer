package com.huapu.huafen.beans;

import java.util.ArrayList;

/**
 * Created by admin on 2017/3/24.
 */

public class BrandsResult extends BaseResultNew{

    public BrandsData obj;

    public static class BrandsData extends BaseData{
        public ArrayList<Brand> brandList;
        public String cacheVersion;
    }
}
