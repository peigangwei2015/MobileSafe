package com.pgw.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.pgw.mobilesafe.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppInfoProvider {
	private Context context;
	
	public AppInfoProvider(Context context) {
		this.context = context;
	}

	/**
	 * 获取所有的应用程序信息
	 * @param context
	 * @return 
	 */
	public ArrayList<AppInfo> getAppInfos(){
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(0);
		ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packInfo:list) {
			//应用程序包名
			String packName=packInfo.packageName;
//			应用程序名
			String name=(String) packInfo.applicationInfo.loadLabel(pm);
//			应用程序图标
			Drawable icon=packInfo.applicationInfo.loadIcon(pm);
			AppInfo info=new AppInfo();
			info.setIcon(icon);
			info.setName(name);
			info.setPackageName(packName);
			int flag=packInfo.applicationInfo.flags;
//			判断是否是系统应用
			if ((flag & ApplicationInfo.FLAG_SYSTEM) ==0 ) {
//				用户程序
				info.setUser(true);
			}else{
//				系统程序
				info.setUser(false);
			}
//			判断程序是否安装在SD卡上
			if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
//				手机内存中
				info.setRom(true);
			}else{
//				SD卡内存中
				info.setRom(false);
			}
			
			appInfos.add(info);
		}
		return appInfos; 
	}
}
