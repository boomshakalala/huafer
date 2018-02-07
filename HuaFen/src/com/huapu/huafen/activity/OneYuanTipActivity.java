package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;

import butterknife.BindView;

/**
 * Created by admin on 2017/5/18.
 */

public class OneYuanTipActivity extends BaseActivity {


    @BindView(R2.id.tvNo) TextView tvNo;
    @BindView(R2.id.tvYes) TextView tvYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_yuan_layout);
        tvNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        tvYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("extra_visit",true);
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
