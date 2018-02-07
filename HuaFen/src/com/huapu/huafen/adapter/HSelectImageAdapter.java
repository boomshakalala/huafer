package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.AlbumNewActivity;
import com.huapu.huafen.activity.GallerySelectFileActivity;
import com.huapu.huafen.activity.RecordVideoActivity;
import com.huapu.huafen.activity.ReleaseActivity;
import com.huapu.huafen.activity.UploadPreviewVideoActivity;
import com.huapu.huafen.beans.AddData;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.VideoBean;
import com.huapu.huafen.beans.VideoData;
import com.huapu.huafen.beans.VideoData.UploadState;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.LogUtil;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by lalo on 2016/11/8.
 */
public class HSelectImageAdapter extends RecyclerView.Adapter<HSelectImageAdapter.HSelectImageHolder> {

    private final static String TAG = HSelectImageAdapter.class.getSimpleName();
    private final static int MAX_COUNT = 9;
    private static final int WHAT_CREATED_BITMAP = 1;
    private static final int WHAT_NOTIFY_ITEM_DATA_CHANGED = WHAT_CREATED_BITMAP + 1;
    private LinkedList<Object> data;
    private Context mContext;
    private ArrayList<ImageItem> selectImages = new ArrayList<ImageItem>();
    private String videoKey;
    private String videoPath;
    private String path1;
    private String path2;
    private String path3;

    private enum ITEM_TYPE {
        VIDEO_TYPE,
        ADD_TYPE,
        IMAGE_TYPE,
        DATA_TYPE,;
    }

    public ArrayList<ImageItem> getSelectImg() {
        return selectImages;
    }

    public void setSelectImages(ArrayList<ImageItem> selectImages) {
        if (selectImages == null || selectImages.size() == 0)
            return;
        if (data.containsAll(this.selectImages)) {
            data.removeAll(this.selectImages);
        }
        this.selectImages = selectImages;
        data.addAll(1, this.selectImages);
        notifyDataSetChanged();
    }

    public void setVideoBean(VideoBean videoBean) {
        setVideoUrl(videoBean.getVideoPath());
        videoPath = videoBean.getVideoPath();
        Object obj = this.data.getFirst();
        if (obj instanceof VideoData) {
            VideoData videoData = (VideoData) obj;
            videoData.uploadState = UploadState.UPLOADED;
            videoData.uploadProgress = 0;
            videoData.bmp = null;
            videoData.url = videoBean.getVideoCover();
            videoData.resId = R.drawable.add_video;
            notifyItemChanged(0);
        }
    }

    public boolean hasVideo() {
        if (!ArrayUtil.isEmpty(data)) {
            Object obj = this.data.getFirst();

            if (obj != null && (obj instanceof VideoData)) {
                return true;
            }
        }
        return false;
    }

    private void initData() {
        data = new LinkedList<Object>();

        data.add(new VideoData("添加视频", R.drawable.add_video));

        data.add(new AddData("添加图片", R.drawable.item_add_icon));

        for (int i = 0; i < 7; i++) {
            data.add(R.drawable.default_pic);
        }
    }

    public HSelectImageAdapter(Context context) {
        this.mContext = context;
        initData();
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = data.get(position);
        if (obj instanceof VideoData) {
            return ITEM_TYPE.VIDEO_TYPE.ordinal();
        } else if (obj instanceof AddData) {
            return ITEM_TYPE.ADD_TYPE.ordinal();
        } else if (obj instanceof Integer) {
            return ITEM_TYPE.IMAGE_TYPE.ordinal();
        }
        return ITEM_TYPE.DATA_TYPE.ordinal();
    }

    @Override
    public HSelectImageAdapter.HSelectImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HSelectImageHolder vh = new HSelectImageHolder(LayoutInflater.from(mContext).inflate(R.layout.image_select_item, parent, false));
        WindowManager wm = ((Activity) mContext).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) vh.ivImg.getLayoutParams();
        lp.height = width / 4;
        lp.width = lp.height;
        vh.ivImg.setLayoutParams(lp);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vh.tvUpload.getLayoutParams();
        layoutParams.height = width / 4;
        layoutParams.width = layoutParams.height;
        vh.ivImg.setLayoutParams(layoutParams);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) vh.tvTip.getLayoutParams();
        params.width = width / 4;
        vh.tvTip.setLayoutParams(params);

        return vh;
    }

    @Override
    public void onBindViewHolder(HSelectImageAdapter.HSelectImageHolder viewHolder, final int position) {
        final int itemType = getItemViewType(position);
        Object item = data.get(position);
        if (itemType == ITEM_TYPE.VIDEO_TYPE.ordinal()) {
            final VideoData videoData = (VideoData) item;
            viewHolder.tvTip.setBackgroundColor(Color.parseColor("#cc9fdeff"));
            if (videoData.bmp == null) {
                if (TextUtils.isEmpty(videoData.url)) {
                    viewHolder.ivImg.setImageResource(videoData.resId);
                } else {

                    ImageLoader.loadImage(viewHolder.ivImg, videoData.url);
                }
            } else {
                viewHolder.ivImg.setImageBitmap(videoData.bmp);
            }
            if (videoData.uploadState == UploadState.IDLE) {
                viewHolder.tvTip.setVisibility(View.VISIBLE);
                viewHolder.tvUpload.setVisibility(View.GONE);
                viewHolder.ivPlay.setVisibility(View.GONE);
                viewHolder.ivDel.setVisibility(View.GONE);
                viewHolder.tvTip.setText("添加视频");
            } else if (videoData.uploadState == UploadState.UPLOADING) {
                viewHolder.tvTip.setVisibility(View.GONE);
                viewHolder.tvUpload.setVisibility(View.VISIBLE);
                viewHolder.ivPlay.setVisibility(View.GONE);
                viewHolder.ivDel.setVisibility(View.GONE);
                String des = String.format(mContext.getResources().getString(R.string.upload), videoData.uploadProgress) + "%";
                viewHolder.tvUpload.setText(des);
            } else if (videoData.uploadState == UploadState.UPLOADED) {
                viewHolder.tvTip.setVisibility(View.VISIBLE);
                viewHolder.tvUpload.setVisibility(View.GONE);
                viewHolder.ivPlay.setVisibility(View.VISIBLE);
                viewHolder.ivDel.setVisibility(View.VISIBLE);
                viewHolder.tvTip.setText("视频");
            } else if (videoData.uploadState == UploadState.FAILED) {
                viewHolder.tvTip.setVisibility(View.GONE);
                viewHolder.tvUpload.setVisibility(View.VISIBLE);
                viewHolder.tvUpload.setText("上传失败");
                viewHolder.ivPlay.setVisibility(View.GONE);
                viewHolder.ivDel.setVisibility(View.VISIBLE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (videoData.uploadState == UploadState.IDLE) {//初始状态
                        Intent intent = new Intent(mContext, RecordVideoActivity.class);
                        ((Activity) mContext).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_VIDEO);
                    } else if (videoData.uploadState == UploadState.UPLOADING) {//正在上传

                    } else if (videoData.uploadState == UploadState.UPLOADED) {//上传结束
                        Intent intent = new Intent(mContext, UploadPreviewVideoActivity.class);
                        intent.putExtra("VIDEO_PATH", videoPath);
                        intent.putExtra("file1", path1);
                        intent.putExtra("file2", path2);
                        intent.putExtra("file3", path3);
                        intent.putExtra("TAG", ReleaseActivity.TAG);
                        intent.putExtra("uploadMode", "uploaded");
                        ((Activity) mContext).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_VIDEO);
                    } else if (videoData.uploadState == UploadState.FAILED) {//上传失败
                        Intent intent = new Intent(mContext, UploadPreviewVideoActivity.class);
                        intent.putExtra("VIDEO_PATH", videoPath);
                        intent.putExtra("file1", path1);
                        intent.putExtra("file2", path2);
                        intent.putExtra("file3", path3);
                        intent.putExtra("TAG", ReleaseActivity.TAG);
                        intent.putExtra("uploadMode", "upload_failed");
                        ((Activity) mContext).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_VIDEO);
                    }
                }
            });

        } else if (itemType == ITEM_TYPE.ADD_TYPE.ordinal()) {//显示“+”图片
            AddData addData = (AddData) item;
            viewHolder.tvUpload.setVisibility(View.GONE);
            viewHolder.ivPlay.setVisibility(View.GONE);
            viewHolder.ivDel.setVisibility(View.GONE);
            viewHolder.tvTip.setBackgroundColor(Color.parseColor("#ccff6677"));

            viewHolder.tvTip.setVisibility(View.VISIBLE);
            viewHolder.tvTip.setText(addData.title);

            if (position == 1) {
            } else {
                // viewHolder.tvTip.setVisibility(View.GONE);
            }

            viewHolder.ivImg.setImageResource(addData.resId);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startAlbumActivityForResult();
                }
            });

        } else if (itemType == ITEM_TYPE.IMAGE_TYPE.ordinal()) {//显示默认图片
            viewHolder.tvUpload.setVisibility(View.GONE);
            viewHolder.ivPlay.setVisibility(View.GONE);
            viewHolder.ivDel.setVisibility(View.GONE);
            int resId = (int) item;
            viewHolder.ivImg.setImageResource(resId);
            viewHolder.tvTip.setVisibility(View.GONE);
        } else {//显示正常的照片缩略图
            viewHolder.tvUpload.setVisibility(View.GONE);
            viewHolder.ivPlay.setVisibility(View.GONE);
            viewHolder.ivDel.setVisibility(View.VISIBLE);
            final ImageItem imgItem = (ImageItem) item;
            viewHolder.tvTip.setBackgroundColor(Color.parseColor("#ccff6677"));
            if (position == 1) {
                viewHolder.tvTip.setVisibility(View.VISIBLE);
                viewHolder.tvTip.setText("主图");
            } else {
                viewHolder.tvTip.setVisibility(View.GONE);
            }

            String url = (String) viewHolder.ivImg.getTag();
            String path = MyConstants.FILE + imgItem.imagePath;
            if (TextUtils.isEmpty(url) || !url.equals(path)) {
                ImageLoader.loadImage(viewHolder.ivImg, path);
                viewHolder.ivImg.setTag(path);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GallerySelectFileActivity.class);
                    // 已选择的图片及位置坐标
                    if (ArrayUtil.isEmpty(selectImages) || position - 1 > selectImages.size() - 1) {
                        return;
                    }
                    intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectImages);
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position - 1);
                    intent.putExtra(MyConstants.EXTRA_TO_GALLERY_FROM, "1");
                    intent.putExtra(MyConstants.EXTRA_PHOTO_DELETE, true);
                    ((Activity) mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
                }
            });
        }
        viewHolder.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                if (itemType == ITEM_TYPE.VIDEO_TYPE.ordinal()) {
                    str = "是否要删除这段视频？";
                } else {
                    str = "是否要删除这张照片？";
                }
                final TextDialog dialog = new TextDialog(mContext, true);
                dialog.setContentText(str);
                dialog.setLeftText("否");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("是");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        if (itemType == ITEM_TYPE.VIDEO_TYPE.ordinal()) {
                            Object obj = data.getFirst();
                            if (obj instanceof VideoData) {
                                setVideoUrl("");
                                VideoData videoData = (VideoData) obj;
                                videoData.uploadState = UploadState.IDLE;
                                videoData.uploadProgress = 0;
                                videoData.bmp = null;
                                videoData.url = "";
                                videoData.resId = R.drawable.add_video;
                                notifyItemChanged(0);
                            }
                        } else {
                            data.remove(position);
                            selectImages.remove(position - 1);
                        }
                        notifyDataSetChanged();
                    }
                });
                dialog.show();

            }
        });
    }

    public void startAlbumActivityForResult() {
        Intent intent = new Intent(mContext, AlbumNewActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectImages);
        ((Activity) mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ALBUM);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (images != null) {
                    setSelectImages(images);
                }

            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM) {
                ArrayList<ImageItem> imags = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
                if (imags != null) {
                    setSelectImages(imags);
                }
            } else if (requestCode == MyConstants.REQUEST_CODE_FOR_VIDEO) {//视频
                if (data != null) {
                    String mode = data.getStringExtra("mode");
                    if ("upload".equals(mode)) {
                        setVideoUrl("");
                        videoPath = data.getStringExtra("videoPath");
                        path1 = data.getStringExtra("file1");
                        path2 = data.getStringExtra("file2");
                        path3 = data.getStringExtra("file3");
                        upload2Ali(videoPath);
                        if (!TextUtils.isEmpty(videoPath)) {

                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    Bitmap bitmap = CommonUtils.createVideoThumbnail(videoPath);
                                    Message msg = mHandler.obtainMessage(WHAT_CREATED_BITMAP, bitmap);
                                    mHandler.sendMessage(msg);
                                }
                            }).start();
                        }

                    } else if ("delete".equals(mode)) {
                        Object obj = this.data.getFirst();
                        if (obj instanceof VideoData) {
                            setVideoUrl("");
                            VideoData videoData = (VideoData) obj;
                            videoData.uploadState = UploadState.IDLE;
                            videoData.uploadProgress = 0;
                            videoData.bmp = null;
                            videoData.url = "";
                            videoData.resId = R.drawable.add_video;
                            notifyItemChanged(0);
                        }
                    } else if ("cancel".equals(mode)) {

                    } else if ("rerecord".equals(mode)) {
                        Intent intent = new Intent(mContext, RecordVideoActivity.class);
                        ((Activity) mContext).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_VIDEO);
                    }
                }
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_CREATED_BITMAP:
                    Bitmap bmp = (Bitmap) msg.obj;
                    Object obj = data.getFirst();
                    if (obj instanceof VideoData) {
                        VideoData videoData = (VideoData) obj;
                        videoData.bmp = bmp;
                        notifyItemChanged(0);
                    }
                    break;
                case WHAT_NOTIFY_ITEM_DATA_CHANGED:
                    notifyItemChanged(0);
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private String videoUrl = "";

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * 上传视频到阿里服务器
     */
    private void upload2Ali(String path) {
        videoKey = DateTimeUtils.getOSSVideoFolder("goods/");
        final String fileName = System.currentTimeMillis() + ".mp4";
        AliUpdateEvent updateEvent = new AliUpdateEvent(mContext, path, MyConstants.OSS_VIDEO_FOLDER_BUCKET, videoKey + fileName);
        updateEvent.asyncPutObjectFromLocalFile(new AliUpdateEvent.OnAliOSSCompleteListener() {

            @Override
            public void onComplete() {
                Object obj = data.getFirst();
                if (obj instanceof VideoData) {
                    setVideoUrl(videoKey + fileName);
                    VideoData videoData = (VideoData) obj;
                    videoData.uploadState = UploadState.UPLOADED;
                    videoData.uploadProgress = 0;
                    mHandler.sendEmptyMessage(WHAT_NOTIFY_ITEM_DATA_CHANGED);
                }
            }

            @Override
            public void onFailed() {
                Object obj = data.getFirst();
                if (obj instanceof VideoData) {
                    VideoData videoData = (VideoData) obj;
                    videoData.uploadState = UploadState.FAILED;
                    videoData.uploadProgress = 0;
                    mHandler.sendEmptyMessage(WHAT_NOTIFY_ITEM_DATA_CHANGED);
                }
            }

            @Override
            public void onProgress(final long currentSize, final long totalSize) {
                long progress = currentSize * 100 / totalSize;
                LogUtil.e("progress", currentSize + "/" + totalSize + ":" + progress + "%");
                Object obj = data.getFirst();
                if (obj instanceof VideoData) {
                    VideoData videoData = (VideoData) obj;
                    videoData.uploadState = UploadState.UPLOADING;
                    videoData.uploadProgress = progress;
                    if (progress % 5 == 0) {
                        mHandler.sendEmptyMessage(WHAT_NOTIFY_ITEM_DATA_CHANGED);
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        if (!ArrayUtil.isEmpty(selectImages) && listener != null) {
            int count = selectImages.size();
            if (count > 4 && count < 8) {
                listener.onChange(true);
            } else {
                listener.onChange(false);
            }
        }

        if (ArrayUtil.isEmpty(data)) {
            return 0;
        } else if (data.size() < MAX_COUNT) {
            return data.size();
        }
        return MAX_COUNT;
    }

    private OnSelectedImagesListener listener;

    public void setOnSelectedImagesListener(OnSelectedImagesListener listener) {
        this.listener = listener;
    }

    public interface OnSelectedImagesListener {
        void onChange(boolean visible);
    }

    public class HSelectImageHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public SimpleDraweeView ivImg;
        public ImageView ivPlay;
        public ImageView ivDel;
        public TextView tvTip;
        public TextView tvUpload;

        public HSelectImageHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivImg = (SimpleDraweeView) itemView.findViewById(R.id.ivImg);
            this.ivDel = (ImageView) itemView.findViewById(R.id.ivDel);
            this.tvTip = (TextView) itemView.findViewById(R.id.tvTip);
            this.tvUpload = (TextView) itemView.findViewById(R.id.tvUpload);
            this.ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);

        }
    }


}
