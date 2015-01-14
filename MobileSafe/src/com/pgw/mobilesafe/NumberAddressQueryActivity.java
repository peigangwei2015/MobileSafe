package com.pgw.mobilesafe;

import com.pgw.mobilesafe.db.dao.NumberAddressQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {
	private EditText et_query_number;
	private TextView tv_result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		et_query_number=(EditText) findViewById(R.id.et_query_number);
		et_query_number.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s!=null && s.length()>2) {
					query(null);
				}
				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		tv_result=(TextView) findViewById(R.id.tv_result);
	}
	/**
	 * 查询来电归属地
	 * @param v
	 */
	public void query(View v){
		String number=et_query_number.getText().toString();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "查询号码不能为空！", 1).show();
			
			return;
		}
		String res=NumberAddressQueryUtils.queryNumber(number);
		tv_result.setText(res);
	}

}
