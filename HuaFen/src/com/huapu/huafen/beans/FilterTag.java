package com.huapu.huafen.beans;

/**
 * Created by danielluan on 2017/9/22.
 */

public class FilterTag extends BaseResult {

    /**
     * 标签说明
     */
    private String title;
    private int id;
    private int sequence;
    private boolean isSelect;
    public boolean isCheck;

    private String value;

    public FilterTag(String title) {
        setTitle(title);
    }

    public boolean getCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public void clean() {
        setCheck(false);
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String tagTitle) {
        this.title = tagTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int ageId) {
        this.id = ageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterTag tag = (FilterTag) o;

        if (id != tag.id) return false;
        return title != null ? title.equals(tag.title) : tag.title == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
