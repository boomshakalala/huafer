package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.FeedBackAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.itemdecoration.SpaceDecoration;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.HeaderAndFooterWrapper;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lalo on 2016/9/30.
 */
public class FeedBackActivity extends BaseActivity implements  View.OnTouchListener {
    private EditText etContent, etPhoneNum, etName;
    private RelativeLayout root;
    private RecyclerView recyclerGallery;
    private TextView tvCommit;
    private FeedBackAdapter mAdapter;
    private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();
    private HeaderAndFooterWrapper wrapper;
    private String strMobile, strContent, strName;
    private String picKey;
    private int taskCount;
    private boolean isStop = false;
    private String reportImageUrls = "";
    private RelativeLayout bottomView;

    private Handler taskHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    taskCount--;
                    String key = (String) msg.obj;
                    reportImageUrls += MyConstants.OSS_IMG_HEAD + key + "@!logo"+ ",";
                    if (taskCount == 0) {
                        startRequestForUserFeedback();
                    }
                    break;

                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_layout);
        initView();
    }

    private void initView() {
        setTitleString("反馈");
        root = (RelativeLayout) findViewById(R.id.root);
        bottomView = (RelativeLayout) findViewById(R.id.bottomView);
        recyclerGallery = (RecyclerView) findViewById(R.id.recyclerGallery);
        GridLayoutManager gManager = new GridLayoutManager(this,4);
        gManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerGallery.setLayoutManager(gManager);
        recyclerGallery.addItemDecoration(new SpaceDecoration(2, 4));
        tvCommit = (TextView) findViewById(R.id.tvCommit);
        mAdapter = new FeedBackAdapter(this, selectBitmap);
        wrapper = new HeaderAndFooterWrapper(mAdapter);
        View header = LayoutInflater.from(this).inflate(R.layout.feed_back_header, null);
        etContent = (EditText) header.findViewById(R.id.etContent);
        View footer = LayoutInflater.from(this).inflate(R.layout.view_footview_feedback, null);
        etPhoneNum = (EditText) footer.findViewById(R.id.etPhoneNum);
        etName = (EditText) footer.findViewById(R.id.etName);
        wrapper.addHeaderView(header);
        wrapper.addFootView(footer);
        recyclerGallery.setAdapter(wrapper);
        tvCommit.setOnClickListener(this);
        etContent.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvCommit:
                release2Sever();
                break;
            default:
                break;
        }
    }
    private void release2Sever() {
        strMobile = etPhoneNum.getText().toString().trim();
        strContent = etContent.getText().toString().trim();
        strName = etName.getText().toString().trim();
        if(TextUtils.isEmpty(strContent)) {
            toast("请输入描述内容");
            return;
        }
        if(TextUtils.isEmpty(strName)) {
            toast("请输入联系人名称");
            return;
        }
        if (TextUtils.isEmpty(strMobile)) {
            toast("手机号为空");
            return;
        }
        tvCommit.setOnClickListener(null);
        isStop = false;
        ProgressDialog.showProgress(this);
        if(selectBitmap == null || selectBitmap.size() < 1) {
            startRequestForUserFeedback();
        } else {
            upload2Ali();
        }

    }


    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali() {
        picKey = DateTimeUtils.getYearMonthDayFolder("/userFeedback/");
        taskCount = selectBitmap.size();
        for (ImageItem item : selectBitmap) {
            if (isStop) {
                return;
            }
            UploadPicTask task = new UploadPicTask();
            task.execute(item.imagePath);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.etContent:
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
        }
        return false;
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
            String fileName = System.currentTimeMillis() + ".jpg";
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String key = picKey + fileName;
            Bitmap bm = ImageUtils.revitionImageSize(urlPath);
            if(bm != null) {
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                AliUpdateEvent updateEvent = new AliUpdateEvent(FeedBackActivity.this, picPath, folder, key);
                PutObjectResult result = updateEvent.putObjectFromLocalFile();
                if (result != null) {
                    response = key;
                }
            }


            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                taskHandler.sendMessage(msg);
            } else {
                taskCount = selectBitmap.size();
                isStop = true;
                ProgressDialog.closeProgress();
                tvCommit.setOnClickListener(FeedBackActivity.this);
                toast("图片上传失败，请重试");
                reportImageUrls = "";
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                selectBitmap=(ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
                    mAdapter.setData(selectBitmap);
                    wrapper.notifyDataSetChanged();
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                // 判断图片是否旋转，处理图片旋转
                ExifInterface exif;
                Bitmap bitmap = ImageUtils.getLoacalBitmap(this, mAdapter.imgPath);
                try {
                    exif = new ExifInterface(mAdapter.imgPath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    // 旋转图片，使其显示方向正常
                    Bitmap newBitmap = ImageUtils.getTotateBitmap(bitmap, orientation);
                    File out = new File(mAdapter.imgPath);
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

                ImageItem item = new ImageItem();
                item.imagePath = mAdapter.imgPath;
                item.isSelected = true;
                selectBitmap.add(item);
//				if (selectBitmap.size() < 6) {
//					gridView.setDisabledLength(1);
//				} else {
//					gridView.setDisabledLength(0);
//				}
                mAdapter.setData(selectBitmap);
                wrapper.notifyDataSetChanged();
            } else if(requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM) {
                selectBitmap=(ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
//					if (selectBitmap.size() < 6) {
//						gridView.setDisabledLength(1);
//					} else {
//						gridView.setDisabledLength(0);
//					}
//					gridViewAdapter.notifyDataSetChanged();
                    mAdapter.setData(selectBitmap);
                    wrapper.notifyDataSetChanged();
                }
            }
        }

        bottomView.setBackgroundColor(getResources().getColor(R.color.base_bg));
    }


    /**
     * 反馈
     */
    private void startRequestForUserFeedback() {
        if(!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            ProgressDialog.closeProgress();
            taskCount = selectBitmap.size();
            isStop = true;
            reportImageUrls = "";
            tvCommit.setOnClickListener(FeedBackActivity.this);
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        if(!TextUtils.isEmpty(reportImageUrls)) {
            reportImageUrls = StringUtils.substringBeforeLast(reportImageUrls, ",");
            params.put("imgs", reportImageUrls);
        }
        params.put("content", strContent);
        params.put("name", strName);
        params.put("phone", strMobile);
        LogUtil.i("liang", "反馈params:"+params.toString());
        OkHttpClientManager.postAsyn(MyConstants.USERFEEDBACK, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("liang", "反馈：" + e.toString());
                ProgressDialog.closeProgress();
                taskCount = selectBitmap.size();
                isStop = true;
                reportImageUrls = "";
                tvCommit.setOnClickListener(FeedBackActivity.this);
            }


            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "反馈：" + response.toString());
                ProgressDialog.closeProgress();
                taskCount = selectBitmap.size();
                isStop = true;
                reportImageUrls = "";
                tvCommit.setOnClickListener(FeedBackActivity.this);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if(!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        FileUtils.delAllFile(FileUtils.getIconDir());
                        // 成功
                        FeedBackActivity.this.finish();
                    } else {
                        CommonUtils.error(baseResult, FeedBackActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
