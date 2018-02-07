package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;

import com.huapu.huafen.R;

/**
 * 关注按钮
 */
public class FollowImageView extends android.support.v7.widget.AppCompatImageView {

    public FollowImageView(Context context) {
        super(context);
    }

    public FollowImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPinkData(boolean isFocus) {
        if (isFocus) {
            setImageResource(R.drawable.btn_follow_gray_pressed);
        } else {
            setImageResource(R.drawable.btn_follow_pink_default);
        }
    }

    public void setPinkData(int status) {
        switch (status) {
            case 1:
                setImageResource(R.drawable.btn_follow_pink_default);
                break;
            case 2:
                setImageResource(R.drawable.btn_follow_gray_pressed);
                break;
            case 3:
                setImageResource(R.drawable.btn_follow_pink_default);
                break;
            case 4:
                setImageResource(R.drawable.btn_follow_gray_each);
                break;
        }
    }

    public void setWhiteData(int status) {
        switch (status) {
            case 1:
                setImageResource(R.drawable.btn_follow_white_default);
                break;
            case 2:
                setImageResource(R.drawable.btn_follow_gray_pressed);
                break;
            case 3:
                setImageResource(R.drawable.btn_follow_white_default);
                break;
            case 4:
                setImageResource(R.drawable.btn_follow_gray_each);
                break;
        }
    }
}
