package com.huapu.huafen.enums;

import com.huapu.huafen.R;

/**
 * Created by mac on 17/6/22.
 */

public class OrderEnum {

    public interface OrderStateImpl {
        int[] getTextRes();
        int[] getTextColorRes();
        int[] getImageRes();
        int[] getLineBackgroundColorRes();
    }

    public enum OrderState implements OrderStateImpl{

        PREPARE_TO_PAY{

            @Override
            public int[] getTextRes() {
                return new int[]{R.string.prepare_to_pay,R.string.prepare_to_ship,R.string.prepare_to_receipt,R.string.prepare_to_comment};
            }

            @Override
            public int[] getTextColorRes() {
                return new int[]{R.color.white_50,R.color.white_50,R.color.white_50,R.color.white_50};
            }

            @Override
            public int[] getImageRes() {
                return new int[]{R.drawable.order_state_payment,R.drawable.order_state_ship,R.drawable.order_state_receipt,R.drawable.order_state_comment};
            }

            @Override
            public int[] getLineBackgroundColorRes() {
                return new int[]{R.color.white_50,R.color.white_50,R.color.white_50,R.color.white_50};
            }
        },
        PAYING{

            @Override
            public int[] getTextRes() {
                return new int[]{R.string.prepare_to_pay,R.string.prepare_to_ship,R.string.prepare_to_receipt,R.string.prepare_to_comment};
            }

            @Override
            public int[] getTextColorRes() {
                return new int[]{R.color.white_50,R.color.white_50,R.color.white_50,R.color.white_50};
            }

            @Override
            public int[] getImageRes() {
                return new int[]{R.drawable.order_state_payment,R.drawable.order_state_ship,R.drawable.order_state_receipt,R.drawable.order_state_comment};
            }

            @Override
            public int[] getLineBackgroundColorRes() {
                return new int[]{R.color.white_50,R.color.white_50,R.color.white_50,R.color.white_50};
            }
        },
        PAYED{

            @Override
            public int[] getTextRes() {
                return new int[]{R.string.has_pay,R.string.prepare_to_ship,R.string.prepare_to_receipt,R.string.prepare_to_comment};
            }

            @Override
            public int[] getTextColorRes() {
                return new int[]{R.color.white,R.color.white_50,R.color.white_50,R.color.white_50};
            }

            @Override
            public int[] getImageRes() {
                return new int[]{R.drawable.order_state_payment_light,R.drawable.order_state_ship,R.drawable.order_state_receipt,R.drawable.order_state_comment};
            }

            @Override
            public int[] getLineBackgroundColorRes() {
                return new int[]{R.color.base_pink,R.color.white_50,R.color.white_50,R.color.white_50};
            }
        },
        PREPARE_TO_RECEIPT{

            @Override
            public int[] getTextRes() {
                return new int[]{R.string.has_pay,R.string.has_ship,R.string.prepare_to_receipt,R.string.prepare_to_comment};
            }

            @Override
            public int[] getTextColorRes() {
                return new int[]{R.color.white,R.color.white,R.color.white_50,R.color.white_50};
            }

            @Override
            public int[] getImageRes() {
                return new int[]{R.drawable.order_state_payment_light,R.drawable.order_state_ship_light,R.drawable.order_state_receipt,R.drawable.order_state_comment};
            }

            @Override
            public int[] getLineBackgroundColorRes() {
                return new int[]{R.color.base_pink,R.color.base_pink,R.color.white_50,R.color.white_50};
            }
        },
        RECEIPT{

            @Override
            public int[] getTextRes() {
                return new int[]{R.string.has_pay,R.string.has_ship,R.string.has_receipt,R.string.prepare_to_comment};
            }

            @Override
            public int[] getTextColorRes() {
                return new int[]{R.color.white,R.color.white,R.color.white,R.color.white_50};
            }

            @Override
            public int[] getImageRes() {
                return new int[]{R.drawable.order_state_payment_light,R.drawable.order_state_ship_light,R.drawable.order_state_receipt_light,R.drawable.order_state_comment};
            }

            @Override
            public int[] getLineBackgroundColorRes() {
                return new int[]{R.color.base_pink,R.color.base_pink,R.color.base_pink,R.color.white_50};
            }
        },
        ORDER_COMPLETE{

            @Override
            public int[] getTextRes() {
                return new int[]{R.string.has_pay,R.string.has_ship,R.string.has_receipt,R.string.has_comment};
            }

            @Override
            public int[] getTextColorRes() {
                return new int[]{R.color.white,R.color.white,R.color.white,R.color.white};
            }

            @Override
            public int[] getImageRes() {
                return new int[]{R.drawable.order_state_payment_light,R.drawable.order_state_ship_light,R.drawable.order_state_receipt_light,R.drawable.order_state_comment_light};
            }

            @Override
            public int[] getLineBackgroundColorRes() {
                return new int[]{R.color.base_pink,R.color.base_pink,R.color.base_pink,R.color.base_pink};
            }
        },

        ;

        @Override
        public int[] getTextRes() {
            return new int[0];
        }

        @Override
        public int[] getTextColorRes() {
            return new int[0];
        }

        @Override
        public int[] getImageRes() {
            return new int[0];
        }

        @Override
        public int[] getLineBackgroundColorRes() {
            return new int[0];
        }
    }


}
