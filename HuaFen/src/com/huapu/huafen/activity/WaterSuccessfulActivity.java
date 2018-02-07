package com.huapu.huafen.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import com.huapu.huafen.R;

/**
 * Created by admin on 2017/2/6.
 */

public class WaterSuccessfulActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_pop);
        ImageView ivContainer = (ImageView)findViewById(R.id.ivContainer);

        View root = findViewById(R.id.flContent);
        root.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        ivContainer.setBackgroundResource(R.drawable.water_successful_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivContainer.getBackground();
        animationDrawable.start();

        mHandler.postDelayed(mRunnable,150*10);
    }

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            finish();
            overridePendingTransition(0,0);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
    }
}
