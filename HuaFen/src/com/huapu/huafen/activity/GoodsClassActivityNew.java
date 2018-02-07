package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.LeftGoodsAdapter;
import com.huapu.huafen.adapter.RightGoodsAdapter;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.SportCatsResult;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.itemdecoration.SpacesDecoration;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商品分类
 *
 * @author liang_xs
 */
public class GoodsClassActivityNew extends BaseActivity {

    private final static String TAG = GoodsClassActivityNew.class.getSimpleName();
    @BindView(R.id.class1) RecyclerView class1;
    @BindView(R.id.class2) RecyclerView class2;

    private LeftGoodsAdapter leftGoodsAdapter;
    private RightGoodsAdapter rightGoodsAdapter;
    /**
     * 1为来自分类首页
     */
    private int jump_type;
    private int campaignId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_new);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(MyConstants.EXTRA_JUMP_TYPE)) {
            jump_type = getIntent().getIntExtra(MyConstants.EXTRA_JUMP_TYPE, 0);
        }
        if (getIntent().hasExtra(MyConstants.CAMPAIGN_ID)) {
            campaignId = getIntent().getIntExtra(MyConstants.CAMPAIGN_ID, 0);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(class1.getContext(),LinearLayoutManager.VERTICAL,false);
        class1.setLayoutManager(linearLayoutManager);
        leftGoodsAdapter = new LeftGoodsAdapter(class1.getContext());
        class1.setAdapter(leftGoodsAdapter);
        leftGoodsAdapter.setOnLeftItemClickListener(new LeftGoodsAdapter.OnLeftItemClickListener() {

            @Override
            public void onItemClick(Cat cat) {

                rightGoodsAdapter.setData(cat.getCats(),cat.getCid());
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(class2.getContext(),2,LinearLayoutManager.VERTICAL,false);
        class2.setLayoutManager(gridLayoutManager);
        class2.addItemDecoration(new SpacesDecoration(10));
        rightGoodsAdapter = new RightGoodsAdapter(class2.getContext());
        class2.setAdapter(rightGoodsAdapter);
        rightGoodsAdapter.setOnRightItemClickListener(new RightGoodsAdapter.OnRightItemClickListener() {

            @Override
            public void onItemClick(Cat cat,int firstId) {
                Intent intent = new Intent();
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_FIRST_CLASS_ID, firstId);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_SECOND_CLASS_ID, cat.getCid());
                intent.putExtra(MyConstants.EXTRA_CHOOSE_CLASS_CHILDNAME, cat.getName());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        if (campaignId !=0) {//活动
            startRequestForSportClass();
        } else {
            ArrayList<Cat> cats = CommonPreference.getCats();
            initClass(cats);
        }
    }

    @Override
    public void initTitleBar() {
        setTitleString("分类");
    }

    private void startRequestForSportClass() {
        HashMap<String, String> params = new HashMap<>();
        params.put("campaignId",String.valueOf(campaignId));
        OkHttpClientManager.postAsyn(MyConstants.GET_SPORT_SEASON_CLASS_LIST, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, "运动分类onError：" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "运动分类onResponse：" + response.toString());
                try {
                    SportCatsResult result = JSON.parseObject(response, SportCatsResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        initClass(result.obj.classifications);
                    } else {
                        CommonUtils.error(result, GoodsClassActivityNew.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initClass(ArrayList<Cat> cats) {
        if (jump_type == 2 && campaignId == 0) {
            for (Cat cat : cats) {
                if (cat.getCid() == 18) {
                    cats.remove(cat);
                    break;
                }
            }
        }


        for(Cat c:cats){
            if(!ArrayUtil.isEmpty(c.getCats())){
                ArrayList<Cat> cs = c.getCats();
                Iterator<Cat> itt = cs.iterator();
                while (itt.hasNext()){
                    Cat cat = itt.next();
                    if(TextUtils.isEmpty(cat.getIcon())){
                        itt.remove();
                    }
                }
            }
        }


        Cat defaultCat = cats.get(0);
        defaultCat.isCheck = true;
        leftGoodsAdapter.setData(cats);
        rightGoodsAdapter.setData(defaultCat.getCats(),defaultCat.getCid());
    }
}
