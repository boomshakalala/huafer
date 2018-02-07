package com.huapu.huafen.beans;

import java.util.List;

/**
 * Created by mac on 17/8/14.
 */

public class StaggeredResult extends BaseResultNew {

    public Data obj;

    public static class Data extends BaseResultNew.BaseData {
        public List<Item> list;
        public int page;
    }
}
