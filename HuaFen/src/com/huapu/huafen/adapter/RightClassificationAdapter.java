package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.ClassificationDetailActivity;
import com.huapu.huafen.beans.ClassificationResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/11.
 */

public class RightClassificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int SPAN_COUNT = 3;
    private final static int TYPE_SLIDER_HEADER = 0;
    private final static int TYPE_GRID_HEADER = 1;
    private final static int TYPE_TITLE_IMG = 2;
    private final static int TYPE_GRID_3 = 3;
    private final static int TYPE_FOOTER = 4;
    private final static int GRID_COUNT = 1;
    private final static String SLIDER = "slider";
    private final static String BRAND_GRID = "brand_grid";
    private final static String IMAGE_GRID = "image_grid";
    private final static String GRID = "grid";
    private final static String FOOTER = "footer";

    private List<Object> data;

    private Context context;
    private String key;

    public RightClassificationAdapter(Context context, String key, List<ClassificationResult.Layout> layout) {
        this.context = context;
        setData(key, layout);
    }

    public RightClassificationAdapter(Context context) {
        this(context, null, null);
    }

    public void setData(String key, List<ClassificationResult.Layout> layout) {
        this.key = key;
        if (!ArrayUtil.isEmpty(layout)) {
            genList(layout);
        } else {
            data = null;
        }

        notifyDataSetChanged();
    }

    private void genList(List<ClassificationResult.Layout> layout) {
        data = new ArrayList<>();
        for (ClassificationResult.Layout la : layout) {
            if (la != null && la.opts != null) {
                String type = la.type;
                if (SLIDER.equals(type)) {//热门专区头图
                    la.opts.type = la.type;
                    data.add(la.opts);
                } else if (IMAGE_GRID.equals(type)) {//分类三张图
                    String image = la.opts.image;
                    if (!TextUtils.isEmpty(image)) {
                        data.add(image);
                    }
                    la.opts.type = la.type;
                    data.add(la.opts);
                } else if (BRAND_GRID.equals(type)) {
                    List<ClassificationResult.Opt> items = la.opts.items;
                    if (!ArrayUtil.isEmpty(items)) {//如果列表为空，即使返回标签，也不展示
                        String image = la.opts.image;
                        if (!TextUtils.isEmpty(image)) {
                            data.add(image);
                        }
                        for (ClassificationResult.Opt opt : items) {
                            opt.type = type;
                        }
                        data.addAll(items);
                    }
                } else if (GRID.equals(type)) {
                    List<ClassificationResult.Opt> items = la.opts.items;
                    if (!ArrayUtil.isEmpty(items)) {//如果列表为空，即使返回标签，也不展示
                        String image = la.opts.image;
                        if (!TextUtils.isEmpty(image)) {
                            data.add(image);
                        }
                        for (ClassificationResult.Opt opt : items) {
                            opt.type = type;
                        }
                        data.addAll(items);
                    }
                }
            }
        }

        int res = 11;
        data.add(res);
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = data.get(position);
        if (obj instanceof ClassificationResult.Option) {
            ClassificationResult.Option option = (ClassificationResult.Option) obj;
            if (option.type.equals(SLIDER)) {
                return TYPE_SLIDER_HEADER;
            } else {
                return TYPE_GRID_HEADER;
            }
        } else if (obj instanceof String) {
            return TYPE_TITLE_IMG;
        } else if (obj instanceof Integer) {
            return TYPE_FOOTER;
        } else {
            return TYPE_GRID_3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SLIDER_HEADER) {
            return new HotRegionHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.class_hot_region_header, parent, false));
        } else if (viewType == TYPE_GRID_HEADER) {
            return new GridImageViewHolder(LayoutInflater.from(context).inflate(R.layout.class_grid_image_new, parent, false));
        } else if (viewType == TYPE_TITLE_IMG) {
            return new TitleImageViewHolder(LayoutInflater.from(context).inflate(R.layout.class_title_img, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(LayoutInflater.from(context).inflate(R.layout.classification_right_footer, parent, false));
        } else {
            return new RightClassificationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_class_grid_layout, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Object obj = data.get(position);
        int viewType = getItemViewType(position);
        if (viewType == TYPE_SLIDER_HEADER) {
            ClassificationResult.Option item = (ClassificationResult.Option) obj;
            HotRegionHeaderViewHolder viewHolder = (HotRegionHeaderViewHolder) holder;



            if (item != null) {
                List<ClassificationResult.itemsType> list = item.itemsTypes;
                if (!ArrayUtil.isEmpty(list)) {
                    final ClassificationResult.itemsType itemsType = list.get(0);
                    if (itemsType.opts != null) {
                        String image = itemsType.opts.image;
                        ImageLoader.loadImageWrapContent(viewHolder.hotRegionHeader,image);
                        viewHolder.hotRegionHeader.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                ActionUtil.dispatchAction(context, itemsType.opts.action, itemsType.opts.target);
                            }
                        });
                    }
                }
            }
        } else if (viewType == TYPE_GRID_HEADER) {
            ClassificationResult.Option item = (ClassificationResult.Option) obj;
            GridImageViewHolder viewHolder = (GridImageViewHolder) holder;
            if (item != null) {
                //viewHolder.classPic.setImageURI(item.image);
                List<ClassificationResult.Opt> list = item.items;
                SimpleDraweeView[] views = new SimpleDraweeView[]{viewHolder.sdv1, viewHolder.sdv2, viewHolder.sdv3, viewHolder.sdv4};

                if (!ArrayUtil.isEmpty(list)) {
                    for (int i = 0; i < views.length && i < list.size(); i++) {
                        final ClassificationResult.Opt opt = list.get(i);
                        if (opt != null) {
                            String image = opt.image;
                            views[i].setImageURI(image);
                            views[i].setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    ActionUtil.dispatchAction(context, opt.action, opt.target, 1000);
                                }
                            });
                        }
                    }
                }
            }
        } else if (viewType == TYPE_TITLE_IMG) {
            String item = (String) obj;
            TitleImageViewHolder viewHolder = (TitleImageViewHolder) holder;
            viewHolder.ivTitle.setImageURI(item);
        } else if (viewType == TYPE_FOOTER) {

        } else {
            final ClassificationResult.Opt item = (ClassificationResult.Opt) obj;
            RightClassificationViewHolder viewHolder = (RightClassificationViewHolder) holder;
            Logger.e("get data:" + item.type);
            if (item != null) {
                viewHolder.classPic.setImageURI(item.image);
                if (!TextUtils.isEmpty(item.title)) {
                    viewHolder.tvName.setText(item.title);
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, ClassificationDetailActivity.class);
                        intent.putExtra("key", key);
                        if (item.type.equals(BRAND_GRID)) {
                            intent.putExtra("SHOWCLASS", true);
                        }
                        intent.putExtra(MyConstants.EXTRA_OPTION, item);
                        context.startActivity(intent);
                    }
                });
            }
        }
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
                    switch (type) {
                        case TYPE_SLIDER_HEADER:
                        case TYPE_GRID_HEADER:
                        case TYPE_TITLE_IMG:
                            return SPAN_COUNT;
                        case TYPE_GRID_3:
                            return GRID_COUNT;
                        case TYPE_FOOTER:
                            return SPAN_COUNT;

                    }
                    return 0;
                }
            });
        }

    }

    //grid 布局
    public class RightClassificationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.classPic)
        SimpleDraweeView classPic;
        @BindView(R2.id.tvName)
        TextView tvName;

        public RightClassificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //ImageLoader.(context).loadImage(classPic, null, R.drawable.default_pic, R.drawable.default_pic,ScalingUtils.ScaleType.FIT_XY);
        }
    }

    //标题图片
    public class TitleImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.ivTitle)
        SimpleDraweeView ivTitle;

        public TitleImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ImageLoader.loadImage(ivTitle, null, R.drawable.default_pic, R.drawable.default_pic, ScalingUtils.ScaleType.FIT_XY);

            //ImageLoader.(context).loadImage(ivTitle, null, R.drawable.default_pic, R.drawable.default_pic);
        }
    }

    //一张图的头部
    public class HotRegionHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.hotRegionHeader)
        SimpleDraweeView hotRegionHeader;

        public HotRegionHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //ImageLoader.(context).loadImage(hotRegionHeader, null, R.drawable.default_pic, R.drawable.default_pic);
        }
    }

    //三张图的头部
    public class GridImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.sdv1)
        SimpleDraweeView sdv1;
        @BindView(R2.id.sdv2)
        SimpleDraweeView sdv2;
        @BindView(R2.id.sdv3)
        SimpleDraweeView sdv3;
        @BindView(R2.id.sdv4)
        SimpleDraweeView sdv4;

        public GridImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ImageLoader.loadImage(sdv1, null, R.drawable.default_pic, R.drawable.default_pic);
            ImageLoader.loadImage(sdv2, null, R.drawable.default_pic, R.drawable.default_pic);
            ImageLoader.loadImage(sdv3, null, R.drawable.default_pic, R.drawable.default_pic);
            ImageLoader.loadImage(sdv4, null, R.drawable.default_pic, R.drawable.default_pic);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }



}
