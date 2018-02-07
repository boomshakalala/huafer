package com.huapu.huafen.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.dialog.EditDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/17.
 */

public class BrandAddHeader extends FrameLayout{

    @BindView(R2.id.tvAddBrand) TextView tvAddBrand;
    @BindView(R2.id.tvAdd) TextView tvAdd;
    private String brandAdd;

    public BrandAddHeader(Context context) {
        this(context,null);
    }

    public BrandAddHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.brand_add_layout,this,true);
        ButterKnife.bind(this);
        tvAddBrand.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                EditDialog dialog = new EditDialog(getContext());
                dialog.setOnBrandAdded(new EditDialog.OnBrandAdded() {

                    @Override
                    public void onBrandAdded(String brand) {
                        brandAdd = brand;
                        tvAddBrand.setText(brand);
                        tvAdd.setEnabled(true);
                        tvAdd.setBackgroundResource(R.drawable.pink_border_rect);
                        tvAdd.setTextColor(getContext().getResources().getColor(R.color.base_pink));
                    }
                });
                dialog.show();
            }
        });




        tvAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if(mOnBrandAdded!=null&&!TextUtils.isEmpty(brandAdd)){
                    mOnBrandAdded.onBrandAdded(brandAdd);
                }
            }
        });

        tvAdd.setEnabled(false);
    }

    public interface OnBrandAdded{
        void onBrandAdded(String brand);
    }

    private OnBrandAdded mOnBrandAdded;

    public void setOnBrandAdded(OnBrandAdded onBrandAdded) {
        this.mOnBrandAdded = onBrandAdded;
    }
}
