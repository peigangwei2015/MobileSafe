package com.pgw.mobilesafe.test;

import java.util.List;
import java.util.Random;

import com.pgw.mobilesafe.db.dao.BlackNumberDao;
import com.pgw.mobilesafe.domain.BlackNumberInfo;

import android.test.AndroidTestCase;

public class TestBlackNumberDao extends AndroidTestCase {
	/**
	 * 测试插入数据
	 */
	public void testInsert () {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		Random rm=new Random();
		String number ="135000000";
		for (int i = 10; i < 100; i++) {
			BlackNumberInfo info=new BlackNumberInfo();
			info.setMode(String.valueOf(rm.nextInt(3)+1));
			info.setNumber(number+i);
			dao.insert(info);
		}
		}
	/**
	 * 测试删除数据
	 */
	public void testDelete () {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		String number111 = "123";
		boolean b=dao.delete(number111);
		assertTrue(b);
	}
	/**
	 * 测试删除全部数据
	 */
	public void testDeleteAll () {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		boolean b=dao.deleteAll();
		assertTrue(b);
	}
	/**
	 * 测试更新拦截模式
	 */
	public void testUpdateMode () {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		boolean b=dao.updateMobe("13500000010", BlackNumberInfo.SMS_HOLDE_UP);
		assertTrue(b);
	}
	
	
	/**
	 * 测试查找
	 */
	public void testFind () {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		BlackNumberInfo b=dao.find("13500000010");
		assertNotNull(b);
	}
	
	
	/**
	 * 测试查找全部
	 */
	public void testFindAll() {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		List<BlackNumberInfo> list=dao.findAll();
		for (BlackNumberInfo info:list) {
			System.out.println(info);
		}
	}
}
