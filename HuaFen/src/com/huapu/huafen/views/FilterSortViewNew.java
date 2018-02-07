package com.huapu.huafen.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.SortAdapter;
import com.huapu.huafen.adapter.SortAdapterNew;
import com.huapu.huafen.beans.SortEntity;

/**
 * Created by admin on 2016/11/17.
 */
public class FilterSortViewNew extends FrameLayout{

    private RecyclerView recycler;
    private SortAdapterNew adapter;

    public FilterSortViewNew(Context context) {
        super(context);
    }

    public FilterSortViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater inflater=(LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sort_filter_layout,this,true);

        LinearLayoutManager recyclerManager = new LinearLayoutManager(getContext());
        recyclerManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(recyclerManager);

        SortEntity[] data = new SortEntity[]{
                new SortEntity(0, "默认排序",true),
                new SortEntity(-3, "收藏最多"),
                new SortEntity(-2, "最新发布"),
                new SortEntity(4, "距离最近")
                };

        adapter = new SortAdapterNew(getContext(),data);

        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new SortAdapterNew.OnItemClickListener() {

            @Override
            public void onItemClick(SortEntity filterData, int position) {
                adapter.setCheckItemByPosition(position);
                if(mOnItemDataSelect!=null){
                    mOnItemDataSelect.onDataSelected(filterData);
                }
                if(listener!=null){
                    listener.close();
                }
            }
        });

    }

    public interface OnDismissListener{
        void close();
    }

    private OnDismissListener listener;

    public void setOnDismissListener(OnDismissListener listener){
        this.listener = listener;
    }

    private OnItemDataSelect mOnItemDataSelect;

    public void setOnItemDataSelect (OnItemDataSelect onItemDataSelect){
        this.mOnItemDataSelect = onItemDataSelect;
    }

    public interface OnItemDataSelect{
        void onDataSelected(SortEntity data);
    }
}
