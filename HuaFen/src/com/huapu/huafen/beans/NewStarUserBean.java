package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qwe on 2017/5/19.
 */

public class NewStarUserBean implements Serializable {
    public int page;
    public List<StarUserBean> list;
    public String snapshotId;
    public static class StarUserBean implements Serializable {
        public StarCount counts;
        public UserData user;
    }
}
