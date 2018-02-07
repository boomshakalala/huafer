package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/4/11.
 */

public class ClassificationResult extends BaseResultNew {

    public ClassificationData obj;

    public static class ClassificationData extends BaseData{
        public List<Indice> indices;
        public String selected;
        public List<Layout> layout;

    }

    public static class Indice implements Serializable{
        public String key;
        public String title;
        public boolean isCheck;

        @Override
        public String toString() {
            return "Indice{" +
                    "key='" + key + '\'' +
                    ", title='" + title + '\'' +
                    ", isCheck=" + isCheck +
                    '}';
        }
    }

    public static class Layout implements Serializable{
        public String type;
        public Option opts;

    }

    public static class Option implements Serializable{
        public List<itemsType> itemsTypes;
        public String image;
        public List<Opt> items;
        public String type;

    }

    public static class itemsType implements Serializable{
        public String type;
        public Opt opts;
    }

    public static class Opt implements Serializable{
        public String title;
        public String note;
        public String image;
        public String action;
        public String target;
        public String type;

        @Override
        public String toString() {
            return "Opt{" +
                    "title='" + title + '\'' +
                    ", note='" + note + '\'' +
                    ", image='" + image + '\'' +
                    ", action='" + action + '\'' +
                    ", target='" + target + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

}
