package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.CityActivity;
import com.huapu.huafen.activity.DistrictActivity;
import com.huapu.huafen.beans.CommentListBean;
import com.huapu.huafen.beans.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liang on 2016/10/26.
 */
public class ProvinceListAdapter extends CommonWrapper<ProvinceListAdapter.ProvinceListHolder>{

    private Context mContext;
    private List<Region> datas = new ArrayList<Region>();
    private Region region;

    public ProvinceListAdapter(Context context, List<Region> datas){
        super();
        this.mContext = context;
        this.datas = datas;
    }

    public void setData(ArrayList<Region> datas){
        if(datas==null){
            datas = new ArrayList<Region>();
        }
        this.datas = datas;
        notifyWrapperDataSetChanged();
    }

    public Region getSelectRegion(){
        return region;
    }

    public void setSelectRegion(Region region) {
        this.region = region;
    }

    @Override
    public ProvinceListHolder onCreateViewHolder(ViewGroup parent, int i) {
        ProvinceListHolder vh = new ProvinceListHolder(LayoutInflater.from(mContext).inflate(R.layout.item_listview_province, parent, false));
        return vh;
    }
    @Override
    public void onBindViewHolder(ProvinceListHolder viewHolder, final int position) {
        if(datas.get(position) == null) {
            return;
        }
        final Region region = datas.get(position);
        if(region == null) {
            return;
        }
        viewHolder.tvName.setText(region.getName());
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectRegion(region);
                Intent intent = new Intent();
                if(region.getDc() == 1) { // 直辖市
                    intent = new Intent(mContext, DistrictActivity.class);
                    intent.putExtra(MyConstants.EXTRA_DISTRICTS, region.getDistricts());
                    ((Activity)mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_DISTRICTS);
                } else {
                    intent = new Intent(mContext, CityActivity.class);
                    intent.putExtra(MyConstants.EXTRA_CITIES, region.getCities());
                    ((Activity)mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_CITIES);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ProvinceListHolder extends RecyclerView.ViewHolder{
        private View item;
        private TextView tvName;


        public ProvinceListHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(CommentListBean comment);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

}
