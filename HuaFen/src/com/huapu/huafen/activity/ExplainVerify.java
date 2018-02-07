package com.huapu.huafen.activity;

import android.os.Bundle;

import com.huapu.huafen.R;

public class ExplainVerify extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_verify);
        getTitleBar().setTitle("实名认证说明");
    }
}
