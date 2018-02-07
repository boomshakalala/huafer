package com.huapu.huafen.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ArticleEditLoopActivity;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.views.ClipSquareImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qwe on 17/4/18.
 */
public class EditTextDialog extends Dialog {

    @BindView(R.id.cutImage)
    TextView cutImage;
    @BindView(R.id.rotateImage)
    TextView rotateImage;
    @BindView(R.id.lightenImage)
    TextView lightenImage;
    @BindView(R.id.imageView)
    ClipSquareImageView imageView;
    @BindView(R.id.operateLayout)
    LinearLayout operateLayout;
    @BindView(R.id.scaleLayout)
    RelativeLayout scaleLayout;
    @BindView(R.id.commonLayout)
    LinearLayout commonLayout;
    private FloridData.TitleMedia media;
    private Activity context;

    private Bitmap bitmapNew;

    private boolean hasLighten = false;
    private float lightenScale = 1.2f;

    public EditTextDialog(@NonNull Context context) {
        super(context);
        setCanceledOnTouchOutside(true);
    }

    public EditTextDialog(@NonNull Activity context, FloridData.TitleMedia media) {
        super(context);
        if (media == null || TextUtils.isEmpty(media.url)) {
            throw new RuntimeException("media can not be null and media's url can not be empty");
        }
        setCanceledOnTouchOutside(true);
        this.media = media;
        this.context = context;
    }

    public EditTextDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_edit_text, null);
        setContentView(view);
        ButterKnife.bind(this, view);
        getWindow().setBackgroundDrawableResource(android.R.color.background_dark);
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        Animation animation = AnimationUtils.loadAnimation(getContext(),
                R.anim.alpha);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });

        initView();
    }

    private void initView() {
        String uri;
        if (media.url.startsWith(MyConstants.HTTP) || media.url.startsWith(MyConstants.HTTPS)) {
            uri = media.url;
        } else {
            uri = MyConstants.FILE + media.url;
        }

        ImageLoader.loadBitmap(getContext(), uri, new BitmapCallback() {
            @Override
            public void onBitmapDownloaded(Bitmap bitmap) {
                if (bitmap == null)
                    return;
                setBitmap(bitmap);
            }
        });
    }

    private void setBitmap(final Bitmap bitmap) {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (media.matrix != null && media.matrix.length > 0) {
                    Bitmap copyBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(copyBmp);
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    ColorMatrix colorMatrix = new ColorMatrix();   //新建颜色矩阵对象
                    colorMatrix.set(media.matrix);
                    paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                    canvas.drawBitmap(copyBmp, 0, 0, paint);
                    imageView.setImageBitmap(copyBmp);
                } else {
                    imageView.setImageBitmap(bitmap);
                }

                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setBorderWeight(1, 1);
                    }
                });
            }
        });
    }

    @OnClick({R.id.cutImage, R.id.rotateImage, R.id.lightenImage, R.id.onePointOne, R.id.threePointFour, R.id.fourPointThree, R.id.closeImage, R.id.confirmImage, R.id.saveText, R.id.disCardText})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cutImage:
                operateLayout.setVisibility(View.GONE);
                scaleLayout.setVisibility(View.VISIBLE);
                commonLayout.setVisibility(View.GONE);
                break;
            case R.id.rotateImage:
                if (null != bitmapNew) {
                    rotateImage(bitmapNew);
                } else {
                    if (null == imageView.getDrawable() || null == ((BitmapDrawable) imageView.getDrawable()).getBitmap()) {
                        return;
                    }
                    Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    rotateImage(imageBitmap);
                }
                break;
            case R.id.lightenImage:
                if (null == imageView.getDrawable() || null == ((BitmapDrawable) imageView.getDrawable()).getBitmap()) {
                    return;
                }
                if (hasLighten) {
                    hasLighten = false;
                    lightenScale = 0.8f;
                } else {
                    hasLighten = true;
                    lightenScale = 1.25f;
                }
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                bitmapNew = bitmap.copy(Bitmap.Config.RGB_565, true);
                Canvas canvas = new Canvas(bitmapNew);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setScale(lightenScale, lightenScale, lightenScale, 1);
                paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                canvas.drawBitmap(bitmapNew, 0, 0, paint);
                imageView.setImageBitmap(bitmapNew);
                break;
            case R.id.onePointOne:
                selectedItem(0);
                break;
            case R.id.threePointFour:
                selectedItem(1);
                break;
            case R.id.fourPointThree:
                selectedItem(2);
                break;
            case R.id.closeImage:
                commonLayout.setVisibility(View.VISIBLE);
                operateLayout.setVisibility(View.VISIBLE);
                scaleLayout.setVisibility(View.GONE);
                break;
            case R.id.confirmImage:
                bitmapNew = imageView.clip();
                commonLayout.setVisibility(View.VISIBLE);
                operateLayout.setVisibility(View.VISIBLE);
                scaleLayout.setVisibility(View.GONE);
                break;
            case R.id.saveText:
                if (context instanceof ArticleEditLoopActivity) {
                    ArticleEditLoopActivity editLoopActivity = (ArticleEditLoopActivity) context;
                    Bitmap imageBitmap = bitmapNew;
                    if (null == imageBitmap) {
                        imageBitmap = imageView.clip();
                    }
                    BufferedOutputStream bos;
                    try {
                        String sdCardDir = context.getCacheDir().toString();
                        File file = new File(sdCardDir, System.currentTimeMillis() + ".png");
                        bos = new BufferedOutputStream(
                                new FileOutputStream(file));
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        bos.flush();
                        bos.close();
                        editLoopActivity.upDatePic(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                this.dismiss();
                break;
            case R.id.disCardText:
                this.dismiss();
                break;
            default:
                break;
        }
    }

    private void rotateImage(Bitmap bitmap) {
        if (null == bitmap) {
            return;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(90, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        bitmapNew = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        imageView.setImageBitmap(bitmapNew);
    }

    private void selectedItem(int index) {
        int widthWeight = 4;
        int heightWeight = 3;

        switch (index) {
            case 0:
                widthWeight = 1;
                heightWeight = 1;
                break;
            case 1:
                widthWeight = 3;
                heightWeight = 4;
                break;
            case 2:
                widthWeight = 4;
                heightWeight = 3;
                break;
        }
        imageView.setBorderWeight(widthWeight, heightWeight);
    }

}
