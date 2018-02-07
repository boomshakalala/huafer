package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.AlbumAdapter;
import com.huapu.huafen.album.utils.AlbumHelper;
import com.huapu.huafen.album.utils.ImageShowManager;
import com.huapu.huafen.beans.ImageBucket;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.Logger;

import java.util.ArrayList;
import java.util.Date;

/**
 * 自定义相册
 *
 * @author liang_xs
 */
public class AlbumNewActivity extends BaseActivity implements OnClickListener {
    private Button btnPreview, btnNext;
    private RecyclerView gvAlbum;
    private TextView tvNoPhoto;
    private View header;
    private AlbumAdapter adapter;
    private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();

    private AlbumHelper helper;
    private ArrayList<ImageBucket> contentList;
    public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
    private ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
    private ImageShowManager imageManager;
    private String from = ""; // from==1为来自首页，启动到发布页面，否则setResult返回上一页
    //	private String imgPath = "";
    private ImageView ivCamera, ivPic1, ivPic2, ivPic3, ivPic4;
    private int maxCount = MyConstants.MATCH_SELECT_DYNAMIC_PHOTO;
    private String fromArticle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_new);
        if (getIntent().hasExtra(MyConstants.MAX_ALBUM_COUNT)) {
            maxCount = getIntent().getIntExtra(MyConstants.MAX_ALBUM_COUNT, MyConstants.MATCH_SELECT_DYNAMIC_PHOTO);
        }

        if (getIntent().hasExtra(MyConstants.EXTRA_ALBUM_FROM_ARTICLE)) {
            fromArticle = getIntent().getStringExtra(MyConstants.EXTRA_ALBUM_FROM_ARTICLE);
            Logger.e("get data:" + fromArticle);
        }

        initView();
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_BITMAP)) {
            selectBitmap = (ArrayList<ImageItem>) getIntent().getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
            if (adapter != null && selectBitmap != null) {
                adapter.setSelectBitmap(selectBitmap);
            }
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_ALBUM_FROM_MAIN)) {
            from = getIntent().getStringExtra(MyConstants.EXTRA_ALBUM_FROM_MAIN);
        }


        refreshBtnRight(selectBitmap.size());
        ProgressDialog.showProgress(AlbumNewActivity.this);
        QueryPhotoFromSD asyn = new QueryPhotoFromSD();
        asyn.execute();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataList = null;
    }

    private void initView() {
        getTitleBar().
                setTitle("所有照片").
                setLeftText("取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).
                setRightText("选择相册", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AlbumNewActivity.this, AlbumListActivity.class);
                        intent.putExtra(AlbumListActivity.EXTRA_IMAGE_BUCKETLIST, contentList);
                        startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_ALBUMLIST);
                    }
                });

        btnPreview = (Button) findViewById(R.id.btnPreview);
        btnNext = (Button) findViewById(R.id.btnNext);
        gvAlbum = (RecyclerView) findViewById(R.id.gvAlbum);
        tvNoPhoto = (TextView) findViewById(R.id.tvNoPhoto);
        GridLayoutManager gManager = new GridLayoutManager(this, 4);
        gManager.setOrientation(GridLayoutManager.VERTICAL);
        gvAlbum.setLayoutManager(gManager);

//        if (!TextUtils.isEmpty(fromArticle) && fromArticle.equals(MyConstants.EXTRA_ALBUM_FROM_ARTICLE)) {
//            adapter = new AlbumAdapter(this, fromArticle);
//        } else {
//            adapter = new AlbumAdapter(this);
//        }
        adapter = new AlbumAdapter(this);

        adapter.setOnItemChangeListener(new AlbumAdapter.OnItemChangeListener() {
            @Override
            public void onItemChange(ArrayList<ImageItem> selectBitmap) {
                refreshBtnRight(selectBitmap.size());
            }
        });
        gvAlbum.setAdapter(adapter.getWrapperAdapter());
        adapter.setMaxCount(maxCount);
    }

    /**
     * 获取数据
     */
    private void initData() {
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<>();
        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }
        for (ImageItem bean : dataList) {
            bean.isSelected = false;
        }
        if (!ArrayUtil.isEmpty(selectBitmap)) {
            for (ImageItem bean : selectBitmap) {
                bean.isSelected = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnPreview:// 预览
                if (adapter.getSelectBitmap().size() == 0) {
                    toast("请选择图片");
                } else {
                    intent = new Intent(AlbumNewActivity.this, GalleryAllFileActivity.class);
                    // 已选择的图片及位置坐标
                    intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, adapter.getSelectBitmap());
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, 0);
                    startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
                }
                break;
            case R.id.btnNext:// 完成，下一步
                if (adapter.getSelectBitmap().size() == 0) {
                    toast("请选择图片");
                    return;
                }
                if (from.equals("1")) {
                    intent = new Intent(AlbumNewActivity.this, ReleaseActivity.class);
                    // 已选择的图片及位置坐标
                    intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, adapter.getSelectBitmap());
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent();
                    // 已选择的图片及位置坐标
                    intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, adapter.getSelectBitmap());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                ArrayList<ImageItem> selectBitmap = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (selectBitmap != null) {
                    for (ImageItem item : dataList) {
                        item.isSelected = false;
                    }
                    for (ImageItem bean : selectBitmap) {
                        bean.isSelected = true;
                    }
                    adapter.setSelectBitmap(selectBitmap);
                    refreshBtnRight(selectBitmap.size());
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_ALBUMLIST) {
                ArrayList<ImageItem> imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_ALBUM_LIST_IMAGES);
                adapter.setData(imageItems);
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
//				Bundle bundle = data.getExtras();
//				//获取相机返回的数据，并转换为图片格式
//				Bitmap bitmap = (Bitmap)bundle.get("data");
//				String imgUri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", "description");
//				imgPath = ImageUtils.getFilePathByContentResolver(this, Uri.parse(imgUri));
                // 最后通知图库更新
//				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imgPath)));
                // 通知手机刷新图片数据库
                ContentValues values = new ContentValues(7);
                values.put(MediaStore.Images.Media.TITLE, adapter.imgPath);
                values.put(MediaStore.Images.Media.DISPLAY_NAME, adapter.imgPath);
                values.put(MediaStore.Images.Media.DATE_TAKEN,
                        new Date().getTime());
                values.put(MediaStore.Images.Media.MIME_TYPE,
                        "image/jpeg");
                values.put(MediaStore.Images.ImageColumns.BUCKET_ID,
                        adapter.imgPath.hashCode());
                values.put(
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        adapter.imgPath);
                values.put(MediaStore.Images.Media.DATA, adapter.imgPath);
                ContentResolver contentResolver = getApplicationContext()
                        .getContentResolver();
                Uri uri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);
                ImageItem item = new ImageItem();
                item.imagePath = adapter.imgPath;
                item.isSelected = true;
                ArrayList<ImageItem> selectBitmap = adapter.getSelectBitmap();
                selectBitmap.add(item);
                Intent intent = new Intent();
                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                setResult(RESULT_OK, intent);
                finish();
            }
        }

    }

    private void initListener() {
        btnPreview.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (adapter != null) {
            if (adapter.getSelectBitmap().size() > 0) {
                adapter.getSelectBitmap().clear();
            }
        }
        super.onBackPressed();
    }

    /**
     * 刷新右侧按钮
     *
     * @param selectSize
     */
    private void refreshBtnRight(int selectSize) {
        btnNext.setText("(" + selectSize + "/" + (maxCount > 0 ? maxCount : MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) + ")" + "完成");
    }

    private class QueryPhotoFromSD extends AsyncTask<Void, Void, Void> {

        public QueryPhotoFromSD() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            initData();
            return null;
        }

        @Override
        protected void onPostExecute(Void n) {
            super.onPostExecute(n);
            adapter.setData(dataList);
            ProgressDialog.closeProgress();
        }

    }
}
