package com.yvelabs.zing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.yvelabs.zing.utils.ClipboardUtils;
import com.yvelabs.zing.utils.FileUtils;
import com.yvelabs.zing.utils.NotificationUtils;
import com.yvelabs.zing.utils.ObjectsUtils;
import com.yvelabs.zing.utils.PreferenceUtils;

public class ZingMainActivity extends Activity {

	private Context zingMainActivityContxt;
	private SimpleAdapter clipListViewSchedule;
	private ListView clipListLV;
	private ArrayList<HashMap<String, String>> clipList = new ArrayList<HashMap<String, String>>();
	private Intent mainServiceIntent;

	private ZingMainFunction function;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initLanguage();
		super.onCreate(savedInstanceState);

		mainServiceIntent = new Intent(this, ZingMainSerivce.class);

		setContentView(R.layout.activity_zing_main);
		zingMainActivityContxt = getBaseContext();
		setProfile();

		clipListLV = (ListView) this.findViewById(R.id.clip_list_lv);
		function = new ZingMainFunction();

		// ListView 单击事件. 选中为当前剪切板
		clipListLV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				// 获得选中项
				HashMap<String, String> selectedMap = (HashMap<String, String>) clipListLV
						.getItemAtPosition(arg2);

				// 设置为当前剪切板
				function.setSelectedToClipboard(zingMainActivityContxt,
						selectedMap);

				// 退出程序
				finish();
			}
		});

		// ListView 长按菜单
		clipListLV
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle(R.string.app_name);
						menu.setHeaderIcon(R.drawable.ic_launcher);
						menu.add(0, 1, 5, R.string.detail);
						menu.add(0, 2, 5, R.string.delete_this);
						menu.add(0, 3, 5, R.string.delete_all);
					}
				});

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 读取文件
		try {
			if (this.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE) != null) {
				clipList = ObjectsUtils.string2ArrayList(new FileUtils()
						.readExternalFile(this.getExternalFilesDir(
								CommonConstants.EXTERNAL_FILES_DIR_TYPE)
								.getAbsolutePath()
								+ CommonConstants.FILE_NAME));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// TODO 处理异常
		} catch (IOException e) {
			e.printStackTrace();
			// TODO 处理异常
		}

		String currentClipboard = ClipboardUtils
				.getCurrentClipContent(zingMainActivityContxt);
		if (currentClipboard != null && currentClipboard.length() > 0)
			function.addNewContent(clipList, zingMainActivityContxt,
					currentClipboard);

		// 构造ListView
		buildClipList(clipList);

		// 通知
		new NotificationUtils().notifySpecifyNotification(
				zingMainActivityContxt, ZingMainActivity.class,
				ClipboardUtils.getCurrentClipContent(zingMainActivityContxt));

		// 启动服务
		this.startService(mainServiceIntent);

	}

	/**
	 * 长按菜单响应函数
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// 得到选中项
		int selectedPosition = ((AdapterContextMenuInfo) item.getMenuInfo()).position;// 获取点击了第几行
		HashMap<String, String> selectedMap = (HashMap<String, String>) clipListLV
				.getItemAtPosition(selectedPosition);

		if (item.getItemId() == 1) {
			// Detail
			detailDialog(selectedMap);

		} else if (item.getItemId() == 2) {
			// delete this
			function.deleteSelected(clipList, zingMainActivityContxt,
					selectedMap);

			// 刷新类表
			refreshClipList(clipList);
		} else if (item.getItemId() == 3) {
			// delete all
			// 弹出确认对话框
			DeleteAllConfirmDialog();

		}

		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_zing_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (R.id.menu_about == item.getItemId()) {
			// TODO 关于信息
			aboutDialog();
		} else if (R.id.menu_settings == item.getItemId()) {
			startActivity(new Intent(this, ZingSetting.class));
		} else if (R.id.menu_close == item.getItemId()) {
			this.stopService(mainServiceIntent);
			new NotificationUtils().removeNotification(zingMainActivityContxt,
					CommonConstants.MAIN_NOTIFICATION_ID);
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 构造ListView
	 * 
	 * @param clipListLV2
	 * @param clipList
	 */
	private void buildClipList(ArrayList<HashMap<String, String>> clipList) {

		int i = 1;
		for (Map<String, String> map : clipList) {
			map.put(CommonConstants.MAP_KEY_SHOW_CONTENT,
					i++ + ") " + map.get(CommonConstants.MAP_KEY_CONTENT));
		}

		clipListViewSchedule = new SimpleAdapter(this, clipList,// 数据来源
				R.layout.main_clip_list,// ListItem的XML实现
				// 动态数组与ListItem对应的子项
				new String[] { CommonConstants.MAP_KEY_SHOW_CONTENT },
				// ListItem的XML文件里面的两个TextView ID
				new int[] { R.id.clip_list_content });
		// 添加并且显示
		clipListLV.setAdapter(clipListViewSchedule);
	}

	/**
	 * 刷新列表
	 * 
	 * @param clipList
	 */
	private void refreshClipList(ArrayList<HashMap<String, String>> clipList) {
		int i = 1;
		for (Map<String, String> map : clipList) {
			map.put(CommonConstants.MAP_KEY_SHOW_CONTENT,
					i++ + ") " + map.get(CommonConstants.MAP_KEY_CONTENT));
		}

		clipListViewSchedule.notifyDataSetChanged();
	}

	/**
	 * 设置Activity样式
	 */
	private void setProfile() {
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.8f;
		window.setAttributes(lp);
	}

	/**
	 * Detail 对话框
	 * 
	 * @param message
	 */
	private void detailDialog(HashMap<String, String> selectedMap) {
		final String message = selectedMap.get(CommonConstants.MAP_KEY_CONTENT);
		String detail = function.getDetail(this, selectedMap);
		AlertDialog.Builder builder = new Builder(ZingMainActivity.this);
		builder.setIcon(R.drawable.ic_detail);
		builder.setTitle(R.string.detail);

		builder.setMessage(ObjectsUtils.getSubString(detail, 300));

		builder.setNegativeButton(R.string.close,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		// 发送邮件
		builder.setNeutralButton(R.string.send_email,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent emailIntent = new Intent();
						emailIntent.setAction(Intent.ACTION_SENDTO);
						emailIntent.setData(Uri.parse("mailto:"));  
						//emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ZingMainActivity.this.getString(R.string.zing_2_0_feedback));  
						emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
						
						ZingMainActivity.this.startActivity(Intent
								.createChooser(emailIntent,
										getString(R.string.sending_email)));
					}
				});

		// 发送短信
		builder.setPositiveButton(R.string.send_sms,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
						smsIntent.setData(Uri.parse("smsto:"));
						smsIntent.putExtra("sms_body", message);
						startActivity(Intent.createChooser(smsIntent,
								getString(R.string.sending_sms)));
					}
				});

		builder.create().show();
	}

	/**
	 * delete all提示框确认
	 */
	private void DeleteAllConfirmDialog() {
		AlertDialog.Builder builder = new Builder(ZingMainActivity.this);
		// builder.setMessage(R.string.are_you_sure_to_delete_all);
		builder.setIcon(R.drawable.ic_delete_all);
		builder.setTitle(R.string.are_you_sure_to_delete_all);

		// 确认按钮
		builder.setPositiveButton(R.string.confirm,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						// delete all 操作
						function.deleteAll(clipList, zingMainActivityContxt);

						// 退出程序
						finish();
					}
				});

		// 取消按钮
		builder.setNegativeButton(R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	/**
	 * about对话框
	 */
	private void aboutDialog() {
		StringBuffer about = new StringBuffer();
		about.append(this.getResources().getString(R.string.connect_us)).append(":\r\n");
		about.append(
				"        "
						+ this.getResources().getString(R.string.email_yvelabs))
				.append("\r\n").append("\r\n");
		about.append(this.getResources().getString(R.string.icon_from)).append(
				":\r\n");
		about.append("        "
				+ this.getResources().getString(R.string.icon_webset));

		AlertDialog.Builder builder = new Builder(ZingMainActivity.this);
		builder.setIcon(R.drawable.ic_about);
		builder.setTitle(R.string.about);

		builder.setMessage(about.toString());
		
		builder.setPositiveButton(getString(R.string.feedback),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent emailIntent = new Intent();
						emailIntent.setAction(Intent.ACTION_SENDTO);
						emailIntent.setData(Uri.parse("mailto:"  + getString(R.string.email_yvelabs)));  
						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ZingMainActivity.this.getString(R.string.zing_2_0_feedback));  
						
						ZingMainActivity.this.startActivity(Intent
								.createChooser(emailIntent,
										getString(R.string.sending_email)));
					}
				});

		builder.setNegativeButton(R.string.close,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	/**
	 * 初始化语言
	 */
	private void initLanguage() {
		Resources resources = getResources();// 获得Resources资源对象
		Configuration config = resources.getConfiguration();// 获得Configuration对象
		String language = PreferenceUtils.getLanguage(this);

		if (language == null || language.length() <= 0) {
			config.locale = Locale.getDefault();
		}

		if ("en".equals(language)) {
			config.locale = Locale.US;
		} else if ("zh-rCN".equals(language)) {
			config.locale = Locale.CHINA;
		} else if ("fre".equals(language)) {
			config.locale = Locale.FRANCE;
		}

		resources.updateConfiguration(config, resources.getDisplayMetrics()); // 更新资源的配置
	}
}
