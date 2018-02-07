package com.huapu.huafen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.UserResult;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.DashLineView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liang_xs on 2016/10/29.
 */
public class UserResultGoodsAdapter extends BaseAdapter {

    private List<UserResult> list = new ArrayList<UserResult>();
    private Context mContext;

    public UserResultGoodsAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void setData(List<UserResult> list) {
        this.list = list;
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
                    R.layout.item_listview_class_personal, null);
            viewHolder = new ViewHolder();
            viewHolder.ctvName = (CommonTitleView) convertView.findViewById(R.id.ctvName);
            viewHolder.ivHeader = (SimpleDraweeView) convertView.findViewById(R.id.ivHeader);
            viewHolder.dlvCity = (DashLineView) convertView.findViewById(R.id.dlvCity);
            viewHolder.tvBuyGoodsCount = (TextView) convertView.findViewById(R.id.tvBuyGoodsCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//			if (position == userInfoBeans.size() - 1) {
//				viewHolder.layoutRecommendation.setPadding(7, 7, 7, 83);
//			} else {
//				viewHolder.layoutRecommendation.setPadding(7, 7, 7, 0);
//			}
        if (list.get(position) == null) {
            return convertView;
        }

        ImageLoader.resizeSmall(viewHolder.ivHeader, list.get(position).getUserData().getAvatarUrl(), 1);

        viewHolder.ctvName.setData(list.get(position).getUserData());
        viewHolder.tvBuyGoodsCount.setText(list.get(position).getUserValue().getGoodsCount() + "个宝贝");
        Area area = list.get(position).getUserData().getArea();
        if (area != null) {
            viewHolder.dlvCity.setData(area.getCity(), area.getArea());
        }

        return convertView;
    }

    class ViewHolder {
        private CommonTitleView ctvName;
        private SimpleDraweeView ivHeader;
        private TextView tvBuyGoodsCount;
        private DashLineView dlvCity;
    }

}
