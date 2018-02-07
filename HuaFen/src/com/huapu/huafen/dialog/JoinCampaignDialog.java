package com.huapu.huafen.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.BaseResultDialog;
import com.huapu.huafen.beans.BaseResultNew;
import com.huapu.huafen.beans.CandidateCampaignsResult;
import com.huapu.huafen.beans.JoinCampaign;
import com.huapu.huafen.callbacks.OnRequestRetryListener;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.events.RefreshEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.DragGridView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.MaxHeightListView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by mac on 2018/1/3.
 */

public class JoinCampaignDialog extends DialogFragment implements AdapterView.OnItemClickListener {
    MaxHeightListView listView;
    HLoadingStateView loadingStateView;
    Context context;
    MyAdapter adapter;
    List<JoinCampaign> dataList;
    String goodsId = "";



    public JoinCampaignDialog(Context context, int goodsId) {
        this.context = context;
        this.goodsId = String.valueOf(goodsId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_join_campaign, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ArrayUtil.isEmpty(dataList)){
                    boolean flag = false;
                    for (JoinCampaign campaign : dataList) {
                        if (campaign.isChecked()){
                            startRequestForAddGoods(goodsId,campaign.getCampaignId(),0);
                            flag = true;
                            break;
                        }
                    }
                    if (!flag){
                        ToastUtil.toast(context,"请选择活动");
                    }
                }

            }
        });
        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        listView = (MaxHeightListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        loadingStateView = (HLoadingStateView) view.findViewById(R.id.loadingStateView);
        loadingStateView.setBackgroudTranslucent();
        if (!TextUtils.isEmpty(goodsId))
        startRequestForGetCandidateCampaigns(goodsId);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Window win = getDialog().getWindow();
        getDialog().setCanceledOnTouchOutside(true);
        // 显示在底部
        win.setGravity(Gravity.CENTER);
        // 去掉padding
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setBackgroundDrawableResource(R.color.translucent);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        int style = android.app.DialogFragment.STYLE_NO_TITLE;
        int theme = R.style.dialog_translate;
        setStyle(style, theme);
    }

    private void startRequestForGetCandidateCampaigns(String goodsId){
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("goodsId", goodsId);
        LogUtil.i("liang", "parmas:" + params.toString());
        if (!CommonUtils.isNetAvaliable(context)){
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }
        OkHttpClientManager.postAsyn(MyConstants.CANDIDATE_CAMPAIGNS,params,new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(e);
                ToastUtil.toast(context, "请检查网络连接");
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                dismiss();
            }

            @Override
            public void onResponse(String response) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                LogUtil.d("获取可参加活动列表："+response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response,BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE){
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            CandidateCampaignsResult data = JSON.parseObject(baseResult.obj, CandidateCampaignsResult.class);
                            initData(data);
                        }
                    }else {
                        CommonUtils.error(baseResult, (Activity) context, "");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void startRequestForAddGoods(final String goodsId, final int campaignId, final int confirmed) {
        HashMap<String,String> params = new HashMap<>();
        params.put("goodsId",goodsId);
        params.put("campaignId",String.valueOf(campaignId));
        if (confirmed != 0)
        params.put("confirmed",String.valueOf(confirmed));
        if (!CommonUtils.isNetAvaliable(context)){
            ToastUtil.toast(context,"请检查网络");
            loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            dismiss();
        }
        OkHttpClientManager.postAsyn(MyConstants.ADD_GOODS, params, new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(e);
                ToastUtil.toast(context, "请检查网络连接");
            }

            @Override
            public void onResponse(String response) {
                LogUtil.d("参加活动："+response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    boolean flag = parseEvent(context, response, new OnRequestRetryListener() {
                        @Override
                        public void onRetry() {
                            startRequestForAddGoods(goodsId,campaignId,1);
                        }
                    });
                    if (flag)
                        return;
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            ToastUtil.toast(getContext(), "参与活动成功");
                            RefreshEvent event = new RefreshEvent();
                            event.refresh = true;
                            EventBus.getDefault().post(event);
                            dismiss();
                        }
                    } else {
                        CommonUtils.error(baseResult, (Activity) getContext(), "");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private boolean parseEvent (final Context context,String response,final OnRequestRetryListener listener){
        boolean flag = false;
        try {
            BaseResult result = JSON.parseObject(response,BaseResult.class);
            if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE){
                BaseResultDialog dialogData = JSON.parseObject(result.obj,BaseResultDialog.class);
                BaseResultNew.Event event = dialogData.dialogEvent;
                if (BaseResultNew.SHOW_DIALOG.equals(event.name)) {

                    if (!ArrayUtil.isEmpty(event.opts.buttons)) {
                        final TextDialog dialog = new TextDialog(context, false);
                        dialog.setContentText(event.opts.content);
                        if (event.opts.buttons.size() == 1) {
                            final BaseResultNew.Event.Option.Button buttonLeft = event.opts.buttons.get(0);
                            dialog.setLeftText(buttonLeft.title);
                            dialog.setLeftCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    dialog.dismiss();
                                    if ("redo".equals(buttonLeft.action)) {
                                        if (listener != null) {
                                            listener.onRetry();
                                        }
                                    } else {
                                        ActionUtil.dispatchAction(context, buttonLeft.action, buttonLeft.target);
                                    }
                                }
                            });

                        } else if (event.opts.buttons.size() == 2) {
                            final BaseResultNew.Event.Option.Button buttonLeft = event.opts.buttons.get(0);
                            final BaseResultNew.Event.Option.Button buttonRight = event.opts.buttons.get(1);
                            dialog.setLeftText(buttonLeft.title);
                            dialog.setLeftCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    dialog.dismiss();
                                    if ("redo".equals(buttonLeft.action)) {
                                        if (listener != null) {
                                            listener.onRetry();
                                        }
                                    } else {
                                        ActionUtil.dispatchAction(context, buttonLeft.action, buttonLeft.target);
                                    }
                                }
                            });
                            dialog.setRightText(buttonRight.title);
                            dialog.setRightCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    dialog.dismiss();
                                    if ("redo".equals(buttonRight.action)) {
                                        if (listener != null) {
                                            listener.onRetry();
                                        }
                                    } else {
                                        ActionUtil.dispatchAction(context, buttonRight.action, buttonRight.target);
                                    }
                                }
                            });
                        }
                        dialog.show();
                    }

                    flag = true;
                } else if (BaseResultNew.UPDATE_USER_PROPERTY.equals(event.name)) {
                    CommonPreference.setGrantedCampaigns(event.opts.grantedCampaigns);
                    CommonPreference.setGrantedOneYun(event.opts.grantedOneYuan);
                }
            }else {
                ToastUtil.toast(context,result.msg);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }


    private void initData(CandidateCampaignsResult data) {
        if (data != null) {
            if (ArrayUtil.isEmpty(data.getList()))
                return;
            adapter = new MyAdapter();
            listView.setAdapter(adapter);
            dataList = data.getList();
            dataList.get(0).setChecked(true);
            adapter.setData(dataList);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JoinCampaign campaign = dataList.get(position);
        if (campaign != null) {
            for (JoinCampaign joinCampaign : dataList) {
                if (joinCampaign.equals(campaign))
                    continue;
                joinCampaign.setChecked(false);
            }
            boolean checked = campaign.isChecked();
            campaign.setChecked(!checked);
            adapter.setData(dataList);
        }
    }

    private class MyAdapter extends BaseAdapter{
        List<JoinCampaign> mData = new ArrayList<>();
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_candidate_campaigns,null);
                holder.ivCampaign = (SimpleDraweeView) convertView.findViewById(R.id.ivCampaign);
                holder.ivCampaignCheck = (ImageView) convertView.findViewById(R.id.ivCampaignCheck);
                holder.tvCampaignTitle = (TextView) convertView.findViewById(R.id.tvCampaignTitle);
                holder.tvCampaignDesc = (TextView) convertView.findViewById(R.id.tvCampaignDesc);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            JoinCampaign campaign = mData.get(position);
            if (campaign != null) {
                holder.tvCampaignTitle.setText(campaign.getCampaignName());
                holder.tvCampaignDesc.setText(campaign.getCampaignNote());
                ImageLoader.loadImage(holder.ivCampaign,campaign.getCampaignImg());
                if (campaign.isChecked())
                    holder.ivCampaignCheck.setVisibility(View.VISIBLE);
                else
                    holder.ivCampaignCheck.setVisibility(View.GONE);
            }

            return convertView;
        }
        public class ViewHolder {
            TextView tvCampaignTitle;
            TextView tvCampaignDesc;
            SimpleDraweeView ivCampaign;
            ImageView ivCampaignCheck;
        }

        public void setData(List<JoinCampaign> data){
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

    }




}
