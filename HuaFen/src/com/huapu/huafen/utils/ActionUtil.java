package com.huapu.huafen.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.activity.ArticleDetailActivity;
import com.huapu.huafen.activity.ArticleSquareActivity;
import com.huapu.huafen.activity.ClassificationDetailActivity;
import com.huapu.huafen.activity.CreditPanelActivity;
import com.huapu.huafen.activity.FansListActivity;
import com.huapu.huafen.activity.FlowerNewActivity;
import com.huapu.huafen.activity.FullyNewRegionActivity;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.LoginActivity;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.activity.MyAuctionActivity;
import com.huapu.huafen.activity.MyFollowedActivity;
import com.huapu.huafen.activity.NewStarRegionActivity;
import com.huapu.huafen.activity.OneYuanRegionActivity;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.ReleaseActivity;
import com.huapu.huafen.activity.ReleaseListActivity;
import com.huapu.huafen.activity.SearchGoodsListActivity;
import com.huapu.huafen.activity.SpecialThemeDetailActivity;
import com.huapu.huafen.activity.StarRegionActivity;
import com.huapu.huafen.activity.TagActivity;
import com.huapu.huafen.activity.TipActivity;
import com.huapu.huafen.activity.VIPUserListActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.ClassificationResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.chatim.activity.DefaultConversationActivity;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.ExtraConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * Action跳转工具类
 * Created by dengbin on 17/12/19.
 */
public class ActionUtil {

    public static void dispatchAction(Context mContext, String action, String target) {
        dispatchAction(mContext, action, target, 0);
    }

    /**
     * @param mContext
     * @param action
     * @param target
     * @param type     =1000为显示分类用 ClassificationDetailActivity
     */
    public static void dispatchAction(Context mContext, String action, String target, int type) {
        if (TextUtils.isEmpty(action) || mContext == null) {
            return;
        }

        LogUtil.d("danielluan", "action:" + action + "#target:" + target);

        if (TextUtils.isEmpty(action))
            return;

        action = action.trim();
        Intent intent = null;
        switch (action) {
            case ActionConstants.OPEN_WEB_VIEW:
                if (TextUtils.isEmpty(target)) {
                    return;
                }

                intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_GOODS_DETAIL:
                if (TextUtils.isEmpty(target)) {
                    return;
                }
                intent = new Intent(mContext, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_USER_SHOP_DETAIL:
                if (TextUtils.isEmpty(target)) {
                    return;
                }
                intent = new Intent(mContext, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, Long.valueOf(target));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_SYSTEM_NOTIFICATIONS:
                intent = new Intent(mContext, DefaultConversationActivity.class);
                intent.putExtra(MyConstants.IM_CONV_TYPE_KEY, MyConstants.IM_CONV_TYPE_CAMPAIGN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_STAR_USER_LIST: //明星用户列表
                intent = new Intent(mContext, NewStarRegionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_STAR_GOODS_LIST: //明星商品（专区）列表
                intent = new Intent(mContext, StarRegionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_VIP_USER: //VIP用户列表
                intent = new Intent(mContext, VIPUserListActivity.class);
                intent.putExtra(MyConstants.EXTRA_TARGET_USER_LEVEL, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_VIP_REGION:  // VIP商品（VIP专区）列表
                intent = new Intent(mContext, FullyNewRegionActivity.class);
                intent.putExtra("type", 110);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_NEW_REGION:  // 全新商品专区列表
                intent = new Intent(mContext, FullyNewRegionActivity.class);
                intent.putExtra("type", 99);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_SEARCH_RESULT_IN_SAME_CITY:   //同城专区
                intent = new Intent(mContext, SearchGoodsListActivity.class);
                intent.putExtra("sameCity", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_ARTICLE_SQUARE:  //花语广场
                intent = new Intent(mContext, ArticleSquareActivity.class);
                intent.putExtra("position", target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_POPUP_IMAGE_WINDOW:
                intent = new Intent(mContext, TipActivity.class);
                intent.putExtra(MyConstants.EXTRA_TARGET_TIP, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0); // 去掉baseactivity中启动动画

                if (type == 1) {
                    CommonUtils.updateSystemUnreadCount();
                }
                break;
            case ActionConstants.OPEN_CATS_NEW_ZONE:
                break;
            case ActionConstants.OPEN_CATS_DISCOUNT_ZONE:
                break;
            case ActionConstants.OPEN_CATS_VIP_ZONE: //分类全新专区
                intent = new Intent(mContext, ClassificationDetailActivity.class);
                ClassificationResult.Opt opt = new ClassificationResult.Opt();
                opt.type = "image_grid";
                opt.target = target;
                opt.action = action;
                if (type == 1000) {
                    intent.putExtra("SHOWCLASS", true);
                }
                intent.putExtra(MyConstants.EXTRA_OPTION, opt);
                intent.putExtra("key", target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_ONE_REGION:
                intent = new Intent(mContext, OneYuanRegionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_ORDER_INFO:
                try {
                    intent = new Intent(mContext, OrderDetailActivity.class);
                    long orderId = Long.parseLong(target);
                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case ActionConstants.OPEN_MOMENT:
                intent = new Intent(mContext, MomentDetailActivity.class);
                intent.putExtra(MomentDetailActivity.MOMENT_ID, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_ARTICLE:
                intent = new Intent(mContext, ArticleDetailActivity.class);
                intent.putExtra(MyConstants.ARTICLE_ID, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_CHAT_VIEW: //打开会话

                break;
            case ActionConstants.OPEN_COMMENT_MSG: //打开回复留言界面

                break;
            case ActionConstants.OPEN_ITEM_COMMENT: //打开商品留言

                break;
            case ActionConstants.UPDATE_USER_PROPERTIES: //更新用户属性

                break;
            case ActionConstants.OPEN_SESAME_CREDIT: //打开芝麻信用
                if (CommonPreference.isLogin()) {
                    boolean flag = CommonPreference.getUserInfo().getZmCreditPoint() > 0;
                    if (flag) {
                        intent = new Intent();
                        intent.setClass(mContext, CreditPanelActivity.class);
                        intent.putExtra("zmCreditPoint", CommonPreference.getUserInfo().getZmCreditPoint());
                        intent.putExtra("isNotMine", CommonPreference.getUserInfo().getUserId() == CommonPreference.getUserId() ? false : true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ((Activity) mContext).startActivityForResult(intent, 0x1311);
                    } else {
                        startRequestForCreditZM(mContext);
                    }
                } else {
                    loginAndToast(mContext);
                }

                break;
            case ActionConstants.OPEN_POST_AUCTION_OBJECT: //打开拍卖发布
                intent = new Intent(mContext, ReleaseActivity.class);
                intent.putExtra(MyConstants.IS_AUCTION, 1);
                intent.putExtra(MyConstants.DRAFT_TYPE, 4);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_MY_AUCTION_OBJECTS: //我的拍卖
                intent = new Intent(mContext, MyAuctionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_MY_SUBTLE_CLASSIFICATION: //特辑
                intent = new Intent(mContext, SpecialThemeDetailActivity.class);
                intent.putExtra("Schizodata", target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_USER_FANS:    // 进入粉丝列表
                intent = new Intent(mContext, FansListActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_FOCUS_USER_LIST:  // 关注-用户列表
                intent = new Intent(mContext, MyFollowedActivity.class);
                intent.putExtra(ExtraConstants.EXTRA_FOLLOW_INDEX, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_FOCUS_GOODS_LIST: // 关注-商品列表
                intent = new Intent(mContext, MyFollowedActivity.class);
                intent.putExtra(ExtraConstants.EXTRA_FOLLOW_INDEX, 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_FOCUS_ARTICLES_LIST:  // 关注-花语列表
                intent = new Intent(mContext, MyFollowedActivity.class);
                intent.putExtra(ExtraConstants.EXTRA_FOLLOW_INDEX, 2);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_MYSELLING_ITEMS: // 打开在售商品页
                intent = new Intent(mContext, ReleaseListActivity.class);
                intent.putExtra("index", 2);
                intent.setAction(action);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_MYUNSHELVED_ITEMS: //打开下架商品页
                intent = new Intent(mContext, ReleaseListActivity.class);
                intent.putExtra("index", 1);
                intent.setAction(action);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                break;
            case ActionConstants.OPEN_MY_POEMS://打开我的花语
                intent = new Intent(mContext, FlowerNewActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, Long.valueOf(target));
                mContext.startActivity(intent);
                break;
        }
    }

    public static Intent dispatchPushAction(Context mContext, String action, String target) {
        if (TextUtils.isEmpty(action)) {
            return null;
        }
        action = action.trim();
        Intent intent = null;
        if (action.equals(ActionConstants.OPEN_WEB_VIEW)) {
            if (TextUtils.isEmpty(target)) {
                return null;
            }
            intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, target);
        } else if (action.equals(ActionConstants.OPEN_GOODS_DETAIL)) {
            if (TextUtils.isEmpty(target)) {
                return null;
            }
            intent = new Intent(mContext, GoodsDetailsActivity.class);
            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, target);
        } else if (action.equals(ActionConstants.OPEN_USER_SHOP_DETAIL)) {
            if (TextUtils.isEmpty(target)) {
                return null;
            }
            intent = new Intent(mContext, PersonalPagerHomeActivity.class);
            intent.putExtra(MyConstants.EXTRA_USER_ID, Long.valueOf(target));
        } else if (action.equals(ActionConstants.OPEN_SYSTEM_NOTIFICATIONS)) {
            intent = new Intent(mContext, DefaultConversationActivity.class);
            intent.putExtra(MyConstants.IM_CONV_TYPE_KEY, MyConstants.IM_CONV_TYPE_CAMPAIGN);
        } else if (action.equals(ActionConstants.OPEN_STAR_USER_LIST)) {//明星用户列表
            intent = new Intent(mContext, NewStarRegionActivity.class);
        } else if (action.equals(ActionConstants.OPEN_STAR_GOODS_LIST)) {//明星商品（专区）列表
            intent = new Intent(mContext, StarRegionActivity.class);
        } else if (action.equals(ActionConstants.OPEN_VIP_USER)) {//VIP用户列表
            intent = new Intent(mContext, VIPUserListActivity.class);
            intent.putExtra(MyConstants.EXTRA_TARGET_USER_LEVEL, target);
        } else if (action.equals(ActionConstants.OPEN_VIP_REGION)) { // VIP商品（VIP专区）列表
            intent = new Intent(mContext, FullyNewRegionActivity.class);
            intent.putExtra("type", 110);
        } else if (action.equals(ActionConstants.OPEN_NEW_REGION)) { // 全新商品专区列表
            intent = new Intent(mContext, FullyNewRegionActivity.class);
            intent.putExtra("type", 99);
        } else if (action.equals(ActionConstants.OPEN_SEARCH_RESULT_IN_SAME_CITY)) {  //同城专区
            intent = new Intent(mContext, SearchGoodsListActivity.class);
            intent.putExtra("sameCity", true);
        } else if (action.equals(ActionConstants.OPEN_ARTICLE_SQUARE)) { //花语广场
            intent = new Intent(mContext, ArticleSquareActivity.class);
            intent.putExtra("position", target);
        } else if (action.equals(ActionConstants.OPEN_POPUP_IMAGE_WINDOW)) {
            intent = new Intent(mContext, TipActivity.class);
            intent.putExtra(MyConstants.EXTRA_TARGET_TIP, target);
        } else if (action.equals(ActionConstants.OPEN_CATS_NEW_ZONE) || action.equals(ActionConstants.OPEN_CATS_DISCOUNT_ZONE) || action.equals(ActionConstants.OPEN_CATS_VIP_ZONE)) {//分类全新专区
            intent = new Intent(mContext, ClassificationDetailActivity.class);
            ClassificationResult.Opt opt = new ClassificationResult.Opt();
            opt.type = "image_grid";
            opt.target = target;
            opt.action = action;
            intent.putExtra(MyConstants.EXTRA_OPTION, opt);
            intent.putExtra("key", target);
        } else if (action.equals(ActionConstants.OPEN_ONE_REGION)) {
            intent = new Intent(mContext, OneYuanRegionActivity.class);
        } else if (action.equals(ActionConstants.OPEN_ORDER_INFO)) {
            try {
                intent = new Intent(mContext, OrderDetailActivity.class);
                long orderId = Long.parseLong(target);
                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (action.equals(ActionConstants.OPEN_MOMENT)) {
            intent = new Intent(mContext, MomentDetailActivity.class);
            intent.putExtra(MomentDetailActivity.MOMENT_ID, target);
        } else if (action.equals(ActionConstants.OPEN_ARTICLE)) {
            intent = new Intent(mContext, ArticleDetailActivity.class);
            intent.putExtra(MyConstants.ARTICLE_ID, target);
        } else if (action.equals(ActionConstants.OPEN_CHAT_VIEW)) {//打开会话

        } else if (action.equals(ActionConstants.OPEN_COMMENT_MSG)) {//打开回复留言界面

        } else if (action.equals(ActionConstants.OPEN_ITEM_COMMENT)) {//打开商品留言

        } else if (action.equals(ActionConstants.UPDATE_USER_PROPERTIES)) {//更新用户属性

        } else if (action.equals(ActionConstants.OPEN_SESAME_CREDIT)) {//打开芝麻信用
            if (CommonPreference.isLogin()) {
                boolean flag = CommonPreference.getUserInfo().getZmCreditPoint() > 0;
                if (flag) {
                    intent = new Intent();
                    intent.setClass(mContext, CreditPanelActivity.class);
                    intent.putExtra("zmCreditPoint", CommonPreference.getUserInfo().getZmCreditPoint());
                    intent.putExtra("isNotMine", CommonPreference.getUserInfo().getUserId() == CommonPreference.getUserId() ? false : true);
                } else {
                    startRequestForCreditZM(mContext);
                }
            } else {
                loginAndToast(mContext);
            }

        } else if (action.equals(ActionConstants.OPEN_POST_AUCTION_OBJECT)) {//打开拍卖发布
            intent = new Intent(mContext, ReleaseActivity.class);
            intent.putExtra(MyConstants.IS_AUCTION, 1);
            intent.putExtra(MyConstants.DRAFT_TYPE, 4);
        } else if (action.equals(ActionConstants.OPEN_MY_AUCTION_OBJECTS)) {//我的拍卖
            intent = new Intent(mContext, MyAuctionActivity.class);
        } else if (action.equals(ActionConstants.OPEN_MY_SUBTLE_CLASSIFICATION)) {//特辑
            intent = new Intent(mContext, SpecialThemeDetailActivity.class);
            intent.putExtra("Schizodata", target);
        } else if (action.equals(ActionConstants.OPEN_USER_FANS)) {//进入粉丝列表
            intent = new Intent(mContext, FansListActivity.class);
            intent.putExtra(MyConstants.EXTRA_USER_ID, target);
        } else if (action.equals(ActionConstants.OPEN_MYSELLING_ITEMS)) {//打开在售商品页
            intent = new Intent(mContext, ReleaseListActivity.class);
            intent.putExtra("index", 2);
            intent.setAction(action);
        } else if (action.equals(ActionConstants.OPEN_MYUNSHELVED_ITEMS)) {//打开下架商品页
            intent = new Intent(mContext, ReleaseListActivity.class);
            intent.putExtra("index", 1);
            intent.setAction(action);
        }
        return intent;
    }

    public static void dispatchAction(Context mContext, String target, int type) {
        if (mContext == null || type <= 0) {
            return;
        }

        if (type == TagActivity.ARTICLE_TAG_BRAND) {//品牌

        } else if (type == TagActivity.ARTICLE_TAG_GOODS) {//商品
            if (TextUtils.isEmpty(target)) {
                return;
            }
            Intent intent = new Intent(mContext, GoodsDetailsActivity.class);
            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, target);
            mContext.startActivity(intent);
        } else if (type == TagActivity.ARTICLE_TAG_LOCATION) {//定位

        } else if (type == TagActivity.ARTICLE_TAG_OTHER) {//其他

        }

    }

    private static void startRequestForCreditZM(final Context context) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(context);
        HashMap<String, String> params = new HashMap<>();

        OkHttpClientManager.postAsyn(MyConstants.CREDIT_FOR_ZMXY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        CreditInfo info = JSON.parseObject(baseResult.obj, CreditInfo.class);
                        String url = info.getCredential();
                        Intent intent = new Intent();
                        intent.setClass(context, WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, url);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "芝麻信用");
                        ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_WEB);

                    } else {
                        CommonUtils.error(baseResult, (Activity) context, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public static void loginAndToast(Context context) {
        ToastUtil.toast(context, "请登录");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        CommonPreference.cleanUserInfoAndAccess();
        CommonPreference.setIntValue("savedMoney", -1);//钱包金额
        CommonPreference.setUserId(0);
    }

    public static void loginAndToast(Context context, int fromType) {
        ToastUtil.toast(context, "请登录");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(fromType);
        context.startActivity(intent);
        CommonPreference.cleanUserInfoAndAccess();
        CommonPreference.setIntValue("savedMoney", -1);//钱包金额
        CommonPreference.setUserId(0);
    }
}
