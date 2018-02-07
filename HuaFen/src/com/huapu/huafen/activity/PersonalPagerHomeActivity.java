package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.MyFragmentPagerAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsInfoBean;
import com.huapu.huafen.beans.PublishSuccessEvent;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.callcenter.CallCenterHelper;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.chatim.activity.PrivateConversationActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.common.RequestCode;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.events.LoginEvent;
import com.huapu.huafen.events.MessageUnReadCountEvent;
import com.huapu.huafen.events.RefreshEvent;
import com.huapu.huafen.fragment.HomePagerFragment;
import com.huapu.huafen.fragment.base.ScrollAbleFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.scrollablelayoutlib.PagerSlidingTabStrip;
import com.huapu.huafen.scrollablelayoutlib.ScrollableLayout;
import com.huapu.huafen.scrollablelayoutlib.ScrollableLayout.OnScrollListener;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.BindSinaUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ShareHelper;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.FlowerLayout;
import com.huapu.huafen.views.FollowImageView;
import com.huapu.huafen.views.HLoadingStateView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 个人主页/店铺
 *
 * @author liang_xs
 */
public class PersonalPagerHomeActivity extends BaseActivity implements OnClickListener, BindSinaUtil.RefreshSinaUI {
    public final static int REQUEST_CODE_FOR_PERSONAL_DETAIL = 0x111;
    private static final int VIDEO_CONTENT_DESC_MAX_LINE = 1;// 默认展示最大行数3行
    private static final int SHRINK_UP_STATE = 1;// 收起状态
    private static final int SPREAD_STATE = 2;// 展开状态
    private static int mState = SHRINK_UP_STATE;//默认收起状态
    public int pageType = 0;
    //下拉刷新
    public PtrFrameLayout mPtrFrame;
    @BindView(R2.id.btnTitleLeft)
    Button btnTitleLeft;
    @BindView(R2.id.btnTitleRight)
    Button btnTitleRight;
    @BindView(R2.id.btnTitleLeft2)
    Button btnTitleLeft2;
    @BindView(R2.id.btnTitleRight2)
    Button btnTitleRight2;
    @BindView(R2.id.tvTitleText)
    TextView tvTitleText;
    @BindView(R2.id.lineTitle)
    View lineTitle;
    @BindView(R2.id.btnMore)
    Button btnMore;
    @BindView(R2.id.btnMore2)
    Button btnMore2;
    @BindView(R2.id.tvUnRead)
    TextView tvUnRead;
    @BindView(R2.id.layoutLeft)
    View layoutLeft;
    @BindView(R2.id.layoutRight)
    View layoutRight;
    @BindView(R2.id.layoutMore)
    View layoutMore;
    @BindView(R2.id.scrollableLayout)
    ScrollableLayout scrollableLayout;
    @BindView(R2.id.pagerStrip)
    PagerSlidingTabStrip tabStrip;
    @BindView(R2.id.vpPerson)
    ViewPager personViewPager;
    @BindView(R2.id.ivHeader)
    SimpleDraweeView ivHeader;
    @BindView(R2.id.ctvName)
    CommonTitleView ctvName;
    @BindView(R2.id.tvComment1)
    TextView tvComment1;
    @BindView(R2.id.tvComment2)
    TextView tvComment2;
    @BindView(R2.id.tvComment3)
    TextView tvComment3;
    @BindView(R2.id.ivFollow)
    FollowImageView ivFollow;
    @BindView(R2.id.tvFansCount)
    TextView tvFansCount;
    @BindView(R2.id.tvFollowCount)
    TextView tvFollowCount;
    @BindView(R2.id.layoutFans)
    View layoutFans;
    @BindView(R2.id.layoutFollow)
    View layoutFollow;
    @BindView(R2.id.tvAuth)
    TextView tvAuth;
    @BindView(R2.id.tvNotice)
    TextView tvNotice;
    @BindView(R2.id.ivNoticeMore)
    ImageView ivNoticeMore;
    @BindView(R2.id.tvLastVisitText)
    TextView tvLastVisitText;
    @BindView(R2.id.tvFavorableRate)
    TextView tvFavorableRate;
    @BindView(R2.id.layoutTitle)
    View layoutTitle;
    @BindView(R2.id.rlCommentEnter)
    RelativeLayout rlCommentEnter;
    @BindView(R2.id.tvPersonalData)
    TextView tvPersonalData;
    @BindView(R2.id.ivEditNotice)
    ImageView ivEditNotice;
    @BindView(R2.id.layoutGoodsRate)
    View layoutGoodsRate;
    @BindView(R2.id.loadingStateView)
    HLoadingStateView loadingStateView;
    @BindView(R2.id.layoutImage)
    LinearLayout layoutImage;
    @BindView(R2.id.weiBoLayoutClose)
    ImageView weiBoLayoutClose;
    @BindView(R2.id.weiboBindingLayout)
    RelativeLayout weiboBindingLayout;
    @BindView(R2.id.weiBoBindText)
    TextView weiBoBindText;
    @BindView(R2.id.flowerLayout)
    FlowerLayout flowerLayout;

    private UserInfo userInfo;
    private ImageView ivTips;
    private PopupWindow morePopwindow;
    private PopupWindow tipPopwindow;
    private int REQUEST_CODE_FOR_GET_CREDIT_SCORE = 0x1311;
    private boolean isSearchExpand = true;
    private boolean isFirst = true;
    private GoodsInfoBean goodsInfoBean;
    private long userId;
    private String dialogTitle = "屏蔽";
    private Intent intent = null;
    private String notice;
    private View layoutSwitchHome, layoutSwitchMsg, layoutSwitchMine, layoutSwitchReport, layoutSwitchShield, layoutSwitchLinkTA;
    private TextView tvReport, tvShield;
    private ImageView ivReport;
    private TextView tvMsgUnRead;
    private int position = -1;
    private int type = -1;

    public void onEventMainThread(final Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof MessageUnReadCountEvent) {
            MessageUnReadCountEvent event = (MessageUnReadCountEvent) obj;
            if (event.isUpdate) {
                initMoreMsgUnRead();
            }
        } else if (obj instanceof RefreshEvent) {
            RefreshEvent event = (RefreshEvent) obj;
            if (event != null && event.refresh) {
                refresh();
            }

        } else if (obj instanceof PublishSuccessEvent) {
            PublishSuccessEvent event = (PublishSuccessEvent) obj;
            if (event != null && event.publishSuccess) {
                refresh();
            }
        }
    }

    private void initMoreMsgUnRead() {
        if (tvUnRead != null) {
            int count = MyConstants.UNREAD_ORDER_COUNT + MyConstants.UNREAD_PRIVATE_COUNT + MyConstants.UNREAD_SYSTEM_COUNT + MyConstants.UNREAD_COMMENT_MSG_COUNT;
            if (count > 0) {
                tvUnRead.setVisibility(View.VISIBLE);
            } else {
                tvUnRead.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_pager_home);
        EventBus.getDefault().register(this);
        if (getIntent().hasExtra(MyConstants.EXTRA_USER_ID)) {
            userId = getIntent().getLongExtra(MyConstants.EXTRA_USER_ID, 0);
        }
        if (getIntent().hasExtra("position")) {
            position = getIntent().getIntExtra("position", -1);
        }
        if (userId == 0) {
            userId = CommonPreference.getUserId();
        }
        //初始化控件
        initViews();
        initMoreMsgUnRead();
        setupPullToRefresh();
        startLoading();
    }

    public void setupPullToRefresh() {
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_web_view_frame);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return scrollableLayout.canPtr();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
                mPtrFrame.refreshComplete();
            }
        });

        ViewUtil.setPtrFrameLayout(mPtrFrame);
    }

    //初始化控件
    private void initViews() {
        ctvName.setNameColor(getResources().getColor(R.color.white));
        int titleHeight = CommonUtils.dp2px(45);
        scrollableLayout.setTitleHeight(titleHeight);
        ivFollow.setOnClickListener(this);
        layoutFans.setOnClickListener(this);
        layoutFollow.setOnClickListener(this);
        layoutLeft.setOnClickListener(this);
        layoutRight.setOnClickListener(this);
        layoutMore.setOnClickListener(this);
        ivNoticeMore.setOnClickListener(this);
        layoutTitle.setOnClickListener(this);
        btnTitleLeft.setOnClickListener(this);
        btnTitleLeft2.setOnClickListener(this);
        btnTitleRight.setOnClickListener(this);
        btnTitleRight2.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnMore2.setOnClickListener(this);
        rlCommentEnter.setOnClickListener(this);
        tvPersonalData.setOnClickListener(this);
        layoutGoodsRate.setOnClickListener(this);
        layoutImage.setOnClickListener(this);
        tvTitleText.setAlpha(0);
        lineTitle.setAlpha(0);
        btnTitleRight2.setAlpha(0);
        btnMore2.setAlpha(0);
        btnTitleLeft2.setAlpha(0);
        scrollableLayout.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(int currentY, int maxY) {
                int topLayoutHeight = 300;
                int titleBarHeight = layoutTitle.getHeight();
                float f = (float) currentY / (float) (topLayoutHeight - titleBarHeight);
                f = f > 1 ? 1 : f;
                btnTitleRight.setAlpha(1 - f);
                btnTitleRight2.setAlpha(f);
                btnMore.setAlpha(1 - f);
                btnMore2.setAlpha(f);
                btnTitleLeft.setAlpha(1 - f);
                btnTitleLeft2.setAlpha(f);
                tvTitleText.setAlpha(f);
                lineTitle.setAlpha(f);
                if (currentY < maxY && !isSearchExpand) {
                    isSearchExpand = true;
                } else if (currentY >= maxY && isSearchExpand) {
                    isSearchExpand = false;
                }

            }
        });
        weiBoLayoutClose.setOnClickListener(this);
        weiBoBindText.setOnClickListener(this);
    }

    public void initFragmentPager(final ViewPager viewPager, PagerSlidingTabStrip pagerSlidingTabStrip, final ScrollableLayout mScrollLayout) {
        if (isFirst) {
            isFirst = false;
            final ArrayList<ScrollAbleFragment> fragmentList = new ArrayList<ScrollAbleFragment>();
            String[] urls = new String[]{MyConstants.USER_SELLING, MyConstants.USER_SOLD};

            for (String url : urls) {
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                bundle.putLong("userId", userId);
                HomePagerFragment homePagerFragment = HomePagerFragment.newInstance(bundle);
                fragmentList.add(homePagerFragment);
            }

            int sellingCount = 0;
            int soldCount = 0;
            if (goodsInfoBean != null && goodsInfoBean.getUserInfo() != null) {
                sellingCount = goodsInfoBean.getUserInfo().getSellingCount();
                soldCount = goodsInfoBean.getUserInfo().getSelledCount();
            }
            List<String> titleList = new ArrayList<String>();
            titleList.add("在售宝贝" + (sellingCount > 999 ? "999+" : sellingCount));
            titleList.add("已售出" + (soldCount > 999 ? "999+" : soldCount));

            viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList));
            mScrollLayout.getHelper().setCurrentScrollableContainer(fragmentList.get(0));
            pagerSlidingTabStrip.setViewPager(viewPager);
            pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int i, float v, int i2) {

                }

                @Override
                public void onPageSelected(int i) {
                    Log.e("onPageSelected", "page:" + i);
                    pageType = i;
                    /** 标注当前页面 **/
                    mScrollLayout.getHelper().setCurrentScrollableContainer(fragmentList.get(i));

                    ScrollAbleFragment fragment = fragmentList.get(i);
                    if (fragment instanceof HomePagerFragment) {
                        if (isSearchExpand) {
                            ((HomePagerFragment) fragment).listViewToTop();
                        }
                    }

                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            viewPager.setCurrentItem(0);
        } else {
            MyFragmentPagerAdapter adapter = (MyFragmentPagerAdapter) viewPager.getAdapter();
            HomePagerFragment fragment = (HomePagerFragment) adapter.getItem(viewPager.getCurrentItem());
            fragment.refresh();
        }
    }

    private void actionToMineFragment(int selectFramgnt) {
        Intent intent = new Intent(PersonalPagerHomeActivity.this, MainActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, selectFramgnt);
        startActivity(intent);
    }

    private void linkTA() {
        if (CommonPreference.isLogin()) {
            if (goodsInfoBean == null || goodsInfoBean.getUserInfo() == null) {
                return;
            }
            // 开启私信会话
            Intent intent = new Intent(this, PrivateConversationActivity.class);
            intent.putExtra(MyConstants.IM_PEER_ID, goodsInfoBean.getUserInfo().getUserId() + "");
            startActivity(intent);
        } else {
            ActionUtil.loginAndToast(this);
        }
    }

    private void initPopMore(View v) {
        if (morePopwindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_more, null);
            layoutSwitchHome = view.findViewById(R.id.layoutSwitchHome);
            layoutSwitchMsg = view.findViewById(R.id.layoutSwitchMsg);
            layoutSwitchMine = view.findViewById(R.id.layoutSwitchMine);
            layoutSwitchReport = view.findViewById(R.id.layoutSwitchReport);
            layoutSwitchShield = view.findViewById(R.id.layoutSwitchShield);
            layoutSwitchLinkTA = view.findViewById(R.id.layoutSwitchLinkTA);
            layoutSwitchShield.setVisibility(View.VISIBLE);
            tvMsgUnRead = (TextView) view.findViewById(R.id.tvMsgUnRead);
            tvReport = (TextView) view.findViewById(R.id.tvReport);
            tvShield = (TextView) view.findViewById(R.id.tvShield);
            ivReport = (ImageView) view.findViewById(R.id.ivReport);
            ivReport.setImageResource(R.drawable.pop_more_complain);
            tvReport.setText("投诉");
            long myUserId = CommonPreference.getUserId();
            long userId = 0L;
            if (goodsInfoBean != null) {
                userId = goodsInfoBean.getUserInfo().getUserId();
            }
            boolean flag = myUserId > 0 && userId > 0 && myUserId == userId;
            if (flag) {
                layoutSwitchReport.setVisibility(View.GONE);
                layoutSwitchShield.setVisibility(View.GONE);
                layoutSwitchLinkTA.setVisibility(View.GONE);
            } else {
                layoutSwitchReport.setVisibility(View.VISIBLE);
                layoutSwitchShield.setVisibility(View.VISIBLE);
                layoutSwitchLinkTA.setVisibility(View.VISIBLE);
            }
            layoutSwitchHome.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(1);
                    morePopwindow.dismiss();
                }
            });
            layoutSwitchMsg.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(3);
                    morePopwindow.dismiss();
                }
            });
            layoutSwitchMine.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(4);
                    morePopwindow.dismiss();
                }
            });
            layoutSwitchReport.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActionToComplain();
                    morePopwindow.dismiss();
                }
            });
            layoutSwitchShield.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startRequestForShieldUser();
                    morePopwindow.dismiss();
                }
            });

            layoutSwitchLinkTA.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    linkTA();
                    morePopwindow.dismiss();
                }
            });
            morePopwindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            morePopwindow.setFocusable(true);
            morePopwindow.setOutsideTouchable(true);
            morePopwindow.setBackgroundDrawable(new BitmapDrawable());
            morePopwindow.setAnimationStyle(R.style.pop_search_switch);
        }
        tvShield.setText(dialogTitle);
        tvMsgUnRead.setVisibility(tvUnRead.getVisibility());
        morePopwindow.showAsDropDown(v);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTitleLeft:
            case R.id.btnTitleLeft2:
                onBackPressed();
                break;
            case R.id.btnTitleRight:
            case R.id.btnTitleRight2:
                startActionToShare();
                break;
            case R.id.btnMore:
            case R.id.btnMore2:
                initPopMore(v);
                break;
            case R.id.ivFollow:
                if (goodsInfoBean == null || goodsInfoBean.getUserInfo() == null) {
                    return;
                }
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(PersonalPagerHomeActivity.this);
                    return;
                }
                if (userId == CommonPreference.getUserId()) {
                    Intent intent = PersonalDataActivity.createIntent(this);
                    startActivityForResult(intent, RequestCode.REQUEST_FOR_ARTICLE_DETAIL);
                } else {
                    final int followShip = goodsInfoBean.getUserInfo().fellowship;

                    if (1 == DialogManager.concernedUserDialog(this, followShip, new DialogCallback() {
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
            case R.id.layoutFans:
                if (goodsInfoBean == null || goodsInfoBean.getUserInfo() == null) {
                    return;
                }
                intent = new Intent(PersonalPagerHomeActivity.this, FansListActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, goodsInfoBean.getUserInfo().getUserId());
                startActivity(intent);
                break;
            case R.id.layoutFollow: // 关注
                if (goodsInfoBean == null || goodsInfoBean.getUserInfo() == null)
                    break;
                intent = MyFollowedActivity.createIntent(this, goodsInfoBean.getUserInfo().getUserId());
                startActivity(intent);
                break;
            case R.id.layoutLike:
                if (goodsInfoBean == null || goodsInfoBean.getUserInfo() == null) {
                    return;
                }
                intent = new Intent(PersonalPagerHomeActivity.this, LikeListActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, goodsInfoBean.getUserInfo().getUserId());
                startActivity(intent);
                break;

            case R.id.rlCommentEnter:
            case R.id.layoutGoodsRate:
                if (goodsInfoBean == null || goodsInfoBean.getUserInfo() == null) {
                    return;
                }
                intent = new Intent(this, CommentsActivity.class);
                intent.putExtra(MyConstants.COMMENT_LIST_ID, goodsInfoBean.getUserInfo().getUserId());
                intent.putExtra(MyConstants.EXTRA_USER_ID, goodsInfoBean.getUserInfo().getUserId());
                intent.putExtra(MyConstants.EXTRA_FROM_WHERE, "userId");
                intent.putExtra(MyConstants.COMMENT_LIST_URL, MyConstants.GETUSERORDERESTIMATE);
                startActivity(intent);
                break;

            case R.id.layoutImage:
                initPopTip(v);
                break;
            case R.id.ivNoticeMore:
                if (mState == SPREAD_STATE) {
                    tvNotice.setMaxLines(VIDEO_CONTENT_DESC_MAX_LINE);
                    tvNotice.requestLayout();
                    ivNoticeMore.setImageResource(R.drawable.personal_notice_more_close);
                    ivEditNotice.setVisibility(View.INVISIBLE);
                    mState = SHRINK_UP_STATE;
                } else if (mState == SHRINK_UP_STATE) {
                    tvNotice.setMaxLines(Integer.MAX_VALUE);
                    tvNotice.requestLayout();
                    ivNoticeMore.setImageResource(R.drawable.personal_notice_more_expand);
                    ivEditNotice.setVisibility(View.VISIBLE);

                    if (userId == CommonPreference.getUserId()) {
                        ivEditNotice.setVisibility(View.VISIBLE);
                    } else {
                        ivEditNotice.setVisibility(View.INVISIBLE);
                    }
                    mState = SPREAD_STATE;
                }
                break;
            case R.id.tvPersonalData:   // 店主资料
                Intent intent = new Intent(this, OwnerInformationActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_INFO, goodsInfoBean.getUserInfo());
                startActivity(intent);
                break;
            case R.id.weiBoLayoutClose:
                CommonPreference.setLongValue("lastTimeCloseWeiBoBind", System.currentTimeMillis());
                weiboBindingLayout.setVisibility(View.GONE);
                break;
            case R.id.weiBoBindText:
                BindSinaUtil bindSinaUtil = new BindSinaUtil(this);
                bindSinaUtil.bindSina();
                break;
            case R.id.contact_customer_service: // 联系爱客服
                CallCenterHelper.start(this, "", "", "");
                break;
            default:
                break;
        }
    }

    private void startActionToShare() {
        if (goodsInfoBean == null || goodsInfoBean.getUserInfo() == null) {
            return;
        }
        ShareHelper.sharePersonal(PersonalPagerHomeActivity.this, goodsInfoBean.getUserInfo());
    }

    private void startActionToComplain() {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(PersonalPagerHomeActivity.this);
            return;
        }
        intent = new Intent(PersonalPagerHomeActivity.this, ReportActivity.class);
        intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, "2");
        intent.putExtra(MyConstants.EXTRA_USER_ID, String.valueOf(userId));
        startActivity(intent);
    }

    private void initPopTip(View v) {

        if (tipPopwindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_tips, null);
            ivTips = (ImageView) view.findViewById(R.id.ivTips);
            ivTips.setBackgroundResource(R.drawable.pop_level_tips);
            tipPopwindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            tipPopwindow.setFocusable(true);
            tipPopwindow.setOutsideTouchable(true);
            // 设置pop外部点击隐藏pop
            tipPopwindow.setBackgroundDrawable(new BitmapDrawable());
            View viewRoot = view.findViewById(R.id.llContent);
            viewRoot.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tipPopwindow.dismiss();
                }
            });
        }

        tipPopwindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_EDIT_TEXT_NOTICE) {
            if (data != null && data.hasExtra(MyConstants.EXTRA_NOTICE)) {
                notice = data.getStringExtra(MyConstants.EXTRA_NOTICE);
                initNotice(notice);
            }
        } else if (requestCode == REQUEST_CODE_FOR_GET_CREDIT_SCORE) {
            if (data == null) {
                return;
            }
            setResult(RESULT_OK, data);
            finish();
        } else if (requestCode == MyConstants.REQUEST_CODE_FOR_WEB) {
            refresh();
        } else if (requestCode == MyConstants.REQUEST_CODE_FOR_NOTICE) {
            refresh();
        } else if (requestCode == RequestCode.REQUEST_FOR_ARTICLE_DETAIL) {

            userInfo = CommonPreference.getUserInfo();
            ViewUtil.setAvatar(ivHeader, userInfo.getUserIcon());

            ctvName.setData(userInfo);
        }
    }

    private void startLoading() {
        startRequestForGetUserShopDetail(LOADING);
    }

    private void refresh() {
        startRequestForGetUserShopDetail(REFRESH);
    }

    private void setBlockedView(UserInfo userInfo) {
        ViewStub vs = (ViewStub) findViewById(R.id.view_blocked_vs);
        View blockedLayout = vs.inflate();

        if (blockedLayout == null || userInfo == null)
            return;

        layoutMore.setVisibility(View.GONE);
        layoutRight.setVisibility(View.GONE);

        int state = userInfo.getStatus();

        SimpleDraweeView avatarIv = (SimpleDraweeView) blockedLayout.findViewById(R.id.avatar_iv);
        CommonTitleView nameView = (CommonTitleView) blockedLayout.findViewById(R.id.ctvName);
        TextView blockedTv = (TextView) blockedLayout.findViewById(R.id.blocked_tv);
        blockedLayout.findViewById(R.id.contact_customer_service).setOnClickListener(this);

        if (state == 3)
            blockedTv.setText("因违规该用户已被封号\n如有疑问请联系客服");
        else
            blockedTv.setText("因违规该店铺已被查封\n如有疑问请联系客服");
        
        ViewUtil.setAvatar(avatarIv, userInfo.getUserIcon());
        nameView.setData(userInfo);
    }

    private GoodsInfoBean getInfoFromJson(String json) {
        if (TextUtils.isEmpty(json))
            return null;
        GoodsInfoBean bean = ParserUtils.parserUserShopDetailData(json);
        return bean;
    }

    /**
     * 获取商铺详情
     */
    private void startRequestForGetUserShopDetail(final String extra) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", String.valueOf(userId));

        LogUtil.i("liang", "parmas:" + params.toString());

        OkHttpClientManager.postAsyn(MyConstants.GETUSERSHOPDETAIL, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (REFRESH.equals(extra)) {
                    mPtrFrame.refreshComplete();
                } else if (LOADING.equals(extra)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                }
            }

            @Override
            public void onResponse(String response) {
                if (REFRESH.equals(extra)) {
                    mPtrFrame.refreshComplete();
                } else if (LOADING.equals(extra)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                }
                LogUtil.i("liang", "商铺详情:" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);

                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {

                        GoodsInfoBean bean = getInfoFromJson(baseResult.obj);

                        if (null != bean) {
                            goodsInfoBean = bean;
                            initUserData(bean);
                            if (bean.getUserInfo().getUserId() == CommonPreference.getUserId()) {
                                CommonPreference.setUserInfo(bean.getUserInfo());
                                LoginEvent event = new LoginEvent();
                                event.isLogin = true;
                                EventBus.getDefault().post(event);
                            }
                        }

                        initFragmentPager(personViewPager, tabStrip, scrollableLayout);

                    } else if (baseResult.code == ParserUtils.RESPONSE_BLOCKED) {
                        // 用户/店铺封禁
                        GoodsInfoBean bean = getInfoFromJson(baseResult.obj);
                        UserInfo userInfo = bean == null ? null : bean.getUserInfo();
                        setBlockedView(userInfo);
                    } else {
                        CommonUtils.error(baseResult, PersonalPagerHomeActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 屏蔽&取消屏蔽用户接口
     *
     * @param
     */
    private void startRequestForShieldUser() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        if (goodsInfoBean == null) {
            return;
        }
        ProgressDialog.showProgress(PersonalPagerHomeActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", String.valueOf(goodsInfoBean.getUserInfo().getUserId()));
        final boolean isShield = goodsInfoBean.getUserInfo().getIsShield();
        if (isShield) {
            params.put("type", "2");
        } else {
            params.put("type", "1");
        }
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.SHIELDUSER, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "屏蔽:" + response);
                scrollableLayout.scrollTo(0, 0);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (isShield) {
                            ToastUtil.toast(PersonalPagerHomeActivity.this, "取消屏蔽成功");
                            goodsInfoBean.getUserInfo().setIsShield(false);
                            dialogTitle = "屏蔽";
                        } else {
                            ToastUtil.toast(PersonalPagerHomeActivity.this, "屏蔽成功");
                            goodsInfoBean.getUserInfo().setIsShield(true);
                            dialogTitle = "取消屏蔽";
                        }
                    } else {
                        CommonUtils.error(baseResult, PersonalPagerHomeActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initUserData(final GoodsInfoBean bean) {
        if (bean.getUserInfo() != null) {
            scrollableLayout.setVisibility(View.VISIBLE);
            boolean isShield = goodsInfoBean.getUserInfo().getIsShield();
            if (isShield) {
                dialogTitle = "取消屏蔽";
            } else {
                dialogTitle = "屏蔽";
            }
            userInfo = bean.getUserInfo();
            tvTitleText.setText(bean.getUserInfo().getUserName());
            scrollableLayout.setVisibility(View.VISIBLE);
            btnTitleRight.setVisibility(View.VISIBLE);
            btnTitleRight2.setVisibility(View.VISIBLE);

            ViewUtil.setAvatar(ivHeader, bean.getUserInfo().getUserIcon());

            ivHeader.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonalPagerHomeActivity.this, ImageActivity.class);
                    intent.putExtra("imageUrl", bean.getUserInfo().getUserIcon());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
            ctvName.setData(bean.getUserInfo());
            String auth = bean.getUserInfo().getStarUserTitle();
            if (!TextUtils.isEmpty(auth)) {
                tvAuth.setText(auth);
                tvAuth.setVisibility(View.VISIBLE);
            } else {
                tvAuth.setVisibility(View.INVISIBLE);
            }
            int fansCount = bean.getUserInfo().getFansCount();
            String fans = CommonUtils.getDoubleCount(fansCount, MyConstants.COUNT_FANS);
            tvFansCount.setText(fans);
            tvFollowCount.setText(bean.getUserInfo().getFocusCount() + "");
            if (userId == CommonPreference.getUserId()) {
                ivFollow.setImageResource(R.drawable.personal_edit_icon);
            } else {
                ivFollow.setWhiteData(bean.getUserInfo().fellowship);
            }

            if (bean.rateCount != null && !bean.rateCount.isEmpty()) {
                int count = 0;
                int countGoods = 0;
                if (bean.rateCount.containsKey("30")) {
                    Integer var = bean.rateCount.get("30");
                    tvComment1.setText(String.valueOf(var));
                    count += var;
                    countGoods = var;
                }

                if (bean.rateCount.containsKey("20")) {
                    Integer var = bean.rateCount.get("20");
                    tvComment2.setText(String.valueOf(var));
                }

                if (bean.rateCount.containsKey("10")) {
                    Integer var = bean.rateCount.get("10");
                    tvComment3.setText(String.valueOf(var));
                    count += var;
                }

                if (count > 0) {
                    int progress = countGoods * 100 / count;
                    tvFavorableRate.setText(progress + "%");
                } else {
                    tvFavorableRate.setText("0%");
                }

            } else {
                tvComment1.setText(String.valueOf(0));
                tvComment2.setText(String.valueOf(0));
                tvComment3.setText(String.valueOf(0));
                tvFavorableRate.setText("0%");
            }


            String lastVisitText = goodsInfoBean.lastVisitText;
            if (!TextUtils.isEmpty(lastVisitText)) {
                tvLastVisitText.setText(lastVisitText);
            }

            notice = bean.getUserInfo().getNotice();

            initNotice(notice);

            flowerLayout.setData(bean.articles, bean.getUserInfo().getUserId());

            //     Logger.e("get userId:" + bean.getUserInfo().getUserId());
            //     Logger.e("get myId:" + CommonPreference.getUserId());
            long time = CommonPreference.getLongValue("lastTimeCloseWeiBoBind", 0L);

            //      Logger.e("get time:" + time);
            if (time == 0 || System.currentTimeMillis() - time > 3 * 24 * 3600 * 1000L) {
                freshWeiBoBind(bean.getUserInfo());
            }
        }
    }

    private void freshWeiBoBind(UserInfo userInfo) {
        if (userInfo.getUserId() == CommonPreference.getUserId()) {
            if (!userInfo.isBindWeibo()) {
                weiboBindingLayout.setVisibility(View.VISIBLE);
            } else {
                weiboBindingLayout.setVisibility(View.GONE);
            }
        } else {
            weiboBindingLayout.setVisibility(View.GONE);
        }
    }

    private void initNotice(String notice) {
        if (!TextUtils.isEmpty(notice)) {
            tvNotice.setText(notice);
            if (userId == CommonPreference.getUserId()) {
                ivEditNotice.setVisibility(View.VISIBLE);
                ivEditNotice.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (goodsInfoBean == null || goodsInfoBean.getUserInfo() == null) {
                            return;
                        }
                        Intent intent = new Intent(PersonalPagerHomeActivity.this, OwnerNoticeEditActivity.class);
                        intent.putExtra(MyConstants.EXTRA_NOTICE_TEXT, PersonalPagerHomeActivity.this.notice);
                        startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_EDIT_TEXT_NOTICE);
                    }
                });

            } else {
                ivEditNotice.setVisibility(View.GONE);
            }
        } else {
            if (userId == CommonPreference.getUserId()) {
                tvNotice.setText("快来编辑自己的店铺公告吧...");
                ivEditNotice.setVisibility(View.VISIBLE);
                ivEditNotice.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (goodsInfoBean == null || goodsInfoBean.getUserInfo() == null) {
                            return;
                        }
                        intent = new Intent(PersonalPagerHomeActivity.this, OwnerNoticeEditActivity.class);
                        startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_EDIT_TEXT_NOTICE);
                    }
                });

            } else {
                tvNotice.setText("店主太忙，忘记写简介了");
                ivEditNotice.setVisibility(View.GONE);
            }
        }

//		tvNotice.setSingleLine(false);
//		ViewTreeObserver vto2 = tvNotice.getViewTreeObserver();
//		vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                tvNotice.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                Layout layout = tvNotice.getLayout();
//                if(layout != null) {
//                    int lines = layout.getLineCount();
//                    if(lines == 1) {
//                        ivNoticeMore.setVisibility(View.GONE);
//                    } else {
//                        tvNotice.setMaxLines(VIDEO_CONTENT_DESC_MAX_LINE);
//                        ivNoticeMore.setVisibility(View.VISIBLE);
//                        ivNoticeMore.setImageResource(R.drawable.personal_notice_more_close);
//                    }
//                }
//            }
//        });
        tvNotice.setMaxLines(Integer.MAX_VALUE);
        tvNotice.post(new Runnable() {

            @Override
            public void run() {
                if (tvNotice.getLineCount() > 1) {
                    tvNotice.setMaxLines(1);
                    ivNoticeMore.setVisibility(View.VISIBLE);
                    ivNoticeMore.setImageResource(R.drawable.personal_notice_more_close);
                    ivEditNotice.setVisibility(View.INVISIBLE);
                } else {
                    tvNotice.setMaxLines(1);
                    ivNoticeMore.setVisibility(View.INVISIBLE);
                    if (userId == CommonPreference.getUserId()) {
                        ivEditNotice.setVisibility(View.VISIBLE);
                    } else {
                        ivEditNotice.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });
    }

    /**
     * 关注
     */
    private void startRequestForConcernedUser(String type, final View view) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        if (goodsInfoBean == null) {
            return;
        }
        ProgressDialog.showProgress(PersonalPagerHomeActivity.this);
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(goodsInfoBean.getUserInfo().getUserId()));
        params.put("type", type);

        final int fellowship = goodsInfoBean.getUserInfo().fellowship;

        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.CONCERNEDUSER, params, new StringCallback() {

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
                            goodsInfoBean.getUserInfo().fellowship = 2;
                        } else if (fellowship == 2) {
                            ivFollow.setWhiteData(1);
                            goodsInfoBean.getUserInfo().fellowship = 1;
                        } else if (fellowship == 3) {
                            ivFollow.setWhiteData(4);
                            goodsInfoBean.getUserInfo().fellowship = 4;
                        } else if (fellowship == 4) {
                            ivFollow.setWhiteData(3);
                            goodsInfoBean.getUserInfo().fellowship = 3;
                        }
                    } else {
                        if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
                            CommonUtils.checkAccess(PersonalPagerHomeActivity.this);
                        }
                        ToastUtil.toast(PersonalPagerHomeActivity.this, baseResult.msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (position >= 0 && type != -1) {
            Intent intent = new Intent();
            intent.putExtra("position", position);
            intent.putExtra("type", type);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    public void refreshSinaUI(String unionId) {
        userInfo.setBindWeibo(true);
        userInfo.setWeiboUserId(unionId);
        CommonPreference.setUserInfo(userInfo);
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(CommonPreference.PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putString(CommonPreference.SINA_UID, unionId).apply();
        freshWeiBoBind(userInfo);
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
        tvUnRead.setVisibility(IMUtils.hasUnread() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}


