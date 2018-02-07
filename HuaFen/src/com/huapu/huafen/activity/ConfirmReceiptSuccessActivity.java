package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.GridAdapter;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.views.ConfirmReceiptSuccessHeader;
import com.huapu.huafen.views.OrderDetailBottom;

import butterknife.BindView;

/**
 * Created by mac on 17/7/25.
 * 确认收货成功页
 */

public class ConfirmReceiptSuccessActivity extends BaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private ConfirmReceiptSuccessHeader confirmReceiptSuccessHeader;
    private GridAdapter adapter;
    @BindView(R.id.orderDetailBottom)
    OrderDetailBottom orderDetailBottom;

    public static int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_receipt_success);
        OrderDetailBean bean = (OrderDetailBean) mIntent.getSerializableExtra(MyConstants.EXTRA_ORDER_DETAIL_BEAN);
        if (bean == null) {
            final TextDialog dialog = new TextDialog(this, false);
            dialog.setContentText("订单信息错误");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
        }

        confirmReceiptSuccessHeader = new ConfirmReceiptSuccessHeader(recyclerView.getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new GridAdapter(recyclerView.getContext());
        adapter.setRecTraceId(bean.recTraceId);
        adapter.addHeaderView(confirmReceiptSuccessHeader);
        recyclerView.setAdapter(adapter.getWrapperAdapter());
        adapter.setData(bean.getRecItems());
        confirmReceiptSuccessHeader.setData(bean);
        orderDetailBottom.setData(bean);
    }

    @Override
    public void initTitleBar() {
        setTitleString("确认收货成功页");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                finish();
            }

        }

    }
}
