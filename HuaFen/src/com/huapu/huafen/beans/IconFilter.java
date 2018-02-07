package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by danielluan on 2017/9/25.
 */

public class IconFilter implements Serializable {

    private String nameDisplay;

    private String imageUrl;

    private String value;

    private boolean ischeck;


    public IconFilter(String imageUrl, String nameDisplay, String value) {
        setImageUrl(imageUrl);
        setNameDisplay(nameDisplay);
        this.setValue(value);
    }
    public IconFilter(String imageUrl, String nameDisplay) {
        setImageUrl(imageUrl);
        setNameDisplay(nameDisplay);
        this.setValue("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IconFilter filter = (IconFilter) o;

        if (!getImageUrl().equals(filter.getImageUrl())) return false;
        return getValue() != null ? getValue().equals(filter.getValue()) : filter.getValue() == null;

    }

    @Override
    public int hashCode() {
        int result = getImageUrl() != null ? getImageUrl().hashCode() : 0;

        return result;
    }

    public String getNameDisplay() {
        return nameDisplay;
    }

    public void setNameDisplay(String nameDisplay) {
        this.nameDisplay = nameDisplay;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }
}