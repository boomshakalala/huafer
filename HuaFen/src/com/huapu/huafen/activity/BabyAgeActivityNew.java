package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.FilterAgeAdapter;
import com.huapu.huafen.beans.Age;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by admin on 2017/3/22.
 */

public class BabyAgeActivityNew extends BaseActivity {

    private final static String TAG = BabyAgeActivityNew.class.getSimpleName();
    @BindView(R2.id.recyclerView) RecyclerView recyclerView;

    private FilterAgeAdapter adapter;
    private View headerView;

    private  ArrayList<Age> selectAgeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_age_range);
        if(getIntent().hasExtra(MyConstants.EXTRA_CHOOSE_AGE)) {

            //获取传进来的age对象，需要传到适配器中，设置选中
            selectAgeList = (ArrayList<Age>) getIntent().getSerializableExtra(MyConstants.EXTRA_CHOOSE_AGE);

            if(selectAgeList!=null){
                LogUtil.e(TAG,selectAgeList.toArray());
            }else{
                LogUtil.e(TAG,"selectAge == null");
            }
        }
        //获取屏幕的高度,方便toast调用
        //WindowManager wm = this.getWindowManager();
        //final int height = wm.getDefaultDisplay().getHeight();

        //提交按钮
        findViewById(R.id.age_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ArrayUtil.isEmpty(adapter.getSelectedAge())){
                    toasttop("必须选择一个年龄段分类哦");
                    //Toast toast = Toast.makeText(BabyAgeActivityNew.this, "必须选择一个年龄段分类哦", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.TOP, 0, height/3);
                    //toast.show();
                    return;
                }

                //提交 按钮按下后返回修改的age对象的集合
                Intent intent = new Intent();
                //获取 age对象集合
                ArrayList<Age> checkPosition = adapter.getSelectedAge();

                if(checkPosition!=null){
                    LogUtil.e(TAG,"setResult:"+checkPosition.toString());
                }else{
                    LogUtil.e(TAG,"setResult: selectAge == null");
                }

                //设置传递参数
                intent.putExtra(MyConstants.EXTRA_CHOOSE_AGE, checkPosition);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new FilterAgeAdapter(this);
        headerView = LayoutInflater.from(this).inflate(R.layout.age_range_title,recyclerView,false);
        adapter.addHeaderView(headerView);
        recyclerView.setAdapter(adapter.getWrapperAdapter());
        startRequestForGetAgeList();
    }


    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("适合年龄段");
    }


    private void startRequestForGetAgeList() {
        if(!CommonUtils.isNetAvaliable(this)) {
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        OkHttpClientManager.postAsyn(MyConstants.GETAGELIST, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                // 调用刷新完成
                LogUtil.e(TAG, "宝宝年龄段列表:" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            List<Age> list = ParserUtils.parserAgeListData(baseResult.obj);
                            adapter.setData(list);
                            //设置选中等待条块
                            adapter.setSelectAge(selectAgeList);
                        }
                    } else {
                        CommonUtils.error(baseResult, BabyAgeActivityNew.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }





}
