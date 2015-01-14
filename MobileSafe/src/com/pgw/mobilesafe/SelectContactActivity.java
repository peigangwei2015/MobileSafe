package com.pgw.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private Uri raw_contacts_uri = Uri
			.parse("content://com.android.contacts/raw_contacts");
	private Uri data_uri = Uri.parse("content://com.android.contacts/data");
	private ListView lv_list_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lv_list_contact = (ListView) findViewById(R.id.lv_list_contact);
		final List<Map<String, String>> data = readContacts();
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.contact_item_view, new String[] { "name", "number" },
				new int[] { R.id.tv_name, R.id.tv_number });
		lv_list_contact.setAdapter(adapter);
		lv_list_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> prent, View view, int position,
					long arg3) {
				Map<String, String> rowData = data.get(position);
				Intent intent=new Intent();
				intent.putExtra("number", rowData.get("number"));
				SelectContactActivity.this.setResult(0, intent);
				finish();
			}
		});
	}


	/**
	 * 读取联系人
	 * 
	 * @return
	 */
	public List<Map<String, String>> readContacts() {
		Cursor cursor = getContentResolver().query(raw_contacts_uri,
				new String[] { "contact_id" }, null, null, null);
		while (cursor != null && cursor.moveToNext()) {
			String contact_id = cursor.getString(cursor
					.getColumnIndex("contact_id"));
			if (TextUtils.isEmpty(contact_id)) {
				return list;
			}
			Map<String, String> contact = new HashMap<String, String>();

			Cursor dataCursor = getContentResolver().query(data_uri,
					new String[] { "mimetype", "data1" }, "raw_contact_id=?",
					new String[] { contact_id }, null);
			while (dataCursor.moveToNext()) {
				String mimetype = dataCursor.getString(0);
				String data1 = dataCursor.getString(1);
				if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
					contact.put("number",
							data1.replaceAll("-", "").replaceAll(" ", ""));
				} else if (mimetype.equals("vnd.android.cursor.item/name")) {
					contact.put("name", data1);
				}
			}
			list.add(contact);
			dataCursor.close();
		}
		cursor.close();
		return list;
	}
}
