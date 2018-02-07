package com.huapu.huafen.fragment.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.avos.avoscloud.AVAnalytics;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * @author liang_xs
 * @ClassName: BaseFragment
 * @Description: Fragment基类
 * @date 2016-03-27
 */
public abstract class BaseFragment extends Fragment {

    public final static String LOADING = "loading";
    public final static String REFRESH = "refresh";
    public final static String LOAD_MORE = "load_more";
    public final static String ACTIVITY_NAME_EXTRA = "_intent_activity_extra";
    protected Button btnTitleLeft, btnTitleRight;
    protected RelativeLayout layout;
    protected boolean userVisibleHint;
    protected boolean isInitView;
    private boolean isOnActivityCreated;
    private boolean isLoaded; // 数据第一次加载后设置为true

    @Override
    public void startActivity(Intent intent) {
        intent.putExtra(ACTIVITY_NAME_EXTRA, this.getClass().getSimpleName());
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(ACTIVITY_NAME_EXTRA, this.getClass().getSimpleName());
        super.startActivityForResult(intent, requestCode);
    }

    public void onFragmentSelected() {
    }

    public void toast(String msg) {
        ToastUtil.toast(getActivity(), msg);
    }

    protected void toast(int resId) {
        ToastUtil.toast(getActivity(), resId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = createView(inflater, container);
        ButterKnife.bind(this, root);
        onViewCreated(root);
        return root;
    }

    protected void initViews(View view) {
        onViewCreated(view);
    }

    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return null;
    }

    public void onViewCreated(View root) {
        ButterKnife.bind(this, root);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isOnActivityCreated = true;
        setUserVisibleHint(getUserVisibleHint());
    }

    @Override
    public void setUserVisibleHint(boolean userVisibleHint) {
        super.setUserVisibleHint(userVisibleHint);
        if (isOnActivityCreated && userVisibleHint && !isLoaded()) {
            loadResponse();
        }
    }

    /**
     * 初始化数据，当视图初始化完毕并且fragment可见时调用
     */
    protected void loadResponse() {
    }

    /**
     * 标注当前页面
     **//*
                mScrollLayout.getHelper().setCurrentScrollableContainer(fragmentList.get(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setCurrentItem(0);
    }*/
    public BaseActivity getContext() {
        if (!(getActivity() instanceof BaseActivity)) {
            throw new RuntimeException("the fragment' getActivity() can not cast to BaseActivity");
        }
        return (BaseActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        MobclickAgent.onResume(getActivity());
        AVAnalytics.onFragmentStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MobclickAgent.onPause(getActivity());
        AVAnalytics.onFragmentStart(getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setLoaded(false);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    protected boolean isNetAvailable() {
        if (!CommonUtils.isNetAvaliable(getActivity())) {
            toast("请检查网络连接");
            return false;
        }
        return true;
    }

    public interface OnCreateViewListener {
        void onCreateView();
    }

}
