package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/4/26.
 */

public class FloridData implements Serializable {

    public String title;
    public long categoryId;
    public String summary;
    public TitleMedia titleMedia;
    public long createdAt;
    public long updatedAt;
    public boolean liked;
    public boolean collected;
    public long uid;
    public String categoryName ;
    public List<Section> sections;
    public long articleId;
    public String poiUid;
    public boolean[] state;

    public static class TitleMedia implements Serializable {
        public String mimeType = "image/jpg";
        public String url;
        public int width;
        public int height;
        public List<Tag> tags;
        public int mediaId ;
//        public String localPath;
        public boolean editAble = false;
        public String filterStyle = "无";
        public float[] matrix = new float[0];

        @Override
        public String toString() {
            return "TitleMedia{" +
                    "mimeType='" + mimeType + '\'' +
                    ", url='" + url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", tags=" + tags +
//                    ", localPath='" + localPath + '\''
                    +
                    '}';
        }
    }

    public static class Tag implements Serializable{
        public int x;
        public int y;
        public int orientation;
        public int type;
        public String title;
        public String action;
        public String target;
        public String txt;
    }

    public static class Section implements Serializable{
        public TitleMedia media;
        public String content;
        public int sequence;
        public long sectionId;
        public boolean isPopup;

        @Override
        public String toString() {
            return "Section{" +
                    "media=" + media +
                    ", content='" + content + '\'' +
                    ", sequence=" + sequence +
                    '}';
        }
    }




}
