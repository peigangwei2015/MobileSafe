package com.pgw.mobilesafe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.pgw.mobilesafe.domain.AppInfo;
import com.pgw.mobilesafe.engine.AppInfoProvider;
import com.pgw.mobilesafe.utils.DensityUtil;

/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 * 
 */
public class AppManageActivity extends Activity implements OnClickListener {
	private ListView appList;
	private AppListAdapter adapter;
	private List<AppInfo> allAppInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private AppInfo appInfo;
	private TextView tv_sd_avail, tv_rom_avail, tv_list_title;
	private PopupWindow window;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanage);
		appList = (ListView) findViewById(R.id.lv_app_list);
		tv_sd_avail = (TextView) findViewById(R.id.tv_sd_avail);
		tv_rom_avail = (TextView) findViewById(R.id.tv_rom_avail);
		tv_list_title = (TextView) findViewById(R.id.tv_title);
		// 设置可用空间
		setAvailSapce();
//		填充数据
		fillData();

		// 注册拖动事件
		appList.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					
					closePopup();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (firstVisibleItem > userAppInfos.size()) {
					tv_list_title.setText("系统程序:" + systemAppInfos.size() + "个");
				}
			}
		});
		// 注册点击事件
		appList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				if (position == userAppInfos.size()) {
					return;
				}

				if (position < userAppInfos.size()) {
					// 用户程序
					appInfo = userAppInfos.get(position);
				} else if (position > userAppInfos.size()) {
					// 系统程序
					int newPosition = position - (userAppInfos.size() + 1);
					appInfo = systemAppInfos.get(newPosition);
				}
				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_window_appmanage, null);
				LinearLayout ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				LinearLayout ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);
				LinearLayout ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				ll_start.setOnClickListener(AppManageActivity.this);
				ll_share.setOnClickListener(AppManageActivity.this);
				ll_uninstall.setOnClickListener(AppManageActivity.this);
				// 判断是否有打开的弹出窗体，如果有则关闭
				closePopup();
				// 设置打开时动画
				ScaleAnimation scaleAnimation = new ScaleAnimation(0.3f, 1f,
						0.3f, 1f, Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1f);

				AnimationSet animationSet = new AnimationSet(true);
				animationSet.addAnimation(scaleAnimation);
				animationSet.addAnimation(alphaAnimation);
				animationSet.setDuration(500);
				contentView.startAnimation(animationSet);
				// 创建气泡窗体
				window = new PopupWindow(contentView, -2, -2);
				window.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));

				int[] location = new int[2];
				view.getLocationInWindow(location);
				int xOffset = DensityUtil.dip2px(getApplicationContext(), 45);
				int yOffset = DensityUtil.dip2px(getApplicationContext(), -5);
				window.showAtLocation(appList, Gravity.TOP | Gravity.LEFT,
						location[0] + xOffset, location[1] + yOffset);
			}
		});

	}
	/**
	 * 填充ListView数据
	 */
	private void fillData() {
		AppInfoProvider appInfoProvider = new AppInfoProvider(this);
		allAppInfos = appInfoProvider.getAppInfos();
		userAppInfos = new ArrayList<AppInfo>();
		systemAppInfos = new ArrayList<AppInfo>();
		for (AppInfo appInfo : allAppInfos) {
			if (appInfo.isUser()) {
				userAppInfos.add(appInfo);
			} else {
				systemAppInfos.add(appInfo);
			}
		}
		adapter = new AppListAdapter();
		appList.setAdapter(adapter);
	}

	/**
	 * 设置可用的空间
	 */
	private void setAvailSapce() {
		// 获取和设置SD卡的可用空间，和内存的可用空间
		long storagAvail = getAvailSpace(Environment
				.getExternalStorageDirectory().getAbsolutePath());
		long romAvail = getAvailSpace(Environment.getDataDirectory()
				.getAbsolutePath());
		String strStorag = Formatter.formatFileSize(getApplicationContext(),
				storagAvail);
		String strRom = Formatter.formatFileSize(getApplicationContext(),
				romAvail);
		tv_sd_avail.setText("SD卡可用空间：" + strStorag);
		tv_rom_avail.setText("内存可用空间：" + strRom);

	}

	/**
	 * 获取可用内存空间
	 * 
	 * @param externalStorageDirectory
	 * @return 剩余的内存空间
	 */
	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		long bcount = statFs.getAvailableBlocks();
		long bsize = statFs.getBlockSize();
		return bcount * bsize;
	}

	private class AppListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return userAppInfos.size() + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			AppInfo info = null;
			// 判断是否可以复用的View对象，如果可以服用就用旧的，如果不可以，就床架你新的View对象
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) convertView.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_appmanage, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_loaction = (TextView) view
						.findViewById(R.id.tv_app_location);
				view.setTag(holder);
			}

			tv_list_title.setText("用户程序:" + userAppInfos.size() + "个");
			if (position == userAppInfos.size()) {
				// 系统程序的标题
				TextView tv_title = new TextView(getApplicationContext());
				String title = "系统程序:" + systemAppInfos.size() + "个";
				tv_title.setText(title);
				tv_title.setBackgroundColor(Color.GRAY);
				tv_title.setTextColor(Color.WHITE);
				return tv_title;
			} else if (position < userAppInfos.size()) {
				// 用户程序
				info = userAppInfos.get(position);
			} else if (position > userAppInfos.size()) {
				// 系统程序
				int newPosition = position - (userAppInfos.size() + 1);
				info = systemAppInfos.get(newPosition);
			}
			if (holder != null && info != null) {
				holder.iv_icon.setImageDrawable(info.getIcon());
				holder.tv_name.setText(info.getName());
				holder.tv_loaction.setText("手机内存");
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

	class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_loaction;
	}

	@Override
	public void onClick(View v) {
		closePopup();
		switch (v.getId()) {
		case R.id.ll_start:
			startApp();
			break;
		case R.id.ll_uninstall:
			uninstallApp();
			break;
		case R.id.ll_share:
			shareApp();
			break;
		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}
	/**
	 * 关闭气泡窗体
	 */
	private void closePopup() {
		if (window!=null && window.isShowing()) {
			window.dismiss();
			window=null;
		}
	}
	/**
	 * 分享应用
	 */
	private void shareApp() {
		Intent intent=new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "给你推荐一款好玩的应用，名称为："+appInfo.getName()+"下载地址为 ： http://www.kengni.com");
		startActivity(intent);
	}

	/**
	 * 卸载应用
	 */
	private void uninstallApp() {
		if (!appInfo.isUser()) {
			Toast.makeText(getApplicationContext(), "不能卸载系统程序，如需卸载，请获取root权限。",
					1).show();
			return;
		}
		 Intent intent=new Intent();
		 intent.setAction("android.intent.action.VIEW");
		 intent.setAction("android.intent.action.DELETE");
		 intent.addCategory("android.intent.category.DEFAULT");
		 intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
		 startActivityForResult(intent, 0);
	}

	/**
	 * 开启应用
	 */
	private void startApp() {
		PackageManager pm=getPackageManager();
		Intent intent=pm.getLaunchIntentForPackage(appInfo.getPackageName());
		if (intent==null) {
			Toast.makeText(getApplicationContext(), "这个应用程序不能启动！", 1).show();
			return;
		}
		startActivity(intent);

	}
}
