package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.BindView;

/**
 * Created by admin on 2017/5/11.
 */

public class ModifyAddressActivity extends BaseActivity {

    @BindView(R2.id.etConsigneeName) EditText etConsigneeName;
    @BindView(R2.id.etConsigneePhone) EditText etConsigneePhone;
    @BindView(R2.id.tvConsigneeCity) TextView tvConsigneeCity;
    @BindView(R2.id.layoutCity) RelativeLayout layoutCity;
    @BindView(R2.id.etConsigneeAddress) EditText etConsigneeAddress;
    @BindView(R2.id.tvCommit) TextView tvCommit;
    private Consignee consignee ;
    private int addressType;
    private int areaId;
    private int cityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consignee = (Consignee)mIntent.getSerializableExtra(MyConstants.EXTRA_ADDRESS);
        if(consignee == null){
            consignee = new Consignee();
        }
        addressType = mIntent.getIntExtra(MyConstants.EXTRA_ADDRESS_TYPE, -1);
        setContentView(R.layout.activity_address_modify);
        initView();
        if(consignee != null) {
            etConsigneeName.setText(consignee.getConsigneeName());
            etConsigneePhone.setText(consignee.getConsigneePhone());
            etConsigneeAddress.setText(consignee.getConsigneeAddress());
            if(consignee.getArea() != null) {
                areaId = consignee.getArea().getAreaId();
                cityId = consignee.getArea().getCityId();
                tvConsigneeCity.setText(consignee.getArea().getCity() + consignee.getArea().getArea());
            }
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent ;
        switch (v.getId()) {
            case R.id.layoutCity:
                intent = new Intent(this, RegionListActivity.class);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_PROVINCE);
                break;

            case R.id.tvCommit:
                String name = etConsigneeName.getText().toString().trim();
                String phone = etConsigneePhone.getText().toString().trim();
                String address = etConsigneeAddress.getText().toString().trim();
                if(TextUtils.isEmpty(name)) {
                    toast("请填写收货人姓名");
                    return;
                }
                if(TextUtils.isEmpty(phone)) {
                    toast("请填写收货人手机号");
                    return;
                }
                if(TextUtils.isEmpty(address)) {
                    toast("请填写收货详细地址");
                    return;
                }
                if(cityId == 0) {
                    toast("请选择城市");
                    return;
                }
                if(areaId == 0) {
                    toast("请选择区域");
                    return;
                }
                startRequestForAddConsigneesList();
                break;
        }
    }

    @Override
    public void initTitleBar() {
        switch (addressType) {
            case MyConstants.TO_ADDRESS_EDIT:
                setTitleString("修改地址");
                break;

            case MyConstants.TO_ADDRESS_ADD:
                setTitleString("新增地址");
                break;
        }

    }

    private void initView(){
        layoutCity.setOnClickListener(this);
        tvCommit.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if(requestCode == MyConstants.INTENT_FOR_RESULT_TO_PROVINCE) {
                if(data == null) {
                    return;
                }

                cityId = data.getIntExtra(MyConstants.EXTRA_SELECT_CITY_ID,0);
                areaId = data.getIntExtra(MyConstants.EXTRA_SELECT_DISTRICT_ID,0);
                String text = data.getStringExtra(MyConstants.EXTRA_CHOOSE_CITYNAME);

                if(!TextUtils.isEmpty(text)){
                    tvConsigneeCity.setText(text);
                }
                Area area = new Area();
                area.setAreaId(areaId);
                area.setCityId(cityId);
                consignee.setArea(area);
            }
        }
    }

    /**
     * 添加或修改收货地址
     */
    private void startRequestForAddConsigneesList(){

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("consigneeName", etConsigneeName.getText().toString().trim());
        params.put("consigneePhone", etConsigneePhone.getText().toString().trim());
        params.put("consigneeAddress", etConsigneeAddress.getText().toString().trim());
        params.put("consigneeCityId", consignee.getArea().getCityId() + "");
        params.put("consigneeAreaId", consignee.getArea().getAreaId() + "");
        if(consignee.getConsigneesId() != null) {
            if (consignee.getConsigneesId() != 0) {
                params.put("consigneesId", consignee.getConsigneesId()+"");
            }
        }

        OkHttpClientManager.postAsyn(MyConstants.SAVEUSERCONSIGNEEINFO, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if(!TextUtils.isEmpty(baseResult.obj)) {
                            ArrayList<Consignee> list = (ArrayList<Consignee>) ParserUtils.parserConsigneesListData(baseResult.obj);
                            if(list != null) {
                                Intent intent = new Intent();
                                intent.putExtra(MyConstants.EXTRA_ADDRESS_LIST, list);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    } else {
                        CommonUtils.error(baseResult, ModifyAddressActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
