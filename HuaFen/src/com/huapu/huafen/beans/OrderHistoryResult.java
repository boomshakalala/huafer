package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 17/6/29.
 */

public class OrderHistoryResult extends BaseResultNew {

    public Data obj;

    public static class Data extends BaseData{
        public OrderHistory orderHistory;
    }

    public static class OrderHistory implements Serializable{
        public List<OrderOperate> orderOperates;
        public GoodsInfo goods;
        public UserInfo user;

    }

    public static class OrderOperate implements Serializable {
        public String title;
        public String createdAt;
        public List<Map<String,String>> properties;
    }

}
