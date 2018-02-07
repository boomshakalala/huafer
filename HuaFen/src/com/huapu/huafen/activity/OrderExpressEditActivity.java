package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Express;
import com.huapu.huafen.beans.ExpressContactListResult;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.OrderJointResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.events.OrderDetailRequestEvent;
import com.huapu.huafen.events.RefundFinishEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * 物流填写
 *
 * @author liang_xs
 */
public class OrderExpressEditActivity extends BaseActivity {

    @BindView(R.id.ivExpressContact)
    ImageView ivExpressContact;
    @BindView(R.id.etExpressNum)
    EditText etExpressNum;
    @BindView(R.id.ivBtnRefunNum)
    ImageView ivBtnRefunNum;
    @BindView(R.id.tvExpress)
    TextView tvExpress;
    @BindView(R.id.layoutExpress)
    RelativeLayout layoutExpress;

    private static final int CAPTURE_REQUEST_CODE = 1;
    private static final int CHOOSE_EXPRESS = 2;
    private long orderId;
    private long refundId;
    private String expressNum;
    private Express express;
    private String orderIds;
    private boolean isModifyCourierNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_express_edit);
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL_ID)) {
            orderId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, 0);
            orderIds = String.valueOf(orderId);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_REFUND_ID)) {
            refundId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_REFUND_ID, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL_IDS)) {
            orderIds = getIntent().getStringExtra(MyConstants.EXTRA_ORDER_DETAIL_IDS);
        }

        if (getIntent().hasExtra(MyConstants.MODIFY_COURIER_NUMBER)) {
            isModifyCourierNumber = getIntent().getBooleanExtra(MyConstants.MODIFY_COURIER_NUMBER, false);
        }
        initView();
    }

    @Override
    public void initTitleBar() {
        getTitleBar().
                setTitle("快递单号").
                setRightText("提交", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(etExpressNum.getText().toString())) {
                            toast("请填写物流单号");
                            return;
                        }
                        if (!CommonUtils.isExpressNum(etExpressNum.getText().toString())) {
                            toast("快递单号只能输入英文和数字哦~");
                            return;
                        }
                        if (express == null) {
                            toast("请选择物流公司");
                            return;
                        }
                        String desc = String.format(getString(R.string.modify_courier_number_desc), express.getExpressName(), etExpressNum.getText().toString());
                        final TextDialog dialog = new TextDialog(OrderExpressEditActivity.this, false);
                        dialog.setContentText(desc);
                        dialog.setLeftText("取消");
                        dialog.setLeftCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                dialog.dismiss();
                            }
                        });
                        dialog.setRightText("确定");
                        dialog.setRightCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                if (!TextUtils.isEmpty(orderIds)) {
                                    if (isModifyCourierNumber) {
                                        startRequestForModifyCourierNumber();
                                    } else {
                                        startRequestForBatchShip();
                                    }
                                } else if (refundId != 0) {
                                    startRequestForReturnGoods();
                                }
                            }
                        });
                        dialog.show();

                    }
                });
    }

    private void initView() {
        int width = CommonUtils.getScreenWidth();
        int height = width * 13 / 32;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ivExpressContact.getLayoutParams();
        params.height = height;
        ivExpressContact.setLayoutParams(params);

        ivBtnRefunNum.setOnClickListener(this);
        layoutExpress.setOnClickListener(this);
        ivExpressContact.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBtnRefunNum:
                startActivityForResult(new Intent(this, CaptureActivity.class), CAPTURE_REQUEST_CODE);
                break;
            case R.id.layoutExpress:
                startActivityForResult(new Intent(this, ChooseExpressActivity.class), CHOOSE_EXPRESS);
                break;
            case R.id.ivExpressContact:
                startActivity(new Intent(this, ExpressContactListActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_REQUEST_CODE) {
                if (data.hasExtra(MyConstants.QR_CODE)) {
                    expressNum = data.getStringExtra(MyConstants.QR_CODE);
                    etExpressNum.setText(expressNum);
                }
            } else if (requestCode == CHOOSE_EXPRESS) {
                if (data.hasExtra(MyConstants.EXTRA_EXPRESS)) {
                    express = (Express) data.getSerializableExtra(MyConstants.EXTRA_EXPRESS);
                    tvExpress.setText(express.getExpressName());
                }
            }
        }

    }


    /**
     * 识别物流公司
     *
     * @param
     */
    private void startRequestForExpressName() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("expressNum", etExpressNum.getText().toString());
        LogUtil.i("liang", "快递公司params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETEXPRESSNAME, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        ExpressContactListResult expressResult = JSON.parseObject(baseResult.obj, ExpressContactListResult.class);
                        List<Express> expressList = expressResult.getExpressList();
                        if (ArrayUtil.isEmpty(expressList) || expressList.size() > 1) {
                            toast("请选择快递公司");
                        } else {
                            toast("成功识别快递公司名称");
                            express = expressList.get(0);
                            tvExpress.setText(express.getExpressName());
                        }
                    } else {
                        CommonUtils.error(baseResult, OrderExpressEditActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 批量发货
     *
     * @param
     */
    private void startRequestForBatchShip() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", orderIds);
        params.put("expressId", String.valueOf(express.getExpressId()));
        params.put("expressNum", etExpressNum.getText().toString());
        OkHttpClientManager.postAsyn(MyConstants.BATCH_DELIVERY, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {

                try {
                    BaseResult baseResult = parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        OrderJointResult result = JSON.parseObject(baseResult.obj, OrderJointResult.class);
                        OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
                        oEvent.isUpdate = true;
                        EventBus.getDefault().post(oEvent);
                        onBackPressed();
                    } else {
                        CommonUtils.error(baseResult, OrderExpressEditActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 修改快递单号
     *
     * @param
     */
    private void startRequestForModifyCourierNumber() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", orderIds);
        params.put("expressId", String.valueOf(express.getExpressId()));
        params.put("expressNum", etExpressNum.getText().toString());
        OkHttpClientManager.postAsyn(MyConstants.MODIFY_COURIER_NUMBER_URL, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {

                try {
                    BaseResult baseResult = parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {

                        onBackPressed();
                    } else {
                        CommonUtils.error(baseResult, OrderExpressEditActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 买家退货
     *
     * @param
     */
    private void startRequestForReturnGoods() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("refundId", String.valueOf(refundId));
        params.put("expressId", String.valueOf(express.getExpressId()));
        params.put("expressNum", etExpressNum.getText().toString());
        LogUtil.i("liang", "买家退货params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.RETURNGOODS, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
                            OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
                            oEvent.isUpdate = true;
                            EventBus.getDefault().post(oEvent);
                            RefundFinishEvent event = new RefundFinishEvent();
                            event.isFinish = true;
                            EventBus.getDefault().post(event);
                            finish();
                        }
                    } else {
                        CommonUtils.error(baseResult, OrderExpressEditActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
