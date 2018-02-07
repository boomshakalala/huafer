package com.huapu.huafen.adapter.pages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CommonWrapper;
import com.huapu.huafen.beans.pages.ComponentBean;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.component.ViewPagerCom;

import java.util.List;

/**
 * 每日上新
 * Created by admin on 2016/12/3.
 */
public class EverydayFreshAdapter extends CommonWrapper<EverydayFreshAdapter.ComponentHolder> {

    private Context context;
    private List<ComponentBean> data;

    public EverydayFreshAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<ComponentBean> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        ComponentBean bean = data.get(position);
        switch (bean.getType()) {
            case "Banner":
                type = 0;
                break;
            case "GoodsShowcaseS1":
                type = 1;
                break;
            case "GoodsShowcaseS2":
                type = 2;
                break;
            case "Button":
                type = 3;
                break;
            default:
                break;
        }
        return type;
    }

    @Override
    public ComponentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.item_everyday_fresh_banner, parent, false);
                break;
            case 1:
                view = inflater.inflate(R.layout.item_everyday_fresh_cas1, parent, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.item_everyday_fresh_cas2, parent, false);
                break;
            case 3:
                view = inflater.inflate(R.layout.item_everyday_fresh_button, parent, false);
                break;
            default:
                break;
        }

        return new ComponentHolder(view);
    }

    @Override
    public void onBindViewHolder(final ComponentHolder holder, final int position) {
        final ComponentBean item = data.get(position);
        if (item == null || TextUtils.isEmpty(item.getAttrs())) {
            return;
        }

        holder.bindData(item);
    }

    public class ComponentHolder extends RecyclerView.ViewHolder {
        public ComponentHolder(View itemView) {
            super(itemView);
        }

        public void bindData(ComponentBean bean) {
            if (TextUtils.isEmpty(bean.getAttrs()))
                return;
            switch (bean.getType()) {
                case "Banner":
                    ViewUtil.updateBanner((ConvenientBanner) itemView, bean.getBanner());
                    break;
                case "GoodsShowcaseS1":
                    ((ViewPagerCom) itemView).setData(bean.getCaseBean());
                    break;
                case "GoodsShowcaseS2":

                    break;
                case "Button":

                    break;
                default:
                    break;
            }
        }
    }

}
