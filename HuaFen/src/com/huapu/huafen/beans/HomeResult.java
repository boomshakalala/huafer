package com.huapu.huafen.beans;

import java.util.ArrayList;

/**
 * Created by danielluan on 2017/10/14.
 */

public class HomeResult extends BaseResult {


    public ArrayList<Item> oneYuan;

    public ArrayList<Item> stars;

    public ArrayList<ActionData> poems;

    public ArrayList<ActionData> vols;

    public ArrayList<CatData> vip;


    public static class ActionData extends BaseResultNew.BaseData {

        public String action;
        public String target;
        public String image;
        public String title;
        public String note;

    }

    public static class CatData extends BaseResultNew.BaseData {

        public String cid;
        public String name;
        public boolean select;

    }


}