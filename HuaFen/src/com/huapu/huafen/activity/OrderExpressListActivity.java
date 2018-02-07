package com.huapu.huafen.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Express;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * 查看物流
 *
 * @author liang_xs
 */
public class OrderExpressListActivity extends BaseActivity {
    private ListView expressListView;
    private MyListAdapter adapter;
    private SimpleDraweeView ivExpressIcon;
    private TextView tvExpressName, tvExpressNum, tvExpress;
    private long orderId;
    private String orderRefundType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_expresslist);
        initView();
        if (getIntent().hasExtra(MyConstants.EXTRA_EXPRESS)) {
            Express bean = (Express) getIntent().getSerializableExtra(MyConstants.EXTRA_EXPRESS);
            if (bean != null) {
                initExpress(bean);
            }
        }

        if (getIntent().hasExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE)) {
            orderRefundType = getIntent().getStringExtra(MyConstants.EXTRA_REFUND_ORDER_TYPE);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL_ID)) {
            orderId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, 0);
            if (orderId != 0) {
                startRequestForGetExpressInfo();
            }
        }

    }

    private void initExpress(Express bean) {
        adapter.setData(bean);
        ImageLoader.resizeSmall(ivExpressIcon, bean.getExpressIcon(), 1);
        tvExpressName.setText(bean.getExpressName());
        tvExpressNum.setText(bean.getExpressNum());
        switch (bean.getExpressState()) {
            case 1:
                tvExpress.setText("已发货");
                break;

            case 2:
                tvExpress.setText("在途中");
                break;

            case 3:
                tvExpress.setText("签收");
                break;

            case 4:
                tvExpress.setText("问题");
                break;
        }
    }

    private void initView() {
        setTitleString("快递信息");
        expressListView = (ListView) findViewById(R.id.expressListView);
        ivExpressIcon = (SimpleDraweeView) findViewById(R.id.ivExpressIcon);
        tvExpressName = (TextView) findViewById(R.id.tvExpressName);
        tvExpressNum = (TextView) findViewById(R.id.tvExpressNum);
        tvExpress = (TextView) findViewById(R.id.tvExpress);

        adapter = new MyListAdapter(this);
        expressListView.setAdapter(adapter);
    }

    /**
     * 获取物流信息
     *
     * @param
     */
    private void startRequestForGetExpressInfo() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(OrderExpressListActivity.this);
        HashMap<String, String> params = new HashMap<>();
        params.put("orderId", String.valueOf(orderId));
        params.put("orderType", orderRefundType);
        LogUtil.i("liang", "物流信息params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETEXPRESSINFO, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "物流信息:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            Express express = ParserUtils.parserOrderExpressData(baseResult.obj);
                            if (express != null) {
                                initExpress(express);
                            }
                        }
                    } else {
                        CommonUtils.error(baseResult, OrderExpressListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    class MyListAdapter extends BaseAdapter {

        private Express bean = new Express();
        private Context mContext;

        public MyListAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public void setData(Express bean) {
            this.bean = bean;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (bean.getTraces() != null) {
                return bean.getTraces().size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if (bean.getTraces() != null) {
                return bean.getTraces().get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_listview_expresslist, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tvExpressState = (TextView) convertView.findViewById(R.id.tvExpressState);
                viewHolder.tvExpressTime = (TextView) convertView.findViewById(R.id.tvExpressTime);
                viewHolder.layoutFirstLine = convertView.findViewById(R.id.layoutFirstLine);
                viewHolder.layoutOtherLine = convertView.findViewById(R.id.layoutOtherLine);
                viewHolder.lineOther = convertView.findViewById(R.id.lineOther);
                viewHolder.lineEnd = convertView.findViewById(R.id.lineEnd);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (bean.getTraces() != null) {
                if (position == 0) {
                    viewHolder.layoutFirstLine.setVisibility(View.VISIBLE);
                    viewHolder.layoutOtherLine.setVisibility(View.GONE);
                } else {
                    viewHolder.layoutFirstLine.setVisibility(View.GONE);
                    viewHolder.layoutOtherLine.setVisibility(View.VISIBLE);
                }
                if (position == bean.getTraces().size() - 1) {
                    viewHolder.lineOther.setVisibility(View.GONE);
                    viewHolder.lineEnd.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.lineOther.setVisibility(View.VISIBLE);
                    viewHolder.lineEnd.setVisibility(View.GONE);
                }
                viewHolder.tvExpressState.setText(bean.getTraces().get(position).getAcceptStation());
                viewHolder.tvExpressTime.setText(bean.getTraces().get(position).getAcceptTime());
            }

            return convertView;
        }

        class ViewHolder {
            public TextView tvExpressState;
            private TextView tvExpressTime;
            private View layoutFirstLine, layoutOtherLine;
            private View lineOther, lineEnd;
        }
    }
}
