package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ModifyAddressActivity;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.squareup.okhttp.Request;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/11.
 */

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.AddressViewHolder> {

    private Context context;
    private List<Consignee> data;
    private boolean isChooseAddress;
    private boolean isClickToCheckDefaultAddress;

    public void setChooseAddress(boolean chooseAddress) {
        isChooseAddress = chooseAddress;
    }

    public void setClickToCheckDefaultAddress(boolean clickToCheckDefaultAddress) {
        isClickToCheckDefaultAddress = clickToCheckDefaultAddress;
    }

    public AddressListAdapter(Context context, List<Consignee> data) {
        this.context = context;
        this.data = data;
    }

    public AddressListAdapter(Context context) {
        this(context, null);
    }

    public void setData(List<Consignee> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AddressViewHolder(LayoutInflater.from(context).inflate(R.layout.item_address, parent, false));
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        final Consignee item = data.get(position);
        if (item != null) {
            Area area = item.getArea();
            if (!TextUtils.isEmpty(item.getConsigneeName()) && !TextUtils.isEmpty(item.getConsigneePhone())) {
                holder.tvReceiver.setText(item.getConsigneeName() + " " + item.getConsigneePhone());
            }
            String province;
            String city;
            String areaName;
            if (area != null) {
                province = area.getProvince();
                city = area.getCity();
                areaName = area.getArea();
            } else {
                province = "";
                city = "";
                areaName = "";
            }
            if (province.equals(city)) {
                province = "";
            }
            holder.tvShippingAddress.setText(province + city + areaName + item.getConsigneeAddress());

            if (item.getIsDefault()) {
                holder.chbSetDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.received_goods_red, 0, 0, 0);
                holder.chbSetDefault.setTextColor(context.getResources().getColor(R.color.base_pink));
                holder.chbSetDefault.setText("默认地址");
                holder.tvDelete.setVisibility(View.GONE);
            } else {
                holder.chbSetDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.received_goods_gray, 0, 0, 0);
                holder.chbSetDefault.setTextColor(context.getResources().getColor(R.color.text_color));
                holder.chbSetDefault.setText("设为默认");
                holder.tvDelete.setVisibility(View.VISIBLE);
            }

            holder.chbSetDefault.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startRequestForSetDefault(item);
                }
            });

            holder.tvEdit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ModifyAddressActivity.class);
                    intent.putExtra(MyConstants.EXTRA_ADDRESS, item);
                    intent.putExtra(MyConstants.EXTRA_ADDRESS_TYPE, MyConstants.TO_ADDRESS_EDIT);
                    ((Activity) context).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ADDRESS_EDIT);

                }
            });

            holder.tvDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final TextDialog textDialog = new TextDialog(context,true);
                    textDialog.setContentText("您确定删除收货地址吗？");
                    textDialog.setLeftText("取消");
                    textDialog.setLeftCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            textDialog.dismiss();
                        }
                    });
                    textDialog.setRightText("确定");
                    textDialog.setRightCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            startRequestForDelete(item.getConsigneesId());
                        }
                    });
                    textDialog.show();

                }
            });

            if(isChooseAddress){
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(isClickToCheckDefaultAddress){
                            startRequestForSetDefault(item);
                        }else{
                            Intent intent = new Intent();
                            intent.putExtra(MyConstants.EXTRA_ADDRESS,item);
                            ((Activity)context).setResult(Activity.RESULT_OK,intent);
                            ((Activity)context).finish();
                        }

                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * 设置默认收货地址
     */
    private void startRequestForSetDefault(final Consignee consignee) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("consigneesId", String.valueOf(consignee.getConsigneesId()));
        OkHttpClientManager.postAsyn(MyConstants.SETDEFAULT, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
            }


            @Override
            public void onResponse(String response) {
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            List<Consignee> list = ParserUtils.parserConsigneesListData(baseResult.obj);
                            setData(list);
                            if(isClickToCheckDefaultAddress){
                                Intent intent = new Intent();
                                intent.putExtra(MyConstants.EXTRA_ADDRESS,consignee);
                                ((Activity)context).setResult(Activity.RESULT_OK,intent);
                                ((Activity)context).finish();
                            }
                        }
                    } else {
                        CommonUtils.error(baseResult, (Activity) context, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startRequestForDelete(long consigneesId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("consigneesId", String.valueOf(consigneesId));
        OkHttpClientManager.postAsyn(MyConstants.DELETE_ADDRESS, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            List<Consignee> list = ParserUtils.parserConsigneesListData(baseResult.obj);
                            setData(list);
                        }
                    } else {
                        CommonUtils.error(baseResult, (Activity) context, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvReceiver) TextView tvReceiver;
        @BindView(R.id.tvShippingAddress) TextView tvShippingAddress;
        @BindView(R.id.chbSetDefault) TextView chbSetDefault;
        @BindView(R.id.tvDelete) TextView tvDelete;
        @BindView(R.id.tvEdit) TextView tvEdit;

        public AddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
