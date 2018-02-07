package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/3/16.
 */

public class OrderListResult implements Serializable {

    public int page;

    public List<OrderListBean> orderLogList;
}
