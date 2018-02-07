package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ShareUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.loopviewpager.CardPagerAdapter;
import com.huapu.huafen.views.loopviewpager.CustomViewPager;
import com.huapu.huafen.views.loopviewpager.Item;
import com.huapu.huafen.views.loopviewpager.ShadowTransformer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Administrator on 2017/4/14.
 */

public class WalletShareActivity extends BaseActivity implements ShadowTransformer.OnPageSelected {
    private static final String guide_key = "first_wallet_share";
    @BindView(R.id.shareBottomLayout)
    LinearLayout shareBottomLayout;
    private String path;
    private List<Item> mlist = new ArrayList<>();
    private int[] mImgs = {R.drawable.first_page, R.drawable.second_page, R.drawable.third_page, R.drawable.forth_page, R.drawable.five_page, R.drawable.six_page};
    private CustomViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private int shareEarned;
    private Item itemSelected;

    private int selectPosition;

    private RelativeLayout layoutBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_share);
        ButterKnife.bind(this);
        path = this.getCacheDir().toString() + "/temp.png";
        if (CommonPreference.getBooleanValue(guide_key, true)) {
            Intent intent = new Intent(this, MontageActivity.class);
            intent.putExtra(MyConstants.EXTRA_MONTAGE, guide_key);
            this.startActivity(intent);
            overridePendingTransition(0, 0);
            CommonPreference.setBooleanValue(guide_key, false);
        }

        initData();
        initView();

    }

    private void initView() {
        getTitleBar().
                setTitle("分享").
                setRightText("生成海报", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WalletShareActivity.this, PreViewActivity.class);
                        intent.putExtra("shareEarned", shareEarned);
                        intent.putExtra("item", itemSelected);
                        startActivity(intent);
                    }
                });

        layoutBase = (RelativeLayout) findViewById(R.id.layoutBase);

        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter(this, mlist);
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter, this);
        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(6);

        mCardAdapter.setOnItemClickListener(new CardPagerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                mViewPager.setCurrentItem(position);
            }
        });
        mViewPager.setCurrentItem(2);
        mCardShadowTransformer.setCanScale(true);
        mCardShadowTransformer.setScale(0.1f, true);


        int statusBarHeight;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight = getResources().getDimensionPixelSize(height);
            Logger.e("get height:" + statusBarHeight);
            Logger.e("get height:" + CommonUtils.dip2px(this, 145));

            DisplayMetrics dm = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels - CommonUtils.dip2px(this, 150);
            int heightIcon = width * 639 / 550;

            /**
             * title height 45dp
             * bottomLayout 100dp
             * editText 50dp
             * image marginTop 10dp
             */
            int bottomHeight = dm.heightPixels - CommonUtils.dip2px(this, 205) - statusBarHeight - heightIcon;


            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
            layoutParams.bottomMargin = bottomHeight / 2;
            mViewPager.setLayoutParams(layoutParams);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initData() {
        shareEarned = getIntent().getIntExtra("shareEarned", 0);
        UserInfo userInfo = CommonPreference.getUserInfo();
        for (int mImg : mImgs) {
            Item item = new Item();
            item.setImg(mImg);
            item.setName(userInfo.getUserName());
            item.setUserPic(userInfo.getUserIcon());
            item.setShareEarned(shareEarned);
            item.setUserLevel(userInfo.getUserLevel());
            mlist.add(item);
        }
    }


    @OnClick({R.id.weiXinFriend, R.id.friendQuan, R.id.sinaWeiBo, R.id.qqFriend})
    public void onViewClicked(View view) {
//        itemSelected.setInputContent(mCardAdapter.getCurrentText());
        View makeView = LayoutInflater.from(this).inflate(R.layout.activity_draw, null);
        ImageView imageView = (ImageView) makeView.findViewById(R.id.myImage);
        imageView.setImageResource(itemSelected.getImg());
//        TextView personName = (TextView) makeView.findViewById(R.id.personName);
//        personName.setText(itemSelected.getName());
        CommonTitleView commonTitleView = (CommonTitleView) makeView.findViewById(R.id.ctvName);
        commonTitleView.setData(CommonPreference.getUserInfo());
        TextView textView = (TextView) commonTitleView.findViewById(R.id.tvName);
        textView.setTextSize(12f);
        ImageView personPic = (ImageView) makeView.findViewById(R.id.personPic);

        SimpleDraweeView imageView1 = (SimpleDraweeView) mCardAdapter.getCardViewAt(selectPosition).findViewById(R.id.personPic);
        imageView1.buildDrawingCache();
        personPic.setImageBitmap(imageView1.getDrawingCache());

        TextView earnMoney = (TextView) makeView.findViewById(R.id.earnMoney);
        earnMoney.setText("我在花粉儿共赚了" + itemSelected.getShareEarned() + "元");
        TextView inputContent = (TextView) makeView.findViewById(R.id.inputContent);
        if (TextUtils.isEmpty(itemSelected.getInputContent())) {
            inputContent.setVisibility(View.GONE);
        } else {
            inputContent.setText(itemSelected.getInputContent());
        }

//        ImageView specialImage = (ImageView) makeView.findViewById(R.id.specialImage);
//
//        switch (itemSelected.getUserLevel()) {
//            case 2:
//                specialImage.setVisibility(View.VISIBLE);
//                specialImage.setImageResource(R.drawable.icon_vip);
//                break;
//            case 3:
//                specialImage.setVisibility(View.VISIBLE);
//                specialImage.setImageResource(R.drawable.icon_xing);
//                break;
//            default:
//                specialImage.setVisibility(View.GONE);
//                break;
//        }
        makeView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        makeView.layout(0, 0, makeView.getMeasuredWidth(), makeView.getMeasuredHeight());
        makeView.buildDrawingCache();
        try {
            String sdCardDir = this.getApplicationContext().getCacheDir().toString();
            File file = new File(sdCardDir, "temp.png");
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            loadBitmapFromView(makeView)
                    .compress(Bitmap.CompressFormat.PNG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            Logger.e("get exception" + e.getMessage());
        }

//        View linearLayout = mCardAdapter.getCardViewAt(selectPosition);
//        CommonUtils.saveImage(makeView, this);
        switch (view.getId()) {
            case R.id.weiXinFriend:
                ShareUtils.shareImage(Wechat.NAME, this.getApplicationContext(), path);
                break;
            case R.id.friendQuan:
                ShareUtils.shareImage(WechatMoments.NAME, this.getApplicationContext(), path);
                break;
            case R.id.sinaWeiBo:
                ShareUtils.shareImage(SinaWeibo.NAME, this.getApplicationContext(), path);
                break;
            case R.id.qqFriend:
                ShareUtils.shareImage(QQ.NAME, this.getApplicationContext(), path);
                break;
        }
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    @Override
    public void pageSelect(int position) {
        selectPosition = position;
        itemSelected = mCardAdapter.getmData().get(position);
    }
}
