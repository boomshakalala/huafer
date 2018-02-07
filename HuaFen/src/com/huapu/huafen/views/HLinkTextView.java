package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by admin on 2017/2/13.
 */

public class HLinkTextView extends TextView {

    public HLinkTextView(Context context) {
        super(context);
    }

    public HLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLinkText(final String linkText, String content){
        if(TextUtils.isEmpty(linkText)||TextUtils.isEmpty(content)){
            throw new RuntimeException("linkText and content can not be empty");
        }
        String link = linkText+"：";
        String format = "回复"+link+content;
        SpannableString str = new SpannableString(format);

        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                if(mOnLinkTextClick!=null){
                    mOnLinkTextClick.onClick(linkText);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#53709c"));
                ds.setUnderlineText(false);
            }
        };

        str.setSpan(clickableSpan,2,2+link.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(str);
        setMovementMethod(LinkMovementMethod.getInstance());
        setHighlightColor(Color.GRAY);
    }

    public interface OnLinkTextClick{
        void onClick(String name);
    }

    private OnLinkTextClick mOnLinkTextClick;

    public void setOnLinkTextClick(OnLinkTextClick onLinkTextClick){
        this.mOnLinkTextClick = onLinkTextClick;
    }
}
