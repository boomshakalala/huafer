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
import com.huapu.huafen.activity.DistrictActivity;
import com.huapu.huafen.beans.City;
import com.huapu.huafen.beans.CommentListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liang on 2016/10/26.
 */
public class CityListAdapter extends CommonWrapper<CityListAdapter.ProvinceListHolder>{

    private Context mContext;
    private List<City> datas = new ArrayList<City>();
    private City city;

    public CityListAdapter(Context context, List<City> datas){
        super();
        this.mContext = context;
        this.datas = datas;
    }

    public void setData(ArrayList<City> datas){
        if(datas==null){
            datas = new ArrayList<City>();
        }
        this.datas = datas;
    }

    public City getSelectCity(){
        return city;
    }

    public void setSelectCity(City city) {
        this.city = city;
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
        final City city = datas.get(position);
        if(city == null) {
            return;
        }
        viewHolder.tvName.setText(city.getName());
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectCity(city);
                Intent intent = new Intent(mContext, DistrictActivity.class);
                intent.putExtra(MyConstants.EXTRA_DISTRICTS, city.getDistricts());
                ((Activity)mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_DISTRICTS);
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
