package com.pgw.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AtoolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		
		
	}
	
	public void numberAddressQuery(View view){
		Intent intent=new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
		
	}
	
}
