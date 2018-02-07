package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import butterknife.BindView;

/**
 * Created by admin on 2017/3/16.
 * 退款选择页
 */


public class RefundOptionActivity extends BaseActivity {

    @BindView(R2.id.rlRefundCashAndGoods) RelativeLayout rlRefundCashAndGoods;
    @BindView(R2.id.rlRefundCash) RelativeLayout rlRefundCash;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_option);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        if(bundle!=null){
            int refundType = bundle.getInt(MyConstants.EXTRA_ORDER_REFUND_TYPE);
            if(refundType == 1){
                Intent startIntent = new Intent(this,OrderRefundEditActivity.class);
                startIntent.putExtras(bundle);
                startActivity(startIntent);
                finish();
                return;
            }
        }
        rlRefundCashAndGoods.setOnClickListener(this);
        rlRefundCash.setOnClickListener(this);
    }

    @Override
    public void initTitleBar() {
        setTitleString("退款选择");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rlRefundCashAndGoods:
            case R.id.rlRefundCash:
                if(v.getId() == R.id.rlRefundCashAndGoods){
                    bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_TYPE, 2);
                }else if(v.getId() == R.id.rlRefundCash){
                    bundle.putInt(MyConstants.EXTRA_ORDER_REFUND_TYPE, 1);
                }
                Intent startIntent = new Intent(this,OrderRefundEditActivity.class);
                startIntent.putExtras(bundle);
                startActivity(startIntent);
                finish();
                break;
            default:
                break;
        }
    }
}
