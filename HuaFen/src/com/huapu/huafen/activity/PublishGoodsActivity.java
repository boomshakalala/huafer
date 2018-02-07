package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CampaignAdapter;
import com.huapu.huafen.beans.Campaign;
import com.huapu.huafen.itemdecoration.CampaignItemDecoration;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.RenderScriptBlur;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 发布选择页面
 * Created by mac on 17/7/22.
 */
public class PublishGoodsActivity extends BaseActivity {

    @BindView(R.id.ivBg)
    ImageView ivBg;
    @BindView(R.id.campaigns)
    RecyclerView campaigns;
    @BindView(R.id.tvPublishGoods)
    TextView tvPublishGoods;
    @BindView(R.id.flGoods)
    FrameLayout flGoods;
    @BindView(R.id.tvPublishOneYuan)
    TextView tvPublishOneYuan;
    @BindView(R.id.flOneYuan)
    FrameLayout flOneYuan;
    @BindView(R.id.tvPublishArticle)
    TextView tvPublishArticle;
    @BindView(R.id.flArticle)
    FrameLayout flArticle;
    @BindView(R.id.flDismiss)
    FrameLayout flDismiss;
    @BindView(R.id.ivDismiss)
    ImageView ivDismiss;
    @BindView(R.id.tvCampaignTip)
    TextView tvCampaignTip;
    private CampaignAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_goods);
        if (MyConstants.flurPublishGoods != null) {
            RenderScriptBlur renderScriptBlur = new RenderScriptBlur(this);
            Bitmap bmp = renderScriptBlur.blur(25, MyConstants.flurPublishGoods);
            ivBg.setImageBitmap(bmp);
        }
        int grantedOneYun = CommonPreference.getGrantedOneYun();
        LogUtil.e("Pid", Process.myPid());
        if (grantedOneYun == 1) {
            flOneYuan.setVisibility(View.VISIBLE);
        } else {
            flOneYuan.setVisibility(View.GONE);
        }

        flDismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        tvPublishArticle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { // 发布花语
                startActivity(new Intent(PublishGoodsActivity.this, PickArticleModeActivity.class));
                finish();
            }
        });

        tvPublishGoods.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { // 发布商品
                Intent intent = new Intent(PublishGoodsActivity.this, ReleaseActivity.class);
                intent.putExtra(MyConstants.EXTRA_ALBUM_FROM_MAIN, "1");
                intent.putExtra(MyConstants.DRAFT_TYPE, 1);
                startActivity(intent);
                finish();
            }
        });

        tvPublishOneYuan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishGoodsActivity.this, ReleaseActivity.class);
                intent.putExtra("visit_one_yuan", true);
                intent.putExtra(MyConstants.DRAFT_TYPE, 3);
                startActivity(intent);
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(campaigns.getContext(), LinearLayoutManager.VERTICAL, false);
        campaigns.setLayoutManager(linearLayoutManager);
        campaigns.addItemDecoration(new CampaignItemDecoration(15));
        ArrayList<Campaign> data = getCampaigns();
        adapter = new CampaignAdapter(campaigns.getContext());
        campaigns.setAdapter(adapter);
        adapter.setData(data);
        if (!ArrayUtil.isEmpty(data)) {
            tvCampaignTip.setVisibility(View.VISIBLE);
        } else {
            tvCampaignTip.setVisibility(View.GONE);
        }

    }

    private ArrayList<Campaign> getCampaigns() {
        ArrayList<Campaign> result = new ArrayList<>();
        ArrayList<Campaign> cacheCampaign = CommonPreference.getCampaigns();
        String grantedCampaigns = CommonPreference.getGrantedCampaigns();
        if (!ArrayUtil.isEmpty(cacheCampaign) && !TextUtils.isEmpty(grantedCampaigns)) {
            String[] grantedList = grantedCampaigns.split(",");
            for (Campaign campaign : cacheCampaign) {
                boolean isContain = containId(grantedList, campaign.getCid());
                long currentTime = System.currentTimeMillis();
                String joinPeriod = campaign.getJoinPeriod();
                if (!TextUtils.isEmpty(joinPeriod)) {
                    String[] arr = joinPeriod.split(",");
                    if (arr.length == 2) {
                        boolean flag = currentTime >= Long.valueOf(arr[0]) && currentTime < Long.valueOf(arr[1]);
                        if (isContain && flag) {
                            result.add(campaign);
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean containId(String[] grantedCampaignsArr, int cid) {
        for (String s : grantedCampaignsArr) {
            if (cid == Integer.valueOf(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);

    }
}
