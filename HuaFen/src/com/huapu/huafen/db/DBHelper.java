package com.huapu.huafen.db;

import com.huapu.huafen.utils.CommonPreference;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper {

	// 数据库名称
	private static final String DATA_BASE_NAME = "huafer.db";

	// 版本
	private static final int VERSION = 3;

	public static SqliteHelper getSqliteHelper(Context context) {

		return new SqliteHelper(context, DATA_BASE_NAME, null, VERSION);
	}

	public static class SqliteHelper extends SQLiteOpenHelper {

		Context context;

		public SqliteHelper(Context context, String name,
							CursorFactory factory, int version) {
			super(context, name, factory, version);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			try {
				db.execSQL(CacheDataService.CREATE_TABLE_SQL);
				db.execSQL(RongPushService.CREATE_TABLE_SQL);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			SharedPreferences sp = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
//			Editor editor = sp.edit();
//			editor.remove(MyConstants.SP_DB_INIT);
//			editor.commit();
			CommonPreference.setCacheVersion("");
			// 先删除表，再创建表
			// 城市表
			String cacheSql = "DROP TABLE IF EXISTS " + CacheDataService.TABLE_NAME;
			String rongPushSql = "DROP TABLE IF EXISTS " + RongPushService.TABLE_NAME;
			db.execSQL(cacheSql);
			db.execSQL(rongPushSql);
			this.onCreate(db);
		}
	}
}
