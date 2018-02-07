package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.ArticleEditLoopActivity;
import com.huapu.huafen.activity.EditArticleSectionContentActivity;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.TagsContainer;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by admin on 2017/4/26.
 */
public class ArticleSectionAdapter extends CommonWrapper<ArticleSectionAdapter.ArticleSectionViewHolder> {

    private final static String TAG = ArticleSectionAdapter.class.getSimpleName();
    private Context context;
    private List<FloridData.Section> data ;
    private int pickerPosition;


    public ArticleSectionAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    public void clearData(){
        if(data == null){
            data = new ArrayList<>();
        }else{
            data.clear();
        }
        FloridData.Section section = new FloridData.Section();
        section.media = new FloridData.TitleMedia();
        data.add(section);
        notifyWrapperDataSetChanged();
    }

    public boolean isAddData(){
        if(!ArrayUtil.isEmpty(data)){
            for(FloridData.Section ss:data){
                if(ss!=null){
                    if(ss.media == null||TextUtils.isEmpty(ss.media.url)){
                        return false;
                    }
                }
            }
            return true;
        }else {
            return false;
        }
    }

    public boolean isCanBePublish(){
        if(!ArrayUtil.isEmpty(data)){
            if(isAddData()){
                return true;
            }
            if(data.size() > 1 && TextUtils.isEmpty(data.get(data.size()-1).media.url) && TextUtils.isEmpty(data.get(data.size()-1).content)){
                return true;
            }
        }else{
            return false;
        }

        return false;
    }

    public boolean isOverFifty(){
        if(!ArrayUtil.isEmpty(data)){
            if(data.size() < 50){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    public void setData(List<FloridData.Section> data){
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public List<FloridData.Section> getData() {
        return data;
    }

    public void addData(){
        if(this.data == null){
            this.data = new ArrayList<>();
        }
        FloridData.Section section = new FloridData.Section();
        section.media = new FloridData.TitleMedia();
        this.data.add(section);
        notifyWrapperDataSetChanged();
    }


    @Override
    public ArticleSectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArticleSectionViewHolder(LayoutInflater.from(context).inflate(R.layout.article_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final ArticleSectionViewHolder holder, final int position) {
        final FloridData.Section item = data.get(position);
        holder.addSection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CommonUtils.pickSingleImage((Activity) context);
                pickerPosition = position;
            }
        });

        if(item.media!=null&&!TextUtils.isEmpty(item.media.url)){

            holder.addSection.setVisibility(View.GONE);
            holder.tagsContainer.setVisibility(View.VISIBLE);

            holder.tagsContainer.setOnHandleContainerListener(new TagsContainer.OnSimpleHandleContainerListener() {

                @Override
                public void onPopup() {
                    if(item.isPopup){
                        holder.flEditContainer.setVisibility(View.GONE);
                    }else{
                        holder.flEditContainer.setVisibility(View.VISIBLE);
                    }

                    item.isPopup = !item.isPopup;
                }
            });
            if(item.media.width == 0 ||item.media.height == 0){
                int[] outSize = CommonUtils.measureBitmap(item.media.url);
                item.media.width = outSize[0];
                item.media.height = outSize[1];
            }

            holder.tagsContainer.setData(item.media);
        }else{
            holder.addSection.setVisibility(View.VISIBLE);
            holder.tagsContainer.setVisibility(View.GONE);
        }

        holder.flEditContainer.setVisibility(item.isPopup?View.VISIBLE:View.GONE);

        holder.tvInputContent.setText(item.content);

        holder.tvInputContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditArticleSectionContentActivity.class);
                intent.putExtra(MyConstants.SECTION,position);
                intent.putExtra(MyConstants.CONTENT,item.content);
                ((Activity)context).startActivityForResult(intent,MyConstants.EDIT_ARTICLE_SECTION_CONTENT);
            }
        });


        holder.tvEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                item.isPopup = false;
                holder.flEditContainer.setVisibility(View.GONE);
                goToEdit(item,position);
            }
        });


        holder.tvDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                item.isPopup = false;
                holder.flEditContainer.setVisibility(View.GONE);
                final TextDialog dialog = new TextDialog(context, false);
                dialog.setContentText("确定删除本条花语？");
                dialog.setLeftText("取消");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("确定");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        if(data!=null&&data.contains(item)){
                            data.remove(item);
                            if(data.isEmpty()){
                                addData();
                                return;
                            }
                            notifyWrapperDataSetChanged();
                        }
                    }
                });
                dialog.show();

            }
        });

    }

    private void goToEdit(FloridData.Section item,int position) {
        Intent intent = new Intent(context, ArticleEditLoopActivity.class);
        ArrayList<ImageItem> list = new ArrayList<>();
        ImageItem imageItem = new ImageItem();
        imageItem.titleMedia = item.media ;
        list.add(imageItem);
        intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP,list);
        intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX,0);
        intent.putExtra(MyConstants.SECTION,position);
        ((Activity)context).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
    }

    @Override
    public int getItemCount() {
        return data == null?0:data.size();
    }

    public class ArticleSectionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.tagsContainer) TagsContainer tagsContainer;
        @BindView(R2.id.addSection) SimpleDraweeView addSection;
        @BindView(R2.id.tvInputContent) TextView tvInputContent;
        @BindView(R2.id.flEditContainer) FrameLayout flEditContainer;
        @BindView(R2.id.tvEdit) TextView tvEdit;
        @BindView(R2.id.tvDelete) TextView tvDelete;

        public ArticleSectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tagsContainer.setContainerType(TagsContainer.ContainerType.PUBLISH);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM){
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if(!ArrayUtil.isEmpty(images)){
                    ImageItem item = images.get(0);
                    String imagePath = item.getImagePath();
                    if(pickerPosition>=0&&pickerPosition<this.data.size()){
                        FloridData.Section section = this.data.get(pickerPosition);
                        if(section.media == null){
                            section.media = new FloridData.TitleMedia();
                        }
                        section.media.url  = imagePath;
                        notifyWrapperDataSetChanged();
                        goToEdit(section,pickerPosition);
                    }
                }
            }else if(requestCode == MyConstants.EDIT_ARTICLE_SECTION_CONTENT){
                if(data!=null){
                    int position = data.getIntExtra(MyConstants.SECTION, -1);
                    String inputContent = data.getStringExtra(MyConstants.INPUT_SECTION_CONTENT);

                    if(position>=0&&position<= this.data.size()){
                        FloridData.Section section = this.data.get(position);
                        if(section.media == null){
                            section.media = new FloridData.TitleMedia();
                        }
                        section.content = inputContent;
                        notifyWrapperDataSetChanged();
                    }
                }
            }else if(requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY){
                if(data!=null){
                    int position = data.getIntExtra(MyConstants.SECTION, -1);
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                    if(!ArrayUtil.isEmpty(images)&& position>=0&&position<= this.data.size()){
                        ImageItem item = images.get(0);
                        FloridData.Section section = this.data.get(position);
                        section.media = item.titleMedia;
                        notifyWrapperDataSetChanged();
                    }
                }
            }
        }
    }


}
