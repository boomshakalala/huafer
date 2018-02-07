package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.BabyAdapter;
import com.huapu.huafen.beans.Baby;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by admin on 2017/4/17.
 */

public class BabiesActivity extends BaseActivity {

    private final static String TAG = BabiesActivity.class.getSimpleName();
    @BindView(R2.id.babiesList)
    RecyclerView babiesList;
    private BabyAdapter adapter;
    private boolean isEditAble;
    private TextView tvAdd;
    private ArrayList<Baby> babies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babies);
        babies = (ArrayList<Baby>) getIntent().getSerializableExtra(MyConstants.EXTRA_BABY_LIST);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        babiesList.setLayoutManager(linearLayoutManager);
        adapter = new BabyAdapter(this, babies);
        adapter.setEmptyView(R.layout.baby_image_view);
        babiesList.setAdapter(adapter.getWrapperAdapter());
        View footerView = LayoutInflater.from(this).inflate(R.layout.baby_add, babiesList, false);
        tvAdd = (TextView) footerView.findViewById(R.id.tvAdd);
        tvAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BabiesActivity.this, EditChildActivity.class);
                intent.putExtra(MyConstants.EXTRA_BABY_LIST, adapter.getData());
                intent.putExtra(MyConstants.EXTRA_BABY_EDIT_TYPE, 2);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_EDIT_CHILD);
            }
        });
        adapter.addFootView(footerView);
        initFooter();
    }


    public void initFooter() {
        if (!ArrayUtil.isEmpty(adapter.getData())) {
            initTitleBar();
            turnOn();
        } else {
            getTitleBar().setRightText("", null);
            tvAdd.setVisibility(View.VISIBLE);
            isEditAble = false;
        }
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("我的宝宝").setRightText("编辑", new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                isEditAble = !isEditAble;
                adapter.setEditAble(isEditAble);
                turnOn();
            }
        });
    }

    private void turnOn() {
        if (isEditAble) {
            getTitleBar().getTitleTextRight().setText("完成");
            tvAdd.setVisibility(View.INVISIBLE);
        } else {
            getTitleBar().getTitleTextRight().setText("编辑");
            if (adapter.getData().size() > 2) {
                tvAdd.setVisibility(View.INVISIBLE);
            } else {
                tvAdd.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_EDIT_CHILD) {//添加
                if (data != null) {
                    babies = (ArrayList<Baby>) data.getSerializableExtra(MyConstants.EXTRA_BABY_LIST);
                    adapter.setData(babies);
                    initFooter();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        adapter.setEditAble(false);
        intent.putExtra(MyConstants.EXTRA_BABY_LIST, babies);
        setResult(RESULT_OK, intent);
        finish();
    }

}
