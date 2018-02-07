package com.huapu.huafen.looper;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huapu.huafen.R;

/**
 * Created by admin on 2016/12/12.
 */
public class IndicatorView extends HorizontalScrollView {


    private static final int[] R_styleable_IndicatorView = new int[] {
    /* 0 */android.R.attr.src, };
    private static final int IndicatorView_src = 0;

    private int mCount;
    private int mPosition;
    private final LinearLayout mIconsLayout;
    private int mSrc;
    private int padding = 3;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R_styleable_IndicatorView);
        mSrc = a.getResourceId(IndicatorView_src, R.drawable.indicator_point);
        a.recycle();
        mIconsLayout = new LinearLayout(context);
        addView(mIconsLayout, new LayoutParams(WRAP_CONTENT, MATCH_PARENT, Gravity.CENTER));
    }

    public void setSrc(int mSrc,int padding) {
        this.mSrc = mSrc;
        this.padding = padding;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    public synchronized void setCount(int count) {
        mCount = count;
        mIconsLayout.removeAllViews();
        // 只有一个元素时不显示
        if (mCount == 1) {
            return;
        }
        for (int i = 0; i < mCount; i++) {
            final ImageView child = new ImageView(getContext());
            child.setImageResource(mSrc);
            child.setPadding(padding, 3, padding, 3);
            mIconsLayout.addView(child);
        }
        setPosition(mPosition);
    }

    public synchronized void setPosition(int position) {
        if (position > mCount) {
            return;
        }
        if (mIconsLayout.getChildCount() == 0) {
            mPosition = 0;
            return;
        }
        if (mPosition != position) {
            ImageView view = (ImageView) mIconsLayout.getChildAt(mPosition);
            if (null != view) {
                view.setSelected(false);
            }
        }
        mPosition = position;
        ImageView view = (ImageView) mIconsLayout.getChildAt(mPosition);
        if (null != view) {
            view.setSelected(true);
        }
    }

//    public void setGallery(Gallery gallery) {
//        setCount(gallery.getCount());
//        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                setPosition(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }

    public static class SavedState extends BaseSavedState {
        public int selectedTabIndex;

        public SavedState(Parcel source) {
            super(source);
            selectedTabIndex = source.readInt();
        }

        public SavedState(Parcelable superState, int selected) {
            super(superState);
            this.selectedTabIndex = selected;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(selectedTabIndex);
        }

        @SuppressWarnings("all")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), mPosition);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setPosition(ss.selectedTabIndex);
    }
}
