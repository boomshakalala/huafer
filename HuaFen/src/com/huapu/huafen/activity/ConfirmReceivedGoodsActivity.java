package com.huapu.huafen.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.huapu.huafen.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qwe on 2017/6/22.
 */

public class ConfirmReceivedGoodsActivity extends BaseActivity {
    @BindView(R.id.blurImage)
    ImageView blurImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_received_goods);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle("确认收货");

    }


    @OnClick({R.id.goHome, R.id.goComment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.goHome:
                break;
            case R.id.goComment:
                break;
        }
    }
}
