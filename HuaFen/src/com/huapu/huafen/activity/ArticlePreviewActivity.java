package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.ArticlePreviewAdapter;
import com.huapu.huafen.beans.ArticleDetailResult;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.PublishSuccessEvent;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.OSSUploader;
import com.huapu.huafen.utils.ShareHelper;
import com.squareup.okhttp.Request;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2017/4/28.
 */

public class ArticlePreviewActivity extends BaseActivity {

    @BindView(R2.id.articles) RecyclerView articles;
    private ArticlePreviewAdapter adapter ;
    private FloridData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_preview);
        data = (FloridData)mIntent.getSerializableExtra(MyConstants.ARTICLE_DATA);
        FloridData.Section emptySection = data.sections.get(data.sections.size() - 1);
        if(TextUtils.isEmpty(emptySection.media.url) && TextUtils.isEmpty(emptySection.content)){
            data.sections.remove(data.sections.size()-1);
        }
        initContainer();
        ArticleDetailResult result = new ArticleDetailResult();
        result.obj = new ArticleDetailResult.ArticleDetailData();
        result.obj.article = data;
        initData(result);
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("预览").setRightText("发布", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                publish();
            }
        });
    }

    private void initContainer() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        articles.setLayoutManager(linearLayoutManager);
        adapter = new ArticlePreviewAdapter(this);
        articles.setAdapter(adapter);
    }


    private void initData(final ArticleDetailResult result) {
        if(result!=null && result.obj!=null){
            initContainerData(result);
        }
    }

    private void initContainerData(ArticleDetailResult result) {
        adapter.setData(result);
    }

    private void publish(){
        if(adapter!=null){
            boolean isUploaded = data.titleMedia.url.startsWith(MyConstants.HTTP) || data.titleMedia.url.startsWith(MyConstants.HTTPS);
            if(isUploaded){
                uploadSections();
            }else{
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

                    }

                    @Override
                    public void onPreExecute() {

                    }
                });
            }
        }
    }

    private void uploadSections() {
        boolean isUploaded = isUploaded(data.sections);
        if(isUploaded){
            doRequestToPublish();
        }else{
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
                    ProgressDialog.showProgress(ArticlePreviewActivity.this);
                }

                @Override
                public void onUploadSuccess(final List<FloridData.Section> list) {
                    ProgressDialog.closeProgress();
                    doRequestToPublish();
                }

                @Override
                public void onUploadFailed(List<FloridData.Section> list) {
                    ProgressDialog.closeProgress();
                    toast("图片上传失败");
                }
            });
        }

    }

    private boolean isUploaded(List<FloridData.Section> sections){
        for(FloridData.Section ss:sections){
            boolean flag1 = !ss.media.url.startsWith(MyConstants.HTTP);
            boolean flag2 = !ss.media.url.startsWith(MyConstants.HTTPS);
            boolean flag = flag1 && flag2;
            if(flag){
                return false;
            }
        }
        return true;
    }

    private void doRequestToPublish(){
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();
        for(int i = 0;i <data.sections.size();i++){
            FloridData.Section ss = data.sections.get(i);
            ss.sequence = i ;
        }
        String jsonData = JSON.toJSONString(data);
        params.put("article", jsonData);
        OkHttpClientManager.postAsyn(MyConstants.ARTICLE_SAVE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                try {
                    ArticleDetailResult result = JSON.parseObject(response, ArticleDetailResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        PublishSuccessEvent event = new PublishSuccessEvent();
                        event.publishSuccess = true;
                        EventBus.getDefault().post(event);

                        if(data.articleId==0){
                            Intent pickIntent = new Intent(ArticlePreviewActivity.this, FlowerNewActivity.class);
                            pickIntent.putExtra(MyConstants.EXTRA_USER_ID, CommonPreference.getUserId());
                            pickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(pickIntent);
                        }else{
                            Intent pickIntent = new Intent(ArticlePreviewActivity.this, ArticleDetailActivity.class);
                            pickIntent.putExtra(MyConstants.ARTICLE_ID, String.valueOf(data.articleId));
                            pickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(pickIntent);
                        }

                        ShareHelper helper = new ShareHelper(ArticlePreviewActivity.this,
                                CommonPreference.getUserInfo().getUserName() + "的花语:" + data.title,
                                TextUtils.isEmpty(result.obj.article.summary) ? "" : (result.obj.article.summary.length() > 20 ? result.obj.article.summary.substring(0, 20) : result.obj.article.summary), //内容
                                result.obj.article.titleMedia.url, //图片
                                MyConstants.SHARE_MOMENT + result.obj.article.articleId,
                                data.state);
                        helper.runShare();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(MyConstants.FINISH_ACTIVITY,true);
                        setResult(RESULT_OK,resultIntent);
                        finish();
                    }else{
                        CommonUtils.error(result,ArticlePreviewActivity.this,"");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
