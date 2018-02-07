package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by qwe on 2017/9/21.
 */
//title
//"msg": "",
//        +          "image": "jpg",
//        +          "action": "",
//        +          "target": ""
public class NoticeData implements Serializable {
    public String title;
    public String image;
    public String action;
    public String msg;
    public String target;
    public String sentAt;
    public long  timestamp;


}
