package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.HPagerAdapter;
import com.huapu.huafen.fragment.FirstBuyFragment;
import com.huapu.huafen.looper.IndicatorView;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by lalo on 2016/11/24.
 * 蒙层引导页面，包括各种蒙层
 */
public class MontageActivity extends FragmentActivity {

    private String extra;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra(MyConstants.EXTRA_MONTAGE)) {
            extra = intent.getStringExtra(MyConstants.EXTRA_MONTAGE);
        }
        if ("first_search".equals(extra)) {
            initSearchMaskLayer();
        } else if ("first_mine".equals(extra)) {
            initHelpCenterMaskLayer();
        } else if ("first_flower".equals(extra)) {
            initFlowerCenterMaskLayer();
        } else if ("first_following".equals(extra)) {
            initFollowOfMeMaskLayer();
        } else if ("first_remarks".equals(extra)) {
            initRemarksMaskLayer();
        } else if ("first_buy".equals(extra)) {
            initFirstBuy();
        } else if ("first_wallet_share".endsWith(extra)) {
            initWalletShareMaskLayer();
        } else if ("first_release_postpaid".equals(extra) || "first_release_postpaid_one".equals(extra)) {
            initPostpaid();
        } else if ("first_mine_pic".equals(extra)) {
            initMinePic();
        } else if (MyConstants.MAIN_GUIDE_TIPS.equals(extra)) {
            initMainGuide();
        } else if (MyConstants.CLASS_GUIDE_TIPS.equals(extra)) {
            initClassGuide();
        } else if (MyConstants.MINE_GUIDE_TIPS.equals(extra)) {
            initVerifyGuide();
        }

    }

    private void initMinePic() {
        setContentView(R.layout.activity_mask_mine_pic);
        findViewById(R.id.walletShareContent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initMainGuide() {
        setContentView(R.layout.activity_mask_main);
        findViewById(R.id.homeTip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonPreference.setIntValue(MyConstants.MAIN_GUIDE_TIPS, 1);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initClassGuide() {
        setContentView(R.layout.activity_mask_classification);
        findViewById(R.id.classTip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonPreference.setIntValue(MyConstants.CLASS_GUIDE_TIPS, 1);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initVerifyGuide() {
        setContentView(R.layout.activity_mask_verify);
        findViewById(R.id.verifyTip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonPreference.setIntValue(MyConstants.MINE_GUIDE_TIPS, 1);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initWalletShareMaskLayer() {
        setContentView(R.layout.activity_mask_wallet_share);
        findViewById(R.id.walletShareContent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initSearchMaskLayer() {
        setContentView(R.layout.search_mask_layout);
        LinearLayout bg = (LinearLayout) findViewById(R.id.llContent);
        bg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initHelpCenterMaskLayer() {
        Intent intent = getIntent();
        int bottom = intent.getIntExtra("bottom", 0);
        boolean isOverScreen = intent.getBooleanExtra("overScreen", false);
        setContentView(R.layout.helper_mask_layout);
        LinearLayout llRoot = (LinearLayout) findViewById(R.id.llRoot);
        View rlHelper = findViewById(R.id.rlHelper);
        if (isOverScreen) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rlHelper.getLayoutParams();
            params.height = CommonUtils.getScreenHeight() - CommonUtils.dp2px(100);
            rlHelper.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rlHelper.getLayoutParams();
            params.height = bottom;
            rlHelper.setLayoutParams(params);
        }

        llRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initFlowerCenterMaskLayer() {
        Intent intent = getIntent();
        final int height = intent.getIntExtra("height", 0);
        setContentView(R.layout.flower_mask_layout);
        final View root = findViewById(R.id.flContent);
        View flMask = findViewById(R.id.flMask);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) flMask.getLayoutParams();
        params.height = height;
        flMask.setLayoutParams(params);
        root.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initFollowOfMeMaskLayer() {
        setContentView(R.layout.follow_of_me_mask_layout);
        final View llContainer = findViewById(R.id.llContainer);
        llContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void initRemarksMaskLayer() {
        Intent intent = getIntent();
        final int bottom = intent.getIntExtra("bottom", 0);
        final boolean isOverScreen = intent.getBooleanExtra("overScreen", false);
        final int height = intent.getIntExtra("height", 0);
        setContentView(R.layout.order_remarks_layout);
        View flContainer = findViewById(R.id.flContainer);
        final View llRemarks = findViewById(R.id.llRemarks);
        final View ivGirl = findViewById(R.id.ivGirl);
        ivGirl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int ivHeight = ivGirl.getHeight();
                if (isOverScreen) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) llRemarks.getLayoutParams();
                    params.topMargin = CommonUtils.getScreenHeight() - height;
                    llRemarks.setLayoutParams(params);
                } else {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) llRemarks.getLayoutParams();
                    params.topMargin = bottom - (ivHeight - CommonUtils.dp2px(30f));
                    llRemarks.setLayoutParams(params);
                }
            }
        });


        flContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initFirstBuy() {
        setContentView(R.layout.first_buy);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        int[] resArr = new int[]{R.drawable.first_buy1, R.drawable.first_buy2, R.drawable.first_buy3};
        final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        for (int i = 0; i < resArr.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("first_buy_src", resArr[i]);
            FirstBuyFragment fragment = FirstBuyFragment.newInstance(bundle);
            fragmentArrayList.add(fragment);

        }
        HPagerAdapter adapter = new HPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        viewPager.setAdapter(adapter);
        final IndicatorView indicatorView = (IndicatorView) findViewById(R.id.indicator);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp.bottomMargin = CommonUtils.dp2px(60.0f);
        indicatorView.setLayoutParams(lp);
        indicatorView.setCount(fragmentArrayList.size());
        indicatorView.setPosition(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                indicatorView.setPosition(position);
                currentIndex = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        View ivIKnow = findViewById(R.id.ivIKnow);
        currentIndex = 0;
        ivIKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < fragmentArrayList.size() - 1) {
                    currentIndex += 1;
                    viewPager.setCurrentItem(currentIndex);
                    indicatorView.setPosition(currentIndex);
                } else {
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
        View ivDelete = findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }

        });

    }

    private void initPostpaid() {
        setContentView(R.layout.release_postapaid);
        Intent intent = getIntent();
        int keyboardHeight = intent.getIntExtra("keyboardHeight", 0);
        View bottom = findViewById(R.id.bottom);
        RelativeLayout.LayoutParams bottomLayoutParams = (RelativeLayout.LayoutParams) bottom.getLayoutParams();
        bottomLayoutParams.height = keyboardHeight;
        View rlPostagePaid = findViewById(R.id.rlPostagePaid);
        RelativeLayout.LayoutParams postpaidParams = (RelativeLayout.LayoutParams) rlPostagePaid.getLayoutParams();
        postpaidParams.width = CommonUtils.getScreenWidth() / 2;
        View rlRoot = findViewById(R.id.rlRoot);
        rlRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }
}
