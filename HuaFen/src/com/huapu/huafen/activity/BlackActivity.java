package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 屏蔽的人列表
 *
 * @author liang_xs
 */
public class BlackActivity extends BaseActivity {
    private ListView blackListView;
    private MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black);
        initView();
        startRequestForGetShieldPageList();
    }


    private void initView() {
        setTitleString("屏蔽列表");
        blackListView = (ListView) findViewById(R.id.blackListView);
        adapter = new MyListAdapter(this);
        blackListView.setAdapter(adapter);
    }


    /**
     * 屏蔽的人列表
     */
    public void startRequestForGetShieldPageList() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        LogUtil.i("liang", "parmas:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETSHIELDPAGELIST, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        LogUtil.i("liang", "屏蔽的人列表" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
                                    List<UserInfo> list = ParserUtils.parserBlackListData(baseResult.obj);
                                    if (list != null) {
                                        adapter.setData(list);
                                    }
                                }
                            } else {
                                CommonUtils.error(baseResult, BlackActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    class MyListAdapter extends BaseAdapter {

        private List<UserInfo> list = new ArrayList<UserInfo>();
        private Context mContext;

        public MyListAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public void setData(List<UserInfo> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_listview_black, null);
                viewHolder = new ViewHolder();
                viewHolder.ctvName = (CommonTitleView) convertView.findViewById(R.id.ctvName);
                viewHolder.tvBtnCancel = (TextView) convertView.findViewById(R.id.tvBtnCancel);
                viewHolder.ivHeader = (SimpleDraweeView) convertView.findViewById(R.id.ivHeader);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.ctvName.setData(list.get(position));

            ImageLoader.resizeSmall(viewHolder.ivHeader, list.get(position).getUserIcon(), 1);

            viewHolder.tvBtnCancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startRequestForShieldUser(position);
                }
            });
            return convertView;
        }

        class ViewHolder {
            public CommonTitleView ctvName;
            public TextView tvBtnCancel;
            private SimpleDraweeView ivHeader;
        }


        /**
         * 屏蔽&取消屏蔽用户接口
         *
         * @param
         */
        private void startRequestForShieldUser(final int position) {
            if (!CommonUtils.isNetAvaliable(mContext)) {
                ToastUtil.toast(mContext, "请检查网络连接");
                return;
            }
            ProgressDialog.showProgress(mContext);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userId", String.valueOf(list.get(position).getUserId()));
            final boolean isShield = list.get(position).getIsShield();
            if (isShield) {
                params.put("type", "2"); // 取消屏蔽
            } else {
                params.put("type", "1"); // 屏蔽
            }
            LogUtil.i("liang", "params:" + params.toString());
            OkHttpClientManager.postAsyn(MyConstants.SHIELDUSER, params,
                    new StringCallback() {

                        @Override
                        public void onError(Request request, Exception e) {
                            ProgressDialog.closeProgress();
                        }

                        @Override
                        public void onResponse(String response) {
                            ProgressDialog.closeProgress();
                            LogUtil.i("liang", "屏蔽:" + response);
                            JsonValidator validator = new JsonValidator();
                            boolean isJson = validator.validate(response);
                            if (!isJson) {
                                return;
                            }
                            try {
                                BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                                if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                    if (isShield) {
                                        ToastUtil.toast(mContext, "取消屏蔽成功");
//										ivLike.setSelected(false);
                                        list.get(position).setIsShield(false);
                                        startRequestForGetShieldPageList();

//										RongIMClient.getInstance().removeFromBlacklist(
//												String.valueOf(list.get(position).getUserId()),
//												new OperationCallback() {
//
//													@Override
//													public void onSuccess() {
//														CommonUtils.toast(mContext, "取消成功");
////														startRequestForGetShieldPageList();
//														list.remove(position);
//														notifyDataSetChanged();
//													}
//
//													@Override
//													public void onError(ErrorCode arg0) {
//														CommonUtils.toast(mContext, "系统异常，稍后再试");
//													}
//												});
                                    } else {
                                        ToastUtil.toast(mContext, "屏蔽成功");
//										ivLike.setSelected(true);
                                        list.get(position).setIsShield(true);
                                        startRequestForGetShieldPageList();

//										RongIMClient.getInstance().addToBlacklist(
//												String.valueOf(list.get(position).getUserId()),
//												new OperationCallback() {
//
//													@Override
//													public void onSuccess() {
//														CommonUtils.toast(mContext, "屏蔽成功");
//														startRequestForGetShieldPageList();
//													}
//
//													@Override
//													public void onError(ErrorCode arg0) {
//														CommonUtils.toast(mContext, "系统异常，稍后再试");
//													}
//												});
                                    }

                                } else {
                                    CommonUtils.error(baseResult, (Activity) mContext, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });
        }
    }
}
