package com.huapu.huafen.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.FlowerData;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.utils.CommonUtils;

public class CommonTitleView extends LinearLayout {

    private final static int HORIZONTAL = 0;
    private final static int VERTICAL = HORIZONTAL + 1;
    private int orientation;
    private TextView tvName;
    private ImageView ivXING;
    private ImageView ivVIP;
    private ImageView ivXIN;
    private ImageView ivZMxy;
    private View layoutImage;

    public CommonTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleView);
        orientation = array.getInt(R.styleable.CommonTitleView_orientation, HORIZONTAL);
        array.recycle();
        initView();
    }

    public void setTextColor(int color){
        tvName.setTextColor(color);
    }

    public CommonTitleView(Context context) {
        super(context, null);
        initView();
    }

    public void setData(UserInfo info) {
        if (info == null) {
            return;
        }
        if (info.getUserName() != null) {
            tvName.setText(info.getUserName());
        }

        /**
         * 花粉儿小秘书官方显示
         */
        if (info.getAuthorityTag() == 1) {
            ivZMxy.setVisibility(View.GONE);
            ivXING.setVisibility(View.GONE);
            ivXIN.setVisibility(View.GONE);
            ivVIP.setVisibility(View.VISIBLE);
            ivVIP.setImageResource(R.drawable.guan_fang);
            return;
        }

        int userLevel = info.getUserLevel();
        if (userLevel <= 1) {
            ivVIP.setVisibility(View.GONE);
            ivXING.setVisibility(View.GONE);
        } else if (userLevel == 2) {
            ivVIP.setVisibility(View.VISIBLE);
            ivXING.setVisibility(View.GONE);
        } else if (userLevel >= 3) {
            ivVIP.setVisibility(View.GONE);
            ivXING.setVisibility(View.VISIBLE);
        }
        if (info.getHasCredit()||info.hasVerified) {
            ivXIN.setVisibility(View.GONE);
        } else {
            ivXIN.setVisibility(View.GONE);
        }

        if (userLevel >= 2) {
            ivZMxy.setVisibility(GONE);
        } else {
            if (info.getZmCreditPoint() > 0||info.hasVerified) {
                ivZMxy.setVisibility(View.VISIBLE);
            } else {
                ivZMxy.setVisibility(View.GONE);
            }
        }

    }

    public TextView getNameTextView() {
        return tvName;
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

    public void setData(FlowerData data) {
        int count = 0;
        if (data == null) {
            return;
        }
        if (!TextUtils.isEmpty(data.getUser().getUserName())) {
            tvName.setText(data.getUser().getUserName());
        }

        int userLevel = data.getUser().getUserLevel();
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
        if (data.getUser().getHasCredit()||data.getUser().hasVerified) {
            ivXIN.setVisibility(View.GONE);
        } else {
            ivXIN.setVisibility(View.GONE);
        }

        if (userLevel >= 2) {
            ivZMxy.setVisibility(GONE);
        } else {
            if (data.getUser().getZmCreditPoint() > 0||data.getUser().hasVerified) {
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


    public void setDataVertical(UserData data) {
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
        } else if (userLevel >= 3) {
            ivVIP.setVisibility(View.GONE);
            ivXING.setVisibility(View.VISIBLE);
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
            } else {
                ivZMxy.setVisibility(View.GONE);
            }
        }

    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (orientation == HORIZONTAL) {
            inflater.inflate(R.layout.common_title_layout, this, true);
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
    public void setNameSize(int type,float size) {
        tvName.setTextSize(type,size);
    }
}
