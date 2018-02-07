package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsInfoBean;
import com.huapu.huafen.chatim.activity.PrivateConversationActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alibaba.mtl.log.a.getContext;

/**
 * Created by admin on 2017/3/30.
 */

public class GoodsDetailBuyerBottom extends LinearLayout {

    private final static String TAG = GoodsDetailBuyerBottom.class.getSimpleName();
    @BindView(R2.id.tvContactTa)
    TextView tvContactTa;
    @BindView(R2.id.tvFavorable)
    TextView tvFavorable;
    private String recTranceId;
    private int recIndex;
    private String searchQuery;

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setRecTranceId(String recTranceId) {
        this.recTranceId = recTranceId;
    }

    public void setRecIndex(int recIndex) {
        this.recIndex = recIndex;
    }

    public GoodsDetailBuyerBottom(Context context) {
        this(context, null);
    }

    public GoodsDetailBuyerBottom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundColor(getContext().getResources().getColor(R.color.white));
        setOrientation(HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.goods_detail_bottom, this, true);
        ButterKnife.bind(this);
    }

    public void setData(final GoodsInfoBean bean, final String goodsId) {

        tvContactTa.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonPreference.isLogin()) {
                    if (bean == null || bean.getGoodsInfo() == null || bean.getUserInfo() == null) {
                        return;
                    }
                    // 开启私信会话
                    Intent intent = new Intent(getContext(), PrivateConversationActivity.class);
                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(bean.getGoodsInfo().getGoodsId()));
                    intent.putExtra(MyConstants.IM_PEER_ID, String.valueOf(bean.getUserInfo().getUserId()));
                    getContext().startActivity(intent);
                }else {
                    ActionUtil.loginAndToast(getContext());
                }
            }
        });
        tvFavorable.setSelected(bean.getGoodsInfo().getIsLike());
        tvFavorable.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getContext());
                    return;
                }
                startRequestForWantBuy(bean, goodsId, tvFavorable);
            }
        });
    }


    /**
     * 喜欢
     *
     * @param
     */
    private void startRequestForWantBuy(final GoodsInfoBean goodsInfoBean, String goodsId, final View view) {
        if (!CommonUtils.isNetAvaliable(getContext())) {
            ToastUtil.toast(getContext(), "请检查网络连接");
            return;
        }
        if (goodsInfoBean == null || goodsInfoBean.getGoodsInfo() == null) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("goodsId", goodsId);
        final boolean isLike = goodsInfoBean.getGoodsInfo().getIsLike();
        if (isLike) {
            params.put("type", "2");
        } else {
            params.put("type", "1");
        }
        if (!TextUtils.isEmpty(recTranceId)) {
            params.put("recTraceId", recTranceId);
        }

        if (recIndex != -1) {
            params.put("recIndex", String.valueOf(recIndex));
        }
        if (!TextUtils.isEmpty(searchQuery)) {
            params.put("searchQuery", searchQuery);
        }

        LogUtil.i(TAG, "params:" + params.toString());
        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.WANTBUY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, "喜欢onError:" + e.getMessage());
                view.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "喜欢:" + response);
                view.setEnabled(true);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (isLike) {
                            tvFavorable.setSelected(false);
                            goodsInfoBean.getGoodsInfo().setIsLike(false);
                            if (mOnFavStateChangedListener != null) {
                                mOnFavStateChangedListener.onChange(false);
                            }
                        } else {
                            tvFavorable.setSelected(true);
                            goodsInfoBean.getGoodsInfo().setIsLike(true);
                            if (goodsInfoBean.getGoodsInfo().getGoodsState() == 2) {
                                boolean isFirstLike = CommonPreference.getBooleanValue(CommonPreference.IS_FIRST_LIKE, false);
                                if (!isFirstLike) {
                                    CommonPreference.setBooleanValue(CommonPreference.IS_FIRST_LIKE, true);
                                    //TODO 第一次弹窗提示
//                                    initPopLike(layoutBtnBuyer2);
                                }
                            }

                            if (mOnFavStateChangedListener != null) {
                                mOnFavStateChangedListener.onChange(true);
                            }

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

    public interface OnFavStateChangedListener {
        void onChange(boolean isFav);
    }

    private OnFavStateChangedListener mOnFavStateChangedListener;

    public void setOnFavStateChangedListener(OnFavStateChangedListener onFavStateChangedListener) {
        this.mOnFavStateChangedListener = onFavStateChangedListener;
    }

}
