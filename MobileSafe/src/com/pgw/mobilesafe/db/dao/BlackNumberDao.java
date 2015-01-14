package com.pgw.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.pgw.mobilesafe.db.BlackNumberSQLiteOpenHelper;
import com.pgw.mobilesafe.domain.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDao {
	private SQLiteOpenHelper sqlHelper;
	private SQLiteDatabase db;
	private String table = "blackNumber";

	/**
	 * Dao的构造方法
	 * 
	 * @param context
	 *            上下文
	 */
	public BlackNumberDao(Context context) {
		sqlHelper = new BlackNumberSQLiteOpenHelper(context);
	}

	/**
	 * 插入黑名单号码
	 * 
	 * @param info
	 *            黑名单号码信息对象
	 * @return 插入在数据库的ID
	 */
	public long insert(BlackNumberInfo info) {
		db = sqlHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", info.getNumber());
		values.put("mode", info.getMode());
		long res = db.insert(table, null, values);
		return res;
	}

	/**
	 * 删除黑名单号码
	 * 
	 * @param number
	 *            要删除的黑名单号码
	 * @return 如果删除成功返回Ture，否则返回false。
	 */
	public boolean delete(String number) {
		db = sqlHelper.getWritableDatabase();
		int res = db.delete(table, "number=?", new String[] { number });
		if (res > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 删除全部黑名单号码
	 * @param number
	 *            要删除的黑名单号码
	 * @return 如果删除成功返回Ture，否则返回false。
	 */
	public boolean deleteAll() {
		db = sqlHelper.getWritableDatabase();
		int res = db.delete(table, null, null);
		if (res > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 更新黑名单拦截模式
	 * 
	 * @param number
	 *            要更改的黑名单号码
	 * @param mode
	 *            新的拦截模式
	 * @return 如果删除成功返回Ture，否则返回false。
	 */
	public boolean updateMobe(String number, String mode) {
		db = sqlHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		int res = db.update(table, values, "number=?", new String[] { number });
		if (res > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 查询一个号码是不是一个黑名单号码
	 * 
	 * @param number
	 *            要查新的号码号码
	 * @return 如果是一个黑名单号码，则返回黑名单号码对象，如果不是，则返回Null
	 */
	public BlackNumberInfo find(String number) {
		BlackNumberInfo info = null;
		db = sqlHelper.getReadableDatabase();
		Cursor cursor = db.query(table, new String[] { "mode" }, "number=?",
				new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			String mode = cursor.getString(0);
			info = new BlackNumberInfo();
			info.setMode(mode);
			info.setNumber(number);
		}
		return info;
	}

	/**
	 * 查询所有的黑名单号码
	 * @return 黑名单号码的集合
	 */
	public List<BlackNumberInfo> findAll() {
		List<BlackNumberInfo> list=new ArrayList<BlackNumberInfo>();
		db = sqlHelper.getReadableDatabase();
		Cursor cursor = db.query(table, null, null, null, null, null, "id desc");
		while (cursor.moveToNext()) {
			String mode = cursor.getString(cursor.getColumnIndex("mode"));
			String number = cursor.getString(cursor.getColumnIndex("number"));
			BlackNumberInfo info = new BlackNumberInfo();
			info.setMode(mode);
			info.setNumber(number);
			list.add(info);
		}
		return list;
	}

}
