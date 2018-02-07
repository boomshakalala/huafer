package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.callbacks.AppBarStateChangeListener;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.fragment.SquareFragment;
import com.huapu.huafen.fragment.SquareManagerFragment;
import com.huapu.huafen.scrollablelayoutlib.PagerSlidingTabStrip;
import com.huapu.huafen.views.TitleBarNew;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;

/**
 * 花语广场页面
 */
public class ArticleSquareActivity extends BaseActivity {

    AppBarLayout appBar;
    private PagerSlidingTabStrip tabLayout;
    private ViewPager viewPager;
    private TitleBarNew titleBar;
    private PopupWindow morePopupWindow;
    private TextView tvMsgUnRead;
    private boolean isExpanded = true;

    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_square);
        initView();
        initData();
    }


    public void initFragmentPager() {
        final ArrayList<SquareFragment> fragmentList = new ArrayList<SquareFragment>();
        List<String> titleList = new ArrayList<String>();
        titleList.add("推荐");
        titleList.add("好物");
        titleList.add("穿搭");
        titleList.add("烹饪");
        titleList.add("旅行");
        titleList.add("护理");
        titleList.add("生活");

        for (int i = 0; i < 7; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("cat", i);
            if (position == i) {
                viewPager.setCurrentItem(i);
                bundle.putBoolean(MyConstants.EXTRA_ORDERS_IS_FIRST_LOAD_ARTICLE, true);
            }
            SquareFragment squareFragment = new SquareFragment();
            squareFragment.setArguments(bundle);
            fragmentList.add(squareFragment);
        }


        viewPager.setAdapter(new SquareManagerFragment(getSupportFragmentManager(), fragmentList, titleList));
        viewPager.setOffscreenPageLimit(titleList.size());

        tabLayout.setViewPager(viewPager);

        if (position != -1) {
            viewPager.setCurrentItem(position);
        } else {
            viewPager.setCurrentItem(0);
        }

    }

    private void initData() {
        if (getIntent().hasExtra("position")) {
            position = getIntent().getIntExtra("position", 0);
            if (position >= 7) {
                position = 0;
            }
        }
        initFragmentPager();
    }

    private void initView() {
        tabLayout = (PagerSlidingTabStrip) findViewById(R.id.article_square_tablayout);
        viewPager = (ViewPager) findViewById(R.id.article_square_viewpager);
        titleBar = (TitleBarNew) findViewById(R.id.titleBar);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        initTitle();
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                isExpanded = state == State.EXPANDED ? true : false;
            }
        });


    }


    private void initTitle() {
        View searchTitle = LayoutInflater.from(this).inflate(R.layout.search_article, null);
        LinearLayout llTextSearch = (LinearLayout) searchTitle.findViewById(R.id.llTextSearch);
        llTextSearch.setOnClickListener(this);
        titleBar.setTitle(searchTitle);
        titleBar.setOnRightButtonClickListener(R.drawable.gray_point, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initPopMore(v);
            }
        });

    }

    private void initPopMore(View v) {
        if (morePopupWindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_more, null);
            View layoutSwitchHome = view.findViewById(R.id.layoutSwitchHome);
            View layoutSwitchMsg = view.findViewById(R.id.layoutSwitchMsg);
            View layoutSwitchMine = view.findViewById(R.id.layoutSwitchMine);
            View layoutSwitchReport = view.findViewById(R.id.layoutSwitchReport);
            layoutSwitchReport.setVisibility(View.GONE);
            tvMsgUnRead = (TextView) view.findViewById(R.id.tvMsgUnRead);
            layoutSwitchHome.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(1);
                    morePopupWindow.dismiss();
                }
            });
            layoutSwitchMsg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(3);
                    morePopupWindow.dismiss();
                }
            });
            layoutSwitchMine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(4);
                    morePopupWindow.dismiss();
                }
            });

            morePopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            morePopupWindow.setFocusable(true);
            morePopupWindow.setOutsideTouchable(true);
            morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            morePopupWindow.setAnimationStyle(R.style.pop_search_switch);
        }
        tvMsgUnRead.setVisibility(titleBar.getMoreBtnBadgeVisibility() ? View.VISIBLE : View.GONE);
        morePopupWindow.showAsDropDown(v);
    }

    private void actionToMineFragment(int selectFragment) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, selectFragment);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llTextSearch:
                Intent intent = new Intent(ArticleSquareActivity.this, SearchFlowerArticleActivity.class);
                intent.putExtra("ary", "arity");
                startActivity(intent);
                break;
        }
    }

    public void onEvent(LCIMOfflineMessageCountChangeEvent updateEvent) {
        updateUnreadBadge();
    }

    public void onEvent(LCIMIMTypeMessageEvent event) {
        updateUnreadBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadBadge();
    }

    private void updateUnreadBadge() {
        titleBar.showMoreBtnBadge(IMUtils.hasUnread());
    }

}
