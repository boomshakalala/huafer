package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/10/17.
 */
public class HomeTitleBar extends LinearLayout {


    @BindView(R.id.titleimage)
    ImageView titleimage;
    @BindView(R.id.navigationtitle)
    TextView navigationtitle;
    @BindView(R.id.indicator)
    ImageView indicator;
    @BindView(R.id.totalTitle)
    View totalTitle;


    public interface TitleClickListenner {

        public void OnTitleClick(Object o);
    }

    private TitleClickListenner listenner;


    public TitleClickListenner getListenner() {
        return listenner;
    }

    public void setListenner(TitleClickListenner listenner) {
        this.listenner = listenner;
    }


    public HomeTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public HomeTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void hideMore() {
        navigationtitle.setVisibility(GONE);
        indicator.setVisibility(GONE);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.title_head_list_new, this, true);
        ButterKnife.bind(this);
        totalTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenner != null) {
                    listenner.OnTitleClick(new Object());
                }
            }
        });


    }

    public void setTitleimage(int resid) {
        titleimage.setImageResource(resid);
    }

    public void setIndicator(int resid) {
        indicator.setImageResource(resid);
    }

}