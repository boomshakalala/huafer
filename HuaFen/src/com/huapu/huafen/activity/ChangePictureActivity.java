package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FilePathResolver;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Think on 2017/4/28.
 */

public class ChangePictureActivity extends BaseActivity {


    @BindView(R.id.iv_photo)
    SimpleDraweeView ivPhoto;
    @BindView(R.id.tvBtnBind)
    TextView tvBtnBind;
    @BindView(R.id.progress)
    ProgressBar progress;
    private String fileName;
    private String key;
    private String imgPath = "";
    private boolean isHeaderChange = false;
    private Bitmap head;// 头像Bitmap
    private boolean isUpdate = false;
    private String head_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        head_url = intent.getStringExtra("image");
        ImageLoader.loadImage(ivPhoto, head_url);
    }

    @OnClick({R.id.tvBtnBind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvBtnBind:
                fileName = System.currentTimeMillis() + "_icon.jpg";
                key = DateTimeUtils.getYearMonthDayFolder("/headIcon/") + fileName;
                imgPath = FileUtils.getIconDir() + fileName;
                PhotoDialog dialog = new PhotoDialog(this);
                dialog.setCameraCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intentCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        File out = new File(imgPath);
                        Uri uri = Uri.fromFile(out);
                        // 获取拍照后未压缩的原图片，并保存在uri路径中
                        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intentCamera, MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA);
                    }
                });
                dialog.setAlbumCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent1, MyConstants.INTENT_FOR_RESULT_TO_ALBUM);
                    }
                });
                dialog.show();
                break;

            case R.id.btnTitleBarLeft:
                if (isHeaderChange) {
                    upload2Ali();
                } else {
                    startRequestForUpdateUser();
                }
                break;


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                // 判断图片是否旋转，处理图片旋转
                ExifInterface exif;
                Bitmap bitmap = ImageUtils.getLoacalBitmap(this, imgPath);
                try {
                    exif = new ExifInterface(imgPath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    // 旋转图片，使其显示方向正常
                    Bitmap newBitmap = ImageUtils.getTotateBitmap(bitmap, orientation);
                    File out = new File(imgPath);
                    boolean save = ImageUtils.saveMyBitmap(out, newBitmap);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    if (newBitmap != null) {
                        newBitmap.recycle();
                        newBitmap = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //拍照
                ImageLoader.loadImage(ivPhoto, "file://" + imgPath);
                upload2Ali();
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM) {
                //本地
                File file1 = new File(FilePathResolver.getPath(this, data.getData()));
                imgPath = file1.toString();
                ImageLoader.loadImage(ivPhoto, "file://" + imgPath);
                upload2Ali();
            }

        }
    }

    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali() {
        if (!TextUtils.isEmpty(imgPath)) {
            Bitmap bm = ImageUtils.revitionImageSize(imgPath);
            if (bm != null) {
                FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                UploadPicTask task = new UploadPicTask();
                task.execute(imgPath, MyConstants.OSS_FOLDER_BUCKET, key);
            }
        } else {
            toast("请选择头像");
        }
    }

    private class UploadPicTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String urlPath = (String) params[0];
            String folder = (String) params[1];
            String key = (String) params[2];
            AliUpdateEvent updateEvent = new AliUpdateEvent(ChangePictureActivity.this, urlPath, folder, key);
            PutObjectResult result = updateEvent.putObjectFromLocalFile();
            if (result == null) {
            } else {
                response = key;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                //上传头像
                startRequestForUpdateUser();
            } else {
                toast("头像上传失败，请重试");
            }
        }

    }

    private void startRequestForUpdateUser() {
        HashMap<String, String> params = new HashMap<>();
        params.put("articleBackground", MyConstants.OSS_IMG_HEAD + key);
        LogUtil.i("lh", "修改封面图" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.SETPREFERENCES, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("lh", "修改封面图:" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "修改个人资料:" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        toast("更改封面图成功");
//                        FinishEvent event = new FinishEvent();
//                        event.isImage = true;
//                        EventBus.getDefault().post(event);
                    } else {
                        CommonUtils.error(baseResult, ChangePictureActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }


}
