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
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/15.
 */

public class ClassificationSecondAdapter extends RecyclerView.Adapter<ClassificationSecondAdapter.ClassificationSecondViewHolder> {

    private final static int[] defaultColors = new int[]{Color.parseColor("#F7F9FB"), Color.parseColor("#FFFFFF")};
    private Context mContext;
    private List<Cat> data;
    private int[] colors = defaultColors;
    private OnItemClickListener listener;

    private boolean simpleUI;

    public ClassificationSecondAdapter(Context context, List<Cat> data) {
        this.mContext = context;
        this.data = data;
    }

    public void setSimpleUI(boolean simpleUI) {
        this.simpleUI = simpleUI;
    }

    public ClassificationSecondAdapter(Context context) {
        this(context, null);
    }

    public void setData(List<Cat> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ClassificationSecondViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ClassificationSecondViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.filter_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ClassificationSecondViewHolder holder, final int position) {
        final Cat item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setCheck(position);
                if (listener != null) {
                    listener.onItemClick(item);
                }

            }
        });
        String name = item.getName();
        if (!TextUtils.isEmpty(name)) {
            holder.tvTitle.setText(name);
        }

//        holder.underLine.setVisibility(View.VISIBLE);


        if (item.isCheck) {//选中
//            holder.underLine.setBackgroundColor(mContext.getResources().getColor(R.color.base_pink));
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_color));
            if (!simpleUI) {
                holder.selected.setVisibility(View.VISIBLE);
                holder.itemView.setBackgroundColor(colors[1]);
            }
        } else {
            if (!simpleUI) {
                holder.itemView.setBackgroundColor(colors[0]);
                holder.selected.setVisibility(View.GONE);
            }
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
//            holder.underLine.setBackgroundColor(mContext.getResources().getColor(R.color.base_tab_bar_divider));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    private void setCheck(int position) {
        if (!ArrayUtil.isEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                if (i == position) {
                    data.get(i).isCheck = true;
                } else {
                    data.get(i).isCheck = false;
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Cat cat);
    }

    class ClassificationSecondViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tvTitle)
        TextView tvTitle;
        @BindView(R2.id.underLine)
        View underLine;
        @BindView(R2.id.selected)
        View selected;

        ClassificationSecondViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
