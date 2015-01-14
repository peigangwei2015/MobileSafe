package com.pgw.mobilesafe;

import java.util.List;


import com.pgw.mobilesafe.db.dao.BlackNumberDao;
import com.pgw.mobilesafe.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_black_number;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao blackNumberDao;
	private BlackNumberAdaper adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_black_number=(ListView) findViewById(R.id.lv_black_number);
		blackNumberDao=new BlackNumberDao(this);
		infos=blackNumberDao.findAll();
		adapter=new BlackNumberAdaper();
		lv_black_number.setAdapter(adapter);
		
	}
	/**
	 * 黑名单ListView的适配器
	 */
	private class BlackNumberAdaper extends BaseAdapter{
		private static final String TAG = "BlackNumberAdaper";

		@Override
		public int getCount() {
			return infos.size();
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view=null;
			ClassHolder holder=null;
			if (convertView!=null && convertView instanceof LinearLayout) {
				view=convertView;
				holder=(ClassHolder) convertView.getTag();
				Log.i(TAG, "复用旧的View对象");
			}else{
				Log.i(TAG, "创建新的View对象");
				holder=new ClassHolder();
				 view=View.inflate(CallSmsSafeActivity.this, R.layout.list_item_black_number, null);
				 holder.tv_number=(TextView) view.findViewById(R.id.tv_number);
				 holder.tv_mode=(TextView) view.findViewById(R.id.tv_mode);
				 holder.iv_delete=(ImageView) view.findViewById(R.id.iv_delete);
				 view.setTag(holder);
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder=new Builder(CallSmsSafeActivity.this);
					builder.setTitle("警告");
					builder.setMessage("你确定要从黑名单中删除这个号码吗？");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						BlackNumberInfo info=infos.get(position);
							blackNumberDao.delete(info.getNumber());
							infos.remove(position);
							adapter.notifyDataSetChanged();
							dialog.dismiss();
						}
					});
					builder.setNegativeButton("取消", null);
					AlertDialog dialog = builder.create();
					dialog.show();
					
				}
			});
			
			
			BlackNumberInfo info=infos.get(position);
			holder.tv_number.setText(info.getNumber());
			String mode=info.getMode();
			if (mode.equals(BlackNumberInfo.CALL_HOLDE_UP)) {
				holder.tv_mode.setText("电话拦截");
			}else if(mode.equals(BlackNumberInfo.SMS_HOLDE_UP)){
				holder.tv_mode.setText("短信拦截");
			}else if(mode.equals(BlackNumberInfo.ALL_HOLDE_UP)){
				holder.tv_mode.setText("全部拦截");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}
	/**
	 * 添加黑名单
	 * @param view
	 */
	public void add(View v){
//		加载布局
		View view=View.inflate(getApplicationContext(), R.layout.black_number_add_dialog, null);
		AlertDialog.Builder builder=new Builder(this);
		final AlertDialog dialog=builder.create();
		dialog.setView(view, 0	, 0, 0, 0);
		dialog.show();
		final EditText et_number=(EditText) view.findViewById(R.id.et_number);
		final CheckBox cb_phone=(CheckBox) view.findViewById(R.id.cb_phone);
		final CheckBox cb_sms=(CheckBox) view.findViewById(R.id.cb_sms);
		Button bt_ok=(Button) view.findViewById(R.id.ok);
		Button bt_cacle=(Button) view.findViewById(R.id.cancle);
//		点击确定，保存数据并更新数据，和关闭对话框
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String number=et_number.getText().toString();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(getApplicationContext(), "号码不能为空！", 1).show();
					return;
				}
				String mode="";
				if (!cb_phone.isChecked() && !cb_sms.isChecked()) {
					Toast.makeText(getApplicationContext(), "请选择拦截模式！", 1).show();
					return ;
				}else if (cb_phone.isChecked() && cb_sms.isChecked()) {
					mode=BlackNumberInfo.ALL_HOLDE_UP;
				}else if(cb_phone.isChecked()){
					mode=BlackNumberInfo.CALL_HOLDE_UP;
				}else if(cb_sms.isChecked()){
					mode=BlackNumberInfo.SMS_HOLDE_UP;
				}
				
				BlackNumberInfo info=new BlackNumberInfo();
				info.setNumber(number);
				info.setMode(mode);
				infos.add(0, info);
				adapter.notifyDataSetChanged();
				blackNumberDao.insert(info);
//				关闭对话框
				dialog.dismiss();
			}
		});
//		点击取消，关闭对话框
		bt_cacle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	static class ClassHolder{
		  TextView tv_number;
		  TextView tv_mode;
		  ImageView iv_delete;
	}
}
