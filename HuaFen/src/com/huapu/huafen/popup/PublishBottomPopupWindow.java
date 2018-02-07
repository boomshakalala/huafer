package com.huapu.huafen.popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.activity.WebViewActivity2;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Campaign;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublishBottomPopupWindow extends PopupWindow implements OnClickListener {
    private View blankSpace;
    private Context mContext;
    private EditText etPrice;
    private RelativeLayout iv_relativeLayout;
    private EditText etPastPrice;
    private TextView tvBtnConfirm;
    private ImageView ivFreeDelivery, ivPostagePaid;
    private int isFreeDelivery = 0;
    private int postagePaid = 0;
    private OnDataReachListener mListener;
    private EditText etPostage;
    private TextView tvPricingStrategy;
    private boolean mode;
    private boolean fixprice;
    private RecyclerView prices;
    PriceAdapter priceAdapter;
    private String price = "";

    private Campaign campaign;


    public PublishBottomPopupWindow(Context context, OnDataReachListener l, Campaign campaign) {
        this.mContext = context;
        this.mListener = l;
        this.campaign = campaign;
        int[] prices = null;
        if (campaign != null) {
            prices = campaign.getPrices();
        }
        if (prices != null && prices.length > 1) {
            this.fixprice = true;

        } else {
            fixprice = false;
        }
        initView(fixprice, prices);
    }

    public static void hideKeyBoard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void setPriceText(String text) {
        if (!TextUtils.isEmpty(text)) {
            etPrice.setText(text);
        }

    }

    public void setPriceInput(boolean isInput) {
        etPrice.setEnabled(isInput);
        if (!isInput) {
            etPastPrice.requestFocus();
        }
    }

    public void setPastPriceText(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.equals("-1")) {
                etPastPrice.setHint("选填");
            } else {
                etPastPrice.setText(text);
            }

        }
    }

    public void setPostageText(String text) {
        if (!TextUtils.isEmpty(text)) {
            etPostage.setText(text);
        }
    }

    public void setFreeDelivery(boolean isFreeDelivery) {
        this.isFreeDelivery = isFreeDelivery ? 1 : 0;
        ivFreeDelivery.setSelected(isFreeDelivery);
    }

    public void setPostagePaid(boolean isPostagePaid) {
        this.postagePaid = isPostagePaid ? 1 : 0;
        ivPostagePaid.setSelected(isPostagePaid);
    }

    private void initView(boolean fixprice, int[] tprices) {

        View view = null;
        if (fixprice) {
            view = LayoutInflater.from(mContext).inflate(R.layout.pop_publish_bottom_fixprice, null);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.pop_publish_bottom, null);
        }
        setContentView(view);
        view.getBackground().setAlpha(150);
        etPrice = (EditText) view.findViewById(R.id.etPrice);
        if (!fixprice) {
            iv_relativeLayout = (RelativeLayout) view.findViewById(R.id.iv_DJ);
        }
        if (fixprice) {
            prices = (RecyclerView) view.findViewById(R.id.recyclerViewPrice);
            LinearLayoutManager recyclerManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            prices.setLayoutManager(recyclerManager);
            priceAdapter = new PriceAdapter(mContext, tprices);
            prices.setAdapter(priceAdapter);
        }
        etPastPrice = (EditText) view.findViewById(R.id.etPastPrice);
        if (CommonPreference.getUserInfo().getUserLevel() == 3) {
            etPastPrice.setHint("选填");
        } else {
            etPastPrice.setHint("¥0");
        }
        etPostage = (EditText) view.findViewById(R.id.etPostage);
        tvBtnConfirm = (TextView) view.findViewById(R.id.tvBtnConfirm);
        ivFreeDelivery = (ImageView) view.findViewById(R.id.ivFreeDelivery);
        ivPostagePaid = (ImageView) view.findViewById(R.id.ivPostagePaid);
        tvBtnConfirm.setOnClickListener(this);
        if (!fixprice) {
            iv_relativeLayout.setOnClickListener(this);
        }
        if (fixprice) {
            setPostagePaid(true);
        }
        ivFreeDelivery.setOnClickListener(this);
        if (!fixprice) {
            ivPostagePaid.setOnClickListener(this);
        }
        blankSpace = view.findViewById(R.id.blankSpace);
        blankSpace.setOnClickListener(this);
        tvPricingStrategy = (TextView) view.findViewById(R.id.tvPricingStrategy);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        if (etPostage != null) {
            etPostage.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                        if ("0".equals(s.toString().trim()) || "00".equals(s.toString().trim())) {
                            etPostage.setText("");
                        } else {
                            isFreeDelivery = 0;
                            ivFreeDelivery.setSelected(false);

                            postagePaid = 0;
                            ivPostagePaid.setSelected(false);
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }


    }

    public void setOneYuanMode(boolean mode) {
        this.mode = mode;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBtnConfirm:
                doValidatePriceForm();
                break;
            case R.id.iv_DJ:
                if (mode) {
                    Intent intent = new Intent(mContext, WebViewActivity2.class);
                    intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.UNARY_RULE_WITHOUT_BTN);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                    hideKeyBoard(mContext);
                } else {
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.PEICESINGMISC);
                    intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "定价攻略");
                    mContext.startActivity(intent);
                    hideKeyBoard(mContext);
                }

                break;

            case R.id.ivFreeDelivery:
                if (ivFreeDelivery.isSelected()) {
                    setFreeDelivery(false);
                } else {
                    etPostage.getText().clear();
                    setPostagePaid(false);
                    setFreeDelivery(true);
                }
                break;
            case R.id.ivPostagePaid:
                if (ivPostagePaid.isSelected()) {
                    setPostagePaid(false);
                } else {
                    etPostage.getText().clear();
                    setFreeDelivery(false);
                    setPostagePaid(true);
                }
                break;
            case R.id.blankSpace:
                if (mListener != null) {
                    mListener.onBlankSpaceClicked();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (mode) {
            tvPricingStrategy.setText("一元专区活动规则");
            etPostage.setHint("0");
        } else {

            if (fixprice) {
                tvPricingStrategy.setText("卖价");
            } else {
                tvPricingStrategy.setText("定价攻略");
            }
            etPostage.setHint("不填则为待议");
        }

    }


    public interface OnDataReachListener {
        void onConfirmButtonClicked(String price, String pastPrice, int ivFreeDelivery, int postagePaid, String postage);

        void onBlankSpaceClicked();
    }


    private void doValidatePriceForm() {
        HashMap<String, String> params = new HashMap<>();

        if (fixprice) {
            if (!TextUtils.isEmpty(price)) {
                params.put("price", price);
            }
        } else {

            if (!TextUtils.isEmpty(etPrice.getText().toString())) {
                params.put("price", etPrice.getText().toString());
            }
        }

        if (!TextUtils.isEmpty(etPastPrice.getText().toString())) {
            params.put("pastPrice", etPastPrice.getText().toString());
        }

        if (!TextUtils.isEmpty(etPostage.getText().toString())) {
            params.put("postage", etPostage.getText().toString());
        }
        params.put("isFreeDelivery", String.valueOf(isFreeDelivery));
        params.put("freightCollect", String.valueOf(postagePaid));
        params.put("campaignId", (this.campaign != null) ? campaign.getCid() + "" : "0");

        if (mode) {
            params.put("goodsFirstCate", "20");
        }
        OkHttpClientManager.postAsyn(MyConstants.VALIDATE_PRICE_FORM, params, new OkHttpClientManager.StringCallback() {

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
                        if (mListener != null) {

                            String p = (fixprice) ? price.trim() : etPrice.getText().toString().trim();
                            mListener.onConfirmButtonClicked(p, etPastPrice.getText().toString().trim(), isFreeDelivery, postagePaid, etPostage.getText().toString());
                        }
                        CommonUtils.hideKeyBoard(mContext);
                        dismiss();
                    } else {
                        ToastUtil.toast(mContext, baseResult.msg);
                        if (baseResult.code == 611) {
                            etPrice.requestFocus();
                        } else if (baseResult.code == 613) {
                            etPrice.requestFocus();
                        } else if (baseResult.code == 614) {
                            etPostage.requestFocus();
                        } else if (baseResult.code == 1307) {
                            etPrice.requestFocus();
                        } else if (baseResult.code == 1309) {
                            etPastPrice.requestFocus();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {

        private List<String> data;
        private Context context;
        private int selectIndex = -1;
        private boolean smallrefresh;

        public PriceAdapter(Context context, int[] prices) {
            this.context = context;
            data = new ArrayList<String>();
            for (int i = 0; prices != null && i < prices.length; i++) {
                data.add("" + prices[i]);
            }
            notifyDataSetChanged();
        }

        public void setData(List<String> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public PriceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PriceViewHolder(LayoutInflater.from(context).inflate(R.layout.pop_publish_bootom_fixprice_text, parent, false));
        }

        @Override
        public void onBindViewHolder(PriceAdapter.PriceViewHolder holder, final int position) {
            final String item = data.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectIndex = position;
                    price = data.get(position);
                    notifyDataSetChanged();
                }
            });


            if (selectIndex == position) {
                holder.tvPrice.setBackgroundResource(R.drawable.filter_price_pink);
                holder.tvPrice.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.tvPrice.setBackgroundResource(R.drawable.grey_round_border_rect);
                holder.tvPrice.setTextColor(Color.parseColor("#cccccc"));
            }

            holder.tvPrice.setText("¥" + item);
        }


        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public class PriceViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tvPrice)
            TextView tvPrice;

            public PriceViewHolder(View itemView) {
                super(itemView);
                if (data != null && data.size() >= 4) {
                    int width = CommonUtils.getScreenWidth() / 5;
                    itemView.getLayoutParams().width = width;
                    //itemView.getLayoutParams().height = width;
                }
                ButterKnife.bind(this, itemView);
            }
        }
    }


}
