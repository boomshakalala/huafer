package com.huapu.huafen.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ImageLoader;

/**
 * @author liang_xs
 */
public class TipActivity extends BaseActivity {
    private View layoutPop;
    private SimpleDraweeView ivTips;
    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);

        initView();
        if (getIntent().hasExtra(MyConstants.EXTRA_TARGET_TIP)) {
            target = getIntent().getStringExtra(MyConstants.EXTRA_TARGET_TIP);
        }
        if (!TextUtils.isEmpty(target)) {

            ImageLoader.loadImage(ivTips, target);

            ViewTreeObserver vto2 = ivTips.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ivTips.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    float width = ivTips.getWidth();
                    float height = width / 0.92f;
                    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                            (int) width, (int) height);
                    // 设置banner高度
                    ivTips.setLayoutParams(localLayoutParams);
                }
            });

        }
    }

    private void initView() {
        layoutPop = findViewById(R.id.layoutPop);
        ivTips = (SimpleDraweeView) findViewById(R.id.ivTips);
        layoutPop.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutPop:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0); // 去掉baseactivity中finish动画
    }
}
