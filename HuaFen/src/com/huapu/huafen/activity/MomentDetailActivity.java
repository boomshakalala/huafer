package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.EasyArticleAdapter;
import com.huapu.huafen.adapter.HPCommentAdapter;
import com.huapu.huafen.adapter.RecArticleAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.EasyArticleContentBean;
import com.huapu.huafen.beans.EasyArticleDetail;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.HPCommentsResult;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ShareDialog;
import com.huapu.huafen.events.RefreshEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.looper.IndicatorView;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ShareUtils;
import com.huapu.huafen.views.ArticleBottomOperateLayout;
import com.huapu.huafen.views.CommentContainer;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.FollowImageView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.ShareArticleLayout;
import com.huapu.huafen.views.TagsContainerNew;
import com.huapu.huafen.views.TitleBarNew;
import com.squareup.okhttp.Request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import de.greenrobot.event.EventBus;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;


/**
 * 简易花语
 * Created by qwe on 17/4/25.
 */
public class MomentDetailActivity extends BaseActivity {
    private String TAG = "MomentDetailActivity";
    public static final String MOMENT_ID = "MOMENT_ID";
    public static final int COMMENT = 100;
    /**
     * 简单花语类型
     */
    private static final int moment_type = 8;
    private static final int REQUEST_EDIT = 130;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.leaveMessageBottom)
    TextView leaveMessageBottom;
    @BindView(R.id.saveArticle)
    TextView saveArticle;
    @BindView(R.id.llContainer)
    LinearLayout llContainer;
    @BindView(R.id.titleBar)
    TitleBarNew titleBar;
    @BindView(R.id.bottomOperateLayout)
    ArticleBottomOperateLayout bottomOperateLayout;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;
    private int screenWidth;
    private UserData userBean;
    private EasyArticleDetail.ObjBean.MomentBean momentBean;
    private EasyArticleDetail.ObjBean.CountBean countBean;
    private View headView;
    private View titleImageView;
    private EasyArticleAdapter easyArticleAdapter;
    private ViewPager viewPager;
    private SimpleDraweeView imageView;
    private TextView userName, dateView, viewText, leaveMessage;
    private TextView titleUserName;
    private SimpleDraweeView circleImage;
    private ShareArticleLayout articleLayout;
    private PopupWindow morePopWindow;
    private View layoutSwitchHome, layoutSwitchMsg, layoutSwitchMine, layoutSwitchReport, layoutSwitchShield;
    private TextView tvReport, tvShield, tvMore;
    private ImageView ivReport;
    private String momentId;
    private IndicatorView indicatorView;
    private TextView tvMsgUnRead;
    private CommentContainer commentContainer;
    private FollowImageView addAttention;
    private RecyclerView rlArticle;
    private List<FloridData.TitleMedia> titleMediaList;
    private CommonTitleView commonTitleView;
    private boolean firstIn = true;
    private long myUserId;
    private RecArticleAdapter recArticleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easyarticle_detail_new);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        if (getIntent().hasExtra(MOMENT_ID)) {
            momentId = getIntent().getStringExtra(MOMENT_ID);
        }
        initView();
        getNetData();
    }

    private void initView() {
        myUserId = CommonPreference.getUserId();
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        titleBar.setBackgroundAlpha(0);

        titleBar.getBtnTitleRight().setImageResource(R.drawable.personal_title_more);
        titleBar.getBtnTitleBarRight2().setImageResource(R.drawable.personal_title_share);
        titleBar.getBtnTitleLeft().setImageResource(R.drawable.personal_title_back);

        titleBar.getBtnTitleBarRight2().setVisibility(VISIBLE);
        titleBar.getBtnTitleRight().setVisibility(VISIBLE);

        titleBar.setOnRightButton2ClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (momentBean == null
                        || TextUtils.isEmpty(momentBean.getTitle())
                        || TextUtils.isEmpty(momentBean.getMedia().get(0).url)) {
                    return;
                }
                String content;
                if (TextUtils.isEmpty(momentBean.getContent())) {
                    content = CommonPreference.getUserInfo().getUserName() + "在花粉儿APP分享了很多有意思的东西，大家快来看看吧！" + ShareUtils.DOWN_LOAD;
                } else {
                    content = momentBean.getContent() + ShareUtils.DOWN_LOAD;
                }
                String title = momentBean.getTitle();
                content = title + ":" + (momentBean.getContent().length() > 22 ? momentBean.getContent().substring(0, 22) + "..." : momentBean.getContent()) + ShareUtils.DOWN_LOAD;
                String adds = momentBean.getContent();
                String feature = "article";
                ShareDialog shareDialog = new ShareDialog(MomentDetailActivity.this, title, content, momentBean.getMedia().get(0).url, MyConstants.SHARE_MOMENT + momentId, feature, adds);
                shareDialog.show();
            }
        });

        titleBar.setOnRightButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initPopMore(v);
            }
        });

        screenWidth = CommonUtils.getScreenWidth();

        /**
         * header view
         */
        headView = LayoutInflater.from(this).inflate(R.layout.layout_header_easyarticle, null);
        viewPager = ButterKnife.findById(headView, R.id.viewPager);
        indicatorView = ButterKnife.findById(headView, R.id.indicator);
        commonTitleView = ButterKnife.findById(headView, R.id.ctvName);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp.bottomMargin = CommonUtils.dp2px(10.0f);
        indicatorView.setLayoutParams(lp);
        imageView = ButterKnife.findById(headView, R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userBean != null) {
                    Intent intent = new Intent(MomentDetailActivity.this, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, userBean.getUserId());
                    startActivity(intent);
                }
            }
        });
        userName = ButterKnife.findById(headView, R.id.userName);
        dateView = ButterKnife.findById(headView, R.id.dateView);
        viewText = ButterKnife.findById(headView, R.id.viewText);
        leaveMessage = ButterKnife.findById(headView, R.id.leaveMessage);
        addAttention = ButterKnife.findById(headView, R.id.addAttention);
        addAttention.setOnClickListener(this);

        /**
         * bottom view
         */
        articleLayout = new ShareArticleLayout(this);


        /**
         * title layout
         */
        titleImageView = LayoutInflater.from(this).inflate(R.layout.layout_title_image, null);
        titleUserName = ButterKnife.findById(titleImageView, R.id.titleUserName);
        circleImage = ButterKnife.findById(titleImageView, R.id.circleImage);
        titleBar.setTitle(titleImageView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        easyArticleAdapter = new EasyArticleAdapter();
        recyclerView.setAdapter(easyArticleAdapter.getWrapperAdapter());
        easyArticleAdapter.addHeaderView(headView);

        View footerView = LayoutInflater.from(this).inflate(R.layout.article_detail_footer, recyclerView, false);
        commentContainer = ButterKnife.findById(footerView, R.id.commentContainer);
        commentContainer.setTargetType(moment_type);
//        commentContainer.setGoodsOwnerId(myUserId);
        //推荐花语
        tvMore = ButterKnife.findById(footerView, R.id.tv_more);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ArticleSquareActivity.class);
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });
        rlArticle = ButterKnife.findById(footerView, R.id.rl_rec_article);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        llManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlArticle.setLayoutManager(llManager);
        recArticleAdapter = new RecArticleAdapter(this);
        rlArticle.setAdapter(recArticleAdapter);

        easyArticleAdapter.addFootView(footerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lHeight = CommonUtils.getScreenWidth();
                titleBar.setAlphaInRecyclerView(dy, lHeight);
            }
        });
    }

    private void initPopMore(View v) {
        if (morePopWindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_more, null);
            layoutSwitchHome = view.findViewById(R.id.layoutSwitchHome);
            layoutSwitchMsg = view.findViewById(R.id.layoutSwitchMsg);
            layoutSwitchMine = view.findViewById(R.id.layoutSwitchMine);
            layoutSwitchReport = view.findViewById(R.id.layoutSwitchReport);
            layoutSwitchShield = view.findViewById(R.id.layoutSwitchShield);
            layoutSwitchShield.setVisibility(VISIBLE);
            tvReport = (TextView) view.findViewById(R.id.tvReport);
            tvShield = (TextView) view.findViewById(R.id.tvShield);
            ivReport = (ImageView) view.findViewById(R.id.ivReport);
            ImageView ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
            /**
             * layoutSwitchReport,layoutSwitchShield
             */
            tvMsgUnRead = (TextView) view.findViewById(R.id.tvMsgUnRead);
            long myUserId = CommonPreference.getUserId();
            long userId = 0L;
            if (userBean != null) {
                userId = userBean.getUserId();
            }
            final boolean flag = myUserId > 0 && userId > 0 && myUserId == userId;
            if (flag) {
                layoutSwitchMsg.setVisibility(VISIBLE);

                layoutSwitchHome.setVisibility(View.GONE);
                layoutSwitchMine.setVisibility(View.GONE);
                layoutSwitchReport.setVisibility(VISIBLE);
                layoutSwitchShield.setVisibility(VISIBLE);
                ivReport.setImageResource(R.drawable.pop_more_edit);
                ivDelete.setImageResource(R.drawable.pop_more_delete);
                tvShield.setText("删除");
                tvReport.setText("编辑");
            } else {
                ivReport.setImageResource(R.drawable.pop_more_report);
                layoutSwitchHome.setVisibility(View.VISIBLE);
                layoutSwitchMine.setVisibility(View.VISIBLE);
                layoutSwitchReport.setVisibility(View.VISIBLE);
                layoutSwitchShield.setVisibility(GONE);
            }

            layoutSwitchHome.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(1);
                    morePopWindow.dismiss();
                }
            });
            layoutSwitchMsg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(3);
                    morePopWindow.dismiss();
                }
            });
            layoutSwitchMine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(4);
                    morePopWindow.dismiss();
                }
            });
            layoutSwitchReport.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (flag) {
                        Intent intent = new Intent(MomentDetailActivity.this, EasyArticleActivity.class);
                        ArrayList<ImageItem> arrayList = new ArrayList<>();
                        for (FloridData.TitleMedia titleMedia : titleMediaList) {
                            ImageItem imageItem = new ImageItem();
                            imageItem.titleMedia = titleMedia;
                            imageItem.imagePath = "";
                            arrayList.add(imageItem);
                        }
                        intent.putExtra(MOMENT_ID, momentId);
                        if (!TextUtils.isEmpty(momentBean.getTitle())) {
                            intent.putExtra("title", momentBean.getTitle());
                        }

                        if (!TextUtils.isEmpty(momentBean.getContent())) {
                            intent.putExtra("content", momentBean.getContent());
                        }

                        if (!TextUtils.isEmpty(momentBean.getCategoryId())) {
                            intent.putExtra("categoryId", momentBean.getCategoryId());
                        }

                        if (!TextUtils.isEmpty(momentBean.getCategoryName())) {
                            intent.putExtra("categoryName", momentBean.getCategoryName());
                        }


                        intent.putExtra(MyConstants.MOMENT_EDIT, arrayList);
                        startActivityForResult(intent, REQUEST_EDIT);
                    } else {
                        startActionToComplain();
                    }

                    morePopWindow.dismiss();
                }
            });
            layoutSwitchShield.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ArrayMap<String, String> arrayMap = new ArrayMap<>();
                    arrayMap.put("momentId", momentId);
                    OkHttpClientManager.postAsyn(MyConstants.MOMENT_DELETE, arrayMap, new OkHttpClientManager.StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(String response) {
                            JsonValidator validator = new JsonValidator();
                            boolean isJson = validator.validate(response);
                            if (!isJson) {
                                return;
                            }
                            BaseResult baseResult = JSON.parseObject(response,
                                    BaseResult.class);
                            if (baseResult.code == 200) {
                                RefreshEvent event = new RefreshEvent();
                                event.refresh = true;
                                EventBus.getDefault().post(event);

                                toast("删除成功！");
                                setResult(RESULT_OK);
                                MomentDetailActivity.this.finish();
                            } else {
                                toast(baseResult.msg);
                            }
                        }
                    });
                    morePopWindow.dismiss();
                }
            });
            morePopWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            morePopWindow.setFocusable(true);
            morePopWindow.setOutsideTouchable(true);
            morePopWindow.setBackgroundDrawable(new BitmapDrawable());
            morePopWindow.setAnimationStyle(R.style.pop_search_switch);
        }
        tvMsgUnRead.setVisibility(titleBar.getMoreBtnBadgeVisibility() ? View.VISIBLE : View.GONE);
        morePopWindow.showAsDropDown(v);
    }

    private void startActionToComplain() {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(this);
            return;
        }
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, moment_type);
        intent.putExtra(MyConstants.EXTRA_USER_ID, String.valueOf(momentBean.getMomentId()));
        startActivity(intent);
    }

    private void actionToMineFragment(int selectFragment) {
        Intent intent = new Intent(MomentDetailActivity.this, MainActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, selectFragment);
        startActivity(intent);
    }

    private void getNetData() {
        final ArrayMap<String, String> arrayMap = new ArrayMap<>(1);
        arrayMap.put("momentId", momentId);
        OkHttpClientManager.postAsyn(MyConstants.EASY_ARTICLE_DETAIL, arrayMap, new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
            }

            @Override
            public void onResponse(String response) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                try {

                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    LogUtil.i(TAG, response.toString());
                    EasyArticleDetail articleDetail = JSON.parseObject(response, EasyArticleDetail.class);
                    userBean = articleDetail.getObj().getUser();
                    momentBean = articleDetail.getObj().getMoment();
                    countBean = articleDetail.getObj().getCount();
                    titleMediaList = momentBean.getMedia();
                    commonTitleView.setData(userBean);
                    recArticleAdapter.setData(articleDetail.getObj().recPoems);
                    if (CommonPreference.getUserId() == userBean.getUserId()) {
                        addAttention.setVisibility(GONE);
                    } else {
                        addAttention.setVisibility(VISIBLE);
                        int fellowship = userBean.getFellowship();
                        //1 无关系 2 已关注 3 被关注 4 互相关注
                        addAttention.setPinkData(fellowship);

                    }
                    userName.setText(userBean.getUserName());
                    titleUserName.setText(userBean.getUserName());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    dateView.setText(simpleDateFormat.format(momentBean.getUpdatedAt()));

                    long userId = userBean.getUserId();

                    if (myUserId > 0 && userId > 0 && myUserId == userId) {
                        viewText.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(countBean.getPv())) {
                            viewText.setText(String.valueOf(countBean.getPv()));
                        } else {
                            viewText.setText("0");
                        }
                    } else {
                        viewText.setVisibility(View.GONE);
                    }
                    //                    String desc = String.format(getString(R.string.msg_count), countDesc);
                    int comment = 0;
                    int replyCount = 0;
                    HPCommentsResult result = articleDetail.getObj().comment;
                    EasyArticleDetail.ObjBean.CountBean count = articleDetail.getObj().getCount();
                    try {
                        if (result != null) {
                            comment = result.getCount();
                        }
                        if (count != null) {
                            replyCount = Integer.parseInt(count.reply);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    int msgCount = comment + replyCount;
                    String countDesc = CommonUtils.getIntegerCount(msgCount, MyConstants.COUNT_COMMENT);
//                    if (!TextUtils.isEmpty(countBean.getComment())) {
                    if (!TextUtils.isEmpty(countDesc)) {
                        leaveMessage.setText(countDesc);
                    } else {
                        leaveMessage.setText("0");
                    }
                    circleImage.setImageURI(userBean.getAvatarUrl());

                    ArrayMap<String, Object> arrayMapBottom = new ArrayMap<>();
                    arrayMapBottom.put("momentId", momentBean.getMomentId());
                    arrayMapBottom.put("moment_type", moment_type);
                    arrayMapBottom.put("isLiked", momentBean.isLiked());
                    arrayMapBottom.put("isCollected", momentBean.isCollected());
                    arrayMapBottom.put("save_count", countBean.getCollection());
                    arrayMapBottom.put("prize_count", countBean.getLike());
                    arrayMapBottom.put("comment_count", countBean.getComment());
                    arrayMapBottom.put("comment_count", countDesc);
                    arrayMapBottom.put("isComment", articleDetail.getObj().getCommentable());
                    bottomOperateLayout.setData(MomentDetailActivity.this, arrayMapBottom);
                    articleLayout.setData(momentBean.getTitle(), momentBean.getContent(), momentBean.getMedia().get(0).url, MyConstants.SHARE_MOMENT + momentId);
                    List<EasyArticleContentBean> beanList = new ArrayList<>();
                    EasyArticleContentBean bean = new EasyArticleContentBean();
                    bean.title = momentBean.getTitle();
                    bean.content = momentBean.getContent();
                    beanList.add(bean);
                    easyArticleAdapter.setData(beanList);

                    imageView.setImageURI(userBean.getAvatarUrl());
                    commentContainer.setGoodsOwnerId(userBean.getUserId());
                    commentContainer.setCommentAble(articleDetail.getObj().getCommentable());
                    commentContainer.setTargetId(Long.parseLong(momentId));
                    commentContainer.setData(articleDetail.getObj().comment, articleDetail.getObj().getCount());
                    commentContainer.setOnHandleHPCommentAdapterListener(new HPCommentAdapter.OnHandleHPCommentAdapterListener() {

                        @Override
                        public void onDelete() {
                            getNetData();
                        }
                    });
                    final List<FloridData.TitleMedia> mediaBeanList = momentBean.getMedia();

                    final int defaultHeight = screenWidth * mediaBeanList.get(0).height / mediaBeanList.get(0).width;
                    ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                    params.height = defaultHeight;
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    viewPager.setLayoutParams(params);
                    indicatorView.setCount(mediaBeanList.size());
                    viewPager.setAdapter(new PagerAdapter() {
                        @Override
                        public int getCount() {
                            return mediaBeanList.size();
                        }

                        @Override
                        public boolean isViewFromObject(View view, Object object) {
                            return view == object;
                        }

                        @Override
                        public Object instantiateItem(ViewGroup container, int position) {
                            final SimpleDraweeView imageViewNew = new SimpleDraweeView(container.getContext());

                            ImageLoader.loadImage(imageViewNew, mediaBeanList.get(position).url);

                            if (mediaBeanList.size() == 1) {
                                ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                                params.height = screenWidth * mediaBeanList.get(position).height / mediaBeanList.get(position).width;
                                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                imageViewNew.setLayoutParams(params);
                            }

                            FrameLayout frameLayout = new FrameLayout(container.getContext());
                            frameLayout.addView(imageViewNew);
                            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.gallery_edit_new, null, false);
                            final TagsContainerNew tagsContainer = (TagsContainerNew) view.findViewById(R.id.tagsContainer);
                            FloridData.TitleMedia media = mediaBeanList.get(position);
                            tagsContainer.setData(media);
                            tagsContainer.setAspectRatio((media.width / media.height) < 0.75f ? 0.75f : (media.width / media.height));
                            frameLayout.addView(view);
                            container.addView(frameLayout);
                            return frameLayout;
                        }

                        @Override
                        public void destroyItem(ViewGroup container, int position, Object object) {
                            container.removeView((View) object);
                        }
                    });
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            if (position == mediaBeanList.size() - 1) {
                                return;
                            }
                            int currentCalHeight = screenWidth * mediaBeanList.get(position).height / mediaBeanList.get(position).width;
                            int nextCalHeight = screenWidth * mediaBeanList.get(position + 1).height / mediaBeanList.get(position + 1).width;
                            int height = (int) ((currentCalHeight == 0 ? defaultHeight : currentCalHeight)
                                    * (1 - positionOffset) +
                                    (nextCalHeight == 0 ? defaultHeight : nextCalHeight)
                                            * positionOffset);

                            ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                            params.height = height;
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            viewPager.setLayoutParams(params);

                        }

                        @Override
                        public void onPageSelected(int position) {
                            indicatorView.setPosition(position);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    if (firstIn) {
                        recyclerView.scrollToPosition(0);
                        firstIn = false;
                    }

                } catch (NullPointerException e) {
                    Log.e("catch", e.getMessage(), e);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == COMMENT) {
                getNetData();
            } else if (requestCode == MyConstants.REQUEST_CODE_COMMENT) {
                getNetData();
            } else if (requestCode == PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL) {
                getNetData();
            } else if (requestCode == REQUEST_EDIT) {
                initView();
                getNetData();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAttention:

                final int followShip = userBean.getFellowship();

                if (1 == DialogManager.concernedUserDialog(this, followShip, new DialogCallback() {
                    @Override
                    public void Click() {
                        // 取消关注
                        startRequestForConcernedUser(followShip, "2");
                    }
                })) {
                    // 关注
                    startRequestForConcernedUser(followShip, "1");
                }
                break;
        }
    }

    private void startRequestForConcernedUser(final int followShip, String type) {
        ArrayMap<String, String> attentionParams = new ArrayMap<>();
        attentionParams.put("userId", String.valueOf(userBean.getUserId()));
        attentionParams.put("type", type);

        addAttention.setEnabled(false);

        OkHttpClientManager.postAsyn(MyConstants.CONCERNEDUSER, attentionParams, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                addAttention.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                addAttention.setEnabled(true);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        //1 无关系 2 已关注 3 被关注 4 互相关注
                        if (followShip == 1) {
                            addAttention.setPinkData(2);
                            userBean.setFellowship(2);
                        } else if (followShip == 2) {
                            addAttention.setPinkData(1);
                            userBean.setFellowship(1);
                        } else if (followShip == 3) {
                            addAttention.setPinkData(4);
                            userBean.setFellowship(4);
                        } else if (followShip == 4) {
                            addAttention.setPinkData(3);
                            userBean.setFellowship(3);
                        }
                    } else {
                        if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
                            CommonUtils.checkAccess(MomentDetailActivity.this);
                        } else {
                            CommonUtils.error(baseResult, MomentDetailActivity.this, "");
                        }
                    }
                } catch (Exception e) {
                    Log.e("catch", e.getMessage(), e);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
