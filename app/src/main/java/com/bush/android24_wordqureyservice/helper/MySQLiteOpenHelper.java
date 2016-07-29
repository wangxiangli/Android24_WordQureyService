package com.bush.android24_wordqureyservice.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "db_words.db";
	private final static int VERSION = 1;
	public SQLiteDatabase dbConn = null;

	public MySQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		// 执行数据库的创建
		dbConn = getReadableDatabase();
	}

	// 对于数据库中的表的创建
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_words(_id INTEGER PRIMARY KEY AUTOINCREMENT , word , detail)");
		db.execSQL("CREATE TABLE IF NOT EXISTS tb_newwords(_id INTEGER PRIMARY KEY AUTOINCREMENT , word , detail)");
	}

	// 对于数据库中的表进行版本更新
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS tb_words");
			db.execSQL("DROP TABLE IF EXISTS tb_newwords");
			onCreate(db);
		}
	}

	public Cursor selectCursor(String sql, String[] selectionArgs) {
		return dbConn.rawQuery(sql, selectionArgs);
	}

	// select id,title from 表名
	public int selectCount(String sql, String[] selectionArgs) {
		Cursor cursor = dbConn.rawQuery(sql, selectionArgs);
		int count = cursor.getCount();
		if (cursor != null) {
			cursor.close();
		}
		return count;
	}

	public List<Map<String, String>> selectList(String sql,
			String[] selectionArgs) {
		Cursor cursor = dbConn.rawQuery(sql, selectionArgs);
		return cursorToList(cursor);
	}

	public List<Map<String, String>> cursorToList(Cursor cursor) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		while (cursor.moveToNext()) {
			// 对于每一行数据进行操作
			Map<String, String> map = new HashMap<String, String>();
			// 对于每行数据的每列进行操作
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
			list.add(map);
		}
		return list;
	}

	public boolean execData(String sql, Object[] bindArgs) {
		try {
			if (bindArgs == null) {
				dbConn.execSQL(sql);
			} else {
				dbConn.execSQL(sql, bindArgs);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void destroy() {
		if (dbConn != null) {
			dbConn.close();
		}
	}
}
