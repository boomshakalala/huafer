package com.huapu.huafen.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielluan on 2017/9/27.
 */

public class VolumnResult extends BaseResult {


    public String pageTitle;

    public filterBarAlphaData filterBarAlpha;

    public int page;

    public brandSectionData brandSection;

    public featuredSectionData featuredSection;

    public ArrayList<Item> items;

    public ArrayList<filterBarData> filterBar;

    public ArrayList<filterMenuData> filterMenu;

    public boolean showFilterBarDiscount;


    public static class filterMenuData extends BaseResultNew.BaseData {

        public String type;

        public Attrs attrs;


        public static class Attrs extends BaseResultNew.BaseData {

            public String name;
            public String title;
            public boolean multiple;
            public ArrayList<Opts> opts;

            public static class Opts extends BaseResultNew.BaseData {
                public String title;
                public String value;
                public boolean selected;
            }
        }
    }


    public static class filterBarData extends BaseResultNew.BaseData {

        public String type;
        public String title;
        public Attrs attrs;

        public static class Attrs extends BaseResultNew.BaseData {
            public int level;
            public String items="";
            public boolean expand;
        }

    }


    public static class brandSectionData extends BaseResultNew.BaseData {

        public String image;
        public String title;
        public String content;

    }


    public static class filterBarAlphaData extends BaseResultNew.BaseData {

        public String name;
        public List<filterItem> items;

        public static class filterItem extends BaseResultNew.BaseData {

            public String image;
            public String title;
            public String value;

        }
    }


    public static class featuredSectionData extends BaseResultNew.BaseData {

        public MoreBtn moreBtn;
        public String title;
        public String content;
        public ArrayList<Item> items;

        public static class MoreBtn extends BaseResultNew.BaseData {

            public String targetType;
            public String targetId;
            public String config;

        }


    }


}