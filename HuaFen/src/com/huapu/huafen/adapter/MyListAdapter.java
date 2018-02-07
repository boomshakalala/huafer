package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsInfoBean;
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

public class MyListAdapter extends BaseAdapter {

    private ArrayList<GoodsInfoBean> list = new ArrayList<GoodsInfoBean>();
    private Context mContext;

    public MyListAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void setData(ArrayList<GoodsInfoBean> productBeans) {
        this.list = productBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_listview_class_goods, null);
            viewHolder = new ViewHolder();
            viewHolder.ctvName = (CommonTitleView) convertView.findViewById(R.id.ctvName);
            viewHolder.tvDes = (TextView) convertView.findViewById(R.id.tvDes);
            viewHolder.dlvCity = (DashLineView) convertView.findViewById(R.id.dlvCity);
            viewHolder.ivHeader = (SimpleDraweeView) convertView.findViewById(R.id.ivHeader);
            viewHolder.ivProPic = (SimpleDraweeView) convertView.findViewById(R.id.ivProPic);
            viewHolder.dlvGoodsName = (DashLineView) convertView.findViewById(R.id.dlvGoodsName);
            viewHolder.cptv = (CommonPriceTagView) convertView.findViewById(R.id.cptv);
            viewHolder.layoutLike = (LinearLayout) convertView.findViewById(R.id.layoutLike);
            viewHolder.ivLike = (ImageView) convertView.findViewById(R.id.ivLike);
            viewHolder.tvLike = (TextView) convertView.findViewById(R.id.tvLike);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//		if (position == userInfoBeans.size() - 1) {
//			viewHolder.layoutRecommendation.setPadding(7, 7, 7, 83);
//		} else {
//			viewHolder.layoutRecommendation.setPadding(7, 7, 7, 0);
//		}

        ImageLoader.resizeSmall(viewHolder.ivHeader, list.get(position).getUserInfo().getUserIcon(), 1);

        if (list.get(position).getGoodsInfo() != null && list.get(position).getGoodsInfo().getGoodsImgs().size() > 0) {

            ImageLoader.resizeMiddle(viewHolder.ivProPic, list.get(position).getGoodsInfo().getGoodsImgs().get(0), 1);
        }
        viewHolder.ctvName.setData(list.get(position).getUserInfo());
        viewHolder.cptv.setData(list.get(position).getGoodsInfo());
        viewHolder.tvDes.setText(list.get(position).getGoodsInfo().getGoodsContent());
        if (list.get(position).getGoodsInfo().getArea() != null) {
            viewHolder.dlvCity.setData(list.get(position).getGoodsInfo().getArea().getCity(), list.get(position).getGoodsInfo().getArea().getArea());
        }
        viewHolder.dlvGoodsName.setData(list.get(position).getGoodsInfo().getGoodsBrand(), list.get(position).getGoodsInfo().getGoodsName());

        viewHolder.layoutLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startForRequestWantBuy(position);
            }
        });
        boolean isLike = list.get(position).getGoodsInfo().getIsLike();
        if (isLike) {
            viewHolder.ivLike.setImageResource(R.drawable.btn_item_like_select);
            viewHolder.tvLike.setTextColor(Color.parseColor("#ffff6677"));
        } else {
            viewHolder.ivLike.setImageResource(R.drawable.btn_item_like_normal);
            viewHolder.tvLike.setTextColor(Color.parseColor("#888888"));
        }
        int likeCount = list.get(position).getGoodsInfo().getLikeCount();
        String count = CommonUtils.getDoubleCount(likeCount, MyConstants.COUNT_FANS);
        viewHolder.tvLike.setText(count);
        return convertView;
    }

    private void startForRequestWantBuy(final int position) {
        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        if (list == null) {
            return;
        }
        final GoodsInfoBean item = list.get(position);
        ProgressDialog.showProgress(mContext);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("goodsId", String.valueOf(item.getGoodsInfo().getGoodsId()));
        final boolean isLike = item.getGoodsInfo().getIsLike();
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
                                    item.getGoodsInfo().setLike(false);
                                    if (item.getGoodsInfo().getLikeCount() > 0) {
                                        item.getGoodsInfo().setLikeCount(item.getGoodsInfo().getLikeCount() - 1);
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    item.getGoodsInfo().setLike(true);
                                    item.getGoodsInfo().setLikeCount(item.getGoodsInfo().getLikeCount() + 1);
                                    notifyDataSetChanged();
                                }

                            } else {
                                CommonUtils.error(baseResult, (Activity) mContext, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });

    }


    class ViewHolder {
        public CommonTitleView ctvName;
        public TextView tvDes;
        private DashLineView dlvCity;
        private SimpleDraweeView ivHeader;
        private SimpleDraweeView ivProPic;
        private DashLineView dlvGoodsName;
        private CommonPriceTagView cptv;
        private LinearLayout layoutLike;
        private ImageView ivLike;
        private TextView tvLike;
    }

}