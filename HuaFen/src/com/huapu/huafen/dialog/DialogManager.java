package com.huapu.huafen.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.avos.avoscloud.PushService;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.activity.LoginActivity;
import com.huapu.huafen.activity.VerifiedActivity;
import com.huapu.huafen.common.RequestCode;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;

import java.util.ArrayList;

import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * dialog管理类
 * Created by dengbin on 17/12/20.
 */
public class DialogManager {

    // 实名认证dialog
    public static void toVerify(final Context context) {
        final TextDialog dialog = new TextDialog(context, false);
        dialog.setContentText("亲，您还未开通实名认证，1分钟完成认证后即可发布");
        dialog.setLeftText("取消");
        dialog.setRightColor(Color.parseColor("#2d8bff"));
        dialog.setLeftCall(new DialogCallback() {

            @Override
            public void Click() {
                dialog.dismiss();
            }
        });
        dialog.setRightText("去开通");
        dialog.setRightCall(new DialogCallback() {

            @Override
            public void Click() {
                Intent intent = new Intent(context, VerifiedActivity.class);
                context.startActivity(intent);
            }

        });
        dialog.show();
    }

    public static void loginAndDialog(final Activity activity) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(activity);
        } else {
            if (CommonPreference.getUserId() != 0)
                PushService.unsubscribe(MyApplication.getApplication(), String.valueOf(CommonPreference.getUserId()));
            CommonPreference.cleanUserInfoAndAccess();
            CommonPreference.setIntValue("savedMoney", -1);//钱包金额
            CommonPreference.setUserId(0);
            LCIMConversationItemCache.getInstance().clearData();
            TextDialog dialog = new TextDialog(activity, false);
            dialog.setContentText("您的账号在其他设备登录，请重新登录！");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                }
            });
            dialog.show();
        }
    }

    // 弹取消关注对话框
    public static int concernedUserDialog(Context context, int followShip, final DialogCallback callback) {
        if (followShip == 2 || followShip == 4) {
            final TextDialog dialog = new TextDialog(context, false);
            dialog.setContentText("确定不再关注该用户了吗？");
            dialog.setLeftText("取消");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                }
            });
            dialog.setRightText("确定");
            dialog.setRightCall(callback);
            dialog.show();
            return 2;
        } else if (followShip == 1 || followShip == 3) {
            return 1;
        }
        return -1;
    }


    // 权限获取
    public static void requestPermission(final Activity activity, final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            new android.support.v7.app.AlertDialog.Builder(activity)
                    .setTitle("缺少权限")
                    .setMessage(rationale)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    //图片获取
    public static void pickImage(android.support.v4.app.Fragment context, ArrayList<String> selectedPaths, boolean showCamera) {
        if (checkPermission(context.getActivity())) {
            MultiImageSelector selector = MultiImageSelector.create();
            selector.showCamera(showCamera);
            selector.count(9);
            selector.multi();
            selector.origin(selectedPaths);
            selector.start(context, RequestCode.REQUEST_IMAGE_PICK);
        }
    }

    //图片获取
    public static void pickImage(Activity activity, ArrayList<String> selectedPaths, boolean showCamera) {
        if (checkPermission(activity)) {
            MultiImageSelector selector = MultiImageSelector.create();
            selector.showCamera(showCamera);
            selector.count(9);
            selector.multi();
            selector.origin(selectedPaths);
            selector.start(activity, RequestCode.REQUEST_IMAGE_PICK);
        }
    }

    //图片获取
    public static void pickImage(Activity activity, ArrayList<String> selectedPaths) {
        pickImage(activity, selectedPaths, false);
    }

    //图片获取
    public static void pickImage(Activity activity, boolean showCamera) {
        pickImage(activity, null, showCamera);
    }

    //图片获取
    public static void pickImage(Activity activity) {
        pickImage(activity, null, false);
    }

    //图片获取
    public static void pickImage(Fragment fragment) {
        pickImage(fragment, null, false);
    }

    private static boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            DialogManager.requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE,
                    "是否允许该程序访问您设备上的的照片, 媒体内容和文件?",
                    RequestCode.REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            return false;
        }
        return true;
    }

}
