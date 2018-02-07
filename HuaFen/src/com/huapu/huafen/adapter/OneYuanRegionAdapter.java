package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.PastOneRankingActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.WebViewActivity2;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CollectionData;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.OneYuanCounter;
import com.huapu.huafen.beans.OneYuanEvent;
import com.huapu.huafen.beans.OneYuanFilter;
import com.huapu.huafen.beans.OneYuanMore;
import com.huapu.huafen.beans.OneYuanRegionResult;
import com.huapu.huafen.beans.OneYuanTitle;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.fragment.RecommendListFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.ClassBannerView;
import com.huapu.huafen.views.CommonPriceTagView;
import com.huapu.huafen.views.CommonTitleViewSmall;
import com.huapu.huafen.views.CounterView;
import com.huapu.huafen.views.CounterViewNew;
import com.huapu.huafen.views.UserView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/19.
 */

public class OneYuanRegionAdapter extends CommonWrapper<RecyclerView.ViewHolder> {

    private final static Integer res = R.drawable.empty_refund;
    private final static String EXTRA_HOT_GOODS = "hotGoods";
    private final static String EXTRA_GOODS = "goods";

    private Context context;
    private List<Object> data;
    private boolean isGrid;

    private final static int SPAN_LIST = 2;
    private final static int SPAN_GRID = 1;

    public final static int FULL_SPAN = 2;
    public int headerCount;


    public OneYuanRegionAdapter(Context context) {
        this.context = context;
    }

    private enum ItemType {
        HEADER,
        TITLE,
        HOT_MORE,
        FILTER,
        COUNT_DOWN,
        EVENT,
        HOT_GOODS,
        GRID,
        EMPTY,;
    }

    public void setData(OneYuanRegionResult result, String cat2, String orderBy) {
        if (result != null && result.obj != null) {
            headerCount = 0;
            data = new ArrayList<>();
            BannerData banner = CommonPreference.getOneEvent();
            if (banner != null) {
                data.add(banner);
                headerCount++;
            }
            if (result.obj.startTime > 0 || result.obj.endTime > 0) {
                OneYuanCounter counter = new OneYuanCounter();
                counter.startTime = result.obj.startTime;
                counter.endTime = result.obj.endTime;
                counter.eventId = result.obj.eventId;
                data.add(counter);//倒计时
                headerCount++;
                if (result.obj.endTime <= System.currentTimeMillis()) {
                    isRefresh = false;
                } else {
                    isRefresh = true;
                }
            }

            if (!TextUtils.isEmpty(result.obj.event) && result.obj.eventId != 0) {
                OneYuanEvent event = new OneYuanEvent();
                event.event = result.obj.event;
                event.eventId = result.obj.eventId;
                data.add(event);
                headerCount++;
            }

            if (!ArrayUtil.isEmpty(result.obj.hotGoods)) {//热门商品
                OneYuanTitle title = new OneYuanTitle();
                data.add(title);
                headerCount++;

                int[] drawables = new int[]{R.drawable.hot_goods_first, R.drawable.one_yuan_second, R.drawable.one_yuan_third};
                for (int i = 0; i < result.obj.hotGoods.size(); i++) {
                    Item tmp = result.obj.hotGoods.get(i);
                    tmp.extra = EXTRA_HOT_GOODS;
                    if (i == 0 || i == 1 || i == 2) {
                        tmp.res = drawables[i];
                    }
                }
                for (Item tmp : result.obj.hotGoods) {
                    tmp.extra = EXTRA_HOT_GOODS;
                }
                data.addAll(result.obj.hotGoods);
                headerCount = headerCount + result.obj.hotGoods.size();

                OneYuanMore oneYuanMore = new OneYuanMore();
                oneYuanMore.action = ActionConstants.OPEN_WEB_VIEW;
                oneYuanMore.target = MyConstants.PAST_RANKING + result.obj.eventId;
                data.add(oneYuanMore);
                headerCount++;
            }

            if (!ArrayUtil.isEmpty(result.obj.list)) {//全部商品
                OneYuanFilter filter = new OneYuanFilter();
                filter.cat2 = cat2;
                filter.orderBy = orderBy;
                data.add(filter);
                headerCount++;

                for (Item item : result.obj.list) {
                    item.extra = EXTRA_GOODS;
                    item.isGrid = isGrid;
                }

                data.addAll(result.obj.list);
            }

        }
        notifyWrapperDataSetChanged();
    }

    @Override
    public void notifyWrapperDataSetChanged() {
        boolean isEmpty = isEmpty();
        if (isEmpty) {
            if (data == null) {
                data = new ArrayList<>();
            }
            if (!data.contains(res)) {
                data.add(res);
            }
        } else {
            if (data.contains(res)) {
                data.remove(res);
            }
        }
        super.notifyWrapperDataSetChanged();
    }


    private boolean isEmpty() {
        if (!ArrayUtil.isEmpty(this.data)) {
            for (Object obj : data) {
                if (obj instanceof Item) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addAll(List<Item> data) {
        if (!ArrayUtil.isEmpty(data)) {
            for (Item item : data) {
                item.extra = EXTRA_GOODS;
                item.isGrid = isGrid;
            }
            this.data.addAll(data);
            notifyWrapperDataSetChanged();
        }
    }

    public List<Object> getData() {
        return data;
    }

    public void setGrid(boolean grid) {
        this.isGrid = grid;
        if (!ArrayUtil.isEmpty(data)) {
            for (Object obj : data) {
                if (obj instanceof Item) {
                    Item item = (Item) obj;
                    if (EXTRA_GOODS.equals(item.extra)) {
                        item.isGrid = grid;
                    }
                }
            }
            notifyWrapperDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = data.get(position);
        if (obj instanceof BannerData) {
            return ItemType.HEADER.ordinal();
        } else if (obj instanceof OneYuanCounter) {
            return ItemType.COUNT_DOWN.ordinal();
        } else if (obj instanceof OneYuanEvent) {
            return ItemType.EVENT.ordinal();
        } else if (obj instanceof OneYuanTitle) {
            return ItemType.TITLE.ordinal();
        } else if (obj instanceof Integer) {
            return ItemType.EMPTY.ordinal();
        } else if (obj instanceof OneYuanMore) {
            return ItemType.HOT_MORE.ordinal();
        } else if (obj instanceof OneYuanFilter) {
            return ItemType.FILTER.ordinal();
        } else {
            Item item = (Item) obj;
            if (EXTRA_HOT_GOODS.equals(item.extra)) {
                return ItemType.HOT_GOODS.ordinal();
            } else {
                return ItemType.GRID.ordinal();
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ItemType.HEADER.ordinal()) {
            BannerViewHolder bannerViewHolder = new BannerViewHolder(LayoutInflater.from(context).inflate(R.layout.view_headview_banner, parent, false));
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bannerViewHolder.bannerView.getLayoutParams();
            int width = CommonUtils.getScreenWidth();
            int height = (int) (width * 0.41f);
            params.width = width;
            params.height = height;
            return bannerViewHolder;
        } else if (viewType == ItemType.COUNT_DOWN.ordinal()) {
            return new CounterViewHolder(LayoutInflater.from(context).inflate(R.layout.count_down_view, parent, false));
        } else if (viewType == ItemType.EVENT.ordinal()) {
            return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.one_yuan_envent_item, parent, false));
        } else if (viewType == ItemType.TITLE.ordinal()) {
            return new TitleViewHolder(LayoutInflater.from(context).inflate(R.layout.one_yuan_title, parent, false));
        } else if (viewType == ItemType.HOT_MORE.ordinal()) {
            return new HotGoodsMoreHolder(LayoutInflater.from(context).inflate(R.layout.hot_goods_more, parent, false));
        } else if (viewType == ItemType.FILTER.ordinal()) {
            return new FilterHolder(LayoutInflater.from(context).inflate(R.layout.one_yuan_classification, parent, false));
        } else if (viewType == ItemType.EMPTY.ordinal()) {
            return new EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.view_empty_image, parent, false));
        } else if (viewType == ItemType.HOT_GOODS.ordinal()) {
            return new HotGoodsViewHolder(LayoutInflater.from(context).inflate(R.layout.hot_goods_item, parent, false));
        } else {
            return new GridViewHolder(LayoutInflater.from(context).inflate(R.layout.goods_item_grid, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Object obj = data.get(position);
        if (holder instanceof BannerViewHolder) {
            BannerViewHolder viewHolder = (BannerViewHolder) holder;
            BannerData item = (BannerData) obj;
            viewHolder.bannerView.setBanners(item.getBanners());
            CommonUtils.setAutoLoop(item, viewHolder.bannerView);
            viewHolder.rule.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WebViewActivity2.class);
                    intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.UNARY_RULE);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(0, 0);
                }
            });
        } else if (holder instanceof CounterViewHolder) {
            CounterViewHolder viewHolder = (CounterViewHolder) holder;
            OneYuanCounter item = (OneYuanCounter) obj;
            if (!viewHolderArrayList.contains(viewHolder)) {
                viewHolderArrayList.add(viewHolder);
            }
            viewHolder.counter = item;
            if (item.startTime - System.currentTimeMillis() > 0) {
                viewHolder.llStart.setVisibility(View.VISIBLE);
                viewHolder.rlEnd.setVisibility(View.GONE);
                viewHolder.countViewStart.reset(item.startTime);
                holder.itemView.setVisibility(View.VISIBLE);
            } else {
                if (item.endTime - System.currentTimeMillis() > 0) {
                    holder.itemView.setVisibility(View.VISIBLE);
                    viewHolder.llStart.setVisibility(View.GONE);
                    viewHolder.rlEnd.setVisibility(View.VISIBLE);
                    viewHolder.countViewEnd.reset(item.endTime);
                } else {
                    holder.itemView.setVisibility(View.GONE);
                }
            }
        } else if (holder instanceof EventViewHolder) {
            EventViewHolder viewHolder = (EventViewHolder) holder;
            final OneYuanEvent item = (OneYuanEvent) obj;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PastOneRankingActivity.class);
                    intent.putExtra("extra_id", String.valueOf(item.eventId));
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(0, 0);
                }
            });

            viewHolder.tvEvent.setText(item.event == null ? "" : item.event);


        } else if (holder instanceof HotGoodsMoreHolder) {

            HotGoodsMoreHolder hotGoodsMoreHolder = (HotGoodsMoreHolder) holder;
            final OneYuanMore hotGoodsMore = (OneYuanMore) obj;
            hotGoodsMoreHolder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(hotGoodsMore.action) && !TextUtils.isEmpty(hotGoodsMore.target)) {
                        ActionUtil.dispatchAction(context, hotGoodsMore.action, hotGoodsMore.target);
                    }
                }
            });


        } else if (holder instanceof FilterHolder) {
            FilterHolder filterHolder = (FilterHolder) holder;
            filterHolder.rgClassification.setOnCheckedChangeListener(null);
            filterHolder.rgSort.setOnCheckedChangeListener(null);
            OneYuanFilter filter = (OneYuanFilter) obj;
            if ("0".equals(filter.cat2)) {
                ((RadioButton) filterHolder.rgClassification.findViewById(R.id.rbAll)).setChecked(true);
            } else if ("2010".equals(filter.cat2)) {
                ((RadioButton) filterHolder.rgClassification.findViewById(R.id.rbLady)).setChecked(true);
            } else if ("2020".equals(filter.cat2)) {
                ((RadioButton) filterHolder.rgClassification.findViewById(R.id.rbBaby)).setChecked(true);
            } else if ("2030".equals(filter.cat2)) {
                ((RadioButton) filterHolder.rgClassification.findViewById(R.id.rbHome)).setChecked(true);
            }

            if ("-3".equals(filter.orderBy)) {
                ((RadioButton) filterHolder.rgSort.findViewById(R.id.rbHot)).setChecked(true);
            } else if ("-2".equals(filter.orderBy)) {
                ((RadioButton) filterHolder.rgSort.findViewById(R.id.rbTime)).setChecked(true);
            }

            filterHolder.rgClassification.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if (mOnFilterChangedListener != null) {
                        mOnFilterChangedListener.onFilterChanged(checkedId);
                    }
                }
            });

            filterHolder.rgSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if (mOnFilterChangedListener != null) {
                        mOnFilterChangedListener.onSortChanged(checkedId);
                    }
                }
            });

        } else if (holder instanceof TitleViewHolder) {


        } else if (holder instanceof EmptyViewHolder) {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            Integer res = (Integer) obj;
            emptyViewHolder.ivEmpty.setImageResource(res.intValue());
        } else if (holder instanceof HotGoodsViewHolder) {
            HotGoodsViewHolder viewHolder = (HotGoodsViewHolder) holder;
            final Item item = (Item) obj;
            if (item != null) {
                if (item.res > 0) {
                    viewHolder.ivIcon.setImageResource(item.res);
                }
                final ArticleAndGoods goods = item.item;
                if (goods != null) {
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, GoodsDetailsActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goods.goodsId + "");
                            intent.putExtra("position", position);
                            ((Activity) context).startActivityForResult(intent, RecommendListFragment.REQUEST_CODE_FOR_GOODS_DETAIL);
                        }
                    });


                    String cover = goods.videoCover;
                    if (!TextUtils.isEmpty(cover)) {
                        String tag = (String) viewHolder.goodsPic.getTag();
                        if (TextUtils.isEmpty(tag) || !tag.equals(cover)) {
                            viewHolder.goodsPic.setImageURI(cover);
                            viewHolder.goodsPic.setTag(cover);
                        }
                        viewHolder.ivPlay.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.ivPlay.setVisibility(View.GONE);
                        if (!ArrayUtil.isEmpty(goods.goodsImgs)) {
                            String url = goods.goodsImgs.get(0);
                            String tag = (String) viewHolder.goodsPic.getTag();
                            if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                                viewHolder.goodsPic.setImageURI(url);
                                viewHolder.goodsPic.setTag(url);
                            }
                        }
                    }

                    if (goods.goodsState == 4 || goods.goodsState == 3) {
                        viewHolder.sellout.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.sellout.setVisibility(View.INVISIBLE);
                    }

                    if (!TextUtils.isEmpty(goods.brand) || !TextUtils.isEmpty(goods.name)) {
                        viewHolder.tvBrandAndName.setText(goods.brand + " | " + goods.name);
                    }


                    viewHolder.tvLikeCount.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startForRequestWantBuy(item);
                        }
                    });

                    boolean isLike = goods.collected;
                    if (isLike) {
                        viewHolder.tvLikeCount.setTextColor(Color.parseColor("#ffff6677"));
                        viewHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(
                                context.getResources().getDrawable(R.drawable.btn_item_like_select),
                                null, null, null);
                    } else {
                        viewHolder.tvLikeCount.setTextColor(Color.parseColor("#8A000000"));
                        viewHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(
                                context.getResources().getDrawable(R.drawable.btn_item_like_normal),
                                null, null, null);

                    }

                    if (item.counts != null && !TextUtils.isEmpty(item.counts.collection)) {
                        viewHolder.tvLikeCount.setText(item.counts.collection);
                    } else {
                        viewHolder.tvLikeCount.setText("0");
                    }

                    viewHolder.cptv.setData(goods);
                }
            }

            if (item.user != null) {
                viewHolder.ctvName.setData(item.user);

                viewHolder.avatar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, item.user.getUserId());
                        context.startActivity(intent);
                    }
                });


                String tag = (String) viewHolder.avatar.getTag();
                String url = item.user.getAvatarUrl();

                if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                    viewHolder.avatar.setImageURI(url);
                    viewHolder.avatar.setTag(url);
                }
            }

        } else {
            GridViewHolder viewHolder = (GridViewHolder) holder;
            final Item item = (Item) obj;
            if (item != null) {
                final ArticleAndGoods goods = item.item;
                if (goods != null) {
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, GoodsDetailsActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goods.goodsId + "");
                            intent.putExtra("position", position);
                            ((Activity) context).startActivityForResult(intent, RecommendListFragment.REQUEST_CODE_FOR_GOODS_DETAIL);
                        }
                    });


                    if (!ArrayUtil.isEmpty(goods.goodsImgs)) {
                        String url = goods.goodsImgs.get(0);
                        String tag = (String) viewHolder.goodsPic.getTag();
                        if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                            viewHolder.goodsPic.setImageURI(url);
                            viewHolder.goodsPic.setTag(url);
                        }
                    }

                    String cover = goods.videoCover;
                    if (!TextUtils.isEmpty(cover)) {
                        String tag = (String) viewHolder.goodsPic.getTag();
                        if (TextUtils.isEmpty(tag) || !tag.equals(cover)) {
                            viewHolder.goodsPic.setImageURI(cover);
                            viewHolder.goodsPic.setTag(cover);
                        }
                        viewHolder.ivPlay.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.ivPlay.setVisibility(View.GONE);
                        if (!ArrayUtil.isEmpty(goods.goodsImgs)) {
                            String url = goods.goodsImgs.get(0);
                            String tag = (String) viewHolder.goodsPic.getTag();
                            if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                                viewHolder.goodsPic.setImageURI(url);
                                viewHolder.goodsPic.setTag(url);
                            }
                        }
                    }

                    if (goods.goodsState == 4) {
                        viewHolder.sellout.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.sellout.setVisibility(View.INVISIBLE);
                    }

                    if (!TextUtils.isEmpty(goods.brand) || !TextUtils.isEmpty(goods.name)) {
                        viewHolder.tvBrandAndName.setText(goods.brand + " | " + goods.name);
                    }


                    viewHolder.tvLikeCount.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startForRequestWantBuy(item);
                        }
                    });

                    boolean isLike = goods.collected;
                    if (isLike) {
                        viewHolder.tvLikeCount.setTextColor(Color.parseColor("#ffff6677"));
                        viewHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(
                                context.getResources().getDrawable(R.drawable.btn_item_like_select),
                                null, null, null);
                    } else {
                        viewHolder.tvLikeCount.setTextColor(Color.parseColor("#8A000000"));
                        viewHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(
                                context.getResources().getDrawable(R.drawable.btn_item_like_normal),
                                null, null, null);

                    }

                    if (item.counts != null && !TextUtils.isEmpty(item.counts.collection)) {
                        viewHolder.tvLikeCount.setText(item.counts.collection);
                    } else {
                        viewHolder.tvLikeCount.setText("0");
                    }

                    viewHolder.cptv.setData(goods);
                }
            }

            if (item.user != null) {
                viewHolder.userView.setData(item.user);

                viewHolder.avatar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, item.user.getUserId());
                        context.startActivity(intent);
                    }
                });


                String tag = (String) viewHolder.avatar.getTag();
                String url = item.user.getAvatarUrl();

                if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                    viewHolder.avatar.setImageURI(url);
                    viewHolder.avatar.setTag(url);
                }
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

            int paddingOut = CommonUtils.dp2px(10f);
            int paddingInner = CommonUtils.dp2px(10f) / 2;

            params.bottomMargin = paddingOut;
            if (((position - headerCount) % 2) == 0) {
                params.leftMargin = paddingOut;
                params.rightMargin = paddingInner;
            } else {
                params.leftMargin = paddingInner;
                params.rightMargin = paddingOut;
            }

        }

    }


    private void startForRequestWantBuy(final Item item) {
        HashMap<String, String> params = new HashMap<>();
        params.put("targetType", "1");
        params.put("targetId", String.valueOf(item.item.goodsId));
        if (item.item.collected) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.COLLECT, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "喜欢:" + response);

                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        CollectionData data = JSON.parseObject(baseResult.obj, CollectionData.class);
                        if (item.item.collected) {
                            item.item.collected = false;
                        } else {
                            item.item.collected = true;
                        }
                        item.counts.collection = String.valueOf(data.getCollections());
                        notifyWrapperDataSetChanged();
                    } else {
                        CommonUtils.error(baseResult, (Activity) context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type ==
                            ItemType.HEADER.ordinal() ||//轮播图
                            type == ItemType.COUNT_DOWN.ordinal() ||//倒计时
                            type == ItemType.EVENT.ordinal() || //期
                            type == ItemType.HOT_GOODS.ordinal() || //热门商品
                            type == ItemType.HOT_MORE.ordinal() || //查看更多
                            type == ItemType.TITLE.ordinal()) { //title
                        return SPAN_LIST;
                    } else if (type == ItemType.GRID.ordinal()) {//grid列表
                        return SPAN_GRID;
                    }
                    return SPAN_LIST;
                }
            });
        }
    }


    public class BannerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bannerView)
        ClassBannerView bannerView;
        @BindView(R.id.rule)
        SimpleDraweeView rule;

        public BannerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class CounterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.countViewStart)
        CounterView countViewStart;
        @BindView(R.id.llStart)
        LinearLayout llStart;
        @BindView(R.id.countViewEnd)
        CounterViewNew countViewEnd;
        @BindView(R.id.rlEnd)
        RelativeLayout rlEnd;
        public OneYuanCounter counter;

        public CounterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvEvent)
        TextView tvEvent;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HotGoodsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivIcon)
        ImageView ivIcon;
        @BindView(R.id.goodsPic)
        SimpleDraweeView goodsPic;
        @BindView(R.id.ivPlay)
        ImageView ivPlay;
        @BindView(R.id.tvBrandAndName)
        TextView tvBrandAndName;
        @BindView(R.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.ctvName)
        CommonTitleViewSmall ctvName;
        @BindView(R.id.tvLikeCount)
        TextView tvLikeCount;
        @BindView(R.id.cptv)
        CommonPriceTagView cptv;
        @BindView(R.id.space)
        View space;
        @BindView(R.id.bottomDivider)
        View bottomDivider;
        @BindView(R.id.sellout)
        ImageView sellout;

        public HotGoodsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {


        public TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HotGoodsMoreHolder extends RecyclerView.ViewHolder {

        public HotGoodsMoreHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FilterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rgClassification)
        RadioGroup rgClassification;
        @BindView(R.id.rgSort)
        RadioGroup rgSort;

        public FilterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goodsPic)
        SimpleDraweeView goodsPic;
        @BindView(R.id.ivPlay)
        ImageView ivPlay;
        @BindView(R.id.tvBrandAndName)
        TextView tvBrandAndName;
        @BindView(R.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.ctvName)
        CommonTitleViewSmall ctvName;
        @BindView(R.id.tvLikeCount)
        TextView tvLikeCount;
        @BindView(R.id.cptv)
        CommonPriceTagView cptv;

        public ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goodsPic)
        SimpleDraweeView goodsPic;
        @BindView(R.id.ivPlay)
        ImageView ivPlay;
        @BindView(R.id.tvBrandAndName)
        TextView tvBrandAndName;
        @BindView(R.id.cptv)
        CommonPriceTagView cptv;
        @BindView(R.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.userView)
        UserView userView;
        @BindView(R.id.tvLikeCount)
        TextView tvLikeCount;
        @BindView(R.id.sellout)
        ImageView sellout;

        public GridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivEmpty)
        ImageView ivEmpty;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private ArrayList<CounterViewHolder> viewHolderArrayList = new ArrayList<>();

    private boolean isRefresh = true;

    public void notifyCountDownDataSetChanged() {

        for (CounterViewHolder viewHolder : viewHolderArrayList) {
            if (!ArrayUtil.isEmpty(data)) {
                if (viewHolder.counter == null) {
                    return;
                }
                if (viewHolder.counter.startTime - System.currentTimeMillis() > 0) {
                    viewHolder.llStart.setVisibility(View.VISIBLE);
                    viewHolder.rlEnd.setVisibility(View.GONE);
                    viewHolder.countViewStart.reset(viewHolder.counter.startTime);
                } else {
                    if (viewHolder.counter.endTime - System.currentTimeMillis() > 0) {
                        viewHolder.llStart.setVisibility(View.GONE);
                        viewHolder.rlEnd.setVisibility(View.VISIBLE);
                        viewHolder.countViewEnd.reset(viewHolder.counter.endTime);
                    } else {
                        if (isRefresh) {
                            isRefresh = false;
                            TextDialog dialog = new TextDialog(context, false);
                            String formatter = context.getResources().getString(R.string.one_yuan_count_down);
                            String format = String.format(formatter, viewHolder.counter.eventId);
                            dialog.setContentText(format);
                            dialog.setLeftText("确定");
                            dialog.setLeftCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    if (onRefreshListener != null) {
                                        onRefreshListener.refresh();
                                    }
                                }
                            });
                            dialog.show();
                        }

                    }
                }
            }
        }
    }

    public interface OnRefreshListener {
        void refresh();
    }

    private OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public interface OnFilterChangedListener {
        void onFilterChanged(int res);

        void onSortChanged(int res);
    }

    private OnFilterChangedListener mOnFilterChangedListener;

    public void setOnFilterChangedListener(OnFilterChangedListener onFilterChangedListener) {
        this.mOnFilterChangedListener = onFilterChangedListener;
    }
}
