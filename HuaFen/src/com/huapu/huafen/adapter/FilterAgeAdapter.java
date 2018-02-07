package com.huapu.huafen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Age;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/3/22.
 */

public class FilterAgeAdapter extends CommonWrapper<FilterAgeAdapter.FilterAgeViewHolder> {

    private List<Age> data;
    private Context context;

    public FilterAgeAdapter(Context context, List<Age> data) {
        this.context = context;
        this.data = data;
    }

    public FilterAgeAdapter(Context context) {
        this(context, null);
    }

    @Override
    public FilterAgeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterAgeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_filter_price_single, parent, false));
    }

    @Override
    public void onBindViewHolder(FilterAgeViewHolder holder, final int position) {
        final Age item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setCheck(position);
            }
        });

        holder.tvPrice.setText(item.getAgeTitle());

        if (item.isCheck) {
            holder.tvPrice.setBackgroundResource(R.drawable.filter_price_pink_rect);
            holder.tvPrice.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.tvPrice.setBackgroundResource(R.drawable.filter_price_light_grey_rect);
            holder.tvPrice.setTextColor(Color.parseColor("#333333"));
        }

        if (position % 4 == 3) {
            holder.itemView.setPadding(0, CommonUtils.dp2px(20), 0, 0);
        } else {
            holder.itemView.setPadding(0, CommonUtils.dp2px(20), CommonUtils.dp2px(5), 0);
        }
    }

    /**
     * 获取age集合
     * @return
     */
    public ArrayList<Age> getSelectedAge() {
        //创建集合
        ArrayList<Age> newAgeArrayList = new ArrayList<>();
        //寻找选中的条目，加入集合
        if(!ArrayUtil.isEmpty(data)){
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).isCheck) {
                    newAgeArrayList.add(data.get(i));
                }
            }
        }
        return newAgeArrayList;
    }


    /**
     * 设置年龄段选中
     * @param selectAgeList
     */
    public void setSelectAge(ArrayList<Age> selectAgeList) {

        if (!ArrayUtil.isEmpty(data)) {
            boolean isContain = false;
            for (int i = 0; i < selectAgeList.size(); i++) {
                Age selectAge = selectAgeList.get(i);

                for (Age age : data) {
                    if (age.equals(selectAge)) {
                        age.isCheck = true;
                        isContain = true;
                        break;
                    }
                }

            }
            //如果没有选中 时 进来  ，默认选中第一个
            /* if (!isContain) {
                data.get(0).isCheck = true;
            }*/
            notifyWrapperDataSetChanged();
        }
    }

    public void setCheck(int position) {
        if (ArrayUtil.isEmpty(data)) {
            return;
        }

        //获取当前选中条块的对象
        Age age = data.get(position);

        //如果没有选中
        if (!age.isCheck) {

            //如果不是最后一个 “全年龄段”
            if (position != data.size() - 1) {

                //先去掉最后一个“全年龄段”的选中
                data.get(data.size() - 1).isCheck = false;

                //清空之前以及之后选中的条目
                clearDoubleCheck(position);

            } else {
                //清空之前所有选中的条块
                clearPreAllCheck();
            }
            //选中当前条块
            age.isCheck = true;

        } else {//如果选中，去除选中
            age.isCheck = false;
        }

        notifyWrapperDataSetChanged();
    }

    /**
     * 去掉除最后一个“全年龄段”以外的所有条块的选中
     */
    private void clearPreAllCheck() {
        //循环遍历，直到集合最后一个
        for (int i = 0; i < data.size() - 1; i++) {
            Age age = data.get(i);

            //去掉选中
            if (age.isCheck) {
                age.isCheck = false;
            }
        }
    }

    /**
     * 去掉前后选中的条块
     *
     * @param position 当前点击的条块索引
     */
    private void clearDoubleCheck(int position) {

        //遍历集合，将前后选中的条块置反
        for (int i = 0; i < data.size(); i++) {
            //比较到当前点击的条块前一个或者后一个时不做处理
            if (i + 1 == position || i - 1 == position)
                continue;

            Age age = data.get(i);

            //如果当前条块选中，置反。。。。。或者不做判断，直接置反也行
            if (age.isCheck) {
                //   Check_Position.remove(age);
                age.isCheck = false;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<Age> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public static class FilterAgeViewHolder extends RecyclerView.ViewHolder {

        public TextView tvPrice;

        public FilterAgeViewHolder(View itemView) {
            super(itemView);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }
}
