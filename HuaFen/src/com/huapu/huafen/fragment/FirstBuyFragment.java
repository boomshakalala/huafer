package com.huapu.huafen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huapu.huafen.R;
import com.huapu.huafen.fragment.base.BaseFragment;

/**
 * Created by admin on 2017/4/5.
 */

public class FirstBuyFragment extends BaseFragment {

    private ImageView ivBuy;
    private ImageView ivIKnow;

    public static FirstBuyFragment newInstance(Bundle bundle){
        FirstBuyFragment fragment = new FirstBuyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_first_buy, container, false);
    }

    @Override
    public void onViewCreated(View root) {
        super.onViewCreated(root);
        Bundle bundle = getArguments();
        int res = bundle.getInt("first_buy_src", 0);
        ivBuy = (ImageView) root.findViewById(R.id.ivBuy);
        ivBuy.setImageResource(res);


    }
}
