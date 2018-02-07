package com.huapu.huafen.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonUtils;

import java.util.ArrayList;


/**
 * Created by admin on 2016/10/25.
 */
public class SelectedControl extends LinearLayout {
    public static final int BUTTON_STYLE_COMMON = 0;
    public static final int BUTTON_STYLE_FILTER = BUTTON_STYLE_COMMON + 1;

    private int leftSelector = R.drawable.comment_sub_title_rb_left_selector;
    private int rightSelector = R.drawable.comment_sub_title_rb_right_selector;
    private int centerSelector = R.drawable.comment_sub_title_rb_selector;
    private int[] textColors = new int[] { 0xffffffff, 0xFF6677 }; // 0 normal 1 enable

    private ArrayList<SelectedButton> mTabButton = new ArrayList<SelectedButton>();
    private String[] nameArray = null;
    private int currentIndex = 0;
    private OnCheckedChangeListener listener = null;
    //文字的字体大小 dp
    private int textSize = 14;

    public SelectedControl(Context context) {
        super(context, null);
    }

    public SelectedControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTabButton = new ArrayList<SelectedButton>();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectedControl);
            int nameArrayId = a.getResourceId(R.styleable.SelectedControl_SelectedNames, -1);
            if (nameArrayId >= 0) {
                nameArray = this.getResources().getStringArray(nameArrayId);
                setTabArray(nameArray);
            }
            a.recycle();
        }
        this.setOrientation(LinearLayout.HORIZONTAL);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    private void genButton() {
        this.setGravity(Gravity.CENTER);
        for (int i = 0; i < nameArray.length; i++) {
            final SelectedButton button = new SelectedButton(this.getContext());
            if (i == 0) {
                button.setBackgroundResource(leftSelector);
            } else if (i == nameArray.length - 1) {
                button.setBackgroundResource(rightSelector);
            } else {
                button.setBackgroundResource(centerSelector);
            }

            button.setPadding(px(10), px(4), px(10), px(4));
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
            button.setText(nameArray[i]);
            button.setIndex(i);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    setCheck(button.getIndex());

                }
            });
            if(textColors != null && textColors.length > 0){
                button.setTextColor(textColors[0]);
            }
            addButton(button);
        }
    }

    public void addButton(SelectedButton button) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        mTabButton.add(button);
        this.addView(button, params);
    }

    public ArrayList<SelectedButton> getButtons() {
        return mTabButton;
    }

    private int px(float dp) {
        return CommonUtils.dp2px(dp);
    }

    // 设置默认选中
    public void setCheck(int n) {
        if (n < mTabButton.size()) {
            for (int i = 0; i < mTabButton.size(); i++) {
                mTabButton.get(i).setEnabled(true);
                mTabButton.get(i).setTextColor(textColors[0]);
            }
            currentIndex = n;
            mTabButton.get(n).setEnabled(false);
            mTabButton.get(n).setTextColor(textColors[1]);
            if (listener != null) {
                listener.onCheckedChanged(SelectedControl.this, n);
            }
        }
    }

    public int getCheckedIndex() {
        return currentIndex;
    }

    public void setTabArray(String[] names, int buttonStyle) {
//        switch (buttonStyle) {
//            case BUTTON_STYLE_COMMON:
//                leftSelector = R.drawable.segmented_button_left_selector;
//                rightSelector = R.drawable.segmented_button_right_selector;
//                centerSelector = R.drawable.segmented_button_center_selector;
//                textColors = new int[] { Color.parseColor(this.getResources().getString(R.color.common_color_white)),
//                        0xff1ba9ba };
//                break;
//            case BUTTON_STYLE_FILTER:
//                leftSelector = R.drawable.segmented_filter_button_left_selector;
//                rightSelector = R.drawable.segmented_filter_button_right_selector;
//                centerSelector = R.drawable.segmented_filter_button_center_selector;
//                textColors = new int[] { Color.GRAY, Color.WHITE };
//                break;
//            default:
//                break;
//
//        }
        if (this.mTabButton.size() > 0) {
            reset();
        }
        nameArray = names;
        genButton();
    }

    public void setTabArray(String[] names) {
        setTabArray(names, BUTTON_STYLE_COMMON);
    }

    public void setTabArray(int textSize,String[] names){
        this.textSize = textSize;
        setTabArray(names);
    }

    private void reset() {
        this.mTabButton.clear();
        this.removeAllViews();
    }

    public String getTabItemName(int position) {
        if (position < nameArray.length) {
            return nameArray[position];
        } else {
            return "";
        }
    }

    public void setTabLabelName(String name, int index) {
        SelectedButton sb = mTabButton.get(index);
        sb.setText(name);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public interface OnCheckedChangeListener {
        public void onCheckedChanged(LinearLayout viewParent, int checkedId);
    }
}
