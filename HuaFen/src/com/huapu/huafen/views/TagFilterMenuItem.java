package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.FilterTag;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/10/1.
 */

public class TagFilterMenuItem extends LinearLayout {

    @BindView(R.id.recyclerView)
    RecyclerView recycler;
    @BindView(R.id.tagText)
    TextView tag;

    private String key = "";
    private String value = "";
    private FilterTagAdapter tagAdapter;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String key, String value);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public TagFilterMenuItem(@NonNull Context context) {
        this(context, null);
    }

    public TagFilterMenuItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.filter_menu_item, this, true);
        ButterKnife.bind(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recycler.setLayoutManager(gridLayoutManager);
        tagAdapter = new FilterTagAdapter(getContext());
        recycler.setAdapter(tagAdapter);

    }

    public void clear() {

        if (tagAdapter != null) {
            tagAdapter.clearChecks();
        }
    }

    public void setData(List<FilterTag> data) {
        tagAdapter.setData(data);
    }


    public void setData(List<FilterTag> data, boolean mutiple) {
        if (tagAdapter != null) {
            tagAdapter.setMutiple(mutiple);
            tagAdapter.setData(data);
        }
    }

    public String getKey() {
        return key;
    }

    public void setTitle(String title) {
        tag.setText(title);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public class FilterPriceHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView tvPrice;

        public FilterPriceHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }


    public class FilterTagAdapter extends RecyclerView.Adapter<FilterPriceHolder> {


        private List<FilterTag> data;
        private boolean mutiple;
        private Context context;


        public FilterTagAdapter(Context context) {
            this.context = context;
        }

        @Override
        public FilterPriceHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new FilterPriceHolder(LayoutInflater.from(context).inflate(R.layout.item_filter_price, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(FilterPriceHolder filterPriceHolder, final int position) {
            final FilterTag item = data.get(position);
            filterPriceHolder.itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    item.isCheck = !item.isCheck;
                    setCheck(position, item.isCheck);
                }
            });

            filterPriceHolder.tvPrice.setText(item.getTitle());

            if (item.isCheck) {
                filterPriceHolder.tvPrice.setBackgroundResource(R.drawable.filter_price_pink);
                filterPriceHolder.tvPrice.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                filterPriceHolder.tvPrice.setBackgroundResource(R.drawable.filter_price_light_grey);
                filterPriceHolder.tvPrice.setTextColor(Color.parseColor("#333333"));
            }

            if (position % 4 == 3) {
                filterPriceHolder.itemView.setPadding(0, CommonUtils.dp2px(10), 0, 0);
            } else {
                filterPriceHolder.itemView.setPadding(0, CommonUtils.dp2px(10), CommonUtils.dp2px(5), 0);
            }
        }


        public void clearChecks() {
            if (ArrayUtil.isEmpty(data)) {
                return;
            }
            for (FilterTag tag : data) {
                tag.clean();
            }

            notifyDataSetChanged();
        }

        public void setCheck(int position, boolean isCheck) {
            if (ArrayUtil.isEmpty(data)) {
                setValue("");
                return;
            }

            if (!isMutiple()) {
                for (int i = 0; i < data.size(); i++) {
                    FilterTag tag = data.get(i);
                    if (position == i) {
                        tag.isCheck = isCheck;
                    } else {
                        tag.isCheck = false;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (FilterTag tag : data) {
                if (tag.isCheck) {
                    sb.append(tag.getValue()).append(",");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            setValue(sb.toString());
            if (listener != null) {
                listener.onItemClick(getKey(), getValue());
            }
            //params.put("tags", sb.toString());
            //LogUtil.e("FilterView", params.get("tags") + "");
            notifyDataSetChanged();
        }


        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }


        public void setData(List<FilterTag> data) {
            this.data = data;
            //init selected
            for (int i = 0; i < data.size(); i++) {
                FilterTag ft = data.get(i);
                if (ft.getCheck()) {
                    setCheck(i, true);
                }
            }
            notifyDataSetChanged();
        }

        public boolean isMutiple() {
            return mutiple;
        }

        public void setMutiple(boolean mutiple) {
            this.mutiple = mutiple;
        }

    }
}
