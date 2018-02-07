package com.huapu.huafen.adapter.util;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
/**
 * 针对ListView的万能适配器 ，(ListView的万能适配器)
 * 以后我们自己的adapter只需要继承AutoAdapter 
 * 实现getLayoutID 和getView33 即可
 * 
 */
public abstract class AutoAdapter extends MyBaseAdapter{

	/*public AutoAdapter(Context context, List<?> list, int layoutID) {
        super(context, list);

    }*/
	public AutoAdapter(Context context, List<?> list) {
        super(context, list);

    }

    public abstract int getLayoutID(int position);



    @Override
    public View getView33(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(getLayoutID(position), parent, false);
        }
        getView33(position, convertView, ViewHolder.getViewHolder(convertView));
        return convertView;
    }


    public abstract void getView33(int position, View v, ViewHolder vh);


}
