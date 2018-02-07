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

public class RongPushService {
	/**
	 * 表名
	 */
	public static final String TABLE_NAME = "RONG_PUSH_DATA";
	/**
	 * 自增id
	 */
	private static String COLUMN_ID = "ID";

	/**
	 * 发送时间戳
	 */
	private static String COLUMN_SEND_TIME = "SEND_TIME";
	

	// 创建表的sql
	public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + " (" 
			+ COLUMN_ID + " INTEGER primary key autoincrement, " 
			+ COLUMN_SEND_TIME + " VARCHAR(200)"
			+ " )";
	private String SELECT_TABLE = "select * from " + TABLE_NAME;

	SqliteHelper sqliteHelper = null;

	public RongPushService(Context context) {

		sqliteHelper = DBHelper.getSqliteHelper(context);

	}

	/**
	 * @param sendTime
	 * @return
	 */
	public void delRongPushData(String sendTime){
		SQLiteDatabase sdb = null;
		try {

			sdb = sqliteHelper.getWritableDatabase();
			String deleteSql = "delete from " + TABLE_NAME + getWhere(sendTime);
			sdb.execSQL(deleteSql);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			sdb.close();
		}
	}
	
	/**
	 * @param sendTime
	 * @return
	 */
	public void addRongPushData(String sendTime){
		delRongPushData(sendTime);
		SQLiteDatabase db = null;
		db = sqliteHelper.getWritableDatabase();
		try {
			StringBuffer sql = new StringBuffer("insert into " + TABLE_NAME);
			sql.append(" (" + COLUMN_SEND_TIME);
			StringBuffer values = new StringBuffer("values('" + sendTime + "'");
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
	 * @param url
	 * @param params
	 * @return
	 */
	public boolean getRongPushData(String sendTime){
		boolean isPush = false;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = sqliteHelper.getReadableDatabase();
			String sql = SELECT_TABLE + getWhere(sendTime);
			cursor = db.rawQuery(sql, null);
			if (cursor.getCount() > 0) {
				isPush = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
		return isPush;
	}
	
	/**
	 * 获取where条件
	 * @param sendTime
	 * @return
	 */
	private String getWhere(String sendTime){
		StringBuffer where = new StringBuffer();
		where.append(" where ");
		where.append(COLUMN_SEND_TIME + "='" + sendTime + "' ");
		return where.toString();
	}
}
