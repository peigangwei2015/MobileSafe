package com.pgw.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.pgw.mobilesafe.service.AddressService;
import com.pgw.mobilesafe.service.CallSmsSafeService;
import com.pgw.mobilesafe.ui.SetingClickView;
import com.pgw.mobilesafe.ui.SetingItemView;
import com.pgw.mobilesafe.utils.ServiceUtils;

public class SetingActivity extends Activity {
	private static final String TAG = "SetingActivity";
	private SetingItemView si_update;
	private SetingItemView siv_address_show;
	private SetingItemView siv_black_number;
	private SharedPreferences sp=null;
	private SetingClickView scv_select_bg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seting_view);
		
		
//		设置自动更新
		setAutoUpdate();
		
//		设置来电归属地显示
		setAddressShow();
		
//		设置来电归属地背景
		setAddressBg();
		
//		设置黑名单
		setBlackNumber();
		
	}
	/**
	 * 设置黑名单
	 */
	private void setBlackNumber() {
		siv_black_number=(SetingItemView) findViewById(R.id.siv_black_number);
//		初始化选中状态
			boolean isRun=ServiceUtils.isServiceRunning(this, CallSmsSafeService.class.getName());
			Log.i(TAG, CallSmsSafeService.class.getName());
			if (isRun) {
				siv_black_number.setChecked(true);
			}else{
				siv_black_number.setChecked(false);
			}
//		绑定点击事件
		siv_black_number.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SetingActivity.this, CallSmsSafeService.class);
				if (siv_black_number.isChecked()) {
//					设置为未选中,并停止黑名单服务
					siv_black_number.setChecked(false);
					stopService(intent);
				}else{
//					设置为选中，启动黑名单服务
					siv_black_number.setChecked(true);
					startService(intent);
				}
			}
		});
		
		
	}
	/**
	 * 设置自动更新
	 */
	private void setAutoUpdate() {
		si_update=(SetingItemView) findViewById(R.id.si_update);
		sp=getSharedPreferences("config", MODE_PRIVATE);
//		恢复上次选择状态
		if(sp.getBoolean("update", false)){
			si_update.setChecked(true);
		}else{					
			si_update.setChecked(false);
		}
		
//		监听用户是否点击
		si_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
//				判断是否是选中状态
				if (si_update.isChecked()) {
					si_update.setChecked(false);
					edit.putBoolean("update", false);
				}else{
					si_update.setChecked(true);
					edit.putBoolean("update", true);
				}
				edit.commit();
			}
		});
	}
	/**
	 * 设置来电归属地显示
	 */
	private void setAddressShow() {
		siv_address_show=(SetingItemView) findViewById(R.id.siv_address_show);
//		判断服务是否开启
		if(ServiceUtils.isServiceRunning(getApplicationContext(), AddressService.class.getName())){
			siv_address_show.setChecked(true);
		}else{
			siv_address_show.setChecked(false);
		}
		/**
		 * 监听点击事件
		 */
		siv_address_show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SetingActivity.this, AddressService.class);
				if (siv_address_show.isChecked()) {
//					关闭来电归属地服务
					siv_address_show.setChecked(false);
					stopService(intent);
				}else{
//					开启来电归属地服务
					startService(intent);
					siv_address_show.setChecked(true);
				}
				
			}
		});
	}
	/**
	 * 设置来电归属地背景
	 */
	private void setAddressBg() {
		final String items []={"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		scv_select_bg=(SetingClickView)findViewById(R.id.scv_select_bg);
		scv_select_bg.setDesc(items[sp.getInt("which", 0)]);
		
		scv_select_bg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder=new Builder(SetingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items , sp.getInt("which", 0),new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						保存选择的值
						Editor editor=sp.edit();
						editor.putInt("which", which);
						editor.commit();
						
						scv_select_bg.setDesc(items[which]);
//						消除对话框
						dialog.dismiss();
					}
					
				});
				builder.setNegativeButton("取消", null);
				builder.show();
				
			}
		});
	}

}
