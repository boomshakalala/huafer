package com.huapu.huafen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.ArticleFilterData;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/10.
 */

public class ArticleEditFilterAdapter extends RecyclerView.Adapter<ArticleEditFilterAdapter.ArticleEditFilterViewHolder> {


    private Context context;
    private List<ArticleFilterData> data;
    private Uri uri;

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public ArticleEditFilterAdapter(Context context) {
        this.context = context;
        data = genData();
    }


    private List<ArticleFilterData> genData() {
        List<ArticleFilterData> list = new ArrayList<>();
        float[][] arr = new float[][]{
                MyConstants.COLOR_MATRIX_NONE,
                MyConstants.COLOR_MATRIX_LOMO,
                MyConstants.COLOR_MATRIX_BLACK_OR_WHITE,
                MyConstants.COLOR_MATRIX_FUGU,
                MyConstants.COLOR_MATRIX_GETE,
                MyConstants.COLOR_MATRIX_RUIHUA,
                MyConstants.COLOR_MATRIX_DANYA,
                MyConstants.COLOR_MATRIX_JIUHONG,
                MyConstants.COLOR_MATRIX_QINGNING,
                MyConstants.COLOR_MATRIX_ROMANTIC,
                MyConstants.COLOR_MATRIX_GUANGYUN,
                MyConstants.COLOR_MATRIX_BLUES,
                MyConstants.COLOR_MATRIX_DREAM,
                MyConstants.COLOR_MATRIX_YESE,
                MyConstants.COLOR_MATRIX_JIAOPIAN,
                MyConstants.COLOR_MATRIX_CHUANTONG
        };
        String[] titles = new String[]{"无", "LOMO", "黑白", "复古", "哥特", "锐化", "淡雅", "酒红", "青柠", "浪漫", "光晕", "蓝调", "梦幻", "夜色", "胶片", "传统"};
        for (int i = 0; i < arr.length; i++) {
            float[] matrix = arr[i];
            ArticleFilterData data = new ArticleFilterData();
            data.colorMatrix = matrix;
            data.filterStyle = titles[i];
            list.add(data);
        }
        return list;
    }

    public List<ArticleFilterData> getData() {
        return data;
    }

    @Override
    public ArticleEditFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArticleEditFilterViewHolder(LayoutInflater.from(context).inflate(R.layout.article_filter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ArticleEditFilterViewHolder holder, final int position) {
        final ArticleFilterData item = data.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setSingleCheck(position);
                if (onItemCLickListener != null) {
                    onItemCLickListener.onItemClick(item);
                }
            }
        });

        if (item.isCheck) {
            RoundingParams params = RoundingParams.fromCornersRadius(0f);
            params.setBorder(context.getResources().getColor(R.color.base_pink), 5f);
            holder.simpleDraweeView.getHierarchy().setRoundingParams(params);
        } else {
            RoundingParams params = RoundingParams.fromCornersRadius(0f);
            params.setBorder(Color.parseColor("#00000000"), 5f);
            holder.simpleDraweeView.getHierarchy().setRoundingParams(params);
        }


        if ("无".equals(item.filterStyle)) {
            holder.simpleDraweeView.setImageURI(uri);
        } else {
            ImageLoader.resizeSmall(holder.simpleDraweeView, uri, 1);
        }

        holder.tvArticleStyle.setText(item.filterStyle);
    }

    public interface OnItemCLickListener {
        void onItemClick(ArticleFilterData data);
    }

    private OnItemCLickListener onItemCLickListener;

    public void setOnItemCLickListener(OnItemCLickListener onItemCLickListener) {
        this.onItemCLickListener = onItemCLickListener;
    }

    private void setSingleCheck(int position) {
        if (!ArrayUtil.isEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                if (i == position) {
                    data.get(i).isCheck = true;
                } else {
                    data.get(i).isCheck = false;
                }
            }
            notifyDataSetChanged();
        }
    }

    public void notifyDataSetChangeByUri(FloridData.TitleMedia media) {
        if (!TextUtils.isEmpty(media.url) &&
                (media.url.startsWith(MyConstants.HTTP) || media.url.startsWith(MyConstants.HTTPS))) {
            setUri(Uri.parse(media.url));
        } else {
            setUri(Uri.parse(MyConstants.FILE + media.url));
        }

        if (!ArrayUtil.isEmpty(data)) {
            for (ArticleFilterData filterData : data) {
                if (filterData.filterStyle.equals(media.filterStyle)) {
                    filterData.isCheck = true;
                } else {
                    filterData.isCheck = false;
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ArticleEditFilterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.simpleDraweeView)
        SimpleDraweeView simpleDraweeView;
        @BindView(R2.id.tvArticleStyle)
        TextView tvArticleStyle;

        public ArticleEditFilterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
