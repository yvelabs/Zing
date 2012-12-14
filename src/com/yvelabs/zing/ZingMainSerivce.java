package com.yvelabs.zing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Notification;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;

import com.yvelabs.zing.utils.ClipboardUtils;
import com.yvelabs.zing.utils.FileUtils;
import com.yvelabs.zing.utils.NotificationUtils;
import com.yvelabs.zing.utils.ObjectsUtils;

public class ZingMainSerivce extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Notification notification = new NotificationUtils().buildNotification(
				this, ZingMainActivity.class, R.drawable.ic_launcher, this
						.getString(R.string.current_clip_content),
				new ClipboardUtils()
						.getCurrentClipContent(ZingMainSerivce.this));

		startForeground(CommonConstants.MAIN_NOTIFICATION_ID, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		ClipboardManager mClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		mClipboard
				.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
					@Override
					public void onPrimaryClipChanged() {
						// ���ļ��ж���list
						// ��ȡ�ļ�
						try {
							if (ZingMainSerivce.this
									.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE) != null) {
								ArrayList<HashMap<String, String>> clipList = ObjectsUtils.string2ArrayList(new FileUtils()
										.readExternalFile(ZingMainSerivce.this
												.getExternalFilesDir(
														CommonConstants.EXTERNAL_FILES_DIR_TYPE)
												.getAbsolutePath()
												+ CommonConstants.FILE_NAME));
								// �������
								new ZingMainFunction()
										.addNewContent(
												clipList,
												ZingMainSerivce.this,
												new ClipboardUtils()
														.getCurrentClipContent(ZingMainSerivce.this));

							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							// TODO �����쳣
						} catch (IOException e) {
							e.printStackTrace();
							// TODO �����쳣
						}
					}
				});

		return super.onStartCommand(intent, flags, startId);
	}

}
