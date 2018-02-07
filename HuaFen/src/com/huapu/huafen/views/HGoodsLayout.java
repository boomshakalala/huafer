package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.huapu.huafen.R;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/3/18.
 */

public class HGoodsLayout extends LinearLayout{


    public HGoodsLayout(Context context) {
        this(context,null);
    }

    public HGoodsLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void setData(Map<String,String> stringMap){
        removeAllViews();
        if(stringMap!=null&&!stringMap.isEmpty()){
            Set<Map.Entry<String, String>> entrySet = stringMap.entrySet();
            Iterator<Map.Entry<String, String>> itt = entrySet.iterator();
            while (itt.hasNext()){
                Map.Entry<String, String> entry = itt.next();
                String k = entry.getKey();
                String v = entry.getValue();
                if(!TextUtils.isEmpty(v)){
                    HGoodsItem item = new HGoodsItem(getContext());
                    item.setItemName(k);
                    if("商品分类".equals(k)){
                        item.setItemDescColor(getContext().getResources().getColor(R.color.base_pink));
                        item.setArrowVisible(true);
                        item.setOnValueClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if(mOnClassArrowClickListener!=null){
                                    mOnClassArrowClickListener.onClick();
                                }
                            }
                        });
                    }
                    item.setItemDesc(v);
                    addView(item);
                }
            }
        }
    }


    public interface OnClassArrowClickListener{
        void onClick();
    }

    private OnClassArrowClickListener mOnClassArrowClickListener;

    public void setOnClassArowClickListener(OnClassArrowClickListener mOnClassArrowClickListener) {
        this.mOnClassArrowClickListener = mOnClassArrowClickListener;
    }
}
