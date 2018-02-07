package com.huapu.huafen.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.album.utils.MD5;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.PhotoDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.events.FinishEvent;
import com.huapu.huafen.events.LoginEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.AliUpdateEvent;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 注册时编辑资料
 *
 * @author liang_xs
 */
public class EditNameActivity extends BaseActivity {
    private SimpleDraweeView ivHeader;
    private View layoutTip;
    private EditText etUserName;
    private TextView tvNameRepeat;
    private TextView tvBtnBegin;
    private ImageView ivTip;
    private Bitmap head;//头像Bitmap
    private String flagId;
    private String name;
    private String icon;
    private String sex;
    private String phone;
    private String code;
    private String uid;
    private String fileName;
    private String key;
    private TextDialog dialog;
    private String type;
    private int maxLength = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        if (getIntent().hasExtra(MyConstants.EXTRA_WECHAT_NAME)) {
            name = getIntent().getStringExtra(MyConstants.EXTRA_WECHAT_NAME);
            if (!TextUtils.isEmpty(name)) {
                int nameLength = CommonUtils.getLength(name);
                if (nameLength > maxLength) {
                    name = name.substring(0, maxLength);
                }
            }
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_WECHAT_ICON)) {
            icon = getIntent().getStringExtra(MyConstants.EXTRA_WECHAT_ICON);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_FLAGID)) {
            flagId = getIntent().getStringExtra(MyConstants.EXTRA_FLAGID);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_WECHAT_SEX)) {
            sex = getIntent().getStringExtra(MyConstants.EXTRA_WECHAT_SEX);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_REGISTER_PHONE)) {
            phone = getIntent().getStringExtra(MyConstants.EXTRA_REGISTER_PHONE);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_REGISTER_CODE)) {
            code = getIntent().getStringExtra(MyConstants.EXTRA_REGISTER_CODE);
        }
        if (getIntent().hasExtra(MyConstants.TYPE)) {
            type = getIntent().getStringExtra(MyConstants.TYPE);
        }
        fileName = System.currentTimeMillis() + "_icon.jpg";
        key = DateTimeUtils.getYearMonthDayFolder("/headIcon/") + fileName;
        initView();
    }

    private void initView() {
        setTitleString("编辑资料");
        layoutTip = findViewById(R.id.layoutTip);
        ivHeader = (SimpleDraweeView) findViewById(R.id.ivHeader);
        etUserName = (EditText) findViewById(R.id.etUserName);
        tvNameRepeat = (TextView) findViewById(R.id.tvNameRepeat);
        tvBtnBegin = (TextView) findViewById(R.id.tvBtnBegin);
        ivTip = (ImageView) findViewById(R.id.ivTip);
        etUserName.setText(name);
        if (!TextUtils.isEmpty(name)) {
            etUserName.setSelection(name.length());
        }

        ImageLoader.resizeSmall(ivHeader, icon, 1);

        tvBtnBegin.setOnClickListener(this);
        ivHeader.setOnClickListener(this);
        initEvent(etUserName);
        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String editName = s.toString();
                int nameLength = CommonUtils.getLength(editName);
                if (nameLength > maxLength) {
                    int len = editName.length();
                    //截取新字符串
                    editName = editName.substring(0, len - 1);
                    etUserName.setText(editName);
                }
                if (nameLength == maxLength) {
                    //设置新光标所在的位置
                    etUserName.setSelection(editName.length());
                }
            }
        });
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

    /**
     * 上传图片到阿里服务器
     */
    private void upload2Ali() {
        if (!TextUtils.isEmpty(icon)) {
            if (icon.contains("http")) {
                downloadPic(icon, fileName);
            } else {
                Bitmap bm = ImageUtils.revitionImageSize(icon);
//				String fileName = System.currentTimeMillis() + file.getName();
                if (bm == null) {
                    tvBtnBegin.setOnClickListener(this);
                    toast("请选择头像");
                    return;
                }
                if (bm != null) {
                    FileUtils.saveBitmap(bm, FileUtils.getIconDir(), fileName);
                    UploadPicTask task = new UploadPicTask();
//					String fileNameBase64 = new String(Base64.encode(fileName.getBytes(), Base64.DEFAULT));
//					fileNameBase64 = fileNameBase64.replaceAll("\n", "");
                    task.execute(icon, MyConstants.OSS_FOLDER_BUCKET, key);
                }
            }
        } else {
            tvBtnBegin.setOnClickListener(this);
            toast("请选择头像");
        }
//		task.execute(bean.getHeadico(), "imgtest2913", "imgtest2913/userid/temp.jpg");
    }

    private void downloadPic(String urlPath, final String fileName) {
        ImageLoader.loadBitmap(EditNameActivity.this, urlPath, new BitmapCallback() {
            @Override
            public void onBitmapDownloaded(Bitmap bitmap) {

                if (bitmap != null) {
                    FileUtils.saveBitmap(bitmap, FileUtils.getIconDir(), fileName);

                    if (!TextUtils.isEmpty(fileName)) {
                        UploadPicTask task = new UploadPicTask();
//				String fileNameBase64 = new String(Base64.encode(result.getBytes(), Base64.DEFAULT));
//				fileNameBase64 = fileNameBase64.replaceAll("\n", "");
                        task.execute(FileUtils.getIconDir() + fileName, MyConstants.OSS_FOLDER_BUCKET, key);
                    } else {
                        tvBtnBegin.setOnClickListener(EditNameActivity.this);
                    }
                }
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
            String response = "";
            String urlPath = (String) params[0];
            String folder = (String) params[1];
            String key = (String) params[2];
            AliUpdateEvent updateEvent = new AliUpdateEvent(EditNameActivity.this, urlPath, folder, key);
            PutObjectResult result = updateEvent.putObjectFromLocalFile();
            // 测试代码，真实环境删除此行
//			startRequestForRegist(key);
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
                startRequestForRegist(result);
            } else {
                tvBtnBegin.setOnClickListener(EditNameActivity.this);
                toast("头像上传失败，请重试");
            }
        }

    }


    private void startRequestForCheckNickName() {
        if (!CommonUtils.isNetAvaliable(this)) {
            tvBtnBegin.setOnClickListener(EditNameActivity.this);
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(EditNameActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userName", etUserName.getText().toString());
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.CHECKNICKNAME, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("liang", "检查昵称error：" + e.toString());
                ProgressDialog.closeProgress();
                tvBtnBegin.setOnClickListener(EditNameActivity.this);
                ToastUtil.toast(EditNameActivity.this, "检查昵称出错");
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "检查昵称：" + response.toString());
                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    ToastUtil.toast(EditNameActivity.this, "检查昵称系统错误");
                    tvBtnBegin.setOnClickListener(EditNameActivity.this);
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        // 昵称符合要求，用户头像下载上传至阿里云
                        upload2Ali();
//						startRequestForRegist();
                    } else {
                        tvBtnBegin.setOnClickListener(EditNameActivity.this);
                        layoutTip.setVisibility(View.VISIBLE);

                        CommonUtils.error(baseResult, EditNameActivity.this, "");

//						if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_TOAST) {
//							
//						} else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_LOGIN) {
//							ActionUtil.loginAndToast(EditNameActivity.this);
//						} else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_LOGOUT) {
//							CommonUtils.loginAndDialog(EditNameActivity.this);
//						}
                    }
                } catch (Exception e) {
                    tvBtnBegin.setOnClickListener(EditNameActivity.this);
                    e.printStackTrace();
                }
            }
        });
    }

    private void startRequestForRegist(String key) {
        if (!CommonUtils.isNetAvaliable(this)) {
            tvBtnBegin.setOnClickListener(EditNameActivity.this);
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(EditNameActivity.this);
        String firstSex = CommonPreference.getStringValue(CommonPreference.FIRSRBABYSEX, "");
        String firstBirth = CommonPreference.getStringValue(CommonPreference.FIRSTBABYBIRTH, "");
        String secondSex = CommonPreference.getStringValue(CommonPreference.SECONDBABYSEX, "");
        String secondBirth = CommonPreference.getStringValue(CommonPreference.SECONDBABYBIRTH, "");
        JSONArray array = new JSONArray();
        JSONObject objFirst = new JSONObject();
        if (!TextUtils.isEmpty(firstSex) && !TextUtils.isEmpty(firstBirth)) {
            try {
                objFirst.put("sex", null == firstSex ? "" : firstSex);
                objFirst.put("dateOfBirth", null == firstBirth ? "" : firstBirth);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(objFirst);
        }

        JSONObject objSecond = new JSONObject();
        if (!TextUtils.isEmpty(secondSex) && !TextUtils.isEmpty(secondBirth)) {
            try {
                objSecond.put("sex", null == secondSex ? "" : secondSex);
                objSecond.put("dateOfBirth", null == secondBirth ? "" : secondBirth);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(objSecond);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        params.put("phoneCode", MD5.Md5(phone + code));
        //TODO
        LogUtil.e("editType", type);
        if ("1".equals(type)) {
            params.put("uid", flagId);
        } else {
            params.put("aliPayId", flagId);
        }

        params.put("sex", sex);
        params.put("wechatNickName", etUserName.getText().toString());
        params.put("wechatUserIcon", MyConstants.OSS_IMG_HEAD + key);
//		if(!TextUtils.isEmpty(CommonPreference.getStringValue(CommonPreference.PREGNANTSTAT, ""))) {
//			params.put("pregnantStat", CommonPreference.getStringValue(CommonPreference.PREGNANTSTAT, ""));
//		}
//		if(!TextUtils.isEmpty(CommonPreference.getStringValue(CommonPreference.DUEDATE, ""))){
//			params.put("dueDate", CommonPreference.getStringValue(CommonPreference.DUEDATE, ""));
//		}
//		if(array.length() > 0) {
//			params.put("babys", array.toString());
//		}
        LogUtil.i("liang", "注册params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.REGISTER, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("liang", "注册error：" + e.toString());
                tvBtnBegin.setOnClickListener(EditNameActivity.this);
                ToastUtil.toast(EditNameActivity.this, "注册出错");
                ProgressDialog.closeProgress();
            }


            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                tvBtnBegin.setOnClickListener(EditNameActivity.this);
                LogUtil.i("liang", "注册：" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    ToastUtil.toast(EditNameActivity.this, "注册系统错误");
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        FileUtils.delAllFile(FileUtils.getIconDir());
                        toast("注册成功");
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            // 解析数据，保存userid
                            UserInfo userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
                            CommonPreference.setUserInfo(userInfo);
                            CommonPreference.setPhone(userInfo.getPhone());
                            com.alibaba.fastjson.JSONObject object = JSON.parseObject(baseResult.obj);
                            String accessSecret = object.getString("accessSecret");
                            String accessToken = object.getString("accessToken");

                            if (!TextUtils.isEmpty(accessSecret)) {
                                CommonPreference.setAccessSecret(accessSecret);
                            }

                            if (!TextUtils.isEmpty(accessToken)) {
                                CommonPreference.setAccessToken(accessToken);
                            }

                            startRequestForUploadPushDevice();
                            LoginEvent lEvent = new LoginEvent();
                            lEvent.isLogin = true;
                            EventBus.getDefault().post(lEvent);

                            FinishEvent fEvent = new FinishEvent();
                            fEvent.isFinish = true;
                            EventBus.getDefault().post(fEvent);

                            Intent intent = new Intent(EditNameActivity.this, ChoosePregnantStatActivity.class);
                            startActivity(intent);
                            EditNameActivity.this.finish();
                        }

                    } else {
                        CommonUtils.error(baseResult, EditNameActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //上传推送deviceId
    private void startRequestForUploadPushDevice() {
        if (!CommonUtils.isNetAvaliable(this)) {
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("aliyunDeviceId", CommonPreference.getStringValue(CommonPreference.ALI_PUSH_TOKEN, ""));
        LogUtil.e("lalo", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.UPLOAD_PUSH_DEVICE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("laloErrorApp", "startRequestForUploadPushDevice:" + e.toString());
            }


            @Override
            public void onResponse(String response) {
                LogUtil.e("laloResponseApp", "startRequestForUploadPushDevice:" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {

                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("lalo", "startRequestForUploadPushDevice crash:" + e.getMessage());
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBtnBegin:
                if (TextUtils.isEmpty(etUserName.getText().toString().trim())) {
                    toast("请输入昵称");
                    return;
                }
                if (!CommonUtils.isNickName(etUserName.getText().toString().trim())) {
                    toast("只支持汉字、字母及数字");
                    return;
                }
                if (etUserName.getText().toString().contains("孙俪") ||
                        etUserName.getText().toString().contains("黄奕") ||
                        etUserName.getText().toString().contains("Suki妮")) {
                    toast("用户昵称非法，请重新输入");
                    return;
                }
                dialog = new TextDialog(this, false);
                dialog.setContentText("昵称确定后不可更改，确定提交吗？");
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
                        tvBtnBegin.setOnClickListener(null);
                        startRequestForCheckNickName();
                    }
                });
                dialog.show();

                break;
            case R.id.ivHeader:
//			imgPath = FileUtils.getCameraPhotoPath();
                imgPath = FileUtils.getIconDir() + fileName;
//			createHomeCaminaDialog();
                PhotoDialog dialog = new PhotoDialog(EditNameActivity.this);
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
        }
    }

    private Dialog photoDialog;
    // 拍照
    private LinearLayout layoutMenu;

    private TextView tvCamera, tvAlbum;
    private String imgPath = "";

    /**
     * 首页底部(拍照，相片)缩放，移动 对话框
     */
    public void createHomeCaminaDialog() {
        imgPath = FileUtils.getCameraPhotoPath();
        photoDialog = new Dialog(EditNameActivity.this, R.style.photo_dialog);
        photoDialog.setContentView(R.layout.dialog_release_menu);
        Window window = photoDialog.getWindow();
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        lp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.DialogBottomTransScaleStyle);

        layoutMenu = (LinearLayout) photoDialog.findViewById(R.id.layoutMenu);
        layoutMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                photoDialog.dismiss();
            }
        });

        // 拍照
        tvCamera = (TextView) photoDialog.findViewById(R.id.tvCamera);
        tvAlbum = (TextView) photoDialog.findViewById(R.id.tvAlbum);

        tvCamera.setOnClickListener(new OnClickListener() {// 相机

            @Override
            public void onClick(View v) {
                photoDialog.dismiss();

            }
        });
        tvAlbum.setOnClickListener(new OnClickListener() {// 相册

            @Override
            public void onClick(View v) {
                photoDialog.dismiss();

            }
        });
        photoDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.INTENT_FOR_RESULT_MAIN_TO_CAMERA) {
                // 判断图片是否旋转，处理图片旋转
                ExifInterface exif;
                Bitmap bitmap = ImageUtils.getLoacalBitmap(EditNameActivity.this, imgPath);
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
                ImageUtils.cropPhoto(EditNameActivity.this, Uri.fromFile(temp));//裁剪图片
            } else if (requestCode == MyConstants.INTENT_FOR_RESULT_TO_ALBUM) {
                ImageUtils.cropPhoto(EditNameActivity.this, data.getData());//裁剪图片
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
//	                    ivHeader.setImageBitmap(head);//用ImageView显示出来
                        icon = imgPath;
                        ImageLoader.resizeSmall(ivHeader, "file://" + icon, 1);
                    }
                }
            }
        }

    }

}
