package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.BabiesActivity;
import com.huapu.huafen.activity.EditChildActivity;
import com.huapu.huafen.beans.Baby;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/17.
 */

public class BabyAdapter extends CommonWrapper<BabyAdapter.BabyViewHolder> {

    private final static String TAG = BabyAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Baby> data;
    private boolean isDataChanged;

    public ArrayList<Baby> getData() {
        return data;
    }

    public void setData(ArrayList<Baby> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public BabyAdapter(Context context, ArrayList<Baby> data) {
        this.context = context;
        this.data = data;
    }


    public BabyAdapter(Context context) {
        this(context, null);
    }

    @Override
    public BabyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BabyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_baby, parent, false));
    }

    @Override
    public void onBindViewHolder(BabyViewHolder holder, final int position) {
        Baby item = data.get(position);
        if (item != null) {
            if (item.isEditAble) {
                holder.itemView.setOnClickListener(null);
                holder.ivDeleteBaby.setVisibility(View.VISIBLE);
                holder.ivDeleteBaby.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        final TextDialog dialog = new TextDialog(context, true);
                        dialog.setContentText("确定删除宝宝信息吗？");
                        dialog.setLeftText("取消");
                        dialog.setLeftCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                dialog.dismiss();
                            }
                        });
                        dialog.setRightText("确定");
                        dialog.setRightCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                dialog.dismiss();
//                                data.remove(position);
                                ArrayList<Baby> tmp = (ArrayList<Baby>) data.clone();
                                tmp.remove(position);
                                doRequestForDeleteBabyInfo(tmp, position);
                                isDataChanged = true;
                            }
                        });

                        dialog.show();

                    }
                });
                holder.rightArrow.setVisibility(View.GONE);
            } else {
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, EditChildActivity.class);
                        intent.putExtra(MyConstants.EXTRA_BABY_LIST, data);
                        intent.putExtra(MyConstants.EXTRA_BABY_POSITION, position);
                        intent.putExtra(MyConstants.EXTRA_BABY_EDIT_TYPE, 1);
                        ((Activity) context).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_EDIT_CHILD);

                    }
                });
                holder.ivDeleteBaby.setVisibility(View.GONE);
                holder.ivDeleteBaby.setOnClickListener(null);
                holder.rightArrow.setVisibility(View.VISIBLE);
            }

            holder.tvBabyTitle.setText(item.getSex() == 0 ? "小公主" : "小王子");

            holder.tvBirth.setText(DateTimeUtils.getYearMonthDay(item.getDateOfBirth()));


        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setEditAble(boolean editAble) {
        if (!ArrayUtil.isEmpty(data)) {
            for (Baby b : data) {
                b.isEditAble = editAble;
            }
            notifyWrapperDataSetChanged();
        }
        this.isDataChanged = false;
    }


    public void doRequestForDeleteBabyInfo(ArrayList<Baby> list, final int position) {
        ProgressDialog.showProgress(context, false);
        HashMap<String, String> params = new HashMap<>();
        JSONArray array = updateChild(list);
        if (array != null) {
            params.put("babys", array.toString());
            params.put("pregnantStat", "3");
        }
        LogUtil.e(TAG, "删除宝宝params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.UPDATEUSER, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, "删除宝宝error:" + e.toString());
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e(TAG, "删除宝宝:" + response.toString());
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            data.remove(position);
                            notifyWrapperDataSetChanged();
                            BabiesActivity activity = (BabiesActivity) context;
                            if (activity != null) {
                                activity.initFooter();
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

    private JSONArray updateChild(ArrayList<Baby> data) {
        JSONArray array = new JSONArray();

        if (!ArrayUtil.isEmpty(data)) {
            for (Baby b : data) {
                JSONObject objFirst = new JSONObject();
                try {
                    objFirst.put("sex", b.getSex());
                    objFirst.put("dateOfBirth", b.getDateOfBirth());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(objFirst);
            }
        }
        return array;
    }

    public class BabyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.ivDeleteBaby)
        ImageView ivDeleteBaby;
        @BindView(R2.id.tvBabyTitle)
        TextView tvBabyTitle;
        @BindView(R2.id.tvBirth)
        TextView tvBirth;
        @BindView(R2.id.rightArrow)
        ImageView rightArrow;

        public BabyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
