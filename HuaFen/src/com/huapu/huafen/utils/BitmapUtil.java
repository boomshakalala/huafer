package com.huapu.huafen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;

import com.huapu.huafen.MyApplication;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.ImageSize;
import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.callbacks.CommonCallback;
import com.huapu.huafen.common.MyConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BitmapUtil {
    public static Bitmap createBitmap(Context context, int bitmapID) {
        Bitmap markerBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.maker_bg);
        Bitmap heardBitmap = BitmapFactory.decodeResource(
                context.getResources(), bitmapID);
        return createBitmap(markerBitmap, heardBitmap);
    }

    public static Bitmap createBitmap(Context context, Bitmap bitmap) {
        Bitmap markerBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.maker_bg);
//		Bitmap bitmap = initImageLoader(url);
        return createBitmap(markerBitmap, bitmap);
    }

    public static Bitmap createBitmap(Bitmap markerBitmap, Bitmap headBitmap) {
        int wbg = markerBitmap.getWidth();
        int hbg = markerBitmap.getHeight();
        int wheard = headBitmap.getWidth();
        int hheard = headBitmap.getHeight();
//		System.out.println(wbg+"   "+hbg);
        Bitmap newb = Bitmap.createBitmap(wbg, hbg, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas canvas = new Canvas(newb);
        canvas.drawBitmap(markerBitmap, 0, 0, null);
        int newWidth = wbg - 20;
        int newheight = hbg / 3 * 2;
        // 定义缩放的高和宽的比例
        float sw = ((float) newWidth) / wheard;
        float sh = ((float) newheight) / hheard;
        Matrix matrix = new Matrix();
        matrix.postScale(sw, sh);
        Bitmap resizeBitmap = Bitmap.createBitmap(headBitmap, 0, 0, wheard, hheard,
                matrix, true);
        canvas.drawBitmap(resizeBitmap, (wbg - newWidth) / 2, 10, null);
        return newb;
    }

    public static Bitmap createRepeater(int width, Bitmap src) {
        int count = (width + src.getWidth() - 1) / src.getWidth(); //计算出平铺填满所给width（宽度）最少需要的重复次数
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth() * count, src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx) {
            canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
        }
        return bitmap;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public static void saveAsBitmap(String url, final float[] matrix, final CommonCallback callback) {
        if (!TextUtils.isEmpty(url)) {
            callback.callback(null);
            return;
        }

        String uri;
        if (url.startsWith(MyConstants.HTTP) || url.startsWith(MyConstants.HTTPS)) {
            uri = url;
        } else {
            uri = MyConstants.FILE + url;
        }

        ImageLoader.loadBitmap(MyApplication.getApplication(), uri, new BitmapCallback() {
            @Override
            public void onBitmapDownloaded(Bitmap bitmap) {
                if (bitmap == null)
                    callback.callback(null);
                Bitmap copyBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(copyBmp);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                ColorMatrix colorMatrix = new ColorMatrix();   //新建颜色矩阵对象
                colorMatrix.set(matrix);
                paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                canvas.drawBitmap(copyBmp, 0, 0, paint);
                String fileName = System.currentTimeMillis() + ".jpg";
                String path = FileUtils.saveBitmap(copyBmp, FileUtils.getIconDir(), fileName);
                callback.callback(path);
            }
        });
    }

    /**
     * 获取合适大小的bitmap
     */
    public static Bitmap createImageThumbnail(File imageFile, int maxWidth) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = getInSampleSize(imageFile, maxWidth);
        return loadBitmap(imageFile, opts);
    }

    /**
     * 获取合适大小的inSampleSize
     */
    public static int getInSampleSize(File filePath, int maxWidth) {
        int inSampleSize;
        ImageSize size = getImgSize(filePath);
        inSampleSize = computeSampleSize(size, -1, maxWidth * maxWidth);
        return inSampleSize;
    }

    /**
     * 从给定文件加载图片
     */
    private static Bitmap loadBitmap(File imgpath, BitmapFactory.Options opts) {
        FileInputStream is = null;
        Bitmap b = null;
        opts.inPreferredConfig = Config.RGB_565;
        try {
            is = new FileInputStream(imgpath);
            b = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (null != is)
                is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 根据file文件获取图片大小
     */
    public static ImageSize getImgSize(File imageFile) {
        if (null == imageFile || !imageFile.exists()) {
            return null;
        }
        ImageSize imageSize;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),
                options);
        imageSize = new ImageSize(options.outWidth, options.outHeight);
        if (null != bmp && !bmp.isRecycled()) {
            bmp.recycle();
            // bmp = null;
        }
        return imageSize;
    }


    private static int computeSampleSize(ImageSize size, int minSideLength,
                                         int maxNumOfPixels) {
        if (size == null || size.getW() == 0 || size.getH() == 0) {
            return 2;
        }
        int initialSize = computeInitialSampleSize(size, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(ImageSize size,
                                                int minSideLength, int maxNumOfPixels) {
        double w = size.getW();
        double h = size.getH();
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
