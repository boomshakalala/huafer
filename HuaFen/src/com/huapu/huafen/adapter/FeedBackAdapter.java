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
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.SearchViewHolder> {
    private Context mContext;
    public String imgPath = "";
    private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();

    public FeedBackAdapter(Context context, ArrayList<ImageItem> list) {
        this.selectBitmap = list;
        this.mContext = context;
    }

    public void setData(ArrayList<ImageItem> list) {
        this.selectBitmap = list;
//		notifyDataSetChanged();
    }

    public class SearchViewHolder extends ViewHolder {
        public SimpleDraweeView ivPic;

        public SearchViewHolder(Context context, View itemView) {
            super(context, itemView);
            ivPic = (SimpleDraweeView) itemView.findViewById(R.id.ivPic);
        }
    }

    @Override
    public int getItemCount() {
        if (selectBitmap.size() == MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
            return MyConstants.MATCH_SELECT_DYNAMIC_PHOTO;
        }
        return (selectBitmap.size() + 1);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder vh, final int position) {

        if (position == selectBitmap.size()) {
            vh.ivPic.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.item_add_icon));
            if (position == MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
                vh.ivPic.setVisibility(View.GONE);
            }
        } else {
            if (!TextUtils.isEmpty(selectBitmap.get(position).imagePath)) {
                ImageLoader.loadImage(vh.ivPic, MyConstants.FILE + selectBitmap.get(position).imagePath);
            } else {
                vh.ivPic.setBackgroundResource(R.drawable.default_pic);
            }
        }
        // 图片点击事件
        vh.ivPic.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 如果是最后一张，则图片为添加新图片，跳转至选择图片的activity
                if (position == selectBitmap.size()) {
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
                            intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                            ((Activity) mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ALBUM);
                        }
                    });
                    dialog.show();
                    return;
                }
                Intent intent = new Intent(mContext, GalleryFileActivity.class);
                // 已选择的图片及位置坐标
                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                intent.putExtra(MyConstants.EXTRA_TO_GALLERY_FROM, "0");
                intent.putExtra(MyConstants.EXTRA_PHOTO_DELETE, true);
                ((Activity) mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
            }
        });
    }


    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        SearchViewHolder vh = new SearchViewHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item_gridview_releasefirst, parent, false));
        WindowManager wm = ((Activity) mContext).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vh.ivPic.getLayoutParams();
        lp.height = width / 4;
        lp.width = lp.height;
        vh.ivPic.setLayoutParams(lp);
        return vh;
    }
}
