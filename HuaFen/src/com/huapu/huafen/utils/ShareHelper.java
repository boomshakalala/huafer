package com.huapu.huafen.utils;

import android.content.Context;
import android.text.TextUtils;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.ArticleDetailResult;
import com.huapu.huafen.beans.FlowerData;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.GoodsInfoBean;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ShareDialog;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by admin on 2017/6/7.
 */

public class ShareHelper implements PlatformActionListener {

    private String title;

    private String content;

    private String imageUrl;

    private String webPageUrl;

    private Context context;

    private boolean[] shareState;

    public ShareHelper(Context context, String title, String content, String imageUrl, String webPageUrl, boolean[] shareState) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.webPageUrl = webPageUrl;
        this.context = context.getApplicationContext();
        this.shareState = shareState;
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        ToastUtil.toast(context, "分享成功");
        shareResult();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        ToastUtil.toast(context, "分享失败");
        shareResult();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        ToastUtil.toast(context, "分享取消");
        shareResult();
    }

    private void shareResult() {
        runShare();
    }

    public void runShare() {
        if (shareState[0]) {
            shareState[0] = false;
            ShareUtils.shareMomentFour(context, WechatMoments.NAME, this.title, this.content, this.imageUrl, this.webPageUrl, this);
            LogUtil.e("ShareHelper", "shareWechatMoments");
            return;
        }

        if (shareState[1]) {
            shareState[1] = false;
            ShareUtils.shareMomentFour(context, Wechat.NAME, this.title, this.content, this.imageUrl, this.webPageUrl, this);
            LogUtil.e("ShareHelper", "shareWechat");
            return;
        }

        if (shareState[2]) {
            shareState[2] = false;
            ShareUtils.shareMomentFour(context, SinaWeibo.NAME, this.title, this.content, this.imageUrl, this.webPageUrl, this);
            LogUtil.e("ShareHelper", "shareWeibo");
            return;
        }

        if (shareState[3]) {
            shareState[3] = false;
            ShareUtils.shareMomentFour(context, QQ.NAME, this.title, this.content, this.imageUrl, this.webPageUrl, this);
            LogUtil.e("ShareHelper", "shareQQ");
        }

    }


    public static void shareGoods(Context mContext, GoodsData goodsData) {
        if (goodsData == null) {
            return;
        }
        String url = MyConstants.SHARE_GOODS_DETAIL + goodsData.getGoodsId();
        String brand = goodsData.getBrand();
        String name = goodsData.getName();
        String goodsDes;
        if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(name)) {
            String goodsFormat = mContext.getResources().getString(R.string.goods_name_desc);
            goodsDes = String.format(goodsFormat, brand, name);
        } else {
            goodsDes = TextUtils.isEmpty(brand) ? (TextUtils.isEmpty(name) ? "" : name) : brand;
        }

        String shareText;
        if (!TextUtils.isEmpty(goodsData.getContent())) {
            shareText = goodsData.getContent();
        } else {
            shareText = CommonPreference.getUserInfo().getUserName() + "在花粉儿分享了最新闲置商品，不快点就没有喽～ ";
        }

        ShareDialog shareDialog = new ShareDialog(mContext, goodsDes, shareText, goodsData.getGoodsImgs().get(0), url);
        shareDialog.show();
    }

    public static void shareGoods(Context mContext, GoodsInfoBean goodsInfoBean, String recTranceId, String searchQuery, int recIndex) {
        if (goodsInfoBean == null) {
            return;
        }
        GoodsInfo goodsInfo = goodsInfoBean.getGoodsInfo();
        UserInfo userInfo = goodsInfoBean.getUserInfo();
        if (goodsInfo == null) {
            return;
        }
        String brand = goodsInfo.getGoodsBrand();
        String name = goodsInfo.getGoodsName();
        String goodsDes;
        String title = "";
        if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(name)) {
            String goodsFormat = mContext.getResources().getString(R.string.share_name_desc);
            goodsDes = String.format(goodsFormat, brand, name);
        } else {
            goodsDes = TextUtils.isEmpty(brand) ? (TextUtils.isEmpty(name) ? "" : name) : brand;
        }
        if (CommonPreference.getUserInfo().getUserId() == userInfo.getUserId()) {
            title = "我在花粉儿发布了一件宝贝，" + name + "";
        } else {
            title = "花粉儿超值好物推荐给你，" + name + "";
        }

        String url = MyConstants.SHARE_GOODS_DETAIL + goodsInfo.getGoodsId();
        String shareText;

        if (CommonPreference.getUserInfo().getUserId() == userInfo.getUserId()) {
            shareText = "我在花粉儿发布了一件不错的宝贝，" + goodsDes + "." + ShareUtils.DOWN_LOAD + url;
        } else {
            shareText = "花粉儿超值好物推荐给你，" + goodsDes + "." + ShareUtils.DOWN_LOAD + url;
        }

        String adds = "";
        if (!TextUtils.isEmpty(goodsInfo.getGoodsContent())) {
            adds = goodsInfo.getGoodsContent();
        } else {
            adds = goodsDes;
        }

        String feature = "goods";
        ShareDialog shareDialog = new ShareDialog(mContext, title, shareText, goodsInfo.getGoodsImgs().get(0), url, feature, adds);
        shareDialog.setRecIndex(recIndex);
        shareDialog.setRecTraceId(recTranceId);
        shareDialog.setSearchQuery(searchQuery);
        shareDialog.setGoodsId(goodsInfo.getGoodsId());
        shareDialog.show();
    }

    public static void shareArticle(Context mContext, ArticleDetailResult result) {
        if (result == null || result.obj == null || result.obj.user == null || result.obj.article == null || result.obj.article.titleMedia == null || TextUtils.isEmpty(result.obj.article.titleMedia.url)) {
            return;
        }
        String url = MyConstants.SHARE_ARTICLE + result.obj.article.articleId;
        String shareContent;
        String adds;
        String title;
        if (CommonPreference.getUserInfo().getUserId() == result.obj.user.getUserId()) {
            title = CommonPreference.getUserInfo().getUserName() + "在花粉儿记录美好生活，快来看看吧！";
            shareContent = CommonPreference.getUserInfo().getUserName() + "在花粉儿记录美好生活，快来看看吧！" + ShareUtils.DOWN_LOAD + url;
            adds = "生活照，好物推荐，旅行攻略...你想看的都在这里";
        } else {
            title = result.obj.article.title;
            if (result.obj.article.summary != null) {
                shareContent = title + ":" + (result.obj.article.summary.length() > 22 ? result.obj.article.summary.substring(0, 22) + "..." : result.obj.article.summary) + ShareUtils.DOWN_LOAD + url;
                adds = result.obj.article.summary;
            } else {
                shareContent = title;
                adds = "";
            }
        }
        String feature = "article";

        ShareDialog shareDialog = new ShareDialog(mContext, title,
                shareContent, result.obj.article.titleMedia.url, url, feature, adds);
        shareDialog.show();
    }

    public static void shareFlower(Context mContext, FlowerData userInfo) {
        if (userInfo == null || userInfo.getUser() == null) {
            return;
        }
        String url = MyConstants.SHARE_ARTICLE_FLOWER + userInfo.getUser().getUserId();
        String title = userInfo.getUser().getUserName() + "在花粉儿记录美好生活，快来看看吧！";
        String shareContent = userInfo.getUser().getUserName() + "在花粉儿记录美好生活，快来看看吧！" + ShareUtils.DOWN_LOAD + url;
        String adds = "生活照，好物推荐，旅行攻略...你想看的都在这里";
        String feature = "article";
        ShareDialog shareDialog = new ShareDialog(mContext, title, shareContent, userInfo.getUser().getAvatarUrl(), url, feature, adds);
        shareDialog.show();
    }

    public static void sharePersonal(Context mContext, UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        String url = MyConstants.SHARE_PERSONAL_HOME + userInfo.getUserId();

        String title;
        String shareContent;
        String adds;

        if (CommonPreference.getUserInfo().getUserId() == userInfo.getUserId()) {
            title = CommonPreference.getUserInfo().getUserName() + "的花粉儿店铺：我在这里买卖闲置！";
            shareContent = "在花粉儿买卖闲置好开心！快来看看我的店铺吧！#" + CommonPreference.getUserInfo().getUserName() + "的花粉儿店铺#" + ShareUtils.DOWN_LOAD + url;
            adds = "孙俪，刘涛都在这里， 快来加入我们吧";
        } else {

            title = "推荐" + userInfo.getUserName() + "的花粉儿店铺给你，快来逛逛吧！";
            shareContent = "我在花粉儿发现了这个闲置达人，快来逛逛她的店铺#" + userInfo.getUserName() + "的花粉儿店铺#" + ShareUtils.DOWN_LOAD + url;
            adds = "我在花粉儿发现了这个闲置达人";
        }

//
//        if (!TextUtils.isEmpty(userInfo.getNotice())) {
//            shareContent = userInfo.getNotice() + ShareUtils.DOWN_LOAD;
//        } else {
//            shareContent = userInfo.getUserName() + "在花粉儿开店啦，快来看看TA的店铺里都有什么宝贝吧！" + ShareUtils.DOWN_LOAD;
//        }


        String feature = "personal";
        ShareDialog shareDialog = new ShareDialog(mContext, title, shareContent, userInfo.getUserIcon(), url, feature, adds);
        shareDialog.show();
    }

}
