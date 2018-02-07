package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.huapu.huafen.callbacks.CommonCallback;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.ArticleEditFilterAdapter;
import com.huapu.huafen.adapter.MomentImageGalleryAdapter;
import com.huapu.huafen.beans.ArticleFilterData;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.EditTextDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.BitmapUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.TagsContainer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 花语选择图片
 * Created by qwe on 2017/5/1.
 */
public class ArticleEditLoopActivity extends BaseActivity {

    @BindView(R2.id.bottomLayout)
    LinearLayout bottomLayout;
    @BindView(R2.id.viewPager)
    ViewPager galleryViewPager;
    @BindView(R2.id.rg)
    RadioGroup rg;
    @BindView(R2.id.llLabel)
    LinearLayout llLabel;
    @BindView(R2.id.flFilters)
    LinearLayout flFilters;
    @BindView(R2.id.recyclerFilter)
    RecyclerView recyclerFilter;
    private ArticleEditFilterAdapter adapter;
    private ArrayList<ImageItem> selectBitmap;

    private int selectedPosition;

    private MomentImageGalleryAdapter pagerAdapter;
    private int position;
    private TagsContainer tagsContainers;
    private boolean isArticleCover = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_BITMAP)) {
            selectBitmap = (ArrayList<ImageItem>) getIntent().getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
        }

        if (getIntent().hasExtra(MyConstants.EXTRA_IMAGE_INDEX)) {
            selectedPosition = getIntent().getIntExtra(MyConstants.EXTRA_IMAGE_INDEX, 0);
        }

        if (getIntent().hasExtra(MyConstants.SECTION)) {
            position = getIntent().getIntExtra(MyConstants.SECTION, -1);
        }

        if (getIntent().hasExtra(MyConstants.COVER)) {
            isArticleCover = getIntent().getBooleanExtra(MyConstants.COVER, false);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_loopedit);

        initView();
    }

    private void initView() {

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.filterText) {
                    pagerAdapter.setEditable(false);
                    llLabel.setVisibility(View.GONE);
                    flFilters.setVisibility(View.VISIBLE);
                    int index = galleryViewPager.getCurrentItem();
                    List<FloridData.TitleMedia> list = pagerAdapter.getTitleMediaList();
                    FloridData.TitleMedia media = list.get(index);
                    adapter.notifyDataSetChangeByUri(media);
                } else if (checkedId == R.id.labelText) {
                    pagerAdapter.setEditable(true);
                    llLabel.setVisibility(View.VISIBLE);
                    flFilters.setVisibility(View.GONE);
                }
            }
        });


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) galleryViewPager.getLayoutParams();
        params.width = CommonUtils.getScreenWidth();
        params.height = (int) (CommonUtils.getScreenWidth() / 0.92f);
        galleryViewPager.setLayoutParams(params);

        List<FloridData.TitleMedia> titleMediaList = new ArrayList<>(selectBitmap.size());
        for (ImageItem imageItem : selectBitmap) {
            titleMediaList.add(imageItem.titleMedia);
        }
        pagerAdapter = new MomentImageGalleryAdapter(titleMediaList);

        pagerAdapter.setOnHandlerTagsListener(new MomentImageGalleryAdapter.OnHandlerTagsListener() {

            @Override
            public void onAddTag(TagsContainer tagsContainers, int[] location, int position) {
                ArticleEditLoopActivity.this.tagsContainers = tagsContainers;
                Intent intent = new Intent(ArticleEditLoopActivity.this, TagActivity.class);
                intent.putExtra(MyConstants.LOCATION_X_Y, location);
                intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_TAG);
                overridePendingTransition(0, 0);
            }
        });
        galleryViewPager.setAdapter(pagerAdapter);
        galleryViewPager.setOffscreenPageLimit(selectBitmap.size() - 1);
        galleryViewPager.setCurrentItem(selectedPosition);
        galleryViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedPosition = position;
                getTitleBar().setTitle("编辑图片(" + (selectedPosition + 1) + "/" + selectBitmap.size() + ")");
                adapter.notifyDataSetChangeByUri(pagerAdapter.getTitleMediaList().get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        rg.check(R.id.labelText);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerFilter.setLayoutManager(layoutManager);
        adapter = new ArticleEditFilterAdapter(this);
        recyclerFilter.setAdapter(adapter);
        adapter.setOnItemCLickListener(new ArticleEditFilterAdapter.OnItemCLickListener() {

            @Override
            public void onItemClick(ArticleFilterData data) {
                FloridData.TitleMedia media = pagerAdapter.getTitleMediaList().get(selectedPosition);
                media.filterStyle = data.filterStyle;
                media.matrix = data.colorMatrix;
                pagerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setOnLeftButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final TextDialog dialog = new TextDialog(ArticleEditLoopActivity.this, true);
                dialog.setContentText("确定退出编辑吗");
                dialog.setLeftText("否");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();

                    }
                });
                dialog.setRightText("是");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
                dialog.show();
            }

        }).setTitle("编辑图片(" + (selectedPosition + 1) + "/" + selectBitmap.size() + ")")
                .setRightText("完成", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dealBitmaps(selectBitmap);
                    }
                });
    }

    private void dealBitmaps(ArrayList<ImageItem> sb) {
        ProgressDialog.showProgress(ArticleEditLoopActivity.this, false);
        // 处理
        dealWithBitmaps(sb);
    }

    private void dealBitmapsFinish(final ArrayList<ImageItem> selectBitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog.closeProgress();
                Intent intent = new Intent();
                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                intent.putExtra(MyConstants.SECTION, ArticleEditLoopActivity.this.position);
                intent.putExtra(MyConstants.COVER, isArticleCover);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private int tmpSelectedSize;
    private int selectedSize;

    private void dealWithBitmaps(ArrayList<ImageItem> selectBitmap) {
        ArrayList<ImageItem> list = new ArrayList<>();
        if (ArrayUtil.isEmpty(selectBitmap)) {
            ProgressDialog.closeProgress();
            return;
        }

        tmpSelectedSize = selectBitmap.size();
        selectedSize = tmpSelectedSize;

        for (ImageItem item : selectBitmap) {
            if (item != null && item.titleMedia != null) {
                dealWithBitmap(list, item);
            } else {
                tmpSelectedSize--;
            }
        }
    }

    private void dealWithBitmap(final ArrayList<ImageItem> list, final ImageItem item) {
        final FloridData.TitleMedia media = item.titleMedia;
        if (media.matrix != null && media.matrix.length > 0) {
            BitmapUtil.saveAsBitmap(media.url, media.matrix, new CommonCallback() {
                @Override
                public void callback(Object obj) {
                    String path = (String) obj;

                    if (!TextUtils.isEmpty(path)) {
                        media.url = path;
                        item.imagePath = path;
                    }

                    media.filterStyle = "无";
                    media.matrix = null;

                    isDealWithBitmapFinish(list, item);
                }
            });
        } else {
            tmpSelectedSize--;
            isDealWithBitmapFinish(list, item);
        }
    }

    private void isDealWithBitmapFinish(final ArrayList<ImageItem> list, final ImageItem item) {
        list.add(item);
        if (list.size() >= selectedSize) {
            // 结果
            dealBitmapsFinish(list);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM) {

                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (!ArrayUtil.isEmpty(images)) {
                    ImageItem item = images.get(0);
                    upDatePic(item.getImagePath());
                }

            } else if (requestCode == MyConstants.REQUEST_CODE_FOR_TAG) {
                if (data != null) {
                    FloridData.Tag tag = (FloridData.Tag) data.getSerializableExtra(MyConstants.ARTICLE_TAG);
                    int[] location = data.getIntArrayExtra(MyConstants.LOCATION_X_Y);
                    int index = data.getIntExtra(MyConstants.EXTRA_IMAGE_INDEX, -1);
                    if (tag != null && location != null && index >= 0 && index < pagerAdapter.getCount()) {
                        if (tagsContainers != null) {
                            tagsContainers.addTag(tag, location);
                        }
                    }
                }
            }
        }
    }


    public void upDatePic(String imagePath) {
        selectBitmap.get(selectedPosition).imagePath = imagePath;
        selectBitmap.get(selectedPosition).titleMedia.url = imagePath;
        List<FloridData.TitleMedia> stringList = new ArrayList<>(selectBitmap.size());
        for (ImageItem imageItem : selectBitmap) {
            stringList.add(imageItem.titleMedia);
        }
        pagerAdapter = new MomentImageGalleryAdapter(stringList);
        galleryViewPager.setAdapter(pagerAdapter);
        galleryViewPager.setCurrentItem(selectedPosition);
        pagerAdapter.setOnHandlerTagsListener(new MomentImageGalleryAdapter.OnHandlerTagsListener() {

            @Override
            public void onAddTag(TagsContainer tagsContainers, int[] location, int position) {
                ArticleEditLoopActivity.this.tagsContainers = tagsContainers;
                Intent intent = new Intent(ArticleEditLoopActivity.this, TagActivity.class);
                intent.putExtra(MyConstants.LOCATION_X_Y, location);
                intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_TAG);
                overridePendingTransition(0, 0);
            }
        });


        int index = galleryViewPager.getCurrentItem();
        List<FloridData.TitleMedia> list = pagerAdapter.getTitleMediaList();
        FloridData.TitleMedia media = list.get(index);
        media.tags = null;
        pagerAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChangeByUri(media);
    }

    @OnClick({R.id.filterText, R.id.labelText, R.id.editText, R.id.replaceText})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.editText:
                int index = galleryViewPager.getCurrentItem();
                List<FloridData.TitleMedia> list = pagerAdapter.getTitleMediaList();
                FloridData.TitleMedia media = list.get(index);

                if (!ArrayUtil.isEmpty(media.tags)) {
                    final TextDialog dialog = new TextDialog(ArticleEditLoopActivity.this, true);
                    dialog.setContentText("“裁剪”和“旋转”后已添加的标签将被删除，是否继续？");
                    dialog.setLeftText("取消");
                    dialog.setLeftCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            dialog.dismiss();

                        }
                    });
                    dialog.setRightText("确定");
                    dialog.setRightCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            dialog.dismiss();
                            EditTextDialog editTextDialog = new EditTextDialog(ArticleEditLoopActivity.this, selectBitmap.get(selectedPosition).titleMedia);
                            editTextDialog.show();
                        }
                    });
                    dialog.show();
                } else {
                    EditTextDialog editTextDialog = new EditTextDialog(ArticleEditLoopActivity.this, selectBitmap.get(selectedPosition).titleMedia);
                    editTextDialog.show();
                }
                break;
            case R.id.replaceText:
                final TextDialog dialog2 = new TextDialog(ArticleEditLoopActivity.this, true);
                dialog2.setContentText("替换后已添加的标签将被删除，是否继续？");
                dialog2.setLeftText("取消");
                dialog2.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog2.dismiss();

                    }
                });
                dialog2.setRightText("确定");
                dialog2.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog2.dismiss();
                        CommonUtils.pickSingleImage(ArticleEditLoopActivity.this);
                    }
                });
                dialog2.show();
                break;
        }
    }
}
