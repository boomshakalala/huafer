package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by qwe on 2017/9/20.
 */
//{
//        "message": {
//        "content": "hxhxxj",
//        "user": {
//        "hasCredit": false
//        }
//        },
//        "user": {
//        "hasCredit": false,
//        "userName": "巴拉巴拉",
//        "avatarUrl": "https://imgs.huafer.cc/huafer/0/headIcon/2016/7/27/1469594751_icon.jpg",
//        "userLevel": 3
//        },
//        "action": "openCommentMessage",
//        "image": "https://imgs.huafer.cc/huafer/1505371/goods/2017/9/15/1505454844910.jpg@!logo",
//        "target": 16372
//        }

//
//    "targetType" -> "1"
//            "user" -> " size = 8"
//            "infoId" -> "1953756"
//            "message" -> " size = 1"
//            "target" -> "41100"
//            "image" -> "https://imgs.huafer.cc/huafer/1503077/goods/2017/10/25/1508910398240.jpg@!logo"
//            "action" -> "openCommentMessage"
public class CommentMsg implements Serializable {
    public Msg message;
    public CommentUser user;
    public String action;
    public String image;
    public long target;
    public int targetType;
    public long timestamp;
    public long infoId;
}
