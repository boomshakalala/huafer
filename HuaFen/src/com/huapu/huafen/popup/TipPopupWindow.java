package com.huapu.huafen.popup;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.huapu.huafen.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/7/21.
 */

public class TipPopupWindow extends BaseNougatFixPopupWindow{

    private String text;

    public void setText(String text) {
        this.text = text;
    }

    @BindView(R.id.tvTip) TextView tvTip;

    public TipPopupWindow(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.tip_popup, null);
        setContentView(view);
        ButterKnife.bind(this,view);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if(!TextUtils.isEmpty(text)){
            tvTip.setText(text);
        }
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }
}
