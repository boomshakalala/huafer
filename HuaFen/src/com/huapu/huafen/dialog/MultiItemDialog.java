package com.huapu.huafen.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.huapu.huafen.R;

/**
 * Created by admin on 2017/2/15.
 */

public class MultiItemDialog {

    private AlertDialog alertDialog ;


    public MultiItemDialog(Context context, String title,CharSequence[] items,
                           DialogInterface.OnClickListener onClickListener, boolean cancelAble) {
        if(context==null){
            throw new NullPointerException("context can not be null");
        }
        AlertDialog.Builder builder;
        if(!TextUtils.isEmpty(title)){
            View titleLayout = LayoutInflater.from(context).inflate(R.layout.multi_title_layout, null, false);
            TextView tvTitle = (TextView) titleLayout.findViewById(R.id.tvTitle);
            tvTitle.setText(title);
            builder = new AlertDialog.Builder(context,R.style.MultiChoiceDialog).
                    setAdapter(new MultiListAdapter(context,items),onClickListener).setCancelable(cancelAble).setCustomTitle(titleLayout);
        }else{
            builder = new AlertDialog.Builder(context,R.style.MultiChoiceDialog).
                    setAdapter(new MultiListAdapter(context,items),onClickListener).setCancelable(cancelAble);
        }

        this.alertDialog = builder.create();
        this.alertDialog.setCanceledOnTouchOutside(true);
        ListView listView = this.alertDialog.getListView();
        listView.setPadding(0,0,0,0);
    }

    public MultiItemDialog(Context context, String title,CharSequence[] items,
                           DialogInterface.OnClickListener onClickListener){
        this(context,title,items,onClickListener,true);
    }


    public MultiItemDialog(Context context,CharSequence[] items,
                           DialogInterface.OnClickListener onClickListener){
        this(context,null,items,onClickListener,true);
    }



    public void dismiss(){
        alertDialog.dismiss();
    }


    public void show(){
        alertDialog.show();
    }



    public class MultiListAdapter extends BaseAdapter {

        private CharSequence[] items;
        private Context context;

        public MultiListAdapter(Context context,CharSequence[] items){
            this.context = context;
            this.items=items;
        }


        @Override
        public int getCount() {
            return items==null?0:items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CharSequence item = items[position];
            ViewHolder vh;
            if(convertView==null){
                vh = new ViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.alert_list_item,null,false);
                vh.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                vh.line = convertView.findViewById(R.id.line);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();

            }

            vh.tvTitle.setText(item);

            if(position==items.length-1){
                vh.line.setVisibility(View.INVISIBLE);
            }else{
                vh.line.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        public class ViewHolder{
            public TextView tvTitle;
            public View line;
        }
    }

}
