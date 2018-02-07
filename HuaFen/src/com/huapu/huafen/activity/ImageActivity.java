package com.huapu.huafen.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;

import butterknife.BindView;

/**
 * Created by admin on 2017/4/22.
 */

public class ImageActivity extends BaseActivity {

    @BindView(R2.id.rlContainer)
    RelativeLayout rlContainer;
    @BindView(R2.id.picture)
    SimpleDraweeView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        String url = mIntent.getStringExtra("imageUrl");
        picture.setImageURI(url);
        rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

    }
}
