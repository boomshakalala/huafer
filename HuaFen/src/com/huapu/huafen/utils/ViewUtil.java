package com.huapu.huafen.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.R;
import com.huapu.huafen.banner.ClassBannerHolder;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.common.TypeConstants;
import com.huapu.huafen.views.PtrAnimationBackgroundHeader;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * ImageView创建工厂
 */
public class ViewUtil {

    /**
     * 获取ImageView视图的同时加载显示url
     */
    public static ImageView getImageView(Context context, String url) {
        SimpleDraweeView imageView = (SimpleDraweeView) LayoutInflater.from(context).inflate(
                R.layout.view_cycle_viewpager_image, null);
        ImageLoader.loadImage(imageView, url);
        return imageView;
    }

    // PtrFrameLayout 属性设置
    public static void setPtrFrameLayout(PtrFrameLayout mPtrFrame) {
        PtrAnimationBackgroundHeader header = new PtrAnimationBackgroundHeader(mPtrFrame.getContext());
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(300);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
    }

    // 空视图
    public static View initImgEmptyView(ViewGroup viewGroup, int imgResId) {
        View viewEmpty = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_empty_image, viewGroup, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.getLayoutParams().width = CommonUtils.getScreenWidth();
        ivEmpty.getLayoutParams().height = CommonUtils.getScreenHeight();
        ivEmpty.setImageResource(imgResId);

        return viewEmpty;
    }

    public static void setAvatar(SimpleDraweeView ivHeader, String url) {
        if (TextUtils.isEmpty(url)) {
            ivHeader.setImageURI("");
            ivHeader.setTag("");
            return;
        }

        String a = (String) ivHeader.getTag();

        if (!TextUtils.isEmpty(a) && TextUtils.equals(a, url))
            return;

        setImg(ivHeader, url, 1, TypeConstants.TYPE_IMAGE_SIZE_SMALL);
    }

    public static void setImgMiddle(SimpleDraweeView iv, String url, float ratio) {
        String a = (String) iv.getTag();
        if (!TextUtils.isEmpty(a) && TextUtils.equals(a, url))
            return;
        setImg(iv, url, ratio, TypeConstants.TYPE_IMAGE_SIZE_MIDDLE);
    }

    private static void setImg(SimpleDraweeView iv, String url, float ratio, int type) {
        switch (type) {
            case TypeConstants.TYPE_IMAGE_SIZE_SMALL:
                ImageLoader.resizeSmall(iv, url, ratio);
                break;
            case TypeConstants.TYPE_IMAGE_SIZE_MIDDLE:
                ImageLoader.resizeMiddle(iv, url, ratio);
                break;
            case TypeConstants.TYPE_IMAGE_SIZE_LARGE:
                ImageLoader.resizeLarge(iv, url, ratio);
                break;
        }
        iv.setTag(url);
    }

    public static void setOffItemAnimator(RecyclerView recyclerView) {
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public static int getTenDp() {
        return MyApplication.getApplication().getResources()
                .getDimensionPixelSize(R.dimen.flower_space_ten);
    }


    public static void updateBanner(final ConvenientBanner banner, final BannerData bannerData){
        if (bannerData == null || bannerData.getBanners() == null || bannerData.getBanners().size() == 0) {
            banner.setVisibility(View.GONE);
            return;
        }
        banner.setVisibility(View.VISIBLE);
        if (bannerData.getBanners().size()>1){
            banner.setCanLoop(false);
            CommonUtils.setAutoLoop(bannerData, banner);
        }else {
            banner.setCanLoop(false);
        }
        banner.setPages(new CBViewHolderCreator<ClassBannerHolder>() {
            @Override
            public ClassBannerHolder createHolder() {
                return new ClassBannerHolder();
            }

        }, bannerData.getBanners());

        banner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CampaignBanner ca = bannerData.getBanners().get(position);
                ActionUtil.dispatchAction(banner.getContext(), ca.getAction(), ca.getTarget());
            }
        });
    }
}
