package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import butterknife.BindView;

public class OtherTagActivity extends BaseActivity {

    @BindView(R2.id.etTagOther) EditText etOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_tag_activity);
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("其他").setRightText("完成", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = etOther.getText().toString().trim();
                if(!TextUtils.isEmpty(content)){
                    Intent intent = new Intent();
                    intent.putExtra(MyConstants.TAG_ELSE, etOther.getText().toString().trim());
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    toast("请填写标签内容");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
