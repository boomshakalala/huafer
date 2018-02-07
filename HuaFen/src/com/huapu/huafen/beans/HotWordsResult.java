package com.huapu.huafen.beans;

import java.util.List;

/**
 * Created by admin on 2017/6/14.
 */

public class HotWordsResult extends BaseResultNew {

    public Data obj;

    public static class Data extends BaseResultNew.BaseData {
        public List<KeyWordData> hotWords;
        public String defaultWord;
    }
}
