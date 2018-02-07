package com.huapu.huafen.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.SortEntity;
import com.huapu.huafen.views.FilterSortView;
import com.huapu.huafen.views.FilterView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/11/14.
 */
public class FilterCaseWindow extends BaseNougatFixPopupWindow implements
        View.OnClickListener, FilterSortView.OnDismissListener, FilterView.OnDismissListener, FilterView.OnConfirmButtonClick {

    private Context mContext;
//    private View blankSpace;
    private FilterView favFilter;

    public FilterCaseWindow(Context context, OnFilterConfirmListener onFilterConfirmListener){
        this.mContext = context;
        this.onFilterConfirmListener = onFilterConfirmListener;
        init();
    }


    private void init(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.filter_case_layout, null);
        setContentView(view);
        favFilter = (FilterView) view.findViewById(R.id.favFilter);
        favFilter.setOnDismissListener(this);
        favFilter.setOnConfirmButtonClick(this);
//        blankSpace=view.findViewById(R.id.blankSpace);
//        blankSpace.setOnClickListener(this);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.pop_search_switch);
    }

    @Override
    public void onClick(View v) {
//        if(v.getId() ==blankSpace.getId()){
//            this.dismiss();
//        }
    }

    @Override
    public void close() {
        this.dismiss();
    }

    @Override
    public void onClick(Map<String, String> params) {
        if(onFilterConfirmListener!=null){
            onFilterConfirmListener.OnFilterConfirm(params);
        }
    }

    public interface OnDismissListener{
        void close();
    }

    private OnDismissListener listener;

    public void setOnDismissListener(OnDismissListener listener){
        this.listener = listener;
    }

    public interface OnFilterConfirmListener{
        void OnFilterConfirm(Map<String, String> params);
    }

    private OnFilterConfirmListener onFilterConfirmListener;

    public void setOnFilterConfirmListener(OnFilterConfirmListener listener){
        this.onFilterConfirmListener = listener;
    }



}
