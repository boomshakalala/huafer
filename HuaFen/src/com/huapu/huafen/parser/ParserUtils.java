package com.huapu.huafen.parser;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.beans.Age;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.Banner;
import com.huapu.huafen.beans.CampaignData;
import com.huapu.huafen.beans.CampaignInfo;
import com.huapu.huafen.beans.CheckUpdateInfo;
import com.huapu.huafen.beans.Classification;
import com.huapu.huafen.beans.Comment;
import com.huapu.huafen.beans.CommentLabel;
import com.huapu.huafen.beans.CommentListBean;
import com.huapu.huafen.beans.ConfigBean;
import com.huapu.huafen.beans.Consignee;
import com.huapu.huafen.beans.Express;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.GoodsInfoBean;
import com.huapu.huafen.beans.KeyWordData;
import com.huapu.huafen.beans.OrderConfirmBean;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.OrderListBean;
import com.huapu.huafen.beans.RefundLabel;
import com.huapu.huafen.beans.RefundLogBean;
import com.huapu.huafen.beans.ReportLabel;
import com.huapu.huafen.beans.SecondaryClassification;
import com.huapu.huafen.beans.SystemMessage;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.beans.UserResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liang_xs
 * @ClassName: Parser
 * @Description: 解析工具类
 * @date 2016-03-27
 */
public class ParserUtils {
    public static String RESPONSE_CODE = "code";
    public static String RESPONSE_OBJ = "obj";
    public static String RESPONSE_MSG = "msg";
    public static int RESPONSE_SUCCESS_CODE = 200;
    public static int RESPONSE_TOKEN_MISS = 202;
    public static int RESPONSE_PHONE_HAS_BEEN_REG = 208;
    public static int RESPONSE_PHONE_BINDS_PHONE = 1107;
    // 店铺、用户被封禁
    public static int RESPONSE_BLOCKED = 251;

    public static int REQUEST_AUTH_ZM_NONE = 1004;
    public static int REQUEST_AUTH_ZM_UNBIND_ALIPAY = 1007;
    public static int RESPONSE_ALIPAY_PAYING_CODE = 807;
    public static int RESPONSE_WECHAT_UID_UNEXIST = 217;
    public static int RESPONSE_ALIPAY_UID_UNEXIST = 1003;
    public static int RESPONSE_GOODS_DETAILS_IS_SELL = 602;
    public static int RESPONSE_ORDER_PRICE_CHANGE = 302;
    public static int RESPONSE_ORDER_REFUND_STATUS_CHANGE = 324;
    public static int CHAT_USERS_NO_GOODS = 404;
    public static int SERVER_IS_FULL = 503;
    public static int WECHAT_ALREADY_REG = 2;//微信已注册
    public static int ALIPAY_ALREADY_REG = 3;//支付宝已注册

    /**
     * 配置同步信息接口
     *
     * @param json
     * @return
     */
    public static ConfigBean parserConfigData(String json) {
        ConfigBean bean = null;
        try {
            bean = JSON.parseObject(JSON.parseObject(json).getString("config"), ConfigBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public static CheckUpdateInfo parserCheckUpdate(String json) {
        CheckUpdateInfo bean = null;
        try {
            bean = JSON.parseObject(JSON.parseObject(json).getString("checkUpdate"), CheckUpdateInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 用户信息
     *
     * @param json
     * @return
     */
    public static UserInfo parserUserInfoData(String json) {
        UserInfo bean = null;
        try {
            bean = JSON.parseObject(JSON.parseObject(json).getString("userInfo"), UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 首页分类页
     *
     * @param json
     * @return
     */
    public static List<Classification> parserGoodscatesData(String json) {
        List<Classification> list = new ArrayList<Classification>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("classifications").toString(), Classification.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 商品详情
     *
     * @param json
     * @return
     */
    public static GoodsInfoBean parserProDetailsData(String json) {
        GoodsInfoBean goodsInfoBean = null;
        try {
            goodsInfoBean = JSON.parseObject(json, GoodsInfoBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goodsInfoBean;
    }

    /**
     * 首页推荐列表
     *
     * @param json
     * @return
     */
    public static List<GoodsInfoBean> parserRecommendListData(String json) {
        List<GoodsInfoBean> list = new ArrayList<GoodsInfoBean>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("goodsList").toString(), GoodsInfoBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<CampaignData> parserCampaignListData(String json) {
        List<CampaignData> list = new ArrayList<CampaignData>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("goodsList").toString(), CampaignData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 购买商品(详情页点击购买)
     *
     * @param json
     * @return
     */
    public static OrderConfirmBean parserOrderBuyData(String json) {
        OrderConfirmBean bean = null;
        try {
            bean = JSON.parseObject(json, OrderConfirmBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 购买商品(详情页点击购买)
     *
     * @param json
     * @return
     */
    public static String parserPingData(String json) {
        String credential = null;
        try {
            credential = JSON.parseObject(json).getString("credential");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return credential;
    }


    /**
     * 一级分类
     *
     * @param json
     * @return
     */
    public static List<Classification> parserGoodsFirstCateData(String json) {
        List<Classification> list = new ArrayList<Classification>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("classifications").toString(), Classification.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 二级分类
     *
     * @param json
     * @return
     */
    public static List<SecondaryClassification> parserGoodsSecondCateData(String json) {
        List<SecondaryClassification> list = new ArrayList<SecondaryClassification>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("classifications").toString(), SecondaryClassification.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 融云token
     *
     * @param json
     * @return
     */
    public static String parserIMTokenData(String json) {
        String imToken = "";
        try {
            imToken = JSON.parseObject(json).getString("token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imToken;
    }


    /**
     * 年龄段
     *
     * @param json
     * @return
     */
    public static List<Age> parserAgeListData(String json) {
        List<Age> list = new ArrayList<Age>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("ages").toString(), Age.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 城市
     *
     * @param json
     * @return
     */
    public static List<Area> parserCityListData(String json) {
        List<Area> list = new ArrayList<Area>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("citys").toString(), Area.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 城区
     *
     * @param json
     * @return
     */
    public static List<Area> parserAreaListData(String json) {
        List<Area> list = new ArrayList<Area>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("areas").toString(), Area.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 收货地址列表
     *
     * @param json
     * @return
     */
    public static List<Consignee> parserConsigneesListData(String json) {
        List<Consignee> list = new ArrayList<Consignee>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("consignees").toString(), Consignee.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 商铺详情
     *
     * @param json
     * @return
     */
    public static GoodsInfoBean parserUserShopDetailData(String json) {
        GoodsInfoBean bean = null;
        try {
            bean = JSON.parseObject(json, GoodsInfoBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 商品详情想要的人列表
     *
     * @param json
     * @return
     */
    public static List<GoodsInfoBean> parserUserWantListData(String json) {
        List<GoodsInfoBean> list = new ArrayList<GoodsInfoBean>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("wantList").toString(), GoodsInfoBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 我发布的列表
     *
     * @param json
     * @return
     */
    public static List<GoodsInfo> parserReleaseListData(String json) {
        List<GoodsInfo> list = new ArrayList<GoodsInfo>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("goodsList").toString(), GoodsInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 我关注的店主列表 / 粉丝列表
     *
     * @param json
     * @return
     */
    public static List<UserInfo> parserFollowListData(String json) {
        List<UserInfo> list = new ArrayList<UserInfo>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("users").toString(), UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 我卖出的
     *
     * @param json
     * @return
     */
    public static List<GoodsInfoBean> parserSellListData(String json) {
        List<GoodsInfoBean> list = new ArrayList<GoodsInfoBean>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("goodsList").toString(), GoodsInfoBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 订单消息列表
     *
     * @param json
     * @return
     */
    public static List<OrderListBean> parserOrderListData(String json) {
        List<OrderListBean> list = new ArrayList<OrderListBean>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("orderLogList").toString(), OrderListBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 投诉理由列表
     *
     * @param json
     * @return
     */
    public static List<ReportLabel> parserReportReasonListData(String json) {
        List<ReportLabel> list = new ArrayList<ReportLabel>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("reportLabels").toString(), ReportLabel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 评价不满意标签列表
     *
     * @param json
     * @return
     */
    public static List<ReportLabel> parserBadEstimateTagListData(String json) {
        List<ReportLabel> list = new ArrayList<ReportLabel>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("badCommentLabels").toString(), ReportLabel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 评价满意标签列表
     *
     * @param json
     * @return
     */
    public static List<ReportLabel> parserGoodEstimateTagListData(String json) {
        List<ReportLabel> list = new ArrayList<ReportLabel>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("goodCommentLabels").toString(), ReportLabel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 屏蔽的人列表
     *
     * @param json
     * @return
     */
    public static List<UserInfo> parserBlackListData(String json) {
        List<UserInfo> list = new ArrayList<UserInfo>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("users").toString(), UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 广告位轮播图
     *
     * @param json
     * @return
     */
    public static List<Banner> parserBinnerListData(String json) {
        List<Banner> list = new ArrayList<Banner>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("banners").toString(), Banner.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 编辑商品时获取商品信息
     *
     * @param json
     * @return
     */
    public static GoodsInfo parserGoodsSimpleInfoData(String json) {
        GoodsInfo bean = null;
        try {
            bean = JSON.parseObject(JSON.parseObject(json).get("goodsInfo").toString(), GoodsInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 订单详情
     *
     * @param json
     * @return
     */
    public static OrderDetailBean parserOrderDetailData(String json) {
        OrderDetailBean bean = null;
        try {
            bean = JSON.parseObject(JSON.parseObject(json).toString(), OrderDetailBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 退款原因标签列表
     *
     * @param json
     * @return
     */
    public static List<RefundLabel> parserRefundLabelListData(String json) {
        List<RefundLabel> list = new ArrayList<RefundLabel>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("refundLabels").toString(), RefundLabel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 物流公司
     *
     * @param json
     * @return
     */
    public static List<Express> parserExpressListData(String json) {
        List<Express> list = new ArrayList<Express>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("expressList").toString(), Express.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 退款历史信息
     *
     * @param json
     * @return
     */
    public static List<RefundLogBean> parserRefundLogInfoListData(String json) {
        List<RefundLogBean> list = new ArrayList<RefundLogBean>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("refundLogList").toString(), RefundLogBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 物流信息
     *
     * @param json
     * @return
     */
    public static Express parserOrderExpressData(String json) {
        Express bean = null;
        try {
            bean = JSON.parseObject(JSON.parseObject(json).get("expressInfo").toString(), Express.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 用户订单所有评价
     *
     * @param json
     * @return
     */
    public static List<CommentListBean> parserOrderCommentListData(String json) {
        List<CommentListBean> list = new ArrayList<CommentListBean>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("comments").toString(), CommentListBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 用户订单所有评价标签
     *
     * @param json
     * @return
     */
    public static List<CommentLabel> parserCommentLabelListData(String json) {
        List<CommentLabel> list = new ArrayList<CommentLabel>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("commentLables").toString(), CommentLabel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 活动所需参数
     *
     * @param json
     * @return
     */
    public static CampaignInfo parserCampaignData(String json) {
        CampaignInfo bean = null;
        try {
            bean = JSON.parseObject(json, CampaignInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 评论详情
     *
     * @param json
     * @return
     */
    public static Comment parserCommentData(String json) {
        Comment bean = null;
        try {
            bean = JSON.parseObject(JSON.parseObject(json).get("comment").toString(), Comment.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }


    /**
     * 明星列表
     *
     * @param json
     * @return
     */
    public static List<UserResult> parserStarListData(String json) {
        List<UserResult> list = new ArrayList<UserResult>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("users").toString(), UserResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 热搜列表
     *
     * @param json
     * @return
     */
    public static List<KeyWordData> parserSearchKeyWordData(String json) {
        List<KeyWordData> list = new ArrayList<KeyWordData>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("hotWords").toString(), KeyWordData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 系统消息列表
     *
     * @param json
     * @return
     */
    public static List<SystemMessage> parserSystemListData(String json) {
        List<SystemMessage> list = new ArrayList<SystemMessage>();
        try {
            list = JSON.parseArray(JSON.parseObject(json).get("notifications").toString(), SystemMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}
