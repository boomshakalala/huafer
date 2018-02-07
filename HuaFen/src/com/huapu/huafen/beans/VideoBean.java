package com.huapu.huafen.beans;


import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/23.
 */
public class VideoBean implements Serializable{
    private String videoPath;
    private String videoCover;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }
}
