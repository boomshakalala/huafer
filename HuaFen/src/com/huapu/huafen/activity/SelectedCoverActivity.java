package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.SelectedCoverAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.RecommendCoverBean;
import com.huapu.huafen.beans.SendSuccessEvent;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qwe on 2017/7/28.
 */

public class SelectedCoverActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String imgPath = "";

    private SelectedCoverAdapter coverAdapter;

    private ArrayList<ImageItem> selectBitmap = new ArrayList<>();

    private TextView take_photo;
    private TextView selected_pic;
    private String picKey = "";
    private boolean isFlower = false;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_cover);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("flower")) {
            isFlower = getIntent().getBooleanExtra("flower", false);
        }

        if (getIntent().hasExtra("type")) {
            type = getIntent().getStringExtra("type");
        }


        initView();
        initData();
    }

    private void initView() {
        getTitleBar().setTitle("选择封面");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        coverAdapter = new SelectedCoverAdapter(this, recyclerView);
        coverAdapter.setFlower(isFlower);
        View view = LayoutInflater.from(this).inflate(R.layout.header_selected_cover, recyclerView, false);
        take_photo = ButterKnife.findById(view, R.id.take_photo);
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPath = FileUtils.getCameraPhotoPath();
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                File out = new File(imgPath);
                Uri uri = Uri.fromFile(out);
                // 获取拍照后未压缩的原图片，并保存在uri路径中
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intentCamera, MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA);
            }
        });

        selected_pic = ButterKnife.findById(view, R.id.selected_pic);
        selected_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectedCoverActivity.this, AlbumNewActivity.class);
                intent.putExtra(MyConstants.MAX_ALBUM_COUNT, 1);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM);
            }
        });
        coverAdapter.setAutoLoadMoreEnable(false);
        coverAdapter.setHeaderEnable(true);
        coverAdapter.addHeaderView(view);

        recyclerView.setAdapter(coverAdapter);
    }

    public void initData() {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("type", type);
        OkHttpClientManager.postAsyn(MyConstants.MY_COVER_LIST, arrayMap, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                Logger.e("get response:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            RecommendCoverBean result = JSON.parseObject(baseResult.obj, RecommendCoverBean.class);
                            coverAdapter.setData(result.backgrounds.get(0).items, result.selectedBackground);
                        }
                    } else {
                        CommonUtils.error(baseResult, SelectedCoverActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                selectBitmap.clear();
                upload2Ali(take_photo);
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                imgPath = "";
                upload2Ali(selected_pic);
            }
        }
    }

    private void upload2Ali(View view) {
        picKey = DateTimeUtils.getYearMonthDayFolder("/arbitration/");
        for (ImageItem item : selectBitmap) {
            UploadPicTask task = new UploadPicTask(view);
            task.execute(item.imagePath);
        }

        if (!TextUtils.isEmpty(imgPath)) {
            UploadPicTask task = new UploadPicTask(view);
            task.execute(imgPath);
        }
    }


    private class UploadPicTask extends AsyncTask<String, Void, String> {

        private View view;

        public UploadPicTask(View view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            view.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String urlPath = params[0];
            String fileName = System.currentTimeMillis() + ".jpg";
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String key = picKey + fileName;
            Bitmap bm = ImageUtils.revitionImageSize(urlPath);
            if (bm != null) {
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                AliUpdateEvent updateEvent = new AliUpdateEvent(SelectedCoverActivity.this, picPath, folder, key);
                PutObjectResult result = updateEvent.putObjectFromLocalFile();
                if (result != null) {
                    response = key;
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            view.setEnabled(true);
            if (!TextUtils.isEmpty(result)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.e("get result:" + MyConstants.OSS_IMG_HEAD + result);
                        String requestUrl = "";
                        ArrayMap<String, String> arrayMap = new ArrayMap<>();
                        if (isFlower) {
                            arrayMap.put("articleBackground", MyConstants.OSS_IMG_HEAD + result);
                            requestUrl = MyConstants.SETPREFERENCES;
                        } else {
                            arrayMap.put("backgroundUrl", MyConstants.OSS_IMG_HEAD + result);
                            requestUrl = MyConstants.UPDATE_BG;
                        }

                        OkHttpClientManager.postAsyn(requestUrl, arrayMap, new OkHttpClientManager.StringCallback() {

                            @Override
                            public void onError(Request request, Exception e) {

                            }

                            @Override
                            public void onResponse(String response) {
                                BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                                if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                    ToastUtil.toast(SelectedCoverActivity.this, "恭喜您，成功更换封面！");
                                    SendSuccessEvent eventSuccess = new SendSuccessEvent();
                                    eventSuccess.isSuccess = true;
                                    EventBus.getDefault().post(eventSuccess);
                                    initData();
                                }
                            }
                        });

                    }
                });
            } else {
                toast("图片上传失败，请重试");
                ProgressDialog.closeProgress();
            }
        }
    }
}
