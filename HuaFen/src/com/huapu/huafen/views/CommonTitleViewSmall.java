package com.huapu.huafen.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.utils.CommonUtils;

public class CommonTitleViewSmall extends LinearLayout {

    private final static int HORIZONTAL = 0;
    private final static int VERTICAL = HORIZONTAL + 1;
    private int orientation;
    private TextView tvName;
    private ImageView ivXING;
    private ImageView ivVIP;
    private ImageView ivXIN;
    private ImageView ivZMxy;
    private View layoutImage;

    public CommonTitleViewSmall(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleView);
        orientation = array.getInt(R.styleable.CommonTitleView_orientation, HORIZONTAL);
        array.recycle();
        initView();
    }

    public CommonTitleViewSmall(Context context) {
        super(context, null);
        initView();
    }

    public void setData(UserData data) {
        int count = 0;
        if (data == null) {
            return;
        }
        if (!TextUtils.isEmpty(data.getUserName())) {
            tvName.setText(data.getUserName());
        }

        int userLevel = data.getUserLevel();
        if (userLevel <= 1) {
            ivVIP.setVisibility(View.GONE);
            ivXING.setVisibility(View.GONE);
        } else if (userLevel == 2) {
            ivVIP.setVisibility(View.VISIBLE);
            ivXING.setVisibility(View.GONE);
            count = 1;
        } else if (userLevel >= 3) {
            ivVIP.setVisibility(View.GONE);
            ivXING.setVisibility(View.VISIBLE);
            count = 2;
        }
        if (data.getHasCredit()||data.hasVerified) {
            ivXIN.setVisibility(View.GONE);
        } else {
            ivXIN.setVisibility(View.GONE);
        }

        if (userLevel >= 2) {
            ivZMxy.setVisibility(GONE);
        } else {
            if (data.getZmCreditPoint() > 0||data.hasVerified) {
                ivZMxy.setVisibility(View.VISIBLE);
                if (count == 0) {
                    count = 1;
                } else {
                    count++;
                }
            } else {
                ivZMxy.setVisibility(View.GONE);
            }
        }

        LayoutParams nameParams = (LayoutParams) tvName.getLayoutParams();
        LayoutParams layoutParams = (LayoutParams) layoutImage.getLayoutParams();
        int right = 0;
        int left = 0;
        switch (count) {
            case 0:
                right = CommonUtils.dp2px(5);
                nameParams.rightMargin = right;
                tvName.setLayoutParams(nameParams);
                left = CommonUtils.dp2px(0);
                layoutParams.leftMargin = left;
                layoutImage.setLayoutParams(layoutParams);
                break;
            case 1:
                right = CommonUtils.dp2px(20);
                nameParams.rightMargin = right;
                tvName.setLayoutParams(nameParams);
                left = CommonUtils.dp2px(-20);
                layoutParams.leftMargin = left;
                layoutImage.setLayoutParams(layoutParams);
                break;
            case 2:
                right = CommonUtils.dp2px(35);
                nameParams.rightMargin = right;
                tvName.setLayoutParams(nameParams);
                left = CommonUtils.dp2px(-35);
                layoutParams.leftMargin = left;
                layoutImage.setLayoutParams(layoutParams);
                break;
            case 3:
                right = CommonUtils.dp2px(50);
                nameParams.rightMargin = right;
                tvName.setLayoutParams(nameParams);
                left = CommonUtils.dp2px(-50);
                layoutParams.leftMargin = left;
                layoutImage.setLayoutParams(layoutParams);
                break;
        }
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (orientation == HORIZONTAL) {
            inflater.inflate(R.layout.common_title_layout_small, this, true);
        } else if (orientation == VERTICAL) {
            inflater.inflate(R.layout.common_title_layout_vertical, this, true);
            setOrientation(LinearLayout.VERTICAL);
        } else {
            throw new RuntimeException("unable orientation value " + orientation);
        }

        tvName = (TextView) findViewById(R.id.tvName);
        ivXING = (ImageView) findViewById(R.id.ivXING);
        ivVIP = (ImageView) findViewById(R.id.ivVIP);
        ivXIN = (ImageView) findViewById(R.id.ivXIN);
        ivZMxy = (ImageView) findViewById(R.id.ivZMxy);
        layoutImage = findViewById(R.id.layoutImage);
    }

    public void setNameColor(int color) {
        tvName.setTextColor(color);
    }
}
