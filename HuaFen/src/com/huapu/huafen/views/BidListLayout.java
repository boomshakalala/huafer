package com.huapu.huafen.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.BidRecordListActivity;
import com.huapu.huafen.adapter.BidRecordAdapter;
import com.huapu.huafen.beans.BidRecord;
import com.huapu.huafen.layoutmanager.LinearLayoutManagerPlus;
import com.huapu.huafen.utils.LogUtil;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/8/2.
 */

public class BidListLayout extends LinearLayout {

    @BindView(R.id.tvCount) TextView tvCount;
    @BindView(R.id.bidList) RecyclerView bidList;
    @BindView(R.id.tvMore) TextView tvMore;
    private BidRecordAdapter adapter ;

    public BidListLayout(Context context) {
        this(context,null);
    }

    public BidListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.bid_list_layout, this, true);
        ButterKnife.bind(this);
        LinearLayoutManagerPlus linearLayoutManager = new LinearLayoutManagerPlus(getContext(), LinearLayoutManager.VERTICAL, false);
        bidList.setLayoutManager(linearLayoutManager);
        adapter = new BidRecordAdapter(getContext());
        bidList.setAdapter(adapter);
    }


    public void setData(final long goodsId , List<BidRecord> list, int count, final int goodsState){
        LogUtil.e("BidListLayout","setData");
        adapter.setGoodsState(goodsState);
        adapter.setData(list);
        tvCount.setText(String.valueOf(count));
        tvMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BidRecordListActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID,goodsId);
                intent.putExtra(MyConstants.GOODS_STATE,goodsState);
                getContext().startActivity(intent);
            }
        });
    }


}
