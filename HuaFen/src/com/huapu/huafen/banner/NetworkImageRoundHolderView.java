package com.huapu.huafen.banner;

import android.content.Context;
import android.view.View;

import com.bigkoo.convenientbanner.holder.Holder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.utils.ImageLoader;

/**
 * 加载网络数据
 */
public class NetworkImageRoundHolderView implements Holder<String> {
    private SimpleDraweeView imageView;

    @Override
    public View createView(Context context) {
        imageView = new SimpleDraweeView(context);
        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.default_pic);
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        ImageLoader.loadImage(imageView, data);
    }
}
