package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/11/7.
 */
public class ShopkeepersData implements Serializable {
    private ArrayList<ShopArticleData> list;
    private int page;
    private String snapshotId;
    private List<VIPUserInfo> vipList;
    private List<StarUserInfo> starList;

    public List<VIPUserInfo> getVipList() {
        return vipList;
    }

    public void setVipList(List<VIPUserInfo> vipList) {
        this.vipList = vipList;
    }

    public List<StarUserInfo> getStarList() {
        return starList;
    }

    public void setStarList(List<StarUserInfo> starList) {
        this.starList = starList;
    }

    public ArrayList<ShopArticleData> getList() {
        return list;
    }

    public void setList(ArrayList<ShopArticleData> list) {
        this.list = list;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public int getPage() {
        return page;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setPage(int page) {
        this.page = page;
    }


}
