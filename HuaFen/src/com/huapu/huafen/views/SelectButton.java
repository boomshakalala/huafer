package com.huapu.huafen.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.events.PriceEvent;

import de.greenrobot.event.EventBus;


/**
 * Created by admin on 2016/11/14.
 */
public class SelectButton extends LinearLayout {

    public int index = -1;
    private TextView tvName;
    private ImageView ivArrow;
    private int price_state = 0;
    private boolean isCheck;

    public SelectButton(Context context) {
        this(context, null);
    }

    public SelectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.select_button_layout, this, true);

        tvName = (TextView) findViewById(R.id.tvName);
        ivArrow = (ImageView) findViewById(R.id.ivArrow);
    }

    public void onSelect(boolean select) {

        if (select) {
            tvName.setTextColor(getResources().getColor(R.color.base_pink));
            if (index == 5) {
                if (price_state == 0) {
                    ivArrow.setImageResource(R.drawable.price_up);
                    price_state = 1;
                    EventBus.getDefault().post(new PriceEvent(1));
                } else if (price_state == 1) {
                    ivArrow.setImageResource(R.drawable.price_low);
                    price_state = 0;
                    EventBus.getDefault().post(new PriceEvent(0));
                }


            } else {
                ivArrow.setImageResource(R.drawable.pink_arrow_up);
            }

        } else {
            tvName.setTextColor(getResources().getColor(R.color.text_black));
            if (index == 5) {
                price_state = 0;
                ivArrow.setImageResource(R.drawable.price_default);
            } else {
                ivArrow.setImageResource(R.drawable.grey_arrow_down);
            }

        }

        isCheck = select;

    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setTextName(CharSequence textName) {
        if (!TextUtils.isEmpty(textName)) {
            if (textName.length() > 2) {
                textName = textName.subSequence(0, 2) + "...";
            }
            tvName.setText(textName);
        }

    }


    /**
     * state=0 default
     * state=1 down
     * state=2 up
     *
     * @param state
     */
    public void setIcon(int state) {
        ivArrow.setVisibility(View.VISIBLE);
        switch (state) {
            case 0:
                ivArrow.setImageResource(R.drawable.price_default);
                break;
            case 1:
                ivArrow.setImageResource(R.drawable.price_low);
            case 2:
                ivArrow.setImageResource(R.drawable.price_up);
                break;
        }


    }

    public void hideIcon() {
        ivArrow.setVisibility(View.GONE);
    }

    public void setTextColor(int colorRes) {
        tvName.setTextColor(colorRes);
    }

}
