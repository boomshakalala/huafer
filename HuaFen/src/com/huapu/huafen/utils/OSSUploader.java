package com.huapu.huafen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huapu.huafen.common.MyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/25.
 */

public class OSSUploader {

    private final static String TAG = OSSUploader.class.getSimpleName();
    public static final String IMAGE_FOLDER = MyConstants.OSS_FOLDER_BUCKET;

    public static final String TYPE_MOMENT = "/momment/";
    public static final String TYPE_ARTICLE = "/article/";

    private static String uploadBitmap2OSS(Context context,String type ,String uri){

        String picKey = DateTimeUtils.getYearMonthDayFolder(type);
        String folder = MyConstants.OSS_FOLDER_BUCKET;
        String fileName = System.currentTimeMillis() + ".jpg";
        String key = picKey + fileName;        //picKey == huafer/uid/goods/year/month/day/  fileName == 13525235423666.jpg
        AliUpdateEvent updateEvent = new AliUpdateEvent(context, uri, folder, key);
        PutObjectResult result = updateEvent.putObjectFromLocalFile();
        if(result != null) {
            String url = MyConstants.OSS_IMG_HEAD + key;
            LogUtil.e(TAG,url);
            return url;
        }
        return null;

    }


    private static <T> List<T> uploadOSS(Context context,String type ,List<T> list ,GenLocalPath<T> genLocalPath){
        if(!ArrayUtil.isEmpty(list)){
            List<T> tmp = new ArrayList<>();
            for(T t:list){
                if(t!=null){
                    String path = genLocalPath.getLocalPath(t);
                    if(!TextUtils.isEmpty(path) && !path.startsWith(MyConstants.HTTP) && !path.startsWith(MyConstants.HTTPS)){
                        Bitmap bm = ImageUtils.revitionImageSize(path);
                        String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), System.currentTimeMillis() + ".jpg");
                        String url = uploadBitmap2OSS(context, type, picPath);
                        if(!TextUtils.isEmpty(url)){
                            tmp.add(t);
                            genLocalPath.deal(t,url);
                        }else{
                            return null;
                        }
                    }
                }
            }
            FileUtils.delAllFile(FileUtils.getIconDir());
            return tmp;
        }
        return null;
    }


    private static <T> T uploadOSS(Context context,String type ,T t ,GenLocalPath<T> genLocalPath){
        if(t!=null){
            String path = genLocalPath.getLocalPath(t);
            if(!TextUtils.isEmpty(path) && !path.startsWith(MyConstants.HTTP) && !path.startsWith(MyConstants.HTTPS)){
                Bitmap bm = ImageUtils.revitionImageSize(path);
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), System.currentTimeMillis() + ".jpg");
                String url = uploadBitmap2OSS(context, type, picPath);
                if(!TextUtils.isEmpty(url)){
                    genLocalPath.deal(t,url);
                    return t;
                }
            }
        }
        return null;
    }

    public interface GenLocalPath<T>{
        String getLocalPath(T t);
        void deal(T t ,String path);
    }


    public static class UploadAsyncTask<T> extends AsyncTask<Void,Void,List<T>> {

        private Context context;
        private List<T> list ;
        private String type;
        private GenLocalPath<T> genLocalPath;
        private OnUploadListener<T> onUploadListener;

        public UploadAsyncTask(Context context,String type ,List<T> list ,GenLocalPath<T> genLocalPath,OnUploadListener<T> onUploadListener){
            this.context = context;
            this.list = list;
            this.type = type;
            this.genLocalPath = genLocalPath;
            this.onUploadListener = onUploadListener;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(onUploadListener!=null){
                onUploadListener.onPreExecute();
            }
        }

        @Override
        protected List<T> doInBackground(Void... params) {
            return uploadOSS(context,type,list,genLocalPath);
        }

        @Override
        protected void onPostExecute(List<T> ts) {
            super.onPostExecute(ts);
            if(!ArrayUtil.isEmpty(ts)){
                if(onUploadListener!=null){
                    onUploadListener.onUploadSuccess(ts);
                }
            }else{
                if(onUploadListener!=null){
                    onUploadListener.onUploadFailed(list);
                }
            }
        }
    }

    public interface OnUploadListener<T> {
        void onUploadSuccess(List<T> list);
        void onUploadFailed(List<T> list);
        void onPreExecute();
    }

    public static <T> void uploadOSSAsync(Context context,String type ,List<T> list ,GenLocalPath<T> genLocalPath,OnUploadListener<T> onUploadListener){
        UploadAsyncTask<T> uploadAsyncTask = new UploadAsyncTask<T>(context,type,list,genLocalPath,onUploadListener);
        uploadAsyncTask.execute();
    }



    public static class UploadSingleAsyncTask<T> extends AsyncTask<Void,Void,T>{

        private Context context;
        private T t ;
        private String type;
        private GenLocalPath<T> genLocalPath;
        private OnUploadSingeListener<T> onUploadListener;

        public UploadSingleAsyncTask(Context context,String type ,T t ,GenLocalPath<T> genLocalPath,OnUploadSingeListener<T> onUploadListener){
            this.context = context;
            this.t = t;
            this.type = type;
            this.genLocalPath = genLocalPath;
            this.onUploadListener = onUploadListener;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(onUploadListener!=null){
                onUploadListener.onPreExecute();
            }
        }

        @Override
        protected T doInBackground(Void... params) {
            return uploadOSS(context,type,t,genLocalPath);
        }

        @Override
        protected void onPostExecute(T t) {
            super.onPostExecute(t);
            if(t!=null){
                if(onUploadListener!=null){
                    onUploadListener.onUploadSuccess(t);
                }
            }else{
                if(onUploadListener!=null){
                    onUploadListener.onUploadFailed(t);
                }
            }
        }
    }

    public interface OnUploadSingeListener<T> {
        void onUploadSuccess(T t);
        void onUploadFailed(T t);
        void onPreExecute();
    }

    public static <T> void uploadSingleOSSAsync(Context context,String type ,T t ,GenLocalPath<T> genLocalPath,OnUploadSingeListener<T> onUploadListener){
        UploadSingleAsyncTask<T> uploadAsyncTask = new UploadSingleAsyncTask<T>(context,type,t,genLocalPath,onUploadListener);
        uploadAsyncTask.execute();
    }
}
