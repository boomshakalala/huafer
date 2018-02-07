package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qwe on 2017/7/31.
 */

public class RecommendCoverBean extends BaseResult {

    public String selectedBackground;
    public List<Background> backgrounds;

    public static class Background implements Serializable {

        public String name;
        public List<Item> items;

        public static class Item implements Serializable {
            public String mediaId;
            public String url;
        }
    }

}
