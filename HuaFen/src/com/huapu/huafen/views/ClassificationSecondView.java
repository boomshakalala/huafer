package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.ClassificationSecondAdapter;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.Classification;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/15.
 */

public class ClassificationSecondView extends FrameLayout{

    @BindView(R2.id.recycler) RecyclerView recycler;
    private ClassificationSecondAdapter classificationSecondAdapter;

    public ClassificationSecondView(@NonNull Context context) {
        this(context,null);
    }

    public ClassificationSecondView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.classification_second_layout,this,true);
        ButterKnife.bind(this);
        LinearLayoutManager recyclerManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recycler.setLayoutManager(recyclerManager);
        classificationSecondAdapter = new ClassificationSecondAdapter(getContext());
        recycler.setAdapter(classificationSecondAdapter);
        classificationSecondAdapter.setOnItemClickListener(new ClassificationSecondAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Cat cat) {
                if(listener!=null){
                    listener.onItemClick(cat);
                }
            }
        });
    }

    public void setData(List<Cat> data){
        classificationSecondAdapter.setData(data);
    }


    public interface OnItemClickListener{
        void onItemClick(Cat cat);
    }

    private OnItemClickListener listener;


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
