package com.huapu.huafen.db;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.huapu.huafen.db.DBHelper.SqliteHelper;
import com.huapu.huafen.utils.CommonPreference;

public class CacheDataService {
	/**
	 * 表名
	 */
	public static final String TABLE_NAME = "CACHE_DATA";
	/**
	 * 自增id
	 */
	private static String COLUMN_ID = "ID";

	/**
	 * 访问地址
	 */
	private static String COLUMN_HTTP_URL = "HTTP_URL";

	/**
	 * 缓存内容
	 */
	private static String COLUMN_JSON_DATA = "JSON_DATA";
	/**
	 * 缓存版本号
	 */
	public static String COLUMN_CACHE_VERSION = "cacheVersion";
	

	// 创建表的sql
	public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + " (" 
			+ COLUMN_ID + " INTEGER primary key autoincrement, " 
			+ COLUMN_HTTP_URL + " VARCHAR(200),"
			+ COLUMN_CACHE_VERSION + " VARCHAR(200),"
			+ COLUMN_JSON_DATA + " TEXT"
			+ " )";
	private String SELECT_TABLE = "select * from " + TABLE_NAME;

	SqliteHelper sqliteHelper = null;

	public CacheDataService(Context context) {

		sqliteHelper = DBHelper.getSqliteHelper(context);

	}
	/**
	 * 根据url和params获取where条件
	 * @param url
	 * @param params
	 * @return
	 */
	private String getWhere(String url, Map<String, String> params){
		StringBuffer where = new StringBuffer();
		where.append(" where ");
		where.append(COLUMN_HTTP_URL + "='" + url + "' ");
		for (String key : params.keySet()){
			if(key.equalsIgnoreCase(COLUMN_CACHE_VERSION)) {
				continue;
			}
			where.append(" and ");
			where.append(key + "='" + params.get(key) + "'");
		}
		return where.toString();
	}
	/**
	 * 根据params动态创建列
	 * @param params
	 */
	private void addColumn(Map<String, String> params){
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = sqliteHelper.getWritableDatabase();
			String sql = SELECT_TABLE + " where 1!=1";
			cursor = db.rawQuery(sql, null);
			
			List<String> columnList = Arrays.asList(cursor.getColumnNames());
			if(!TextUtils.isEmpty(columnList.toString())) {
				Log.d("sql", columnList.toString());
			}
			for (String columnName : params.keySet()){
				if(!columnList.contains(columnName)){
					String updateTable = "alter table " + TABLE_NAME + " add " + columnName + " VARCHAR(200)";
					db.execSQL(updateTable);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
	}

	/**
	 * 根据URL和PARAMS，删除缓存数据
	 * @param url
	 * @param params
	 * @return
	 */
	public void delCacheData(String url, Map<String, String> params){
		SQLiteDatabase sdb = null;
		try {

			sdb = sqliteHelper.getWritableDatabase();
			String deleteSql = "delete from " + TABLE_NAME + getWhere(url, params);
			sdb.execSQL(deleteSql);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			sdb.close();
		}
	}
	
	/**
	 * 根据URL和PARAMS，添加缓存数据
	 * @param url
	 * @param params
	 * @return
	 */
	public void addCacheData(String url, Map<String, String> params, String content){
//		params.put("token", String.valueOf(CommonPreference.getUserId()));
		addColumn(params);
		delCacheData(url, params);
		SQLiteDatabase db = null;
		db = sqliteHelper.getWritableDatabase();
		try {
			StringBuffer sql = new StringBuffer("insert into " + TABLE_NAME);
			sql.append(" (" + COLUMN_HTTP_URL);
			sql.append("," + COLUMN_JSON_DATA);
			StringBuffer values = new StringBuffer("values('" + url + "'");
			values.append(",'" + content + "'");
			for (String key : params.keySet()){
				//设置添加值
				sql.append(",");
				sql.append(key);
				//设置添加列
				values.append(",");
				values.append("'" + params.get(key) + "'");
			}
			sql.append(") ");
			values.append(")");
			sql.append(values);
			if(!TextUtils.isEmpty(sql.toString())) {
				Log.d("sql", sql.toString());
			}
			db.execSQL(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * 根据URL和PARAMS，获取缓存数据
	 * @param url
	 * @param params
	 * @return
	 */
	public String getCacheData(String url, Map<String, String> params){
//		params.put("token", String.valueOf(CommonPreference.getUserId()));
		String content = "";
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = sqliteHelper.getReadableDatabase();
			String sql = SELECT_TABLE + getWhere(url, params);
			cursor = db.rawQuery(sql, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				//获取content
				int contentIndex = cursor.getColumnIndex(COLUMN_JSON_DATA);
				content = cursor.getString(contentIndex);
			}
			if(!TextUtils.isEmpty(content)) {
				Log.d("sql", content);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
		return content;
	}
	
	/**
	 * 根据URL和PARAMS，获取缓存版本号
	 * @param url
	 * @param params
	 * @return
	 */
	public String getCacheVersion(String url, Map<String, String> params){
//		params.put("token", String.valueOf(CommonPreference.getUserId()));
		addColumn(params);
		String content = "";
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = sqliteHelper.getReadableDatabase();
			String sql = SELECT_TABLE + getWhere(url, params);
			cursor = db.rawQuery(sql, null);
			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				//获取content
				int contentIndex = cursor.getColumnIndex(COLUMN_CACHE_VERSION);
				content = cursor.getString(contentIndex);
			}
			if(!TextUtils.isEmpty(content)) {
				Log.d("sql", content);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
		return content;
	}
}
