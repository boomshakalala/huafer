package twodimencode.create2code;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class Create2DCode {
	/**
	 * 将字符串转换成二维码
	 * */
	public static Bitmap Create2DCode(String str) throws WriterException {

		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, 400, 400);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 将logo(中间)加入到二维码中或将图标(在右上角)加入二维码中
	 * 加logo传1，加图片传2
	 * */
	public static Bitmap logo2DCode(Bitmap code, Bitmap logo,int maptype) {
		if (code == null) {
			return null;
		}
		int bgWidth = code.getWidth();
		int bgHeight = code.getHeight();
		int fgWidth = logo.getWidth();
		int fgHeight = logo.getHeight();
		int _left = 0,_top=0;
		switch (maptype) {
		case 1:
			_left=(bgWidth - fgWidth) / 2;
			_top=(bgHeight - fgHeight) / 2;
			break;
		case 2:
			_left=(bgWidth - fgWidth);
			_top=0;
			break;
		}
		Bitmap newmap = Bitmap
				.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(newmap);
		canvas.drawBitmap(code, 0, 0, null);
		canvas.drawBitmap(logo, _left,
				_top, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newmap;
	}

	/**
	 * 将logo(中间)和图标(在右上角)加入二维码中
	 * */
	public static Bitmap logo2DCode(Bitmap code,Bitmap logo,Bitmap pic){
		
		Bitmap logocode=logo2DCode(code, logo,1);
		
		if (logocode == null) {
			return null;
		}
		int bgWidth = logocode.getWidth();
		int bgHeight = logocode.getHeight();
		int fgWidth = pic.getWidth();
//		int fgHeight = pic.getHeight();
		Bitmap newmap=Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas canvas=new Canvas(newmap);
		canvas.drawBitmap(logocode, 0, 0,null);
		canvas.drawBitmap(pic,(bgWidth-fgWidth), 0, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newmap;
	}

}
