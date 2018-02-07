package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.DashLineView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PersonalHomeAdapter extends BaseAdapter {
    Context mContext;
    private LayoutInflater inflater;
    private List<GoodsInfo> list = new ArrayList<GoodsInfo>();

    public PersonalHomeAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public void setData(List<GoodsInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public GoodsInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gridview_goods, null);
            viewHolder = new ViewHolder();
            viewHolder.ivPhoto = (SimpleDraweeView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.dlvGoodsName = (DashLineView) convertView.findViewById(R.id.dlvGoodsName);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            viewHolder.tvPastPrice = (TextView) convertView.findViewById(R.id.tvPastPrice);
            viewHolder.tvLike = (TextView) convertView.findViewById(R.id.tvLike);
            WindowManager wm = ((Activity) mContext).getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.ivPhoto.getLayoutParams();
            lp.height = width / 2;
            viewHolder.ivPhoto.setLayoutParams(lp);
            viewHolder.tvPastPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (list.get(position) != null && list.get(position).getGoodsImgs() != null && list.get(position).getGoodsImgs().size() > 0) {
            if (!TextUtils.isEmpty(list.get(position).getGoodsImgs().get(0))) {
                ImageLoader.loadImage(viewHolder.ivPhoto, list.get(position).getGoodsImgs().get(0));
            }
        } else {
            viewHolder.ivPhoto.setBackgroundResource(R.drawable.item_add_icon);
        }

//        SimplifySpanBuild simplifySpanBuild1 = new SimplifySpanBuild(mContext, viewHolder.tvSpanTitle);
//        simplifySpanBuild1.appendNormalText(list.get(position).getGoodsBrand()).appendSpecialUnit(new SpecialImageUnit(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher), 1, 50).setGravity(SpecialGravityEnum.CENTER)).appendNormalText(list.get(position).getGoodsName());
//        viewHolder.tvSpanTitle.setText(simplifySpanBuild1.build());
        viewHolder.dlvGoodsName.setData(list.get(position).getGoodsBrand(), list.get(position).getGoodsName());
        viewHolder.tvPrice.setText(String.valueOf(list.get(position).getPrice()));
        CommonUtils.setPriceSizeData(viewHolder.tvPastPrice, "", list.get(position).getPastPrice());
//        if(list.get(position).getArea() != null) {
//        	viewHolder.tvLike.setText(list.get(position).getArea().getCity());
//        }
        GoodsInfo item = list.get(position);
        int likeCount = list.get(position).getLikeCount();
        String count = CommonUtils.getDoubleCount(likeCount, MyConstants.COUNT_FANS);
        viewHolder.tvLike.setText(count);
        viewHolder.tvLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startForRequestWantBuy(position);
            }
        });
        final boolean isLike = item.getIsLike();
        if (isLike) {
            viewHolder.tvLike.setTextColor(Color.parseColor("#ffff6677"));
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.btn_item_like_select);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.tvLike.setCompoundDrawables(drawable, null, null, null);
        } else {
            viewHolder.tvLike.setTextColor(Color.parseColor("#888888"));
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.btn_item_like_normal);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.tvLike.setCompoundDrawables(drawable, null, null, null);
        }
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
        final GoodsInfo item = list.get(position);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("goodsId", String.valueOf(item.getGoodsId()));
        final boolean isLike = item.getIsLike();
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

                    }

                    @Override
                    public void onResponse(String response) {
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
                                    item.setLike(false);
                                    if (item.getLikeCount() > 0) {
                                        item.setLikeCount(item.getLikeCount() - 1);
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    item.setLike(true);
                                    item.setLikeCount(item.getLikeCount() + 1);
                                    notifyDataSetChanged();
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

    class ViewHolder {
        private SimpleDraweeView ivPhoto;
        private DashLineView dlvGoodsName;
        private TextView tvPrice;
        private TextView tvPastPrice;
        private TextView tvLike;

    }
}