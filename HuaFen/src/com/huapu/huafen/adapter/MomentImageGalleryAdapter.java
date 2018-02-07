package com.huapu.huafen.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.TagsContainer;

import java.util.List;

/**
 * Created by qwe on 2017/5/3.
 */

public class MomentImageGalleryAdapter extends PagerAdapter {

    private float SCREEN_RATIO = 0.92f;
    private float MIN_ASPECT_RATIO = 0.75f;
    private List<FloridData.TitleMedia> titleMediaList;

    public List<FloridData.TitleMedia> getTitleMediaList() {
        return titleMediaList;
    }

    public MomentImageGalleryAdapter(List<FloridData.TitleMedia> titleMediaList) {
        this.titleMediaList = titleMediaList;
    }

    public void setEditable(boolean editable) {
        if (!ArrayUtil.isEmpty(titleMediaList)) {
            for (FloridData.TitleMedia media : titleMediaList) {
                media.editAble = editable;
            }
            notifyDataSetChanged();
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.gallery_edit, null, false);
        final TagsContainer tagsContainer = (TagsContainer) view.findViewById(R.id.tagsContainer);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tagsContainer.getLayoutParams();
        container.addView(view);
        FloridData.TitleMedia media = titleMediaList.get(position);
        if (media != null) {
            if (!media.url.startsWith(MyConstants.HTTP) && !media.url.startsWith(MyConstants.HTTPS)) {
                int[] outSize = CommonUtils.measureBitmap(media.url);
                media.width = outSize[0];
                media.height = outSize[1];
            }
            if (media.width != 0 && media.height != 0) {
                float aspectRatio = (float) media.width / media.height;
                if (aspectRatio < MIN_ASPECT_RATIO) {
                    aspectRatio = MIN_ASPECT_RATIO;
                }

                if (aspectRatio < SCREEN_RATIO) {
                    params.height = (int) (CommonUtils.getScreenWidth() / SCREEN_RATIO);
                    params.width = (int) (params.height * aspectRatio);

                } else {
                    params.width = FrameLayout.LayoutParams.MATCH_PARENT;
                    params.height = (int) (CommonUtils.getScreenWidth() / aspectRatio);
                }


                params.gravity = Gravity.CENTER;

                tagsContainer.setLayoutParams(params);
                tagsContainer.setData(media, media.editAble);
                tagsContainer.setOnHandleContainerListener(new TagsContainer.OnSimpleHandleContainerListener() {

                    @Override
                    public void onAddTag(float x, float y) {
                        int[] location = new int[2];
                        location[0] = (int) x;
                        location[1] = (int) y;
                        if (onHandlerTagsListener != null) {
                            onHandlerTagsListener.onAddTag(tagsContainer, location, position);
                        }
                    }
                });
            }

        }

        return view;
    }

    public interface OnHandlerTagsListener {
        void onAddTag(TagsContainer tagsContainers, int[] location, int position);
    }

    private OnHandlerTagsListener onHandlerTagsListener;

    public void setOnHandlerTagsListener(OnHandlerTagsListener onHandlerTagsListener) {
        this.onHandlerTagsListener = onHandlerTagsListener;
    }

    public void setData(List<FloridData.TitleMedia> titleMediaList) {
        this.titleMediaList = titleMediaList;
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return null == titleMediaList ? 0 : titleMediaList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
