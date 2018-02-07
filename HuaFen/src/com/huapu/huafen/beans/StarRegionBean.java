package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qwe on 2017/5/19.
 */

public class StarRegionBean implements Serializable{
    public int page;
    public List<VIPRegionBean.ActiveUsersBean> activeUsers;
    public List<UserWithGoods> recUserWithGoods;


    public static class UserWithGoods implements Serializable {
        /**
         * counts : {"week":"6"}
         * user : {"zmCreditPoint":700,"userId":690895,"avatarUrl":"http://imgs.huafer.cc/huafer/0/headIcon/2017/3/15/1489573279048_icon.jpg","userName":"杨光测试","userLevel":2,"hasCredit":true}
         */

        public StarCount counts;
        public UserData user;
        public List<ItemBean> item;


    }

    public static class ItemBean implements Serializable {
        /**
         * status : 0
         * goodsId : 890485
         * goodsImgs : ["http://imgs.huafer.cc/huafer/80/goods/2017/4/27/1493292199.jpg@!logo"]
         * price : 1
         * pastPrice : 0
         */

        public int status;
        public int goodsId;
        public int price;
        public int pastPrice;
        public List<String> goodsImgs;
    }
}
