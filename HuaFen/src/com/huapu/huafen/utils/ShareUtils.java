package com.huapu.huafen.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.http.OkHttpClientManager;
import com.mob.tools.utils.UIHandler;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * @author liang_xs 分享工具类
 */
public class ShareUtils {

    public static final String DOWN_LOAD = "（下载花粉儿APP查看更多精彩内容：https://i.huafer.cc/get）";

    public static void showShare(final Context context, String type, String title,
                                 String text, String imageUrl, String url) {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(text);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        // 启动分享GUI
        oks.show(context);
    }

    public static void shareImage(String platformToShare, final Context context, String path) {
        OnekeyShare oks = new OnekeyShare();

        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(context, "请先生成海报并保存", Toast.LENGTH_SHORT).show();
            return;
        }
        oks.setSilent(false);
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        // 令编辑页面显示为Dialog模式
//        oks.setDialogMode();
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();

        try {
            oks.setImagePath(path);
            oks.setCallback(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    UIHandler.sendEmptyMessage(0, new Handler.Callback() {
                        public boolean handleMessage(Message msg) {
                            ToastUtil.toast(context, "分享成功");
                            return false;
                        }
                    });

                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    UIHandler.sendEmptyMessage(0, new Handler.Callback() {
                        public boolean handleMessage(Message msg) {
                            ToastUtil.toast(context, "分享失败");
                            return false;
                        }
                    });

                }

                @Override
                public void onCancel(Platform platform, int i) {
                    UIHandler.sendEmptyMessage(0, new Handler.Callback() {
                        public boolean handleMessage(Message msg) {
                            ToastUtil.toast(context, "分享已取消");
                            return false;
                        }
                    });

                }
            });
            oks.show(context);
        } catch (NullPointerException e) {
            Logger.e("NullPointerException found:" + e.getMessage());
        }
    }

    /**
     * 分享
     */
    public static void share(final Context context, String type, String title,
                             String text, String imageUrl, String url) {

        // 初始化ShareSDK
        ShareSDK.initSDK(context);
        Platform platform = ShareSDK.getPlatform(context, type);
        Wechat.ShareParams sp = new Wechat.ShareParams();
        // sp.shareType = Platform.SHARE_TEXT;
        sp.shareType = Platform.SHARE_WEBPAGE;// 分享网页
        sp.imageUrl = imageUrl;
        if (type.equals(SinaWeibo.NAME)) {
            sp.setText(title + " " + text + url);
        } else {
            sp.url = url;
            if (type.equals(WechatMoments.NAME) || type.equals(Wechat.NAME)) {
                if (text.contains(DOWN_LOAD)) {
                    sp.setText(text.replace(DOWN_LOAD, ""));
                } else {
                    sp.setText(text);
                }

            } else {
                sp.setText(text);
            }
        }
        sp.setSite(context.getString(R.string.app_name));
        sp.setSiteUrl(url);
        sp.setTitle(title);
        sp.setTitleUrl(url);

        platform.setPlatformActionListener(new ShareCallBack(context));
        // 执行图文分享
        platform.share(sp);
    }

    public static void share(final Context context, String type, String title,
                             String text, String imageUrl, String url, String recTranceId, String searchQuery, int recIndex, int shareType, long goodsId) {

        // 初始化ShareSDK
        ShareSDK.initSDK(context);
        Platform platform = ShareSDK.getPlatform(context, type);
        Wechat.ShareParams sp = new Wechat.ShareParams();
        // sp.shareType = Platform.SHARE_TEXT;
        sp.shareType = Platform.SHARE_WEBPAGE;// 分享网页
        sp.imageUrl = imageUrl;
        if (type.equals(SinaWeibo.NAME)) {
            sp.setText(title + " " + text + url);
        } else {
            sp.url = url;
            if (type.equals(WechatMoments.NAME) || type.equals(Wechat.NAME)) {
                if (text.contains(DOWN_LOAD)) {
                    sp.setText(text.replace(DOWN_LOAD, ""));
                } else {
                    sp.setText(text);
                }

            } else {
                sp.setText(text);
            }

        }
        sp.setSite(context.getString(R.string.app_name));
        sp.setSiteUrl(url);
        sp.setTitle(title);
        sp.setTitleUrl(url);

        platform.setPlatformActionListener(new ShareCallBack(context, goodsId, recTranceId, recIndex, searchQuery, shareType));
        // 执行图文分享
        platform.share(sp);
    }


    public static void shareMomentFour(final Context context, String type, String title,
                                       String text, String imageUrl, String url, PlatformActionListener platformActionListener) {
        // 初始化ShareSDK
        ShareSDK.initSDK(context);
        Platform platform = ShareSDK.getPlatform(context, type);
        Wechat.ShareParams sp = new Wechat.ShareParams();
        // sp.shareType = Platform.SHARE_TEXT;
        sp.shareType = Platform.SHARE_WEBPAGE;// 分享网页
        sp.imageUrl = imageUrl;
        if (type.equals(SinaWeibo.NAME)) {
            sp.setText(title + " " + text + url);
        } else {
            sp.url = url;
            sp.setText(text);
        }
        sp.setSite(context.getString(R.string.app_name));
        sp.setSiteUrl(url);
        sp.setTitle(title);
        sp.setTitleUrl(url);

        platform.setPlatformActionListener(platformActionListener);
        // 执行图文分享
        platform.share(sp);
    }

    /**
     * 分享到 微信好友
     */
    public static void shareToWeChat(final Context context, String title,
                                     String text, String imageUrl, String url) {

        // 初始化ShareSDK
        ShareSDK.initSDK(context);
        Platform wechat = ShareSDK.getPlatform(context, Wechat.NAME);
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setTitle(title);
        sp.setText(text);
        sp.shareType = Platform.SHARE_TEXT;
        sp.shareType = Platform.SHARE_WEBPAGE;// 分享网页
        sp.imageUrl = imageUrl;
        sp.url = url;

        wechat.setPlatformActionListener(new ShareCallBack(context));
        // 执行图文分享
        wechat.share(sp);
    }

    /**
     * 分享到 微信朋友圈
     */
    public static void shareTowechatMoments(final Context context,
                                            String title, String text, String imageUrl, String url) {
        // 初始化ShareSDK
        ShareSDK.initSDK(context);
        Platform wechatMoments = ShareSDK.getPlatform(context,
                WechatMoments.NAME);
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setTitle(title);
        sp.setText(text);
        sp.shareType = Platform.SHARE_TEXT;
        sp.shareType = Platform.SHARE_WEBPAGE;// 分享网页
        sp.imageUrl = imageUrl;
        sp.url = url;

        wechatMoments.setPlatformActionListener(new ShareCallBack(context));
        // 执行图文分享
        wechatMoments.share(sp);
    }

    /**
     * 分享到新浪微博
     */
    public static void shareToSinaWeibo(final Context context, String title,
                                        String text, String imageUrl, String url) {
        // 初始化ShareSDK
        ShareSDK.initSDK(context);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setTitle(title);
        sp.setText(url + "\\" + text);
        sp.imageUrl = imageUrl;

        weibo.setPlatformActionListener(new ShareCallBack(context));
        // 执行图文分享
        weibo.share(sp);
    }

    /**
     * 动态 分享到qq空间
     */
    public static void shareToQZone(final Context context, String title,
                                    String titleUrl, String text, String site, String siteUrl,
                                    String imageUrl) {
        // 初始化ShareSDK
        ShareSDK.initSDK(context);
        Platform qZoneMoments = ShareSDK.getPlatform(context, QZone.NAME);
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.setTitle(title);
        sp.setText(text);
        sp.setTitleUrl(titleUrl);
        sp.setSite(site);
        sp.setSiteUrl(siteUrl);
        sp.imageUrl = imageUrl;

        qZoneMoments.setPlatformActionListener(new ShareCallBack(context));
        // 执行图文分享
        qZoneMoments.share(sp);
    }

    /**
     * 动态 分享到qq
     */
    public static void shareToQq(final Context context, String title,
                                 String titleUrl, String text, String imageUrl) {
        // 初始化ShareSDK
        ShareSDK.initSDK(context);
        Platform qqMoments = ShareSDK.getPlatform(context, QQ.NAME);
        QQ.ShareParams sp = new QQ.ShareParams();
        sp.setTitle(title);
        sp.setTitleUrl(titleUrl);
        sp.setText(text);
        sp.imageUrl = imageUrl;

        qqMoments.setPlatformActionListener(new ShareCallBack(context));
        // 执行图文分享
        qqMoments.share(sp);
    }

    /**
     * @author liang_xs 动态 分享回调监听
     */
    private static class ShareCallBack implements PlatformActionListener {

        Context context;
        private String recTraceId;
        private int recIndex;
        private String searchQuery;
        private int shareType;
        private long goodsId;

        public ShareCallBack(Context context) {
            this.context = context;
        }

        public ShareCallBack(Context context, long goodsId, String recTraceId, int recIndex, String searchQuery, int shareType) {
            this.context = context;
            this.goodsId = goodsId;
            this.recTraceId = recTraceId;
            this.recIndex = recIndex;
            this.searchQuery = searchQuery;
            this.shareType = shareType;
        }

        @Override
        public void onCancel(Platform arg0, int arg1) {
            ToastUtil.toast(context, "分享取消");
        }

        @Override
        public void onComplete(Platform arg0, int arg1,
                               HashMap<String, Object> arg2) {
            ToastUtil.toast(context, "分享成功");
            if (!TextUtils.isEmpty(recTraceId) && goodsId != 0 && shareType > 0) {
                doRequest();
            }

        }

        private void doRequest() {
            HashMap<String, String> params = new HashMap<>();
            params.put("goodsId", String.valueOf(goodsId));
            params.put("type", String.valueOf(shareType));
            params.put("recTraceId", recTraceId);
            params.put("recIndex", String.valueOf(recIndex));
            if (!TextUtils.isEmpty(searchQuery)) {
                params.put("searchQuery", searchQuery);
            }

            LogUtil.e("share", params.toString());

            OkHttpClientManager.postAsyn(MyConstants.SHARE, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {

                }

                @Override
                public void onResponse(String response) {
                    LogUtil.e("share", response);
                }
            });

        }


        @Override
        public void onError(Platform arg0, int arg1, Throwable arg2) {
            // TODO Auto-generated method stub
            arg2.printStackTrace();
            String message = arg2.getMessage();
            if (message != null && !message.equals("")) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(message);
                    String error_code = jsonObject.getString("error_code");
                    if (error_code != null && error_code.equals("20019")) {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            // 在这里添加分享失败的处理代码
            ToastUtil.toast(context, "分享失败" + message);
        }

    }

    // /**
    // * 分享，添加友盟点击事件统计
    // */
    // public static void AddUMonEvent(String SHARE_TYPE, String SHARE_CHANNEL
    // , String SHARE_ID, String SHARE_NAME) {
    // HashMap<String, String> param = new HashMap<String, String>();
    // Utils.getStatistical("SHARE_NUM", param);
    // HashMap<String, String> params = new HashMap<String, String>();
    // params.put("SHARE_TYPE", SHARE_TYPE);
    // params.put("SHARE_CHANNEL", SHARE_CHANNEL);
    // params.put("SHARE_ID", SHARE_ID);
    // params.put("SHARE_NAME", SHARE_NAME);
    // Utils.getStatistical("SHARE_DATA", params);
    // }

}
