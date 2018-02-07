package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditScoreData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.zmxy.view.SesameCreditPanel;
import com.huapu.huafen.zmxy.view.SesameItemModel;
import com.huapu.huafen.zmxy.view.SesameModel;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 芝麻信用
 * Created by admin on 2016/9/2.
 */
public class CreditPanelActivity extends BaseActivity {
    private SesameCreditPanel panel;
    private int zmCreditPoint;
    private Button btnUpdate;
    private boolean isRequesting = true;
    private boolean isNotMine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_panel_layout);
        findViewById(R.id.whatIsSesame).setOnClickListener(this);

        getTitleBar().setTitle("芝麻信用");

        Intent intent = getIntent();
        if (intent.hasExtra("zmCreditPoint")) {
            zmCreditPoint = intent.getIntExtra("zmCreditPoint", 0);
        }
        if (zmCreditPoint < 350 || zmCreditPoint > 950) {
            final TextDialog dialog = new TextDialog(this, false);
            dialog.setContentText("目前无法获取到您的信用积分，如需显示正确积分，" +
                    "请先到您的支付宝取消芝麻信用对花粉儿的授权，然后在花粉儿更新芝麻积分后，重新授权");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    if (dialog != null) {
                        dialog.dismiss();
                        CommonPreference.setUserZmCreditPoint(0);
                        Intent intent = new Intent();
                        intent.putExtra(MyConstants.AUTH_ZM_CANCEL, 1234);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    CreditPanelActivity.this.finish();
                }
            });
            dialog.show();
            return;
        }
        if (intent.hasExtra("isNotMine")) {
            isNotMine = intent.getBooleanExtra("isNotMine", false);
        }

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        if (isNotMine) {
            btnUpdate.setVisibility(View.GONE);
        }
        btnUpdate.setOnClickListener(this);
        btnUpdate.setEnabled(false);
        panel = (SesameCreditPanel) findViewById(R.id.panel);
        panel.setOnProgressLoadListener(new SesameCreditPanel.OnProgressLoadListener() {

            @Override
            public void onStart() {
                btnUpdate.setEnabled(false);
            }

            @Override
            public void onEnd() {
                btnUpdate.setEnabled(true);
            }
        });
        panel.setDataModel(getData());
    }

    private SesameModel getData() {
        SesameModel model = new SesameModel();
        model.setUserTotal(zmCreditPoint);
        if (zmCreditPoint >= 350 && zmCreditPoint < 550) {
            model.setAssess("信用较差");
        } else if (zmCreditPoint >= 550 && zmCreditPoint < 600) {
            model.setAssess("信用中等");
        } else if (zmCreditPoint >= 600 && zmCreditPoint < 650) {
            model.setAssess("信用良好");
        } else if (zmCreditPoint >= 650 && zmCreditPoint < 700) {
            model.setAssess("信用优秀");
        } else if (zmCreditPoint >= 700 && zmCreditPoint <= 950) {
            model.setAssess("信用极好");
        }
        model.setTotalMin(350);
        model.setTotalMax(950);
        model.setFirstText("");
        model.setTopText("因为信用，所以简单");
        model.setFourText("");
        ArrayList<SesameItemModel> sesameItemModels = new ArrayList<>();

        SesameItemModel ItemModel350 = new SesameItemModel();
        ItemModel350.setArea("较差");
        ItemModel350.setMin(350);
        ItemModel350.setMax(550);
        sesameItemModels.add(ItemModel350);

        SesameItemModel ItemModel550 = new SesameItemModel();
        ItemModel550.setArea("中等");
        ItemModel550.setMin(550);
        ItemModel550.setMax(600);
        sesameItemModels.add(ItemModel550);

        SesameItemModel ItemModel600 = new SesameItemModel();
        ItemModel600.setArea("良好");
        ItemModel600.setMin(600);
        ItemModel600.setMax(650);
        sesameItemModels.add(ItemModel600);

        SesameItemModel ItemModel650 = new SesameItemModel();
        ItemModel650.setArea("优秀");
        ItemModel650.setMin(650);
        ItemModel650.setMax(700);
        sesameItemModels.add(ItemModel650);

        SesameItemModel ItemModel700 = new SesameItemModel();
        ItemModel700.setArea("极好");
        ItemModel700.setMin(700);
        ItemModel700.setMax(950);
        sesameItemModels.add(ItemModel700);

        model.setSesameItemModels(sesameItemModels);
        return model;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate:
                if (isRequesting) {
                    startRequestForCreditScore();
                }
                break;
            case R.id.whatIsSesame:
                Intent intent = new Intent(CreditPanelActivity.this, WebViewActivity.class);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.WHAT_ZM);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    private void startRequestForCreditScore() {
        isRequesting = false;
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();

        LogUtil.i("lalo", "获取信用" + params.toString());

        OkHttpClientManager.postAsyn(MyConstants.CREDIT_UPDATE_CODE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("lalo", "获取信用积分error：" + e.toString());
                isRequesting = true;
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e("lalo", "获取信用积分：" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        CreditScoreData data = JSON.parseObject(baseResult.obj, CreditScoreData.class);
                        zmCreditPoint = data.zmCreditPoint;
                        if (zmCreditPoint <= 950 && zmCreditPoint >= 350) {
                            CommonPreference.setUserZmCreditPoint(zmCreditPoint);
                            panel.setOnProgressLoadListener(new SesameCreditPanel.OnProgressLoadListener() {
                                @Override
                                public void onStart() {
                                    isRequesting = false;
                                }

                                @Override
                                public void onEnd() {
                                    isRequesting = true;
                                }
                            });
                            panel.setDataModel(getData());
                            panel.resetCreditPanel();
                        } else {
                            ToastUtil.toast(CreditPanelActivity.this, "平台授权已取消，如需评估信用分，请返回后重新认证");
                            CommonPreference.setUserZmCreditPoint(0);
                            Intent intent = new Intent();
                            intent.putExtra(MyConstants.AUTH_ZM_CANCEL, 1234);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                    } else {
                        CommonUtils.error(baseResult, CreditPanelActivity.this, "");
                        isRequesting = true;
                    }
                } catch (Exception e) {
                    isRequesting = true;
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
