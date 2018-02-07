package com.huapu.huafen.banner;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.holder.Holder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.utils.CommonUtils;

/**
 *
 * 加载网络数据
 */
public class ClassBannerHolder implements Holder<CampaignBanner> {
    private SimpleDraweeView imageView;

    @Override
    public View createView(Context context) {

        imageView= new SimpleDraweeView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                CommonUtils.getScreenWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        imageView.setAspectRatio(1.33f);

        GenericDraweeHierarchyBuilder builder = GenericDraweeHierarchyBuilder.newInstance(context.getResources());

        GenericDraweeHierarchy genericDraweeHierarchy = builder.
                setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP).
                setPlaceholderImage(R.drawable.default_pic, ScalingUtils.ScaleType.FOCUS_CROP).
                setFailureImage(R.drawable.default_pic, ScalingUtils.ScaleType.FOCUS_CROP).build();
        imageView.setHierarchy(genericDraweeHierarchy);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, CampaignBanner data) {

        String url =data.getImage();
        String tag = (String)imageView.getTag();
        if(TextUtils.isEmpty(tag)||!tag.equals(url)){
            imageView.setTag(url);
            imageView.setImageURI(url);
        }
    }

}
