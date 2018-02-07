package com.huapu.huafen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Baby;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.utils.DateTimeUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.views.CommonTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liang_xs on 2016/10/29.
 */
public class UserInfoBabyAdapter extends BaseAdapter {

    private List<UserInfo> list = new ArrayList<UserInfo>();
    private Context mContext;

    public UserInfoBabyAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void setData(List<UserInfo> list) {
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
        if (position < 0 || position >= getCount()) {
            return convertView;
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_listview_follow, null);
            viewHolder = new ViewHolder();
            viewHolder.ctvName = (CommonTitleView) convertView.findViewById(R.id.ctvName);
            viewHolder.tvFirstBaby = (TextView) convertView
                    .findViewById(R.id.tvFirstBaby);
            viewHolder.tvSecondBaby = (TextView) convertView
                    .findViewById(R.id.tvSecondBaby);
            viewHolder.tvFollowState = (TextView) convertView
                    .findViewById(R.id.tvFollowState);
            viewHolder.ivHeader = (SimpleDraweeView) convertView
                    .findViewById(R.id.ivHeader);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ctvName.setData(list.get(position));

        ImageLoader.resizeSmall(viewHolder.ivHeader, list.get(position).getUserIcon(), 1);

        viewHolder.tvFirstBaby
                .setBackgroundResource(R.drawable.text_blue_round_bg);
        viewHolder.tvFollowState.setVisibility(View.GONE);
        ArrayList<Baby> babys = list.get(position).getBabys();
        if (babys != null) {
            if (babys.size() == 0) {
                viewHolder.tvFirstBaby.setVisibility(View.GONE);
                viewHolder.tvSecondBaby.setVisibility(View.GONE);
            } else if (babys.size() == 1) {
                viewHolder.tvFirstBaby.setVisibility(View.VISIBLE);
                viewHolder.tvSecondBaby.setVisibility(View.GONE);
                if (babys.get(0).getSex() == 0) {
                    viewHolder.tvFirstBaby
                            .setBackgroundResource(R.drawable.text_pink_light_round_bg);
                    viewHolder.tvFirstBaby.setText("小公主\n"
                            + DateTimeUtils.getBabyAge(babys.get(0)
                            .getDateOfBirth()));
                } else if (babys.get(0).getSex() == 1) {
                    viewHolder.tvFirstBaby
                            .setBackgroundResource(R.drawable.text_blue_round_bg);
                    viewHolder.tvFirstBaby.setText("小王子\n"
                            + DateTimeUtils.getBabyAge(babys.get(0)
                            .getDateOfBirth()));
                }
            } else if (babys.size() == 2) {
                viewHolder.tvFirstBaby.setVisibility(View.VISIBLE);
                viewHolder.tvSecondBaby.setVisibility(View.VISIBLE);
                if (babys.get(0).getSex() == 0) {
                    viewHolder.tvFirstBaby
                            .setBackgroundResource(R.drawable.text_pink_light_round_bg);
                    viewHolder.tvFirstBaby.setText("小公主\n"
                            + DateTimeUtils.getBabyAge(babys.get(0)
                            .getDateOfBirth()));
                } else if (babys.get(0).getSex() == 1) {
                    viewHolder.tvFirstBaby
                            .setBackgroundResource(R.drawable.text_blue_round_bg);
                    viewHolder.tvFirstBaby.setText("小王子\n"
                            + DateTimeUtils.getBabyAge(babys.get(0)
                            .getDateOfBirth()));
                }
                if (babys.get(1).getSex() == 0) {
                    viewHolder.tvFirstBaby
                            .setBackgroundResource(R.drawable.text_pink_light_round_bg);
                    viewHolder.tvSecondBaby.setText("小公主\n"
                            + DateTimeUtils.getBabyAge(babys.get(1)
                            .getDateOfBirth()));
                } else if (babys.get(1).getSex() == 1) {
                    viewHolder.tvFirstBaby
                            .setBackgroundResource(R.drawable.text_blue_round_bg);
                    viewHolder.tvSecondBaby.setText("小王子\n"
                            + DateTimeUtils.getBabyAge(babys.get(1)
                            .getDateOfBirth()));
                }
            }
        }
        return convertView;
    }

    class ViewHolder {
        private CommonTitleView ctvName;
        private TextView tvFirstBaby, tvSecondBaby;
        private TextView tvFollowState;
        private SimpleDraweeView ivHeader;
    }
}
