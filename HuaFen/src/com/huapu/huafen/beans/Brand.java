package com.huapu.huafen.beans;

import com.huapu.huafen.utils.SortToken;

import java.io.Serializable;

/**
 * Created by admin on 2017/3/24.
 */

public class Brand implements Serializable {

    public String nameDisplay;

    public String brandName;

    public int brandId;

    public String imageUrl;

    public SortToken sortToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Brand brand = (Brand) o;

        if (brandId != brand.brandId) return false;
        return brandName != null ? brandName.equals(brand.brandName) : brand.brandName == null;

    }

    @Override
    public int hashCode() {
        int result = brandName != null ? brandName.hashCode() : 0;
        result = 31 * result + brandId;
        return result;
    }
}
