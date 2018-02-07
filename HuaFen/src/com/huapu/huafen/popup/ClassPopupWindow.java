package com.huapu.huafen.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.views.ClassFilterView;
import java.util.ArrayList;

/**
 * Created by admin on 2016/11/14.
 */
public class ClassPopupWindow extends BaseNougatFixPopupWindow implements
        View.OnClickListener, ClassFilterView.OnDismissListener, ClassFilterView.OnItemDataSelect {

    private Context mContext;
    private View blankSpace;
    private ClassFilterView favFilter;

    public ClassPopupWindow(Context context, OnItemDataSelect onItemDataSelect){
        this.mContext = context;
        this.mOnItemDataSelect = onItemDataSelect;
        init();
    }


    private void init(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.class_pop_filter_layout, null);
        setContentView(view);
        favFilter = (ClassFilterView) view.findViewById(R.id.favFilter);
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


    public void setData(ArrayList<Cat> data){
        favFilter.setData(data);
    }

    private OnItemDataSelect mOnItemDataSelect;

    public void setOnItemDataSelect (OnItemDataSelect onItemDataSelect){
        this.mOnItemDataSelect = onItemDataSelect;
    }

    /****
     * 调用此方法是设置分类默认选中（两级列表选中），如想用bean的id判断，则需重写adapter的getItemId方法，return的
     * 是该javaBean的业务id
     * @param bigCaseId
     * @param smallCaseId
     */

    public void setDefaultCheck(int bigCaseId,int smallCaseId){
        favFilter.setDefaultCheck(bigCaseId,smallCaseId);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() ==blankSpace.getId()){
            this.dismiss();
        }
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        favFilter.resetState();
    }

    @Override
    public void close() {
        this.dismiss();
    }

    @Override
    public void onDataSelected(int[] catId,String text) {
        if(mOnItemDataSelect!=null){
            mOnItemDataSelect.onDataSelected(catId,text);
        }
    }

    public interface OnItemDataSelect{
        void onDataSelected(int[] catId,String text);
    }


}
