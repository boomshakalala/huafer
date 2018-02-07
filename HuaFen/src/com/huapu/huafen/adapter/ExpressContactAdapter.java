package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Express;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.List;

/**
 * 快递电话
 */
public class ExpressContactAdapter extends CommonWrapper<ExpressContactAdapter.ExpressContactHolder> {


    private List<Express> data;

    private Context context;

    public ExpressContactAdapter(Context context, List<Express> data){
        this.context = context;
        this.data = data;
    }

    public ExpressContactAdapter(Context context){
        this(context,null);
    }

    public void  setData(List<Express> data){
        this.data = data;
        notifyWrapperDataSetChanged();
    }


    public void addAll(List<Express> data){
        if(!ArrayUtil.isEmpty(data)){
            this.data.addAll(data);
        }
        notifyWrapperDataSetChanged();
    }
    @Override
    public ExpressContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ExpressContactHolder viewHolder = new ExpressContactHolder(LayoutInflater.from(context).inflate(R.layout.item_express_contact, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExpressContactHolder holder, int position) {
        final Express item = data.get(position);
        if(item == null) {
            return;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(item.getExpressTel())) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + item.getExpressTel());
                    intent.setData(data);
                    context.startActivity(intent);
                }
            }
        });
        holder.tvExpressName.setText(item.getExpressName());
        holder.tvExpressTel.setText(String.valueOf(item.getExpressTel()));
    }


    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public class ExpressContactHolder extends RecyclerView.ViewHolder{

        public TextView tvExpressName;
        public TextView tvExpressTel;

        public ExpressContactHolder(View itemView) {
            super(itemView);
            this.tvExpressName = (TextView) itemView.findViewById(R.id.tvExpressName);
            this.tvExpressTel = (TextView) itemView.findViewById(R.id.tvExpressTel);
        }
    }
}
