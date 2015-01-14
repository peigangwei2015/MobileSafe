package com.pgw.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.pgw.mobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {
	protected static final String TAG = "SplashActivity";
	protected static final int JSON_ERROR = 0;
	protected static final int NETWORK_ERROR = 1;
	protected static final int URL_ERROR = 2;
	protected static final int ENTER_HOME = 3;
	protected static final int ALERT_UPDATE_DIALOG = 4;
	private TextView tv_splash_version, tv_splash_update;
	private String descrption;
	private String apkurl;
	private SharedPreferences sp = null;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ENTER_HOME: // 进入主界面
				enterHome();
				Log.i(TAG, "进入主界面");
				break;
			case ALERT_UPDATE_DIALOG:// 弹出更新对话框
				Log.i(TAG, "弹出更新对话框");
				showUpdateDialog();
				break;
			case URL_ERROR:// Url错误
				Log.i(TAG, "Url错误");
				Toast.makeText(getApplicationContext(), "Url错误", 0).show();
				enterHome();
				break;
			case NETWORK_ERROR:// 网络错误
				Log.i(TAG, "网络错误");
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
				enterHome();
				break;
			case JSON_ERROR:// Json解析错误
				Log.i(TAG, "Json解析错误");
				Toast.makeText(getApplicationContext(), "Json解析错误", 0).show();
				enterHome();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 进入主界面
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	};

	/**
	 * 显示更新对话框
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示更新");
		builder.setMessage(descrption);
		// 用户取消处理
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {

				dialog.dismiss();
				enterHome();
			}
		});
		/**
		 * 更新按钮
		 */
		builder.setPositiveButton("马上更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载文件并替换安装

				// 判断SDCard是否存在
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 下载文件并提示安装
					FinalHttp finalhttp = new FinalHttp();
					finalhttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/Download/mobilesafe2.0.apk",
							new AjaxCallBack<File>() {

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									// 下载失败，打印信息
									t.printStackTrace();
									Log.e(TAG, "下载失败");
									Toast.makeText(getApplicationContext(),
											"下载失败！", 1).show();
									enterHome();
									super.onFailure(t, errorNo, strMsg);
								}

								@Override
								public void onLoading(long count, long current) {
									// 下载进度
									int pro = (int) (current * 100 / count);
									tv_splash_update.setText("下载进度：" + pro
											+ "%");
									super.onLoading(count, current);
								}

								@Override
								public void onSuccess(File t) {
									// 下载成功提示替换
									installAPK(t);
									super.onSuccess(t);
								}

							});
				} else {
					// 下载失败
					Toast.makeText(getApplicationContext(),
							"SD卡不存在，下载失败，请安装SD卡!", 1).show();
					enterHome();
					return;
				}
			}
		});
		builder.setNegativeButton("下次在说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 点击取消，关闭对话框，进入主界面
				dialog.dismiss();
				enterHome();

			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 安装APK
	 * 
	 * @param t
	 */
	private void installAPK(File t) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(t),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 设置版本号
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本号：" + getVersionName());
		// 拷贝初始化数据库
		AssetManager assets = getAssets();
		File file = new File(getFilesDir(), "address.db");
		if (!file.exists()) {
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				is = assets.open("address.db");
				fos = new FileOutputStream(file);
				byte[] temp = new byte[1024];
				int len = 0;
				while ((len = is.read(temp)) != -1) {
					fos.write(temp);
				}
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					is = null;
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					fos = null;
				}
			}
		}

		tv_splash_update = (TextView) findViewById(R.id.tv_splash_update);
		if (sp.getBoolean("update", false)) {
			// 检查更新
			checkUpdate();
		} else {
			// 设置不更新,等待两秒后进入主界面
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}

		// 动画
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(800);
		findViewById(R.id.rl_root_splash).startAnimation(aa);

	}

	/**
	 * 检查服务器是否有新版版本
	 */
	private void checkUpdate() {
		new Thread() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL(getString(R.string.updateUrl));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						// 得到Json结果
						InputStream is = conn.getInputStream();
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "联网成功:" + result);
						// 解析Json数据
						JSONObject obj = new JSONObject(result);
						String version = obj.getString("version");
						descrption = obj.getString("descrption");
						apkurl = obj.getString("apkurl");
						// 检测是否有新版本
						if (getVersionName().equals(version)) {
							// 无新版本，进入主页面
							msg.what = ENTER_HOME;
						} else {
							// 有新版本，弹出提示更新对话框
							msg.what = ALERT_UPDATE_DIALOG;
						}
					}

				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					// 停留两秒
					long endTime = System.currentTimeMillis();
					long dTime = endTime - startTime;
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// 发送消息
					handler.sendMessage(msg);
				}

			}
		}.start();

	}

	/**
	 * 获取当前应用的版本号
	 * 
	 * @return
	 */
	private String getVersionName() {
		// 得到包管理器
		PackageManager pm = getPackageManager();
		try {
			// 得到包信息
			PackageInfo pInfo = pm.getPackageInfo(getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

}
