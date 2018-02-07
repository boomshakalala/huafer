package com.huapu.huafen.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * Created by admin on 2017/2/16.
 */

public class ReplyCommitActivity extends BaseActivity {

    private final static String TAG = ReplyCommitActivity.class.getSimpleName();
    private EditText etComment;
    private TextView tvLength;
    private long commentId;
    private String userName;
    private int targetType;

    private String FROM_WEB = "";

    private String WEB_RETURN_STRING = "cancled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_commit);
        Intent intent = getIntent();
        commentId = intent.getLongExtra(MyConstants.Comment.TARGET_ID,0);
        userName = intent.getStringExtra(MyConstants.USER_NAME);
        targetType = intent.getIntExtra(MyConstants.Comment.TARGET_TYPE,0);

        if (intent.hasExtra("FROM_WEB")) {
            FROM_WEB = getIntent().getStringExtra("FROM_WEB");
        }
        initTitleBar();
        initView();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)etComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null) {
                    inputManager.showSoftInput(etComment, 0);
                }
            }
        },200);

    }

    @Override
    public void initTitleBar(){
        getTitleBar().
                setTitle("回复").
                setLeftText("取消", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(etComment!=null){
                            String comments= etComment.getText().toString();
                            if(!TextUtils.isEmpty(comments)){
                                final TextDialog dialog = new TextDialog(ReplyCommitActivity.this,true);
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
                }).
                setRightText("发送", Color.parseColor("#cccccc"), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startRequestForCommitData();
                    }
                }).
                getTitleTextRight().
                setEnabled(false);
    }

    private void initView(){
        etComment = (EditText) findViewById(R.id.etComment);
        etComment.setHint("回复 " +userName);
        etComment.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etComment.getText().toString();
                tvLength.setText(content.length() + "/"
                        + "100");
                if(s!=null&&s.toString().replaceAll("\\n","").replaceAll("\\t", "").trim().length()>0){
                    getTitleBar().getTitleTextRight().setEnabled(true);
                    LogUtil.e(TAG,"true");
                    getTitleBar().setRightTextColor(Color.parseColor("#ff6677"));
                }else{
                    getTitleBar().getTitleTextRight().setEnabled(false);
                    LogUtil.e(TAG,"false");
                    getTitleBar().setRightTextColor(Color.parseColor("#cccccc"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvLength = (TextView) findViewById(R.id.tvLength);
    }


    private void startRequestForCommitData(){
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String,String>();
        params.put(MyConstants.Comment.TARGET_ID, String.valueOf(commentId));
        String comment = etComment.getText().toString().trim();
        if(!TextUtils.isEmpty(comment)) {
            comment = comment.replaceAll("\\n", "").replaceAll("\\t", "");
        } else {
            toast("请输入文字");
            ProgressDialog.closeProgress();
            return;
        }
        params.put(MyConstants.Comment.CONTENT, comment);
        params.put(MyConstants.Comment.TARGET_TYPE, String.valueOf(targetType));
        getTitleBar().getTitleTextRight().setEnabled(false);
        getTitleBar().setRightTextColor(Color.parseColor("#cccccc"));
        OkHttpClientManager.postAsyn(MyConstants.REPLAY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                getTitleBar().getTitleTextRight().setEnabled(true);
                getTitleBar().setRightTextColor(Color.parseColor("#ff6677"));
                ProgressDialog.closeProgress();
                WEB_RETURN_STRING = "failed";
            }

            @Override
            public void onResponse(String response) {
                getTitleBar().getTitleTextRight().setEnabled(true);
                getTitleBar().setRightTextColor(Color.parseColor("#ff6677"));
                ProgressDialog.closeProgress();
                LogUtil.e("lalo", "芝麻信用：" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    WEB_RETURN_STRING = "failed";
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        toast("回复成功");
                        WEB_RETURN_STRING = "success";
                        onBackPressed();
                    } else {
                        CommonUtils.error(baseResult, ReplyCommitActivity.this, "");
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
            imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
        }

        if (FROM_WEB.equals("FROM_WEB")) {
            Intent intent = new Intent();
            intent.putExtra("result", WEB_RETURN_STRING);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }
}
