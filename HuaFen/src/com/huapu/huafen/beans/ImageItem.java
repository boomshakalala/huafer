package com.huapu.huafen.beans;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;

import java.io.Serializable;

/**
 * 本地图片
 *
 * @author liang_xs
 */
public class ImageItem implements Cloneable, Serializable {
    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    public Bitmap bitmap;
    public boolean isSelected = false;
    //	public boolean isCheck = false;
    public int pageId; // 图片显示类型：横屏，竖屏，正方形
    public String musicId; // 音乐id
    public String content = ""; // 模版内容
    public String title = ""; // 模版标题
    public String labelTitle = ""; // 贴纸关联的贴纸标题
    public int labelId; // 贴纸关联的贴纸ID
    /**
     * 1:高图，2：宽图，3：正方形
     */
    public int shape;
    public String dbCurrentTimeId;
    public String MD5;
    public String url;

    /**
     * 新添加处理
     *
     * @return
     */
    public FloridData.TitleMedia titleMedia;


    public ImageItem() {
        super();
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void getBitmap(Context context, BitmapCallback callback) {
        if (bitmap == null) {
            if (imagePath.contains("http")) {
                ImageLoader.loadBitmap(context, Uri.parse(imagePath), callback);
            } else {
                bitmap = ImageUtils.revitionImageSize(imagePath);
                callback.onBitmapDownloaded(bitmap);
            }
        } else
            callback.onBitmapDownloaded(bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    @Override
    public ImageItem clone() {
        ImageItem clone = null;
        try {
            clone = (ImageItem) super.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImageItem) {
            return TextUtils.equals(((ImageItem) obj).getImagePath(), getImagePath());
        }
        return false;
    }
}
