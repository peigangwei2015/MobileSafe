package com.pgw.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BootCompletReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompletReceiver";
	private SharedPreferences sp;
	private TelephonyManager tm;
	@Override
	public void onReceive(Context context, Intent intent) {
//		判断Sim卡是否变更
		sp=context.getSharedPreferences("config", context.MODE_PRIVATE);
		tm=(TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
//		取出保存的SIm串号
		String saveSim=sp.getString("sim", "");
		
//		获取现在的Sim卡串号
		String currentSim=tm.getSimSerialNumber();
//		比较是否一致
		if (!saveSim.equals(currentSim)) {
//			Sim卡更换，发短信给安全号码
			Log.i(TAG, "Sim卡更换，发短信给安全号码");
			Toast.makeText(context, "Sim卡更换，发短信给安全号码", 1).show();
		}
		
		
		
		

	}

}
