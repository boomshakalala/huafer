package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.AlbumNewActivity;
import com.huapu.huafen.activity.GalleryFileActivity;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;

public class FootViewReportList extends LinearLayout {

    private LayoutInflater mInflater;
    private Context mContext;
    private View mView;
    private MyGridView gridView;
    public GridViewAdapter adapter;
    public EditText etTopWord;
    private TextView tvInputCount;

    public FootViewReportList(Context context) {
        super(context);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        initialize();
    }

    private void initialize() {
        mView = mInflater.inflate(R.layout.view_footview_report, null);
        gridView = (MyGridView) mView.findViewById(R.id.gridView);
        etTopWord = (EditText) mView.findViewById(R.id.etTopWord);
        tvInputCount = (TextView) mView.findViewById(R.id.tvInputCount);
        etTopWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String content = etTopWord.getText().toString();
                tvInputCount.setText(content.length() + "/"
                        + "200");
            }

        });
        this.addView(mView);
    }

    public void notifyAdapter(ArrayList<ImageItem> list) {
        adapter = new GridViewAdapter(mContext, list);
        gridView.setAdapter(adapter);
    }

    public class GridViewAdapter extends BaseAdapter {
        Context mContext;
        private LayoutInflater inflater;
        private ArrayList<ImageItem> list = new ArrayList<ImageItem>();

        public GridViewAdapter(Context mContext, ArrayList<ImageItem> list) {
            this.mContext = mContext;
            this.list = list;
            inflater = LayoutInflater.from(mContext);
        }
//		public void setData(ArrayList<ImageItem> list){
//			this.list = list;
//			notifyDataSetChanged();
//		}

        @Override
        public int getCount() {
            if (list.size() == MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
                return MyConstants.MATCH_SELECT_DYNAMIC_PHOTO;
            }
            return (list.size() + 1);
        }

        @Override
        public ImageItem getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_gridview_releasefirst, null);
                viewHolder = new ViewHolder();
                viewHolder.ivPic = (SimpleDraweeView) convertView.findViewById(R.id.ivPic);
                WindowManager wm = ((Activity) mContext).getWindowManager();
                int width = wm.getDefaultDisplay().getWidth();
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.ivPic.getLayoutParams();
                lp.height = width / 4;
                viewHolder.ivPic.setLayoutParams(lp);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (list.size() == 1) {

            }
            if (position == list.size()) {
                LogUtil.i("liangxs", "最后");
                viewHolder.ivPic.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.item_add_icon));
                if (position == MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
                    viewHolder.ivPic.setVisibility(View.GONE);
                }
            } else {
                LogUtil.i("liangxs", "图片------");
                if (!TextUtils.isEmpty(list.get(position).imagePath)) {

                    ImageLoader.loadImage(viewHolder.ivPic, MyConstants.FILE + list.get(position).imagePath);
                    
                } else {
                    viewHolder.ivPic.setBackgroundResource(R.drawable.default_pic);
                }
            }

            // 图片点击事件
            viewHolder.ivPic.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // 如果是最后一张，则图片为添加新图片，跳转至选择图片的activity
                    if (position == list.size()) {
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
                                intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, list);
                                ((Activity) mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_COMMENT_TO_ALBUM);
                            }
                        });
                        dialog.show();
                        return;
                    }
                    Intent intent = new Intent(mContext, GalleryFileActivity.class);
                    // 已选择的图片及位置坐标
                    intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, list);
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                    intent.putExtra(MyConstants.EXTRA_PHOTO_DELETE, true);
                    ((Activity) mContext).startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_ALBUM_TO_GALLERY);
                }
            });

            return convertView;
        }

        class ViewHolder {
            private SimpleDraweeView ivPic;
        }

    }


    public String imgPath = "";
}
