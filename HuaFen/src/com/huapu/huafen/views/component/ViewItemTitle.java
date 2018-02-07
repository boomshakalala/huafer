package com.huapu.huafen.views.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.pages.ShowcaseBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 控件title
 * Created by dengbin on 18/1/10.
 */
public class ViewItemTitle extends RelativeLayout {
    private Context context;

    @BindView(R.id.item_title_title)
    TextView titleView;
    @BindView(R.id.item_title_sub_title)
    TextView subtitleView;
    @BindView(R.id.item_title_right)
    TextView moreView;

    public ViewItemTitle(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ViewItemTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        View.inflate(context, R.layout.view_item_title, this);
        ButterKnife.bind(this, this);
    }

    public void setData(ShowcaseBean data) {
        titleView.setText(data.getTitle());
        subtitleView.setText(data.getSubtitle());
        moreView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 18/1/10

            }
        });
    }
}
