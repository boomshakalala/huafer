package com.huapu.huafen.beans;

import java.util.List;

/**
 * Created by mac on 17/8/2.
 */

public class BidRecordListResult extends BaseResultNew{

    public Data obj;

    public static class Data extends BaseData{
        public List<BidRecord> bidList;
        public int bidCount;
        public int page;
    }
}
