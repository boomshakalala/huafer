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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.looper.IndicatorView;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

/**
 * 预览网络图片
 *
 * @author liang_xs
 */
public class GalleryUrlActivity extends BaseActivity {
    
    private GalleryViewPager mViewPager;
    private View layout;
    private int position;
    private Button btnLeft;
    private Button btnRightChecked;
    private IndicatorView indicatorView;
    private int index;
    private List<String> items = new ArrayList<>();
    private boolean isLongPress;
    private TextDialog dialog;
    private boolean isCanDownload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_url);
        isNeedsFinishAnimation = false;
        if (getIntent().hasExtra(MyConstants.EXTRA_SHOW_PIC)) {
            items = (List<String>) getIntent().getSerializableExtra(
                    MyConstants.EXTRA_SHOW_PIC);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_BITMAP)) {
            items = (List<String>) getIntent().getSerializableExtra(
                    MyConstants.EXTRA_SELECT_BITMAP);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_CAN_DOWNLOAD)) {
            isCanDownload = getIntent().getBooleanExtra(MyConstants.EXTRA_CAN_DOWNLOAD, true);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_IMAGE_INDEX)) {
            position = getIntent().getIntExtra(MyConstants.EXTRA_IMAGE_INDEX,
                    -1);
        }

        initView();
        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
            @Override
            public void onItemChange(int currentPosition) {
                // Toast.makeText(GalleryUrlActivity.this, "Current item is " +
                // currentPosition, Toast.LENGTH_SHORT).show();
                position = currentPosition;
            }
        });
        if (isCanDownload) {
            pagerAdapter.setOnTouchLongClick(new UrlPagerAdapter.OnTouchLongClick() {

                @Override
                public void onLongClick() {
                    isLongPress = true;
                    dialog = new TextDialog(GalleryUrlActivity.this, true);
                    dialog.setContentText("是否要保存图片？");
                    dialog.setLeftText("取消");
                    dialog.setLeftCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            dialog.dismiss();
                            isLongPress = false;
                        }
                    });
                    dialog.setRightText("确定");
                    dialog.setRightCall(new DialogCallback() {
                        @Override
                        public void Click() {
                            dialog.dismiss();
                            btnRightChecked.performClick();
                            isLongPress = false;
                        }
                    });
                    dialog.show();
                }
            });
        }
        pagerAdapter.setOnViewClick(new UrlPagerAdapter.OnViewClick() {

            @Override
            public void onClick() {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        return;
                    }
                }
                GalleryUrlActivity.this.finish();
                GalleryUrlActivity.this.overridePendingTransition(0, 0);
            }
        });

        mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                indicatorView.setPosition(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        if (position != -1) {
            mViewPager.setCurrentItem(position);
        }
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) indicatorView.getLayoutParams();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        indicatorView.setCount(items.size());
        indicatorView.setPosition(position);
    }

    private void initView() {
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRightChecked = (Button) findViewById(R.id.btnRightChecked);
        indicatorView = (IndicatorView) findViewById(R.id.indicator);
        layout = findViewById(R.id.layoutBottom);
        layout.setVisibility(View.GONE);
        btnRightChecked.setBackgroundResource(0);
        btnLeft.setOnClickListener(this);
        btnRightChecked.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                finish();
                break;
            case R.id.btnRightChecked:
                if (position < 0 || position > (items.size() - 1)) {
                    return;
                }
                if (FileUtils.isSDCardExist()) {
                    ProgressDialog.showProgress(this);
                    OkHttpClientManager.downloadAsync(items.get(position), FileUtils.getHDir(), System.currentTimeMillis() + ".jpg", new OkHttpClientManager.StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            ToastUtil.toast(GalleryUrlActivity.this, "图片保存失败");
                            ProgressDialog.closeProgress();
                        }

                        @Override
                        public void onResponse(String response) {
                            ProgressDialog.closeProgress();
                            ContentValues values = new ContentValues(7);
                            values.put(MediaStore.Images.Media.TITLE, response);
                            values.put(MediaStore.Images.Media.DISPLAY_NAME, response);
                            values.put(MediaStore.Images.Media.DATE_TAKEN,
                                    new Date().getTime());
                            values.put(MediaStore.Images.Media.MIME_TYPE,
                                    "image/jpeg");
                            values.put(MediaStore.Images.ImageColumns.BUCKET_ID,
                                    response.hashCode());
                            values.put(
                                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                                    response);
                            values.put("_data", response);
                            ContentResolver contentResolver = GalleryUrlActivity.this.getApplicationContext()
                                    .getContentResolver();
                            Uri uri = contentResolver.insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values);
                            ToastUtil.toast(GalleryUrlActivity.this, "图片保存成功");
                        }
                    });
                } else {
                    ToastUtil.toast(GalleryUrlActivity.this, "sd卡不可用，无法保存");
                }
                break;
        }
    }
}