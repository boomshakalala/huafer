package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qwe on 2017/5/17.
 */

public class VIPRegionBean implements Serializable {

    public int page;
    public List<HotGoodsBean> hotGoods;
    public List<ActiveUsersBean> activeUsers;
    public List<ListBean> list;
    public List<HotCatsBean> hotCats;

    public static class ActiveUsersBean implements Serializable {
        /**
         * counts : {"week":"6"}
         * user : {"zmCreditPoint":700,"userId":690895,"avatarUrl":"http://imgs.huafer.cc/huafer/0/headIcon/2017/3/15/1489573279048_icon.jpg","userName":"杨光测试","userLevel":2,"hasCredit":true}
         */

        public StarCount counts;
        public UserData user;

    }

    public static class ListBean implements Serializable {
        public String itemType;
        public HotGoodsBean.ItemBean item;
        public Count counts;
        public UserData user;
    }

    public static class HotCatsBean implements Serializable {
        public String name;
        public String description;
        public String icon;
        public int cid;
    }


}


