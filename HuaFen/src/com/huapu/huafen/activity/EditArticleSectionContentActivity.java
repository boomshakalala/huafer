package com.huapu.huafen.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/27.
 */

public class EditArticleSectionContentActivity extends BaseActivity {

    @BindView(R.id.editInput)
    EditText editInput;
    @BindView(R.id.tvCount)
    TextView tvCount;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_section_content);
        ButterKnife.bind(this);
        position = mIntent.getIntExtra(MyConstants.SECTION, -1);
        String content = mIntent.getStringExtra(MyConstants.CONTENT);
        if (!TextUtils.isEmpty(content)) {
            editInput.setText(content);
            editInput.setSelection(content.length());
        }
        if (position == -1) {
            finish();
        }
//        editInput.addTextChangedListener(new SimpleTextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s != null) {
//                    String content = s.toString().trim();
//                    int count = content.length();
//                    tvCount.setText(count+"/400");
//                } else {
//                    tvCount.setText("0/400");
//                }
//            }
//        });

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)editInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null) {
                    inputManager.showSoftInput(editInput, 0);
                }
            }
        },200);
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("花语").setRightText("完成", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MyConstants.INPUT_SECTION_CONTENT, editInput.getText().toString().trim());
                intent.putExtra(MyConstants.SECTION, position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
