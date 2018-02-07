package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.ClassificationResult;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/11.
 */

public class LeftClassificationAdapter extends RecyclerView.Adapter<LeftClassificationAdapter.LeftClassificationViewHolder> {

    private Context context;
    private List<ClassificationResult.Indice> data;

    public LeftClassificationAdapter(Context context, List<ClassificationResult.Indice> data) {
        this.context = context;
        this.data = data;
    }

    public LeftClassificationAdapter(Context context) {
        this(context, null);
    }

    public void setData(List<ClassificationResult.Indice> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setData(List<ClassificationResult.Indice> data, String selected) {
        this.data = data;
        setCheck(selected);
        notifyDataSetChanged();
    }

    public void setGuideData(List<ClassificationResult.Indice> data, String selected) {
        this.data = data;
        //setCheck(selected);
        for (int i = 0; i < data.size(); i++) {
            ClassificationResult.Indice item = data.get(i);
            item.isCheck = selected.equals(item.title);
            if (item.isCheck) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public LeftClassificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LeftClassificationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_class_layout1, parent, false));
    }

    @Override
    public void onBindViewHolder(LeftClassificationViewHolder holder, final int position) {
        final ClassificationResult.Indice item = data.get(position);
        if (item != null) {
            if (!TextUtils.isEmpty(item.title)) {
                holder.tvTitle.setText(item.title);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    setCheck(position);
                    LogUtil.e("LeftClassificationAdapter", "position=" + position + ",item==" + item.toString());
                }
            });

            if (item.isCheck) {
                holder.leftView.setVisibility(View.VISIBLE);
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.leftView.setVisibility(View.GONE);
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.class_layout_item_bg));
            }

        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class LeftClassificationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.leftView)
        View leftView;
        @BindView(R2.id.tvTitle)
        TextView tvTitle;

        public LeftClassificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private void setCheck(int position) {
        if (!ArrayUtil.isEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                ClassificationResult.Indice item = data.get(i);
                boolean isCheck = i == position ? true : false;
                item.isCheck = isCheck;
                if (item.isCheck) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(item);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    private void setCheck(String selected) {
        if (!ArrayUtil.isEmpty(data)) {

            ArrayList<ClassificationResult.Indice> list = new ArrayList<ClassificationResult.Indice>();

            for (int i = 0; i < data.size(); i++) {
                ClassificationResult.Indice item = data.get(i);
                boolean isCheck = (!TextUtils.isEmpty(item.key) && !TextUtils.isEmpty(selected) && item.key.equals(selected)) ? true : false;
                item.isCheck = isCheck;
                if (item.isCheck) {
                    list.add(item);
                }
            }
            if (list.size() > 0) {
                ClassificationResult.Indice item = list.get(list.size() - 1);
                if (mOnItemClickListener != null) {
                    //mOnItemClickListener.onItemClick(item);
                    LogUtil.d("danielluan", item.title);

                }
            }
            notifyDataSetChanged();
        }

    }


    public interface OnItemClickListener {
        void onItemClick(ClassificationResult.Indice item);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
