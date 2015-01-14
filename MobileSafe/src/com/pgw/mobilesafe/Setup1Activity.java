package com.pgw.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
public class Setup1Activity extends BaseSetupActivity {
	protected static final String TAG = "Setup1Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}
	

	@Override
	public void showNext() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		
	}

	@Override
	public void showPre() {
		
	}
	
	

}
