package com.pgw.mobilesafe.domain;

public class BlackNumberInfo {
	/**
	 * 短信拦截
	 */
	public static final String SMS_HOLDE_UP = "1";
	/**
	 * 电话拦截
	 */
	public static final String CALL_HOLDE_UP = "2";
	/**
	 * 全部拦截
	 */
	public static final String ALL_HOLDE_UP = "3";
	
	private String number;
	private String mode;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "BlackUserInfo [number=" + number + ", mode=" + mode + "]";
	}
}
