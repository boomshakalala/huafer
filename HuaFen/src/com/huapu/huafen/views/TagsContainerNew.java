package com.huapu.huafen.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/29.
 */

public class TagsContainerNew extends FrameLayout {

    private final static String TAG = TagsContainerNew.class.getSimpleName();
    private ArrayList<ArticleTag> tagArrayList;
    private FloridData.TitleMedia media;
    private float aspectRatio;
    private boolean editAble;
    private ViewDragHelper viewDragHelper;
    private ContainerType containerType = ContainerType.DEFAULT;
    private boolean isAnimationRunning;
    private boolean isTagsVisible = true;
    private OnHandleContainerListener onHandleContainerListener;


    public TagsContainerNew(@NonNull Context context) {
        this(context, null);
    }

    public TagsContainerNew(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }


    private void init() {
        tagArrayList = new ArrayList<>();
        initDrag();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTagsVisible) {
                    hideTagsAnimation();
                } else {
                    showTagsAnimation();
                }

            }
        });

    }

    private void initDrag() {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                LogUtil.e(TAG, "tryCaptureView");
                return editAble && (child instanceof ArticleTag);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (getPaddingLeft() > left) {
                    return getPaddingLeft();
                }

                if (getWidth() - child.getWidth() < left) {
                    return getWidth() - child.getWidth();
                }

                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (getPaddingTop() > top) {
                    return getPaddingTop();
                }

                if (getHeight() - child.getHeight() < top) {
                    return getHeight() - child.getHeight();
                }

                return top;
            }


            @Override
            public int getViewHorizontalDragRange(View child) {
                return (child instanceof ArticleTag) ? child.getWidth() : 0;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return (child instanceof ArticleTag) ? child.getHeight() : 0;
            }

            @Override
            public void onViewReleased(View releasedChild, float xVel, float yVel) {
                LogUtil.e(TAG, "onViewReleased");
                super.onViewReleased(releasedChild, xVel, yVel);
                if (releasedChild instanceof ArticleTag) {
                    ArticleTag tag = (ArticleTag) releasedChild;
                    int x, y;
                    int sX, sY;
                    if (tag.tag.orientation == 0) {
                        sX = tag.getLeft();
                        sY = tag.getTop() + tag.getMeasuredHeight() / 2;
                    } else {
                        sX = tag.getRight();
                        sY = tag.getTop() + tag.getMeasuredHeight() / 2;
                    }
                    x = CommonUtils.getPixelX(sX, media.width, getMeasuredWidth());
                    y = CommonUtils.getPixelY(aspectRatio, sY, (int) (media.width / aspectRatio), getMeasuredWidth());
                    tag.tag.x = x;
                    tag.tag.y = y;
                    LogUtil.e(TAG, String.format("(%1$d,%2$d)", tag.tag.x, tag.tag.y));
                }

            }


        });
//        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        return viewDragHelper.shouldInterceptTouchEvent(event) && gestureDetector.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        viewDragHelper.processTouchEvent(event);
//        return true;
//    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public void setData(FloridData.TitleMedia media, boolean editAble) {
        if (media == null) {
            return;
        }
        this.media = media;
        this.editAble = editAble;
        removeAllTags();
        addTags(media, editAble);
    }

    public void setData(FloridData.TitleMedia media) {
        this.setData(media, false);
    }

    private void addTags(final FloridData.TitleMedia media, final boolean editAble) {
        List<FloridData.Tag> tags = media.tags;
        if (!ArrayUtil.isEmpty(tags)) {
            for (FloridData.Tag t : tags) {
                final ArticleTag tag = new ArticleTag(getContext());
                tag.setData(t, editAble);
                tag.setOnHandleTagListener(new ArticleTag.OnSimpleHandleTagListener() {

                    @Override
                    public void onDeleteTag() {
                        if (media.tags != null && media.tags.contains(tag.tag)) {
                            final TextDialog dialog = new TextDialog(getContext(), false);
                            dialog.setContentText("您确定删除该标签吗？");
                            dialog.setLeftText("取消");
                            dialog.setLeftCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.setRightText("确定");
                            dialog.setRightCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    media.tags.remove(tag.tag);
                                    notifyDataChanged();
                                }
                            });
                            dialog.show();

                        }
                    }
                });
                tagArrayList.add(tag);
                addView(tag, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    private void notifyDataChanged() {
        removeAllTags();
        addTags(media, editAble);
    }

    private void removeAllTags() {
        removeAllViews();
        if (!ArrayUtil.isEmpty(tagArrayList)) {
            tagArrayList.clear();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int tt, int r, int b) {
        LogUtil.e(TAG, "onLayout");
        super.onLayout(changed, l, tt, r, b);
        layoutTags();
    }

    private void layoutTags() {

        if (!ArrayUtil.isEmpty(tagArrayList)) {
            for (ArticleTag tag : tagArrayList) {

                FloridData.Tag t = tag.tag;
                if (t != null) {
                    int px = CommonUtils.getScreenLocationX(media.width, t.x, getMeasuredWidth());
                    int py = CommonUtils.getScreenLocationY(aspectRatio, (int) (media.width / aspectRatio), t.y, getMeasuredWidth());
                    int left, top, right, bottom;
                    int widthTag = tag.getMeasuredWidth();
                    int heightTag = tag.getMeasuredHeight();

                    if (t.orientation == 0) {//向右
                        left = px;
                        top = py - heightTag / 2;
                        right = px + widthTag;
                        bottom = py + heightTag / 2;
                    } else {
                        left = px - widthTag;
                        top = py - heightTag / 2;
                        right = px;
                        bottom = py + heightTag / 2;
                    }

                    tag.layout(left, top, right, bottom);
                }
            }
            invalidate();
        }
    }

    public void addTag(FloridData.Tag tag, int[] location) {
        if (tag != null) {
            if (media.tags == null) {
                media.tags = new ArrayList<>();
            }
            media.tags.add(tag);
            int sX = location[0];
            int sY = location[1];
            int x = CommonUtils.getPixelX(sX, media.width, getMeasuredWidth());
            int y = CommonUtils.getPixelY(aspectRatio, sY, (int) (media.width / aspectRatio), getMeasuredWidth());
            tag.x = x;
            tag.y = y;
            notifyDataChanged();
        }
    }

    private void showTagsAnimation() {
        if (!isAnimationRunning && !ArrayUtil.isEmpty(tagArrayList)) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    isAnimationRunning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimationRunning = false;
                    isTagsVisible = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isAnimationRunning = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            List<Animator> objectAnimatorList = new ArrayList<>();
            for (ArticleTag tag : tagArrayList) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(tag, View.ALPHA, 0.0f, 1.0f);
                objectAnimatorList.add(animator);
            }
            animatorSet.playTogether(objectAnimatorList);
            animatorSet.setDuration(300);
            animatorSet.start();
        }
    }

    private void hideTagsAnimation() {
        if (!isAnimationRunning && !ArrayUtil.isEmpty(tagArrayList)) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    isAnimationRunning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimationRunning = false;
                    isTagsVisible = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isAnimationRunning = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            List<Animator> objectAnimatorList = new ArrayList<>();
            for (ArticleTag tag : tagArrayList) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(tag, View.ALPHA, 1.0f, 0.0f);
                objectAnimatorList.add(animator);
            }
            animatorSet.playTogether(objectAnimatorList);
            animatorSet.setDuration(300);
            animatorSet.start();
        }
    }

    public void setOnHandleContainerListener(OnHandleContainerListener onHandleContainerListener) {
        this.onHandleContainerListener = onHandleContainerListener;
    }

    public enum ContainerType {
        DEFAULT,
        PUBLISH,
        DETAIL,;
    }

    public interface OnHandleContainerListener {
        void onPopup();

        void onAddTag(float x, float y);
    }

    public abstract static class OnSimpleHandleContainerListener implements OnHandleContainerListener {

        @Override
        public void onPopup() {

        }

        @Override
        public void onAddTag(float x, float y) {

        }
    }

}
