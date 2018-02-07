package com.huapu.huafen.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ClassFilterAdapter;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.layoutmanager.LinearLayoutManagerPlus;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by lalo on 2016/11/16.
 */
public class ClassFilterView extends FrameLayout {

    private int screenWidth;
    private RecyclerView recycler1;
    private RecyclerView recycler2;
    private ClassFilterAdapter adapter1;
    private ClassFilterAdapter adapter2;
    private int index1 = -1;
    private int index2 = -1;
    private int bigCaseId;
    private boolean isAnimating;
    private OnDismissListener listener;
    private OnItemDataSelect mOnItemDataSelect;

    public ClassFilterView(Context context) {
        this(context, null);
    }

    public ClassFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenWidth = CommonUtils.getScreenWidth();
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.class_filter_layout, this, true);

        LinearLayoutManagerPlus recyclerManager1 = new LinearLayoutManagerPlus(getContext());
        recyclerManager1.setOrientation(LinearLayoutManager.VERTICAL);
        recycler1 = (RecyclerView) findViewById(R.id.recycler1);
        recycler1.setLayoutManager(recyclerManager1);

        LinearLayoutManager recyclerManager2 = new LinearLayoutManager(getContext());
        recyclerManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recycler2 = (RecyclerView) findViewById(R.id.recycler2);
        recycler2.setLayoutManager(recyclerManager2);

        adapter1 = new ClassFilterAdapter(getContext());
        recycler1.setAdapter(adapter1);

        adapter2 = new ClassFilterAdapter(getContext());
        recycler2.setAdapter(adapter2);

        FrameLayout.LayoutParams classParam = (FrameLayout.LayoutParams) recycler2.getLayoutParams();
        classParam.width = screenWidth / 2;
        recycler2.setLayoutParams(classParam);

        int[] colors = new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF")};
        adapter2.setItemColors(colors);
        adapter2.isNeedSetItemBackground = false;
        adapter2.isNeedsUnderLine = false;

        adapter1.setOnItemClickListener(new ClassFilterAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Cat filterData, int position) {
                adapter1.setCheckItemByPosition(position);
                boolean notNeedsShowAnimation;
                int cid = filterData.getCid();
                if (cid == 0) {
                    notNeedsShowAnimation = true;
                } else {
                    notNeedsShowAnimation = false;
                }
                if (!notNeedsShowAnimation) {
                    adapter1.isNeedSetItemBackground = true;
                    adapter1.showTips = true;
                    ArrayList<Cat> list = filterData.getCats();
                    bigCaseId = filterData.getCid();
                    adapter2.setData(list);
                    if (recycler2.getVisibility() == View.GONE) {
                        startAnimation();
                    }

                } else {
                    index1 = position;
                    index2 = -1;

                    adapter1.isNeedSetItemBackground = false;
                    int[] catIds = new int[2];

                    if (mOnItemDataSelect != null) {
                        mOnItemDataSelect.onDataSelected(catIds, filterData.getName());
                    }

                    if (listener != null) {
                        listener.close();
                    }
                }

            }
        });


        adapter2.setOnItemClickListener(new ClassFilterAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Cat filterData, int position) {
                adapter2.setCheckItemByPosition(position);

                index1 = adapter1.currentIndex;
                index2 = position;

                adapter1.isNeedSetItemBackground = true;
                adapter1.showTips = true;
                adapter2.isNeedSetItemBackground = false;

                int[] catIds = new int[2];
                catIds[0] = bigCaseId;
                catIds[1] = filterData.getCid();
                if (mOnItemDataSelect != null) {
                    mOnItemDataSelect.onDataSelected(catIds, filterData.getName());
                }
                if (listener != null) {
                    listener.close();
                }

            }
        });
    }

    private void startAnimation() {
        if (isAnimating) {
            return;
        }
        recycler2.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(recycler2, "translationX", screenWidth, 0);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
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

    public void resetState() {
        if (index2 == -1) {
            if (index1 > 0) {
                recycler2.setVisibility(View.VISIBLE);
                adapter1.isNeedSetItemBackground = true;
            } else {
                recycler2.setVisibility(View.GONE);
                adapter1.isNeedSetItemBackground = false;
            }

        } else {
            recycler2.setVisibility(View.VISIBLE);
        }
        adapter1.setCheckItemByPosition(index1);
        Cat cat = adapter1.getItem(index1);
        if (cat != null) {
            bigCaseId = cat.getCid();
            adapter2.setData(cat.getCats());
        }
        adapter2.setCheckItemByPosition(index2);
    }

    public void setDefaultCheck(int bigCaseId, int smallCaseId) {
        long bigId = bigCaseId;
        long smallId = smallCaseId;
        int count = adapter1.getItemCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                long id = adapter1.getItemId(i);
                if (id == bigId) {
                    index1 = i;
                    break;
                }
            }
        }

        Cat smallCase = adapter1.getItem(index1);
        if (smallCase != null && !ArrayUtil.isEmpty(smallCase.getCats())) {
            ArrayList<Cat> cats = smallCase.getCats();
            for (int i = 0; i < cats.size(); i++) {
                Cat cat = cats.get(i);
                if (cat.getCid() == smallId) {
                    index2 = i;
                    break;
                }
            }
        }
        adapter1.currentIndex = index1;
    }

    public void setData(ArrayList<Cat> data) {
        this.adapter1.setData(data);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

    public void setOnItemDataSelect(OnItemDataSelect onItemDataSelect) {
        this.mOnItemDataSelect = onItemDataSelect;
    }

    public interface OnDismissListener {
        void close();
    }

    public interface OnItemDataSelect {
        void onDataSelected(int[] catId, String text);
    }

}
