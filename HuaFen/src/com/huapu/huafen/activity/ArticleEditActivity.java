package com.huapu.huafen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.huapu.huafen.R;

/**
 * Created by admin on 2017/4/26.
 */

public class ArticleEditActivity  extends BaseActivity {


    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_edit);
        imageUri = mIntent.getData();
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("编辑图片").setRightText("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(imageUri);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
