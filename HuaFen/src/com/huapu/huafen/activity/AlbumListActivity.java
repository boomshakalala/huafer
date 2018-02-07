package com.huapu.huafen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.ImageBucket;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.common.MyConstants;

import java.util.ArrayList;

/**
 * 自定义相册文件夹
 *
 * @author liang_xs
 */
public class AlbumListActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
    private ListView lvAlbumList;
    private AlbumListAdapter adapter;
    private ArrayList<ImageBucket> imageBucketBeans = new ArrayList<ImageBucket>();
    public static final String EXTRA_IMAGE_BUCKETLIST = "extra_image_bucketlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        initView();
        if (getIntent().hasExtra(EXTRA_IMAGE_BUCKETLIST)) {
            imageBucketBeans = (ArrayList<ImageBucket>) getIntent().getSerializableExtra(EXTRA_IMAGE_BUCKETLIST);
        }
        adapter = new AlbumListAdapter();
        lvAlbumList.setAdapter(adapter);
        lvAlbumList.setOnItemClickListener(this);
    }

    private void initView() {
        getTitleBar().
                setTitle("相簿").
                setLeftText("取消", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        lvAlbumList = (ListView) findViewById(R.id.lvAlbumList);
    }

    /**
     * @author liangxs
     */
    class AlbumListAdapter extends BaseAdapter {

        public AlbumListAdapter() {
        }

        @Override
        public int getCount() {
            return imageBucketBeans == null ? 0 : imageBucketBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return imageBucketBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(AlbumListActivity.this).inflate(
                        R.layout.item_listview_ablumlist, parent, false);
                viewHolder.ivAlbumList = (SimpleDraweeView) convertView
                        .findViewById(R.id.ivAlbumListIcon);
                viewHolder.tvFolderName = (TextView) convertView.findViewById(R.id.tvFolderName);
                viewHolder.tvPhotoCount = (TextView) convertView.findViewById(R.id.tvPhotoCount);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String path;
            if (imageBucketBeans.get(position).imageList != null) {

                //path = photoAbsolutePathList.get(position);
                //封面图片路径
                path = imageBucketBeans.get(position).imageList.get(0).imagePath;
                // 给folderName设置值为文件夹名�?
                //holder.folderName.setText(fileNameList.get(position));
                viewHolder.tvFolderName.setText(imageBucketBeans.get(position).bucketName);

                // 给fileNum设置文件夹内图片数量
                //holder.fileNum.setText("" + fileNum.get(position));
                viewHolder.tvPhotoCount.setText("" + imageBucketBeans.get(position).count);

            } else
                path = "android_hybrid_camera_default";
            if (path.contains("android_hybrid_camera_default")) {
                Uri uri = Uri.parse(MyConstants.RES + R.drawable.album_camera_no_pictures);
                viewHolder.ivAlbumList.setImageURI(uri);
            } else {
                final ImageItem item = imageBucketBeans.get(position).imageList.get(0);
                viewHolder.ivAlbumList.setTag(item.imagePath);
                viewHolder.ivAlbumList.setImageURI(Uri.parse(MyConstants.FILE + item.imagePath));
            }
            return convertView;
        }

        private class ViewHolder {
            private SimpleDraweeView ivAlbumList;
            private TextView tvFolderName;
            private TextView tvPhotoCount;
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent();
        String folderName = imageBucketBeans.get(position).bucketName;
        intent.putExtra(MyConstants.EXTRA_ALBUM_LIST_FOLDERNAME, folderName);
        intent.putExtra(MyConstants.EXTRA_ALBUM_LIST_IMAGES, (ArrayList<ImageItem>) imageBucketBeans.get(position).imageList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
