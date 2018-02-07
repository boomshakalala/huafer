package com.huapu.huafen.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.Logger;

/**
 * Created by su on 2016/4/19.
 */
public class SecurityPasswordEditText extends LinearLayout {
    private EditText mEditText;
    private TextView oneTextView;
    private TextView twoTextView;
    private TextView threeTextView;
    private TextView fourTextView;
    private TextView[] mTextViews;

    private StringBuilder builder = new StringBuilder();
    private SecurityEditCompileListener mListener;
    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            Logger.e("get Text:" + mEditText.getText());
            setBuilderValue(s.toString());
        }
    };

    public SecurityPasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        initWidget(inflater);
    }

    private void initWidget(LayoutInflater inflater) {
        View contentView = inflater.inflate(R.layout.auth_code_edittext_widget, this, false);
        mEditText = (EditText) contentView
                .findViewById(R.id.sdk2_pwd_edit_simple);
        oneTextView = (TextView) contentView
                .findViewById(R.id.pwd_one_tv);
        twoTextView = (TextView) contentView
                .findViewById(R.id.pwd_two_tv);
        threeTextView = (TextView) contentView
                .findViewById(R.id.pwd_three_tv);
        fourTextView = (TextView) contentView
                .findViewById(R.id.pwd_four_tv);

        LayoutParams lParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mEditText.addTextChangedListener(mTextWatcher);
        mTextViews = new TextView[]{oneTextView, twoTextView, threeTextView,
                fourTextView};
        this.addView(contentView, lParams);
    }

    private void setBuilderValue(String str) {
        int strLen = str.length();
        int builderLen = builder.length();
        if (strLen == builderLen) {
            return;
        }
        if (strLen < builderLen) {
            for (int i = strLen; i <= builderLen - 1; i++) {
                mTextViews[i].setText("");
            }
            builder = new StringBuilder(str);
        } else {
            builder = new StringBuilder(str);
            for (int i = builderLen; i <= strLen - 1; i++) {
                mTextViews[i].setText(builder.toString().substring(i, i + 1));
            }
        }
        if (mTextViews.length == builder.length() && this.mListener != null) {
            mListener.onNumCompleted(str);
        }

        for (TextView textView : mTextViews) {
            if (null != textView) {
                if (textView.getText().length() == 1) {
                    textView.setBackgroundResource(R.drawable.verification_edit_bg_focus);
                } else {
                    textView.setBackgroundResource(R.drawable.verification_edit_bg_normal);
                }
            }

        }
    }

    public void clearText() {
        for (TextView textView : mTextViews) {
            if (null != textView) {
                textView.setText("");
                if (textView.getText().length() == 1) {
                    textView.setBackgroundResource(R.drawable.verification_edit_bg_focus);
                } else {
                    textView.setBackgroundResource(R.drawable.verification_edit_bg_normal);
                }
            }
        }

        builder.setLength(0);
        mEditText.setText("");
    }


    public void setSecurityEditCompileListener(
            SecurityEditCompileListener mListener) {
        this.mListener = mListener;
    }

    public interface SecurityEditCompileListener {
        void onNumCompleted(String num);
    }
}