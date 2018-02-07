package com.huapu.huafen.utils;

import java.io.Serializable;
/**
 * Created by admin on 2017/3/27.
 */

public class Pair<F, S> implements Serializable{
    public F first;
    public S second;

    /**
     * Constructor for a Pair.
     *
     * @param first the first object in the Pair
     * @param second the second object in the pair
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public Pair() {
    }
}
