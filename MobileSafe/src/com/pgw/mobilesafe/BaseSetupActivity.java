package com.pgw.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	protected static final String TAG = "BaseSetupActivity";
	private GestureDetector gesture;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		gesture=new GestureDetector(this,new SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
//				屏蔽滑动过慢
				if (Math.abs(velocityX)<200) {
					Toast.makeText(BaseSetupActivity.this, "滑动不给力，请加快速度！", Toast.LENGTH_SHORT).show();
					return true;
				}
				
//					手指向右滑动进入上一步
				if ((e2.getRawX()-e1.getRawX())>200) {
					Log.i(TAG, "手指向右滑动进入上一步");
					showPre();
					return true;
				}
				
//					手指向左滑动进入下一步
				if ((e1.getRawX()-e2.getRawX())>200) {
					Log.i(TAG, "手指向左滑动进入下一步");
					showNext();
					return true;
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	public abstract void showNext() ;
	public abstract void showPre() ;
	/**
	 * 下一步
	 * @param v
	 */
	public void next(View v) {
		showNext();
	}
	/**
	 * 上一步
	 * @param v
	 */
	public void pre(View v) {
		showPre();
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gesture.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
}
