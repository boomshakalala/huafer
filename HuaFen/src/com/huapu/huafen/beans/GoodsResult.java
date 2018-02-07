
package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liang_xs by 2016/11/02
 */
public class GoodsResult implements Serializable {

    private int page;
    private List<Goods> goods;

    private GoodsStatusCounts goodsStatusCounts;


    public GoodsStatusCounts getGoodsStatusCounts() {
        return goodsStatusCounts;
    }

    public void setGoodsStatusCounts(GoodsStatusCounts goodsStatusCounts) {
        this.goodsStatusCounts = goodsStatusCounts;
    }

    public static class GoodsStatusCounts implements Serializable{
        public int notAudit;
        public int selling;
        public int offShelf;
        public int waitingToWater;
        public int waitingToShelve;
    }

    public List<Goods> getGoods() {
        return goods;
    }

    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
