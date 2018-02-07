package com.huapu.huafen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.List;

/**
 * Created by admin on 2016/11/14.
 */
public class ClassFilterAdapter extends RecyclerView.Adapter<ClassFilterAdapter.FilterHolder> {

    private final static int[] defaultColors = new int[]{Color.parseColor("#F7F9FB"), Color.parseColor("#FFFFFF")};
    public int currentIndex;
    public boolean isNeedSetItemBackground = true;
    public boolean showTips = false;
    public boolean isNeedsUnderLine;//初始化布局是否需要下划线
    private Context mContext;
    private List<Cat> data;
    private int[] colors = defaultColors;
    private OnItemClickListener listener;

    public ClassFilterAdapter(Context context, List<Cat> data) {
        this.mContext = context;
        this.data = data;
    }

    public ClassFilterAdapter(Context context) {
        this(context, null);
    }

    public void setItemColors(int[] colors) {
        this.colors = colors;
    }

    public void setData(List<Cat> data) {
        this.data = data;
        setCheckItemByPosition(-1);
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new FilterHolder(LayoutInflater.from(mContext).
                inflate(R.layout.filter_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(FilterHolder filterHolder, final int position) {
        final Cat filterData = data.get(position);
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

        if (filterData.isCheck) {//选中
            if (isNeedSetItemBackground) {
                filterHolder.itemView.setBackgroundColor(colors[1]);
            } else {
                filterHolder.itemView.setBackgroundColor(colors[0]);
//                filterHolder.underLine.setVisibility(View.VISIBLE);
//                filterHolder.underLine.setBackgroundColor(mContext.getResources().getColor(R.color.base_pink));
            }

            if (showTips) {
                filterHolder.selected.setVisibility(View.VISIBLE);
            } else {
                filterHolder.selected.setVisibility(View.GONE);
            }

            filterHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_color));
            currentIndex = position;
        } else {
            filterHolder.selected.setVisibility(View.GONE);
            filterHolder.itemView.setBackgroundColor(colors[0]);
            filterHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
            if (isNeedsUnderLine) {
                filterHolder.underLine.setVisibility(View.VISIBLE);
                filterHolder.underLine.setBackgroundColor(mContext.getResources().getColor(R.color.base_tab_bar_divider));
            } else {
                filterHolder.underLine.setVisibility(View.INVISIBLE);
            }

        }

    }

    public void setCheckItemByPosition(int position) {
        if (!ArrayUtil.isEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                Cat item = data.get(i);
                if (i == position) {
                    item.isCheck = true;
                } else {
                    item.isCheck = false;
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getCid();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public Cat getItem(int position) {
        if (ArrayUtil.isEmpty(data) || position >= data.size() || position < 0) {
            return null;
        }
        return data.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(Cat filterData, int position);
    }

    public class FilterHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public TextView tvTitle;
        public View underLine;
        public View selected;

        public FilterHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.underLine = itemView.findViewById(R.id.underLine);
            this.selected = itemView.findViewById(R.id.selected);
        }
    }


}
