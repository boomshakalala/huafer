package com.huapu.huafen.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.amzing.BaseAmazingAdapter;
import com.huapu.huafen.beans.Brand;
import com.huapu.huafen.utils.Pair;
import java.util.ArrayList;

/**
 * Created by admin on 2017/3/24.
 */

public class BrandListAdapter extends BaseAmazingAdapter<Brand> {

    private Context context;
    private OnBrandClickListener onItemListener;

    public BrandListAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Pair<String, ArrayList<Brand>>> data){
        this.data=data;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if(data!=null){
            return super.getCount();
        }else{
            return 0;
        }
    }

    @Override
    protected void bindSectionHeader(View view, int position, String section, boolean displaySectionHeader) {
        View header = view.findViewById(R.id.ll_header);
        TextView lSectionHeader= (TextView) header.findViewById(R.id.tvBrandTitle);
        if(displaySectionHeader){
            header.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(section)){
                lSectionHeader.setText(section);
            }
        }else{
            header.setVisibility(View.GONE);
        }
    }

    @Override
    public View getAmazingView(final int position, View convertView, final ViewGroup parent, final String section) {
        final Brand item = getItem(position);
        ViewHolder holder;
        if (convertView==null) {
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.brand_item,null);
            holder.tvBrand= (TextView) convertView.findViewById(R.id.tvBrand);
            convertView.setTag(holder);

        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        if(!TextUtils.isEmpty(item.brandName)){
            holder.tvBrand.setText(item.brandName);
        }


        holder.tvBrand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(onItemListener!=null){
                    onItemListener.onItemClick(item);
                }

            }
        });
        return convertView;
    }

    @Override
    protected void configurePinnedHeader(View header, int position, String section, int alpha) {
        if(header!=null){
            TextView lSectionHeader=(TextView) header.findViewById(R.id.tvBrandTitle);
            if(!TextUtils.isEmpty(section)){
                lSectionHeader.setText(section);
            }
        }
    }


    public  void setOnBrandListener(OnBrandClickListener onItemListener){
        this.onItemListener=onItemListener;
    }

    public interface OnBrandClickListener{
        void onItemClick(Brand brand);
    }

    private static class ViewHolder{
        public TextView tvBrand;
    }
}
