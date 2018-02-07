package com.huapu.huafen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.huapu.huafen.callbacks.BitmapCallback;

/**
 * 图片加载-fresco
 * Created by admin on 2017/3/2.
 */
public class ImageLoader {
    public static final int IMG_SIZE_MIN = 256;
    public static final int IMG_SIZE_MIDDLE = 512;
    public static final int IMG_SIZE_LARGE = 768;
    public static final int IMG_SIZE_MAX = 1280;
    public static final int FADE_DURATION = 128;

    public static void loadImage(SimpleDraweeView simpleDraweeView, String url) {
        if (TextUtils.isEmpty(url))
            return;
        Uri uri = Uri.parse(url);
        simpleDraweeView.setImageURI(uri);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView, String url, int duration) {
        if (TextUtils.isEmpty(url))
            return;
        Uri uri = Uri.parse(url);

        GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
        hierarchy.setFadeDuration(duration);

        simpleDraweeView.setHierarchy(hierarchy);
        simpleDraweeView.setImageURI(uri);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView,
                                 String uri,
                                 int placeHolder,
                                 int failureHolder) {

        loadImage(simpleDraweeView, uri, placeHolder, failureHolder, ScalingUtils.ScaleType.FOCUS_CROP,
                ScalingUtils.ScaleType.FOCUS_CROP, ScalingUtils.ScaleType.FOCUS_CROP);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView,
                                 String uri,
                                 Drawable placeHolder,
                                 Drawable failureHolder) {

        loadImage(simpleDraweeView, uri, placeHolder, failureHolder, ScalingUtils.ScaleType.FOCUS_CROP,
                ScalingUtils.ScaleType.FOCUS_CROP, ScalingUtils.ScaleType.FOCUS_CROP);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView,
                                 String uri,
                                 int placeHolder,
                                 int failureHolder,
                                 ScalingUtils.ScaleType scaleType) {

        loadImage(simpleDraweeView, uri, placeHolder, failureHolder, scaleType, scaleType, scaleType);
    }

    public static void resumeLoadImage() {
        Fresco.getImagePipeline().resume();
    }

    public static void pauseLoadImage() {
        Fresco.getImagePipeline().pause();
    }

    /**
     * @param aspectRatio 宽高比
     */
    public static void resizeSmall(SimpleDraweeView simpleDraweeView, String path, float aspectRatio) {
        if (TextUtils.isEmpty(path))
            return;
        resizeSmall(simpleDraweeView, Uri.parse(path), aspectRatio);
    }

    /**
     * @param aspectRatio 宽高比
     */
    public static void resizeSmall(SimpleDraweeView simpleDraweeView, Uri uri, float aspectRatio) {
        int width = IMG_SIZE_MIN;
        int height = (int) (width / aspectRatio);
        resize(simpleDraweeView, uri, width, height);
    }

    /**
     * @param aspectRatio 宽高比
     */
    public static void resizeMiddle(SimpleDraweeView simpleDraweeView, String path, float aspectRatio) {
        if (TextUtils.isEmpty(path))
            return;
        int width = IMG_SIZE_MIDDLE;
        int height = (int) (width / aspectRatio);
        resize(simpleDraweeView, Uri.parse(path), width, height);
    }

    /**
     * @param aspectRatio 宽高比
     */
    public static void resizeLarge(SimpleDraweeView simpleDraweeView, String path, float aspectRatio) {
        if (TextUtils.isEmpty(path))
            return;
        int width = IMG_SIZE_LARGE;
        int height = (int) (width / aspectRatio);
        resize(simpleDraweeView, Uri.parse(path), width, height);
    }

    private static void resize(SimpleDraweeView simpleDraweeView, Uri uri, int width, int height) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height, IMG_SIZE_MAX))
                .setAutoRotateEnabled(true) //如果图片是侧着,可以自动旋转
                .build();

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(simpleDraweeView.getController())
                .setAutoPlayAnimations(true) //自动播放gif动画
                .build();

        simpleDraweeView.setController(controller);
    }

    public static void loadImageWrapContent(final SimpleDraweeView iv, String url) {
        ControllerListener listener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                if (imageInfo != null) {
                    updateIv(iv, imageInfo);
                }
            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                if (imageInfo != null) {
                    updateIv(iv, imageInfo);
                }
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(url)
                .setControllerListener(listener)
                .build();

        iv.setController(controller);
    }

    private static void updateIv(SimpleDraweeView iv, ImageInfo imageInfo) {
        ViewGroup.LayoutParams params = iv.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        iv.setLayoutParams(params);
        iv.setAspectRatio((float) imageInfo.getWidth() / (float) imageInfo.getHeight());
    }

    // 下载图片
    public static void loadBitmap(Context context, Uri uri, final BitmapCallback callback) {
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        DataSource<CloseableReference<CloseableImage>> dataSource
                = imagePipeline.fetchDecodedImage(imageRequest, context);

        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                //bitmap即为下载所得图片
                callback.onBitmapDownloaded(bitmap);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                callback.onBitmapDownloaded(null);
            }
        }, CallerThreadExecutor.getInstance());
    }

    public static void loadBitmap(Context context, String uri, final BitmapCallback callback) {
        if (TextUtils.isEmpty(uri))
            return;
        loadBitmap(context, Uri.parse(uri), callback);
    }

    private static void resize(SimpleDraweeView simpleDraweeView, Uri uri, int width, int height,
                               float maxBitmapSize, float[] matrix) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(new ColorMatrixPostprocessor(matrix))
                .setResizeOptions(new ResizeOptions(width, height, maxBitmapSize))
                .setAutoRotateEnabled(true) //如果图片是侧着,可以自动旋转
                .build();

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(simpleDraweeView.getController())
                .setAutoPlayAnimations(true) //自动播放gif动画
                .build();

        simpleDraweeView.setController(controller);
    }

    private static void loadImage(SimpleDraweeView simpleDraweeView,
                                  String uri,
                                  Drawable placeHolder,
                                  Drawable failureHolder,
                                  ScalingUtils.ScaleType actualImageScaleType,
                                  ScalingUtils.ScaleType placeholderImageScaleType,
                                  ScalingUtils.ScaleType failureImageScaleType) {

        if (simpleDraweeView == null) {
            return;
        }

        GenericDraweeHierarchy hierarchy;
        try {
            hierarchy = simpleDraweeView.getHierarchy();
            hierarchy.setFadeDuration(FADE_DURATION);
            hierarchy.setActualImageScaleType(actualImageScaleType);
            hierarchy.setPlaceholderImage(placeHolder, placeholderImageScaleType);
            hierarchy.setFailureImage(failureHolder, failureImageScaleType);
        } catch (Exception e) {
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(
                    simpleDraweeView.getContext().getResources());

            hierarchy = builder.setFadeDuration(FADE_DURATION).
                    setActualImageScaleType(actualImageScaleType).
                    setPlaceholderImage(placeHolder, placeholderImageScaleType).
                    setFailureImage(failureHolder, failureImageScaleType).
                    build();
            simpleDraweeView.setHierarchy(hierarchy);
        }

        simpleDraweeView.setImageURI(uri);
    }

    private static void loadImage(SimpleDraweeView simpleDraweeView,
                                  String uri,
                                  int placeHolder,
                                  int failureHolder,
                                  ScalingUtils.ScaleType actualImageScaleType,
                                  ScalingUtils.ScaleType placeholderImageScaleType,
                                  ScalingUtils.ScaleType failureImageScaleType) {

        if (simpleDraweeView == null || TextUtils.isEmpty(uri)) {
            return;
        }

        GenericDraweeHierarchy hierarchy;
        try {
            hierarchy = simpleDraweeView.getHierarchy();
            hierarchy.setFadeDuration(FADE_DURATION);
            hierarchy.setActualImageScaleType(actualImageScaleType);
            hierarchy.setPlaceholderImage(placeHolder, placeholderImageScaleType);
            hierarchy.setFailureImage(failureHolder, failureImageScaleType);
        } catch (Exception e) {
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(
                    simpleDraweeView.getContext().getResources());
            hierarchy = builder.setFadeDuration(FADE_DURATION).
                    setActualImageScaleType(actualImageScaleType).
                    setPlaceholderImage(placeHolder, placeholderImageScaleType).
                    setFailureImage(failureHolder, failureImageScaleType).
                    build();
            simpleDraweeView.setHierarchy(hierarchy);
        }

        simpleDraweeView.setImageURI(uri);
    }
}
