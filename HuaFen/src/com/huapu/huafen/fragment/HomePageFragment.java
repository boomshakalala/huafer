package com.huapu.huafen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.HomePageAdapter;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.GoodsListResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.itemdecoration.SpaceItemDecoration;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lalo on 2016/11/27.
 */
public class HomePageFragment extends BaseFragment implements LoadMoreWrapper.OnLoadMoreListener {

    private final static String REFRESH="refresh";
    private final static String LOAD_MORE="load_more";
    private String type ;
    private RecyclerView recyclerView;
    private HomePageAdapter adapter;
    private long userId;
    private int page;
    private View loadMoreView;

    public static HomePageFragment newInstance(Bundle bundle){
        HomePageFragment tabFragment = new HomePageFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        Bundle data = getArguments();
        userId = data.getLong("userId");
        type = data.getString("type");
        return inflater.inflate(R.layout.home_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager manager  = new GridLayoutManager(getActivity(),2);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(5));
        loadMoreView =LayoutInflater.from(getActivity()).inflate(R.layout.load_layout, recyclerView, false);
        adapter  = new HomePageAdapter(this);
        View viewEmpty = LayoutInflater.from(getActivity()).inflate(R.layout.view_empty_image, recyclerView,false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_personal_home);
        adapter.setEmptyView(viewEmpty);
        adapter.setOnLoadMoreListener(this);
        recyclerView.setAdapter(adapter.getWrapperAdapter());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startRequestForList(REFRESH);
    }


    public void refresh(){
        page = 0;
        startRequestForList(REFRESH);
    }


    /**
     * 用户闲置的宝贝接口(没卖出的)
     * @param
     */
    private void startRequestForList(final String extra){
        if(!CommonUtils.isNetAvaliable(getActivity())) {
            ToastUtil.toast(getActivity(), "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", String.valueOf(userId));
        params.put("type", type);
        params.put("page", String.valueOf(page));
        OkHttpClientManager.postAsyn(MyConstants.GETUSERRELEASEGOODSPAGELIST, params,

                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            LogUtil.i("liang", "用户闲置的宝贝接口(没卖出的):"+response);
                            JsonValidator validator = new JsonValidator();
                            boolean isJson = validator.validate(response);
                            if(!isJson) {
                                return;
                            }
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if(!TextUtils.isEmpty(baseResult.obj)) {
                                    GoodsListResult result = JSON.parseObject(baseResult.obj, GoodsListResult.class);
                                    initData(result,extra);

                                }
                            } else {
                                CommonUtils.error(baseResult, getActivity(), "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


    private void initData(GoodsListResult result,String extra){
        if(result.page==0){//分页完毕
            adapter.setLoadMoreView(null);
        }else{
            adapter.setLoadMoreView(loadMoreView);
        }
        page++;
        List<GoodsInfo> list = result.getGoodsList();

        if(REFRESH.equals(extra)){
            adapter.setData(list);
        }else if(LOAD_MORE.equals(extra)){
            if(list == null){
                list = new ArrayList<>();
            }
            adapter.addData(list);
        }


    }


    @Override
    public void onLoadMoreRequested() {
        startRequestForList(LOAD_MORE);
    }
}
