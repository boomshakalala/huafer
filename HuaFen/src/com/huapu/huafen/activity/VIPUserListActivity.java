package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.VUserAdapter;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.User;
import com.huapu.huafen.beans.UserListResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.ClassBannerView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2016/12/3.
 */
public class VIPUserListActivity extends BaseActivity
                                                        implements
                                                        PullToRefreshBase.OnRefreshListener<RecyclerView>,
                                                        LoadMoreWrapper.OnLoadMoreListener{

    private PullToRefreshRecyclerView ptrRecyclerRecommendUser;
    private HLoadingStateView loadingStateView;
    private VUserAdapter adapter;
    private int page = 0;
    private View loadMoreLayout;
    private String userLevel;

    //广告轮播
    private ClassBannerView bannerView;
    private View header;
    private View layoutBanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_user_list_layout);
        if (getIntent().hasExtra(MyConstants.EXTRA_TARGET_USER_LEVEL)) {
            userLevel = getIntent().getStringExtra(MyConstants.EXTRA_TARGET_USER_LEVEL);
        }
        initView();
        initBanner();
        startLoading();
    }

    /**
     * 推荐用户列表
     */

    private void initView() {

        setTitleString("花粉儿VIP");
        loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
        ptrRecyclerRecommendUser = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecyclerRecommendUser);
        CommonUtils.buildPtr(ptrRecyclerRecommendUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecyclerRecommendUser.getRefreshableView().setLayoutManager(linearLayoutManager);
        adapter = new VUserAdapter(this);
        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout,
                ptrRecyclerRecommendUser.getRefreshableView(), false);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, ptrRecyclerRecommendUser.getRefreshableView(),false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_vip_user);
        adapter.setEmptyView(viewEmpty);
        ptrRecyclerRecommendUser.setAdapter(adapter.getWrapperAdapter());

        ptrRecyclerRecommendUser.setOnRefreshListener(this);
        adapter.setOnLoadMoreListener(this);

    }

    @Override
    public void onTitleBarDoubleOnClick() {
        if(ptrRecyclerRecommendUser != null) {
            ptrRecyclerRecommendUser.getRefreshableView().scrollToPosition(0);
        }
    }

    private BannerData banners ;
    private ArrayList<CampaignBanner> publicBanners;
    private void initBanner() {
        if("3".equals(userLevel)){
            banners = CommonPreference.getStarUserBanner();
        }else if("2,3".equals(userLevel)){
            banners = CommonPreference.getVipUserBanner();
        }
        if(banners != null) {
            publicBanners = banners.getBanners();
            if(!ArrayUtil.isEmpty(publicBanners)) {
                header = LayoutInflater.from(this).inflate(R.layout.view_headview_star_banner, ptrRecyclerRecommendUser.getRefreshableView(),false);
                bannerView = (ClassBannerView) header.findViewById(R.id.bannerView);
                layoutBanner = header.findViewById(R.id.layoutBanner);
                int width = CommonUtils.getScreenWidth();
                int height = CommonUtils.getMyHeight();
                LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                        width, height);
                // 设置banner高度
                layoutBanner.setLayoutParams(localLayoutParams);
                bannerView.setBanners(publicBanners);
                adapter.addHeaderView(header);
                CommonUtils.setAutoLoop(banners, bannerView);
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {//下拉刷新
        refresh();
    }

    @Override
    public void onLoadMoreRequested() {//加载更多
        loadMore();
    }

    private void startLoading(){
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        page=0;
        startRequestForRecommendUserList(LOADING);
    }

    private void refresh(){
        page=0;
        startRequestForRecommendUserList(REFRESH);
    }

    private void loadMore(){
        startRequestForRecommendUserList(LOAD_MORE);
    }

    /**
     * 获取粉丝列表
     * @param
     */
    private void startRequestForRecommendUserList(final String extra){
        if(!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userLevel",userLevel);
        params.put("page", page+"");
        params.put("orderBy", "1");
        params.put("pageNum","12");
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.RECOMMEND_USER_LIST, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if(extra.equals(LOADING)){
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                }else if(extra.equals(REFRESH)){
                    ptrRecyclerRecommendUser.onRefreshComplete();
                }
            }

            @Override
            public void onResponse(String response) {
                if(extra.equals(LOADING)){
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                }else if(extra.equals(REFRESH)){
                    ptrRecyclerRecommendUser.onRefreshComplete();
                }
                try {
                    LogUtil.i("liang", "followingList:"+response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if(!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        UserListResult result = JSON.parseObject(baseResult.obj, UserListResult.class);
                        initData(result,extra);
                    } else {
                        CommonUtils.error(baseResult, VIPUserListActivity.this, "");
                    }
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }
            }

        });
    }

    private void initData(UserListResult result,String extra){
        if(result.page==0){
            adapter.setLoadMoreView(null);
        }else{
            adapter.setLoadMoreView(loadMoreLayout);
        }
        page++;

        List<User> users = result.getUsers();
        if(extra.equals(LOADING)){
            adapter.setData(users);
        }else if(extra.equals(REFRESH)){
            adapter.setData(users);
        }else if(LOAD_MORE.equals(extra)){
            if(users == null){
                users = new ArrayList<>();
            }
            adapter.addAll(users);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL){
                if(data!=null){
                    int position = data.getIntExtra("position", -1);
                    int type = data.getIntExtra("type",0);
                    if(position!=-1&&type!=0){
                        adapter.updateFollowState(position,type);
                    }
                }
            }
        }
    }
}
