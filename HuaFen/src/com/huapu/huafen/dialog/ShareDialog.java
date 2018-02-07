package com.huapu.huafen.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.ShareUtils;
import com.huapu.huafen.utils.ToastUtil;

import java.util.ArrayList;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * @author liang_xs 分享对话框
 */
public class ShareDialog extends Dialog implements OnClickListener {

    private Context context;
    private GridView gvShare;
    private Button btnShare;
    private ArrayList<ShareItem> shareArrayList;
    private ShareCallback shareCallback;
    private String title, text, imageUrl, url, feature, adds;

    public ShareDialog(Context context, int theme) {
        super(context, R.style.photo_dialog);
        this.context = context;
    }


    private String recTraceId;
    private int recIndex;
    private String searchQuery;
    private long goodsId;

    public void setRecTraceId(String recTraceId) {
        this.recTraceId = recTraceId;
    }


    public void setRecIndex(int recIndex) {
        this.recIndex = recIndex;
    }


    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * @param context
     */
    public ShareDialog(Context context, String... shareItem) {
        super(context, R.style.photo_dialog);
        this.context = context;
        if (shareItem.length == 4) {
            this.title = shareItem[0];
            this.text = shareItem[1];
            this.imageUrl = shareItem[2];
            this.url = shareItem[3];
        }
        if (shareItem.length == 5) {
            this.title = shareItem[0];
            this.text = shareItem[1];
            this.imageUrl = shareItem[2];
            this.url = shareItem[3];
            this.feature = shareItem[4];
        }

        if (shareItem.length == 6) {
            this.title = shareItem[0];
            this.text = shareItem[1];
            this.imageUrl = shareItem[2];
            this.url = shareItem[3];
            this.feature = shareItem[4];
            this.adds = shareItem[5];
        }
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);

        Window window = this.getWindow();
        LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        lp.width = dm.widthPixels;
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogBottomStyle);

        gvShare = (GridView) findViewById(R.id.gvShare);
        btnShare = (Button) findViewById(R.id.btnShareCancel);
        btnShare.setOnClickListener(this);

        showType();
    }

    /**
     * 初始化数据
     */
    public void showType() {
        shareArrayList = new ArrayList<ShareItem>();
        ShareItem wechat = new ShareItem(R.drawable.share_friend,
                R.string.share_wechat);
        ShareItem wechatMoments = new ShareItem(R.drawable.share_friends,
                R.string.share_wechat_moments);
        ShareItem sinaWeibo = new ShareItem(R.drawable.share_sina,
                R.string.share_sina);
        ShareItem qzone = new ShareItem(R.drawable.share_zone,
                R.string.share_zone);
        ShareItem qq = new ShareItem(R.drawable.share_qq,
                R.string.share_qq);
        ShareItem copy = new ShareItem(R.drawable.share_copy,
                R.string.share_copy);
        shareArrayList.add(wechat);
        shareArrayList.add(wechatMoments);
        shareArrayList.add(sinaWeibo);
        shareArrayList.add(qq);
        shareArrayList.add(copy);

        ShareAdapter shareAdapter = new ShareAdapter(context, shareArrayList);
        gvShare.setAdapter(shareAdapter);
        gvShare.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ShareItem shareItem = shareArrayList.get(position);
                if (shareItem.textId == R.string.share_wechat_moments) {//朋友圈
                    if ("webview".equals(feature)) {
                        text = "";
                    }
                    ShareUtils.share(context, WechatMoments.NAME, title, text, imageUrl, url, recTraceId, searchQuery, recIndex, 2, goodsId);
                } else if (shareItem.textId == R.string.share_wechat) {//微信好友
                    wechatqqfriend();
                    ShareUtils.share(context, Wechat.NAME, title, text, imageUrl, url, recTraceId, searchQuery, recIndex, 1, goodsId);
                } else if (shareItem.textId == R.string.share_sina) {//微博
                    if ("webview".equals(feature)) {
                        text = title + text + ShareUtils.DOWN_LOAD;
                    }
                    title = "";
                    ShareUtils.share(context, SinaWeibo.NAME, title, text, imageUrl, url, recTraceId, searchQuery, recIndex, 3, goodsId);
                } else if (shareItem.textId == R.string.share_qq) {//QQ
                    wechatqqfriend();
                    ShareUtils.share(context, QQ.NAME, title, text, imageUrl, url, recTraceId, searchQuery, recIndex, -1, goodsId);
                } else if (shareItem.textId == R.string.share_zone) {//QQm空间
                    ShareUtils.share(context, QZone.NAME, title, text, imageUrl, url, recTraceId, searchQuery, recIndex, 4, goodsId);
                } else if (shareItem.textId == R.string.share_copy) {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                    cm.setText(url);
                    ToastUtil.toast(context, "复制成功");
                }
                ShareDialog.this.dismiss();

            }
        });

    }

    private void wechatqqfriend() {
        if ("goods".equals(feature) || "article".equals(feature) || "personal".equals(feature) || "flower".equals(feature)) {
            text = adds;
        }
    }

    /**
     * 点击事件
     *
     * @param shareCallback
     */
    public void setOnClickBack(ShareCallback shareCallback) {
        this.shareCallback = shareCallback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShareCancel:
                this.dismiss();
                break;

            default:
                break;
        }

    }

    /**
     * @author 点击事件
     */
    public interface ShareCallback {
        void shareItemOnClic(int position, ShareItem shareItem);
    }

    /**
     * 分享item
     */
    public class ShareAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<ShareItem> shareArrayList;

        public ShareAdapter(Context context, ArrayList<ShareItem> shareArrayList) {
            super();
            this.shareArrayList = shareArrayList;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (null != shareArrayList) {
                return shareArrayList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return shareArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ShareViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_gridview_share, null);
                viewHolder = new ShareViewHolder();
                viewHolder.layoutShare = (RelativeLayout) convertView
                        .findViewById(R.id.layoutShare);
                viewHolder.ivShareIcon = (ImageView) convertView
                        .findViewById(R.id.ivShareIcon);
                viewHolder.tvShareName = (TextView) convertView
                        .findViewById(R.id.tvShareName);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ShareViewHolder) convertView.getTag();
            }

            final ShareItem shareItem = shareArrayList.get(position);
            viewHolder.ivShareIcon.setBackgroundResource(shareItem.resId);
            viewHolder.tvShareName.setText(context.getResources().getText(
                    shareItem.textId));

            return convertView;
        }

        class ShareViewHolder {
            public RelativeLayout layoutShare;
            public ImageView ivShareIcon;
            public TextView tvShareName;
        }

    }

    class ShareItem {
        /**
         * 图片资源ID
         */
        public int resId;

        /**
         * 分享标题
         */
        public int textId;


        public ShareItem(int resId, int textId) {
            this.resId = resId;
            this.textId = textId;
        }


        public int getResId() {
            return resId;
        }


        public void setResId(int resId) {
            this.resId = resId;
        }


        public int getTextId() {
            return textId;
        }


        public void setTextId(int textId) {
            this.textId = textId;
        }


    }

}
