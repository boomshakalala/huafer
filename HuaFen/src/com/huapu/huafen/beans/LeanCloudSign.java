package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by qwe on 2017/9/19.
 */

public class LeanCloudSign implements Serializable {
//     "msg": "btbuVwvFMiwXMdsfI4PwKYap-gzGzoHsz:1505371:183:1505808280981:74.25561109839659",
//             "sign": "0957B9B1F84E0FFEB0AA04B5D0903CDEFD31A2A9",
//             "nonce": "74.25561109839659",
//             "ts": 1505808280981
    public  String msg;
    public  String sign;
    public  String nonce;
    public  long ts;
}
