package com.huapu.huafen.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Baby;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.dialog.DateChooseDialogNew;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

/**
 * 修改孩子信息
 * @author liang_xs
 *
 */
public class EditChildActivity extends BaseActivity {
	private final static String TAG = EditChildActivity.class.getSimpleName();
	private ArrayList<Baby> babies;
	private Baby baby ;
	private int position;
	private int editType;
	private TextView tvChildSex, tvChildBirthday;
	private View layoutChildSex, layoutChildBirthday;
	private View tvBtnConfirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_child);
		initView();
		if(getIntent().hasExtra(MyConstants.EXTRA_BABY_LIST)) {
			babies = (ArrayList<Baby>) getIntent().getSerializableExtra(MyConstants.EXTRA_BABY_LIST);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_BABY_POSITION)) {
			position = getIntent().getIntExtra(MyConstants.EXTRA_BABY_POSITION, -1);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_BABY_EDIT_TYPE)) {
			editType = getIntent().getIntExtra(MyConstants.EXTRA_BABY_EDIT_TYPE, -1);
		}

		if(editType == 1){
			baby = babies.get(position);
		}else{
			baby = new Baby();
			baby.setSex(0);
			baby.setDateOfBirth(System.currentTimeMillis());
		}

		if(baby.getSex() == 0) {
			tvChildSex.setText("小公主");
		} else {
			tvChildSex.setText("小王子");
		}
		tvChildBirthday.setText(DateTimeUtils.getYearMonthDay(baby.getDateOfBirth()));
		
	}
	private void initView() {
		setTitleString("宝宝信息");
		tvChildSex = (TextView) findViewById(R.id.tvChildSex);
		tvChildBirthday = (TextView) findViewById(R.id.tvChildBirthday);
		layoutChildSex = findViewById(R.id.layoutChildSex);
		layoutChildBirthday = findViewById(R.id.layoutChildBirthday);
		tvBtnConfirm = findViewById(R.id.tvBtnConfirm);
		layoutChildSex.setOnClickListener(this);
		layoutChildBirthday.setOnClickListener(this);
		tvBtnConfirm.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layoutChildSex:
			PhotoDialog babySexDialog = new PhotoDialog(EditChildActivity.this, "小王子", "小公主");
			babySexDialog.setCameraCall(new DialogCallback() {

				@Override
				public void Click() {
					baby.setSex(1);
					tvChildSex.setText("小王子");
				}
			});
			babySexDialog.setAlbumCall(new DialogCallback() {

				@Override
				public void Click() {
					baby.setSex(0);
					tvChildSex.setText("小公主");
				}
			});
			babySexDialog.show();
			break;
			
		case R.id.layoutChildBirthday:
			String date = "";
			if (tvChildBirthday.getText().toString().equals("选择时间")) {
				date = "";
			} else {
				date = tvChildBirthday.getText().toString();
			}

			DateChooseDialogNew dateDialog = new DateChooseDialogNew(EditChildActivity.this,
					DateChooseDialogNew.PASTTIME_MODE, Calendar.getInstance().get(Calendar.YEAR) - 16, date);

			dateDialog.setDialogTitle("日期选择");
			dateDialog.setOnClickListener(new DateChooseDialogNew.ChooseDateCallback() {

				@Override
				public void dateItemOnClic(String dateString) {
					baby.setDateOfBirth(Long.valueOf(DateTimeUtils.getTime("yyyy-MM-dd", dateString)));
					tvChildBirthday.setText(dateString);
				}
			});
			dateDialog.show();
			break;
		case R.id.tvBtnConfirm:
			startRequestForUpdateUser();
			break;
		}
	}
	


	/**
	 * 修改个人资料
	 * type:1,修改 2，删除
	 */
	private void startRequestForUpdateUser() {
		HashMap<String, String> params = new HashMap<>();
		JSONArray array = updateChild();
		if (array != null) {
			params.put("babys", array.toString());
			params.put("pregnantStat", "3");
		}
		LogUtil.e(TAG, editType==1?"修改宝宝":"添加宝宝"+"params:"+params.toString());
		OkHttpClientManager.postAsyn(MyConstants.UPDATEUSER, params, new StringCallback() {

			@Override
			public void onError(Request request, Exception e) {
				LogUtil.e(TAG, editType==1?"修改宝宝":"添加宝宝"+"onError:"+ e.toString());
			}

			@Override
			public void onResponse(String response) {
				LogUtil.e(TAG, editType==1?"修改宝宝":"添加宝宝"+"response:" + response.toString());
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if (!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if (!TextUtils.isEmpty(baseResult.obj)) {
							Intent intent = new Intent();
							intent.putExtra(MyConstants.EXTRA_BABY_LIST, babies);
							setResult(RESULT_OK, intent);
							finish();
						}
					} else {
						CommonUtils.error(baseResult, EditChildActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}
	
	private JSONArray updateChild(){
		JSONArray array = new JSONArray();

		if(editType == 2){//添加
			if(babies == null){
				babies = new ArrayList<>();
			}
			babies.add(baby);
		}

		for(Baby bb:babies){
			JSONObject object = new JSONObject();
			try {
				object.put("sex", bb.getSex());
				object.put("dateOfBirth", bb.getDateOfBirth());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(object);
		}

		return array;
	}
	
}
