package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.DisplayTime;
import com.huapu.huafen.beans.Goods;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsValue;
import com.huapu.huafen.beans.JoinCampaign;
import com.huapu.huafen.callbacks.OnRequestRetryListener;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogDelete;
import com.huapu.huafen.dialog.JoinCampaignDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ShareHelper;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liang on 2016/10/26.
 */
public class ReleaseListAdapter extends CommonWrapper<ReleaseListAdapter.ReleaseListHolder> {

    private final static int REQUEST_CODE_RELEASE_LIST = 0x1111;
    private static final int REQUEST_CODE_RELEASE_CODE = 0x212;

    private Context mContext;
    private List<Goods> data = new ArrayList<Goods>();
    private Fragment fragment;

    public ReleaseListAdapter(Context context, List<Goods> data) {
        super();
        this.mContext = context;
        this.data = data;
    }

    public ReleaseListAdapter(Fragment fragment, List<Goods> data) {
        super();
        this.fragment = fragment;
        this.mContext = fragment.getActivity();
        this.data = data;
    }

    public void setData(List<Goods> data) {
        if (data == null) {
            data = new ArrayList<Goods>();
        }
        this.data = data;
    }

    public void addAll(List<Goods> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        this.data.addAll(data);
    }

    public boolean isEmpty() {
        return ArrayUtil.isEmpty(data);
    }

    @Override
    public ReleaseListHolder onCreateViewHolder(ViewGroup parent, int i) {
        ReleaseListHolder vh = new ReleaseListHolder(LayoutInflater.from(mContext).inflate(R.layout.item_listview_releaselist, parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(final ReleaseListHolder viewHolder, final int position) {
        if (ArrayUtil.isEmpty(data)) {
            return;
        }
        final Goods goods = data.get(position);
        if (goods == null || goods.getGoodsData() == null || goods.getAuditResult() == null) {
            return;
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goods.getAuditResult().getAuditStatus() == 4) {
                    return;
                }
                Intent intent = new Intent(mContext, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goods.getGoodsData().getGoodsId()));
                if (fragment != null) {
                    fragment.startActivityForResult(intent, REQUEST_CODE_RELEASE_LIST);
                } else if (mContext != null) {
                    ((BaseActivity) mContext).startActivityForResult(intent, REQUEST_CODE_RELEASE_LIST);
                }
            }
        });

        if (goods.getGoodsData().getGoodsImgs() != null && goods.getGoodsData().getGoodsImgs().size() > 0) {
            viewHolder.goodsImage.setImageURI(goods.getGoodsData().getGoodsImgs().get(0));
        }

        GoodsValue goodsValue = goods.getGoodsValue();

        if (goodsValue != null) {
            int lookCount = goodsValue.getLookCount();
            if (lookCount < 0) {
                viewHolder.tvPreview.setVisibility(View.GONE);
            } else {
                viewHolder.tvPreview.setVisibility(View.VISIBLE);
                viewHolder.tvPreview.setText(lookCount + "人浏览");
            }
        } else {
            viewHolder.tvPreview.setVisibility(View.GONE);
        }
        int price = goods.getGoodsData().getPrice();
        viewHolder.tvPrice.setText(Html.fromHtml(String.format(mContext.getString(R.string.price_tag), price)));

        String brand = goods.getGoodsData().getBrand();
        String goodsName = goods.getGoodsData().getName();
        String goodsNameDesc;
        if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
            String format = mContext.getString(R.string.goods_name_desc);
            goodsNameDesc = String.format(format, brand, goodsName);
        } else if (!TextUtils.isEmpty(brand) && TextUtils.isEmpty(goodsName)) {
            goodsNameDesc = brand;
        } else if (TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
            goodsNameDesc = goodsName;
        } else {
            goodsNameDesc = "";
        }
        viewHolder.tvGoodsName.setText(goodsNameDesc);

        if (goods.getAuditResult().getAuditStatus() == 1 || goods.getAuditResult().getAuditStatus() == 2) {
            viewHolder.tvLikeCount.setVisibility(View.VISIBLE);
            viewHolder.tvLikeCount.setText(String.valueOf(goods.getGoodsValue().getLikeCount()));
            viewHolder.tvLikeCount.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startForRequestWantBuy(position, viewHolder.tvLikeCount);
                }
            });

            final boolean isLike = goods.getGoodsData().getLiked();
            if (isLike) {
                viewHolder.tvLikeCount.setTextColor(Color.parseColor("#ffff6677"));
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.btn_item_like_select);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                viewHolder.tvLikeCount.setCompoundDrawables(drawable, null, null, null);
            } else {
                viewHolder.tvLikeCount.setTextColor(Color.parseColor("#888888"));
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.btn_item_like_normal);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                viewHolder.tvLikeCount.setCompoundDrawables(drawable, null, null, null);
            }
        } else {
            viewHolder.tvLikeCount.setVisibility(View.GONE);
            viewHolder.tvLikeCount.setOnClickListener(null);
        }
        if (goods.getAuditResult().getAuditStatus() == 5) {
            viewHolder.ivState.setSelected(true);
            viewHolder.ivState.setVisibility(View.VISIBLE);
        } else if (goods.getAuditResult().getAuditStatus() == 3 || goods.getAuditResult().getAuditStatus() == 4) {
            viewHolder.ivState.setSelected(false);
            viewHolder.ivState.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivState.setVisibility(View.GONE);
        }

        boolean flag = goods.getGoodsData().getFirstCateId() == 20;//一元专区
        int goodsState = goods.getGoodsData().getGoodsState();
        int auditStatus = goods.getAuditResult().getAuditStatus();
        if (CommonPreference.getUserInfo().getUserLevel() < 2 && flag && (goodsState == 1 || goodsState == 2) && (auditStatus == 1 || auditStatus == 2)) {
            viewHolder.tvBidCount.setVisibility(View.GONE);
            viewHolder.flAuctionIcon.setVisibility(View.GONE);
            viewHolder.tvAuctionPrice.setVisibility(View.GONE);//起拍价
            viewHolder.tvAuctionTime.setVisibility(View.GONE);//拍卖时间
            viewHolder.tvPrice.setVisibility(View.VISIBLE);//价格
            viewHolder.tvPastPrice.setVisibility(View.VISIBLE);//原价

            viewHolder.tvBtnDown.setVisibility(View.GONE);
            viewHolder.tvBtnEdit.setVisibility(View.GONE);
            viewHolder.tvBtnDel.setVisibility(View.VISIBLE);
            viewHolder.tvBtnDel.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
            viewHolder.tvBtnDel.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
            viewHolder.tvBtnShare.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
            viewHolder.tvBtnShare.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);
        } else {
            if (goods.getGoodsData().isAuction == 1) {
                viewHolder.flAuctionIcon.setVisibility(View.VISIBLE);
                viewHolder.tvAuctionPrice.setVisibility(View.VISIBLE);//起拍价
                viewHolder.tvPrice.setVisibility(View.GONE);//价格
                viewHolder.tvPastPrice.setVisibility(View.GONE);//原价
                viewHolder.ivMoreBtn.setVisibility(View.GONE);


                if (goodsState == 1 || goodsState == 2) {//在售
                    if (auditStatus == 1 || auditStatus == 2) {//审核通过（在售）
                        if (goods.getGoodsData().hasBidDepositPayed == 1) {//有人缴纳保证金
                            viewHolder.tvBtnDel.setVisibility(View.GONE);
                            viewHolder.tvBtnDown.setVisibility(View.GONE);
                            viewHolder.tvBtnEdit.setVisibility(View.GONE);
                            viewHolder.tvBtnShare.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnDel.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                            viewHolder.tvBtnDel.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                            viewHolder.tvBtnDown.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                            viewHolder.tvBtnDown.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                            viewHolder.tvBtnEdit.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                            viewHolder.tvBtnEdit.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                            viewHolder.tvBtnShare.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
                            viewHolder.tvBtnShare.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);
                        } else {
                            viewHolder.tvBtnDel.setVisibility(View.GONE);
                            viewHolder.tvBtnDown.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnEdit.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnShare.setVisibility(View.VISIBLE);
                            viewHolder.tvBtnDel.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                            viewHolder.tvBtnDel.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                            viewHolder.tvBtnDown.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                            viewHolder.tvBtnDown.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                            viewHolder.tvBtnEdit.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                            viewHolder.tvBtnEdit.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                            viewHolder.tvBtnShare.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
                            viewHolder.tvBtnShare.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);
                        }

                        viewHolder.tvAuctionTime.setVisibility(View.VISIBLE);//拍卖时间


                        if (goodsState == 1) {//在售
                            viewHolder.tvAuctionTime.setText(DateTimeUtils.getStringDate(goods.getGoodsData().bidEndTime, DateTimeUtils.MM_DD_HH_MM) + "结束");
                            viewHolder.tvBidCount.setVisibility(View.VISIBLE);
                            viewHolder.tvBidCount.setText(goods.getGoodsData().bidCount + "人出价");
                            if (goods.getGoodsData().bidder > 0) {
                                String des = String.format(mContext.getString(R.string.auction_price), "当前价", goods.getGoodsData().hammerPrice);
                                viewHolder.tvAuctionPrice.setText(Html.fromHtml(des));

                            } else {
                                String des = String.format(mContext.getString(R.string.auction_price), "起拍价", goods.getGoodsData().hammerPrice);
                                viewHolder.tvAuctionPrice.setText(Html.fromHtml(des));
                            }
                        } else {//预售
                            viewHolder.tvAuctionTime.setText(DateTimeUtils.getStringDate(goods.getGoodsData().bidStartTime, DateTimeUtils.MM_DD_HH_MM) + "开拍");
                            viewHolder.tvBidCount.setVisibility(View.GONE);
                            String des = String.format(mContext.getString(R.string.auction_price), "起拍价", goods.getGoodsData().hammerPrice);
                            viewHolder.tvAuctionPrice.setText(Html.fromHtml(des));

                        }
                    } else {//待审核
                        viewHolder.tvBidCount.setVisibility(View.GONE);
                        viewHolder.tvAuctionTime.setVisibility(View.GONE);//拍卖时间
                        viewHolder.tvBtnDel.setVisibility(View.VISIBLE);
                        viewHolder.tvBtnDown.setVisibility(View.GONE);
                        if (auditStatus == 4) {
                            viewHolder.tvBtnEdit.setVisibility(View.GONE);
                        } else {
                            viewHolder.tvBtnEdit.setVisibility(View.VISIBLE);
                        }
                        viewHolder.tvBtnShare.setVisibility(View.GONE);

                        if (viewHolder.tvBtnEdit.getVisibility() == View.VISIBLE) {
                            viewHolder.tvBtnDel.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                            viewHolder.tvBtnDel.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                            viewHolder.tvBtnEdit.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
                            viewHolder.tvBtnEdit.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);
                        } else {
                            viewHolder.tvBtnDel.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
                            viewHolder.tvBtnDel.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);
                        }
                    }

                } else if (goodsState == 5) {//下架
                    viewHolder.tvAuctionTime.setVisibility(View.GONE);//拍卖时间
                    viewHolder.tvBidCount.setVisibility(View.GONE);
                    viewHolder.tvBtnDel.setVisibility(View.VISIBLE);
                    viewHolder.tvBtnDown.setVisibility(View.GONE);
                    viewHolder.tvBtnEdit.setVisibility(View.VISIBLE);
                    viewHolder.tvBtnShare.setVisibility(View.GONE);

                    viewHolder.tvBtnDel.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                    viewHolder.tvBtnDel.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                    viewHolder.tvBtnEdit.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
                    viewHolder.tvBtnEdit.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);


                    viewHolder.tvAuctionPrice.setVisibility(View.GONE);//起拍价
                    viewHolder.tvPrice.setVisibility(View.VISIBLE);//价格
                    viewHolder.tvPastPrice.setVisibility(View.GONE);//原价

                    if (goods.getGoodsData().bidder > 0) {
                        String des = String.format(mContext.getString(R.string.auction_price), "成交价", goods.getGoodsData().hammerPrice);
                        viewHolder.tvPrice.setText(Html.fromHtml(des));
                    } else {
                        String des = String.format(mContext.getString(R.string.auction_price), "起拍价", goods.getGoodsData().hammerPrice);
                        viewHolder.tvPrice.setText(Html.fromHtml(des));
                    }
                }
            } else {
                viewHolder.tvBidCount.setVisibility(View.GONE);
                viewHolder.flAuctionIcon.setVisibility(View.GONE);
                viewHolder.tvAuctionPrice.setVisibility(View.GONE);//起拍价
                viewHolder.tvAuctionTime.setVisibility(View.GONE);//拍卖时间
                viewHolder.tvPrice.setVisibility(View.VISIBLE);//价格
                viewHolder.tvPastPrice.setVisibility(View.VISIBLE);//原价
                int btnCount = 4;
                if ((goodsState == 1 || goodsState == 2) && (auditStatus == 1 || auditStatus == 2)) { // 不可删除
                    viewHolder.tvBtnDel.setVisibility(View.GONE);
                    btnCount--;
                } else {
                    viewHolder.tvBtnDel.setVisibility(View.VISIBLE);
                }
                if (goodsState == 5 || auditStatus == 3 || auditStatus == 4 || auditStatus == 5) { // 不可下架
                    viewHolder.tvBtnDown.setVisibility(View.GONE);
                    btnCount--;
                } else {
                    viewHolder.tvBtnDown.setVisibility(View.VISIBLE);
                }
                if (auditStatus == 4) { // 不可编辑
                    viewHolder.tvBtnEdit.setVisibility(View.GONE);
                    btnCount--;
                } else {
                    viewHolder.tvBtnEdit.setVisibility(View.VISIBLE);
                }
                if (goodsState == 5 || auditStatus == 3 || auditStatus == 4 || auditStatus == 5) { // 不可分享
                    viewHolder.tvBtnShare.setVisibility(View.GONE);
                    btnCount--;
                } else {
                    viewHolder.tvBtnShare.setVisibility(View.VISIBLE);
                }

                viewHolder.tvBtnDel.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                viewHolder.tvBtnDel.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                viewHolder.tvBtnDown.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                viewHolder.tvBtnDown.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                viewHolder.tvBtnEdit.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                viewHolder.tvBtnEdit.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                viewHolder.tvBtnShare.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                viewHolder.tvBtnShare.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                switch (btnCount) {
                    case 1:
                        viewHolder.tvBtnDel.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
                        viewHolder.tvBtnDel.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);
                        break;
                    case 2:
                        viewHolder.tvBtnEdit.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
                        viewHolder.tvBtnEdit.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);
                        break;
                    case 3:
                        if (viewHolder.tvBtnShare.getVisibility() == View.GONE) {
                            viewHolder.tvBtnShare.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                            viewHolder.tvBtnShare.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                            viewHolder.tvBtnEdit.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
                            viewHolder.tvBtnEdit.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);
                        } else {
                            viewHolder.tvBtnEdit.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
                            viewHolder.tvBtnEdit.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
                            viewHolder.tvBtnDown.setVisibility(View.GONE);
                            viewHolder.tvBtnShare.setVisibility(View.GONE);
                            viewHolder.ivMoreBtn.setVisibility(View.VISIBLE);
                            viewHolder.ivMoreBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<String> strs = new ArrayList<>();
                                    strs.add("下架");
                                    strs.add("分享");
                                    final DialogDelete dialogDelete = new DialogDelete(mContext,strs);
                                    AdapterView.OnItemClickListener onItemCLickListener = new AdapterView.OnItemClickListener(){
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                                            dialogDelete.dismiss();
                                            switch (p){
                                                case 0: //下架
                                                    final TextDialog dialog = new TextDialog(mContext, false);
                                                    dialog.setContentText("您确定下架商品吗？");
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
                                                            //下架
                                                            startRequestForChangeGoodsState(position, 1, goods.getGoodsData());
                                                        }
                                                    });
                                                    dialog.show();
                                                    break;
                                                case 1://分享
                                                    ShareHelper.shareGoods(mContext, goods.getGoodsData());
                                                    break;
                                            }
                                        }
                                    };
                                    dialogDelete.setOnItemClickListener(onItemCLickListener);
                                    dialogDelete.show(((FragmentActivity)mContext).getFragmentManager(),"");
                                }
                            });
                            if (goods.getHasCandidateCampaigns() != 0 && goods.getGoodsData().isAuction != 1 ){
                                //显示参加活动
                                viewHolder.tvJoinCampaign.setTextColor(mContext.getResources().getColor(R.color.base_pink_light));
                                viewHolder.tvJoinCampaign.setBackgroundResource(R.drawable.text_white_round_pink_light_stroke_bg);
                                viewHolder.tvJoinCampaign.setVisibility(View.VISIBLE);
                                viewHolder.tvJoinCampaign.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FragmentManager manager = ((FragmentActivity)mContext).getSupportFragmentManager();
                                        new JoinCampaignDialog(mContext,goods.getGoodsData().getGoodsId())
                                                .show(manager,"");
                                    }
                                });
                            }else {
                                //隐藏参加活动
                                viewHolder.tvJoinCampaign.setVisibility(View.GONE);

                            }
                        }
                        break;
                }
            }

        }

        if (TextUtils.isEmpty(goods.getAuditResult().getReason())) {
            viewHolder.layoutReason.setVisibility(View.GONE);
            viewHolder.viewReason.setVisibility(View.GONE);
        } else {
            viewHolder.layoutReason.setVisibility(View.VISIBLE);
            viewHolder.viewReason.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(goods.getAuditResult().getAction())) {
                viewHolder.layoutActionReason.setVisibility(View.GONE);
                viewHolder.tvReason.setText("原因：" + goods.getAuditResult().getReason());
            } else {
                viewHolder.tvReason.setText(goods.getAuditResult().getReason());
                viewHolder.layoutActionReason.setVisibility(View.VISIBLE);
                viewHolder.layoutActionReason.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActionUtil.dispatchAction(mContext, goods.getAuditResult().getAction(), goods.getAuditResult().getTarget());
                    }
                });
            }
        }
        viewHolder.tvBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextDialog dialog = new TextDialog(mContext, false);
                dialog.setContentText("您确定删除商品吗？");
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
                        //删除
                        startRequestForChangeGoodsState(position, 2, goods.getGoodsData());
                    }
                });
                dialog.show();
            }
        });
        viewHolder.tvBtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextDialog dialog = new TextDialog(mContext, false);
                dialog.setContentText("您确定下架商品吗？");
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
                        //下架
                        startRequestForChangeGoodsState(position, 1, goods.getGoodsData());
                    }
                });
                dialog.show();
            }
        });
        viewHolder.tvBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextDialog dialog = new TextDialog(mContext, false);
                dialog.setContentText("您确定编辑商品吗？");
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
                        //编辑
                        CommonUtils.editGoodsData((Activity) mContext, goods.getGoodsData(), REQUEST_CODE_RELEASE_CODE);
                    }
                });
                dialog.show();
            }
        });
        viewHolder.tvBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareHelper.shareGoods(mContext, goods.getGoodsData());
            }
        });
        viewHolder.layoutReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionUtil.dispatchAction(mContext, goods.getAuditResult().getAction(), goods.getAuditResult().getTarget());
            }
        });

        if (goods.getGoodsData().isAuction == 1) {//拍卖商品 不展示浇水
            viewHolder.rlWatering.setVisibility(View.GONE);
            viewHolder.tvWater.setVisibility(View.GONE);
        } else {
            final DisplayTime displayTime = goods.getDisplayTime();
            if (displayTime != null) {
                int displayTimeRemaining = displayTime.getRemain();
                int displayTimeTotal = displayTime.getTotal();
                if (displayTimeRemaining > 0 && displayTimeTotal > 0) {
                    viewHolder.rlWatering.setVisibility(View.VISIBLE);
                    viewHolder.tvWater.setVisibility(View.VISIBLE);
                    float time = ((float) displayTimeRemaining) / 24;
                    int var = (int) Math.ceil(time);

                    if (var < 8) {
                        Spanned var1 = Html.fromHtml(String.format(mContext.getString(R.string.goods_auto_off_sale_desc_red),
                                var));
                        viewHolder.tvWaterDesc.setText(var1);
                    } else {
                        viewHolder.tvWaterDesc.setText(String.format(mContext.getString(R.string.goods_auto_off_sale_desc),
                                var));
                    }


                    viewHolder.rlWatering.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            DisplayTime.BtnHelp helpBtn = displayTime.getHelpBtn();
                            if (helpBtn != null) {
                                ActionUtil.dispatchAction(mContext, helpBtn.getAction(), helpBtn.getTarget());
                            }
                        }
                    });

                    if (displayTime.getRenewable() == 0) {
                        viewHolder.tvWater.setBackgroundResource(R.drawable.water_grey_bg);
                        viewHolder.tvWater.setTextColor(Color.parseColor("#FFFFFF"));
                        Drawable drawable = mContext.getResources().getDrawable(R.drawable.water_drop_white);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.tvWater.setCompoundDrawables(drawable, null, null, null);
                    } else {
                        viewHolder.tvWater.setBackgroundResource(R.drawable.water_blue_bg);
                        viewHolder.tvWater.setTextColor(Color.parseColor("#FFFFFF"));
                        Drawable drawable = mContext.getResources().getDrawable(R.drawable.water_drop_white);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.tvWater.setCompoundDrawables(drawable, null, null, null);
                    }
                    viewHolder.tvWater.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (displayTime.getRenewable() != 0) {
                                startRequestForWatering(goods);
                            }

                        }
                    });
                } else {
                    viewHolder.rlWatering.setVisibility(View.GONE);
                    viewHolder.tvWater.setVisibility(View.GONE);
                }

            } else {
                viewHolder.rlWatering.setVisibility(View.GONE);
                viewHolder.tvWater.setVisibility(View.GONE);
            }

        }


    }

    private void startForRequestWantBuy(final int position, final View view) {
        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        if (data == null) {
            return;
        }
        final Goods item = data.get(position);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("goodsId", String.valueOf(item.getGoodsData().getGoodsId()));
        final boolean isLike = item.getGoodsData().getLiked();
        if (isLike) {
            params.put("type", "2");
        } else {
            params.put("type", "1");
        }
        LogUtil.i("liang", "params:" + params.toString());
        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.WANTBUY, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        view.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response) {
                        view.setEnabled(true);
                        LogUtil.i("liang", "喜欢:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (isLike) {
                                    item.getGoodsData().setLiked(false);
                                    if (item.getGoodsValue().getLikeCount() > 0) {
                                        item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() - 1);
                                    }
                                    notifyWrapperDataSetChanged();
                                } else {
                                    item.getGoodsData().setLiked(true);
                                    item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() + 1);
                                    notifyWrapperDataSetChanged();
                                }

                            } else {
                                CommonUtils.error(baseResult, (BaseActivity) mContext, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });

    }

    /**
     * 改变商品状态
     *
     * @param type
     * 1.下架
     * 2.删除
     */
    final HashMap<String, String> params = new HashMap<String, String>();

    private void startRequestForChangeGoodsState(final int position, final int type, final GoodsData goodsData) {
        if (goodsData == null) {
            return;
        }
        if (type == 1) {
            if ((goodsData.getPrivileges() & MyConstants.P_DOWN) == 0) {
                final TextDialog dialog = new TextDialog(mContext, false);
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
        } else if (type == 2) {
            if ((goodsData.getPrivileges() & MyConstants.P_EDIT) == 0) {
                final TextDialog dialog = new TextDialog(mContext, false);
                dialog.setContentText("商品已参与了活动，不可编辑哦~");
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

        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(mContext);

        params.put("goodId", String.valueOf(goodsData.getGoodsId()));
        params.put("stateType", String.valueOf(type));
        params.put("campaignId", String.valueOf(goodsData.getCampaignId()));
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.CHANGGOODSSTATE, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        boolean flag = CommonUtils.parseEvent(mContext, response, new OnRequestRetryListener() {

                            @Override
                            public void onRetry() {
                                params.put("confirmed", "1");
                                startRequestForChangeGoodsState(position, type, goodsData);
                            }
                        });
                        if (flag) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                onItemClickListener.onDownAndDeleteItemClick();
//                                ToastUtil.toast(mContext, "操作成功");
//                                data.remove(position);
//                                getWrapperAdapter().notifyDataSetChanged();
                            } else {
                                CommonUtils.error(baseResult, (Activity) mContext, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });

    }


    private void startRequestForWatering(final Goods goods) {

        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("goodsId", String.valueOf(goods.getGoodsData().getGoodsId()));

        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.WATER, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "浇水:" + response);

                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        DisplayTime displayTime = JSON.parseObject(baseResult.obj, DisplayTime.class);
                        goods.setDisplayTime(displayTime);
                        onItemClickListener.onWaterItemClick();
                        notifyWrapperDataSetChanged();
//                        Intent intent = new Intent(mContext, WaterSuccessfulActivity.class);
//                        fragment.startActivity(intent);
//                        fragment.getActivity().overridePendingTransition(0, 0);
                    } else {
                        //TODO
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ReleaseListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvWaterDesc)
        TextView tvWaterDesc;
        @BindView(R.id.rlWatering)
        RelativeLayout rlWatering;
        @BindView(R.id.goodsImage)
        SimpleDraweeView goodsImage;
        @BindView(R.id.flGoods)
        FrameLayout flGoods;
        @BindView(R.id.tvGoodsName)
        TextView tvGoodsName;
        @BindView(R.id.tvPreview)
        TextView tvPreview;
        @BindView(R.id.tvAuctionPrice)
        TextView tvAuctionPrice;
        @BindView(R.id.tvPrice)
        TextView tvPrice;
        @BindView(R.id.tvPastPrice)
        TextView tvPastPrice;
        @BindView(R.id.tvAuctionTime)
        TextView tvAuctionTime;
        @BindView(R.id.tvLikeCount)
        TextView tvLikeCount;
        @BindView(R.id.ivState)
        ImageView ivState;
        @BindView(R.id.tvReason)
        TextView tvReason;
        @BindView(R.id.layoutActionReason)
        LinearLayout layoutActionReason;
        @BindView(R.id.layoutReason)
        RelativeLayout layoutReason;
        @BindView(R.id.viewReason)
        View viewReason;
        @BindView(R.id.tvWater)
        TextView tvWater;
        @BindView(R.id.tvBtnDel)
        TextView tvBtnDel;
        @BindView(R.id.tvBtnDown)
        TextView tvBtnDown;
        @BindView(R.id.tvBtnEdit)
        TextView tvBtnEdit;
        @BindView(R.id.tvBtnShare)
        TextView tvBtnShare;
        @BindView(R.id.flAuctionIcon)
        FrameLayout flAuctionIcon;
        @BindView(R.id.tvBidCount)
        TextView tvBidCount;
        @BindView(R.id.ivMoreBtn)
        ImageView ivMoreBtn;
        @BindView(R.id.tvJoinCampaign)
        TextView tvJoinCampaign;

        public ReleaseListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        //        void onItemClick(CommentListBean comment);
        void onWaterItemClick();

        void onDownAndDeleteItemClick();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
