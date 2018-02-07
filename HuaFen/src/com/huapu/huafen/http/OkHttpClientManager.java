package com.huapu.huafen.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.huapu.huafen.MyApplication;
import com.huapu.huafen.db.CacheDataService;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.EncodeUtil;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.StringUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author liang_xs
 * @ClassName: OkHttpClientManager
 * @Description: OkHttpClient管理器
 * @date 2016-03-27
 */
public class OkHttpClientManager {
    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;


    private static final String TAG = "OkHttpClientManager";

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());
        mOkHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("X-Platform", "android")
                        .addHeader("X-App-Id", "com.huapu.huafen")
                        .addHeader("X-App-Version", CommonUtils.getAppVersionName())
                        .addHeader("X-Dist-Channel", CommonUtils.getAppMetaData(MyApplication.getApplication(), "UMENG_CHANNEL"))
                        .build();
                return chain.proceed(newRequest);
            }
        });
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return Response
     */
    private Response _getAsyn(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return 字符�?
     */
    private String _getAsString(String url) throws IOException {
        Response execute = _getAsyn(url);
        return execute.body().string();
    }


    /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    private void _getAsyn(String url, final StringCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }


    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参�?
     * @return
     */
    private Response _post(String url, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        LogUtil.i("OkHttpClientManager","请求网络url"+url);
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }


    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参�?
     * @return 字符�?
     */
    public String _postAsString(String url, Param... params) throws IOException {
        Response response = _post(url, params);
        return response.body().string();
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final StringCallback callback, Param... params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步的post请求(缓存)
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsynCache(String url, final StringCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(url, params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResultCache(callback, request, url, paramsArr);
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final StringCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request);
    }

    /**
     * 同步基于post的文件上�?
     *
     * @param params
     * @return
     */
    private Response _post(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File[] files, String[] fileKeys, HashMap<String, String> paramsMap) throws IOException {
        Param[] params = map2Params(paramsMap);
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
    }

    private Response _post(String url, File file, String fileKey, HashMap<String, String> paramsMap) throws IOException {
        Param[] params = map2Params(paramsMap);
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步基于post的文件上�?
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
    private void _postAsyn(String url, StringCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上�?
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKey
     * @throws IOException
     */
    private void _postAsyn(String url, StringCallback callback, File[] files, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上�?
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
    private void _postAsyn(String url, StringCallback callback, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
    private void _postAsyn(String url, StringCallback callback, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件单名称且携带其他form参数上传(HashMap)
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param paramsMap
     * @throws IOException
     */
    private void _postAsyn(String url, StringCallback callback, File file, String fileKey, HashMap<String, String> paramsMap) throws IOException {
        Param[] params = map2Params(paramsMap);
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，多文件不同名称且携带其他form参数上传（HashMap�?
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @param paramsMap
     * @throws IOException
     */
    private void _postAsyn(String url, StringCallback callback, File[] files, String[] fileKeys, HashMap<String, String> paramsMap) throws IOException {
        Param[] params = map2Params(paramsMap);
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，多文件同名称且携带其他form参数上传（HashMap�?
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKey
     * @param paramsMap
     * @throws IOException
     */
    private void _postAsyn(String url, StringCallback callback, File[] files, String fileKey, HashMap<String, String> paramsMap) throws IOException {
        Param[] params = map2Params(paramsMap);
        Request request = buildMultipartFormRequest(url, files, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final StringCallback callback) {
        _downloadAsyn(url, destFileDir, getFileName(url), callback);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final String fileName, final StringCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, fileName);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第�?个参数为文件的绝对路�?
                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });
    }


    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 加载图片
     *
     * @param view
     * @param url
     * @throws IOException
     */
    private void _displayImage(final ImageView view, final String url, final int errorResId) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                setErrorResId(view, errorResId);
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                try {
                    is = response.body().byteStream();
                    ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
                    ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(view);
                    int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
                    try {
                        is.reset();
                    } catch (IOException e) {
                        response = _getAsyn(url);
                        is = response.body().byteStream();
                    }

                    BitmapFactory.Options ops = new BitmapFactory.Options();
                    ops.inJustDecodeBounds = false;
                    ops.inSampleSize = inSampleSize;
                    final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageBitmap(bm);
                        }
                    });
                } catch (Exception e) {
                    setErrorResId(view, errorResId);

                } finally {
                    if (is != null) try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private void setErrorResId(final ImageView view, final int errorResId) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                view.setImageResource(errorResId);
            }
        });
    }


    //*************对外公布的方�?************


    public static Response getAsyn(String url) throws IOException {
        return getInstance()._getAsyn(url);
    }


    public static String getAsString(String url) throws IOException {
        return getInstance()._getAsString(url);
    }

    public static void getAsyn(String url, StringCallback callback) {
        getInstance()._getAsyn(url, callback);
    }

    public static Response post(String url, Param... params) throws IOException {
        return getInstance()._post(url, params);
    }
    public static Response post(String url, final Map<String, String> params) throws IOException {
        Param[] paramsArr = map2Params(params);
        return getInstance()._post(url, paramsArr);
    }
    public static String postAsString(String url, Param... params) throws IOException {
        return getInstance()._postAsString(url, params);
    }

    public static void postAsyn(String url, final StringCallback callback, Param... params) {
        getInstance()._postAsyn(url, callback, params);
    }

    private static int errorLevel = 0;
    private static long finishTime = 0;

    public static void postAsyn(final String url, final Map<String, String> params, final StringCallback callback) {
        LogUtil.i("liangxs", "errorLevel:" + errorLevel);
        LogUtil.i("liangxs", "finishTime:" + finishTime);
        CountDownTimer timer = null;
        if (System.currentTimeMillis() < finishTime) {
            LogUtil.i("liangxs", "延时");
            if (errorLevel == 0) {
                LogUtil.i("liangxs", "errorLevel =====0");
                getInstance()._postAsynCache(url, callback, params);
            } else if (errorLevel == 5) {
                CacheDataService cacheDataService = new CacheDataService(MyApplication.getApplication());
                String locationJson = cacheDataService.getCacheData(url, params);
                if (TextUtils.isEmpty(locationJson)) {
                    timer = new CountDownTimer(errorLevel * 1000, errorLevel * 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            getInstance()._postAsynCache(url, callback, params);
                        }
                    };
                    timer.start();
                } else {
                    callback.onResponse(locationJson);
                    LogUtil.i("liangxs", "缓存取--------");
                }
            } else {
                timer = new CountDownTimer(errorLevel * 1000, errorLevel * 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        getInstance()._postAsynCache(url, callback, params);
                    }
                };
                timer.start();
            }

        } else {
            LogUtil.i("liangxs", "无时效");
            errorLevel = 0;
            getInstance()._postAsynCache(url, callback, params);
        }
    }

    public static void postAsynNoCache(String url, Map<String, String> params, final StringCallback callback) {
        getInstance()._postAsyn(url, callback, params);
    }

    public static Response post(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        return getInstance()._post(url, files, fileKeys, params);
    }

    public static Response post(String url, File[] files, String[] fileKeys, HashMap<String, String> paramsMap) throws IOException {
        return getInstance()._post(url, files, fileKeys, paramsMap);
    }

    public static Response post(String url, File file, String fileKey) throws IOException {
        return getInstance()._post(url, file, fileKey);
    }

    public static Response post(String url, File file, String fileKey, Param... params) throws IOException {
        return getInstance()._post(url, file, fileKey, params);
    }

    public static Response post(String url, File file, String fileKey, HashMap<String, String> paramsMap) throws IOException {
        return getInstance()._post(url, file, fileKey, paramsMap);
    }

    public static void postAsyn(String url, StringCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, params);
    }

    public static void postAsyn(String url, StringCallback callback, File[] files, String fileKey, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKey, params);
    }


    public static void postAsyn(String url, StringCallback callback, File file, String fileKey) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey);
    }


    public static void postAsyn(String url, StringCallback callback, File file, String fileKey, Param... params) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, params);
    }

    public static void postAsyn(String url, StringCallback callback, File file, String fileKey, HashMap<String, String> paramsMap) throws IOException {
        getInstance()._postAsyn(url, callback, file, fileKey, paramsMap);
    }

    public static void postAsyn(String url, StringCallback callback, File[] files, String[] fileKeys, HashMap<String, String> paramsMap) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKeys, paramsMap);
    }

    public static void postAsyn(String url, StringCallback callback, File[] files, String fileKey, HashMap<String, String> paramsMap) throws IOException {
        getInstance()._postAsyn(url, callback, files, fileKey, paramsMap);
    }

    public static void displayImage(final ImageView view, String url, int errorResId) throws IOException {
        getInstance()._displayImage(view, url, errorResId);
    }


    public static void displayImage(final ImageView view, String url) {
        getInstance()._displayImage(view, url, -1);
    }

    public static void downloadAsync(String url, String destDir, StringCallback callback) {
        getInstance()._downloadAsyn(url, destDir, callback);
    }

    public static void downloadAsync(String url, String destDir, String fileName, StringCallback callback) {
        getInstance()._downloadAsyn(url, destDir, fileName, callback);
    }

    //****************************


    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, Param[] params) {
        params = validateParam(params);

        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        for (Param param : params) {
            if (null == param.value || "".equals(param.value)) {
                continue;
            }
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, null == param.value ? "" : param.value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                if (fileKeys.length == files.length) {
                    //TODO 根据文件名设置contentType
                    builder.addPart(Headers.of("Content-Disposition",
                            "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                            fileBody);
                } else {
                    //TODO 根据文件名设置contentType
                    builder.addPart(Headers.of("Content-Disposition",
                            "form-data; name=\"" + fileKeys[0] + "\"; filename=\"" + fileName + "\""),
                            fileBody);
                }

            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    private Param[] validateParam(Param[] params) {
        if (params == null)
            return new Param[0];
        else return params;
    }

    private Param[] map2Params(String url, Map<String, String> params) {
        if (params == null) return new Param[0];
        // TODO 在此对params进行排序
        CacheDataService cacheDataService = new CacheDataService(MyApplication.getApplication());
        String cacheVersion = cacheDataService.getCacheVersion(url, params);
        if (!TextUtils.isEmpty(cacheVersion)) {
            params.put(CacheDataService.COLUMN_CACHE_VERSION, cacheVersion); // 添加缓存参数
        }
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    private static Param[] map2Params(Map<String, String> params) {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    private static final String SESSION_KEY = "Set-Cookie";
    private static final String mSessionKey = "JSESSIONID";

    private Map<String, String> mSessions = new HashMap<String, String>();

    private void deliveryResultCache(final StringCallback callback, final Request request, final String url, final Param[] paramsArr) {

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    final String string = response.body().string();
                    LogUtil.d("danielluan", "http response " + string);
                    sendSuccessResultCallback(string, callback, url, paramsArr);
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                }

            }
        });

        LogUtil.d("danielluan", "http " + request.toString());
        StringBuffer sb = new StringBuffer();
        for (Param param : paramsArr) {
            sb.append(param.toString() + ";");
        }
        LogUtil.d("danielluan", "http params =====>" + sb.toString());
    }

    private void deliveryResult(final StringCallback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    if (response.isSuccessful()) {
                        final String string = response.body().string();
                        LogUtil.d("danielluan", "http " + string);
                        sendSuccessResultCallback(string, callback);
                    } else {
                        throw new IOException("response code:" + response.code());
                    }
                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                }

            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final StringCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    private void sendSuccessResultCallback(final String string, final StringCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(string);
                }
            }
        });
    }

    private void sendSuccessResultCallback(final String string, final StringCallback callback, final String url, final Param[] paramsArr) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    CacheDataService cacheDataService = new CacheDataService(MyApplication.getApplication());
                    HashMap<String, String> params = new HashMap<String, String>();
                    String locationVersion = "";
                    for (Param param : paramsArr) {
                        if ((param.key).equalsIgnoreCase(CacheDataService.COLUMN_CACHE_VERSION)) {
                            locationVersion = param.value;
                        }
                        params.put(param.key, param.value);
                    }
                    try {
                        JSONObject obj = new JSONObject(string);
                        String objString = obj.optString("obj");
                        int objErrorLevel = obj.optInt("errorLevel");
                        if (errorLevel != objErrorLevel && objErrorLevel != -1) {
                            errorLevel = objErrorLevel;
                            finishTime = System.currentTimeMillis() + errorLevel * 10000;
                        }
                        if (!TextUtils.isEmpty(objString)) {
                            JSONObject cacheJson = new JSONObject(objString);
                            String cacheVersion = cacheJson.optString(CacheDataService.COLUMN_CACHE_VERSION);
                            if (!TextUtils.isEmpty(cacheVersion)) {
                                if (locationVersion.equals(cacheVersion)) {
                                    String locationJson = cacheDataService.getCacheData(url, params);
                                    callback.onResponse(locationJson);
                                    LogUtil.i("liang", "locationJson: " + locationJson);
                                } else {
                                    params.put(CacheDataService.COLUMN_CACHE_VERSION, cacheVersion); // 添加缓存版本到数据库
                                    cacheDataService.addCacheData(url, params, string);
                                    callback.onResponse(string);
                                    LogUtil.i("liang", "onResponse: " + string);
                                }
                            } else {
                                callback.onResponse(string);
                            }
                        } else {
                            callback.onResponse(string);
                        }
                    } catch (JSONException e) {
                        callback.onResponse(string);
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private Request buildPostRequest(String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            builder.add(param.key, null == param.value ? "" : param.value);
        }

        Request request;
        RequestBody requestBody = builder.build();
        String header = auth(url, params);
        if (TextUtils.isEmpty(header)) {
            request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
        } else {
            try {
                request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", encodeHeadInfo(header))
                        .post(requestBody)
                        .build();
            } catch (IllegalArgumentException e) {
                request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
            }
        }


        return request;
    }

    private String encodeHeadInfo(String headInfo) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0, length = headInfo.length(); i < length; i++) {
            char c = headInfo.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                stringBuffer.append(String.format("\\u%04x", (int) c));
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }

    private String auth(String url, Param[] params) {
        String header = "";
        String activationSecret = CommonPreference.getActivationSecret();
        String accessSecret = CommonPreference.getAccessSecret();

        String activationToken = CommonPreference.getActivationToken();
        String accessToken = CommonPreference.getAccessToken();
        String millis = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
        String uuid = CommonUtils.getUUID();

        String key;
        try {
            if (!TextUtils.isEmpty(accessSecret)) {
                String var1 = EncodeUtil.encode(activationSecret);
                String var2 = EncodeUtil.encode(accessSecret);
                key = var1 + "&" + var2;
            } else {
                key = EncodeUtil.encode(activationSecret);
            }
        } catch (UnsupportedEncodingException e) {
            key = null;
        }


        if (TextUtils.isEmpty(key)) {
            return null;
        }
        LogUtil.e("key", key);
        Map<String, String> map = new HashMap<>();
        map.put("hpauth_a", activationToken);
        map.put("hpauth_u", accessToken);
        map.put("hpauth_n", uuid);
        map.put("hpauth_m", "HMAC-SHA1");
        map.put("hpauth_v", "1.0");
        map.put("hpauth_t", millis);
        Map<String, String> paramsMap = params2Map(params);
        map.putAll(paramsMap);
        Map<String, String> baseMap = CommonUtils.sortMapByKey(map);
        LogUtil.e("baseMap", baseMap.toString());
        try {
            String u = URLEncoder.encode(url, "utf-8");
            String base = "POST&" + u + "&";
            if (baseMap != null) {
                for (Map.Entry<String, String> entry : baseMap.entrySet()) {
                    String k = entry.getKey();
                    String v = entry.getValue();
                    String encodeKey = EncodeUtil.encode(k);
                    String encodeValue;
                    if (!TextUtils.isEmpty(v)) {
                        String tmp = EncodeUtil.encode(v);
                        encodeValue = tmp.replaceAll("\\+", "%20");
                    } else {
                        encodeValue = "";
                    }

                    String part = encodeKey + "=" + encodeValue + "&";
                    base += part;
                }
                base = StringUtils.substringBeforeLast(base, "&");
            }

            LogUtil.e("encodeBase", base + "");
            String hmacSha1 = CommonUtils.hmacSha1(base, key);
            LogUtil.e("hmacSha1", hmacSha1);
            header = "HPAuth " + "a=\"" + activationToken + "\", " + "u=\"" + accessToken + "\", " +
                    "n=\"" + uuid + "\", " + "s=\"" + hmacSha1 + "\", " +
                    "m=\"" + "HMAC-SHA1" + "\", " + "v=\"1.0\", " + "t=\"" + millis + "\"";
            LogUtil.e("header", header);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return header;
    }

    private Map<String, String> params2Map(Param[] params) {
        if (params == null) return null;
        Map<String, String> map = new HashMap<>();
        for (Param param : params) {
            map.put(param.key, param.value);
        }
        return map;
    }

    public interface StringCallback {
        void onError(Request request, Exception e);

        void onResponse(String response);
    }

//    public static abstract class ResultCallback<T>
//    {
//        Type mType;
//
//        public ResultCallback()
//        {
//            mType = getSuperclassTypeParameter(getClass());
//        }
//
//        static Type getSuperclassTypeParameter(Class<?> subclass)
//        {
//            Type superclass = subclass.getGenericSuperclass();
//            if (superclass instanceof Class)
//            {
//                throw new RuntimeException("Missing type parameter.");
//            }
//            ParameterizedType parameterized = (ParameterizedType) superclass;
//
//            Object o = JSON.parseObject(string, callback.mType);
//            List<VO> list = JSON.parseObject("...", new TypeReference<List<VO>>() {});
//            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
//        }
//
//        public abstract void onError(Request request, Exception e);
//
//        public abstract void onResponse(T response);
//    }

    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;

        public String toString() {
            return key + "=" + value;
        }
    }


    public void setCertificates(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init
                    (
                            null,
                            trustManagerFactory.getTrustManagers(),
                            new SecureRandom()
                    );
            mOkHttpClient.setSslSocketFactory(sslContext.getSocketFactory());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
