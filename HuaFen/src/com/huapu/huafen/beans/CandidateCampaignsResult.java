package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac on 2018/1/3.
 */

public class CandidateCampaignsResult implements Serializable {
    List<JoinCampaign> list;

    public List<JoinCampaign> getList() {
        return list;
    }

    public void setList(List<JoinCampaign> list) {
        this.list = list;
    }
}
