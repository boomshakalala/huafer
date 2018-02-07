package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qwe on 2017/5/17.
 */

public class HotGoodsBean implements Serializable {

    public String itemType;
    public ItemBean item;
    public Count counts;
    public UserData user;

    public static class ItemBean implements Serializable {
        public String name;
        public boolean liked;
        public boolean isFreeDelivery;
        public int isNew;
        public int distance;
        public int crowd;
        public boolean collected;
        public long goodsId;
        public String brand;
        public String price;
        public String pastPrice;
        public int goodsState;
        public String content;
        public int postage;
        public int auditStatus;
        public List<String> goodsImgs;
    }


}
