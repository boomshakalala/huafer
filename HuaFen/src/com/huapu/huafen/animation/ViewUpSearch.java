package com.huapu.huafen.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.views.SearchView;

/**
 * Created by sreay on 15/8/20.
 */
public class ViewUpSearch extends LinearLayout {

    private SearchView round;
    private View circle;
    private TextView tvRecommend;
    private TextView tvNearby;
    private TextView tvFollow;
    private View commendLine,nearbyLine,followLine;
    public View layoutSearch;
    public View llContainer;

    public ViewUpSearch(Context context) {
        super(context);
        initView(context);
    }

    public ViewUpSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_choice_up_search, this, true);
        llContainer = findViewById(R.id.llContainer);
        round = (SearchView) findViewById(R.id.round);
        circle = findViewById(R.id.circle);

        tvRecommend = (TextView) findViewById(R.id.tvRecommend);
        tvNearby = (TextView) findViewById(R.id.tvNearby);
        tvFollow = (TextView) findViewById(R.id.tvFollow);

        commendLine = findViewById(R.id.commendLine);
        nearbyLine = findViewById(R.id.nearbyLine);
        followLine = findViewById(R.id.followLine);
        layoutSearch = findViewById(R.id.layoutSearch);
    }


    public void updateShow(boolean isExpand) {
        if (isExpand) {
            expandSearch();
        } else {
            closeSearch();
        }
    }
    
    private void expandSearch() {

        circle.setVisibility(View.VISIBLE);
        ObjectAnimator circleAlpha = ObjectAnimator.ofFloat(circle, "alpha", 1f, 0f);
        ObjectAnimator roundAlpha = ObjectAnimator.ofFloat(round, "alpha", 0f, 1f);
        ObjectAnimator roundSize = ObjectAnimator.ofFloat(round, "size", 0, 1f);
        round.setPivotX(round.getWidth());// 设置动画原点为控件右侧；设置0为左侧
        AnimatorSet animSet= new AnimatorSet();
        animSet.play(circleAlpha).with(roundAlpha).with(roundSize);
        animSet.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                llContainer.setVisibility(GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSet.setDuration(200);
        animSet.start();
    }

    private void closeSearch() {
        ObjectAnimator roundSize = ObjectAnimator.ofFloat(round, "size", 1f, 0);
        ObjectAnimator circleAlpha = ObjectAnimator.ofFloat(circle, "alpha", 0f, 1f);
        circle.setVisibility(View.VISIBLE);
        round.setOffsetRight(Math.round(circle.getWidth()/1.2f));
        ObjectAnimator roundAlpha = ObjectAnimator.ofFloat(round, "alpha", 1f, 0f);
        round.setPivotX(round.getWidth());// 设置动画原点为控件右侧；设置0为左侧
        round.setPivotY(round.getHeight() / 2);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(roundSize).with(circleAlpha).with(roundAlpha);
        animSet.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                llContainer.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSet.setDuration(200);
        animSet.start();
    }

}
