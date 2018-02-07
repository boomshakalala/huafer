package com.huapu.huafen.beans;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by admin on 2016/12/22.
 */

public class VideoData implements Serializable {

    public String title;

    public long uploadProgress;

    public int resId;

    public Bitmap bmp;

    public String url;

    public UploadState uploadState = UploadState.IDLE;

    public enum UploadState{
        IDLE,
        UPLOADING,
        UPLOADED,
        FAILED,
        ;

    }

    public VideoData(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public VideoData() {
    }
}
