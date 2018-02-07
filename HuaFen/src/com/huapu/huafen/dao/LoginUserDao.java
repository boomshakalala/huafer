//package com.huapu.huafen.dao;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import android.content.Context;
//
//import com.huapu.huafen.db.DatabaseHelper;
//import com.j256.ormlite.android.apptools.OpenHelperManager;
//import com.j256.ormlite.dao.Dao;
//
//public class LoginUserDao {
//	private Dao<LoginUser, Integer> dao;
//	public LoginUserDao(Context context) {
//		DatabaseHelper dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
//		try {
//			dao = dbHelper.getDao(LoginUser.class);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//	public List<LoginUser> get(){
//		try {
//			return dao.queryForAll();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	public LoginUser getLoginUser(){
//		List<LoginUser> all = null;
//		try {
//			all = dao.queryForAll();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		if (all!=null&&all.size()>0) {
//			return all.get(0);
//		}
//		return null;
//	}
//	public void addOrUpdate(LoginUser testUser) {
//		try {
//			dao.createOrUpdate(testUser);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//}
