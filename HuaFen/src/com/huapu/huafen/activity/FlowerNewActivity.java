package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.AbstractRecyclerAdapter;
import com.huapu.huafen.adapter.MyFlowerAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.FlowerData;
import com.huapu.huafen.beans.PublishSuccessEvent;
import com.huapu.huafen.beans.SendSuccessEvent;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.common.RequestCode;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.events.FinishEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ShareHelper;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.FollowImageView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PtrAnimationBackgroundHeader;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.huapu.huafen.views.TitleBarNew;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static android.view.View.VISIBLE;

/**
 * 我的花语页面
 */
public class FlowerNewActivity extends BaseActivity implements AbstractRecyclerAdapter.LoadMoreListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrFrameLayout)
    PtrDefaultFrameLayout mPtrFrame;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;
    SimpleDraweeView changerIma;
    SimpleDraweeView ivHeader;
    TextView tvAuth;
    CommonTitleView ctvName;
    TextView tvPersonalData;
    FollowImageView ivFollow;
    TextView tvFollowCount;
    LinearLayout layoutFollow;
    TextView tvFansCount;
    LinearLayout layoutFans;
    TextView tvScCount;
    LinearLayout layoutSc;
    TextView tvPraise;
    LinearLayout layoutPraise;
    @BindView(R.id.titleBar)
    TitleBarNew titleBar;
    private long userId;
    private FlowerData flowerData;
    private TextView tvMsgUnRead;
    private TextView tvReport, tvShield;
    private View layoutSwitchHome, layoutSwitchMsg, layoutSwitchMine, layoutSwitchReport, layoutSwitchShield;
    private String dialogTitle = "屏蔽";
    private Intent intent = null;
    private int fellowShip;
    private int page;
    private View loadMoreView;
    private MyFlowerAdapter myFlowerAdapter;
    private View headerView;
    private String string;
    private View titleImageView;
    private TextView titleUserName;
    private SimpleDraweeView circleImage;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myflower);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
        if (getIntent().hasExtra(MyConstants.EXTRA_USER_ID)) {
            userId = getIntent().getLongExtra(MyConstants.EXTRA_USER_ID, 0);
        }
        initView();
    }

    private void initView() {
        setupPullToRefresh();
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(layoutManager);
        loadMoreView = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        loadMoreView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        loadMoreView.setPadding(CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(25));

        myFlowerAdapter = new MyFlowerAdapter(this, recyclerView);
        if (CommonPreference.getUserId() > 0 && userId > 0) {
            if (CommonPreference.getUserId() == userId) {
                myFlowerAdapter.setIsMe(true);
            }
        }
        myFlowerAdapter.setAutoLoadMoreEnable(false);
        myFlowerAdapter.setLoadMoreListener(this);
        recyclerView.setAdapter(myFlowerAdapter);
        myFlowerAdapter.setHeaderEnable(true);
        /**
         * ImageView changerIma;
         CircleImageView ivHeader;
         TextView tvAuth;
         CommonTitleView ctvName;
         TextView tvPersonalData;
         FollowImageView ivFollow;
         TextView tvFollowCount;
         LinearLayout layoutFollow;
         TextView tvFansCount;
         LinearLayout layoutFans;
         TextView tvScCount;
         LinearLayout layoutSc;
         TextView tvPraise;
         LinearLayout layoutPraise;
         */
        headerView = LayoutInflater.from(this).inflate(R.layout.header_myflower, recyclerView, false);
        changerIma = ButterKnife.findById(headerView, R.id.changerIma);
        ivHeader = ButterKnife.findById(headerView, R.id.ivHeader);
        tvAuth = ButterKnife.findById(headerView, R.id.tvAuth);
        ctvName = ButterKnife.findById(headerView, R.id.ctvName);
        tvPersonalData = ButterKnife.findById(headerView, R.id.tvPersonalData);
        ivFollow = ButterKnife.findById(headerView, R.id.ivFollow);
        tvFollowCount = ButterKnife.findById(headerView, R.id.tvFollowCount);
        layoutFollow = ButterKnife.findById(headerView, R.id.layoutFollow);
        tvFansCount = ButterKnife.findById(headerView, R.id.tvFansCount);
        layoutFans = ButterKnife.findById(headerView, R.id.layoutFans);
        tvScCount = ButterKnife.findById(headerView, R.id.tvScCount);
        layoutSc = ButterKnife.findById(headerView, R.id.layoutSc);
        tvPraise = ButterKnife.findById(headerView, R.id.tvPraise);
        layoutPraise = ButterKnife.findById(headerView, R.id.layoutPraise);

        layoutFollow.setOnClickListener(this);
        ivFollow.setOnClickListener(this);
        layoutFans.setOnClickListener(this);
        tvPersonalData.setOnClickListener(this);
        changerIma.setOnClickListener(this);

        myFlowerAdapter.addHeaderView(headerView);
        myFlowerAdapter.setAutoLoadMoreEnable(false);


        titleBar.setBackgroundAlpha(0);
        titleBar.getBtnTitleRight().setImageResource(R.drawable.personal_title_more);
        titleBar.getBtnTitleBarRight2().setImageResource(R.drawable.personal_title_share);
        titleBar.getBtnTitleLeft().setImageResource(R.drawable.personal_title_back);
        titleBar.getBtnTitleBarRight2().setVisibility(VISIBLE);
        titleBar.getBtnTitleRight().setVisibility(VISIBLE);

        /**
         * title layout
         */
        titleImageView = LayoutInflater.from(this).inflate(R.layout.layout_title_image, null);
        titleUserName = ButterKnife.findById(titleImageView, R.id.titleUserName);
        circleImage = ButterKnife.findById(titleImageView, R.id.circleImage);
        titleBar.setTitle(titleImageView);
        titleBar.setOnRightButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initPopMore(v);
            }
        });
        titleBar.setOnRightButton2ClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActionToShare();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lHeight = CommonUtils.getScreenWidth();
                titleBar.setAlphaInRecyclerView(dy, lHeight);
            }
        });
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onSendSuccessEvent(SendSuccessEvent event) {
        if (null != event && event.isSuccess) {
            refresh();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTitleLeft:
            case R.id.btnTitleLeft2:
                onBackPressed();
                break;
            case R.id.changerIma:
                if (userId == CommonPreference.getUserId()) {
                    Intent intent = new Intent(this, SelectedCoverActivity.class);
                    intent.putExtra("image", string);
                    intent.putExtra("type", "2");
                    intent.putExtra("flower", true);
                    startActivity(intent);
                }
                break;
            case R.id.tvPersonalData:
                finish();
                break;
            case R.id.ivFollow:
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(FlowerNewActivity.this);
                    return;
                }
                if (userId == CommonPreference.getUserId()) {
                    Intent intent = PersonalDataActivity.createIntent(this);
                    startActivityForResult(intent, RequestCode.REQUEST_FOR_ARTICLE_DETAIL);
                } else {
                    int fellowship = fellowShip;
                    if (1 == DialogManager.concernedUserDialog(this, fellowship, new DialogCallback() {
                        @Override
                        public void Click() {
                            // 取消关注
                            startRequestForConcernedUser("2", ivFollow);
                        }
                    })) {
                        // 关注
                        startRequestForConcernedUser("1", ivFollow);
                    }
                }
                break;
            case R.id.layoutFollow:
                intent = MyFollowedActivity.createIntent(this, userId);
                startActivity(intent);
                break;
            case R.id.layoutFans:
                intent = new Intent(FlowerNewActivity.this, FansListActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, userId);
                startActivity(intent);
                break;
            //弹出pop_tag
            case R.id.layoutPraise:

                break;
            case R.id.layoutSc:

                break;
        }
    }

    private void startRequestForConcernedUser(String type, final View view) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(userId));
        params.put("type", type);

        final int fellowship = fellowShip;

        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.CONCERNEDUSER, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                view.setEnabled(true);
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                view.setEnabled(true);
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "关注:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        //1 无关系 2 已关注 3 被关注 4 互相关注
                        if (fellowship == 1) {
                            ivFollow.setWhiteData(2);
                            fellowShip = 2;
                        } else if (fellowship == 2) {
                            ivFollow.setWhiteData(1);
                            fellowShip = 1;
                        } else if (fellowship == 3) {
                            ivFollow.setWhiteData(4);
                            fellowShip = 4;
                        } else if (fellowship == 4) {
                            ivFollow.setWhiteData(3);
                            fellowShip = 3;
                        }
                    } else {
                        if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
                            CommonUtils.checkAccess(FlowerNewActivity.this);
                        } else {
                            CommonUtils.error(baseResult, FlowerNewActivity.this, "");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startActionToShare() {
        if (flowerData == null || flowerData.getUser() == null) {
            return;
        }
        ShareHelper.shareFlower(this, flowerData);
    }

    public void setupPullToRefresh() {
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                int positions[] = new int[2];
                int[] maxs = manager.findFirstCompletelyVisibleItemPositions(positions);
                return findMax(maxs) == 0 && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }
        });
        PtrAnimationBackgroundHeader header = new PtrAnimationBackgroundHeader(this);
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

    }

    private int findMax(int[] array) {
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private void refresh() {
        recyclerView.scrollToPosition(0);
        if (page > 0) {
            myFlowerAdapter.initLoading(false);
        }
        page = 0;
        doRequest(REFRESH);
    }

    private void loadMore() {
        doRequest(LOAD_MORE);
    }

    private void doRequest(final String state) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            if (loadingStateView != null) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }
            if (recyclerView != null) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        if (onFollowGoodsPullListener != null) {
//                            onFollowGoodsPullListener.onRecommendPull();
//                        }
                    }
                }, 1000);
            }
//            isUnloaded = true;
            return;
        }
        ArrayMap<String, String> params = new ArrayMap<>(2);

        params.put("page", String.valueOf(page));
        params.put("userId", String.valueOf(userId));
        OkHttpClientManager.postAsyn(MyConstants.GETLIST, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (state.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (state.equals(REFRESH)) {
                    mPtrFrame.refreshComplete();
                } else if (state.equals(LOAD_MORE)) {

                }

            }


            @Override
            public void onResponse(String response) {
                try {

                    if (state.equals(LOADING)) {
                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                    } else if (state.equals(REFRESH)) {
                        if (state.equals(REFRESH)) {
                            mPtrFrame.refreshComplete();
                        }
                    } else if (state.equals(LOAD_MORE)) {
                        myFlowerAdapter.notifyMoreFinish();
                    }
                    Logger.e("get response:" + response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            FlowerData data = JSON.parseObject(baseResult.obj, FlowerData.class);
                            initData(data, state);
                            initUserData(data);
                            flowerData = data;
                        }
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void actionToMineFragment(int selectFramgnt) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, selectFramgnt);
        startActivity(intent);
    }

    private void initPopMore(View v) {
        if (popupWindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_more, null);
            layoutSwitchHome = view.findViewById(R.id.layoutSwitchHome);
            layoutSwitchMsg = view.findViewById(R.id.layoutSwitchMsg);
            layoutSwitchMine = view.findViewById(R.id.layoutSwitchMine);
            layoutSwitchReport = view.findViewById(R.id.layoutSwitchReport);
            layoutSwitchShield = view.findViewById(R.id.layoutSwitchShield);
            tvMsgUnRead = (TextView) view.findViewById(R.id.tvMsgUnRead);
            tvReport = (TextView) view.findViewById(R.id.tvReport);
            tvShield = (TextView) view.findViewById(R.id.tvShield);
            layoutSwitchHome.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(1);
                    popupWindow.dismiss();
                }
            });
            layoutSwitchMsg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(3);
                    popupWindow.dismiss();
                }
            });
            layoutSwitchMine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(4);
                    popupWindow.dismiss();
                }
            });
            layoutSwitchReport.setVisibility(View.GONE);
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setAnimationStyle(R.style.pop_search_switch);
        }
        tvShield.setText(dialogTitle);
        tvMsgUnRead.setVisibility(titleBar.getMoreBtnBadgeVisibility() ? View.VISIBLE : View.GONE);
        popupWindow.showAsDropDown(v);
    }

    public void onEventMainThread(FinishEvent event) {
        if (event != null && event.isImage) {
            refresh();
        }
    }

    private void setAvatar(String url) {
        String o = (String) ivHeader.getTag();
        if (!TextUtils.equals(o, url)) {
            ImageLoader.resizeSmall(ivHeader, url, 1);
            ivHeader.setTag(url);
        }
    }

    private void initUserData(final FlowerData bean) {
        setAvatar(bean.getUser().getAvatarUrl());
        ctvName.setNameColor(Color.WHITE);
        ctvName.setData(bean);
        userId = bean.getUser().getUserId();
        titleUserName.setText(bean.getUser().getUserName());
        circleImage.setImageURI(bean.getUser().getAvatarUrl());
        string = bean.getUser().getArticleBackground();

        String o = (String) changerIma.getTag();
        if (!TextUtils.equals(o, string)) {
            ImageLoader.loadImage(changerIma, string);
            changerIma.setTag(string);
        }

        ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlowerNewActivity.this, ImageActivity.class);
                intent.putExtra("imageUrl", bean.getUser().getAvatarUrl());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        tvFollowCount.setText(bean.getCount().getFocus());
        tvFansCount.setText(bean.getCount().getFans());
        //收藏
        if (bean.getCount().getCollection() != null) {
            tvScCount.setText(bean.getCount().getCollection());
        } else {
            tvScCount.setText("0");
        }

        //点赞
        if (bean.getCount().getLike() != null) {
            tvPraise.setText(bean.getCount().getLike());
        } else {
            tvPraise.setText("0");
        }

        //关注按钮的显示
        fellowShip = bean.getUser().getFellowship();
        if (userId == CommonPreference.getUserId()) {
            tvPersonalData.setText("我的店铺");
        } else {
            ivFollow.setWhiteData(fellowShip);
            tvPersonalData.setText("TA的店铺");
        }

    }

    private void initData(FlowerData data, String state) {
        if (data.getPage() == 0) {
            myFlowerAdapter.setAutoLoadMoreEnable(false);
        } else {
            myFlowerAdapter.setAutoLoadMoreEnable(true);
        }

        page++;

        if (state.equals(LOADING)) {
            myFlowerAdapter.setData(data.getList(), data);
        } else if (state.equals(REFRESH)) {
            myFlowerAdapter.setData(data.getList(), data);
        } else if (LOAD_MORE.equals(state)) {
            myFlowerAdapter.appendData(data.getList());
        }
    }

    @Override
    public void onLoadMore() {
        loadMore();
    }

    public void onEventMainThread(final Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof PublishSuccessEvent) {
            PublishSuccessEvent event = (PublishSuccessEvent) obj;
            if (event.publishSuccess) {
                refresh();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == RequestCode.REQUEST_FOR_ARTICLE_DETAIL) {
            refresh();
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
