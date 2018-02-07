package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.Age;
import com.huapu.huafen.recycler.base.ViewHolder;
import com.huapu.huafen.recycler.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.List;

public class SearchFilterAdapter extends RecyclerView.Adapter<SearchFilterAdapter.SearchViewHolder> {

	private List<Age> mData = new ArrayList<Age>();
	private Context mContext;
	private ArrayList<Age> selectAge = new ArrayList<Age>();
	private HeaderAndFooterWrapper wrapper;

	public List<Age> getmData() {
		return mData;
	}

	public void setmData(List<Age> mData) {
		this.mData = mData;
	}

	public ArrayList<Age> getSelectAge() {
		return selectAge;
	}

	public void setSelectAge(ArrayList<Age> selectAge) {
		this.selectAge = selectAge;
	}

	public HeaderAndFooterWrapper getWrapper() {
		return wrapper;
	}

	public void setWrapper(HeaderAndFooterWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public SearchFilterAdapter(Context context, List<Age> data) {
		this.mData = data;
		this.mContext=context;
		wrapper = new HeaderAndFooterWrapper(this);
	}

	public void setData(List<Age> data){
		this.mData=data;
		notifyDataSetChanged();
	}
	
	public class SearchViewHolder extends ViewHolder {
		public TextView tvFilterAge;

		public SearchViewHolder(Context context, View itemView) {
			super(context, itemView);
			tvFilterAge = (TextView) itemView.findViewById(R.id.tvFilterAge);

		}

	}

	public void notifySelect(){
		notifyDataSetChanged();
		wrapper.notifyDataSetChanged();
	}
	@Override
	public int getItemCount() {
		return mData.size();
	}

	@Override
	public void onBindViewHolder(final SearchViewHolder vh, int position) {
		final Age item = mData.get(position);
		vh.tvFilterAge.setText(item.getAgeTitle());
		if(item.getIsSelect()) {
			vh.tvFilterAge.setSelected(true);
		} else {
			vh.tvFilterAge.setSelected(false);
		}
		vh.itemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(item.getAgeId() == 8) {
					if(item.getIsSelect()) {
						item.setIsSelect(false);
						selectAge.remove(item);
					} else {
						for(Age age : mData) {
							age.setIsSelect(false);
							item.setIsSelect(true);
							selectAge.clear();
							selectAge.add(item);
						}
					}
				} else {
					for(Age age : mData) {
						if(age.getAgeId() == 8) {
							age.setIsSelect(false);
							selectAge.remove(age);
							break;
						}
					}
					if(item.getIsSelect()) {
						item.setIsSelect(false);
						selectAge.remove(item);
					} else {
						item.setIsSelect(true);
						selectAge.add(item);
					}
				}
				notifyDataSetChanged();
				wrapper.notifyDataSetChanged();
			}
		});
	}


	@Override
	public SearchViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
		SearchViewHolder vh = new SearchViewHolder(parent.getContext(),
				LayoutInflater.from(parent.getContext()).
				inflate(R.layout.item_listview_filter_age, parent, false));
		return vh;
	}
}
