package com.huapu.huafen.views.loopviewpager;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.views.CommonTitleView;

import java.util.ArrayList;
import java.util.List;


public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    public Activity mContext;
    public List<Item> mData;
    OnItemClickListener mOnItemClickListener;
    private List<CardView> mViews;
    private float mBaseElevation;

    private String currentText = "";

    private int maxNum = 90;
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
            else return null;
        }
    };

    public CardPagerAdapter(Activity context, List<Item> list) {
        mContext = context;
        mViews = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            mViews.add(null);
        }
        this.mData = list;
    }

    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px2dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public List<Item> getmData() {
        return mData;
    }

    public String getCurrentText() {
        if (null != currentText) {
            return currentText.trim();
        }
        return "";
    }

    public void setCurrentText(String text) {
        this.currentText = text;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_fancycoverflow, container, false);
        container.addView(view);
        final Item item = mData.get(position);
        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mIconWidth = dm.widthPixels - dip2px(mContext, 150);
        Logger.e("get icon height:" + mIconWidth);

        CardView cardView = (CardView) view.findViewById(R.id.cardView);


        ImageView mIcon = (ImageView) view.findViewById(R.id.profile_image);

        ViewGroup.LayoutParams layoutParams = mIcon.getLayoutParams();
        layoutParams.width = mIconWidth;
//        layoutParams.height = dm.heightPixels - dip2px(mContext, 410);
        layoutParams.height = (int) (mIconWidth * 1.005);
        mIcon.setScaleType(ImageView.ScaleType.FIT_START);

        mIcon.setLayoutParams(layoutParams);
        mIcon.setImageResource(item.getImg());


        CommonTitleView commonTitleView = (CommonTitleView) view.findViewById(R.id.ctvName);
        commonTitleView.setData(CommonPreference.getUserInfo());
        TextView textView = (TextView) commonTitleView.findViewById(R.id.tvName);
        textView.setTextSize(11f);
//        ImageView specialImage = (ImageView) view.findViewById(R.id.specialImage);

        SimpleDraweeView personPic = (SimpleDraweeView) view.findViewById(R.id.personPic);
        ImageLoader.resizeSmall(personPic, item.getUserPic(), 1);

        TextView personName = (TextView) view.findViewById(R.id.personName);
        personName.setText(item.getName());
        TextView earnMoney = (TextView) view.findViewById(R.id.earnMoney);
        earnMoney.setText("我在花粉儿共赚了" + item.getShareEarned() + "元");
        final EditText editText = (EditText) view.findViewById(R.id.editText);
//        editText.setFilters(new InputFilter[]{filter});
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentText = s.toString().trim();
                if (s.length() >= maxNum) {
                    ToastUtil.toast(mContext, "最多输入90个字！");
//                    s.delete(maxNum, s.length());
//                    editText.setText(s);
//                    editText.setSelection(s.length());
                }

                mData.get(position).setInputContent(s.toString());
            }
        });

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) mOnItemClickListener.onClick(position);
            }
        });
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemClickListener {
        void onClick(int position);
    }
}
