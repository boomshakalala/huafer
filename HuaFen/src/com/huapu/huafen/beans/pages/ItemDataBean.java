package com.huapu.huafen.beans.pages;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.beans.GoodsData;

import java.io.Serializable;

/**
 * 列表页item数据
 * Created by dengbin on 18/1/10.
 */
public class ItemDataBean implements Serializable {
    private String itemType;
    private String item;
    private int depositStatus;
    
    private GoodsData goods;

    public GoodsData getGoods() {
        if (goods != null)
            return goods;
        if (TextUtils.isEmpty(getItem()))
            return null;
        GoodsData data = JSON.parseObject(getItem(), GoodsData.class);
        setGoods(data);
        return goods;
    }

    public void setGoods(GoodsData goods) {
        this.goods = goods;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(int depositStatus) {
        this.depositStatus = depositStatus;
    }
}
