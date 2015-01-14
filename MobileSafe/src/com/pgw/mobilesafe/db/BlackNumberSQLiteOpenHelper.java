package com.pgw.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberSQLiteOpenHelper extends SQLiteOpenHelper {

	public BlackNumberSQLiteOpenHelper(Context context) {
		super(context, "blackNumber.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String sql="create table blackNumber(id integer primary key autoincrement,number varchar(20),mode varchar(1))";
		db.execSQL(sql);

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
