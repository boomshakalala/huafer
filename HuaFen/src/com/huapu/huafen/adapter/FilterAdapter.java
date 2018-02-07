package com.huapu.huafen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.List;

/**
 * Created by admin on 2016/11/14.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {

    private final static int[] defaultColors = new int[]{Color.parseColor("#F7F9FB"), Color.parseColor("#eeeeee")};
    public int filterId;
    public int currentIndex;
    public boolean isNeedSetItemBackground = true;
    public boolean showTips = false;
    public boolean isNeedsUnderLine;//初始化布局是否需要下划线
    private Context mContext;
    private List<FilterAreaData> data;
    private int[] colors = defaultColors;
    private OnItemClickListener listener;

    public FilterAdapter(Context context, List<FilterAreaData> data) {
        this.mContext = context;
        this.data = data;
    }

    public FilterAdapter(Context context) {
        this(context, null);
    }

    public void setItemColors(int[] colors) {
        this.colors = colors;
    }

    public void setData(List<FilterAreaData> data) {
        this.data = data;
        setCheckItemByPosition(-1);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getDid();
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new FilterHolder(LayoutInflater.from(mContext).
                inflate(R.layout.filter_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(FilterHolder filterHolder, final int position) {
        final FilterAreaData filterData = data.get(position);
        filterHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(filterData, position);
                }

            }
        });
        String name = filterData.getName();
        if (!TextUtils.isEmpty(name)) {
            filterHolder.tvTitle.setText(name);
        }


//        if (filterData.isShowLocationIcon) {
//            filterHolder.ivLocation.setVisibility(View.VISIBLE);
//        } else {
//            filterHolder.ivLocation.setVisibility(View.GONE);
//        }

        if (filterData.isCheck) {//选中
            if (isNeedSetItemBackground) {
                filterHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                filterHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_color));
            } else {
                filterHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
//                filterHolder.underLine.setVisibility(View.VISIBLE);
//                filterHolder.underLine.setBackgroundColor(mContext.getResources().getColor(R.color.base_pink));

                filterHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_color));
            }

            if (showTips) {
                filterHolder.selected.setVisibility(View.VISIBLE);
            } else {
                filterHolder.selected.setVisibility(View.GONE);
            }

            currentIndex = position;
        } else {
            filterHolder.selected.setVisibility(View.GONE);
            filterHolder.itemView.setBackgroundColor(colors[0]);
            filterHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
//            if (isNeedsUnderLine) {
//                filterHolder.underLine.setVisibility(View.VISIBLE);
//                filterHolder.underLine.setBackgroundColor(mContext.getResources().getColor(R.color.base_tab_bar_divider));
//            } else {
//                filterHolder.underLine.setVisibility(View.INVISIBLE);
//            }

        }
    }

    public void setCheckItemByPosition(int position) {
        if (!ArrayUtil.isEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                FilterAreaData item = data.get(i);
                if (i == position) {
                    item.isCheck = true;
                } else {
                    item.isCheck = false;
                }
            }
            notifyDataSetChanged();
        }
    }

    public boolean isDC(int position) {
        if (!ArrayUtil.isEmpty(data) && position < data.size() && position >= 0) {
            FilterAreaData item = data.get(position);
            return item.getDc() == 1;
        }
        return false;
    }

    public FilterAreaData getItem(int position) {
        if (ArrayUtil.isEmpty(data) || position >= data.size() || position < 0) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(FilterAreaData filterData, int position);
    }

    public class FilterHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public View underLine;
        public ImageView ivLocation;
        public View selected;

        public FilterHolder(View itemView) {
            super(itemView);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.underLine = itemView.findViewById(R.id.underLine);
            this.ivLocation = (ImageView) itemView.findViewById(R.id.ivLocation);
            this.selected = itemView.findViewById(R.id.selected);
        }
    }


}
