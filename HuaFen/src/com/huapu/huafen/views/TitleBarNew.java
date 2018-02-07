package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.LogUtil;

/**
 * Created by admin on 2016/11/2.
 */
public class TitleBarNew extends LinearLayout implements View.OnTouchListener {

    private final static String TAG = TitleBarNew.class.getSimpleName();
    private RelativeLayout layoutBase;
    private ImageView btnTitleLeft;//左侧箭头退出
    private FrameLayout centerLayout;//中间区域
    private TextView tvTitleText;//标题
    private ImageView btnTitleRight;//右侧按钮
    private ImageView btnTitleBarRight2;
    private CharSequence mTitleText;//标题文案
    private TitleBarDoubleOnClick titleBarDoubleOnClick;
    private TextView tvTitleLeft;
    private TextView tvTitleRight;
    private View divider;
    private TextView tvMoreMsgUnRead;

    //leftText
    public TitleBarNew setLeftText(CharSequence text, int color, OnClickListener onLeftTextListener) {
        btnTitleLeft.setVisibility(GONE);
        tvTitleLeft.setVisibility(VISIBLE);
        tvTitleLeft.setText(text);
        tvTitleLeft.setTextColor(color);
        tvTitleLeft.setOnClickListener(onLeftTextListener);
        return this;
    }

    public TitleBarNew setLeftText(CharSequence text, ColorStateList colorStateList, OnClickListener onLeftTextListener) {
        btnTitleLeft.setVisibility(GONE);
        tvTitleLeft.setVisibility(VISIBLE);
        tvTitleLeft.setText(text);
        tvTitleLeft.setTextColor(colorStateList);
        tvTitleLeft.setOnClickListener(onLeftTextListener);
        return this;
    }

    public TitleBarNew setLeftText(CharSequence text, OnClickListener onLeftTextListener) {
        setLeftText(text, Color.parseColor("#333333"), onLeftTextListener);
        return this;
    }

    public TextView getTitleTextLeft() {
        return tvTitleLeft;
    }


    public TitleBarNew setRightText(CharSequence text, ColorStateList colorStateList, OnClickListener onRightTextListener) {
        btnTitleRight.setVisibility(GONE);
        tvTitleRight.setVisibility(VISIBLE);
        tvTitleRight.setText(text);
        tvTitleRight.setTextColor(colorStateList);
        tvTitleRight.setOnClickListener(onRightTextListener);
        return this;
    }

    public TitleBarNew setRightText(CharSequence text, int color, OnClickListener onRightTextListener) {
        btnTitleRight.setVisibility(GONE);
        tvTitleRight.setVisibility(VISIBLE);
        tvTitleRight.setText(text);
        tvTitleRight.setTextColor(color);
        tvTitleRight.setOnClickListener(onRightTextListener);
        return this;
    }

    public TitleBarNew setRightText(CharSequence text, OnClickListener onRightTextListener) {
        setRightText(text, Color.parseColor("#333333"), onRightTextListener);
        return this;
    }

    public FrameLayout getCenterLayout() {
        return centerLayout;
    }

    public TextView getTitleTextRight() {
        return tvTitleRight;
    }

    public TitleBarNew(Context context) {
        this(context, null);
    }

    public TitleBarNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TitleBarNew);
        mTitleText = array.getString(R.styleable.TitleBarNew_titleBarText);
        array.recycle();

        //初始化控件
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.title_bar_new, this, true);

        layoutBase = (RelativeLayout) findViewById(R.id.layoutBase);
        btnTitleLeft = (ImageView) findViewById(R.id.ivTitleBarLeft);
        btnTitleLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    ((Activity) getContext()).onBackPressed();
                } catch (Exception e) {
                    LogUtil.e(TAG, "crash.." + e.getMessage());
                }
            }
        });
        centerLayout = (FrameLayout) findViewById(R.id.titleBarCenterLayout);
        centerLayout.setOnTouchListener(this);
        tvTitleText = (TextView) findViewById(R.id.tvTitleBarText);
        btnTitleRight = (ImageView) findViewById(R.id.btnTitleBarRight);
        btnTitleBarRight2 = (ImageView) findViewById(R.id.btnTitleBarRight2);

        tvTitleLeft = (TextView) findViewById(R.id.tvTitleBarLeft);
        tvTitleRight = (TextView) findViewById(R.id.tvTitleBarRight);

        divider = findViewById(R.id.divider);
        tvMoreMsgUnRead = (TextView) findViewById(R.id.tvMoreMsgUnRead);
        if (!TextUtils.isEmpty(mTitleText)) {
            setTitle(mTitleText);
        }
    }

    private long firstClick;
    private long lastClick;
    // 计算点击的次数
    private int count;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
                if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
                    count = 0;
                }
                count++;
                if (count == 1) {
                    firstClick = System.currentTimeMillis();
                } else if (count == 2) {
                    lastClick = System.currentTimeMillis();
                    // 两次点击小于300ms 也就是连续点击
                    if (lastClick - firstClick < 300) {// 判断是否是执行了双击事件
                        if (null != titleBarDoubleOnClick) {
                            titleBarDoubleOnClick.onTitleBarDoubleOnClick();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public void setRightTextColor(int rightTextColor) {
        this.tvTitleRight.setTextColor(rightTextColor);
    }


    public interface TitleBarDoubleOnClick {
        void onTitleBarDoubleOnClick();
    }

    public TitleBarNew setTitleOnClick(TitleBarDoubleOnClick titleBarDoubleOnClick) {
        this.titleBarDoubleOnClick = titleBarDoubleOnClick;
        return this;
    }

    public TitleBarNew setTitle(CharSequence titleText) {
        this.setVisibility(VISIBLE);
        this.mTitleText = titleText;
        if (!TextUtils.isEmpty(mTitleText)) {
            tvTitleText.setVisibility(View.VISIBLE);
            tvTitleText.setText(mTitleText);
        }
        return this;
    }

    public TitleBarNew setTitle(View centerView) {
        this.setVisibility(VISIBLE);
        tvTitleText.setVisibility(View.GONE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        centerLayout.addView(centerView, lp);
        return this;
    }

    public TitleBarNew setOnLeftButtonClickListener(int resId, OnClickListener listener) {
        btnTitleLeft.setVisibility(VISIBLE);
        tvTitleLeft.setVisibility(GONE);
        btnTitleLeft.setImageResource(resId);
        btnTitleLeft.setOnClickListener(listener);
        return this;
    }


    public TitleBarNew setOnLeftButtonClickListener(OnClickListener listener) {
        btnTitleLeft.setVisibility(VISIBLE);
        tvTitleLeft.setVisibility(GONE);
        btnTitleLeft.setImageResource(R.drawable.personal_title_back_close);
        btnTitleLeft.setOnClickListener(listener);
        return this;
    }

    public TitleBarNew setOnRightButtonClickListener(int resId, OnClickListener listener) {
        btnTitleRight.setVisibility(VISIBLE);
        tvTitleRight.setVisibility(GONE);
        btnTitleRight.setImageResource(resId);
        btnTitleRight.setOnClickListener(listener);
        return this;
    }

    public TitleBarNew setOnRightButtonClickListener(OnClickListener listener) {
        btnTitleRight.setVisibility(VISIBLE);
        tvTitleRight.setVisibility(GONE);
        btnTitleRight.setOnClickListener(listener);
        return this;
    }

    public TitleBarNew setOnRightButton2ClickListener(int resId, OnClickListener listener) {
        btnTitleBarRight2.setVisibility(VISIBLE);
        tvTitleRight.setVisibility(GONE);
        btnTitleBarRight2.setImageResource(resId);
        btnTitleBarRight2.setOnClickListener(listener);
        return this;
    }

    public TitleBarNew setOnRightButton2ClickListener(OnClickListener listener) {
        btnTitleBarRight2.setVisibility(VISIBLE);
        tvTitleRight.setVisibility(GONE);
        btnTitleBarRight2.setOnClickListener(listener);
        return this;
    }

    public ImageView getBtnTitleBarRight2() {
        return btnTitleBarRight2;
    }

    public ImageView getBtnTitleRight() {
        return btnTitleRight;
    }

    public void setBackgroundAlpha(int f) {
        layoutBase.getBackground().mutate().setAlpha(f);
    }

    private int overallXScroll = 0;

    public void setAlphaInRecyclerView(int dy, int lHeight) {
        overallXScroll = overallXScroll + dy;// 累加y值 解决滑动一半y值为0
        if (overallXScroll <= 0) {   //设置标题的背景颜色
            setBackgroundAlpha(0);
            divider.setVisibility(INVISIBLE);
            if (getBtnTitleBarRight2().getBackground() != null) {
                getBtnTitleBarRight2().getBackground().mutate().setAlpha(255);
            }
            if (getBtnTitleRight().getBackground() != null) {
                getBtnTitleRight().getBackground().mutate().setAlpha(255);
            }
            if (getBtnTitleLeft().getBackground() != null) {
                getBtnTitleLeft().getBackground().mutate().setAlpha(255);
            }
            centerLayout.setVisibility(GONE);
        } else if (overallXScroll > 0 && overallXScroll <= lHeight) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
            float scale = (float) overallXScroll / lHeight;
            float alpha = (255 * scale);
            setBackgroundAlpha((int) alpha);
            if (getBtnTitleBarRight2().getBackground() != null) {
                getBtnTitleBarRight2().getBackground().mutate().setAlpha((int) (255 - alpha));
            }
            if (getBtnTitleRight().getBackground() != null) {
                getBtnTitleRight().getBackground().mutate().setAlpha((int) (255 - alpha));
            }
            if (getBtnTitleLeft().getBackground() != null) {
                getBtnTitleLeft().getBackground().mutate().setAlpha((int) (255 - alpha));
            }

            if (alpha > 100) {
                getBtnTitleBarRight2().setImageResource(R.drawable.personal_title_share_close);
                getBtnTitleRight().setImageResource(R.drawable.personal_title_more_close);
                getBtnTitleLeft().setImageResource(R.drawable.personal_title_back_close);
                centerLayout.setVisibility(VISIBLE);
            } else {
                getBtnTitleBarRight2().setImageResource(R.drawable.personal_title_share);
                getBtnTitleRight().setImageResource(R.drawable.personal_title_more);
                getBtnTitleLeft().setImageResource(R.drawable.personal_title_back);
                centerLayout.setVisibility(GONE);
            }
            divider.setVisibility(INVISIBLE);
        } else {
            setBackgroundAlpha(255);
            if (getBtnTitleBarRight2().getBackground() != null) {
                getBtnTitleBarRight2().getBackground().mutate().setAlpha(0);
            }
            if (getBtnTitleRight().getBackground() != null) {
                getBtnTitleRight().getBackground().mutate().setAlpha(0);
            }
            if (getBtnTitleLeft().getBackground() != null) {
                getBtnTitleLeft().getBackground().mutate().setAlpha(0);
            }
            divider.setVisibility(VISIBLE);
            centerLayout.setVisibility(VISIBLE);
        }
    }

    public void showMoreBtnBadge(boolean yes) {
        tvMoreMsgUnRead.setVisibility(yes ? VISIBLE : GONE);
    }

    public boolean getMoreBtnBadgeVisibility() {
        return tvMoreMsgUnRead.getVisibility() == View.VISIBLE;
    }

    public ImageView getBtnTitleLeft() {
        return btnTitleLeft;
    }


    public View getDivider() {
        return divider;
    }
}
