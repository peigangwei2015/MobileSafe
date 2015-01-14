package com.pgw.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	private SharedPreferences sp;
	private TextView tv_safenumber;
	private ImageView iv_protecting_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = sp.getBoolean("configed", false);
//		判断是否设置信息
		if (!configed) {
//			跳转至设置向导
			Intent intent=new Intent(this, Setup1Activity.class);
			startActivity(intent);
			return;
		}
		setContentView(R.layout.activity_lost_find);
		tv_safenumber=(TextView) findViewById(R.id.tv_safenumber);
		iv_protecting_status=(ImageView) findViewById(R.id.iv_protecting_status);
		
		tv_safenumber.setText(sp.getString("safeNumber", ""));
		boolean protecting=sp.getBoolean("protecting", false);
		if (protecting) {
			iv_protecting_status.setImageResource(R.drawable.lock);
		}else{
			iv_protecting_status.setImageResource(R.drawable.unlock);
		}
		
	}
	/**
	 * 重新进入设置向导
	 * @param v
	 */
	public void reEnterSetup(View v){
		Intent intent=new Intent(this, Setup1Activity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

}
