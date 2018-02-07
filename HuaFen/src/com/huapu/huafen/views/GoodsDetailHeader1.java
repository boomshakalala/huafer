package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
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
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.ClassificationDetailActivity;
import com.huapu.huafen.activity.GalleryActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.activity.WebViewActivity2;
import com.huapu.huafen.adapter.HPCommentAdapter;
import com.huapu.huafen.beans.Age;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Campaign;
import com.huapu.huafen.beans.ClassificationResult;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.GoodsInfoBean;
import com.huapu.huafen.beans.HPCommentsResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ProductPreSellDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.looper.LooperPager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/3/18.
 */

public class GoodsDetailHeader1 extends LinearLayout implements AudioManager.OnAudioFocusChangeListener {

    private final static String TAG = GoodsDetailHeader1.class.getSimpleName();
    /**
     * 轮播图区域
     **/
    @BindView(R2.id.rlLooperPager)
    RelativeLayout rlLooperPager;
    @BindView(R2.id.looperPager)
    LooperPager looperPager;//带视频的轮播图
    @BindView(R2.id.layoutPlay)
    RelativeLayout layoutPlay;//语音播放按钮
    @BindView(R2.id.ivPlayState)
    ImageView ivPlayState;//语音播放状态
    @BindView(R2.id.tvSoundTime)
    TextView tvSoundTime;//语音时长
    @BindView(R2.id.tvPreSellTime)
    TextView tvPreSellTime;//预售时间
    @BindView(R2.id.tvGoodsNameAndBrand)
    TextView tvGoodsNameAndBrand;//名字和品牌
    @BindView(R2.id.tvViews)
    TextView tvViews;//访问次数
    @BindView(R.id.llGoodsPriceLayout)
    LinearLayout llGoodsPriceLayout;
    @BindView(R2.id.tvPrice)
    TextView tvPrice;//现价
    @BindView(R2.id.tvPastPrice)
    TextView tvPastPrice;//原价
    @BindView(R2.id.tvFreeDelivery)
    TextView tvFreeDelivery;//邮费绿色
    @BindView(R.id.llAuctionPriceLayout)
    LinearLayout llAuctionPriceLayout;
    @BindView(R.id.tvAuctionDes)
    TextView tvAuctionDes;
    @BindView(R.id.tvAuctionPrice)
    TextView tvAuctionPrice;
    @BindView(R2.id.tvContent)
    TextView tvContent;//留言内容
    @BindView(R2.id.tvLocation)
    TextView tvLocation;//定位信息
    @BindView(R2.id.goodsLayout)
    HGoodsLayout goodsLayout;//情报内容
    @BindView(R2.id.bannerView)
    ClassBannerView bannerView;
    @BindView(R2.id.llGuarantee)
    LinearLayout llGuarantee;//担保
    @BindView(R.id.tvGuarantee)
    TextView tvGuarantee;
    @BindView(R2.id.llFromTo)
    LinearLayout llFromTo;//拍卖所得
    @BindView(R.id.tvFromTo)
    TextView tvFromTo;
    /**
     * 店主信息
     **/
    @BindView(R2.id.avatar)
    SimpleDraweeView avatar;//头像
    @BindView(R2.id.ctvName)
    CommonTitleView ctvName;//名字和图标
    @BindView(R2.id.tvAuth)
    TextView tvAuth;//花粉儿认证内容
    @BindView(R2.id.tvFavorableRateAndOnSell)
    TextView tvFavorableRateAndOnSell;//好评率和在售数目
    @BindView(R2.id.ivFollow)
    FollowImageView ivFollow;//好评率和在售数目
    @BindView(R.id.llPreSellTime)
    LinearLayout llPreSellTime;
    @BindView(R.id.llAuctionTime)
    LinearLayout llAuctionTime;
    @BindView(R.id.tvAuctionTime)
    TextView tvAuctionTime;
    @BindView(R.id.bidListLayout)
    BidListLayout bidListLayout;
    @BindView(R.id.commentContainer)
    CommentContainer commentContainer;
    private MediaPlayer media;
    private boolean downloadState = false; // 下载状态
    private boolean playState = false; // 播放状态
    private AudioManager mAudioManager;
    private String recTraceId;
    private int recIndex;
    private String searchQuery;
    private Handler handler = new Handler();
    private BidStartTimer bidStartTimer;
    private BidEndTimer bidEndTimer;
    private OnRefreshListener mOnRefreshListener;

    public GoodsDetailHeader1(Context context) {
        this(context, null);
    }

    public GoodsDetailHeader1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.goods_detail_header1, this, true);
        ButterKnife.bind(this);
    }

    public void setRecTraceId(String recTraceId) {
        this.recTraceId = recTraceId;
    }

    public void setRecIndex(int recIndex) {
        this.recIndex = recIndex;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setData(GoodsInfoBean bean, FragmentManager fragmentManager) {
        if (bean != null) {
            initPart1(bean, fragmentManager);
            initPart2(bean);
            initPart3(bean);
            initPart4(bean);
        }
    }

    private void initPart1(GoodsInfoBean bean, FragmentManager fragmentManager) {
        final GoodsInfo goodsInfo = bean.getGoodsInfo();
        if (goodsInfo != null) {
            /**滑动的轮播控件**/
            initLooperPager(fragmentManager, goodsInfo);

            /**语音时间**/
            initRecordSound(goodsInfo);

            /**预售时间\名称\品牌\价格\简介\定位等设置**/
            initGoodsInfo(bean);
        }
    }

    private void initPart2(final GoodsInfoBean bean) {

        final GoodsInfo goodsInfo = bean.getGoodsInfo();

        String secondClassName = goodsInfo.getScfName();
        String ageRange = "";
        List<Age> ages = goodsInfo.getAgeList();
        if (!ArrayUtil.isEmpty(ages)) {
            StringBuilder sb = new StringBuilder();
            for (Age a : ages) {
                if (a != null) {
                    sb.append(a.getAgeTitle()).append(",");
                }
            }
            if (!sb.toString().isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }
            ageRange = sb.toString();
        }
        String goodsState = goodsInfo.getIsNew() == 1 ? "全新" : "闲置";

        String discount;
        if (goodsInfo.getDiscount() <= 0 || goodsInfo.getDiscount() >= 10) {
            discount = "";
        } else {
            discount = goodsInfo.getDiscount() + "折";
        }

        String paymentMethod = "";
        ArrayList<Integer> goodsOperations = goodsInfo.getGoodsOperations();

        if (!ArrayUtil.isEmpty(goodsOperations)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer operation : goodsOperations) {
                if (operation == 1) {
                    paymentMethod += "";
                    stringBuilder.append("微信支付").append("/");
                } else if (operation == 2) {
                    stringBuilder.append("支付宝支付").append("/");
                }
            }
            if (!stringBuilder.toString().isEmpty()) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }

            paymentMethod = stringBuilder.toString();
        }

        String deliveryMethod = "快递";
        if (goodsInfo.getSupportFaceToFace() == 1) {
            deliveryMethod = deliveryMethod + "/当面交易";
        }

        String receipt = "";
        if (goodsInfo.getHasReceipt() == 1) {
            receipt = "含专柜小票";
        }

        String[] keys = new String[]{"商品分类", "适合年龄", "商品状态", "商品折扣", "支付方式", "配送方式", "有无票据"};
        String[] values = new String[]{secondClassName, ageRange, goodsState, discount, paymentMethod, deliveryMethod, receipt};

        TreeMap<String, String> map = new TreeMap<>();

        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }

        goodsLayout.setData(map);
        goodsLayout.setOnClassArowClickListener(new HGoodsLayout.OnClassArrowClickListener() {

            @Override
            public void onClick() {
                Intent intent = new Intent(getContext(), ClassificationDetailActivity.class);
                ClassificationResult.Opt opt = new ClassificationResult.Opt();
                opt.type = "grid";
                opt.title = bean.getGoodsInfo().getScfName();
                opt.note = String.valueOf(bean.getGoodsInfo().getCfId());
                opt.target = String.valueOf(bean.getGoodsInfo().getScfId());
                intent.putExtra(MyConstants.EXTRA_OPTION, opt);
                intent.putExtra("key", opt.note);
                getContext().startActivity(intent);
            }
        });

        ViewGroup.LayoutParams params = bannerView.getLayoutParams();
        params.height = CommonUtils.getScreenWidth() * 12 / 75;
        params.width = CommonUtils.getScreenWidth();
        bannerView.setLayoutParams(params);

        BannerData banner1 = bean.getBanner1();
        if (banner1 != null && !ArrayUtil.isEmpty(banner1.getBanners())) {
            bannerView.setVisibility(View.VISIBLE);
            bannerView.setBanners(banner1.getBanners());
            CommonUtils.setAutoLoop(banner1, bannerView);
        } else {
            bannerView.setVisibility(View.GONE);
        }
        //担保
        llGuarantee.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (goodsInfo.isAuction == 1) {
                    Intent intent = new Intent(getContext(), WebViewActivity2.class);
                    intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.BID_DESPOSIT_RULE);
                    getContext().startActivity(intent);
                    ((Activity) getContext()).overridePendingTransition(0, 0);
                } else {
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.GUARANTEE);
                    getContext().startActivity(intent);
                }
            }
        });
        if (goodsInfo.isAuction == 1) {
            tvGuarantee.setText("保证金规则");
        } else {
            tvGuarantee.setText("使用花粉儿担保交易，保障钱款安全");
        }
        if (bean.tips1 != null) {
            //拍卖
            llFromTo.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (bean.tips1.action.equals(ActionConstants.OPEN_WEB_VIEW)) {
                        Intent intent = new Intent(getContext(), WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, bean.tips1.target);
                        getContext().startActivity(intent);
                    }

                }
            });
            tvFromTo.setText(bean.tips1.title);
        } else {
            llFromTo.setVisibility(GONE);
        }
    }

    private void initPart3(final GoodsInfoBean bean) {
        final UserInfo userInfo = bean.getUserInfo();

        if (userInfo != null) {
            //头像
            String userIcon = userInfo.getUserIcon();
            ImageLoader.loadImage(avatar, userIcon, R.drawable.default_head, R.drawable.default_head);
            avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean == null || bean.getUserInfo() == null) {
                        return;
                    }
                    LogUtil.i(TAG, "用户id:  " + bean.getUserInfo().getUserId());
                    Intent intent = new Intent(getContext(), PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, bean.getUserInfo().getUserId());
                    getContext().startActivity(intent);
                }
            });
            //名字
            ctvName.setData(userInfo);
            //花粉人儿认证
            String starUserTitle = userInfo.getStarUserTitle();
            if (!TextUtils.isEmpty(starUserTitle)) {
                tvAuth.setVisibility(VISIBLE);
                tvAuth.setText(starUserTitle);
            } else {
                tvAuth.setVisibility(GONE);
            }
            //好评率和在售商品
            String favorableRate;
            if (bean.rateCount != null && !bean.rateCount.isEmpty()) {
                int count = 0;
                int countGoods = 0;
                if (bean.rateCount.containsKey("30")) {
                    Integer var = bean.rateCount.get("30");
                    count += var;
                    countGoods = var;
                }

                if (bean.rateCount.containsKey("20")) {
                    Integer var = bean.rateCount.get("20");
                }

                if (bean.rateCount.containsKey("10")) {
                    Integer var = bean.rateCount.get("10");
                    count += var;
                }

                if (count > 0) {
                    int progress = countGoods * 100 / count;
                    favorableRate = progress + "%   ";
                } else {
                    favorableRate = "0%   ";
                }

            } else {
                favorableRate = "0%   ";
            }
            int sellingCount = userInfo.getSellingCount();
            String favRateAndSellingCount = String.format(getContext().getResources().getString(R.string.fav_rate_and_selling_count), favorableRate, sellingCount);
            tvFavorableRateAndOnSell.setText(favRateAndSellingCount);
            if (CommonPreference.getUserId() == userInfo.getUserId()) {
                ivFollow.setVisibility(GONE);
            } else {
                ivFollow.setVisibility(VISIBLE);
                final int followShip = userInfo.fellowship;
                //1 无关系 2 已关注 3 被关注 4 互相关注
                ivFollow.setPinkData(followShip);

                ivFollow.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (1 == DialogManager.concernedUserDialog(getContext(), followShip, new DialogCallback() {
                            @Override
                            public void Click() {
                                startRequestForConcernedUser("2", userInfo, ivFollow);
                            }
                        })) {
                            startRequestForConcernedUser("1", userInfo, ivFollow);
                        }
                    }
                });
            }
        }
    }

    private void initPart4(final GoodsInfoBean bean) {
        final HPCommentsResult result = bean.getComment();
        final GoodsInfo goodsInfo = bean.getGoodsInfo();

        if (bean.bidInfo != null && !ArrayUtil.isEmpty(bean.bidInfo.bidList)) {
            bidListLayout.setVisibility(VISIBLE);
            bidListLayout.setData(goodsInfo.getGoodsId(), bean.bidInfo.bidList, bean.bidInfo.bidCount, goodsInfo.getGoodsState());
        } else {
            bidListLayout.setVisibility(GONE);
        }

        commentContainer.setTargetType(1);
        commentContainer.setOnHandleHPCommentAdapterListener(new HPCommentAdapter.OnHandleHPCommentAdapterListener() {

            @Override
            public void onDelete() {
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        });
        commentContainer.setCommentAble(bean.getCommentable());
        commentContainer.setTargetId(goodsInfo.getGoodsId());
        commentContainer.setData(result, goodsInfo);
        commentContainer.setSearchQuery(searchQuery);
        commentContainer.setRecTraceId(recTraceId);
        commentContainer.setRecIndex(recIndex);
        commentContainer.setGoodsOwnerId(bean.getUserInfo().getUserId());
    }

    // 活动
    private String getCampaign(int campaignId) {
        if (campaignId <= 0)
            return "";

        ArrayList<Campaign> campaigns = CommonPreference.getCampaigns();
        if (ArrayUtil.isEmpty(campaigns))
            return "";

        for (Campaign c : campaigns) {
            if (campaignId == c.getCid())
                return c.getName();
        }

        return "";
    }

    private void initGoodsInfo(GoodsInfoBean bean) {
        GoodsInfo goodsInfo = bean.getGoodsInfo();
        if (goodsInfo != null) {
            //预售时间
            long preSellTime = goodsInfo.getPresell();
            long systemTime = System.currentTimeMillis();
            long residue = preSellTime - systemTime;

            if (residue > 0) {
                llPreSellTime.setVisibility(View.VISIBLE);
                looperPager.setIndicatorViewMarginBottom(40f);
                // 活动
                String var1 = getCampaign(goodsInfo.getCampaignId());
                String var2 = "";
                if (preSellTime > 0) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    var2 = format.format(new Date(preSellTime));
                }

                String timeFormat = String.format(getContext().getResources().getString(R.string.goods_detail_pre_sell), var1, var2);
                tvPreSellTime.setText(timeFormat);
            } else {
                llPreSellTime.setVisibility(View.GONE);
                looperPager.setIndicatorViewMarginBottom(10f);
            }

            if (goodsInfo.isAuction == 1) {
                if (goodsInfo.getGoodsState() == 2) {//预售
                    long bidStartTime = goodsInfo.bidStartTime;
                    long tmpStart = bidStartTime - goodsInfo.currentTimeMillis;
                    if (tmpStart > 0) {
                        llAuctionTime.setVisibility(VISIBLE);
                        llAuctionTime.setBackgroundColor(Color.parseColor("#CC523920"));
                        looperPager.setIndicatorViewMarginBottom(40f);
                        if (bidStartTimer != null) {
                            handler.removeCallbacks(bidStartTimer);
                        }
                        tvAuctionTime.setGravity(Gravity.LEFT);
                        String residueTime = DateTimeUtils.getResidueTime(tmpStart);
                        tvAuctionTime.setText("距离拍卖开始时间  " + residueTime);
                        handler.postDelayed(bidStartTimer = new BidStartTimer(tmpStart), 1000);
                    } else {
                        llAuctionTime.setVisibility(GONE);
                    }
                } else if (goodsInfo.getGoodsState() == 1) {//在售
                    long bidEndTime = goodsInfo.bidEndTime;
                    long tmpEnd = bidEndTime - goodsInfo.currentTimeMillis;
                    if (tmpEnd > 0) {
                        llAuctionTime.setVisibility(VISIBLE);
                        llAuctionTime.setBackgroundColor(Color.parseColor("#CC523920"));
                        looperPager.setIndicatorViewMarginBottom(40f);
                        if (bidEndTimer != null) {
                            handler.removeCallbacks(bidEndTimer);
                        }
                        tvAuctionTime.setGravity(Gravity.LEFT);
                        String residueTime = DateTimeUtils.getResidueTime(tmpEnd);
                        tvAuctionTime.setText("距离拍卖结束时间  " + residueTime);
                        handler.postDelayed(bidEndTimer = new BidEndTimer(tmpEnd), 1000);
                    } else {
                        llAuctionTime.setVisibility(VISIBLE);
                        llAuctionTime.setBackgroundColor(Color.parseColor("#CC888888"));
                        tvAuctionTime.setGravity(Gravity.CENTER_HORIZONTAL);
                        tvAuctionTime.setText("本拍卖已结束");
                        looperPager.setIndicatorViewMarginBottom(40f);
                    }
                } else if (goodsInfo.getGoodsState() == 4 || goodsInfo.getGoodsState() == 3 || goodsInfo.getGoodsState() == 5) {
                    llAuctionTime.setVisibility(VISIBLE);
                    llAuctionTime.setBackgroundColor(Color.parseColor("#CC888888"));
                    tvAuctionTime.setGravity(Gravity.CENTER_HORIZONTAL);
                    tvAuctionTime.setText("本拍卖已结束");
                    looperPager.setIndicatorViewMarginBottom(40f);
                } else {
                    llAuctionTime.setVisibility(GONE);
                    looperPager.setIndicatorViewMarginBottom(10f);
                }

            }


            //品牌和名字
            String brand = goodsInfo.getGoodsBrand();
            String name = goodsInfo.getGoodsName();
            String goodsDes;
            if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(name)) {
                String goodsFormat = getContext().getResources().getString(R.string.goods_name_desc);
                goodsDes = String.format(goodsFormat, brand, name);
            } else {
                goodsDes = TextUtils.isEmpty(brand) ? (TextUtils.isEmpty(name) ? "" : name) : brand;
            }
            tvGoodsNameAndBrand.setText(goodsDes);
            UserInfo userInfo = bean.getUserInfo();
            if (userInfo != null && userInfo.getUserId() > 0 && CommonPreference.getUserId() > 0 && CommonPreference.getUserId() == userInfo.getUserId()) {
                //浏览数
                int views = goodsInfo.getGoodsPV();
                String viewsDes = String.format(getContext().getResources().getString(R.string.goods_detail_views), views);
                tvViews.setText(viewsDes);
            } else {
                String lastVisitText = bean.lastVisitText;
                if (!TextUtils.isEmpty(lastVisitText)) {
                    tvViews.setText(lastVisitText);
                }
            }

            if (goodsInfo.isAuction == 1) {//拍卖商品
                llGoodsPriceLayout.setVisibility(GONE);
                llAuctionPriceLayout.setVisibility(VISIBLE);
                if (goodsInfo.getGoodsState() == 2) {//预售
                    tvAuctionDes.setText("起拍价");
                } else if (goodsInfo.getGoodsState() == 1) {//在售
                    if (goodsInfo.bidder > 0) {//有人出价
                        tvAuctionDes.setText("当前价格");
                    } else {
                        tvAuctionDes.setText("起拍价");
                    }
                } else if (goodsInfo.getGoodsState() == 4) {//已售出
                    tvAuctionDes.setText("成交价");
                } else if (goodsInfo.getGoodsState() == 3) {//交易中
                    tvAuctionDes.setText("成交价");
                } else if (goodsInfo.getGoodsState() == 5) {//下架
                    if (goodsInfo.bidder > 0) {//有人出价
                        tvAuctionDes.setText("成交价");
                    } else {
                        tvAuctionDes.setText("起拍价");
                    }
                } else {
                    tvAuctionDes.setText("起拍价");
                }
                tvAuctionPrice.setText("¥" + goodsInfo.hammerPrice);
            } else {
                llGoodsPriceLayout.setVisibility(VISIBLE);
                llAuctionPriceLayout.setVisibility(GONE);
                //现价和原价
                int price = goodsInfo.getPrice();
                int pastPrice = goodsInfo.getPastPrice();
                tvPrice.setText("¥" + String.valueOf(price));
                if (pastPrice < 0) {
                    tvPastPrice.setVisibility(GONE);
                } else {
                    tvPastPrice.setVisibility(VISIBLE);
                    tvPastPrice.setText("¥" + String.valueOf(pastPrice));
                }

                tvPastPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

                //邮费
                boolean isFreeDelivery = goodsInfo.getIsFreeDelivery();
                int postType = goodsInfo.getPostType();
                if (isFreeDelivery) {
                    tvFreeDelivery.setText("包邮");
                    tvFreeDelivery.setVisibility(View.VISIBLE);
                } else {
                    tvFreeDelivery.setVisibility(View.VISIBLE);
                    if (postType == 4) {//邮费到付
                        tvFreeDelivery.setText("邮费到付");
                    } else {
                        if (goodsInfo.getPostage() != 0) {
                            CommonUtils.setPriceSizeData(tvFreeDelivery, "邮费", goodsInfo.getPostage());
                        } else {
                            tvFreeDelivery.setText("运费待议");
                        }
                    }
                }

            }


            String goodsContent = goodsInfo.getGoodsContent();
            if (!TextUtils.isEmpty(goodsContent)) {
                tvContent.setVisibility(VISIBLE);
                tvContent.setText(goodsContent);
            } else {
                tvContent.setVisibility(GONE);
            }

            //定位信息
            Area areaBean = goodsInfo.getArea();
            String city = areaBean.getCity();
            String area = areaBean.getArea();
            String locationDes;
            if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(area)) {
                locationDes = String.format(getContext().getResources().getString(R.string.goods_name_desc), city, area);
            } else {
                locationDes = TextUtils.isEmpty(city) ? (TextUtils.isEmpty(area) ? "" : area) : city;
            }
            tvLocation.setText(locationDes);
        }

    }

    private void initRecordSound(final GoodsInfo goodsInfo) {
        int sound = goodsInfo.getSoundTime();
        if (sound == 0) {
            layoutPlay.setVisibility(View.GONE);
        } else {
            layoutPlay.setVisibility(View.VISIBLE);
            layoutPlay.requestLayout();
            tvSoundTime.setText(String.valueOf(sound) + "\"");
        }
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        layoutPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!downloadState) {
                    if (goodsInfo != null) {
                        String voiceRecord = goodsInfo.getSound();
                        if (!TextUtils.isEmpty(voiceRecord)) {
                            downloadVoice(voiceRecord);
                        }
                    }
                    return;
                }
                // 如果不是正在播放
                if (!playState) {
                    try {
                        mAudioManager.requestAudioFocus(GoodsDetailHeader1.this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                        media.prepare();
                        media.start();
                        playState = true;
                        ivPlayState.setBackgroundResource(R.drawable.play_state_animation);
                        AnimationDrawable playAnimation = (AnimationDrawable) ivPlayState.getBackground();
                        playAnimation.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mediaStop();
                }
            }
        });
    }

    private void downloadVoice(String voiceUrl) {
        OkHttpClientManager.downloadAsync(voiceUrl, FileUtils.getVoiceDir(), new OkHttpClientManager.StringCallback() {

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, response);
                String voicePath = response;
                downloadState = true;
                layoutPlay.setVisibility(View.VISIBLE);
                // 实例化MediaPlayer对象
                media = new MediaPlayer();
                media.setAudioStreamType(AudioManager.STREAM_MUSIC);
                File file = new File(voicePath);
                try {
                    // 设置播放资源
                    media.setDataSource(file.getAbsolutePath());
                    // 设置播放结束时监听
                    media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            try {
                                mp.stop();
                            } catch (Exception e) {

                            }
                            if (playState) {
                                playState = false;
                                ivPlayState.setBackgroundResource(R.drawable.play_state3);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, request.toString());
                downloadState = false;
                layoutPlay.setVisibility(View.GONE);
            }
        });
    }

    private void mediaStop() {
        try {
            playState = false;
            AnimationDrawable playAnimation = (AnimationDrawable) ivPlayState.getBackground();
            playAnimation.stop();
            if (ivPlayState != null) {
                ivPlayState.setBackgroundResource(R.drawable.play_state3);
            }
            if (media != null) {
                if (media.isPlaying()) {
                    media.stop();
                }
            }
        } catch (Exception e) {

        }
    }

    private void initLooperPager(FragmentManager fragmentManager, GoodsInfo goodsInfo) {

        int width = CommonUtils.getScreenWidth();
        LayoutParams params = new LayoutParams(width, width);
        rlLooperPager.setLayoutParams(params);
        looperPager.setAutoLoop(false);
        ArrayList<VBanner> banners = covertMediumBanners(goodsInfo);
        final ArrayList<VBanner> oBanners = covertOriginalBanners(goodsInfo);
        looperPager.setData(banners, fragmentManager);
        looperPager.setOnItemClickListener(new LooperPager.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), GalleryActivity.class);
                intent.putExtra("banners", oBanners);
                intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                getContext().startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(0, 0);
            }
        });
    }

    private ArrayList<VBanner> covertOriginalBanners(GoodsInfo goodsInfo) {
        if (goodsInfo == null) {
            return null;
        }
        ArrayList<VBanner> banners = new ArrayList<VBanner>();
        ArrayList<String> imgs = goodsInfo.getGoodsImgs();
        ArrayList<String> images = CommonUtils.getOSSStyle(imgs, MyConstants.OSS_ORIGINAL_STYLE);

        if (!TextUtils.isEmpty(goodsInfo.getVideoPath())) {
            VBanner videoBanner = new VBanner(1, goodsInfo.getVideoCover());
            videoBanner.videoPath = goodsInfo.getVideoPath();
            banners.add(videoBanner);
        }

        for (String banner : images) {
            VBanner vBanner = new VBanner(2, banner);
            banners.add(vBanner);
        }

        for (int i = 0; i < banners.size(); i++) {
            VBanner banner = banners.get(i);
            banner.position = i;
        }

        return banners;

    }

    private ArrayList<VBanner> covertMediumBanners(GoodsInfo goodsInfo) {
        if (goodsInfo == null) {
            return null;
        }
        ArrayList<VBanner> banners = new ArrayList<VBanner>();
        ArrayList<String> imgs = goodsInfo.getGoodsImgs();
        ArrayList<String> images = CommonUtils.getOSSStyle(imgs, MyConstants.OSS_MEDIUM_STYLE);

        if (!TextUtils.isEmpty(goodsInfo.getVideoPath())) {
            VBanner videoBanner = new VBanner(1, goodsInfo.getVideoCover());
            videoBanner.videoPath = goodsInfo.getVideoPath();
            banners.add(videoBanner);
        }

        for (String banner : images) {
            VBanner vBanner = new VBanner(2, banner);
            banners.add(vBanner);
        }

        for (int i = 0; i < banners.size(); i++) {
            VBanner banner = banners.get(i);
            banner.position = i;
        }

        return banners;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                mediaStop();
                break;
            default:
                break;
        }
    }

    /**
     * 关注
     *
     * @param
     */
    private void startRequestForConcernedUser(String type, final UserInfo userInfo, final View view) {
        if (!CommonUtils.isNetAvaliable(getContext())) {
            ToastUtil.toast(getContext(), "请检查网络连接");
            return;
        }
        if (userInfo == null) {
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(userInfo.getUserId()));
        final int fellowship = userInfo.fellowship;
        params.put("type", type);

        LogUtil.i(TAG, "params:" + params.toString());
        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.CONCERNEDUSER, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                view.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                view.setEnabled(true);
                LogUtil.i(TAG, "关注:" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        //1 无关系 2 已关注 3 被关注 4 互相关注
                        if (fellowship == 1) {
                            ivFollow.setPinkData(2);
                            userInfo.fellowship = 2;
                        } else if (fellowship == 2) {
                            ivFollow.setPinkData(1);
                            userInfo.fellowship = 1;
                        } else if (fellowship == 3) {
                            ivFollow.setPinkData(4);
                            userInfo.fellowship = 4;
                        } else if (fellowship == 4) {
                            ivFollow.setPinkData(3);
                            userInfo.fellowship = 3;
                        }
                    } else {
                        if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
                            CommonUtils.checkAccess((Activity) getContext());
                        }
                        ToastUtil.toast(getContext(), baseResult.msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.llPreSellTime)
    public void onViewClicked() {
        final ProductPreSellDialog dialog;
        dialog = new ProductPreSellDialog(getContext());
        dialog.show();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    public void removeCallback() {
        if (handler != null) {
            handler.removeCallbacks(bidStartTimer);
            handler.removeCallbacks(bidEndTimer);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.e(TAG, "onDetachedFromWindow");
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public class BidStartTimer implements Runnable {

        private long dValue;

        public BidStartTimer(long dValue) {
            this.dValue = dValue;
        }

        @Override
        public void run() {
            dValue -= 1000;

            if (dValue > 0) {
                llAuctionTime.setVisibility(VISIBLE);
                llAuctionTime.setBackgroundColor(Color.parseColor("#CC523920"));
                looperPager.setIndicatorViewMarginBottom(41f);
                tvAuctionTime.setGravity(Gravity.LEFT);
                String residueTime = DateTimeUtils.getResidueTime(dValue);
                tvAuctionTime.setText("距离拍卖开始时间  " + residueTime);
                handler.postDelayed(this, 1000);
            } else {
                if (handler != null) {
                    handler.removeCallbacks(this);
                }
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        }
    }

    public class BidEndTimer implements Runnable {

        private long dValue;

        public BidEndTimer(long dValue) {
            this.dValue = dValue;
        }

        @Override
        public void run() {
            dValue -= 1000;

            if (dValue > 0) {
                llAuctionTime.setVisibility(VISIBLE);
                llAuctionTime.setBackgroundColor(Color.parseColor("#CC523920"));
                looperPager.setIndicatorViewMarginBottom(41f);
                tvAuctionTime.setGravity(Gravity.LEFT);
                String residueTime = DateTimeUtils.getResidueTime(dValue);
                tvAuctionTime.setText("距离拍卖结束时间  " + residueTime);
                handler.postDelayed(this, 1000);
            } else {
                llAuctionTime.setVisibility(VISIBLE);
                llAuctionTime.setBackgroundColor(Color.parseColor("#CC888888"));
                tvAuctionTime.setGravity(Gravity.CENTER_HORIZONTAL);
                tvAuctionTime.setText("本拍卖已结束");
                looperPager.setIndicatorViewMarginBottom(41f);

                if (handler != null) {
                    handler.removeCallbacks(this);
                }
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        }
    }
}
