package com.huapu.huafen.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.FilterTag;
import com.huapu.huafen.beans.VolumnResult;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by danielluan on 2017/9/22.
 */

public class FilterMenuView extends LinearLayout implements View.OnClickListener {

    public boolean isEmpty = true;
    private LinearLayout itemContainer;
    private TextView tvBtnClean;
    private TextView tvBtnConfirm;
    private HashMap<String, String> params = new HashMap<String, String>();
    private OnConfirmButtonClick onConfirmButtonClick;
    private OnDismissListener listener;


    public FilterMenuView(Context context) {
        this(context, null);
    }

    public FilterMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        setOrientation(VERTICAL);
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.filter_special_layout, this, true);
        itemContainer = (LinearLayout) findViewById(R.id.itemContainer);
        tvBtnConfirm = (TextView) findViewById(R.id.tvBtnConfirm);
        tvBtnConfirm.setOnClickListener(this);
        tvBtnClean = (TextView) findViewById(R.id.tvBtnClean);
        tvBtnClean.setOnClickListener(this);
    }


    private void addItemView(View view) {
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, CommonUtils.dp2px(10), 0, 0);
        itemContainer.addView(view, params);
    }

    public void setMenuDataList(ArrayList<VolumnResult.filterMenuData> menudata) {

        for (int i = 0; menudata != null && i < menudata.size(); i++) {
            VolumnResult.filterMenuData menu = menudata.get(i);
            LogUtil.d("danielluan", "#########" + menu.attrs.name);
            if (menu.type.equals("TagFilter")) {
                TagFilterMenuItem tagFilterMenuItem = new TagFilterMenuItem(getContext());
                tagFilterMenuItem.setTitle(menu.attrs.title);
                tagFilterMenuItem.setKey(menu.attrs.name);
                tagFilterMenuItem.setOnItemClickListener(new TagFilterMenuItem.OnItemClickListener() {
                    @Override
                    public void onItemClick(String key, String value) {
                        params.put(key, value);
                    }
                });
                ArrayList<VolumnResult.filterMenuData.Attrs.Opts> opts = menu.attrs.opts;
                if (opts != null) {
                    ArrayList<FilterTag> list = new ArrayList<FilterTag>();
                    for (int j = 0; j < opts.size(); j++) {
                        VolumnResult.filterMenuData.Attrs.Opts opt = opts.get(j);
                        FilterTag tag = new FilterTag(opt.title);
                        tag.setValue(opt.value);
                        tag.setCheck(opt.selected);
                        list.add(tag);
                    }
                    tagFilterMenuItem.setData(list, menu.attrs.multiple);
                    addItemView(tagFilterMenuItem);
                }

            }
            if (menu.type.equals("RangeFilter")) {
                RangeFilterMenuItem rangeFilterMenuItem = new RangeFilterMenuItem(getContext());
                rangeFilterMenuItem.setTitle(menu.attrs.title);
                //rangeFilterMenuItem.setKey(menu.attrs.name);
                LogUtil.d("danielluan", "#########" + menu.attrs.title);
                ArrayList<VolumnResult.filterMenuData.Attrs.Opts> opts = menu.attrs.opts;
                if (opts != null) {
                    ArrayList<FilterTag> list = new ArrayList<FilterTag>();
                    for (int j = 0; j < opts.size(); j++) {
                        VolumnResult.filterMenuData.Attrs.Opts opt = opts.get(j);
                        FilterTag tag = new FilterTag(opt.title);
                        tag.setValue(opt.value);
                        tag.setCheck(opt.selected);
                        list.add(tag);
                    }
                }
                addItemView(rangeFilterMenuItem);
            }
        }

    }

    private void checkEmpty() {
        String price = params.get("price");
        String brandnew = params.get("brandnew");
        String freeShipping = params.get("freeShipping");
        String ageIds = params.get("ages");
        String tags = params.get("tags");
        if (
                (TextUtils.isEmpty(price) || "0".equals(price)) &&
                        (TextUtils.isEmpty(brandnew) || "0".equals(brandnew)) &&
                        (TextUtils.isEmpty(freeShipping) || "0".equals(freeShipping)) &&
                        TextUtils.isEmpty(ageIds) && TextUtils.isEmpty(tags)
                ) {
            isEmpty = true;
        } else {
            isEmpty = false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvBtnClean) {
            for (int i = 0; i < itemContainer.getChildCount(); i++) {
                View child = itemContainer.getChildAt(i);
                if (child instanceof TagFilterMenuItem) {
                    TagFilterMenuItem tag = (TagFilterMenuItem) child;
                    tag.clear();
                } else if (child instanceof RangeFilterMenuItem) {
                    RangeFilterMenuItem range = (RangeFilterMenuItem) child;
                    range.clear();
                }
            }
            params.put("tags", "");
            params.put("ages", "");
            params.put("price", "0");
            isEmpty = true;
        } else if (v.getId() == R.id.tvBtnConfirm) {

            RangeFilterMenuItem range = null;
            for (int i = 0; i < itemContainer.getChildCount(); i++) {
                View child = itemContainer.getChildAt(i);
                if (child instanceof RangeFilterMenuItem) {
                    range = (RangeFilterMenuItem) child;
                }
            }
            String lowPrice = "";
            String highPrice = "";
            if (range != null) {
                lowPrice = range.getLowPrice();
                highPrice = range.getHighPrice();
                if (!TextUtils.isEmpty(lowPrice) && !TextUtils.isEmpty(highPrice)) {
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
                } else if (!TextUtils.isEmpty(lowPrice) && TextUtils.isEmpty(highPrice)) {
//                ToastUtil.toast(getContext(), "请填写商品最大价格");
                    params.put("price", lowPrice + ":");
                } else if (TextUtils.isEmpty(lowPrice) && !TextUtils.isEmpty(highPrice)) {
//                ToastUtil.toast(getContext(), "请填写商品最小价格");
                    params.put("price", ":" + highPrice);
                }
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


    public void setOnConfirmButtonClick(OnConfirmButtonClick onConfirmButtonClick) {
        this.onConfirmButtonClick = onConfirmButtonClick;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
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


}

