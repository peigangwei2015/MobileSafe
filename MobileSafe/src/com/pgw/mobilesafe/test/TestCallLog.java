package com.pgw.mobilesafe.test;

import com.pgw.mobilesafe.db.dao.CallLogDao;

import android.test.AndroidTestCase;

public class TestCallLog extends AndroidTestCase {
	
	public void testDelLast(){
		CallLogDao dao=new CallLogDao(getContext());
		dao.delLast("10086");
	}

}
