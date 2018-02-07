package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.SelectedCoverActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.RecommendCoverBean;
import com.huapu.huafen.beans.SendSuccessEvent;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qwe on 2017/7/31.
 */

public class SelectedCoverAdapter extends AbstractRecyclerAdapter {

    private int HEADER_TYPE_TWO = 1000;

    private int NORMAL_ITEM_TYPE = HEADER_TYPE_TWO + 1;

    private List<RecommendCoverBean.Background.Item> itemList;

    private String selectedBackGroud = "";

    private boolean isFlower;


    public SelectedCoverAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
    }

    public void setFlower(boolean flower) {
        this.isFlower = flower;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (null != itemList && itemList.size() > 0) {
            count += itemList.size();
        }

        count += 1;
        if (mIsFooterEnable) count++;
        if (mIsHeaderEnable) count++;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int headerPosition = 0;

        if (headerPosition == position && mIsHeaderEnable && null != headerView) {
            return TYPE_HEADER;
        }

        if (position == 1) {
            return HEADER_TYPE_TWO;
        }
        return NORMAL_ITEM_TYPE;
    }

    public void setData(List<RecommendCoverBean.Background.Item> list, String selectedBackGroud) {
        this.itemList = list;
        this.selectedBackGroud = selectedBackGroud;
        notifyDataSetChanged();
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        if (viewType == HEADER_TYPE_TWO) {
            return new HeaderTwoHolder(LayoutInflater.from(context).inflate(R.layout.header_two_star_region, parent, false));
        } else {
            return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item_adapter_cover, parent, false));
        }

    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, final boolean isItem) {
        try {
            if (holder instanceof HeaderTwoHolder) {
                HeaderTwoHolder headerTwoHolder = (HeaderTwoHolder) holder;
                headerTwoHolder.seeAll.setVisibility(View.GONE);
                if (position == 1) {
                    headerTwoHolder.headerTwoText.setText("推荐");
                    headerTwoHolder.rightIcon.setVisibility(View.INVISIBLE);
                }

            } else if (holder instanceof ItemViewHolder) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                final RecommendCoverBean.Background.Item item = itemList.get(position - 2);

                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayMap<String, String> arrayMap = new ArrayMap<>();
                        arrayMap.put("backgroundUrl", item.url);
                        arrayMap.put("mediaId", item.mediaId);
                        arrayMap.put("articleBackground", item.url);
                        String requestUrl;
                        if (isFlower) {
                            requestUrl = MyConstants.SETPREFERENCES;
                        } else {
                            requestUrl = MyConstants.UPDATE_BG;
                        }
                        OkHttpClientManager.postAsyn(requestUrl, arrayMap, new OkHttpClientManager.StringCallback() {

                            @Override
                            public void onError(Request request, Exception e) {

                            }

                            @Override
                            public void onResponse(String response) {
                                BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                                if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                    ToastUtil.toast(context, "恭喜您，成功更换封面！");
                                    SendSuccessEvent eventSuccess = new SendSuccessEvent();
                                    eventSuccess.isSuccess = true;
                                    EventBus.getDefault().post(eventSuccess);
                                    if (context instanceof SelectedCoverActivity) {
                                        SelectedCoverActivity activity = (SelectedCoverActivity) context;
                                        activity.initData();
                                    }
                                }

                            }
                        });
                    }
                });

                int tenDp = context.getResources().getDimensionPixelSize(R.dimen.flower_space_ten);

                int screenWidth = CommonUtils.getScreenWidth();
                int width = (screenWidth - (tenDp * 3)) / 2;
                int height = width * 162 / 340;

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) itemViewHolder.simpleDraweeView.getLayoutParams();
                lp.height = height;
                lp.width = width;

                if ((position - 2) % 2 == 0) {
                    lp.setMargins(tenDp, tenDp, 0, 0);
                } else {
                    lp.setMargins(tenDp / 2, tenDp, tenDp, 0);
                }

                if (!TextUtils.isEmpty(selectedBackGroud) && item.url.equals(selectedBackGroud)) {
                    itemViewHolder.selectedImage.setVisibility(View.VISIBLE);
                } else {
                    itemViewHolder.selectedImage.setVisibility(View.GONE);
                }

                itemViewHolder.simpleDraweeView.setLayoutParams(lp);

                float ar = (float) width / (float) height;
                ImageLoader.resizeMiddle(itemViewHolder.simpleDraweeView, item.url, ar);
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == TYPE_FOOTER || type == TYPE_HEADER || type == HEADER_TYPE_TWO) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    static final class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.simpleDraweeView)
        SimpleDraweeView simpleDraweeView;
        @BindView(R.id.selectedImage)
        ImageView selectedImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    static final class HeaderTwoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.headerTwoText)
        TextView headerTwoText;
        @BindView(R.id.right_icon)
        ImageView rightIcon;
        @BindView(R.id.seeAll)
        TextView seeAll;
        @BindView(R.id.space)
        View space;

        public HeaderTwoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
