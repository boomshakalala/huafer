package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.HotGoodsBean;
import com.huapu.huafen.beans.LikeListBean;
import com.huapu.huafen.beans.ShopArticleData;
import com.huapu.huafen.utils.CommonUtils;

public class CommonPriceTagView extends LinearLayout {

	private TextView tvIsFreeDelivery;
	private TextView tvIsNew;
	private View layoutTip;
	private CommonPriceView cpvPrice;
	private TextView tvDiscount;

	public CommonPriceTagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public CommonPriceTagView(Context context) {
		super(context, null);
		initView();
	}

	public void setData(GoodsData info){
		if(info == null){
			return ;
		}
		cpvPrice.setData(info);
		int countTips = 0;
		if(info.getIsFreeDelivery()) {
			tvIsFreeDelivery.setVisibility(View.VISIBLE);
			countTips = 1;
		} else {
			tvIsFreeDelivery.setVisibility(View.GONE);
		}
		if(info.getIsNew() == 1) {
			tvIsNew.setVisibility(VISIBLE);
			if(countTips == 0) {
				countTips = 1;
			} else {
				countTips++;
			}
		} else {
			tvIsNew.setVisibility(GONE);
		}

		if(info.getDiscount()>0&&info.getDiscount()<10) {
			tvDiscount.setVisibility(GONE);
			tvDiscount.setText(String.valueOf(info.getDiscount())+"折");
		} else {
			tvDiscount.setVisibility(GONE);
		}

		LayoutParams nameParams = (LayoutParams) cpvPrice.getLayoutParams();
		LayoutParams layoutParams = (LayoutParams) layoutTip.getLayoutParams();
		int right ;
		int left ;
		switch (countTips) {
			case 0:
				right = CommonUtils.dp2px(0);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(0);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 1:
				right = CommonUtils.dp2px(40);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-40);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 2:
				right = CommonUtils.dp2px(80);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-80);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
		}
	}

	public void setData(ShopArticleData info){
		if(info == null){
			return ;
		}
		cpvPrice.setData(info);
		int countTips = 0;
		if(info.getItem().isFreeDelivery()) {
			tvIsFreeDelivery.setVisibility(View.VISIBLE);
			countTips = 1;
		} else {
			tvIsFreeDelivery.setVisibility(View.GONE);
		}
		if(info.getItem().getIsNew() == 1) {
			tvIsNew.setVisibility(VISIBLE);
			if(countTips == 0) {
				countTips = 1;
			} else {
				countTips++;
			}
		} else {
			tvIsNew.setVisibility(GONE);
		}

		if(info.getItem().getDiscount()>0&&info.getItem().getDiscount()<10) {
			tvDiscount.setVisibility(GONE);
			tvDiscount.setText(String.valueOf(info.getItem().getDiscount())+"折");
		} else {
			tvDiscount.setVisibility(GONE);
		}

		LayoutParams nameParams = (LayoutParams) cpvPrice.getLayoutParams();
		LayoutParams layoutParams = (LayoutParams) layoutTip.getLayoutParams();
		int right ;
		int left ;
		switch (countTips) {
			case 0:
				right = CommonUtils.dp2px(0);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(0);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 1:
				right = CommonUtils.dp2px(40);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-40);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 2:
				right = CommonUtils.dp2px(80);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-80);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
		}
	}

	public void setData(GoodsInfo info){
		if(info == null){
			return ;
		}
		cpvPrice.setData(info);
		int countTips = 0;
		if(info.getIsFreeDelivery()) {
			tvIsFreeDelivery.setVisibility(View.VISIBLE);
			countTips = 1;
		} else {
			tvIsFreeDelivery.setVisibility(View.GONE);
		}
		if(info.getIsNew() == 1) {
			tvIsNew.setVisibility(VISIBLE);
			if(countTips == 0) {
				countTips = 1;
			} else {
				countTips++;
			}
		} else {
			tvIsNew.setVisibility(GONE);
		}

		if(info.getDiscount()>0&&info.getDiscount()<10) {
			tvDiscount.setVisibility(VISIBLE);
			tvDiscount.setText(String.valueOf(info.getDiscount())+"折");
		} else {
			tvDiscount.setVisibility(GONE);
		}

		LayoutParams nameParams = (LayoutParams) cpvPrice.getLayoutParams();
		LayoutParams layoutParams = (LayoutParams) layoutTip.getLayoutParams();
		int right ;
		int left ;
		switch (countTips) {
			case 0:
				right = CommonUtils.dp2px(0);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(0);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 1:
				right = CommonUtils.dp2px(40);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-40);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 2:
				right = CommonUtils.dp2px(80);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-80);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
		}
	}
	private void initView(){
		LayoutInflater inflater=(LayoutInflater) getContext().
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.common_price_tag_layout,this,true);
		tvIsFreeDelivery=(TextView) findViewById(R.id.tvIsFreeDelivery);
		tvIsNew=(TextView) findViewById(R.id.tvIsNew);
		tvDiscount = (TextView) findViewById(R.id.tvDiscount);
		layoutTip = findViewById(R.id.layoutTip);
		cpvPrice = (CommonPriceView) findViewById(R.id.cpvPrice);
	}

	public void setData(LikeListBean.ListInfo.ItemInfo info) {

		if(info == null){
			return ;
		}
		cpvPrice.setData(info);
		int countTips = 0;
		if(info.isFreeDelivery()) {
			tvIsFreeDelivery.setVisibility(View.VISIBLE);
			countTips = 1;
		} else {
			tvIsFreeDelivery.setVisibility(View.GONE);
		}
		if(info.getIsNew() == 1) {
			tvIsNew.setVisibility(VISIBLE);
			if(countTips == 0) {
				countTips = 1;
			} else {
				countTips++;
			}
		} else {
			tvIsNew.setVisibility(GONE);
		}

		if(info.getDiscount()>0&&info.getDiscount()<10) {
			tvDiscount.setVisibility(VISIBLE);
			tvDiscount.setText(String.valueOf(info.getDiscount())+"折");
		} else {
			tvDiscount.setVisibility(GONE);
		}

		LayoutParams nameParams = (LayoutParams) cpvPrice.getLayoutParams();
		LayoutParams layoutParams = (LayoutParams) layoutTip.getLayoutParams();
		int right ;
		int left ;
		switch (countTips) {
			case 0:
				right = CommonUtils.dp2px(0);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(0);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 1:
				right = CommonUtils.dp2px(40);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-40);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 2:
				right = CommonUtils.dp2px(80);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-80);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
		}
	}


	public void setData(ArticleAndGoods goods){
		if(goods == null){
			return ;
		}
		cpvPrice.setData(goods);
		int countTips = 0;
		if(goods.isFreeDelivery) {
			tvIsFreeDelivery.setVisibility(View.VISIBLE);
			countTips = 1;
		} else {
			tvIsFreeDelivery.setVisibility(View.GONE);
		}
		if(goods.isNew == 1) {
			tvIsNew.setVisibility(VISIBLE);
			if(countTips == 0) {
				countTips = 1;
			} else {
				countTips++;
			}
		} else {
			tvIsNew.setVisibility(GONE);
		}

		if(goods.discount>0&&goods.discount<10) {
			tvDiscount.setVisibility(GONE);
			tvDiscount.setText(String.valueOf(goods.discount)+"折");
		} else {
			tvDiscount.setVisibility(GONE);
		}

        LayoutParams nameParams = (LayoutParams) cpvPrice.getLayoutParams();
        LayoutParams layoutParams = (LayoutParams) layoutTip.getLayoutParams();
        int right;
        int left;
        switch (countTips) {
            case 0:
                right = CommonUtils.dp2px(0);
                nameParams.rightMargin = right;
                cpvPrice.setLayoutParams(nameParams);
                left = CommonUtils.dp2px(0);
                layoutParams.leftMargin = left;
                layoutTip.setLayoutParams(layoutParams);
                break;
            case 1:
                right = CommonUtils.dp2px(40);
                nameParams.rightMargin = right;
                cpvPrice.setLayoutParams(nameParams);
                left = CommonUtils.dp2px(-40);
                layoutParams.leftMargin = left;
                layoutTip.setLayoutParams(layoutParams);
                break;
            case 2:
                right = CommonUtils.dp2px(80);
                nameParams.rightMargin = right;
                cpvPrice.setLayoutParams(nameParams);
                left = CommonUtils.dp2px(-80);
                layoutParams.leftMargin = left;
                layoutTip.setLayoutParams(layoutParams);
                break;
        }
    }

    public void setData(HotGoodsBean.ItemBean goods) {
        if (goods == null) {
            return;
        }
        cpvPrice.setData(goods.price, goods.pastPrice);
        int countTips = 0;
        if (goods.isFreeDelivery) {
            tvIsFreeDelivery.setVisibility(View.VISIBLE);
            countTips = 1;
        } else {
            tvIsFreeDelivery.setVisibility(View.GONE);
        }
        if (goods.isNew == 1) {
            tvIsNew.setVisibility(VISIBLE);
            if (countTips == 0) {
                countTips = 1;
            } else {
                countTips++;
            }
        } else {
            tvIsNew.setVisibility(GONE);
        }

//		if(goods.discount>0&&goods.discount<10) {
//			tvDiscount.setVisibility(GONE);
//			tvDiscount.setText(String.valueOf(goods.discount)+"折");
//		} else {
//			tvDiscount.setVisibility(GONE);
//		}

		LayoutParams nameParams = (LayoutParams) cpvPrice.getLayoutParams();
		LayoutParams layoutParams = (LayoutParams) layoutTip.getLayoutParams();
		int right ;
		int left ;
		switch (countTips) {
			case 0:
				right = CommonUtils.dp2px(0);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(0);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 1:
				right = CommonUtils.dp2px(40);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-40);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 2:
				right = CommonUtils.dp2px(80);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-80);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
		}
	}
}
