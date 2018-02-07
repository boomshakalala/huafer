package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.SwapAdapter;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.callbacks.ItemMoveCallBackImpl;
import com.huapu.huafen.callbacks.ItemMoveHelperApi;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import butterknife.BindView;

/**
 * Created by admin on 2017/4/28.
 */

public class ArticleSortActivity  extends BaseActivity implements ItemMoveHelperApi {

    private final static String TAG = ArticleSortActivity.class.getSimpleName();
    @BindView(R2.id.swapRecyclerView)
    RecyclerView swapRecyclerView;
    private SwapAdapter adapter ;
    private ArrayList<FloridData.Section> sections;
    private FloridData.Section emptySection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_sort);
        sections = (ArrayList<FloridData.Section>)mIntent.getSerializableExtra(MyConstants.SECTION_LIST);
        if(!ArrayUtil.isEmpty(sections)  && sections.size() > 1){
            FloridData.Section emptySection = sections.get(sections.size() - 1);
            if(TextUtils.isEmpty(emptySection.media.url) && TextUtils.isEmpty(emptySection.content)){
                this.emptySection = sections.remove(sections.size()-1);
            }
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        swapRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SwapAdapter(this,sections);
        swapRecyclerView.setAdapter(adapter);

        ItemMoveCallBackImpl mMoveCallBack = new ItemMoveCallBackImpl(this);
        mMoveCallBack.setDragStartPosition(0);
        ItemTouchHelper touchHelper = new ItemTouchHelper(mMoveCallBack);
        touchHelper.attachToRecyclerView(swapRecyclerView);
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("调整图文排序").setRightText("完成", new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(emptySection!=null){
                    sections.add(emptySection);
                }
                Intent intent = new Intent();
                intent.putExtra(MyConstants.SECTION_LIST,sections);
                LogUtil.e(TAG,sections);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        adapter.notifyItemMoved(fromPosition, toPosition);
        if(adapter.getData()!=null&&!adapter.getData().isEmpty()){
            Collections.swap(adapter.getData(),fromPosition,toPosition);
            LogUtil.e(TAG,sections);
        }
    }

    @Override
    public void onMoveStart() {

    }

    @Override
    public void onMoveEnd() {

    }
}
