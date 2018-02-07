package com.huapu.huafen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;

/**
 * Created by admin on 2016/12/14.
 */
public class BannerFragment extends BaseFragment implements View.OnClickListener {

    private SimpleDraweeView imageView;
    private VBanner banner;
    private ImageView ivLogo;
    private boolean rise;
    private float dp;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        banner = (VBanner) getArguments().getSerializable("banner");
        rise = getArguments().getBoolean("riselogo");
        if (banner == null) {
            return;
        }

        ImageLoader.loadImage(imageView, banner.imgUrl);

        if (ivLogo != null && rise) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ivLogo.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.bottomMargin = CommonUtils.dp2px(40);
            }
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("banner", banner);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    @Override
    public void onViewCreated(View root) {
        imageView = (SimpleDraweeView) root.findViewById(R.id.banner);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onViewClick != null) {
                    onViewClick.onClick();
                }
            }
        });
        ivLogo = (ImageView) root.findViewById(R.id.ivLogo);


    }

    public interface OnViewClick {
        void onClick();
    }

    private OnViewClick onViewClick;

    public void setOnViewClick(OnViewClick onViewClick) {
        this.onViewClick = onViewClick;
    }

}
