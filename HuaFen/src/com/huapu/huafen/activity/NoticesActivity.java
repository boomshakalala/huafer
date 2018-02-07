package com.huapu.huafen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.huapu.huafen.R;
import com.huapu.huafen.banner.NetworkImageRoundHolderView;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.Notices;
import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.BitmapUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.StringUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 花语
 *
 * @author liang_xs
 *         <p>
 *         1. 判断是自己看还是其他人看
 *         case 自己看：
 *         显示编辑按钮
 *         点击编辑后将图片下载到本地
 *         case 别人看：
 *         不显示编辑按钮
 *         不需要将图片下载到本地
 */
public class NoticesActivity extends BaseActivity implements OnItemClickListener {
    //	private RoundCornerImageView ivNoticePic;
    private ImageView ivNoticeBg;
    private ImageView ivCancel;
    private int picWidth, picHeight;
    private int bgWidth;
    private View layoutNoticeDefault, layoutNoticeContent;
    private TextView tvBtnEdit, tvBtnClean, tvBtnRelease, tvBtnPreview;
    private View layoutBottomEdit;
    private View layoutRelease;
    private View layoutPreview;
    private View layoutEdit;
    private EditText etTitle, etContent;
    private TextView tvTitle, tvContent;
    private ImageView ivMore;
    //	private String imgPath = "";
//	private String fileName;
    private Bitmap bitmap;// 头像Bitmap
    private Notices notices;
    private long userId;
    private boolean isChangePic = false;
    private String picKey;
    private String paramsTitle = "";
    private String paramsContent = "";
    private String paramsImg = "";
    private TextView tvInputCount;
    private ImageView ivDefault;
    private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();
    private ConvenientBanner viewpagerBanner;
    private boolean isEdit = false;
    private boolean isStop = false;
    private View layoutContentMore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);
        initView();
        if (getIntent().hasExtra(MyConstants.EXTRA_USER_ID)) {
            userId = getIntent().getLongExtra(MyConstants.EXTRA_USER_ID, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_NOTICES)) {
            notices = (Notices) getIntent().getSerializableExtra(MyConstants.EXTRA_NOTICES);
        }
        if (userId == CommonPreference.getUserId()) {
            layoutBottomEdit.setVisibility(View.VISIBLE);
            ivDefault.setImageResource(R.drawable.icon_notice_default);
        } else {
            layoutBottomEdit.setVisibility(View.GONE);
            ivDefault.setImageResource(R.drawable.icon_notice_default_other);
        }
        if (notices == null) {
            layoutNoticeDefault.setVisibility(View.VISIBLE);
            layoutNoticeContent.setVisibility(View.GONE);
            tvBtnClean.setVisibility(View.GONE);
        } else {
            layoutNoticeDefault.setVisibility(View.GONE);
            layoutNoticeContent.setVisibility(View.VISIBLE);
            layoutEdit.setVisibility(View.GONE);
            layoutPreview.setVisibility(View.VISIBLE);
            tvBtnClean.setVisibility(View.VISIBLE);
            if (!ArrayUtil.isEmpty(notices.getImages())) {
//				ImageLoaderRoundManager.getImageLoader().displayImage(notices.getImages().get(0), ivNoticePic, ImageLoaderRoundManager.getImageOptions());
//				taskCountDownload = notices.getImages().size();
//				for (final String picUrl : notices.getImages()) {
//					new SavePicTask().execute(picUrl);
//				}
                setPic();
                setViewpagerBanner(notices.getImages());
            }
            tvTitle.setText(notices.getTitle());
            tvContent.setText(notices.getContent());
            setMore();
        }
    }

    private void setMore() {
        ViewTreeObserver vto2 = tvContent.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tvContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//				int heightTextView = tvContent.getHeight();
//				int heightLayout = layoutContentMore.getHeight();
//				LogUtil.i("textview", "heightTextView: " + heightTextView + ", heightLayout: " + heightLayout);
                Layout layout = tvContent.getLayout();
                if (layout != null) {
                    int lines = layout.getLineCount();
                    if (lines > 8) {
                        ivMore.setVisibility(View.VISIBLE);
                    } else {
                        ivMore.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setViewpagerBanner(ArrayList<String> images) {
        if (images != null && images.size() > 0) {
            //加载网络数据
            viewpagerBanner.setPages(new CBViewHolderCreator<NetworkImageRoundHolderView>() {
                @Override
                public NetworkImageRoundHolderView createHolder() {
                    return new NetworkImageRoundHolderView();
                }

            }, images).setOnItemClickListener(this);
            if (images.size() > 1) {
                viewpagerBanner//设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可以不设
                        .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});
            } else {
                viewpagerBanner.setPageIndicator(
                        new int[]{0, 0}
                );
            }
        }
    }

    private void initView() {
        ivNoticeBg = (ImageView) findViewById(R.id.ivNoticeBg);
        ivCancel = (ImageView) findViewById(R.id.ivCancel);
//		ivNoticePic = (RoundCornerImageView) findViewById(R.id.ivNoticePic);
        layoutNoticeDefault = findViewById(R.id.layoutNoticeDefault);
        layoutNoticeContent = findViewById(R.id.layoutNoticeContent);
        layoutRelease = findViewById(R.id.layoutRelease);
        layoutPreview = findViewById(R.id.layoutPreview);
        layoutBottomEdit = findViewById(R.id.layoutBottomEdit);
        layoutEdit = findViewById(R.id.layoutEdit);
        tvBtnEdit = (TextView) findViewById(R.id.tvBtnEdit);
        tvBtnClean = (TextView) findViewById(R.id.tvBtnClean);
        tvBtnRelease = (TextView) findViewById(R.id.tvBtnRelease);
        tvBtnPreview = (TextView) findViewById(R.id.tvBtnPreview);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvContent = (TextView) findViewById(R.id.tvContent);
        ivMore = (ImageView) findViewById(R.id.ivMore);
        tvInputCount = (TextView) findViewById(R.id.tvInputCount);
        ivDefault = (ImageView) findViewById(R.id.ivDefault);
        viewpagerBanner = (ConvenientBanner) findViewById(R.id.viewpagerBanner);
        layoutContentMore = findViewById(R.id.layoutContentMore);
        ViewTreeObserver vto2 = ivNoticeBg.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivNoticeBg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                bgWidth = ivNoticeBg.getWidth();
                Resources res = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.notice_bg);
                ivNoticeBg.setImageBitmap(BitmapUtil.createRepeater(bgWidth, bitmap));//screenWidth为屏幕宽度（或显示图片的imageview宽度）
            }
        });
        ivCancel.setOnClickListener(this);
        tvBtnEdit.setOnClickListener(this);
        tvBtnClean.setOnClickListener(this);
        tvBtnRelease.setOnClickListener(this);
        tvBtnPreview.setOnClickListener(this);
        tvContent.setMovementMethod(new ScrollingMovementMethod());
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
                String content = etContent.getText().toString();
                tvInputCount.setText(content.length() + "/"
                        + "300");
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivNoticePic:
                break;
            case R.id.ivCancel:
                onBackPressed();
                break;
            case R.id.tvBtnEdit:
//				ivNoticePic.setOnClickListener(this);
                isEdit = true;
                isChangePic = true;
                layoutBottomEdit.setVisibility(View.GONE);
                layoutRelease.setVisibility(View.VISIBLE);
                layoutNoticeDefault.setVisibility(View.GONE);
                layoutNoticeContent.setVisibility(View.VISIBLE);
                if (notices == null) {
//					ImageLoaderRoundManager.getImageLoader().displayImage("drawable://"+ R.drawable.notice_def, ivNoticePic, ImageLoaderRoundManager.getImageOptions());
                    ArrayList<String> images = new ArrayList<String>();
                    images.add("drawable://" + R.drawable.notice_def);
                    setPic();
                    setViewpagerBanner(images);
                } else {
                    layoutPreview.setVisibility(View.GONE);
                    layoutEdit.setVisibility(View.VISIBLE);
                    if (!ArrayUtil.isEmpty(notices.getImages())) {
                        ProgressDialog.showProgress(NoticesActivity.this);
                        taskCountDownload = notices.getImages().size();
                        for (final String picUrl : notices.getImages()) {
                            savePic(picUrl);
                            // new SavePicTask().execute(picUrl);
                        }
//						paramsImg = notices.getImages().get(0);
                    }
                    etTitle.setText(notices.getTitle());
                    etContent.setText(notices.getContent());
                }
                break;
            case R.id.tvBtnClean:
                final TextDialog dialog = new TextDialog(NoticesActivity.this, false);
                dialog.setContentText("确定清空花语吗？");
                dialog.setLeftText("取消");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("确认");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        startRequestForSetNotices();
                    }
                });
                dialog.show();
                break;
            case R.id.tvBtnRelease:
                paramsTitle = etTitle.getText().toString().trim();
                paramsContent = etContent.getText().toString().trim();
//				Bitmap bit = ImageUtils.getLoacalBitmap(this, imgPath);
                if (ArrayUtil.isEmpty(selectBitmap)) {
                    toast("请选择图片");
                    return;
                }
                if (TextUtils.isEmpty(paramsTitle)) {
                    toast("请填写标题");
                    return;
                }
                if (TextUtils.isEmpty(paramsContent)) {
                    toast("请填写正文");
                    return;
                }
                tvBtnRelease.setOnClickListener(null);
                isStop = false;
                ProgressDialog.showProgress(this);
                upload2Ali();
                break;
            case R.id.tvBtnPreview:
                if (isEdit) {
                    paramsTitle = etTitle.getText().toString().trim();
                    paramsContent = etContent.getText().toString().trim();
                    if (ArrayUtil.isEmpty(selectBitmap)) {
                        toast("请选择图片");
                        return;
                    }
                    if (TextUtils.isEmpty(paramsTitle)) {
                        toast("请填写标题");
                        return;
                    }
                    if (TextUtils.isEmpty(paramsContent)) {
                        toast("请填写正文");
                        return;
                    }
                    tvBtnPreview.setOnClickListener(null);
                    startRequestForPreviewNotices();
                } else {
//					ivNoticePic.setOnClickListener(this);
                    isEdit = true;
                    tvBtnPreview.setText("    预览    ");
                    layoutEdit.setVisibility(View.VISIBLE);
                    layoutPreview.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void setPic() {
        ViewTreeObserver vto2 = viewpagerBanner.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewpagerBanner.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                picWidth = viewpagerBanner.getWidth();
                picHeight = viewpagerBanner.getHeight();
                LayoutParams params = viewpagerBanner.getLayoutParams();
                params.height = picWidth;
                viewpagerBanner.setLayoutParams(params);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0); // 去掉baseactivity中finish动画
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (!ArrayUtil.isEmpty(selectBitmap)) {
                    for (ImageItem bean : selectBitmap) {
                        bean.isSelected = true;
                    }
                    ArrayList<String> images = new ArrayList<String>();
                    for (ImageItem imageItem : selectBitmap) {
                        images.add("file://" + imageItem.getImagePath());
                    }
                    setViewpagerBanner(images);
                } else {
                    ArrayList<String> images = new ArrayList<String>();
                    images.add("drawable://" + R.drawable.notice_def);
                    setViewpagerBanner(images);
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM) {
                ArrayList<ImageItem> selectImages = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                selectBitmap.addAll(selectImages);
                ArrayList<String> images = new ArrayList<String>();
                for (ImageItem imageItem : selectBitmap) {
                    images.add("file://" + imageItem.getImagePath());
                }
                setViewpagerBanner(images);
            }
        }
    }

    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali() {
        picKey = DateTimeUtils.getYearMonthDayFolder("/notices/");
        uploadTaskCount = selectBitmap.size();
        for (ImageItem item : selectBitmap) {
            if (isStop) {
                return;
            }
            UploadPicTask task = new UploadPicTask();
            task.execute(item.imagePath);
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = null;
        if (isEdit) {
            if (!ArrayUtil.isEmpty(selectBitmap)) {
                intent = new Intent(this, GalleryFileActivity.class);
                intent.putExtra(MyConstants.EXTRA_TO_GALLERY_FROM, "2");
                // 已选择的图片及位置坐标
                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                intent.putExtra(MyConstants.EXTRA_PHOTO_DELETE, true);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
            } else {
                intent = new Intent(this, AlbumNewActivity.class);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ALBUM);
            }
        } else {
            if (isChangePic) {
                if (selectBitmap != null && selectBitmap.size() > 0) {
                    ArrayList<String> imgs = new ArrayList<>();
                    for (ImageItem item : selectBitmap) {
                        imgs.add("file://" + item.imagePath);
                    }
                    intent = new Intent(this, GalleryUrlActivity.class);
                    intent.putExtra(MyConstants.EXTRA_SHOW_PIC, imgs);
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                    intent.putExtra(MyConstants.EXTRA_CAN_DOWNLOAD, false);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
//				intent = new Intent(this, GalleryFileActivity.class);
//				intent.putExtra(MyConstants.EXTRA_TO_GALLERY_FROM, "3");
//				// 已选择的图片及位置坐标
//				intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
//				intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
//				intent.putExtra(MyConstants.EXTRA_PHOTO_DELETE, false);
//				startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
            } else {
                if (!ArrayUtil.isEmpty(notices.getImages())) {
                    intent = new Intent(this, GalleryUrlActivity.class);
                    intent.putExtra(MyConstants.EXTRA_SHOW_PIC, notices.getImages());
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        }
    }

    private int uploadTaskCount;
    private Handler taskHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    uploadTaskCount--;
                    String key = (String) msg.obj;
                    paramsImg += MyConstants.OSS_IMG_HEAD + key + ",";
                    if (uploadTaskCount == 0) {
                        startRequestForSetNotices();
                    }
                    break;

                default:
                    break;
            }

        }
    };

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
            if (bm != null) {
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                AliUpdateEvent updateEvent = new AliUpdateEvent(NoticesActivity.this, picPath, folder, key);
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
                uploadTaskCount = selectBitmap.size();
                paramsImg = "";
                isStop = true;
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(NoticesActivity.this);
                toast("图片上传失败，请重试");
            }
        }
    }

    /**
     * 预览花语
     */
    private void startRequestForPreviewNotices() {
        if (!CommonUtils.isNetAvaliable(this)) {
            tvBtnPreview.setOnClickListener(this);
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content", etContent.getText().toString().trim());
        LogUtil.e("liang", "params花语预览：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.PREVIEWNOTICE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
                tvBtnPreview.setOnClickListener(NoticesActivity.this);
                LogUtil.e("liang", "error花语预览：" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                tvBtnPreview.setOnClickListener(NoticesActivity.this);
                LogUtil.e("liang", "花语预览：" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        JSONObject obj = JSON.parseObject(baseResult.obj);
                        if (obj.containsKey("content")) {
                            String content = obj.getString("content");
                            isEdit = false;
                            tvBtnPreview.setText("继续编辑");
                            layoutEdit.setVisibility(View.GONE);
                            String title = etTitle.getText().toString().trim();
                            tvTitle.setText(title);
                            tvContent.setText(content);
                            setMore();
                            layoutPreview.setVisibility(View.VISIBLE);
                        } else {
                            ToastUtil.toast(NoticesActivity.this, "数据异常");
                        }
                    } else {
                        CommonUtils.error(baseResult, NoticesActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 发布花语
     * 当三个参数全部为空时，代表清空花语
     */
    private void startRequestForSetNotices() {
        if (!CommonUtils.isNetAvaliable(this)) {
            uploadTaskCount = selectBitmap.size();
            paramsImg = "";
            isStop = true;
            ProgressDialog.closeProgress();
            tvBtnRelease.setOnClickListener(NoticesActivity.this);
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        if (!TextUtils.isEmpty(paramsImg)) {
            paramsImg = StringUtils.substringBeforeLast(paramsImg, ",");
        }
        params.put("image", paramsImg);
        params.put("title", paramsTitle);
        params.put("content", paramsContent);
        LogUtil.e("liang", "params花语：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.SETNOTICE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                uploadTaskCount = selectBitmap.size();
                paramsImg = "";
                isStop = true;
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(NoticesActivity.this);
                LogUtil.e("liang", "error花语：" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                uploadTaskCount = selectBitmap.size();
                paramsImg = "";
                isStop = true;
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(NoticesActivity.this);
                LogUtil.e("liang", "花语：" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
//						toast("发布成功");
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        CommonUtils.error(baseResult, NoticesActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private int taskCountDownload;

    @SuppressLint("HandlerLeak")
    private Handler taskHandlerDownload = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    taskCountDownload--;
                    if (taskCountDownload == 0) {
                        ArrayList<String> images = new ArrayList<>();
                        for (ImageItem imageItem : selectBitmap) {
                            images.add("file://" + imageItem.getImagePath());
                        }
                        setViewpagerBanner(images);
                        ProgressDialog.closeProgress();
                    }
                    break;
                case 1:
                    taskCountDownload = notices.getImages().size();
                    toast("图片下载失败");
                    ProgressDialog.closeProgress();
                    break;
            }
        }
    };

    private void savePic(String urlPath) {
        ImageLoader.loadBitmap(NoticesActivity.this, urlPath, new BitmapCallback() {
            @Override
            public void onBitmapDownloaded(Bitmap bitmap1) {
                if (bitmap1 == null)
                    return;
                String fileName = System.currentTimeMillis() + Math.random() * 100 + ".jpg";
                String picPath = FileUtils.saveBitmap(bitmap1, FileUtils.getIconDir(), fileName);

                if (!TextUtils.isEmpty(picPath)) {
                    ImageItem item = new ImageItem();
                    item.imagePath = picPath;
                    item.isSelected = true;
                    selectBitmap.add(item);
                    taskHandlerDownload.sendEmptyMessage(0);
                } else {
                    taskHandlerDownload.sendEmptyMessage(1);
                }
            }
        });
    }
}
