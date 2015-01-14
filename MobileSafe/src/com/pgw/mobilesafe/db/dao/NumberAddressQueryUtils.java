package com.pgw.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {
	private static String path = "data/data/com.pgw.mobilesafe/files/address.db";
	/**
	 * 查询号码来电归属地
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number) {
		String address = null;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (number.matches("^1[345678]\\d{9}$")) {
//			判断是正常手机号，去数据库查询
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });
			while (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		}else{
//			其他号码
			switch (number.length()) {
			case 3:
				address="特殊电话";
				break;
			case 4:
				address="模拟器号码";
				break;
			case 5:
				address="客服号码";
				break;
			case 7:
				address="本机号码";
				break;
			}
		}
		return address;
	}
}
