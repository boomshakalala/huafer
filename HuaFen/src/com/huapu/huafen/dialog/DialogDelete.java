package com.huapu.huafen.dialog;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.huapu.huafen.R;

import java.util.List;


/**
 * 删除照片对话框
 *
 * @author DengBin
 */
@SuppressLint("ValidFragment")
public class DialogDelete extends DialogFragment {
    private AdapterView.OnItemClickListener mOnItemClickListener;

    private ListView listView;

    private List<String> items;

    private Context mContext;

    public DialogDelete(Context context,List<String> items) {
        this.items = items;
        mContext = context;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Window win = getDialog().getWindow();
        getDialog().setCanceledOnTouchOutside(true);
        // 显示在底部
        win.setGravity(Gravity.BOTTOM);
        // 去掉padding
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setBackgroundDrawableResource(R.color.translucent);
        LayoutParams lp = win.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        int style = DialogFragment.STYLE_NO_TITLE;
        int theme = R.style.dialog_translate;
        setStyle(style, theme);
    }

    private void initView(View view) {
        view.findViewById(R.id.btnDialogCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new MyAdapter());
        if (mOnItemClickListener != null)
            listView.setOnItemClickListener(mOnItemClickListener);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_dialog,null);
                holder.textView = (TextView) convertView;
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 0){
                holder.textView.setBackgroundResource(R.drawable.bg_top_corner_white);
            }else if (position == items.size()-1){
                holder.textView.setBackgroundResource(R.drawable.bg_bottom_corner_white);
            }else {
                holder.textView.setBackgroundResource(R.color.white);
            }
            holder.textView.setText(items.get(position));

            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }
}
