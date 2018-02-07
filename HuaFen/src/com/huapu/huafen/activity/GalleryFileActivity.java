/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.huapu.huafen.activity;

import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.FilePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;

/**
 * 预览本地图片
 * @author liang_xs
 *
 */
public class GalleryFileActivity extends BaseActivity {

	private GalleryViewPager mViewPager;
	private ArrayList<ImageItem> selectBitmap = new ArrayList<ImageItem>();
	private ArrayList<ImageItem> removeBitmap = new ArrayList<ImageItem>();
	private ArrayList<ImageItem> dateList = new ArrayList<ImageItem>();
	private int position;
	private Button btnRightChecked;
	private Button btnDraw, btnAdd;
	private int selectPosition;
	private FilePagerAdapter pagerAdapter;
	private int outWidth = 300;
	private int outHeight = 300;
	private Button btnLeft, btnFinish;
	private TextView tvTitleGallery;
	private String from = "0"; // 1来自发布页预览，显示编辑；2表示来自花语，需要显示编辑及添加;3花语预览，不显示编辑及添加删除
	private boolean delete;

	private List<String> items = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_file);
		if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_BITMAP)) {
			selectBitmap = (ArrayList<ImageItem>) getIntent()
					.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
		}
		if (getIntent().hasExtra(MyConstants.EXTRA_IMAGES)) {
			dateList = (ArrayList<ImageItem>) getIntent()
					.getSerializableExtra(MyConstants.EXTRA_IMAGES);
			// 移除相册第一条数据，其为空值，相册内第一条为跳转相册
			dateList.remove(0);
		}
		if (getIntent().hasExtra(MyConstants.EXTRA_IMAGE_INDEX)) {
			position = getIntent()
					.getIntExtra(MyConstants.EXTRA_IMAGE_INDEX, 0);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_TO_GALLERY_FROM)) {
			from = getIntent().getStringExtra(MyConstants.EXTRA_TO_GALLERY_FROM);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_PHOTO_DELETE)) {
			delete = getIntent().getBooleanExtra(MyConstants.EXTRA_PHOTO_DELETE, false);
		}
		initView();
	}

	@Override
	public void onBackPressed() {
		if (removeBitmap != null) {
			for (ImageItem removeItem : removeBitmap) {
				for (ImageItem item : selectBitmap) {
					if (removeItem.imagePath.equals(item.imagePath)) {
						selectBitmap.remove(item);
						break;
					}
				}
			}
		}
		Intent intent = new Intent();
		intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void initView() {
		btnLeft = (Button) findViewById(R.id.btnLeft);
		btnRightChecked = (Button) findViewById(R.id.btnRightChecked);
		btnRightChecked.setBackgroundResource(0);
		btnRightChecked.setText("删除");
		tvTitleGallery = (TextView) findViewById(R.id.tvTitleGallery);
		btnFinish = (Button) findViewById(R.id.btnFinish);
		if(dateList.size() != 0) {
			tvTitleGallery.setText(0 + " / " + dateList.size());
		} else {
			tvTitleGallery.setText(0 + " / " + selectBitmap.size());
		}
		btnFinish.setText("(" + selectBitmap.size() + "/" + MyConstants.MATCH_SELECT_DYNAMIC_PHOTO + ")完成");
		btnDraw = (Button) findViewById(R.id.btnDraw);
		btnAdd = (Button) findViewById(R.id.btnAdd);

		if(delete) {
			btnRightChecked.setVisibility(View.VISIBLE);
		} else {
			btnRightChecked.setVisibility(View.GONE);
		}
		if(from.equals("1")) {
			btnDraw.setVisibility(View.VISIBLE);
			btnAdd.setVisibility(View.GONE);
		} else if(from.equals("2")){
			btnDraw.setVisibility(View.VISIBLE);
			if(selectBitmap.size() < MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
				btnAdd.setVisibility(View.VISIBLE);
			} else {
				btnAdd.setVisibility(View.GONE);
			}
		} else if(from.equals("3")){
			btnDraw.setVisibility(View.GONE);
			btnAdd.setVisibility(View.GONE);
			btnFinish.setVisibility(View.GONE);
		} else {
			btnDraw.setVisibility(View.GONE);
			btnAdd.setVisibility(View.GONE);
		}
		if(dateList.size() != 0) {
			for (ImageItem item : dateList) {
				items.add(item.imagePath);
			}
		} else {
			for (ImageItem item : selectBitmap) {
				items.add(item.imagePath);
			}
		}
		pagerAdapter = new FilePagerAdapter(this, items);
		pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
			@Override
			public void onItemChange(int currentPosition) {

				selectPosition = currentPosition;
				if(dateList.size() != 0) {
					tvTitleGallery.setText((currentPosition + 1) + " / " + dateList.size());
				} else {
					tvTitleGallery.setText((currentPosition + 1) + " / " + selectBitmap.size());
				}
				
			}
		});

		mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(position);

		btnRightChecked.setOnClickListener(this);
		btnDraw.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
		btnLeft.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btnLeft:
			onBackPressed();
			break;

		case R.id.btnRightChecked:
			final TextDialog dialog = new TextDialog(this, false);
			dialog.setContentText("您确定删除这张图片吗？");
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
					Intent intent = new Intent();
					selectBitmap.remove(selectPosition);
					if(selectBitmap.size() == 0) {
						intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
						setResult(RESULT_OK, intent);
						finish();
					} else {
						if(from.equals("2")){
							if(selectBitmap.size() < MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
								btnAdd.setVisibility(View.VISIBLE);
							} else {
								btnAdd.setVisibility(View.GONE);
							}
						}

						items = new ArrayList<String>();
						for (ImageItem item : selectBitmap) {
							items.add(item.imagePath);
						}
						pagerAdapter.setData(items);
						tvTitleGallery.setText((selectPosition + 1) + " / " + selectBitmap.size());
						btnFinish.setText("("
								+ (selectBitmap.size()) + "/" + MyConstants.MATCH_SELECT_DYNAMIC_PHOTO + ")"
								+ "完成");
					}
				}
			});
			dialog.show();

			break;
		case R.id.btnFinish:
			if(selectBitmap != null && selectBitmap.size() > 0) {
				if (removeBitmap != null) {
					for (ImageItem removeItem : removeBitmap) {
						for (ImageItem imageItem : selectBitmap) {
							if (removeItem.imagePath.equals(imageItem.imagePath)) {
								selectBitmap.remove(imageItem);
								break;
							}
						}
					}
				}
				intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
				setResult(RESULT_OK, intent);
				finish();
			} else {
				toast("请选择一张图片");
			}
			break;

		case R.id.btnDraw://编辑按钮
			/*intent.setClass(getApplicationContext(), DrawPhotoActivity.class);
			String path = selectBitmap.get(selectPosition).imagePath;
			intent.putExtra(DrawPhotoActivity.FILEPATH, path);
			// 设置回调
			startActivityForResult(intent,
					MyConstants.INTENT_FOR_RESULT_GALLERY_TO_DRAWPHOTO);*/
			intent.setClass(getApplicationContext(), CropActivity.class);
			String path = selectBitmap.get(selectPosition).imagePath;
			intent.putExtra(CropActivity.FILEPATH, path);
			// 设置回调
			startActivityForResult(intent,
					MyConstants.INTENT_FOR_RESULT_GALLERY_TO_DRAWPHOTO);
//			startActivity(new Intent(this, CropActivity.class));
			break;

		case R.id.btnAdd:
			intent = new Intent(this, AlbumNewActivity.class);
			intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, selectBitmap);
			startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ALBUM);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == MyConstants.INTENT_FOR_RESULT_GALLERY_TO_DRAWPHOTO) {
				//将CropActivity中经过旋转、裁剪等处理后生成的图片保存新路径返回过来
				String path = data.getStringExtra(MyConstants.EXTRA_DRAW_PHOTO_PATH);
				selectBitmap.get(selectPosition).imagePath = path;
				items.clear();
				List<String> list = new ArrayList<String>();
				for (ImageItem item : selectBitmap) {
					list.add(item.imagePath);
				}
				items.addAll(list);
				pagerAdapter.notifyDataSetChanged();
			} else if (requestCode == MyConstants.INTENT_FOR_RESULT_GALLERY_TO_CLIPPHOTO) {
				// 拿到剪切数据
//				Bitmap bmap = data.getParcelableExtra("data");

//				// 显示剪切的图像
//				ImageView imageview = (ImageView) this
//						.findViewById(R.id.imageview);
//				imageview.setImageBitmap(bmap);

				// 图像保存到文件中
//				FileOutputStream foutput = null;
//				try {
//					foutput = new FileOutputStream(new File(selectBitmap.get(selectPosition).imagePath));
//					bmap.compress(Bitmap.CompressFormat.PNG, 100, foutput);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} finally {
//					if (null != foutput) {
//						try {
//							foutput.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
				String path = data.getStringExtra(MyConstants.EXTRA_DRAW_PHOTO_PATH);
				selectBitmap.get(selectPosition).imagePath = path;
				items.clear();
				List<String> list = new ArrayList<String>();
				for (ImageItem item : selectBitmap) {
					list.add(item.imagePath);
				}
				items.addAll(list);
				pagerAdapter.notifyDataSetChanged();
			}  else if(requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM) {
				ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
				selectBitmap = new ArrayList<ImageItem>();
				selectBitmap.addAll(images);
				items = new ArrayList<String>();
				for (ImageItem item : selectBitmap) {
					items.add(item.imagePath);
				}
				pagerAdapter.setData(items);
				tvTitleGallery.setText((selectPosition + 1) + " / " + selectBitmap.size());
				btnFinish.setText("("
						+ (selectBitmap.size()) + "/" + MyConstants.MATCH_SELECT_DYNAMIC_PHOTO + ")"
						+ "完成");
				if(selectBitmap.size() < MyConstants.MATCH_SELECT_DYNAMIC_PHOTO) {
					btnAdd.setVisibility(View.VISIBLE);
				} else {
					btnAdd.setVisibility(View.GONE);
				}
			}
		}
	}

}