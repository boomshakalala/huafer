package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVAnalytics;
import com.huapu.huafen.ActivityManager;
import com.huapu.huafen.R;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.OnKeyboardVisibilityTool;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.TitleBarNew;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import cn.leancloud.chatkit.LCChatKit;

/**
 * @author liang_xs
 * @ClassName: BaseActivity
 * @Description: Activity基类
 * @date 2016-03-27
 */
public abstract class BaseActivity extends FragmentActivity implements
        TitleBarNew.TitleBarDoubleOnClick,
        View.OnClickListener {

    protected final static String LOADING = "loading";
    protected final static String REFRESH = "refresh";
    protected final static String LOAD_MORE = "load_more";
    protected final static String ACTIVITY_NAME_EXTRA = "_intent_activity_extra";
    private static final String TAG = "BaseActivity";
    public String fromActivity;
    private TitleBarNew mTitleBar;
    protected ViewGroup mRoot;
    protected Intent mIntent;
    protected Activity context;

    @Override
    public void onTitleBarDoubleOnClick() {

    }

    protected void setTitleString(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleBar.setTitle(title);
        }
    }

    public TitleBarNew getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.add(this);
        context = this;
        // 友盟统计
        MobclickAgent.setDebugMode(false);
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setSessionContinueMillis(50000);// 提交间隔时间
        MobclickAgent.setCatchUncaughtExceptions(true);

        //LeanCloud统计
        AVAnalytics.onPause(this);

        mIntent = getIntent();
        if (mIntent != null) {
            fromActivity = getIntent().getStringExtra(ACTIVITY_NAME_EXTRA);
            // 处理来自 IM 的 intent
            Bundle extras = getIntent().getExtras();
            LogUtil.i(TAG, String.format("intent.extras %s", extras));
            if (extras != null && extras.containsKey(MyConstants.IM_MARK_AS_READ)) {
                LCChatKit.getInstance().getClient()
                        .getConversation(extras.getString(MyConstants.IM_MARK_AS_READ)).read();
            }
        }
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, true);
    }

    public void onKeyBoardStateChanged(View root, boolean isShow, int heightDiff) {
        LogUtil.e("onKeyBoardStateChanged", isShow + "," + heightDiff);
    }

    @Override
    public void setContentView(int layoutResID) {
        final View content = getLayoutInflater().inflate(layoutResID, null);
        setContentView(content);
    }

    public void setContentView(int layoutResID, boolean autoInject) {
        final View content = getLayoutInflater().inflate(layoutResID, null);
        setContentView(content, autoInject);
    }

    public void setContentView(View view, boolean autoInject) {
        mRoot = genRootView();
        mTitleBar = new TitleBarNew(this);
        mRoot.addView(mTitleBar, -1, -2);
        mRoot.addView(view, -1, -1);
        super.setContentView(mRoot);
        mTitleBar.setVisibility(View.GONE);
        mTitleBar.setTitleOnClick(this);
        initTitleBar();
        // 键盘弹出监听 - 老版本
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                final int heightDiff = mRoot.getRootView().getHeight() - mRoot.getHeight();

//                if (heightDiff > 100) {//弹出
//                    onKeyBoardStateChanged(mRoot, true, heightDiff);
//                } else {
//                    onKeyBoardStateChanged(mRoot, false, heightDiff);
//                }
            }
        });

        // 键盘弹出监听 - 在用
        OnKeyboardVisibilityTool.setKeyboardListener(this,
                new OnKeyboardVisibilityTool.OnKeyboardVisibilityListener() {
                    public void onVisibilityChanged(boolean visible) {
                        final int heightDiff = mRoot.getRootView().getHeight() - mRoot.getHeight();
                        onKeyBoardStateChanged(mRoot, visible, heightDiff);
                    }
                });

        if (autoInject) {
            ButterKnife.bind(this);
        }
    }

    public ViewGroup genRootView() {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    public void initTitleBar() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.remove(this);
    }

    protected void toast(String msg) {
        ToastUtil.toast(this, msg);
    }

    protected void toasttop(String msg) {
        ToastUtil.toasttop(this, msg);
    }

    protected void toast(int resId) {
        ToastUtil.toast(this, resId);
    }

    @Override
    public void startActivity(Intent intent) {
        intent.putExtra(ACTIVITY_NAME_EXTRA, this.getClass().getSimpleName());
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(ACTIVITY_NAME_EXTRA, this.getClass().getSimpleName());
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    protected boolean isNeedsFinishAnimation = true;

    @Override
    public void finish() {
        super.finish();
        if (isNeedsFinishAnimation) {
            overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        MobclickAgent.onResume(this);

        AVAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }

    protected boolean isNetAvailable() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return false;
        }
        return true;
    }
}
