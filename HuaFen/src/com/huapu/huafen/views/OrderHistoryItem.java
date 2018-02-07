package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.huapu.huafen.R;
import java.util.Iterator;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/6/29.
 */

public class OrderHistoryItem extends LinearLayout {


    @BindView(R.id.tvKey) TextView tvKey;
    @BindView(R.id.tvValue) TextView tvValue;

    public OrderHistoryItem(Context context) {
        this(context,null);
    }

    public OrderHistoryItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.order_item, this, true);
        ButterKnife.bind(this);
    }

    public void setData(Map<String,String> data,int color){
        if(data!=null && !data.isEmpty()){
            tvKey.setTextColor(getContext().getResources().getColor(color));
            tvValue.setTextColor(getContext().getResources().getColor(color));
            Iterator<Map.Entry<String, String>> itt = data.entrySet().iterator();
            while (itt.hasNext()){
                Map.Entry<String, String> entry = itt.next();
                String key = entry.getKey();
                String value = entry.getValue();
                tvKey.setText(key);
                tvValue.setText(value);
            }
        }
    }

}
