package com.huapu.huafen.utils;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.Audit;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.Brand;
import com.huapu.huafen.beans.Campaign;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.Credentials;
import com.huapu.huafen.beans.JoinBtn;
import com.huapu.huafen.beans.Kvs;
import com.huapu.huafen.beans.KvsdData;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.MyGoods;
import com.huapu.huafen.beans.Region;
import com.huapu.huafen.beans.Sale;
import com.huapu.huafen.beans.Screen;
import com.huapu.huafen.beans.SplashScreen;
import com.huapu.huafen.beans.Star;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.beans.VIP;
import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.events.KvsdEvent;

import java.io.Serializable;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * @author liang_xs
 * @ClassName: CommonPreference
 * @Description: SharedPreference管理
 * @date 2016-03-27
 */
public class CommonPreference {

    public static final String ACTION_IMAGE_URL = "action_image_url";
    public static final String PREF_FILE_NAME = "common_settings";
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String IS_FIRST = "is_first";
    public static final String IS_UPDATE = "is_update";
    public static final String IS_SELECT_DISTRICT = "is_select_district";
    public static final String SELECT_DISTRICT = "select_district";
    public static final String SELECT_CITY = "select_city";
    public static final String SELECT_CITY_ID = "select_city_id";
    public static final String RONG_TOKEN = "rong_token";
    public static final String APP_TOKEN = "app_token";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_ICON = "user_icon";
    public static final String USER_SEX = "user_sex";
    public static final String USER_LEVEL = "user_level";
    public static final String USER_CREDIT = "user_credit";
    public static final String USER_PROVINCE = "user_province";
    public static final String USER_CITY = "user_city";
    public static final String USER_AREA = "user_area";
    public static final String USER_PROVINCE_ID = "user_province_id";
    public static final String USER_CITY_ID = "user_city_id";
    public static final String USER_AREA_ID = "user_area_id";
    public static final String USER_IS_BIND_ALI = "user_is_bind_ali";
    public static final String USER_HAS_VERIFIED = "user_has_verified";
    public static final String USER_IS_BIND_WECHAT = "user_is_bind_wechat";
    public static final String USER_IS_BIND_SINA_WEIBO = "user_is_bind_sina_weibo";
    public static final String user_releaseCount = "user_releaseCount";
    public static final String user_selledCount = "user_selledCount";
    public static final String user_buyedCount = "user_buyedCount";
    public static final String user_refundCount = "user_refundCount";
    public static final String IS_TIP_BING = "is_tip_bing";
    public static final String ISDISTURB = "isdisturb";
    public static final String PREGNANTSTAT = "pregnantstat";
    public static final String DUEDATE = "duedate";
    public static final String FIRSRBABYSEX = "firsrbabysex";
    public static final String FIRSTBABYBIRTH = "firstbabybirth";
    public static final String SECONDBABYSEX = "secondbabysex";
    public static final String SECONDBABYBIRTH = "secondbabybirth";
    public static final String WECHAT_UID = "wechat_uid";
    public static final String SINA_UID = "sina_uid";
    public static final String CACHE_VERSION = "cache_version";
    public static final String BRAND_CACHE_VERSION = "brand_cache_version";
    public static final String MOMENT_CACHE_VERSION = "moment_cache_version";


    public static final String ERROR_LEVEL = "error_level";
    public static final String SEARCH_HISTORY_LIST_KEY = "search_history_list_key";
    public static final String SEARCH_ARTICLE_HISTORY_LIST_KEY = "search_article_history_list_key";
    public static final String USER_ZM_CREDIT_POINT = "zm_credit_point";
    public static final String USER_NOTIFY_COMMENT = "user_notify_comment";
    public static final String USER_NOTIFY_ORDER = "user_notify_order";
    public static final String USER_NOTIFY_NOTICE = "user_notify_notice";
    public static final String USER_NOTIFY_CAMPAIGN = "user_notify_campaign";
    public static final String USER_NOTIFY_FEATURE_ALERTS = "user_notify_feature_alerts";
    public static final String USER_GOODS_COMMENT = "user_goods_comment";
    public static final String KVSD = "kvsd";
    public static final String CAMPAIGN_BANNERS = "campaign_banners";
    public static final String HOME_BANNERS1 = "home_banners1";
    public static final String HOME_BANNERS1_5 = "home_banners1_5";

    public static final String HOME_BANNERS2 = "home_banners2";
    public static final String CATE_BANNERS1 = "cate_banners1";
    public static final String CATE_BANNERS2 = "cate_banners2";
    public static final String VIP_USER_BANNER = "vip_user_banner ";
    public static final String VIP_GOODS_BANNER = "vip_goods_banner";
    public static final String NEW_GOODS_BANNER = "new_goods_banner";
    public static final String STAR_USER_BANNER = "star_user_banner";
    public static final String STAR_GOODS_BANNER = "star_goods_banner";
    public static final String FOLLOWING_BANNER = "following_banner";
    public static final String SHOW_CASES = "show_cases";
    public static final String SPLASH_SCREEN = "splash_screen";

    public static final String CAMPAIGNS = "campaigns";
    public static final String VIP = "vip";
    public static final String STAR = "star";
    public static final String SALE = "sale";
    public static final String AUDIT = "audit";
    public static final String MY_GOODS = "my_goods";
    public static final String GRANTEDCAMPAIGNS = "grantedCampaigns";
    public static final String GRANTEDONEYUAN = "grantedOneYuan";
    public static final String REGIONSD = "regionsd";
    public static final String REGIONS = "regions";
    public static final String CATSD = "catsd";
    public static final String CATS = "cats";
    public static final String NOTIFICATIONS = "notifications";
    public static final String VERSION_CODE = "version_code";
    public static final String IS_FIRST_LIKE = "is_first_like";
    public static final String ALI_PUSH_TOKEN = "ali_push_token";
    public static final String DISTURB_STATUS = "disturb_status";
    public static final String OPEN = "open";
    public static final String NIGHT_ONLY = "nightOnly";
    public static final String CLOSE = "close";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_SECRET = "access_secret";
    private static final String ACTIVATION_TOKEN = "activation_token";
    private static final String ACTIVATION_SECRET = "activation_secret";
    private static final String BRANDS_KEY = "brands_share_key";


    private static final String COMMODITY_KEY = "commodity_key";

    private static final String BRANDS_HISTORY_KEY = "brands_history_share_key";
    private static final String BRAND_GROUPS = "brad_groups";
    private static final String SPORT_CATS = "sport_cats";
    private final static String API_HOST = "_hua_fen_api_host";
    private final static String DEFAULT_DEV_HOST = "https://api-t.huafer.cc/api/";
    private static final String ONE_EVENT = "one_event";
    private static final String CREDENTIALS = "Credentials";
    private static final String USER_PHONE = "huafer_user_phone_phone";

    @Deprecated
    public static String getAppToken() {
        return CommonPreference.getStringValue(CommonPreference.APP_TOKEN, "");
    }

    public static String getCacheVersion() {
        return CommonPreference.getStringValue(CommonPreference.CACHE_VERSION, "");
    }

    public static void setCacheVersion(String cacheVersion) {
        CommonPreference.setStringValue(CommonPreference.CACHE_VERSION, cacheVersion);
    }

    public static String getBrandCacheVersion() {
        return CommonPreference.getStringValue(CommonPreference.BRAND_CACHE_VERSION, "");
    }

    public static void setBrandCacheVersion(String brandCacheVersion) {
        CommonPreference.setStringValue(CommonPreference.BRAND_CACHE_VERSION, brandCacheVersion);
    }

    public static String getKVSDSHA1() {
        return CommonPreference.getStringValue(CommonPreference.KVSD, "");
    }

    public static void setKVSDSHA1(String kvsdSHA1) {
        CommonPreference.setStringValue(CommonPreference.KVSD, kvsdSHA1);
    }

    public static long getUserId() {
        return CommonPreference.getLongValue(CommonPreference.USER_ID, 0);
    }

    public static void setUserId(long userId) {
        CommonPreference.setLongValue(CommonPreference.USER_ID, userId);
    }

    public static int getVersionCode() {
        return CommonPreference.getIntValue(CommonPreference.VERSION_CODE, 0);
    }

    public static void setVersionCode(int versionCode) {
        CommonPreference.setIntValue(CommonPreference.VERSION_CODE, versionCode);
    }

    public static void clearKVSDObject() {
        setKVSDSHA1("");
        setCampaignBanners(new ArrayList<CampaignBanner>());
        setCampaigns(new ArrayList<Campaign>());
    }

    public static void setKVSDObject(KvsdData kvsdData) {
        LogUtil.e("KvsdData", kvsdData.toString());
        if (kvsdData == null) {
            return;
        }
        //		kvsdData =KvsdData.mockData(kvsdData);

        String activationToken = kvsdData.getActivationToken();
        if (!TextUtils.isEmpty(activationToken)) {
            setActivationToken(activationToken);
        }

        String activationSecret = kvsdData.getActivationSecret();
        if (!TextUtils.isEmpty(activationSecret)) {
            setActivationSecret(activationSecret);
        }

        String accessSecret = kvsdData.getAccessSecret();
        if (!TextUtils.isEmpty(accessSecret)) {
            CommonPreference.setAccessSecret(accessSecret);
        }

        String accessToken = kvsdData.getAccessToken();
        if (!TextUtils.isEmpty(accessToken)) {
            CommonPreference.setAccessToken(accessToken);
        }

        String userPhone = kvsdData.getPhone();
        if (!TextUtils.isEmpty(userPhone)) {
            CommonPreference.setPhone(userPhone);
        }

        //		int notifications = kvsdData.getNotifications();
//        UnreadMessageCounts unreadMessageCounts = kvsdData.getUnreadMessageCounts();
//        if (unreadMessageCounts != null) {
//            MyConstants.UNREAD_ORDER_COUNT = unreadMessageCounts.getOrder();
//            MyConstants.UNREAD_SYSTEM_COUNT = unreadMessageCounts.getSystem();
//            MyConstants.UNREAD_COMMENT_MSG_COUNT = unreadMessageCounts.getComment();
//            MessageUnReadCountEvent event = new MessageUnReadCountEvent();
//            event.isUpdate = true;
//            EventBus.getDefault().post(event);
//        }
        //		setNotifications(notifications);
        String grantedCampaigns = kvsdData.getGrantedCampaigns();
        setGrantedCampaigns(grantedCampaigns);
        String catsd = kvsdData.getCatsd();
        String localCatsd = getCatsd();
        if (!TextUtils.isEmpty(catsd)
                && !catsd.equals(localCatsd)
                ) {
            setCatsd(catsd);
            ArrayList<Cat> cats = kvsdData.getCats();
            setCats(cats);
        }
        String regionsd = kvsdData.getRegionsd();
        String localRegionsd = getRegionsd();
        if (!TextUtils.isEmpty(regionsd)
                && !regionsd.equals(localRegionsd)
                ) {
            setRegionsd(regionsd);
            ArrayList<Region> regions = kvsdData.getRegions();
            setRegions(regions);
        }
        LogUtil.e("getKVSDSHA1", getKVSDSHA1());
        String kvsdSHA1 = kvsdData.getKvsd();
        String localSHA1 = getKVSDSHA1();
        LogUtil.e("lh1.5", kvsdData.getKvs());
        LogUtil.d("danielluanll", "kvsdSHA1 =" + kvsdSHA1);
        LogUtil.d("danielluanll", "localSHA1 =" + localSHA1);


        if (!TextUtils.isEmpty(kvsdSHA1) && !kvsdSHA1.equals(localSHA1)) {

            Kvs kvs = kvsdData.getKvs();
            LogUtil.e("lh", kvsdData.getKvs());
            LogUtil.e("lh2.0", kvs.getHomeBanner2());
            LogUtil.d("danielluanll", "kvs =" + kvs);

            BannerData homeBanner1 = kvs.getHomeBanner1();
            setHomeBanners1(homeBanner1);
            BannerData homeBanner1_5 = kvs.getHomeBanner1_5();
            setHomeBanners1_5(homeBanner1_5);
            LogUtil.e("lh1.5", kvs.getHomeBanner1_5());
            BannerData homeBanner2 = kvs.getHomeBanner2();
            setHomeBanners2(homeBanner2);
            LogUtil.e("lh2.0", kvs.getHomeBanner2());
            BannerData cateBanner1 = kvs.getCateBanner1();
            setCateBanners1(cateBanner1);
            BannerData cateBanner2 = kvs.getCateBanner2();
            setCateBanners2(cateBanner2);
            ArrayList<CampaignBanner> showcases = kvs.getShowcases();
            setShowcases(showcases);
            ArrayList<CampaignBanner> banners = kvs.getCampaignBanners();
            setCampaignBanners(banners);
            BannerData vipUserBanner = kvs.getVipUserBanner();
            setVipUserBanner(vipUserBanner);
            BannerData vipGoodsBanner = kvs.getVipGoodsBanner();
            setVipGoodsBanner(vipGoodsBanner);
            BannerData starUserBanner = kvs.getStarUserBanner();
            setStarUserBanner(starUserBanner);
            BannerData starGoodsBanner = kvs.getStarGoodsBanner();
            setStarGoodsBanner(starGoodsBanner);
            BannerData newGoodsBanner = kvs.getBrandnewGoodsBanner();
            setNewGoodsBanner(newGoodsBanner);
            BannerData followingBanner = kvs.getFollowingBanner();
            setFollowingBanner(followingBanner);
            BannerData oneEvent = kvs.getOneEvent();
            setOneEvent(oneEvent);
            // 测试数据
            //			ArrayList<CampaignBanner> list = new ArrayList<>();
            //			CampaignBanner banner = new CampaignBanner();
            //			banner.setAction("openWebview");
            //			banner.setImage("http://huafer-test.oss-cn-beijing.aliyuncs.com/banner/testBannerOne.jpg");
            //			banner.setTarget("http://huafer-test.oss-cn-beijing.aliyuncs.com/banner/testBannerOne.jpg");
            //			list.add(banner);
            //			star = new Star();
            //			star.setBanners(list);
            // 测试结束
            SplashScreen splashScreen = kvs.getSplashScreen();

            // TODO 测试数据
            //				splashScreen = new SplashScreen();
            //				splashScreen.setImage("http://img.my.csdn.net/uploads/201301/30/1359513520_7746.png");
            //				splashScreen.setName("广告");
            //				splashScreen.setAction("openWebview");
            //				splashScreen.setEnd(1512448357000L);
            //				splashScreen.setStart(1480912357000L);
            //				splashScreen.setTarget("http://www.huafer.cc");
            //				splashScreen.setRepeat(1);
            //				splashScreen.setRepeatTime(1000*60);
            // TODO 测试结束
            setSplashScreen(splashScreen);

            Audit audit = kvs.getAudit();
            setAudit(audit);
            Sale sale = kvs.getSale();
            setSale(sale);
            ArrayList<MyGoods> myGoods = kvs.getMyGoods();
            setMyGoods(myGoods);

            LogUtil.d("danielluanll", "myGoods =" + myGoods.size());

            ArrayList<Campaign> campaigns = kvs.getCampaigns();
            setCampaigns(campaigns);
            LogUtil.e("getGrantedCampaigns", getGrantedCampaigns());
            ArrayList<CampaignBanner> getCampaignBanners = getCampaignBanners();
            if (!ArrayUtil.isEmpty(getCampaignBanners)) {
                for (CampaignBanner ba : getCampaignBanners) {
                    LogUtil.e("CampaignBanner", ba.toString());
                }
            }
            final ArrayList<Campaign> getCampaigns = getCampaigns();
            if (!ArrayUtil.isEmpty(getCampaigns)) {
                for (Campaign ba : getCampaigns) {
                    LogUtil.e("getCampaigns", ba.toString());
                }
            }
            setKVSDSHA1(kvsdSHA1);
        }
        SplashScreen splashScreen = getSplashScreen();
        if (splashScreen != null) {
            ArrayList<Screen> list = splashScreen.getList();
            for (int i = 0; list != null && i < list.size(); i++) {
                Screen screen = list.get(i);
                if (!TextUtils.isEmpty(screen.getImage()) && !TextUtils.isEmpty(screen.getName())) {
                    final String imgUrl = screen.getImage();
                    final String name = screen.getName();
                    Bitmap bmp = FileUtils.getBitmap(name);
                    if (bmp == null) {
                        ImageLoader.loadBitmap(MyApplication.getApplication(), imgUrl, new BitmapCallback() {
                            @Override
                            public void onBitmapDownloaded(final Bitmap bitmap) {
                                if (bitmap != null) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean isSaved = FileUtils.saveBitmap(name, bitmap);
                                            if (isSaved) {

                                            }
                                        }
                                    }).start();
                                }
                            }
                        });
                    }
                }
            }
        }
        ArrayList<Campaign> campaignsList = getCampaigns();
        if (!ArrayUtil.isEmpty(campaignsList)) {
            final Campaign campaign = campaignsList.get(0);
            Bitmap bmp = FileUtils.getBitmap(campaign.getJoinBtn().getTitle() + "_joinBtn");
            if (bmp == null) {
                final JoinBtn btn = campaign.getJoinBtn();
                String joinPeriod = campaign.getJoinPeriod();
                if (btn != null && btn.getIcon() != null && !TextUtils.isEmpty(joinPeriod)) {
                    String imgUrl = btn.getIcon();

                    ImageLoader.loadBitmap(MyApplication.getApplication(),
                            imgUrl, new BitmapCallback() {
                                @Override
                                public void onBitmapDownloaded(final Bitmap bitmap) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            boolean isSaved = FileUtils.saveBitmap(campaign.getJoinBtn().getTitle() + "_joinBtn", bitmap);
                                            if (isSaved) {
                                                KvsdEvent event = new KvsdEvent();
                                                event.isKvsdUpdate = true;
                                                event.campaign = campaign;
                                                EventBus.getDefault().post(event);
                                            }
                                        }
                                    }).start();
                                }
                            });
                } else {
                    KvsdEvent event = new KvsdEvent();
                    event.isKvsdUpdate = true;
                    EventBus.getDefault().post(event);
                }
            }
        } else {
            KvsdEvent event = new KvsdEvent();
            event.isKvsdUpdate = true;
            EventBus.getDefault().post(event);
        }

    }

    public static int getNotifications() {
        return getIntValue(NOTIFICATIONS, 0);
    }

    public static void setNotifications(int notifications) {
        setIntValue(NOTIFICATIONS, notifications);
    }

    public static String getGrantedCampaigns() {
        return getStringValue(GRANTEDCAMPAIGNS, "");
    }

    public static void setGrantedCampaigns(String grantedCampaigns) {
        setStringValue(GRANTEDCAMPAIGNS, grantedCampaigns);
    }

    public static int getGrantedOneYun() {
        return getIntValue(GRANTEDONEYUAN, 0);
    }


    public static void setGrantedOneYun(int grantedOneYun) {
        setIntValue(GRANTEDONEYUAN, grantedOneYun);
    }

    public static ArrayList<CampaignBanner> getCampaignBanners() {
        ArrayList<CampaignBanner> campaignBanners = getSerializable(CAMPAIGN_BANNERS, new TypeReference<ArrayList<CampaignBanner>>() {
        });
        return campaignBanners;
    }

    public static void setCampaignBanners(ArrayList<CampaignBanner> banner) {
        setSerializable(CAMPAIGN_BANNERS, banner);
    }

    public static String getRegionsd() {
        return getStringValue(REGIONSD, "");
    }

    public static void setRegionsd(String regions) {
        setStringValue(REGIONSD, regions);
    }

    public static ArrayList<Region> getRegions() {
        ArrayList<Region> regions = getSerializable(REGIONS, new TypeReference<ArrayList<Region>>() {
        });
        return regions;
    }

    public static void setRegions(ArrayList<Region> regions) {
        setSerializable(REGIONS, regions);
    }

    public static String getCatsd() {
        return getStringValue(CATSD, "");
    }

    public static void setCatsd(String catsd) {
        setStringValue(CATSD, catsd);
    }

    public static ArrayList<Cat> getCats() {
        ArrayList<Cat> cats = getSerializable(CATS, new TypeReference<ArrayList<Cat>>() {
        });
        return cats;
    }

    public static void setCats(ArrayList<Cat> cats) {
        setSerializable(CATS, cats);
    }

    public static BannerData getHomeBanners1() {
        return getObject(HOME_BANNERS1, BannerData.class);
    }

    public static void setHomeBanners1(BannerData banner) {
        setObject(HOME_BANNERS1, banner);
    }

    public static BannerData getHomeBanners1_5() {
        return getObject(HOME_BANNERS1_5, BannerData.class);
    }

    public static void setHomeBanners1_5(BannerData banner) {
        setObject(HOME_BANNERS1_5, banner);
    }

    public static BannerData getHomeBanners2() {
        return getObject(HOME_BANNERS2, BannerData.class);
    }

    public static void setHomeBanners2(BannerData banner) {
        setObject(HOME_BANNERS2, banner);
    }


    public static BannerData getCateBanners1() {
        return getObject(CATE_BANNERS1, BannerData.class);
    }

    public static void setCateBanners1(BannerData banner) {
        setObject(CATE_BANNERS1, banner);
    }

    public static BannerData getCateBanners2() {
        return getObject(CATE_BANNERS2, BannerData.class);
    }

    public static void setCateBanners2(BannerData banner) {
        setObject(CATE_BANNERS2, banner);
    }

    public static BannerData getVipUserBanner() {
        return getObject(VIP_USER_BANNER, BannerData.class);
    }

    public static void setVipUserBanner(BannerData banner) {
        setObject(VIP_USER_BANNER, banner);
    }

    public static BannerData getVipGoodsBanner() {
        return getObject(VIP_GOODS_BANNER, BannerData.class);
    }

    public static void setVipGoodsBanner(BannerData banner) {
        setObject(VIP_GOODS_BANNER, banner);
    }

    public static BannerData getNewGoodsBanner() {
        return getObject(NEW_GOODS_BANNER, BannerData.class);
    }

    public static void setNewGoodsBanner(BannerData banner) {
        setObject(NEW_GOODS_BANNER, banner);
    }

    public static BannerData getStarUserBanner() {
        return getObject(STAR_USER_BANNER, BannerData.class);
    }

    public static void setStarUserBanner(BannerData banner) {
        setObject(STAR_USER_BANNER, banner);
    }

    public static BannerData getStarGoodsBanner() {
        return getObject(STAR_GOODS_BANNER, BannerData.class);
    }

    public static void setStarGoodsBanner(BannerData banner) {
        setObject(STAR_GOODS_BANNER, banner);
    }

    public static BannerData getFollowingBanner() {
        return getObject(FOLLOWING_BANNER, BannerData.class);
    }

    public static void setFollowingBanner(BannerData banner) {
        setObject(FOLLOWING_BANNER, banner);
    }

    public static BannerData getOneEvent() {
        return getObject(ONE_EVENT, BannerData.class);
    }

    public static void setOneEvent(BannerData banner) {
        setObject(ONE_EVENT, banner);
    }

    public static SplashScreen getSplashScreen() {
        return getObject(SPLASH_SCREEN, SplashScreen.class);
    }

    public static void setSplashScreen(SplashScreen splashScreen) {
        setObject(SPLASH_SCREEN, splashScreen);
    }

    public static ArrayList<CampaignBanner> getShowcases() {
        ArrayList<CampaignBanner> campaignBanners = getSerializable(SHOW_CASES, new TypeReference<ArrayList<CampaignBanner>>() {
        });
        return campaignBanners;
    }

    public static void setShowcases(ArrayList<CampaignBanner> banner) {
        setSerializable(SHOW_CASES, banner);
    }

    public static ArrayList<Campaign> getCampaigns() {
        ArrayList<Campaign> campaigns
                = getSerializable(CAMPAIGNS, new TypeReference<ArrayList<Campaign>>() {
        });
        return campaigns;
    }

    public static void setCampaigns(ArrayList<Campaign> campaigns) {
        setSerializable(CAMPAIGNS, campaigns);
    }

    public static Audit getAudit() {
        Audit audit = getSerializable(AUDIT, new TypeReference<Audit>() {
        });
        return audit;
    }

    public static void setAudit(Audit audit) {
        setSerializable(AUDIT, audit);
    }

    public static VIP getVIP() {
        VIP vip = getSerializable(VIP, new TypeReference<VIP>() {
        });
        return vip;
    }

    public static void setVIP(VIP vip) {
        setSerializable(VIP, vip);
    }

    public static Star getStar() {
        Star star = getSerializable(STAR, new TypeReference<Star>() {
        });
        return star;
    }

    public static void setStar(Star star) {
        setSerializable(STAR, star);
    }

    public static Sale getSale() {
        Sale sale = getSerializable(SALE, new TypeReference<Sale>() {
        });
        return sale;
    }

    public static void setSale(Sale sale) {
        setSerializable(SALE, sale);
    }

    public static ArrayList<MyGoods> getMyGoods() {
        ArrayList<MyGoods> publish = getSerializable(MY_GOODS, new TypeReference<ArrayList<MyGoods>>() {
        });
        return publish;
    }

    public static void setMyGoods(ArrayList<MyGoods> publish) {
        setSerializable(MY_GOODS, publish);
    }

    public static boolean isLogin() {
        return getUserId() == 0 ? false : true;
    }

    public static void setUserZmCreditPoint(int zmCreditPoint) {
        setIntValue(USER_ZM_CREDIT_POINT, zmCreditPoint);
    }

    public static String getNotifyComment() {
        return getStringValue(USER_NOTIFY_COMMENT, "1");
    }

    public static void setNotifyComment(String notifyComment) {
        if ("1".equals(notifyComment) || "0".equals(notifyComment)) {
            setStringValue(USER_NOTIFY_COMMENT, notifyComment);
        }
    }

    public static String getNotifyNotice() {
        return getStringValue(USER_NOTIFY_NOTICE, "1");
    }

    public static void setNotifyNotice(String notifyPrivate) {
        if ("1".equals(notifyPrivate) || "0".equals(notifyPrivate)) {
            setStringValue(USER_NOTIFY_NOTICE, notifyPrivate);
        }
    }


    public static String getNotifyOrder() {
        return getStringValue(USER_NOTIFY_ORDER, "1");
    }

    public static void setNotifyOrder(String notifyOrder) {
        if ("1".equals(notifyOrder) || "0".equals(notifyOrder)) {
            setStringValue(USER_NOTIFY_ORDER, notifyOrder);
        }
    }

    public static String getNotifyCampaign() {
        return getStringValue(USER_NOTIFY_CAMPAIGN, "1");
    }

    public static void setNotifyCampaign(String notify) {
        if ("1".equals(notify) || "0".equals(notify)) {
            setStringValue(USER_NOTIFY_CAMPAIGN, notify);
        }
    }

    public static String getNotifyOtherFeatureAlerts() {
        return getStringValue(USER_NOTIFY_FEATURE_ALERTS, "1");
    }

    public static void setNotifyOtherFeatureAlerts(String notifyOtherFeatureAlerts) {
        if ("2".equals(notifyOtherFeatureAlerts) || "1".equals(notifyOtherFeatureAlerts) || "0".equals(notifyOtherFeatureAlerts)) {
            setStringValue(USER_NOTIFY_FEATURE_ALERTS, notifyOtherFeatureAlerts);
        }
    }

    public static String getGoodsComment() {
        return getStringValue(USER_GOODS_COMMENT, "1");
    }

    public static void setGoodsComment(String goodsComment) {
        if ("1".equals(goodsComment)
                || "2".equals(goodsComment)
                || "0".equals(goodsComment)) {
            setStringValue(USER_GOODS_COMMENT, goodsComment);
        }
    }

    public static void cleanUserInfoAndAccess() {
        setUserId(0);
        setStringValue(USER_ICON, "");
        setStringValue(USER_NAME, "");
        setIntValue(USER_SEX, 0);
        setIntValue(USER_LEVEL, 1);
        setBooleanValue(USER_CREDIT, false);
        setBooleanValue(USER_HAS_VERIFIED, false);
        setStringValue(USER_PROVINCE, "");
        setStringValue(USER_CITY, "");
        setStringValue(USER_AREA, "");
        setIntValue(USER_PROVINCE_ID, 0);
        setIntValue(USER_CITY_ID, 0);
        setIntValue(USER_AREA_ID, 0);
        setBooleanValue(USER_IS_BIND_ALI, false);
        setBooleanValue(USER_IS_BIND_WECHAT, false);
        if (MyApplication.fromVersionCode < 22 || !CommonPreference.isLogin()) {
            setIntValue(USER_ZM_CREDIT_POINT, 0);
        }

        setNotifyOrder("1");
        setNotifyComment("1");
        setGoodsComment("1");

        //删除登录accessToken accessSecret
        CommonPreference.setAccessToken("");
        CommonPreference.setAccessSecret("");
    }

    public static UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(getUserId());
        userInfo.setUserIcon(getStringValue(USER_ICON, ""));
        userInfo.setUserName(getStringValue(USER_NAME, ""));
        userInfo.setUserSex(getIntValue(USER_SEX, 0));
        userInfo.setUserLevel(getIntValue(USER_LEVEL, 1));
        userInfo.setHasCredit(getBooleanValue(USER_CREDIT, false));
        userInfo.setZmCreditPoint(getIntValue(USER_ZM_CREDIT_POINT, 0));
        userInfo.setNotifyComment(getNotifyComment());
        userInfo.setNotifyOrder(getNotifyOrder());
        userInfo.setGoodsComment(getGoodsComment());
        userInfo.buyedCount = getIntValue(user_buyedCount, 0);
        userInfo.refundCount = getIntValue(user_refundCount, 0);
        userInfo.setReleaseCount(getIntValue(user_releaseCount, 0));
        userInfo.setSelledCount(getIntValue(user_selledCount, 0));
        Area area = new Area();
        area.setProvince(getStringValue(USER_PROVINCE, ""));
        area.setCity(getStringValue(USER_CITY, ""));
        area.setArea(getStringValue(USER_AREA, ""));
        area.setProvinceId(getIntValue(USER_PROVINCE_ID, 0));
        area.setCityId(getIntValue(USER_CITY_ID, 0));
        area.setAreaId(getIntValue(USER_AREA_ID, 0));
        userInfo.setArea(area);
        userInfo.hasVerified = getBooleanValue(USER_HAS_VERIFIED, false);
        return userInfo;
    }

    public static void setUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            setUserId(userInfo.getUserId());
            setStringValue(USER_ICON, userInfo.getUserIcon());
            setStringValue(USER_NAME, userInfo.getUserName());
            setIntValue(USER_SEX, userInfo.getUserSex());
            setIntValue(USER_LEVEL, userInfo.getUserLevel());
            setIntValue(user_buyedCount, userInfo.buyedCount);
            setIntValue(user_refundCount, userInfo.refundCount);
            setIntValue(user_releaseCount, userInfo.getReleaseCount());
            setIntValue(user_selledCount, userInfo.getSelledCount());
            setBooleanValue(USER_CREDIT, userInfo.getHasCredit());
            if (userInfo.getArea() != null) {
                setStringValue(USER_PROVINCE, userInfo.getArea().getProvince());
                setStringValue(USER_CITY, userInfo.getArea().getCity());
                setStringValue(USER_AREA, userInfo.getArea().getArea());
                setIntValue(USER_PROVINCE_ID, userInfo.getArea().getProvinceId());
                setIntValue(USER_CITY_ID, userInfo.getArea().getCityId());
                setIntValue(USER_AREA_ID, userInfo.getArea().getAreaId());
            }

            setBooleanValue(USER_IS_BIND_ALI, userInfo.getIsBindALi());
            setBooleanValue(USER_HAS_VERIFIED, userInfo.hasVerified);
            setBooleanValue(USER_IS_BIND_WECHAT, userInfo.getIsBindWechat());
            setBooleanValue(USER_IS_BIND_SINA_WEIBO, userInfo.isBindWeibo());
            setIntValue(USER_ZM_CREDIT_POINT, userInfo.getZmCreditPoint());
            if (!TextUtils.isEmpty(userInfo.getNotifyComment())) {
                setNotifyComment(userInfo.getNotifyComment());
            }
            if (!TextUtils.isEmpty(userInfo.getNotifyOrder())) {
                setNotifyOrder(userInfo.getNotifyOrder());
            }
            if (!TextUtils.isEmpty(userInfo.getGoodsComment())) {
                setGoodsComment(userInfo.getGoodsComment());
            }
        }
    }

    public static String getPhone() {
        return getStringValue(USER_PHONE, "");
    }

    public static void setPhone(String phone) {
        setStringValue(USER_PHONE, phone);
    }

    public static String getStringValue(String keyWord, String defaultValue) {
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        return sp.getString(keyWord, defaultValue);
    }

    public static void setStringValue(String keyWord, String value) {
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putString(keyWord, value).commit();
    }

    public static int getIntValue(String keyWord, int defaultValue) {
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        return sp.getInt(keyWord, defaultValue);
    }

    public static void setIntValue(String keyWord, int value) {
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putInt(keyWord, value).commit();
    }

    public static boolean getBooleanValue(String keyWord, boolean defaultValue) {
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        return sp.getBoolean(keyWord, defaultValue);
    }

    public static void setBooleanValue(String keyWord, boolean isCheck) {
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putBoolean(keyWord, isCheck).commit();
    }

    public static long getLongValue(String keyWord, long defaultValue) {
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        return sp.getLong(keyWord, defaultValue);
    }

    public static void setLongValue(String keyWord, long value) {
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putLong(keyWord, value).commit();
    }

    public static void setSerializable(String key, Serializable value) {
        String json = JSON.toJSONString(value);
        LogUtil.e("setSerializable", "key ==" + key + ",json == " + json + "");
        setStringValue(key, json);
    }

    public static <T extends Serializable> T getSerializable(String key, TypeReference<T> type) {
        try {
            return JSON.parseObject(getStringValue(key, null), type);
        } catch (Exception e) {
            LogUtil.e("getSerializable", e.getMessage());
            return null;
        }
    }

    public static void setObject(String key, Object obj) {
        String jsonString = JSON.toJSONString(obj);
        setStringValue(key, jsonString);
    }

    public static <T> T getObject(String key, Class<T> clazz) {

        try {
            String json = getStringValue(key, "");
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static void remove(String key) {
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

    public static void saveLocationMillions() {
        setLongValue("currentTimeMillis", System.currentTimeMillis());
    }

    public static long getLocationMillions() {
        return getLongValue("currentTimeMillis", 0L);
    }

    public static void saveSleepMillions() {
        setLongValue("sleepMillions", System.currentTimeMillis());
    }

    public static long getSleepMillions() {
        return getLongValue("sleepMillions", 0L);
    }

    public static void saveLocalData(LocationData data) {
        setObject("LocationData", data);
    }

    public static LocationData getLocalData() {
        return getObject("LocationData", LocationData.class);
    }

    public static String getApiHost() {
        return CommonPreference.getStringValue(API_HOST, DEFAULT_DEV_HOST);
    }

    public static void setApiHost(String url) {
        CommonPreference.setStringValue(API_HOST, url);
    }

    public static String getAccessToken() {
        return getStringValue(ACCESS_TOKEN, "");
    }

    public static void setAccessToken(String accessToken) {
        setStringValue(ACCESS_TOKEN, accessToken);
    }

    public static String getAccessSecret() {
        return getStringValue(ACCESS_SECRET, "");
    }

    public static void setAccessSecret(String accessSecret) {
        setStringValue(ACCESS_SECRET, accessSecret);
    }

    public static String getActivationToken() {
        return getStringValue(ACTIVATION_TOKEN, "");
    }

    public static void setActivationToken(String activationToken) {
        setStringValue(ACTIVATION_TOKEN, activationToken);
    }

    public static String getActivationSecret() {
        return getStringValue(ACTIVATION_SECRET, "");
    }


    public static void setActivationSecret(String activationSecret) {
        setStringValue(ACTIVATION_SECRET, activationSecret);

    }

    public static ArrayList<Brand> getBrands() {
        return getSerializable(BRANDS_KEY, new TypeReference<ArrayList<Brand>>() {
        });
    }

    public static void setBrands(ArrayList<Brand> brands) {
        setSerializable(BRANDS_KEY, brands);
    }

    public static ArrayList<Commodity> getCommod() {
        return getSerializable(COMMODITY_KEY, new TypeReference<ArrayList<Commodity>>() {
        });
    }

    public static void setCommod(ArrayList<Commodity> commodities) {
        setSerializable(COMMODITY_KEY, commodities);
    }

    public static void setHistoryBrand(ArrayList<Brand> brands) {
        setSerializable(BRANDS_HISTORY_KEY, brands);
    }

    public static ArrayList<Brand> getHistoryBrands() {
        return getSerializable(BRANDS_HISTORY_KEY, new TypeReference<ArrayList<Brand>>() {
        });
    }

    public static ArrayList<Pair<String, ArrayList<Brand>>> getBrandGroups() {
        return getSerializable(BRAND_GROUPS, new TypeReference<ArrayList<Pair<String, ArrayList<Brand>>>>() {
        });
    }

    public static void setBrandGroups(ArrayList<Pair<String, ArrayList<Brand>>> data) {
        setSerializable(BRAND_GROUPS, data);
    }

    public static ArrayList<Cat> getSportCats() {
        ArrayList<Cat> cats = getSerializable(SPORT_CATS, new TypeReference<ArrayList<Cat>>() {
        });
        return cats;
    }

    public static void setSportCats(ArrayList<Cat> cats) {
        setSerializable(SPORT_CATS, cats);
    }

    public static Credentials getCredentials() {
        return getObject(CREDENTIALS, Credentials.class);
    }

    public static void setCredentials(Credentials result) {
        setObject(CREDENTIALS, result);
    }

}
