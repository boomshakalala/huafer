package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.DepositActivity;
import com.huapu.huafen.activity.MontageActivity;
import com.huapu.huafen.activity.OrderConfirmActivity;
import com.huapu.huafen.activity.VerifiedActivity;
import com.huapu.huafen.beans.Audit;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.GoodsInfoBean;
import com.huapu.huafen.beans.OrderConfirmBean;
import com.huapu.huafen.callbacks.OnRequestRetryListener;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.JoinCampaignDialog;
import com.huapu.huafen.dialog.OfferPriceDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import static com.huapu.huafen.utils.LogUtil.e;

/**
 * Created by admin on 2017/3/22.
 */

public class GoodsDetailBottomLayout extends LinearLayout implements GoodsDetailBuyerBottom.OnFavStateChangedListener {

    private final static String TAG = GoodsDetailBottomLayout.class.getSimpleName();
    private Context context;
    /**
     * 1 在售
     * 2 预售中
     * 3 交易中
     * 4 已售出
     * 5 下架
     * 6 被举报
     * 7 已删除
     */
    private final static int ON_SELL = 1;//在售
    private final static int PRE_SELL = ON_SELL + 1;//预售中
    private final static int ON_DEALING = PRE_SELL + 1;//交易中
    private final static int SOLD = ON_DEALING + 1;//已售出
    private final static int OFF_SHELF = SOLD + 1;//下架
    private final static int BE_REPORTED = OFF_SHELF + 1;//被举报
    private final static int DELETED = BE_REPORTED + 1;//已删除

    /**
     * 1 通过上首页
     * 2 通过不上首页
     * 3 轻微拒绝
     * 4 严重拒绝
     * 5 待审核
     */
    private final static int AUDIT_HOME_PAGE = 1;
    private final static int AUDIT_UN_HOME_PAGE = AUDIT_HOME_PAGE + 1;
    private final static int AUDIT_LITTLE_REFUSED = AUDIT_UN_HOME_PAGE + 1;
    private final static int AUDIT_SERIOUS_REFUSED = AUDIT_LITTLE_REFUSED + 1;
    private final static int NOT_AUDITED = AUDIT_SERIOUS_REFUSED + 1;
    private Handler handler = new Handler();
    private CountDownTime runnable;
    private BidStartTimer runnable2;
    /**
     * 修改商品状态
     */
    HashMap<String, String> params = new HashMap<>();
    private String recTranceId;
    private int recIndex;
    private String searchQuery;
    private String goodsId;
    private GoodsInfoBean goodsInfoBean;
    private OnFavStateChangedListener mOnFavStateChangedListener;
    private boolean hasCandidateCampaigns;

    public GoodsDetailBottomLayout(Context context) {
        this(context, null);
    }


    public GoodsDetailBottomLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        setOrientation(HORIZONTAL);
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setRecTranceId(String recTranceId) {
        this.recTranceId = recTranceId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public void setRecIndex(int recIndex) {
        this.recIndex = recIndex;
    }

    public boolean isHasCandidateCampaigns() {
        return hasCandidateCampaigns;
    }

    public void setHasCandidateCampaigns(boolean hasCandidateCampaigns) {
        this.hasCandidateCampaigns = hasCandidateCampaigns;
    }

    public void setData(GoodsInfoBean goodsInfoBean, String goodsId, boolean isBuyer) {
        this.setVisibility(VISIBLE);
        removeAllViews();
        if (goodsInfoBean == null) {
            return;
        }

        GoodsInfo goodsInfo = goodsInfoBean.getGoodsInfo();
        long systemTime = System.currentTimeMillis();
        //开拍时间
        long bidStartTime = goodsInfo.bidStartTime;
        long tmpStart = bidStartTime - goodsInfo.currentTimeMillis;
        //出售时间
        long preSell = goodsInfo.getPresell();
        long residue = preSell - systemTime;
        if (goodsInfo != null) {
            int goodsState = goodsInfo.getGoodsState();
            int auditStatus = goodsInfo.getAuditStatus();

            Audit audit = CommonPreference.getAudit();
            int buyAble = 0;
            if (audit != null) {
                buyAble = audit.getBuyable();
            }
            /**************************卖家（本人）**************************/
            if (goodsInfoBean.getUserInfo().getUserId() == CommonPreference.getUserId()) {
                if (goodsInfoBean.editable == 0 && goodsInfoBean.unshelvable == 0) {
                    genDeleteCommodity(goodsInfo);
                } else {
                    if (goodsState == ON_SELL) {//在售
                        if (auditStatus == AUDIT_HOME_PAGE) {//通过上首页
                            if (goodsInfo.isAuction == 1) {
                                if (goodsInfo.hasBidDepositPayed == 1) {
                                    if (goodsInfo.bidder == 0) {
                                        genGrayState("已有人交纳保证金");
                                    } else {
                                        genGrayState("已有人出价");
                                    }
                                } else {
                                    genOnShelfAndEditCommodity(goodsInfo);
                                }
                            } else {
                                genOnShelfAndEditCommodity(goodsInfo);
                            }
                        } else if (auditStatus == AUDIT_UN_HOME_PAGE) {//通过不上首页
                            if (goodsInfo.isAuction == 1) {
                                if (goodsInfo.hasBidDepositPayed == 1) {
                                    if (goodsInfo.bidder == 0) {
                                        genGrayState("已有人交纳保证金");
                                    } else {
                                        genGrayState("已有人出价");
                                    }
                                } else {
                                    genOnShelfAndEditCommodity(goodsInfo);
                                }
                            } else {
                                genOnShelfAndEditCommodity(goodsInfo);
                            }
                        } else if (auditStatus == AUDIT_LITTLE_REFUSED) {//轻微拒绝
                            genDeleteAndEditCommodity(goodsInfo);
                        } else if (auditStatus == AUDIT_SERIOUS_REFUSED) {//严重拒绝
                            genDeleteCommodity(goodsInfo);
                        } else if (auditStatus == NOT_AUDITED) {//未审核
                            genDeleteAndEditCommodity(goodsInfo);
                        }
                    } else if (goodsState == PRE_SELL) {//预售中
                        if (auditStatus == AUDIT_HOME_PAGE) {//通过上首页
                            if (goodsInfo.isAuction == 1) {
                                if (goodsInfo.hasBidDepositPayed == 1) {
                                    if (goodsInfo.bidder == 0) {
                                        genGrayState("已有人交纳保证金");
                                    } else {
                                        genGrayState("已有人出价");
                                    }
                                } else {
                                    genOnShelfAndEditCommodity(goodsInfo);
                                }
                            } else {
                                genOnShelfAndEditCommodity(goodsInfo);
                            }
                        } else if (auditStatus == AUDIT_UN_HOME_PAGE) {//通过不上首页
                            if (goodsInfo.isAuction == 1) {
                                if (goodsInfo.hasBidDepositPayed == 1) {
                                    if (goodsInfo.bidder == 0) {
                                        genGrayState("已有人交纳保证金");
                                    } else {
                                        genGrayState("已有人出价");
                                    }
                                } else {
                                    genOnShelfAndEditCommodity(goodsInfo);
                                }
                            } else {
                                genOnShelfAndEditCommodity(goodsInfo);
                            }
                        } else if (auditStatus == AUDIT_LITTLE_REFUSED) {//轻微拒绝
                            genDeleteAndEditCommodity(goodsInfo);
                        } else if (auditStatus == AUDIT_SERIOUS_REFUSED) {//严重拒绝
                            genDeleteCommodity(goodsInfo);
                        } else if (auditStatus == NOT_AUDITED) {//未审核
                            genDeleteAndEditCommodity(goodsInfo);
                        }
                    } else if (goodsState == ON_DEALING) {//交易中
                        genGrayState("交易中");
                    } else if (goodsState == SOLD) {//已售出
                        genGrayState("已售出");
                    } else if (goodsState == OFF_SHELF) {//下架
                        genDeleteAndEditCommodity(goodsInfo);
                    } else if (goodsState == BE_REPORTED) {//被举报
                        this.setVisibility(GONE);
                    } else if (goodsState == DELETED) {//已删除
                        this.setVisibility(GONE);
                    }
                }

                /**************************买家**************************/
            } else {
                if (goodsState == ON_SELL) {//在售
                    if (auditStatus == AUDIT_HOME_PAGE) {//通过上首页
                        //拍卖品
                        if (goodsInfo.isAuction == 1) {
                            genAuctionBuy(goodsInfoBean);
                        } else {
                            genBuyNow(goodsInfoBean, goodsId);
                        }
                    } else if (auditStatus == AUDIT_UN_HOME_PAGE) {//通过不上首页
                        if (goodsInfo.isAuction == 1) {
                            genAuctionBuy(goodsInfoBean);

                        } else {
                            genBuyNow(goodsInfoBean, goodsId);
                        }
                    } else if (auditStatus == AUDIT_LITTLE_REFUSED) {//轻微拒绝
                        if (buyAble == 0) {
                            genGrayState("审核未通过");
                        } else {
                            if (goodsInfo.isAuction == 1) {
                                genAuctionBuy(goodsInfoBean);
                            } else {
                                genBuyNow(goodsInfoBean, goodsId);
                            }
                        }
                    } else if (auditStatus == AUDIT_SERIOUS_REFUSED) {//严重拒绝
                        if (buyAble == 0) {
                            genGrayState("审核未通过");
                        } else {
                            if (goodsInfo.isAuction == 1) {
                                genAuctionBuy(goodsInfoBean);
                            } else {
                                genBuyNow(goodsInfoBean, goodsId);
                            }
                        }
                    } else if (auditStatus == NOT_AUDITED) {//未审核
                        if (buyAble == 0) {
                            genGrayState("待审核");
                        } else {
                            if (goodsInfo.isAuction == 1) {
                                genAuctionBuy(goodsInfoBean);
                            } else {
                                genBuyNow(goodsInfoBean, goodsId);
                            }
                        }
                    }

                } else if (goodsState == PRE_SELL) {//预售中

                    if (auditStatus == AUDIT_HOME_PAGE) {//通过上首页
                        if (goodsInfo.isAuction == 1) {
                            if (goodsInfo.hasBidDepositPayed == 1) {
                                genAuctionPreSell(goodsInfoBean, tmpStart);
                            } else {
                                genAuctionBuy(goodsInfoBean);
                            }
                        } else {
                            genBuyPreSell(goodsInfoBean, goodsId, residue);
                        }
                    } else if (auditStatus == AUDIT_UN_HOME_PAGE) {//通过不上首页
                        if (goodsInfo.isAuction == 1) {
                            if (goodsInfo.hasBidDepositPayed == 1) {
                                genAuctionPreSell(goodsInfoBean, tmpStart);
                            } else {
                                genAuctionBuy(goodsInfoBean);
                            }
                        } else {
                            genBuyPreSell(goodsInfoBean, goodsId, residue);
                        }
                    } else if (auditStatus == AUDIT_LITTLE_REFUSED) {//轻微拒绝
                        if (buyAble == 0) {
                            genBuyState(goodsInfoBean, goodsId, "审核未通过");
                        } else {
                            if (goodsInfo.isAuction == 1) {
                                if (goodsInfo.hasBidDepositPayed == 1) {
                                    genAuctionPreSell(goodsInfoBean, tmpStart);
                                } else {
                                    genAuctionBuy(goodsInfoBean);
                                }
                            } else {
                                genBuyPreSell(goodsInfoBean, goodsId, residue);
                            }
                        }
                    } else if (auditStatus == AUDIT_SERIOUS_REFUSED) {//严重拒绝
                        if (buyAble == 0) {
                            genBuyState(goodsInfoBean, goodsId, "审核未通过");
                        } else {
                            if (goodsInfo.isAuction == 1) {
                                if (goodsInfo.hasBidDepositPayed == 1) {
                                    genAuctionPreSell(goodsInfoBean, tmpStart);
                                } else {
                                    genAuctionBuy(goodsInfoBean);
                                }
                            } else {
                                genBuyPreSell(goodsInfoBean, goodsId, residue);
                            }
                        }
                    } else if (auditStatus == NOT_AUDITED) {//未审核
                        if (buyAble == 0) {
                            genBuyState(goodsInfoBean, goodsId, "待审核");
                        } else {
                            if (goodsInfo.isAuction == 1) {
                                if (goodsInfo.hasBidDepositPayed == 1) {
                                    genAuctionPreSell(goodsInfoBean, tmpStart);
                                } else {
                                    genAuctionBuy(goodsInfoBean);
                                }
                            } else {
                                genBuyPreSell(goodsInfoBean, goodsId, residue);
                            }
                        }
                    }
                } else if (goodsState == ON_DEALING) {//交易中
                    if (isBuyer) {
                        genBuyState(goodsInfoBean, goodsId, "已拍下");
                    } else {
                        genBuyState(goodsInfoBean, goodsId, "已被抢走了");
                    }
                } else if (goodsState == SOLD) {//已售出
                    if (goodsInfo.isAuction == 1) {
                        genAuctionSoldOut(goodsInfoBean);
                    } else {
                        genBuyState(goodsInfoBean, goodsId, "已售出");
                    }

                } else if (goodsState == OFF_SHELF) {//下架
                    genBuyState(goodsInfoBean, goodsId, "已下架");
                } else if (goodsState == BE_REPORTED) {//被举报
                    this.setVisibility(GONE);
                } else if (goodsState == DELETED) {//已删除
                    this.setVisibility(GONE);
                }
            }
        }
    }

    //下架和编辑商品（自己，卖家）
    private void genOnShelfAndEditCommodity(GoodsInfo goodsInfo) {
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);

        TextView textOffShelf = new TextView(getContext());//下架
        textOffShelf.setGravity(Gravity.CENTER);
        textOffShelf.setText("下架");
        textOffShelf.setTextColor(getContext().getResources().getColor(R.color.text_color_gray));
        textOffShelf.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textOffShelf.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        textOffShelf.setOnClickListener(new OnShelfClickListener(goodsInfo));
        textOffShelf.setLayoutParams(params);

        TextView joinCampaign = new TextView(getContext());//下架
        joinCampaign.setGravity(Gravity.CENTER);
        joinCampaign.setText("参加活动");
        joinCampaign.setTextColor(getContext().getResources().getColor(R.color.text_color_gray));
        joinCampaign.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        joinCampaign.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        joinCampaign.setOnClickListener(new OnJoinCampaignClickListener());
        joinCampaign.setLayoutParams(params);

        View line = new View(getContext());
        LayoutParams lineParams = new LayoutParams(1, LayoutParams.MATCH_PARENT);
        line.setBackgroundResource(R.color.lcim_divider_line_color);
        line.setLayoutParams(lineParams);


        TextView textEditCommodity = new TextView(getContext());//编辑商品
        textEditCommodity.setGravity(Gravity.CENTER);
        textEditCommodity.setText("编辑商品");
        textEditCommodity.setTextColor(getContext().getResources().getColor(R.color.white));
        textEditCommodity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textEditCommodity.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
        textEditCommodity.setOnClickListener(new OnEditCommodityClickListener(goodsInfo));
        textEditCommodity.setLayoutParams(params);

        addView(textOffShelf);
        if (hasCandidateCampaigns && goodsInfo.isAuction!=1){
            addView(line);
            addView(joinCampaign);
        }
        addView(textEditCommodity);
    }

    //删除和编辑商品（自己，卖家）
    private void genDeleteAndEditCommodity(GoodsInfo goodsInfo) {
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);

        TextView textDeleteCommodity = new TextView(getContext());//删除
        textDeleteCommodity.setGravity(Gravity.CENTER);
        textDeleteCommodity.setText("删除");
        textDeleteCommodity.setTextColor(getContext().getResources().getColor(R.color.text_color_gray));
        textDeleteCommodity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textDeleteCommodity.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        textDeleteCommodity.setOnClickListener(new OnDeleteCommodityClickListener(goodsInfo));
        textDeleteCommodity.setLayoutParams(params);

        TextView textEditCommodity = new TextView(getContext());//编辑商品
        textEditCommodity.setGravity(Gravity.CENTER);
        textEditCommodity.setText("编辑商品");
        textEditCommodity.setTextColor(getContext().getResources().getColor(R.color.white));
        textEditCommodity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textEditCommodity.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
        textEditCommodity.setOnClickListener(new OnEditCommodityClickListener(goodsInfo));
        textEditCommodity.setLayoutParams(params);

        addView(textDeleteCommodity);
        addView(textEditCommodity);
    }

    //审核状态为严重拒绝，只展示删除按钮（自己，卖家）
    private void genDeleteCommodity(GoodsInfo goodsInfo) {
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
        TextView textDeleteCommodity = new TextView(getContext());//删除商品
        textDeleteCommodity.setGravity(Gravity.CENTER);
        textDeleteCommodity.setText("删除");
        textDeleteCommodity.setTextColor(getContext().getResources().getColor(R.color.white));
        textDeleteCommodity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textDeleteCommodity.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
        textDeleteCommodity.setOnClickListener(new OnDeleteCommodityClickListener(goodsInfo));
        textDeleteCommodity.setLayoutParams(params);
        addView(textDeleteCommodity);
    }

    //卖家审核状态未通过或待审核
    private void genGrayState(CharSequence charSequence) {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setText(charSequence);
        textView.setOnClickListener(null);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setBackgroundColor(getResources().getColor(R.color.base_btn_normal));

        addView(textView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    //立即购买
    private void genBuyNow(GoodsInfoBean goodsInfoBean, String goodsId) {
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);

        GoodsDetailBuyerBottom goodsDetailBuyerBottom = new GoodsDetailBuyerBottom(getContext());
        goodsDetailBuyerBottom.setRecIndex(recIndex);
        goodsDetailBuyerBottom.setSearchQuery(searchQuery);
        goodsDetailBuyerBottom.setRecTranceId(recTranceId);
        goodsDetailBuyerBottom.setData(goodsInfoBean, goodsId);
        goodsDetailBuyerBottom.setLayoutParams(params);
        goodsDetailBuyerBottom.setOnFavStateChangedListener(this);

        TextView textEditCommodity = new TextView(getContext());//编辑商品
        textEditCommodity.setGravity(Gravity.CENTER);
        textEditCommodity.setText("立即购买");
        textEditCommodity.setTextColor(getContext().getResources().getColor(R.color.white));
        textEditCommodity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textEditCommodity.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
        textEditCommodity.setOnClickListener(new OnBuyNowClickListener(goodsInfoBean, goodsId));
        textEditCommodity.setLayoutParams(params);

        addView(goodsDetailBuyerBottom);
        addView(textEditCommodity);
    }
    //拍卖品/交保证金或出价
    private void genAuctionBuy(GoodsInfoBean goodsInfoBean) {
        removeAllViews();
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);

        GoodsDetailBuyerBottom goodsDetailBuyerBottom = new GoodsDetailBuyerBottom(getContext());
        goodsDetailBuyerBottom.setRecIndex(recIndex);
        goodsDetailBuyerBottom.setSearchQuery(searchQuery);
        goodsDetailBuyerBottom.setRecTranceId(recTranceId);
        goodsDetailBuyerBottom.setData(goodsInfoBean,String.valueOf(goodsInfoBean.getGoodsInfo().getGoodsId()));
        goodsDetailBuyerBottom.setLayoutParams(params);
        goodsDetailBuyerBottom.setOnFavStateChangedListener(this);

        GoodsInfo goodsInfo = goodsInfoBean.getGoodsInfo();

        if (goodsInfo.hasBidDepositPayed == 1) {
            View group = LayoutInflater.from(getContext()).inflate(R.layout.auction_buy_bottom, null);
            TextView textView = (TextView) group.findViewById(R.id.tvBidBuy);
            group.setLayoutParams(params);
            textView.setText("出价");
            group.setOnClickListener(new OnAuctionBuyClick(textView,goodsInfo.hasBidDepositPayed));
            addView(goodsDetailBuyerBottom);
            addView(group);
        } else {
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getContext().getResources().getColor(R.color.white));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
            textView.setLayoutParams(params);
            textView.setText("交保证金报名");
            textView.setOnClickListener(new OnAuctionBuyClick(textView,goodsInfo.hasBidDepositPayed));
            addView(goodsDetailBuyerBottom);
            addView(textView);
        }
    }


    private void genAuctionSoldOut(GoodsInfoBean goodsInfoBean) {
        removeAllViews();
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);

        GoodsDetailBuyerBottom goodsDetailBuyerBottom = new GoodsDetailBuyerBottom(getContext());
        goodsDetailBuyerBottom.setRecIndex(recIndex);
        goodsDetailBuyerBottom.setSearchQuery(searchQuery);
        goodsDetailBuyerBottom.setRecTranceId(recTranceId);
        goodsDetailBuyerBottom.setData(goodsInfoBean, String.valueOf(goodsInfoBean.getGoodsInfo().getGoodsId()));
        goodsDetailBuyerBottom.setLayoutParams(params);
        goodsDetailBuyerBottom.setOnFavStateChangedListener(this);

        View group = LayoutInflater.from(getContext()).inflate(R.layout.auction_sold_out_bottom, null);
//        TextView tvAuctionDes = (TextView) group.findViewById(R.id.tvAuctionDes);
//        tvAuctionDes.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//        tvAuctionDes.setCompoundDrawablePadding(0);
        group.setLayoutParams(params);
        addView(goodsDetailBuyerBottom);
        addView(group);
    }


    private void genAuctionPreSell(GoodsInfoBean goodsInfoBean, long deltime) {
        removeAllViews();
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
        GoodsDetailBuyerBottom goodsDetailBuyerBottom = new GoodsDetailBuyerBottom(getContext());
        goodsDetailBuyerBottom.setRecIndex(recIndex);
        goodsDetailBuyerBottom.setSearchQuery(searchQuery);
        goodsDetailBuyerBottom.setRecTranceId(recTranceId);
        goodsDetailBuyerBottom.setData(goodsInfoBean, String.valueOf(goodsInfoBean.getGoodsInfo().getGoodsId()));
        goodsDetailBuyerBottom.setLayoutParams(params);
        goodsDetailBuyerBottom.setOnFavStateChangedListener(this);
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getContext().getResources().getColor(R.color.white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
        textView.setLayoutParams(params);
        if (deltime > 0) {
            String residueTime = DateTimeUtils.getResidue(deltime);
            textView.setText("距离开拍" + residueTime);
            if (runnable2 != null) {
                handler.removeCallbacks(runnable2);
            }
            handler.postDelayed(runnable2 = new BidStartTimer(deltime, textView), 1000);
            textView.setOnClickListener(null);
        } else {
            textView.setText("出价");
            textView.setOnClickListener(new OnAuctionBuyClick(textView,1));
        }
        addView(goodsDetailBuyerBottom);
        addView(textView);


    }

    //买家适合未通过或待审核状态
    private void genBuyState(GoodsInfoBean goodsInfoBean, String goodsId, CharSequence charSequence) {
        removeAllViews();
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);

        GoodsDetailBuyerBottom goodsDetailBuyerBottom = new GoodsDetailBuyerBottom(getContext());
        goodsDetailBuyerBottom.setRecIndex(recIndex);
        goodsDetailBuyerBottom.setSearchQuery(searchQuery);
        goodsDetailBuyerBottom.setRecTranceId(recTranceId);
        goodsDetailBuyerBottom.setData(goodsInfoBean, goodsId);
        goodsDetailBuyerBottom.setLayoutParams(params);
        goodsDetailBuyerBottom.setOnFavStateChangedListener(this);

        TextView textView = new TextView(getContext());//右侧灰色按钮
        textView.setGravity(Gravity.CENTER);
        textView.setText(charSequence);
        textView.setTextColor(getContext().getResources().getColor(R.color.white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setBackgroundColor(getResources().getColor(R.color.base_btn_normal));
        textView.setOnClickListener(null);
        textView.setLayoutParams(params);

        addView(goodsDetailBuyerBottom);
        addView(textView);
    }

    @Override
    public void onChange(boolean isFav) {
        if (mOnFavStateChangedListener != null) {
            mOnFavStateChangedListener.onChange(isFav);
        }
    }

    private void genBuyPreSell(GoodsInfoBean goodsInfoBean, String goodsId, long residue) {
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);

        GoodsDetailBuyerBottom goodsDetailBuyerBottom = new GoodsDetailBuyerBottom(getContext());
        goodsDetailBuyerBottom.setRecIndex(recIndex);
        goodsDetailBuyerBottom.setSearchQuery(searchQuery);
        goodsDetailBuyerBottom.setRecTranceId(recTranceId);
        goodsDetailBuyerBottom.setData(goodsInfoBean, goodsId);
        goodsDetailBuyerBottom.setLayoutParams(params);
        goodsDetailBuyerBottom.setOnFavStateChangedListener(this);

        TextView textView = new TextView(getContext());//预售或立即购买
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getContext().getResources().getColor(R.color.white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setBackgroundColor(getContext().getResources().getColor(R.color.base_pink));
        textView.setLayoutParams(params);

        if (residue > 0) {
            String residueTime = DateTimeUtils.getResidue(residue);
            textView.setText("距离开售" + residueTime);
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }
            handler.postDelayed(runnable = new CountDownTime(residue, textView, goodsId, goodsInfoBean), 1000);
            textView.setOnClickListener(null);
        } else {
            textView.setText("立即购买");
            textView.setOnClickListener(new OnBuyNowClickListener(goodsInfoBean, goodsId));
        }

        addView(goodsDetailBuyerBottom);
        addView(textView);

    }

    private void startRequestForChangGoodsState(final String stateType, final GoodsInfo goodsInfo) {
        if (goodsInfo == null) {
            return;
        }

        int campaignId = goodsInfo.getCampaignId();

        if (stateType.equals("1")) {
            if ((goodsInfo.getPrivileges() & MyConstants.P_DOWN) == 0) {
                final TextDialog dialog = new TextDialog(getContext(), false);
                dialog.setContentText("商品已参与了活动，不可下架哦~");
                dialog.setLeftText("确定");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return;
            }
        } else if (stateType.equals("2")) {
            if ((goodsInfo.getPrivileges() & MyConstants.P_DELETE) == 0) {
                final TextDialog dialog = new TextDialog(getContext(), false);
                dialog.setContentText("商品已参与了活动，不可删除哦~");
                dialog.setLeftText("确定");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return;
            }
        }
        if (!CommonUtils.isNetAvaliable(getContext())) {
            ToastUtil.toast(getContext(), "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(getContext());

        params.put("goodId", String.valueOf(goodsInfo.getGoodsId()));
        params.put("stateType", stateType);
        params.put("campaignId", String.valueOf(campaignId));
        e(TAG, "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.CHANGGOODSSTATE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                e(TAG, "下架商品:" + response);
                try {
                    boolean flag = CommonUtils.parseEvent(getContext(), response, new OnRequestRetryListener() {

                        @Override
                        public void onRetry() {
                            params.put("confirmed", "1");
                            startRequestForChangGoodsState(stateType, goodsInfo);
                        }
                    });
                    if (flag) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        ToastUtil.toast(getContext(), "操作成功");
                        if (getContext() instanceof Activity) {
                            ((Activity) getContext()).finish();
                        }
                    } else {
                        CommonUtils.error(baseResult, (Activity) getContext(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 购买商品
     *
     * @param
     */
    private void startRequestForOrderBuy(final GoodsInfoBean goodsInfoBean, final String goodsId) {
        if (TextUtils.isEmpty(goodsId)) {
            return;
        }
        ProgressDialog.showProgress(getContext());
        HashMap<String, String> params = new HashMap<>();
        params.put("goodsId", goodsId);
        OkHttpClientManager.postAsyn(MyConstants.ORDERBUY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                e(TAG, "购买商品:" + response);

                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            OrderConfirmBean bean = ParserUtils.parserOrderBuyData(baseResult.obj);
                            if (bean != null) {
                                Intent intent = new Intent(getContext(), OrderConfirmActivity.class);
                                intent.putExtra(MyConstants.EXTRA_ORDER_CONFIRM_BEAN, bean);
                                intent.putExtra(MyConstants.REC_TRAC_ID, recTranceId);
                                intent.putExtra(MyConstants.POSITION, recIndex);
                                intent.putExtra(MyConstants.SEARCH_QUERY, searchQuery);
                                ((Activity) getContext()).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_ORDER_CONFIRM);
                            }
                        }
                    } else if (baseResult.code == ParserUtils.RESPONSE_GOODS_DETAILS_IS_SELL) {
                        genBuyState(goodsInfoBean, goodsId, "已被抢走了");
                    } else {
                        CommonUtils.error(baseResult, (Activity) getContext(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setOnFavStateChangedListener(OnFavStateChangedListener onFavStateChangedListener) {
        this.mOnFavStateChangedListener = onFavStateChangedListener;
    }

    public interface OnFavStateChangedListener {
        void onChange(boolean isFav);
    }

    private class OnAuctionBuyClick implements OnClickListener {
        private TextView textView;
        private int type;

        public OnAuctionBuyClick(TextView textView,int type) {
            this.textView=textView;
            this.type=type;
        }

        @Override
        public void onClick(View v) {
            if (!CommonPreference.isLogin()) {
                ActionUtil.loginAndToast(getContext());
                return;
            }
            if(ConfigUtil.isToVerify()){
                final TextDialog dialog = new TextDialog(context, false);
                if (type == 1){
                    dialog.setContentText("亲，您还未开通实名认证，1分钟完成认证后即可出价");
                }else{
                    dialog.setContentText("亲，您还未开通实名认证，1分钟完成认证后即可报名");

                }
                dialog.setLeftText("取消");
                dialog.setRightColor(Color.parseColor("#2d8bff"));
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("去开通");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        Intent intent = new Intent(context, VerifiedActivity.class);
                        context.startActivity(intent);

                    }
                });
                dialog.show();
            }else {
                HashMap<String, String> params3 = new HashMap<>();
                params3.put("goodsId", goodsId);
                if (!TextUtils.isEmpty(recTranceId)) {
                    params3.put("recTraceId", recTranceId);
                }
                if (recIndex != -1) {
                    params3.put("recIndex", String.valueOf(recIndex));
                }
                if (!TextUtils.isEmpty(searchQuery)) {
                    params3.put("searchQuery", searchQuery);
                }
                OkHttpClientManager.postAsyn(MyConstants.GETGOODSDETAIL, params3, new OkHttpClientManager.StringCallback() {


                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        GoodsInfoBean goodsInfoBean = ParserUtils.parserProDetailsData(baseResult.obj);
                        GoodsInfo goodsInfo = goodsInfoBean.getGoodsInfo();
                        int goodsState = goodsInfo.getGoodsState();
                        if (goodsState == ON_DEALING) {//交易中
                            Toast.makeText(getContext(), "本次拍卖已结束，去别处看看！", Toast.LENGTH_SHORT).show();
                            if (onRefreshListener != null) {
                                onRefreshListener.onRefresh();
                            }
                        } else if (goodsState == PRE_SELL) {//预售
                            Intent intent = new Intent(getContext(), DepositActivity.class);
                            intent.putExtra("goodsInfo", goodsInfo);
                            ((Activity) getContext()).startActivityForResult(intent, MyConstants.EXTRA_DESPOCIT);
                        } else if (goodsState == ON_SELL) {//在售
                            if (goodsInfo.hasBidDepositPayed == 1) {//缴纳
                                OfferPriceDialog offerPriceDialog = new OfferPriceDialog(getContext(), goodsInfo.hammerPrice, goodsInfo.bidIncrement, goodsInfo.getGoodsId());
                                offerPriceDialog.setOnRefreshListener(new OfferPriceDialog.OnRefreshListener() {

                                    @Override
                                    public void onRefresh() {
                                        if (onRefreshListener != null) {
                                            onRefreshListener.onRefresh();
                                        }
                                    }
                                });
                                offerPriceDialog.show();
                            } else {
                                Intent intent = new Intent(getContext(), DepositActivity.class);
                                intent.putExtra("goodsInfo", goodsInfo);
                                ((Activity) getContext()).startActivityForResult(intent, MyConstants.EXTRA_DESPOCIT);
                            }
                        }
                    }
                });
            }
        }
    }


    public class CountDownTime implements Runnable {

        private long residue;
        private TextView textView;
        private String goodsId;
        private GoodsInfoBean bean;

        public CountDownTime(long residue, TextView textView, String goodsId, GoodsInfoBean bean) {
            this.residue = residue;
            this.textView = textView;
            this.goodsId = goodsId;
            this.bean = bean;
        }

        @Override
        public void run() {
            residue -= 1000;
            if (residue > 0) {
                String residueTime = DateTimeUtils.getResidue(residue);
                textView.setText("距离开售" + residueTime);
                handler.postDelayed(this, 1000);
            } else {
                textView.setOnClickListener(new OnBuyNowClickListener(bean, goodsId));
                textView.setText("立即购买");
                handler.removeCallbacks(this);
            }
        }
    }
    public class BidStartTimer implements Runnable {

        private long deltime;
        private TextView textView;

        public BidStartTimer(long deltime,TextView textView) {
            this.deltime = deltime;
            this.textView=textView;
        }

        @Override
        public void run() {
            deltime -= 1000;
            if(deltime > 0){
                String shortstring = DateTimeUtils.getResidue(deltime);
                textView.setText("距离开拍" + shortstring);
                handler.postDelayed(this, 1000);
            } else {
                textView.setText("出价");
                Drawable drawable= getResources().getDrawable(R.drawable.auction_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView.setCompoundDrawables(drawable,null,null,null);
                textView.setOnClickListener(new OnAuctionBuyClick(textView,1));
                handler.removeCallbacks(this);
            }
        }
    }
    //下架
    public class OnShelfClickListener implements OnClickListener {

        private GoodsInfo goodsInfo;

        public OnShelfClickListener(GoodsInfo goodsInfo) {
            this.goodsInfo = goodsInfo;
        }

        @Override
        public void onClick(View v) {
            final TextDialog dialog = new TextDialog(getContext(), false);

            dialog.setContentText("您确定下架吗？");
            dialog.setLeftText("取消");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                }
            });
            dialog.setRightText("确定");
            dialog.setRightCall(new DialogCallback() {

                @Override
                public void Click() {
                    startRequestForChangGoodsState("1", goodsInfo); // 下架
                }
            });
            dialog.show();
        }
    }

    //编辑商品
    public class OnEditCommodityClickListener implements OnClickListener {

        private GoodsInfo goodsInfo;

        public OnEditCommodityClickListener(GoodsInfo goodsInfo) {
            this.goodsInfo = goodsInfo;
        }

        @Override
        public void onClick(View v) {
            CommonUtils.editGoodsInfo(getContext(), goodsInfo, MyConstants.GOODS_DETAILS_EDIT);

        }
    }

    public class OnJoinCampaignClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            FragmentManager manager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            new JoinCampaignDialog(getContext(),Integer.valueOf(goodsId)).show(manager,"");
        }
    }

    //删除商品
    public class OnDeleteCommodityClickListener implements OnClickListener {


        private GoodsInfo goodsInfo;

        public OnDeleteCommodityClickListener(GoodsInfo goodsInfo) {
            this.goodsInfo = goodsInfo;
        }

        @Override
        public void onClick(View v) {
            final TextDialog dialog = new TextDialog(getContext(), false);
            dialog.setContentText("您确定删除吗？");
            dialog.setLeftText("取消");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                }
            });
            dialog.setRightText("确定");
            dialog.setRightCall(new DialogCallback() {

                @Override
                public void Click() {
                    startRequestForChangGoodsState("2", goodsInfo); // 删除
                }
            });
            dialog.show();
        }
    }

    //立即购买
    public class OnBuyNowClickListener implements OnClickListener {

        private String goodsId;
        private GoodsInfoBean goodsInfoBean;

        public OnBuyNowClickListener(GoodsInfoBean goodsInfoBean, String goodsId) {
            this.goodsInfoBean = goodsInfoBean;
            this.goodsId = goodsId;
        }


        @Override
        public void onClick(View v) {
            if (!CommonPreference.isLogin()) {
                ActionUtil.loginAndToast(getContext());
                return;
            }
            if(ConfigUtil.isToVerify()){
                final TextDialog dialog = new TextDialog(context, false);
                dialog.setContentText("亲，您还未开通实名认证，1分钟完成认证后即可下单");
                dialog.setLeftText("取消");
                dialog.setRightColor(Color.parseColor("#2d8bff"));
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("去开通");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        Intent intent = new Intent(context, VerifiedActivity.class);
                        context.startActivity(intent);

                    }
                });
                dialog.show();
            }else {
                if (CommonPreference.getBooleanValue("first_buy", true)) {
                    Intent intent = new Intent(getContext(), MontageActivity.class);
                    intent.putExtra(MyConstants.EXTRA_MONTAGE, "first_buy");
                    getContext().startActivity(intent);
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).overridePendingTransition(0, 0);
                    } else {
                        throw new RuntimeException("context must be a Activity");
                    }
                    CommonPreference.setBooleanValue("first_buy", false);
                } else {
                    startRequestForOrderBuy(goodsInfoBean, goodsId);
                }
            }
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    private OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

}
