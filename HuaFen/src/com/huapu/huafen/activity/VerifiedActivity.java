package com.huapu.huafen.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 实名认证
 */
public class VerifiedActivity extends BaseActivity {

    @BindView(R.id.ed_name)
    EditText edName;
    @BindView(R.id.ed_id_number)
    EditText edIdNumber;
    @BindView(R.id.ed_bc_number)
    EditText edBcNumber;
    @BindView(R.id.ed_cp_number)
    EditText edCpNumber;
    @BindView(R.id.btn_verified)
    Button btnVerified;
    @BindView(R.id.re_su)
    RelativeLayout reSu;
    @BindView(R.id.ll_verified)
    LinearLayout llVerified;
    private int onAll = 0;
    private int onName = 0;
    private int onId = 0;
    private int onBc = 0;
    private int onCp = 0;
    private int ON_NUME = 1000;
    private int ON_ID = 1001;
    private int ON_BC = 1002;
    private int ON_CP = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified);
        ButterKnife.bind(this);
        addTextChangedListener(edName, ON_NUME);
        addTextChangedListener(edIdNumber, ON_ID);
        addTextChangedListener(edBcNumber, ON_BC);
        addTextChangedListener(edCpNumber, ON_CP);
    }

    //设置EditText输入监听
    private void addTextChangedListener(final EditText editText, final int tag) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                    }
                    editText.setText(str1);

                    editText.setSelection(start);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                textChanged(s, tag);
                LogUtil.e("VerifiedActivity  ", s.toString());
            }
        });
    }

    @Override
    public void initTitleBar() {
        super.initTitleBar();
        getTitleBar().
                setTitle("实名认证").
                setRightText("说明", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VerifiedActivity.this, ExplainVerify.class);
                        startActivity(intent);
                    }
                });
    }

    @OnClick(R.id.btn_verified)
    public void onViewClicked() {
        if (onAll == 4) {
            String name = edName.getText().toString();
            String idNum = edIdNumber.getText().toString();
            String cardNum = edBcNumber.getText().toString();
            String phone = edCpNumber.getText().toString();
//            Boolean noShow = true;
//            if (name.length() == 0) {
//                showAlertDialog("请输入姓名");
//                noShow = false;
//            }
//            if (idNum.length() == 0 && noShow) {
//                showAlertDialog("请输入身份证号");
//                noShow = false;
//            }
//            if (cardNum.length() == 0 && noShow) {
//                showAlertDialog("请输入支持银行卡卡号");
//                noShow = false;
//            }
//            if (phone.length() == 0 && noShow) {
//                showAlertDialog("请输入银行卡预留手机号");
//                noShow = false;
//            }
//            if (noShow) {
            LogUtil.i("VerifiedActivity", "请求数据");
            HashMap<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("cardNum", cardNum);
            params.put("idNum", idNum);
            params.put("phone", phone);

            buttonEnable(false);

            OkHttpClientManager.postAsyn(MyConstants.VERIFYCARD, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    LogUtil.i("VerifiedActivity", "返回失败" + e);
                    buttonEnable(true);
                }

                @Override
                public void onResponse(String response) {
                    LogUtil.i("VerifiedActivity", "返回成功" + response);
                    try {
                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                            UserInfo userInfo = CommonPreference.getUserInfo();
                            userInfo.hasVerified = true;
                            CommonPreference.setUserInfo(userInfo);
                            llVerified.setVisibility(View.INVISIBLE);
                            reSu.setVisibility(View.VISIBLE);
                            buttonEnable(true);
                        } else {
                            toast(baseResult.msg);
                            buttonEnable(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void buttonEnable(boolean enable) {
        if (enable) {
            btnVerified.setBackground(getResources().getDrawable(R.drawable.rect_red_bg));
            btnVerified.setEnabled(true);
        } else {
            btnVerified.setBackground(getResources().getDrawable(R.drawable.rect_gray_bg));
            btnVerified.setEnabled(false);
        }
    }

    private void textChanged(CharSequence s, int tag) {
        if (s.length() != 0) {
            if (tag == ON_BC) onBc = 1;
            if (tag == ON_CP) onCp = 1;
            if (tag == ON_ID) onId = 1;
            if (tag == ON_NUME) onName = 1;
        } else {
            if (tag == ON_BC) onBc = 0;
            if (tag == ON_CP) onCp = 0;
            if (tag == ON_ID) onId = 0;
            if (tag == ON_NUME) onName = 0;
        }
        onAll = onBc + onCp + onName + onId;
        LogUtil.e("VerifiedActivity", "onAll  " + s.length() + "  " + onAll);
        if (onAll == 4) {
            btnVerified.setBackground(getResources().getDrawable(R.drawable.rect_red_bg));
        } else {
            btnVerified.setBackground(getResources().getDrawable(R.drawable.rect_gray_bg));
        }
    }

    public void showAlertDialog(String mesage) {
        final AlertDialog dialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.VerifiedAlertDialog);
        final View alertDialog = LayoutInflater.from(this).inflate(R.layout.layout_alertdialog,
                null, false);

        alertDialogBuilder.setView(alertDialog);
        Button btnCheck = (Button) alertDialog.findViewById(R.id.btn_check);
        TextView tvMsg = (TextView) alertDialog.findViewById(R.id.tv_msg);
        tvMsg.setText(mesage);
        dialog = alertDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setLayout((int) (0.75 * CommonUtils.getScreenWidth()), LinearLayout.LayoutParams.WRAP_CONTENT);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("VerifiedActivity", "按钮被点击了");
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

}
