package com.huapu.huafen.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.StarListActivity;
import com.huapu.huafen.activity.VIPUserListActivity;
import com.huapu.huafen.adapter.HUserAdapter;
import com.huapu.huafen.beans.User;
import com.huapu.huafen.utils.CommonUtils;

import java.util.List;

/**
 * Created by admin on 2016/12/2.
 * 用于显示横排的推荐用户列表
 */
public class RecommendedUserView extends LinearLayout{

    private TextView tvMore;
    private RecyclerView recommendRecyclerView;
    private HUserAdapter adapter;
    private TYPE type = TYPE.getDefault();
    public enum TYPE{
        STAR,
        VIP
        ;

        public static TYPE getDefault(){
            return VIP;
        }
    }


    public void setType(TYPE type) {
        this.type = type;
    }

    public RecommendedUserView(Context context) {
        this(context,null);
    }

    public RecommendedUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setPadding(0,CommonUtils.dp2px(10),0,CommonUtils.dp2px(5));
        initView();
    }

    private void initView(){
        LayoutInflater inflater=(LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.recommended_user_layout,this,true);
        tvMore = (TextView) findViewById(R.id.tvMore);
        tvMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (type){
                    case STAR:
                        intent.setClass(getContext(), StarListActivity.class);
                        getContext().startActivity(intent);
                        break;
                    case VIP:
                        intent.setClass(getContext(), VIPUserListActivity.class);
                        intent.putExtra(MyConstants.EXTRA_TARGET_USER_LEVEL,"2,3");
                        getContext().startActivity(intent);
                        break;
                    default:
                        break;
                }


            }
        });

        recommendRecyclerView = (RecyclerView) findViewById(R.id.recommendRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new HUserAdapter(getContext());
        recommendRecyclerView.setAdapter(adapter);

    }

    public void setData(List<User> userList){
        adapter.setData(userList);
    }

}
