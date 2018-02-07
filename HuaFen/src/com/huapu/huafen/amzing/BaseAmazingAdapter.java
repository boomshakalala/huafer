package com.huapu.huafen.amzing;

import android.view.View;
import android.view.ViewGroup;
import com.huapu.huafen.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAmazingAdapter<T> extends AmazingAdapter<T>{
	
	protected ArrayList<Pair<String, ArrayList<T>>> data;
	
	@Override
	public int getCount() {
		int res = 0;
        for (int i = 0; i < data.size(); i++) {
            res += data.get(i).second.size();
        }
        return res;
	}

	@Override
	public T getItem(int position) {
		int c = 0;
        for (int i = 0; i < data.size(); i++) {
            if (position >= c && position < c + data.get(i).second.size()) {
                return data.get(i).second.get(position - c);
            }
            c += data.get(i).second.size();
        }
        return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public ArrayList<Pair<String, ArrayList<T>>> getData() {
		return data;
	}

	@Override
	protected void bindSectionHeader(View view, int position,
			boolean displaySectionHeader){
		String section = getSections()[getSectionForPosition(position)];
		bindSectionHeader(view,position,section,displaySectionHeader);
		
	}
	
	protected abstract void bindSectionHeader(View view, int position
			,String section,boolean displaySectionHeader);
	
	@Override
	public  View getAmazingView(int position, View convertView, ViewGroup parent){
		String section = getSections()[getSectionForPosition(position)];
		return getAmazingView(position, convertView, parent,section);
	}
	
	public abstract View getAmazingView(int position, View convertView, ViewGroup parent,String section);
	
	@Override
	public void configurePinnedHeader(View header, int position, int alpha){
		String section = getSections()[getSectionForPosition(position)];
		configurePinnedHeader(header,position,section,alpha);
	}
	
	protected abstract void configurePinnedHeader(View header, int position,String section, int alpha);
	
	@Override
	public int getPositionForSection(int section) {
		if (section < 0) {
            section = 0;
        }
        if (section >= data.size()) {
            section = data.size() - 1;
        }
        int c = 0;
        for (int i = 0; i < data.size(); i++) {
            if (section == i) {
                return c;
            }
            c += data.get(i).second.size();
        }
        return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		int c = 0;
        for (int i = 0; i < data.size(); i++) {
            if (position >= c && position < c + data.get(i).second.size()) {
                return i;
            }
            c += data.get(i).second.size();
        }
        return -1;
	}

	@Override
	public String[] getSections() {
		String[] res = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            res[i] = data.get(i).first;
        }
        return res;
	}

}
