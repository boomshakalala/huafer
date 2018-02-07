package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CommentsDetailAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CommentDetailData;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.OrderData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.DashLineView;
import com.huapu.huafen.views.HLoadingStateView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by admin on 2016/11/10.
 */
public class CommentsDetailActivity extends BaseActivity {

    private static final String TAG = CommentsDetailActivity.class.getSimpleName();
    private static final int REQUEST_CODE_FOR_COMMENT_EIDT = 0x1211;
    private RecyclerView commentRecyclerView;
    private CommentsDetailAdapter recyclerViewAdapter;
    private TextView tvReplied;
    private HLoadingStateView loadingStateView;
    private long orderId;

    private long originId;
    private View headerView;
    private DashLineView dlvGoodsName;
    private SimpleDraweeView ivGoodPic;
    private View layoutGoods;
    private TextView tvDesc, tvPrice, tvPastPrice, tvFreeDelivery;
    private LinearLayout goodsLayout;
    private CommentDetailData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_detail_layout);

        Intent intent = getIntent();
        if (intent.hasExtra(MyConstants.EXTRA_COMMENT_ID)) {
            orderId = intent.getLongExtra(MyConstants.EXTRA_COMMENT_ID, 0);
        }

        if (intent.hasExtra(MyConstants.EXTRA_FROM_USER_ID)) {
            originId = intent.getLongExtra(MyConstants.EXTRA_FROM_USER_ID, 0);
        }

        if (orderId <= 0) {
            final TextDialog dialog = new TextDialog(this);
            dialog.setContentText("无法获取评论详情");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    CommentsDetailActivity.this.finish();
                }
            });
            dialog.show();
            return;
        }

        initView();

        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        startRequestForCommentList();
    }

    private void initView() {
        setTitleString("评价详情");
        commentRecyclerView = (RecyclerView) findViewById(R.id.commentRecyclerView);
        loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
        tvReplied = (TextView) findViewById(R.id.tvReplied);
        tvReplied.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentRecyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new CommentsDetailAdapter(this);
        headerView = LayoutInflater.from(this).inflate(R.layout.comments_detail_header_new, null, false);
        dlvGoodsName = (DashLineView) headerView.findViewById(R.id.dlvGoodsName);
        ivGoodPic = (SimpleDraweeView) headerView.findViewById(R.id.ivGoodPic);
        layoutGoods = headerView.findViewById(R.id.layoutGoods);
        layoutGoods.setOnClickListener(this);
        tvDesc = (TextView) headerView.findViewById(R.id.tvDesc);
        goodsLayout = (LinearLayout) headerView.findViewById(R.id.goodsLayout);
        tvPrice = (TextView) headerView.findViewById(R.id.tvPrice);
        tvPastPrice = (TextView) headerView.findViewById(R.id.tvPastPrice);
        tvFreeDelivery = (TextView) headerView.findViewById(R.id.tvFreeDelivery);

        commentRecyclerView.setAdapter(recyclerViewAdapter.getWrapperAdapter());
    }


    private void startRequestForCommentList() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", String.valueOf(orderId));
        params.put("originId", String.valueOf(originId));
        LogUtil.e(TAG, "param:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GET_ORDER_COMMENTS_DETAIL, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i(TAG, "onError：" + e.getMessage());
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }

            @Override
            public void onResponse(String response) {
                try {
                    LogUtil.i(TAG, "订单评价：" + response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            data = JSON.parseObject(baseResult.obj, CommentDetailData.class);
                            initData(data);
                        }
                    } else {
                        CommonUtils.error(baseResult, CommentsDetailActivity.this, "");
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "crush..." + e.getMessage());
                }
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }
        });
    }


    private void initHeader(CommentDetailData data) {
        final GoodsData goodsData = data.getGoodsData();

        if (goodsData != null) {
            dlvGoodsName.setData(goodsData.getBrand(), goodsData.getName());
            List<String> list = goodsData.getGoodsImgs();
            if (!ArrayUtil.isEmpty(list)) {
                String url = list.get(0);

                ImageLoader.resizeSmall(ivGoodPic, url, 1);
            }

            final OrderData orderData = data.getOrderData();

            if (orderData != null) {
                goodsLayout.setVisibility(GONE);
                tvDesc.setVisibility(VISIBLE);
                long currentMillions = orderData.getReceiveTime();
                if (currentMillions > 0) {
                    String time = DateTimeUtils.getYearMonthDayHourMinuteSecond(currentMillions);
                    String timeDes = String.format(getString(R.string.deal_successful_time), time);
                    tvDesc.setText(timeDes);
                }

                long myUserId = CommonPreference.getUserId();
                long buyerId = orderData.getBuyerId();
                long sellerId = orderData.getSellerId();
                boolean flag = myUserId > 0 && buyerId > 0 && sellerId > 0;
                if (flag && (myUserId == buyerId || myUserId == sellerId)) {
                    layoutGoods.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CommentsDetailActivity.this, OrderDetailActivity.class);
                            intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderData.getOrderId());
                            startActivity(intent);
                        }
                    });
                } else {
                    layoutGoods.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CommentsDetailActivity.this, GoodsDetailsActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goodsData.getGoodsId()));
                            startActivity(intent);
                        }
                    });
                }

            } else {
                goodsLayout.setVisibility(VISIBLE);
                tvDesc.setVisibility(GONE);
                int price = goodsData.getPrice();
                int pastPrice = goodsData.getPastPrice();
                tvPrice.setText("¥" + String.valueOf(price));
                if (pastPrice < 0) {
                    tvPastPrice.setVisibility(GONE);
                } else {
                    tvPastPrice.setVisibility(VISIBLE);
                    tvPastPrice.setText("¥" + String.valueOf(pastPrice));
                }

                tvPastPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

                //邮费
                boolean isFreeDelivery = goodsData.getIsFreeDelivery();
                if (isFreeDelivery) {
                    tvFreeDelivery.setText("包邮");
                    tvFreeDelivery.setVisibility(VISIBLE);
                } else {
                    tvFreeDelivery.setVisibility(VISIBLE);
                    if (goodsData.getPostage() != 0) {
                        CommonUtils.setPriceSizeData(tvFreeDelivery, "邮费", goodsData.getPostage());
                    } else {
                        tvFreeDelivery.setText("运费待议");
                    }
                }
                layoutGoods.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CommentsDetailActivity.this, GoodsDetailsActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goodsData.getGoodsId()));
                        startActivity(intent);
                    }
                });
            }

            recyclerViewAdapter.addHeaderView(headerView);
        }
    }

    private void initData(CommentDetailData data) {
        OrderData orderData = data.getOrderData();
        ArrayList<CommentDetailData.Comments> comments = data.getComments();
        initHeader(data);
        recyclerViewAdapter.setData(comments);

        if (orderData != null &&
                !ArrayUtil.isEmpty(comments) && comments.size() == 1 &&
                comments.get(0).getUserData().getUserId() != CommonPreference.getUserId()) {//展示

            tvReplied.setVisibility(View.VISIBLE);
        } else {
            tvReplied.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvReplied) {
            if (data == null || data.getGoodsData() == null || data.getOrderData() == null) {
                return;
            }
            GoodsData goodsData = data.getGoodsData();
            OrderData orderData = data.getOrderData();
            Intent intent = new Intent();
            intent.setClass(this, CommentEditReplyActivity.class);
            intent.putExtra(MyConstants.EXTRA_COMMENT_ID, orderId);
            intent.putExtra(MyConstants.EXTRA_GOODS_DATA, goodsData);
            intent.putExtra(MyConstants.EXTRA_ORDER_DATA, orderData);
            startActivityForResult(intent, REQUEST_CODE_FOR_COMMENT_EIDT);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOR_COMMENT_EIDT) {
                if (data != null) {
                    boolean commentSuccessful = data.getBooleanExtra("commentSuccessful", false);
                    if (commentSuccessful) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }
        }


    }
}