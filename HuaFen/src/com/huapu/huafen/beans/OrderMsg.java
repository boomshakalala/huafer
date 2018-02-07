package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by qwe on 2017/9/21.
 */

public class OrderMsg implements Serializable {
    public GoodsData goods;
    public OrderUser user;
    public MsgOrder order;
    public String messageId;
    public long timestamp;
}
