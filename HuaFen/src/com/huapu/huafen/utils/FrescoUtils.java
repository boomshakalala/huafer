package com.huapu.huafen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.CacheChoice;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.request.ImageRequestBuilder;


public class FrescoUtils {

    private static final String REMOTE_URL_SIGN = "http://";
    private static final String FILE_PATH_SIGN = "file://";
    private static Handler mainHandler = new Handler();

    public static ImageRequest buildRequest(Uri uri, int width, int height) {

        //RequestLevel.DISK_CACHE，不管用，暂时不知道解决办法,已经给facebook提交了issue

        //	ImageRequest request = ImageRequestBuilder
        //		    .newBuilderWithSource(uri)
        //		    .setAutoRotateEnabled(true)
        //		    .setLocalThumbnailPreviewsEnabled(true)
        //		    .setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH)
        //		    .setProgressiveRenderingEnabled(false)
        //		    .setResizeOptions(new ResizeOptions(width, height))
        //		    .build();
        return (width != 0 && height != 0) ? ImageRequestBuilder.newBuilderWithSource(uri).setAutoRotateEnabled(true).setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH).setResizeOptions(new ResizeOptions(width, height)).build() : ImageRequestBuilder.newBuilderWithSource(uri).setLocalThumbnailPreviewsEnabled(true).setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH).build();
    }


    /**
     * 这里的图片不在默认文件缓存，不会因为其他图片频繁修改而删除
     *
     * @param uri
     * @return
     */
    public static ImageRequest buildSmallRequest(Uri uri) {

        return ImageRequestBuilder.newBuilderWithSource(uri).setAutoRotateEnabled(true).setCacheChoice(CacheChoice.SMALL).setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH).build();
    }

    public static ImageRequest buildSmallRequest(Uri uri, int width, int height) {
        return ImageRequestBuilder.newBuilderWithSource(uri).setAutoRotateEnabled(true).setCacheChoice(CacheChoice.SMALL).setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH).setResizeOptions(new ResizeOptions(width, height)).build();
    }


    public static Uri getUri(String url) {
        return StringUtils.isEmpty(url) ? Uri.parse("") : Uri.parse(url);
    }

    public static Uri getFileUri(String path) {
        return StringUtils.isEmpty(path) ? Uri.parse("") : Uri.parse("file://" + path);
    }

    public static Uri getResUri(int resId) {
        return Uri.parse("res:///" + resId);
    }

    public static Uri getAssesUri(String path) {
        return StringUtils.isEmpty(path) ? Uri.parse("") : Uri.parse("asset:///" + path);
    }



//    public static GenericDraweeHierarchy getNormalHierarchy(Context context) {
//        GenericDraweeHierarchyBuilder builder =
//                new GenericDraweeHierarchyBuilder(context.getResources());
//
//        ColorDrawable holderImage = new ColorDrawable(ContextCompat.getColor(context, R.color.image_back_color));
//
//
//        GenericDraweeHierarchy hierarchy = builder
//                .setFadeDuration(300)
//                .setPlaceholderImage(holderImage)
//                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.FIT_XY)
//                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
//                .setBackground(holderImage)
//                .build();
//
//        return hierarchy;
//    }


    public static void smallReqImage(SimpleDraweeView view, String url) {
        view.setController(Fresco.newDraweeControllerBuilder()
                .setImageRequest(buildSmallRequest(getUri(url)))
                .setOldController(view.getController())
                .build());
    }

    public static void smallReqImageWithListener(SimpleDraweeView view, String url, ControllerListener listener) {
        view.setController(Fresco.newDraweeControllerBuilder()
                .setImageRequest(buildSmallRequest(getUri(url)))
                .setOldController(view.getController())
                .setControllerListener(listener)
                .build());


    }

    public static long Fib(int n) {
        if (n < 2) {
            return n;
        } else {
            return Fib(n - 1) + Fib(n - 2);
        }
    }


    public static void bind(SimpleDraweeView draweeView, Uri uri) {
        bind(draweeView, uri, 0, 0);
    }

    public static void bindAvatarThumbnail(SimpleDraweeView simpleDraweeView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.toLowerCase().startsWith(REMOTE_URL_SIGN)) {
            bindByUrl(simpleDraweeView, url, 100, 100, null);
        } else {
            bindByFilePath(simpleDraweeView, url);
        }
    }

    public static void bindAvatarOriginal(SimpleDraweeView simpleDraweeView, String url, LoadImageListener loadImageListener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.toLowerCase().startsWith(REMOTE_URL_SIGN)) {
            bindByUrl(simpleDraweeView, url, 0, 0, loadImageListener);
        } else {
            bindByFilePath(simpleDraweeView, url);
        }
    }

    public static void bindMsgOriginal(SimpleDraweeView simpleDraweeView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.toLowerCase().startsWith(REMOTE_URL_SIGN)) {
            bindByUrl(simpleDraweeView, url, 0, 0, null);
        } else {
            bindByFilePath(simpleDraweeView, url);
        }

    }

    public static void bindMsgThumbnail(SimpleDraweeView simpleDraweeView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.toLowerCase().startsWith(REMOTE_URL_SIGN)) {
            bindByUrl(simpleDraweeView, url, 0, 0, null);
        } else {
            bindByFilePath(simpleDraweeView, url);
        }
    }

    public static void bindCoverSmall(SimpleDraweeView simpleDraweeView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.toLowerCase().startsWith(REMOTE_URL_SIGN)) {
            bindByUrl(simpleDraweeView, url, 324, 324, null);
        } else {
            bindByFilePath(simpleDraweeView, url);
        }
    }

    public static void bindCoverOriginal(SimpleDraweeView simpleDraweeView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.toLowerCase().startsWith(REMOTE_URL_SIGN)) {
            bindByUrl(simpleDraweeView, url, 0, 0, null);
        } else {
            bindByFilePath(simpleDraweeView, url);
        }
    }

    public static void bindCoverLarge(SimpleDraweeView simpleDraweeView, String url,int width,int height) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.toLowerCase().startsWith(REMOTE_URL_SIGN)) {
            bindByUrl(simpleDraweeView, url, width, height, null);
        } else {
            bindByFilePath(simpleDraweeView, url);
        }
    }

    private static void bindByUrl(final SimpleDraweeView simpleDraweeView, final String url, final int width, final int height, final LoadImageListener loadImageListener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (width == 0 && height != 0) {
            return;
        }
        if (width != 0 && height == 0) {
            return;
        }
        if (width == 0 && height == 0) {
            bind(simpleDraweeView, url, loadImageListener);
            return;
        }
        int endIndex = url.lastIndexOf(".");

        if (endIndex < 1) {
            return;
        }
        final String realUrl = url.substring(0, endIndex) + "_" + width + "-" + height + url.substring(endIndex, url.length());

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(
                            String id,
                            @Nullable ImageInfo imageInfo,
                            @Nullable Animatable anim) {
                    }

                    @Override
                    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        if (loadImageListener != null) {
                            loadImageListener.onFailed();
                        }
                        if (width == 0 && height == 0) {
                            return;
                        }
                        bind(simpleDraweeView, url, loadImageListener);
                    }
                })
                .setUri(getUri(realUrl))
                // other setters
                .build();
        simpleDraweeView.setController(controller);
    }

    private static void bindByFilePath(SimpleDraweeView simpleDraweeView, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Uri uri;
        if (path.toLowerCase().startsWith(FILE_PATH_SIGN)) {
            uri = Uri.parse(path);
        } else {
            uri = getFileUri(path);
        }
        if (uri == null) {
            return;
        }
        bind(simpleDraweeView, uri);
    }

    public static void bind(SimpleDraweeView draweeView, String url) {
        if (!TextUtils.isEmpty(url)) {
            bind(draweeView, Uri.parse(url), 0, 0);
        } else {
            bind(draweeView, Uri.parse(""), 0, 0);
        }
    }

    public static void bind(SimpleDraweeView draweeView, Uri uri, int size) {
        bind(draweeView, uri, size, size);
    }

    private static void bind(SimpleDraweeView simpleDraweeView, final String url, final LoadImageListener loadImageListener) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(
                            String id,
                            @Nullable ImageInfo imageInfo,
                            @Nullable Animatable anim) {
                        if (loadImageListener != null) {
                            loadImageListener.onSuccess(url);
                        }
                    }
                    @Override
                    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        if (loadImageListener != null) {
                            loadImageListener.onFailed();
                        }
                    }
                })
                .setUri(getUri(url))
                // other setters
                .build();
        simpleDraweeView.setController(controller);
    }

    public static void bind(SimpleDraweeView draweeView, Uri uri, int width, int height) {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder
                .newBuilderWithSource(uri).setAutoRotateEnabled(true);

        if (width != 0 && height != 0) {
            imageRequestBuilder.setResizeOptions(
                    new ResizeOptions(
                            width,
                            height));
        }

//        DelayPostprocessor postprocessor = DelayPostprocessor.getFastPostprocessor();
//        imageRequestBuilder.setPostprocessor(postprocessor);

        // Create the Builder
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build());
        builder.setOldController(draweeView.getController());
        builder.setTapToRetryEnabled(true);
        draweeView.setController(builder.build());
    }

    public interface LoadImageListener {

        void onSuccess(String url);

        void onFailed();
    }
}
