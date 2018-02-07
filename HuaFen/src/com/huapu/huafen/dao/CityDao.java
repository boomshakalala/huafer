//package com.huapu.huafen.dao;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import android.content.Context;
//
//import com.huapu.huafen.beans.CityBean;
//import com.huapu.huafen.db.DatabaseHelper;
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.table.TableUtils;
//
//public class CityDao {
//	private Context context;
//	private Dao<CityBean, Integer> cityDaoOpe;
//	private DatabaseHelper helper;
//
//	public CityDao(Context context) {
//		this.context = context;
//		try {
//			helper = DatabaseHelper.getHelper(context);
//			cityDaoOpe = helper.getDao(CityBean.class);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 增加一个城市
//	 * 
//	 * @param city
//	 */
//	public void add(CityBean city) {
//		try {
//			cityDaoOpe.create(city);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//	}
//	public void delete(){
//		try {
//			cityDaoOpe.delete(get());
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public List<CityBean> get() {
//		List<CityBean> cityBeans = null;
//		try {
//			cityBeans = cityDaoOpe.queryForAll();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return cityBeans;
//	}
//
//}
