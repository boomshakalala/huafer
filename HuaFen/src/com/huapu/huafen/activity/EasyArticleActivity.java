package com.huapu.huafen.activity;

import android.annotation.SuppressLint;
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
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.EasyArticleDetail;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.MomentCategoryBean;
import com.huapu.huafen.beans.PublishSuccessEvent;
import com.huapu.huafen.beans.SendSuccessEvent;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.views.ShareArticleLayout;
import com.squareup.okhttp.Request;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.huapu.huafen.activity.MomentDetailActivity.MOMENT_ID;
import static com.huapu.huafen.utils.CommonPreference.getUserId;

/**
 * 发布花语-简易模式
 * Created by qwe on 17/4/25.
 */
public class EasyArticleActivity extends BaseActivity implements OkHttpClientManager.StringCallback {

    private static final int maxPhoto = 9;
    private static final int maxInputWords = 36;

    @BindView(R.id.scrollview)
    ScrollView scrollView;
    @BindView(R.id.articleTitle)
    EditText articleTitle;
    @BindView(R.id.articleContent)
    EditText articleContent;
    @BindView(R.id.gvPic)
    GridView gvPic;
    @BindView(R.id.leftWords)
    TextView leftWords;
    @BindView(R.id.shareArticleLayout)
    ShareArticleLayout shareArticleLayout;
    @BindView(R.id.tvPickClassification)
    TextView tvPickClassification;

    private String imgPath = "";
    private GridViewAdapter gridViewAdapter;
    private ArrayList<ImageItem> selectBitmap = new ArrayList<>();
    private boolean isStop = false;
    private String commentImgs = "";
    private int taskCount = 0;
    private String picKey = "";

    private int taskTotal = 0;
    private String momentId;
    private String title = "";
    private String content = "";
    private String fromHome = "";
    private String categoryId = "";
    private String categoryName = "";

    @SuppressLint("HandlerLeak")
    private Handler taskHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    taskCount--;
                    String key = (String) msg.obj;
                    commentImgs += key + ",";
                    if (taskCount == 0) {
                        if (!TextUtils.isEmpty(title)) {
                            updateMoment();
                        } else {
                            sendData();
                        }
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
        setContentView(R.layout.activity_easyarticle);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(MOMENT_ID)) {
            momentId = getIntent().getStringExtra(MOMENT_ID);
        }

        if (getIntent().hasExtra("FROM_HOME")) {
            fromHome = getIntent().getStringExtra("FROM_HOME");
        }

        if (getIntent().hasExtra("categoryId")) {
            categoryId = getIntent().getStringExtra("categoryId");
        }

        if (getIntent().hasExtra("categoryName")) {
            categoryName = getIntent().getStringExtra("categoryName");

            if (!TextUtils.isEmpty(categoryName)) {
                setCategory(categoryName);
            }
        }

        if (getIntent().hasExtra("title")) {
            title = getIntent().getStringExtra("title");
            articleTitle.setText(title);
            articleTitle.setSelection(title.length());
            leftWords.setText("可输入" + (maxInputWords - title.length()) + "字");
        }

        if (getIntent().hasExtra("content")) {
            content = getIntent().getStringExtra("content");
            articleContent.setText(content);
        }

        initView();
    }

    private void initView() {
        getTitleBar().setTitle("")
                .setLeftText("取消", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        EasyArticleActivity.this.finish();
                    }
                })
                .setRightText("", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

        findViewById(R.id.sendText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConfigUtil.isToVerify()) {
                    DialogManager.toVerify(EasyArticleActivity.this);
                } else {
                    if (TextUtils.isEmpty(articleTitle.getText().toString().trim())) {
                        toast("请输入花语标题");
                        return;
                    }

                    if (null == selectBitmap || selectBitmap.size() <= 0) {
                        toast("请上传1张照片");
                        return;
                    }

                    if (TextUtils.isEmpty(categoryName)) {
                        toast("请选择分类");
                        return;
                    }

                    if (selectBitmap != null && selectBitmap.size() > 0) {
                        ProgressDialog.showProgress(EasyArticleActivity.this);
                        getTitleBar().getTitleTextRight().setEnabled(false);
                        upload2Ali();
                    }
                }
            }
        });

        findViewById(R.id.tvPickClassification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EasyArticleActivity.this, MomentCategoryActivity.class);
                startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_PICK_CATEGORY);
            }
        });

        gridViewAdapter = new GridViewAdapter(EasyArticleActivity.this, selectBitmap);
        if (getIntent().hasExtra(MyConstants.MOMENT_EDIT)) {
            selectBitmap = (ArrayList<ImageItem>) getIntent().getSerializableExtra(MyConstants.MOMENT_EDIT);
            gridViewAdapter.setData(selectBitmap);
        }

        gvPic.setAdapter(gridViewAdapter);

        articleTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                leftWords.setText("可输入" + (maxInputWords - s.length()) + "字");
            }
        });
    }

    private void setCategory(String categoryName) {

        String text = "选择分类";
        int textColor = R.color.base_pink;
        int drawable = R.drawable.text_white_pink_rect_bg;

        if (!TextUtils.isEmpty(categoryName)) {
            text = categoryName;

            if ("好物".equals(categoryName)) {
                drawable = R.drawable.pink_rect_bg;
            } else if ("时尚".equals(categoryName)) {
                drawable = R.drawable.chuanda_rect_bg;
            } else if ("美食".equals(categoryName)) {
                drawable = R.drawable.pengren_rect_bg;
            } else if ("旅行".equals(categoryName)) {
                drawable = R.drawable.lvxing_rect_bg;
            } else if ("美妆".equals(categoryName)) {
                drawable = R.drawable.huli_rect_bg;
            } else if ("生活".equals(categoryName)) {
                drawable = R.drawable.qita_rect_bg;
            } else if ("运动".equals(categoryName)) {
                drawable = R.drawable.sports_rect_bg;
            } else if ("亲子".equals(categoryName)) {
                drawable = R.drawable.qinzi_rect_bg;
            } else if ("影音".equals(categoryName)) {
                drawable = R.drawable.yingyin_rect_bg;
            }
            textColor = R.color.white;
        }

        tvPickClassification.setText(text);
        tvPickClassification.setTextColor(getResources().getColor(textColor));
        tvPickClassification.setBackgroundResource(drawable);
    }

    @Override
    public void onError(Request request, Exception e) {
        getTitleBar().getTitleTextRight().setEnabled(true);
        ProgressDialog.closeProgress();
        toast("发布失败");
    }

    @Override
    public void onResponse(String response) {
        Logger.e("get response:" + response);
        getTitleBar().getTitleTextRight().setEnabled(true);
        ProgressDialog.closeProgress();

        JsonValidator validator = new JsonValidator();
        boolean isJson = validator.validate(response);
        if (!isJson) {
            return;
        }

        BaseResult baseResult = JSON.parseObject(response,
                BaseResult.class);

        if (baseResult.code == 200) {
            try {
                PublishSuccessEvent event = new PublishSuccessEvent();
                event.publishSuccess = true;
                EventBus.getDefault().post(event);

                SendSuccessEvent eventSuccess = new SendSuccessEvent();
                eventSuccess.isSuccess = true;
                org.greenrobot.eventbus.EventBus.getDefault().post(eventSuccess);

                toast("发布成功");
                EasyArticleDetail.ObjBean.MomentBean data = JSON.parseObject(baseResult.obj, EasyArticleDetail.ObjBean.MomentBean.class);
                Logger.e("get momentId:" + data.getMomentId());

                shareArticleLayout.setData(CommonPreference.getUserInfo().getUserName() + "的花语：" + articleTitle.getText().toString().trim(), articleContent.getText().toString().trim(), gridViewAdapter.getItem(0).titleMedia.url, MyConstants.SHARE_MOMENT + data.getMomentId());
                shareArticleLayout.runShare();

                if (!TextUtils.isEmpty(fromHome)) {
                    Intent pickIntent = new Intent(EasyArticleActivity.this, FlowerNewActivity.class);
                    pickIntent.putExtra(MyConstants.EXTRA_USER_ID, getUserId());
                    pickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(pickIntent);
                }
                this.finish();
            } catch (NullPointerException e) {
                Logger.e(e.getMessage());
            }

        } else {
            CommonUtils.error(baseResult, EasyArticleActivity.this, "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
                    gridViewAdapter.setData(selectBitmap);
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                // 判断图片是否旋转，处理图片旋转
                ExifInterface exif;
                Bitmap bitmap = ImageUtils.getLoacalBitmap(EasyArticleActivity.this, imgPath);
                try {
                    exif = new ExifInterface(imgPath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    // 旋转图片，使其显示方向正常
                    Bitmap newBitmap = ImageUtils.getTotateBitmap(bitmap, orientation);
                    File out = new File(imgPath);
                    boolean save = ImageUtils.saveMyBitmap(out, newBitmap);
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    if (newBitmap != null) {
                        newBitmap.recycle();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ImageItem item = new ImageItem();
                item.imagePath = imgPath;
                item.isSelected = true;
                if (null == item.titleMedia) {
                    item.titleMedia = new FloridData.TitleMedia();
                    item.titleMedia.url = imgPath;
                }
                selectBitmap.add(item);
                gridViewAdapter = new GridViewAdapter(EasyArticleActivity.this, selectBitmap);
                gridViewAdapter.setData(selectBitmap);
                gvPic.setAdapter(gridViewAdapter);

                if (null == item.titleMedia) {
                    item.titleMedia = new FloridData.TitleMedia();
                }
                item.titleMedia.url = imgPath;

                Intent intent = new Intent(EasyArticleActivity.this, ArticleEditLoopActivity.class);
                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                if (null != selectBitmap && selectBitmap.size() > 1) {
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, selectBitmap.size() - 1);
                } else {
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, 0);
                }

                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM) {
                selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                for (ImageItem imageItem : selectBitmap) {
                    if (null == imageItem.titleMedia) {
                        imageItem.titleMedia = new FloridData.TitleMedia();
                        if (!TextUtils.isEmpty(imageItem.imagePath)) {
                            imageItem.titleMedia.url = imageItem.imagePath;
                        }

                    }
                    if (!TextUtils.isEmpty(imageItem.imagePath)) {
                        imageItem.titleMedia.url = imageItem.imagePath;
                    }
                }

                if (selectBitmap != null) {
                    gridViewAdapter = new GridViewAdapter(EasyArticleActivity.this, selectBitmap);
                    gvPic.setAdapter(gridViewAdapter);
                }

                Intent intent = new Intent(EasyArticleActivity.this, ArticleEditLoopActivity.class);
                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, 0);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);


            } else if (requestCode == MyConstants.REQUEST_CODE_FOR_PICK_CATEGORY) {
                if (data != null) {
                    MomentCategoryBean.ObjBean.CatsBean bean = (MomentCategoryBean.ObjBean.CatsBean) data.getSerializableExtra("CATEGOR_BEAN");
                    if (bean != null) {
                        categoryId = String.valueOf(bean.getCatId());
                        categoryName = bean.getName();
                        Logger.e("get log:" + bean.getCatId() + ":name" + bean.getName());
                        setCategory(categoryName);
                    }
                }
            }
        }
    }

    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali() {
        picKey = DateTimeUtils.getYearMonthDayFolder("/arbitration/");
        for (ImageItem item : selectBitmap) {
            if (isStop) {
                return;
            }

            if (TextUtils.isEmpty(item.titleMedia.url) || !item.titleMedia.url.startsWith("http")) {
                if (!TextUtils.isEmpty(title)) {
                    taskTotal++;
                }
                taskCount++;
            }
        }


        if (taskCount > 0) {
            for (ImageItem item : selectBitmap) {
                if (isStop) {
                    return;
                }

                if (TextUtils.isEmpty(item.titleMedia.url) || !item.titleMedia.url.startsWith("http")) {
                    UploadPicTask task = new UploadPicTask();
                    task.execute(item);
                }

            }
        } else {
            if (!TextUtils.isEmpty(title)) {
                updateMoment();
            }
        }
    }

    private void sendData() {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", articleTitle.getText().toString().trim());
        jsonObject.put("content", articleContent.getText().toString().trim());
        JSONArray jsonArray = new JSONArray();
        for (ImageItem imageItem : selectBitmap) {
            String jsonString = JSON.toJSONString(imageItem.titleMedia);
            if (!TextUtils.isEmpty(jsonString)) {
                jsonArray.add(JSONObject.parse(jsonString));
            }

        }

        jsonObject.put("media", jsonArray);
        jsonObject.put("location", "");
        jsonObject.put("poiUid", "");
        jsonObject.put("categoryId", categoryId);
        arrayMap.put("moment", jsonObject.toString());
        OkHttpClientManager.postAsyn(MyConstants.SEND_EASY_ARTICLE, arrayMap, this);
    }

    private void updateMoment() {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", articleTitle.getText().toString().trim());
        jsonObject.put("content", articleContent.getText().toString().trim());
        JSONArray jsonArray = new JSONArray();
        for (ImageItem imageItem : selectBitmap) {
            String jsonString = JSON.toJSONString(imageItem.titleMedia);
            if (!TextUtils.isEmpty(jsonString)) {
                jsonArray.add(JSONObject.parse(jsonString));
            }

        }

        jsonObject.put("media", jsonArray);
        jsonObject.put("location", "");
        jsonObject.put("poiUid", "");
        jsonObject.put("categoryId", categoryId);
        jsonObject.put("momentId", momentId);
        arrayMap.put("moment", jsonObject.toString());

        OkHttpClientManager.postAsyn(MyConstants.MOMENT_UPDATE, arrayMap, new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                    if (!TextUtils.isEmpty(baseResult.obj)) {
                        try {
                            EasyArticleDetail easyArticleDetail = JSON.parseObject(response, EasyArticleDetail.class);
                            String momentId = String.valueOf(easyArticleDetail.getObj().getMoment().getMomentId());
                            toast("编辑成功！");
                            shareArticleLayout.setData(CommonPreference.getUserInfo().getUserName() + "的花语：" + articleTitle.getText().toString().trim(), articleContent.getText().toString().trim(), gridViewAdapter.getItem(0).titleMedia.url, MyConstants.SHARE_MOMENT + momentId);
                            shareArticleLayout.runShare();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            EasyArticleActivity.this.finish();
                        } catch (NullPointerException e) {
                            Logger.e(e.getMessage());
                        }

                    }
                } else {
                    toast(baseResult.msg);
                }
            }
        });
    }

    private class GridViewAdapter extends BaseAdapter {
        Context mContext;
        private LayoutInflater inflater;
        private ArrayList<ImageItem> list;

        public GridViewAdapter(Context mContext, ArrayList<ImageItem> list) {
            this.list = list;
            this.mContext = mContext;
            inflater = LayoutInflater.from(mContext);
        }

        public void setData(ArrayList<ImageItem> itemArrayList) {
            this.list = itemArrayList;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (list.size() == maxPhoto) {
                return maxPhoto;
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
                convertView = inflater.inflate(R.layout.adapter_item_grid_pic, null);
                viewHolder = new ViewHolder();
                viewHolder.ivPic = (SimpleDraweeView) convertView.findViewById(R.id.ivPic);
                viewHolder.coverText = (TextView) convertView.findViewById(R.id.coverText);
                viewHolder.deleteIcon = (ImageView) convertView.findViewById(R.id.deleteIcon);
                WindowManager wm = EasyArticleActivity.this.getWindowManager();
                int width = (int) (wm.getDefaultDisplay().getWidth() - (CommonUtils.getScreenDensity() * 60));
                ViewGroup.LayoutParams lp = viewHolder.ivPic.getLayoutParams();
                lp.height = width / 3;
                viewHolder.ivPic.setLayoutParams(lp);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == list.size()) {
                viewHolder.deleteIcon.setVisibility(View.GONE);
                viewHolder.ivPic.setImageResource(R.drawable.moment_add_pic);
                viewHolder.coverText.setVisibility(View.GONE);
                viewHolder.ivPic.setVisibility(View.VISIBLE);
                if (position == maxPhoto) {
                    viewHolder.ivPic.setVisibility(View.GONE);
                }
            } else {
                String uri = list.get(position).titleMedia.url;

                if (!TextUtils.isEmpty(uri)) {
                    if (uri.startsWith("http")) {
                        uri = CommonUtils.getOSSStyle(uri, MyConstants.OSS_SMALL_STYLE);
                    } else {
                        uri = MyConstants.FILE + uri;
                    }

                    if (position == 0) {
                        viewHolder.coverText.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.coverText.setVisibility(View.GONE);
                    }

                    viewHolder.ivPic.setVisibility(View.VISIBLE);
                    ImageLoader.resizeMiddle(viewHolder.ivPic, uri, 1);
                    viewHolder.deleteIcon.setVisibility(View.VISIBLE);
                }
            }

            viewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != selectBitmap && selectBitmap.size() > 0) {
                        selectBitmap.remove(position);
                        GridViewAdapter.this.notifyDataSetChanged();
                        scrollView.smoothScrollTo(0, scrollView.getScrollY() + 1);
                    }
                }
            });

            // 图片点击事件
            viewHolder.ivPic.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // 如果是最后一张，则图片为添加新图片，跳转至选择图片的activity
                    if (position == list.size()) {
//						createHomeCaminaDialog();
                        PhotoDialog dialog = new PhotoDialog(EasyArticleActivity.this);
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
                                Intent intent = new Intent(EasyArticleActivity.this, AlbumNewActivity.class);
                                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                                intent.putExtra(MyConstants.MAX_ALBUM_COUNT, maxPhoto);
                                intent.putExtra(MyConstants.EXTRA_ALBUM_FROM_ARTICLE, MyConstants.EXTRA_ALBUM_FROM_ARTICLE);
                                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM);
                            }
                        });
                        dialog.show();
                        return;
                    }
                    Intent intent = new Intent(EasyArticleActivity.this, ArticleEditLoopActivity.class);
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
            private ImageView deleteIcon;
            private TextView coverText;
        }
    }

    private class UploadPicTask extends AsyncTask<ImageItem, Void, ImageItem> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ImageItem doInBackground(ImageItem... params) {
            ImageItem imageItem = params[0];
            String urlPath = imageItem.imagePath;
            String fileName = System.currentTimeMillis() + ".jpg";
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String key = picKey + fileName;
            BufferedInputStream in;
            try {
                Bitmap bm = ImageUtils.revitionImageSize(urlPath);
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), System.currentTimeMillis() + ".jpg");
                in = new BufferedInputStream(new FileInputStream(new File(picPath)));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                if (null == imageItem.titleMedia) {
                    imageItem.titleMedia = new FloridData.TitleMedia();
                }
                imageItem.titleMedia.width = options.outWidth;
                imageItem.titleMedia.height = options.outHeight;
                imageItem.titleMedia.mimeType = options.outMimeType;
                imageItem.titleMedia.url = imageItem.imagePath;
                in.close();
                AliUpdateEvent updateEvent = new AliUpdateEvent(EasyArticleActivity.this, picPath, folder, key);
                PutObjectResult result = updateEvent.putObjectFromLocalFile();
                if (result != null) {
                    imageItem.titleMedia.url = MyConstants.OSS_IMG_HEAD + key;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return imageItem;

        }

        @Override
        protected void onPostExecute(ImageItem result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result.titleMedia.url)) {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = result.titleMedia.url;
                taskHandler.sendMessage(msg);
            } else {
                if (!TextUtils.isEmpty(title)) {
                    taskCount = taskTotal;
                }
                isStop = true;
                getTitleBar().getTitleTextRight().setOnClickListener(EasyArticleActivity.this);
                toast("图片上传失败，请重试");
                ProgressDialog.closeProgress();
                commentImgs = "";
            }
        }
    }

}
