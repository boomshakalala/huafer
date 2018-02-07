package com.huapu.huafen.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ClassificationDetailActivity;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.ClassificationResult;
import com.huapu.huafen.beans.HotGoodsBean;
import com.huapu.huafen.beans.VIPRegionBean;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qwe on 2017/5/17.
 */

public class RegionCommonHeader extends LinearLayout implements View.OnClickListener {
    @BindView(R.id.bannerView)
    ClassBannerView bannerView;
    @BindView(R.id.oneImage)
    SimpleDraweeView oneImage;
    @BindView(R.id.twoImage)
    SimpleDraweeView twoImage;
    @BindView(R.id.threeImage)
    SimpleDraweeView threeImage;
    @BindView(R.id.fourImage)
    SimpleDraweeView fourImage;
    @BindView(R.id.hotOneImage)
    SimpleDraweeView hotOneImage;
    @BindView(R.id.hotOneText)
    TextView hotOneText;
    @BindView(R.id.hotTwoImage)
    SimpleDraweeView hotTwoImage;
    @BindView(R.id.hotTwoText)
    TextView hotTwoText;
    @BindView(R.id.hotThreeImage)
    SimpleDraweeView hotThreeImage;
    @BindView(R.id.hotThreeText)
    TextView hotThreeText;
    @BindView(R.id.hotFourImage)
    SimpleDraweeView hotFourImage;
    @BindView(R.id.hotFourText)
    TextView hotFourText;
    @BindView(R.id.hotFiveImage)
    SimpleDraweeView hotFiveImage;
    @BindView(R.id.hotFiveText)
    TextView hotFiveText;
    @BindView(R.id.hotSixImage)
    SimpleDraweeView hotSixImage;
    @BindView(R.id.hotSixText)
    TextView hotSixText;
    @BindView(R.id.threeUserImage)
    SimpleDraweeView threeUserImage;
    @BindView(R.id.twoUserImage)
    SimpleDraweeView twoUserImage;
    @BindView(R.id.firstUserImage)
    SimpleDraweeView firstUserImage;
    @BindView(R.id.vipLayout)
    RelativeLayout vipLayout;
    @BindView(R.id.threeVIPImage)
    ImageView threeVIPImage;
    @BindView(R.id.twoVIPImage)
    ImageView twoVIPImage;
    @BindView(R.id.firstVIPImage)
    ImageView firstVIPImage;
    @BindView(R.id.sixSecondLayout)
    LinearLayout sixSecondLayout;
    @BindView(R.id.oneTitleName)
    TextView oneTitleName;
    @BindView(R.id.oneContentText)
    TextView oneContentText;
    @BindView(R.id.twoTitleName)
    TextView twoTitleName;
    @BindView(R.id.twoContentText)
    TextView twoContentText;
    @BindView(R.id.threeTitleName)
    TextView threeTitleName;
    @BindView(R.id.threeContentText)
    TextView threeContentText;
    @BindView(R.id.fourTitleName)
    TextView fourTitleName;
    @BindView(R.id.fourContentText)
    TextView fourContentText;
    @BindView(R.id.firstPicLayout)
    RelativeLayout firstPicLayout;
    @BindView(R.id.secondPicLayout)
    RelativeLayout secondPicLayout;
    @BindView(R.id.threePicLayout)
    RelativeLayout threePicLayout;
    @BindView(R.id.fourPicLayout)
    RelativeLayout fourPicLayout;
    private List<VIPRegionBean.HotCatsBean> hotCatsBean;

    private int screenWidth;


    private List<HotGoodsBean> goodsBeanList;

    private BannerData newGoodsBanner;

    private ArrayList<CampaignBanner> publicBanners;

    private Context context;

    private int type;

    public RegionCommonHeader(Context context, int type) {
        super(context);
        this.context = context;
        this.type = type;
        initView();
    }

    public RegionCommonHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RegionCommonHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.header_region_common, this, true);
        ButterKnife.bind(this, view);
        screenWidth = CommonUtils.getScreenWidth();

        if (type == 110) {
            vipLayout.setVisibility(VISIBLE);
        } else {
            vipLayout.setVisibility(GONE);
        }
        initBanner();
    }

    private void initBanner() {

        if (type == 110) {
            newGoodsBanner = CommonPreference.getVipGoodsBanner();
        } else {
            newGoodsBanner = CommonPreference.getNewGoodsBanner();
        }
        if (newGoodsBanner != null) {
            publicBanners = newGoodsBanner.getBanners();
            if (!ArrayUtil.isEmpty(publicBanners)) {
                int width = CommonUtils.getScreenWidth();
                int height = width * 24 / 75;
                LayoutParams localLayoutParams = new LayoutParams(
                        width, height);
                // 设置banner高度
                bannerView.setLayoutParams(localLayoutParams);
                bannerView.setBanners(publicBanners);
                CommonUtils.setAutoLoop(newGoodsBanner, bannerView);
            }
        }
    }

    public void setData(List<VIPRegionBean.HotCatsBean> hotCatsBean, List<HotGoodsBean> hotGoodsBeanList, List<VIPRegionBean.ActiveUsersBean> activeUsersBeanList) {
        this.hotCatsBean = hotCatsBean;
        this.goodsBeanList = hotGoodsBeanList;

        if (null != hotCatsBean) {
            oneImage.setImageURI(hotCatsBean.get(0).icon);
            twoImage.setImageURI(hotCatsBean.get(1).icon);
            threeImage.setImageURI(hotCatsBean.get(2).icon);
            fourImage.setImageURI(hotCatsBean.get(3).icon);

            oneTitleName.setText(hotCatsBean.get(0).name);
            oneContentText.setText(hotCatsBean.get(0).description);

            twoTitleName.setText(hotCatsBean.get(1).name);
            twoContentText.setText(hotCatsBean.get(1).description);

            threeTitleName.setText(hotCatsBean.get(2).name);
            threeContentText.setText(hotCatsBean.get(2).description);

            fourTitleName.setText(hotCatsBean.get(3).name);
            fourContentText.setText(hotCatsBean.get(3).description);

            firstPicLayout.setOnClickListener(this);
            secondPicLayout.setOnClickListener(this);
            threePicLayout.setOnClickListener(this);
            fourPicLayout.setOnClickListener(this);
        }


        initHotGoodsNew();

        if (null != activeUsersBeanList && activeUsersBeanList.size() > 0) {
            initVIPUser(activeUsersBeanList);
        }

    }


    private void initVIPUser(List<VIPRegionBean.ActiveUsersBean> activeUsersBeanList) {

        if (null != activeUsersBeanList.get(0)) {
            firstUserImage.setImageURI(activeUsersBeanList.get(0).user.getAvatarUrl());
            firstVIPImage.setVisibility(VISIBLE);
        } else {
            firstVIPImage.setVisibility(GONE);
            firstUserImage.setVisibility(GONE);
        }

        if (null != activeUsersBeanList.get(1)) {
            twoUserImage.setImageURI(activeUsersBeanList.get(1).user.getAvatarUrl());
            twoVIPImage.setVisibility(VISIBLE);
        } else {
            twoUserImage.setVisibility(GONE);
            twoVIPImage.setVisibility(GONE);
        }

        if (null != activeUsersBeanList.get(2)) {
            threeUserImage.setImageURI(activeUsersBeanList.get(2).user.getAvatarUrl());
            threeVIPImage.setVisibility(VISIBLE);
        } else {
            threeUserImage.setVisibility(GONE);
            threeVIPImage.setVisibility(GONE);
        }

        vipLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.VIP_BRAND);
                context.startActivity(intent);
            }
        });
    }

    private void initHotGoodsNew() {
        ViewGroup.LayoutParams layoutParams = hotOneImage.getLayoutParams();
        int tenDp = context.getResources().getDimensionPixelSize(R.dimen.flower_space_ten);
        layoutParams.width = (screenWidth - 4 * tenDp) / 3;
        layoutParams.height = (screenWidth - 4 * tenDp) / 3;

        if (null != goodsBeanList.get(0) && !TextUtils.isEmpty(goodsBeanList.get(0).item.goodsImgs.get(0))) {
            hotOneImage.setVisibility(VISIBLE);
            hotOneText.setVisibility(VISIBLE);
            hotOneImage.setLayoutParams(layoutParams);
            hotOneImage.setImageURI(goodsBeanList.get(0).item.goodsImgs.get(0));
            hotOneText.setText("¥" + String.valueOf(goodsBeanList.get(0).item.price));
            hotOneImage.setOnClickListener(this);
        } else {
            hotOneImage.setVisibility(GONE);
            hotOneText.setVisibility(GONE);
        }


        if (null != goodsBeanList.get(1) && !TextUtils.isEmpty(goodsBeanList.get(1).item.goodsImgs.get(0))) {
            hotTwoImage.setVisibility(VISIBLE);
            hotTwoText.setVisibility(VISIBLE);
            hotTwoImage.setLayoutParams(layoutParams);
            hotTwoImage.setImageURI(goodsBeanList.get(1).item.goodsImgs.get(0));
            hotTwoText.setText("¥" + String.valueOf(goodsBeanList.get(1).item.price));
            hotTwoImage.setOnClickListener(this);
        } else {
            hotTwoImage.setVisibility(GONE);
            hotTwoText.setVisibility(GONE);
        }


        if (null != goodsBeanList.get(2) && !TextUtils.isEmpty(goodsBeanList.get(2).item.goodsImgs.get(0))) {
            hotThreeImage.setVisibility(VISIBLE);
            hotThreeText.setVisibility(VISIBLE);
            hotThreeImage.setLayoutParams(layoutParams);
            hotThreeImage.setImageURI(goodsBeanList.get(2).item.goodsImgs.get(0));
            hotThreeText.setText("¥" + String.valueOf(goodsBeanList.get(2).item.price));
            hotThreeImage.setOnClickListener(this);
        } else {
            hotThreeImage.setVisibility(GONE);
            hotThreeText.setVisibility(GONE);
        }

        if (goodsBeanList.size() > 3 && !TextUtils.isEmpty(goodsBeanList.get(3).item.goodsImgs.get(0))) {
            hotFourImage.setVisibility(VISIBLE);
            hotFourText.setVisibility(VISIBLE);
            hotFourImage.setLayoutParams(layoutParams);
            hotFourImage.setImageURI(goodsBeanList.get(3).item.goodsImgs.get(0));
            hotFourText.setText("¥" + String.valueOf(goodsBeanList.get(3).item.price));
            hotFourImage.setOnClickListener(this);
            sixSecondLayout.setVisibility(VISIBLE);
        } else {
            hotFourImage.setVisibility(GONE);
            hotFourText.setVisibility(GONE);
            sixSecondLayout.setVisibility(GONE);
            return;
        }

        if (null != goodsBeanList.get(4) && !TextUtils.isEmpty(goodsBeanList.get(4).item.goodsImgs.get(0))) {
            hotFiveImage.setVisibility(VISIBLE);
            hotFiveText.setVisibility(VISIBLE);
            hotFiveImage.setLayoutParams(layoutParams);
            hotFiveImage.setImageURI(goodsBeanList.get(4).item.goodsImgs.get(0));
            hotFiveText.setText("¥" + String.valueOf(goodsBeanList.get(4).item.price));
            hotFiveImage.setOnClickListener(this);
        } else {
            hotFiveImage.setVisibility(GONE);
            hotFiveText.setVisibility(GONE);
        }

        if (null != goodsBeanList.get(5) && !TextUtils.isEmpty(goodsBeanList.get(5).item.goodsImgs.get(0))) {
            hotSixImage.setVisibility(VISIBLE);
            hotSixText.setVisibility(VISIBLE);
            hotSixImage.setLayoutParams(layoutParams);
            hotSixImage.setImageURI(goodsBeanList.get(5).item.goodsImgs.get(0));
            hotSixText.setText("¥" + String.valueOf(goodsBeanList.get(5).item.price));
            hotSixImage.setOnClickListener(this);
        } else {
            hotSixImage.setVisibility(GONE);
            hotSixText.setVisibility(GONE);
        }
    }


    @Override
    public void onClick(View v) {
        String goodsId = "";
        ClassificationResult.Opt opt = new ClassificationResult.Opt();
        opt.type = "grid";
        switch (v.getId()) {
            case R.id.hotOneImage:
                goodsId = String.valueOf(goodsBeanList.get(0).item.goodsId);
                break;
            case R.id.hotTwoImage:
                goodsId = String.valueOf(goodsBeanList.get(1).item.goodsId);
                break;
            case R.id.hotThreeImage:
                goodsId = String.valueOf(goodsBeanList.get(2).item.goodsId);
                break;
            case R.id.hotFourImage:
                goodsId = String.valueOf(goodsBeanList.get(3).item.goodsId);
                break;
            case R.id.hotFiveImage:
                goodsId = String.valueOf(goodsBeanList.get(4).item.goodsId);
                break;
            case R.id.hotSixImage:
                goodsId = String.valueOf(goodsBeanList.get(5).item.goodsId);
                break;
            case R.id.firstPicLayout:
                opt.title = hotCatsBean.get(0).name;
                opt.note = String.valueOf(hotCatsBean.get(0).cid / 100);
                opt.target = String.valueOf(hotCatsBean.get(0).cid);
                break;
            case R.id.secondPicLayout:
                opt.title = hotCatsBean.get(1).name;
                opt.note = String.valueOf(hotCatsBean.get(1).cid / 100);
                opt.target = String.valueOf(hotCatsBean.get(1).cid);
                break;
            case R.id.threePicLayout:
                opt.title = hotCatsBean.get(2).name;
                opt.note = String.valueOf(hotCatsBean.get(2).cid / 100);
                opt.target = String.valueOf(hotCatsBean.get(2).cid);
                break;
            case R.id.fourPicLayout:
                opt.title = hotCatsBean.get(3).name;
                opt.note = String.valueOf(hotCatsBean.get(3).cid / 100);
                opt.target = String.valueOf(hotCatsBean.get(3).cid);
                break;
            default:
                break;
        }

        if (!TextUtils.isEmpty(opt.title)) {
            Intent intent = new Intent(context, ClassificationDetailActivity.class);
            /**
             * vip页面
             */
            if (type == 110) {
                opt.action = ActionConstants.OPEN_CATS_VIP_ZONE;
            } else {
                opt.action = ActionConstants.OPEN_CATS_NEW_ZONE;
            }
            intent.putExtra(MyConstants.EXTRA_OPTION, opt);
            intent.putExtra("FROM_PAGE", "NEW_VIP");
            intent.putExtra("key", opt.note);
            context.startActivity(intent);
            return;
        }

        if (!TextUtils.isEmpty(goodsId)) {
            Intent intent = new Intent(context, GoodsDetailsActivity.class);
            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goodsId));
            context.startActivity(intent);
        }


    }
}