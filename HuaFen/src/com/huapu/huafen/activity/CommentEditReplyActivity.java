package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CommentEditAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.OrderData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.events.OrderDetailRequestEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.itemdecoration.SpaceDecoration;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.HeaderAndFooterWrapper;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.DashLineView;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 评论编辑
 *
 * @author liang_xs
 */
public class CommentEditReplyActivity extends BaseActivity {
    private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();
    private RecyclerView recyclerView;
    private CommentEditAdapter mAdapter;
    private HeaderAndFooterWrapper wrapper;
    private View header;
    private EditText etContent;
    private TextView tvInputCount;
    private SimpleDraweeView ivGoodPic;
    private DashLineView dlvGoodsName;
    private TextView tvPrice;

    private GoodsData goodsData;
    private OrderData orderData;
    private long orderId;
    private RadioGroup rg;
    private int satisfaction = 0;
    private boolean isStop = false;
    private String commentImgs = "";
    private int taskCount;
    private String picKey = "";
    private TextView commitComment;
    private Handler taskHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    taskCount--;
                    String key = (String) msg.obj;
                    commentImgs += MyConstants.OSS_IMG_HEAD + key + ",";
                    if (taskCount == 0) {
                        startRequestForEstimateOrder();
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
        setContentView(R.layout.activity_comment_edit);
        commitComment = (TextView) findViewById(R.id.commitComment);
        commitComment.setVisibility(View.VISIBLE);
        commitComment.setOnClickListener(this);
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_BITMAP)) {
            selectBitmap = (ArrayList<ImageItem>) getIntent().getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_DATA)) {
            orderData = (OrderData) getIntent().getSerializableExtra(MyConstants.EXTRA_ORDER_DATA);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_GOODS_DATA)) {
            goodsData = (GoodsData) getIntent().getSerializableExtra(MyConstants.EXTRA_GOODS_DATA);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_COMMENT_ID)) {
            orderId = getIntent().getLongExtra(MyConstants.EXTRA_COMMENT_ID, 0L);
        }
        initView();
        if (goodsData != null && orderData != null) {
            if (!ArrayUtil.isEmpty(goodsData.getGoodsImgs())) {

                ImageLoader.resizeSmall(ivGoodPic, goodsData.getGoodsImgs().get(0), 1);

                dlvGoodsName.setData(goodsData.getBrand(), goodsData.getName());
                long currentMillions = orderData.getReceiveTime();
                if (currentMillions > 0) {
                    String time = DateTimeUtils.getYearMonthDayHourMinuteSecond(currentMillions);
                    String timeDes = String.format(getString(R.string.deal_successful_time), time);
                    tvPrice.setText(timeDes);
                }
//                CommonUtils.setPriceSizeData(tvPrice, "", orderData.getPrice());
            }
        }
        satisfaction = 30;

    }

    private void initView() {
        setTitleString("评价");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new SpaceDecoration(2, 4));
        initRecycler();
    }

    private void initRecycler() {
        if (mAdapter == null) {
            mAdapter = new CommentEditAdapter(this);
            wrapper = new HeaderAndFooterWrapper(mAdapter);
            header = LayoutInflater.from(this).inflate(R.layout.view_headview_comment_edit, null);
            etContent = (EditText) header.findViewById(R.id.etContent);
            tvInputCount = (TextView) header.findViewById(R.id.tvInputCount);
            ivGoodPic = (SimpleDraweeView) header.findViewById(R.id.ivGoodPic);
            dlvGoodsName = (DashLineView) header.findViewById(R.id.dlvGoodsName);
            tvPrice = (TextView) header.findViewById(R.id.tvPrice);
            rg = (RadioGroup) header.findViewById(R.id.rg);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if (checkedId == R.id.rbSatisfaction) {
                        satisfaction = 30;
                    } else if (checkedId == R.id.rbGeneral) {
                        satisfaction = 20;
                    } else if (checkedId == R.id.rbDissatisfied) {
                        satisfaction = 10;
                    }
                }
            });
            wrapper.addHeaderView(header);

            recyclerView.setAdapter(wrapper);
            etContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    String content = etContent.getText().toString().trim();
                    tvInputCount.setText(content.length() + "/"
                            + "100");
                }

            });

        } else {
            mAdapter.setData(selectBitmap);
            mAdapter.notifyDataSetChanged();
            wrapper.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commitComment:
                if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
                    toast("请填写评论内容");
                    return;
                }
                if (satisfaction == 0) {
                    toast("请选择满意度");
                    return;
                }
                ProgressDialog.showProgress(CommentEditReplyActivity.this);
                commitComment.setOnClickListener(null);
                isStop = false;
                if (selectBitmap != null && selectBitmap.size() > 0) {
                    upload2Ali();
                } else {
                    startRequestForEstimateOrder();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
                    mAdapter.setData(selectBitmap);
                    wrapper.notifyDataSetChanged();
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                // 判断图片是否旋转，处理图片旋转
                ExifInterface exif;
                Bitmap bitmap = ImageUtils.getLoacalBitmap(CommentEditReplyActivity.this, mAdapter.imgPath);
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
                if (selectBitmap != null) {
                    mAdapter.setData(selectBitmap);
                    wrapper.notifyDataSetChanged();
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
                    mAdapter.setData(selectBitmap);
                    wrapper.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 对订单评论
     *
     * @param
     */
    private void startRequestForEstimateOrder() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            ProgressDialog.closeProgress();
            taskCount = selectBitmap.size();
            isStop = true;
            commentImgs = "";
            commitComment.setOnClickListener(CommentEditReplyActivity.this);
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", String.valueOf(orderId));
        params.put("satisfaction", String.valueOf(satisfaction));
        params.put("content", etContent.getText().toString().trim());

        if (!TextUtils.isEmpty(commentImgs)) {
            params.put("imgs", commentImgs);
        }
        LogUtil.i("liang", "评论params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.RATEORDER, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                        taskCount = selectBitmap.size();
                        isStop = true;
                        commentImgs = "";
                        commitComment.setOnClickListener(CommentEditReplyActivity.this);
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        taskCount = selectBitmap.size();
                        isStop = true;
                        commentImgs = "";
                        commitComment.setOnClickListener(CommentEditReplyActivity.this);
                        LogUtil.i("liang", "评论:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                toast("评论成功");
                                OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
                                oEvent.isUpdate = true;
                                EventBus.getDefault().post(oEvent);
                                Intent intent = new Intent();
                                intent.putExtra("commentSuccessful", true);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                CommonUtils.error(baseResult, CommentEditReplyActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali() {
        picKey = DateTimeUtils.getYearMonthDayFolder("/comment/");
        taskCount = selectBitmap.size();
        for (ImageItem item : selectBitmap) {
            if (isStop) {
                return;
            }
            UploadPicTask task = new UploadPicTask();
            task.execute(item.imagePath);
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
            String urlPath = params[0];
            String fileName = System.currentTimeMillis() + ".jpg";
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String key = picKey + fileName;
            Bitmap bm = ImageUtils.revitionImageSize(urlPath);
            if (bm != null) {
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                AliUpdateEvent updateEvent = new AliUpdateEvent(CommentEditReplyActivity.this, picPath, folder, key);
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
                ProgressDialog.closeProgress();
                taskCount = selectBitmap.size();
                isStop = true;
                commentImgs = "";
                commitComment.setOnClickListener(CommentEditReplyActivity.this);
                toast("图片上传失败，请重试");
            }
        }
    }
}
