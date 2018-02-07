package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.OrderInfo;
import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.RenderScriptBlur;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/6/22.
 */
public class OrderTopView extends FrameLayout {

    @BindView(R.id.orderTopBg)
    SimpleDraweeView orderTopBg;
    @BindView(R.id.orderStateView)
    OrderStateView orderStateView;
    @BindView(R.id.tvOrderStateDes)
    TextView tvOrderStateDes;
    @BindView(R.id.tvCountdownDes)
    TextView tvCountdownDes;
    @BindView(R.id.llOrderState)
    LinearLayout llOrderState;
    @BindView(R.id.llOrderCancel)
    LinearLayout llOrderCancel;
    @BindView(R.id.tvOrderCancelDes)
    TextView tvOrderCancelDes;
    @BindView(R.id.tvCancelCountDownDes)
    TextView tvCancelCountDownDes;

    private Handler handler = new Handler();
    private CountDownRunnable runnable;

    private class CountDownRunnable implements Runnable {

        private TextView textView;
        private OrderInfo orderInfo;

        public CountDownRunnable(TextView textView, OrderInfo orderInfo) {
            this.textView = textView;
            this.orderInfo = orderInfo;
        }

        @Override
        public void run() {
            setUpCountDown(textView, orderInfo);
        }
    }

    public OrderTopView(@NonNull Context context) {
        super(context);
    }

    public OrderTopView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.order_top_layout, this, true);
        ButterKnife.bind(this);
    }


    public void setData(OrderInfo orderInfo, String url) {

        int orderStatus = orderInfo.getOrderStatus();
        removeCallback();
        if (orderStatus == 6) {
            llOrderCancel.setVisibility(VISIBLE);
            llOrderState.setVisibility(GONE);
            String orderStatusTitle = orderInfo.getOrderStateTitle();
            if (!TextUtils.isEmpty(orderStatusTitle)) {
                tvOrderCancelDes.setVisibility(VISIBLE);
                tvOrderCancelDes.setText(orderStatusTitle);
            } else {
                tvOrderCancelDes.setVisibility(GONE);
            }
            runnable = new CountDownRunnable(tvCancelCountDownDes, orderInfo);
            handler.post(runnable);
        } else {
            llOrderCancel.setVisibility(GONE);
            llOrderState.setVisibility(VISIBLE);
            orderStateView.setData(orderStatus);
            String orderStatusTitle = orderInfo.getOrderStateTitle();
            if (!TextUtils.isEmpty(orderStatusTitle)) {
                tvOrderStateDes.setVisibility(VISIBLE);
                tvOrderStateDes.setText(orderStatusTitle);
            } else {
                tvOrderStateDes.setVisibility(GONE);
            }
            runnable = new CountDownRunnable(tvCountdownDes, orderInfo);
            handler.post(runnable);
        }

        ImageLoader.loadBitmap(getContext(), url, new BitmapCallback() {
            @Override
            public void onBitmapDownloaded(Bitmap bitmap) {
                if (bitmap == null)
                    return;
                RenderScriptBlur renderScriptBlur = new RenderScriptBlur(getContext());
                final Bitmap bmp = renderScriptBlur.blur(20, bitmap);
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        orderTopBg.setImageBitmap(bmp);
                    }
                });
            }
        });

    }

    private void setUpCountDown(TextView textView, OrderInfo orderInfo) {
        orderInfo.setOrderResidualTime(orderInfo.getOrderResidualTime() - 1000);
        long tmp = orderInfo.getOrderResidualTime();
        if (tmp > 0) {
            textView.setVisibility(VISIBLE);
            setCountDown(textView, tmp, orderInfo.getOrderStateUnderTitle());
            handler.postDelayed(runnable, 1000);
        } else {
            textView.setVisibility(GONE);
            removeCallback();
        }
    }


    private void setCountDown(TextView textView, long mss, String des) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String residueTime;
        if (days != 0) {
            residueTime = String.format(getContext().getString(R.string.count_down_day_hour_minute_second), String.valueOf(days), String.valueOf(hours), String.valueOf(minutes), String.valueOf(seconds), des);
        } else {
            if (hours != 0) {
                residueTime = String.format(getContext().getString(R.string.count_down_hour_minute_second), String.valueOf(hours), String.valueOf(minutes), String.valueOf(seconds), des);
            } else {
                if (minutes != 0) {
                    residueTime = String.format(getContext().getString(R.string.count_down_minute_second), String.valueOf(minutes), String.valueOf(seconds), des);
                } else {
                    residueTime = String.format(getContext().getString(R.string.count_down_second), String.valueOf(seconds), des);
                }
            }
        }
        textView.setText(Html.fromHtml(residueTime));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallback();
    }

    private void removeCallback() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
