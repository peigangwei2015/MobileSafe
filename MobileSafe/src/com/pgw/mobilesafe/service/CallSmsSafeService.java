package com.pgw.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.pgw.mobilesafe.db.dao.BlackNumberDao;
import com.pgw.mobilesafe.db.dao.CallLogDao;
import com.pgw.mobilesafe.domain.BlackNumberInfo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsSafeService extends Service {
	private BroadcastReceiver receiver;
	private TelephonyManager tm;
	private PhoneStateListener listener;
	private BlackNumberDao blackNumberDao;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		blackNumberDao = new BlackNumberDao(this);
		// 监听电话状态
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		// 注册短信广播接受者
		regSmsReceiver();

		super.onCreate();
	}

	/**
	 * 注册短信广播接受者
	 */
	private void regSmsReceiver() {
		receiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		// 取消注册短信广播接受者
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	/**
	 * 短信拦截，广播接受者
	 */
	private class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (int i = 0; i < objs.length; i++) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) objs[i]);
				String sender = sms.getDisplayOriginatingAddress();
				// 判断是否是黑名单号码，如果是则判断是否为短信拦截或全部拦截
				BlackNumberDao dao = new BlackNumberDao(context);
				BlackNumberInfo bInfo = dao.find(sender);
				if (bInfo != null
						&& (bInfo.getMode()
								.equals(BlackNumberInfo.SMS_HOLDE_UP) || bInfo
								.getMode().equals(BlackNumberInfo.ALL_HOLDE_UP))) {
					abortBroadcast();
				}
			}
		}
	}

	/**
	 * 电话状态监听器
	 */
	private class MyPhoneStateListener extends PhoneStateListener {

		private static final String TAG = "MyPhoneStateListener";

		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			// 电话响铃
			case TelephonyManager.CALL_STATE_RINGING:
				// 判断是否是黑名单号码，并且是否是电话拦截或是全部拦截，如果是，则挂断电话
				BlackNumberInfo info = blackNumberDao.find(incomingNumber);
				if (info != null
						&& (info.getMode().equals(BlackNumberInfo.ALL_HOLDE_UP) || info
								.getMode()
								.equals(BlackNumberInfo.CALL_HOLDE_UP))) {
					Log.v(TAG, "挂断电话！");
//					Toast.makeText(getApplicationContext(), "挂断电话！", 1).show();
//					挂断电话
					endCall();
//					删除通话记录
					new Thread(){
						public void run() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							CallLogDao callLogDao=new CallLogDao(getApplicationContext());
							callLogDao.delLast(incomingNumber);
						};
					}.start();
					
				}
				break;

			}

		}
		/**
		 * 挂断电话
		 */
		private void endCall() {
			try {
				Method method=TelephonyManager.class.getDeclaredMethod("getITelephony", (Class[]) null);
				method.setAccessible(true);
				TelephonyManager tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				ITelephony iTelephony=(ITelephony) method.invoke(tm, (Object[]) null);
				iTelephony.endCall();
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
