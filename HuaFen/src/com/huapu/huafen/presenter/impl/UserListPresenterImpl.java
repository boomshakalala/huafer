package com.huapu.huafen.presenter.impl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserListResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.fragment.UserListFragment;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.model.UserListModel;
import com.huapu.huafen.model.impl.UserListModelImpl;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.presenter.ListPresenter;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * 用户列表
 * Created by dengbin on 17/12/15.
 */
public class UserListPresenterImpl implements ListPresenter {
    private UserListFragment mFragment;
    private UserListModel mModel;
    private int type;

    public UserListPresenterImpl(UserListFragment fragment) {
        this.mFragment = fragment;
        this.type = mFragment.getType();
        this.mModel = new UserListModelImpl();
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

        mModel.getUserList(type, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (extra.equals(BaseFragment.LOADING)) {
                    mFragment.setLoadingState(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(BaseFragment.REFRESH)) {
                    mFragment.onRefreshComplete();
                }
            }

            @Override
            public void onResponse(String response) {
                if (extra.equals(BaseFragment.LOADING)) {
                    mFragment.setLoadingState(HLoadingStateView.State.COMPLETE);
                } else if (TextUtils.equals(extra, BaseFragment.REFRESH)) {
                    mFragment.onRefreshComplete();
                }

                try {
                    LogUtil.i("liang", "followingList:" + response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        UserListResult result = JSON.parseObject(baseResult.obj, UserListResult.class);
                        mFragment.setData(result, extra);
                    } else {
                        CommonUtils.error(baseResult, mFragment.getActivity(), "");
                    }

                    mFragment.setLoaded(true);
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }
            }
        });
    }
}
