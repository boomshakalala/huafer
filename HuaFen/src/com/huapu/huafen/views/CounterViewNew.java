package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/19.
 */

public class CounterViewNew extends LinearLayout {

    @BindView(R.id.tvHour1) TextView tvHour1;
    @BindView(R.id.tvHour2) TextView tvHour2;
    @BindView(R.id.tvMinute1) TextView tvMinute1;
    @BindView(R.id.tvMinute2) TextView tvMinute2;
    @BindView(R.id.tvSecond1) TextView tvSecond1;
    @BindView(R.id.tvSecond2) TextView tvSecond2;

    public CounterViewNew(Context context) {
        this(context,null);
    }

    public CounterViewNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.counter_view_new, this, true);
        setOrientation(HORIZONTAL);
        ButterKnife.bind(this);
    }

    public boolean reset(long timeMillis){
        long currentTimeMillis = System.currentTimeMillis();
        if(timeMillis - currentTimeMillis > 0){
            long time = timeMillis - currentTimeMillis;
            resetUI(time);
            return false;
        }else{
            resetUI(0);
            return true;
        }
    }

    private void resetUI(long time){
        long hour = (time%(1000*24*60*60))/(1000*60*60);

        long hour1 = hour/10;
        long hour2 = hour%10;

        long minute = (time % (1000 * 60 * 60)) / (1000 * 60);

        long minute1 = minute/10;
        long minute2 = minute%10;

        long second = (time % (1000 * 60)) / 1000;

        long second1 = second/10;
        long second2= second%10;

        tvHour1.setText(String.valueOf(hour1));
        tvHour2.setText(String.valueOf(hour2));
        tvMinute1.setText(String.valueOf(minute1));
        tvMinute2.setText(String.valueOf(minute2));
        tvSecond1.setText(String.valueOf(second1));
        tvSecond2.setText(String.valueOf(second2));
    }


}
