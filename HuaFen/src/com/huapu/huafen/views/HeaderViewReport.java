package com.huapu.huafen.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;

public class HeaderViewReport extends LinearLayout {

    private LayoutInflater mInflater;
    private Context mContext;
    private View mView;
    public TextView tvReport;
    public SimpleDraweeView ivReportGoods;
    public TextView tvReportReason;
    public DashLineView dlvGoodsName;
    public View layoutReport;
    public View layoutReportComment;
    public View layoutReportGoods;
    public SimpleDraweeView ivHeader;
    public CommonTitleView ctvName;
    public TextView tvTime;
    public TextView tvContent;

    public HeaderViewReport(Context context) {
        super(context);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        initialize();
    }

    private void initialize() {
        mView = mInflater.inflate(R.layout.view_headview_report, null);
        tvReport = (TextView) mView.findViewById(R.id.tvReport);
        dlvGoodsName = (DashLineView) mView.findViewById(R.id.dlvGoodsName);
        tvReportReason = (TextView) mView.findViewById(R.id.tvReportReason);
        ivReportGoods = (SimpleDraweeView) mView.findViewById(R.id.ivReportGoods);
        layoutReport = mView.findViewById(R.id.layoutReport);
        layoutReportComment = mView.findViewById(R.id.layoutReportComment);
        layoutReportGoods = mView.findViewById(R.id.layoutReportGoods);
        ivHeader = (SimpleDraweeView) mView.findViewById(R.id.ivHeader);
        ctvName = (CommonTitleView) mView.findViewById(R.id.ctvName);
        tvTime = (TextView) mView.findViewById(R.id.tvTime);
        tvContent = (TextView) mView.findViewById(R.id.tvContent);
        this.addView(mView);
    }


}
