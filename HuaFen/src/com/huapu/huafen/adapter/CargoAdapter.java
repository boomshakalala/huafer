package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.views.CommonPriceTagView;

import java.util.ArrayList;

/**
 */
public class CargoAdapter extends CommonWrapper<RecyclerView.ViewHolder> {


    private ArrayList<Commodity> commodities = new ArrayList<>();

    private Context context;

    public CargoAdapter(Context context) {
        this.context = context;
    }

    public void addDatas(ArrayList<Commodity> datas) {
        commodities.addAll(datas);
        notifyWrapperDataSetChanged();
    }

    public void setData(ArrayList<Commodity> datas) {
        this.commodities = datas;
        notifyWrapperDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return commodities == null ? 0 : commodities.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cargo_list,
                viewGroup, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Commodity commodity = commodities.get(position);
        final GoodsData goodsData = commodity.getGoodsData();

        if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //需要回调传值
                    Intent intent = new Intent();
                    intent.putExtra(MyConstants.EXTRA_GOODS_DATA,goodsData);
                    ((Activity)context).setResult(Activity.RESULT_OK,intent);
                    ((Activity)context).finish();
//                    Intent intent = new Intent(context, GoodsDetailsActivity.class);
//                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, commodity.getGoodsData().getGoodsId() + "");
//                    intent.putExtra("position", position);
//                    ((Activity) context).startActivityForResult(intent, RecommendListFragment.REQUEST_CODE_FOR_GOODS_DETAIL);
                }
            });

            if (goodsData != null) {
                String cover = goodsData.getVideoCover();
                if (!TextUtils.isEmpty(cover)) {
                    vh.ivProPic.setImageURI(cover);
                    vh.ivPlay.setVisibility(View.VISIBLE);
                } else {
                    if (!ArrayUtil.isEmpty(goodsData.getGoodsImgs())) {
                        vh.ivProPic.setImageURI(goodsData.getGoodsImgs().get(0));
                    }
                    vh.ivPlay.setVisibility(View.GONE);
                }

                vh.cptv.setData(goodsData);

                String brand = goodsData.getBrand();
                String goodsName = goodsData.getName();
                String goodsNameDesc;
                if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
                    String format = context.getString(R.string.goods_name_desc);
                    goodsNameDesc = String.format(format, brand, goodsName);
                } else if (!TextUtils.isEmpty(brand) && TextUtils.isEmpty(goodsName)) {
                    goodsNameDesc = brand;
                } else if (TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
                    goodsNameDesc = goodsName;
                } else {
                    goodsNameDesc = "";
                }
                vh.tvTitle.setText(goodsNameDesc);
            }

            boolean flag = position == commodities.size() - 1;
            if (flag) {
                vh.classLine.setVisibility(View.INVISIBLE);
            } else {
                vh.classLine.setVisibility(View.VISIBLE);
            }
        }


    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        public SimpleDraweeView ivProPic;
        private ImageView ivPlay;
        public CommonPriceTagView cptv;
        View classLine;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProPic = (SimpleDraweeView) itemView.findViewById(R.id.ivProPic);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
            cptv = (CommonPriceTagView) itemView.findViewById(R.id.cptv);
            classLine = itemView.findViewById(R.id.class_line);

        }
    }

    public boolean isEmpty() {
        return ArrayUtil.isEmpty(commodities);
    }


}
