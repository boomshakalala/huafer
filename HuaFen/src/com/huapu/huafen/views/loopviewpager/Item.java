package com.huapu.huafen.views.loopviewpager;

import java.io.Serializable;

public class Item implements Serializable {
    private int position;
    private int img;
    private String name;
    private boolean isSelected;
    private String userPic;
    private int shareEarned;
    private String inputContent;

    private int userLevel;

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getInputContent() {
        return inputContent;
    }

    public void setInputContent(String inputContent) {
        this.inputContent = inputContent;
    }

    public int getShareEarned() {
        return shareEarned;
    }

    public void setShareEarned(int shareEarned) {
        this.shareEarned = shareEarned;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
