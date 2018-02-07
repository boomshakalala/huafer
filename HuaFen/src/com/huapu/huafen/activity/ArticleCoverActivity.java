package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.MomentCategoryBean;
import com.huapu.huafen.callbacks.SimpleTextWatcher;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.ShareArticleLayout;
import com.huapu.huafen.views.TagsContainer;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 花语图文模式
 * Created by admin on 2017/4/25.
 */
public class ArticleCoverActivity extends BaseActivity {

    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.tvSave)
    TextView tvSave;
    @BindView(R.id.tagsContainer)
    TagsContainer tagsContainer;
    @BindView(R.id.addSection)
    SimpleDraweeView addSection;
    @BindView(R.id.etInputTitle)
    EditText etInputTitle;
    @BindView(R.id.tvPickClassification)
    TextView tvPickClassification;
    @BindView(R.id.tvInputCount)
    TextView tvInputCount;
    @BindView(R.id.editCover)
    FrameLayout editCover;
    @BindView(R.id.shareArticleLayout)
    ShareArticleLayout shareArticleLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    private FloridData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_cover);
        data = (FloridData) mIntent.getSerializableExtra(MyConstants.ARTICLE_DATA);
        if (data == null) {
            data = new FloridData();
            data.titleMedia = new FloridData.TitleMedia();
        }

        if (data == null) {
            final TextDialog dialog = new TextDialog(this, false);
            dialog.setContentText("数据异常");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                    finish();
                }
            });
            return;
        }

        initTitle();
        initView();
        initData();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 180);

    }

    private void initTitle() {
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etInputTitle.getText().toString().trim())) {
                    toast("请填写标题");
                    return;
                }
                if (TextUtils.isEmpty(data.titleMedia.url)) {
                    toast("请选择图片");
                    return;
                }

                if (!TextUtils.isEmpty(etInputTitle.getText().toString().trim()) && etInputTitle.getText().toString().trim().length() > 36) {
                    toast("标题字数超过限制");
                    return;
                }

                if (TextUtils.isEmpty(data.categoryName) || data.categoryId == 0) {
                    toast("请选择分类");
                    return;
                }
                data.title = etInputTitle.getText().toString().trim();
                Intent intent = new Intent();
                intent.setClass(ArticleCoverActivity.this, ArticleSectionsActivity.class);
                data.state = shareArticleLayout.getShareState();
                intent.putExtra(MyConstants.ARTICLE_DATA, data);
                startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_ARTICLE_SECTIONS);
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toast("敬请期待");
            }
        });
    }

    private void initView() {
        addSection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CommonUtils.pickSingleImage(ArticleCoverActivity.this);
            }
        });

        editCover.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToEdit(data.titleMedia);
            }
        });


        tvPickClassification.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticleCoverActivity.this, MomentCategoryActivity.class);
                startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_PICK_CATEGORY);
            }
        });

        etInputTitle.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    String content = s.toString().trim();
                    int count = 36 - content.length();
                    if (count < 0) {
                        tvInputCount.setTextColor(getResources().getColor(R.color.base_pink));
                    } else {
                        tvInputCount.setTextColor(getResources().getColor(R.color.text_black_light));
                    }
                    String format = String.format(getResources().getString(R.string.article_cover_input), count);
                    tvInputCount.setText(format);
                    data.title = content;
                } else {
                    tvInputCount.setTextColor(getResources().getColor(R.color.text_black_light));
                    tvInputCount.setText(String.format(getResources().getString(R.string.article_cover_input), 36));
                    data.title = null;
                }
            }
        });

        tvInputCount.setText(String.format(getResources().getString(R.string.article_cover_input), 36));
    }

    private void initData() {

        //tagContainer
        if (this.data.titleMedia != null && !TextUtils.isEmpty(this.data.titleMedia.url)) {
            tagsContainer.setData(this.data.titleMedia);
            addSection.setVisibility(View.GONE);
            tagsContainer.setVisibility(View.VISIBLE);
            editCover.setVisibility(View.VISIBLE);
        } else {
            tagsContainer.clear();
            addSection.setVisibility(View.VISIBLE);
            tagsContainer.setVisibility(View.GONE);
            editCover.setVisibility(View.GONE);
        }

        //etInputTitle

        if (!TextUtils.isEmpty(this.data.title)) {
            etInputTitle.setText(this.data.title);
        }

        //tvPickClassification

        String text = "选择分类";
        int textColor = R.color.base_pink;
        int drawable = R.drawable.text_white_pink_rect_bg;
        String categoryName = this.data.categoryName;

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

    private void goToEdit(FloridData.TitleMedia media) {
        Intent intent = new Intent(this, ArticleEditLoopActivity.class);
        ArrayList<ImageItem> list = new ArrayList<>();
        ImageItem imageItem = new ImageItem();
        imageItem.titleMedia = media;
        list.add(imageItem);
        intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, list);
        intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, 0);
        intent.putExtra(MyConstants.SECTION, 0);
        startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM) {
                if (data != null) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                    if (!ArrayUtil.isEmpty(images)) {
                        ImageItem item = images.get(0);
                        this.data.titleMedia.url = item.getImagePath();
                        int[] outBorders = CommonUtils.measureBitmap(this.data.titleMedia.url);
                        this.data.titleMedia.width = outBorders[0];
                        this.data.titleMedia.height = outBorders[1];
                        initData();
                        goToEdit(this.data.titleMedia);
                    }
                }
            } else if (requestCode == MyConstants.REQUEST_CODE_FOR_ARTICLE_SECTIONS) {
                if (data != null) {
                    boolean isNeedsFinishActivity = data.getBooleanExtra(MyConstants.FINISH_ACTIVITY, false);
                    if (isNeedsFinishActivity) {
                        finish();
                    }
                }
            } else if (requestCode == MyConstants.REQUEST_CODE_FOR_PICK_CATEGORY) {
                if (data != null) {
                    MomentCategoryBean.ObjBean.CatsBean bean = (MomentCategoryBean.ObjBean.CatsBean) data.getSerializableExtra("CATEGOR_BEAN");
                    if (bean != null) {
                        this.data.categoryId = bean.getCatId();
                        this.data.categoryName = bean.getName();
                        initData();
                    }
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                if (data != null) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                    if (!ArrayUtil.isEmpty(images)) {
                        ImageItem item = images.get(0);
                        this.data.titleMedia = item.titleMedia;
                        if (!this.data.titleMedia.url.startsWith(MyConstants.HTTP) && !this.data.titleMedia.url.startsWith(MyConstants.HTTPS)) {
                            int[] outBorders = CommonUtils.measureBitmap(this.data.titleMedia.url);
                            this.data.titleMedia.width = outBorders[0];
                            this.data.titleMedia.height = outBorders[1];
                        }
                        initData();
                    }
                }

            }
        }


    }


}
