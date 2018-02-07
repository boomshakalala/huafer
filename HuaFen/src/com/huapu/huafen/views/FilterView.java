package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Age;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.FilterPrice;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/11/17.
 */
public class FilterView extends LinearLayout implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public boolean isEmpty = true;
    private RecyclerView recyclerView;
    private FilterPriceAdapter adapter;
    private TextView tvBtnClean;
    private TextView tvBtnConfirm;
    private EditText etPriceLow;
    private EditText etPriceHigh;
    private CheckBox chbNew;
    private CheckBox chbPostage;
    private CheckBox chbVIP;
    private CheckBox chbStar;
    private LinearLayout llFitAge;
    private HashMap<String, String> params = new HashMap<String, String>();
    private ArrayList<FilterPrice> prices = new ArrayList<FilterPrice>();
    private CheckBox space0;
    private CheckBox space1;
    private RecyclerView recyclerViewAge;
    private FilterAgeAdapter filterAgeAdapter;
    private STATE state = STATE.VIP;
    private OnConfirmButtonClick onConfirmButtonClick;
    private OnDismissListener listener;
    private boolean isShowAge = true;

    public FilterView(Context context) {
        this(context, null);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setState(STATE state) {
        this.state = state;
    }

    private void init() {
        prices = new ArrayList<FilterPrice>();
        FilterPrice p0 = new FilterPrice(":100", "100元以下");
        FilterPrice p1 = new FilterPrice("100:300", "100~300元");
        FilterPrice p2 = new FilterPrice("300:500", "300~500元");
        FilterPrice p3 = new FilterPrice("500:1000", "500~1000元");
        FilterPrice p4 = new FilterPrice("1000:3000", "1000~3000元");
        FilterPrice p5 = new FilterPrice("3000:", "3000元以上");
        prices.add(p0);
        prices.add(p1);
        prices.add(p2);
        prices.add(p3);
        prices.add(p4);
        prices.add(p5);

        setOrientation(VERTICAL);
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.filter_layout, this, true);
        chbNew = (CheckBox) findViewById(R.id.chbNew);
        chbNew.setOnCheckedChangeListener(this);
        chbPostage = (CheckBox) findViewById(R.id.chbPostage);
        chbPostage.setOnCheckedChangeListener(this);
        chbVIP = (CheckBox) findViewById(R.id.chbVIP);
        chbVIP.setOnCheckedChangeListener(this);
        chbStar = (CheckBox) findViewById(R.id.chbStar);
        chbStar.setOnCheckedChangeListener(this);
        space0 = (CheckBox) findViewById(R.id.space0);
        space1 = (CheckBox) findViewById(R.id.space1);

        llFitAge = (LinearLayout) findViewById(R.id.llFitAge);
        recyclerViewAge = (RecyclerView) findViewById(R.id.recyclerViewAge);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerViewAge.setLayoutManager(gridLayoutManager);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FilterPriceAdapter();
        recyclerView.setAdapter(adapter);
        tvBtnConfirm = (TextView) findViewById(R.id.tvBtnConfirm);
        tvBtnConfirm.setOnClickListener(this);
        tvBtnClean = (TextView) findViewById(R.id.tvBtnClean);
        tvBtnClean.setOnClickListener(this);
        etPriceLow = (EditText) findViewById(R.id.etPriceLow);
        etPriceHigh = (EditText) findViewById(R.id.etPriceHigh);
        etPriceLow.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    adapter.clearChecks();
                }

            }

        });
        etPriceHigh.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    adapter.clearChecks();
                }
            }

        });

        initState();

    }

    public void initState() {
        switch (state) {
            case VIP://VIP专区
            case STAR://明星专区
                chbNew.setVisibility(VISIBLE);
                chbPostage.setVisibility(VISIBLE);
                chbVIP.setVisibility(GONE);
                chbStar.setVisibility(GONE);
                space0.setVisibility(INVISIBLE);
                space1.setVisibility(INVISIBLE);
                break;
            case NEW://全新商品
                chbNew.setVisibility(GONE);
                chbPostage.setVisibility(VISIBLE);
                chbVIP.setVisibility(VISIBLE);
                chbStar.setVisibility(VISIBLE);
                space0.setVisibility(INVISIBLE);
                LinearLayout.LayoutParams paramsNew = (LayoutParams) space0.getLayoutParams();
                paramsNew.rightMargin = 0;
                space1.setVisibility(GONE);
                if (isShowAge) {
                    startRequestForGetAgeList();
                }
                break;
            case SEARCH_RESULT://搜索结果
                chbNew.setVisibility(VISIBLE);
                chbPostage.setVisibility(VISIBLE);
                chbVIP.setVisibility(VISIBLE);
                chbStar.setVisibility(VISIBLE);
                LinearLayout.LayoutParams params = (LayoutParams) chbPostage.getLayoutParams();
                params.rightMargin = 0;
                space0.setVisibility(GONE);
                space1.setVisibility(GONE);
                if (isShowAge) {
                    startRequestForGetAgeList();
                }
                break;
            case CLASSIFICATION_VIP:
                chbNew.setVisibility(VISIBLE);
                chbPostage.setVisibility(VISIBLE);
                chbVIP.setVisibility(GONE);
                chbStar.setVisibility(GONE);
                space0.setVisibility(INVISIBLE);
                space1.setVisibility(INVISIBLE);
                if (isShowAge) {
                    startRequestForGetAgeList();
                }
                break;
            default:
                break;

        }
    }

    public void initStateNew() {
        switch (state) {
            case VIP://VIP专区
                chbNew.setVisibility(VISIBLE);
                chbPostage.setVisibility(VISIBLE);
                chbVIP.setVisibility(GONE);
                chbStar.setVisibility(VISIBLE);
                space0.setVisibility(INVISIBLE);
                space1.setVisibility(INVISIBLE);
                break;
            case NEW://全新商品
                chbNew.setVisibility(GONE);
                chbPostage.setVisibility(VISIBLE);
                chbVIP.setVisibility(VISIBLE);
                chbStar.setVisibility(VISIBLE);
                space0.setVisibility(INVISIBLE);
                LinearLayout.LayoutParams paramsNew = (LayoutParams) space0.getLayoutParams();
                paramsNew.rightMargin = 0;
                space1.setVisibility(GONE);
                startRequestForGetAgeList();
                break;
            case CLASSIFICATION_VIP:
                chbNew.setVisibility(VISIBLE);
                chbPostage.setVisibility(VISIBLE);
                chbVIP.setVisibility(GONE);
                chbStar.setVisibility(VISIBLE);
                space0.setVisibility(INVISIBLE);
                space1.setVisibility(INVISIBLE);
                startRequestForGetAgeList();
                break;
            default:
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == chbNew) {
            if (isChecked) {
                params.put("brandnew", "1");
            } else {
                params.put("brandnew", "0");
            }
        } else if (buttonView == chbPostage) {
            if (isChecked) {
                params.put("freeShipping", "1");
            } else {
                params.put("freeShipping", "0");
            }
        } else if (buttonView == chbVIP) {
            if (isChecked) {
                params.put("vipGoods", "1");
            } else {
                params.put("vipGoods", "0");
            }
        } else if (buttonView == chbStar) {
            if (isChecked) {
                params.put("starGoods", "1");
            } else {
                params.put("starGoods", "0");
            }
        }
        checkEmpty();
    }

    public CheckBox getCheckBox() {
        return chbStar;
    }

    private void checkEmpty() {
        String price = params.get("price");
        String brandnew = params.get("brandnew");
        String freeShipping = params.get("freeShipping");
        String ageIds = params.get("ageIds");
        String vipGoods = params.get("vipGoods");
        String starGoods = params.get("starGoods");
        if (
                (TextUtils.isEmpty(price) || "0".equals(price)) &&
                        (TextUtils.isEmpty(brandnew) || "0".equals(brandnew)) &&
                        (TextUtils.isEmpty(freeShipping) || "0".equals(freeShipping)) &&
                        (TextUtils.isEmpty(vipGoods) || "0".equals(vipGoods)) &&
                        (TextUtils.isEmpty(starGoods) || "0".equals(starGoods)) &&
                        TextUtils.isEmpty(ageIds)
                ) {
            isEmpty = true;
        } else {
            isEmpty = false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvBtnClean) {
            etPriceHigh.setText("");
            etPriceLow.setText("");
            adapter.clearChecks();
            if (filterAgeAdapter != null) {
                filterAgeAdapter.clearChecks();
            }
            chbNew.setChecked(false);
            chbPostage.setChecked(false);
            chbStar.setChecked(false);
            chbVIP.setChecked(false);
            params.put("price", "0");
            params.put("brandnew", "0");
            params.put("freeShipping", "0");
            params.put("vipGoods", "0");
            params.put("starGoods", "0");
            params.put("ageIds", "");
            isEmpty = true;
        } else if (v.getId() == R.id.tvBtnConfirm) {
            String lowPrice = etPriceLow.getText().toString();
            String highPrice = etPriceHigh.getText().toString();
            /*if(TextUtils.isEmpty(lowPrice)&&TextUtils.isEmpty(highPrice)&&adapter.getCheckFilterPrice()==null){
                ToastUtil.toast(getContext(),"请选择价格区间或填写价格区间");
                return;
            }else */
            if (TextUtils.isEmpty(lowPrice) && TextUtils.isEmpty(highPrice) && adapter.getCheckFilterPrice() != null) {
                FilterPrice price = adapter.getCheckFilterPrice();
                params.put("price", price.value);
            } else if (!TextUtils.isEmpty(lowPrice) && !TextUtils.isEmpty(highPrice) && adapter.getCheckFilterPrice() == null) {

                try {
                    int high = Integer.parseInt(highPrice);
                    int low = Integer.parseInt(lowPrice);
                    if (low >= high) {
                        ToastUtil.toast(getContext(), "商品最低价格不能大于或等于商品最高价格");
                        return;
                    } else {
                        params.put("price", lowPrice + ":" + highPrice);
                    }
                } catch (Exception e) {
                    ToastUtil.toast(getContext(), "填写的价格格式不符");
                    return;
                }
            } else if (!TextUtils.isEmpty(lowPrice) && TextUtils.isEmpty(highPrice) && adapter.getCheckFilterPrice() == null) {
//                ToastUtil.toast(getContext(), "请填写商品最大价格");
                params.put("price", lowPrice + ":");
            } else if (TextUtils.isEmpty(lowPrice) && !TextUtils.isEmpty(highPrice) && adapter.getCheckFilterPrice() == null) {
//                ToastUtil.toast(getContext(), "请填写商品最小价格");
                params.put("price", ":" + highPrice);
            }


            checkEmpty();
            if (onConfirmButtonClick != null) {
                onConfirmButtonClick.onClick(params);
            }

            if (listener != null) {
                listener.close();
            }
        }
    }

    private void startRequestForGetAgeList() {
        if (!CommonUtils.isNetAvaliable(getContext())) {
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        OkHttpClientManager.postAsyn(MyConstants.GETAGELIST, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        // 调用刷新完成
                        LogUtil.i("liang", "宝宝年龄段列表:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response,
                                    BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
                                    List<Age> list = ParserUtils.parserAgeListData(baseResult.obj);
                                    if (list != null) {
                                        filterAgeAdapter = new FilterAgeAdapter();
                                        filterAgeAdapter.setData(list);
                                        recyclerViewAge.setAdapter(filterAgeAdapter);
                                        llFitAge.setVisibility(VISIBLE);
                                    }
                                }
                            } else {
                                CommonUtils.error(baseResult, (Activity) getContext(), "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    public void setOnConfirmButtonClick(OnConfirmButtonClick onConfirmButtonClick) {
        this.onConfirmButtonClick = onConfirmButtonClick;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

    public void setAge(boolean flag) {
        this.isShowAge = flag;
        llFitAge.setVisibility(flag ? VISIBLE : GONE);
    }


    public enum STATE {
        NEW,
        VIP,
        STAR,
        SEARCH_RESULT,
        CLASSIFICATION_VIP;
    }

    public interface OnDismissListener {
        void close();
    }

    public interface OnConfirmButtonClick {
        void onClick(Map<String, String> params);
    }

    public class FilterPriceAdapter extends RecyclerView.Adapter<FilterPriceHolder> {

        @Override
        public FilterPriceHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new FilterPriceHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_filter_price, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(FilterPriceHolder filterPriceHolder, final int position) {
            final FilterPrice item = prices.get(position);
            filterPriceHolder.itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    setCheck(position);
                    etPriceHigh.setText("");
                    etPriceLow.setText("");
                }
            });

            filterPriceHolder.tvPrice.setText(item.price);

            if (item.isCheck) {
                filterPriceHolder.tvPrice.setBackgroundResource(R.drawable.filter_price_pink);
                filterPriceHolder.tvPrice.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                filterPriceHolder.tvPrice.setBackgroundResource(R.drawable.filter_price_light_grey);
                filterPriceHolder.tvPrice.setTextColor(Color.parseColor("#333333"));
            }

            if (position % 3 == 2) {
                filterPriceHolder.itemView.setPadding(0, CommonUtils.dp2px(10), 0, 0);
            } else {
                filterPriceHolder.itemView.setPadding(0, CommonUtils.dp2px(10), CommonUtils.dp2px(5), 0);
            }
        }


        public FilterPrice getCheckFilterPrice() {
            FilterPrice result = null;
            for (int i = 0; i < prices.size(); i++) {
                FilterPrice price = prices.get(i);
                if (price.isCheck) {
                    result = price;
                }
            }

            return result;
        }

        public void clearChecks() {
            for (FilterPrice price : prices) {
                price.isCheck = false;
            }

            notifyDataSetChanged();
            params.put("price", "0");
        }

        public void setCheck(int position) {
            for (int i = 0; i < prices.size(); i++) {
                FilterPrice price = prices.get(i);
                if (i == position) {
                    price.isCheck = true;
                } else {
                    price.isCheck = false;
                }
            }
            notifyDataSetChanged();
        }


        @Override
        public int getItemCount() {
            return prices.size();
        }


    }

    public class FilterPriceHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView tvPrice;

        public FilterPriceHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }

    public class FilterAgeAdapter extends RecyclerView.Adapter<FilterPriceHolder> {

        private List<Age> data;

        @Override
        public FilterPriceHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new FilterPriceHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_filter_price, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(FilterPriceHolder filterPriceHolder, final int position) {
            final Age item = data.get(position);
            filterPriceHolder.itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    item.isCheck = !item.isCheck;
                    setCheck(position, item.isCheck);
                }
            });

            filterPriceHolder.tvPrice.setText(item.getAgeTitle());

            if (item.isCheck) {
                filterPriceHolder.tvPrice.setBackgroundResource(R.drawable.filter_price_pink);
                filterPriceHolder.tvPrice.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                filterPriceHolder.tvPrice.setBackgroundResource(R.drawable.filter_price_light_grey);
                filterPriceHolder.tvPrice.setTextColor(Color.parseColor("#333333"));
            }

            if (position % 4 == 3) {
                filterPriceHolder.itemView.setPadding(0, CommonUtils.dp2px(10), 0, 0);
            } else {
                filterPriceHolder.itemView.setPadding(0, CommonUtils.dp2px(10), CommonUtils.dp2px(5), 0);
            }
        }


        public void clearChecks() {
            if (ArrayUtil.isEmpty(data)) {
                return;
            }
            for (Age age : data) {
                age.isCheck = false;
            }

            notifyDataSetChanged();
        }

        public void setCheck(int position, boolean isCheck) {
            if (ArrayUtil.isEmpty(data)) {
                params.put("ageIds", "");
                return;
            }
            if (position == data.size() - 1) {
                for (int i = 0; i < data.size(); i++) {
                    Age age = data.get(i);
                    if (position == i) {
                        age.isCheck = true;
                    } else {
                        age.isCheck = false;
                    }
                }
            } else {
                for (int i = 0; i < data.size() - 1; i++) {
                    if (position == i) {
                        Age age = data.get(i);
                        age.isCheck = isCheck;
                    }
                }

                data.get(data.size() - 1).isCheck = false;

            }

            StringBuilder sb = new StringBuilder();
            for (Age age : data) {
                if (age.isCheck) {
                    sb.append(age.getAgeId()).append(",");
                }

            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

            params.put("ageIds", sb.toString());
            LogUtil.e("FilterView", params.get("ageIds") + "");
            notifyDataSetChanged();
        }


        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }


        public void setData(List<Age> data) {
            this.data = data;
            notifyDataSetChanged();
        }
    }
}
