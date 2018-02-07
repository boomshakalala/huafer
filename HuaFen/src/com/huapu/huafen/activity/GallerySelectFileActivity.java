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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.FilePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;

/**
 * 预览选中的本地图片
 *
 * @author liang_xs
 */
public class GallerySelectFileActivity extends BaseActivity implements
        OnClickListener {

    private GalleryViewPager mViewPager;
    private FilePagerAdapter pagerAdapter;
    private Button btnLeft, btnRight;
    private TextView tvBtnFirst;
    private ImageView ivBtnDel;
    private TextView tvTitleGallery;
    private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();
    private int position;
    private List<String> items = new ArrayList<String>();
    private int positionFirst;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_select_file);
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_BITMAP)) {
            selectBitmap = (ArrayList<ImageItem>) getIntent()
                    .getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_IMAGE_INDEX)) {
            position = getIntent()
                    .getIntExtra(MyConstants.EXTRA_IMAGE_INDEX, 0);
        }
        if(ArrayUtil.isEmpty(selectBitmap) || position < 0) {
            toast("图片数据异常，请退出重试");
            return;
        }
        positionFirst = 0;
        initView();
    }

    private void initView() {
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        tvTitleGallery = (TextView) findViewById(R.id.tvTitleGallery);
        ivBtnDel = (ImageView) findViewById(R.id.ivBtnDel);
        tvBtnFirst = (TextView) findViewById(R.id.tvBtnFirst);
        for (ImageItem item : selectBitmap) {
            items.add(item.imagePath);
        }
        pagerAdapter = new FilePagerAdapter(this, items);
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
            @Override
            public void onItemChange(int currentPosition) {
                position = currentPosition;
                initBtn();
            }
        });

        mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(position);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        ivBtnDel.setOnClickListener(this);
        initBtn();
    }

    private void initBtn() {
        tvTitleGallery.setText((position + 1) + " / " + selectBitmap.size());
        if(position == positionFirst) {
            tvBtnFirst.setText("已为主图");
            tvBtnFirst.setOnClickListener(null);
            tvBtnFirst.setTextColor(getResources().getColor(R.color.text_color_gray));
        } else {
            tvBtnFirst.setText("设置为主图");
            tvBtnFirst.setOnClickListener(this);
            tvBtnFirst.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnLeft:
                onBackPressed();
                break;
            case R.id.btnRight:
                intent.setClass(getApplicationContext(), CropActivity.class);
                String path = selectBitmap.get(position).imagePath;
                intent.putExtra(CropActivity.FILEPATH, path);
                // 设置回调
                startActivityForResult(intent,
                        MyConstants.INTENT_FOR_RESULT_GALLERY_TO_DRAWPHOTO);
                break;

            case R.id.ivBtnDel:
                final TextDialog dialog = new TextDialog(this, false);
                dialog.setContentText("您确定删除这张照片吗？");
                dialog.setLeftText("取消");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("删除");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        if(!ArrayUtil.isEmpty(selectBitmap) && selectBitmap.size() > position) {
                            selectBitmap.remove(position);
                            if (selectBitmap.size() == 0) {
                                Intent intent = new Intent();
                                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                items = new ArrayList<String>();
                                for (ImageItem item : selectBitmap) {
                                    items.add(item.imagePath);
                                }
                                pagerAdapter.setData(items);
                                if(position == positionFirst) {
                                    positionFirst = 0;
                                } else if(position < positionFirst) {
                                    positionFirst = positionFirst - 1;
                                }
                                initBtn();
                            }
                        }
                    }
                });
                dialog.show();
                break;

            case R.id.tvBtnFirst:
                positionFirst = position;
                toast("已设置为主图");
                initBtn();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(!ArrayUtil.isEmpty(selectBitmap) && selectBitmap.size() > positionFirst) {
            ImageItem item = selectBitmap.get(positionFirst);
            selectBitmap.remove(positionFirst);
            selectBitmap.add(0, item);
        }
        Intent intent = new Intent();
        intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_GALLERY_TO_DRAWPHOTO) {
                //将CropActivity中经过旋转、裁剪等处理后生成的图片保存新路径返回过来
                String path = data.getStringExtra(MyConstants.EXTRA_DRAW_PHOTO_PATH);
                selectBitmap.get(position).imagePath = path;
                items.clear();
                List<String> list = new ArrayList<String>();
                for (ImageItem item : selectBitmap) {
                    list.add(item.imagePath);
                }
                items.addAll(list);
                pagerAdapter.notifyDataSetChanged();
                toast("照片编辑成功");
            }
        }
    }

}