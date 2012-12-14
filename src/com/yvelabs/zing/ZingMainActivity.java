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

		// ListView �����¼�. ѡ��Ϊ��ǰ���а�
		clipListLV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				// ���ѡ����
				HashMap<String, String> selectedMap = (HashMap<String, String>) clipListLV
						.getItemAtPosition(arg2);

				// ����Ϊ��ǰ���а�
				function.setSelectedToClipboard(zingMainActivityContxt,
						selectedMap);

				// �˳�����
				finish();
			}
		});

		// ListView �����˵�
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

		// ��ȡ�ļ�
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
			// TODO �����쳣
		} catch (IOException e) {
			e.printStackTrace();
			// TODO �����쳣
		}

		String currentClipboard = ClipboardUtils
				.getCurrentClipContent(zingMainActivityContxt);
		if (currentClipboard != null && currentClipboard.length() > 0)
			function.addNewContent(clipList, zingMainActivityContxt,
					currentClipboard);

		// ����ListView
		buildClipList(clipList);

		// ֪ͨ
		new NotificationUtils().notifySpecifyNotification(
				zingMainActivityContxt, ZingMainActivity.class,
				ClipboardUtils.getCurrentClipContent(zingMainActivityContxt));

		// ��������
		this.startService(mainServiceIntent);

	}

	/**
	 * �����˵���Ӧ����
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// �õ�ѡ����
		int selectedPosition = ((AdapterContextMenuInfo) item.getMenuInfo()).position;// ��ȡ����˵ڼ���
		HashMap<String, String> selectedMap = (HashMap<String, String>) clipListLV
				.getItemAtPosition(selectedPosition);

		if (item.getItemId() == 1) {
			// Detail
			detailDialog(selectedMap);

		} else if (item.getItemId() == 2) {
			// delete this
			function.deleteSelected(clipList, zingMainActivityContxt,
					selectedMap);

			// ˢ�����
			refreshClipList(clipList);
		} else if (item.getItemId() == 3) {
			// delete all
			// ����ȷ�϶Ի���
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
			// TODO ������Ϣ
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
	 * ����ListView
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

		clipListViewSchedule = new SimpleAdapter(this, clipList,// ������Դ
				R.layout.main_clip_list,// ListItem��XMLʵ��
				// ��̬������ListItem��Ӧ������
				new String[] { CommonConstants.MAP_KEY_SHOW_CONTENT },
				// ListItem��XML�ļ����������TextView ID
				new int[] { R.id.clip_list_content });
		// ��Ӳ�����ʾ
		clipListLV.setAdapter(clipListViewSchedule);
	}

	/**
	 * ˢ���б�
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
	 * ����Activity��ʽ
	 */
	private void setProfile() {
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.8f;
		window.setAttributes(lp);
	}

	/**
	 * Detail �Ի���
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

		// �����ʼ�
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

		// ���Ͷ���
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
	 * delete all��ʾ��ȷ��
	 */
	private void DeleteAllConfirmDialog() {
		AlertDialog.Builder builder = new Builder(ZingMainActivity.this);
		// builder.setMessage(R.string.are_you_sure_to_delete_all);
		builder.setIcon(R.drawable.ic_delete_all);
		builder.setTitle(R.string.are_you_sure_to_delete_all);

		// ȷ�ϰ�ť
		builder.setPositiveButton(R.string.confirm,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						// delete all ����
						function.deleteAll(clipList, zingMainActivityContxt);

						// �˳�����
						finish();
					}
				});

		// ȡ����ť
		builder.setNegativeButton(R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	/**
	 * about�Ի���
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
	 * ��ʼ������
	 */
	private void initLanguage() {
		Resources resources = getResources();// ���Resources��Դ����
		Configuration config = resources.getConfiguration();// ���Configuration����
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

		resources.updateConfiguration(config, resources.getDisplayMetrics()); // ������Դ������
	}
}
