package com.huapu.huafen.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.huapu.huafen.MyApplication;
import com.huapu.huafen.common.MyConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;

/**
 * @author liang_xs
 * @date 2016-03-27
 */
public class ImageUtils {
    // public static String getPhotopath() {
    // // String picName = TimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss");
    // // return FileUtils.getInst().getPhotoSavedPath() + "/"+ picName+".jpg";
    // // 照片全路径
    // String fileName = "";
    // // 文件夹路径
    // String pathUrl = FileUtils.SD_APP_PATH + "/";
    // // String imageName = "stickerCamera.jpg";
    // String imageName = DateTimeUtils.dtFormat(new Date(),
    // "yyyyMMddHHmmss")+".jpg";
    // File file = new File(pathUrl);
    // file.mkdirs();// 创建文件夹
    // fileName = pathUrl + imageName;
    // return fileName;
    // }

    private static final int STROKE_WIDTH = 4;

    /**
     * 旋转图片，使其显示正常
     *
     * @param bitmap      原始图片
     * @param orientation 图片旋转属性
     * @return 旋转正常后的图片
     */
    public static Bitmap getTotateBitmap(Bitmap bitmap, int orientation) {
        // 处理图片旋转的问题
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90: {// 旋转了90度
                matrix.setRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }
            break;
            case ExifInterface.ORIENTATION_ROTATE_180: {// 旋转了180度
                matrix.setRotate(180);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }
            break;
            case ExifInterface.ORIENTATION_ROTATE_270: {// 旋转了270度
                matrix.setRotate(270);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }
            break;
            default:
                break;
        }
        return bitmap;
    }

    /**
     * 相片按相框的比例动态缩放
     *
     * @param context
     * @param width   模板宽度
     * @param height  模板高度
     * @return
     */
    public static Bitmap upImageSize(Context context, Bitmap bmp, int width, int height) {
        if (bmp == null) {
            return null;
        }
        // 计算比例
        float scaleX = (float) width / bmp.getWidth();// 宽的比例
        float scaleY = (float) height / bmp.getHeight();// 高的比例
        //新的宽高
        int newW = 0;
        int newH = 0;
        if (scaleX > scaleY) {
            newW = (int) (bmp.getWidth() * scaleX);
            newH = (int) (bmp.getHeight() * scaleX);
        } else if (scaleX <= scaleY) {
            newW = (int) (bmp.getWidth() * scaleY);
            newH = (int) (bmp.getHeight() * scaleY);
        }
        return Bitmap.createScaledBitmap(bmp, newW, newH, true);
    }

    /**
     * 保存图片方法
     **/
    public static boolean saveMyBitmap(File file, Bitmap mBitmap)
            throws IOException {
        boolean saveComplete = true;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(file);
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            // 计算缩放的比例
            int finalWidth = 800;
            int finalHeight = (int) (finalWidth * 1.0 * (height * 1.0 / width * 1.0));
            double x = width * finalHeight;
            double y = height * finalWidth;

            if (x > y) {
                finalHeight = (int) (y / (double) width);
            } else if (x < y) {
                finalWidth = (int) (x / (double) height);
            }

            if (finalWidth > width && finalHeight > height) {
                finalWidth = width;
                finalHeight = height;
            }
            Matrix matrix = new Matrix();
            matrix.reset();
            // 计算宽高缩放率
            float scaleWidth = ((float) finalWidth) / (float) width;
            float scaleHeight = ((float) finalHeight) / (float) height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, (int) width,
                    (int) height, matrix, true);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            // 回收内存空间
            mBitmap.recycle();
            System.gc();
        } catch (FileNotFoundException e) {
            saveComplete = false;
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            saveComplete = false;
        }
        return saveComplete;
    }

    /**
     * 加载本地图片
     * <p>
     * 本地图片路径
     *
     * @return Bitmap 本地图片不存在时返回null
     */
    public static Bitmap getLoacalBitmap(Context context, String file) {
        Bitmap bitmap = null;
        if (file.contains("file://")) {
            file = file.replace("file://", "");
        }
        // 进一步判断文件是否存在
        File check = new File(file);
        // 本地图片路径不存在，返回null
        if (!check.exists()) {
            return null;
        }
        // 读取图片
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = false;
            // 表示16位位图,565代表对应三原色占的位数
            newOpts.inPreferredConfig = Config.RGB_565;
            newOpts.inInputShareable = true;
            newOpts.inPurgeable = true;// 设置图片可以被回收
            bitmap = BitmapFactory.decodeFile(file, newOpts);
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//	        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//	        bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        } catch (Exception e) {
            e.printStackTrace();
            // 读取图片出错时返回null
            return null;
        }
        return bitmap;
    }

    public static Bitmap comp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 以省内存的方式读取显示图片 避免om
     *
     * @return Options
     */
    public static void showImage(Context context, ImageView iv, int resouse) {

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resouse);
        iv.setImageBitmap(BitmapFactory.decodeStream(is, null, opt));
        iv.setScaleType(ScaleType.CENTER_CROP);
    }

    public static Bitmap articleCompressImage(String path, int width, int height) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        BufferedInputStream in;
        Bitmap bitmap = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();

            int w = options.outWidth;
            int h = options.outHeight;
            int be = 1;
            if (w > h && w > width) {
                be = options.outWidth / width;
            } else if (w < h && h > height) {
                be = options.outHeight / height;
            }
            if (be <= 0)
                be = 1;
            options.inSampleSize = be;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(in, null, options);


//            int i = 0;
//            while (true) {
//                if ((options.outWidth >> i <= 1000)
//                        && (options.outHeight >> i <= 1000)) {
//                    in = new BufferedInputStream(new FileInputStream(new File(
//                            path)));
//                    options.inSampleSize = (int) Math.pow(2.0D, i);
//                    options.inJustDecodeBounds = false;
//                    bitmap = BitmapFactory.decodeStream(in, null, options);
//                    break;
//                }
//                i += 1;
//            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static String saveFile(Bitmap bm, String fileName) throws IOException {
        File myCaptureFile = new File(MyApplication.getApplication().getCacheDir(), fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return myCaptureFile.getAbsolutePath();
    }

    /**
     * 压缩图片
     */
    public static Bitmap revitionImageSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        BufferedInputStream in;
        Bitmap bitmap = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;
            while (true) {
                if ((options.outWidth >> i <= 1000)
                        && (options.outHeight >> i <= 1000)) {
                    in = new BufferedInputStream(new FileInputStream(new File(
                            path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 根据InputStream获取图片实际的宽度和高度
     *
     * @param imageStream
     * @return
     */
    public static ImageSize getImageSize(InputStream imageStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);
        return new ImageSize(options.outWidth, options.outHeight);
    }

    public static int calculateInSampleSize(ImageSize srcSize,
                                            ImageSize targetSize) {
        // 源图片的宽度
        int width = srcSize.width;
        int height = srcSize.height;
        int inSampleSize = 1;

        int reqWidth = targetSize.width;
        int reqHeight = targetSize.height;

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比�?
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    /**
     * 根据ImageView获得图片压缩的宽和高
     *
     * @param view
     * @return
     */
    public static ImageSize getImageViewSize(View view) {

        ImageSize imageSize = new ImageSize();

        imageSize.width = getExpectWidth(view);
        imageSize.height = getExpectHeight(view);

        return imageSize;
    }

    /**
     * 根据view获得期望的高
     *
     * @param view
     * @return
     */
    private static int getExpectHeight(View view) {

        int height = 0;
        if (view == null)
            return 0;

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        // 如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
        if (params != null
                && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = view.getWidth(); // 获得实际的宽�?
        }
        if (height <= 0 && params != null) {
            height = params.height; // 获得布局文件中的声明的宽�?
        }

        if (height <= 0) {
            height = getImageViewFieldValue(view, "mMaxHeight");// 获得设置的最大的宽度
        }

        // 如果宽度还是没有获取到，憋大招，使用屏幕的宽�?
        if (height <= 0) {
            DisplayMetrics displayMetrics = view.getContext().getResources()
                    .getDisplayMetrics();
            height = displayMetrics.heightPixels;
        }

        return height;
    }

    /**
     * 根据view获得期望的宽
     *
     * @param view
     * @return
     */
    private static int getExpectWidth(View view) {
        int width = 0;
        if (view == null)
            return 0;

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        // 如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
        if (params != null
                && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = view.getWidth(); // 获得实际的宽�?
        }
        if (width <= 0 && params != null) {
            width = params.width; // 获得布局文件中的声明的宽�?
        }

        if (width <= 0)

        {
            width = getImageViewFieldValue(view, "mMaxWidth");// 获得设置的最大的宽度
        }
        // 如果宽度还是没有获取到，憋大招，使用屏幕的宽�?
        if (width <= 0)

        {
            DisplayMetrics displayMetrics = view.getContext().getResources()
                    .getDisplayMetrics();
            width = displayMetrics.widthPixels;
        }

        return width;
    }

    /**
     * 通过反射获取imageview的某个属性
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
        }
        return value;

    }

    public static final Bitmap grey(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap.createBitmap(width, height,
                Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return faceIconGreyBitmap;
    }

    public static Bitmap getBitmap(Context context, String filename) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {

            InputStream is = am.open(filename);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static Bitmap toRoundBitmap(File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        return toRoundBitmap(bitmap);
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            left = 0;
            bottom = width;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);

        // 画白色圆圈
        paint.reset();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH / 2,
                paint);
        return output;
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap, boolean drawCircle) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            left = 0;
            bottom = width;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        if (drawCircle) {
            // 画白色圆圈
            paint.reset();
            paint.setColor(0xFFf5b800);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);
            canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH
                    / 2, paint);
        }
        return output;
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap, boolean drawCircle, int roundPx) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            top = 0;
            left = 0;
            bottom = width;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(bitmap, src, dst, paint);
        if (drawCircle) {
            // 画白色圆圈
            paint.reset();
            paint.setColor(0xFFf5b800);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);
            canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH
                    / 2, paint);
        }
        return output;
    }

    public static Bitmap toRoundBitmap(Context context, String filePath) {
        Bitmap bitmap = getBitmap(context, filePath);
        return toRoundBitmap(bitmap);
    }

    /**
     * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
     * <p>
     * A.网络路径: url="http://blog.foreverlove.us/image.png" ;
     * <p>
     * B.本地路径:url="file://mnt/sdcard/photo/image.png";
     * <p>
     * C.支持的图片格式 ,png, jpg,bmp,gif等等
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            in.close();
            dataStream.close();
            out.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    /**
     * 调用系统的裁剪
     *
     * @param uri
     */
    public static void cropPhoto(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_CROP);
    }

    /**
     * 调用系统的裁剪
     *
     * @param uri
     */


    public static void FlowerPhoto(Activity activity, Uri uri) {
        Uri mDestinationUri;
        mDestinationUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "cropImage.png"));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra("output", mDestinationUri);
        activity.startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_CROP);
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(
                        c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

    // 加水印 也可以加文字
    public static Bitmap watermarkBitmap(Bitmap src, Bitmap watermark, String title) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        //需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        Paint paint = new Paint();
        //加入图片
        if (watermark != null) {
            int ww = watermark.getWidth();
            int wh = watermark.getHeight();
            paint.setAlpha(50);
            cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, paint);// 在src的右下角画入水印
        }
        //加入文字
        if (title != null) {
            String familyName = "宋体";
            Typeface font = Typeface.create(familyName, Typeface.BOLD);
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.RED);
            textPaint.setTypeface(font);
            textPaint.setTextSize(22);
            //这里是自动换行的
            StaticLayout layout = new StaticLayout(title, textPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            layout.draw(cv);
            //文字就加左上角算了
            //cv.drawText(title,0,40,paint);
        }
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储
        return newb;
    }

    public static class ImageSize {
        int width;
        int height;

        public ImageSize() {
        }

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "ImageSize{" + "width=" + width + ", height=" + height + '}';
        }
    }
}
