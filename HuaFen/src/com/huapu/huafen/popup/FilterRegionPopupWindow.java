package com.huapu.huafen.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.Region;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.views.FilterRegionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/11/14.
 */
public class FilterRegionPopupWindow extends BaseNougatFixPopupWindow implements
        View.OnClickListener, FilterRegionView.OnDismissListener, FilterRegionView.OnItemDataSelect {

    private Context mContext;

    private View blankSpace;

    private FilterRegionView favFilter;


    public FilterRegionPopupWindow(Context context, OnItemDataSelect onItemDataSelect){
        this.mContext = context;
        this.mOnItemDataSelect = onItemDataSelect;
        init();
    }


    private void init(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_filter_layout, null);
        setContentView(view);
//        view.getBackground().setAlpha(150);
        favFilter = (FilterRegionView) view.findViewById(R.id.favFilter);
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


    public void setData(ArrayList<Region> data){
        String json = JSON.toJSONString(data);
        List<FilterAreaData> list = JSON.parseArray(json, FilterAreaData.class);
        FilterAreaData filterAreaData =findLocationCityByFilterAreaData(list);
        if(filterAreaData!=null){
            list.add(0,filterAreaData);
        }
        favFilter.setData(list);
    }

    private FilterAreaData findLocationCityByFilterAreaData(List<FilterAreaData> list){
        LocationData locationData = CommonPreference.getLocalData();
        FilterAreaData locationCity = null;
        if(locationData!=null){
            String locCity = locationData.city;
            for (FilterAreaData r : list) {
                if (r.getDc() == 1) {
                    if(r.getName().equals(locCity)){
                        locationCity = new FilterAreaData();
                        locationCity.setDistricts(r.getDistricts());
                        locationCity.setDid(r.getDid());
                        locationCity.setName(r.getName());
                        locationCity.setDc(r.getDc());
                        locationCity.isShowLocationIcon = true;
                        break;
                    }
                } else {
                    ArrayList<FilterAreaData> cities = r.getCities();
                    if(cities!=null){
                        for(FilterAreaData c:cities){
                            if(c.getName().equals(locCity)){
                                locationCity = new FilterAreaData();
                                locationCity.setCities(c.getDistricts());
                                locationCity.setDid(c.getDid());
                                locationCity.setName(c.getName());
                                locationCity.setDc(c.getDc());
                                locationCity.isLocationCity = true;
                                locationCity.provinceDid = r.getDid();
                                locationCity.isShowLocationIcon = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return locationCity;
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

    public void setDefaultCheck(int...params){
        favFilter.setDefaultCheck(params);
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
    public void onDataSelected(int[] dids,String text) {
        if(mOnItemDataSelect!=null){
            mOnItemDataSelect.onDataSelected(dids,text);
        }
    }

    public interface OnItemDataSelect{
        void onDataSelected(int[] dids,String text);
    }


}
