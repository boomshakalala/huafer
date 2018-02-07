package com.huapu.huafen.recycler.base;

import android.view.View;

public interface ItemViewDelegate<T> {

	int getItemViewLayoutId();

	boolean isForViewType(T item, int position);

	void convert(ViewHolder holder, T t, int position);

}
