package com.huapu.huafen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.alipay.AliPayHelper;
import com.huapu.huafen.beans.Age;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Brand;
import com.huapu.huafen.beans.Campaign;
import com.huapu.huafen.beans.City;
import com.huapu.huafen.beans.District;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.OneResult;
import com.huapu.huafen.beans.Region;
import com.huapu.huafen.beans.SecondaryClassification;
import com.huapu.huafen.beans.Tag;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.beans.VideoBean;
import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.events.GoodsEditEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.popup.PublishBottomPopupWindow;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.StringUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.BGAFlowLayout;
import com.huapu.huafen.views.HImagesSelectView;
import com.huapu.huafen.views.SlideSwitch;
import com.squareup.okhttp.Request;
import com.upoc.numberpicker.NumberPicker;
import com.uraroji.garage.android.mp3recvoice.RecMicToMp3;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import de.greenrobot.event.EventBus;

/**
 * 发布第一步
 *
 * @author liang_xs
 */
public class ReleaseActivity extends BaseActivity implements SlideSwitch.OnCheckedChangeListener, RecMicToMp3.ThreadCallBack, View.OnTouchListener {

    public static final String TAG = ReleaseActivity.class.getSimpleName();

    private static final int REQUEST_CODE_FOR_BRANDS = 12;
    private static final int REQUEST_CODE_FOR_1_YUAN = 13;
    private static final int REQUEST_CODE_FOR_PICK_ONE_YUAN_CLASSIFICATION = 14;
    private static final int REQUEST_CODE_FOR_AUCTION_PRICE = 15;
    private static int MAX_TIME = 20; // 最长录制时间，单位秒，0为无时间限制
    private static int MIX_TIME = 2; // 最短录制时间，单位秒，0为无时间限制，建议设为1
    private static int RECORD_NO = 0; // 不在录音
    private static int RECORD_ING = 1; // 正在录音
    private static int RECODE_ED = 2; // 完成录音
    private static int RECODE_STATE = 0; // 录音的状态
    protected String mPrice = "0";
    protected String mPastPrice = "";
    protected String mPostage = "";
    @BindView(R2.id.etGoodsName)
    EditText etGoodsName;
    @BindView(R2.id.layoutClassTitle)
    View layoutClassTitle;
    @BindView(R2.id.layoutAgeTitle)
    View layoutAgeTitle;
    @BindView(R2.id.tvClass)
    TextView tvClass;
    @BindView(R2.id.tvAge)
    TextView tvAge;
    @BindView(R2.id.tvBtnRelease)
    TextView tvBtnRelease;
    @BindView(R2.id.etProDes)
    EditText etProDes;
    @BindView(R2.id.tvInputCount)
    TextView tvInputCount;
    @BindView(R2.id.layoutPrice)
    View layoutPrice;
    @BindView(R2.id.layoutReleaseTime)
    View layoutReleaseTime;
    @BindView(R2.id.tvLocation)
    TextView tvLocation;
    @BindView(R2.id.tvReleaseTime)
    TextView tvReleaseTime;
    @BindView(R2.id.tvPricePop)
    TextView tvPricePop;
    @BindView(R2.id.ivBtnTalk)
    ImageView ivBtnTalk;
    @BindView(R2.id.rlBtnVoicePlay)
    RelativeLayout rlBtnVoicePlay;
    @BindView(R2.id.tvBtnVoiceDel)
    TextView tvBtnVoiceDel;
    @BindView(R2.id.llPlayingState)
    LinearLayout llPlayingState;
    @BindView(R2.id.selectedView)
    HImagesSelectView selectedView;
    @BindView(R2.id.scrollView)
    ScrollView scrollView;
    @BindView(R2.id.mFlowLayout)
    BGAFlowLayout mFlowLayout;
    @BindView(R2.id.tvRecordLength)
    TextView tvRecordLength;
    @BindView(R2.id.ivPlayState)
    ImageView ivPlayState;
    @BindView(R2.id.layoutBrand)
    RelativeLayout layoutBrand;
    @BindView(R2.id.tvBrandPop)
    TextView tvBrandPop;

    @BindView(R.id.llAuction)
    LinearLayout llAuction;
    @BindView(R.id.rlStartingPrice)
    RelativeLayout rlStartingPrice;
    @BindView(R.id.tvStatingPrice)
    TextView tvStatingPrice;
    @BindView(R.id.rlBond)
    RelativeLayout rlBond;
    @BindView(R.id.tvBond)
    TextView tvBond;
    @BindView(R.id.rlFareIncrease)
    RelativeLayout rlFareIncrease;
    @BindView(R.id.tvFareIncrease)
    TextView tvFareIncrease;
    @BindView(R.id.rlShootingTime)
    RelativeLayout rlShootingTime;
    @BindView(R.id.tvShootingTime)
    TextView tvShootingTime;
    @BindView(R.id.rlEndTime)
    RelativeLayout rlEndTime;
    @BindView(R.id.tvEndTime)
    TextView tvEndTime;

    ArrayList<Age> selectPosition = new ArrayList<>();
    //	private MyRecorder recorder;
    private Dialog dialogTime,
            dialogStartingPrieTime,//拍卖开始时间dialog
            dialogEndPriceTime;//拍卖结束时间dialog
    private NumberPicker npStartD, npStartH, npStartM,
            npStartingPriceD, npStartingPriceH, npStartingPriceM,//拍卖开始时间
            npEndingPriceD, npEndingPriceH, npEndingPriceM;//拍卖结束时间
    private float recodeTime = 0.0f; // 录音的时间
    private double voiceValue = 0.0; // 麦克风获取的音量值
    private boolean playState = false; // 播放状态
    /**
     * 语音音量显示
     */
    private Dialog dialog;
    /**
     * 更新的语音图标
     */
    private ImageButton dialog_image;
    private TextView tvSoundTime;
    /**
     * 更新时间的线程
     */
    private Thread timeThread;
    /**
     * MediaPlayer
     */
    private MediaPlayer media;
    private int firstClassId, secondClassId;
    private boolean isStop = false;
    private String[] dArray = new String[30];
    private String[] hArray;
    private String[] mArray;
    private int selectD, selectStartH, selectStartM,
            selectStartingPriceD, selectStartingPriceH, selectStartingPriceM,//拍卖开始时间天时分
            selectEndingPriceD, selectEndingPriceH, selectEndingPriceM//拍卖结束时间天时分
            ;
    private ArrayList<ImageItem> selectBitmap = new ArrayList<>();
    private String goodsId = "";
    private String ageName = "";
    private String name, brand, content;
    private int brandId;
    private String ageId = "";
    private String picKey, soundKey;
    private String goodsImageUrl = "";
    private String sound = "";
    private String sellTime = "";
    private String province = "";
    private String city = "";
    private String district = "";
    private GoodsInfo goodsInfo;
    private int isFreeDelivery = 0;
    private int freightCollect = 0;
    private PublishBottomPopupWindow mPop;
    private Campaign campaign;
    private int campaignId;
    private String cat2Name = "";
    private String editFrom = "";
    private boolean isCheck = false;
    private LocationData locationData;
    private ArrayList<String> tagList = new ArrayList<>();
    private int taskCount;
    private ArrayList<ImageItem> downloadImage = new ArrayList<>();
    private int taskCountDownload;
    private PopupWindow tipPopwindow;
    private boolean oneYuanFlag;
    private boolean oneYuanMode;
    private RecMicToMp3 mRecMicToMp3 = new RecMicToMp3(
            FileUtils.getVoiceDir()
                    + "/voice.mp3", 8000);
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    taskCountDownload--;
                    if (taskCountDownload == 0) {
                        selectedView.setSelectImages(selectBitmap);
                        downloadImage.addAll(selectBitmap);
                        LogUtil.e("downloadImage", downloadImage.size());
                        ProgressDialog.closeProgress();
                    }
                    break;
                case 1:
                    ToastUtil.toast(ReleaseActivity.this, "图片加载失败");
                    ProgressDialog.closeProgress();
                    break;
                case 0x10:
                    // 录音超过20秒自动停止,录音状态设为语音完成
                    if (RECODE_STATE == RECORD_ING) {
                        RECODE_STATE = RECODE_ED;
                        // 如果录音图标正在显示的话,关闭显示图标
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        // 停止录音
                        mRecMicToMp3.stop();
                        voiceValue = 0.0;
                        // 如果录音时长小于1秒，显示录音失败的图标
                        if (recodeTime < MIX_TIME) {
                            recodeTime = 0.0f;
                            mRecMicToMp3.stop();
                            showWarnToast();
                            RECODE_STATE = RECORD_NO;
                            llPlayingState.setVisibility(View.GONE);
                            ivBtnTalk.setVisibility(View.VISIBLE);
                        } else {
                            llPlayingState.setVisibility(View.VISIBLE);
                            int time = Math.round(recodeTime);
                            tvRecordLength.setText(time + "\"");
                            ivBtnTalk.setVisibility(View.GONE);
                        }
                    }
                    break;

                case 0x11:
                    setDialogImage(msg.arg1);
                    break;

                case 0x12:
                    int soundTime = (int) recodeTime;
                    tvSoundTime.setText("录音时间：" + String.valueOf(soundTime));
                    break;

                default:
                    break;
            }
        }
    };
    /**
     * 录音线程
     */
    private Runnable ImageThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            // 如果是正在录音状态
            while (RECODE_STATE == RECORD_ING) {
                if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
                    mHandler.sendEmptyMessage(0x10);
                } else {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        mHandler.sendEmptyMessage(0x12);
                        if (RECODE_STATE == RECORD_ING) {
//							voiceValue = recorder.getAmplitude();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    };
    private long downMillis = 0L;
    private int isAuction;
    private int marketPrice;
    private int sellPrice;
    private int bidDeposit;
    private int bidIncrement;
    private String bidStartTime;
    private String bidEndTime;
    private long bidder;
    private int hasBidDepositPayed;
    private int draftType;//草稿类型
    private boolean fixprice;
    @SuppressLint("HandlerLeak")
    private Handler taskHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    taskCount--;
                    LogUtil.e("taskCount", "taskCount");
                    String key = (String) msg.obj;
                    goodsImageUrl += key + "@!logo" + ",";
                    if (taskCount == 0) {
                        if (CommonPreference.getBooleanValue(CommonPreference.IS_TIP_BING, true)) {
                            CommonPreference.setBooleanValue(CommonPreference.IS_TIP_BING, false);
                            if (!CommonPreference.getBooleanValue(CommonPreference.USER_IS_BIND_WECHAT, false)) {
                                final TextDialog dialog = new TextDialog(ReleaseActivity.this, true);
                                dialog.setContentText("绑定微信账号，使用微信担保交易");
                                dialog.setLeftText("不绑定");
                                dialog.setLeftCall(new DialogCallback() {

                                    @Override
                                    public void Click() {
                                        startRequestForSaveGoodsInfo();
                                    }
                                });
                                dialog.setRightText("去绑定");
                                dialog.setRightCall(new DialogCallback() {

                                    @Override
                                    public void Click() {
                                        tvBtnRelease.setOnClickListener(ReleaseActivity.this);
                                        shareLogin(ReleaseActivity.this, Wechat.NAME);
                                    }
                                });
                                dialog.show();
                                return;
                            }
                            if (!CommonPreference.getBooleanValue(CommonPreference.USER_IS_BIND_ALI, false)) {
                                final TextDialog dialog = new TextDialog(ReleaseActivity.this, true);
                                dialog.setContentText("绑定支付宝账户，为闲置交易安全护航，买卖更便捷更省心");
                                dialog.setLeftText("不绑定");
                                dialog.setLeftCall(new DialogCallback() {

                                    @Override
                                    public void Click() {
                                        startRequestForSaveGoodsInfo();
                                    }
                                });
                                dialog.setRightText("去绑定");
                                dialog.setRightCall(new DialogCallback() {

                                    @Override
                                    public void Click() {
                                        tvBtnRelease.setOnClickListener(ReleaseActivity.this);
                                        AliPayHelper helper = new AliPayHelper(ReleaseActivity.this);
                                        helper.authV2(new AliPayHelper.OnAliPayAuthCompleteListener() {

                                            @Override
                                            public void onComplete(String aliId, String authCode) {
                                                startRequestForBindALi(aliId, authCode);
                                            }
                                        });
                                    }
                                });
                                dialog.show();
                                return;
                            }
                            startRequestForSaveGoodsInfo();
                        } else {
                            startRequestForSaveGoodsInfo();
                        }
                    }
                    break;
                case 1://保存草稿
                    taskCount--;
                    String key1 = (String) msg.obj;
                    goodsImageUrl += key1 + "@!logo" + ",";
                    if (taskCount == 0) {
                        startRequestForCommitDraftBox();
                    }

                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_page);
        if (getIntent().hasExtra(MyConstants.EXTRA_GOODS_DETAIL_ID)) {
            goodsId = getIntent().getStringExtra(MyConstants.EXTRA_GOODS_DETAIL_ID);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_GOODS_EDIT_FROM)) {// editFrom:goods_details_edit，为从详情进入，完成页点击查看详情直接finish当前页
            editFrom = getIntent().getStringExtra(MyConstants.EXTRA_GOODS_EDIT_FROM);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_SELECT_BITMAP)) {
            selectBitmap = (ArrayList<ImageItem>) getIntent().getSerializableExtra(MyConstants.EXTRA_SELECT_BITMAP);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_CAMPAIGN_BEAN)) {
            campaign = (Campaign) getIntent().getSerializableExtra(MyConstants.EXTRA_CAMPAIGN_BEAN);
            LogUtil.e("campaign", campaign);
            LogUtil.d("danielluant", campaign);
//            if (campaign != null) {
//                if (campaign.getCid() == 20) {
//                    fixprice = true;
//                }
//            }
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_CAMPAIGN_ID)) {
            campaignId = getIntent().getIntExtra(MyConstants.EXTRA_CAMPAIGN_ID, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_CAT2_ID) && getIntent().hasExtra(MyConstants.EXTRA_CAT2_NAME)) {
            cat2Name = getIntent().getStringExtra(MyConstants.EXTRA_CAT2_NAME);
            secondClassId = getIntent().getIntExtra(MyConstants.EXTRA_CAT2_ID, 0);
        }
        draftType = mIntent.getIntExtra(MyConstants.DRAFT_TYPE, 0);
        isAuction = mIntent.getIntExtra(MyConstants.IS_AUCTION, 0);
        hArray = getResources().getStringArray(R.array.hours_array);
        mArray = getResources().getStringArray(R.array.minute_array);

        initView();

        initCalendarData();

        initRecMic();

        ArrayList<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag(false, "全新"));
        if (isAuction != 1) {
            tagList.add(new Tag(false, "可当面交易"));
        }
        tagList.add(new Tag(false, "专柜小票"));
        initTagData(tagList);
        if (!TextUtils.isEmpty(goodsId)) {//编辑商品
            startRequestForGetGoodsDetails();
        } else {//发布商品
            startRequestForDraftBoxContainer();
        }
        if (TextUtils.isEmpty(cat2Name) || secondClassId == 0)
            tvClass.setText("必选");
        else
            tvClass.setText(cat2Name);
        visitOneYuanActive();
    }

    private void visitOneYuanActive() {
        oneYuanFlag = mIntent.getBooleanExtra("visit_one_yuan", false);
        if (oneYuanFlag) {
            setOneYuanMode(true);
            mPrice = "1";
            mPop.setPriceText(mPrice);
            tvPricePop.setText(mPrice);
            layoutReleaseTime.setVisibility(View.VISIBLE);
            doRequestForOneSellTime();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tvBtnRelease: // 立即发布
                if (ConfigUtil.isToVerify()) {
                    DialogManager.toVerify(this);
                } else {
                    release2Sever();
                }
                break;
            case R.id.layoutClassTitle:
                if (oneYuanMode) {
                    Intent intent1 = new Intent(this, OneYuanClassificationActivity.class);
                    startActivityForResult(intent1, REQUEST_CODE_FOR_PICK_ONE_YUAN_CLASSIFICATION);
                } else {
                    intent = new Intent(ReleaseActivity.this, GoodsClassActivityNew.class);
                    if (campaign != null) {
                        intent.putExtra(MyConstants.CAMPAIGN_ID, campaign.getCid());
                    }
                    if (campaignId != 0) {
                        intent.putExtra(MyConstants.CAMPAIGN_ID, campaignId);
                    }
                    startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_RELEASESECOND_TO_PROCLASS);
                }

                break;

            case R.id.layoutAgeTitle:
                intent = new Intent(ReleaseActivity.this, BabyAgeActivityNew.class);
                intent.putExtra(MyConstants.EXTRA_CHOOSE_AGE, selectPosition);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_RELEASESECOND_TO_BABYAGE);
                break;
            case R.id.ivBtnTalk:
                break;
            case R.id.rlBtnVoicePlay:
                // 如果不是正在播放
                if (!playState) {
                    // 实例化MediaPlayer对象
                    media = new MediaPlayer();
                    File file = new File(FileUtils.getVoiceDir() + "/voice.mp3");
                    try {
                        // 设置播放资源
                        media.setDataSource(file.getAbsolutePath());
                        // 准备播放
                        media.prepare();
                        // 开始播放
                        media.start();
                        // 改变播放的标志位
                        playState = true;


                        // 设置播放结束时监听
                        media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (playState) {
                                    playState = false;
                                }
                                try {
                                    AnimationDrawable animationDrawable = (AnimationDrawable) ivPlayState.getBackground();
                                    animationDrawable.stop();
                                    ivPlayState.setBackgroundResource(R.drawable.play_state3);
                                } catch (Exception e) {

                                }
                            }
                        });
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        toast("录音文件为空，请先录音。");
                    }
                    ivPlayState.setBackgroundResource(R.drawable.play_state_animation);
                    AnimationDrawable animationDrawable = (AnimationDrawable) ivPlayState.getBackground();
                    animationDrawable.start();
                } else {
                    // 如果正在播放
                    if (media.isPlaying()) {
                        media.stop();
                        playState = false;
                    } else {
                        playState = false;
                    }
                    try {
                        AnimationDrawable animationDrawable = (AnimationDrawable) ivPlayState.getBackground();
                        animationDrawable.stop();
                        ivPlayState.setBackgroundResource(R.drawable.play_state3);
                    } catch (Exception e) {

                    }


                }
                break;

            case R.id.tvBtnVoiceDel:
                if (media != null) {
                    // 如果正在播放
                    if (media.isPlaying()) {
                        media.stop();
                        playState = false;
                    }

                }
                FileUtils.delAllFile(FileUtils.getVoiceDir());
                recodeTime = 0.0f;
                toast("录音已删除");
                llPlayingState.setVisibility(View.GONE);
                ivBtnTalk.setVisibility(View.VISIBLE);
                try {
                    AnimationDrawable animationDrawable = (AnimationDrawable) ivPlayState.getBackground();
                    animationDrawable.stop();
                    ivPlayState.setBackgroundResource(R.drawable.play_state3);
                } catch (Exception e) {

                }
                break;

            case R.id.layoutPrice:
                CommonUtils.showKeyBoard(this);
                if (isCheck) {
                    isCheck = false;
                } else {
                    isCheck = true;
                }
                break;
            case R.id.layoutReleaseTime:
                if (campaign != null) {
                    String sellDate = campaign.getPeroid();
                    String sellBegin = StringUtils.substringBeforeLast(sellDate, ",");
                    String sellEnd = StringUtils.substringAfterLast(sellDate, ",");
                    if (!TextUtils.isEmpty(sellBegin) && !TextUtils.isEmpty(sellEnd)) {
                        if (sellBegin.equals(sellEnd)) {
                            toast("该活动不能修改时间噢");
                        } else {
                            showStartDialog();
                        }
                    } else {
                        showStartDialog();
                    }
                } else {
                    showStartDialog();
                }
                break;
            case R.id.rlShootingTime://拍卖开始时间
                showStartingPriceDialog();
                break;
            case R.id.rlEndTime://拍卖开始时间
                showEndingPriceDialog();
                break;
            case R.id.tvLocation: // 省份选择
                intent = new Intent(ReleaseActivity.this, ProvinceActivity.class);
                intent.putExtra("isLocation", true);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_PROVINCE);
                break;
            case R.id.layoutBrand:
                intent = new Intent(ReleaseActivity.this, BrandListActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_BRANDS);
                break;
            case R.id.rlStartingPrice://起拍价
                etGoodsName.requestFocus();
                Bundle marketData = new Bundle();
                marketData.putInt(MyConstants.PRICE, sellPrice);
                marketData.putInt(MyConstants.PRICE_STYLE, AuctionPriceActivity.PRICE_STYLE_MARKET);
                AuctionPriceActivity.hStartActivityForResult(this, marketData, REQUEST_CODE_FOR_AUCTION_PRICE);
                break;
            case R.id.rlBond://保证金
                etGoodsName.requestFocus();
                Bundle bidDespositData = new Bundle();
                bidDespositData.putInt(MyConstants.PRICE, bidDeposit);
                bidDespositData.putInt(MyConstants.PRICE_STYLE, AuctionPriceActivity.PRICE_STYLE_BID_DESPOSIT);
                AuctionPriceActivity.hStartActivityForResult(this, bidDespositData, REQUEST_CODE_FOR_AUCTION_PRICE);
                break;
            case R.id.rlFareIncrease:
                etGoodsName.requestFocus();
                Bundle bidIncrementData = new Bundle();
                bidIncrementData.putInt(MyConstants.PRICE, bidIncrement);
                bidIncrementData.putInt(MyConstants.PRICE_STYLE, AuctionPriceActivity.PRICE_STYLE_BID_INCREMENT);
                AuctionPriceActivity.hStartActivityForResult(this, bidIncrementData, REQUEST_CODE_FOR_AUCTION_PRICE);
                break;
        }
    }

    private void initTagData(ArrayList<Tag> list) {
        mFlowLayout.removeAllViews();
        for (Tag tag : list) {
            mFlowLayout.addView(getTagLabel(tag),
                    new ViewGroup.MarginLayoutParams(
                            ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                            ViewGroup.MarginLayoutParams.WRAP_CONTENT));
        }
    }

    private TextView getTagLabel(final Tag tag) {
        final TextView label = new TextView(this);
        if (tag.isSelect()) {
            label.setTextColor(getResources().getColor(R.color.white));
            label.setBackgroundResource(R.drawable.text_pink_rect_bg);
            label.setSelected(true);
            tagList.add(tag.getName());
        } else {
            label.setTextColor(getResources().getColor(R.color.text_color));
            label.setBackgroundResource(R.drawable.text_gray_rect_bg);
            label.setSelected(false);
            if (tagList.contains(tag.getName())) {
                tagList.remove(tag.getName());
            }
        }
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        int padding = BGAFlowLayout.dp2px(this, 8);
        int paddingTop = BGAFlowLayout.dp2px(this, 4);
        label.setPadding(padding, paddingTop, padding, paddingTop);
        label.setText(tag.getName());
        label.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (label.isSelected()) {
                    label.setSelected(false);
                    label.setTextColor(getResources().getColor(R.color.text_color));
                    label.setBackgroundResource(R.drawable.text_gray_rect_bg);
                    if (tagList.contains(tag.getName())) {
                        tagList.remove(tag.getName());
                    }
                } else {
                    label.setSelected(true);
                    label.setTextColor(getResources().getColor(R.color.white));
                    label.setBackgroundResource(R.drawable.text_pink_rect_bg);
                    tagList.add(tag.getName());
                }
            }
        });
        return label;
    }

    @Override
    public void onBackPressed() {
        exitRelease();

    }

    private void exitRelease() {
        boolean flag = hasContent();
        if (flag && TextUtils.isEmpty(goodsId)) {//有内容
            final TextDialog dialog = new TextDialog(this, true);
            dialog.setContentText("是否保存为草稿，以便再次继续使用？");
            dialog.setLeftText("否");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    // 删除本地录音
                    FileUtils.delAllFile(FileUtils.getVoiceDir());
                    // 删除本地图片
                    FileUtils.delAllFile(FileUtils.getIconDir());
                    if (goodsInfo != null) {
                        long draftId = goodsInfo.getDraftId();
                        if (draftId > 0) {
                            startRequestForDelDraftBox(true);
                        } else {
                            finish();
                        }
                    } else {
                        finish();
                    }

                }
            });
            dialog.setRightText("是");
            dialog.setRightCall(new DialogCallback() {

                @Override
                public void Click() {
                    ProgressDialog.showProgress(ReleaseActivity.this);
                    File file = new File(FileUtils.getVoiceDir() + "/voice.mp3");
                    if (recodeTime >= MIX_TIME && file.exists()) {
                        commitSoundDraftBox2Ali();
                    } else {
                        commitDraftBox2Ali();
                    }
                }
            });
            dialog.show();
        } else {
            if (!TextUtils.isEmpty(goodsId)) {
                final TextDialog dialog = new TextDialog(this, false);
                dialog.setContentText("您确定取消这次发布吗？");
                dialog.setLeftText("取消");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("确定");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        // 删除本地录音
                        FileUtils.delAllFile(FileUtils.getVoiceDir());
                        // 删除本地图片
                        FileUtils.delAllFile(FileUtils.getIconDir());
                        finish();
                    }
                });
                dialog.show();
            } else {
                super.onBackPressed();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_RELEASESECOND_TO_PROCLASS) {
                firstClassId = data.getIntExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_FIRST_CLASS_ID, 0);
                secondClassId = data.getIntExtra(MyConstants.EXTRA_GOODS_DETAIL_FILTER_SECOND_CLASS_ID, 0);
                String childName = data.getStringExtra(MyConstants.EXTRA_CHOOSE_CLASS_CHILDNAME);
                tvClass.setText(childName);
                if (firstClassId == 17 || firstClassId == 18 || firstClassId == 19 || firstClassId == 21) { // 辣妈专区 || 奶爸专区 ||运动季
                    Age age = new Age();
                    age.setAgeId(8);
                    age.setAgeTitle("全年龄段");
                    age.setSequence(8);
                    ArrayList<Age> list = new ArrayList<>();
                    list.add(age);
                    selectPosition = list;
                    ageId = "8";
                    ageName = "全年龄段";
                    tvAge.setText(ageName);
                    layoutAgeTitle.setVisibility(View.GONE);
                } else if (secondClassId == 1010 || secondClassId == 1020 || secondClassId == 1030 || secondClassId == 1040 || secondClassId == 1050 // 孕妈用品
                        || secondClassId == 1220 || secondClassId == 1230//家居电器
                        || secondClassId == 1610 || secondClassId == 1620 || secondClassId == 1630 || secondClassId == 1640 || secondClassId == 1650 || secondClassId == 1660 // 家居电器
                        ) {  // 婴儿洗护

                    Age age = new Age();
                    age.setAgeId(8);
                    age.setAgeTitle("全年龄段");
                    age.setSequence(8);
                    ArrayList<Age> list = new ArrayList<>();
                    list.add(age);
                    selectPosition = list;
                    ageId = "8";
                    ageName = "全年龄段";
                    tvAge.setText(ageName);
                    layoutAgeTitle.setVisibility(View.GONE);
                } else {
                    Age age = new Age();
                    age.setAgeId(8);
                    age.setAgeTitle("全年龄段");
                    age.setSequence(8);
                    ArrayList<Age> list = new ArrayList<>();
                    list.add(age);
                    selectPosition = list;
                    ageId = "8";
                    ageName = "全年龄段";
                    tvAge.setText(ageName);
                    layoutAgeTitle.setVisibility(View.VISIBLE);
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_RELEASESECOND_TO_BABYAGE) {
                ageId = "";
                ageName = "";
                tvAge.setText(ageName);

                //获取返回的age集合
                selectPosition = (ArrayList<Age>) data.getSerializableExtra(MyConstants.EXTRA_CHOOSE_AGE);
                //遍历集合，保存ageId以及显示名称
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < selectPosition.size(); i++) {
                    //获取age
                    Age age = selectPosition.get(i);

                    if (age != null) {
                        String tmp = String.valueOf(age.getAgeId());
                        sb.append(tmp).append(",");
                        ageName = age.getAgeTitle();

                        //追加名称
                        tvAge.append(ageName + " ");
                    }
                }
                if (sb.toString().length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                ageId = sb.toString();
                //如果没有选中 设置为  必选
                if (ageName.equals("")) {
                    tvAge.setText("必选");
                }

            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_RELEASETHIRD_TO_CHOOSECITY) {
                city = data.getStringExtra(MyConstants.EXTRA_CHOOSE_CITYNAME);
                district = data.getStringExtra(MyConstants.EXTRA_CHOOSE_DISNAME);
                tvLocation.setText(city + " " + district);
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_PROVINCE) {
                if (data == null) {
                    return;
                }
                province = "";
                city = "";
                district = "";
                if (data.hasExtra(MyConstants.EXTRA_REGION)) {
                    Region regionData = (Region) data.getSerializableExtra(MyConstants.EXTRA_REGION);
                    province = regionData.getName();
                }
                if (data.hasExtra(MyConstants.EXTRA_CITY)) {
                    City cityData = (City) data.getSerializableExtra(MyConstants.EXTRA_CITY);
                    city = cityData.getName();
                }
                if (data.hasExtra(MyConstants.EXTRA_DISTRICT)) {
                    District districtData = (District) data.getSerializableExtra(MyConstants.EXTRA_DISTRICT);
                    district = districtData.getName();
                }
                if (TextUtils.isEmpty(city)) {
                    city = province;
                    tvLocation.setText(province + " " + district);
                } else {
                    if (city.equals(province)) {
                        tvLocation.setText(province + " " + district);
                    } else {
                        tvLocation.setText(province + " " + city + " " + district);
                    }
                }
            } else if (requestCode == REQUEST_CODE_FOR_1_YUAN) {//1元返回
                if (data != null) {
                    setOneYuanMode(true);
                    CommonUtils.showKeyBoard(this);
                    mPop.showAtLocation(mRoot, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    doRequestForOneSellTime();
                }
            } else if (requestCode == REQUEST_CODE_FOR_PICK_ONE_YUAN_CLASSIFICATION) {
                if (data != null) {
                    SecondaryClassification result = (SecondaryClassification) data.getSerializableExtra("classification");
                    secondClassId = result.getClassificationId();
                    firstClassId = result.getGoodsFirstCatesId();
                    if (secondClassId == 2020) {
                        layoutAgeTitle.setVisibility(View.VISIBLE);
                    } else {
                        layoutAgeTitle.setVisibility(View.GONE);
                    }

                    ageId = "8";
                    ageName = "全年龄段";
                    tvAge.setText(ageName);

                    tvClass.setText(result.getClassificationName());
                }
            } else if (requestCode == MyConstants.REQUEST_CODE_FOR_VIDEO) {
                selectedView.onActivityResult(requestCode, resultCode, data);
            } else if (requestCode == REQUEST_CODE_FOR_BRANDS) {
                if (data != null) {
                    Brand goodsBrand = (Brand) data.getSerializableExtra(MyConstants.BRAND_RESULT);
                    if (goodsBrand != null) {
                        brand = goodsBrand.brandName;
                        brandId = goodsBrand.brandId;
                        tvBrandPop.setText(brand);
                    }
                }
            } else if (requestCode == REQUEST_CODE_FOR_AUCTION_PRICE) {
                if (data != null) {
                    int price = data.getIntExtra(MyConstants.PRICE, 0);
                    int priceStyle = data.getIntExtra(MyConstants.PRICE_STYLE, 0);
                    if (priceStyle == AuctionPriceActivity.PRICE_STYLE_MARKET) {
                        sellPrice = price;
                        tvStatingPrice.setText(String.valueOf(price));
                    } else if (priceStyle == AuctionPriceActivity.PRICE_STYLE_BID_DESPOSIT) {
                        bidDeposit = price;
                        tvBond.setText(String.valueOf(price));
                    } else if (priceStyle == AuctionPriceActivity.PRICE_STYLE_BID_INCREMENT) {
                        bidIncrement = price;
                        tvFareIncrease.setText(String.valueOf(price));
                    }

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            CommonUtils.hideKeyBoard(ReleaseActivity.this);
                        }
                    }, 200);
                }
            } else {
                if (data == null) {
                    return;
                }
                selectedView.onActivityResult(requestCode, resultCode, data);
                selectBitmap.clear();
                selectBitmap = selectedView.getSelectImg();
            }
        }
    }

    private void setOneYuanMode(boolean flag) {
        oneYuanMode = flag;
        mPop.setOneYuanMode(flag);
        if (flag) {
            layoutReleaseTime.setVisibility(View.VISIBLE);
            layoutReleaseTime.setOnClickListener(null);
            tvBtnRelease.setText("发布我的一元好物");
            layoutAgeTitle.setVisibility(View.GONE);
            firstClassId = 20;
            draftType = 3;
        } else {
            int level = CommonPreference.getIntValue(CommonPreference.USER_LEVEL, 0);
            if (level <= 1 && campaign == null) {
                layoutReleaseTime.setVisibility(View.GONE);
            } else {
                layoutReleaseTime.setVisibility(View.VISIBLE);
            }

            sellTime = "";
            tvReleaseTime.setText("立即发布");
            layoutReleaseTime.setOnClickListener(this);
            tvBtnRelease.setText("立即发布");

            if (campaign != null) {
                String ages = campaign.getAges();
                if (!TextUtils.isEmpty(ages)) {
                    layoutAgeTitle.setVisibility(View.VISIBLE);
                } else {
                    layoutAgeTitle.setVisibility(View.GONE);
                }

            } else {
                layoutAgeTitle.setVisibility(View.VISIBLE);
            }
            firstClassId = 0;
            draftType = 1;
        }


        if (TextUtils.isEmpty(cat2Name))
            tvClass.setText("必选");
        else
            tvClass.setText(cat2Name);

        ageId = "";
        tvAge.setText("必选");
    }

    @Override
    public void onCheckedChanged(View v, boolean checked) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.delAllFile(FileUtils.getVoiceDir());
    }

    @Override
    public void initTitleBar() {
        super.initTitleBar();
        getTitleBar().
                setTitle("发布").
                setLeftText("取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }).
                setRightText("发布秘籍", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (campaign != null) {
                            Intent intent = new Intent(ReleaseActivity.this, WebViewActivity2.class);
                            intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.CAMPAIGN_RULE + campaign.getCid());
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        } else {
                            Intent intent = new Intent(ReleaseActivity.this, WebViewActivity.class);
                            intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.WEBVIEW_RELEASE_SKILL);
                            intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "发布秘籍");
                            startActivity(intent);
                        }
                    }
                });


    }

    private void initPopTip(View v) {
        if (tipPopwindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.publish_rule_pic, null);
            tipPopwindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tipPopwindow.setFocusable(true);
            tipPopwindow.setOutsideTouchable(true);
            View viewRoot = view.findViewById(R.id.llContent);
            viewRoot.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tipPopwindow.dismiss();
                }
            });
            // 设置pop外部点击隐藏pop
            tipPopwindow.setBackgroundDrawable(new BitmapDrawable());
        }
        tipPopwindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    /**
     * @return void
     * @Title: initGridView
     * @Description: 初始化GridView
     * @author liang_xs
     */
    private void initView() {
        ShareSDK.initSDK(getApplicationContext());
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 如果是正在录音
                if (RECODE_STATE == RECORD_ING) {
                    RECODE_STATE = RECODE_ED;
                    // 如果录音图标正在显示,关闭
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    // 停止录音
                    mRecMicToMp3.stop();
                    voiceValue = 0.0;
                    if (recodeTime < MIX_TIME) {
                        recodeTime = 0.0f;
                        mRecMicToMp3.stop();
                        showWarnToast();
                        RECODE_STATE = RECORD_NO;
                        llPlayingState.setVisibility(View.GONE);
                        ivBtnTalk.setVisibility(View.VISIBLE);
                    } else {
                        llPlayingState.setVisibility(View.VISIBLE);
                        int time = Math.round(recodeTime);
                        tvRecordLength.setText(time + "\"");
                        ivBtnTalk.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

        layoutPrice.setOnClickListener(this);

        layoutReleaseTime.setOnClickListener(this);
        // 播放语音
        rlBtnVoicePlay.setOnClickListener(this);
        layoutClassTitle.setOnClickListener(this);
        if (campaign != null) {
            String ages = campaign.getAges();
            if (!TextUtils.isEmpty(ages)) {
                layoutAgeTitle.setVisibility(View.VISIBLE);
            } else {
                layoutAgeTitle.setVisibility(View.GONE);
            }
            getTitleBar().getTitleTextRight().setText("发布规则");
        } else {
            layoutAgeTitle.setVisibility(View.VISIBLE);
            getTitleBar().getTitleTextRight().setText("发布秘籍");
        }
        layoutAgeTitle.setOnClickListener(this);
        etProDes.setOnClickListener(this);
        ivBtnTalk.setOnClickListener(this);
        tvBtnVoiceDel.setOnClickListener(this);
        tvBtnRelease.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
        layoutBrand.setOnClickListener(this);
        rlShootingTime.setOnClickListener(this);
        rlEndTime.setOnClickListener(this);
        rlStartingPrice.setOnClickListener(this);
        rlBond.setOnClickListener(this);
        rlFareIncrease.setOnClickListener(this);

        // 录音按钮监听
        ivBtnTalk.setOnTouchListener(this);
        initEvent(etGoodsName);
        initPopupWindowAndKeyboardState();
        etProDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String content = etProDes.getText().toString();
                tvInputCount.setText(content.length() + "/"
                        + "200");
            }

        });

        if (isAuction == 1) {
            llAuction.setVisibility(View.VISIBLE);
            layoutPrice.setVisibility(View.GONE);
            layoutReleaseTime.setVisibility(View.GONE);
            mPastPrice = "";
        } else {
            llAuction.setVisibility(View.GONE);
            layoutPrice.setVisibility(View.VISIBLE);
        }
    }

    private void initCalendarData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        Date currentDay = DateTimeUtils.getDate(str, "yyyy年MM月dd日");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDay);
        for (int i = 0; i < dArray.length; i++) {
            long t = ((long) i) * 24L * 60L * 60L * 1000L;
            long time = calendar.getTimeInMillis();
            long millis = t + time;
            dArray[i] = DateTimeUtils.getDateStr(millis, "yyyy年MM月dd日");
        }
        selectD = 0;
        calendar.setTime(new Date());
        selectStartH = calendar.get(Calendar.HOUR_OF_DAY);
        selectStartM = calendar.get(Calendar.MINUTE);
    }

    private void initCalendarData(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(timeMillis);// 获取当前时间
        String str = formatter.format(curDate);
        Date currentDay = DateTimeUtils.getDate(str, "yyyy年MM月dd日");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDay);
        for (int i = 0; i < dArray.length; i++) {
            long t = ((long) i) * 24L * 60L * 60L * 1000L;
            long time = calendar.getTimeInMillis();
            long millis = t + time;
            dArray[i] = DateTimeUtils.getDateStr(millis, "yyyy年MM月dd日");
        }
        selectD = 0;
        calendar.setTime(new Date());
        selectStartH = calendar.get(Calendar.HOUR_OF_DAY);
        selectStartM = calendar.get(Calendar.MINUTE);
    }

    private void setSelectDate(String sellDate) {
        if (TextUtils.isEmpty(sellDate)) {
            return;
        }
        String YMD = DateTimeUtils.getYearMonthDayText(Long.valueOf(sellDate));
        for (int i = 0; i < dArray.length; i++) {
            if (dArray[i].equals(YMD)) {
                selectD = i;
                break;
            }
        }
        String Hour = DateTimeUtils.getHour(Long.valueOf(sellDate));
        for (int i = 0; i < hArray.length; i++) {
            if (hArray[i].equals(Hour)) {
                selectStartH = i;
                break;
            }
        }
        String Minute = DateTimeUtils.getMinute(Long.valueOf(sellDate));
        for (int i = 0; i < mArray.length; i++) {
            if (mArray[i].equals(Minute)) {
                selectStartM = i;
                break;
            }
        }
    }

    private void setSelectStartingPriceDate(long startingPriceDate) {

        String YMD = DateTimeUtils.getYearMonthDayText(startingPriceDate);
        for (int i = 0; i < dArray.length; i++) {
            if (dArray[i].equals(YMD)) {
                selectStartingPriceD = i;
                break;
            }
        }
        String Hour = DateTimeUtils.getHour(Long.valueOf(startingPriceDate));
        for (int i = 0; i < hArray.length; i++) {
            if (hArray[i].equals(Hour)) {
                selectStartingPriceH = i;
                break;
            }
        }
        String Minute = DateTimeUtils.getMinute(Long.valueOf(startingPriceDate));
        for (int i = 0; i < mArray.length; i++) {
            if (mArray[i].equals(Minute)) {
                selectStartingPriceM = i;
                break;
            }
        }
    }


    private void setSelectEndingPriceDate(long endingPriceDate) {

        String YMD = DateTimeUtils.getYearMonthDayText(endingPriceDate);
        for (int i = 0; i < dArray.length; i++) {
            if (dArray[i].equals(YMD)) {
                selectEndingPriceD = i;
                break;
            }
        }
        String Hour = DateTimeUtils.getHour(Long.valueOf(endingPriceDate));
        for (int i = 0; i < hArray.length; i++) {
            if (hArray[i].equals(Hour)) {
                selectEndingPriceH = i;
                break;
            }
        }
        String Minute = DateTimeUtils.getMinute(Long.valueOf(endingPriceDate));
        for (int i = 0; i < mArray.length; i++) {
            if (mArray[i].equals(Minute)) {
                selectEndingPriceM = i;
                break;
            }
        }
    }

    public void showStartDialog() {
        dialogTime = new Dialog(ReleaseActivity.this, R.style.ClassCount);
        dialogTime.setContentView(R.layout.dialog_pre_sell_time);
        Window dialogWindow = dialogTime.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialogWindow.setWindowAnimations(R.style.shareDialog);
        dialogTime.show();
        dialogTime.findViewById(R.id.dialog_cancle).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                npStartD.setValue(0);
                npStartH.setValue(calendar.get(Calendar.HOUR_OF_DAY));
                npStartM.setValue(calendar.get(Calendar.MINUTE));
            }
        });
        dialogTime.findViewById(R.id.dialog_submit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sellTime = DateTimeUtils.getTime("yyyy年MM月dd日 HH:mm",
                        dArray[npStartD.getValue()] + " " + hArray[npStartH.getValue()] + ":" + mArray[npStartM.getValue()]);
                long millis = Long.valueOf(sellTime);
                Calendar calendar = Calendar.getInstance();
                long time = calendar.getTimeInMillis();
                if (millis < time) {
                    toast("出售时间必须大于当前时间");
                    return;
                }
                selectD = npStartD.getValue();
                selectStartH = npStartH.getValue();
                selectStartM = npStartM.getValue();
                if (dialogTime.isShowing()) {
                    dialogTime.dismiss();
                }
                tvReleaseTime.setText(dArray[selectD] + " " + hArray[selectStartH] + ":" + mArray[selectStartM]);
                LogUtil.i("liang",
                        DateTimeUtils.getTime("yyyy年MM月dd日 HH:mm", dArray[selectD] + " " + hArray[selectStartH]));
                LogUtil.i("liang",
                        DateTimeUtils.getDateStr(
                                Long.valueOf(DateTimeUtils.getTime("yyyy年MM月dd日 HH:mm",
                                        dArray[selectD] + " " + hArray[selectStartH] + ":" + mArray[selectStartM])),
                                "yyyy年MM月dd日 HH:mm"));
            }
        });

        npStartD = (NumberPicker) dialogTime.findViewById(R.id.np_D);
        npStartH = (NumberPicker) dialogTime.findViewById(R.id.np_H);
        npStartM = (NumberPicker) dialogTime.findViewById(R.id.np_M);

        npStartD.setDisplayedValues(dArray);
        npStartD.setMaxValue(dArray.length - 1);
        npStartD.setMinValue(0);
        npStartD.setValue(selectD);
        // 设置是否可以来回滚动
        npStartD.setWrapSelectorWheel(true);

        npStartH.setDisplayedValues(hArray);
        npStartH.setMaxValue(hArray.length - 1);
        npStartH.setMinValue(0);
        npStartH.setValue(selectStartH);

        npStartM.setDisplayedValues(mArray);
        npStartM.setMaxValue(mArray.length - 1);
        npStartM.setMinValue(0);
        npStartM.setValue(selectStartM);

        npStartD.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        npStartH.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        npStartM.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
    }


    public void showStartingPriceDialog() {
        dialogStartingPrieTime = new Dialog(ReleaseActivity.this, R.style.ClassCount);
        dialogStartingPrieTime.setContentView(R.layout.dialog_pre_sell_time);
        Window dialogWindow = dialogStartingPrieTime.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialogWindow.setWindowAnimations(R.style.shareDialog);
        dialogStartingPrieTime.show();
        dialogStartingPrieTime.findViewById(R.id.dialog_cancle).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                npStartingPriceD.setValue(0);
                npStartingPriceH.setValue(calendar.get(Calendar.HOUR_OF_DAY));
                npStartingPriceM.setValue(calendar.get(Calendar.MINUTE));
            }
        });
        dialogStartingPrieTime.findViewById(R.id.dialog_submit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                bidStartTime = DateTimeUtils.getTime("yyyy年MM月dd日 HH:mm",
                        dArray[npStartingPriceD.getValue()] + " " + hArray[npStartingPriceH.getValue()] + ":" + mArray[npStartingPriceM.getValue()]);
                long millis = Long.valueOf(bidStartTime);
                Calendar calendar = Calendar.getInstance();
                long time = calendar.getTimeInMillis();
                if (millis < time) {
                    toast("开拍时间必须大于当前时间");
                    bidStartTime = "";
                    tvShootingTime.setText("");
                    return;
                }
                if (millis - time > 1000 * 60 * 60 * 24 * 10) {
                    toast("开拍时间不得晚于当前10天");
                    bidStartTime = "";
                    tvShootingTime.setText("");
                    return;

                }

//				if(!TextUtils.isEmpty(bidEndTime)){
//					long endMillis = Long.parseLong(bidEndTime);
//					if(endMillis - millis > 1000*60*60*24*15){
//						toast("开拍时间不能早于结束时间15天");
//						bidStartTime = "";
//						tvShootingTime.setText("");
//						return;
//					}
//
//				}
                selectStartingPriceD = npStartingPriceD.getValue();
                selectStartingPriceH = npStartingPriceH.getValue();
                selectStartingPriceM = npStartingPriceM.getValue();
                if (dialogStartingPrieTime.isShowing()) {
                    dialogStartingPrieTime.dismiss();
                }
                tvShootingTime.setText(dArray[selectStartingPriceD] + " " + hArray[selectStartingPriceH] + ":" + mArray[selectStartingPriceM]);

            }
        });

        npStartingPriceD = (NumberPicker) dialogStartingPrieTime.findViewById(R.id.np_D);
        npStartingPriceH = (NumberPicker) dialogStartingPrieTime.findViewById(R.id.np_H);
        npStartingPriceM = (NumberPicker) dialogStartingPrieTime.findViewById(R.id.np_M);

        npStartingPriceD.setDisplayedValues(dArray);
        npStartingPriceD.setMaxValue(dArray.length - 1);
        npStartingPriceD.setMinValue(0);
        npStartingPriceD.setValue(selectStartingPriceD);
        // 设置是否可以来回滚动
        npStartingPriceD.setWrapSelectorWheel(true);

        npStartingPriceH.setDisplayedValues(hArray);
        npStartingPriceH.setMaxValue(hArray.length - 1);
        npStartingPriceH.setMinValue(0);
        npStartingPriceH.setValue(selectStartingPriceH);

        npStartingPriceM.setDisplayedValues(mArray);
        npStartingPriceM.setMaxValue(mArray.length - 1);
        npStartingPriceM.setMinValue(0);
        npStartingPriceM.setValue(selectStartingPriceM);
    }


    public void showEndingPriceDialog() {
        dialogEndPriceTime = new Dialog(ReleaseActivity.this, R.style.ClassCount);
        dialogEndPriceTime.setContentView(R.layout.dialog_pre_sell_time);
        Window dialogWindow = dialogEndPriceTime.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialogWindow.setWindowAnimations(R.style.shareDialog);
        dialogEndPriceTime.show();
        dialogEndPriceTime.findViewById(R.id.dialog_cancle).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                npEndingPriceD.setValue(0);
                npEndingPriceH.setValue(calendar.get(Calendar.HOUR_OF_DAY));
                npEndingPriceM.setValue(calendar.get(Calendar.MINUTE));
            }
        });
        dialogEndPriceTime.findViewById(R.id.dialog_submit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                bidEndTime = DateTimeUtils.getTime("yyyy年MM月dd日 HH:mm",
                        dArray[npEndingPriceD.getValue()] + " " + hArray[npEndingPriceH.getValue()] + ":" + mArray[npEndingPriceM.getValue()]);
                long millis = Long.valueOf(bidEndTime);
                Calendar calendar = Calendar.getInstance();
                long time = calendar.getTimeInMillis();
                if (millis < time) {
                    toast("结束时间必须大于当前时间");
                    return;
                }

                if (!TextUtils.isEmpty(bidStartTime)) {
                    long startMillis = Long.parseLong(bidStartTime);
                    if (millis - startMillis > 1000 * 60 * 60 * 24 * 15) {
                        toast("结束时间不能晚于开拍时间15天");
                        bidEndTime = "";
                        tvEndTime.setText("");
                        return;
                    }

                }
                selectEndingPriceD = npEndingPriceD.getValue();
                selectEndingPriceH = npEndingPriceH.getValue();
                selectEndingPriceM = npEndingPriceM.getValue();
                if (dialogEndPriceTime.isShowing()) {
                    dialogEndPriceTime.dismiss();
                }
                tvEndTime.setText(dArray[selectEndingPriceD] + " " + hArray[selectEndingPriceH] + ":" + mArray[selectEndingPriceM]);

            }
        });

        npEndingPriceD = (NumberPicker) dialogEndPriceTime.findViewById(R.id.np_D);
        npEndingPriceH = (NumberPicker) dialogEndPriceTime.findViewById(R.id.np_H);
        npEndingPriceM = (NumberPicker) dialogEndPriceTime.findViewById(R.id.np_M);

        npEndingPriceD.setDisplayedValues(dArray);
        npEndingPriceD.setMaxValue(dArray.length - 1);
        npEndingPriceD.setMinValue(0);
        npEndingPriceD.setValue(selectEndingPriceD);
        // 设置是否可以来回滚动
        npEndingPriceD.setWrapSelectorWheel(true);

        npEndingPriceH.setDisplayedValues(hArray);
        npEndingPriceH.setMaxValue(hArray.length - 1);
        npEndingPriceH.setMinValue(0);
        npEndingPriceH.setValue(selectEndingPriceH);

        npEndingPriceM.setDisplayedValues(mArray);
        npEndingPriceM.setMaxValue(mArray.length - 1);
        npEndingPriceM.setMinValue(0);
        npEndingPriceM.setValue(selectEndingPriceM);
    }


    private void initEvent(EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /* 判断是否是“done”键 */
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    /* 隐藏软键盘 */
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void initGoodsInfo() {
        if (goodsInfo == null) {
            return;
        }
        if (goodsInfo.getCampaignId() != 0) {
            int cid = goodsInfo.getCampaignId();
            ArrayList<Campaign> list = CommonPreference.getCampaigns();
            if (!ArrayUtil.isEmpty(list)) {
                for (Campaign c : list) {
                    if (c.getCid() == cid) {
                        campaign = c;
                        initCampaignData();
                        break;
                    }
                }
            }
        }
        initGoods();
    }

    private void initCampaignData() {
        if (campaign == null) {
            return;
        }
        String sellDate = campaign.getPeroid();
        String sellBegin = StringUtils.substringBeforeLast(sellDate, ",");
        setSelectDate(sellBegin);
        if (!TextUtils.isEmpty(sellBegin)) {
            long currentTime = System.currentTimeMillis();
            long beginTime = Long.valueOf(sellBegin);
            if (currentTime <= beginTime) {
                sellDate = DateTimeUtils.getYearMonthDayHourMinute(beginTime);
                initCalendarData(beginTime);
            } else if (currentTime > beginTime) {
                sellDate = DateTimeUtils.getYearMonthDayHourMinute(currentTime);
                initCalendarData(currentTime);
            }

        } else {
            sellDate = "立即发布";
        }
        sellTime = String.valueOf(sellBegin);
        tvReleaseTime.setText(sellDate);
        int[] prices = campaign.getPrices();
        if (prices != null && prices.length == 1 && prices[0] != 0) {
            mPrice = String.valueOf(prices[0]);
            tvPricePop.setText(mPrice);
            mPop.setPriceText(mPrice);
            mPop.setPriceInput(false);
        }
    }

    private void initGoods() {
        if (!TextUtils.isEmpty(goodsInfo.getSound())) {
            startRequestForDownloadSound(goodsInfo.getSound(), FileUtils.getVoiceDir(), "/voice.mp3");
        } else {
            //图片数目不为0
            if (!ArrayUtil.isEmpty(goodsInfo.getGoodsImgs())) {
                taskCountDownload = goodsInfo.getGoodsImgs().size();
                for (final String picUrl : goodsInfo.getGoodsImgs()) {
                    savePic(picUrl);
                }
            }
        }

        //有视频
        if (!TextUtils.isEmpty(goodsInfo.getVideoCover()) && !TextUtils.isEmpty(goodsInfo.getVideoPath())) {
            VideoBean bean = new VideoBean();
            bean.setVideoCover(goodsInfo.getVideoCover());
            bean.setVideoPath(goodsInfo.getVideoPath());
            selectedView.setVideoBean(bean);
        }

        initReleaseTime();
        if (!TextUtils.isEmpty(goodsInfo.getGoodsName())) {
            etGoodsName.setText(goodsInfo.getGoodsName());
            etGoodsName.setSelection(goodsInfo.getGoodsName().length());
        }
        if (!TextUtils.isEmpty(goodsInfo.getGoodsBrand())) {
            tvBrandPop.setText(goodsInfo.getGoodsBrand());
            brand = goodsInfo.getGoodsBrand();
            brandId = goodsInfo.goodsBrandId;
        }


        if (campaign != null) {
            String ages = campaign.getAges();
            if (!TextUtils.isEmpty(ages)) {
                layoutAgeTitle.setVisibility(View.VISIBLE);
            } else {
                layoutAgeTitle.setVisibility(View.VISIBLE);
            }

            getTitleBar().getTitleTextRight().setText("发布规则");
        } else {
            layoutAgeTitle.setVisibility(View.VISIBLE);
            getTitleBar().getTitleTextRight().setText("发布秘籍");
        }


        firstClassId = goodsInfo.getCfId();
        if (firstClassId == 17) {
            layoutAgeTitle.setVisibility(View.GONE);
        } else if (firstClassId == 18) {
            layoutAgeTitle.setVisibility(View.GONE);
        } else if (firstClassId == 19) {
            layoutAgeTitle.setVisibility(View.GONE);
        } else if (firstClassId == 21) {
            layoutAgeTitle.setVisibility(View.GONE);
        } else if (firstClassId == 22) {
            layoutAgeTitle.setVisibility(View.GONE);
        } else if (firstClassId == 20) {
            setOneYuanMode(true);
        } else {
            layoutAgeTitle.setVisibility(View.VISIBLE);
        }
        tvClass.setText(goodsInfo.getScfName());
        ArrayList<Age> ages = goodsInfo.getAgeList();
        selectPosition = ages;
        if (!ArrayUtil.isEmpty(ages)) {
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder stringBuilder1 = new StringBuilder();
            for (Age age : ages) {
                if (age != null) {
                    String title = age.getAgeTitle();
                    if (!TextUtils.isEmpty(title)) {
                        stringBuilder.append(title).append(" ");
                    }

                    int ageId = age.getAgeId();
                    if (ageId > 0) {
                        stringBuilder1.append(ageId).append(",");
                    }
                }
            }
            if (!TextUtils.isEmpty(stringBuilder.toString())) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            ageName = stringBuilder.toString();

            if (!TextUtils.isEmpty(stringBuilder1.toString())) {
                stringBuilder1.deleteCharAt(stringBuilder1.length() - 1);
            }
            ageId = stringBuilder1.toString();
            tvAge.setText(ageName);
        }
        int isNew = goodsInfo.getIsNew();
        int hasReceipt = goodsInfo.getHasReceipt();
        int supportFaceToFace = goodsInfo.getSupportFaceToFace();
        ArrayList<Tag> list = new ArrayList<>();
        if (isNew == 1) {
            list.add(new Tag(true, "全新"));
        } else {
            list.add(new Tag(false, "全新"));
        }

        isAuction = goodsInfo.isAuction;
        if (isAuction != 1) {
            if (supportFaceToFace == 1) {
                list.add(new Tag(true, "可当面交易"));
            } else {
                list.add(new Tag(false, "可当面交易"));
            }
        }
        if (hasReceipt == 1) {
            list.add(new Tag(true, "专柜小票"));
        } else {
            list.add(new Tag(false, "专柜小票"));
        }
        initTagData(list);
        if (!TextUtils.isEmpty(goodsInfo.getGoodsContent())) {
            etProDes.setText(goodsInfo.getGoodsContent());
            etProDes.setSelection(goodsInfo.getGoodsContent().length());
        }

        secondClassId = goodsInfo.getScfId();

        if (secondClassId == 1010 || secondClassId == 1020 || secondClassId == 1030 || secondClassId == 1040 || secondClassId == 1050 // 孕妈用品
                || secondClassId == 1220 || secondClassId == 1230//家居电器
                || secondClassId == 1610 || secondClassId == 1620 || secondClassId == 1630 || secondClassId == 1640 || secondClassId == 1650 || secondClassId == 1660 || secondClassId == 2010 || secondClassId == 2030) {
            layoutAgeTitle.setVisibility(View.GONE);
        } else {
            layoutAgeTitle.setVisibility(View.VISIBLE);
        }
        recodeTime = goodsInfo.getSoundTime();
        Area area = goodsInfo.getArea();
        if (area != null) {
            province = area.getProvince();
            city = area.getCity();
            district = area.getArea();
            if (!TextUtils.isEmpty(province)) {
                if (province.equals(city)) {
                    tvLocation.setText(province + " " + district);
                } else {
                    tvLocation.setText(province + " " + city + " " + district);
                }
            }
        }


        if (isAuction == 1) {
            llAuction.setVisibility(View.VISIBLE);
            layoutPrice.setVisibility(View.GONE);
            layoutReleaseTime.setVisibility(View.GONE);
            mPastPrice = "";
            sellPrice = goodsInfo.sellPric;
            bidDeposit = goodsInfo.bidDeposit;
            bidIncrement = goodsInfo.bidIncrement;
            bidStartTime = String.valueOf(goodsInfo.bidStartTime);
            bidEndTime = String.valueOf(goodsInfo.bidEndTime);
            hasBidDepositPayed = goodsInfo.hasBidDepositPayed;
            bidder = goodsInfo.bidder;

            tvStatingPrice.setText(String.valueOf(sellPrice));
            tvBond.setText(String.valueOf(bidDeposit));
            tvFareIncrease.setText(String.valueOf(bidIncrement));
            tvShootingTime.setText(DateTimeUtils.getStringDate(goodsInfo.bidStartTime, DateTimeUtils.YYYY_MM_DD_HH_MM_CHINESE));
            setSelectStartingPriceDate(goodsInfo.bidStartTime);
            tvEndTime.setText(DateTimeUtils.getStringDate(goodsInfo.bidEndTime, DateTimeUtils.YYYY_MM_DD_HH_MM_CHINESE));
            setSelectEndingPriceDate(goodsInfo.bidEndTime);
        } else {
            llAuction.setVisibility(View.GONE);
            layoutPrice.setVisibility(View.VISIBLE);
            mPrice = String.valueOf(goodsInfo.getPrice());
            mPastPrice = String.valueOf(goodsInfo.getPastPrice());

            if (goodsInfo.getPresell() == 0) {
                sellTime = "";
            } else {
                sellTime = String.valueOf(goodsInfo.getPresell());
            }
            setSelectDate(sellTime);
            String sellDate = "";
            if (!TextUtils.isEmpty(sellTime)) {
                sellDate = DateTimeUtils.getYearMonthDayHourMinute(Long.valueOf(sellTime));
            } else {
                sellDate = "立即发布";
            }
            tvReleaseTime.setText(sellDate);
        }

        if (goodsInfo.getPostage() != 0) {
            mPostage = String.valueOf(goodsInfo.getPostage());
        }

        tvPricePop.setText(mPrice);


        if (goodsInfo.getIsFreeDelivery()) {
            isFreeDelivery = 1;
        } else {
            isFreeDelivery = 0;
        }

        if (goodsInfo.getPostType() == 4) {
            freightCollect = 1;
        } else {
            freightCollect = 0;
        }


        mPop.setPriceText(String.valueOf(goodsInfo.getPrice()));
        mPop.setPastPriceText(String.valueOf(goodsInfo.getPastPrice()));

        if (goodsInfo.getPostage() != 0) {
            mPop.setPostageText(String.valueOf(goodsInfo.getPostage()));
        }
        mPop.setFreeDelivery(goodsInfo.getIsFreeDelivery());
        mPop.setPostagePaid(goodsInfo.getPostType() == 4 ? true : false);

    }

    private void initPopupWindowAndKeyboardState() {
        mPop = new PublishBottomPopupWindow(this, new PublishBottomPopupWindow.OnDataReachListener() {

            @Override
            public void onConfirmButtonClicked(final String price, final String pastPrice, final int ivFreeDelivery, final int p, final String postage) {
                if ("1".equals(price)) {
                    if (campaign == null) {
                        if (!oneYuanMode && CommonPreference.getGrantedOneYun() == 1) {
                            tvPricePop.setText(String.valueOf(price));
                            mPrice = price;
                            mPastPrice = pastPrice;
                            mPostage = postage;
                            ReleaseActivity.this.isFreeDelivery = ivFreeDelivery;
                            ReleaseActivity.this.freightCollect = p;
                            Intent intent = new Intent(ReleaseActivity.this, OneYuanTipActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_FOR_1_YUAN);
                            overridePendingTransition(0, 0);
                        } else {
                            tvPricePop.setText(String.valueOf(price));
                            mPrice = price;
                            mPastPrice = pastPrice;
                            mPostage = postage;
                            ReleaseActivity.this.isFreeDelivery = ivFreeDelivery;
                            ReleaseActivity.this.freightCollect = p;
                        }

                    } else {
                        tvPricePop.setText(String.valueOf(price));
                        mPrice = price;
                        mPastPrice = pastPrice;
                        mPostage = postage;
                        ReleaseActivity.this.isFreeDelivery = ivFreeDelivery;
                        ReleaseActivity.this.freightCollect = p;
                    }
                } else {
                    if (campaign == null) {
                        if (oneYuanMode) {
                            final TextDialog dialog = new TextDialog(ReleaseActivity.this, false);
                            dialog.setContentText("是否要退出一元店？");
                            dialog.setLeftText("取消");
                            dialog.setLeftCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.setRightText("确定");
                            dialog.setRightCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    tvPricePop.setText(String.valueOf(price));
                                    mPrice = price;
                                    mPastPrice = pastPrice;
                                    mPostage = postage;
                                    ReleaseActivity.this.isFreeDelivery = ivFreeDelivery;
                                    ReleaseActivity.this.freightCollect = p;
                                    setOneYuanMode(false);

                                }
                            });
                            dialog.show();

                        } else {
                            tvPricePop.setText(String.valueOf(price));
                            mPrice = price;
                            mPastPrice = pastPrice;
                            mPostage = postage;
                            ReleaseActivity.this.isFreeDelivery = ivFreeDelivery;
                            ReleaseActivity.this.freightCollect = p;
                        }

                    } else {
                        tvPricePop.setText(String.valueOf(price));
                        mPrice = price;
                        mPastPrice = pastPrice;
                        mPostage = postage;
                        ReleaseActivity.this.isFreeDelivery = ivFreeDelivery;
                        ReleaseActivity.this.freightCollect = p;
                    }
                }

            }

            @Override
            public void onBlankSpaceClicked() {
                CommonUtils.hideKeyBoard(ReleaseActivity.this);

            }
        }, campaign);
        mPop.setFocusable(true);
        mPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    @Override
    public void onKeyBoardStateChanged(View root, boolean isShow, final int heightDiff) {
        super.onKeyBoardStateChanged(root, isShow, heightDiff);
        if (isShow) {
            tvBtnRelease.setVisibility(View.GONE);
            if (isCheck) {
                mPop.showAtLocation(root, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            }

            if (mPop.isShowing()) {
                if (oneYuanMode) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            LogUtil.e("keyboardHeight", "keyboardHeight");
                            intent.putExtra("keyboardHeight", heightDiff);
                            CommonUtils.buildMontage(ReleaseActivity.this, intent, "first_release_postpaid_one");
                        }
                    }, 300);

                } else {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            LogUtil.e("keyboardHeight", "keyboardHeight");
                            intent.putExtra("keyboardHeight", heightDiff);
                            CommonUtils.buildMontage(ReleaseActivity.this, intent, "first_release_postpaid");
                        }
                    }, 300);

                }

            }

        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    tvBtnRelease.setVisibility(View.VISIBLE);
                }
            }, 150);
            if (isCheck) {
                isCheck = false;
                mPop.dismiss();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private void initRecMic() {
        mRecMicToMp3.setCallBack(this);
        mRecMicToMp3.setHandle(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RecMicToMp3.MSG_REC_STARTED:
                        break;
                    case RecMicToMp3.MSG_REC_STOPPED:
                        break;
                    case RecMicToMp3.MSG_ERROR_GET_MIN_BUFFERSIZE:
                        ToastUtil.toast(ReleaseActivity.this, "手机不支持录音功能");
                        break;
                    case RecMicToMp3.MSG_ERROR_CREATE_FILE:
                        ToastUtil.toast(ReleaseActivity.this, "录音文件生成异常");
                        break;
                    case RecMicToMp3.MSG_ERROR_REC_START:
                        ToastUtil.toast(ReleaseActivity.this, "录音无法开始");
                        break;
                    case RecMicToMp3.MSG_ERROR_AUDIO_RECORD:
                        ToastUtil.toast(ReleaseActivity.this, "无法录音，请检查是否禁止了权限");
                        // 如果录音图标正在显示的话,关闭显示图标
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (mRecMicToMp3 != null) {
                            // 停止录音
                            mRecMicToMp3.stop();
                            voiceValue = 0.0;
                            recodeTime = 0;
                        }
                        break;
                    case RecMicToMp3.MSG_ERROR_AUDIO_ENCODE:
                        ToastUtil.toast(ReleaseActivity.this, "文件编码失败");
                        break;
                    case RecMicToMp3.MSG_ERROR_WRITE_FILE:
                        ToastUtil.toast(ReleaseActivity.this, "文件保存失败");
                        break;
                    case RecMicToMp3.MSG_ERROR_CLOSE_FILE:
                        ToastUtil.toast(ReleaseActivity.this, "文件保存失败");
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void onCurrentVoice(int currentVolume) {
        Message msgMessage = mHandler.obtainMessage();
        msgMessage.what = 0x11;
        msgMessage.arg1 = currentVolume;
        mHandler.sendMessage(msgMessage);
    }

    // 录音Dialog图片随声音大小切换
    void setDialogImage(int voiceValue) {
        if (voiceValue <= 1) {
            dialog_image.setBackgroundResource(R.drawable.sound_animate_01);
            LogUtil.i("liang", "音量 <= 1");
        } else if (voiceValue > 1 && voiceValue <= 2) {
            dialog_image.setBackgroundResource(R.drawable.sound_animate_02);
            LogUtil.i("liang", "音量 <= 2");
        } else if (voiceValue > 2 && voiceValue <= 3) {
            dialog_image.setBackgroundResource(R.drawable.sound_animate_03);
            LogUtil.i("liang", "音量 <= 3");
        } else if (voiceValue > 3 && voiceValue <= 4) {
            dialog_image.setBackgroundResource(R.drawable.sound_animate_04);
            LogUtil.i("liang", "音量 <= 4");
        } else if (voiceValue > 4 && voiceValue <= 5) {
            dialog_image.setBackgroundResource(R.drawable.sound_animate_05);
            LogUtil.i("liang", "音量 <= 5");
        } else if (voiceValue > 5 && voiceValue <= 6) {
            dialog_image.setBackgroundResource(R.drawable.sound_animate_06);
            LogUtil.i("liang", "音量 <= 6");
        } else if (voiceValue > 6 && voiceValue <= 7) {
            dialog_image.setBackgroundResource(R.drawable.sound_animate_07);
            LogUtil.i("liang", "音量 <= 7");
        }
    }

    /**
     * 显示正在录音的图标
     */
    private void showVoiceDialog() {
        dialog = new Dialog(ReleaseActivity.this, R.style.DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.talk_layout);
        dialog_image = (ImageButton) dialog.findViewById(R.id.ibTalkLog);
        tvSoundTime = (TextView) dialog.findViewById(R.id.tvSoundTime);
        dialog.show();
    }

    /**
     * 录音时间太短时Toast显示
     */
    void showWarnToast() {
        Toast toast = new Toast(ReleaseActivity.this);
        LinearLayout linearLayout = new LinearLayout(ReleaseActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        // 定义一个ImageView
        ImageView imageView = new ImageView(ReleaseActivity.this);
        imageView.setImageResource(R.drawable.icon_chat_talk_no); // 图标

        TextView mTv = new TextView(ReleaseActivity.this);
        mTv.setText("时间太短   录音失败");
        mTv.setTextSize(14);
        mTv.setTextColor(Color.WHITE);// 字体颜色
        // mTv.setPadding(0, 10, 0, 0);

        // 将ImageView和ToastView合并到Layout中
        linearLayout.addView(imageView);
        linearLayout.addView(mTv);
        linearLayout.setGravity(Gravity.CENTER);// 内容居中
        linearLayout.setBackgroundResource(R.drawable.record_bg);// 设置自定义toast的背景

        toast.setView(linearLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间 100为向下移100dp
        toast.show();
        FileUtils.delAllFile(FileUtils.getVoiceDir());
    }

    /**
     * 录音计时线程
     */
    private void myThread() {
        timeThread = new Thread(ImageThread);
        timeThread.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                ivBtnTalk.setBackgroundResource(R.drawable.btn_voice_play_pressed);
                long millis = System.currentTimeMillis();
                if (millis - downMillis > 500) {
                    LogUtil.i("ReleaseActivity", "onTouch Down");
                    downMillis = System.currentTimeMillis();
                    // 如果当前不是正在录音状态，开始录音
                    if (RECODE_STATE != RECORD_ING) {
                        RECODE_STATE = RECORD_ING;
                        // 显示录音情况
                        showVoiceDialog();
                        // 开始录音
                        mRecMicToMp3.start();
                        // 计时线程
                        myThread();
                        // 播放动画
                        //TODO
                        if (media != null) {
                            // 如果正在播放
                            if (media.isPlaying()) {
                                media.stop();
                                playState = false;
                            } else {
                                playState = false;
                            }
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:// 离开
                ivBtnTalk.setBackgroundResource(R.drawable.btn_voice_play);
                // 如果是正在录音
                if (RECODE_STATE == RECORD_ING) {
                    RECODE_STATE = RECODE_ED;
                    // 如果录音图标正在显示,关闭
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    // 停止录音
                    mRecMicToMp3.stop();
                    voiceValue = 0.0;
                    if (recodeTime < MIX_TIME) {
                        recodeTime = 0.0f;
                        mRecMicToMp3.stop();
                        showWarnToast();
                        RECODE_STATE = RECORD_NO;

                        llPlayingState.setVisibility(View.GONE);
                        ivBtnTalk.setVisibility(View.VISIBLE);
                    } else {

                        llPlayingState.setVisibility(View.VISIBLE);
                        int time = Math.round(recodeTime);
                        tvRecordLength.setText(time + "\"");
                        ivBtnTalk.setVisibility(View.GONE);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                ivBtnTalk.setBackgroundResource(R.drawable.btn_voice_play);
                break;
        }
        return false;
    }

    private void startRequestForDownloadSound(String url, final String dir, final String fileName) {
        OkHttpClientManager.downloadAsync(url, dir, fileName, new StringCallback() {

            @Override
            public void onResponse(String response) {
                llPlayingState.setVisibility(View.VISIBLE);
                int time = Math.round(recodeTime);
                tvRecordLength.setText(time + "\"");
                ivBtnTalk.setVisibility(View.GONE);

                if (goodsInfo.getGoodsImgs() != null && goodsInfo.getGoodsImgs().size() > 0) {
                    taskCountDownload = goodsInfo.getGoodsImgs().size();
                    for (final String picUrl : goodsInfo.getGoodsImgs()) {
                        savePic(picUrl);
                    }
                } else {
                    ProgressDialog.closeProgress();
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }
        });
    }

    private boolean hasContent() {
        name = etGoodsName.getText().toString().trim();
        content = etProDes.getText().toString().trim();
        if (!ArrayUtil.isEmpty(selectBitmap)) {
            return true;
        }
        if (!TextUtils.isEmpty(name)) {
            return true;
        }
        if (!TextUtils.isEmpty(brand)) {
            return true;
        }
        if (secondClassId != 0) {
            return true;
        }

        if (selectedView.isHasVideo()) {
            return true;
        }

        int price;
        try {
            Number number = NumberFormat.getNumberInstance(Locale.FRENCH).parse(mPrice);
            price = number.intValue();
        } catch (Exception e) {
            price = 0;
        }
        if (price != 0) {
            return true;
        }


        int pastPrice;
        try {
            Number number = NumberFormat.getNumberInstance(Locale.FRENCH).parse(mPastPrice);
            pastPrice = number.intValue();
        } catch (Exception e) {
            pastPrice = 0;
        }
        if (pastPrice != 0) {
            return true;
        }

        if (!TextUtils.isEmpty(content)) {
            return true;
        }

        if (!TextUtils.isEmpty(ageId)) {
            return true;
        }

        File file = new File(FileUtils.getVoiceDir() + "/voice.mp3");
        if (recodeTime >= MIX_TIME && file.exists()) {
            return true;
        }

        if (!ArrayUtil.isEmpty(tagList)) {
            return true;
        }


        return false;
    }

    private void release2Sever() {
        name = etGoodsName.getText().toString().trim();
        content = etProDes.getText().toString().trim();
        if (selectBitmap == null || selectBitmap.size() == 0) {
            toast("请选择图片");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            toast("请输入宝贝名称");
            return;
        }
        if (TextUtils.isEmpty(brand)) {
            toast("请选择宝贝品牌");
            return;
        }

        if (secondClassId == 0) {
            toast("请选择商品分类");
            return;
        }


        if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city) || TextUtils.isEmpty(district)) {
            toast("请选择位置");
            return;
        }

        if (isAuction == 1) {
            if (sellPrice == 0) {
                toast("起拍价最小金额1元");
                return;
            }

            if (bidDeposit == 0) {
                toast("保证金最小金额1元");
                return;
            }

            if (bidIncrement == 0) {
                toast("加价幅度最小金额1元");
                return;
            }

            if (TextUtils.isEmpty(bidStartTime)) {
                toast("请选择开拍时间");
                return;
            }

            if (TextUtils.isEmpty(bidEndTime)) {
                toast("请选择拍卖结束时间");
                return;
            }


        } else {
            if (TextUtils.isEmpty(mPrice)) {
                toast("请输入价格");
                return;
            }
            int price;
            try {
                Number number = NumberFormat.getNumberInstance(Locale.FRENCH).parse(mPrice);
                price = number.intValue();
            } catch (ParseException e) {
                price = 0;
            }
            if (price == 0) {
                toast("最小金额1元");
                return;
            }

            if (CommonPreference.getUserInfo().getUserLevel() != 3) {
                if (TextUtils.isEmpty(mPastPrice)) {
                    toast("请输入原价");
                    return;
                }
            }
        }


        if (campaign != null) {
            String ages = campaign.getAges();
            if (!TextUtils.isEmpty(ages)) {
                if (TextUtils.isEmpty(ageId)) {
                    toast("请选择宝宝年龄段");
                    return;
                }
            }
        } else {
            if (oneYuanMode) {
                if (secondClassId == 2020) {
                    if (TextUtils.isEmpty(ageId)) {
                        toast("请选择宝宝年龄段");
                        return;
                    }
                }
            } else {
                if (TextUtils.isEmpty(ageId)) {
                    toast("请选择宝宝年龄段");
                    return;
                }
            }

        }

        if (oneYuanMode) {
            int level = CommonPreference.getUserInfo().getUserLevel();
            if (level >= 2) {
                tvBtnRelease.setOnClickListener(null);
                ProgressDialog.showProgress(ReleaseActivity.this);
                isStop = false;
                File file = new File(FileUtils.getVoiceDir() + "/voice.mp3");
                if (recodeTime >= MIX_TIME && file.exists()) {
                    uploadSound2Ali();
                } else {
                    upload2Ali();
                }
            } else {
                final TextDialog dialog = new TextDialog(this, false);
                dialog.setContentText("一元专区商品发布后不可编辑，请确认商品信息");
                dialog.setLeftText("再编辑一下");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("确认发布");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        tvBtnRelease.setOnClickListener(null);
                        ProgressDialog.showProgress(ReleaseActivity.this);
                        isStop = false;
                        File file = new File(FileUtils.getVoiceDir() + "/voice.mp3");
                        if (recodeTime >= MIX_TIME && file.exists()) {
                            uploadSound2Ali();
                        } else {
                            upload2Ali();
                        }
                    }
                });
                dialog.show();
            }
        } else {
            tvBtnRelease.setOnClickListener(null);
            ProgressDialog.showProgress(this);
            isStop = false;
            File file = new File(FileUtils.getVoiceDir() + "/voice.mp3");
            if (recodeTime >= MIX_TIME && file.exists()) {
                uploadSound2Ali();
            } else {
                upload2Ali();
            }
        }

    }

    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali() {
        picKey = DateTimeUtils.getYearMonthDayFolder("/goods/");
        goodsImageUrl = "";
        taskCount = selectBitmap.size();
        for (ImageItem item : selectBitmap) {
            if (isStop) {
                goodsImageUrl = "";
                return;
            }
            UploadPicTask task = new UploadPicTask();
            task.execute(item.imagePath);
        }
    }

    /**
     * 上传声音文件到阿里服务器
     */
    private void uploadSound2Ali() {
        soundKey = DateTimeUtils.getYearMonthDayFolder("/goods/sound/");
        UploadSoundTask task = new UploadSoundTask();
        task.execute(FileUtils.getVoiceDir() + "/voice.mp3");
    }

    /**
     * 发布商品
     */
    private void startRequestForSaveGoodsInfo() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            ProgressDialog.closeProgress();
            tvBtnRelease.setOnClickListener(ReleaseActivity.this);
            taskCount = selectBitmap.size();
            isStop = true;
            goodsImageUrl = "";
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        if (goodsInfo != null) {
            if (goodsInfo.getGoodsId() != 0) {
                params.put("goodsId", String.valueOf(goodsInfo.getGoodsId()));
            }

            if (goodsInfo.getDraftId() > 0) {
                params.put("draftId", String.valueOf(goodsInfo.getDraftId()));
            }
        }
        goodsImageUrl = StringUtils.substringBeforeLast(goodsImageUrl, ",");
        params.put("goodsImageUrl", goodsImageUrl);
        params.put("goodsName", name);
        if (!TextUtils.isEmpty(brand)) {
            params.put("goodsBrand", brand);
        }

        if (brandId > 0) {
            params.put("goodsBrandId", String.valueOf(brandId));
        }
        params.put("classificationId", secondClassId + "");

        params.put("ageIdList", ageId);
        params.put("goodsContent", content);

        params.put("isAuction", String.valueOf(isAuction));
        if (isAuction == 1) {
            params.put("price", String.valueOf(sellPrice));
            params.put("bidDeposit", String.valueOf(bidDeposit));
            params.put("bidIncrement", String.valueOf(bidIncrement));
            params.put("auctionStart", bidStartTime);
            params.put("auctionEnd", bidEndTime);
        } else {
            params.put("price", mPrice);
            if (CommonPreference.getUserInfo().getUserLevel() == 3 && TextUtils.isEmpty(mPastPrice)) {
                mPastPrice = "0";
            }
            params.put("pastPrice", mPastPrice);
        }


        if (!TextUtils.isEmpty(mPostage)) {
            params.put("postage", mPostage);

        }
        params.put("isFreeDelivery", String.valueOf(isFreeDelivery));
        params.put("freightCollect", String.valueOf(freightCollect));
        if (!TextUtils.isEmpty(sellTime)) {
            long currentTime = System.currentTimeMillis();
            long beginTime = Long.valueOf(sellTime);
            String sell;
            if (currentTime <= beginTime) {
                sell = String.valueOf(beginTime);
            } else {
                sell = String.valueOf(currentTime);
            }
            params.put("presell", sell);
        }
        if (campaign != null) {
            if (goodsInfo != null && goodsInfo.getCampaignId() > 0) {
                params.put("campaignId", String.valueOf(goodsInfo.getCampaignId()));
            } else {
                params.put("campaignId", String.valueOf(campaign.getCid()));
            }
        } else if (campaignId != 0) {
            params.put("campaignId", String.valueOf(campaignId));
        } else {
            params.put("campaignId", String.valueOf(0));
        }
        if (recodeTime >= MIX_TIME && !TextUtils.isEmpty(sound)) {
            params.put("sound", sound);
            int time = Math.round(recodeTime);
            params.put("soundTime", String.valueOf(time));
        }
        if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(district)) {
            params.put("province", String.valueOf(province));
            params.put("city", String.valueOf(city));
            params.put("district", String.valueOf(district));
        }
        if (locationData != null) {
            params.put("country", locationData.country);
            double lat = locationData.gLat;
            double lng = locationData.gLng;
            String latitude = String.valueOf(lat);
            String longitude = String.valueOf(lng);
            params.put("coordinate", longitude + "," + latitude);
        }

        if (!ArrayUtil.isEmpty(tagList)) {
            for (String tag : tagList) {
                if (tag.equals("全新")) {
                    params.put("isNew", "1");
                } else if (tag.equals("专柜小票")) {
                    params.put("hasReceipt", "1");
                } else if (tag.equals("可当面交易")) {
                    params.put("supportFaceToFace", "1");
                }
            }
        }

        if (!TextUtils.isEmpty(selectedView.getVideoUrl())) {
            params.put("videoPath", selectedView.getVideoUrl());
        }
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.SAVEGOODSINFO, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, "发布商品onError：" + e.toString());
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(ReleaseActivity.this);
                taskCount = selectBitmap.size();
                isStop = true;
                goodsImageUrl = "";
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "发布商品onResponse：" + response.toString());
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(ReleaseActivity.this);
                taskCount = selectBitmap.size();
                isStop = true;
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    goodsImageUrl = "";
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            JSONObject object = JSON.parseObject(baseResult.obj);
                            long goodsId = object.getLongValue("goodsId");
                            int auditStatus = object.getIntValue("auditStatus");
                            if (isAuction == 1) {
                                Intent intent = new Intent(ReleaseActivity.this, GoodsDetailsActivity.class);
                                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goodsId));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(ReleaseActivity.this, ReleaseFinishActivity.class);
                                intent.putExtra(MyConstants.EXTRA_GOODS_EDIT_FROM, editFrom);
                                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goodsId);
                                intent.putExtra(MyConstants.EXTRA_GOODS_CONTENT, content);
                                intent.putExtra(MyConstants.EXTRA_SELECT_IMG,
                                        StringUtils.substringBefore(goodsImageUrl, ","));
                                intent.putExtra(MyConstants.EXTRA_SELECT_PRICE, mPrice);
                                intent.putExtra(MyConstants.EXTRA_SELECT_PAST_PRICE, mPastPrice);
                                intent.putExtra(MyConstants.EXTRA_GOODS_NAME, name);
                                intent.putExtra(MyConstants.EXTRA_SELECT_CITY, city);
                                intent.putExtra(MyConstants.EXTRA_AUDIT_STATUS, auditStatus);
                                intent.putExtra("visit_one_yuan", oneYuanFlag);
                                if (campaign != null) {
                                    intent.putExtra(MyConstants.EXTRA_CAMPAIGN_ID, campaign.getCid());
                                } else {
                                    intent.putExtra(MyConstants.EXTRA_CAMPAIGN_ID, String.valueOf(0));
                                }
                                startActivity(intent);
                                overridePendingTransition(0, 0); // 去掉baseactivity中启动动画
                            }

                            // 商品发布成功后删除本地录音
                            FileUtils.delAllFile(FileUtils.getVoiceDir());
                            // 删除本地图片
                            FileUtils.delAllFile(FileUtils.getIconDir());
                            // 删除本地视频
                            FileUtils.delAllFile(FileUtils.getVideoDir());
                            GoodsEditEvent event = new GoodsEditEvent();
                            event.isSave = true;
                            EventBus.getDefault().post(event);

                            finish();
                        }
                    } else {
                        goodsImageUrl = "";
                        CommonUtils.error(baseResult, ReleaseActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param activity
     * @Description: 第三方登录
     */
    private void shareLogin(final Activity activity, final String name) {
        ProgressDialog.showProgress(this);
        final Platform platform = ShareSDK.getPlatform(activity, name);
        if (platform != null) {
            if (platform.isValid()) {
                platform.removeAccount(true);
                ShareSDK.removeCookieOnAuthorize(true);
            }
            platform.SSOSetting(false); // 设置false表示使用SSO授权方式
            platform.showUser(null);// 执行登录，登录后在回调里面获取用户资料
            platform.setPlatformActionListener(new PlatformActionListener() {

                @Override
                public void onError(Platform arg0, final int arg1, final Throwable arg2) {
                    LogUtil.i("liang", "onError: " + arg2.toString());
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ProgressDialog.closeProgress();
                            if (platform != null) {
                                if (platform.isValid()) {
                                    platform.removeAccount(true);
                                }
                            }
                        }
                    });
                }

                @Override
                public void onComplete(final Platform platform, int action, HashMap<String, Object> arg2) {
                    LogUtil.i("liang", "onComplete: " + action);
                    if (action == Platform.ACTION_USER_INFOR) {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (platform != null) {
                                    PlatformDb platDB = platform.getDb();
                                    if (platDB != null) {
                                        String uId = platDB.getUserId();
                                        startRequestForBindWechat(uId);
                                    }

                                }

                            }
                        });
                    }
                }

                @Override
                public void onCancel(Platform arg0, int arg1) {
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            toast("取消登录");
                            ProgressDialog.closeProgress();
                        }
                    });
                    if (platform != null) {
                        if (platform.isValid()) {
                            platform.removeAccount(true);
                        }
                    }
                }
            });
        }
    }

    private void doRequestForOneSellTime() {

        HashMap<String, String> params = new HashMap<>();

        OkHttpClientManager.postAsyn(MyConstants.ONE_SELL_TIME, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {

                try {
                    OneResult result = JSON.parseObject(response, OneResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (result != null && result.obj != null) {
                            long time = result.obj.time;
                            sellTime = String.valueOf(time);
                            String tmp = DateTimeUtils.getDateStr(time, "yyyy年MM月dd日 HH:mm");
                            tvReleaseTime.setText(tmp);
                        }
                    } else {
                        CommonUtils.error(result, ReleaseActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startRequestForBindALi(String aliId, String authCode) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(ReleaseActivity.this);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "2");
        params.put("aliId", aliId);
        params.put("authCode", authCode);
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        Iterator<Map.Entry<String, String>> it = entrySet.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            Map.Entry<String, String> set = it.next();
            sb.append(set.getKey()).append('=').append(set.getValue()).append(" , ");

        }
        LogUtil.e("bindAccountParam", sb.toString());
        OkHttpClientManager.postAsyn(MyConstants.BIND_ACCOUNT, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            UserInfo userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
                            CommonPreference.setUserInfo(userInfo);
                        }
                    } else {
                        CommonUtils.error(baseResult, ReleaseActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void startRequestForBindWechat(String flagId) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(ReleaseActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
        params.put("uid", flagId);
        LogUtil.e("bindAccountParam", params.toString());
        OkHttpClientManager.postAsyn(MyConstants.BIND_ACCOUNT, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            UserInfo userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
                            CommonPreference.setUserInfo(userInfo);
                        }
                    } else {
                        CommonUtils.error(baseResult, ReleaseActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 获取商品详情
     */
    private void startRequestForGetGoodsDetails() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(ReleaseActivity.this);
        HashMap<String, String> params = new HashMap<>();
        params.put("goodsId", goodsId);
        OkHttpClientManager.postAsyn(MyConstants.GETGOODSSIMPLEINFO, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "商品详情:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            goodsInfo = ParserUtils.parserGoodsSimpleInfoData(baseResult.obj);
                            initGoodsInfo();
                        }
                    } else {
                        ProgressDialog.closeProgress();
                        CommonUtils.error(baseResult, ReleaseActivity.this, "");
                    }
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化首次发布商品
     */
    private void initEmptyRelease() {
        initLocation();
        initReleaseTime();
        initCampaignData();
    }

    private void initReleaseTime() {
        if (isAuction == 1) {
            layoutReleaseTime.setVisibility(View.GONE);
        } else {
            int level = CommonPreference.getIntValue(CommonPreference.USER_LEVEL, 0);
            if (level <= 1 && campaign == null && !oneYuanMode) {
                layoutReleaseTime.setVisibility(View.GONE);
            } else {
                layoutReleaseTime.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initLocation() {
        locationData = CommonPreference.getLocalData();
        if (locationData != null) {
            province = locationData.province;
            city = locationData.city;
            district = locationData.district;
            tvLocation.setText(city + " " + district);
        }
    }

    private void commitSoundDraftBox2Ali() {
        soundKey = DateTimeUtils.getYearMonthDayFolder("/goods/sound/");
        CommitDraftBoxSoundTask task = new CommitDraftBoxSoundTask();
        task.execute(FileUtils.getVoiceDir() + "/voice.mp3");
    }

    private void commitDraftBox2Ali() {
        picKey = DateTimeUtils.getYearMonthDayFolder("/goods/");
        goodsImageUrl = "";
        taskCount = selectBitmap.size();
        if (taskCount > 0) {
            for (ImageItem item : selectBitmap) {
                if (isStop) {
                    goodsImageUrl = "";
                    return;
                }
                CommitPicToDraftBoxTask task = new CommitPicToDraftBoxTask();
                task.execute(item.imagePath);
            }
        } else {
            startRequestForCommitDraftBox();
        }
    }

    //请求草稿箱内容
    private void startRequestForDraftBoxContainer() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this, false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(draftType));
        if (campaign != null) {
            params.put("campaignId", String.valueOf(campaign.getCid()));
        } else if (campaignId != 0) {
            params.put("campaignId", String.valueOf(campaignId));
        } else {
            params.put("campaignId", String.valueOf(0));
        }
        LogUtil.e(TAG, "草稿箱内容params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GET_DRAFT_BOX, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, "草稿箱内容onError:" + e.getMessage());
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e(TAG, "草稿箱内容response:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    final BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            final TextDialog dialog = new TextDialog(ReleaseActivity.this, false);
                            dialog.setContentText("是否恢复上次编辑的内容？");
                            dialog.setLeftText("否");
                            dialog.setLeftCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    dialog.dismiss();
                                    initEmptyRelease();
                                    startRequestForDelDraftBox(false);
                                }
                            });
                            dialog.setRightText("是");
                            dialog.setRightCall(new DialogCallback() {

                                @Override
                                public void Click() {
                                    goodsInfo = ParserUtils.parserGoodsSimpleInfoData(baseResult.obj);
                                    initGoodsInfo();
                                }
                            });
                            dialog.show();
                        } else {
                            initEmptyRelease();
                        }
                    } else {
                        CommonUtils.error(baseResult, ReleaseActivity.this, "");
                    }
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }
            }
        });
    }

    //保存草稿
    private void startRequestForCommitDraftBox() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            ProgressDialog.closeProgress();
            tvBtnRelease.setOnClickListener(ReleaseActivity.this);
            taskCount = selectBitmap.size();
            isStop = true;
            goodsImageUrl = "";
            return;
        }
        ProgressDialog.showProgress(this, false);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(draftType));
        goodsImageUrl = StringUtils.substringBeforeLast(goodsImageUrl, ",");
        params.put("goodsImageUrl", goodsImageUrl);
        params.put("goodsName", name);
        if (!TextUtils.isEmpty(brand)) {
            params.put("goodsBrand", brand);
        }
        params.put("classificationId", secondClassId + "");
        params.put("ageIdList", ageId);
        params.put("goodsContent", content);
        params.put("goodsFirstCate", firstClassId + "");

        params.put("isAuction", String.valueOf(isAuction));
        if (isAuction == 1) {
            params.put("price", String.valueOf(sellPrice));
            params.put("bidDeposit", String.valueOf(bidDeposit));
            params.put("bidIncrement", String.valueOf(bidIncrement));
            if (!TextUtils.isEmpty(bidStartTime)) {
                params.put("auctionStart", bidStartTime);
            }

            if (!TextUtils.isEmpty(bidEndTime)) {
                params.put("auctionEnd", bidEndTime);
            }
        } else {
            params.put("price", mPrice);
        }

        if (!TextUtils.isEmpty(mPastPrice)) {
            params.put("pastPrice", mPastPrice);
        }

        if (!TextUtils.isEmpty(mPostage)) {
            params.put("postage", mPostage);
        }
        params.put("isFreeDelivery", String.valueOf(isFreeDelivery));
        params.put("freightCollect", String.valueOf(freightCollect));
        if (!TextUtils.isEmpty(sellTime)) {
            params.put("presell", sellTime);
        }
        if (campaign != null) {
            params.put("campaignId", String.valueOf(campaign.getCid()));
        } else if (campaignId != 0) {
            params.put("campaignId", String.valueOf(campaignId));
        } else {
            params.put("campaignId", String.valueOf(0));
        }
        if (recodeTime >= MIX_TIME && !TextUtils.isEmpty(sound)) {
            params.put("sound", sound);
            int time = Math.round(recodeTime);
            params.put("soundTime", String.valueOf(time));
        }
        if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(district)) {
            params.put("province", String.valueOf(province));
            params.put("city", String.valueOf(city));
            params.put("district", String.valueOf(district));
        }
        if (locationData != null) {
            params.put("country", locationData.country);
            double lat = locationData.gLat;
            double lng = locationData.gLng;
            String latitude = String.valueOf(lat);
            String longitude = String.valueOf(lng);
            params.put("coordinate", longitude + "," + latitude);
        }

        if (!ArrayUtil.isEmpty(tagList)) {
            for (String tag : tagList) {
                if (tag.equals("全新")) {
                    params.put("isNew", "1");
                } else if (tag.equals("专柜小票")) {
                    params.put("hasReceipt", "1");
                } else if (tag.equals("可当面交易")) {
                    params.put("supportFaceToFace", "1");
                }
            }
        }

        if (!TextUtils.isEmpty(selectedView.getVideoUrl())) {
            params.put("videoPath", selectedView.getVideoUrl());
        }


        if (brandId > 0) {
            params.put("goodsBrandId", String.valueOf(brandId));
        }

        LogUtil.e(TAG, "保存草稿params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.SAVE_DRAFT_BOX, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, "保存草稿onError：" + e.getMessage());
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(ReleaseActivity.this);
                taskCount = selectBitmap.size();
                isStop = true;
                goodsImageUrl = "";
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "保存草稿：" + response.toString());
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(ReleaseActivity.this);
                taskCount = selectBitmap.size();
                isStop = true;
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    goodsImageUrl = "";
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        finish();
                    } else {
                        CommonUtils.error(baseResult, ReleaseActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //删除草稿
    private void startRequestForDelDraftBox(final boolean isFinish) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ProgressDialog.closeProgress();
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(draftType));
        LogUtil.e(TAG, String.format("删除草稿params:%s", params.toString()));
        OkHttpClientManager.postAsyn(MyConstants.DEL_DRAFT_BOX, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, String.format("删除草稿onError：%s", e.getMessage()));
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, String.format("删除草稿：%s", response.toString()));
                ProgressDialog.closeProgress();

                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (isFinish) {
                            finish();
                        }
                    } else {
                        CommonUtils.error(baseResult, ReleaseActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void savePic(final String urlPath) {
        ImageLoader.loadBitmap(this, urlPath, new BitmapCallback() {
            @Override
            public void onBitmapDownloaded(Bitmap bitmap) {
                if (bitmap == null) {
                    mHandler.sendEmptyMessage(1);
                    return;
                }

                String fileName = System.currentTimeMillis() + Math.random() * 100 + ".jpg";
                String picPath = FileUtils.saveBitmap(bitmap, FileUtils.getIconDir(), fileName);
                if (TextUtils.isEmpty(picPath))
                    return;
                File file = new File(picPath);
                String fileMd5 = FileUtils.getFileMD5(file);
                ImageItem item = new ImageItem();
                item.imagePath = picPath;
                item.isSelected = true;
                item.MD5 = fileMd5;
                item.url = urlPath;

                if (selectBitmap != null && selectBitmap.size() < 8) {
                    selectBitmap.add(item);
                }
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    private class UploadPicTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            boolean isChange = true;
            String response = "";
            String urlPath = params[0];
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String fileName = System.currentTimeMillis() + ".jpg";
            String key = picKey + fileName;
            Bitmap bm = ImageUtils.revitionImageSize(urlPath);
            if (bm != null) {
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                if (!TextUtils.isEmpty(picPath)) {
                    File file = new File(picPath);
                    String fileMd5 = FileUtils.getFileMD5(file);
                    if (downloadImage != null && downloadImage.size() > 0) {
                        for (ImageItem download : downloadImage) {
                            if (download.MD5.equals(fileMd5)) {
                                isChange = false;
                                response = download.url;
                                downloadImage.remove(download);
                                break;
                            }
                        }
                    }
                    if (isChange) {
                        AliUpdateEvent updateEvent = new AliUpdateEvent(ReleaseActivity.this, picPath, folder, key);
                        PutObjectResult result = updateEvent.putObjectFromLocalFile();
                        if (result != null) {
                            response = key;
                        }
                    }
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                Message msg = new Message();
                msg.what = 0;
                if (result.contains("http")) {
                    msg.obj = result;
                } else {
                    msg.obj = MyConstants.OSS_IMG_HEAD + result;
                }
                taskHandler.sendMessage(msg);
            } else {
                taskCount = selectBitmap.size();
                goodsImageUrl = "";
                isStop = true;
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(ReleaseActivity.this);
                toast("图片上传失败，请重试");
            }
        }
    }

    private class UploadSoundTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String urlPath = params[0];
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String fileName = System.currentTimeMillis() + ".mp3";
            String key = soundKey + fileName;
            AliUpdateEvent updateEvent = new AliUpdateEvent(ReleaseActivity.this, urlPath, folder, key);
            PutObjectResult result = updateEvent.putObjectFromLocalFile();
            if (result != null) {
                response = key;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                sound = MyConstants.OSS_IMG_HEAD + result;
                upload2Ali();
            } else {
                toast("声音上传失败，请重试");
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(ReleaseActivity.this);


            }
        }
    }

    private class CommitPicToDraftBoxTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            boolean isChange = true;
            String response = "";
            String urlPath = (String) params[0];
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String fileName = System.currentTimeMillis() + ".jpg";
            String key = picKey + fileName;
            Bitmap bm = ImageUtils.revitionImageSize(urlPath);
            if (bm != null) {
                String picPath = FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                if (!TextUtils.isEmpty(picPath)) {
                    File file = new File(picPath);
                    String fileMd5 = FileUtils.getFileMD5(file);
                    if (downloadImage != null && downloadImage.size() > 0) {
                        for (ImageItem download : downloadImage) {
                            if (download.MD5.equals(fileMd5)) {
                                isChange = false;
                                response = download.url;
                                downloadImage.remove(download);
                                break;
                            }
                        }
                    }
                    if (isChange) {
                        AliUpdateEvent updateEvent = new AliUpdateEvent(ReleaseActivity.this, picPath, folder, key);
                        PutObjectResult result = updateEvent.putObjectFromLocalFile();
                        if (result != null) {
                            response = key;
                        }
                    }
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                Message msg = new Message();
                msg.what = 1;
                if (result.contains("http")) {
                    msg.obj = result;
                } else {
                    msg.obj = MyConstants.OSS_IMG_HEAD + result;
                }
                taskHandler.sendMessage(msg);
            } else {
                taskCount = selectBitmap.size();
                goodsImageUrl = "";
                isStop = true;
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(ReleaseActivity.this);
                toast("图片上传失败，请重试");
            }
        }
    }

    private class CommitDraftBoxSoundTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String urlPath = params[0];
            String folder = MyConstants.OSS_FOLDER_BUCKET;
            String fileName = System.currentTimeMillis() + ".mp3";
            String key = soundKey + fileName;
            AliUpdateEvent updateEvent = new AliUpdateEvent(ReleaseActivity.this, urlPath, folder, key);
            PutObjectResult result = updateEvent.putObjectFromLocalFile();
            if (result != null) {
                response = key;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                sound = MyConstants.OSS_IMG_HEAD + result;
                commitDraftBox2Ali();
            } else {
                toast("声音上传失败，请重试");
                ProgressDialog.closeProgress();
                tvBtnRelease.setOnClickListener(ReleaseActivity.this);
            }
        }
    }
}
