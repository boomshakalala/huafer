package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ClassificationDetailActivity;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.NewStarListActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.ClassificationResult;
import com.huapu.huafen.beans.HotGoodsBean;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.beans.VIPRegionBean;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;

/**
 * Created by qwe on 2017/5/17.
 */

public class RegionCommonHeaderVIP extends LinearLayout implements View.OnClickListener {
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

    @BindView(R.id.headerTwoText)
    TextView headerTwoText;
    @BindView(R.id.activeUserImage1)
    SimpleDraweeView activeUserImage1;
    @BindView(R.id.activeUserName1)
    TextView activeUserName1;
    @BindView(R.id.fansNumber1)
    TextView fansNumber1;
    @BindView(R.id.onLineState1)
    TextView onLineState1;
    @BindView(R.id.followImage1)
    FollowImageView followImage1;
    @BindView(R.id.activeUserImage2)
    SimpleDraweeView activeUserImage2;
    @BindView(R.id.activeUserName2)
    TextView activeUserName2;
    @BindView(R.id.fansNumber2)
    TextView fansNumber2;
    @BindView(R.id.onLineState2)
    TextView onLineState2;
    @BindView(R.id.followImage2)
    FollowImageView followImage2;
    @BindView(R.id.activeUserImage3)
    SimpleDraweeView activeUserImage3;
    @BindView(R.id.activeUserName3)
    TextView activeUserName3;
    @BindView(R.id.fansNumber3)
    TextView fansNumber3;
    @BindView(R.id.onLineState3)
    TextView onLineState3;
    @BindView(R.id.followImage3)
    FollowImageView followImage3;
    @BindView(R.id.activeUserImage4)
    SimpleDraweeView activeUserImage4;
    @BindView(R.id.activeUserName4)
    TextView activeUserName4;
    @BindView(R.id.fansNumber4)
    TextView fansNumber4;
    @BindView(R.id.onLineState4)
    TextView onLineState4;
    @BindView(R.id.followImage4)
    FollowImageView followImage4;
    @BindView(R.id.activeUserImage5)
    SimpleDraweeView activeUserImage5;
    @BindView(R.id.activeUserName5)
    TextView activeUserName5;
    @BindView(R.id.fansNumber5)
    TextView fansNumber5;
    @BindView(R.id.onLineState5)
    TextView onLineState5;
    @BindView(R.id.followImage5)
    FollowImageView followImage5;
    @BindView(R.id.activeUserImage6)
    SimpleDraweeView activeUserImage6;
    @BindView(R.id.activeUserName6)
    TextView activeUserName6;
    @BindView(R.id.fansNumber6)
    TextView fansNumber6;
    @BindView(R.id.onLineState6)
    TextView onLineState6;
    @BindView(R.id.followImage6)
    FollowImageView followImage6;

    @BindView(R.id.seeAll)
    TextView seeAll;

    @BindView(R.id.right_icon)
    ImageView right_icon;


    private List<VIPRegionBean.HotCatsBean> hotCatsBean;

    private int screenWidth;


    private List<HotGoodsBean> goodsBeanList;

    private BannerData newGoodsBanner;

    private ArrayList<CampaignBanner> publicBanners;

    private List<VIPRegionBean.ActiveUsersBean> activeUsersBeanList;

    private Context context;

    private int type;

    public RegionCommonHeaderVIP(Context context, int type) {
        super(context);
        this.context = context;
        this.type = type;
        initView();
    }

    public RegionCommonHeaderVIP(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RegionCommonHeaderVIP(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.header_region_common_vip, this, true);
        ButterKnife.bind(this, view);
        screenWidth = CommonUtils.getScreenWidth();

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
//                int width = CommonUtils.getScreenWidth();
//                int height = width * 24 / 75;
//                LayoutParams localLayoutParams = new LayoutParams(
//                        width, height);
//                // 设置banner高度
//                bannerView.setLayoutParams(localLayoutParams);
                bannerView.setBanners(publicBanners);
                CommonUtils.setAutoLoop(newGoodsBanner, bannerView);
            }
        }
    }

    public void setData(List<VIPRegionBean.HotCatsBean> hotCatsBean, List<HotGoodsBean> hotGoodsBeanList, List<VIPRegionBean.ActiveUsersBean> activeUsersBeanList) {
        this.hotCatsBean = hotCatsBean;
        this.goodsBeanList = hotGoodsBeanList;
        this.activeUsersBeanList = activeUsersBeanList;

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


    private void initVIPUser(final List<VIPRegionBean.ActiveUsersBean> activeUsersBeanList) {

        seeAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewStarListActivity.class);
                intent.putExtra("VIP", "VIP");
                context.startActivity(intent);
            }
        });

        right_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewStarListActivity.class);
                intent.putExtra("VIP", "VIP");
                context.startActivity(intent);
            }
        });
        activeUserImage1.setImageURI(activeUsersBeanList.get(0).user.getAvatarUrl());
        activeUserImage2.setImageURI(activeUsersBeanList.get(1).user.getAvatarUrl());
        activeUserImage3.setImageURI(activeUsersBeanList.get(2).user.getAvatarUrl());
        activeUserImage4.setImageURI(activeUsersBeanList.get(3).user.getAvatarUrl());
        activeUserImage5.setImageURI(activeUsersBeanList.get(4).user.getAvatarUrl());
        activeUserImage6.setImageURI(activeUsersBeanList.get(5).user.getAvatarUrl());

        activeUserName1.setText(activeUsersBeanList.get(0).user.getUserName());
        activeUserName2.setText(activeUsersBeanList.get(1).user.getUserName());
        activeUserName3.setText(activeUsersBeanList.get(2).user.getUserName());
        activeUserName4.setText(activeUsersBeanList.get(3).user.getUserName());
        activeUserName5.setText(activeUsersBeanList.get(4).user.getUserName());
        activeUserName6.setText(activeUsersBeanList.get(5).user.getUserName());


        followImage1.setPinkData(activeUsersBeanList.get(0).user.getFellowship());
        followImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttentionState(followImage1, activeUsersBeanList.get(0).user.getFellowship(), activeUsersBeanList.get(0).user, followImage1);
            }
        });

        followImage2.setPinkData(activeUsersBeanList.get(1).user.getFellowship());
        followImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttentionState(followImage2, activeUsersBeanList.get(1).user.getFellowship(), activeUsersBeanList.get(1).user, followImage2);
            }
        });

        followImage3.setPinkData(activeUsersBeanList.get(2).user.getFellowship());
        followImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttentionState(followImage3, activeUsersBeanList.get(2).user.getFellowship(), activeUsersBeanList.get(2).user, followImage3);
            }
        });

        followImage4.setPinkData(activeUsersBeanList.get(3).user.getFellowship());
        followImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttentionState(followImage4, activeUsersBeanList.get(3).user.getFellowship(), activeUsersBeanList.get(3).user, followImage4);
            }
        });

        followImage5.setPinkData(activeUsersBeanList.get(4).user.getFellowship());
        followImage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttentionState(followImage5, activeUsersBeanList.get(4).user.getFellowship(), activeUsersBeanList.get(4).user, followImage5);
            }
        });

        followImage6.setPinkData(activeUsersBeanList.get(5).user.getFellowship());
        followImage6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttentionState(followImage6, activeUsersBeanList.get(5).user.getFellowship(), activeUsersBeanList.get(5).user, followImage6);
            }
        });

        fansNumber1.setText("商品：" + activeUsersBeanList.get(0).counts.selling);
        if ("当前在线".equals(activeUsersBeanList.get(0).user.getLastVisitText())) {
            onLineState1.setTextColor(Color.parseColor("#78D067"));
        } else {
            onLineState1.setTextColor(Color.parseColor("#8A000000"));
        }
        onLineState1.setText(activeUsersBeanList.get(0).user.getLastVisitText());

        fansNumber2.setText("商品：" + activeUsersBeanList.get(1).counts.selling);
        if ("当前在线".equals(activeUsersBeanList.get(1).user.getLastVisitText())) {
            onLineState2.setTextColor(Color.parseColor("#78D067"));
        } else {
            onLineState2.setTextColor(Color.parseColor("#8A000000"));
        }
        onLineState2.setText(activeUsersBeanList.get(1).user.getLastVisitText());

        fansNumber3.setText("商品：" + activeUsersBeanList.get(2).counts.selling);
        if ("当前在线".equals(activeUsersBeanList.get(2).user.getLastVisitText())) {
            onLineState3.setTextColor(Color.parseColor("#78D067"));
        } else {
            onLineState3.setTextColor(Color.parseColor("#8A000000"));
        }
        onLineState3.setText(activeUsersBeanList.get(2).user.getLastVisitText());

        fansNumber4.setText("商品：" + activeUsersBeanList.get(3).counts.selling);
        if ("当前在线".equals(activeUsersBeanList.get(3).user.getLastVisitText())) {
            onLineState4.setTextColor(Color.parseColor("#78D067"));
        } else {
            onLineState4.setTextColor(Color.parseColor("#8A000000"));
        }
        onLineState4.setText(activeUsersBeanList.get(3).user.getLastVisitText());

        fansNumber5.setText("商品：" + activeUsersBeanList.get(4).counts.selling);
        if ("当前在线".equals(activeUsersBeanList.get(4).user.getLastVisitText())) {
            onLineState5.setTextColor(Color.parseColor("#78D067"));
        } else {
            onLineState5.setTextColor(Color.parseColor("#8A000000"));
        }
        onLineState5.setText(activeUsersBeanList.get(4).user.getLastVisitText());

        fansNumber6.setText("商品：" + activeUsersBeanList.get(5).counts.selling);
        if ("当前在线".equals(activeUsersBeanList.get(5).user.getLastVisitText())) {
            onLineState6.setTextColor(Color.parseColor("#78D067"));
        } else {
            onLineState6.setTextColor(Color.parseColor("#8A000000"));
        }

        onLineState6.setText(activeUsersBeanList.get(5).user.getLastVisitText());
    }

    private void doAttentionState(final FollowImageView followImageView, final int followShip, ArrayMap<String, String> attentionParams, final UserData userData, final View view) {

        view.setEnabled(false);

        OkHttpClientManager.postAsyn(MyConstants.CONCERNEDUSER, attentionParams, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                view.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                view.setEnabled(true);
                Logger.e("get response:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        //1 无关系 2 已关注 3 被关注 4 互相关注
                        if (followShip == 1) {
                            followImageView.setPinkData(2);
                            userData.setFellowship(2);
                        } else if (followShip == 2) {
                            followImageView.setPinkData(1);
                            userData.setFellowship(1);
                        } else if (followShip == 3) {
                            followImageView.setPinkData(4);
                            userData.setFellowship(4);
                        } else if (followShip == 4) {
                            followImageView.setPinkData(3);
                            userData.setFellowship(3);
                        }
                    } else {
                        if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
                            CommonUtils.checkAccess((Activity) mContext);
                        }

                        ToastUtil.toast(context, baseResult.msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAttentionState(final FollowImageView followImageView, final int followShip,
                                   final UserData userData, final View view) {

        final ArrayMap<String, String> attentionParams = new ArrayMap<>();
        attentionParams.put("userId", String.valueOf(userData.getUserId()));

        int type = DialogManager.concernedUserDialog(context, followShip, new DialogCallback() {
            @Override
            public void Click() {
                // 取消关注
                attentionParams.put("type", "2");
                doAttentionState(followImageView, followShip, attentionParams, userData, view);
            }
        });

        if (type == 1) {
            attentionParams.put("type", "1");
            doAttentionState(followImageView, followShip, attentionParams, userData, view);
        }
    }

    private void initHotGoodsNew() {

        headerTwoText.setText("VIP活跃榜");
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

    @OnClick({R.id.activeUserImage1, R.id.activeUserName1, R.id.fansNumber1, R.id.onLineState1, R.id.followImage1, R.id.activeUserImage2, R.id.activeUserName2, R.id.fansNumber2, R.id.onLineState2, R.id.followImage2, R.id.activeUserImage3, R.id.activeUserName3, R.id.fansNumber3, R.id.onLineState3, R.id.followImage3, R.id.activeUserImage4, R.id.activeUserName4, R.id.fansNumber4, R.id.onLineState4, R.id.followImage4, R.id.activeUserImage5, R.id.activeUserName5, R.id.fansNumber5, R.id.onLineState5, R.id.followImage5, R.id.activeUserImage6, R.id.activeUserName6, R.id.fansNumber6, R.id.onLineState6, R.id.followImage6})
    public void onViewClicked(View view) {
        if (activeUsersBeanList == null || activeUsersBeanList.size() == 0)
            return;
        Intent intent;
        switch (view.getId()) {
            case R.id.activeUserImage1:
                intent = new Intent(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, activeUsersBeanList.get(0).user.getUserId());
                context.startActivity(intent);
                break;
            case R.id.activeUserName1:
                break;
            case R.id.fansNumber1:
                break;
            case R.id.onLineState1:
                break;
            case R.id.followImage1:
                break;
            case R.id.activeUserImage2:
                intent = new Intent(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, activeUsersBeanList.get(1).user.getUserId());
                context.startActivity(intent);
                break;
            case R.id.activeUserName2:
                break;
            case R.id.fansNumber2:
                break;
            case R.id.onLineState2:
                break;
            case R.id.followImage2:
                break;
            case R.id.activeUserImage3:
                intent = new Intent(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, activeUsersBeanList.get(2).user.getUserId());
                context.startActivity(intent);
                break;
            case R.id.activeUserName3:
                break;
            case R.id.fansNumber3:
                break;
            case R.id.onLineState3:
                break;
            case R.id.followImage3:
                break;
            case R.id.activeUserImage4:
                intent = new Intent(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, activeUsersBeanList.get(3).user.getUserId());
                context.startActivity(intent);
                break;
            case R.id.activeUserName4:
                break;
            case R.id.fansNumber4:
                break;
            case R.id.onLineState4:
                break;
            case R.id.followImage4:
                break;
            case R.id.activeUserImage5:
                intent = new Intent(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, activeUsersBeanList.get(4).user.getUserId());
                context.startActivity(intent);
                break;
            case R.id.activeUserName5:
                break;
            case R.id.fansNumber5:
                break;
            case R.id.onLineState5:
                break;
            case R.id.followImage5:
                break;
            case R.id.activeUserImage6:
                intent = new Intent(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, activeUsersBeanList.get(5).user.getUserId());
                context.startActivity(intent);
                break;
            case R.id.activeUserName6:
                break;
            case R.id.fansNumber6:
                break;
            case R.id.onLineState6:
                break;
            case R.id.followImage6:
                break;
        }
    }
}