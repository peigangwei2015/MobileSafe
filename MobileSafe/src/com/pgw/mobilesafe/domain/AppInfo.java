package com.pgw.mobilesafe.domain;

import android.graphics.drawable.Drawable;
/**
 *应用程序信息封装Bean 
 */
public class AppInfo {
	private Drawable icon;
	private String name;
	private String packageName;
	private boolean isUser;
	private boolean isRom;
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public boolean isUser() {
		return isUser;
	}
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
	public boolean isRom() {
		return isRom;
	}
	public void setRom(boolean isRom) {
		this.isRom = isRom;
	}
	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", name=" + name + ", packageName="
				+ packageName + ", isUser=" + isUser + ", isRom=" + isRom + "]";
	}
}
