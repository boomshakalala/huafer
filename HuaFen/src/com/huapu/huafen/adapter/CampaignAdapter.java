package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ReleaseActivity;
import com.huapu.huafen.beans.Campaign;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/7/24.
 */

public class CampaignAdapter extends RecyclerView.Adapter <CampaignAdapter.CampaignViewHolder> {


    private List<Campaign> data;

    private Context context;

    public CampaignAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Campaign> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public CampaignViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CampaignViewHolder(LayoutInflater.from(context).inflate(R.layout.campaign_item,parent,false));
    }

    @Override
    public void onBindViewHolder(CampaignViewHolder holder, int position) {
        final Campaign item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReleaseActivity.class);
                intent.putExtra(MyConstants.EXTRA_CAMPAIGN_BEAN, item);
                intent.putExtra(MyConstants.DRAFT_TYPE,2);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

        holder.campaignItem.setImageURI(item.getJoinBtn().getIcon());
    }

    @Override
    public int getItemCount() {
        return data == null ?0:data.size();
    }

    public class CampaignViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.campaignItem) SimpleDraweeView campaignItem;

        public CampaignViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
