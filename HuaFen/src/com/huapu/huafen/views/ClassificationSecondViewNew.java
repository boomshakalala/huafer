package com.huapu.huafen.views;

/**
 * Created by danielluan on 2017/10/10.
 */

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ClassificationSecondAdapter;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.utils.CommonUtils;

import java.util.List;

/**
 * Created by lalo on 2016/11/14.
 */
public class ClassificationSecondViewNew extends FrameLayout {

    private RecyclerView firstclass;
    private RecyclerView sencondclass;
    private ClassificationSecondAdapter firstclassAdapter;
    private ClassificationSecondAdapter sencondclassAdapter;
    private int screenWidth;
    private boolean animatingCity;
    private int level;
    private String items = "";
    private boolean expand;

    private Cat cat1;


    public ClassificationSecondViewNew(Context context) {
        this(context, null);
    }

    public ClassificationSecondViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenWidth = CommonUtils.getScreenWidth();
        init();
    }


    private void init() {

        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_filter_second_classsification_new, this, true);

        LinearLayoutManager recyclerViewProvinceManager = new LinearLayoutManager(getContext());
        recyclerViewProvinceManager.setOrientation(LinearLayoutManager.VERTICAL);
        firstclass = (RecyclerView) findViewById(R.id.first_classs);
        firstclass.setLayoutManager(recyclerViewProvinceManager);

        LinearLayoutManager recyclerViewCityManager = new LinearLayoutManager(getContext());
        recyclerViewCityManager.setOrientation(LinearLayoutManager.VERTICAL);
        sencondclass = (RecyclerView) findViewById(R.id.second_class);
        sencondclass.setLayoutManager(recyclerViewCityManager);
        firstclassAdapter = new ClassificationSecondAdapter(getContext());
        sencondclassAdapter = new ClassificationSecondAdapter(getContext());
        sencondclassAdapter.setSimpleUI(true);
        firstclass.setAdapter(firstclassAdapter);
        sencondclass.setAdapter(sencondclassAdapter);

        firstclassAdapter.setOnItemClickListener(new ClassificationSecondAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cat cat) {
                if (level == 1) {
                    cat1 = cat;
                    if (cat.getCid() == 0) {
                        if (listener != null) {
                            listener.onItemClick(cat1, null);
                            sencondclass.setVisibility(GONE);
                        }
                    }
                    List<Cat> cats = CommonUtils.getSecondCats(cat.getCid());
                    if (cats != null) {
                        sencondclassAdapter.setData(cats);
                        startSecondAnimation();
                    }
                } else if (level == 2) {
                    if (listener != null) {
                        listener.onItemClick(cat);
                    }
                }
            }
        });

        sencondclassAdapter.setOnItemClickListener(new ClassificationSecondAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cat cat) {
                if (listener != null) {
                    listener.onItemClick(cat1, cat);
                }
            }
        });
        LayoutParams cityParams = (LayoutParams) sencondclass.getLayoutParams();
        cityParams.width = screenWidth * 2 / 3;
        sencondclass.setLayoutParams(cityParams);
    }


    public void Config(int level, String items, boolean expand) {
        this.level = level;
        this.items = items;
        this.expand = expand;
        if (this.level == 1) {
            if (TextUtils.isEmpty(this.items)) {
                if (!this.expand) {
                    List<Cat> cats = CommonUtils.getCats();
                    firstclassAdapter.setData(cats);
                }
            } else {
                if (!this.expand) {
                    List<Cat> cats = CommonUtils.getCats(items);
                    firstclassAdapter.setData(cats);
                }
            }
        } else if (this.level == 2) {

            if (TextUtils.isEmpty(this.items)) {
                if (!this.expand) {
                    List<Cat> cats = CommonUtils.getCats();
                    firstclassAdapter.setData(cats);
                }
            } else {
                if (!this.expand) {
                    List<Cat> cats = CommonUtils.getSecondCats(items);
                    firstclassAdapter.setData(cats);
                }
            }


        }

    }


    private void startSecondAnimation() {
        if (animatingCity) {
            return;
        }
        sencondclass.setVisibility(GONE);
        sencondclass.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(sencondclass, "translationX", screenWidth, 0);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                animatingCity = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatingCity = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(300);
        animator.start();
    }


    public void setfirstData(List<Cat> data) {
        firstclassAdapter.setData(data);
    }


    public void setsecondData(List<Cat> data) {
        sencondclassAdapter.setData(data);
    }


    public interface OnItemClickListener {
        void onItemClick(Cat cat);

        void onItemClick(Cat cat1, Cat cat2);
    }

    private OnItemClickListener listener;


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}