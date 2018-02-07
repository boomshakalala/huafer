package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.adapter.ThemeIconFilterAdapter;
import com.huapu.huafen.beans.IconFilter;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.fragment.RecommendListFragment;
import com.huapu.huafen.utils.CommonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;

/**
 * Created by danielluan on 2017/9/25.
 */

public class FeatureGridView extends LinearLayout {

    @BindView(R.id.tablelayout)
    TableLayout tablelayout;

    @BindView(R.id.title)
    FeatureTitleBar title;

    private ThemeIconFilterAdapter iconfilterAdapter;

    private OnItemClickListener listener;

    private final static int numofColumn = 3;

    ArrayList<Item> data = new ArrayList<Item>();

    private Context context;

    public FeatureGridView(@NonNull Context context) {
        this(context, null);
    }

    public FeatureGridView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public FeatureTitleBar getTitleBar() {
        return title;
    }


    public void setTitle(String ttitle) {

        title.setTvTitle(ttitle);

    }

    public void setSubTitle(String ttitle) {

        title.setSubTitle(ttitle);

    }


    public void setNavigationtitle(String ttitle) {

        title.setNavigationtitle(ttitle);
    }

    private void init() {

        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater.from(getContext()).inflate(R.layout.feature_table_layout, this, true);
        ButterKnife.bind(this);
        title.setTvTitle("VIP优选");
        title.setSubTitle("超值好评");
        title.setNavigationtitle("查看更多");
        title.setIndicator(R.drawable.right_enter);
        refresh();
    }


    private void refresh() {

        tablelayout.removeAllViews();

        int width = (int) (226.0f / 750.0f * CommonUtils.getScreenWidth());

        int margin = (CommonUtils.getScreenWidth() - 3 * width) / 4;
        //margin=0;

        //生成3行，3列的表格
        int rown = (data.size() % numofColumn == 0) ? data.size() / numofColumn : data.size() / numofColumn + 1;
        for (int row = 0; row < rown; row++) {
            TableRow tableRow = new TableRow(getContext());

            for (int col = 0; col < numofColumn && (row * numofColumn + col) < data.size(); col++) {
                //tv用于显示
                FeatureItemView tv = new FeatureItemView(getContext());
                FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) tv.getItemimag().getLayoutParams();
                fl.width = width;
                fl.height = width;
                fl = (FrameLayout.LayoutParams) tv.getContianer().getLayoutParams();
                fl.leftMargin = margin;
                fl.topMargin = margin;
                final Item it = data.get(row * numofColumn + col);
                IconFilter fi = new IconFilter(it.item.goodsImgs.get(0), "¥" + it.item.price, "");
                tv.setData(fi);
                TableRow.LayoutParams tl = new TableRow.LayoutParams();
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, GoodsDetailsActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, it.item.goodsId + "");
                        //intent.putExtra(MyConstants.POSITION, position);
                        //intent.putExtra(MyConstants.SEARCH_QUERY, searchQuery);
                        ((Activity) context).startActivityForResult(intent, RecommendListFragment.REQUEST_CODE_FOR_GOODS_DETAIL);
                    }
                });
                tv.setLayoutParams(tl);
                tableRow.addView(tv);
            }
            //新建的TableRow添加到TableLayout
            tablelayout.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        }

    }

    public void setData(ArrayList<Item> data) {
        this.data = data;
        refresh();
    }


    public interface OnItemClickListener {
        void onItemClick(View fi);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}