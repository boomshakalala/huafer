package com.huapu.huafen.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.BasePostprocessor;

/**
 * Created by admin on 2017/5/10.
 */

public class ColorMatrixPostprocessor extends BasePostprocessor {

    private float[] matrix;

    public ColorMatrixPostprocessor(float[] matrix) {
        this.matrix = matrix;
    }

//    @Override
//    public void process(Bitmap dest, Bitmap source) {
//        super.process(dest, source);
//
//        Canvas canvas = new Canvas(dest);
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        ColorMatrix colorMatrix = new ColorMatrix();   //新建颜色矩阵对象
//        colorMatrix.set(matrix);
//        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
//        canvas.drawBitmap(source, 0, 0, paint);
//    }

    @Override
    public void process(Bitmap bitmap) {
        super.process(bitmap);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorMatrix colorMatrix = new ColorMatrix();   //新建颜色矩阵对象
        colorMatrix.set(matrix);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("color=" + matrix.toString());
    }
}
