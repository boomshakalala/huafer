package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Campaign;
import com.huapu.huafen.beans.CreditScoreData;
import com.huapu.huafen.beans.Share;
import com.huapu.huafen.callcenter.CallCenterHelper;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.ShareDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.PhoneInfoUitls;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.MoveImageView;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class WebViewActivity extends BaseActivity {

    public static final int FROM_WEBVIEW = 1000;
    public static final int FROM_LOGIN = 1001;
    public static final int FROM_WEBVIEW2 = 2000;

    private static final String APP_CACHE_DIRNAME = "/webcache"; // web缓存目录
    private WebView webview;
    private String webViewUrl = "";
    private String strTitle = "";
    private ImageView ivBtnService;
    private boolean isHelp = false;
    private String right = "";
    private MoveImageView moveImage;
    private boolean stopLoading = false;
    private Share share;
    private ArrayList<String> titleList = new ArrayList<String>();
    private String js_func_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        if (getIntent().hasExtra(MyConstants.EXTRA_WEBVIEW_URL)) {
            webViewUrl = getIntent().getStringExtra(MyConstants.EXTRA_WEBVIEW_URL);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_WEBVIEW_TITLE)) {
            strTitle = getIntent().getStringExtra(MyConstants.EXTRA_WEBVIEW_TITLE);
        }
        if (getIntent().hasExtra("isHelp")) {// 为1表示是帮助中心，需要小花图标
            isHelp = getIntent().getBooleanExtra("isHelp", false);
        }

        initView();
    }

    private void initView() {
        webview = (WebView) findViewById(R.id.webview);
        ivBtnService = (ImageView) findViewById(R.id.ivBtnService);
        ivBtnService.setOnClickListener(this);
        if (isHelp) {
            moveImage = new MoveImageView(this);
            moveImage.setBackgroundResource(R.drawable.btn_help_service);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = CommonUtils.dp2px(50);
            params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            this.addContentView(moveImage, params);
            moveImage.setOnImageClickListener(new MoveImageView.ImageClickListener() {

                @Override
                public void onImageClick() {
                    //RongCloudEvent.startRongCustomerServiceChat(WebViewActivity.this);
                    CallCenterHelper.start(WebViewActivity.this, "", "", "");
                }
            });
            right = "意见反馈";
        } else {
            ivBtnService.setVisibility(View.GONE);
        }

        getTitleBar().
                setTitle(strTitle).
                setRightText(right, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (isHelp) {
//                            Intent intent = new Intent(WebViewActivity.this, FeedBackActivity.class);
//                            startActivity(intent);
                            FeedbackAPI.openFeedbackActivity();
                        }
                    }
                });
        initWebView();
    }

    private void initWebView() {

        final ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("X-Platform", "android");
        arrayMap.put("X-AppId", "com.huapu.huafen");
        arrayMap.put("X-AppVersion", CommonUtils.getAppVersionName());
        arrayMap.put("X-DistChannel", CommonUtils.getAppMetaData(MyApplication.getApplication(), "UMENG_CHANNEL"));

        WebSettings webSettings = webview.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (CommonUtils.isNetAvaliable(this)) {
            // 建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置缓存模式
        } else {
            // 建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 设置缓存模式
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(webSettings.getUserAgentString())
                .append(" huafer/").append(CommonUtils.getAppVersionName())
                .append(" NetType/").append(PhoneInfoUitls.getNetworkType());

        webSettings.setUserAgentString(stringBuilder.toString());
        // 开启DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        // 开启database storage API功能
        webSettings.setDatabaseEnabled(true);
        webSettings.setSavePassword(false);
//		String cacheDirPath = MyApplication.getApp().getFilesDir().getAbsolutePath() + APP_CACHE_DIRNAME;
        String cacheDirPath = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data" + File.separator + getPackageName() + APP_CACHE_DIRNAME;
        } else {
            cacheDirPath = MyApplication.getApplication().getFilesDir().getAbsolutePath() + APP_CACHE_DIRNAME;
        }
        // 设置数据库缓存路径
        webSettings.setDatabasePath(cacheDirPath); // API 19
        // 设置Application caches缓存目录
        webSettings.setAppCachePath(cacheDirPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 开启Application Cache功能
        webSettings.setAppCacheEnabled(true);

        webview.addJavascriptInterface(new JavaScriptInterface(this), "androidJSBridge");

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("https://api.huafer.cc/download.html")
                        || url.contains("https://i.huafer.cc/misc/bank-card-auth/")) {
                    view.stopLoading();
                    startRequestForCommitUrl(url);
                } else {
                    view.loadUrl(url, arrayMap);
                }
                return true;
            }

            // 页面开始时调用
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.d("danielluan", "on page start");
            }

            // 页面加载完成调用
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webViewUrl = url;
                webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                // super.onReceivedSslError(view, handler, error);

                // 接受所有网站的证书，忽略SSL错误，执行访问网页
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitleString(title);
                titleList.add(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                LogUtil.d("danielluan", "onProgressChanged " + newProgress);

            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue,
                        result);
            }

            @Override
            @Deprecated
            public void onExceededDatabaseQuota(String url,
                                                String databaseIdentifier, long quota,
                                                long estimatedDatabaseSize, long totalQuota,
                                                QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(estimatedDatabaseSize * 2);
            }
        });

        webview.removeJavascriptInterface("searchBoxJavaBridge_");
        webview.removeJavascriptInterface("accessibilityTraversal");
        webview.removeJavascriptInterface("accessibility");

//		webViewUrl = "file:///android_asset/test.html";
//		webViewUrl = "http://192.168.0.31:3002/main.html";
        if (TextUtils.isEmpty(webViewUrl)) {
            toast("数据异常，请退出重试");
            finish();
        } else {
            webview.loadUrl(webViewUrl, arrayMap);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBtnService:
                //RongCloudEvent.startRongCustomerServiceChat(this);
                CallCenterHelper.start(this, "", "", "");
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
//			stopLoading = true;
            webview.goBack();
            if (titleList.size() > 1) {
                titleList.remove(titleList.size() - 1);
                String title = titleList.get(titleList.size() - 1);
                setTitleString(title);
            }
        } else {
            finish();
        }
    }


    private void startRequestForCommitUrl(final String url) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("authUrl", url);
        LogUtil.i("lalo", "芝麻信用" + params.toString());

        // 芝麻信用跳转回的url，仅用于判断是否芝麻信用授权成功
        OkHttpClientManager.postAsyn(MyConstants.CREDIT_CALLBACK, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("lalo", "芝麻信用error：" + e.toString());
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e("lalo", "芝麻信用：" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        startRequestForCreditScore();
                    } else if (baseResult.code == ParserUtils.REQUEST_AUTH_ZM_UNBIND_ALIPAY) {
                        final TextDialog dialog = new TextDialog(WebViewActivity.this, false);
                        dialog.setContentText(baseResult.msg);

                        dialog.setRightText("确定");
                        dialog.setRightCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                finish();
                            }
                        });
                        dialog.show();

                    } else if (baseResult.code == ParserUtils.REQUEST_AUTH_ZM_NONE) {
                        finish();
                    }
//					else if(baseResult.code == ){
//
//					}
                    else {
                        // 加载页面
                        webview.loadUrl(url);
                        // CommonUtils.error(baseResult, WebViewActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void startRequestForCreditScore() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();

        LogUtil.i("lalo", "获取信用" + params.toString());

        OkHttpClientManager.postAsyn(MyConstants.CREDIT_UPDATE_CODE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("lalo", "获取信用积分error：" + e.toString());
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e("lalo", "获取信用积分：" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        CreditScoreData data = JSON.parseObject(baseResult.obj, CreditScoreData.class);
//						int zmCreditPoint=JSONObject.parseObject(baseResult.obj).getIntValue("zmCreditPoint");
//						CommonPreference.setUserInfo(data.userInfo);
                        CommonPreference.setUserZmCreditPoint(data.zmCreditPoint);
                        Intent intent = new Intent();
                        intent.putExtra(MyConstants.EXTRA_GET_CREDIT_SCORE, MyConstants.GET_CREDIT_SCORE_VALUE);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        CommonUtils.error(baseResult, WebViewActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FROM_WEBVIEW) {
                webview.loadUrl("javascript:" + js_func_name + "(" + "'" + data.getStringExtra("result") + "'" + ")");
            } else if (requestCode == FROM_LOGIN) {
                webview.loadUrl("javascript:" + js_func_name + "(" + "'" + data.getStringExtra("result") + "'" + "," + "'" + getClientInfo() + "'" + ")");
            }
        }
        webview.loadUrl("javascript:onResume(" + "'" + getClientInfo() + "'" + ")");
    }

    public String getClientInfo() {
        String platform = "android";
        String version = CommonUtils.getAppVersionName();
        String userId = String.valueOf(CommonPreference.getUserId());
        String accessToken = CommonPreference.getAccessToken();
        String activationToken = CommonPreference.getActivationToken();

        JSONObject obj = new JSONObject();
        try {
            obj.put("platform", platform);
            obj.put("version", version);
            obj.put("userId", userId);
            obj.put("accessToken", accessToken);
            obj.put("activationToken", activationToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    public class JavaScriptInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public void close(String json) {
            WebViewActivity.this.finish();
        }

        @JavascriptInterface
        public void refresh(String json) {
            try {
                WebViewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.reload();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @JavascriptInterface
        public void openPostComment(String json) {
            if (ConfigUtil.isToVerify()) {
                DialogManager.toVerify(WebViewActivity.this);
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(json);
                js_func_name = jsonObject.optString("callbackFuncName", "");
                Intent intent = new Intent(WebViewActivity.this, CommentCommitActivity.class);
                intent.putExtra(MyConstants.Comment.TARGET_ID, jsonObject.optLong("targetId", -1));
                intent.putExtra(MyConstants.Comment.TARGET_TYPE, jsonObject.optInt("targetType", -1));
                intent.putExtra("FROM_WEB", "FROM_WEB");
                startActivityForResult(intent, FROM_WEBVIEW);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void openPostCommentReply(String json) {
            if (ConfigUtil.isToVerify()) {
                DialogManager.toVerify(WebViewActivity.this);
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(json);
                js_func_name = jsonObject.optString("callbackFuncName", "");
                Intent intent = new Intent(WebViewActivity.this, ReplyCommitActivity.class);
                intent.putExtra(MyConstants.Comment.TARGET_ID, jsonObject.optLong("targetId", -1));
                intent.putExtra(MyConstants.Comment.TARGET_TYPE, jsonObject.optInt("targetType", -1));
                intent.putExtra(MyConstants.USER_NAME, jsonObject.optString("userNickName", ""));
                intent.putExtra("FROM_WEB", "FROM_WEB");
                startActivityForResult(intent, FROM_WEBVIEW);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //跳转拍卖发布页
        @JavascriptInterface
        public void openPostAuctionObject(String json) {
            // 验证登录
            if (!CommonPreference.isLogin()) {
                ActionUtil.loginAndToast(WebViewActivity.this);
                return;
            }
            // 验证实名认证
            if (ConfigUtil.isToVerify()) {
                DialogManager.toVerify(WebViewActivity.this);
                return;
            }

            Intent intent = new Intent(WebViewActivity.this, ReleaseActivity.class);
            intent.putExtra(MyConstants.EXTRA_ALBUM_FROM_MAIN, "1");
            intent.putExtra(MyConstants.IS_AUCTION, 1);
            intent.putExtra(MyConstants.DRAFT_TYPE, 4);
            startActivityForResult(intent, FROM_WEBVIEW2);
        }

        @JavascriptInterface
        public void openPostItem(String strOpts){
            try {
                JSONObject jsonObject = new JSONObject(strOpts);
                Intent intent = new Intent(WebViewActivity.this, ReleaseActivity.class);
                intent.putExtra(MyConstants.EXTRA_CAMPAIGN_ID,jsonObject.optInt("campaignId",0));
                intent.putExtra(MyConstants.EXTRA_CAT2_ID,jsonObject.optInt("cat2Id",0));
                intent.putExtra(MyConstants.EXTRA_CAT2_NAME,jsonObject.optString("cat2Name",""));
                startActivityForResult(intent, FROM_WEBVIEW2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void openMyAuctionObjects(String json) {
            if (!CommonPreference.isLogin()) {
                ActionUtil.loginAndToast(WebViewActivity.this);
                return;
            }

            Intent intent = new Intent(WebViewActivity.this, MyAuctionActivity.class);
            startActivityForResult(intent, FROM_WEBVIEW2);
        }


        @JavascriptInterface
        public void openCommentList(String json) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                Intent intent = new Intent(WebViewActivity.this, HPCommentListActivityNew.class);
                intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, jsonObject.optLong("targetId", -1));
                intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_TYPE, jsonObject.optInt("targetType", -1));
                startActivityForResult(intent, FROM_WEBVIEW2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //打开留言回复列表视图
        @JavascriptInterface
        public void openCommentReplyList(String json) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                Intent intent = new Intent(WebViewActivity.this, HPReplyListActivityNew.class);
                intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, jsonObject.optLong("commentId", -1));
                startActivityForResult(intent, FROM_WEBVIEW2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @JavascriptInterface
        public String getClientInfo() {
            return WebViewActivity.this.getClientInfo();
        }

        @JavascriptInterface
        public void openLogin(String json) {
            try {
                if (CommonPreference.getUserId() > 0) {
                    return;
                }
                JSONObject jsonObject = new JSONObject(json);
                js_func_name = jsonObject.optString("callbackFuncName", "");
                Intent intent = new Intent(WebViewActivity.this, LoginActivity.class);
                intent.putExtra("FROM_WEB", "FROM_WEB");
                startActivityForResult(intent, FROM_LOGIN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void openHome(String json) {
            Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
            startActivityForResult(intent, FROM_WEBVIEW2);
        }

        @JavascriptInterface
        public void openArticleSquare(String json) {
            Logger.e("get data:" + json);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(json);
                Intent intent = new Intent(WebViewActivity.this, ArticleSquareActivity.class);
                intent.putExtra("position", jsonObject.optInt("cat", 0));
                startActivityForResult(intent, FROM_WEBVIEW2);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @JavascriptInterface
        public void openSchizoList(String json) {
            LogUtil.d("danielluan", json);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(json);
                Intent intent = new Intent(WebViewActivity.this, SpecialThemeDetailActivity.class);
                intent.putExtra("Schizodata", json);
                startActivityForResult(intent, FROM_WEBVIEW2);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        @JavascriptInterface
        public void openMyPoems(String json) {
            LogUtil.d("danielluan", json);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(json);
                Long uid = jsonObject.optLong("userId");

//                if (!CommonPreference.isLogin()) {
//                    ActionUtil.loginAndToast(WebViewActivity.this);
//                    return;
//                }

                Intent intent = new Intent(WebViewActivity.this, FlowerNewActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, uid);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, FROM_WEBVIEW2);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        @JavascriptInterface
        public void saveImage(String json) {


            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(json);
                final String url = jsonObject.optString("imageUrl");
                final String jsfuncname = jsonObject.optString("callbackFuncName");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final TextDialog dialog = new TextDialog(WebViewActivity.this, true);
                        dialog.setContentText("是否要保存图片？");
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
                                dialog.dismiss();
                                if (TextUtils.isEmpty(url)) {
                                    return;
                                }
                                CommonUtils.downloadImage(WebViewActivity.this, url, new OkHttpClientManager.StringCallback() {
                                    @Override
                                    public void onError(Request request, Exception e) {

                                    }

                                    @Override
                                    public void onResponse(String response) {

                                        webview.loadUrl("javascript:" + jsfuncname + "(" + "'" + response + "'" + ")");

                                    }
                                });

                            }
                        });
                        dialog.show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void setShareInfo(String json) {
            LogUtil.e("url", "setShareInfo" + json);
            if (!TextUtils.isEmpty(json)) {
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(json);
                if (!isJson) {
                    return;
                }
                try {
                    share = JSON.parseObject(json, Share.class);
                    if (share != null) {
                        if (TextUtils.isEmpty(share.getTitle()) || TextUtils.isEmpty(share.getContent()) || TextUtils.isEmpty(share.getImage())) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    getTitleBar().getTitleTextRight().setBackgroundResource(0);
                                    getTitleBar().setOnRightButtonClickListener(0, null);
                                }
                            });

                        } else {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    getTitleBar().setOnRightButtonClickListener(R.drawable.btn_title_share_gray, new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            final String url;
                                            String shareUrl = share.getUrl();
                                            if (!TextUtils.isEmpty(shareUrl) && shareUrl.startsWith("http")) {
                                                url = shareUrl;
                                            } else {
                                                url = webview.getUrl();
                                            }
                                            String feature = "webview";
                                            String adds = "";
                                            ShareDialog shareDialog = new ShareDialog(WebViewActivity.this, share.getTitle(), share.getContent(), share.getImage(), url, feature, adds);
                                            shareDialog.show();
                                        }
                                    });
                                }
                            });
                        }

                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                getTitleBar().getTitleTextRight().setBackgroundResource(0);
                                getTitleBar().setOnRightButtonClickListener(0, null);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            getTitleBar().getTitleTextRight().setBackgroundResource(0);
                            getTitleBar().setOnRightButtonClickListener(0, null);
                        }
                    });
                }

            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        getTitleBar().getTitleTextRight().setBackgroundResource(0);
                        getTitleBar().setOnRightButtonClickListener(0, null);
                    }
                });
            }
        }

        @JavascriptInterface
        public String openWindow(String json) {
            JSONObject object;
            try {
                object = new JSONObject(json);
                String activityName = "";
                String params = "";
                String authRequired = "";

                if (object.has("target")) {
                    activityName = object.getString("target");
                } else {
                    return "20002";
                }

                if (object.has("params")) {
                    params = object.getString("params");
                } else {
                    return "20003";
                }

                if (object.has("authRequired")) {
                    authRequired = object.getString("authRequired");
                } else {
                    return "20004";
                }

                if (activityName.endsWith("ReleaseActivity") && ConfigUtil.isToVerify()) {
                    DialogManager.toVerify(WebViewActivity.this);
                    return "20002";
                }

                Class<?> activity = Class.forName(activityName);

                if (activity == null)
                    return "20002";

                Intent intent = new Intent(mContext, activity);
                if (!TextUtils.isEmpty(params)) {
                    JSONObject obj = new JSONObject(params);
                    Iterator iterator = obj.keys();
//						if(!iterator.hasNext()) {
//							return "20003:params's key is null";
//						}
                    if (iterator != null) {
                        while (iterator.hasNext()) {
                            String key = iterator.next().toString();
                            Object value = obj.get(key);
                            if (value instanceof String) {
                                intent.putExtra(key, String.valueOf(value));
                            } else if (value instanceof Long) {
                                intent.putExtra(key, Long.valueOf(value.toString()));
                            } else if (value instanceof Integer) {
                                if (key.equals("extra_user_id") || key.equals("extra_order_detail_id")) {
                                    intent.putExtra(key, Long.valueOf(value.toString()));
                                } else if (key.equals("extra_goods_detail_id")) {
                                    intent.putExtra(key, String.valueOf(value));
                                } else {
                                    intent.putExtra(key, Integer.valueOf(value.toString()));
                                }
                            } else if (value instanceof Boolean) {
                                intent.putExtra(key, Boolean.valueOf(value.toString()));
                            }

                            if (key.equals("extra_campaign_id")) {
                                int var = obj.getInt("extra_campaign_id");
                                if (var > 0) {
                                    ArrayList<Campaign> campaigns = CommonPreference.getCampaigns();
                                    if (!ArrayUtil.isEmpty(campaigns)) {
                                        for (Campaign c : campaigns) {
                                            if (c != null && var == c.getCid()) {
                                                intent.putExtra(MyConstants.EXTRA_CAMPAIGN_BEAN, c);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!TextUtils.isEmpty(authRequired)) {
                    if (authRequired.equals("1")) {// 需要验证登录状态
                        boolean isLogin = CommonPreference.isLogin();
                        if (isLogin) {
                            startActivityForResult(intent, FROM_WEBVIEW2);
                        } else {
                            ActionUtil.loginAndToast(WebViewActivity.this);
                        }
                    } else {
                        startActivityForResult(intent, FROM_WEBVIEW2);
                    }
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return "20001";
            } catch (JSONException e) {
                e.printStackTrace();
                return e.toString();
            }
            return "200";
        }
    }
}
