package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
//图片旋转、裁剪等处理
public class CropActivity extends BaseActivity {

	/** 传递动作,为takephoto 表示拍照，否则传递过来的是图片的路径 */
	public static final String FILEPATH = "filepath";
	public static final String ACTION_INIT = "action_init";
	
	
	// Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    Bitmap croppedImage;
    Bitmap bitmap;
	private int imgHeight, imgWidth;

	/** 操纵图片的路径 **/
	private String filePath = "";
	//广播
	private BroadcastReceiver broadcastReceiver = null;
	Intent intent = null;
	
	
    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }
    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

		setTitleString("图片编辑");
        //获取传递过来的图片路径
        intent = getIntent();
        if(intent.hasExtra(FILEPATH)){
        	//获取传递过来的图片地址，保存到新的文件地址中
        	filePath = intent.getStringExtra(FILEPATH);
        }
        
        // Initialize components of the app
        final CropImageView cropImageView = (CropImageView) findViewById(R.id.cropImageView);
       // bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sidewayssky);
        //通过工具类将其转换为bitmap
        bitmap = ImageUtils.getLoacalBitmap(this, filePath);
		if(bitmap == null) {
			toast("图片异常，不支持编辑");
			finish();
		}
//        bitmap = ImageUtils.compressImage(bitmap);
//        bitmap = ImageUtils.revitionImageSize(filePath);
        //并显示到控件上
        cropImageView.setImageBitmap(bitmap);
        // set fixedaspectratio 设置选中是否为方形
        cropImageView.setFixedAspectRatio(true);

        //Sets the rotate button 旋转
        final ImageView ivBtnRotate = (ImageView) findViewById(R.id.ivBtnRotate);
		ivBtnRotate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	bitmap = cropImageView.rotateBitmap(ROTATE_NINETY_DEGREES);
            }
        });
        

        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);


        final ImageView tvBtnCrop = (ImageView) findViewById(R.id.tvBtnCrop);
		tvBtnCrop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedImage();
				if(croppedImage != null) {
					bitmap = croppedImage;
					cropImageView.setImageBitmap(bitmap);
				}
            }
        });

		//Sets the rotate button 改变亮度
		final ImageView ivBtnColor = (ImageView) findViewById(R.id.ivBtnColor);
		ivBtnColor.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(ivBtnColor.isSelected()) {
					ivBtnColor.setSelected(false);
					cropImageView.setImageBitmap(bitmap);
				} else {
					ivBtnColor.setSelected(true);
					imgHeight = bitmap.getHeight();
					imgWidth = bitmap.getWidth();
					Bitmap bmp = Bitmap.createBitmap(imgWidth, imgHeight,
							Config.ARGB_8888);
					int brightness = 160 - 127;
					ColorMatrix cMatrix = new ColorMatrix();
					// 改变亮度
					cMatrix.set(new float[] {
							1, 0, 0, 0, brightness,
							0, 1, 0, 0, brightness,
							0, 0, 1, 0, brightness,
							0, 0, 0, 1, 0 });

					Paint paint = new Paint();
					paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

					Canvas canvas = new Canvas(bmp);
					// 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
					canvas.drawBitmap(bitmap, 0, 0, paint);
					cropImageView.setImageBitmap(bmp);
				}

			}
		});

        //放弃
        TextView tvBtnGiveUp = (TextView) findViewById(R.id.tvBtnGiveUp);
		tvBtnGiveUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CropActivity.this.finish();
			}
		});
        //继续（完成）
        TextView tvBtnNext = (TextView) findViewById(R.id.tvBtnNext);
		tvBtnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//1.先把bitmap保存到sd卡
				//新建一个文件保存照片
				File file = new File(FileUtils.getIconDir() + System.currentTimeMillis() + ".jpg");
				try {
					boolean isSave = ImageUtils.saveMyBitmap(file, bitmap);// 将图片重新存入SD卡
					if(isSave){
						bitmap.recycle();
						//裁剪后的图片
						if(croppedImage != null){
							croppedImage.recycle();
						}
						Intent intent = new Intent();
				//		intent.setClass(getApplicationContext(), GalleryFileActivity.class);
						intent.putExtra(MyConstants.EXTRA_DRAW_PHOTO_PATH, file.getAbsolutePath());
						setResult(Activity.RESULT_OK, intent);
//						Toast.makeText(CropActivity.this, "已保存至SD卡"+FileUtils.getIconDir()+"目录下", Toast.LENGTH_LONG).show();
						finish();
					}else{
//						Toast.makeText(CropActivity.this, "图片保存失败", Toast.LENGTH_LONG).show();
						toast("图片保存失败");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				//2.把sd卡路径返回到GalleryFileActivity中，在GalleryFileActivity的onActivityResult()中获取到此路径path
			}
		});

    }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnTitleLeft:
			finish();
			break;

		default:
			break;
		}
	}

}
