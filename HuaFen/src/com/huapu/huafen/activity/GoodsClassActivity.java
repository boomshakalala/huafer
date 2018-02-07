package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.SportCatsResult;
import com.huapu.huafen.expandtab.ViewClassNew;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 商品分类
 * @author liang_xs
 *
 */
public class GoodsClassActivity extends BaseActivity {

	private final static String TAG =GoodsClassActivity.class.getSimpleName();

	private ViewClassNew middle;
	/**
	 * 1为来自分类首页
	 */
	private int jump_type;
	private int campaignId;
	private int firstClassId = 0, secondClassId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_class);
		if(getIntent().hasExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_FIRST_CLASS_ID)) {
			firstClassId = getIntent().getIntExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_FIRST_CLASS_ID, 0);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_SECOND_CLASS_ID)) {
			secondClassId = getIntent().getIntExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_SECOND_CLASS_ID, 0);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_JUMP_TYPE)) {
			jump_type = getIntent().getIntExtra(MyConstants.EXTRA_JUMP_TYPE, 0);
		}
		if(getIntent().hasExtra(MyConstants.CAMPAIGN_ID)) {
			campaignId = getIntent().getIntExtra(MyConstants.CAMPAIGN_ID, 0);
		}
		if(campaignId > 0){//运动季活动
			startRequestForSportClass();
		}else{
			ArrayList<Cat> cats = CommonPreference.getCats();
			initClass(cats);
		}
	}

	@Override
	public void initTitleBar() {
		setTitleString("分类");
	}

	private void startRequestForSportClass(){
		HashMap<String, String> params = new HashMap<>();
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
						CommonUtils.error(result, GoodsClassActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initClass(ArrayList<Cat> cats){
		if(jump_type == 2 && campaignId == 0) {
			for(Cat cat : cats) {
				if(cat.getCid() == 18) {
					cats.remove(cat);
					break;
				}
			}
		}
		middle = new ViewClassNew(GoodsClassActivity.this);
		LinearLayout layoutContent = (LinearLayout) findViewById(R.id.layoutContent);
		layoutContent.addView(middle);
		middle.setCatData(cats);
		ArrayList<Cat> secondCats = new ArrayList<Cat>();
		int firstPos = 0;
		int secondPos = 0;
		if(firstClassId != 0) {
			for(int i = 0; i < cats.size(); i++) {
				if(cats.get(i).getCid() == firstClassId) {
					firstPos = i;
					LogUtil.i("class-----", "firstClassId:" + firstClassId);
					LogUtil.i("class-----", "firstPos:" + firstPos);
					secondCats = cats.get(i).getCats();
					break;
				}
			}
		}
		if(secondClassId != 0) {
			for(int i = 0; i < secondCats.size(); i++) {
				if(secondCats.get(i).getCid() == secondClassId) {
					secondPos = i;
					LogUtil.i("class-----", "secondClassId:" + secondClassId);
					LogUtil.i("class-----", "secondPos:" + secondPos);
					break;
				}
			}
			middle.setSelectItem(firstPos, secondPos);
		} else {
			middle.setSelectItem(firstPos);
		}
		middle.setOnSelectListener(new ViewClassNew.OnSelectListener() {

			@Override
			public void getValue(Cat firstList, Cat secondList, String showText) {
				Intent intent = new Intent();
				intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_FIRST_CLASS_ID, firstList.getCid());
				intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_SECOND_CLASS_ID, secondList.getCid());
				intent.putExtra(MyConstants.EXTRA_CHOOSE_CLASS_CHILDNAME, showText);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}
}
