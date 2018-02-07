package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/3/18.
 */

public class BaseResultNew implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String SHOW_DIALOG = "showDialog";
    public static final String UPDATE_USER_PROPERTY = "updateUserProperty";
    public int code;
    public String msg;
    public List<Event> events;

    public static class BaseData implements Serializable{

    }

    public static class Event implements Serializable{

        public String name;
        public Option opts;

        public static class Option implements Serializable{
            public String title;
            public String content;
            public List<Button> buttons;
            public String grantedCampaigns;
            public int grantedOneYuan;

            public static class Button implements Serializable{
                public String title;
                public String action;
                public String target;
            }
        }

    }
}
