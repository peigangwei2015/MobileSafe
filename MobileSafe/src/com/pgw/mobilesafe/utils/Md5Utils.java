package com.pgw.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {
	/**
	 * md5算法，密码加密
	 * @param password
	 * @return
	 */
	public static String md5(String password) {
		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] temp = digest.digest(password.getBytes());
			for (byte b : temp) {
				int res = b & 0xff;
				String str = Integer.toHexString(res);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

	}
}
