/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.album.utils.AlbumHelper;
import com.huapu.huafen.beans.ImageBucket;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.FilePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;

/**
 * 预览本地图片
 *
 * @author liang_xs
 */
public class GalleryAllFileActivity extends BaseActivity {

    private ArrayList<ImageItem> selectBitmap = new ArrayList<>();
    private ArrayList<ImageItem> dataList = new ArrayList<>();
    private int position;
    private Button btnRightChecked;
    private int selectPosition;
    private FilePagerAdapter pagerAdapter;
    private Button btnFinish;
    private TextView tvTitleGallery;
    private String from = ""; // 1来自发布页预览
    private String fromArticle;
    private List<String> items = new ArrayList<>();
    private int maxCount = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_file);
//        this.dateList=dateList;
//        dateList=dataList;
//        dateList.remove(0);
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_BITMAP)) {
            selectBitmap = (ArrayList<ImageItem>) getIntent().getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_IMAGES)) {
            dataList = (ArrayList<ImageItem>) getIntent().getSerializableExtra(MyConstants.EXTRA_IMAGES);
            // 移除相册第一条数据，其为空值，相册内第一条为跳转相册
            dataList.remove(0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_IMAGE_INDEX)) {
            position = getIntent().getIntExtra(MyConstants.EXTRA_IMAGE_INDEX, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_TO_GALLERY_FROM)) {
            from = getIntent().getStringExtra(MyConstants.EXTRA_TO_GALLERY_FROM);
        }

        if (getIntent().hasExtra(MyConstants.EXTRA_ALBUM_FROM_ARTICLE)) {
            fromArticle = getIntent().getStringExtra(MyConstants.EXTRA_ALBUM_FROM_ARTICLE);
        }

        if (getIntent().hasExtra(MyConstants.MAX_ALBUM_COUNT)) {
            maxCount = getIntent().getIntExtra(MyConstants.MAX_ALBUM_COUNT, 8);
        }

        initData();
    }

    private void initView() {
        Button btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRightChecked = (Button) findViewById(R.id.btnRightChecked);
        tvTitleGallery = (TextView) findViewById(R.id.tvTitleGallery);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        if (selectBitmap.size() != 0) {
            tvTitleGallery.setText(0 + " / " + dataList.size());
        } else {
            tvTitleGallery.setText(0 + " / " + selectBitmap.size());
        }
        btnFinish.setText("(" + selectBitmap.size() + "/" + maxCount + ")完成");
        Button btnDraw = (Button) findViewById(R.id.btnDraw);
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setVisibility(View.GONE);
        if (from.equals("1")) {
            btnDraw.setVisibility(View.VISIBLE);
        } else {
            btnDraw.setVisibility(View.GONE);
        }
        if (dataList.size() > 0) {
            for (ImageItem item : dataList) {
                items.add(item.imagePath);
            }
        } else {
            for (ImageItem item : selectBitmap) {
                items.add(item.imagePath);
            }
        }
        pagerAdapter = new FilePagerAdapter(this, items);
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {

            @Override
            public void onItemChange(int currentPosition) {

                selectPosition = currentPosition;
                if (dataList.size() != 0) {
                    tvTitleGallery.setText((currentPosition + 1) + " / " + dataList.size());
                    if (dataList.get(currentPosition).isSelected) {
                        btnRightChecked.setSelected(true);
                    } else {
                        btnRightChecked.setSelected(false);
                    }
                } else {
                    tvTitleGallery.setText((currentPosition + 1) + " / " + selectBitmap.size());
                    if (selectBitmap.get(currentPosition).isSelected) {
                        btnRightChecked.setSelected(true);
                    } else {
                        btnRightChecked.setSelected(false);
                    }
                }

            }
        });

        GalleryViewPager mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(position);

        btnRightChecked.setOnClickListener(this);
        btnDraw.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnLeft:
                finish();
                break;
            case R.id.btnRightChecked:
                ImageItem item;
                if (dataList.size() != 0) {
                    item = dataList.get(selectPosition);
                    if (item.isSelected) {
                        if (!TextUtils.isEmpty(fromArticle) && fromArticle.equals(MyConstants.EXTRA_ALBUM_FROM_ARTICLE)) {
                            for (ImageItem bean : selectBitmap) {
                                if (bean.imagePath.equals(item.imagePath)) {
                                    if (null != bean.titleMedia && !TextUtils.isEmpty(bean.titleMedia.url)) {
                                        toast(getResources().getString(R.string.cannot_edit));
                                        break;
                                    } else if (null == bean.titleMedia || TextUtils.isEmpty(bean.titleMedia.url)) {
                                        item.isSelected = false;
                                        btnRightChecked.setSelected(false);
                                        selectBitmap.remove(bean);
                                        break;
                                    }
                                }

                            }

                        } else {
                            item.isSelected = false;
                            btnRightChecked.setSelected(false);
                            for (ImageItem bean : selectBitmap) {
                                if (bean.imagePath.equals(item.imagePath)) {
                                    selectBitmap.remove(bean);
                                    break;
                                }
                            }
                        }
                    } else {
                        if (selectBitmap.size() >= maxCount) {
                            ToastUtil.toast(GalleryAllFileActivity.this, "达到数量上限");
                            return;
                        }
                        item.isSelected = true;
                        btnRightChecked.setSelected(true);
                        selectBitmap.add(item);
                    }
                } else {
                    item = selectBitmap.get(selectPosition);
                    if (item.isSelected) {
                        item.isSelected = false;
                        btnRightChecked.setSelected(false);
                        for (ImageItem bean : selectBitmap) {
                            if (bean.imagePath.equals(item.imagePath)) {
                                selectBitmap.remove(item);
                                break;
                            }
                        }
                    } else {
                        if (selectBitmap.size() >= maxCount) {
                            ToastUtil.toast(GalleryAllFileActivity.this, "达到数量上限");
                            return;
                        }
                        item.isSelected = true;
                        btnRightChecked.setSelected(true);
                        selectBitmap.add(item);
                    }
                }

                btnFinish.setText("("
                        + (selectBitmap.size()) + "/" + maxCount + ")"
                        + "完成");
                break;
            case R.id.btnFinish:
                if (!ArrayUtil.isEmpty(selectBitmap)) {
                    intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    toast("请选择一张图片");
                }
                break;

            case R.id.btnDraw://编辑按钮
                intent.setClass(getApplicationContext(), CropActivity.class);
                String path = selectBitmap.get(selectPosition).imagePath;
                intent.putExtra(CropActivity.FILEPATH, path);
                // 设置回调
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_GALLERY_TO_DRAWPHOTO);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_GALLERY_TO_DRAWPHOTO) {
                //将CropActivity中经过旋转、裁剪等处理后生成的图片保存新路径返回过来
                String path = data.getStringExtra(MyConstants.EXTRA_DRAW_PHOTO_PATH);
                selectBitmap.get(selectPosition).imagePath = path;
                items.clear();
                List<String> list = new ArrayList<String>();
                for (ImageItem item : selectBitmap) {
                    list.add(item.imagePath);
                }
                items.addAll(list);
                pagerAdapter.notifyDataSetChanged();
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_GALLERY_TO_CLIPPHOTO) {
                String path = data.getStringExtra(MyConstants.EXTRA_DRAW_PHOTO_PATH);
                selectBitmap.get(selectPosition).imagePath = path;
                items.clear();
                List<String> list = new ArrayList<String>();
                for (ImageItem item : selectBitmap) {
                    list.add(item.imagePath);
                }
                items.addAll(list);
                pagerAdapter.notifyDataSetChanged();
            }
        }
    }

    private Handler handler = new Handler();

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlbumHelper helper = AlbumHelper.getHelper();
                helper.init(getApplicationContext());

                ArrayList<ImageBucket> contentList = helper.getImagesBucketList(false);
                dataList = new ArrayList<>();
                for (int i = 0; i < contentList.size(); i++) {
                    dataList.addAll(contentList.get(i).imageList);
                }
//                for (ImageItem bean : dataList) {
//                    bean.isSelected = false;
//                }
                if (!ArrayUtil.isEmpty(selectBitmap)) {
                    for (ImageItem bean : selectBitmap) {
                        int index = dataList.indexOf(bean);
                        if (index >= 0) {
                            dataList.get(index).isSelected = true;
                        }
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                    }
                });
            }
        }).start();
    }

}