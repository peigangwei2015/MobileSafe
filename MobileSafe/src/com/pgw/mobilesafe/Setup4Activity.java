package com.pgw.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private SharedPreferences sp;
	private CheckBox cb_protecting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		cb_protecting=(CheckBox) findViewById(R.id.cb_protecting);
		cb_protecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				if (isChecked) {
					cb_protecting.setText("你已经开启防盗保护");
					editor.putBoolean("protecting", true);
				}else{
					cb_protecting.setText("你没有开启防盗保护");
					editor.putBoolean("protecting", false);
				}
				editor.commit();
			}
		});
//		恢复状态
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			cb_protecting.setText("你已经开启防盗保护");
			cb_protecting.setChecked(true);
		}else{
			cb_protecting.setText("你没有开启防盗保护");
			cb_protecting.setChecked(false);
		}
	}

	@Override
	public void showNext() {
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
		
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);		
	}

}
