package com.huapu.huafen.presenter.impl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.ShopkeepersData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.ArticleListFragment;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.presenter.ListPresenter;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.views.HLoadingStateView;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * 商品列表
 * Created by dengbin on 17/12/15.
 */
public class ArticleListPresenterImpl implements ListPresenter {
    private ArticleListFragment mFragment;
    private int type;

    public ArticleListPresenterImpl(ArticleListFragment fragment) {
        this.mFragment = fragment;
        this.type = mFragment.getType();
    }

    @Override
    public void getList(final String extra) {
        if (!CommonUtils.isNetAvaliable(mFragment.getContext())) {
            mFragment.toast("请检查网络连接");
            return;
        }

        if (TextUtils.equals(BaseFragment.LOADING, extra)) {
            mFragment.setLoadingState(HLoadingStateView.State.LOADING);
        }

        Map<String, String> params = mFragment.getParams();

        OkHttpClientManager.postAsyn(MyConstants.FOLLOWING_POEMS, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                switch (extra) {
                    case BaseFragment.LOADING:
                        mFragment.setLoadingState(HLoadingStateView.State.COMPLETE);
                        break;
                    case BaseFragment.REFRESH:
                        mFragment.onRefreshComplete();
                        break;
                    case BaseFragment.LOAD_MORE:

                        break;
                }
            }

            @Override
            public void onResponse(String response) {
                try {
                    if (extra.equals(BaseFragment.LOADING)) {
                        mFragment.setLoadingState(HLoadingStateView.State.COMPLETE);
                    } else if (TextUtils.equals(extra, BaseFragment.REFRESH)) {
                        mFragment.onRefreshComplete();
                    } else if (extra.equals(BaseFragment.LOAD_MORE)) {

                    }
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            ShopkeepersData data = JSON.parseObject(baseResult.obj, ShopkeepersData.class);
                            mFragment.setData(data, extra);
                        }
                    } else {
                        CommonUtils.error(baseResult, mFragment.getActivity(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
