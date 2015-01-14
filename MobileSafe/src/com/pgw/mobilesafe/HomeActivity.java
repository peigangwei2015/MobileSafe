package com.pgw.mobilesafe;

import com.pgw.mobilesafe.utils.Md5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private static String TAG = "HomeActivity";
	private GridView gv_list_home;
	private SharedPreferences sp;
	private static String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",
			"手机杀毒", "缓存清理", "高级工具", "设置中心" };
	public static int[] ids = { R.drawable.safe, R.drawable.callmsgsafe,
			R.drawable.app, R.drawable.taskmanager, R.drawable.netmanager,
			R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools,
			R.drawable.settings };

	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_list_home = (GridView) findViewById(R.id.gv_list_home);
		adapter = new MyAdapter();
		gv_list_home.setAdapter(adapter);
		gv_list_home.setOnItemClickListener(new OnItemClickListener() {
			private EditText et_password;
			private EditText et_confirm_password;
			private Button bt_cancle;
			private Button bt_ok;
			private AlertDialog dialog;

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Intent intent = null;
				switch (position) {
				case 0:// 进入手机防盗
					showPwdDialog();
					break;
				case 1:// 进入通讯卫士
					intent = new Intent(HomeActivity.this, CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2:// 进入软件管理
					intent = new Intent(HomeActivity.this, AppManageActivity.class);
					startActivity(intent);
					break;
				case 7:// 高级工具
					intent = new Intent(HomeActivity.this, AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8:// 设置中心
					intent = new Intent(HomeActivity.this, SetingActivity.class);
					startActivity(intent);
					break;
				

				
				}
			}

			private void showPwdDialog() {
				// 判断是否设置了密码
				if (isSetupPwd()) {
					// 设置了密码，就弹出输入密码对话框
					showEnterPwdDialog();

				} else {
					// 没有设置密码，就弹出设置密码对话框
					showSetupPwdDialog();
				}
			}

			/**
			 * 弹出输入密码对话框
			 */
			private void showEnterPwdDialog() {
				AlertDialog.Builder builder = new Builder(HomeActivity.this);
				View view = View.inflate(HomeActivity.this,
						R.layout.enter_password_dialog, null);
				et_password = (EditText) view.findViewById(R.id.et_password);
				bt_ok = (Button) view.findViewById(R.id.ok);
				bt_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 点击确定，判断输入密码是否为空，和确认密码是否一致，如果一致，保存密码，关闭对话框
						String password = et_password.getText().toString()
								.trim();
						// 判断密码和确认密码是否为空
						if (TextUtils.isEmpty(password)) {
							Toast.makeText(HomeActivity.this, "密码不能为空！", 1)
									.show();
							return;
						}
						// 判断密码是否正确
						String savePassword = sp.getString("password", "");
						if (Md5Utils.md5(password).equals(savePassword)) {
							// 密码正确，取消输入框并进入手机防盗
							dialog.dismiss();
							Log.i(TAG, "密码正确，取消输入框并进入手机防盗");
							Intent intent=new Intent(HomeActivity.this,LostFindActivity.class); 
							startActivity(intent);
						} else {
							// 密码错误，提示用户
							et_password.setText("");
							Toast.makeText(HomeActivity.this, "密码错误，请重新输入!", 1)
									.show();
							return;
						}

					}
				});
				// 处理点击取消事件
				bt_cancle = (Button) view.findViewById(R.id.cancle);
				bt_cancle.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 点击取消，关闭对话框
						dialog.dismiss();
					}
				});
				// builder.setView(view);
				dialog = builder.create();
				dialog.setView(view, 0, 0, 0, 0);
				dialog.show();

			}

			/**
			 * 弹出设置密码对话框
			 */
			private void showSetupPwdDialog() {
				AlertDialog.Builder builder = new Builder(HomeActivity.this);
				View view = View.inflate(HomeActivity.this,
						R.layout.setup_password_dialog, null);
				et_password = (EditText) view.findViewById(R.id.et_password);
				et_confirm_password = (EditText) view
						.findViewById(R.id.et_confirm_password);
				bt_ok = (Button) view.findViewById(R.id.ok);
				bt_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 点击确定，判断输入密码是否为空，和确认密码是否一致，如果一致，保存密码，关闭对话框
						String password = et_password.getText().toString()
								.trim();
						String confirmPassword = et_confirm_password.getText()
								.toString().trim();
						// 判断密码和确认密码是否为空
						if (TextUtils.isEmpty(password)
								|| TextUtils.isEmpty(confirmPassword)) {
							Toast.makeText(HomeActivity.this, "密码和确认密码都不能为空！",
									1).show();
							return;
						}
						// 判断密码和确认密码是否一致
						if (password.equals(confirmPassword)) {
							// 密码一致，保存密码并关闭对话框
							Editor editor = sp.edit();
							editor.putString("password", Md5Utils.md5(password));
							editor.commit();
							dialog.dismiss();
						} else {
							// 密码不一致，清空密码，提示密码和输入密码不一致
							et_password.setText("");
							et_confirm_password.setText("");
							Toast.makeText(HomeActivity.this,
									"密码和确认密码不一致，请重新输入！", 1).show();
							return;
						}

					}
				});
				// 处理点击取消事件
				bt_cancle = (Button) view.findViewById(R.id.cancle);
				bt_cancle.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 点击取消，关闭对话框
						dialog.dismiss();
					}
				});
				// builder.setView(view);
				dialog = builder.create();
				dialog.setView(view, 0, 0, 0, 0);
				dialog.show();
			}

			/**
			 * 判断是否设置密码
			 * 
			 * @return
			 */
			private boolean isSetupPwd() {
				String pwd = sp.getString("password", null);
				return !TextUtils.isEmpty(pwd);
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this,
					R.layout.list_item_home, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			tv_item.setText(names[position]);
			iv_item.setImageResource(ids[position]);
			return view;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
}
