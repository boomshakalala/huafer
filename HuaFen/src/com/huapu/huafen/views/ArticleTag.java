package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/29.
 */

public class ArticleTag extends LinearLayout implements View.OnClickListener {

    @BindView(R2.id.tvTag)
    TextView tvTag;
    @BindView(R2.id.ivLeftDelete)
    ImageView ivLeftDelete;
    @BindView(R2.id.ivRightDelete)
    ImageView ivRightDelete;
    public FloridData.Tag tag;


    public ArticleTag(Context context) {
        this(context,null);
    }

    public ArticleTag(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.tag_article,this,true);
        ButterKnife.bind(this);
        tvTag.setMaxWidth(CommonUtils.getScreenWidth()/3);
    }


    public void setData(final FloridData.Tag tag , final boolean editAble){
        setData(tag,editAble,true);
    }

    public void setData(final FloridData.Tag tag , final boolean editAble , boolean isClickAble){

        this.tag = tag;

        if(tag!=null){
            if(!TextUtils.isEmpty(tag.title)){
                int orientation = tag.orientation;
                if(orientation == 0){
                    if(!TextUtils.isEmpty(tag.txt)){
                        tvTag.setText(tag.txt);
                    }else{
                        tvTag.setText(tag.title);
                    }
                    setBackgroundResource(R.drawable.article_scale_bg_right);
                    if(editAble){
                        ivRightDelete.setVisibility(VISIBLE);
                        ivLeftDelete.setVisibility(GONE);
                    }else{
                        ivRightDelete.setVisibility(GONE);
                        ivLeftDelete.setVisibility(GONE);
                    }

                }else{
                    if(!TextUtils.isEmpty(tag.txt)){
                        tvTag.setText(tag.txt);
                    }else{
                        tvTag.setText(tag.title);
                    }
                    setBackgroundResource(R.drawable.article_scale_bg_left);
                    if(editAble){
                        ivRightDelete.setVisibility(GONE);
                        ivLeftDelete.setVisibility(VISIBLE);
                    }else{
                        ivRightDelete.setVisibility(GONE);
                        ivLeftDelete.setVisibility(GONE);
                    }
                }
            }

            if(isClickAble){
                tvTag.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(editAble){
                            if(tag.orientation == 0){
                                tag.orientation = 1;
                                setBackgroundResource(R.drawable.article_scale_bg_left);
                                ivRightDelete.setVisibility(GONE);
                                ivLeftDelete.setVisibility(VISIBLE);
                            }else{
                                tag.orientation = 0;
                                setBackgroundResource(R.drawable.article_scale_bg_right);
                                ivRightDelete.setVisibility(VISIBLE);
                                ivLeftDelete.setVisibility(GONE);
                            }
                        }else{
                            if(onHandleTagListener !=null){
                                boolean flag = onHandleTagListener.interceptorClick();
                                if(flag){
                                    return;
                                }
                            }
                            ActionUtil.dispatchAction(getContext(),tag.target ,tag.type);
                        }

                    }
                });
            }


            tvTag.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if(editAble){
                        if(onHandleTagListener !=null){
                            onHandleTagListener.onDeleteTag();
                        }
                    }
                    return false;
                }
            });


            ivLeftDelete.setOnClickListener(this);
            ivRightDelete.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {
        if(v == ivLeftDelete|| v == ivRightDelete){
            if(onHandleTagListener !=null){
                onHandleTagListener.onDeleteTag();
            }
        }
    }

    public interface OnHandleTagListener {
        void onDeleteTag();
        boolean interceptorClick();
    }

    private OnHandleTagListener onHandleTagListener;

    public void setOnHandleTagListener(OnHandleTagListener onHandleTagListener) {
        this.onHandleTagListener = onHandleTagListener;
    }

    public abstract static class OnSimpleHandleTagListener implements OnHandleTagListener{

        @Override
        public void onDeleteTag() {

        }

        @Override
        public boolean interceptorClick() {
            return false;
        }
    }
}
