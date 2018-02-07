package com.huapu.huafen.adapter;

import java.util.LinkedList;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.SearchGoodsListActivity;
import com.huapu.huafen.activity.SearchPersonalListActivity;
import com.huapu.huafen.beans.CodeValuePair;
import com.huapu.huafen.recycler.base.ViewHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchHistoryAdapter extends CommonWrapper<SearchHistoryAdapter.SearchViewHolder> {

	private LinkedList<CodeValuePair> mData = new LinkedList<>();
	private Context mContext;

	public SearchHistoryAdapter(Context context) {
		this.mContext=context;
	}

	public void setData(LinkedList<CodeValuePair> data){
		this.mData=data;
		notifyWrapperDataSetChanged();
	}
	
	public class SearchViewHolder extends ViewHolder {
		public TextView tvSearch;
		
		public SearchViewHolder(Context context, View itemView) {
			super(context, itemView);
			tvSearch = (TextView) itemView.findViewById(R.id.tvSearch);
		}

	}

	public void clearData(){
		mData.clear();
		notifyWrapperDataSetChanged();
	}
	
	@Override
	public int getItemCount() {
		return mData == null?0:mData.size();
	}

	@Override
	public void onBindViewHolder(SearchViewHolder vh, int position) {
		final CodeValuePair item = mData.get(position);
		if(item.code == 1){
			vh.tvSearch.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
		}else{
			vh.tvSearch.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icon_search_personal,0);
		}

		if(!TextUtils.isEmpty(item.value)){
			vh.tvSearch.setText(item.value);
		}
		vh.itemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent;
				if(item.code==1){
					intent = new Intent(mContext, SearchGoodsListActivity.class);
				}else{
					intent = new Intent(mContext, SearchPersonalListActivity.class);
				}
				intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, item.value);
				((Activity)mContext).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_SEARCH);
			}
		});
	}


	@Override
	public SearchViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
		return new SearchViewHolder(parent.getContext(),
				LayoutInflater.from(parent.getContext()).
						inflate(R.layout.search_history_item, parent, false));
	}
}
