package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.HomeResult;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.zhy.magicviewpager.transformer.AlphaPageTransformer;
import com.zhy.magicviewpager.transformer.ScaleInTransformer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/10/13.
 */
public class CoverFlowView extends LinearLayout {

    @BindView(R.id.title)
    HomeTitleBar titleBar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    private PagerAdapter adapter;

    private HomeTitleBar.TitleClickListenner listenner;

    public CoverFlowView(@NonNull Context context) {
        this(context, null);
    }

    public CoverFlowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeTitleBar.TitleClickListenner getListenner() {
        return listenner;
    }

    public void setListenner(HomeTitleBar.TitleClickListenner listenner) {
        this.listenner = listenner;
    }

    public interface PageClickListener {
        public void pageOnclick(Object o);

    }

    float mDownX;
    float mDownY;
    boolean updown;
    int mTouchSlop;

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean returns = super.onInterceptTouchEvent(ev);
//        float currentX = ev.getX();
//        float currentY = ev.getY();
//        int shiftX = (int) Math.abs(currentX - mDownX);
//        int shiftY = (int) Math.abs(currentY - mDownY);
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mDownX = currentX;
//                mDownY = currentY;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (shiftX > mTouchSlop && shiftX > shiftY) {
//                    updown = false;
//                } else if (shiftY > mTouchSlop && shiftY > shiftX) {
//                    updown = true;
//                }
//                if (updown) {
//                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                if (shiftX > mTouchSlop && shiftX > shiftY) {
//                    updown = false;
//                } else if (shiftY > mTouchSlop && shiftY > shiftX) {
//                    updown = true;
//                }
//                if (updown) {
//                    return true;
//                }
//                break;
//            default:
//                break;
//        }
//
//        return returns;
//
//    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.main_page_cover, this, true);
        ButterKnife.bind(this);
        FrameLayout.LayoutParams viewpagerParams = (FrameLayout.LayoutParams) viewPager.getLayoutParams();
        int margin = (int) (129.0f / 750.0f * CommonUtils.getScreenWidth());
        viewpagerParams.leftMargin = margin;
        viewpagerParams.rightMargin = margin;
        //viewPager.setPageMargin(20);//设置page间间距，自行根据需求设置
        viewPager.setOffscreenPageLimit(3);//>=3
        viewPager.setAdapter(new CoverPageAdapter());//写法不变

        viewPager.setPageTransformer(true,
                new AlphaPageTransformer(new ScaleInTransformer()));
        titleBar.setListenner(new HomeTitleBar.TitleClickListenner() {
            @Override
            public void OnTitleClick(Object o) {

                if (listenner != null) {
                    listenner.OnTitleClick(o);
                }
            }
        });


    }

    public void setPageAdapter(PagerAdapter pa) {

        viewPager.setAdapter(pa);
    }

    public void setCurentItem(int item) {
        viewPager.setCurrentItem(item);
    }

    public void setTitleimage(int resid) {
        titleBar.setTitleimage(resid);
    }

    public void setIndicator(int resid) {
        titleBar.setIndicator(resid);
    }

    public class CoverPageAdapter extends PagerAdapter {

        SimpleDraweeView simpleDraweeView;

        TextView title;

        TextView note;

        private ArrayList<HomeResult.ActionData> data = new
                ArrayList<HomeResult.ActionData>();

        private PageClickListener listener;

        public int pointer = 250;

        public void setData(ArrayList<HomeResult.ActionData> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return 500;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            int pos = 0;
            int size = (data.size() > 0 ? data.size() : 1);
            if (position - pointer < 0) {
                pos = (position - pointer) + size;
                while (pos < 0) {
                    pos = pos + size;
                }
            } else if (position - pointer >= size) {
                pos = (position - pointer) - size;
                while (pos >= size) {
                    pos = pos - size;
                }
            } else {
                pos = position - pointer;
            }
            View view = LayoutInflater.from(getContext()).inflate(R.layout.main_page_poems, null);

            simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.itemimage);
            title = (TextView) view.findViewById(R.id.title);
            note = (TextView) view.findViewById(R.id.note);

            float width = 450.0f / 750.0f * CommonUtils.getScreenWidth();
            float height = 252.0f / 750.0f * CommonUtils.getScreenWidth();

            simpleDraweeView.getLayoutParams().width = (int) width;
            simpleDraweeView.getLayoutParams().height = (int) height;

            float aspectRatio = width / height;

            if (data.size() > 0) {
                final HomeResult.ActionData actionData = data.get(pos);

                if (actionData != null) {
                    simpleDraweeView.setImageURI(actionData.image);

                    ImageLoader.resizeMiddle(simpleDraweeView, actionData.image, aspectRatio);

                    title.setText(actionData.title.trim());
                    note.setText(actionData.note);
                }

                simpleDraweeView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.pageOnclick(actionData);
                        }
                    }
                });
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public PageClickListener getListener() {
            return listener;
        }

        public void setListener(PageClickListener listener) {
            this.listener = listener;
        }
    }

}
