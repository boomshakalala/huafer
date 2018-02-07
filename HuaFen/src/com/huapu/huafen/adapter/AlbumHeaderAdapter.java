package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.activity.GalleryFileActivity;
import com.huapu.huafen.album.utils.BitmapUtilities;
import com.huapu.huafen.album.utils.ImageShowManager;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ToastUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by liang on 2016/10/26.
 */
public class AlbumHeaderAdapter extends CommonWrapper<AlbumHeaderAdapter.AlbumHolder>{

    private Context mContext;
    private ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
    private ImageShowManager imageManager;
    private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();
    public String imgPath = "";

    public AlbumHeaderAdapter(Context context){
        super();
        this.mContext = context;
        imageManager = ImageShowManager.from(mContext);
    }

    public void setData(ArrayList<ImageItem> datas){
        if(datas==null){
            datas = new ArrayList<ImageItem>();
        }
        datas.remove(0);
        datas.remove(1);
        datas.remove(2);
        datas.remove(3);
        this.dataList = datas;
//        this.dataList.add(0, new ImageItem());
        getWrapperAdapter().notifyDataSetChanged();
    }

    public void setSelectBitmap(ArrayList<ImageItem> selectBitmap){
        this.selectBitmap = selectBitmap;
        getWrapperAdapter().notifyDataSetChanged();
    }

    public ArrayList<ImageItem> getSelectBitmap(){
        return selectBitmap;
    }

    @Override
    public AlbumHolder onCreateViewHolder(ViewGroup parent, int i) {
        AlbumHolder vh = new AlbumHolder(LayoutInflater.from(mContext).inflate(R.layout.item_gridview_album, parent, false));
        return vh;
    }
    @Override
    public void onBindViewHolder(AlbumHolder viewHolder, final int position) {
        if(dataList.get(position) == null) {
            return;
        }
        String path;
        if (dataList != null && dataList.size() > position) {
            path = dataList.get(position).imagePath;
            if(TextUtils.isEmpty(path)) {
                path = "camera";
            }
        } else {
            path = "camera_default";
        }
        if(path.equals("camera")) {
            viewHolder.ivPhoto.setImageResource(R.drawable.btn_camera);
            viewHolder.btnChecked.setVisibility(View.GONE);
        } else if (path.contains("camera_default")) {
            viewHolder.ivPhoto.setImageResource(R.drawable.album_camera_no_pictures);
            viewHolder.btnChecked.setVisibility(View.GONE);
        } else {
            viewHolder.btnChecked.setVisibility(View.VISIBLE);
            final ImageItem item = dataList.get(position);
            // 首先检测是否已经有线程在加载同样的资源（如果则取消较早的），避免出现重复加载
            if (cancelPotentialLoad(item.imagePath, viewHolder.ivPhoto)) {
                AsyncLoadImageTask task = new AsyncLoadImageTask(viewHolder.ivPhoto);
                viewHolder.ivPhoto.setImageDrawable(new LoadingDrawable(task));
                task.execute(path);
            }
            for (ImageItem bean : selectBitmap){
                if(bean.imagePath.equals(item.imagePath)){
                    item.isSelected = true;
                }
            }
            if (item.isSelected) {
                viewHolder.btnChecked.setSelected(true);
            } else {
                viewHolder.btnChecked.setSelected(false);
            }
        }
        final Button btnChecked = viewHolder.btnChecked;
        btnChecked.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageItem item = dataList.get(position);
                if(item.isSelected) {
                    item.isSelected = false;
                    btnChecked.setSelected(false);
                    for (ImageItem bean : selectBitmap){
                        if(bean.imagePath.equals(item.imagePath)){
                            selectBitmap.remove(bean);
                            break;
                        }
                    }
                } else {
                    if (selectBitmap.size() >= MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
                        ToastUtil.toast(mContext, "达到数量上限");
                        return;
                    }
                    item.isSelected = true;
                    btnChecked.setSelected(true);
                    selectBitmap.add(item);
                }
                if(onItemChangeListener != null) {
                    onItemChangeListener.onItemChange(selectBitmap);
                }
            }
        });

        viewHolder.item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(position == 0) {
                    imgPath = FileUtils.getHCameraPath();
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    File out = new File(imgPath);
                    Uri uri = Uri.fromFile(out);
                    // 获取拍照后未压缩的原图片，并保存在uri路径中
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    ((Activity)mContext).startActivityForResult(intentCamera, MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA);
                } else {
                    Intent intent = new Intent(mContext, GalleryFileActivity.class);
                    intent.putExtra(MyConstants.EXTRA_IMAGES, dataList);
                    intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
                    // 第一条为跳转相册，所以position需要-1
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, (position - 1));
                    ((Activity)mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class AlbumHolder extends RecyclerView.ViewHolder{
        private View item;
        private ImageView ivPhoto;
        private Button btnChecked;


        public AlbumHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            ivPhoto = (ImageView) itemView
                    .findViewById(R.id.ivPhoto);
            btnChecked = (Button) itemView
                    .findViewById(R.id.btnChecked);
            WindowManager wm = ((Activity)mContext).getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivPhoto.getLayoutParams();
            int padding = CommonUtils.dp2px(2);
            lp.height = (width / 4) - padding;
            lp.width = width / 4;
            ivPhoto.setLayoutParams(lp);
        }
    }

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemChangeListener{
        void onItemChange(ArrayList<ImageItem> selectBitmap);
    }
    private OnItemChangeListener onItemChangeListener;

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener){
        this.onItemChangeListener = onItemChangeListener;
    }


    /**
     * 判断当前的imageview是否在加载相同的资源
     *
     * @param url
     * @param imageview
     * @return
     */
    private boolean cancelPotentialLoad(String url, ImageView imageview) {

        AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
        if (loadImageTask != null) {
            String bitmapUrl = loadImageTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                loadImageTask.cancel(true);
            } else {
                // 相同的url已经在加载中.
                return false;
            }
        }
        return true;
    }

    /**
     * 负责加载图片的异步线程
     *
     * @author Administrator
     *
     */
    class AsyncLoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        private String url = null;

        public AsyncLoadImageTask(ImageView imageview) {
            super();
            imageViewReference = new WeakReference<ImageView>(imageview);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            /**
             * 具体的获取bitmap的部分，流程： 从内存缓冲区获取，如果没有向硬盘缓冲区获取，如果没有从sd卡/网络获取
             */

            Bitmap bitmap = null;
            this.url = params[0];

            // 从内存缓存区域读取
            bitmap = imageManager.getBitmapFromMemory(url);
            if (bitmap != null) {
//					Log.d("dqq", "return by 内存");
                return bitmap;
            }
            // 从硬盘缓存区域中读取
            bitmap = imageManager.getBitmapFormDisk(url);
            if (bitmap != null) {
                imageManager.putBitmapToMemery(url, bitmap);
//					Log.d("dqq", "return by 硬盘");
                return bitmap;
            }

            // 没有缓存则从原始位置读取
            bitmap = BitmapUtilities.getBitmapThumbnail(url,
                    ImageShowManager.bitmap_width,
                    ImageShowManager.bitmap_height);
            if (bitmap != null) {
                imageManager.putBitmapToMemery(url, bitmap);
                imageManager.putBitmapToDisk(url, bitmap);
            }

//				Log.d("dqq", "return by 原始读取");
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap resultBitmap) {
            if (isCancelled()) {
                // 被取消了
                resultBitmap = null;
            }
            if (imageViewReference != null) {
                ImageView imageview = imageViewReference.get();
                AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
                if (this == loadImageTask) {
                    imageview.setImageDrawable(null);
                    imageview.setImageBitmap(resultBitmap);
                }

            }

            super.onPostExecute(resultBitmap);
        }
    }

    /**
     * 根据imageview，获得正在为此imageview异步加载数据的函数
     *
     * @param imageview
     * @return
     */
    private AsyncLoadImageTask getAsyncLoadImageTask(ImageView imageview) {
        if (imageview != null) {
            Drawable drawable = imageview.getDrawable();
            if (drawable instanceof LoadingDrawable) {
                LoadingDrawable loadedDrawable = (LoadingDrawable) drawable;
                return loadedDrawable.getLoadImageTask();
            }
        }
        return null;
    }

    /**
     * 记录imageview对应的加载任务，并且设置默认的drawable
     *
     * @author Administrator
     *
     */
    class LoadingDrawable extends ColorDrawable {
        // 引用与drawable相关联的的加载线程
        private final WeakReference<AsyncLoadImageTask> loadImageTaskReference;

        public LoadingDrawable(AsyncLoadImageTask loadImageTask) {
//				super(getResources().getColor(R.color.accent_red));
            loadImageTaskReference = new WeakReference<AsyncLoadImageTask>(
                    loadImageTask);
        }

        public AsyncLoadImageTask getLoadImageTask() {
            return loadImageTaskReference.get();
        }
    }

}
