package com.huapu.huafen.common;

import android.graphics.Bitmap;

import com.huapu.huafen.utils.Config;

/**
 * @author liang_xs
 * @ClassName: MyConstants
 * @Description: 常量类
 * @date 2016-03-27
 */
public class MyConstants {

    public static final String BASE_URL = Config.API_HOST;
    public static final String BASE_WEB_URL = Config.API_WEB_HOST;
    //	public static final String OSS_FOLDER_BUCKET = "huafer-test"; // OSS测试
    public static final String OSS_FOLDER_BUCKET = "huafer";
    public static final String OSS_VIDEO_FOLDER_BUCKET = Config.OSS_VIDEO_FOLDER_BUCKET;
    //	public static final String OSS_IMG_HEAD = "http://huafer-test.img-cn-beijing.aliyuncs.com/";// 测试地址
    public static final String OSS_IMG_HEAD = "http://imgs.huafer.cc/";
    public static final String OSS_SMALL_STYLE = "@!small"; // 小图→用于列表
    public static final String OSS_MEDIUM_STYLE = "@!medium"; // 中图→用于商品详情
    public static final String OSS_ORIGINAL_STYLE = "@!original"; // 大图→用于大图预览
    public static final String OSS_BANNER_STYLE = "@!banner"; // banner →banner
    public static final String RONG_KEFU_KEY = "KEFU146675008576544";

    public static final String VIP_BRAND = "https://i.huafer.cc/vip-zone/active-user-ladder";

    public static final String MOMENT_EDIT = "MOMENT_EDIT";
    /**
     * leancloud验签
     */
    public static final String LEANCLOUD_SIGN = BASE_URL + "user/v1/loginSign";
    public static final String LEANCLOUD_SIGN2 = BASE_URL + "user/v1/beforeConversationSign";
    /**
     * 更新普通花语
     */
    public static final String MOMENT_UPDATE = BASE_URL + "moment/v1/update";
    /**
     * 花语删除
     */
    public static final String ARTICLE_DELETE = BASE_URL + "article/v1/del";
    /**
     * 简易花语删除
     */
    public static final String MOMENT_DELETE = BASE_URL + "/moment/v1/del";
    /**
     * 支付宝账号绑定
     */
    public static final String BIND_ACCOUNT = BASE_URL + "user/v1/userBindAccount";
    /**
     * 绑定新浪微博
     */
    public static final String BIND_SINA_WEIBO = BASE_URL + "/user/v1/bindOAuthAccount";
    /**
     * 卖家回复评论
     */
    public static final String COMMIT_SELLER_COMMENT = BASE_URL + "order/v1/saveSellerComments";
    /**
     * 芝麻信用授权
     */
    public static final String CREDIT_FOR_ZMXY = BASE_URL + "sesame/v1/auth";
    /**
     * 芝麻信用跳转回的url，仅用于判断是否芝麻信用授权成功
     */
    public static final String CREDIT_CALLBACK = BASE_URL + "sesame/v1/callBack";
    /**
     * 芝麻信用分数查询
     */
    public static final String CREDIT_UPDATE_CODE = BASE_URL + "sesame/v1/queryNo";
    /**
     * 更换手机号
     */
    public static final String REBIND_PHONE_NUMBER = BASE_URL + "user/v1/modifyUserPhone";
    /**
     * 查询明星列表
     */
    public static final String QUERYCELEBRITYPAGE = BASE_URL + "user/v1/queryCelebrityPage";
    /**
     * 查询热搜商品
     */
    public static final String QUERY_HOT_GOODS = BASE_URL + "search/v1/hotGoods";
    /**
     * 查询热搜用户
     */
    public static final String QUERY_HOT_USERS = BASE_URL + "search/v1/hotUsers";

    public static final String MY_PROFILE = BASE_URL + "user/v1/profile";

    /**
     * 封面列表
     */
    public static final String MY_COVER_LIST = BASE_URL + "user/v1/profile/selectBackground";

    /**
     * 确认出价
     */
    public static final String CONFIRM_OFFER_PRICE = BASE_URL + "auction/v1/bid";


    /**
     * 交纳保证金
     */
    public static final String PAY_DEPOSIT = BASE_URL + "pay/v2/payBidDeposity";

    /**
     * 我的拍卖列表
     */
    public static final String MY_AUCTION = BASE_URL + "auction/v1/myAuctions";

    /**
     *
     */
    public static final String PAY_DEPOSIT_SUCCESS = BASE_URL + "pay/v1/payBidDepositySuccess";
    /**
     * 更新背景
     */
    public static final String UPDATE_BG = BASE_URL + "user/v1/profile/updateBackground";

    /**
     * 搜索商品下拉提示
     */
    public static final String QUERY_KEY_WORDS_GOODS = BASE_URL + "search/suggestGoods";

    /**
     * 搜索用户下拉提示
     */
    public static final String QUERY_KEY_WORDS_USERS = BASE_URL + "search/suggestUsers";
    /**
     * VIP专区列表
     **/
    public static final String VIP = BASE_URL + "zone/v1/vip";
    /**
     * 明星专区列表
     **/
    public static final String STAR = BASE_URL + "zone/v1/star";
    /**
     * 全新专区列表
     **/
    public static final String BRANDNEW = BASE_URL + "zone/v1/brandnew";
    /**
     * 新版全新专区
     */
    public static final String FULLY_BRANDNEW = BASE_URL + "zone/v2/brandnew";
    /**
     * vip上新榜
     */
    public static final String NEW_VIPREGION = BASE_URL + "zone/v2/vipList";
    /**
     * 新明星列表
     */
    public static final String NEW_STRT_LIST = BASE_URL + "zone/v1/starList";
    /**
     * VIP列表
     */
    public static final String NEW_VIP_LIST = BASE_URL + "zone/v1/vipUserList";
    /**
     *
     */
    public static final String NEW_STAR = BASE_URL + "zone/v2/star";
    /**
     * s双十一
     **/
    public static final String DOUBLE_ELEVEN = BASE_URL + "";
    /**
     * 用户反馈
     **/
    public static final String USERFEEDBACK = BASE_URL + "user/v1/userFeedback";
    /**
     * kvs配置接口
     */
    public static final String INIT_KVSD = BASE_URL + "sys/v1/init";
    /**
     * 活动列表
     */
    public static final String CAMPAIGN_LIST = BASE_URL + "goods/v1/queryCampaignsGoodsPage";
    /**
     * 系统消息
     */
    public static final String GETSYSTEMNOTIFICATIONS = BASE_URL + "sys/v1/getSystemNotifications";
    /**
     * 上传推送deviceId
     */
    public static final String UPLOAD_PUSH_DEVICE = BASE_URL + "v1/updatePushToken";
    /**
     * 订单列表类接口
     */
    public static final String ORDERS = BASE_URL + "v1/orders";
    /**
     * 商品列表类接口
     */
    public static final String GOODS = BASE_URL + "v1/goods";
    /**
     * 编辑花语接口
     */
    public static final String SETNOTICE = BASE_URL + "user/v1/setNotice";
    /**
     * 预览花语接口
     */
    public static final String PREVIEWNOTICE = BASE_URL + "user/v1/previewNotice";
    /**
     * 首页推荐
     */
    public static final String RECOMMEND_LIST = BASE_URL + "v1/homeFeatured";
    /**
     * 城市地区
     */
    public static final String REGIONS = BASE_URL + "v1/regions";
    /**
     * VIP 明星推荐用户
     */
    public static final String RECOMMEND_USER_LIST = BASE_URL + "user/v1/suggested";
    /**
     * 服务器繁忙重试
     */
    public static final String RETRY = BASE_URL + "sys/v1/retry";
    /**
     * 会话界面获取商品信息
     */
    public static final String GETGOODS = BASE_URL + "chat/v1/getGoods";
    /**
     * 会话界面设置用户关系
     */
    public static final String SETGOODS = BASE_URL + "chat/v1/setGoods";
    /**
     * 聊天安全提醒
     */
    public static final String ONMESSAGERECEIVED = BASE_URL + "chat/v1/onMessageReceived";
    /**
     * 留言列表
     */
    public static final String COMMENTLIST = BASE_URL + "comment/v1/commentList";
    /**
     * 留言点赞
     */
    public static final String LIKE = BASE_URL + "like/v1/like";
    /**
     * 回复列表
     */
    public static final String REPLYLIST = BASE_URL + "comment/v2/replyList";
    /**
     * 消息留言列表
     */
    public static final String COMMENTSMSG = BASE_URL + "comment/v1/commentsMsg";
    /**
     * 删除评论
     */
    public static final String DEL_COMMENT = BASE_URL + "comment/v1/delComment";
    /**
     * 删除回复
     */
    public static final String DEL_REPLY = BASE_URL + "comment/v1/delReply";
    /**
     * 回复
     */
    public static final String REPLAY = BASE_URL + "comment/v1/reply";
    /**
     * 留言
     */
    public static final String COMMENT = BASE_URL + "comment/v1/comment";
    /**
     * 设置偏好设置
     */
    public static final String SETPREFERENCES = BASE_URL + "user/v1/setPreferences";

    public static final String GETPREFERENCES = BASE_URL + "user/v1/getPreferences";
    /**
     * 浇水
     */
    public static final String WATER = BASE_URL + "goods/v1/renewDisplayTime";
    public static final String WATER_ALL = BASE_URL + "goods/v1/batchWatering";
    /**
     * 参加活动列表
     */
    public static final String CANDIDATE_CAMPAIGNS = BASE_URL + "campaign/v1/getCandidateCampaigns";
    /**
     * 参加活动
     */
    public static final String ADD_GOODS = BASE_URL + "/campaign/v1/addGoods";
    /**
     * 上架
     */
    public static final String SHELVE_ALL = BASE_URL + "goods/v1/batchShelve";
    /**
     * 校验发布商品价格
     */
    public static final String VALIDATE_PRICE_FORM = BASE_URL + "goods/v1/validatePriceForm";

    public static final String ONE_SELL_TIME = BASE_URL + "zone/v1/oneSellTime";

    public static final String ONE_REGION = BASE_URL + "zone/v1/one";

    public static final String GET_STS_TOKEN = BASE_URL + "sys/v1/getStsToken";

    public static final String ORDER_HISTORY_LIST = BASE_URL + "order/v1/getOrderHistory";


    //public static final String VOLUMN_LIST = BASE_URL +  "vol/v1/list";
    public static final String VOLUMN_LIST = BASE_URL + "v1/schizo";
    public static final String HOME_LIST = BASE_URL + "v2/home";
    public static final String HOME_VIP = BASE_URL + "v1/home/vip";

    public static final String CAMPAIGN_URL = "campaign_url";
    public static final String COMMENT_LIST_URL = "comment_list_url";
    public static final String COMMENT_LIST_ID = "comment_list_id";
    public static final String CAMPAIGN_TITLE = "campaign_title";
    public static final String CAMPAIGN_STYLE = "campaign_style";
    public static final String CAMPAIGN_ID = "campaign_id";
    public static final String REQUEST_VER = "version";
    public static final String REQUEST_TOKEN = "token";
    public static final String REQUEST_PAGECOUNT = "pagecount";
    public static final String REQUEST_CURRPAGE = "currpage";
    public static final int TO_ADDRESS_EDIT = 1;
    public static final int TO_ADDRESS_ADD = 2;

    public static final String CLASSIFICATION_LAYOUT = "classification_layout_check";
    public static final String ONE_YUAN_LAYOUT = "one_yuan_layout";
    public static final String REGION_LAYOUT = "classification_layout_check";
    public static final String SEARCH_LIST_LAYOUT = "search_list_layout_check";
    public static final String EXTRA_ALBUM_LIST_FOLDERNAME = "extra_album_list_foldername";
    public static final String EXTRA_ALBUM_LIST_IMAGES = "extra_album_list_images";
    public static final String EXTRA_ALBUM_TYPE = "extra_album_type";
    public static final String EXTRA_IMAGES = "extra_images";
    public static final String EXTRA_CAN_DOWNLOAD = "extra_can_download";
    public static final String EXTRA_IMAGE_INDEX = "extra_image_index";
    public static final String EXTRA_REPORT_TYPE = "extra_report_type";
    public static final String EXTRA_GOODS_EDIT = "extra_goods_edit";
    public static final String EXTRA_GOODS_DETAIL = "extra_goods_detail";
    public static final String EXTRA_HPCOMMENT_DATA = "extra_hpcommentlist";
    public static final String EXTRA_HPREPLY_DATA = "extra_hpreplylist";
    public static final String EXTRA_GOODS_DETAIL_ID = "extra_goods_detail_id";
    public static final String EXTRA_GOODS_CONTENT = "EXTRA_GOODS_CONTENT";
    public static final String EXTRA_COMMENT_TARGET_ID = "extra_comment_target_id";
    public static final String EXTRA_COMMENT_TARGET_TYPE = "extra_comment_target_type";
    public static final String EXTRA_CLASS_DETAIL = "extra_class_detail";
    public static final String EXTRA_GOODS_DETAIL_FILTER_CLASS_FIRST = "extra_goods_detail_filter_class_first";
    public static final String EXTRA_GOODS_DETAIL_FILTER_CLASS_SECOND = "extra_goods_detail_filter_class_second";
    public static final String EXTRA_GOODS_DETAIL_FILTER_FIRST_CLASS_ID = "extra_goods_detail_filter_first_class_id";
    public static final String EXTRA_GOODS_DETAIL_FILTER_SECOND_CLASS_ID = "extra_goods_detail_filter_second_class_id";
    public static final String EXTRA_GOODS_EDIT_FROM = "extra_goods_edit_from";
    public static final String EXTRA_NOW_CITY = "extra_now_city";
    public static final String EXTRA_DISTRICT = "extra_district";
    public static final String EXTRA_SELECT_CITY = "extra_select_city";
    public static final String EXTRA_SELECT_PRICE = "extra_select_price";
    public static final String EXTRA_SELECT_PAST_PRICE = "extra_select_past_price";
    public static final String EXTRA_GOODS_NAME = "extra_goods_name";
    public static final String EXTRA_SELECT_IMG = "extra_select_img";
    public static final String EXTRA_SELECT_CITY_ID = "extra_select_city_id";
    public static final String EXTRA_POIITEM = "extra_poiitem";
    public static final String EXTRA_VILLAGE = "extra_village";
    public static final String EXTRA_MAPVIEW_LAT = "extra_mapview_lat";
    public static final String EXTRA_MAPVIEW_LNG = "extra_mapview_lng";
    public static final String EXTRA_DRAW_PHOTO_PATH = "extra_draw_photo_path";
    public static final String EXTRA_SELECT_BITMAP = "extra_select_bitmap";
    public static final String EXTRA_MEDIA = "extra_media";
    public static final String EXTRA_SHOW_PIC = "extra_show_pic";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_STATUS = "extra_status";
    public static final String EXTRA_FLAGID = "extra_flagid";
    public static final String EXTRA_RELEASE_PRONAME = "extra_release_proname";
    public static final String EXTRA_RELEASE_CONTENT = "extra_release_content";
    public static final String EXTRA_RELEASE_GOODS_BRAND = "extra_release_goods_brand";
    public static final String EXTRA_RELEASE_ISNEW = "extra_release_isnew";
    public static final String EXTRA_RELEASE_SECOND_CLASS_ID = "extra_release_second_class_id";
    public static final String EXTRA_RELEASE_FIRST_CLASS_ID = "extra_release_first_class_id";
    public static final String EXTRA_RELEASE_AGE_ID = "extra_release_age_id";
    public static final String EXTRA_RELEASE_RECODETIME = "extra_release_recodetime";
    public static final String EXTRA_CHOOSE_CITYNAME = "extra_choose_cityname";
    public static final String EXTRA_CHOOSE_CITYID = "extra_choose_cityid";
    public static final String EXTRA_CHOOSE_DISNAME = "extra_choose_disname";
    public static final String EXTRA_CHOOSE_DISID = "extra_choose_disid";
    public static final String EXTRA_CHOOSE_CLASS_GROUDID = "extra_choose_class_groudid";
    public static final String EXTRA_CHOOSE_AGES = "extra_choose_ages";
    public static final String EXTRA_CHOOSE_AGE = "extra_choose_age";
    public static final String EXTRA_CHOOSE_CLASS_CHILDID = "extra_choose_class_childid";
    public static final String EXTRA_CHOOSE_BRAND_ID = "extra_choose_brand_id";
    public static final String EXTRA_CHOOSE_BRAND_NAME = "extra_choose_brand_name";
    public static final String EXTRA_CHOOSE_CLASS_CHILDNAME = "extra_choose_class_childname";
    public static final String EXTRA_ADDRESS = "extra_address";
    public static final String EXTRA_ADDRESS_LIST = "extra_address_list";
    public static final String EXTRA_ADDRESS_TYPE = "extra_address_type";
    public static final String EXTRA_USER_INFO = "extra_user_info";
    public static final String EXTRA_BABY_LIST = "extra_baby_list";
    public static final String EXTRA_BABY = "extra_baby";
    public static final String EXTRA_BABY_POSITION = "extra_baby_position";
    public static final String EXTRA_BABY_EDIT_TYPE = "extra_baby_edit_type";
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_WECHAT_NAME = "extra_wechat_name";
    public static final String EXTRA_WECHAT_ICON = "extra_wechat_icon";
    public static final String EXTRA_WECHAT_SEX = "extra_wechat_sex";
    public static final String EXTRA_REGISTER_PHONE = "extra_register_phone";
    public static final String EXTRA_REGISTER_CODE = "extra_register_code";
    public static final String EXTRA_SEARCH_KEYWORD = "extra_search_keyword";
    public static final String EXTRA_WEBVIEW_URL = "extra_webview_url";
    public static final String EXTRA_WEBVIEW_TITLE = "extra_webview_title";
    public static final String EXTRA_ORDER_CONFIRM_BEAN = "extra_order_confirm_bean";
    public static final String EXTRA_ORDER_DETAIL_BEAN = "extra_order_detail_bean";
    public static final String EXTRA_ORDER_DETAIL_ID = "extra_order_detail_id";
    public static final String EXTRA_REFUND_ORDER_TYPE = "extra_refund_order_type";
    public static final String EXTRA_ORDER_DETAIL_IDS = "extra_order_detail_ids";
    public static final String EXTRA_ORDER_PRICE = "extra_order_price";
    public static final String EXTRA_ORDER_POSTAGE = "extra_order_postage";
    public static final String EXTRA_ORDER_FREEDELIVERY = "extra_order_freedelivery";
    public static final String EXTRA_ORDER_MESSAGE_ID = "extra_order_message_id";
    public static final String EXTRA_ORDER_DETAIL = "extra_order_detail";
    public static final String EXTRA_ORDER_DATA = "extra_order_data";
    public static final String EXTRA_GOODS_DATA = "extra_goods_data";
    public static final String EXTRA_REFUND_LOG_DATA_DETAIL = "extra_refund_log_data_detail";
    public static final String EXTRA_ORDER_ARBITRATION = "extra_order_arbitration";
    public static final String EXTRA_ORDER_REFUND_TYPE = "extra_order_refund_type";
    public static final String EXTRA_ORDER_REFUND_PRICE = "extra_order_refund_money";
    public static final String EXTRA_ORDER_REFUND_POST_AGE = "extra_order_refund_post_age";
    public static final String EXTRA_ADDRESS_FOR_ORDER = "extra_address_for_order";
    public static final String EXTRA_EXPRESS = "extra_express";
    public static final String EXTRA_ORDER_REFUND_FROM = "extra_order_refund_from";
    public static final String EXTRA_ORDER_REFUND_ID = "extra_order_refund_id";
    public static final String EXTRA_ORDER_TARGET = "extra_order_target";
    public static final String EXTRA_ORDER_TIME = "extra_order_time";
    public static final String EXTRA_ORDERS_ROLE = "extra_orders_role";
    public static final String EXTRA_ORDERS_IS_FIRST_LOAD = "extra_orders_is_first_load";
    public static final String EXTRA_ORDERS_IS_FIRST_LOAD_ARTICLE = "extra_orders_is_first_load_article";
    public static final String EXTRA_ORDERS_STATUS = "extra_orders_status";
    public static final String EXTRA_ORDERS_STATE = "extra_orders_state";
    public static final String EXTRA_TO_GALLERY_FROM = "extra_to_gallery_from_re";
    public static final String EXTRA_ALBUM_FROM_MAIN = "1"; // 来自主页
    public static final String EXTRA_ALBUM_FROM_ARTICLE = "EXTRA_ALBUM_FROM_ARTICLE"; // 花语专用
    public static final String EXTRA_COMMENT_ID = "extra_comment_id";
    public static final String EXTRA_FROM_WHERE = "extra_from_where";
    public static final String EXTRA_FROM_USER_ID = "EXTRA_FROM_USER_ID";
    public static final String EXTRA_FROM_GOODS_DETAILS = "extra_from_goods_details";
    public static final String EXTRA_FROM_ORDER = "order";  // 来自订单
    public static final String EXTRA_FROM_HOME = "home";  // 来自主页
    public static final String EXTRA_NOTICE = "extra_notice";
    public static final String EXTRA_CREDIT = "extra_credit";
    public static final String EXTRA_NOTICE_TEXT = "extra_notice_text";
    public static final String EXTRA_JUMP_TYPE = "extra_jump_type";
    public static final String EXTRA_MAIN_JUMP = "extra_main_jump"; // 1. 跳转到会话列表
    public static final String EXTRA_GET_CREDIT_SCORE = "get_credit_score";
    public static final String EXTRA_NEW_PHONE_NUM = "new_phone_number";
    public static final String EXTRA_PHONE_NUMBER = "phone_num";
    public static final String EXTRA_CAMPAIGN_BEAN = "campaign_bean";
    public static final String EXTRA_CAMPAIGN_ID = "extra_campaign_id";
    public static final String EXTRA_CAT2_ID = "extra_cat2_id";
    public static final String EXTRA_CAT2_NAME = "extra_cat2_name";
    public static final String EXTRA_MAP = "push_message_extra_map";
    public static final String EXTRA_LEAN_CLOUD_DATA = "com.avos.avoscloud.Data";
    public static final String GET_CREDIT_SCORE_VALUE = "get_credit_score_over";
    public static final String MAIN_GUIDE_TIPS = "main_guide_tips";
    public static final String MINE_GUIDE_TIPS = "mine_guide_tips";
    public static final String CLASS_GUIDE_TIPS = "class_guide_tips";
    public static final String EXTRA_NOTICES = "extra_notices";
    public static final String EXTRA_CITIES = "extra_cities";
    public static final String EXTRA_CITY = "extra_city";
    public static final String EXTRA_DISTRICTS = "extra_districts";
    public static final String EXTRA_REGION = "extra_region";
    public static final String EXTRA_AUDIT_STATUS = "extra_audit_status";
    public static final String EXTRA_MONTAGE = "extra_montage";
    public static final String EXTRA_PHOTO_DELETE = "extra_photo_delete";
    public static final String EXTRA_SELECT_WHICH = "extra_select_which";
    public static final String EXTRA_TARGET_TIP = "extra_target_tip";
    public static final String EXTRA_TARGET_USER_LEVEL = "userLevel";
    public static final String EXTRA_ORDER_MEMO_EDIT = "extra_remark_edit";
    public static final String MODIFY_COURIER_NUMBER = "extra_modify_courier_number";
    public static final String EXTRA_CAN_MODIFY_REFUND = "extra_can_modify_refund";
    public static final String EXTRA_ADD_BRAND = "extra_add_brand";
    public static final String INPUT_SECTION_CONTENT = "input_section_content";
    public static final String SECTION = "section";
    public static final String COVER_LOCAL_PATH = "cover_local_path";
    public static final String ARTICLE_TAG = "article_tag";
    public static final String LOCATION_X_Y = "location_x_y";
    public static final String EXTRA_MOMENT_HINT = "MOMENT_HINT";

    public static final String POSITION = "position";
    public static final String REC_TRAC_ID = "recTraceId";
    /**
     * 相册选择最多数量
     */
    public static final int MATCH_SELECT_DYNAMIC_PHOTO = 8;
    public static final String USER_NAME = "user_name";
    public static final int INTENT_FOR_RESULT_ALBUM_TO_GALLERY = 1000;
    public static final int INTENT_FOR_RESULT_ALBUM_TO_ALBUMLIST = 1001;
    public static final int INTENT_FOR_RESULT_MAIN_TO_CAMERA = 1002;
    public static final int INTENT_FOR_RESULT_SHOWEDIT_TO_ALBUM = 1003;
    public static final int INTENT_FOR_RESULT_RELEASETHIRD_TO_CHOOSECITY = 1004;
    public static final int INTENT_FOR_RESULT_RELEASETHIRD_TO_CHOOSEDISTRICT = 1005;
    public static final int INTENT_FOR_RESULT_RELEASETHIRD_TO_CHOOSEVILLAGE = 1006;
    public static final int INTENT_FOR_RESULT_GALLERY_TO_DRAWPHOTO = 1007;
    public static final int INTENT_FOR_RESULT_RELEASESECOND_TO_PROCLASS = 1008;
    public static final int INTENT_FOR_RESULT_GALLERY_TO_CLIPPHOTO = 1009;
    public static final int INTENT_FOR_RESULT_RELEASESECOND_TO_BABYAGE = 1010;
    public static final int INTENT_FOR_RESULT_RELEASESECOND_TO_BRAND = 1011;
    public static final int INTENT_FOR_RESULT_COMMENT_TO_ALBUM = 1012;
    public static final int INTENT_FOR_RESULT_HOME_TO_POINT = 1012;
    public static final int INTENT_FOR_RESULT_TO_ALBUM = 1013;
    public static final int INTENT_FOR_RESULT_TO_CROP = 1014;
    public static final int INTENT_FOR_RESULT_TO_ADDRESS_EDIT = 1015;
    public static final int INTENT_FOR_RESULT_TO_CHOOSE_CITY = 1016;
    public static final int INTENT_FOR_RESULT_TO_SEARCH = 1017;
    public static final int INTENT_FOR_RESULT_TO_ADDRESS = 1018;
    public static final int INTENT_FOR_RESULT_TO_ORDER_CHANGE = 1019;
    public static final int INTENT_FOR_RESULT_TO_EDIT_TEXT_NOTICE = 1020;
    public static final int INTENT_FOR_RESULT_TO_EDIT_CHILD = 1021;
    public static final int INTENT_FOR_RESULT_TO_MY_CHILD = 1022;
    public static final int FROM_FLAGS_PERSONALDATAACTIVITY = 1051;
    public final static int REQUEST_CODE_FOR_ZM_CREDIT = 0x1234;
    public static final int REQUEST_CODE_FOR_REFRESH = 0x1232;
    public final static int REQUEST_CODE_FOR_WEB = REQUEST_CODE_FOR_ZM_CREDIT + 1;//跳转到web code
    public final static int REQUEST_CODE_FOR_VIDEO = 0x555;//视频业务跳转code
    public final static int REQUEST_CODE_FOR_NOTICE = 1023;//跳转到花语
    public final static int INTENT_FOR_RESULT_TO_CITIES = 1024;
    public final static int INTENT_FOR_RESULT_TO_DISTRICTS = 1025;
    public final static int INTENT_FOR_RESULT_TO_PROVINCE = 1026;
    public static final int INTENT_FOR_RESULT_TO_ORDER_MEMO_EDIT = 1027;
    public static final int REQUEST_CODE_COMMENT = 1028;
    public static final int EDIT_ARTICLE_SECTION_CONTENT = 1029;
    public static final int REQUEST_CODE_FOR_SWAP_SECTIONS = 1030;
    public static final int REQUEST_CODE_FOR_ARTICLE_SECTIONS = REQUEST_CODE_FOR_SWAP_SECTIONS + 1;
    public static final int REQUEST_CODE_FOR_PICK_CATEGORY = REQUEST_CODE_FOR_ARTICLE_SECTIONS + 1;
    public static final int REQUEST_CODE_FOR_TAG = REQUEST_CODE_FOR_PICK_CATEGORY + 1;
    public static final int REQUEST_CODE_FOR_ORDER_CONFIRM = REQUEST_CODE_FOR_TAG + 1;
    public static final int EXTRA_DESPOCIT = REQUEST_CODE_FOR_ORDER_CONFIRM + 1;
    public static final int REQUEST_CODE_FOR_SEARCH = 0x123;
    public static final int REQUEST_CODE_FOR_FLOWER = 0x456;
    public static final String BRAND_RESULT = "check_brand";
    public static final String QR_CODE = "qr_code";
    public static final String SHARE_TARGET_TYPE = "targetType";
    public static final String SHARE_TARGET_ID = "targetId";
    public static final String SHARE_GOODS = "0";
    public static final String SHARE_USER = "1";
    public static final String APP_LINK_USER = "u";
    public static final String APP_LINK_GOODS = "g";
    public static final String APP_LINK_MOMENTS = "moments";
    public static final String APP_LINK_ARTICLES = "articles";
    public static final String APP_LINK_FPOEMS = "fpoems";
    public static final String EXTRA_APP_LEVEL = "extra_applevel";
    public static final String EXTRA_APP_URL = "extra_app_url";
    public static final String EXTRA_APP_CONTENT = "extra_app_content";
    public static final String EXTRA_START_MAIN = "extra_start_main";
    public static final String TYPE = "type";
    public static final String EXTRA_OPTION = "extra_option";
    public static final int P_EDIT = 1;
    public static final int P_DELETE = 2;
    public static final int P_DOWN = 4;

    public static final String GOODS_DETAILS_EDIT = "goods_details_edit";
    public static final String ORDER_DETAILS = "order_details";
    public static final int COUNT_FANS = 99999;
    public static final int COUNT_COMMENT = 10000;
    public static final String SECTION_LIST = "section_list";
    public static final String FINISH_ACTIVITY = "finish_activity";
    public static final String ARTICLE_ID = "article_id";
    public static final String COMMENTABLE = "commentAble";
    /**
     * 发布简易花语
     */
    public static final String SEND_EASY_ARTICLE = BASE_URL + "/moment/v1/save";
    /**
     * 简单花语详情
     */
    public static final String EASY_ARTICLE_DETAIL = BASE_URL + "/moment/v1/detail";
    public static final String ARTICLE_DATA = "article_data";
    public static final String MAX_ALBUM_COUNT = "max_album_count";
    /**
     *
     */
    public static final String ARTICLE_CATEGORY = BASE_URL + "/article/v1/getCats";
    /**
     * 简易花语收藏
     */
    public static final String MOMENT_SAVE = BASE_URL + "collection/v1/collect";

    public static final String PAST_ONE_YUAN = BASE_URL + "zone/v1/getOneEvents";
    public final static String HTTP = "http://";
    public final static String HTTPS = "https://";
    public final static String FILE = "file://";
    public final static String CONTENT = "content://";
    public final static String RES = "res:///";
    public final static String Drawable = "drawable://";
    //NONE
    public final static float[] COLOR_MATRIX_NONE = new float[0];
    //LOMO
    public final static float[] COLOR_MATRIX_LOMO = {
            1.7f, 0.1f, 0.1f, 0, -73.1f,
            0, 1.7f, 0.1f, 0, -73.1f,
            0, 0.1f, 1.6f, 0, -73.1f,
            0, 0, 0, 1.0f, 0};
    //黑白
    public final static float[] COLOR_MATRIX_BLACK_OR_WHITE = {
            0.8f, 1.6f, 0.2f, 0, -163.9f,
            0.8f, 1.6f, 0.2f, 0, -163.9f,
            0.8f, 1.6f, 0.2f, 0, -163.9f,
            0, 0, 0, 1.0f, 0};
    //复古
    public final static float[] COLOR_MATRIX_FUGU = {
            0.2f, 0.5f, 0.1f, 0, 40.8f,
            0.2f, 0.5f, 0.1f, 0, 40.8f,
            0.2f, 0.5f, 0.1f, 0, 40.8f,
            0, 0, 0, 1, 0};
    //哥特
    public final static float[] COLOR_MATRIX_GETE = {
            1.9f, -0.3f, -0.2f, 0, -87.0f,
            -0.2f, 1.7f, -0.1f, 0, -87.0f,
            -0.1f, -0.6f, 2.0f, 0, -87.0f,
            0, 0, 0, 1.0f, 0};
    //锐化
    public final static float[] COLOR_MATRIX_RUIHUA = {
            4.8f, -1.0f, -0.1f, 0, -388.4f,
            -0.5f, 4.4f, -0.1f, 0, -388.4f,
            -0.5f, -1.0f, 5.2f, 0, -388.4f,
            0, 0, 0, 1.0f, 0};
    //淡雅
    public final static float[] COLOR_MATRIX_DANYA = {
            0.6f, 0.3f, 0.1f, 0, 73.3f,
            0.2f, 0.7f, 0.1f, 0, 73.3f,
            0.2f, 0.3f, 0.4f, 0, 73.3f,
            0, 0, 0, 1.0f, 0};
    //酒红
    public final static float[] COLOR_MATRIX_JIUHONG = {
            1.2f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.9f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.8f, 0.0f, 0.0f,
            0, 0, 0, 1.0f, 0};
    //清宁
    public final static float[] COLOR_MATRIX_QINGNING = {
            0.9f, 0, 0, 0, 0,
            0, 1.1f, 0, 0, 0,
            0, 0, 0.9f, 0, 0,
            0, 0, 0, 1.0f, 0};
    //浪漫
    public final static float[] COLOR_MATRIX_ROMANTIC = {
            0.9f, 0, 0, 0, 63.0f,
            0, 0.9f, 0, 0, 63.0f,
            0, 0, 0.9f, 0, 63.0f,
            0, 0, 0, 1.0f, 0};
    //光晕
    public final static float[] COLOR_MATRIX_GUANGYUN = {
            0.9f, 0, 0, 0, 74.9f,
            0, 0.9f, 0, 0, 74.9f,
            0, 0, 0.9f, 0, 74.9f,
            0, 0, 0, 1.0f, 0};
    //蓝调
    public final static float[] COLOR_MATRIX_BLUES = {
            2.1f, -1.4f, 0.6f, 0.0f, -31.0f,
            -0.3f, 2.0f, -0.3f, 0.0f, -31.0f,
            -1.1f, -0.2f, 2.6f, 0.0f, -31.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    };
    //梦幻
    public final static float[] COLOR_MATRIX_DREAM = {
            0.8f, 0.3f, 0.1f, 0.0f, 46.5f,
            0.1f, 0.9f, 0.0f, 0.0f, 46.5f,
            0.1f, 0.3f, 0.7f, 0.0f, 46.5f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    };
    //夜色
    public final static float[] COLOR_MATRIX_YESE = {
            1.0f, 0.0f, 0.0f, 0.0f, -66.6f,
            0.0f, 1.1f, 0.0f, 0.0f, -66.6f,
            0.0f, 0.0f, 1.0f, 0.0f, -66.6f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    };
    //胶片
    public final static float[] COLOR_MATRIX_JIAOPIAN =
            {0.71f, 0.2f, 0.0f, 0.0f, 60.0f,
                    0.0f, 0.94f, 0.0f, 0.0f, 60.0f,
                    0.0f, 0.0f, 0.62f, 0.0f, 60.0f,
                    0, 0, 0, 1.0f, 0};
    //传统
    public final static float[] COLOR_MATRIX_CHUANTONG =
            {1.0f, 0.0f, 0.0f, 0, -10.0f,
                    0.0f, 1.0f, 0.0f, 0, -10.0f,
                    0.0f, 0.0f, 1.0f, 0, -10.0f,
                    0, 0, 0, 1, 0};
    /**
     * 1元专区历史
     */
    public static final String PAST_RANKING = "https://i.huafer.cc/one-yuan-zone/hot-item-ladder/";
    public static final String COVER = "is_article_cover";
    public static final String POI_RESULT = "poi_result";
    public static final String TAG_ELSE = "tag_else";
    public static final String CHOOSE_ADDRESS_KEY = "choose_address_key";
    public static final String SEARCH_QUERY = "searchQuery";
    public static final String IS_AUCTION = "is_auction";
    public static final String PRICE = "price";
    public static final String PRICE_STYLE = "price_style";
    public static final String GOODS_STATE = "goods_state";
    public static final String DRAFT_TYPE = "draft_type";
    public static final String EXTRA_SELECT_DISTRICT_ID = "district_id";


    /**
     * 收藏,取消收藏
     */
    public static String COLLECT = BASE_URL + "collection/v1/collect";
    /**
     * 我的花语列表
     */
    public static String GETLIST = BASE_URL + "article/v1/getList";
    /**
     * LogUtil日志打印开关：true：打印日志，false：关闭日志
     **/
    public static boolean printLog = Config.DEBUG;
    /**
     * 分享商品详情链接
     */
    public static String SHARE_GOODS_DETAIL = "https://i.huafer.cc/g/";
    /**
     * 分享店铺首页链接
     */
    public static String SHARE_PERSONAL_HOME = "https://i.huafer.cc/u/";
    // 我的花语分享-图文
    public static final String SHARE_ARTICLE = "http://i.huafer.cc/articles/";
    // 我的花语分享-简易
    public static final String SHARE_MOMENT = "http://i.huafer.cc/moments/";
    public static final String SHARE_ARTICLE_FLOWER = "http://i.huafer.cc/fpoems/";
    /**
     * 帮助中心
     */
//	public static String WEBVIEW_HELP_CENTER = "http://www.huafer.cc/help/helpCenter.html";
    public static String WEBVIEW_HELP_CENTER = "http://www.huafer.cc/help-center/index.html";
    /**
     * 发布技巧
     */
    public static String WEBVIEW_RELEASE_SKILL = "http://www.huafer.cc/help-center/goods/publish-tip.html ";
    /**
     * 注册条款
     */
    public static String WEBVIEW_ZHUCE = "http://huafer.cc/help/zhuCe.html";
    /**
     * 查看新浪微博
     */
    public static String WEBVIEW_SINAWEIBO = "http://weibo.com/";
    /**
     * 定价攻略
     */
    public static String PEICESINGMISC = "http://www.huafer.cc/misc/tip-of-pricing/index.html";
    /**
     * 注册条款
     */
    public static String WEB_VIEW_CONVERSATION_NOTICE = "http://www.huafer.cc/help-center/fraud.html";
    /**
     * 联系我们
     */
    public static String CONTACTUS = "http://www.huafer.cc/help/contactUs.html";
    /**
     * 信用金说明
     */
    public static String XINYONGJIN = "http://www.huafer.cc/help/xinYongJinBaoZhengXieYI.html";
    /**
     * 退款帮助
     */
    public static String TUIKUAN_HELP = "http://www.huafer.cc/help/tuiKuanHelp.html";
    /**
     * 聊天温馨提示
     */
    public static String CHAT_TIPS = "http://www.huafer.cc/help/chatTips.html";
    /**
     * 什么是芝麻信用
     */
    public static String WHAT_ZM = "http://www.huafer.cc/banner/banner08.html";
    /**
     * 钱包界面，赚到和节省
     */
    public static String EARN_AND_SAVE = "http://www.huafer.cc/help-center/wallet/profit.html";
    /**
     * 钱包界面，钱款去哪儿了
     */
    public static String WHERE_CASH = "http://www.huafer.cc/help-center/wallet/where.html";
    /**
     * 商品详情担保
     */
    public static String GUARANTEE = "http://www.huafer.cc/help-center/guarantee.html";

    public static String UNARY_RULE_WITHOUT_BTN = "https://i.huafer.cc/misc/one-yuan-rules-v2";

    public static String UNARY_RULE = "https://i.huafer.cc/misc/one-yuan-rules";

    public static String BID_RECORD_RULE = "https://i.huafer.cc/misc/bid-rules";

    public static String BID_DESPOSIT_RULE = "https://i.huafer.cc/misc/security-deposit/";

    public static String CAMPAIGN_RULE = "https://i.huafer.cc/misc/activity-rules-";

    public static String BID_RULE_WITHOUT_TITLE = "https://i.huafer.cc/misc/security-deposit-v2/";

    /**
     * 配置同步接口
     */
    public static String SYNCCONFIG = BASE_URL + "sysn/v1/sysnConfig";
    /**
     * 首页分类页
     */
    public static String GETGOODSCATES = BASE_URL + "goodsCate/v1/getGoodsCates";
    /**
     * 商品详情
     */
    public static String GETGOODSDETAIL = BASE_URL + "goods/v2/getGoodsDetail";
    /**
     * 编辑商品时获取商品详情
     */
    public static String GETGOODSSIMPLEINFO = BASE_URL + "goods/v1/getGoodsSimpleInfo";
    /**
     * 发布页获取草稿信息
     */
    public static String GET_DRAFT_BOX = BASE_URL + "draft/v1/getDraft";
    /**
     * 用户登录
     */
    public static String USERLOGIN = BASE_URL + "user/v3/userLogin";
    /**
     * 获取手机验证码
     */
    public static String GETCHECKCODE = BASE_URL + "sms/v1/getCheckCode";
    /**
     * 验证手机验证码
     */
    public static String CHECKCODE = BASE_URL + "sms/v1/checkCode";
    /**
     * 验证用户昵称是否重复
     */
    public static String CHECKNICKNAME = BASE_URL + "user/v1/checkNickName";
    /**
     * 注册
     */
    public static String REGISTER = BASE_URL + "user/v1/register";
    /**
     * 获取融云token
     */
    public static String GETRONGCLOUDTOKEN = BASE_URL + "user/v1/getRongCloudToken";
    /**
     * 搜索
     */
    public static String SEARCHGOODSORUSER = BASE_URL + "search/v1/searchGoodsOrUser";
    /**
     * 搜索since1.7.0
     */
    public static String SEARCH_GOODS_LIST = BASE_URL + "search/v1/goods";
    /**
     * 附近
     */
    public static String HOMENEARBY = BASE_URL + "search/v1/homeNearby";
    /**
     * 用户搜索
     */
    public static String SEARCH_USERS_LIST = BASE_URL + "search/v1/searchUser";
    /**
     * 粉丝列表
     */
    public static String FOLLOWERS = BASE_URL + "user/v2/getFollowers";
    /**
     * 关注列表
     */
    public static String FOLLOWING_LIST = BASE_URL + "user/v2/getFollowings";
    /**
     * 关注列表
     */
    public static String PAGES = BASE_URL + "v1/page";
    /**
     * 已关注用户在售商品
     **/
    public static String FOLLOWING_GOODS = BASE_URL + "user/v1/getFollowingGoods";
    /**
     * 已关注用户的花语列表
     */
    public static String FOLLOWING_POEMS = BASE_URL + "user/v1/getFollowingPoems";
    /**
     * 首页关注
     **/
    public static String FOLLOWINGLIST = BASE_URL + "user/v1/followingList";
    /**
     * 获取个人资料
     */
    public static String GETUSERINFO = BASE_URL + "user/v1/getUserInfo";
    /**
     * 商品发布
     */
    public static String SAVEGOODSINFO = BASE_URL + "goods/v2/saveGoodsInfo";
    /**
     * 发布页保存草稿
     */
    public static String SAVE_DRAFT_BOX = BASE_URL + "draft/v1/save";
    /**
     * 发布页删除草稿
     */
    public static String DEL_DRAFT_BOX = BASE_URL + "draft/v1/delDraft";
    /**
     * 商品一级分类
     */
    public static String GETGOODSFIRSTCATE = BASE_URL + "goodsCate/v1/getGoodsFirstCate";
    /**
     * 商品二级分类
     */
    public static String GETGOODSSECONDCATES = BASE_URL + "goodsCate/v1/getGoodsSecondCates";
    /**
     * 宝宝年龄段
     */
    public static String GETAGELIST = BASE_URL + "age/v2/getAgeList";
    /**
     * 城市列表
     */
    public static String GETCITYS = BASE_URL + "city/v1/getCitys";
    /**
     * 区域列表
     */
    public static String GETAREAS = BASE_URL + "area/v1/getAreas";
    /**
     * 收货地址列表
     */
    public static String GETUSERCONSIGNEEINFO = BASE_URL + "userConsigneeInfo/v1/getUserConsigneeInfo";
    /**
     * 添加或修改收货地址
     */
    public static String SAVEUSERCONSIGNEEINFO = BASE_URL + "userConsigneeInfo/v1/saveUserConsigneeInfo";
    /**
     * 设置默认收获地址
     */
    public static String SETDEFAULT = BASE_URL + "userConsigneeInfo/v1/setDefault";
    /**
     * 获取店铺详情
     */
    public static String GETUSERSHOPDETAIL = BASE_URL + "user/v2/getUserShopDetail";
    /**
     * 获取店铺详情
     */
    public static String GETUSERSHOPDETAIL_02 = BASE_URL + "user/v2/getUserShopDetail";
    /**
     * 获取我喜欢的列表
     */
    public static String GETUSERWANTLIST = BASE_URL + "goods/v1/getUserWantList";
    //    public static String I_LIKES = BASE_URL + "goods/v1/getUserLiking";
    public static String I_LIKES = BASE_URL + "collection/v1/getList";
    /**
     * 我发布的 （ 1 在售  2 下架  3 已卖出  4  删除）
     */
    public static String GETUSERRELEASEGOODSPAGELIST = BASE_URL + "goods/v1/getUserReleaseGoodsPageList";
    /**
     * 我卖出的 （1 交易中 2已卖出）
     */
    public static String GETUSERSELLGOODSPAGELIST = BASE_URL + "goods/v1/getUserSellGoodsPageList";
    /**
     * 我买到的（1 交易中 2已卖出）
     */
    public static String GETUSERBUYGOODSPAGELIST = BASE_URL + "goods/v1/getUserBuyGoodsPageList";
    /**
     * 获取用户关注的店铺列表接口(关注的人)
     */
    public static String GETCONCERNEDPAGELIST = BASE_URL + "user/v1/getConcernedPageList";
    /**
     * 订单消息列表
     */
    public static String GETORDERLOG = BASE_URL + "order/v2/getOrderLog";
    /**
     * 关注/取消关注用户
     */
    public static String CONCERNEDUSER = BASE_URL + "user/v1/concernedUser";
    /**
     * 关注/取消关注商品
     */
    public static String WANTBUY = BASE_URL + "goods/v1/wantBuy";
    /**
     * 获取粉丝列表
     */
    public static String GETUSERFANSPAGELIST = BASE_URL + "user/v1/getUserFansPageList";
    /**
     * 商品状态修改接口(1下架 2删除)
     */
    public static String CHANGGOODSSTATE = BASE_URL + "goods/v1/changGoodsState";
    /**
     * 获取投诉理由接口
     */
    public static String GETREPORTREASON = BASE_URL + "reportReason/v1/getReportReason";
    /**
     * 评价标签
     */
    public static String GETESTIMATETAG = BASE_URL + "estimateTag/v1/getEstimateTag";
    /**
     * 融云获取用户头像专用
     */
    public static String GETUSERICON = BASE_URL + "user/v1/getUserIcon";
    /**
     * 屏蔽&取消屏蔽用户接口
     */
    public static String SHIELDUSER = BASE_URL + "user/v1/shieldUser";
    /**
     * 获取已屏蔽人接口
     */
    public static String GETSHIELDPAGELIST = BASE_URL + "user/v1/getShieldPageList";
    /**
     * 首页广告
     */
    public static String BANNER = BASE_URL + "sysn/v1/banner";
    /**
     * 修改用户资料
     */
    public static String UPDATEUSER = BASE_URL + "user/v1/updateUser";
    /**
     * 投诉
     */
    public static String SAVEREPORTINFO = BASE_URL + "report/v1/saveReportInfo";
    /**
     * 仲裁
     */
    public static String SAVEREPORTINFO_V2 = BASE_URL + "report/v2/saveReportInfo";
    /**
     * 仲裁举证
     */
    public static String SAVEARBITRATIONPROOF = BASE_URL + "report/v1/saveArbitrationProof";
    /**
     * 退出登录
     */
    public static String USERLOGOFF = BASE_URL + "user/v1/userLogOff";
    /**
     * 购买（详情点击马上买）
     */
    public static String ORDERBUY = BASE_URL + "order/v1/orderBuy";
    /**
     * 创建订单
     */
    public static String ORDERCREATE = BASE_URL + "order/v1/orderCreate";
    /**
     * 获取支付凭证（购买）
     */
    public static String ORDERPAY = BASE_URL + "pay/v2/orderPay";
    /**
     * 取消订单
     */
    public static String ORDERCANCEL = BASE_URL + "order/v1/orderCancel";
    /**
     * 支付成功通知服务器
     */
    public static String PAYSUCCESS = BASE_URL + "pay/v2/paySuccess";
    /**
     * 获取订单信息
     */
    public static String GETORDERINFO = BASE_URL + "order/v1/getOrderInfo";
    /**
     * 买家提醒发货
     */
    public static String NOTICESHIP = BASE_URL + "order/v1/noticeShip";
    /**
     * 修改订单备注
     */
    public static String CHANGEORDERMEMO = BASE_URL + "order/v1/changeOrderMemo";
    /**
     * 获取退款理由标签
     */
    public static String GETREFUNDREASONLIST = BASE_URL + "refundReason/v1/getRefundReasonList";
    /**
     * 延长收货
     */
    public static String DELAYDELIVERY = BASE_URL + "order/v1/delayDelivery";
    /**
     * 确认收货
     */
    public static String ORDERDELIVERY = BASE_URL + "order/v2/orderDelivery";
    /**
     * 提醒买家收货
     */
    public static String NOTICEDELIVERY = BASE_URL + "order/v1/noticeDelivery";
    /**
     * 申请退款接口
     */
    public static String APPLYREFUND = BASE_URL + "refund/v1/applyRefund";
    /**
     * 发货接口
     */
    public static String ORDERSHIP = BASE_URL + "order/v1/orderShip";
    /**
     * 获取物流公司列表
     */
    public static String GETEXPRESSCOMPANY = BASE_URL + "expressCompany/v1/getExpressCompany";
    /**
     * 修改订单价格
     */
    public static String CHANGEORDERPRICE = BASE_URL + "order/v1/changeOrderPrice";
    /**
     * 取消退款申请
     */
    public static String CANCLEREFUND = BASE_URL + "refund/v1/cancleRefund";
    /**
     * 获取退款历史
     */
    public static String GETREFUNDLOGLIST = BASE_URL + "refund/v1/getRefundLogList";
    /**
     * 拒绝退款
     */
    public static String REFUSEREFUND = BASE_URL + "refund/v1/refuseRefund";
    /**
     * 同意退款
     */
    public static String AGREEREFUND = BASE_URL + "refund/v1/agreeRefund";
    /**
     * 买家退货
     */
    public static String RETURNGOODS = BASE_URL + "refund/v1/returnGoods";
    /**
     * 获取物流信息
     */
    public static String GETEXPRESSINFO = BASE_URL + "express/v2/getExpressInfo";
    /**
     * 获取卖家拒绝退款理由
     */
    public static String GETREFUSEREASO = BASE_URL + "reportReason/v1/getRefuseReason";
    /**
     * 卖家确认收货后退款接口
     */
    public static String RECEIPTGOODSANDREFUND = BASE_URL + "refund/v1/receiptGoodsAndRefund";
    /**
     * 对订单评论
     */
    public static String ESTIMATEORDER = BASE_URL + "order/v1/estimateOrder";
    /**
     * 对订单评论 1.6.0
     */
    public static String RATEORDER = BASE_URL + "v2/rateOrder";
    /**
     * 获取用户所有评价
     */
    public static String GETUSERORDERESTIMATE = BASE_URL + "order/v5/getOrderRates";
    /**
     * 获取订单评价详情
     */
    public static String GET_ORDER_COMMENTS_DETAIL = BASE_URL + "order/v5/getOrderRate";
    /**
     * 获取订单所有评价
     */
    public static String GETORDERESTIMATE = BASE_URL + "user/v1/getOrderEstimate";
    public static String GOODS_LIKE_LIST = BASE_URL + "goods/v1/getLikes";
    /**
     * 个人店铺闲置
     */
    public static String USER_SELLING = BASE_URL + "goods/v1/getUserSelling";
    /**
     * 个人店铺售出
     */
    public static String USER_SOLD = BASE_URL + "goods/v1/getUserSold";
    /**
     * 钱包首页
     */
    public static String WALLET_HOME = BASE_URL + "wallet/v1/home";
    /**
     * 获取用户所有评价标签
     */
    public static String GETUSERALLCOMMENTSLABEL = BASE_URL + "user/v1/getUserAllCommentsLabel";
    /**
     * 获取用评价详情
     */
    public static String GETCOMMENTSDETAIL = BASE_URL + "goods/v1/getCommentsDetail";
    /**
     * 编辑公告
     */
    public static String EDITUSERSHOPNOTICE = BASE_URL + "user/v1/editUserShopNotice";
    /**
     * 取消仲裁
     */
    public static String CANCLEAABITRATION = BASE_URL + "report/v2/cancleaAbitration";
    /**
     * 信用金获取凭证（购买）
     */
    public static String CREDITPAY = BASE_URL + "pay/v1/creditPay";
    /**
     * 信用金支付成功后通知服务器
     */
    public static String CREDITPAYSUCCESS = BASE_URL + "pay/v1/creditPaySuccess";
    /**
     * 解冻信用金
     */
    public static String THAWCREDITMONEY = BASE_URL + "user/v1/thawCreditMoney";
    /**
     * 获取用户手机号
     */
    public static String GETUSERPHONE = BASE_URL + "user/v1/getUserPhone";
    /**
     * 是否能解除信用金
     */
    public static String CANUNBIND = BASE_URL + "user/v1/canUnbind";
    /**
     * 支付宝授权登录请求加签
     */
    public static String ALIPAY_AUTH_REQUEST_SIGN = BASE_URL + "user/v1/getAliLoginSign";
    /**
     * 获取物流公司
     */
    public static String GETEXPRESSNAME = BASE_URL + "express/v1/recognizeNumber";
    /**
     * 批量发货列表
     **/
    public static String ORDER_JOINT_LIST = BASE_URL + "order/v1/batches";
    /**
     * 批量发货
     **/
    public static String BATCH_DELIVERY = BASE_URL + "order/v1/batchShip";
    /**
     * 全部已读
     **/
    public static String BATCH_ORDER_READ = BASE_URL + "order/v1/batchOpenUserMessage";
    /**
     * 修改快递单号卖家
     **/
    public static String MODIFY_COURIER_NUMBER_URL = BASE_URL + "order/v1/updateOrderExoress";
    /**
     * 请求品牌列表
     **/
    public static String GET_BRANDS = BASE_URL + "brand/v1/getBrands";


    public static String BRAND_SUGGEST_BY_KEYWORD = BASE_URL + "v1/brands/suggest";
    /**
     * 请求运动季活动分类列表
     **/
    public static String GET_SPORT_SEASON_CLASS_LIST = BASE_URL + "goodsCate/v1/getCampaignCats";
    public static String ARTICLE_SAVE = BASE_URL + "article/v1/publish";
    public static String ARTICLE_DETAIL = BASE_URL + "article/v1/detail";
    public static String DELETE_ADDRESS = BASE_URL + "userConsigneeInfo/v1/deleteUserConsigneeInfo";

    public static String AUCTION_BID_LIST = BASE_URL + "auction/v1/bidList";

    public static String ORDER_DELETE = BASE_URL + "order/v1/orderRemove";

    public static String SHARE = BASE_URL + "v1/share";

    /**
     * 新版分类
     **/
    public static String CLASS_NEW = BASE_URL + "cats";
    /**
     * 实名认证
     **/
    public static String VERIFYCARD = BASE_URL + "user/verifyCard";
    //WEB
    /**
     * Family主页
     **/
    public static String WEBVIEW_FAMILY = BASE_WEB_URL + "f";

    /* 新版分类
    **/
    public static String CLASS_NEW_VOLUMN = BASE_URL + "v2/cats";


    public static String ONE_YUAN_CLASSIFICATION = BASE_URL + "zone/v1/one";
    public static boolean ONSTAGE = true;
    public static int GET_RONG_TOKEN_TYPE_NO = 0;
    public static int GET_RONG_TOKEN_TYPE_YES = 1;
    public static int UNREAD_ORDER_COUNT = 0;
    public static int UNREAD_NOTICE_COUNT = 0;
    public static int UNREAD_PRIVATE_COUNT = 0;
    public static int UNREAD_SYSTEM_COUNT = 0;
    public static int UNREAD_COMMENT_MSG_COUNT = 0;
    public static String AUTH_ZM_CANCEL = "auth_zm_cancel";
    public static String REQUEST_KEY_GOODS_COMMENT = "comment";
    public static String REQUEST_KEY_NOTIFY_COMMENT = "notificationComment";
    public static String REQUEST_KEY_NOTIFY_ORDER = "notificationOrder";
    public static String REQUEST_KEY_NOTIFY_NOTICE = "notificationOther";
    public static String REQUEST_KEY_NOTIFY_CAMPAIGN = "notificationCampaign";
    public static String REQUEST_KEY_NOTIFY_OTHER_FEATURE_ALERTS = "notificationOtherFeatureAlerts";
//    public static String REQUEST_VALUE_ALL = "all";
//    public static String REQUEST_VALUE_FRIEND_ONLY = "friendOnly";
//    public static String REQUEST_VALUE_NONE = "none";
    /**
     * 花语广场
     **/
    public static String ARTICLESQUARE = BASE_URL + "/fpoem/v1/discovery";
    public static String SEARCHARTICLESQUARE = BASE_URL + "fpoem/v1/search";
    public static Bitmap flurPublishGoods;

    public static final class Comment {
        public static final String TARGET_ID = "targetId";
        public static final String TARGET_TYPE = "targetType";
        public static final String CONTENT = "content";

        public static final int TARGET_GOODS = 1;
        public static final int TARGET_ARTICLE = 6;
        public static final int TARGET_ARTICLE_ESAY = 8;

    }


    public static final String CHATKIT_ID = Config.DEBUG
            ? "MAt7GaT4VjeOXrHC4NSBuGYP-gzGzoHsz" : "btbuVwvFMiwXMdsfI4PwKYap-gzGzoHsz";
    public static final String CHATKIT_KEY = Config.DEBUG
            ? "gXiqlO8xkJoheaDY6XrLJtlG" : "ydftg00dqjHeGzbo05uM7sjN";
//    public static final String CHATKIT_ID = "btbuVwvFMiwXMdsfI4PwKYap-gzGzoHsz";
//    public static final String CHATKIT_KEY = "ydftg00dqjHeGzbo05uM7sjN";

    public static final String FLYME_ID = "3113862";
    public static final String FLYME_KEY = "56ebbf8fa89349408e0dad323c86e888";
    public static final String MI_KEY = "5661749461747";
    public static final String MI_ID = "2882303761517494747";

    /**
     * 留言会话 ID
     */
    public static final String COMMENT_ID = Config.DEBUG
            ? "59fc33beee920a003cf998fd" : "59eaf2dbac502e0069c78313";
    /**
     * 通知会话 ID
     */
    public static final String NOTICE_ID = Config.DEBUG
            ? "59fc33db44d904003ee70061" : "59eaf2717565710046a60664";
    /**
     * 订单会话 ID
     */
    public static final String CONV_ORDER_ID = Config.DEBUG
            ? "59fc33a1ee920a003cf998ca" : "59eaf346570c350045c654cf";
    /**
     * 广播系统 ID
     */
    public static final String ACTIVITY_ID = Config.DEBUG
            ? "59fc33e9128fe100445d5ed1" : "59eaf3948d6d8170be120303";


    private static String getConstantsForIM(String str) {
        return "im." + str;
    }

    public static final String IM_PEER_ID = getConstantsForIM("peer_id");
    public static final String IM_CONV_ID = getConstantsForIM("conv_id");
    public static final String IM_MARK_AS_READ = getConstantsForIM("mark_as_read");
    public static final String IM_CONV_TYPE_KEY = "type";
    public static final int IM_CONV_TYPE_CAMPAIGN = 1;
    public static final int IM_CONV_TYPE_NOTIFICATION = 2;
    public static final int IM_CONV_TYPE_COMMENT = 3;


    public static int FROM_FLAGS_VERIFIED = 1;

}
