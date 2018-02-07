package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac on 17/8/2.
 */

public class BidInfo implements Serializable{
    public List<BidRecord> bidList;
    public int bidCount;
}
