package com.yvelabs.zing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.yvelabs.zing.utils.ClipboardUtils;
import com.yvelabs.zing.utils.FileUtils;
import com.yvelabs.zing.utils.NotificationUtils;
import com.yvelabs.zing.utils.ObjectsUtils;
import com.yvelabs.zing.utils.PreferenceUtils;

public class ZingMainFunction {
	
	public void setSelectedToClipboard (Context zingMainActivityContxt, Map<String, String> selectedMap) {
		// ����Ϊ��ǰ���а�
		ClipboardUtils.setCurrentClipContent(zingMainActivityContxt,
				selectedMap.get(CommonConstants.MAP_KEY_CONTENT));

		// ����֪ͨ
		new NotificationUtils().notifySpecifyNotification(
				zingMainActivityContxt,
				ZingMainActivity.class,
				ClipboardUtils
						.getCurrentClipContent(zingMainActivityContxt));

		// Toast��ʾ
		Toast.makeText(
				zingMainActivityContxt,
				ObjectsUtils.getSubString(selectedMap.get(CommonConstants.MAP_KEY_CONTENT),
						15), Toast.LENGTH_SHORT).show();
	}
	
	public void deleteAll (ArrayList<HashMap<String, String>> clipList, Context zingMainActivityContxt) {
		//ClipList ���
		clipList = new ArrayList<HashMap<String, String>>();
		
		//���а� ���
		ClipboardUtils.setCurrentClipContent(zingMainActivityContxt, "");
		
		//�ļ� ɾ��
		if (zingMainActivityContxt.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE) != null) {
			FileUtils.deleteFile(zingMainActivityContxt.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE).getAbsolutePath()
					+ CommonConstants.FILE_NAME);
		} else {
			//TODO �쳣����
		}
		
		//֪ͨ
		new NotificationUtils().notifySpecifyNotification(zingMainActivityContxt, ZingMainActivity.class, ClipboardUtils.getCurrentClipContent(zingMainActivityContxt));
		
		//Toast��ʾ
		Toast.makeText(zingMainActivityContxt, zingMainActivityContxt.getString(R.string.all_be_deleted), Toast.LENGTH_SHORT).show();
		
		//�رճ���
	}
	
	public void deleteSelected (ArrayList<HashMap<String, String>> clipList, Context zingMainActivityContxt, HashMap<String, String> selectedMap) {
		String selectedContent = selectedMap.get(CommonConstants.MAP_KEY_CONTENT);
		
		//ClipList ��List�м���ظ�, ɾ�������ظ�
		ObjectsUtils.deleteAllTheRepeat(clipList, selectedContent);
		
		//���а�: 
		//�ж�ɾ���ļ�¼�Ƿ�����ڼ��а���, ������� ���а����������
		if (selectedContent.equals(ClipboardUtils.getCurrentClipContent(zingMainActivityContxt))) {
			//���ClipList��������, ȡDate����
			if (clipList != null && clipList.size() > 0) {
				Collections.sort(clipList, new ObjectsUtils().new SortByDate());
				ClipboardUtils.setCurrentClipContent(zingMainActivityContxt, clipList.get(0).get(CommonConstants.MAP_KEY_CONTENT));
			} else {
				//���ClipListΪ��, ���а��ÿ�
				ClipboardUtils.setCurrentClipContent(zingMainActivityContxt, "");
			}
		}
		
		//�ļ�  ��ClipListд���ļ�
		try {
			FileUtils.writeExternalFile(zingMainActivityContxt.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE).getAbsolutePath() + CommonConstants.FILE_NAME, 
					ObjectsUtils.arrayList2String(clipList));
		} catch (Exception e) {
			e.printStackTrace();
			//TODO �쳣����
		}
		
		//֪ͨ
		new NotificationUtils().notifySpecifyNotification(zingMainActivityContxt, ZingMainActivity.class, ClipboardUtils.getCurrentClipContent(zingMainActivityContxt));

		//Toast��ʾ
		Toast.makeText(zingMainActivityContxt, ObjectsUtils.getSubString(selectedContent, 7) + " " + zingMainActivityContxt.getString(R.string.has_been_deleted), Toast.LENGTH_SHORT).show();
		
		//ҳ�� ������Listˢ��
	}
	
	public void addNewContent (ArrayList<HashMap<String, String>> clipList, Context zingMainActivityContxt, String newContent) {
		//ClipList:
		//����ظ�, ɾ�������ظ�
		ObjectsUtils.deleteAllTheRepeat(clipList, newContent);
		//�������ݼ���ClipList
		HashMap<String, String> newContentMap = new HashMap<String, String>();
		newContentMap.put(CommonConstants.MAP_KEY_DATE, ObjectsUtils.getCurrentTime());
		newContentMap.put(CommonConstants.MAP_KEY_CONTENT, newContent);
		clipList.add(newContentMap);
		//��ʱ��˳���ClipList����
		Collections.sort(clipList, new ObjectsUtils().new SortByDate());
		//��������
		ObjectsUtils.limitedList(clipList, PreferenceUtils.getMaxRowNumbers(zingMainActivityContxt));
		
		//�ļ� ��ClipListд���ļ�
		try {
			FileUtils.writeExternalFile(zingMainActivityContxt.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE).getAbsolutePath() + CommonConstants.FILE_NAME, 
					ObjectsUtils.arrayList2String(clipList));
		} catch (Exception e) {
			e.printStackTrace();
			//TODO �쳣����
		}
		
		//֪ͨ
		new NotificationUtils().notifySpecifyNotification(zingMainActivityContxt, ZingMainActivity.class, ClipboardUtils.getCurrentClipContent(zingMainActivityContxt));
	}
	
	public String getDetail (Context zingMainActivityContxt, HashMap<String, String> map) {
		Date date = new Date(Long.parseLong(map.get(CommonConstants.MAP_KEY_DATE)));
		SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATE_FORMAT_1);
		StringBuilder result = new StringBuilder();
		
		result.append(zingMainActivityContxt.getString(R.string.content)).append(": ").append("\r\n").append(map.get(CommonConstants.MAP_KEY_SHOW_CONTENT)).append("\r\n").append("\r\n");
		result.append(zingMainActivityContxt.getString(R.string.added_time)).append(": ").append(sdf.format(date));
		return result.toString();
	}

}
