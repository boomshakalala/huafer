package com.huapu.huafen.beans;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/5/20.
 */

public class PastRankingResult extends BaseResultNew{

    public Data obj;

    public static class Data extends BaseData {
        public List<Map<String,String>> list;
    }

}
