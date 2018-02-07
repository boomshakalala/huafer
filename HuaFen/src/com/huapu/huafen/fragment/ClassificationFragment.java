package com.huapu.huafen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.MontageActivity;
import com.huapu.huafen.activity.SearchActivity;
import com.huapu.huafen.adapter.LeftClassificationAdapter;
import com.huapu.huafen.adapter.RightClassificationAdapter;
import com.huapu.huafen.beans.ClassificationResult;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 分类
 * Created by admin on 2017/4/11.
 */
public class ClassificationFragment extends BaseFragment implements View.OnClickListener, LeftClassificationAdapter.OnItemClickListener {

    @BindView(R2.id.llTextSearch)
    LinearLayout llTextSearch;
    @BindView(R2.id.recyclerClass1)
    RecyclerView recyclerClass1;
    private LeftClassificationAdapter leftClassificationAdapter;
    @BindView(R2.id.recyclerClass2)
    RecyclerView recyclerClass2;
    private RightClassificationAdapter rightClassificationAdapter;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.classification_layout, container, false);
    }

    @Override
    public void onViewCreated(View root) {
        super.onViewCreated(root);
        init();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        doRequest();
        if (CommonPreference.getIntValue(MyConstants.CLASS_GUIDE_TIPS, 0) == 0) {
            Intent intent = new Intent(getActivity(), MontageActivity.class);
            intent.putExtra(MyConstants.EXTRA_MONTAGE, MyConstants.CLASS_GUIDE_TIPS);
            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        }

    }


    private void init() {
        llTextSearch.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerClass1.setLayoutManager(layoutManager);
        leftClassificationAdapter = new LeftClassificationAdapter(getContext());
        recyclerClass1.setAdapter(leftClassificationAdapter);
        leftClassificationAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), RightClassificationAdapter.SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        recyclerClass2.setLayoutManager(gridLayoutManager);
        rightClassificationAdapter = new RightClassificationAdapter(getContext());
        recyclerClass2.setAdapter(rightClassificationAdapter);

        ViewUtil.setOffItemAnimator(recyclerClass1);
        ViewUtil.setOffItemAnimator(recyclerClass2);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.llTextSearch) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        }
    }

    public void doRequest() {
        HashMap<String, String> params = new HashMap<>();
        OkHttpClientManager.postAsyn(MyConstants.CLASS_NEW_VOLUMN, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("lalo", "campaignError:" + e.toString());
            }

            @Override
            public void onResponse(String response) {

                try {
                    ClassificationResult result = JSON.parseObject(response, ClassificationResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        initData(result);
                    } else {
                        CommonUtils.error(result, getContext(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void doRequest2(final String key) {
        HashMap<String, String> params = new HashMap<>();
        params.put("catId", key);
        OkHttpClientManager.postAsyn(MyConstants.CLASS_NEW_VOLUMN, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("lalo", "campaignError:" + e.toString());
            }


            @Override
            public void onResponse(String response) {

                try {
                    ClassificationResult result = JSON.parseObject(response, ClassificationResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        rightClassificationAdapter.setData(key, result.obj.layout);
                        cache.put(key, result.obj.layout);
                    } else {
                        CommonUtils.error(result, getContext(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(ClassificationResult result) {
        if (result != null) {
            List<ClassificationResult.Indice> list = result.obj.indices;
            String select = result.obj.selected;
            if (CommonPreference.getIntValue(MyConstants.CLASS_GUIDE_TIPS, 0) == 0) {
                leftClassificationAdapter.setGuideData(list, "男士专区");
                recyclerClass1.scrollToPosition(list.size() - 1);
            } else {
                leftClassificationAdapter.setData(list, select);
            }
            rightClassificationAdapter.setData(select, result.obj.layout);
            cache.put(result.obj.selected, result.obj.layout);

        }
    }

    @Override
    public void onItemClick(ClassificationResult.Indice item) {
        rightClassificationAdapter.setData(null, null);//防止网络慢时，切换下一标签，还显示原来页面内容
        if (cache.containsKey(item.key) && !ArrayUtil.isEmpty(cache.get(item.key))) {
            rightClassificationAdapter.setData(item.key, cache.get(item.key));
            recyclerClass2.smoothScrollToPosition(0);
        } else {
            doRequest2(item.key);
        }
    }

    private HashMap<String, List<ClassificationResult.Layout>> cache = new HashMap<>();

    @Override
    public void onResume() {
        super.onResume();
        if (CommonPreference.getIntValue(MyConstants.MAIN_GUIDE_TIPS, 0) == 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(getActivity(), MontageActivity.class);
                    intent.putExtra(MyConstants.EXTRA_MONTAGE, MyConstants.MAIN_GUIDE_TIPS);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);

                }
            }, 1000);
        }
    }
}
