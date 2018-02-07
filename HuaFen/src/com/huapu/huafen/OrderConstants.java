package com.huapu.huafen;

/**
 * Created by mac on 17/6/26.
 */

public class OrderConstants {

    //订单主状态码
    public final static class OrderStatus{
        public final static int PREPARE_TO_PAY = 1;//待支付
        public final static int PAYING = PREPARE_TO_PAY + 1;//支付中
        public final static int PAYED = PAYING + 1;//已支付
        public final static int PREPARE_TO_RECEIPT = PAYED + 1;//待收货
        public final static int RECEIPT = PREPARE_TO_RECEIPT + 1;//已收货
        public final static int ORDER_CLOSE = RECEIPT + 1;//已关闭
        public final static int ORDER_COMPLETE = ORDER_CLOSE + 1;//订单完成
    }

    //订单异常状态
    public final static class OrderState {
        public final static int NORMAL = 0;//正常
        public final static int CANCEL = NORMAL + 1;//取消
        public final static int REFUND = CANCEL + 1;//退款
        public final static int ARBITRATION = REFUND + 1;//仲裁
    }

    //订单状态描述码
    public final static class OrderStateCode {
        public final static int CANCEL_BY_BUYER = 1;//买家取消1
        public final static int CANCEL_BY_SELLER = CANCEL_BY_BUYER + 1;//卖家取消2
        public final static int CANCEL_BY_PAY_OVER_TIME = CANCEL_BY_SELLER + 1;//支付超时取消3
        public final static int CANCEL_BY_SHIP_OVER_TIME = CANCEL_BY_PAY_OVER_TIME + 1;//发货超时取消4
        public final static int CANCEL_BY_SYSTEM = CANCEL_BY_SHIP_OVER_TIME + 1;//系统取消5
        public final static int APPLY_FOR_REFUND = CANCEL_BY_SYSTEM + 1;//申请退款6
        public final static int CANCEL_REFUND = APPLY_FOR_REFUND + 1;//取消退款7
        public final static int AGREE_TO_REFUND = CANCEL_REFUND + 1;//同意退款8
        public final static int REFUSE_REFUND = AGREE_TO_REFUND + 1;//拒绝退款9
        public final static int BUYER_HAS_REFUND = REFUSE_REFUND + 1;//买家已退货10
        public final static int REFUND_COMPLETE = BUYER_HAS_REFUND + 1;//退款完成11
        public final static int IN_ARBITRATION = REFUND_COMPLETE + 1;//仲裁中12
        public final static int AGREE_TO_REFUND_AND_CASH = IN_ARBITRATION + 1;//同意退货退款13
        public final static int HAS_RECEIPT_REFUND = AGREE_TO_REFUND_AND_CASH + 1;//收货并退款14
        public final static int SYSTEM_AGREE_REFUND = HAS_RECEIPT_REFUND + 1;//系统同意退款15
        public final static int SYSTEM_CANCEL_REFUND = SYSTEM_AGREE_REFUND + 1;//系统取消退款16
        public final static int SYSTEM_AUTO_CONFIRM_RECEIPT = SYSTEM_CANCEL_REFUND +1;//系统自动确认收货17
        public final static int DELAY_FOR_RECEIPT = SYSTEM_AUTO_CONFIRM_RECEIPT + 1;//延迟收货18
        public final static int HAS_RECEIPT = DELAY_FOR_RECEIPT + 1;//已收货19
        public final static int PAYMENT_PROCESSING = HAS_RECEIPT + 1;//支付处理中20
        public final static int ARBITRATION_EVIDENCE_PERIOD = PAYMENT_PROCESSING + 1;//仲裁举证期(3天倒计时)21
        public final static int PLATFORM_INTERVENTION_PERIOD = ARBITRATION_EVIDENCE_PERIOD + 1;//平台介入期(平台5-7内完成,不可取消仲裁)22
    }
}
