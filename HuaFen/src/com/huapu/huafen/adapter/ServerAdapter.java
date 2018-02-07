package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.ServerData;
import java.util.List;

/**
 * Created by admin on 2017/1/4.
 */

public class ServerAdapter extends CommonWrapper<ServerAdapter.ServerViewHolder> {

    private List<ServerData> data;
    private Context context;

    public ServerAdapter(Context context,List<ServerData> data){
        this.context = context;
        this.data = data;
    }

    public ServerAdapter(Context context){
        this(context,null);
    }

    public void setData(List<ServerData> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ServerViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_server,parent,false));
    }

    @Override
    public void onBindViewHolder(ServerViewHolder holder, int position) {
        final ServerData item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setCheck(item);
                if(onCheckListener!=null){
                    onCheckListener.onCheck(item);
                }
            }
        });
        holder.tvName.setText(item.name);
        holder.etInput.setText(item.devUrl);
        holder.ivCheck.setImageResource(item.isCheck?
                R.drawable.icon_free_delivery_select:R.drawable.icon_free_delivery_normal);



    }

    private void setCheck(ServerData serverData){
        for(ServerData d:data){
            if(d == serverData){
                d.isCheck = true;
            }else{
                d.isCheck = false;
            }
        }

        notifyWrapperDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }



    public static class ServerViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public ImageView ivCheck;
        public TextView etInput;


        public ServerViewHolder(View itemView) {
            super(itemView);
            this.tvName = (TextView) itemView.findViewById(R.id.tvName);
            this.ivCheck = (ImageView) itemView.findViewById(R.id.ivCheck);
            this.etInput = (TextView) itemView.findViewById(R.id.etInput);
        }


    }

    public interface OnCheckListener{
        void onCheck(ServerData serverData);
    }

    private OnCheckListener onCheckListener;

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }
}
