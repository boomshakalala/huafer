package com.huapu.huafen.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.RefundLogData;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;

public class HeaderViewArbitration extends LinearLayout {

    private LayoutInflater mInflater;
    private Context mContext;
    private View mView;
    public EditText etArbitration;
    public EditText etPhoneNum;
    private SimpleDraweeView ivReportGoods;
    private TextView tvGoodsName;
    private TextView tvOrderNum;
    private TextView tvOrderCreateTime;
    private TextView tvPayTime;
    private TextView tvPayType;
    private TextView tvInputCount;


    public HeaderViewArbitration(Context context) {
        super(context);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        initialize();
    }

    public void setData(OrderDetailBean bean) {
        String url = bean.getGoodsInfo().getGoodsImgs().get(0);
        ImageLoader.resizeSmall(ivReportGoods, url, 1);

        tvGoodsName.setText(bean.getGoodsInfo().getGoodsName());
        tvOrderNum.setText(String.valueOf(bean.getOrderInfo().getOrderNum()));
        tvOrderCreateTime.setText(DateTimeUtils.getYearMonthDayHourMinuteSecond(bean.getOrderInfo().getOrderCreateTime()));
        tvPayTime.setText(DateTimeUtils.getYearMonthDayHourMinuteSecond(bean.getOrderInfo().getOrderPayTime()));
        if (bean.getOrderInfo().getOrderPayType() == 1) {
            tvPayType.setText("微信支付");
        } else if (bean.getOrderInfo().getOrderPayType() == 2) {
            tvPayType.setText("支付宝支付");
        }
    }

    public void setData(RefundLogData refundLogData) {
        String url = refundLogData.getGoodsData().getGoodsImgs().get(0);
        ImageLoader.resizeSmall(ivReportGoods, url, 1);

        tvGoodsName.setText(refundLogData.getGoodsData().getName());
        tvOrderNum.setText(String.valueOf(refundLogData.getOrderData().getOrderNum()));
        tvOrderCreateTime.setText(DateTimeUtils.getYearMonthDayHourMinuteSecond(refundLogData.getOrderData().getOrderCreateTime()));
        tvPayTime.setText(DateTimeUtils.getYearMonthDayHourMinuteSecond(refundLogData.getOrderData().getOrderPayTime()));
        if (refundLogData.getOrderData().getOrderPayType() == 1) {
            tvPayType.setText("微信支付");
        } else if (refundLogData.getOrderData().getOrderPayType() == 2) {
            tvPayType.setText("支付宝支付");
        }
    }

    private void initialize() {
        mView = mInflater.inflate(R.layout.view_headview_arbitration, null);
        etArbitration = (EditText) mView.findViewById(R.id.etArbitration);
        etPhoneNum = (EditText) mView.findViewById(R.id.etPhoneNum);
        tvGoodsName = (TextView) mView.findViewById(R.id.tvGoodsName);
        tvOrderNum = (TextView) mView.findViewById(R.id.tvOrderNum);
        tvOrderCreateTime = (TextView) mView.findViewById(R.id.tvOrderCreateTime);
        tvPayTime = (TextView) mView.findViewById(R.id.tvPayTime);
        tvPayType = (TextView) mView.findViewById(R.id.tvPayType);
        ivReportGoods = (SimpleDraweeView) mView.findViewById(R.id.ivReportGoods);
        tvInputCount = (TextView) mView.findViewById(R.id.tvInputCount);
//		etArbitration.addTextChangedListener(new SimpleTextWatcher() {
//		    @Override
//		    public void onTextChanged(CharSequence s, int start, int before, int count) {
//				if(s!=null){
//					String content = s.toString().trim();
//					tvInputCount.setText(content.length() + "/" + "200");
//				}else{
//					tvInputCount.setText("0" + "/" + "200");
//				}
//		    }
//		});

        this.addView(mView);
    }

}
