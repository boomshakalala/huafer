package com.huapu.huafen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Classification;
import com.huapu.huafen.utils.ImageLoader;

import java.util.List;

/**
 * 分类中的 商品一级分类的adapter
 */
public class ClassGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<Classification> lists;

    public ClassGridViewAdapter(Context context, List<Classification> list) {
        this.context = context;
        this.lists = list;
    }

    public void setData(List<Classification> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_gridview_fragment_class, null);
            holder = new ViewHolder();
            holder.tvFirstName = (TextView) convertView.findViewById(R.id.tvFirstName);
            holder.ivFirstClass = (SimpleDraweeView) convertView.findViewById(R.id.ivFirstClass);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Classification firstBean = lists.get(position);
        holder.tvFirstName.setText(firstBean.getClassificationName());
        
        ImageLoader.resizeMiddle(holder.ivFirstClass, firstBean.getClassificationIcon(), 1);
        return convertView;
    }


    class ViewHolder {
        private SimpleDraweeView ivFirstClass;
        private TextView tvFirstName, tvFirstSlogan;

    }

}
