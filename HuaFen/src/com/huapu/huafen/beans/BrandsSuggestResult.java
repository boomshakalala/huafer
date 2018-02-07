package com.huapu.huafen.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/3/24.
 */

public class BrandsSuggestResult extends BaseResultNew{

    public BrandsData obj;

    public static class BrandsData extends BaseData{
        public List<Brand> list;
    }
}
