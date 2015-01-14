package com.pgw.mobilesafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
/**
 * 通话记录的Dao对通话记录的CRUD操作
 */
public class CallLogDao {
	private Context context;

	public CallLogDao(Context context) {
		this.context = context;
	}
	/**
	 * 删除最后一条记录
	 * @param number 要删除的电话号码
	 * @return  
	 */
	public boolean delLast(String number){
		Uri uri=Uri.parse("content://call_log/calls");
		Cursor cursor=context.getContentResolver().query(uri, new String[]{"_id"}, "number=?", new String[]{number}, "_id desc limit 1");
		if (cursor!=null && cursor.moveToFirst()) {
			String _id=cursor.getString(0);
			int res=context.getContentResolver().delete(uri, "_id=?", new String[]{_id});
			return res>0?true:false;
		}
		return false;
	}
	
	

}
