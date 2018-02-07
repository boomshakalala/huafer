package com.huapu.huafen.callcenter;

import android.graphics.Bitmap;
import android.net.Uri;

import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.utils.ImageLoader;
import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.UnicornImageLoader;

/**
 * Created by danielluan on 2017/9/20.
 */
public class UILImageLoader implements UnicornImageLoader {

    @Override
    public Bitmap loadImageSync(final String uri, int width, int height) {
        return null;
    }

    @Override
    public void loadImage(String uri, int width, int height, final ImageLoaderListener listener) {
        ImageLoader.loadBitmap(null, Uri.parse(uri), new BitmapCallback() {
            @Override
            public void onBitmapDownloaded(Bitmap bitmap) {
                listener.onLoadComplete(bitmap);
            }
        });
    }
}