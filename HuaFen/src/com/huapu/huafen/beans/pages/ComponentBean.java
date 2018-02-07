package com.huapu.huafen.beans.pages;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.beans.BannerData;

import java.io.Serializable;

/**
 * 组件
 * Created by dengbin on 18/1/9.
 */
public class ComponentBean implements Serializable {
    private String type;
    private String attrs;

    private BannerData banner;
    private ShowcaseBean caseBean;
    private ButtonBean buttonBean;

    public ShowcaseBean getCaseBean() {
        return caseBean;
    }

    public void setCaseBean(ShowcaseBean caseBean) {
        this.caseBean = caseBean;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs;
    }

    public BannerData getBanner() {
        return banner;
    }

    public void setBanner(BannerData banner) {
        this.banner = banner;
    }

    public ButtonBean getButtonBean() {
        return buttonBean;
    }

    public void setButtonBean(ButtonBean buttonBean) {
        this.buttonBean = buttonBean;
    }

    public void parseData() {
        if (TextUtils.isEmpty(getAttrs()))
            return;
        switch (getType()) {
            case "Banner":
                setBanner(JSON.parseObject(getAttrs(), BannerData.class));
                break;
            case "GoodsShowcaseS1":
                setCaseBean(JSON.parseObject(getAttrs(), ShowcaseBean.class));
                break;
            case "GoodsShowcaseS2":
                setCaseBean(JSON.parseObject(getAttrs(), ShowcaseBean.class));
                break;
            case "Button":
                setButtonBean(JSON.parseObject(getAttrs(), ButtonBean.class));
                break;
            default:
                break;
        }
    }
}
