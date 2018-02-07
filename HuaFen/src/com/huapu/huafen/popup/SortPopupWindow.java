package com.huapu.huafen.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.SortEntity;
import com.huapu.huafen.views.ClassFilterView;
import com.huapu.huafen.views.FilterSortView;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/14.
 */
public class SortPopupWindow extends BaseNougatFixPopupWindow implements
        View.OnClickListener, FilterSortView.OnDismissListener, FilterSortView.OnItemDataSelect {

    private Context mContext;
    private View blankSpace;
    private FilterSortView favFilter;

    public SortPopupWindow(Context context, OnItemDataSelect onItemDataSelect){
        this.mContext = context;
        this.mOnItemDataSelect = onItemDataSelect;
        init();
    }


    private void init(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.sort_pop_filter_layout, null);
        setContentView(view);
        favFilter = (FilterSortView) view.findViewById(R.id.favFilter);
        favFilter.setOnDismissListener(this);
        favFilter.setOnItemDataSelect(this);
        blankSpace=view.findViewById(R.id.blankSpace);
        blankSpace.setOnClickListener(this);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.pop_search_switch);
    }

    private OnItemDataSelect mOnItemDataSelect;

    public void setOnItemDataSelect (OnItemDataSelect onItemDataSelect){
        this.mOnItemDataSelect = onItemDataSelect;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() ==blankSpace.getId()){
            this.dismiss();
        }
    }

    @Override
    public void close() {
        this.dismiss();
    }

    @Override
    public void onDataSelected(SortEntity data) {
        if(mOnItemDataSelect!=null){
            mOnItemDataSelect.onDataSelected(data);
        }
    }

    public interface OnItemDataSelect{
        void onDataSelected(SortEntity data);
    }


}
