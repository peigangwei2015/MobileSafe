package com.pgw.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class GPSService extends Service {

	private LocationListener listener = new MyListener();
	private LocationManager lm;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startLocation();
	}

	@Override
	public void onDestroy() {
		lm.removeUpdates(listener);
		listener = null;
		super.onDestroy();
	}

	public void startLocation() {
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 60 * 1000, 50, listener);
	}

	private class MyListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			StringBuffer sb = new StringBuffer();
			sb.append("经度：" + location.getLongitude() + "\r\n");
			sb.append("纬度：" + location.getLatitude() + "\r\n");
			sb.append("精确度：" + location.getAccuracy() + "\r\n");
			System.out.println(sb);
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean protecting = sp.getBoolean("protecting", false);
			if (protecting) {
				SmsManager.getDefault().sendTextMessage(
						sp.getString("safeNumber", ""), null, sb.toString(),
						null, null);
			}

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

	}

}
