package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.RedPointTitleAdapter;
import com.huapu.huafen.beans.GoodsResult;
import com.huapu.huafen.beans.MyGoods;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.events.GoodsEditEvent;
import com.huapu.huafen.events.GoodsStateEvent;
import com.huapu.huafen.fragment.ReleaseListFragment;
import com.huapu.huafen.scrollablelayoutlib.PagerSlidingTabStrip;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 我发布的
 * <p>
 * Created by liang on 2016/10/26.
 */
public class ReleaseListActivity extends BaseActivity implements ReleaseListFragment.OnRefreshOverListener {
    private final String TAG = ReleaseListActivity.class.getSimpleName();
    private List<String> titleList = new ArrayList<String>();
    private List<MyGoods> types = new ArrayList<MyGoods>();
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private int index;
    private PagerSlidingTabStrip pagerStrip;
    private ViewPager pager;
    private ArrayList<MyGoods> myGoods;
    private RedPointTitleAdapter adapter;
    private String myAction = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_orders_list);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("index")) {
            index = intent.getIntExtra("index", -1);
            myAction = intent.getAction();
            LogUtil.d("danielluan", "action=" + myAction + "$" + index + "action " + intent.getAction());
        }

        myGoods = CommonPreference.getMyGoods();
        int userLevel = CommonPreference.getIntValue(CommonPreference.USER_LEVEL, 0);
        String level = String.valueOf(userLevel);
        if (!ArrayUtil.isEmpty(myGoods)) {
            for (MyGoods publish : myGoods) {
                String granted = publish.getGranted();
                String granteds[] = granted.split(",");
                if (Arrays.asList(granteds).contains("0")) {
                    titleList.add(publish.getTitle());
                    types.add(publish);
                } else if (Arrays.asList(granteds).contains(level)) {
                    titleList.add(publish.getTitle());
                    types.add(publish);
                }
            }
        }

        dealformpush(userLevel);
        if (index == -1 || index >= types.size()) {
            index = 0;
        }
        initView();
    }

    private void dealformpush(int userLevel) {


        if (ActionConstants.OPEN_MYSELLING_ITEMS.equals(myAction)) {
            if (userLevel < 2)// ordinary user
            {
                index = 1;
            } else {
                index = 0;
            }
        } else if (ActionConstants.OPEN_MYUNSHELVED_ITEMS.equals(myAction)) {

            if (userLevel < 2)// ordinary user
            {
                index = 2;
            } else {
                index = 1;
            }
        }
    }

    private void initView() {
        setTitleString("我发布的");
        pagerStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerStrip);
        pager = (ViewPager) findViewById(R.id.pager);
        if (ArrayUtil.isEmpty(titleList)) {
            return;
        }
        for (int i = 0; i < titleList.size(); i++) {
            Bundle bundle = new Bundle();
            if (index == i) {
                pager.setCurrentItem(i);
                bundle.putBoolean(MyConstants.EXTRA_ORDERS_IS_FIRST_LOAD, true);
            }
            bundle.putSerializable(MyConstants.EXTRA_TYPE, types.get(i));
            ReleaseListFragment fragment = ReleaseListFragment.newInstance(bundle);
            fragment.setOnRefreshOverListener(this);
            fragmentList.add(fragment);
        }
        adapter = new RedPointTitleAdapter(getSupportFragmentManager(), fragmentList, titleList);

        int[] points = new int[titleList.size()];
        int[] drawables = null;
        if (points.length == 3) {
            drawables = new int[]{
                    R.drawable.pending_selector,
                    R.drawable.in_the_sale_selector,
                    R.drawable.off_sale_selector};
        } else if (points.length == 2) {
            drawables = new int[]{
                    R.drawable.in_the_sale_selector,
                    R.drawable.off_sale_selector};
        }

        adapter.setRedPoints(points);
        adapter.setImageRes(drawables);

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(titleList.size());
        pagerStrip.setViewPager(pager);
        pagerStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.e("onPageSelected", "page:" + i);
                index = i;
                LogUtil.i(TAG, "切换到onPageSelected  " + i + "   " + fragmentList.size());
//				ReleaseListFragment fragment = (ReleaseListFragment) fragmentList.get(i);
//				fragment.setRefreshing();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                LogUtil.i(TAG, "切换到onPageSelected  " + i + "   " + fragmentList.size());
//				ReleaseListFragment fragment = (ReleaseListFragment) fragmentList.get(i);
//				fragment.setRefreshing();

            }
        });
        pager.setCurrentItem(index);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onTitleBarDoubleOnClick() {
        if (ArrayUtil.isEmpty(fragmentList)) {
            return;
        }
        Fragment fragment = fragmentList.get(index);
        ReleaseListFragment releaseListFragment;
        if (fragment == null) {
            return;
        }
        if (fragment instanceof ReleaseListFragment) {
            releaseListFragment = (ReleaseListFragment) fragment;
            if (releaseListFragment.ptrRecycler == null) {
                return;
            }
            releaseListFragment.ptrRecycler.getRefreshableView().smoothScrollToPosition(0);
        }
    }

    @Override
    public void onComplete(GoodsResult.GoodsStatusCounts goodsStatusCounts) {
        if (goodsStatusCounts == null) {
            return;
        }
        if (adapter != null) {
            if (ArrayUtil.isEmpty(titleList)) {
                return;
            }

            int[] points = new int[titleList.size()];
            if (points.length == 3) {
                points[0] = goodsStatusCounts.notAudit;
                points[1] = goodsStatusCounts.selling;
                points[2] = goodsStatusCounts.offShelf;
            } else if (points.length == 2) {
                points[0] = goodsStatusCounts.selling;
                points[1] = goodsStatusCounts.offShelf;
            }

            adapter.setRedPoints(points);
            if (pagerStrip != null) {
                pagerStrip.notifyDataSetChanged();
            }
        }
    }

    private static final int REQUEST_CODE_RELEASE_CODE = 0x212;
    private final static int REQUEST_CODE_RELEASE_LIST = 0x1111;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RELEASE_CODE || requestCode == REQUEST_CODE_RELEASE_LIST) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof ReleaseListFragment) {
                    ReleaseListFragment frag = (ReleaseListFragment) fragment;
                    frag.setRefreshing();
                }
            }
        }
    }

    public void onEventMainThread(final Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof GoodsEditEvent) {
            GoodsEditEvent event = (GoodsEditEvent) obj;
            if (event.isSave) {
                for (Fragment fragment : fragmentList) {
                    if (fragment instanceof ReleaseListFragment) {
                        ReleaseListFragment frag = (ReleaseListFragment) fragment;
                        frag.setRefreshing();
                    }
                }
            }
        }
        if (obj instanceof GoodsStateEvent) {
            GoodsStateEvent event = (GoodsStateEvent) obj;
            if (event.isSave) {
                for (Fragment fragment : fragmentList) {
                    if (fragment instanceof ReleaseListFragment) {
                        ReleaseListFragment frag = (ReleaseListFragment) fragment;
                        frag.setRefreshing();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}