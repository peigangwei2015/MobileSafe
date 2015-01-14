package com.pgw.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pgw.mobilesafe.R;
import com.pgw.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {

	protected static final String TAG = "AddressService";
	private TelephonyManager tm;
	private PhoneStateListener listener;
	private BroadcastReceiver receiver;
	private WindowManager wm;
	private View view;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 监听来电
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new PhoneStateListenerImp();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 注册外拨电话接受者
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		receiver = new OutCallReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		// 取消注册外拨电话接受者
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	/**
	 * 监听来电
	 * 
	 * @author Administrator
	 */
	private class PhoneStateListenerImp extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			// 来电响铃，显示来电归属地
			case TelephonyManager.CALL_STATE_RINGING:
				String address = NumberAddressQueryUtils
						.queryNumber(incomingNumber);
				 Toast.makeText(getApplicationContext(), address, 1).show();
				 if(!TextUtils.isEmpty(address))myToast(address);
				break;
			// 挂断电话取消来电归属地显示
			case TelephonyManager.CALL_STATE_IDLE:
				if (view != null) {
					wm.removeView(view);
					view = null;
				}
				break;

			}
		}
	}

	/**
	 * 监听外拨电话
	 */
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			String address = NumberAddressQueryUtils.queryNumber(number);
			// Toast.makeText(getApplicationContext(), address, 1).show();
			if(!TextUtils.isEmpty(address))myToast(address);
		}

	}

	/**
	 * 自定义Toast样式
	 * 
	 * @param address
	 */
	public void myToast(String address) {
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		view = View.inflate(getApplicationContext(), R.layout.address_show,
				null);
		// 双击关闭
		view.setOnClickListener(new OnClickListener() {
			long[] mHits = new long[2];

			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					if (view != null)
						wm.removeView(view);
					view = null;
				}
			}
		});
		// 设置触摸事件
		view.setOnTouchListener(new OnTouchListener() {
			int startX, startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 按下事件
				case MotionEvent.ACTION_DOWN:
					//Log.i(TAG, "按下事件");
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				// 移动事件
				case MotionEvent.ACTION_MOVE:
					// //Log.i(TAG, "移动事件");
					// 设置偏移量
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();

					int dx = newX - startX;
					int dy = newY - startY;
					//Log.i(TAG, "dx:" + dx + "   dy:" + dy);
					params.x += dx;
					params.y += dy;
					//Log.i(TAG, "params.x= " + params.x + "  params.y="
//							+ params.y);
					// 判断是否边界
					if (params.x < 0) {
						params.x = 0;
					}

					if (params.y < 0) {
						params.y = 0;
					}

					if (params.x > (wm.getDefaultDisplay().getWidth() - view
							.getWidth())) {
						params.x = (wm.getDefaultDisplay().getWidth() - view
								.getWidth());
					}
					if (params.y > (wm.getDefaultDisplay().getHeight() - view
							.getHeight())) {
						params.y = (wm.getDefaultDisplay().getHeight() - view
								.getHeight());
					}
					// 更新UI
					wm.updateViewLayout(view, params);
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				// 抬起事件
				case MotionEvent.ACTION_UP:
					//Log.i(TAG, "抬起事件");
					// 记录位置
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					break;

				}
				return false;
			}
		});
		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int ids[] = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		view.setBackgroundResource(ids[sp.getInt("which", 0)]);
		// view.setBackgroundColor(Color.RED);

		TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
		tv_address.setText(address);

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		params.gravity = Gravity.TOP + Gravity.LEFT;
		// 初始化位置
		params.x = sp.getInt("lastX", 0);
		params.y = sp.getInt("lastY", 0);
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.format=PixelFormat.TRANSLUCENT;
		wm.addView(view, params);

	}

}
