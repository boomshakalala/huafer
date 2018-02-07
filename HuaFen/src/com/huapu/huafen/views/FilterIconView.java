package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ThemeIconFilterAdapter;
import com.huapu.huafen.beans.IconFilter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/9/25.
 */

public class FilterIconView extends LinearLayout {

    @BindView(R.id.recyclericon)
    RecyclerView recycler;
    private ThemeIconFilterAdapter iconfilterAdapter;

    private OnItemClickListener listener;

    public FilterIconView(@NonNull Context context) {
        this(context, null);
    }

    public FilterIconView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.filter_icon_view, this, true);
        ButterKnife.bind(this);
        LinearLayoutManager recyclerManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(recyclerManager);
        iconfilterAdapter = new ThemeIconFilterAdapter(getContext());
        recycler.setAdapter(iconfilterAdapter);
        iconfilterAdapter.setOnItemClickListener(new ThemeIconFilterAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(IconFilter fi) {
                if (listener != null) {
                    listener.onItemClick(fi);
                }
            }
        });
    }

    public void setData(List<IconFilter> data) {
        iconfilterAdapter.setData(data);
    }


    public interface OnItemClickListener {
        void onItemClick(IconFilter fi);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}