package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.ArticleSectionAdapter;
import com.huapu.huafen.beans.ArticleDetailResult;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.PublishSuccessEvent;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.OSSUploader;
import com.huapu.huafen.utils.ShareHelper;
import com.huapu.huafen.views.TagsContainer;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;

/**
 * 发布图文混排花语第二步
 * Created by admin on 2017/4/26.
 */
public class ArticleSectionsActivity extends BaseActivity {

    private static final String TAG = ArticleSectionsActivity.class.getSimpleName();
    private static final int REQUEST_CODE_FOR_PREVIEW = 56;
    //    @BindView(R2.id.tvSave) TextView tvSave;
    @BindView(R2.id.tvPublish)
    TextView tvPublish;
    @BindView(R2.id.tvPreview)
    TextView tvPreview;
    @BindView(R2.id.tvSort)
    TextView tvSort;
    @BindView(R2.id.articleSections)
    RecyclerView articleSections;
    @BindView(R2.id.ivBack)
    ImageView ivBack;
    private ArticleSectionAdapter adapter;

    private FloridData data;
    private TextView tvAddArticle;
    private TagsContainer tagsContainer;
    private FrameLayout editCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_sections);
        if (getIntent().hasExtra(MyConstants.ARTICLE_DATA)) {
            data = (FloridData) getIntent().getSerializableExtra(MyConstants.ARTICLE_DATA);
        }
        if (data == null) {
            finish();
        }
        initTitle();
        initView();
        initData();
    }

    private void initTitle() {
//        tvSave.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        tvPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean canBePublished = adapter.isCanBePublish() && (!TextUtils.isEmpty(data.title) && data.titleMedia != null && !TextUtils.isEmpty(data.titleMedia.url));
                if (canBePublished) {
                    Intent intent = new Intent(ArticleSectionsActivity.this, ArticlePreviewActivity.class);
                    data.sections = adapter.getData();
                    intent.putExtra(MyConstants.ARTICLE_DATA, data);
                    startActivityForResult(intent, REQUEST_CODE_FOR_PREVIEW);
                } else {
                    toast("您有未完成的内容，请填写完成后，再进行预览");
                }
            }
        });

        tvSort.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (adapter != null && !ArrayUtil.isEmpty(adapter.getData())) {
                    boolean canBePublished = adapter.isCanBePublish();
                    if (!canBePublished) {
                        toast("您有未完成的内容，请填写完成后，再进行排序");
                        return;
                    }
                    Intent intent = new Intent(ArticleSectionsActivity.this, ArticleSortActivity.class);
                    ArrayList<FloridData.Section> tmp = new ArrayList<FloridData.Section>();
                    for (FloridData.Section section : adapter.getData()) {
                        tmp.add(section);
                    }

                    intent.putExtra(MyConstants.SECTION_LIST, tmp);
                    startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SWAP_SECTIONS);
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        articleSections.setLayoutManager(manager);
        adapter = new ArticleSectionAdapter(this);
        articleSections.setAdapter(adapter.getWrapperAdapter());

        final View sectionHeader = LayoutInflater.from(this).inflate(R.layout.article_sections_header, articleSections, false);
        tagsContainer = (TagsContainer) sectionHeader.findViewById(R.id.tagsContainer);
        editCover = (FrameLayout) sectionHeader.findViewById(R.id.editCover);
        editCover.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToEdit(data.titleMedia);
            }
        });

        TextView tvTitle = (TextView) sectionHeader.findViewById(R.id.tvTitle);
        tvTitle.setText(data.title);
        tagsContainer.setData(data.titleMedia);


        View sectionFooter = LayoutInflater.from(this).inflate(R.layout.article_section_footer, articleSections, false);
        tvAddArticle = (TextView) sectionFooter.findViewById(R.id.tvAddArticle);

        tvAddArticle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (adapter.isOverFifty()) {
                    toast("您发布的内容过长（超过50条），请删除后再进行发布");
                    return;
                }
                boolean canBeAdded = adapter.isAddData();
                if (canBeAdded) {
                    adapter.addData();
                    articleSections.smoothScrollToPosition(adapter.getItemCount() + 1);
                } else {
                    toast("您有未完成的内容，请填写完成后，再添加花语");
                }
            }
        });


        adapter.addHeaderView(sectionHeader);
        adapter.addFootView(sectionFooter);


        tvPublish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ConfigUtil.isToVerify()) {
                    DialogManager.toVerify(ArticleSectionsActivity.this);
                } else {
                    publish();
                }
            }
        });
    }

    private void initData() {
        if (!ArrayUtil.isEmpty(data.sections)) {
            adapter.setData(data.sections);
        } else {
            adapter.addData();
        }
    }


    private void publish() {
        if (adapter != null) {
            boolean canBePublished = adapter.isCanBePublish() && (!TextUtils.isEmpty(data.title) && data.titleMedia != null && !TextUtils.isEmpty(data.titleMedia.url));
            if (canBePublished) {
                tvPublish.setEnabled(false);
                boolean isUploaded = data.titleMedia.url.startsWith(MyConstants.HTTP) || data.titleMedia.url.startsWith(MyConstants.HTTPS);
                if (isUploaded) {
                    uploadSections();
                } else {
                    OSSUploader.uploadSingleOSSAsync(this, OSSUploader.TYPE_MOMENT, data.titleMedia, new OSSUploader.GenLocalPath<FloridData.TitleMedia>() {

                        @Override
                        public String getLocalPath(FloridData.TitleMedia media) {
                            return media.url;
                        }

                        @Override
                        public void deal(FloridData.TitleMedia s, String path) {
                            s.url = path;
                        }

                    }, new OSSUploader.OnUploadSingeListener<FloridData.TitleMedia>() {

                        @Override
                        public void onUploadSuccess(FloridData.TitleMedia titleMedia) {
                            uploadSections();
                        }

                        @Override
                        public void onUploadFailed(FloridData.TitleMedia titleMedia) {
                            tvPublish.setEnabled(true);
                        }

                        @Override
                        public void onPreExecute() {

                        }
                    });
                }
            } else {
                toast("您有未填写完整的花语，请填写完整后上传");
            }
        }
    }

    private void uploadSections() {
        data.sections = adapter.getData();
        FloridData.Section section = data.sections.get(data.sections.size() - 1);

        if (TextUtils.isEmpty(section.media.url)) {
            data.sections.remove(data.sections.size() - 1);
            adapter.notifyWrapperDataSetChanged();
        }
        boolean isUploaded = isUploaded(data.sections);
        if (isUploaded) {
            doRequestToPublish();
        } else {
            OSSUploader.uploadOSSAsync(this, OSSUploader.TYPE_MOMENT, data.sections, new OSSUploader.GenLocalPath<FloridData.Section>() {

                @Override
                public String getLocalPath(FloridData.Section section) {
                    return section.media.url;
                }

                @Override
                public void deal(FloridData.Section section, String path) {
                    section.media.url = path;
                }

            }, new OSSUploader.OnUploadListener<FloridData.Section>() {

                @Override
                public void onPreExecute() {
                    ProgressDialog.showProgress(ArticleSectionsActivity.this);
                }

                @Override
                public void onUploadSuccess(final List<FloridData.Section> list) {
                    ProgressDialog.closeProgress();
                    doRequestToPublish();
                }

                @Override
                public void onUploadFailed(List<FloridData.Section> list) {
                    ProgressDialog.closeProgress();
                    tvPublish.setEnabled(true);
                    toast("图片上传失败");
                }
            });
        }

    }

    private boolean isUploaded(List<FloridData.Section> sections) {
        for (FloridData.Section ss : sections) {
            boolean flag1 = !ss.media.url.startsWith(MyConstants.HTTP);
            boolean flag2 = !ss.media.url.startsWith(MyConstants.HTTPS);
            boolean flag = flag1 && flag2;
            if (flag) {
                return false;
            }
        }
        return true;
    }

    private void doRequestToPublish() {
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();
        for (int i = 0; i < data.sections.size(); i++) {
            FloridData.Section ss = data.sections.get(i);
            ss.sequence = i;
        }
        String jsonData = JSON.toJSONString(data);
        params.put("article", jsonData);
        OkHttpClientManager.postAsyn(MyConstants.ARTICLE_SAVE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                tvPublish.setEnabled(true);
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, response);
                tvPublish.setEnabled(true);
                ProgressDialog.closeProgress();
                try {
                    ArticleDetailResult result = JSON.parseObject(response, ArticleDetailResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        PublishSuccessEvent event = new PublishSuccessEvent();
                        event.publishSuccess = true;
                        EventBus.getDefault().post(event);

                        if (data.articleId == 0) {
                            Intent pickIntent = new Intent(ArticleSectionsActivity.this, FlowerNewActivity.class);
                            pickIntent.putExtra(MyConstants.EXTRA_USER_ID, CommonPreference.getUserId());
                            pickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(pickIntent);
                        } else {
                            Intent pickIntent = new Intent(ArticleSectionsActivity.this, ArticleDetailActivity.class);
                            pickIntent.putExtra(MyConstants.ARTICLE_ID, String.valueOf(data.articleId));
                            pickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(pickIntent);
                        }

                        ShareHelper helper = new ShareHelper(ArticleSectionsActivity.this,
                                CommonPreference.getUserInfo().getUserName() + "的花语:" + data.title,
                                TextUtils.isEmpty(result.obj.article.summary)
                                        ? "" : (result.obj.article.summary.length() > 20
                                        ? result.obj.article.summary.substring(0, 20)
                                        : result.obj.article.summary), //内容

                                result.obj.article.titleMedia.url, //图片
                                MyConstants.SHARE_ARTICLE + result.obj.article.articleId,
                                data.state);
                        helper.runShare();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(MyConstants.FINISH_ACTIVITY, true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        CommonUtils.error(result, ArticleSectionsActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
        intent.putExtra(MyConstants.COVER, true);
        startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyConstants.REQUEST_CODE_FOR_SWAP_SECTIONS) {
                if (data != null) {
                    ArrayList<FloridData.Section> tmp = (ArrayList<FloridData.Section>) data.getSerializableExtra(MyConstants.SECTION_LIST);
                    LogUtil.e(TAG, tmp);
                    adapter.setData(tmp);
                    articleSections.scrollToPosition(0);
                }

            } else if (requestCode == REQUEST_CODE_FOR_PREVIEW) {
                if (data != null) {
                    boolean isNeedsFinishActivity = data.getBooleanExtra(MyConstants.FINISH_ACTIVITY, false);
                    if (isNeedsFinishActivity) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(MyConstants.FINISH_ACTIVITY, true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                if (data != null) {
                    boolean isArticleCover = data.getBooleanExtra(MyConstants.COVER, false);
                    if (isArticleCover) {
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                        if (!ArrayUtil.isEmpty(images)) {
                            ImageItem item = images.get(0);
                            this.data.titleMedia = item.titleMedia;
                            if (!this.data.titleMedia.url.startsWith(MyConstants.HTTP) && !this.data.titleMedia.url.startsWith(MyConstants.HTTPS)) {
                                int[] outBorders = CommonUtils.measureBitmap(this.data.titleMedia.url);
                                this.data.titleMedia.width = outBorders[0];
                                this.data.titleMedia.height = outBorders[1];
                            }
                            tagsContainer.setData(this.data.titleMedia);
                            tagsContainer.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (adapter != null) {
                            adapter.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }
            } else {
                if (adapter != null) {
                    adapter.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

    }
}
