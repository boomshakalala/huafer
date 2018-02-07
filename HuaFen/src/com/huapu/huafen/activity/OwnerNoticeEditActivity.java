package com.huapu.huafen.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.callbacks.SimpleTextWatcher;
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
import com.squareup.okhttp.Request;

import java.util.HashMap;

import butterknife.BindView;

/**
 * 店铺公告
 * Created by admin on 2017/3/15.
 */

public class OwnerNoticeEditActivity extends BaseActivity {

    @BindView(R2.id.etOwnerNotice) EditText etOwnerNotice;
    @BindView(R2.id.tvLength) TextView tvLength;
    private String notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_notice_edit);
        Intent intent = getIntent();

        etOwnerNotice.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etOwnerNotice.getText().toString();
                tvLength.setText(content.length() + "/"
                        + "400");
                if(s!=null&&s.toString().replaceAll("\\n","").replaceAll("\\t", "").trim().length()>0){
                    getTitleBar().getTitleTextRight().setEnabled(true);
                    getTitleBar().setRightTextColor(Color.parseColor("#333333"));
                }else{
                    getTitleBar().getTitleTextRight().setEnabled(false);
                    getTitleBar().setRightTextColor(Color.parseColor("#cccccc"));
                }
            }
        });
        if(intent.hasExtra(MyConstants.EXTRA_NOTICE_TEXT)){
            notice = intent.getStringExtra(MyConstants.EXTRA_NOTICE_TEXT);
            if(!TextUtils.isEmpty(notice)){
                etOwnerNotice.setText(notice);
            }
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)etOwnerNotice.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null) {
                    inputManager.showSoftInput(etOwnerNotice, 0);
                }
            }
        },200);
    }

    @Override
    public void initTitleBar() {
        getTitleBar().
                setTitle("编辑公告").
                setRightText("保存", Color.parseColor("#cccccc"), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startRequestForEditUserShopNotice();
                    }
                }).
                setOnLeftButtonClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(etOwnerNotice!=null){
                            String comments= etOwnerNotice.getText().toString();
                            if(!TextUtils.isEmpty(comments)){
                                final TextDialog dialog = new TextDialog(OwnerNoticeEditActivity.this,true);
                                dialog.setContentText("确定退出？");
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
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                                dialog.show();
                            }else{
                                onBackPressed();
                            }
                        }else{
                            onBackPressed();
                        }
                    }
                });
    }


    /**
     * 编辑公告
     * @param
     */
    private void startRequestForEditUserShopNotice(){
        if(!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(OwnerNoticeEditActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", String.valueOf(CommonPreference.getUserId()));
        params.put("notice", etOwnerNotice.getText().toString());
        LogUtil.i("liang", "编辑公告parmas:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.EDITUSERSHOPNOTICE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "编辑公告:"+response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if(!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        Intent intent = new Intent();
                        intent.putExtra(MyConstants.EXTRA_NOTICE, etOwnerNotice.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        CommonUtils.error(baseResult, OwnerNoticeEditActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etOwnerNotice.getWindowToken(), 0);
        }
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
