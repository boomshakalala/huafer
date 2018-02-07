package com.huapu.huafen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.huapu.huafen.R;
import com.huapu.huafen.common.ExtraConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.ArticleListFragment;
import com.huapu.huafen.fragment.GoodsListFragment;
import com.huapu.huafen.fragment.UserListFragment;
import com.huapu.huafen.scrollablelayoutlib.PagerSlidingTabStrip;
import com.huapu.huafen.utils.CommonPreference;

import java.util.List;

import butterknife.BindView;

/**
 * 关注/我关注的
 *
 * @author dengbin
 */
public class MyFollowedActivity extends BaseActivity {

    @BindView(R.id.my_followed_tablayout)
    PagerSlidingTabStrip mTablayout;
    @BindView(R.id.my_followed_viewpager)
    ViewPager mViewPager;

    private long userId;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_followed);

        if (getIntent().hasExtra(MyConstants.EXTRA_USER_ID)) {
            userId = getIntent().getLongExtra(MyConstants.EXTRA_USER_ID, 0);
        }

        long myUserId = CommonPreference.getUserId();

        if (userId == 0) {
            userId = myUserId;
        }

        index = getIntent().getIntExtra(ExtraConstants.EXTRA_FOLLOW_INDEX, 0);

        initView(userId == myUserId);
    }

    @Override
    public void onTitleBarDoubleOnClick() {

    }

    private Fragment[] getFragments() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);

        GoodsListFragment followGoodsFragment = GoodsListFragment.newInstance(bundle);
        ArticleListFragment articleListFragment = ArticleListFragment.newInstance(bundle);

        final Fragment fragments[] = new Fragment[]{UserListFragment.getInstance(0, userId),
                followGoodsFragment, articleListFragment};

        return fragments;
    }

    /**
     * 获取单品类别列表
     */
    private void initView(boolean isMe) {
        setTitleString("关注");

        if (isMe) {
            mViewPager.setVisibility(View.VISIBLE);
            mTablayout.setVisibility(View.VISIBLE);

            final Fragment[] fragments = getFragments();

            final String titles[] = new String[]{"用户", "商品", "花语"};

            mViewPager.setOffscreenPageLimit(3);

            //给ViewPager设置适配器
            mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return fragments[position];
                }

                @Override
                public int getCount() {
                    return fragments.length;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    return titles[position];
                }
            });
            //将TabLayout和ViewPager关联起来。
            mTablayout.setViewPager(mViewPager);

            if (index < 0 || index >= fragments.length)
                index = 0;

        } else {
            mViewPager.setVisibility(View.GONE);
            mTablayout.setVisibility(View.GONE);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            UserListFragment fragment = UserListFragment.getInstance(0, userId);
            ft.add(R.id.fragment_container, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (index > 0)
            mViewPager.setCurrentItem(index, true);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL) {
            if (data != null) {
                List<Fragment> list = getSupportFragmentManager().getFragments();
                for (Fragment f : list) {
                    f.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    // 跳转intent
    public static Intent createIntent(Context activity, long userId) {
        Intent intent = new Intent(activity, MyFollowedActivity.class);
        intent.putExtra(MyConstants.EXTRA_USER_ID, userId);
        return intent;
    }
}
