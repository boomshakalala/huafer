package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsInfoBean;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.views.CommonPriceTagView;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.DashLineView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liang_xs on 2016/10/28.
 */
public class PicsListAdapter extends BaseAdapter {

    private Context context;
    private List<GoodsInfoBean> infoBeans = new ArrayList<GoodsInfoBean>();
    private RecyclerViewGalleryAdapter reAdapter;

    public PicsListAdapter() {
        super();
    }

    public PicsListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<GoodsInfoBean> infoBeans) {
        this.infoBeans = infoBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return infoBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return infoBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_commodity, null);
            viewHolder = new ViewHolder();
            viewHolder.ctvName = (CommonTitleView) convertView.findViewById(R.id.ctvName);
            viewHolder.tvLike = (TextView) convertView.findViewById(R.id.tvLike);
            viewHolder.tvDes = (TextView) convertView.findViewById(R.id.tvDes);
            viewHolder.dlvCity = (DashLineView) convertView.findViewById(R.id.dlvCity);
            viewHolder.ivHeader = (SimpleDraweeView) convertView.findViewById(R.id.ivHeader);
            viewHolder.ivLike = (ImageView) convertView.findViewById(R.id.ivLike);
            viewHolder.layoutLike = convertView.findViewById(R.id.layoutLike);
            viewHolder.rvHorizontalPic = (RecyclerView) convertView
                    .findViewById(R.id.rvHorizontalPic);
            viewHolder.dlvGoodsName = (DashLineView) convertView.findViewById(R.id.dlvGoodsName);
            // 创建一个线性布局管理器
            LinearLayoutManager layoutManagerPic = new LinearLayoutManager(
                    context);
            layoutManagerPic.setOrientation(LinearLayoutManager.HORIZONTAL);
            // 设置布局管理器
            viewHolder.rvHorizontalPic.setLayoutManager(layoutManagerPic);
            viewHolder.cptv = (CommonPriceTagView) convertView.findViewById(R.id.cptv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//			if (position == userInfoBeans.size() - 1) {
//				viewHolder.layoutRecommendation.setPadding(7, 7, 7, 83);
//			} else {
//				viewHolder.layoutRecommendation.setPadding(7, 7, 7, 0);
//			}
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, infoBeans.get(position).getGoodsInfo().getGoodsId() + "");
                context.startActivity(intent);
            }
        });
        if (infoBeans.get(position) != null && infoBeans.get(position).getUserInfo() != null) {
            UserInfo info = infoBeans.get(position).getUserInfo();
            viewHolder.ctvName.setData(info);

            ImageLoader.resizeSmall(viewHolder.ivHeader, infoBeans.get(position).getUserInfo().getUserIcon(), 1);

            viewHolder.cptv.setData(infoBeans.get(position).getGoodsInfo());
            if (TextUtils.isEmpty(infoBeans.get(position).getGoodsInfo().getGoodsContent())) {
                viewHolder.tvDes.setVisibility(View.GONE);
            } else {
                viewHolder.tvDes.setVisibility(View.VISIBLE);
                viewHolder.tvDes.setText(infoBeans.get(position).getGoodsInfo().getGoodsContent());
            }
            viewHolder.dlvCity.setData(infoBeans.get(position).getGoodsInfo().getArea().getCity(),
                    infoBeans.get(position).getGoodsInfo().getArea().getArea());
            //喜欢按钮
            viewHolder.layoutLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startForResqutWantBuy(position);
                }
            });
            viewHolder.ivHeader.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, infoBeans.get(position).getUserInfo().getUserId());
                    context.startActivity(intent);
                }
            });
            reAdapter = new RecyclerViewGalleryAdapter(context);
            viewHolder.rvHorizontalPic.setAdapter(reAdapter);
            reAdapter.setData(infoBeans.get(position).getGoodsInfo());
            boolean isLike = infoBeans.get(position).getGoodsInfo().getIsLike();
            if (isLike) {
                viewHolder.ivLike.setImageResource(R.drawable.btn_item_like_select);
                viewHolder.tvLike.setTextColor(Color.parseColor("#ffff6677"));
            } else {
                viewHolder.ivLike.setImageResource(R.drawable.btn_item_like_normal);
                viewHolder.tvLike.setTextColor(Color.parseColor("#888888"));
            }

            viewHolder.tvLike.setText(String.valueOf(infoBeans.get(position).getGoodsInfo().getLikeCount()));
            viewHolder.dlvGoodsName.setData(infoBeans.get(position).getGoodsInfo().getGoodsBrand(), infoBeans.get(position).getGoodsInfo().getGoodsName());
        }

        return convertView;
    }

    private void startForResqutWantBuy(final int position) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }
        if (infoBeans == null) {
            return;
        }
        ProgressDialog.showProgress(context);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("goodsId", String.valueOf(infoBeans.get(position).getGoodsInfo().getGoodsId()));
        final boolean isLike = infoBeans.get(position).getGoodsInfo().getIsLike();
        if (isLike) {
            params.put("type", "2");
        } else {
            params.put("type", "1");
        }
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.WANTBUY, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
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
                                    infoBeans.get(position).getGoodsInfo().setIsLike(false);
                                    if (infoBeans.get(position).getGoodsInfo().getLikeCount() > 0) {
                                        infoBeans.get(position).getGoodsInfo().setLikeCount(infoBeans.get(position).getGoodsInfo().getLikeCount() - 1);
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    infoBeans.get(position).getGoodsInfo().setIsLike(true);
                                    infoBeans.get(position).getGoodsInfo().setLikeCount(infoBeans.get(position).getGoodsInfo().getLikeCount() + 1);
                                    notifyDataSetChanged();
                                }

                            } else {
                                CommonUtils.error(baseResult, (Activity) context, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    class ViewHolder {
        private CommonTitleView ctvName;
        private TextView tvLike;
        private TextView tvDes;
        private DashLineView dlvCity;
        private RecyclerView rvHorizontalPic;
        private SimpleDraweeView ivHeader;
        private ImageView ivLike;
        private View layoutLike;
        private DashLineView dlvGoodsName;
        private CommonPriceTagView cptv;
    }
}
