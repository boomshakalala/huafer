package com.huapu.huafen.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.ShareUtils;
import com.huapu.huafen.utils.ToastUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by qwe on 2017/4/28.
 */

public class ShareArticleLayout extends FrameLayout implements PlatformActionListener {

    @BindView(R.id.share_pengyouquan)
    ImageView sharePengyouquan;
    @BindView(R.id.share_wx)
    ImageView shareWx;
    @BindView(R.id.share_sina)
    ImageView shareSina;
    @BindView(R.id.share_qq)
    ImageView shareQq;
    @BindView(R.id.shareLayout)
    RelativeLayout shareLayout;
    private String title;

    private String content;

    private String imageUrl;

    private String webPageUrl;

    private Context context;

    private boolean canSharePengyou = true;

    private boolean canShareWX = false;

    private boolean canShareSina = false;

    private boolean canShareQQ = false;


    public ShareArticleLayout(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ShareArticleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public ShareArticleLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_share_article, this, true);
        ButterKnife.bind(this, view);
    }

    public void setData(String title, String content, String imageUrl, String webPageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.webPageUrl = webPageUrl;
    }

    public void runShare() {
        if (canSharePengyou) {
            canSharePengyou = false;
            ShareUtils.shareMomentFour(context, WechatMoments.NAME, this.title, this.content, this.imageUrl, this.webPageUrl, this);
            return;
        }

        if (canShareWX) {
            canShareWX = false;
            ShareUtils.shareMomentFour(context, Wechat.NAME, this.title, this.content, this.imageUrl, this.webPageUrl, this);
            return;
        }

        if (canShareSina) {
            canShareSina = false;
            ShareUtils.shareMomentFour(context, SinaWeibo.NAME, this.title, this.content, this.imageUrl, this.webPageUrl, this);
            return;
        }

        if (canShareQQ) {
            canShareQQ = false;
            ShareUtils.shareMomentFour(context, QQ.NAME, this.title, this.content, this.imageUrl, this.webPageUrl, this);
        }

    }


    @OnClick({R.id.share_pengyouquan, R.id.share_wx, R.id.share_sina, R.id.share_qq})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.share_pengyouquan:
                canSharePengyou = !canSharePengyou;
                if (canSharePengyou) {
                    sharePengyouquan.setImageResource(R.drawable.moment_pengyouquan);
                } else {
                    sharePengyouquan.setImageResource(R.drawable.share_peng_gray);
                }

//                canSharePengyou = !canSharePengyou;
//                ShareUtils.share(context, WechatMoments.NAME, this.title, this.content, this.imageUrl, this.webPageUrl);
                break;
            case R.id.share_wx:
                canShareWX = !canShareWX;

                if (canShareWX) {
                    shareWx.setImageResource(R.drawable.moment_wx);
                } else {
                    shareWx.setImageResource(R.drawable.share_wx_gray);
                }
//                ShareUtils.share(context, Wechat.NAME, this.title, this.content, this.imageUrl, this.webPageUrl);
                break;
            case R.id.share_sina:

                canShareSina = !canShareSina;

                if (canShareSina) {
                    shareSina.setImageResource(R.drawable.moment_weibo);
                } else {
                    shareSina.setImageResource(R.drawable.share_weibo_gray);
                }
//                ShareUtils.share(context, SinaWeibo.NAME, this.title, this.content, this.imageUrl, this.webPageUrl);
                break;
            case R.id.share_qq:

                canShareQQ = !canShareQQ;
                if (canShareQQ) {
                    shareQq.setImageResource(R.drawable.moment_qq);
                } else {
                    shareQq.setImageResource(R.drawable.share_qq_gray);
                }
//                ShareUtils.share(context, QQ.NAME, this.title, this.content, this.imageUrl, this.webPageUrl);
                break;
        }
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

    public boolean[] getShareState(){
        boolean[] state = new boolean[4];
        state[0] = canSharePengyou;
        state[1] = canShareWX;
        state[2] = canShareSina;
        state[3] = canShareQQ;
        return state;
     }

}
