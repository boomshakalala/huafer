package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.Baby;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.City;
import com.huapu.huafen.beans.District;
import com.huapu.huafen.beans.Region;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.fragment.MineFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.BindSinaUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.BabyInfoLayout;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人资料修改
 *
 * @author liang_xs
 */
public class PersonalDataActivity extends BaseActivity implements BindSinaUtil.RefreshSinaUI {

    private static final String PREF_FILE_NAME = "common_settings";
    private final static int BIND_TO_ALIPAY = 0x1111;
    private final static int BIND_TO_WECHAT = BIND_TO_ALIPAY + 1;
    private final static int REQUEST_CODE_FOR_BIND_LIST = 0x123;
    private final static int REQUEST_CODE_FOR_REBIND = REQUEST_CODE_FOR_BIND_LIST + 1;
    @BindView(R.id.aliStatus)
    TextView aliStatus;
    @BindView(R.id.weChatStatus)
    TextView weChatStatus;
    @BindView(R.id.sinaWeiBoStatus)
    TextView sinaWeiBoStatus;
    @BindView(R.id.bindALipay)
    RelativeLayout bindALipay;
    @BindView(R.id.bindWechat)
    RelativeLayout bindWechat;
    /*@BindView(R.id.ivAlipayArrow)
    ImageView ivAlipayArrow;*/
    @BindView(R.id.ivSinaArrow)
    ImageView ivSinaArrow;
    /*@BindView(R.id.ivWechatArrow)
    ImageView ivWechatArrow;*/
    @BindView(R.id.babyInfoLayout)
    BabyInfoLayout babyInfoLayout;
    @BindView(R.id.bindSinaLayout)
    RelativeLayout bindSinaLayout;
    @BindView(R.id.aliBind)
    TextView aliBind;
    @BindView(R.id.weiXinBind)
    TextView weiXinBind;
    @BindView(R.id.weiBoBind)
    TextView weiBoBind;
    @BindView(R.id.tv_verified)
    TextView tvVerified;
    @BindView(R.id.tv_verifiedStatus)
    TextView tvVerifiedStatus;
    @BindView(R.id.rl_verified)
    RelativeLayout rlVerified;
    private SimpleDraweeView ivHeader;
    private View layoutHead, layoutGender, layoutCity, layoutPhone,
            layoutAddressList, layoutEditChild;
    private TextView tvBtnAdd, tvCity;
    private String imgPath = "";
    private Bitmap head;// 头像Bitmap
    private TextView tvName, tvSex, tvPhoneNum, tvFirstBabySex, tvFirstBabyBirthday, tvSecondBabySex,
            tvSecondBabyBirthday;
    private String firstBabyBirth, secondBabyBirth;
    private String firstBabySex, secondBabySex;
    private int areaId, cityId;
    private String sex;
    private UserInfo userInfo = new UserInfo();
    private String fileName;
    private String key;
    private boolean isHeaderChange = false;
    private boolean isUpdate = false;
    private RelativeLayout bindAccount;//绑定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        ButterKnife.bind(this);
        initView();
        startRequestForGetUserInfo();
    }

    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali() {
        if (!TextUtils.isEmpty(imgPath)) {
            Bitmap bm = ImageUtils.revitionImageSize(imgPath);
            if (bm != null) {
                FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                UploadPicTask task = new UploadPicTask();
                task.execute(imgPath, MyConstants.OSS_FOLDER_BUCKET, key);
            }
        } else {
            toast("请选择头像");
        }
    }

    @OnClick({R.id.bindALipay, R.id.bindWechat, R.id.bindSinaLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bindALipay:
                Intent intent = new Intent();
                intent.setClass(this, BindALiActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_BIND_LIST);
                break;
            case R.id.bindWechat:
                Intent intentWeChat = new Intent();
                intentWeChat.setClass(this, BindWechatActivity.class);
                startActivityForResult(intentWeChat, REQUEST_CODE_FOR_BIND_LIST);
                break;
            case R.id.bindSinaLayout:
                if (userInfo.isBindWeibo()) {
                    Intent webViewIntent = new Intent();
                    webViewIntent.setClass(this, WebViewActivity.class);
                    webViewIntent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.BASE_URL + "/social/v1/goToUserWeibo?uid=" + userInfo.getWeiboUserId());
                    startActivity(webViewIntent);
                } else {
                    BindSinaUtil bindSinaUtil = new BindSinaUtil(this);
                    bindSinaUtil.bindSina();
                }

                break;
        }
    }

    private void initView() {
        setTitleString("个人资料");
        layoutAddressList = findViewById(R.id.layoutAddressList);
        layoutEditChild = findViewById(R.id.layoutEditChild);
        layoutHead = findViewById(R.id.layoutHead);
        layoutGender = findViewById(R.id.layoutGender);
        layoutCity = findViewById(R.id.layoutCity);
        layoutPhone = findViewById(R.id.layoutPhone);
//		layoutWechat = findViewById(R.id.layoutWechat);
//		layoutWeibo = findViewById(R.id.layoutWeibo);
//		layoutALi = findViewById(R.id.layoutALi);
        ivHeader = (SimpleDraweeView) findViewById(R.id.ivHeader);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvName = (TextView) findViewById(R.id.tvName);
        tvSex = (TextView) findViewById(R.id.tvSex);
        tvPhoneNum = (TextView) findViewById(R.id.tvPhoneNum);

        layoutAddressList.setOnClickListener(this);
        layoutEditChild.setOnClickListener(this);
        layoutHead.setOnClickListener(this);
        layoutGender.setOnClickListener(this);
        layoutCity.setOnClickListener(this);
        layoutPhone.setOnClickListener(this);
        rlVerified.setOnClickListener(this);
//		 layoutWechat.setOnClickListener(this);
//		layoutWeibo.setOnClickListener(this);
//		layoutALi.setOnClickListener(this);
        bindAccount = (RelativeLayout) findViewById(R.id.bindAccount);
        bindAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnTitleBarLeft:
                if (isHeaderChange) {
                    upload2Ali();
                } else {
                    startRequestForUpdateUser();
                }
                break;
            case R.id.layoutHead:
                fileName = System.currentTimeMillis() + "_icon.jpg";
                key = DateTimeUtils.getYearMonthDayFolder("/headIcon/") + fileName;
                imgPath = FileUtils.getIconDir() + fileName;
                PhotoDialog dialog = new PhotoDialog(PersonalDataActivity.this);
                dialog.setCameraCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intentCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        File out = new File(imgPath);
                        Uri uri = Uri.fromFile(out);
                        // 获取拍照后未压缩的原图片，并保存在uri路径中
                        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intentCamera, MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA);
                    }
                });
                dialog.setAlbumCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent1, MyConstants.INTENT_FOR_RESULT_TO_ALBUM);
                    }
                });
                dialog.show();
                break;
            case R.id.layoutGender:
                PhotoDialog sexDialog = new PhotoDialog(PersonalDataActivity.this, "男", "女");
                sexDialog.setCameraCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        isUpdate = true;
                        sex = "1";
                        tvSex.setText("男");
                    }
                });
                sexDialog.setAlbumCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        isUpdate = true;
                        sex = "0";
                        tvSex.setText("女");
                    }
                });
                sexDialog.show();
                break;
            case R.id.layoutCity:
                intent = new Intent(PersonalDataActivity.this, ProvinceActivity.class);
                intent.putExtra("isLocation", false);
                startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_PROVINCE);
                break;
            case R.id.layoutPhone:
                if (userInfo == null) {
                    return;
                }
                intent = new Intent(PersonalDataActivity.this, PhoneRebindActivity.class);
                String phoneNum = userInfo.getPhone();
                intent.putExtra(MyConstants.EXTRA_PHONE_NUMBER, phoneNum);
                startActivityForResult(intent, REQUEST_CODE_FOR_REBIND);
                break;
//		case R.id.layoutWechat:
//			intent = new Intent(PersonalDataActivity.this, BindWechatActivity.class);
//			startActivity(intent);
//			break;
//		case R.id.layoutWeibo:
//			break;
            case R.id.layoutAddressList:
                intent = new Intent(PersonalDataActivity.this, AddressListActivityNew.class);
                startActivity(intent);
                break;
            case R.id.layoutEditChild:
                intent = new Intent(PersonalDataActivity.this, BabiesActivity.class);
                if (userInfo != null) {
                    intent.putExtra(MyConstants.EXTRA_BABY_LIST, userInfo.getBabys());
                    startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_MY_CHILD);
                }
                break;
//		case R.id.layoutALi:
//			intent = new Intent(PersonalDataActivity.this, BindALiActivity.class);
//			startActivity(intent);
//			break;
            case R.id.bindAccount:
                if (userInfo == null) {
                    return;
                }
                intent = new Intent(PersonalDataActivity.this, BindListActivity.class);
                boolean isBindALi = userInfo.getIsBindALi();
                boolean isBindWechat = userInfo.getIsBindWechat();
                boolean isBindWeibo = userInfo.isBindWeibo();
                LogUtil.e("isBindALi and isBindWechat", isBindALi + " and " + isBindWechat);
                intent.putExtra("isBindALi", isBindALi);
                intent.putExtra("isBindWechat", isBindWechat);
//			startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_FOR_BIND_LIST);
                break;
            case R.id.rl_verified:
                if (ConfigUtil.isToVerify()) {
                    intent = new Intent(PersonalDataActivity.this, VerifiedActivity.class);
                    startActivityForResult(intent, MyConstants.FROM_FLAGS_PERSONALDATAACTIVITY);
                }
            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                // 判断图片是否旋转，处理图片旋转
                ExifInterface exif;
                Bitmap bitmap = ImageUtils.getLoacalBitmap(PersonalDataActivity.this, imgPath);
                try {
                    exif = new ExifInterface(imgPath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    // 旋转图片，使其显示方向正常
                    Bitmap newBitmap = ImageUtils.getTotateBitmap(bitmap, orientation);
                    File out = new File(imgPath);
                    boolean save = ImageUtils.saveMyBitmap(out, newBitmap);
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    if (newBitmap != null) {
                        newBitmap.recycle();
                        newBitmap = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File temp = new File(imgPath);
                ImageUtils.cropPhoto(PersonalDataActivity.this, Uri.fromFile(temp));// 裁剪图片
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM) {
                ImageUtils.cropPhoto(PersonalDataActivity.this, data.getData());// 裁剪图片
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_CROP) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        File out = new File(imgPath);
                        try {
                            ImageUtils.saveMyBitmap(out, head);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // ivHeader.setImageBitmap(head);//用ImageView显示出来
                        isHeaderChange = true;
                        isUpdate = true;

                        ImageLoader.resizeSmall(ivHeader, MyConstants.FILE + imgPath, 1);
                    }
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_RELEASETHIRD_TO_CHOOSECITY) {
                isUpdate = true;
                String city = data.getStringExtra(MyConstants.EXTRA_CHOOSE_CITYNAME);
                String district = data.getStringExtra(MyConstants.EXTRA_CHOOSE_DISNAME);
                areaId = data.getIntExtra(MyConstants.EXTRA_CHOOSE_DISID, 0);
                cityId = data.getIntExtra(MyConstants.EXTRA_CHOOSE_CITYID, 0);
                tvCity.setText(city + " | " + district);
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_MY_CHILD) {
                ArrayList<Baby> babies = (ArrayList<Baby>) data.getSerializableExtra(MyConstants.EXTRA_BABY_LIST);
                userInfo.setBabys(babies);
                initData(userInfo);
            } else if (requestCode == REQUEST_CODE_FOR_BIND_LIST) {
                int bindResult = data.getIntExtra("BIND_RESULT", -1);
                if (bindResult == 2) {
                    userInfo.setIsBindALi(true);
                    CommonPreference.setUserInfo(userInfo);
                    freshAliInfo(userInfo);
                } else if (bindResult == 1) {
                    userInfo.setIsBindWechat(true);
                    CommonPreference.setUserInfo(userInfo);
                    freshWeXinInfo(userInfo);
                }
            } else if (requestCode == REQUEST_CODE_FOR_REBIND) {
                if (data == null) {
                    return;
                }

                if (userInfo != null) {
                    String newPhoneNum = data.getStringExtra(MyConstants.EXTRA_NEW_PHONE_NUM);
                    if (!TextUtils.isEmpty(newPhoneNum)) {
                        userInfo.setPhone(newPhoneNum);
                        tvPhoneNum.setText(userInfo.getPhone());
                    }
                }
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_PROVINCE) {
                if (data == null) {
                    return;
                }
                isUpdate = true;
                String province = "";
                String city = "";
                String district = "";
                Region regionData = new Region();
                if (data.hasExtra(MyConstants.EXTRA_REGION)) {
                    regionData = (Region) data.getSerializableExtra(MyConstants.EXTRA_REGION);
                    province = regionData.getName();
                }
                if (data.hasExtra(MyConstants.EXTRA_CITY)) {
                    City cityData = (City) data.getSerializableExtra(MyConstants.EXTRA_CITY);
                    cityId = cityData.getDid();
                    city = cityData.getName();
                } else {
                    cityId = regionData.getDid();
                    city = province;
                }
                if (data.hasExtra(MyConstants.EXTRA_DISTRICT)) {
                    District districtData = (District) data.getSerializableExtra(MyConstants.EXTRA_DISTRICT);
                    areaId = districtData.getDid();
                    district = districtData.getName();
                }
                tvCity.setText(city + " " + district);
            } else if (requestCode == MyConstants.FROM_FLAGS_PERSONALDATAACTIVITY) {
                freshVerified();
            }


        }
    }

    /**
     * 获取个人资料
     */
    private void startRequestForGetUserInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
        LogUtil.i("liang", "获取个人资料params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETUSERINFO, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("liang", "个人资料error:" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "个人资料:" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            // 解析数据
                            userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
                            CommonPreference.setUserInfo(userInfo);
                            if (userInfo != null) {
                                initData(userInfo);
                            }
                        }
                    } else {
                        CommonUtils.error(baseResult, PersonalDataActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        if (isHeaderChange) {
            upload2Ali();
        } else {
            startRequestForUpdateUser();
        }
    }

    /**
     * 修改个人资料
     */
    private void startRequestForUpdateUser() {
        if (!isUpdate) {
            finish();
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        JSONArray array = new JSONArray();
        JSONObject objFirst = new JSONObject();
        if (!TextUtils.isEmpty(firstBabySex) && !TextUtils.isEmpty(firstBabyBirth)) {
            try {
                objFirst.put("sex", null == firstBabySex ? "" : firstBabySex);
                objFirst.put("dateOfBirth", null == firstBabyBirth ? "" : firstBabyBirth);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(objFirst);
        }

        JSONObject objSecond = new JSONObject();
        if (!TextUtils.isEmpty(secondBabySex) && !TextUtils.isEmpty(secondBabyBirth)) {
            try {
                objSecond.put("sex", null == secondBabySex ? "" : secondBabySex);
                objSecond.put("dateOfBirth", null == secondBabyBirth ? "" : secondBabyBirth);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(objSecond);
        }

        if (isHeaderChange) {
            params.put("userIcon", MyConstants.OSS_IMG_HEAD + key);
        }
        if (array.length() > 0) {
            params.put("babys", array.toString());
            params.put("pregnantStat", "3");
        }
        if (cityId != 0 && areaId != 0) {
            params.put("cityId", String.valueOf(cityId));
            params.put("areaId", String.valueOf(areaId));
        }
        if (!TextUtils.isEmpty(sex)) {
            params.put("sex", sex);
        }
        LogUtil.i("liang", "修改个人资料params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.UPDATEUSER, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("liang", "修改个人资料error:" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "修改个人资料:" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
                            CommonPreference.setUserInfo(userInfo);
                            if (MineFragment.mineFragment != null) {
                                if (MineFragment.mineFragment.ivHeader != null) {
                                    ImageLoader.resizeSmall(MineFragment.mineFragment.ivHeader, userInfo.getUserIcon(), 1);
                                }
                            }
                            setResult(RESULT_OK);
                            // 解析数据
                            finish();
                        }
                    } else {
                        CommonUtils.error(baseResult, PersonalDataActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void freshAliInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }

        if (userInfo.getIsBindALi()) {
            aliStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
            Drawable drawable = getResources().getDrawable(R.drawable.icon_bind_list_ali);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            aliBind.setCompoundDrawables(drawable, null, null, null);
            aliStatus.setText("已绑定");
            //ivAlipayArrow.setVisibility(View.INVISIBLE);
            bindALipay.setEnabled(false);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.ali_unbind);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            aliBind.setCompoundDrawables(drawable, null, null, null);

            aliStatus.setTextColor(getResources().getColor(R.color.base_circle_border));
            aliStatus.setText("去绑定");
            //ivAlipayArrow.setVisibility(View.VISIBLE);
            bindALipay.setEnabled(true);
        }
    }

    private void freshWeXinInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        if (userInfo.getIsBindWechat()) {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_bind_list_wechat);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            weiXinBind.setCompoundDrawables(drawable, null, null, null);

            weChatStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
            weChatStatus.setText("已绑定");
            //ivWechatArrow.setVisibility(View.INVISIBLE);
            bindWechat.setEnabled(false);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.weixin_unbind);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            weiXinBind.setCompoundDrawables(drawable, null, null, null);

            weChatStatus.setTextColor(getResources().getColor(R.color.base_circle_border));
            weChatStatus.setText("去绑定");
            //ivWechatArrow.setVisibility(View.VISIBLE);
            bindWechat.setEnabled(true);
        }
    }

    private void freshWeiBoInfo(UserInfo userInfo) {
        if (userInfo.isBindWeibo()) {
            Drawable drawable = getResources().getDrawable(R.drawable.wd_weibo_open);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            weiBoBind.setCompoundDrawables(drawable, null, null, null);

            sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
            sinaWeiBoStatus.setText("查看微博");
            ivSinaArrow.setVisibility(View.VISIBLE);
            bindSinaLayout.setEnabled(true);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.wd_weibo_grey);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            weiBoBind.setCompoundDrawables(drawable, null, null, null);

            sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.base_circle_border));
            sinaWeiBoStatus.setText("去绑定");
            ivSinaArrow.setVisibility(View.GONE);
            bindSinaLayout.setEnabled(true);
        }
    }

    private void freshVerified() {
        if (!userInfo.hasVerified) {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_not_verified);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvVerified.setCompoundDrawables(drawable, null, null, null);
            tvVerifiedStatus.setText("去认证");
            tvVerifiedStatus.setTextColor(getResources().getColor(R.color.base_circle_border));
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_is_verified);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvVerified.setCompoundDrawables(drawable, null, null, null);
            tvVerifiedStatus.setText("已认证");
            tvVerifiedStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
        }
    }

    private void initData(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        ImageLoader.resizeSmall(ivHeader, userInfo.getUserIcon(), 1);
        tvName.setText(userInfo.getUserName());
        if (userInfo.getUserSex() == 0) {
            tvSex.setText("女");
        } else if (userInfo.getUserSex() == 1) {
            tvSex.setText("男");
        }
        tvPhoneNum.setText(userInfo.getPhone());
        Area area = userInfo.getArea();
        if (area != null) {
            tvCity.setText(area.getCity() + area.getArea());
        }

        babyInfoLayout.setBabies(userInfo.getBabys());

        freshAliInfo(userInfo);

        freshWeXinInfo(userInfo);

        freshWeiBoInfo(userInfo);
        freshVerified();
    }


    @Override
    public void refreshSinaUI(String unionId) {
        userInfo.setBindWeibo(true);
        userInfo.setWeiboUserId(unionId);
        CommonPreference.setUserInfo(userInfo);
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putString(CommonPreference.SINA_UID, unionId).apply();
        freshWeiBoInfo(userInfo);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, PersonalDataActivity.class);
    }

    private class UploadPicTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String urlPath = (String) params[0];
            String folder = (String) params[1];
            String key = (String) params[2];
            AliUpdateEvent updateEvent = new AliUpdateEvent(PersonalDataActivity.this, urlPath, folder, key);
            PutObjectResult result = updateEvent.putObjectFromLocalFile();
            if (result == null) {
            } else {
                response = key;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                startRequestForUpdateUser();
            } else {
                toast("头像上传失败，请重试");
            }
        }

    }

}
