package com.huapu.huafen.beans;

import java.util.List;

/**
 * Created by admin on 2017/5/19.
 */

public class OneYuanRegionResult extends BaseResultNew {

    public Data obj;

    public static class Data extends BaseData {
        public long startTime;
        public long endTime;
        public long eventId;
        public String event;
        public List<Item> hotGoods;
        public List<Item> list;
        public int page;
    }
}
