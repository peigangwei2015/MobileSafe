package com.pgw.mobilesafe.receiver;

import com.pgw.mobilesafe.R;
import com.pgw.mobilesafe.db.dao.BlackNumberDao;
import com.pgw.mobilesafe.domain.BlackNumberInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {


	private LocationListener listener =new MyListener();
	private String phone;
	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
//		获取安全号码
		phone=context.getSharedPreferences("config", context.MODE_PRIVATE).getString("safeNumber", "");
	
		for (Object obj:objs) {
			SmsMessage sms=SmsMessage.createFromPdu((byte[]) obj);
			String sender=sms.getDisplayOriginatingAddress();
			String body=sms.getDisplayMessageBody();
			if (body.equals("#*alarm*#")) {
//				播放报警音乐
				MediaPlayer mediaPlayer=MediaPlayer.create(context, R.raw.ylzs);
				mediaPlayer.setLooping(false);
				mediaPlayer.setVolume(1.0f, 1.0f);
				mediaPlayer.start();
				abortBroadcast();
			}else if(body.equals("#*location*#")){
//				开启定位
				startLocation(context);
			}else if(body.equals("#*wipedata*#")){
				
			}else if(body.equals("#*lockscreen*#")){
				
			}

			
			
			
		}
		
	}
	
	
	public void startLocation(Context context){
		LocationManager lm=(LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		Criteria criteria=new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 0, 0, listener  );
	}
	
	private class MyListener implements LocationListener{
		@Override
		public void onLocationChanged(Location location) {
			StringBuffer sb=new StringBuffer();
			sb.append("经度："+location.getLongitude()+"\r\n");
			sb.append("纬度："+location.getLatitude()+"\r\n");
			sb.append("精确度："+location.getAccuracy()+"\r\n");
			SmsManager.getDefault().sendTextMessage(phone, null, sb.toString(), null, null);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		@Override
		public void onProviderEnabled(String provider) {}
		@Override
		public void onProviderDisabled(String provider) {}
		
	}

}
