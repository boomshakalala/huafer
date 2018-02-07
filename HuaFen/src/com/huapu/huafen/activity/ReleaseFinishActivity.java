package com.huapu.huafen.activity;

import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Audit;
import com.huapu.huafen.beans.Campaign;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ShareUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.views.CommonTitleView;

import java.util.ArrayList;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 发布成功页
 *
 * @author liang_xs
 */
public class ReleaseFinishActivity extends BaseActivity {
    private long goodsId;
    private String imgPath;
    private String price;
    private String pastPrice;
    private String goodsName;
    private SimpleDraweeView ivGoodsPic;
    private SimpleDraweeView ivHeader;
    private TextView tvGoodsName, tvGoodsPrice, tvGoodsPastPrice;
    private TextView tvBtnGoods, tvBtnRelease;
    private String editFrom = "";
    private View layoutShare;
    private int auditStatus;
    private CommonTitleView ctvName;
    private View layoutReleaseFinish;
    private ImageView ivShareWX, ivShareFriend, ivShareSina, ivShareQQ, ivShareCopy;
    private int campaignId;
    private boolean oneYuanFlag;

    private String goods_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_finish);
        initView();
        if (getIntent().hasExtra(MyConstants.EXTRA_AUDIT_STATUS)) {
            auditStatus = getIntent().getIntExtra(MyConstants.EXTRA_AUDIT_STATUS, auditStatus);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_GOODS_DETAIL_ID)) {
            goodsId = getIntent().getLongExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_IMG)) {
            imgPath = getIntent().getStringExtra(MyConstants.EXTRA_SELECT_IMG);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_PRICE)) {
            price = getIntent().getStringExtra(MyConstants.EXTRA_SELECT_PRICE);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_PAST_PRICE)) {
            pastPrice = getIntent().getStringExtra(MyConstants.EXTRA_SELECT_PAST_PRICE);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_GOODS_NAME)) {
            goodsName = getIntent().getStringExtra(MyConstants.EXTRA_GOODS_NAME);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_GOODS_EDIT_FROM)) {
            editFrom = getIntent().getStringExtra(MyConstants.EXTRA_GOODS_EDIT_FROM);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_CAMPAIGN_ID)) {
            campaignId = getIntent().getIntExtra(MyConstants.EXTRA_CAMPAIGN_ID, 0);
        }

        if (getIntent().hasExtra(MyConstants.EXTRA_GOODS_CONTENT)) {
            goods_content = getIntent().getStringExtra(MyConstants.EXTRA_GOODS_CONTENT);
        }

        oneYuanFlag = mIntent.getBooleanExtra("visit_one_yuan", false);

        Audit audit = CommonPreference.getAudit();
        if (audit != null) {
            int shareable = audit.getShareable();
            if (shareable == 1) {
                layoutShare.setVisibility(View.VISIBLE);
            } else {
                if (auditStatus == 3 || auditStatus == 4 || auditStatus == 5) {
                    layoutShare.setVisibility(View.GONE);
                }
            }
        }

        ImageLoader.loadImage(ivGoodsPic, imgPath);

        String url = CommonPreference.getStringValue(CommonPreference.USER_ICON, "");
        ImageLoader.resizeSmall(ivHeader, url, 1);

        UserInfo userInfo = CommonPreference.getUserInfo();
        ctvName.setData(userInfo);
        tvGoodsName.setText(goodsName);
        tvGoodsPrice.setText(price);
        try {
            CommonUtils.setPriceSizeData(tvGoodsPastPrice, "", Integer.valueOf(pastPrice));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        ivShareWX = (ImageView) findViewById(R.id.ivShareWX);
        ivShareFriend = (ImageView) findViewById(R.id.ivShareFriend);
        ivShareSina = (ImageView) findViewById(R.id.ivShareSina);
        ivShareQQ = (ImageView) findViewById(R.id.ivShareQQ);
        ivShareCopy = (ImageView) findViewById(R.id.ivShareCopy);
        ivGoodsPic = (SimpleDraweeView) findViewById(R.id.ivGoodsPic);
        ivHeader = (SimpleDraweeView) findViewById(R.id.ivHeader);
        ctvName = (CommonTitleView) findViewById(R.id.ctvName);
        tvGoodsName = (TextView) findViewById(R.id.tvGoodsName);
        tvGoodsPrice = (TextView) findViewById(R.id.tvGoodsPrice);
        tvGoodsPastPrice = (TextView) findViewById(R.id.tvGoodsPastPrice);
        tvGoodsPastPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvBtnRelease = (TextView) findViewById(R.id.tvBtnRelease);
        tvBtnGoods = (TextView) findViewById(R.id.tvBtnGoods);
        layoutShare = findViewById(R.id.layoutShare);
        layoutReleaseFinish = findViewById(R.id.layoutReleaseFinish);
        ViewTreeObserver vto2 = ivGoodsPic.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivGoodsPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                LayoutParams params = ivGoodsPic.getLayoutParams();
                params.height = ivGoodsPic.getWidth();
                ivGoodsPic.setLayoutParams(params);
            }
        });

        tvBtnRelease.setOnClickListener(this);
        tvBtnGoods.setOnClickListener(this);
        layoutReleaseFinish.setOnClickListener(this);
        ivShareWX.setOnClickListener(this);
        ivShareFriend.setOnClickListener(this);
        ivShareSina.setOnClickListener(this);
        ivShareQQ.setOnClickListener(this);
        ivShareCopy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String share_content;
        Intent intent;
        if (TextUtils.isEmpty(goods_content)) {
            share_content = CommonPreference.getUserInfo().getUserName() + "在花粉儿分享了最新闲置商品，不快点就没有喽～" + ShareUtils.DOWN_LOAD;
        } else {
            share_content = goods_content + ShareUtils.DOWN_LOAD;
        }
        switch (v.getId()) {
            case R.id.layoutReleaseFinish:
                onBackPressed();
                break;
            case R.id.ivShareWX:
                ShareUtils.share(this, Wechat.NAME, goodsName, share_content, imgPath, MyConstants.SHARE_GOODS_DETAIL + goodsId);
                break;
            case R.id.ivShareFriend:
                ShareUtils.share(this, WechatMoments.NAME, goodsName, share_content, imgPath, MyConstants.SHARE_GOODS_DETAIL + goodsId);
                break;
            case R.id.ivShareSina:
                ShareUtils.share(this, SinaWeibo.NAME, goodsName, share_content, imgPath, MyConstants.SHARE_GOODS_DETAIL + goodsId);
                break;
            case R.id.ivShareQQ:
                ShareUtils.share(this, QQ.NAME, goodsName, share_content, imgPath, MyConstants.SHARE_GOODS_DETAIL + goodsId);
                break;
            case R.id.ivShareCopy:
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText(MyConstants.SHARE_GOODS_DETAIL + goodsId);
                ToastUtil.toast(this, "复制成功");
                break;
            case R.id.tvBtnRelease:
                intent = new Intent(this, ReleaseActivity.class);
                if (campaignId != 0) {
                    ArrayList<Campaign> campaigns = CommonPreference.getCampaigns();
                    if (!ArrayUtil.isEmpty(campaigns)) {
                        for (Campaign campaign : campaigns) {
                            if (campaign.getCid() == campaignId) {
                                intent.putExtra(MyConstants.EXTRA_CAMPAIGN_BEAN, campaign);
                                break;
                            }
                        }
                    }
                }
                intent.putExtra("visit_one_yuan", oneYuanFlag);
                startActivity(intent);
                finish();
                break;

            case R.id.tvBtnGoods:
                if (!TextUtils.isEmpty(editFrom) && editFrom.equals(MyConstants.GOODS_DETAILS_EDIT)) {
                    finish();
                } else {
                    intent = new Intent(this, GoodsDetailsActivity.class);
                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goodsId));
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0); // 去掉baseactivity中动画
    }
}
