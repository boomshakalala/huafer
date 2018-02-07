package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/12/16.
 */
public class WalletResult implements Serializable{

    public int earned;//赚

    public int saved;//省

    public String tip;

    public List<Transaction> transactions;

    public int page;

    public static class Transaction implements Serializable{

        public int type;
        public long orderId;
        public String goodsImageUrl;
        public int amount;
        public long createdAt;
        public String goodsName;
    }
}
