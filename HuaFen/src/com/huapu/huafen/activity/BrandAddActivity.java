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

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.callbacks.SimpleTextWatcher;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;

/**
 * Created by admin on 2017/2/16.
 */

public class BrandAddActivity extends BaseActivity {

    private final static String TAG = BrandAddActivity.class.getSimpleName();
    private EditText etComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_add);
        Intent intent = getIntent();
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
        getTitleBar().setTitle("添加品牌").setLeftText("取消",new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(etComment!=null){
                    String comments= etComment.getText().toString();
                    if(!TextUtils.isEmpty(comments)){
                        final TextDialog dialog = new TextDialog(BrandAddActivity.this,true);
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
        }).setRightText("确定", Color.parseColor("#cccccc"), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra(MyConstants.EXTRA_ADD_BRAND,comment);
                setResult(RESULT_OK);
            }
        });
        getTitleBar().getTitleTextRight().setEnabled(false);


    }

    private void initView(){
        etComment = (EditText) findViewById(R.id.etComment);
        etComment.setHint("请输入品牌");
        etComment.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null&&s.toString().replaceAll("\\n","").replaceAll("\\t", "").trim().length()>0){
                    getTitleBar().getTitleTextRight().setEnabled(true);
                    getTitleBar().setRightTextColor(Color.parseColor("#333333"));
                }else{
                    getTitleBar().getTitleTextRight().setEnabled(false);
                    getTitleBar().setRightTextColor(Color.parseColor("#cccccc"));
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
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
