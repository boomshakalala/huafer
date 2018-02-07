package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.RefundLogData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.events.OrderDetailRequestEvent;
import com.huapu.huafen.events.RefundFinishEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.GridViewWithHeaderAndFooter;
import com.huapu.huafen.views.HeaderViewArbitration;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 人工客服
 *
 * @author liang_xs
 */
public class ArbitrationActivity extends BaseActivity {
    private GridViewWithHeaderAndFooter gvPic;
    private HeaderViewArbitration headerViewArbitration;
    private GridViewAdapter gridViewAdapter;
    private OrderDetailBean bean;
    private int type;// 1,申请；2，举证
    private String imgPath = "";
    private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();
    private RefundLogData refundLogData;
    private long orderId = 0;
    private long arbitrationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arbitration);
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_BITMAP)) {
            selectBitmap = (ArrayList<ImageItem>) getIntent().getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_ARBITRATION)) {
            type = getIntent().getIntExtra(MyConstants.EXTRA_ORDER_ARBITRATION, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL)) {
            bean = (OrderDetailBean) getIntent().getSerializableExtra(MyConstants.EXTRA_ORDER_DETAIL);
            if (bean != null) {
                if (bean.getOrderInfo() != null) {
                    orderId = bean.getOrderInfo().getOrderId();
                }
                if (bean.getArbitrationInfo() != null) {
                    arbitrationId = bean.getArbitrationInfo().getArbitrationId();
                }
            }
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_REFUND_LOG_DATA_DETAIL)) {
            refundLogData = (RefundLogData) getIntent().getSerializableExtra(MyConstants.EXTRA_REFUND_LOG_DATA_DETAIL);
            orderId = refundLogData.getOrderData().getOrderId();
        }
        initView();
    }

    private void initView() {

        getTitleBar().
                setTitle("仲裁").
                setRightText("提交", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (headerViewArbitration != null) {
                            if (TextUtils.isEmpty(headerViewArbitration.etArbitration.getText().toString())) {
                                toast("请填写仲裁内容");
                                return;
                            }

                            if (TextUtils.isEmpty(headerViewArbitration.etPhoneNum.getText().toString())) {
                                toast("请填写联系电话");
                                return;
                            } else {
                                String phone = headerViewArbitration.etPhoneNum.getText().toString();
                                if (phone.length() < 11) {
                                    toast("请填写正确的手机号");
                                    return;
                                }
                            }


                            ProgressDialog.showProgress(ArbitrationActivity.this);
                            if (selectBitmap != null && selectBitmap.size() > 0) {
                                upload2Ali(view);
                            } else {
                                if (type == 1) { // 申请仲裁
                                    startRequestForSaveReportInfo(view, orderId);
                                } else if (type == 2) { // 仲裁补充举证
                                    if (bean == null || bean.getArbitrationInfo() == null) {
                                        toast("仲裁信息不存在");
                                        return;
                                    }
                                    startRequestForSaveArbitrationProof(view, arbitrationId);
                                }
                            }
                        }
                    }
                });
        gvPic = (GridViewWithHeaderAndFooter) findViewById(R.id.gvPic);
        headerViewArbitration = new HeaderViewArbitration(this);
        gvPic.addHeaderView(headerViewArbitration);
        gridViewAdapter = new GridViewAdapter(ArbitrationActivity.this, selectBitmap);
        gvPic.setAdapter(gridViewAdapter);
        if (bean != null && bean.getGoodsInfo() != null && bean.getOrderInfo() != null) {
            headerViewArbitration.setData(bean);
        }
        if (refundLogData != null && refundLogData.getGoodsData() != null && refundLogData.getOrderData() != null) {
            headerViewArbitration.setData(refundLogData);
        }
    }

    class GridViewAdapter extends BaseAdapter {
        Context mContext;
        private LayoutInflater inflater;
        private ArrayList<ImageItem> list;

        public GridViewAdapter(Context mContext, ArrayList<ImageItem> list) {
            this.list = list;
            this.mContext = mContext;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (list.size() == MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
                return MyConstants.MATCH_SELECT_DYNAMIC_PHOTO;
            }
            return (list.size() + 1);
        }

        @Override
        public ImageItem getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_gridview_releasefirst, null);
                viewHolder = new ViewHolder();
                viewHolder.ivPic = (SimpleDraweeView) convertView.findViewById(R.id.ivPic);
                WindowManager wm = ArbitrationActivity.this.getWindowManager();
                int width = wm.getDefaultDisplay().getWidth();
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.ivPic.getLayoutParams();
                lp.height = width / 4;
                viewHolder.ivPic.setLayoutParams(lp);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == list.size()) {
                viewHolder.ivPic.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.item_add_icon));
                if (position == MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
                    viewHolder.ivPic.setVisibility(View.GONE);
                }
            } else {
                if (!TextUtils.isEmpty(list.get(position).imagePath)) {
                    ImageLoader.loadImage(viewHolder.ivPic, MyConstants.FILE + list.get(position).imagePath);
                } else {
                    viewHolder.ivPic.setBackgroundResource(R.drawable.item_add_icon);
                }
            }

            // 图片点击事件
            viewHolder.ivPic.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // 如果是最后一张，则图片为添加新图片，跳转至选择图片的activity
                    if (position == list.size()) {
//						createHomeCaminaDialog();
                        PhotoDialog dialog = new PhotoDialog(ArbitrationActivity.this);
                        dialog.setCameraCall(new DialogCallback() {

                            @Override
                            public void Click() {
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
                        dialog.setAlbumCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                Intent intent = new Intent(ArbitrationActivity.this, AlbumNewActivity.class);
                                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM);
                            }
                        });
                        dialog.show();
                        return;
                    }
                    Intent intent = new Intent(ArbitrationActivity.this, GalleryFileActivity.class);
                    // 已选择的图片及位置坐标
                    intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                    intent.putExtra(MyConstants.EXTRA_PHOTO_DELETE, true);
                    startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
                }
            });

            return convertView;
        }

        class ViewHolder {
            private SimpleDraweeView ivPic;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
                    gridViewAdapter = new GridViewAdapter(ArbitrationActivity.this, selectBitmap);
                    gvPic.setAdapter(gridViewAdapter);
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                // 判断图片是否旋转，处理图片旋转
                ExifInterface exif;
                Bitmap bitmap = ImageUtils.getLoacalBitmap(ArbitrationActivity.this, imgPath);
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

                ImageItem item = new ImageItem();
                item.imagePath = imgPath;
                item.isSelected = true;
                selectBitmap.add(item);
                gridViewAdapter = new GridViewAdapter(ArbitrationActivity.this, selectBitmap);
                gvPic.setAdapter(gridViewAdapter);
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
                    gridViewAdapter = new GridViewAdapter(ArbitrationActivity.this, selectBitmap);
                    gvPic.setAdapter(gridViewAdapter);
                }
            }
        }
    }


    /**
     * 仲裁
     *
     * @param
     */
    private void startRequestForSaveReportInfo(final View view, long orderId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ProgressDialog.closeProgress();
            taskCount = selectBitmap.size();
            commentImgs = "";
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", String.valueOf(CommonPreference.getUserId()));
        params.put("typeId", String.valueOf(orderId));
        params.put("type", "3");
        params.put("reportContent", headerViewArbitration.etArbitration.getText().toString());
        params.put("repoterPhone", headerViewArbitration.etPhoneNum.getText().toString());
        if (!TextUtils.isEmpty(commentImgs)) {
            params.put("reportImageUrls", commentImgs);
        }
        view.setEnabled(false);
        LogUtil.i("liang", "仲裁params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.SAVEREPORTINFO_V2, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        view.setEnabled(true);
                        ProgressDialog.closeProgress();
                        taskCount = selectBitmap.size();
                        commentImgs = "";
                    }

                    @Override
                    public void onResponse(String response) {
                        view.setEnabled(true);
                        LogUtil.i("liang", "仲裁:" + response);
                        taskCount = selectBitmap.size();
                        commentImgs = "";
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
                                oEvent.isUpdate = true;
                                EventBus.getDefault().post(oEvent);
                                RefundFinishEvent event = new RefundFinishEvent();
                                event.isFinish = true;
                                EventBus.getDefault().post(event);
                                finish();
                            } else {
                                CommonUtils.error(baseResult, ArbitrationActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }


    /**
     * 仲裁补充证据
     *
     * @param
     */
    private void startRequestForSaveArbitrationProof(final View view, long arbitrationId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ProgressDialog.closeProgress();
            taskCount = selectBitmap.size();
            commentImgs = "";
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(ArbitrationActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("arbitrationId", String.valueOf(arbitrationId));
        params.put("evidenceContent", headerViewArbitration.etArbitration.getText().toString());
        params.put("evidencePhone", headerViewArbitration.etPhoneNum.getText().toString());
        if (!TextUtils.isEmpty(commentImgs)) {
            params.put("evidenceImageUrls", commentImgs);
        }
        LogUtil.i("liang", "仲裁举证params:" + params.toString());
        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.SAVEARBITRATIONPROOF, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        view.setEnabled(true);
                        ProgressDialog.closeProgress();
                        taskCount = selectBitmap.size();
                        commentImgs = "";
                    }

                    @Override
                    public void onResponse(String response) {
                        view.setEnabled(true);
                        LogUtil.i("liang", "仲裁举证:" + response);
                        ProgressDialog.closeProgress();
                        taskCount = selectBitmap.size();
                        commentImgs = "";
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
                                oEvent.isUpdate = true;
                                EventBus.getDefault().post(oEvent);
                                RefundFinishEvent event = new RefundFinishEvent();
                                event.isFinish = true;
                                EventBus.getDefault().post(event);
                                finish();
                            } else {
                                CommonUtils.error(baseResult, ArbitrationActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    private String commentImgs = "";
    private int taskCount;
    private String picKey = "";

    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali(View view) {
        picKey = DateTimeUtils.getYearMonthDayFolder("/arbitration/");
        taskCount = selectBitmap.size();
        for (ImageItem item : selectBitmap) {
            UploadPicTask task = new UploadPicTask(view);
            task.execute(item.imagePath);
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
            String urlPath = (String) params[0];
            String fileName = System.currentTimeMillis() + ".jpg";
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String key = picKey + fileName;
            Bitmap bm = ImageUtils.revitionImageSize(urlPath);
            if (bm != null) {
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                AliUpdateEvent updateEvent = new AliUpdateEvent(ArbitrationActivity.this, picPath, folder, key);
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
            view.setEnabled(true);
            if (!TextUtils.isEmpty(result)) {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                taskHandler.sendMessage(msg);
            } else {
                taskCount = selectBitmap.size();
                toast("图片上传失败，请重试");
                ProgressDialog.closeProgress();
                commentImgs = "";
            }
        }
    }

    private Handler taskHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    taskCount--;
                    String key = (String) msg.obj;
                    commentImgs += MyConstants.OSS_IMG_HEAD + key + "@!logo" + ",";
                    if (taskCount == 0) {
                        if (type == 1) { // 申请仲裁
                            startRequestForSaveReportInfo(getTitleBar().getTitleTextRight(), orderId);
                        } else if (type == 2) { // 仲裁补充举证
                            if (bean == null || bean.getArbitrationInfo() == null) {
                                return;
                            }
                            startRequestForSaveArbitrationProof(getTitleBar().getTitleTextRight(), arbitrationId);
                        }
                    }
                    break;

                default:
                    break;
            }

        }
    };
}
