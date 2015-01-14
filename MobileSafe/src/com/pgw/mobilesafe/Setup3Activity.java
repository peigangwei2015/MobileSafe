package com.pgw.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_safenumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_safenumber=(EditText) findViewById(R.id.et_safenumber);
		et_safenumber.setText(sp.getString("safeNumber", ""));
	}
	@Override
	public void showNext() {
//		判断是否输入安全号码
		if (TextUtils.isEmpty(et_safenumber.getText().toString())) {
			Toast.makeText(Setup3Activity.this, "请输入安全号码！", 1).show();
			return;
		}else{
			Editor editor = sp.edit();
			editor.putString("safeNumber", et_safenumber.getText().toString().trim());
			editor.commit();
		}
		
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);		
	}
	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);		
	}
	/**
	 * 选择联系人
	 * @param view
	 */
	public void selectContact(View view){
		Intent intent=new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data==null) {
			return;
		}
		String number=data.getStringExtra("number");
		et_safenumber.setText(number);
	}
}
