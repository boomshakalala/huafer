package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.CommentListBean;
import com.huapu.huafen.beans.District;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liang on 2016/10/26.
 */
public class DistrictListAdapter extends CommonWrapper<DistrictListAdapter.ProvinceListHolder>{

    private Context mContext;
    private List<District> datas = new ArrayList<District>();

    public DistrictListAdapter(Context context, List<District> datas){
        super();
        this.mContext = context;
        this.datas = datas;
    }

    public void setData(ArrayList<District> datas){
        if(datas==null){
            datas = new ArrayList<District>();
        }
        this.datas = datas;
    }

    public void addAll(ArrayList<District> datas){
        if(datas==null){
            datas = new ArrayList<District>();
        }
        this.datas.addAll(datas);
    }

    public boolean isEmpty(){
        return ArrayUtil.isEmpty(datas);
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
        final District district = datas.get(position);
        if(district == null) {
            return;
        }
        viewHolder.tvName.setText(district.getName());
        viewHolder.ivArrow.setVisibility(View.GONE);
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MyConstants.EXTRA_DISTRICT, datas.get(position));
                ((Activity)mContext).setResult(Activity.RESULT_OK, intent);
                ((Activity)mContext).finish();
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
        private ImageView ivArrow;


        public ProvinceListHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ivArrow = (ImageView) itemView.findViewById(R.id.ivArrow);
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
