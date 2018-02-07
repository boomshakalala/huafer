package com.huapu.huafen.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.activity.ClassificationDetailActivity;

/**
 * Created by admin on 2016/11/14.
 */
public class FilterSelectView extends LinearLayout {


    private Context context;
    private OnCheckedChangedListener mOnCheckedChangedListener;

    CharSequence[] selections;

    public FilterSelectView(Context context) {
        this(context, null);
    }


    public FilterSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FilterSelectView);
        selections = array.getTextArray(R.styleable.FilterSelectView_selections);
        setSelections(selections);
        array.recycle();
    }


    public void setSelections(int except) {
        removeAllViews();
        int count = selections.length;
        for (int i = 0; i < count; i++) {
            if (i == except) {
                continue;
            }
            if (except == 2) {
                if (i > except) {
                    continue;
                }
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LayoutParams.MATCH_PARENT, 1);
            final SelectButton button = new SelectButton(getContext());
            button.setGravity(Gravity.CENTER_HORIZONTAL);
            button.index = i;
            if (i > 1) {
                button.hideIcon();
            }
            if (i == count - 1) {
                button.setIcon(0);
            }
            button.setTextName(selections[i]);
            button.setLayoutParams(params);
            addView(button);

            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearChecked(button.index);

                    if (mOnCheckedChangedListener != null) {
                        mOnCheckedChangedListener.onChecked(FilterSelectView.this, button);
                    }
                }
            });

            button.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }
    }

    public void setSelections(CharSequence[] selections) {

        removeAllViews();
        int count = selections.length;
        for (int i = 0; i < count; i++) {
            if (i == 1) {
                if (context instanceof ClassificationDetailActivity) {
                    ClassificationDetailActivity detailActivity = (ClassificationDetailActivity) context;
                    if (!detailActivity.showClass) {
                        continue;
                    }

                }
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LayoutParams.MATCH_PARENT, 1);
            final SelectButton button = new SelectButton(getContext());
            button.setGravity(Gravity.CENTER_HORIZONTAL);
            button.index = i;
            if (i > 1) {
                button.hideIcon();
            }
            if (i == count - 1) {
                button.setIcon(0);
            }
            button.setTextName(selections[i]);
            button.setLayoutParams(params);
            addView(button);

            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearChecked(button.index);

                    if (mOnCheckedChangedListener != null) {
                        mOnCheckedChangedListener.onChecked(FilterSelectView.this, button);
                    }
                }
            });

            button.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }
    }

    private void clearChecked(int index) {
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof SelectButton) {
                SelectButton btn = (SelectButton) getChildAt(i);
                if (btn.index != index) {
                    btn.onSelect(false);
                } else {
                    boolean isCheck = btn.isCheck();
                    if (btn.index == 5) {
                        btn.onSelect(true);
                    } else {
                        btn.onSelect(!isCheck);
                    }

                }
            }
        }
    }

    public void clearTwoChecked(int index) {
        for (int i = 0; i < 2; i++) {
            View child = getChildAt(i);
            if (child instanceof SelectButton) {
                SelectButton btn = (SelectButton) getChildAt(i);
                if (btn.index != index) {
                    btn.onSelect(false);
                } else {
                    boolean isCheck = btn.isCheck();
                    if (btn.index == 5) {
                        btn.onSelect(true);
                    } else {
                        btn.onSelect(!isCheck);
                    }

                }
            }
        }
    }

    public void clearOneChecked(int index) {
        for (int i = 0; i < 1; i++) {
            View child = getChildAt(i);
            if (child instanceof SelectButton) {
                SelectButton btn = (SelectButton) getChildAt(i);
                if (btn.index != index) {
                    btn.onSelect(false);
                } else {
                    boolean isCheck = btn.isCheck();
                    if (btn.index == 5) {
                        btn.onSelect(true);
                    } else {
                        btn.onSelect(!isCheck);
                    }

                }
            }
        }
    }


    public void setTitleByIndex(String title, int index) {
        SelectButton button = findButtonByIndex(index);
        button.setTextName(title);
    }

    public SelectButton findButtonByIndex(int index) {
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof SelectButton) {
                SelectButton btn = (SelectButton) getChildAt(i);
                if (btn.index != index) {
                    continue;
                } else {
                    return btn;
                }
            }
        }
        return null;
    }

    public void setSelectButtonsEnable(boolean enable) {
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof SelectButton) {
                SelectButton btn = (SelectButton) getChildAt(i);
                btn.setEnabled(enable);
            }
        }
    }

    public void clearChecked() {
        clearChecked(-1);
    }

    public void setOnCheckedChangedListener(OnCheckedChangedListener onCheckedChangedListener) {
        this.mOnCheckedChangedListener = onCheckedChangedListener;
    }

    public interface OnCheckedChangedListener {
        void onChecked(FilterSelectView v, SelectButton button);
    }

}
