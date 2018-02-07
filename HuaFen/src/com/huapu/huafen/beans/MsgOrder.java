package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by qwe on 2017/9/21.
 */

public class MsgOrder implements Serializable {
    public long buyerId;
    public long sellerId;
    public long orderId;
    public String  statusTitle;
    public boolean  arbitration;
    public int status;
}
