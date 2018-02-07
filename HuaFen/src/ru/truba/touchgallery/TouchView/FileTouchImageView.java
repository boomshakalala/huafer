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
package ru.truba.touchgallery.TouchView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class FileTouchImageView extends UrlTouchImageView {

    public FileTouchImageView(Context ctx) {
        super(ctx);

    }

    public FileTouchImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public void setUrl(String imagePath) {
        new ImageLoadTask().execute(imagePath);
    }

    //No caching load
    public class ImageLoadTask extends UrlTouchImageView.ImageLoadTask {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String path = strings[0];
//            Bitmap bm = null;
//            try {
//            	File file = new File(path);
//            	FileInputStream fis = new FileInputStream(file);
//                InputStreamWrapper bis = new InputStreamWrapper(fis, 8192, file.length());
//                bis.setProgressListener(new InputStreamProgressListener()
//				{					
//					@Override
//					public void onProgress(float progressValue, long bytesLoaded,
//							long bytesTotal)
//					{
//						publishProgress((int)(progressValue * 100));
//					}
//				});
//                try {
//                	 bm = BitmapFactory.decodeStream(bis);
//                } catch (OutOfMemoryError e) {
//                    e.printStackTrace();
//                }
//               
//                bis.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return bm;
            BufferedInputStream in;
            Bitmap bitmap = null;
            try {
                in = new BufferedInputStream(new FileInputStream(
                        new File(path)));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = 2;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                BitmapFactory.decodeStream(in, null, options);
                in.close();
                int i = 0;
                while (true) {
                    if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
                        in = new BufferedInputStream(new FileInputStream(new File(path)));
                        options.inSampleSize = (int) Math.pow(2.0D, i);
                        options.inJustDecodeBounds = false;
                        bitmap = BitmapFactory.decodeStream(in, null, options);
                        break;
                    }
                    i += 1;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return bitmap;
        }
    }
}
