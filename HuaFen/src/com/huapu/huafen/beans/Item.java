package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2017/5/14.
 */

public class Item implements Serializable {
    public String itemType;
    public long orderId;
    public UserData user;
    public int depositStatus;
    public ArticleAndGoods item;
    public Count counts;
    public String extra;//额外备用
    public boolean isGrid;
    public int res;
}
