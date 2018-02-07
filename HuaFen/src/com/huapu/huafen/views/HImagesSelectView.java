package com.huapu.huafen.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.HSelectImageAdapter;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.VideoBean;
import com.huapu.huafen.itemdecoration.HorizontalItemDecoration;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/8.
 */
public class HImagesSelectView extends FrameLayout implements HSelectImageAdapter.OnSelectedImagesListener, View.OnClickListener {

    private final static String TAG = HImagesSelectView.class.getSimpleName();

    private RecyclerView recyclerGallery;
    private HSelectImageAdapter adapter;
    private ImageView ibAdd;

    public HImagesSelectView(Context context) {
        this(context,null);

    }

    public HImagesSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater inflater=(LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_selected_layout,this,true);

        ibAdd = (ImageView) findViewById(R.id.ibAdd);

        ibAdd.setOnClickListener(this);

        recyclerGallery = (RecyclerView) findViewById(R.id.recyclerGallery);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerGallery.setLayoutManager(layoutManager);
        recyclerGallery.addItemDecoration(new HorizontalItemDecoration());
        adapter = new HSelectImageAdapter(getContext());
        adapter.setOnSelectedImagesListener(this);
        recyclerGallery.setAdapter(adapter);

    }

    @Override
    public void onChange(boolean visible) {
        ibAdd.setVisibility(visible? View.VISIBLE:View.GONE);
        if(visible){
            recyclerGallery.setPadding(0,0, ibAdd.getWidth(),0);
        }else{
            recyclerGallery.setPadding(0,0,0,0);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == ibAdd.getId()){
            if(adapter!=null){
                adapter.startAlbumActivityForResult();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(adapter!=null){
            adapter.onActivityResult(requestCode,resultCode,data);
        }
    }

    public ArrayList<ImageItem> getSelectImg(){
        return adapter.getSelectImg();
    }
    public void setSelectImages(ArrayList<ImageItem> selectImages){
        adapter.setSelectImages(selectImages);
    }
    public void setVideoBean(VideoBean videoBean){
        adapter.setVideoBean(videoBean);
    }
    public String getVideoUrl(){
        return adapter.getVideoUrl();
    }

    public boolean isHasVideo(){
        return !TextUtils.isEmpty(adapter!=null?adapter.getVideoUrl():"");
    }

}
