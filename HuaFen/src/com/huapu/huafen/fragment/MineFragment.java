package com.huapu.huafen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.CreditActivity;
import com.huapu.huafen.activity.CreditPanelActivity;
import com.huapu.huafen.activity.FlowerNewActivity;
import com.huapu.huafen.activity.LikeListActivity;
import com.huapu.huafen.activity.MontageActivity;
import com.huapu.huafen.activity.MyAuctionActivity;
import com.huapu.huafen.activity.MyFollowedActivity;
import com.huapu.huafen.activity.OrdersListActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.ReleaseListActivity;
import com.huapu.huafen.activity.SelectedCoverActivity;
import com.huapu.huafen.activity.SetActivity;
import com.huapu.huafen.activity.VerifiedActivity;
import com.huapu.huafen.activity.WalletActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.adapter.GridAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.MyInfoBean;
import com.huapu.huafen.beans.SendSuccessEvent;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.PtrAnimationBackgroundHeader;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 我的
 *
 * @author liang_xs
 * @ClassName: MineFragment
 * @date 2016-03-27
 */
public class MineFragment extends BaseFragment implements OnClickListener {
    private String TAG = "MineFragment";
    private final static int REQUEST_CODE_FOR_WALLET = 0x527;
    private static final String guide_key = "first_mine_pic";
    private static final int REQUEST_CODE_FOR_GET_CREDIT_SCORE = 0x1311;
    public static MineFragment mineFragment;
    public SimpleDraweeView ivHeader;
    public CommonTitleView ctvName;
    public TextView tvXINMoney;
    public UserInfo userInfo;
    private View layoutMyself, layoutZm, layoutXin, layoutRelease, layoutSell, layoutBuy, layoutRefund, layoutHelp, layoutSet, layoutLike, layoutWallet, tvFamily;
    private View viewLineXin;
    private TextView auction;
    private SimpleDraweeView headerBgView;
    private TextView tvReleaseCount, tvSellCount, tvBuyCount, tvRefundCount;
    private RelativeLayout rlVerified;
    private OnCreateViewListener listener;
    private int bottom;
    private View attentionLayout, flowerLayout;
    private PtrFrameLayout mPtrFrame;
    private View mContentView;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private GridAdapter adapter;

    private TextView loginText;

    private ImageView recommandImage;

    private TextView tvVerified;

    public void setOnCreateViewListener(OnCreateViewListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_mine, container, false);
        mineFragment = this;
        EventBus.getDefault().register(this);
        initViews(layout);
        userInfo = CommonPreference.getUserInfo();
        if (userInfo != null) {
            initData(userInfo);
        }
        if (this.listener != null) {
            listener.onCreateView();
        }
        return layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onSendSuccessEvent(SendSuccessEvent event) {
        if (null == event)
            return;
        if (event.isSuccess) {
            getNetData();
        } else if (event.freshPage) {
            if (null != adapter) {
                adapter.setData(null);
                Logger.e("clear adapter");
            }
            refreshHeaderUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshHeaderUI();
        getNetData();
        if (CommonPreference.getUserId() > 0) {
            if (CommonPreference.getBooleanValue(guide_key, true)) {
                Intent intent = new Intent(getActivity(), MontageActivity.class);
                intent.putExtra(MyConstants.EXTRA_MONTAGE, guide_key);
                this.startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
                CommonPreference.setBooleanValue(guide_key, false);
            }

//            if (CommonPreference.getIntValue(MyConstants.MINE_GUIDE_TIPS, 0) == 0) {
//                Intent intent = new Intent(getActivity(), MontageActivity.class);
//                intent.putExtra(MyConstants.EXTRA_MONTAGE, MyConstants.MINE_GUIDE_TIPS);
//                startActivity(intent);
//                getActivity().overridePendingTransition(0, 0);
//            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mineFragment = null;
    }

    public void initViews(View layout) {
        super.initViews(layout);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        ViewUtil.setOffItemAnimator(recyclerView);

        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.header_mine, null, false);

        getContentViews(mContentView);

        auction.setOnClickListener(this);
        flowerLayout.setOnClickListener(this);
        attentionLayout.setOnClickListener(this);
        layoutMyself.setOnClickListener(this);
        layoutZm.setOnClickListener(this);
        layoutXin.setOnClickListener(this);
        layoutRelease.setOnClickListener(this);
        layoutSell.setOnClickListener(this);
        layoutBuy.setOnClickListener(this);
        layoutRefund.setOnClickListener(this);
        layoutHelp.setOnClickListener(this);
        tvFamily.setOnClickListener(this);
        layoutSet.setOnClickListener(this);
        layoutLike.setOnClickListener(this);
        layoutWallet.setOnClickListener(this);
        tvVerified.setOnClickListener(this);

        mPtrFrame = (PtrFrameLayout) layout.findViewById(R.id.ptrView);
        PtrAnimationBackgroundHeader header = new PtrAnimationBackgroundHeader(getContext());
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(300);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);

                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                int index = manager.findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);
                boolean indexTop = false;
                if (childAt == null || (index == 0 && childAt.getTop() == 0)) {
                    indexTop = true;
                }

                return indexTop && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getNetData();
//                if(CommonPreference.isLogin()){
//                    getNetData();
//                    boolean hasVerified = CommonPreference.getUserInfo().hasVerified;
//                    int userLevel = CommonPreference.getUserInfo().getUserLevel();
//                    if(hasVerified||userLevel==3){
//                        rlVerified.setVisibility(View.GONE);
//                        mPtrFrame.refreshComplete();
//                    }else {
////                        adapter.addHeaderView(mContentView);
//                        rlVerified.setVisibility(View.VISIBLE);
//                        mPtrFrame.refreshComplete();
//                    }
//                }else {
//                    mPtrFrame.refreshComplete();
//                }
            }
        });
        ViewTreeObserver observer = layoutHelp.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int[] location = new int[2];
                layoutHelp.getLocationOnScreen(location);
                bottom = location[1];
                int dy = layoutHelp.getHeight() / 2;
                Intent intent = new Intent();
                int height = bottom - dy;
                if (bottom >= CommonUtils.getScreenHeight() - CommonUtils.dp2px(100)) {
                    intent.putExtra("overScreen", true);
                } else {
                    intent.putExtra("overScreen", false);
                }
                intent.putExtra("bottom", height);
//                CommonUtils.buildMontage(getActivity(), intent, "first_mine");
                layoutHelp.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        refreshHeaderUI();

        adapter = new GridAdapter(getActivity());
        adapter.addHeaderView(mContentView);
        recyclerView.setAdapter(adapter.getWrapperAdapter());
        if (CommonPreference.isLogin()) {
            boolean hasVerified = CommonPreference.getUserInfo().hasVerified;
            int userLevel = CommonPreference.getUserInfo().getUserLevel();
            if (hasVerified || userLevel == 3) {
                rlVerified.setVisibility(View.GONE);
            }
        } else {
            rlVerified.setVisibility(View.GONE);
        }
    }

    private void getContentViews(View mContentView) {
        loginText = (TextView) mContentView.findViewById(R.id.loginText);
        recommandImage = (ImageView) mContentView.findViewById(R.id.recommandImage);
        tvVerified = (TextView) mContentView.findViewById(R.id.tv_verified);
        rlVerified = (RelativeLayout) mContentView.findViewById(R.id.rl_verified);
        headerBgView = (SimpleDraweeView) mContentView.findViewById(R.id.commonBg);
        layoutMyself = mContentView.findViewById(R.id.layoutMyself);
        layoutZm = mContentView.findViewById(R.id.layoutZm);
        layoutXin = mContentView.findViewById(R.id.layoutXin);
        layoutRelease = mContentView.findViewById(R.id.layoutRelease);
        layoutSell = mContentView.findViewById(R.id.layoutSell);
        layoutBuy = mContentView.findViewById(R.id.layoutBuy);
        layoutRefund = mContentView.findViewById(R.id.layoutRefund);
        layoutHelp = mContentView.findViewById(R.id.layoutHelp);
        tvFamily = mContentView.findViewById(R.id.tv_family);
        layoutSet = mContentView.findViewById(R.id.layoutSet);
        layoutLike = mContentView.findViewById(R.id.layoutLike);
        ivHeader = (SimpleDraweeView) mContentView.findViewById(R.id.ivHeader);
        ctvName = (CommonTitleView) mContentView.findViewById(R.id.ctvName);
        ctvName.setTextColor(getResources().getColor(R.color.white));
        tvReleaseCount = (TextView) mContentView.findViewById(R.id.tvReleaseCount);
        tvSellCount = (TextView) mContentView.findViewById(R.id.tvSellCount);
        tvBuyCount = (TextView) mContentView.findViewById(R.id.tvBuyCount);
        tvRefundCount = (TextView) mContentView.findViewById(R.id.tvRefundCount);
        tvXINMoney = (TextView) mContentView.findViewById(R.id.tvXINMoney);
        layoutWallet = mContentView.findViewById(R.id.layoutWallet);
        auction = (TextView) mContentView.findViewById(R.id.auction);
        flowerLayout = mContentView.findViewById(R.id.flowerLayout);
        attentionLayout = mContentView.findViewById(R.id.attentionLayout);
        viewLineXin = mContentView.findViewById(R.id.viewLineXin);

        ivHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startPersonStore();
            }
        });
        mContentView.findViewById(R.id.enterStoreLayout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startPersonStore();
            }
        });
    }

    private void refreshHeaderUI() {

        initData(userInfo);

        if (CommonPreference.getUserId() > 0) {
            mContentView.findViewById(R.id.enterStoreLayout).setVisibility(View.VISIBLE);
            loginText.setVisibility(View.GONE);
            recommandImage.setVisibility(View.VISIBLE);
        } else {

            headerBgView.setImageURI("");
            headerBgView.setTag(null);

            mContentView.findViewById(R.id.enterStoreLayout).setVisibility(View.GONE);
            loginText.setVisibility(View.VISIBLE);
            recommandImage.setVisibility(View.GONE);
        }
    }

    private void startPersonStore() {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(getActivity());
            return;
        }
        Intent intent = new Intent(getActivity(), PersonalPagerHomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.layoutMyself: // 个人主页
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), SelectedCoverActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
            case R.id.layoutXin: // 信用金
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), CreditActivity.class);
                intent.putExtra(MyConstants.EXTRA_CREDIT, userInfo);
                startActivity(intent);
                break;
            case R.id.layoutRelease: // 我发布的
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), ReleaseListActivity.class);
                startActivity(intent);
                break;

            case R.id.layoutSell: // 我卖出的
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), OrdersListActivity.class);
                intent.putExtra(MyConstants.EXTRA_ORDERS_ROLE, 2);
                startActivity(intent);
                break;

            case R.id.layoutBuy: // 我买到的
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), OrdersListActivity.class);
                intent.putExtra(MyConstants.EXTRA_ORDERS_ROLE, 1);
                startActivity(intent);
                break;

            case R.id.layoutRefund: // 退款
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), OrdersListActivity.class);
                intent.putExtra(MyConstants.EXTRA_ORDERS_ROLE, 0);
                startActivity(intent);
                break;

            case R.id.layoutHelp: // 帮助中心
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.WEBVIEW_HELP_CENTER);
                intent.putExtra("isHelp", true);
//			intent.putExtra("isShare", true);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "帮助中心");
                startActivity(intent);
                break;

            case R.id.layoutSet: // 设置
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), SetActivity.class);
                startActivity(intent);
                break;

            case R.id.layoutLike: // 我喜欢的
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), LikeListActivity.class);
                startActivity(intent);
                break;
            case R.id.layoutZm: // 芝麻信用
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                if (userInfo == null) {
                    return;
                }
                if (userInfo.getZmCreditPoint() > 0) {
                    intent = new Intent();
                    intent.setClass(getActivity(), CreditPanelActivity.class);
                    intent.putExtra("zmCreditPoint", userInfo.getZmCreditPoint());
                    startActivityForResult(intent, REQUEST_CODE_FOR_GET_CREDIT_SCORE);
                } else {
                    startRequestForCreditZM();
                }
                break;

            case R.id.layoutWallet: // 小金库
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), WalletActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_WALLET);
                break;
            case R.id.attentionLayout:  // 我关注的
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = MyFollowedActivity.createIntent(getActivity(), CommonPreference.getUserId());
                startActivity(intent);
                break;
            case R.id.flowerLayout: // 花语
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    break;
                }
                intent = new Intent(getActivity(), FlowerNewActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, CommonPreference.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.auction: // 拍卖
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), MyAuctionActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_verified:
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity(), MyConstants.FROM_FLAGS_VERIFIED);
                    return;
                }
                intent = new Intent(getActivity(), VerifiedActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_family:
                intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.WEBVIEW_FAMILY);
//                intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE,"花粉儿family");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case MyConstants.REQUEST_CODE_FOR_WEB:
                if (data == null) {
                    return;
                }
                String flag = data.getStringExtra(MyConstants.EXTRA_GET_CREDIT_SCORE);
                if (flag.equals(MyConstants.GET_CREDIT_SCORE_VALUE)) {
                    userInfo = CommonPreference.getUserInfo();
                    if (userInfo != null) {
                        initData(userInfo);
                        if (isCredited()) {
                            layoutZm.performClick();
                        }
                    }
                }
                break;
            case REQUEST_CODE_FOR_GET_CREDIT_SCORE:
                if (data == null) {
                    return;
                }

                int cancelCode = data.getIntExtra(MyConstants.AUTH_ZM_CANCEL, 0);
                if (cancelCode == 1234) {
                    userInfo = CommonPreference.getUserInfo();
                    if (userInfo != null) {
                        initData(userInfo);
                    }
                }
                break;
            case REQUEST_CODE_FOR_WALLET:
                if (data != null) {
                    int savedMoney = data.getIntExtra("saved", -1);
                    if (savedMoney > 0) {
                        String savedDes = String.format(getActivity().getResources().getString(R.string.saved_des), savedMoney);
                        CommonPreference.setIntValue("savedMoney", savedMoney);
                    }
                }
                break;
        }
    }

    private boolean isCredited() {
        return userInfo.getZmCreditPoint() > 0 || userInfo.hasVerified;
    }

    private void startRequestForCreditZM() {
        if (!CommonUtils.isNetAvaliable(getActivity())) {
            ToastUtil.toast(getActivity(), "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(getActivity());
        HashMap<String, String> params = new HashMap<String, String>();
        // 芝麻信用授权
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
                        intent.setClass(getActivity(), WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, url);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "芝麻信用");
                        getActivity().startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_WEB);

                    } else {
                        CommonUtils.error(baseResult, getActivity(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void initData(UserInfo userInfo) {
        if (ivHeader == null) {
            return;
        }

        ctvName.setData(userInfo);

        if (CommonPreference.getUserId() > 0) {
            if (null != userInfo) {
                ViewUtil.setAvatar(ivHeader, userInfo.getUserIcon());

                tvReleaseCount.setText(String.valueOf(userInfo.getReleaseCount()));
                tvSellCount.setText(String.valueOf(userInfo.getSelledCount()));
                tvBuyCount.setText(String.valueOf(userInfo.buyedCount));
                tvRefundCount.setText(String.valueOf(userInfo.refundCount));
            }
        } else {
            ViewUtil.setAvatar(ivHeader, MyConstants.RES + R.drawable.ic_head);
            tvReleaseCount.setText("-");
            tvSellCount.setText("-");
            tvBuyCount.setText("-");
            tvRefundCount.setText("-");
        }
    }

    private void resetInfo(MyInfoBean result) {
        String o = (String) headerBgView.getTag();
        String url = result.user.getProfileBackground();
        if (!TextUtils.equals(o, url)) {
            ImageLoader.loadImage(headerBgView, url, 0);
            headerBgView.setTag(url);
        }

        CommonPreference.setUserInfo(result.user);

        initData(result.user);
        adapter.setRecTraceId(result.recTraceId);
        adapter.setData(result.recItems);
        boolean hasVerified1 = CommonPreference.getUserInfo().hasVerified;
        LogUtil.i(TAG, "实名认证：" + hasVerified1);
        boolean hasVerified = result.user.hasVerified;
        int userLevel = result.user.getUserLevel();
        if (hasVerified || userLevel == 3) {
            rlVerified.setVisibility(View.GONE);
            mPtrFrame.refreshComplete();
        } else {
            rlVerified.setVisibility(View.GONE);
            mPtrFrame.refreshComplete();
        }
    }

    private void getNetData() {
        OkHttpClientManager.postAsyn(MyConstants.MY_PROFILE, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                mPtrFrame.refreshComplete();
            }

            @Override
            public void onResponse(String response) {
                Logger.e("get response:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    mPtrFrame.refreshComplete();
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            MyInfoBean result = JSON.parseObject(baseResult.obj, MyInfoBean.class);
                            resetInfo(result);
                        }
                    } else {
                        mPtrFrame.refreshComplete();
                        refreshHeaderUI();
                        adapter.setData(null);
//                        CommonUtils.error(baseResult, getContext(), "");
                    }
                } catch (Exception e) {
                    mPtrFrame.refreshComplete();
                    e.printStackTrace();
                }

            }
        });
    }

    public interface OnCreateViewListener {
        void onCreateView();
    }
}
