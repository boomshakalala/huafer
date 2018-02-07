package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.AlbumNewActivity;
import com.huapu.huafen.activity.GalleryFileActivity;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.recycler.base.ViewHolder;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class CommentEditAdapter extends RecyclerView.Adapter<CommentEditAdapter.CommentEditViewHolder> {
    public String imgPath = "";
    private Context mContext;
    private ArrayList<ImageItem> mData = new ArrayList<>();

    public CommentEditAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<ImageItem> selectBitmap) {
        this.mData = selectBitmap;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mData.size() == MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
            return MyConstants.MATCH_SELECT_DYNAMIC_PHOTO;
        }
        return (mData.size() + 1);
    }

    @Override
    public void onBindViewHolder(CommentEditViewHolder vh, final int position) {
        if (position == mData.size()) {
            vh.ivPic.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.comment_add));
            if (position == MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
                vh.ivPic.setVisibility(View.GONE);
            }
        } else {
            if (!TextUtils.isEmpty(mData.get(position).imagePath)) {
                ImageLoader.loadImage(vh.ivPic, "file://" + mData.get(position).imagePath);
            } else {
                vh.ivPic.setBackgroundResource(R.drawable.comment_add);
            }
        }

        // 图片点击事件
        vh.ivPic.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 如果是最后一张，则图片为添加新图片，跳转至选择图片的activity
                if (position == mData.size()) {
//						createHomeCaminaDialog();
                    PhotoDialog dialog = new PhotoDialog(mContext);
                    dialog.setCameraCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            imgPath = FileUtils.getCameraPhotoPath();
                            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intentCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                            File out = new File(imgPath);
                            Uri uri = Uri.fromFile(out);
                            // 获取拍照后未压缩的原图片，并保存在uri路径中
                            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            ((Activity) mContext).startActivityForResult(intentCamera, MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA);
                        }
                    });
                    dialog.setAlbumCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            Intent intent = new Intent(mContext, AlbumNewActivity.class);
                            intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, mData);
                            ((Activity) mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM);
                        }
                    });
                    dialog.show();
                    return;
                }
                Intent intent = new Intent(mContext, GalleryFileActivity.class);
                // 已选择的图片及位置坐标
                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, mData);
                intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                intent.putExtra(MyConstants.EXTRA_PHOTO_DELETE, true);
                ((Activity) mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
            }
        });
        int remainder = position % 4;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) vh.ivPic.getLayoutParams();
        if (remainder == 0) {
            params.leftMargin = CommonUtils.dp2px(10f);
            params.rightMargin = 0;
        } else if (remainder == 3) {
            params.leftMargin = 0;
            params.rightMargin = CommonUtils.dp2px(10f);
        } else if (remainder == 1) {
            params.leftMargin = CommonUtils.dp2px(10) / 3 * 2;
            params.rightMargin = CommonUtils.dp2px(10) / 3;
        } else {
            params.leftMargin = CommonUtils.dp2px(10) / 3;
            params.rightMargin = CommonUtils.dp2px(10) / 3 * 2;
        }


    }

    @Override
    public CommentEditViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        CommentEditViewHolder vh = new CommentEditViewHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_edit_pic, parent, false));
        WindowManager wm = ((Activity) mContext).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        RecyclerView.LayoutParams itemPrams = (RecyclerView.LayoutParams) vh.itemView.getLayoutParams();
        itemPrams.width = itemPrams.height = width / 4;

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vh.ivPic.getLayoutParams();
        lp.width = lp.height = width / 4 - CommonUtils.dp2px(10f);
        vh.ivPic.setLayoutParams(lp);

        return vh;
    }

    public class CommentEditViewHolder extends ViewHolder {
        public SimpleDraweeView ivPic;

        public CommentEditViewHolder(Context context, View itemView) {
            super(context, itemView);
            ivPic = (SimpleDraweeView) itemView.findViewById(R.id.ivPic);

        }

    }
}
