//package com.huapu.huafen.dao;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import android.content.Context;
//
//import com.huapu.huafen.beans.CityBean;
//import com.huapu.huafen.beans.DisBean;
//import com.huapu.huafen.db.DatabaseHelper;
//import com.j256.ormlite.dao.Dao;
//
//public class DisDao {
//	private Dao<DisBean, Integer> disDaoOpe;
//	private DatabaseHelper helper;
//
//	@SuppressWarnings("unchecked")
//	public DisDao(Context context) {
//		try {
//			helper = DatabaseHelper.getHelper(context);
//			disDaoOpe = helper.getDao(DisBean.class);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 添加一个地区
//	 * 
//	 * @param dis
//	 */
//	public void add(DisBean dis) {
//		try {
//			disDaoOpe.create(dis);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void delete(){
//		try {
//			disDaoOpe.delete(get());
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public List<DisBean> get() {
//		List<DisBean> disBeans = null;
//		try {
//			disBeans = disDaoOpe.queryForAll();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return disBeans;
//	}
//	
//	/**
//	 * 通过Id得到一个城市
//	 * 
//	 * @param disId
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public DisBean getCityWithDis(int disId) {
//		DisBean disBean = null;
//		try {
//			disBean = disDaoOpe.queryForId(disId);
//			helper.getDao(CityBean.class).refresh(disBean.getCity());
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return disBean;
//	}
//
//	/**
//	 * 通过Id得到一个地区
//	 * 
//	 * @param disId
//	 * @return
//	 */
//	public DisBean get(int disId) {
//		DisBean article = null;
//		try {
//			article = disDaoOpe.queryForId(disId);
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return article;
//	}
//
//	/**
//	 * 通过cityId获取所有的地区
//	 * 
//	 * @param cityId
//	 * @return
//	 */
//	public List<DisBean> listByUserId(int cityId) {
//		try {
//			return disDaoOpe.queryBuilder().where().eq("city_id", cityId)
//					.query();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//}
