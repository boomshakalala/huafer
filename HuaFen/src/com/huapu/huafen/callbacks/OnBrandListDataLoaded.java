package com.huapu.huafen.callbacks;

import com.huapu.huafen.beans.Brand;
import com.huapu.huafen.utils.Pair;

import java.util.ArrayList;

/**
 * Created by admin on 2017/3/25.
 */

public interface OnBrandListDataLoaded {

    void onLoad(ArrayList<Pair<String, ArrayList<Brand>>> brandGroups);
}
