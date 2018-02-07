package com.huapu.huafen.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.activity.MainActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.activity.BindPhoneActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * SinaWeibo.NAME
 * Wechat.NAME
 * @author liang_xs
 *
 */
public class ShareLoginUtil {

	/**
	 * @Description: 第三方登录
	 */
	public static void shareLogin(final Activity activity, final String name){
		startRequestForUserLogin(activity, "1", "o2NPGvisqesbbW6C-SoNqlNarUeg", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "爵色","0");
//		startRequestForUserLogin(activity, "1", "o2NPGvntgWc39ZWL5RGLxhmFKvvA", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "成龙","0");
//		startRequestForUserLogin(activity, "1", "o2NPGvntgWc39ZWL5RGLxhmFKvvA3", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "测试","0");
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
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
//						Toast.makeText(activity, "onError: " + name + arg1, Toast.LENGTH_SHORT).show();
							if (platform != null) {
								if (platform.isValid ()) {
									platform.removeAccount(true);
								}
							}
						}
					});
				}

				@Override
				public void onComplete(final Platform platform, int action,
									   HashMap<String, Object> arg2) {
					// 用户资源都保存到res
					// 通过打印res数据看看有哪些数据是你想要的
					if (action == Platform.ACTION_USER_INFOR) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
//							Toast.makeText(activity,"onComplete:" + platform.getName(), Toast.LENGTH_SHORT).show();
								taskOtherLogin(activity, platform);
							}
						});
					}
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
//						Toast.makeText(activity,"onCancel:" + name, Toast.LENGTH_SHORT).show();
//						// TODO 测试数据
//						String flagId = "2519872637";// 爵色微博
//						startRequestForIsUserType(activity, "2", flagId, "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "爵色o");
						}
					});
					if (platform != null) {
						if (platform.isValid ()) {
							platform.removeAccount(true);
						}
					}
				}
			});
		}

	}
	
	/**
	 * @Description: 清空USERID
	 */
	private static void shareLogout(Platform platform){
		if (platform != null) {
			if (platform.isValid ()) {
				platform.removeAccount(true);
			}
		}
	}
	
	private String otherTpye = "";
	private static void taskOtherLogin(final Activity activity, Platform platform){
		PlatformDb platDB = platform.getDb();// 获取数平台数据DB
		// 通过DB获取各种数据
		final String strUserId = platDB.getUserId();
		String strToken = platDB.getToken();
		String strUserIcon = platDB.getUserIcon();
		String strUserName = platDB.getUserName();
		String strGender = platDB.getUserGender();
		String strType = platform.getName();
//		FileUtils.writeString2File("userId:" + strUserId + "name:" + strType + "gender:" + strGender, "/sdcard/shareLogin/", "qqSex.txt");
		// TODO 数据返回服务器，判断用户是否已经注册
		if(strType.equals(SinaWeibo.NAME)) {
			strType = "2";
		} else if(strType.equals(Wechat.NAME)) {
			strType = "1";
		}
		if(strGender.equals("m")) {
			strGender = "1";
		} else if(strGender.equals("f")) {
			strGender = "0";
		}
		strUserName = strUserName.replaceAll(" ", "");
		ProgressDialog.showProgress(activity);
		startRequestForUserLogin(activity, "1", strUserId, strUserIcon, strUserName, strGender);
//		if(strType.equals("Wechat")) {
//			FileUtils.writeString2File("name:" + strType + "gender:" + strGender, "/sdcard/shareLogin/", "weixinSex.txt");
//			otherTpye = MyConstants.OTHER_LOGIN_WEIXIN;
////			if(strGender.equals("2")) {
////				strGender = "0";
////			} else {
////				strGender = "1";
////			}
//		} else if (strType.equals("SinaWeibo")) {
//			FileUtils.writeString2File("name:" + strType + "gender:" + strGender, "/sdcard/shareLogin/", "sinaSex.txt");
//			otherTpye = MyConstants.OTHER_LOGIN_SINA;
////			if(strGender.equals("f")) {
////				strGender = "0";
////			} else {
////				strGender = "1";
////			}
//		} else if (strType.equals("QZone")) {
//			otherTpye = MyConstants.OTHER_LOGIN_QQ;
////			if (!TextUtils.isEmpty(strGender)) {
////				if(strGender.equals("f")) {
////					strGender = "0";
////				} else {
////					strGender = "1";
////				}
//		}
//		new WXRegistAsyncTask(activity, new IUserInfoAsyncTaskCallBack() {
//			
//			@Override
//			public void onSuccess(UserInfoBean bean) {
//				if(bean != null) {
//					if(TextUtils.isEmpty(bean.phone)) {
//						Toast.makeText(activity, "未绑定用户", Toast.LENGTH_SHORT).show();
//						Intent intent = new Intent(activity,
//								RegestMenberActivity.class);
//						intent.putExtra("type", "2");
//						intent.putExtra("bind", otherTpye);
//						intent.putExtra("otherUid", strUserId);
//						activity.startActivity(intent);
//					} else {
//						Toast.makeText(activity, "已绑定用户", Toast.LENGTH_SHORT).show();
//						String tokensp = AppInfoUtils.getToken();
//						BaseActivity.session = new Session();
//						BaseActivity.session.token = tokensp;
//						BaseActivity.session.userInfoBean = bean;
//						AppInfoUtils.changeLoginState(true, BaseActivity.session);//保存登陆状态
//						Intent intent = new Intent(activity,
//								MainActivity.class);
//						activity.startActivity(intent);
//					}
//				}
//			}
//			
//			@Override
//			public void onFailed(String errorMessage) {
//				// TODO Auto-generated method stub
//				
//			}
//		}).execute(otherTpye, strUserId);
//		if (strType.equals("QZone")) {
//			strType = "qq";
//		} else if (strType.equals("SinaWeibo")) {
//			strType = "weibo";
//		}
//		if (strGender.equals("f")) {
//			strGender = "2";
//		} else if (strGender.equals("m")) {
//			strGender = "1";
//		} else if (TextUtils.isEmpty(strGender)) {
//			strGender = "0"; 
//		} else {
//			strGender = "0";
//		}
//		UserData userData=UserData.getInstance(this.getApplicationContext());
//		userData.setOtherNickname(strUserName);
//		userData.setOtherType(strType);
//		userData.setOtherUsergender(strGender);
//		userData.setOtherUsericon(strUserIcon);
//		userData.setOtherUserid(strUserId);
//		String model = "mobile=no&version=4&module=member_login&loginfield=auto&action=login";
//		HttpRequest.loginRequest(this, model,phoneNum, password,this);
//		String md5=ToolsUtil.stringMD5(password);
//		String model = "mobile=no&version="+HttpConfig.version+"&module=member_otherlogin&userid="+strUserId+"&nickname="+strUserName+"&usericon="+strUserIcon+"&gender="+strGender+"&type="+strType;
//		HttpRequest.loginOtherRequest(this, model,this);
		shareLogout(platform);
	}
	
	/**
	 * 登录
	 * @param activity
	 * @param type
	 * 		短信验证类型
	 * 			1:用户注册
				2:密码找回
				3:修改密码
				4:微信换绑定
	 * @param uId
	 * 		微信id
	 * @param uIcon
	 * @param uName
	 * @param sex
	 */
	private static void startRequestForUserLogin(final Activity activity, final String type, final String uId, final String uIcon, final String uName, final String sex){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(MyConstants.REQUEST_VER, CommonUtils.getAppVersionName());
		params.put("uid", uId);
		LogUtil.i("liang", "登录params：" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.USERLOGIN, params,
				new StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						LogUtil.i("liang", "登录:"+response);
						Logger.d("danielluan", "登录:"+response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								if(!TextUtils.isEmpty(baseResult.obj)) {
									UserInfo userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
									CommonPreference.setUserId(userInfo.getUserId());
									CommonPreference.setStringValue(CommonPreference.USER_ICON, userInfo.getUserIcon());
									CommonPreference.setStringValue(CommonPreference.USER_NAME, userInfo.getUserName());
									CommonPreference.setIntValue(CommonPreference.USER_SEX, userInfo.getUserSex());
									CommonPreference.setIntValue(CommonPreference.USER_LEVEL, userInfo.getUserLevel());
									CommonPreference.setBooleanValue(CommonPreference.USER_CREDIT, userInfo.getHasCredit());
									Intent intent = new Intent(activity, MainActivity.class);
									activity.startActivity(intent);
									activity.finish();
								}
							} else if (baseResult.code == ParserUtils.RESPONSE_WECHAT_UID_UNEXIST) {
								Intent intent = new Intent(activity, BindPhoneActivity.class);
								intent.putExtra(MyConstants.EXTRA_TYPE, type);
								intent.putExtra(MyConstants.EXTRA_FLAGID, uId);
								intent.putExtra(MyConstants.EXTRA_WECHAT_ICON, uIcon);
								intent.putExtra(MyConstants.EXTRA_WECHAT_NAME, uName);
								intent.putExtra(MyConstants.EXTRA_WECHAT_SEX, sex);
								activity.startActivity(intent);
							} else {
								CommonUtils.error(baseResult, activity, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});
	}
}
