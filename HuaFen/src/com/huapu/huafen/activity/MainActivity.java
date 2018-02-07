package com.huapu.huafen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.crashlytics.android.Crashlytics;
import com.huapu.huafen.ActivityManager;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.home.EverydayFreshActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.KvsdData;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.events.LoginEvent;
import com.huapu.huafen.events.MessagePrivateCountEvent;
import com.huapu.huafen.events.PushEvent;
import com.huapu.huafen.fragment.ClassificationFragment;
import com.huapu.huafen.fragment.IndexFragment;
import com.huapu.huafen.fragment.MessageFragment;
import com.huapu.huafen.fragment.MineFragment;
import com.huapu.huafen.fragment.NewIndexFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.RequestManagerSys;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.Config;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.CrashHandler;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.MessageController;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.OnClick;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;

/**
 * @author liang_xs
 * @Description: 主activity
 * @date 2016-03-27
 */
public class MainActivity extends BaseActivity implements
        OnClickListener, MineFragment.OnCreateViewListener {

    private String TAG = "MainActivity";
    @SuppressLint("StaticFieldLeak")
    public static MainActivity mActivity;

    public View tabHome;
    private View tabClass, tabMessage, tabMine, tabRelease, currSelectedTab;
    private TextView tvTabHome, tvTabClass, tvTabMessage, tvTabMine;

    public NewIndexFragment indexFragment = new NewIndexFragment();
    private ClassificationFragment classFragment = new ClassificationFragment();
    private MessageFragment messageFragment = new MessageFragment();
    private MineFragment mineFragment = new MineFragment();

    private Fragment currFragment;

    private long exitTime = 0;
    private String imgPath = "";
    private TextView tvMsgUnRead;
    private int appLevel;
    private String appDownloadUrl;
    private String appContent;
    private long orderId;
    private String extraMap;
    private boolean isMineCreateView;
    private int selectFragment;
    private long firstClick;
    private int count;
    private long lastClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        EventBus.getDefault().register(this);
//        CrashHandler handler = CrashHandler.getInstance();
//
//        handler.init(getApplicationContext(), this);
        if (!Config.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        CommonPreference.setBooleanValue(CommonPreference.IS_FIRST, false);
        CommonPreference.setBooleanValue(CommonPreference.IS_UPDATE, false);
        ActivityManager.add(this);
        initView();

        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL_ID)) {
            orderId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, 0);
            if (orderId != 0) {
                Intent intent = new Intent(MainActivity.this, OrderDetailActivity.class);
                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderId);
                startActivity(intent);
            }
        }
        if (getIntent().hasExtra(MyConstants.SHARE_TARGET_TYPE)) {
            Intent intent = new Intent();
            String targetType = getIntent().getStringExtra(MyConstants.SHARE_TARGET_TYPE);
            String targetId = getIntent().getStringExtra(MyConstants.SHARE_TARGET_ID);
            Uri uri = Uri.parse(targetType);
            if (uri != null) {
                String type = uri.getPathSegments().get(0);
                try {
                    targetId = uri.getPathSegments().get(1);
                } catch (Exception e) {
                    LogUtil.d("danielluan", e.toString());
                }
                if (type != null && type.equals(MyConstants.APP_LINK_USER)) { // 店铺
                    intent = new Intent(this, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, Long.valueOf(targetId));
                } else if (type != null && type.equals(MyConstants.APP_LINK_GOODS)) { // 商品
                    intent = new Intent(this, GoodsDetailsActivity.class);
                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, targetId);
                } else if (type != null && type.equals(MyConstants.APP_LINK_MOMENTS)) { // 简易花语
                    intent = new Intent(this, MomentDetailActivity.class);
                    intent.putExtra(MomentDetailActivity.MOMENT_ID, targetId);
                } else if (type != null && type.equals(MyConstants.APP_LINK_ARTICLES)) { // 图文花语
                    intent = new Intent(this, ArticleDetailActivity.class);
                    intent.putExtra(MyConstants.ARTICLE_ID, targetId);
                } else if (type != null && type.equals(MyConstants.APP_LINK_FPOEMS)) { // 我的花语
                    intent = new Intent(this, FlowerNewActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, targetId);
                } else {
                    intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, "https://i.huafer.cc" + uri.getPath());
                }
                startActivity(intent);
            }
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_APP_LEVEL)) {
            appLevel = getIntent().getIntExtra(MyConstants.EXTRA_APP_LEVEL, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_APP_URL)) {
            appDownloadUrl = getIntent().getStringExtra(MyConstants.EXTRA_APP_URL);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_APP_CONTENT)) {
            appContent = getIntent().getStringExtra(MyConstants.EXTRA_APP_CONTENT);
        }
        if (appLevel > 1 && !TextUtils.isEmpty(appDownloadUrl) && !TextUtils.isEmpty(appContent)) {
            Intent intent = new Intent(this, DownloadActivity.class);
            intent.putExtra(MyConstants.EXTRA_APP_LEVEL, appLevel);
            intent.putExtra(MyConstants.EXTRA_APP_URL, appDownloadUrl);
            intent.putExtra(MyConstants.EXTRA_APP_CONTENT, appContent);
            startActivity(intent);
        }
        Intent it = getIntent();
        if (it.hasExtra(MyConstants.EXTRA_MAP)) {
            extraMap = it.getStringExtra(MyConstants.EXTRA_MAP);
            PushEvent pe = new PushEvent();
            pe.isPush = true;
            pe.extraMap = extraMap;
            EventBus.getDefault().post(pe);
        }

        parsePush(getIntent());

        mineFragment.setOnCreateViewListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(MyConstants.EXTRA_SELECT_WHICH)) {
            selectFragment = intent.getIntExtra(MyConstants.EXTRA_SELECT_WHICH, 1);
            switch (selectFragment) {
                case 0:
                    selectFragment(indexFragment);
                    selectView(tabHome);
                    break;
                case 1:
                    selectFragment(indexFragment);
                    selectView(tabHome);
                    break;
                case 2:
                    selectFragment(classFragment);
                    selectView(tabClass);
                    break;
                case 3:
                    if (!CommonPreference.isLogin()) {
                        ActionUtil.loginAndToast(MainActivity.this);
                        return;
                    }
                    selectFragment(messageFragment);
                    selectView(tabMessage);
                    break;
                case 4:
                    selectFragment(mineFragment);
                    selectView(tabMine);
                    break;
            }
        }

        parsePush(intent);
    }

    // 解析push数据
    private void parsePush(Intent intent) {
        extraMap = intent.getStringExtra(MyConstants.EXTRA_LEAN_CLOUD_DATA);
        if (TextUtils.isEmpty(extraMap))
            return;

        PushEvent pe = new PushEvent();
        pe.isPush = true;
        pe.extraMap = extraMap;
        EventBus.getDefault().post(pe);

        LogUtil.e("1", extraMap);
    }

    public void updateUnread(int count) {
        if (tvMsgUnRead != null) {
            if (count > 0) {
                tvMsgUnRead.setVisibility(View.VISIBLE);
            } else {
                tvMsgUnRead.setVisibility(View.GONE);
            }
        }
    }

    /**
     * @return void
     * @Title: initView
     * @Description: 初始化控件
     * @author liang_xs
     */
    private void initView() {
        tabHome = findViewById(R.id.tabHome);
        tabClass = findViewById(R.id.tabClass);
        tabMessage = findViewById(R.id.tabMessage);
        tabMine = findViewById(R.id.tabMine);
        tabRelease = findViewById(R.id.tabRelease);
        tvTabHome = (TextView) findViewById(R.id.tvTabHome);
        tvTabClass = (TextView) findViewById(R.id.tvTabCategory);
        tvTabMessage = (TextView) findViewById(R.id.tvTabMessage);
        tvTabMine = (TextView) findViewById(R.id.tvTabMine);
        tvMsgUnRead = (TextView) findViewById(R.id.tvMsgUnRead);

        tabHome.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击~

                        if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
                            count = 0;
                        }
                        count++;
                        if (count == 1) {
                            firstClick = System.currentTimeMillis();
                        } else if (count == 2) {
                            lastClick = System.currentTimeMillis();
                            // 两次点击小于300ms 也就是连续点击
                            if (lastClick - firstClick < 300) {// 判断是否是执行了双击事件

                                if (indexFragment.pageType == 0) {
                                    indexFragment.mScrollLayout.scrollTo(0, 0);
                                    indexFragment.recommendFragment.listViewToTop();
                                } else if (indexFragment.pageType == 1) {
                                    indexFragment.mScrollLayout.scrollTo(0, 0);
                                    indexFragment.followGoodsFragment.listViewToTop();
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        indexFragment.mPtrFrame.autoRefresh();
                                    }
                                }, 120);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        selectFragment(indexFragment);
        selectView(tabHome);
    }

    public void selectIndexFragment() {
        selectFragment(indexFragment);
        selectView(tabHome);
    }

    private void selectFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (currFragment != fragment) {
            if (currFragment != null && currFragment.isAdded()) {
                transaction.hide(currFragment);
            }
            if (!fragment.isAdded()) {
                transaction.add(R.id.fragmentContainer, fragment).commitAllowingStateLoss();
            } else {
                transaction.show(fragment).commitAllowingStateLoss();
            }
            currFragment = fragment;
        }
    }

    public void onEventMainThread(final Object obj) {
        LogUtil.i(TAG, "MainActivity：onEventMainThread" + obj.toString());
        if (obj == null) {
            return;
        }

        if (obj instanceof LoginEvent) {
            LogUtil.i(TAG, "LoginEvent");
            LoginEvent event = (LoginEvent) obj;
            if (event.isLogin) {
                ConfigUtil.loginLeanCloud(getApplication());
                UserInfo userInfo = CommonPreference.getUserInfo();
                if (mineFragment != null) {
                    mineFragment.initData(userInfo);
                }

                startRequestForKvsd();
            }
        } else if (obj instanceof PushEvent) {
            PushEvent event = (PushEvent) obj;
            if (event.isPush && !TextUtils.isEmpty(event.extraMap)) {
                MessageController.dispatchMessage(this, event.extraMap);
            }
        } else if (obj instanceof MessagePrivateCountEvent) {
            MessagePrivateCountEvent event = (MessagePrivateCountEvent) obj;
            if (event.isUpdate) {
                updatePrivateUnreadCount();
            }
        }
    }

    private void updatePrivateUnreadCount() {

    }

    @OnClick({R.id.tabClass, R.id.tabMessage, R.id.tabMine, R.id.tabRelease, R.id.tabHome})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabHome:
                if (currSelectedTab == tabHome) {
                    if (indexFragment.pageType == 0) {
                        indexFragment.mScrollLayout.scrollTo(0, 0);
                        indexFragment.recommendFragment.listViewToTop();
                    } else if (indexFragment.pageType == 1) {
                        indexFragment.mScrollLayout.scrollTo(0, 0);
                        indexFragment.followGoodsFragment.listViewToTop();
                    }
                }
                selectFragment(indexFragment);
                break;

            case R.id.tabClass:
                selectFragment(classFragment);
                break;

            case R.id.tabMessage:
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(MainActivity.this);
                    return;
                }
                selectFragment(messageFragment);
                break;

            case R.id.tabMine:
                // selectFragment(mineFragment);
                // TODO: 18/1/9 自测
                EverydayFreshActivity.startMe(context);
                break;
            case R.id.tabRelease:   // 发布
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(MainActivity.this);
                    return;
                }

                if (ConfigUtil.isToVerify()) {
                    DialogManager.toVerify(MainActivity.this);
                } else {
                    MyConstants.flurPublishGoods = CommonUtils.shotActivity(this);
                    Intent intent = new Intent(this, PublishGoodsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
                break;
            default:
                break;
        }

        selectView(v);
    }

    private void doRequestForCredit() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();

        OkHttpClientManager.postAsyn(MyConstants.CREDIT_FOR_ZMXY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e("lalo", "芝麻信用：" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        CreditInfo info = JSON.parseObject(baseResult.obj, CreditInfo.class);
                        String url = info.getCredential();
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, url);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "芝麻信用");
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_WEB);

                    } else {
                        CommonUtils.error(baseResult, MainActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                // 判断图片是否旋转，处理图片旋转
                ExifInterface exif;
                Bitmap bitmap = ImageUtils.getLoacalBitmap(MainActivity.this, imgPath);
                try {
                    exif = new ExifInterface(imgPath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    // 旋转图片，使其显示方向正常
                    Bitmap newBitmap = ImageUtils.getTotateBitmap(bitmap, orientation);
                    File out = new File(imgPath);
                    boolean save = ImageUtils.saveMyBitmap(out, newBitmap);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    if (newBitmap != null) {
                        newBitmap.recycle();
                        newBitmap = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ImageItem item = new ImageItem();
                item.imagePath = imgPath;
                item.isSelected = true;
                ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();
                selectBitmap.add(item);
                Intent intent = new Intent(MainActivity.this, ReleaseActivity.class);
                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                startActivity(intent);
            }
        }

        LogUtil.e("isMineCreateView", isMineCreateView + "");
        if (isMineCreateView) {
            mineFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void selectView(View v) {
        if (v == tabRelease) {
            return;
        }
        if (currSelectedTab != null) {
            currSelectedTab.setSelected(false);
        }
        v.setSelected(true);
        currSelectedTab = v;
        if (v == tabHome) {
            tvTabHome.setSelected(true);
            tvTabMessage.setSelected(false);
            tvTabClass.setSelected(false);
            tvTabMine.setSelected(false);
        } else if (v == tabMessage) {
            tvTabHome.setSelected(false);
            tvTabMessage.setSelected(true);
            tvTabClass.setSelected(false);
            tvTabMine.setSelected(false);
        } else if (v == tabClass) {
            tvTabHome.setSelected(false);
            tvTabClass.setSelected(true);
            tvTabMessage.setSelected(false);
            tvTabMine.setSelected(false);
        } else if (v == tabMine) {
            tvTabHome.setSelected(false);
            tvTabMine.setSelected(true);
            tvTabMessage.setSelected(false);
            tvTabClass.setSelected(false);
        }
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.toast(this, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            MobclickAgent.onKillProcess(this);
            ActivityManager.finishAllActivities();
            System.exit(0);
        }
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    public void onCreateView() {
        isMineCreateView = true;
        LogUtil.e("isMineCreateView...callBack", isMineCreateView + "");

    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume");
        super.onResume();
        updateMessageBtnBadge();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mActivity = null;
        LogUtil.i("liang", "MainActivity onDestroy");
    }

    public void onEvent(LCIMOfflineMessageCountChangeEvent event) {
        updateMessageBtnBadge();
    }

    public void onEvent(LCIMIMTypeMessageEvent event) {
        updateMessageBtnBadge();
    }

    public void updateMessageBtnBadge() {
        if (tvMsgUnRead != null) {
            tvMsgUnRead.setVisibility(IMUtils.hasUnread() ? View.VISIBLE : View.GONE);
        }
    }

    // getGrantedCampaigns
    private void startRequestForKvsd() {
        OkHttpClientManager.StringCallback callBack = new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                try {
                    boolean flag = CommonUtils.parseEvent(mActivity, response, null);
                    if (flag) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code != ParserUtils.RESPONSE_SUCCESS_CODE)
                        return;
                    if (TextUtils.isEmpty(baseResult.obj))
                        return;
                    KvsdData kvsdData = JSON.parseObject(baseResult.obj, KvsdData.class);
                    String grantedCampaigns = kvsdData.getGrantedCampaigns();
                    CommonPreference.setGrantedCampaigns(grantedCampaigns);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        RequestManagerSys.startRequestForKvsd(CommonPreference.getAppToken(), callBack);
    }
}
