package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.AddressListAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.squareup.okhttp.Request;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;

/**
 * Created by admin on 2017/5/11.
 */

public class AddressListActivityNew extends BaseActivity {


    @BindView(R2.id.addressList) RecyclerView addressList;
    public AddressListAdapter adapter ;
    @BindView(R2.id.tvAddAddress) TextView tvAddAddress;
    private boolean isChooseAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_list);
        isChooseAddress = mIntent.getBooleanExtra(MyConstants.CHOOSE_ADDRESS_KEY,false);
        LinearLayoutManager manager = new LinearLayoutManager(addressList.getContext(),LinearLayoutManager.VERTICAL,false);
        addressList.setLayoutManager(manager);
        adapter = new AddressListAdapter(this);
        if(DepositActivity.class.getSimpleName().equals(fromActivity)){
            adapter.setClickToCheckDefaultAddress(true);
        }
        adapter.setChooseAddress(isChooseAddress);
        addressList.setAdapter(adapter);
        tvAddAddress.setOnClickListener(this);
        startRequestForGetAddressList();
    }

    @Override
    public void initTitleBar() {
        setTitleString("我的收货地址");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v == tvAddAddress){
            Intent intent = new Intent(this,ModifyAddressActivity.class);
            intent.putExtra(MyConstants.EXTRA_ADDRESS_TYPE , MyConstants.TO_ADDRESS_ADD);
            startActivityForResult(intent,MyConstants.INTENT_FOR_RESULT_TO_ADDRESS_EDIT);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ADDRESS_EDIT) {
                startRequestForGetAddressList();
            }
        }
    }

    /**
     * 获取地址列表
     */
    private void startRequestForGetAddressList(){
        if(!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        OkHttpClientManager.postAsyn(MyConstants.GETUSERCONSIGNEEINFO, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if(!TextUtils.isEmpty(baseResult.obj)) {
                            List<Consignee> data = ParserUtils.parserConsigneesListData(baseResult.obj);
                            adapter.setData(data);
                        }
                    } else {
                        CommonUtils.error(baseResult, AddressListActivityNew.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
