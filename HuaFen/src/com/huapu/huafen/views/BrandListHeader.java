package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.Brand;
import com.huapu.huafen.views.flowlayout.FlowLayout;
import com.huapu.huafen.views.flowlayout.TagAdapter;
import com.huapu.huafen.views.flowlayout.TagFlowLayout;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/3/25.
 */

public class BrandListHeader extends LinearLayout{

    @BindView(R2.id.tagFlowLayout) TagFlowLayout tagFlowLayout;


    public BrandListHeader(Context context) {
        this(context,null);
    }

    public BrandListHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setBackgroundColor(context.getResources().getColor(R.color.white));
        LayoutInflater.from(context).inflate(R.layout.brand_list_header,this,true);
        ButterKnife.bind(this);
    }

    public void setData(final List<Brand> brandList){

        tagFlowLayout.setAdapter(new TagAdapter<Brand>(brandList) {

            @Override
            public View getView(FlowLayout parent, int position, Brand brand) {
                TextView tag = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tag_layout,
                        tagFlowLayout, false);
                tag.setText(brand.brandName);
                return tag;
            }
        });
        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {

            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Brand item = brandList.get(position);
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(item);
                }
                return false;
            }
        });
    }

    public interface OnItemClickListener{
        void onItemClick(Brand brand);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
