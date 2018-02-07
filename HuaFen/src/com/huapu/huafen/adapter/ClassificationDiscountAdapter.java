package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.CodeValuePair;
import com.huapu.huafen.beans.Discount;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.Pair;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/14.
 */

public class ClassificationDiscountAdapter extends RecyclerView.Adapter<ClassificationDiscountAdapter.ClassificationDiscountViewHolder>{


    private Context context;
    private List<Discount> data ;

    public ClassificationDiscountAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
        data.add(new Discount("1","一折"));
        data.add(new Discount("2","二折"));
        data.add(new Discount("3","三折"));
        data.add(new Discount("4","四折"));
        data.add(new Discount("5","五折"));
    }

    @Override
    public ClassificationDiscountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ClassificationDiscountViewHolder viewHolder = new ClassificationDiscountViewHolder(LayoutInflater.from(context).inflate(R.layout.item_discount, parent, false));
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)viewHolder.itemView.getLayoutParams();
        params.width = CommonUtils.getScreenWidth()/5;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ClassificationDiscountViewHolder holder, int position) {
        final Discount item = data.get(position);
        if(item!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    item.isCheck = !item.isCheck;
                    String discount = getDiscount();
                    if(mOnDiscountClickListener!=null){
                        mOnDiscountClickListener.onDiscountResponse(discount);
                    }
                    notifyDataSetChanged();
                }
            });

            holder.tvDiscount.setText(item.value);

            if(item.isCheck){
                holder.tvDiscount.setTextColor(context.getResources().getColor(R.color.base_pink));
                holder.tvDiscount.setBackgroundResource(R.drawable.pink_border_rect);
            }else{
                holder.tvDiscount.setTextColor(context.getResources().getColor(R.color.text_color_gray));
                holder.tvDiscount.setBackgroundResource(R.drawable.gray_border_rect);
            }
        }



    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    private String getDiscount(){
        StringBuilder sb = new StringBuilder("");
        String discount;
        for(Discount d:data){
            if(d.isCheck){
                sb.append(d.key).append(",");
            }
        }
        if(!TextUtils.isEmpty(sb.toString())){
            sb.deleteCharAt(sb.length()-1);
            discount = sb.toString();
        }else {
            discount = "1,2,3,4,5";
        }
        return discount;
    }

    public class ClassificationDiscountViewHolder extends RecyclerView.ViewHolder{

        @BindView(R2.id.tvDiscount) TextView tvDiscount;

        public ClassificationDiscountViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnDiscountClickListener{
        void onDiscountResponse(String discount);
    }

    private OnDiscountClickListener mOnDiscountClickListener;


    public void setOnDiscountClickListener(OnDiscountClickListener onDiscountClickListener) {
        this.mOnDiscountClickListener = onDiscountClickListener;
    }
}
