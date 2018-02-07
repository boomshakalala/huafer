package com.huapu.huafen.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huapu.huafen.utils.LogUtil;

import java.util.List;

import static android.R.id.list;

/**
 * Created by qwe on 2017/10/8.
 */

public class ImDao {
    //    public ImDao(Context context) {
    //        imdbHelper = new IMDBHelper(context);
//        this.context = context;
//    }
//    private final IMDBHelper imdbHelper;
//    private SQLiteDatabase db;
//    private Context context;
    public static final String TABLE_NAME = "message";
    public static final String INFO = "info";
    public static final String MESSAGE_ID = "messageid";
//    public static final String update = "update " + TABLE_NAME + " set " +
//            INFO + "='1'" + " where " + MESSAGE_ID + "'" + messageId + "'";
//    public static final String insert = "insert into " + TABLE_NAME + " (" + MESSAGE_ID + ", " + INFO + ") values (" + messageId + ", 0)";

    public static void insertMsg(SQLiteDatabase db, String messageId) {
//        db.beginTransaction();
//        db.setTransactionSuccessful();
        if (queryMsg2(db,messageId)){
            ContentValues values = new ContentValues();
            values.put(MESSAGE_ID,messageId);
            values.put(INFO,0);
            long index = db.insert(TABLE_NAME,null,values);
            if(index > 0) {
                LogUtil.e("ImDao","添加成功");
            }else {
                LogUtil.e("ImDao","添加失败");
            }
            db.close();
        }
    }

    public static void insertMsg(SQLiteDatabase db, List<String> messageIdList) {
        for (String messageId : messageIdList) {
            if (queryMsg2(db,messageId)){
//                db.execSQL( "insert into " + TABLE_NAME + " (" + MESSAGE_ID + ", " + INFO + ") values (" + messageId + ", 0)");
                ContentValues values = new ContentValues();
                values.put(MESSAGE_ID,messageId);
                values.put(INFO,0);
                long index = db.insert(TABLE_NAME,null,values);
                if(index > 0) {
                   LogUtil.e("ImDao","添加成功");
                }else {
                    LogUtil.e("ImDao","添加失败");
                }
            }
        }
        db.close();
    }

    public static void updateMSg(SQLiteDatabase db, String messageId) {
//        ContentValues values=new ContentValues();
//        values.put("info",1);
//        db.update(TABLE_NAME,values,"messageid=?",new String[]{id});
//        db.execSQL("update " + TABLE_NAME + " set " +
//                INFO + "='1'" + " where " + MESSAGE_ID + "'" + messageId + "'");
        ContentValues values = new ContentValues();
        values.put(INFO,1);
        db.update(TABLE_NAME,values,MESSAGE_ID+"=?",new String[]{""+messageId});
        db.close();
    }

    public static void updateMSg(SQLiteDatabase db, List<String> messageIdList) {
        for (String messageId : messageIdList) {
            ContentValues values = new ContentValues();
            values.put(INFO,1);
            db.update(TABLE_NAME,values,MESSAGE_ID+"=?",new String[]{""+messageId});
        }
        db.close();
    }

    public static int queryMsg(SQLiteDatabase db, String messageId) {
        Cursor cursor = db.query(TABLE_NAME, null, MESSAGE_ID + " = ?", new String[]{messageId}, null, null, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
           return cursor.getInt(cursor.getColumnIndex(INFO));
        }else {
            return 1;
        }

    }
    public static Boolean queryMsg(SQLiteDatabase db, List<String> messageIdList) {
        Boolean hasUnRead = false;
        for (String messageId : messageIdList) {
            Cursor cursor = db.query(TABLE_NAME, null, MESSAGE_ID + " = ?", new String[]{messageId}, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getInt(cursor.getColumnIndex(INFO)) == 0) hasUnRead = true;
            }
        }
        db.close();
        return hasUnRead;
    }

    public static boolean queryMsg2(SQLiteDatabase db, String messageId) {
        Cursor cursor = db.query(TABLE_NAME, null, MESSAGE_ID + " = ?", new String[]{messageId}, null, null, null);
        if (cursor.getCount() > 0) {
            return false;
        } else {
            return true;
        }
    }
}
