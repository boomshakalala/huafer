package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2017/1/4.
 */

public class ServerData implements Serializable {

    public String name;
    public String devUrl;
    public boolean isCheck;

    @Override
    public String toString() {
        return name+":\nbaseUrl="+devUrl;
    }
}
