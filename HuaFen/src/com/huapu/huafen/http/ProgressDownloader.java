package com.huapu.huafen.http;

import android.os.Handler;
import android.os.Looper;

import com.huapu.huafen.MyApplication;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/10/24.
 */
public class ProgressDownloader {

    public static final String TAG = "ProgressDownloader";

    private ProgressResponseBody.ProgressListener progressListener;
    private String url;
    private OkHttpClient client;
    private File destination;
    private Call call;

    public ProgressDownloader(String url, File destination, ProgressResponseBody.ProgressListener progressListener) {
        this.url = url;
        this.destination = destination;
        this.progressListener = progressListener;
        //在下载、暂停后的继续下载中可复用同一个client对象
        client = getProgressClient();
    }
    //每次下载需要新建新的Call对象
    private Call newCall(long startPoints) {
        Request request = new Request.Builder()
                .url(url)
                .header("RANGE", "bytes=" + startPoints + "-")//断点续传要用到的，指示下载的区间
                .build();
        return client.newCall(request);
    }

    public OkHttpClient getProgressClient() {
//        // 拦截器，用上ProgressResponseBody
//        Interceptor interceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Response originalResponse = chain.proceed(chain.request());
//                return originalResponse.newBuilder()
//                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
//                        .build();
//            }
//        };
//
//        return new OkHttpClient.Builder().addNetworkInterceptor(interceptor).build();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mOkHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("X-Platform", "android")
                        .addHeader("X-App-Id", "com.huapu.huafen")
                        .addHeader("X-App-Version", CommonUtils.getAppVersionName())
                        .addHeader("X-Dist-Channel", CommonUtils.getAppMetaData(MyApplication.getApplication(), "UMENG_CHANNEL"))
                        .build();
                Response originalResponse = chain.proceed(newRequest);
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
        return mOkHttpClient;
    }

    // startsPoint指定开始下载的点
    public void download(final long startsPoint) {
        call = newCall(startsPoint);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtil.i("e", e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.body().contentLength() <= 0) {
                    return;
                }
                save(response, startsPoint);
            }
        });
    }

    public void pause() {
        if(call!=null){
            call.cancel();
        }
    }

    private void save(Response response, long startsPoint) throws IOException{
        ResponseBody body = response.body();
        InputStream is = body.byteStream();
        FileOutputStream fos = new FileOutputStream(destination);
        try {
            int len;
            byte[] temp = new byte[1024];
            int i;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
        }finally {
            fos.close();
            is.close();
        }
    }

//    private void save(Response response, long startsPoint) throws IOException {
//        ResponseBody body = response.body();
//        InputStream in = body.byteStream();
//        FileChannel channelOut = null;
//        // 随机访问文件，可以指定断点续传的起始位置
//        RandomAccessFile randomAccessFile = null;
//        try {
//            randomAccessFile = new RandomAccessFile(destination, "rwd");
//            //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
//            channelOut = randomAccessFile.getChannel();
//            // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
//            MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, startsPoint, body.contentLength());
//            byte[] buffer = new byte[1024];
//            int len;
//            while ((len = in.read(buffer)) != -1) {
//                mappedBuffer.put(buffer, 0, len);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                in.close();
//                if (channelOut != null) {
//                    channelOut.close();
//                }
//                if (randomAccessFile != null) {
//                    randomAccessFile.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
