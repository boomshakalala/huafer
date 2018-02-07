package com.huapu.huafen.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;

import butterknife.BindView;

/**
 * Created by admin on 2017/4/28.
 */

public class ArticlePublishFinishActivity extends BaseActivity {


    @BindView(R2.id.articleCover)
    SimpleDraweeView articleCover;
    @BindView(R2.id.tvFinish)
    TextView tvFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_publish_finish);
        String path = mIntent.getStringExtra(MyConstants.COVER_LOCAL_PATH);
        articleCover.setImageURI("file://"+path);

        tvFinish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
