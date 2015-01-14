package com.pgw.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.pgw.mobilesafe.ui.SetingItemView;

public class Setup2Activity extends BaseSetupActivity {
	private SetingItemView siv_setup2_sim;
	private TelephonyManager tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//		恢复是否绑定Sim卡
		siv_setup2_sim=(SetingItemView) findViewById(R.id.siv_setup2_sim);
		String sim=sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			siv_setup2_sim.setChecked(false);
		}else {
			siv_setup2_sim.setChecked(true);
		}
		
		siv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				判断是否绑定Sim卡
				Editor editor = sp.edit();
				if (siv_setup2_sim.isChecked()) {
					siv_setup2_sim.setChecked(false);
					editor.putString("sim", null);
				}else{
					siv_setup2_sim.setChecked(true);
					String sim=tm.getSimSerialNumber();
					editor.putString("sim", sim);
				}
				editor.commit();
				
			}
		});
	}
	

	@Override
	public void showNext() {
		String sim=sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(Setup2Activity.this, "你没有绑定Sim卡，请绑定Sim卡。", 1).show();
			return;
		}
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);		
	}

}
