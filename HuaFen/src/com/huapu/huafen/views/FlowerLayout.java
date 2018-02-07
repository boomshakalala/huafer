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
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.FlowerNewActivity;
import com.huapu.huafen.activity.PickArticleModeActivity;
import com.huapu.huafen.adapter.HorizontalArticleAdapter;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/6/8.
 */

public class FlowerLayout extends LinearLayout {

    @BindView(R2.id.tvMore) TextView tvMore;
    @BindView(R2.id.recyclerView) RecyclerView recyclerView;
    @BindView(R2.id.emptyPlaceHolder) SimpleDraweeView emptyPlaceHolder;
    private HorizontalArticleAdapter adapter ;

    public FlowerLayout(Context context) {
        this(context, null);
    }

    public FlowerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_flower, this, true);
        ButterKnife.bind(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    public void setData(List<Item> list, final long userId) {
        if(!ArrayUtil.isEmpty(list)){
            recyclerView.setVisibility(VISIBLE);
            emptyPlaceHolder.setVisibility(GONE);
            tvMore.setVisibility(VISIBLE);
        }else{
            recyclerView.setVisibility(GONE);
            tvMore.setVisibility(GONE);
            long myUserId = CommonPreference.getUserId();
            if(myUserId > 0 && myUserId == userId){
                emptyPlaceHolder.setVisibility(VISIBLE);
            }else{
                this.setVisibility(GONE);
            }
        }
        adapter = new HorizontalArticleAdapter(getContext(),userId);
        recyclerView.setAdapter(adapter);
        adapter.setData(list);

        tvMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FlowerNewActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, userId);
                getContext().startActivity(intent);
            }
        });

        emptyPlaceHolder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), PickArticleModeActivity.class));
            }
        });
    }
}
