package com.huapu.huafen.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.beans.Credentials;
import com.huapu.huafen.beans.CredentialsResult;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * 初始化阿里云图片服务器
 *
 * @author liang_xs
 */
public class AliUpdateEvent {
    private OSS oss;
    // 运行sample前需要配置以下字段为有效的值
    private static final String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    private String uploadFilePath = "";

    private String testBucket = "";
    private String uploadObject = "";
    private Context mContext;

    public AliUpdateEvent(Context mContext, String uploadFilePath, String testBucket, String uploadObject) {
        this.mContext = mContext;
        this.uploadFilePath = uploadFilePath;
        this.testBucket = testBucket;
        this.uploadObject = uploadObject;
    }

    private void init(Credentials credentials) {
        try {
            OSSStsTokenCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(credentials.accessKeyId, credentials.accessKeySecret, credentials.securityToken);
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            OSSLog.enableLog();
            oss = new OSSClient(mContext, endpoint, credentialProvider, conf);
        } catch (Exception e) {
            Log.d("putObjectFromLocalFile", e.getMessage());
        }

    }

    @Nullable
    private Credentials getCredentials() {
        Credentials credentials = CommonPreference.getCredentials();
        if (credentials == null) {
            credentials = doRequestForCredentials();
        } else {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String utcTime = credentials.expiration;
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.util.Date utcDate = format.parse(utcTime);
                long utcToLocal = utcDate.getTime();

                Calendar calendar = Calendar.getInstance();
                long now = calendar.getTimeInMillis();
                if (now > utcToLocal - 1000 * 60 * 10) {
                    credentials = doRequestForCredentials();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return credentials;
    }

    private Credentials doRequestForCredentials() {
        Credentials credentials = null;
        try {
            String response = OkHttpClientManager.getInstance()._postAsString(MyConstants.GET_STS_TOKEN);
            CredentialsResult result = JSON.parseObject(response, CredentialsResult.class);
            if (result != null && result.code == ParserUtils.RESPONSE_SUCCESS_CODE && result.obj != null) {
                credentials = result.obj.credentials;
                CommonPreference.setCredentials(credentials);
            }
        } catch (Exception e) {
            LogUtil.e("AliUpdateEvent", e.getMessage());
        }
        return credentials;
    }


    /**
     * 从本地文件上传，使用非阻塞的异步接口
     */
    public void asyncPutObjectFromLocalFile(final OnAliOSSCompleteListener mOnAliOSSCompleteListener) {
        Credentials credentials = CommonPreference.getCredentials();
        if (credentials == null) {
            asyncInitAndUpload(mOnAliOSSCompleteListener);
        } else {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String utcTime = credentials.expiration;
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.util.Date utcDate = format.parse(utcTime);
                long utcToLocal = utcDate.getTime();

                Calendar calendar = Calendar.getInstance();
                long now = calendar.getTimeInMillis();
                if (now > utcToLocal - 1000 * 60 * 10) {
                    asyncInitAndUpload(mOnAliOSSCompleteListener);
                } else {
                    init(credentials);
                    asyncUpload(mOnAliOSSCompleteListener);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    private void asyncInitAndUpload(final OnAliOSSCompleteListener mOnAliOSSCompleteListener) {
        OkHttpClientManager.postAsyn(MyConstants.GET_STS_TOKEN, new HashMap<String, String>(), new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                try {
                    CredentialsResult result = JSON.parseObject(response, CredentialsResult.class);
                    if (result != null && result.code == ParserUtils.RESPONSE_SUCCESS_CODE && result.obj != null) {
                        Credentials cc = result.obj.credentials;
                        CommonPreference.setCredentials(cc);
                        init(cc);
                        asyncUpload(mOnAliOSSCompleteListener);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void asyncUpload(final OnAliOSSCompleteListener mOnAliOSSCompleteListener) {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(testBucket, uploadObject, uploadFilePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                if (mOnAliOSSCompleteListener != null) {
                    mOnAliOSSCompleteListener.onProgress(currentSize, totalSize);
                }
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                if (mOnAliOSSCompleteListener != null) {
                    mOnAliOSSCompleteListener.onComplete();
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (mOnAliOSSCompleteListener != null) {
                    mOnAliOSSCompleteListener.onFailed();
                }
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    /**
     * 从本地文件上传，采用阻塞的同步接口
     */
    public PutObjectResult putObjectFromLocalFile() {
        Credentials credentials = getCredentials();
        init(credentials);
        PutObjectResult putResult = null;
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(testBucket, uploadObject, uploadFilePath);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream");
        try {
            // 设置Md5以便校验
            metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 如果是从文件上传
            // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(byte[])); // 如果是上传二进制数据
        } catch (IOException e) {
            e.printStackTrace();
        }
        put.setMetadata(metadata);

        try {
            putResult = oss.putObject(put);
            Log.d("PutObject", "UploadSuccess");
            Log.d("ETag", putResult.getETag());
            Log.d("RequestId", putResult.getRequestId());
            String a = putResult.getResponseHeader().get("url");
        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
            return null;
        } catch (ServiceException e) {
            // 服务异常
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
            return null;
        } catch (NullPointerException e) {
            Log.d("putObjectFromLocalFile", e.getMessage());
        }
        return putResult;
    }


    public interface OnAliOSSCompleteListener {
        void onComplete();

        void onFailed();

        void onProgress(long currentSize, long totalSize);
    }
}
